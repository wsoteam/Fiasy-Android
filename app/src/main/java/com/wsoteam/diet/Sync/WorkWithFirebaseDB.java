package com.wsoteam.diet.Sync;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.wsoteam.diet.articles.POJO.ListArticles;
import com.wsoteam.diet.BranchOfAnalyzer.Const;
import com.wsoteam.diet.BranchOfAnalyzer.CustomFood.CustomFood;
import com.wsoteam.diet.BranchOfAnalyzer.POJOClaim.Claim;
import com.wsoteam.diet.BranchOfAnalyzer.templates.POJO.FoodTemplate;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.DietPlans.POJO.DietPlan;
import com.wsoteam.diet.POJOProfile.FavoriteFood;
import com.wsoteam.diet.POJOProfile.Profile;
import com.wsoteam.diet.POJOProfile.SubInfo;
import com.wsoteam.diet.POJOProfile.TrackInfo;
import com.wsoteam.diet.Recipes.POJO.ListRecipes;
import com.wsoteam.diet.Recipes.POJO.RecipeItem;
import com.wsoteam.diet.Sync.POJO.UserData;
import com.wsoteam.diet.Sync.POJO.WeightDiaryObject;
import com.wsoteam.diet.common.promo.POJO.Promo;
import com.wsoteam.diet.common.promo.POJO.UserPromo;
import com.wsoteam.diet.model.Breakfast;
import com.wsoteam.diet.model.Dinner;
import com.wsoteam.diet.model.Eating;
import com.wsoteam.diet.model.Grade;
import com.wsoteam.diet.model.Lunch;
import com.wsoteam.diet.model.OpenArticles;
import com.wsoteam.diet.model.Snack;
import com.wsoteam.diet.model.Water;
import com.wsoteam.diet.presentation.measurment.POJO.Chest;
import com.wsoteam.diet.presentation.measurment.POJO.Hips;
import com.wsoteam.diet.presentation.measurment.POJO.Waist;
import com.wsoteam.diet.presentation.measurment.POJO.Weight;

import com.wsoteam.diet.utils.FirebaseExtKt;
import com.wsoteam.diet.utils.RxFirebase;
import io.reactivex.Flowable;
import io.reactivex.Single;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorkWithFirebaseDB {
    public static final int PLAN_UPDATED = 0;
    public static final int EATING_UPDATED = 1;
    public static final int WEIGHT_UPDATED = 2;

    private final static Map<String, String> OUTCOMES = new HashMap<String, String>(){{
        put("waters", "water_added");
        put("breakfasts", "breakfast_added");
        put("dinners", "dinner_added");
        put("lunches", "lunch_added");
        put("snacks", "snack_added");
    }};

    private final static MutableLiveData<Integer> databaseUpdates = new MutableLiveData<>();
    private final static AtomicBoolean hasUpdatesListener = new AtomicBoolean(false);


    public static LiveData<Integer> liveUpdates(){
        return databaseUpdates;
    }

    public static void setWholeTestObject() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData userData = dataSnapshot.getValue(UserData.class); }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static UserData getUserData(DataSnapshot snapshot){
        try {
            return snapshot.getValue(UserData.class);
        } catch (DatabaseException e){
          e.printStackTrace();

          dropUserMealsDiary();
          return null;
        }
    }

    public static void setFirebaseStateListener() {
        if (hasUpdatesListener.compareAndSet(false, true)) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final UserData user = getUserData(dataSnapshot);

                    if (user != null) {
                        new UserDataHolder().bindObjectWithHolder(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public static void dropUserMealsDiary(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference currentUserRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY)
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        currentUserRef.updateChildren(new HashMap<String, Object>(){{
            put("breakfasts", null);
            put("lunches", null);
            put("dinners", null);
        }});
    }

    public static String addEating(Eating eating, String type){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
            child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(type);
        final String id = myRef.push().getKey();
        myRef.child(id).setValue(eating);

        databaseUpdates.postValue(EATING_UPDATED);

        final String outcome = OUTCOMES.get(type);

        if (!TextUtils.isEmpty(outcome)) {
            OneSignal.sendOutcome(outcome);
        }

        return id;
    }

    public static void editEating(String id, Eating eating, String type){
      FirebaseDatabase database = FirebaseDatabase.getInstance();
      DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
          child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(type).child(id);

      myRef.setValue(eating);

      databaseUpdates.postValue(EATING_UPDATED);
    }

    public static void removeEating(String id, String type) {
      final FirebaseDatabase database = FirebaseDatabase.getInstance();
      final DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
          child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(type).child(id);
      myRef.removeValue();

      databaseUpdates.postValue(EATING_UPDATED);
    }

    public static Single<Map<String, Eating>> takeMeals(String type){
        return takeMeals(type, Eating.class);
    }

    public static <T> Single<Map<String, T>> takeMeals(String type, Class<T> typeClass){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference data = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
            child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(type);

        return RxFirebase.from(data)
            .flatMap(snapshot -> Flowable.fromIterable(snapshot.getChildren()))
            .toMap(d -> d.getKey(), d -> d.getValue(typeClass));
    }

    public static void addBreakfast(Breakfast breakfast) {
        addEating(breakfast, "breakfasts");
    }

    public static void addLunch(Lunch lunch) {
        addEating(lunch, "lunches");
    }

    public static void addDinner(Dinner dinner) {
        addEating(dinner, "dinners");
    }

    public static void addSnack(Snack snack) {
        addEating(snack, "snacks");
    }

    public static String addWater(Water water) {
        return addEating(water, "waters");
    }

    public static void updateWater(String key, float water) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
            child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("waters").child(key)
            .child("waterCount");
        myRef.setValue(water);

        databaseUpdates.postValue(EATING_UPDATED);
    }

    public static void setMaxWater(float maxWater){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
            child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile").child("maxWater");
        myRef.setValue(maxWater);

        databaseUpdates.postValue(EATING_UPDATED);
    }

    public static void putProfileValue(Profile profile) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile");
        myRef.setValue(profile);
    }

    public static void addWeightDiaryItem(WeightDiaryObject data) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("diaryDataList");
        myRef.push().setValue(data);
    }

    public static void replaceWeightDiaryItem(WeightDiaryObject data, String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("diaryDataList").child(key);
        myRef.setValue(data);
    }

    public static void saveListRecipes(ListRecipes data) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("RECIPES_PLANS");
        myRef.setValue(data);
    }


    public static void setSubInfo(SubInfo subInfo) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("subInfo");
        myRef.setValue(subInfo);
    }

    public static void setTrackInfo(TrackInfo trackInfo) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("trackInfo");
        myRef.setValue(trackInfo);
    }

    public static void check() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, UserData> stringUserDataHashMap = (HashMap<String, UserData>) dataSnapshot.getValue();
                Log.e("LOL", String.valueOf(stringUserDataHashMap.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void addUserRecipe(RecipeItem recipeItem) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("recipes");
        myRef.push().setValue(recipeItem);
    }


    public static void addUsersSharedRecipe(RecipeItem recipeItem){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.USERS_RECIPES);
        myRef.push().setValue(recipeItem);
    }


    public static void saveListRecipesNew(ListRecipes data) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("RECIPES_PLANS_NEW");
        myRef.setValue(data);
    }

    public static void saveListArticles(ListArticles data) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ARTICLES");
        myRef.setValue(data);
    }

    public static void sendClaim(Claim claim) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Const.CLAIM_PATH);
        myRef.push().setValue(claim);
    }

    public static String addFoodFavorite(FavoriteFood food) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("foodFavorites");
        String key = myRef.push().getKey();
        myRef.child(key).setValue(food);
        return key;
    }

    public static void rewriteFoodFavorite(FavoriteFood food) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("foodFavorites");
        myRef.child(food.getKey()).setValue(food);
    }


    public static void deleteFavorite(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("foodFavorites").child(key);
        myRef.removeValue();
    }

    public static void removeBreakfast(String key) {
        removeEating(key, "breakfasts");
    }

    public static void removeLunch(String key) {
        removeEating(key, "lunches");
    }

    public static void removeDinner(String key) {
        removeEating(key, "dinners");
    }

    public static void removeSnack(String key) {
        removeEating(key, "snacks");
    }

    public static void editBreakfast(Breakfast breakfast, String key) {
        editEating(key, breakfast, "breakfasts");
    }

    public static void editLunch(Lunch lunch, String key) {
        editEating(key, lunch, "lunches");
    }

    public static void editDinner(Dinner dinner, String key) {
        editEating(key, dinner, "dinners");
    }

    public static void editSnack(Snack snack, String key) {
        editEating(key, snack, "snacks");
    }


    public static String addFavoriteRecipe(RecipeItem recipe) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favoriteRecipes");
        String key = myRef.push().getKey();
        myRef.child(key).setValue(recipe);
        return key;
    }

    public static void deleteFavoriteRecipe(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favoriteRecipes").child(key);
        myRef.removeValue();
    }

    public static void addCustomFood(CustomFood customFood) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("customFoods");
        myRef.push().setValue(customFood);
    }

    public static void shareCustomFood(CustomFood customFood) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.LIST_CUSTOM_FOOD);
        myRef.push().setValue(customFood);
    }

    public static void deleteCustomFood(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("customFoods").child(key);
        myRef.removeValue();
    }

    public static void rewriteCustomFood(CustomFood customFood) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("customFoods").child(customFood.getKey());
        myRef.setValue(customFood);
    }
  public static void joinDietPlan(DietPlan plan) {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
        child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    myRef.child("plan").setValue(plan);

    databaseUpdates.postValue(PLAN_UPDATED);
  }

  public static void leaveDietPlan() {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
        child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("plan");
    myRef.removeValue();

    databaseUpdates.postValue(PLAN_UPDATED);
  }

    public static String addFoodTemplate(FoodTemplate foodTemplate) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("foodTemplates");
        String key = myRef.push().getKey();
        foodTemplate.setKey(key);
        myRef.child(key).setValue(foodTemplate);
        return key;
    }

    public static void setPhotoURL(String url) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile").child("photoUrl");
        myRef.setValue(url);
    }

  public static void setRecipeInDiaryFromPlan(String day, String meal, String recipeNumber,
      boolean value) {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY)
        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
        .child("plan")
        .child("recipeForDays")
        .child(day)
        .child(meal)
        .child(recipeNumber);
    myRef.child("addedInDiaryFromPlan").setValue(value);
  }
    public static void deleteFoodTemplate(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("foodTemplates").child(key);
        myRef.removeValue();
    }
    public static void editFoodTemplate(String key,FoodTemplate foodTemplate){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("foodTemplates").child(key);
        myRef.setValue(foodTemplate);
    }

    public static void setVisibilityFoodTemplate(String key, boolean visibility){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("foodTemplates")
                .child(key).child("showFoods");
        myRef.setValue(visibility);
    }

    public static void setUserPromo(UserPromo userPromo){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userPromo");
        myRef.setValue(userPromo);
    }

    public static void setEmptyUserPromo(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userPromo");
        myRef.removeValue();
    }

    public static void changePromo(Promo promo){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(com.wsoteam.diet.common.promo.Config.promoStoragePath).
                child(promo.getId());
        myRef.setValue(promo);
    }


    public static void setWeight(Weight weight, String timeInMillis) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("weights").child(timeInMillis);
        myRef.setValue(weight);

        databaseUpdates.postValue(WEIGHT_UPDATED);

        OneSignal.sendOutcome("weight_updated");
    }

    public static void deleteWeight(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("weights").child(key);
        myRef.removeValue();
    }

    public static void setChest(Chest chest) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chest").child(String.valueOf(chest.getTimeInMillis()));
        myRef.setValue(chest);
    }

    public static void setWaist(Waist waist) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("waist").child(String.valueOf(waist.getTimeInMillis()));
        myRef.setValue(waist);
    }

    public static void setHips(Hips hips) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("hips").child(String.valueOf(hips.getTimeInMillis()));
        myRef.setValue(hips);
    }

    public static void addArticleSeries(OpenArticles openArticles) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("articleSeries");
        myRef.child(openArticles.getId()).setValue(openArticles);
    }

    public static void saveGrade(Grade grade){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("FEEDBACK").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        grade.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        String key = myRef.push().getKey();
        myRef.child(key).setValue(grade);
    }
}

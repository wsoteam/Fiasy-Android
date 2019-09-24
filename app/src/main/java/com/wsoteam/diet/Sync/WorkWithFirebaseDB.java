package com.wsoteam.diet.Sync;

import androidx.annotation.NonNull;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wsoteam.diet.Activism.POJO.ActivismFirebaseObject;
import com.wsoteam.diet.Articles.POJO.ListArticles;
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
import com.wsoteam.diet.model.Lunch;
import com.wsoteam.diet.model.Snack;
import com.wsoteam.diet.model.Water;
import java.util.HashMap;

public class WorkWithFirebaseDB {

    public static void setWholeTestObject() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData userData = dataSnapshot.getValue(UserData.class);
                Log.e("LOL", "get user data");
            }

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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
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

    public static void addBreakfast(Breakfast breakfast) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("breakfasts");
        myRef.push().setValue(breakfast);
    }

    public static void addLunch(Lunch lunch) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lunches");
        myRef.push().setValue(lunch);
    }

    public static void addDinner(Dinner dinner) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("dinners");
        myRef.push().setValue(dinner);
    }

    public static void addSnack(Snack snack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("snacks");
        myRef.push().setValue(snack);
    }

    public static void addWater(Water water) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("water");
        myRef.push().setValue(water);
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

    public static void saveActivism(ActivismFirebaseObject data) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ACTIVISM");
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("breakfasts").child(key);
        myRef.removeValue();
    }

    public static void removeLunch(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lunches").child(key);
        myRef.removeValue();
    }

    public static void removeDinner(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("dinners").child(key);
        myRef.removeValue();
    }

    public static void removeSnack(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("snacks").child(key);
        myRef.removeValue();
    }

    public static void editBreakfast(Breakfast breakfast, String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("breakfasts").child(key);
        myRef.setValue(breakfast);
    }

    public static void editLunch(Lunch lunch, String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lunches").child(key);
        myRef.setValue(lunch);
    }

    public static void editDinner(Dinner dinner, String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("dinners").child(key);
        myRef.setValue(dinner);
    }

    public static void editSnack(Snack snack, String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("snacks").child(key);
        myRef.setValue(snack);
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
  }

  public static void leaveDietPlan() {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
        child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("plan");
    myRef.removeValue();
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
}

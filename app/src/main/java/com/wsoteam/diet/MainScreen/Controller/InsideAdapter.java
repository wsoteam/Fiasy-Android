package com.wsoteam.diet.MainScreen.Controller;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wsoteam.diet.DietPlans.POJO.DietPlan;
import com.wsoteam.diet.Recipes.POJO.RecipeItem;
import com.wsoteam.diet.Recipes.POJO.plan.RecipeForDay;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.model.Eating;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;

import com.wsoteam.diet.presentation.plans.DateHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InsideAdapter extends RecyclerView.Adapter<InsideViewHolder> {
    private List<Eating> oneGroupOfEating;
    private Context context;
    private int choiseEating;
    private final int BREAKFAST = 0, LUNCH = 1, DINNER = 2, SNACK = 3;
    private EatingHolderCallback callback;
    private UpdateCallback updateCallback;


    public InsideAdapter(List<Eating> oneGroupOfEating, Context context, boolean isFull, int choiseEating, EatingHolderCallback callback) {
        this.choiseEating = choiseEating;
        this.callback = callback;
        if (oneGroupOfEating.size() != 0 && !isFull) {
            this.oneGroupOfEating = new ArrayList<>();
            //this.oneGroupOfEating.add(oneGroupOfEating.get(0));
            this.context = context;
        } else {
            this.oneGroupOfEating = oneGroupOfEating;
            this.context = context;
        }
    }

    public InsideAdapter setUpdateCallback(@NonNull UpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
        return this;
    }

    @NonNull
    @Override
    public InsideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new InsideViewHolder(layoutInflater, parent, context);
    }

    @Override
    public void onBindViewHolder(@NonNull InsideViewHolder holder, int position) {
        boolean isLastElement = false;
        if (position == oneGroupOfEating.size() - 1){
            isLastElement = true;
        }
        holder.bind(oneGroupOfEating.get(position), choiseEating, isLastElement,   new InsideHolderCallback() {
            @Override
            public void itemWasClicked(int position) {
                removeRecipeForDietPlan(position);
                removeItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return oneGroupOfEating.size();
    }

    private void removeItem(int position) {
        switch (choiseEating) {
            case BREAKFAST:
                WorkWithFirebaseDB.removeBreakfast(oneGroupOfEating.get(position).getUrlOfImages());
                break;
            case LUNCH:
                WorkWithFirebaseDB.removeLunch(oneGroupOfEating.get(position).getUrlOfImages());
                break;
            case DINNER:
                WorkWithFirebaseDB.removeDinner(oneGroupOfEating.get(position).getUrlOfImages());
                break;
            case SNACK:
                WorkWithFirebaseDB.removeSnack(oneGroupOfEating.get(position).getUrlOfImages());
                break;
        }
        oneGroupOfEating.remove(position);
        notifyItemRemoved(position);
        callback.itemWasRemoved(position);
    }

    private void removeRecipeForDietPlan(int position){

        DietPlan plan = UserDataHolder.getUserData().getPlan();
        if (plan != null) {
            Eating eating = oneGroupOfEating.get(position);
            //yyyy-MM-dd
            Date startPlan = DateHelper.stringToDate(plan.getStartDate());
            Date addInDiary = DateHelper.stringToDate(eating.getYear() + "-" + (eating.getMonth() + 1) + "-" + eating.getDay());
//            Log.d("kkk", "removeRecipeForDietPlan: startPlan " + startPlan);
//            Log.d("kkk", "removeRecipeForDietPlan: addInDiary " + addInDiary);
            long mill = (startPlan.getTime() + (plan.getDaysAfterStart() * 24 * 60 * 60 * 1000));
//            Log.d("kkk", "removeRecipeForDietPlan: " + new Date(mill));
            if (addInDiary.getTime() >= startPlan.getTime()
                && addInDiary.getTime() <= mill ){
                int day = (int)(addInDiary.getTime() - startPlan.getTime()) / (24 * 60 * 60 * 1000);
//                Log.d("kkk", "removeRecipeForDietPlan: test day " + day);

                String dayS = String.valueOf(day), mealS, recipeNumberS;

                if (day >= plan.getRecipeForDays().size()) return;
                RecipeForDay recipeForDay = plan.getRecipeForDays().get(day);

                if (getRecipePosition(recipeForDay.getBreakfast(), eating.getName()) != null){
                    mealS = "breakfast";
                    recipeNumberS = getRecipePosition(recipeForDay.getBreakfast(), eating.getName());
                    WorkWithFirebaseDB.setRecipeInDiaryFromPlan(dayS, mealS, recipeNumberS, false);
                }
                if (getRecipePosition(recipeForDay.getLunch(), eating.getName()) != null){
                    mealS = "lunch";
                    recipeNumberS = getRecipePosition(recipeForDay.getLunch(), eating.getName());
                    WorkWithFirebaseDB.setRecipeInDiaryFromPlan(dayS, mealS, recipeNumberS, false);
                }
                if (getRecipePosition(recipeForDay.getDinner(), eating.getName()) != null){
                    mealS = "dinner";
                    recipeNumberS = getRecipePosition(recipeForDay.getDinner(), eating.getName());
                    WorkWithFirebaseDB.setRecipeInDiaryFromPlan(dayS, mealS, recipeNumberS, false);
                }
                if (getRecipePosition(recipeForDay.getSnack(), eating.getName()) != null){
                    mealS = "snack";
                    recipeNumberS = getRecipePosition(recipeForDay.getSnack(), eating.getName());
                    WorkWithFirebaseDB.setRecipeInDiaryFromPlan(dayS, mealS, recipeNumberS, false);
                }
            }
        }

        if (updateCallback != null) {
            updateCallback.update();
        }
    }

    private String getRecipePosition(List<RecipeItem> recipeItemList, String name){

        for (int i = 0; i < recipeItemList.size(); i++){
            if(recipeItemList.get(i).getName().equals(name)){
                recipeItemList.get(i).setAddedInDiaryFromPlan(false);
                return String.valueOf(i);
            }
        }

        return null;
    }

}



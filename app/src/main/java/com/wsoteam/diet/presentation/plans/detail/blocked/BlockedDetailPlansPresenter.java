package com.wsoteam.diet.presentation.plans.detail.blocked;



import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.wsoteam.diet.DietPlans.POJO.DietPlan;
import com.wsoteam.diet.Recipes.POJO.RecipesHolder;
import com.wsoteam.diet.Recipes.POJO.plan.PlansGroupsRecipe;
import com.wsoteam.diet.common.Analytics.Events;
import com.wsoteam.diet.presentation.global.BasePresenter;
import com.wsoteam.diet.presentation.global.Screens;

import ru.terrakok.cicerone.Router;

@InjectViewState
public class BlockedDetailPlansPresenter extends BasePresenter<BlockedDetailPlansView> {

    private DietPlan dietPlan;
    private Router router;
    PlansGroupsRecipe plansRecipe;

    public BlockedDetailPlansPresenter(Router router) {
        this.router = router;
    }

    void setDietPlan(DietPlan diet){
        this.dietPlan = diet;
        plansRecipe = new PlansGroupsRecipe(RecipesHolder.get(), dietPlan);
        getViewState().showData(dietPlan);
        Events.logViewPlan(diet.getFlag());
    }

    void clickedShare(){
        getViewState().sharePlan(dietPlan.getName() + "\n https://play.google.com/store/apps/details?id=com.wild.diet" );
    }

    void clickedPremButton(){
       router.navigateTo(new Screens.SubscriptionScreen());
    }

    void clickedClose(){
        router.exit();
    }

    PlansGroupsRecipe getRecipes(){
        return plansRecipe;
    }
}

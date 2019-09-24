package com.wsoteam.diet.presentation.plans.detail;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.arellomobile.mvp.InjectViewState;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.DietPlans.POJO.DietPlan;
import com.wsoteam.diet.Recipes.POJO.RecipeItem;
import com.wsoteam.diet.Recipes.POJO.RecipesHolder;
import com.wsoteam.diet.Recipes.POJO.plan.PlansGroupsRecipe;
import com.wsoteam.diet.Recipes.POJO.plan.RecipeForDay;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.common.Analytics.Events;
import com.wsoteam.diet.presentation.global.BasePresenter;
import com.wsoteam.diet.presentation.global.Screens;
import com.wsoteam.diet.presentation.plans.DateHelper;
import com.wsoteam.diet.presentation.plans.adapter.HorizontalDetailPlansAdapter;
import com.wsoteam.diet.presentation.plans.adapter.VerticalDetailPlansAdapter;

import java.util.Date;
import java.util.List;

import ru.terrakok.cicerone.Router;

@InjectViewState
public class DetailPlansPresenter extends BasePresenter<DetailPlansView> {

    Router router;
    Intent intent;
    DietPlan dietPlan;
    Context context;

    PlansGroupsRecipe plansRecipe;
    List<RecipeForDay> recipeForDays;
    VerticalDetailPlansAdapter adapter;
    boolean isCurrentPlan;
    HorizontalDetailPlansAdapter.OnItemClickListener adapterListener =
            new HorizontalDetailPlansAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(RecipeItem recipeItem, String day, String meal,
                                        String recipeNumber) {
                    Events.logViewRecipePlan(recipeItem.getName(), dietPlan.getFlag());
                    if (dietPlan.getDaysAfterStart() == Integer.parseInt(day)) {
                        router.navigateTo(
                                new Screens.PlanRecipeScreen(recipeItem, View.VISIBLE, day, meal, recipeNumber));
                    } else {
                        router.navigateTo(new Screens.PlanRecipeScreen(recipeItem, day, meal, recipeNumber));
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }

                @Override
                public void onClickGoAllPlans(View view) {
                    router.exit();
                    WorkWithFirebaseDB.leaveDietPlan();
                    UserDataHolder.getUserData().setPlan(null);
                }
            };

    public DetailPlansPresenter(Router router, Intent intent, Context context) {
        this.router = router;
        this.intent = intent;
        this.context = context;
        initDietPlan();
        Events.logViewPlan(dietPlan.getFlag());
    }

    private void initDietPlan() {
        dietPlan = (DietPlan) intent.getSerializableExtra(Config.DIETS_PLAN_INTENT);
        if (UserDataHolder.getUserData() != null
                && UserDataHolder.getUserData().getPlan() != null
                && UserDataHolder.getUserData().getPlan().getName().equals(dietPlan.getName())) {
            getViewState().visibilityButtonJoin(false);
            dietPlan = UserDataHolder.getUserData().getPlan();
            adapter = new VerticalDetailPlansAdapter(dietPlan, true);
            isCurrentPlan = true;
        } else {
            //RecipesHolder.get()
            plansRecipe = new PlansGroupsRecipe(RecipesHolder.get(), dietPlan);
            recipeForDays = plansRecipe.getRecipeForDays();
            dietPlan.setRecipes(getList(), plansRecipe.size());
            adapter = new VerticalDetailPlansAdapter(dietPlan);
            isCurrentPlan = false;
            getViewState().visibilityButtonLeave(false);

        }

        adapter.SetOnItemClickListener(adapterListener);
        getViewState().setAdapter(adapter);
    }

    void clickedClose() {
        router.exit();
    }

    List<RecipeForDay> getList() {
        return recipeForDays;
    }

    PlansGroupsRecipe getRecipes() {
        return plansRecipe;
    }

    void clickedJoin() {
        DietPlan plan = UserDataHolder.getUserData().getPlan();
        if (plan == null || plan.getDaysAfterStart() >= plan.getCountDays()) {
            joinPlans();
        } else {
            getViewState().startAlert(UserDataHolder.getUserData().getPlan().getName());
        }
    }

    void joinPlans() {
        Events.logConnectPlan(dietPlan.getFlag(), "def", 0);
        dietPlan.setStartDate(DateHelper.dateToString(new Date()));
        UserDataHolder.getUserData().setPlan(dietPlan);
        WorkWithFirebaseDB.joinDietPlan(dietPlan);
        getViewState().visibilityButtonJoin(false);
        getViewState().showAlertJoinToPlan();
        initDietPlan();
    }

    void clickedLeave() {
        Events.logPlanLeave(UserDataHolder.getUserData().getPlan().getFlag(), UserDataHolder.getUserData().getPlan().getDaysAfterStart());
        WorkWithFirebaseDB.leaveDietPlan();
        getViewState().visibilityButtonJoin(true);
        UserDataHolder.getUserData().setPlan(null);
        initDietPlan();
    }

    void clickedShare() {
        getViewState().sharePlan(
                dietPlan.getName() + "\n https://play.google.com/store/apps/details?id=com.wild.diet");
    }

    void onResume() {
        if (isCurrentPlan) {
            adapter.updateList(UserDataHolder.getUserData().getPlan().getRecipeForDays());
        }
    }
}

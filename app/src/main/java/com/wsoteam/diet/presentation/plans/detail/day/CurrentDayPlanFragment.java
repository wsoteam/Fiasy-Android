package com.wsoteam.diet.presentation.plans.detail.day;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.google.android.material.tabs.TabLayout;

import com.wsoteam.diet.BranchOfAnalyzer.Dialogs.AddFoodDialog;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.DietPlans.POJO.DietPlan;
import com.wsoteam.diet.MainScreen.Controller.UpdateCallback;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Recipes.POJO.RecipeItem;
import com.wsoteam.diet.Recipes.POJO.plan.RecipeForDay;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.common.Analytics.Events;
import com.wsoteam.diet.di.CiceroneModule;
import com.wsoteam.diet.model.Breakfast;
import com.wsoteam.diet.model.Dinner;
import com.wsoteam.diet.model.Lunch;
import com.wsoteam.diet.model.Snack;
import com.wsoteam.diet.presentation.global.Screens;
import com.wsoteam.diet.presentation.plans.DateHelper;
import com.wsoteam.diet.presentation.plans.browse.BrowsePlansActivity;
import com.wsoteam.diet.presentation.plans.detail.DetailPlansActivity;
import java.util.Calendar;
import java.util.List;
import ru.terrakok.cicerone.Router;

import static android.content.Context.MODE_PRIVATE;

public class CurrentDayPlanFragment extends MvpAppCompatFragment implements TabLayout.OnTabSelectedListener {

  @BindView(R.id.recycler) RecyclerView recyclerView;
  @BindView(R.id.tabs) TabLayout tabLayout;
  @BindView(R.id.clRecipes) ConstraintLayout activePlan;
  @BindView(R.id.clNotActivePlan) ConstraintLayout notActivePlan;
  @BindView(R.id.clFinishPlan) ConstraintLayout finishPlan;
  @BindView(R.id.tvPlanName) TextView planName;
  @BindView(R.id.textView154) TextView dayTextView;

  private LinearLayoutManager layoutManager;
  private CurrentDayPlanAdapter adapter;
  private RecipeForDay recipeForDay;
  private int day;
  private int daysPicked;
  private Router router;
  private long dateForShowRecipe;
  private UpdateCallback updateCallback;

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_current_day_plan,
        container, false);
    ButterKnife.bind(this, view);

    router = CiceroneModule.router();
    tabLayout.addOnTabSelectedListener(this);
    layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    recyclerView.setLayoutManager(layoutManager);

    adapter = new CurrentDayPlanAdapter();
    adapter.SetOnItemClickListener(mItemClickListener);

    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(adapter);
    initData(UserDataHolder.getUserData() != null ?
        UserDataHolder.getUserData().getPlan() : null);
    return view;
  }

  public void setUpdateCallback(@NonNull UpdateCallback updateCallback){
    this.updateCallback = updateCallback;
  }

  public void showRecipesForDate(long date){

    dateForShowRecipe = date;
    initData(UserDataHolder.getUserData().getPlan());
  }

  View.OnClickListener planListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      //TODO тут открываются рецепты плана с виджета на главном экране
     Intent intent = new Intent(getContext(), DetailPlansActivity.class);
     intent.putExtra(Config.DIETS_PLAN_INTENT, UserDataHolder.getUserData().getPlan());
    startActivity(intent);
    }
  };

  private void initData(DietPlan plan){

    if (plan != null){
      day = plan.getDaysAfterStart();

      if (day >= plan.getCountDays()) {
        activePlan.setVisibility(View.GONE);
        notActivePlan.setVisibility(View.GONE);
        finishPlan.setVisibility(View.VISIBLE);
        Events.logPlanComplete(plan.getFlag(), 0);
      }else {

        activePlan.setVisibility(View.VISIBLE);
        notActivePlan.setVisibility(View.GONE);
        finishPlan.setVisibility(View.GONE);
        planName.setOnClickListener(planListener);

        if (dateForShowRecipe == 0) {
          recipeForDay = plan.getRecipeForDays().get(day);
          daysPicked = day;
          planName.setText("\"" + plan.getName() + "\"");
          dayTextView.setText(
              String.format(getString(R.string.planDays), day + 1, plan.getCountDays()));
          onTabSelected(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()));
          daysPicked = -1;
        } else {

          if (dateForShowRecipe < DateHelper.stringToDate(plan.getStartDate()).getTime()){
            activePlan.setVisibility(View.GONE);
          }else {
            int days = (int)((dateForShowRecipe - DateHelper.stringToDate(plan.getStartDate()).getTime()) / (24 * 60 * 60 * 1000));
            //Log.d("kkk", "initData: " + days);
            if (days < plan.getCountDays()) {
              daysPicked = days;
              recipeForDay = plan.getRecipeForDays().get(days);
              daysPicked = days;
              planName.setText("\"" + plan.getName() + "\"");
              dayTextView.setText(
                  String.format(getString(R.string.planDays), days + 1, plan.getCountDays()));
              onTabSelected(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()));
            } else {
              activePlan.setVisibility(View.GONE);
            }
          }
        }
      }
    } else {
      recipeForDay = null;
      activePlan.setVisibility(View.GONE);
      notActivePlan.setVisibility(View.VISIBLE);
      finishPlan.setVisibility(View.GONE);
    }
  }

  CurrentDayPlanAdapter.OnItemClickListener mItemClickListener =
      new CurrentDayPlanAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(RecipeItem recipeItem, String days, String meal,
            String recipeNumber) {
          //Log.d("kkk", "onItemClick: " + day + "  -- " + days + " -- " + router);

          Screens.PlanRecipeScreen screen = new Screens.PlanRecipeScreen(recipeItem,
              daysPicked == day ? View.VISIBLE : View.GONE,
              days, meal, recipeNumber);
          getActivity().startActivity(screen.getActivityIntent(getContext()));

        }

        @Override public void onButtonClick(RecipeItem recipeItem, String day, String meal,
            String recipeNumber) {
          savePortion(recipeItem, day, meal, recipeNumber);

          if (updateCallback != null){
            updateCallback.update();
          }

        }
      };

  @Override
  public void onTabSelected(TabLayout.Tab tab) {

    boolean isCurrentDay = daysPicked == day;

    if (recipeForDay != null) {
      switch (tab.getPosition()) {
        case 0:
          adapter.updateList(recipeForDay.getBreakfast(), isCurrentDay, daysPicked, "breakfast");
          break;
        case 1:
          adapter.updateList(recipeForDay.getLunch(), isCurrentDay, daysPicked, "lunch");
          break;
        case 2:
          adapter.updateList(recipeForDay.getDinner(), isCurrentDay, daysPicked, "dinner");
          break;
        case 3:
          adapter.updateList(recipeForDay.getSnack(), isCurrentDay, daysPicked, "snack");
          break;
        default:
          adapter.updateList(recipeForDay.getBreakfast(), isCurrentDay, daysPicked, "breakfast");
      }
    }
  }

  @OnClick(R.id.tvViewcOtherPlans)
  void clickedOther(){
    WorkWithFirebaseDB.leaveDietPlan();
    UserDataHolder.getUserData().setPlan(null);
    getActivity().startActivity(new Intent(getContext(), BrowsePlansActivity.class));
  }

  @OnClick(R.id.tvViewPlans)
  void clickedViewPlans(){
    getActivity().startActivity(new Intent(getContext(), BrowsePlansActivity.class));
  }

  @Override public void onTabUnselected(TabLayout.Tab tab) {

  }

  @Override public void onTabReselected(TabLayout.Tab tab) {

  }

  @Override public void onResume() {
    super.onResume();
    initData(UserDataHolder.getUserData().getPlan());
  }


  private void savePortion(RecipeItem recipe, String dayPlan, String meal,
      String recipeNumber) {

    Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);

    int kcal = recipe.getCalories();
    int carbo = (int) recipe.getCarbohydrates();
    int prot = recipe.getPortions();
    int fat = (int) recipe.getFats();
    int weight = -1;

    String name = recipe.getName();
    String urlOfImage = recipe.getUrl();

    //Amplitude.getInstance().logEvent(AmplitudaEvents.success_add_food);
    switch (meal) {
      case "breakfast":
        Breakfast breakfast = new Breakfast(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year);

        WorkWithFirebaseDB.addBreakfast(breakfast);
        UserDataHolder.getUserData().getBreakfasts().put(System.currentTimeMillis() + "", breakfast);
        break;
      case "lunch":
        Lunch lunch = new Lunch(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year);
        WorkWithFirebaseDB.addLunch(lunch);
        UserDataHolder.getUserData().getLunches().put(System.currentTimeMillis() + "", lunch);
        break;
      case "dinner":
        Dinner dinner = new Dinner(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year);
        WorkWithFirebaseDB.addDinner(dinner);
        UserDataHolder.getUserData().getDinners().put(System.currentTimeMillis() + "", dinner);
        break;
      case "snack":
        Snack snack =new Snack(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year);
        WorkWithFirebaseDB.addSnack(snack);
        UserDataHolder.getUserData().getSnacks().put(System.currentTimeMillis() + "", snack);
        break;
    }

    WorkWithFirebaseDB.setRecipeInDiaryFromPlan(dayPlan, meal, recipeNumber, true);


    AlertDialog alertDialog = AddFoodDialog.createChoiseEatingAlertDialog(getContext());
    alertDialog.show();
    getActivity().getSharedPreferences(Config.IS_ADDED_FOOD, MODE_PRIVATE).edit()
        .putBoolean(Config.IS_ADDED_FOOD, true)
        .commit();
    new CountDownTimer(800, 100) {
      @Override
      public void onTick(long millisUntilFinished) {

      }

      @Override
      public void onFinish() {
        setAddedInDiaryFromPlan(recipeNumber);
        alertDialog.dismiss();
      }
    }.start();
  }

  private void setAddedInDiaryFromPlan(String recipeNumber) {

    List<RecipeItem> recipeItemList = adapter.getLisrRecipe();

    if (recipeItemList != null){
      RecipeItem recipeItem = recipeItemList.get(Integer.parseInt(recipeNumber));
      recipeItem.setAddedInDiaryFromPlan(true);
    }
    adapter.notifyDataSetChanged();
  }
}

package com.wsoteam.diet.Recipes.v2;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amplitude.api.Amplitude;
import com.bumptech.glide.Glide;
import com.wsoteam.diet.AmplitudaEvents;
import com.wsoteam.diet.BranchOfAnalyzer.Dialogs.AddFoodDialog;
import com.wsoteam.diet.common.Analytics.EventProperties;
import com.wsoteam.diet.common.Analytics.Events;
import com.wsoteam.diet.model.Breakfast;
import com.wsoteam.diet.model.Dinner;
import com.wsoteam.diet.model.Lunch;
import com.wsoteam.diet.model.Snack;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Recipes.POJO.RecipeItem;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecipeActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener{
    private final int BREAKFAST_POSITION = 0, LUNCH_POSITION = 1, DINNER_POSITION = 2, SNACK_POSITION = 3, EMPTY_FIELD = -1;
    @BindView(R.id.ivHead) ImageView ivHead;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvUsers) TextView tvTime;
    @BindView(R.id.llIngedientsItem) LinearLayout llIngredients;
    @BindView(R.id.llInstructions) LinearLayout llInstructions;
    @BindView(R.id.cvIngedients) CardView cvIngredients;
    @BindView(R.id.cvInstructions) CardView cvInstructions;
    @BindView(R.id.tvCarbohydrates) TextView tvCarbohydrates;
    @BindView(R.id.tvCellulose) TextView tvCellulose;
    @BindView(R.id.tvSugar) TextView tvSugar;
    @BindView(R.id.tvFat) TextView tvFat;
    @BindView(R.id.tvSaturatedFats) TextView tvSaturatedFats;
    @BindView(R.id.tvUnSaturatedFats) TextView tvUnSaturatedFats;
    @BindView(R.id.tvProtein) TextView tvProtein;
    @BindView(R.id.tvCholesterol) TextView tvCholesterol;
    @BindView(R.id.tvSodium) TextView tvSodium;
    @BindView(R.id.tvPotassium) TextView tvPotassium;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tvRecipeKK) TextView tvKkal;

    @BindView(R.id.tvComplexityField) TextView tvComplexityField;
    @BindView(R.id.tvComplexity) TextView tvComplexity;

    RecipeItem recipeItem;
    Window window;
    MenuItem favoriteMenuItem;

    private String key;
    HashMap<String, RecipeItem> favoriteRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);

        window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.parseColor("#66000000"));
        recipeItem = (RecipeItem) getIntent().getSerializableExtra(Config.RECIPE_INTENT);
        Events.logViewRecipe(recipeItem.getName());

        tvKkal.setText(recipeItem.getCalories() + " ккал на порцию");
        tvName.setText(recipeItem.getName());
        tvTime.setText(String.valueOf(recipeItem.getTime()));
        tvCarbohydrates.setText(String.valueOf(recipeItem.getCarbohydrates()));
        tvCellulose.setText(String.valueOf(recipeItem.getCellulose()));
        tvSugar.setText(String.valueOf(recipeItem.getSugar()));
        tvFat.setText(String.valueOf(recipeItem.getFats()));
        tvSaturatedFats.setText(String.valueOf(recipeItem.getSaturatedFats()));
        tvUnSaturatedFats.setText(String.valueOf(recipeItem.getUnSaturatedFats()));
        tvProtein.setText(String.valueOf(recipeItem.getPortions()));
        tvCholesterol.setText(String.valueOf(recipeItem.getCholesterol()));
        tvSodium.setText(String.valueOf(recipeItem.getSodium()));
        tvPotassium.setText(String.valueOf(recipeItem.getPotassium()));

        if (recipeItem.getComplexity() != null && !"".equals(recipeItem.getComplexity())){
            tvComplexityField.setVisibility(View.VISIBLE);
            tvComplexity.setVisibility(View.VISIBLE);
            tvComplexity.setText(recipeItem.getComplexity());
        }

        mToolbar.setTitleTextColor(0xFFFFFFFF);
        mToolbar.setPadding(0, dpToPx(24), 0, 0);
        mToolbar.setBackgroundColor(Color.parseColor("#32000000"));
        mToolbar.inflateMenu(R.menu.recipe_menu);
        Menu menu = mToolbar.getMenu();
        favoriteMenuItem = menu.findItem(R.id.mFavorites);


        mToolbar.setOnMenuItemClickListener(this);
        mToolbar.setNavigationIcon(R.drawable.back_arrow_icon_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if (recipeItem.getIngredients() != null) {
            int indexIngredients = 0;
            int borderIngredients = recipeItem.getIngredients().size();
            for (String ingredient :
                    recipeItem.getIngredients()) {
                indexIngredients++;
                View view = getLayoutInflater().inflate(R.layout.plan_recipes_ingredient, null);
                View line = getLayoutInflater().inflate(R.layout.line_horizontal, null);
                line.setPadding(0, 0, 0, 0);
                TextView textView = view.findViewById(R.id.tvIngredient);
                textView.setText(ingredient);
                llIngredients.addView(view);
                if (indexIngredients < borderIngredients) {
                    llIngredients.addView(line);
                }
            }
        } else {cvIngredients.setVisibility(View.GONE);}

        if (recipeItem.getInstruction() != null) {
            int indexInstruction = 0;
            int borderInstruction = recipeItem.getInstruction().size();
            for (String instruction :
                    recipeItem.getInstruction()) {
                indexInstruction++;
                View view = getLayoutInflater().inflate(R.layout.plan_recipes_instruction, null);
                View line = getLayoutInflater().inflate(R.layout.line_horizontal, null);
                line.setPadding(dpToPx(70), 0, 0, 0);
                TextView textView = view.findViewById(R.id.tvInstruction);
                textView.setText(instruction);
                llInstructions.addView(view);
                if (indexInstruction < borderInstruction) {
                    llInstructions.addView(line);
                }
            }
        } else {cvInstructions.setVisibility(View.GONE);}

        String url = recipeItem.getUrl();
        if (url == null || url.equals("link")) {
            url = "https://firebasestorage.googleapis.com/v0/b/diet-for-test.appspot.com/o/loading.jpg?alt=media&token=f1b6fe6d-57e3-4bca-8be3-9ebda9dc715e";
        }

        Glide.with(this).load(url).into(ivHead);


        checkFavorite();
    }

    private void checkFavorite(){
        if (UserDataHolder.getUserData() != null &&
                UserDataHolder.getUserData().getFavoriteRecipes() != null){
            favoriteRecipes = UserDataHolder.getUserData().getFavoriteRecipes();

            for (Map.Entry<String, RecipeItem> e : favoriteRecipes.entrySet()) {
                if (recipeItem.getName().equals(e.getValue().getName())){
                    this.key = e.getKey();
                    favoriteMenuItem.setIcon(R.drawable.icon_favorites_delete);
                    return;
                }
            }
        }
    }

    public int dpToPx(int dp) {
        float density = getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    private int statusBarHeight(android.content.res.Resources res) {
        return (int) (24 * res.getDisplayMetrics().density);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.mShare:
                Intent  i = new Intent(
                        android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_TEXT, recipeToString(recipeItem));
                startActivity(Intent.createChooser(i, getResources().getString(R.string.titleShareDialogRecipe)));
                return true;
            case R.id.mFavorites:
                if (key == null) {
                    Log.e("LOL", "fav");
                    Events.logAddFavoriteRecipe(recipeItem.getName());
                    key = WorkWithFirebaseDB.addFavoriteRecipe(recipeItem);
                    favoriteMenuItem.setIcon(R.drawable.icon_favorites_delete);
//                    SuccessfulAlertDialog.start(this, "Рецепт добавлен в избранное!");

                } else {
                    WorkWithFirebaseDB.deleteFavoriteRecipe(key);
                    favoriteMenuItem.setIcon(R.drawable.icon_favorites);
//                    SuccessfulAlertDialog.start(this, "Рецепт удален из избранного!");
                    key = null;
                }

                return true;
        }
        return false;

    }

    private String recipeToString(RecipeItem recipe){

        String result = recipe.getName() + "\n" +
                        "Время готовки: " + recipe.getTime() + " минут" + "\n";
        for (String step:
             recipe.getInstruction()) {
            result += step + "\n";
        }

        return result + "https://play.google.com/store/apps/details?id=com.wild.diet";
    }


    @OnClick(R.id.addDiary)
    public void onViewClicked(View view) {

        new AlertDialogChoiseEating().createChoiseEatingAlertDialog(this).show();

    }


    public class AlertDialogChoiseEating {

        public AlertDialog createChoiseEatingAlertDialog(Context context) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            AlertDialog alertDialog = builder.create();
            View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_choise_eating_type, null);

            ImageButton ibChoiseEatingBreakFast = view.findViewById(R.id.ibChoiseEatingBreakFast);
            ImageButton ibChoiseEatingLunch = view.findViewById(R.id.ibChoiseEatingLunch);
            ImageButton ibChoiseEatingDinner = view.findViewById(R.id.ibChoiseEatingDinner);
            ImageButton ibChoiseEatingSnack = view.findViewById(R.id.ibChoiseEatingSnack);

            ibChoiseEatingBreakFast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    datePicker(BREAKFAST_POSITION);
                    alertDialog.cancel();
                }
            });
            ibChoiseEatingLunch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    datePicker(LUNCH_POSITION);
                    alertDialog.cancel();
                }
            });
            ibChoiseEatingDinner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    datePicker(DINNER_POSITION);
                    alertDialog.cancel();
                }
            });
            ibChoiseEatingSnack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    datePicker(SNACK_POSITION);
                    alertDialog.cancel();
                }
            });

            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            alertDialog.setView(view);
            return alertDialog;
        }
    }

    private void datePicker(int idOfEating) {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int year, int month, int day) {
                        //this condition is necessary to work properly on all android versions
//                        if(view.isShown()){
                        savePortion(idOfEating, recipeItem, year, month, day);
//                        }
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    private void savePortion(int idOfEating, RecipeItem recipe, int year, int month, int day) {

        int kcal = recipe.getCalories();
        int carbo = (int) recipe.getCarbohydrates();
        int prot = recipe.getPortions();
        int fat = (int) recipe.getFats();
        int weight = -1;

        String name = recipe.getName();
        String urlOfImage = recipe.getUrl();

        switch (idOfEating) {
            case BREAKFAST_POSITION:
                WorkWithFirebaseDB.
                        addBreakfast(new Breakfast(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year));
                Events.logAddRecipeInDiary(EventProperties.recipe_intake_breakfast, name);
                break;
            case LUNCH_POSITION:
                WorkWithFirebaseDB.
                        addLunch(new Lunch(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year));
                Events.logAddRecipeInDiary(EventProperties.recipe_intake_lunch, name);
                break;
            case DINNER_POSITION:
                WorkWithFirebaseDB.
                        addDinner(new Dinner(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year));
                Events.logAddRecipeInDiary(EventProperties.recipe_intake_dinner, name);
                break;
            case SNACK_POSITION:
                WorkWithFirebaseDB.
                        addSnack(new Snack(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year));
                Events.logAddRecipeInDiary(EventProperties.recipe_intake_snack, name);
                break;
        }
        AlertDialog alertDialog = AddFoodDialog.createChoiseEatingAlertDialog(this);
        alertDialog.show();
        getSharedPreferences(Config.IS_ADDED_FOOD, MODE_PRIVATE).edit().putBoolean(Config.IS_ADDED_FOOD, true).commit();
        new CountDownTimer(800, 100) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                alertDialog.dismiss();
                onBackPressed();
            }
        }.start();
    }

}

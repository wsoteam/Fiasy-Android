package com.wsoteam.diet.BranchOfAnalyzer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.bumptech.glide.Glide;
import com.wsoteam.diet.AmplitudaEvents;
import com.wsoteam.diet.Authenticate.POJO.Box;
import com.wsoteam.diet.BranchOfAnalyzer.Dialogs.AddFoodDialog;
import com.wsoteam.diet.BranchOfAnalyzer.Dialogs.ClaimForm;
import com.wsoteam.diet.BranchOfAnalyzer.POJOFoodSQL.Food;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.InApp.ActivitySubscription;
import com.wsoteam.diet.POJOProfile.FavoriteFood;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.common.Analytics.EventProperties;
import com.wsoteam.diet.common.Analytics.Events;
import com.wsoteam.diet.model.Breakfast;
import com.wsoteam.diet.model.Dinner;
import com.wsoteam.diet.model.Lunch;
import com.wsoteam.diet.model.Snack;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.intercom.android.sdk.Intercom;

public class ActivityDetailOfFood extends AppCompatActivity {
    private final int BREAKFAST_POSITION = 0, LUNCH_POSITION = 1, DINNER_POSITION = 2, SNACK_POSITION = 3, EMPTY_FIELD = -1;
    @BindView(R.id.spnFood) Spinner spnFood;
    @BindView(R.id.tvActivityDetailOfFoodCollapsingTitle) TextView tvTitle;
    @BindView(R.id.tvActivityDetailOfFoodCalculateKcal) TextView tvCalculateKcal;
    @BindView(R.id.tvActivityDetailOfFoodCalculateFat) TextView tvCalculateFat;
    @BindView(R.id.tvActivityDetailOfFoodCalculateCarbo) TextView tvCalculateCarbohydrates;
    @BindView(R.id.tvActivityDetailOfFoodCalculateProtein) TextView tvCalculateProtein;
    @BindView(R.id.edtActivityDetailOfFoodPortion) EditText edtWeight;
    @BindView(R.id.ibAddFavorite) ImageButton ibAddFavorite;
    @BindView(R.id.tvLabelCellulose) TextView tvLabelCellulose;
    @BindView(R.id.tvLabelSugar) TextView tvLabelSugar;
    @BindView(R.id.tvLabelFats) TextView tvLabelFats;
    @BindView(R.id.tvLabelSaturated) TextView tvLabelSaturated;
    @BindView(R.id.tvLabelMonoUnSaturated) TextView tvLabelMonoUnSaturated;
    @BindView(R.id.tvLabelPolyUnSaturated) TextView tvLabelPolyUnSaturated;
    @BindView(R.id.tvLabelСholesterol) TextView tvLabelСholesterol;
    @BindView(R.id.tvLabelSodium) TextView tvLabelSodium;
    @BindView(R.id.tvLabelPotassium) TextView tvLabelPotassium;
    @BindView(R.id.tvBrand) TextView tvBrand;
    @BindView(R.id.tvCarbohydrates) TextView tvCarbohydrates;
    @BindView(R.id.tvCellulose) TextView tvCellulose;
    @BindView(R.id.tvSugar) TextView tvSugar;
    @BindView(R.id.tvFats) TextView tvFats;
    @BindView(R.id.tvProteins) TextView tvProteins;
    @BindView(R.id.tvSaturated) TextView tvSaturated;
    @BindView(R.id.tvPolyUnSaturated) TextView tvPolyUnSaturated;
    @BindView(R.id.tvMonoUnSaturated) TextView tvMonoUnSaturated;
    @BindView(R.id.tvСholesterol) TextView tvСholesterol;
    @BindView(R.id.tvSodium) TextView tvSodium;
    @BindView(R.id.tvPotassium) TextView tvPotassium;
    @BindView(R.id.tvDj) TextView tvDj;
    @BindView(R.id.tvKcal) TextView tvKcal;
    @BindView(R.id.btnPremCell) TextView btnPremCell;
    @BindView(R.id.btnPremSugar) TextView btnPremSugar;
    @BindView(R.id.btnPremSaturated) TextView btnPremSaturated;
    @BindView(R.id.btnPremMonoUnSaturated) TextView btnPremMonoUnSaturated;
    @BindView(R.id.btnPremPolyUnSaturated) TextView btnPremPolyUnSaturated;
    @BindView(R.id.btnPremCholy) TextView btnPremCholy;
    @BindView(R.id.btnPremSod) TextView btnPremSod;
    @BindView(R.id.btnPremPot) TextView btnPremPot;
    @BindView(R.id.cardView6) CardView cardView6;

    private Food foodItem;
    private boolean isFavorite = false, isOwnFood = false;
    private FavoriteFood currentFavorite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_of_food);
        ButterKnife.bind(this);

        int[] viewList = new int[]{R.id.tvCellulose, R.id.tvSugar, R.id.tvSaturated, R.id.tvСholesterol, R.id.tvSodium,
                R.id.tvPotassium, R.id.tvMonoUnSaturated, R.id.tvPolyUnSaturated,
                R.id.tvLabelCellulose, R.id.tvLabelSugar, R.id.tvLabelSaturated, R.id.tvLabelMonoUnSaturated, R.id.tvLabelPolyUnSaturated,
                R.id.tvLabelСholesterol, R.id.tvLabelSodium, R.id.tvLabelPotassium, R.id.btnPremCell, R.id.btnPremSugar, R.id.btnPremSaturated,
                R.id.btnPremMonoUnSaturated, R.id.btnPremPolyUnSaturated, R.id.btnPremCholy, R.id.btnPremSod, R.id.btnPremPot};

        for (int viewId : viewList) {
            findViewById(viewId).setVisibility(View.GONE);
        }

        foodItem = (Food) getIntent().getSerializableExtra(Config.INTENT_DETAIL_FOOD);
        isOwnFood = getIntent().getBooleanExtra(Config.TAG_OWN_FOOD, false);
        bindFields();
        bindSpinnerChoiceEating();
        cardView6.setBackgroundResource(R.drawable.shape_calculate);
        getFavoriteFood();

        edtWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("-")) {
                    edtWeight.setText("");
                } else {
                    if (!edtWeight.getText().toString().equals("")) {
                        calculateMainParameters(charSequence);
                    } else {
                        tvCalculateProtein.setText("0 " + getString(R.string.gramm));
                        tvCalculateKcal.setText("0 " + getString(R.string.kcal));
                        tvCalculateCarbohydrates.setText("0 " + getString(R.string.gramm));
                        tvCalculateFat.setText("0 " + getString(R.string.gramm));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Events.logViewFood(foodItem.getName());

    }

    private void getFavoriteFood() {
        if (UserDataHolder.getUserData() != null
                && UserDataHolder.getUserData().getFoodFavorites() != null
                && UserDataHolder.getUserData().getFoodFavorites().size() != 0) {
            Iterator iterator = UserDataHolder.getUserData().getFoodFavorites().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                if (((FavoriteFood) pair.getValue()).getName().equals(foodItem.getName()) && ((FavoriteFood) pair.getValue()).getBrand().equals(foodItem.getBrand())) {
                    currentFavorite = (FavoriteFood) pair.getValue();
                    isFavorite = true;
                    Glide.with(this).load(R.drawable.ic_fill_favorite).into(ibAddFavorite);
                    return;
                }
            }

        }
    }


    private void bindSpinnerChoiceEating() {
        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                R.layout.item_spinner_food_search, getResources().getStringArray(R.array.eatingList));
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown_food_search);
        spnFood.setAdapter(adapter);
        spnFood.setSelection(getIntent().getIntExtra(Config.TAG_CHOISE_EATING, 0));
    }

    private void bindFields() {
        tvTitle.setText(foodItem.getName().toUpperCase());
        tvFats.setText(String.valueOf(Math.round(foodItem.getFats() * 100)) + " г");
        tvCarbohydrates.setText(String.valueOf(Math.round(foodItem.getCarbohydrates() * 100)) + " г");
        tvProteins.setText(String.valueOf(Math.round(foodItem.getProteins() * 100)) + " г");
        tvKcal.setText(String.valueOf(Math.round(foodItem.getCalories() * 100)));
        tvDj.setText(String.valueOf(Math.round(foodItem.getKilojoules() * 100)));

        if (foodItem.getBrand() != null && !foodItem.getBrand().equals("")) {
            tvBrand.setText("(" + foodItem.getBrand() + ")");
        }

        if (foodItem.getSugar() != EMPTY_FIELD) {
            tvLabelSugar.setVisibility(View.VISIBLE);
            tvSugar.setVisibility(View.VISIBLE);
            tvSugar.setText(String.valueOf(Math.round(foodItem.getSugar() * 100)) + " г");
            if (!isPremiumUser()) {
                btnPremSugar.setVisibility(View.VISIBLE);
            }
        }
        if (foodItem.getSaturatedFats() != EMPTY_FIELD) {
            tvLabelSaturated.setVisibility(View.VISIBLE);
            tvSaturated.setVisibility(View.VISIBLE);
            tvSaturated.setText(String.valueOf(Math.round(foodItem.getSaturatedFats() * 100)) + " г");
            if (!isPremiumUser()) {
                btnPremSaturated.setVisibility(View.VISIBLE);
            }
        }
        if (foodItem.getMonoUnSaturatedFats() != EMPTY_FIELD) {
            tvLabelMonoUnSaturated.setVisibility(View.VISIBLE);
            tvMonoUnSaturated.setVisibility(View.VISIBLE);
            tvMonoUnSaturated.setText(String.valueOf(Math.round(foodItem.getMonoUnSaturatedFats() * 100)) + " г");
            if (!isPremiumUser()) {
                btnPremMonoUnSaturated.setVisibility(View.VISIBLE);
            }
        }
        if (foodItem.getPolyUnSaturatedFats() != EMPTY_FIELD) {
            tvLabelPolyUnSaturated.setVisibility(View.VISIBLE);
            tvPolyUnSaturated.setVisibility(View.VISIBLE);
            tvPolyUnSaturated.setText(String.valueOf(Math.round(foodItem.getPolyUnSaturatedFats() * 100)) + " г");
            if (!isPremiumUser()) {
                btnPremPolyUnSaturated.setVisibility(View.VISIBLE);
            }
        }
        if (foodItem.getCholesterol() != EMPTY_FIELD) {
            tvLabelСholesterol.setVisibility(View.VISIBLE);
            tvСholesterol.setVisibility(View.VISIBLE);
            tvСholesterol.setText(String.valueOf(Math.round(foodItem.getCholesterol() * 100)) + " мг");
            if (!isPremiumUser()) {
                btnPremCholy.setVisibility(View.VISIBLE);
            }
        }
        if (foodItem.getCellulose() != EMPTY_FIELD) {
            tvLabelCellulose.setVisibility(View.VISIBLE);
            tvCellulose.setVisibility(View.VISIBLE);
            tvCellulose.setText(String.valueOf(Math.round(foodItem.getCellulose() * 100)) + " г");
            if (!isPremiumUser()) {
                btnPremCell.setVisibility(View.VISIBLE);
            }
        }
        if (foodItem.getSodium() != EMPTY_FIELD) {
            tvLabelSodium.setVisibility(View.VISIBLE);
            tvSodium.setVisibility(View.VISIBLE);
            tvSodium.setText(String.valueOf(Math.round(foodItem.getSodium() * 100)) + " мг");
            if (!isPremiumUser()) {
                btnPremSod.setVisibility(View.VISIBLE);
            }
        }
        if (foodItem.getPottassium() != EMPTY_FIELD) {
            tvLabelPotassium.setVisibility(View.VISIBLE);
            tvPotassium.setVisibility(View.VISIBLE);
            tvPotassium.setText(String.valueOf(Math.round(foodItem.getPottassium() * 100)) + " мг");
            if (!isPremiumUser()) {
                btnPremPot.setVisibility(View.VISIBLE);
            }
        }

        if (isOwnFood) {
            ibAddFavorite.setVisibility(View.GONE);
        }

    }

    private boolean isPremiumUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.STATE_BILLING, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(Config.STATE_BILLING, false) || isOwnFood) {
            return true;
        } else {
            return false;
        }
    }

    private void savePortion(int idOfEating) {
        String food_intake = "";

        String wholeDate = getIntent().getStringExtra(Config.INTENT_DATE_FOR_SAVE);
        String[] arrayOfNumbersForDate = wholeDate.split("\\.");

        int day = Integer.parseInt(arrayOfNumbersForDate[0]);
        int month = Integer.parseInt(arrayOfNumbersForDate[1]) - 1;
        int year = Integer.parseInt(arrayOfNumbersForDate[2]);

        int kcal = Integer.parseInt(tvCalculateKcal.getText().toString().split(" ")[0]);
        int carbo = Integer.parseInt(tvCalculateCarbohydrates.getText().toString().split(" ")[0]);
        int prot = Integer.parseInt(tvCalculateProtein.getText().toString().split(" ")[0]);
        int fat = Integer.parseInt(tvCalculateFat.getText().toString().split(" ")[0]);

        int weight = Integer.parseInt(edtWeight.getText().toString());

        String name = foodItem.getName();
        String urlOfImage = "empty_url";

        switch (spnFood.getSelectedItemPosition()) {
            case BREAKFAST_POSITION:
                WorkWithFirebaseDB.
                        addBreakfast(new Breakfast(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year));
                food_intake = EventProperties.food_intake_breakfast;
                break;
            case LUNCH_POSITION:
                WorkWithFirebaseDB.
                        addLunch(new Lunch(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year));
                food_intake = EventProperties.food_intake_lunch;
                break;
            case DINNER_POSITION:
                WorkWithFirebaseDB.
                        addDinner(new Dinner(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year));
                food_intake = EventProperties.food_intake_dinner;
                break;
            case SNACK_POSITION:
                WorkWithFirebaseDB.
                        addSnack(new Snack(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year));
                food_intake = EventProperties.food_intake_snack;
                break;
        }
        String food_category = getFoodCategory();
        String food_item = foodItem.getName();
        food_item += " - " + foodItem.getBrand();
        String food_date = getDateType(day, month, year);
        Events.logAddFood(food_intake, food_category, food_date, food_item, kcal, weight);

        AlertDialog alertDialog = AddFoodDialog.createChoiseEatingAlertDialog(ActivityDetailOfFood.this);
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

    private String getDateType(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        if (currentDay == day && currentMonth == month && currentYear == year) {
            return EventProperties.food_date_today;
        } else if (currentDay > day && currentMonth >= month && currentYear >= year) {
            return EventProperties.food_date_future;
        } else {
            return EventProperties.food_date_past;
        }
    }

    private String getFoodCategory() {
        if (isFavorite) {
            return EventProperties.food_category_favorites;
        } else if (isOwnFood) {
            return EventProperties.food_category_custom;
        } else {
            return EventProperties.food_category_base;
        }
    }


    private void calculateMainParameters(CharSequence stringPortion) {
        double portion = Double.parseDouble(stringPortion.toString());

        tvCalculateProtein.setText(String.valueOf(Math.round(portion * foodItem.getProteins())) + " " + getString(R.string.gramm));
        tvCalculateKcal.setText(String.valueOf(Math.round(portion * foodItem.getCalories())) + " " + getString(R.string.kcal));
        tvCalculateCarbohydrates.setText(String.valueOf(Math.round(portion * foodItem.getCarbohydrates())) + " " + getString(R.string.gramm));
        tvCalculateFat.setText(String.valueOf(Math.round(portion * foodItem.getFats())) + " " + getString(R.string.gramm));
    }

    @OnClick({R.id.btnSaveEating, R.id.ivBack, R.id.btnPremCell, R.id.btnPremCholy,
            R.id.btnPremMonoUnSaturated, R.id.btnPremPolyUnSaturated, R.id.btnPremPot, R.id.btnPremSaturated, R.id.btnPremSod,
            R.id.btnPremSugar, R.id.tvSendClaim, R.id.ibShareFood, R.id.ibSendClaim, R.id.ibAddFavorite})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSaveEating:
                if (edtWeight.getText().toString().equals("")
                        || edtWeight.getText().toString().equals(" ")
                        || Integer.parseInt(edtWeight.getText().toString()) == 0) {
                    Toast.makeText(ActivityDetailOfFood.this, R.string.input_weight_of_eating, Toast.LENGTH_SHORT).show();
                } else {
                    savePortion(getIntent().getIntExtra(Config.TAG_CHOISE_EATING, 0));
                }
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.btnPremCell:
                showPremiumScreen();
                break;
            case R.id.btnPremCholy:
                showPremiumScreen();
                break;
            case R.id.btnPremMonoUnSaturated:
                showPremiumScreen();
                break;
            case R.id.btnPremPolyUnSaturated:
                showPremiumScreen();
                break;
            case R.id.btnPremPot:
                showPremiumScreen();
                break;
            case R.id.btnPremSaturated:
                showPremiumScreen();
                break;
            case R.id.btnPremSod:
                showPremiumScreen();
                break;
            case R.id.btnPremSugar:
                showPremiumScreen();
                break;
            case R.id.tvSendClaim:
                ClaimForm.createChoiseEatingAlertDialog(this, foodItem);
                break;
            case R.id.ibShareFood:
                shareFood(foodItem);
                break;
            case R.id.ibSendClaim:
                ClaimForm.createChoiseEatingAlertDialog(this, foodItem);
                break;
            case R.id.ibAddFavorite:
                if (isFavorite) {
                    isFavorite = false;
                    Glide.with(this).load(R.drawable.ic_empty_favorite).into(ibAddFavorite);
                    WorkWithFirebaseDB.deleteFavorite(currentFavorite.getKey());
                } else {
                    isFavorite = true;
                    Glide.with(this).load(R.drawable.ic_fill_favorite).into(ibAddFavorite);
                    currentFavorite = new FavoriteFood(foodItem.getId(), foodItem.getFullInfo(), addFavorite());
                }
                break;
        }
    }

    private void shareFood(Food foodItem) {
        Amplitude.getInstance().logEvent(Events.PRODUCT_PAGE_SHARE);
        Intercom.client().logEvent(Events.PRODUCT_PAGE_SHARE);
        String forSend;
        if (foodItem.getBrand() == null) {
            forSend = foodItem.getName()
                    + String.valueOf(foodItem.getCalories() * 100) + " Ккал. Узнайте % микроэлементов в продукте" +
                    " в дневнике питания Fiasy \n" + "https://play.google.com/store/apps/details?id=" + getPackageName();
        } else {
            forSend = foodItem.getName() + " (" + foodItem.getBrand() + ") - "
                    + String.valueOf(foodItem.getCalories() * 100) + " Ккал. Узнайте % микроэлементов в продукте" +
                    " в дневнике питания Fiasy \n" + "https://play.google.com/store/apps/details?id=" + getPackageName();
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, forSend);
        startActivity(intent);
    }

    private void showPremiumScreen() {
        Amplitude.getInstance().logEvent(Events.PRODUCT_PAGE_MICRO);
        Intercom.client().logEvent(Events.PRODUCT_PAGE_MICRO);
        Intent intent = new Intent(ActivityDetailOfFood.this, ActivitySubscription.class);
        Box box = new Box(AmplitudaEvents.view_prem_elements, EventProperties.trial_from_elements, false,
                true, null, false);
        intent.putExtra(Config.TAG_BOX, box);
        startActivity(intent);
    }

    private String addFavorite() {
        Events.logAddFavorite(foodItem.getName());
        FavoriteFood favoriteFood = new FavoriteFood(foodItem.getId(), foodItem.getFullInfo(), "empty", foodItem.getName(), foodItem.getBrand(),
                foodItem.getPortion(), foodItem.isLiquid(), foodItem.getKilojoules(), foodItem.getCalories(), foodItem.getProteins(), foodItem.getCarbohydrates(),
                foodItem.getSugar(), foodItem.getFats(), foodItem.getSaturatedFats(), foodItem.getMonoUnSaturatedFats(), foodItem.getPolyUnSaturatedFats(), foodItem.getCholesterol(),
                foodItem.getCellulose(), foodItem.getSodium(), foodItem.getPottassium(), foodItem.getPercentCarbohydrates(), foodItem.getPercentFats(), foodItem.getPercentProteins());
        String key = WorkWithFirebaseDB.addFoodFavorite(favoriteFood);
        return key;
    }

}

package com.wsoteam.diet.BranchOfAnalyzer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.wsoteam.diet.AmplitudaEvents;
import com.wsoteam.diet.BranchOfAnalyzer.Dialogs.AddFoodDialog;
import com.wsoteam.diet.model.Breakfast;
import com.wsoteam.diet.model.Dinner;
import com.wsoteam.diet.model.Eating;
import com.wsoteam.diet.model.Lunch;
import com.wsoteam.diet.model.Snack;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityDetailSavedFood extends AppCompatActivity {
    @BindView(R.id.tvActivityDetailOfFoodCollapsingTitle) TextView tvTitle;
    @BindView(R.id.tvActivityDetailOfFoodCalculateKcal) TextView tvCalculateKcal;
    @BindView(R.id.tvActivityDetailOfFoodCalculateFat) TextView tvCalculateFat;
    @BindView(R.id.tvActivityDetailOfFoodCalculateCarbo) TextView tvCalculateCarbohydrates;
    @BindView(R.id.tvActivityDetailOfFoodCalculateProtein) TextView tvCalculateProtein;
    @BindView(R.id.edtActivityDetailOfFoodPortion) EditText edtWeight;
    @BindView(R.id.cardView6) CardView cardView6;

    @BindView(R.id.tvCarbohydrates) TextView tvCarbohydrates;
    @BindView(R.id.tvFats) TextView tvFats;
    @BindView(R.id.tvProteins) TextView tvProteins;
    @BindViews({R.id.tvCellulose, R.id.tvSugar, R.id.tvSaturated, R.id.tvСholesterol, R.id.tvSodium,
            R.id.tvPotassium, R.id.tvMonoUnSaturated, R.id.tvPolyUnSaturated,
            R.id.tvLabelCellulose, R.id.tvLabelSugar, R.id.tvLabelSaturated, R.id.tvLabelMonoUnSaturated, R.id.tvLabelPolyUnSaturated,
            R.id.tvLabelСholesterol, R.id.tvLabelSodium, R.id.tvLabelPotassium, R.id.btnPremCell, R.id.btnPremSugar, R.id.btnPremSaturated,
            R.id.btnPremMonoUnSaturated, R.id.btnPremPolyUnSaturated, R.id.btnPremCholy, R.id.btnPremSod, R.id.btnPremPot})
    List<View> viewList;

    private final int BREAKFAST_POSITION = 0, LUNCH_POSITION = 1, DINNER_POSITION = 2, SNACK_POSITION = 3, EMPTY_FIELD = -1;
    private Eating foodItem;
    private double calories = 0, proteins = 0, carbo = 0, fats = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_of_food);
        ButterKnife.bind(this);
        foodItem = (Eating) getIntent().getSerializableExtra(Config.INTENT_DETAIL_FOOD);
        reCalculate();
        bindFields();
        ButterKnife.apply(viewList, (view, value, index) -> view.setVisibility(value), View.GONE);
        cardView6.setBackgroundResource(R.drawable.shape_calculate);

        edtWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals(" ")
                        || charSequence.toString().equals("-")) {
                    edtWeight.setText("0");
                } else {
                    if (!edtWeight.getText().toString().equals("")) {
                        calculateMainParameters(charSequence);
                    } else {
                        tvCalculateProtein.setText("0 " + getString(R.string.gramm));
                        tvCalculateKcal.setText("= 0 " + getString(R.string.kcal));
                        tvCalculateCarbohydrates.setText("0 " + getString(R.string.gramm));
                        tvCalculateFat.setText("0 " + getString(R.string.gramm));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Amplitude.getInstance().logEvent(AmplitudaEvents.view_detail_food);

    }

    private void reCalculate() {
        double weight = foodItem.getWeight();
        calories = ((double) foodItem.getCalories()) / weight;
        carbo = ((double) foodItem.getCarbohydrates()) / weight;
        fats = ((double) foodItem.getFat()) / weight;
        proteins = ((double) foodItem.getProtein()) / weight;
    }

    private void bindFields() {
        tvTitle.setText(foodItem.getName());
        tvFats.setText(String.valueOf(Math.round(fats * 100)) + " г");
        tvCarbohydrates.setText(String.valueOf(Math.round(carbo * 100)) + " г");
        tvProteins.setText(String.valueOf(Math.round(proteins * 100)) + " г");
    }

    private void savePortion(int idOfEating) {

        int day = foodItem.getDay();
        int month = foodItem.getMonth();
        int year = foodItem.getYear();

        int kcal = Integer.parseInt(tvCalculateKcal.getText().toString().split(" ")[0]);
        int carbo = Integer.parseInt(tvCalculateCarbohydrates.getText().toString().split(" ")[0]);
        int prot = Integer.parseInt(tvCalculateProtein.getText().toString().split(" ")[0]);
        int fat = Integer.parseInt(tvCalculateFat.getText().toString().split(" ")[0]);

        int weight = Integer.parseInt(edtWeight.getText().toString());

        String name = foodItem.getName();
        String urlOfImage = "empty_url";

        switch (idOfEating) {
            case BREAKFAST_POSITION:
                WorkWithFirebaseDB.
                        editBreakfast(new Breakfast(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year),
                                foodItem.getUrlOfImages());
                break;
            case LUNCH_POSITION:
                WorkWithFirebaseDB.
                        editLunch(new Lunch(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year),
                                foodItem.getUrlOfImages());
                break;
            case DINNER_POSITION:
                WorkWithFirebaseDB.
                        editDinner(new Dinner(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year),
                                foodItem.getUrlOfImages());
                break;
            case SNACK_POSITION:
                WorkWithFirebaseDB.
                        editSnack(new Snack(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year),
                                foodItem.getUrlOfImages());
                break;
        }
        AlertDialog alertDialog = AddFoodDialog.createChoiseEatingAlertDialog(ActivityDetailSavedFood.this);
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


    private void calculateMainParameters(CharSequence stringPortion) {
        double portion = Double.parseDouble(stringPortion.toString());

        tvCalculateProtein.setText(String.valueOf(Math.round(portion * proteins)) + " " + getString(R.string.gramm));
        tvCalculateKcal.setText("= " + String.valueOf(Math.round(portion * calories)) + " " + getString(R.string.kcal));
        tvCalculateCarbohydrates.setText(String.valueOf(Math.round(portion * carbo)) + " " + getString(R.string.gramm));
        tvCalculateFat.setText(String.valueOf(Math.round(portion * fats)) + " " + getString(R.string.gramm));

    }


    @OnClick({R.id.btnSaveEating, R.id.ivBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSaveEating:
                if (edtWeight.getText().toString().equals("")
                        || edtWeight.getText().toString().equals(" ")
                        || Integer.parseInt(edtWeight.getText().toString()) == 0) {
                    Toast.makeText(ActivityDetailSavedFood.this, R.string.input_weight_of_eating, Toast.LENGTH_SHORT).show();
                } else {
                    savePortion(getIntent().getIntExtra(Config.TAG_CHOISE_EATING, 0));
                }
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }
}

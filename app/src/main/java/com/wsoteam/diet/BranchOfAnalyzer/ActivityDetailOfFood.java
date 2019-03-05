package com.wsoteam.diet.BranchOfAnalyzer;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.wsoteam.diet.BranchOfAnalyzer.POJOEating.Eating;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.POJOFoodItem.FoodItem;
import com.wsoteam.diet.BranchOfAnalyzer.POJOEating.Breakfast;
import com.wsoteam.diet.BranchOfAnalyzer.POJOEating.Dinner;
import com.wsoteam.diet.BranchOfAnalyzer.POJOEating.Lunch;
import com.wsoteam.diet.BranchOfAnalyzer.POJOEating.Snack;
import com.wsoteam.diet.R;
import com.yandex.metrica.YandexMetrica;

import java.util.Calendar;

public class ActivityDetailOfFood extends AppCompatActivity {
    private TextView tvTitle, tvKcal,
            tvProtein, tvFat, tvCarbohydrates,
            tvCalculateFat, tvCalculateProtein, tvCalculateCarbohydrates, tvCalculateKcal, tvProperties;
    private DonutProgress pbCarbohydrates, pbFat, pbProtein;
    private EditText edtWeight;
    private Button btnSaveEating;

    private CardView cvDetailOfFoodProperties;

    private FoodItem foodItem;
    private final String TAG_OWN_PRODUCT = "OWN";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_of_food);

        foodItem = (FoodItem) getIntent().getSerializableExtra("ActivityDetailOfFood");


        tvTitle = findViewById(R.id.tvActivityDetailOfFoodCollapsingTitle);
        tvKcal = findViewById(R.id.tvActivityDetailOfFoodKcal);
        tvCarbohydrates = findViewById(R.id.tvActivityDetailOfFoodCountOfCarbohydrates);
        tvFat = findViewById(R.id.tvActivityDetailOfFoodCountOfFat);
        tvProtein = findViewById(R.id.tvActivityDetailOfFoodCountOfProtein);

        cvDetailOfFoodProperties = findViewById(R.id.cvDetailOfFoodProperties);

        btnSaveEating = findViewById(R.id.btnSaveEating);

        tvCalculateFat = findViewById(R.id.tvActivityDetailOfFoodCalculateFat);
        tvCalculateCarbohydrates = findViewById(R.id.tvActivityDetailOfFoodCalculateCarbo);
        tvCalculateKcal = findViewById(R.id.tvActivityDetailOfFoodCalculateKcal);
        tvCalculateProtein = findViewById(R.id.tvActivityDetailOfFoodCalculateProtein);

        tvProperties = findViewById(R.id.tvActivityDetailOfFoodProperties);

        edtWeight = findViewById(R.id.edtActivityDetailOfFoodPortion);

        pbCarbohydrates = findViewById(R.id.pbActivityDetailOfFoodCarbo);
        pbFat = findViewById(R.id.pbActivityDetailOfFoodFat);
        pbProtein = findViewById(R.id.pbActivityDetailOfFoodProtein);

        if (foodItem.getNameOfGroup().equals(TAG_OWN_PRODUCT)) {
            foodItem.setDescription("");
            foodItem.setComposition("");
            foodItem.setProperties("");
            cvDetailOfFoodProperties.setVisibility(View.GONE);
        }


        tvTitle.setText(foodItem.getName());
        tvKcal.setText(foodItem.getCalories() + " " + getString(R.string.for_100_g));
        tvFat.setText(getString(R.string.fat_detail) + " " + foodItem.getFat() + "" + getString(R.string.gramm));
        tvProtein.setText(getString(R.string.protein_detail) + " " + foodItem.getProtein() + " " + getString(R.string.gramm));
        tvCarbohydrates.setText(getString(R.string.carbohydrates_detail) + " " + foodItem.getCarbohydrates() + " " + getString(R.string.gramm));

        if (!foodItem.getComposition().equals(".")) {
            tvProperties.setText(foodItem.getComposition());
        }

        if (!foodItem.getDescription().equals(".")) {
            tvProperties.setText(tvProperties.getText().toString() + "\n" + foodItem.getDescription());
        }

        if (!foodItem.getProperties().equals(".")) {
            tvProperties.setText(tvProperties.getText().toString() + "\n" + foodItem.getProperties());
        }

        calculateNumbersForProgressBars();

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
                        calculateMainParameters();
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

        btnSaveEating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtWeight.getText().toString().equals("") || edtWeight.getText().toString().equals(" ")) {
                    Toast.makeText(ActivityDetailOfFood.this, R.string.input_weight_of_eating, Toast.LENGTH_SHORT).show();
                } else {
                    savePortion(getIntent().getStringExtra(Config.TAG_CHOISE_EATING));
                }

            }
        });
        YandexMetrica.reportEvent("Открыт экран: Детализация продукта группы - " + foodItem.getNameOfGroup());

    }

    private void savePortion(String stringExtra) {
        Calendar calendar = Calendar.getInstance();

        int kcal = Integer.parseInt(tvCalculateKcal.getText().toString().split(" ")[0]);
        int carbo = Integer.parseInt(tvCalculateCarbohydrates.getText().toString().split(" ")[0]);
        int prot = Integer.parseInt(tvCalculateProtein.getText().toString().split(" ")[0]);
        int fat = Integer.parseInt(tvCalculateFat.getText().toString().split(" ")[0]);

        int weight = Integer.parseInt(edtWeight.getText().toString());

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        String name = foodItem.getName();
        String urlOfImage = foodItem.getUrlOfImages();

        switch (stringExtra) {
            case Config.INTENT_CHOISE_BREAKFAST:
                new Breakfast(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year).save();
                break;
            case Config.INTENT_CHOISE_LUNCH:
                new Lunch(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year).save();
                break;
            case Config.INTENT_CHOISE_DINNER:
                new Dinner(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year).save();
                break;
            case Config.INTENT_CHOISE_SNACK:
                new Snack(name, urlOfImage, kcal, carbo, prot, fat, weight, day, month, year).save();
                break;
        }
        onBackPressed();
    }

    private void calculateMainParameters() {
        Double fat, carbohydrates, protein, kcal, partOfStartWeight;

        fat = Double.parseDouble(foodItem.getFat());
        carbohydrates = Double.parseDouble(foodItem.getCarbohydrates());
        protein = Double.parseDouble(foodItem.getProtein());
        kcal = Double.parseDouble(foodItem.getCalories());

        partOfStartWeight = Double.parseDouble(edtWeight.getText().toString()) / 100;

        fat = fat * partOfStartWeight;
        carbohydrates = carbohydrates * partOfStartWeight;
        protein = protein * partOfStartWeight;
        kcal = kcal * partOfStartWeight;

        tvCalculateProtein.setText(String.valueOf(protein.intValue()) + " " + getString(R.string.gramm));
        tvCalculateKcal.setText(String.valueOf(kcal.intValue()) + " " + getString(R.string.kcal));
        tvCalculateCarbohydrates.setText(String.valueOf(carbohydrates.intValue()) + " " + getString(R.string.gramm));
        tvCalculateFat.setText(String.valueOf(fat.intValue()) + " " + getString(R.string.gramm));

    }

    private void calculateNumbersForProgressBars() {
        Double fat, carbohydrates, protein;
        String maxPercent = "100";
        int maxCountForProgressBar = 100;

        fat = Double.parseDouble(foodItem.getFat());
        carbohydrates = Double.parseDouble(foodItem.getCarbohydrates());
        protein = Double.parseDouble(foodItem.getProtein());

        if (fat > maxCountForProgressBar) {
            pbFat.setDonut_progress(maxPercent);
        } else {
            pbFat.setDonut_progress(String.valueOf(Math.round(fat)));
        }
        if (carbohydrates > maxCountForProgressBar) {
            pbProtein.setDonut_progress(maxPercent);
        } else {
            pbProtein.setDonut_progress(String.valueOf(Math.round(protein)));
        }
        if (protein > maxCountForProgressBar) {
            pbCarbohydrates.setDonut_progress(maxPercent);
        } else {
            pbCarbohydrates.setDonut_progress(String.valueOf(Math.round(carbohydrates)));
        }

    }
}

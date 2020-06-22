package com.wsoteam.diet.model;

import android.content.Context;

import com.wsoteam.diet.POJOProfile.Profile;
import com.wsoteam.diet.R;

public class ModelFactory {

    public static Profile getDefaultProfile(Context context){
        Profile profile = new Profile();
        profile.setAge(22);
        profile.setDifficultyLevel("Средняя");
        profile.setExerciseStress("Малоактивный");
        profile.setFirstName(context.getString(R.string.default_name));
        profile.setLastName("default");
        profile.setGoLevel(0);
        profile.setGoal(0);
        profile.setHeight(170);
        profile.setFemale(true);
        profile.setLoseWeight(0.0);
        profile.setMaxCarbo(145);
        profile.setMaxFat(67);
        profile.setMaxKcal(1849);
        profile.setMaxProt(168);
        profile.setMaxWater(2);
        profile.setMonth(5);
        profile.setNumberOfDay(1);
        profile.setPhotoUrl("");
        profile.setWaterCount(2250);
        profile.setWeight(75);
        profile.setYear(2020);
        return profile;
    }
}

package com.wsoteam.diet.common.helpers;

import android.content.Context;

import com.wsoteam.diet.App;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.POJOProfile.Profile;
import com.wsoteam.diet.R;
import com.wsoteam.diet.presentation.main.water.WaterActivity;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.presentation.measurment.POJO.Weight;

import java.util.Calendar;

public class BodyCalculates {

    public static final String DEFAULT_FIRST_NAME = "default";
    public static final String DEFAULT_LAST_NAME = "default";
    public static final double DEFAULT_WEIGHT = 90;
    public static final int DEFAULT_HEIGHT = 180;
    public static final int DEFAULT_AGE = 27;

    private static final int WATER_ON_KG_FEMALE = 30;
    private static final int WATER_ON_KG_MALE = 40;
    private static final double RATE_NONE = 1.2;
    private static final double RATE_EASY = 1.375;
    private static final double RATE_MEDIUM = 1.4625;
    private static final double RATE_HARD = 1.55;
    private static final double RATE_UP_HARD = 1.6375;
    private static final double RATE_SUPER = 1.725;
    private static final double RATE_UP_SUPER = 1.9;
    private static final double COUNT_UP_LINE = 300;
    private static final double COUNT_DOWN_LINE = 500;

    public static Profile generateProfile(Context context) {
        return calculate(context, DEFAULT_WEIGHT, DEFAULT_HEIGHT, DEFAULT_AGE, false,
                context.getString(R.string.level_medium), context.getString(R.string.dif_level_normal));
    }

    public static Profile calculate(Context context, double weight, int height, int age,
                                    boolean isFemale, String sportActivity, String dif_level) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        Profile profile =
                new Profile(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, isFemale, age, height, weight, 0,
                        sportActivity, "", 0, 0, 0,
                        0, 0, dif_level, day, month, year);

        return calculateNew(context, profile, true);
    }

    public static Profile calculate(Context context, String height, String weight, String age, String sex, String activity, String goal) {
        Profile profile = UserDataHolder.getUserData().getProfile();
        profile.setHeight(Integer.parseInt(height));
        profile.setWeight(Double.parseDouble(weight));
        profile.setAge(Integer.parseInt(age));
        profile.setFemale(sex.equalsIgnoreCase(context.getResources().getString(R.string.profile_female)));
        profile.setGoLevel(convertToActivityDigital(activity));
        profile.setGoal(convertToGoalDigital(goal));

        return calculateDigital(context, profile, true);
    }

    public static int convertToGoalDigital(String goal) {
        String[] goals = App.getInstance().getResources().getStringArray(R.array.prf_goals);
        int goalDig = 0;
        for (int i = 0; i < goals.length; i++) {
            if (goal.equalsIgnoreCase(goals[i])) {
                goalDig = i;
            }
        }
        return goalDig;
    }

    public static int convertToActivityDigital(String activity) {
        String[] activities = App.getInstance().getResources().getStringArray(R.array.prf_activity_level);
        int goLevel = 0;
        for (int i = 0; i < activities.length; i++) {
            if (activity.equalsIgnoreCase(activities[i])) {
                goLevel = i;
            }
        }
        return goLevel;
    }

    public static String convertToOldGoal(Context context, String goal) {
        String oldGoal = "";

        if (goal.equalsIgnoreCase(context.getResources().getStringArray(R.array.goals)[0])) {
            oldGoal = context.getString(R.string.dif_level_easy);
        } else if (goal.equalsIgnoreCase(context.getResources().getStringArray(R.array.goals)[1])) {
            oldGoal = context.getString(R.string.dif_level_normal);
        } else if (goal.equalsIgnoreCase(context.getResources().getStringArray(R.array.goals)[2])) {
            oldGoal = context.getString(R.string.dif_level_hard);
        } else if (goal.equalsIgnoreCase(context.getResources().getStringArray(R.array.goals)[3])) {
            oldGoal = context.getString(R.string.dif_level_hard_up);
        }
        return oldGoal;
    }

    public static String convertToOldActivity(Context context, String activity) {
        String oldActivity = "";
        if (activity.equalsIgnoreCase(context.getResources().getStringArray(R.array.activities)[0])) {
            oldActivity = context.getString(R.string.level_none);
        } else if (activity.equalsIgnoreCase(context.getResources().getStringArray(R.array.activities)[1])) {
            oldActivity = context.getString(R.string.level_easy);
        } else if (activity.equalsIgnoreCase(context.getResources().getStringArray(R.array.activities)[2])) {
            oldActivity = context.getString(R.string.level_medium);
        } else if (activity.equalsIgnoreCase(context.getResources().getStringArray(R.array.activities)[3])) {
            oldActivity = context.getString(R.string.level_hard);
        } else if (activity.equalsIgnoreCase(context.getResources().getStringArray(R.array.activities)[4])) {
            oldActivity = context.getString(R.string.level_up_hard);
        } else if (activity.equalsIgnoreCase(context.getResources().getStringArray(R.array.activities)[5])) {
            oldActivity = context.getString(R.string.level_super);
        } else if (activity.equalsIgnoreCase(context.getResources().getStringArray(R.array.activities)[6])) {
            oldActivity = context.getString(R.string.level_up_super);
        }
        return oldActivity;
    }

    public static String convertToNewActivity(Context context, String exerciseStress) {
        String active = "";
        if (exerciseStress.equalsIgnoreCase(context.getResources().getString(R.string.level_none))) {
            active = context.getResources().getStringArray(R.array.activities)[0];
        } else if (exerciseStress.equalsIgnoreCase(context.getResources().getString(R.string.level_easy))) {
            active = context.getResources().getStringArray(R.array.activities)[1];
        } else if (exerciseStress.equalsIgnoreCase(context.getResources().getString(R.string.level_medium))) {
            active = context.getResources().getStringArray(R.array.activities)[2];
        } else if (exerciseStress.equalsIgnoreCase(context.getResources().getString(R.string.level_hard))) {
            active = context.getResources().getStringArray(R.array.activities)[3];
        } else if (exerciseStress.equalsIgnoreCase(context.getResources().getString(R.string.level_up_hard))) {
            active = context.getResources().getStringArray(R.array.activities)[4];
        } else if (exerciseStress.equalsIgnoreCase(context.getResources().getString(R.string.level_super))) {
            active = context.getResources().getStringArray(R.array.activities)[5];
        } else if (exerciseStress.equalsIgnoreCase(context.getResources().getString(R.string.level_up_super))) {
            active = context.getResources().getStringArray(R.array.activities)[6];
        }
        return active;
    }

    public static String convertToNewGoal(Context context, String difficultyLevel) {
        String choisedGoal = "";
        if (difficultyLevel.equalsIgnoreCase(context.getResources().getString(R.string.dif_level_easy))) {
            choisedGoal = context.getResources().getStringArray(R.array.goals)[0];
        } else if (difficultyLevel.equalsIgnoreCase(context.getResources().getString(R.string.dif_level_normal))) {
            choisedGoal = context.getResources().getStringArray(R.array.goals)[1];
        } else if (difficultyLevel.equalsIgnoreCase(context.getResources().getString(R.string.dif_level_hard))) {
            choisedGoal = context.getResources().getStringArray(R.array.goals)[2];
        } else if (difficultyLevel.equalsIgnoreCase(context.getResources().getString(R.string.dif_level_hard_up))) {
            choisedGoal = context.getResources().getStringArray(R.array.goals)[3];
        }
        return choisedGoal;
    }

    public static Profile calculateNew(Context context, Profile profile, boolean isNeedCreateMeasurment) {

        double BMR, KFA = 0, result, target = 0, FPCindex;
        double fat, protein, carbohydrate;
        String stressLevel = profile.getExerciseStress();
        String difficultyLevel = profile.getDifficultyLevel();
        int waterCount;
        float maxWater = 2f;

        final double RATE_NONE = 1.2;
        final double RATE_EASY = 1.375;
        final double RATE_MEDIUM = 1.46;
        final double RATE_HARD = 1.55;
        final double RATE_UP_HARD = 1.6375;
        final double RATE_SUPER = 1.725;
        final double RATE_UP_SUPER = 1.9;

        final double TARGET_NORMAL = 0;
        final double TARGET_LOOSE_WEIGHT = 0.15; //похудение
        final double TARGET_MUSCLE = 0.3;
        final double TARGET_SAVE = 0.1; //Сохранение мышц и сжигание жира

        if (profile.isFemale()) {
            FPCindex = 16;
            BMR = (10 * profile.getWeight() + 6.25 * profile.getHeight() - 5 * profile.getAge() - 161);
            waterCount = WATER_ON_KG_FEMALE * (int) profile.getWeight();
        } else {
            FPCindex = 36;
            BMR = (10 * profile.getWeight() + 6.25 * profile.getHeight() - 5 * profile.getAge() + 5);
            waterCount = WATER_ON_KG_MALE * (int) profile.getWeight();
        }


        if (profile.getGoLevel() == Config.FIRST_LEVEL) {
            KFA = RATE_NONE;
        } else if (profile.getGoLevel() == Config.SECOND_LEVEL) {
            KFA = RATE_EASY;
        } else if (profile.getGoLevel() == Config.THIRD_LEVEL) {
            KFA = RATE_MEDIUM;
        } else if (profile.getGoLevel() == Config.FOURTH_LEVEL ) {
            KFA = RATE_HARD;
        } else if (profile.getGoLevel() == Config.FIFTH_LEVEL ) {
            KFA = RATE_UP_HARD;
        } else if (profile.getGoLevel() == Config.SIXTH_LEVEL ) {
            KFA = RATE_SUPER;
        } else if (profile.getGoLevel() == Config.SEVENTH_LEVEL) {
            KFA = RATE_UP_SUPER;
        }


        if (profile.getGoal() == Config.FIRST_GOAL) {
            target = 1 - TARGET_NORMAL;
        } else if (profile.getGoal() == Config.SECOND_GOAL) {
            target = 1 - TARGET_LOOSE_WEIGHT;
        } else if (profile.getGoal() == Config.THIRD_GOAL) {
            target = 1 + TARGET_MUSCLE;
        } else if (profile.getGoal() == Config.FOURTH_GOAL) {
            target = 1 - TARGET_SAVE;
        }

        result = (BMR * KFA) * target;


        fat = (result * 0.25 / 9) + FPCindex;
        protein = (result * 0.4 / 4) - FPCindex;
        carbohydrate = (result * 0.35 / 4) - FPCindex;


        profile.setMaxWater(WaterActivity.DEFAULT_NORMA);
        profile.setWaterCount(waterCount);
        profile.setMaxKcal((int) result);
        profile.setMaxFat((int) fat);
        profile.setMaxProt((int) protein);
        profile.setMaxCarbo((int) carbohydrate);

        profile.setMaxWater(maxWater);

        if (isNeedCreateMeasurment) {
            createWeightMeas(profile.getWeight());
        }
        return profile;
    }

    public static Profile calculateDigital(Context context, Profile profile, boolean isNeedCreateMeasurment) {

        double BMR, KFA = 0, result, target = 0, FPCindex;
        double fat, protein, carbohydrate;
        int waterCount;
        float maxWater = 2f;

        final double RATE_NONE = 1.2;
        final double RATE_EASY = 1.375;
        final double RATE_MEDIUM = 1.46;
        final double RATE_HARD = 1.55;
        final double RATE_UP_HARD = 1.6375;
        final double RATE_SUPER = 1.725;
        final double RATE_UP_SUPER = 1.9;

        final double TARGET_NORMAL = 0;
        final double TARGET_LOOSE_WEIGHT = 0.15; //похудение
        final double TARGET_MUSCLE = 0.3;
        final double TARGET_SAVE = 0.1; //Сохранение мышц и сжигание жира

        if (profile.isFemale()) {
            FPCindex = 16;
            BMR = (10 * profile.getWeight() + 6.25 * profile.getHeight() - 5 * profile.getAge() - 161);
            waterCount = WATER_ON_KG_FEMALE * (int) profile.getWeight();
        } else {
            FPCindex = 36;
            BMR = (10 * profile.getWeight() + 6.25 * profile.getHeight() - 5 * profile.getAge() + 5);
            waterCount = WATER_ON_KG_MALE * (int) profile.getWeight();
        }


        if (profile.getGoLevel() == Config.FIRST_LEVEL) {
            KFA = RATE_NONE;
        } else if (profile.getGoLevel() == Config.SECOND_LEVEL) {
            KFA = RATE_EASY;
        } else if (profile.getGoLevel() == Config.THIRD_LEVEL) {
            KFA = RATE_MEDIUM;
        } else if (profile.getGoLevel() == Config.FOURTH_LEVEL ) {
            KFA = RATE_HARD;
        } else if (profile.getGoLevel() == Config.FIFTH_LEVEL ) {
            KFA = RATE_UP_HARD;
        } else if (profile.getGoLevel() == Config.SIXTH_LEVEL ) {
            KFA = RATE_SUPER;
        } else if (profile.getGoLevel() == Config.SEVENTH_LEVEL) {
            KFA = RATE_UP_SUPER;
        }


        if (profile.getGoal() == Config.FIRST_GOAL) {
            target = 1 - TARGET_NORMAL;
        } else if (profile.getGoal() == Config.SECOND_GOAL) {
            target = 1 - TARGET_LOOSE_WEIGHT;
        } else if (profile.getGoal() == Config.THIRD_GOAL) {
            target = 1 + TARGET_MUSCLE;
        } else if (profile.getGoal() == Config.FOURTH_GOAL) {
            target = 1 - TARGET_SAVE;
        }

        result = (BMR * KFA) * target;


        fat = (result * 0.25 / 9) + FPCindex;
        protein = (result * 0.4 / 4) - FPCindex;
        carbohydrate = (result * 0.35 / 4) - FPCindex;


        profile.setMaxWater(WaterActivity.DEFAULT_NORMA);
        profile.setWaterCount(waterCount);
        profile.setMaxKcal((int) result);
        profile.setMaxFat((int) fat);
        profile.setMaxProt((int) protein);
        profile.setMaxCarbo((int) carbohydrate);

        profile.setMaxWater(maxWater);

        if (isNeedCreateMeasurment) {
            createWeightMeas(profile.getWeight());
        }
        return profile;
    }

    public static void createWeightMeas(double weightValue) {
        Calendar calendar = Calendar.getInstance();
        calendar = DateAndTime.dropTime(calendar);
        Weight weight = new Weight("", calendar.getTimeInMillis(), weightValue);
        WorkWithFirebaseDB.setWeight(weight, String.valueOf(weight.getTimeInMillis()));
    }

    public static Profile cloneProfile(Profile profile) {
        Profile profileCalculate = new Profile();
        profileCalculate.setHeight(profile.getHeight());
        profileCalculate.setWeight(profile.getWeight());
        profileCalculate.setAge(profile.getAge());
        profileCalculate.setFemale(profile.isFemale());
        profileCalculate.setGoal(profile.getGoal());
        profileCalculate.setGoLevel(profile.getGoLevel());

        return profileCalculate;
    }

    public static boolean isDefaultParams(Context context) {
        Profile profile = UserDataHolder.getUserData().getProfile();
        int userKcal = profile.getMaxKcal();
        int userProt = profile.getMaxProt();
        int userFat = profile.getMaxFat();
        int userCarbo = profile.getMaxCarbo();


        Profile profileDefaultMainParams = BodyCalculates.calculateDigital(context, cloneProfile(profile), false);

        if (userKcal == profileDefaultMainParams.getMaxKcal()
                && userProt == profileDefaultMainParams.getMaxProt()
                && userFat == profileDefaultMainParams.getMaxFat()
                && userCarbo == profileDefaultMainParams.getMaxCarbo()) {
            return true;
        } else {
            return false;
        }
    }

    public static void saveWeight(double weight, Context context) {
        Profile profile = UserDataHolder.getUserData().getProfile();
        if (isDefaultParams(context)) {
            profile.setWeight(weight);
            profile = calculateNew(context, profile, true);
        } else {
            profile.setWeight(weight);
        }
        WorkWithFirebaseDB.putProfileValue(profile);
    }

    public static void handleProfile() {
        if (UserDataHolder.getUserData() != null && UserDataHolder.getUserData().getProfile() != null) {
            if (isProfileOld()) {
                makeProfileDigital();
            }
        }
    }

    private static void makeProfileDigital() {
        Profile profile = UserDataHolder.getUserData().getProfile();
        Context context = App.getInstance();

        String stressLevel = profile.getExerciseStress();
        String difficultyLevel = profile.getDifficultyLevel();

        if (stressLevel.equalsIgnoreCase(context.getString(R.string.level_none))) {
            profile.setExerciseStress(Config.EMPTY_FIELD);
            profile.setGoLevel(Config.FIRST_LEVEL);
        } else if (stressLevel.equalsIgnoreCase(context.getString(R.string.level_easy))) {
            profile.setExerciseStress(Config.EMPTY_FIELD);
            profile.setGoLevel(Config.SECOND_LEVEL);
        } else if (stressLevel.equalsIgnoreCase(context.getString(R.string.level_medium))) {
            profile.setExerciseStress(Config.EMPTY_FIELD);
            profile.setGoLevel(Config.THIRD_LEVEL);
        } else if (stressLevel.equalsIgnoreCase(context.getString(R.string.level_hard))) {
            profile.setExerciseStress(Config.EMPTY_FIELD);
            profile.setGoLevel(Config.FOURTH_LEVEL);
        } else if (stressLevel.equalsIgnoreCase(context.getString(R.string.level_up_hard))) {
            profile.setExerciseStress(Config.EMPTY_FIELD);
            profile.setGoLevel(Config.FIFTH_LEVEL);
        } else if (stressLevel.equalsIgnoreCase(context.getString(R.string.level_super))) {
            profile.setExerciseStress(Config.EMPTY_FIELD);
            profile.setGoLevel(Config.SIXTH_LEVEL);
        } else if (stressLevel.equalsIgnoreCase(context.getString(R.string.level_up_super))) {
            profile.setExerciseStress(Config.EMPTY_FIELD);
            profile.setGoLevel(Config.SEVENTH_LEVEL);
        }


        if (difficultyLevel.equalsIgnoreCase(context.getString(R.string.dif_level_easy))) {
            profile.setDifficultyLevel(Config.EMPTY_FIELD);
            profile.setGoal(Config.FIRST_GOAL);
        } else if (difficultyLevel.equalsIgnoreCase(context.getString(R.string.dif_level_normal))) {
            profile.setDifficultyLevel(Config.EMPTY_FIELD);
            profile.setGoal(Config.SECOND_GOAL);
        } else if (difficultyLevel.equalsIgnoreCase(context.getString(R.string.dif_level_hard))) {
            profile.setDifficultyLevel(Config.EMPTY_FIELD);
            profile.setGoal(Config.THIRD_GOAL);
        } else if (difficultyLevel.equalsIgnoreCase(context.getString(R.string.dif_level_hard_up))) {
            profile.setDifficultyLevel(Config.EMPTY_FIELD);
            profile.setGoal(Config.FOURTH_GOAL);
        }

        UserDataHolder.getUserData().setProfile(profile);
        WorkWithFirebaseDB.putProfileValue(profile);
    }

    private static boolean isProfileOld() {
        boolean isProfileOld = false;
        if (!UserDataHolder.getUserData().getProfile().getExerciseStress().equals(Config.EMPTY_FIELD) && !UserDataHolder.getUserData().getProfile().getDifficultyLevel().equals(Config.EMPTY_FIELD)) {
            isProfileOld = true;
        }
        return isProfileOld;
    }

    public static String getGoalName(int goal) {
        String choisedGoal = App.getInstance().getResources().getStringArray(R.array.prf_goals)[goal];
        return choisedGoal;
    }

    public static String getActivityName(int activityLevel) {
        String activity = App.getInstance().getResources().getStringArray(R.array.prf_activity_level)[activityLevel];
        return activity;
    }


}

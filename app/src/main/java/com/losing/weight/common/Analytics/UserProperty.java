package com.losing.weight.common.Analytics;

import android.content.Context;

import com.amplitude.api.Amplitude;
import com.amplitude.api.Identify;
import com.google.firebase.auth.FirebaseAuth;
import com.losing.weight.App;
import com.losing.weight.POJOProfile.Profile;
import com.losing.weight.R;

public class UserProperty {
    public static final String registration = "registration";
    public static final String registration_google = "google";
    public static final String registration_fb = "facebook";
    public static final String registration_email = "email";
    public static final String name_const = "name";

    public static final String q_male_status = "male";
    public static final String q_male_status_male = "male";
    public static final String q_male_status_female = "female";

    public static final String q_height_status = "height";

    public static final String q_weight_status = "weight";

    public static final String q_age_status = "age";

    public static final String q_active_status = "active";
    public static final String q_active_status1 = "minimal";
    public static final String q_active_status2 = "light";
    public static final String q_active_status3 = "two_trainings";
    public static final String q_active_status4 = "five_trainings";
    public static final String q_active_status5 = "everyday_intensive";
    public static final String q_active_status6 = "ten_trainings";
    public static final String q_active_status7 = "hard_work";

    public static final String q_goal_status = "goal";
    public static final String q_goal_status1 = "keep_fit";
    public static final String q_goal_status2 = "lose_weight";
    public static final String q_goal_status3 = "gain_muscles";
    public static final String q_goal_status4 = "burn_fat";

    public static final String calorie = "calorie";
    public static final String proteins = "proteins";
    public static final String fats = "fats";
    public static final String сarbohydrates = "сarbohydrates";

    public static final String premium_status = "premium_status";
    public static final String buy = "pay";
    public static final String not_buy = "free";
    public static final String trial = "trial";
    public static final String preferential = "unable";
    public static final String paid = "paid";

    public static final String premium_duration = "premium_duration";
    public static final String premium_price = "premium_price";
    public static final String premium_6month = "6month";
    public static final String premium_3month = "3month";
    public static final String premium_year = "year";

    public static final String trial_duration = "trial_duration";
    public static final String ltv_duration = "ltv_duration";
    public static final String ltv_revenue = "ltv_revenue";

    public static final String user_id = "id";
    public static final String abtest = "abtest";

    public static final String EMAIL = "email";

    public static final String first_day = "first_day";
    public static final String first_week = "first_week";
    public static final String first_month = "first_month";

    public static void setUserProperties(Profile profile, Context context, boolean isChangeProfile) {
        try {
            String goal = "", active = "", sex;
            String userStressLevel = profile.getExerciseStress();
            String userGoal = profile.getDifficultyLevel();

            String age = String.valueOf(profile.getAge());
            String weight = String.valueOf(profile.getWeight());
            String height = String.valueOf(profile.getHeight());

            if (userStressLevel.equalsIgnoreCase(context.getResources().getString(R.string.level_none))) {
                active = UserProperty.q_active_status1;
            } else if (userStressLevel.equalsIgnoreCase(context.getResources().getString(R.string.level_easy))) {
                active = UserProperty.q_active_status2;
            } else if (userStressLevel.equalsIgnoreCase(context.getResources().getString(R.string.level_medium))) {
                active = UserProperty.q_active_status3;
            } else if (userStressLevel.equalsIgnoreCase(context.getResources().getString(R.string.level_hard))) {
                active = UserProperty.q_active_status4;
            } else if (userStressLevel.equalsIgnoreCase(context.getResources().getString(R.string.level_up_hard))) {
                active = UserProperty.q_active_status5;
            } else if (userStressLevel.equalsIgnoreCase(context.getResources().getString(R.string.level_super))) {
                active = UserProperty.q_active_status6;
            } else if (userStressLevel.equalsIgnoreCase(context.getResources().getString(R.string.level_up_super))) {
                active = UserProperty.q_active_status7;
            }

            if (userGoal.equalsIgnoreCase(context.getResources().getString(R.string.dif_level_easy))) {
                goal = UserProperty.q_goal_status1;
            } else if (userGoal.equalsIgnoreCase(context.getResources().getString(R.string.dif_level_normal))) {
                goal = UserProperty.q_goal_status2;
            } else if (userGoal.equalsIgnoreCase(context.getResources().getString(R.string.dif_level_hard))) {
                goal = UserProperty.q_goal_status3;
            } else if (userGoal.equalsIgnoreCase(context.getResources().getString(R.string.dif_level_hard_up))) {
                goal = UserProperty.q_goal_status4;
            }

            if (profile.isFemale()) {
                sex = UserProperty.q_male_status_female;
            } else {
                sex = UserProperty.q_male_status_male;
            }
            if (!isChangeProfile) analLogIn();
            UserProperty.logProperties(sex, height, weight, age, active, goal, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    String.valueOf(profile.getMaxKcal()), String.valueOf(profile.getMaxProt()), String.valueOf(profile.getMaxFat()), String.valueOf(profile.getMaxCarbo()), profile.getFirstName());
        } catch (Exception ex) {
            Events.logSetUserPropertyError(ex.getMessage());
        }
    }


    private static void logProperties(String sex, String height, String weight, String age, String active, String goal, String id, String kcal, String prot, String fat, String carbo, String name) {
        Identify identify = new Identify()
                .set(q_male_status, sex)
                .set(q_height_status, height)
                .set(q_weight_status, weight)
                .set(q_age_status, age)
                .set(q_active_status, active)
                .set(q_goal_status, goal)
                .set(calorie, kcal)
                .set(proteins, prot)
                .set(fats, fat)
                .set(сarbohydrates, carbo)
                .set(name_const, name)
                .set(user_id, id);
        Amplitude.getInstance().identify(identify);

        App.getFAInstance()
            .setUserProperty(q_male_status, sex);


        App.getFAInstance()
            .setUserProperty(q_height_status, height);
        App.getFAInstance()
            .setUserProperty(q_weight_status, weight);
        App.getFAInstance()
            .setUserProperty(q_age_status, age);
        App.getFAInstance()
            .setUserProperty(q_goal_status, goal);
        App.getFAInstance()
            .setUserProperty(calorie, kcal);
        App.getFAInstance()
            .setUserProperty(proteins, prot);
        App.getFAInstance()
            .setUserProperty(fats, fat);
        App.getFAInstance()
            .setUserProperty(сarbohydrates, carbo);
        App.getFAInstance()
            .setUserProperty(name_const, name);

        /*UserProfile profile = UserProfile.newBuilder()
                .apply(Attribute.name().withValue(name))
                .apply(Attribute.customString(q_male_status).withValue(sex))
                .apply(Attribute.customString(q_height_status).withValue(height))
                .apply(Attribute.customString(q_weight_status).withValue(weight))
                .apply(Attribute.customString(q_age_status).withValue(age))
                .apply(Attribute.customString(q_active_status).withValue(active))
                .apply(Attribute.customString(q_goal_status).withValue(goal))
                .apply(Attribute.customString(calorie).withValue(calorie))
                .apply(Attribute.customString(proteins).withValue(proteins))
                .apply(Attribute.customString(fats).withValue(fats))
                .apply(Attribute.customString(сarbohydrates).withValue(сarbohydrates))
                .apply(Attribute.customString(name_const).withValue(name))
                .apply(Attribute.customString(user_id).withValue(id))
                .build();

        YandexMetrica.setUserProfileID(id);
        YandexMetrica.reportUserProfile(profile);*/
    }

    public static void setPremStatus(String status) {
        Identify identify = new Identify()
                .set(premium_status, status);
        Amplitude.getInstance().identify(identify);

        App.getFAInstance()
            .setUserProperty(premium_status, status);

        /*UserProfile profile = UserProfile.newBuilder()
                .apply(Attribute.customString(premium_status).withValue(status))
                .build();
        YandexMetrica.reportUserProfile(profile);*/
    }

    public static void setPremState(String price, String duration) {
        final Identify identify = new Identify()
            .set(premium_duration, price)
            .set(premium_price, duration);

        /*Amplitude.getInstance().identify(identify);
        UserProfile profile = UserProfile.newBuilder()
                .apply(Attribute.customString(premium_duration).withValue(price))
                .apply(Attribute.customString(premium_price).withValue(duration))
                .build();
        YandexMetrica.reportUserProfile(profile);*/
    }

    public static void setUserProvider(String provider) {
        Identify identify = new Identify()
                .set(registration, provider);
        Amplitude.getInstance().identify(identify);

        App.getFAInstance()
            .setUserProperty(registration, provider);
        /*UserProfile profile = UserProfile.newBuilder()
                .apply(Attribute.customString(registration).withValue(provider))
                .build();
        YandexMetrica.reportUserProfile(profile);*/
    }

    public static void setDate(String day, String week, String month) {
        Identify identify = new Identify()
                .set(first_day, day)
                .set(first_week, week)
                .set(first_month, month);
        Amplitude.getInstance().identify(identify);

        App.getFAInstance()
            .setUserProperty(first_day, day);
        App.getFAInstance()
            .setUserProperty(first_week, week);
        App.getFAInstance()
            .setUserProperty(first_month, month);

        /*UserProfile profile = UserProfile.newBuilder()
                .apply(Attribute.customString(first_day).withValue(day))
                .apply(Attribute.customString(first_week).withValue(week))
                .apply(Attribute.customString(first_month).withValue(month))
                .build();
        YandexMetrica.reportUserProfile(profile);*/
    }

    private static void analLogIn() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Identify identify = new Identify()
                .set(EMAIL, email);
        Amplitude.getInstance().identify(identify);

        App.getFAInstance()
            .setUserProperty(EMAIL, email);

        /*UserProfile profile = UserProfile.newBuilder()
                .apply(Attribute.customString(EMAIL).withValue(email))
                .build();
        YandexMetrica.reportUserProfile(profile);*/
    }
}
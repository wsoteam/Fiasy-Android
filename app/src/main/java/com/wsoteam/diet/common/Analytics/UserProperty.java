package com.wsoteam.diet.common.Analytics;

import android.content.Context;
import android.util.Log;

import com.amplitude.api.Amplitude;
import com.amplitude.api.Identify;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wsoteam.diet.POJOProfile.Profile;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.UserDataHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.UserAttributes;
import io.intercom.android.sdk.identity.Registration;

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
    public static final String trial_duration = "trial_duration";
    public static final String ltv_duration = "ltv_duration";
    public static final String ltv_revenue = "ltv_revenue";

    public static final String user_id = "id";

    public static final String EMAIL = "email";

    public static final String first_day = "first_day";
    public static final String first_week = "first_week";
    public static final String first_month = "first_month";

    public static void setUserProperties(Profile profile, Context context) {
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

        UserAttributes userAttributes = new UserAttributes.Builder()
                .withCustomAttribute(q_male_status, sex)
                .withCustomAttribute(q_height_status, Integer.parseInt(height))
                .withCustomAttribute(q_weight_status, Double.parseDouble(weight))
                .withCustomAttribute(q_age_status, Integer.parseInt(age))
                .withCustomAttribute(q_active_status, active)
                .withCustomAttribute(q_goal_status, goal)
                .withCustomAttribute(calorie, Integer.parseInt(kcal))
                .withCustomAttribute(proteins, Integer.parseInt(prot))
                .withCustomAttribute(fats, Integer.parseInt(fat))
                .withCustomAttribute(сarbohydrates, Integer.parseInt(carbo))
                .withCustomAttribute(name_const, name)
                .withCustomAttribute(user_id, id)
                .build();
        Intercom.client().updateUser(userAttributes);
    }

    public static void setPremStatus(String status) {
        Identify identify = new Identify()
                .set(premium_status, status);
        Amplitude.getInstance().identify(identify);

        signInIntercom();
        UserAttributes userAttributes = new UserAttributes.Builder()
                .withCustomAttribute(premium_status, status)
                .build();
        Intercom.client().updateUser(userAttributes);
    }

    public static void setUserProvider(String provider) {
        Identify identify = new Identify()
                .set(registration, provider);
        Amplitude.getInstance().identify(identify);

        signInIntercom();
        UserAttributes userAttributes = new UserAttributes.Builder()
                .withCustomAttribute(registration, provider)
                .build();
        Intercom.client().updateUser(userAttributes);
    }

    public static void setEmail(String email) {
        Identify identify = new Identify()
                .set(EMAIL, email);
        Amplitude.getInstance().identify(identify);

        signInIntercom();
        UserAttributes userAttributes = new UserAttributes.Builder()
                .withCustomAttribute(EMAIL, email)
                .build();
        Intercom.client().updateUser(userAttributes);
    }

    public static void setDate(String day, String week, String month) {
        Identify identify = new Identify()
                .set(first_day, day)
                .set(first_week, week)
                .set(first_month, month);
        Amplitude.getInstance().identify(identify);

        //signInIntercom();
        UserAttributes userAttributes = new UserAttributes.Builder()
                .withCustomAttribute(first_day, day)
                .withCustomAttribute(first_week, week)
                .withCustomAttribute(first_month, month)
                .build();
        Intercom.client().updateUser(userAttributes);
    }

    private static void signInIntercom() {
        Registration registration = Registration.create().withUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Intercom.client().registerIdentifiedUser(registration);
        Intercom.client().handlePushMessage();
    }
}

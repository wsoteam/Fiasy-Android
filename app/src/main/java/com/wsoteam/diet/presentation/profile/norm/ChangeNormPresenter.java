package com.wsoteam.diet.presentation.profile.norm;

import android.content.Context;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.wsoteam.diet.POJOProfile.Profile;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.common.Analytics.UserProperty;
import com.wsoteam.diet.common.helpers.BodyCalculates;

@InjectViewState
public class ChangeNormPresenter extends MvpPresenter<ChangeNormView> {
    private Context context;

    public ChangeNormPresenter(Context context) {
        this.context = context;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        if (UserDataHolder.getUserData() != null && UserDataHolder.getUserData().getProfile() != null) {

            String goal = BodyCalculates.getGoalName(UserDataHolder.getUserData().getProfile().getGoal());
            String activity = BodyCalculates.getActivityName(UserDataHolder.getUserData().getProfile().getGoLevel());

            getViewState().bindFields(UserDataHolder.getUserData().getProfile(), goal, activity);
        }

    }

    public boolean calculateAndSave(String height, String weight, String age, String sex, String activity, String goal) {
        Profile profile = BodyCalculates.calculate(context, height, weight, age, sex, activity, goal);
        UserProperty.setUserProperties(profile, context, true);
        WorkWithFirebaseDB.putProfileValue(profile);
        return true;
    }

    public void onlySave(String height, String weight, String age, String sex, String activity, String goal, String kcal, String prot, String carbo, String fats) {
        Profile profile = UserDataHolder.getUserData().getProfile();
        profile.setHeight(Integer.parseInt(height));
        profile.setWeight(Double.parseDouble(weight));
        profile.setAge(Integer.parseInt(age));

        profile.setFemale(sex.equalsIgnoreCase(context.getResources().getString(R.string.profile_female)));

        profile.setGoal(BodyCalculates.convertToGoalDigital(goal));
        profile.setGoLevel(BodyCalculates.convertToActivityDigital(activity));

        profile.setMaxKcal(Integer.parseInt(kcal));
        profile.setMaxProt(Integer.parseInt(prot));
        profile.setMaxCarbo(Integer.parseInt(carbo));
        profile.setMaxFat(Integer.parseInt(fats));

        UserProperty.setUserProperties(profile, context, true);
        WorkWithFirebaseDB.putProfileValue(profile);
        BodyCalculates.createWeightMeas(profile.getWeight());
    }

    public void convertAndSetGoal(int i) {
        String goal = context.getResources().getStringArray(R.array.prf_goals)[i];
        getViewState().setGoal(goal);
    }

    public void convertAndSetActivity(int i) {
        String activity = context.getResources().getStringArray(R.array.prf_activity_level)[i];
        getViewState().setActivity(activity);
    }

    public void dropParams() {
        Profile profileDefaultMainParams = BodyCalculates.calculateDigital(context, BodyCalculates.cloneProfile(UserDataHolder.getUserData().getProfile()), true);

        getViewState().setDefaultPremParams(String.valueOf(profileDefaultMainParams.getMaxKcal()), String.valueOf(profileDefaultMainParams.getMaxFat()),
                String.valueOf(profileDefaultMainParams.getMaxCarbo()), String.valueOf(profileDefaultMainParams.getMaxProt()));
    }

    public boolean isDefaultParams() {
        return BodyCalculates.isDefaultParams(context);
    }
}

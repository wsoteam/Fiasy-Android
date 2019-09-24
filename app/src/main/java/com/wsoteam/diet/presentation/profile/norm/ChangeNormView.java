package com.wsoteam.diet.presentation.profile.norm;

import com.arellomobile.mvp.MvpView;
import com.wsoteam.diet.POJOProfile.Profile;

public interface ChangeNormView extends MvpView {
    void bindFields(Profile profile, String goal, String activity);
    void setGoal(String goal);
    void setActivity(String activity);
    void setDefaultPremParams(String kcal, String fat, String carbo, String prot);
}

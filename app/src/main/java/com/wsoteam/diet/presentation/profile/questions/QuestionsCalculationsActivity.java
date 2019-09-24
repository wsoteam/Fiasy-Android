package com.wsoteam.diet.presentation.profile.questions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.amplitude.api.Amplitude;
import com.google.firebase.auth.FirebaseAuth;
import com.wsoteam.diet.AmplitudaEvents;
import com.wsoteam.diet.Authenticate.POJO.Box;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.POJOProfile.Profile;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.common.Analytics.EventProperties;
import com.wsoteam.diet.common.Analytics.UserProperty;
import com.wsoteam.diet.common.helpers.BodyCalculates;

public class QuestionsCalculationsActivity extends AppCompatActivity {
    @BindView(R.id.loader)
    ImageView loader;
    private boolean isNeedShowOnboard, createUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_calculations);
        ButterKnife.bind(this);

        RotateAnimation rotate =
                new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);
        rotate.setDuration(4000);
        rotate.setRepeatMode(Animation.INFINITE);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());

        loader.startAnimation(rotate);

        SharedPreferences sp = getSharedPreferences(Config.ONBOARD_PROFILE, MODE_PRIVATE);

        boolean isFemale = sp.getBoolean(Config.ONBOARD_PROFILE_SEX, true);
        int age = sp.getInt(Config.ONBOARD_PROFILE_YEARS, BodyCalculates.DEFAULT_AGE);
        int height = sp.getInt(Config.ONBOARD_PROFILE_HEIGHT, BodyCalculates.DEFAULT_HEIGHT);
        double weight =
                (double) sp.getInt(Config.ONBOARD_PROFILE_WEIGHT, (int) BodyCalculates.DEFAULT_WEIGHT);
        String activity = sp.getString(Config.ONBOARD_PROFILE_ACTIVITY, getString(R.string.level_none));
        String diff = sp.getString(Config.ONBOARD_PROFILE_PURPOSE, getString(R.string.dif_level_easy));

        Profile profileFinal =
                BodyCalculates.calculate(this, weight, height, age, isFemale, activity, diff);

        if (getSharedPreferences(Config.IS_NEED_SHOW_ONBOARD, MODE_PRIVATE).getBoolean(
                Config.IS_NEED_SHOW_ONBOARD, false)) {
            isNeedShowOnboard = true;
            getSharedPreferences(Config.IS_NEED_SHOW_ONBOARD, MODE_PRIVATE).edit()
                    .putBoolean(Config.IS_NEED_SHOW_ONBOARD, false)
                    .apply();
        }
        createUser = getIntent().getBooleanExtra(Config.CREATE_PROFILE, true);

        new Handler().postDelayed(() -> saveProfile(isNeedShowOnboard, profileFinal, createUser), 4000);
    }


    void saveProfile(boolean isNeedShowOnboard, Profile profile, boolean createProfile) {
        if (createProfile) {
            //Intent intent = new Intent(this, MainAuthNewActivity.class);
            Intent intent = new Intent(this, AfterQuestionsActivity.class);
            if (isNeedShowOnboard) {
                Box box = new Box();
                box.setBuyFrom(EventProperties.trial_from_onboard);
                box.setComeFrom(AmplitudaEvents.view_prem_free_onboard);
                box.setOpenFromIntrodaction(true);
                box.setOpenFromPremPart(false);
                intent.putExtra(Config.CREATE_PROFILE, true)
                        .putExtra(Config.INTENT_PROFILE, profile);
            } else {
                intent.putExtra(Config.INTENT_PROFILE, profile);
            }
            startActivity(intent);
            finish();
        }

        if (profile != null) {
            UserProperty.setUserProperties(profile, this);
            WorkWithFirebaseDB.putProfileValue(profile);

        }
    }
}
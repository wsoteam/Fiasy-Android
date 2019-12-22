package com.wsoteam.diet.presentation.profile.norm.choise.goal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.wsoteam.diet.R;
import com.wsoteam.diet.presentation.profile.norm.Config;

import static com.wsoteam.diet.Config.ONBOARD_PROFILE_PURPOSE;

public class GoalActivity extends AppCompatActivity {

    private int numberChoisedFigure = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        getData();
        getSupportFragmentManager().beginTransaction().replace(R.id.clContainer, GoalFragment.newInstance(numberChoisedFigure)).commit();
    }

    private void getData() {
        String newGoal = getIntent().getStringExtra(Config.GOAL);
        String[] newGoals = getResources().getStringArray(R.array.prf_goals);
        String oldGoalForSend = "";
        String[] levels = new String[]{
                getString(R.string.dif_level_easy),
                getString(R.string.dif_level_normal),
                getString(R.string.dif_level_hard),
                getString(R.string.dif_level_hard_up)
        };
        for (int i = 0; i < newGoals.length; i++) {
            if (newGoal.equalsIgnoreCase(newGoals[i])) {
                oldGoalForSend = levels[i];
                numberChoisedFigure = i;
            }
        }

        getSharedPreferences(com.wsoteam.diet.Config.ONBOARD_PROFILE, MODE_PRIVATE).edit().putString(ONBOARD_PROFILE_PURPOSE, oldGoalForSend).commit();
    }
}

package com.wsoteam.diet.presentation.profile.norm.choise.goal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.wsoteam.diet.R;
import com.wsoteam.diet.presentation.profile.norm.Config;
import com.wsoteam.diet.presentation.profile.questions.fragments.QuestionPurposeFragments;

import butterknife.BindView;

public class GoalFragment extends QuestionPurposeFragments {
    public static final String GOAL_TAG = "GoalFragment";
    @BindView(R.id.cbLooseWeight) RadioButton cbLooseWeight;
    @BindView(R.id.cbNormal) RadioButton cbNormal;
    @BindView(R.id.cbMuscle) RadioButton cbMuscle;
    @BindView(R.id.cbSave) RadioButton cbSave;
    @BindView(R.id.btnNext) Button btnSave;
    private final int EASY = 0, NORMAL = 1, HARD = 2, UPHARD = 3;

    public static GoalFragment newInstance(int goal) {
        Bundle bundle = new Bundle();
        bundle.putInt(GOAL_TAG, goal);
        GoalFragment goalFragment = new GoalFragment();
        goalFragment.setArguments(bundle);
        return goalFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        changeGoal(getArguments().getInt(GOAL_TAG));
        btnSave.setEnabled(true);
        btnSave.setText(getActivity().getResources().getString(R.string.save));
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(Config.GOAL_CHANGE_RESULT, getCheckedId());
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
    }

    private void changeGoal(int position) {
        switch (position) {
            case EASY:
                cbNormal.setChecked(true);
                break;
            case NORMAL:
                cbLooseWeight.setChecked(true);
                break;
            case HARD:
                cbMuscle.setChecked(true);
                break;
            case UPHARD:
                cbSave.setChecked(true);
                break;
        }
    }
}

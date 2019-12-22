package com.wsoteam.diet.presentation.profile.norm.choise.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.wsoteam.diet.R;
import com.wsoteam.diet.presentation.profile.norm.Config;
import com.wsoteam.diet.presentation.profile.questions.fragments.QuestionActivityFragments;

import butterknife.BindView;

public class ActivFragment extends QuestionActivityFragments {
    public static final String ACTIVITY_TAG = "ActivFragment";
    private String activity;
    @BindView(R.id.pbActivity) AppCompatSeekBar pbActivity;
    @BindView(R.id.tvActivity) TextView tvActivity;
    @BindView(R.id.btnNext) Button btnSave;

    public static ActivFragment newInstance(String activity) {
        Bundle bundle = new Bundle();
        bundle.putString(ACTIVITY_TAG, activity);
        ActivFragment activFragment = new ActivFragment();
        activFragment.setArguments(bundle);
        return activFragment;
    }


    @Override
    public void onResume() {
        super.onResume();
        activity = getArguments().getString(ACTIVITY_TAG);
        changeProgress(choisePosition(activity));
        pbActivity.setProgress(choisePosition(activity));
        tvActivity = getView().findViewById(R.id.tvActivity);
        btnSave = getView().findViewById(R.id.btnNext);
        btnSave.setText(getActivity().getResources().getString(R.string.save));
        btnSave.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra(Config.ACTIVITY_CHANGE_RESULT, pbActivity.getProgress());
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        });
    }

    private int choisePosition(String activity) {
        int position = 0;
        String[] activities = getActivity().getResources().getStringArray(R.array.prf_activity_level);
        for (int i = 0; i < activities.length; i++) {
            if (activity.equalsIgnoreCase(activities[i])) {
                position = i;
                break;
            }
        }
        return position;
    }
}

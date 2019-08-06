package com.wsoteam.diet.presentation.profile.questions.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.helpers.DrawableAlwaysCrossFadeFactory;
import com.wsoteam.diet.presentation.profile.questions.QuestionsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;


public class QuestionActivityFragments extends Fragment {
    @BindView(R.id.pbActivity)
    AppCompatSeekBar pbActivity;
    @BindView(R.id.tvActivity)
    TextView tvActivity;
    @BindView(R.id.ivImage)
    AppCompatImageView ivImage;

    public static QuestionActivityFragments newInstance() {
        return new QuestionActivityFragments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_activity, container, false);
        ButterKnife.bind(this, view);

        changeProgress(0);
        pbActivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                changeProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        return view;
    }

    private void changeProgress(int progress) {
        String text = getString(R.string.onboard_activity_level_none);
        int imageResource = R.drawable.ic_activity0;
        switch (progress) {
            case 0:
                imageResource = R.drawable.ic_activity0;
                text = getString(R.string.onboard_activity_level_none);
                break;
            case 1:
                imageResource = R.drawable.ic_activity1;
                text = getString(R.string.onboard_activity_level_easy);
                break;
            case 2:
                imageResource = R.drawable.ic_activity2;
                text = getString(R.string.onboard_activity_level_medium);
                break;
            case 3:
                imageResource = R.drawable.ic_activity3;
                text = getString(R.string.onboard_activity_level_hard);
                break;
            case 4:
                imageResource = R.drawable.ic_activity4;
                text = getString(R.string.onboard_activity_level_hard_up);
                break;
            case 5:
                imageResource = R.drawable.ic_activity5;
                text = getString(R.string.onboard_activity_level_super);
                break;
            case 6:
                imageResource = R.drawable.ic_activity6;
                text = getString(R.string.onboard_activity_level_super_up);
                break;
        }
        Glide.with(QuestionActivityFragments.this)
                .load(imageResource)
                .transition(DrawableTransitionOptions.with(new DrawableAlwaysCrossFadeFactory()))
                .into(ivImage);
        tvActivity.setText(text);
    }

    @OnClick(R.id.btnNext)
    public void onClickNext() {
        SharedPreferences sp = getActivity().getSharedPreferences(Config.ONBOARD_PROFILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Config.ONBOARD_PROFILE_ACTIVITY, getDiffLevel(pbActivity.getProgress()));
        editor.apply();
        ((QuestionsActivity) getActivity()).nextQuestion();
    }

    private String getDiffLevel(int position) {
        switch (position) {
            case 0:
                return getString(R.string.level_none);
            case 1:
                return getString(R.string.level_easy);
            case 2:
                return getString(R.string.level_medium);
            case 3:
                return getString(R.string.level_hard);
            case 4:
                return getString(R.string.level_up_hard);
            case 5:
                return getString(R.string.level_super);
            case 6:
                return getString(R.string.level_up_super);
            default:
                return getString(R.string.level_none);
        }
    }
}
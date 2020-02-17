package com.wsoteam.diet.presentation.profile.questions.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wsoteam.diet.Config;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.views.ruler.RulerValuePickerListener;
import com.wsoteam.diet.common.views.ruler.VerticalRulerValuePicker;
import com.wsoteam.diet.presentation.profile.questions.QuestionsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;

public class QuestionHeightFragments extends Fragment {
    @BindView(R.id.tvHeight)
    TextView tvHeight;
    @BindView(R.id.rulerPicker)
    VerticalRulerValuePicker rulerPicker;
    private int height;

    public static QuestionHeightFragments newInstance() {
        return new QuestionHeightFragments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_height, container, false);
        ButterKnife.bind(this, view);

        final SharedPreferences prefs = getActivity()
            .getSharedPreferences(Config.ONBOARD_PROFILE, MODE_PRIVATE);

        height = prefs.getInt(Config.ONBOARD_PROFILE_HEIGHT, 180);
        tvHeight.setText(String.format(getString(R.string.onboard_height_pattern), height));

        rulerPicker.selectValue(height);
        rulerPicker.setValuePickerListener(new RulerValuePickerListener() {
            @Override
            public void onValueChange(int selectedValue) {
                height = 390 - selectedValue;
                tvHeight.setText(String.format(getString(R.string.onboard_height_pattern), height));
            }

            @Override
            public void onIntermediateValueChange(int selectedValue) {

            }
        });
        return view;
    }

    @OnClick(R.id.btnNext)
    public void onClickNext() {
        SharedPreferences sp = getActivity().getSharedPreferences(Config.ONBOARD_PROFILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Config.ONBOARD_PROFILE_HEIGHT, height);
        editor.apply();
        ((QuestionsActivity) getActivity()).nextQuestion();
    }
}
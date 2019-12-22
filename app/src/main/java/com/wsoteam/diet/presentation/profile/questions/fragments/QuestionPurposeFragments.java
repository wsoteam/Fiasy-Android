package com.wsoteam.diet.presentation.profile.questions.fragments;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kotlin.collections.ArraysKt;

import com.wsoteam.diet.Config;
import com.wsoteam.diet.R;
import com.wsoteam.diet.presentation.profile.questions.QuestionsActivity;
import com.wsoteam.diet.presentation.teach.TeachUtil;

import static android.content.Context.MODE_PRIVATE;
import static com.wsoteam.diet.Config.ONBOARD_PROFILE_PURPOSE;

public class QuestionPurposeFragments extends Fragment {
  @BindView(R.id.cbLooseWeight)
  RadioButton cbLooseWeight;
  @BindView(R.id.cbNormal)
  RadioButton cbNormal;
  @BindView(R.id.cbMuscle)
  RadioButton cbMuscle;
  @BindView(R.id.cbSave)
  RadioButton cbSave;

  @BindView(R.id.btnNext)
  View nextButton;

  private View selectedPurpose;

  private static final int[] female = {
      R.drawable.question_female_purpose_1,
      R.drawable.question_female_purpose_2,
      R.drawable.question_female_purpose_3,
      R.drawable.question_female_purpose_4,
  };

  private static final int[] male = {
      R.drawable.question_male_purpose_1,
      R.drawable.question_male_purpose_2,
      R.drawable.question_male_purpose_3,
      R.drawable.question_male_purpose_4,
  };

  public static QuestionPurposeFragments newInstance() {
    return new QuestionPurposeFragments();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_question_purpose, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    nextButton.setEnabled(getCheckedId() >= 0);

    final boolean isFemale = getActivity()
        .getSharedPreferences(Config.ONBOARD_PROFILE, MODE_PRIVATE)
        .getBoolean(Config.ONBOARD_PROFILE_SEX, true);

    final Resources r = getResources();

    final View[] views = {
        cbLooseWeight,
        cbNormal,
        cbMuscle,
        cbSave
    };

    int selected = getLastCheck();

    for (int i = 0; i < views.length; i++) {
      final int targetId = isFemale ? female[i] : male[i];
      final VectorDrawableCompat d = VectorDrawableCompat
          .create(r, targetId, requireContext().getTheme());

      views[i].setBackground(d.mutate());

      if(i == selected){
        selectedPurpose = views[i];
        setSelectedPurposeTint(R.color.purpose_icon_selected);
      }
    }

    nextButton.setEnabled(selected >= 0);
  }

  public int getCheckedId() {
    int position = -1;

    if (cbNormal.isChecked()) {
      position = 0;
    } else if (cbLooseWeight.isChecked()) {
      position = 1;
    } else if (cbMuscle.isChecked()) {
      position = 2;
    } else if (cbSave.isChecked()) {
      position = 3;
    }

    return position;
  }

  @OnClick(R.id.btnNext)
  public void onClickNext() {
    final int position = getCheckedId();

    if (position == -1) {
      return;
    }
    TeachUtil.setOpen(getContext(),true);
    ((QuestionsActivity) getActivity()).saveProfile();
  }

  @OnClick({ R.id.cbNormal, R.id.cbLooseWeight, R.id.cbMuscle, R.id.cbSave })
  public void onClick(View view) {
    // uncheck current
    setSelectedPurposeTint(R.color.purpose_icon_normal);

    cbNormal.setChecked(false);
    cbLooseWeight.setChecked(false);
    cbMuscle.setChecked(false);
    cbSave.setChecked(false);

    switch (view.getId()) {
      case R.id.cbNormal:
        cbNormal.setChecked(true);
        break;
      case R.id.cbLooseWeight:
        cbLooseWeight.setChecked(true);
        break;
      case R.id.cbMuscle:
        cbMuscle.setChecked(true);
        break;
      case R.id.cbSave:
        cbSave.setChecked(true);
        break;
    }

    // check new one
    selectedPurpose = view;
    setSelectedPurposeTint(R.color.purpose_icon_selected);

    nextButton.setEnabled(getCheckedId() >= 0);

    final String difLevel = getDiffLevel(getCheckedId());
    SharedPreferences sp = getActivity().getSharedPreferences(Config.ONBOARD_PROFILE, MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();
    editor.putString(ONBOARD_PROFILE_PURPOSE, difLevel);
    editor.apply();
  }

  public void setSelectedPurposeTint(int tintColor) {
    if (selectedPurpose != null) {
      VectorDrawableCompat d = (VectorDrawableCompat) selectedPurpose.getBackground();
      d.setTint(ContextCompat.getColor(requireContext(), tintColor));

      selectedPurpose.invalidateDrawable(d);
    }
  }

  public int getLastCheck() {
    final String level = getActivity().getSharedPreferences(Config.ONBOARD_PROFILE, MODE_PRIVATE)
            .getString(ONBOARD_PROFILE_PURPOSE, null);

    final String[] levels = new String[]{
            getString(R.string.dif_level_normal),
            getString(R.string.dif_level_easy),
            getString(R.string.dif_level_hard),
            getString(R.string.dif_level_hard_up),
    };

    return ArraysKt.indexOf(levels, level);
  }

  public String getDiffLevel(int position) {
    switch (position) {
      case 1:
        return getString(R.string.dif_level_normal);
      case 2:
        return getString(R.string.dif_level_hard);
      case 3:
        return getString(R.string.dif_level_hard_up);
      default:
        return getString(R.string.dif_level_easy);
    }
  }
}
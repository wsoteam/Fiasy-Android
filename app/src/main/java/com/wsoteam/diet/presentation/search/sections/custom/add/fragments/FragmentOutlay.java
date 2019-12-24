package com.wsoteam.diet.presentation.search.sections.custom.add.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.google.android.material.textfield.TextInputLayout;
import com.wsoteam.diet.BranchOfAnalyzer.CustomFood.CustomFood;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.networking.food.POJO.Result;
import com.wsoteam.diet.presentation.search.sections.custom.add.ActivityCreateFood;
import com.wsoteam.diet.presentation.search.sections.custom.add.SayForward;
import java.util.ArrayList;
import java.util.List;

public class FragmentOutlay extends Fragment implements SayForward {

  private final static String TAG = "FragmentOutlay";
  private final int FAT_SIZE = 9;
  private final int OTHER_SIZE = 4;

  @BindView(R.id.edtKcal) EditText edtKcal;
  @BindView(R.id.edtFats) EditText edtFats;
  @BindView(R.id.edtCarbo) EditText edtCarbo;
  @BindView(R.id.edtProt) EditText edtProt;
  @BindView(R.id.tvAlert) TextView tvAlert;
  @BindView(R.id.textInputLayout22) TextInputLayout tilKcal;
  @BindView(R.id.textInputLayout1) TextInputLayout tilFat;
  @BindView(R.id.textInputLayout23) TextInputLayout tilCarbo;
  @BindView(R.id.textInputLayout24) TextInputLayout tilProt;

  private Button btnForward;
  private Result result;
  private List<EditText> fields = new ArrayList<>();
  private List<TextInputLayout> boxes = new ArrayList<>();
  private Unbinder unbinder;


  public static FragmentOutlay newInstance(CustomFood customFood) {
    Bundle bundle = new Bundle();
    bundle.putSerializable(TAG, customFood);
    FragmentOutlay fragmentOutlay = new FragmentOutlay();
    fragmentOutlay.setArguments(bundle);
    return fragmentOutlay;
  }

  @Override
  public boolean forward() {
    if (checkReady(true)) {
      setInfo();
      return true;
    } else {
      return false;
    }
  }

  @Override public boolean checkForwardPossibility() {
    return checkReady(false);
  }

  private void setInfo() {
    Result customFood = ((ActivityCreateFood) getActivity()).customFood;
    customFood.setCalories(Double.parseDouble(edtKcal.getText().toString()));
    customFood.setFats(Double.parseDouble(edtFats.getText().toString()));
    customFood.setProteins(Double.parseDouble(edtProt.getText().toString()));
    customFood.setCarbohydrates(Double.parseDouble(edtCarbo.getText().toString()));
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_outlay_new, container, false);
    unbinder = ButterKnife.bind(this, view);
    btnForward = getActivity().findViewById(R.id.btnForward);
    fillArrays();
    result = ((ActivityCreateFood) getActivity()).customFood;
    setListeners();
    return view;
  }

  private void fillArrays() {
    fields.add(edtKcal);
    fields.add(edtFats);
    fields.add(edtCarbo);
    fields.add(edtProt);

    boxes.add(tilKcal);
    boxes.add(tilFat);
    boxes.add(tilCarbo);
    boxes.add(tilProt);
  }

  private void setListeners() {
    edtCarbo.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        handlEnter();
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
    edtFats.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        handlEnter();
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
    edtKcal.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        handlEnter();
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
    edtProt.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        handlEnter();
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
  }

  private void handlEnter() {
    if (checkReady(false)) {
      showBottomAlert(isRightFormula());
      activateBottom();
    } else {
      deactivateBottom();
    }
  }

  private void deactivateBottom() {
    btnForward.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_gray));
  }

  private void activateBottom() {
    btnForward.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_orange));
  }

  private void showBottomAlert(boolean rightFormula) {
    if (rightFormula) {
      tvAlert.setVisibility(View.INVISIBLE);
    } else {
      tvAlert.setVisibility(View.VISIBLE);
    }
  }

  private boolean checkReady(boolean isNeedShowErros) {
    boolean isReady = true;
    for (int i = 0; i < fields.size(); i++) {
      String text = fields.get(i).getText().toString().replaceAll("\\s+", " ").trim();
      if (text.equals("")) {
        isReady = false;
        if (isNeedShowErros) {
          showError(boxes.get(i));
        }
      } else {
        hideError(boxes.get(i));
      }
    }
    return isReady;
  }

  private void hideError(TextInputLayout til) {
    if (til.isErrorEnabled()) {
      til.setErrorEnabled(false);
    }
  }

  private void showError(TextInputLayout til) {
    if (!til.isErrorEnabled()) {
      til.setErrorEnabled(true);
      til.setErrorTextColor(getActivity().getResources().getColorStateList(R.color.cst_error));
      til.setError(getActivity().getResources().getString(R.string.cst_error_text));
    }
  }

  private boolean isRightFormula() {
    int kcal = Integer.parseInt(edtKcal.getText().toString());
    int fat = Integer.parseInt(edtFats.getText().toString());
    int carbo = Integer.parseInt(edtCarbo.getText().toString());
    int prot = Integer.parseInt(edtProt.getText().toString());
    int sum = fat * FAT_SIZE + carbo * OTHER_SIZE + prot * OTHER_SIZE;
    return kcal > sum;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}

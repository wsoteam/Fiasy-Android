package com.wsoteam.diet.presentation.search.sections.custom.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.wsoteam.diet.BranchOfAnalyzer.CustomFood.CustomFood;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.networking.food.POJO.Result;
import com.wsoteam.diet.presentation.search.sections.custom.ActivityCreateFood;
import com.wsoteam.diet.presentation.search.sections.custom.SayForward;
import java.util.ArrayList;
import java.util.List;

public class FragmentOutlay extends Fragment implements SayForward {

  @BindView(R.id.edtKcal) EditText edtKcal;
  @BindView(R.id.edtFats) EditText edtFats;
  @BindView(R.id.edtCarbo) EditText edtCarbo;
  @BindView(R.id.edtProt) EditText edtProt;
  @BindView(R.id.tvAlert) TextView tvAlert;
  private Result result;
  private final int FAT_SIZE = 9;
  private final int OTHER_SIZE = 4;
  private List<EditText> fields = new ArrayList<>();

  Unbinder unbinder;
  private final double EMPTY_PARAM = -1.0;

  private final static String TAG = "FragmentOutlay";

  public static FragmentOutlay newInstance(CustomFood customFood) {
    Bundle bundle = new Bundle();
    bundle.putSerializable(TAG, customFood);
    FragmentOutlay fragmentOutlay = new FragmentOutlay();
    fragmentOutlay.setArguments(bundle);
    return fragmentOutlay;
  }

  @Override
  public boolean forward() {
    if (isCanForward()) {
      setInfo();
      return true;
    } else {
      Toast.makeText(getActivity(), getString(R.string.error_toast), Toast.LENGTH_SHORT).show();
      return false;
    }
  }

  private void setInfo() {
    Result customFood = ((ActivityCreateFood) getActivity()).customFood;
    customFood.setCalories(Double.parseDouble(edtKcal.getText().toString()));
    customFood.setFats(Double.parseDouble(edtFats.getText().toString()));
    customFood.setProteins(Double.parseDouble(edtProt.getText().toString()));
    customFood.setCarbohydrates(Double.parseDouble(edtCarbo.getText().toString()));
  }

  private boolean isCanForward() {
    if (!edtKcal.getText().toString().equals("")
        && !edtFats.getText().toString().equals("")
        && !edtCarbo.getText().toString().equals("")
        && !edtProt.getText().toString().equals("")
        && !edtKcal.getText().toString().equals(".")
        && !edtFats.getText().toString().equals(".")
        && !edtCarbo.getText().toString().equals(".")
        && !edtProt.getText().toString().equals(".")) {
      return true;
    } else {
      return false;
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_outlay_new, container, false);
    unbinder = ButterKnife.bind(this, view);
    fields.add(edtKcal);
    fields.add(edtFats);
    fields.add(edtCarbo);
    fields.add(edtProt);
    result = ((ActivityCreateFood) getActivity()).customFood;
    setListeners();
    return view;
  }

  private void setListeners() {
    edtCarbo.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (checkReady()) {
          showBottomAlert(isRightFormula());
        }
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
    edtFats.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (checkReady()) {
          showBottomAlert(isRightFormula());
        }
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
    edtKcal.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (checkReady()) {
          showBottomAlert(isRightFormula());
        }
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
    edtProt.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (checkReady()) {
          showBottomAlert(isRightFormula());
        }
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
  }

  private void showBottomAlert(boolean rightFormula) {
    if (rightFormula) {
      tvAlert.setVisibility(View.INVISIBLE);
    } else {
      tvAlert.setVisibility(View.VISIBLE);
    }
  }

  private boolean checkReady() {
    boolean isReady = true;
    for (EditText editText : fields) {
      editText.getText().toString().replaceAll("\\s+", " ").trim();
      if (editText.getText().toString().equals("")) {
        isReady = false;
      }
    }
    return isReady;
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

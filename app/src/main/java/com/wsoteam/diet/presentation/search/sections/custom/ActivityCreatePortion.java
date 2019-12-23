package com.wsoteam.diet.presentation.search.sections.custom;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.material.textfield.TextInputLayout;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.networking.food.POJO.MeasurementUnit;

public class ActivityCreatePortion extends AppCompatActivity {
  @BindView(R.id.edtName) EditText edtName;
  @BindView(R.id.edtSize) EditText edtSize;
  @BindView(R.id.edtUnits) EditText edtUnits;
  @BindView(R.id.btnForward) Button btnForward;
  boolean isLiquid = false;
  boolean isBeReady = false;
  @BindView(R.id.textInputLayout22) TextInputLayout tilName;
  @BindView(R.id.textInputLayout23) TextInputLayout tilSize;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_portion);
    ButterKnife.bind(this);

    isLiquid = getIntent().getBooleanExtra(Config.TAG_IS_LIQUID, false);
    if (isLiquid) {
      edtUnits.setText(getResources().getString(R.string.cst_liquid_units));
    } else {
      edtUnits.setText(getResources().getString(R.string.cst_no_liquid_units));
    }

    edtName.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        handlSavedOpportunity(count, edtSize);
        if (count > 0) {
          tilName.setErrorEnabled(false);
        }
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });

    edtSize.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        handlSavedOpportunity(count, edtName);
        if (count > 0) {
          tilSize.setErrorEnabled(false);
        }
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
  }


  private void handlSavedOpportunity(int count, EditText editText) {
    if (count > 0 && isOtherReady(editText)) {
      makeButtonReady();
    } else {
      makeButtonUnReady();
    }
  }

  private void makeButtonUnReady() {
    btnForward.setBackground(getResources().getDrawable(R.drawable.shape_gray));
  }

  private void makeButtonReady() {
    btnForward.setBackground(getResources().getDrawable(R.drawable.shape_orange));
  }

  private boolean isOtherReady(EditText editText) {
    editText.getText().toString().replaceAll("\\s+", " ").trim();
    return !editText.getText().toString().equals("");
  }

  @OnClick({ R.id.ibClose, R.id.btnForward }) public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.ibClose:
        onBackPressed();
        break;
      case R.id.btnForward:
        if (checkErrors()) {
          saveAndClose();
        }
        break;
    }
  }

  private boolean checkErrors() {
    boolean isReady = true;
    if (edtName.getText().toString().replaceAll("\\s+", " ").trim().equals("")) {
      setError(tilName);
      isReady = false;
    } else {
      hideError(tilName);
    }

    if (edtSize.getText().toString().replaceAll("\\s+", " ").trim().equals("")) {
      setError(tilSize);
      isReady = false;
    } else {
      hideError(tilSize);
    }
    return isReady;
  }

  private void hideError(TextInputLayout tilName) {
    if (tilName.isErrorEnabled()) {
      tilName.setErrorEnabled(false);
    }
  }

  private void setError(TextInputLayout tilName) {
    if (!tilName.isErrorEnabled()) {
      tilName.setErrorEnabled(true);
      tilName.setErrorTextColor(getResources().getColorStateList(R.color.cst_error));
      tilName.setError(getResources().getString(R.string.cst_error_text));
    }
  }

  private void saveAndClose() {
    MeasurementUnit measurementUnit = new MeasurementUnit();
    measurementUnit.setName(edtName.getText().toString());
    measurementUnit.setAmount(Integer.parseInt(edtSize.getText().toString()));
    Intent intent = new Intent();
    intent.putExtra(Config.NEW_MEASURMENT, measurementUnit);
    setResult(RESULT_OK, intent);
    finish();
  }
}

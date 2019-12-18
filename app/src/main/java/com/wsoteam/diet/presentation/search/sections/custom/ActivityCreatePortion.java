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

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_portion);
    ButterKnife.bind(this);

    isLiquid = getIntent().getBooleanExtra(Config.RESULT_SEND_TAG, false);
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
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });

    edtSize.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        handlSavedOpportunity(count, edtName);
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
  }

  private void handlSavedOpportunity(int count, EditText edtSize) {
    if (count > 0 && isOtherReady(edtSize) && !isBeReady) {
      makeButtonReady();
    } else if (isBeReady) {
      makeButtonUnReady();
    }
  }

  private void makeButtonUnReady() {
    isBeReady = false;
    btnForward.setBackground(getResources().getDrawable(R.drawable.shape_gray));
    btnForward.setEnabled(false);
  }

  private void makeButtonReady() {
    isBeReady = true;
    btnForward.setBackground(getResources().getDrawable(R.drawable.shape_orange));
    btnForward.setEnabled(true);
  }

  private boolean isOtherReady(EditText editText) {
    editText.getText().toString().replaceAll("\\s+", " ").trim();
    return !editText.getText().equals("");
  }

  @OnClick({ R.id.btnBack, R.id.btnForward }) public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.btnBack:
        onBackPressed();
        break;
      case R.id.btnForward:
        saveAndClose();
        break;
    }
  }

  private void saveAndClose() {
    MeasurementUnit measurementUnit = new MeasurementUnit();
    measurementUnit.setName(edtName.getText().toString());
    measurementUnit.setAmount(Integer.parseInt(edtSize.getText().toString()));
    Intent intent = new Intent();
    intent.putExtra(Config.RESULT_RECIEVE_TAG, measurementUnit);
    setResult(RESULT_OK, intent);
    finish();
  }
}

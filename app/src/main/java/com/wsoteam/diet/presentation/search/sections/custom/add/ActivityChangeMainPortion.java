package com.wsoteam.diet.presentation.search.sections.custom.add;

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

public class ActivityChangeMainPortion extends AppCompatActivity {
  @BindView(R.id.edtName) EditText edtName;
  @BindView(R.id.edtSize) EditText edtSize;
  @BindView(R.id.edtUnits) EditText edtUnits;
  @BindView(R.id.btnForward) Button btnForward;
  boolean isLiquid = false;
  @BindView(R.id.textInputLayout22) TextInputLayout textInputLayout22;
  @BindView(R.id.textInputLayout23) TextInputLayout tilSize;
  private double mainPortion = 0;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_portion);
    ButterKnife.bind(this);
    textInputLayout22.setVisibility(View.GONE);
    isLiquid = getIntent().getBooleanExtra(Config.TAG_IS_LIQUID, false);
    if (isLiquid) {
      edtUnits.setText(getResources().getString(R.string.cst_liquid_units));
    } else {
      edtUnits.setText(getResources().getString(R.string.cst_no_liquid_units));
    }
    mainPortion = getIntent().getDoubleExtra(Config.SIZE_MAIN_PORTION, 0);

    edtSize.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        handlSavedOpportunity(count);
        if (count > 0 && tilSize.isErrorEnabled()){
          tilSize.setErrorEnabled(false);
        }
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
  }

  private void handlSavedOpportunity(int count) {
    if (count > 0) {
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

  @OnClick({ R.id.ibClose, R.id.btnForward }) public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.ibClose:
        onBackPressed();
        break;
      case R.id.btnForward:
        if (checkError()) {
          saveAndClose();
        }
        break;
    }
  }

  private boolean checkError() {
    if (edtSize.getText().toString().replaceAll("\\s+", " ").trim().equals("")){
      tilSize.setErrorEnabled(true);
      tilSize.setErrorTextColor(getResources().getColorStateList(R.color.cst_error));
      tilSize.setError(getResources().getString(R.string.cst_error_text));
      return false;
    }else {
      return true;
    }
  }

  private void saveAndClose() {
    double newMainPortion = Double.parseDouble(edtSize.getText().toString());
    Intent intent = new Intent();
    intent.putExtra(Config.NEW_SIZE_MAIN_PORTION, newMainPortion);
    setResult(RESULT_OK, intent);
    finish();
  }
}
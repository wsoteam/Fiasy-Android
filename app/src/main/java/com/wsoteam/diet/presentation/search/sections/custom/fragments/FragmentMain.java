package com.wsoteam.diet.presentation.search.sections.custom.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wsoteam.diet.BarcodeScanner.BaseScanner;
import com.wsoteam.diet.BranchOfAnalyzer.CustomFood.CustomFood;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.networking.food.POJO.Brand;
import com.wsoteam.diet.common.networking.food.POJO.Result;
import com.wsoteam.diet.presentation.search.sections.custom.ActivityCreateFood;
import com.wsoteam.diet.presentation.search.sections.custom.SayForward;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FragmentMain extends Fragment implements SayForward {

  @BindView(R.id.edtBrand) EditText edtBrand;
  @BindView(R.id.edtName) EditText edtName;
  @BindView(R.id.edtBarcode) EditText edtBarcode;
  Unbinder unbinder;
  private final static String TAG = "FragmentMainInfo";
  @BindView(R.id.rgrpType) RadioGroup rgrpType;
  @BindView(R.id.rbtnFood) RadioButton rbtnFood;
  @BindView(R.id.rbtnLiquid) RadioButton rbtnLiquid;

  public static FragmentMain newInstance(Result customFood) {
    Bundle bundle = new Bundle();
    bundle.putSerializable(TAG, customFood);
    FragmentMain fragmentMainInfo = new FragmentMain();
    fragmentMainInfo.setArguments(bundle);
    return fragmentMainInfo;
  }

  @Override
  public boolean forward() {
    if (!edtName.getText().toString().equals("")
        && !edtName.getText().toString().equals(" ")
        && !edtName.getText().toString().replaceAll("\\s+", " ").equals(" ")) {
      setInfo();
      return true;
    } else {
      Toast.makeText(getActivity(), getString(R.string.error_name), Toast.LENGTH_SHORT).show();
      return false;
    }
  }

  private void setInfo() {
    boolean isLiquid = rbtnLiquid.isChecked();
    String brand = edtBrand.getText().toString().replaceAll("\\s+", " ").trim();
    String name = edtName.getText().toString().replaceAll("\\s+", " ").trim();
    Brand currentBrand = new Brand();
    currentBrand.setName(brand);
    Result customFood = ((ActivityCreateFood) getActivity()).customFood;
    customFood.setBrand(currentBrand);
    customFood.setName(name);
    customFood.setBarcode(edtBarcode.getText().toString());
    customFood.setLiquid(isLiquid);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_main_info_new, container, false);
    unbinder = ButterKnife.bind(this, view);
    rbtnFood.setTextColor(getActivity().getResources().getColor(R.color.cst_active_rb));
    setForwardListener();
    return view;
  }

  private void setForwardListener() {
    edtName.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        String name = edtName.getText().toString().replaceAll("\\s+", " ").trim();
        Button btnForward = getActivity().findViewById(R.id.btnForward);
        if (count > 0 && !name.equals("")) {
          btnForward.setEnabled(true);
          btnForward.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_orange));
        }else {
          btnForward.setEnabled(false);
          btnForward.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_gray));
        }
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
  }

  private void bindFields(CustomFood customFood) {
    edtName.setText(customFood.getName());
    edtBrand.setText(customFood.getBrand());
    edtBarcode.setText(customFood.getBarcode());
  }

  @OnClick({ R.id.rbtnFood, R.id.rbtnLiquid })
  public void onRadioButtonClicked(RadioButton radioButton) {
    boolean checked = radioButton.isChecked();
    switch (radioButton.getId()) {
      case R.id.rbtnFood:
        rbtnFood.setTextColor(getActivity().getResources().getColor(R.color.cst_active_rb));
        rbtnLiquid.setTextColor(getActivity().getResources().getColor(R.color.cst_inactive_rb));
        break;
      case R.id.rbtnLiquid:
        rbtnLiquid.setTextColor(getActivity().getResources().getColor(R.color.cst_active_rb));
        rbtnFood.setTextColor(getActivity().getResources().getColor(R.color.cst_inactive_rb));
        break;
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick(R.id.ibBarcode)
  public void onViewClicked() {
    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(getActivity(),
          new String[] { Manifest.permission.CAMERA }, 1);
    } else {
      startActivityForResult(new Intent(getActivity(), BaseScanner.class), 1);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (data == null) {
      return;
    }
    edtBarcode.setText(data.getStringExtra(Config.BARCODE_STRING_NAME));
  }
}

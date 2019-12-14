package com.wsoteam.diet.presentation.search.sections.custom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.wsoteam.diet.BranchOfAnalyzer.CustomFood.ActivityCreateFood;
import com.wsoteam.diet.BranchOfAnalyzer.CustomFood.CustomFood;
import com.wsoteam.diet.BranchOfAnalyzer.CustomFood.SayForward;
import com.wsoteam.diet.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FragmentResult extends Fragment implements SayForward {

    @BindView(R.id.edtBrand)
    EditText edtBrand;
    @BindView(R.id.edtName) EditText edtName;
    @BindView(R.id.edtBarcode) EditText edtBarcode;

    @BindView(R.id.edtKcal) EditText edtKcal;
    @BindView(R.id.edtFats) EditText edtFats;
    @BindView(R.id.edtCarbo) EditText edtCarbo;
    @BindView(R.id.edtProt) EditText edtProt;

    @BindView(R.id.edtCell) EditText edtCell;
    @BindView(R.id.edtSugar) EditText edtSugar;
    @BindView(R.id.edtCholesterol) EditText edtCholesterol;
    @BindView(R.id.edtSodium) EditText edtSodium;
    @BindView(R.id.edtPottassium) EditText edtPottassium;

    @BindView(R.id.edtSaturatedFats) EditText edtSaturatedFats;
    @BindView(R.id.edtMonoUnSaturatedFats) EditText edtMonoUnSaturatedFats;
    @BindView(R.id.edtPolyUnSaturatedFats) EditText edtPolyUnSaturatedFats;

    Unbinder unbinder;
    private final String EMPTY_FIELD = "-";
    private final double EMPTY_PARAM = -1;

    @Override
    public boolean forward() {
        return true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            bindFields();
        }
    }

    private void bindFields() {
        CustomFood customFood = ((ActivityCreateFood) getActivity()).customFood;
        edtName.setText(customFood.getName());
        edtKcal.setText(String.valueOf(customFood.getCalories()));
        edtFats.setText(String.valueOf(customFood.getFats()));
        edtCarbo.setText(String.valueOf(customFood.getCarbohydrates()));
        edtProt.setText(String.valueOf(customFood.getProteins()));

        if (customFood.getBrand().equals("")) {
            edtBrand.setText(EMPTY_FIELD);
        } else {
            edtBrand.setText(customFood.getBrand());
        }
        if (customFood.getBarcode().equals("")) {
            edtBarcode.setText(EMPTY_FIELD);
        } else {
            edtBarcode.setText(customFood.getBarcode());
        }

        if (customFood.getCellulose() == EMPTY_PARAM){
            edtCell.setText(EMPTY_FIELD);
        }else {
            edtCell.setText(String.valueOf(customFood.getCellulose()));
        }

        if (customFood.getSugar() == EMPTY_PARAM){
            edtSugar.setText(EMPTY_FIELD);
        }else {
            edtSugar.setText(String.valueOf(customFood.getSugar()));
        }

        if (customFood.getCholesterol() == EMPTY_PARAM){
            edtCholesterol.setText(EMPTY_FIELD);
        }else {
            edtCholesterol.setText(String.valueOf(customFood.getCholesterol()));
        }

        if (customFood.getSodium() == EMPTY_PARAM){
            edtSodium.setText(EMPTY_FIELD);
        }else {
            edtSodium.setText(String.valueOf(customFood.getSodium()));
        }

        if (customFood.getPottassium() == EMPTY_PARAM){
            edtPottassium.setText(EMPTY_FIELD);
        }else {
            edtPottassium.setText(String.valueOf(customFood.getPottassium()));
        }

        if (customFood.getSaturatedFats() == EMPTY_PARAM){
            edtSaturatedFats.setText(EMPTY_FIELD);
        }else {
            edtSaturatedFats.setText(String.valueOf(customFood.getSaturatedFats()));
        }

        if (customFood.getMonoUnSaturatedFats() == EMPTY_PARAM){
            edtMonoUnSaturatedFats.setText(EMPTY_FIELD);
        }else {
            edtMonoUnSaturatedFats.setText(String.valueOf(customFood.getMonoUnSaturatedFats()));
        }

        if (customFood.getPolyUnSaturatedFats() == EMPTY_PARAM){
            edtPolyUnSaturatedFats.setText(EMPTY_FIELD);
        }else {
            edtPolyUnSaturatedFats.setText(String.valueOf(customFood.getPolyUnSaturatedFats()));
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            setUserVisibleHint(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

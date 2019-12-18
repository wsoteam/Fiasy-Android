package com.wsoteam.diet.presentation.search.sections.custom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.wsoteam.diet.BranchOfAnalyzer.CustomFood.CustomFood;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.networking.food.POJO.Result;
import com.wsoteam.diet.presentation.search.sections.custom.ActivityCreateFood;
import com.wsoteam.diet.presentation.search.sections.custom.SayForward;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FragmentOutlay extends Fragment implements SayForward {

    @BindView(R.id.edtKcal)
    EditText edtKcal;
    @BindView(R.id.edtFats) EditText edtFats;
    @BindView(R.id.edtCarbo) EditText edtCarbo;
    @BindView(R.id.edtProt) EditText edtProt;

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
        if (isCanForward()){
            setInfo();
            return true;
        }else {
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
                && !edtProt.getText().toString().equals(".")){
            return true;
        }else {
            return false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outlay_new, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (((ActivityCreateFood) getActivity()).isEdit) {
            bindFields((CustomFood) getArguments().getSerializable(TAG));
        }
        return view;
    }

    private void bindFields(CustomFood customFood) {
        edtKcal.setText(String.valueOf(customFood.getCalories() * 100));
        edtFats.setText(String.valueOf(customFood.getFats() * 100));
        edtCarbo.setText(String.valueOf(customFood.getCarbohydrates() * 100));
        edtProt.setText(String.valueOf(customFood.getProteins() * 100));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

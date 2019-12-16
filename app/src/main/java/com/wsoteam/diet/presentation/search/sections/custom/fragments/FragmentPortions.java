package com.wsoteam.diet.presentation.search.sections.custom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wsoteam.diet.BranchOfAnalyzer.CustomFood.CustomFood;
import com.wsoteam.diet.R;
import com.wsoteam.diet.presentation.search.sections.custom.ActivityCreateFood;
import com.wsoteam.diet.presentation.search.sections.custom.SayForward;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FragmentPortions extends Fragment implements SayForward {
    private final static String TAG = "FragmentPortions";
    private Unbinder unbinder;

    public static FragmentPortions newInstance(CustomFood customFood) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG, customFood);
        FragmentPortions fragmentMainInfo = new FragmentPortions();
        fragmentMainInfo.setArguments(bundle);
        return fragmentMainInfo;
    }


    @Override
    public boolean forward() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_portions, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (((ActivityCreateFood) getActivity()).isEdit) {
            bindFields((CustomFood) getArguments().getSerializable(TAG));
        }
        return view;
    }

    private void bindFields(CustomFood customFood) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

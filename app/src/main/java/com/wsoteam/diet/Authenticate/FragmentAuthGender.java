package com.wsoteam.diet.Authenticate;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wsoteam.diet.R;

public class FragmentAuthGender extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.auth_fragment_gender, null);

        view.findViewById(R.id.auth_gender_f).setOnClickListener((View.OnClickListener) getActivity());
        view.findViewById(R.id.auth_gender_m).setOnClickListener((View.OnClickListener) getActivity());

        return view;
    }

}

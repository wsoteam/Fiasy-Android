package com.wsoteam.diet.Authenticate;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.wsoteam.diet.R;

public class FragmentAuthFirst extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.intro_activity, null);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        Button
        view.findViewById(R.id.auth_first_btn_registration).setOnClickListener((View.OnClickListener) getActivity());
        view.findViewById(R.id.auth_first_btn_signin).setOnClickListener((View.OnClickListener) getActivity());

        return view;
    }
}
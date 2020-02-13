package com.wsoteam.diet.Authenticate;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wsoteam.diet.R;

public class FragmentAuthHello extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.auth_fragment_hello, null);

//        Button
        view.findViewById(R.id.auth_hello_btn_next).setOnClickListener((View.OnClickListener) getActivity());

        return view;
    }
}
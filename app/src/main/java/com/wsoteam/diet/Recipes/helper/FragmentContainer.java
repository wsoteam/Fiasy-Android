package com.wsoteam.diet.Recipes.helper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import com.wsoteam.diet.BranchProfile.Fragments.FragmentProfile;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Recipes.favorite.FavoriteRecipesFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentContainer extends Fragment {

    Fragment firstFragment;
    Fragment secondFragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_container, container, false);
        ButterKnife.bind(this, view);
        firstFragment = new FavoriteRecipesFragment();
        secondFragment = new FragmentProfile();

        return view;
    }

    @OnClick({R.id.radio0, R.id.radio1})
    public void onRadioButtonClicked(RadioButton radioButton) {
        // Is the button now checked?
        boolean checked = radioButton.isChecked();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        // Check which radio button was clicked
        switch (radioButton.getId()) {
            case R.id.radio0:
                if (checked) {
                    transaction.replace(R.id.childContainer, firstFragment).commit();
                }
                break;
            case R.id.radio1:
                if (checked) {
                    transaction.replace(R.id.childContainer, secondFragment).commit();
                }
                break;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        firstFragment = new FavoriteRecipesFragment();
        secondFragment = new FragmentProfile();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.childContainer, firstFragment).commit();
    }
}

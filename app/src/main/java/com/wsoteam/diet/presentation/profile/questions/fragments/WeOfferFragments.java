package com.wsoteam.diet.presentation.profile.questions.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import com.wsoteam.diet.R;

public class WeOfferFragments extends Fragment {


  public static WeOfferFragments newInstance() {
    return new WeOfferFragments();
  }

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_we_offer, container, false);
    ButterKnife.bind(this, view);

    return view;
  }
}

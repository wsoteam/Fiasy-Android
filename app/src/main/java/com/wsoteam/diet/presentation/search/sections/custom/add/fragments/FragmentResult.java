package com.wsoteam.diet.presentation.search.sections.custom.add.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.networking.food.POJO.Result;
import com.wsoteam.diet.presentation.search.sections.custom.add.ActivityCreateFood;
import com.wsoteam.diet.presentation.search.sections.custom.add.SayForward;
import com.wsoteam.diet.presentation.search.sections.custom.add.fragments.controller.result.ResultAdapter;

public class FragmentResult extends Fragment implements SayForward {

  @BindView(R.id.rvResult) RecyclerView rvResult;

  private ResultAdapter adapter;
  private Result result;
  private Unbinder unbinder;

  @Override
  public boolean forward() {
    finCalculate(result);
    return true;
  }

  private void finCalculate(Result result) {
    result.setCalories(result.getCalories() / result.getPortion());
    result.setFats(result.getFats() / result.getPortion());
    result.setCarbohydrates(result.getCarbohydrates() / result.getPortion());
    result.setProteins(result.getProteins() / result.getPortion());

    if (result.getSaturatedFats() != Config.EMPTY_COUNT){
      result.setSaturatedFats(result.getSaturatedFats() / result.getPortion());
    }
    if (result.getMonounsaturatedFats() != Config.EMPTY_COUNT){
      result.setMonounsaturatedFats(result.getMonounsaturatedFats() / result.getPortion());
    }
    if (result.getPolyunsaturatedFats() != Config.EMPTY_COUNT){
      result.setPolyunsaturatedFats(result.getPolyunsaturatedFats() / result.getPortion());
    }
    if (result.getCholesterol() != Config.EMPTY_COUNT){
      result.setCholesterol(result.getCholesterol() / result.getPortion());
    }
    if (result.getCellulose() != Config.EMPTY_COUNT){
      result.setCellulose(result.getCellulose() / result.getPortion());
    }
    if (result.getSodium() != Config.EMPTY_COUNT){
      result.setSodium(result.getSodium() / result.getPortion());
    }
    if (result.getPottasium() != Config.EMPTY_COUNT){
      result.setPottasium(result.getPottasium() / result.getPortion());
    }
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser && isResumed()) {
      updateUI();
    }
  }

  @Override public boolean checkForwardPossibility() {
    return true;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_result, container, false);
    unbinder = ButterKnife.bind(this, view);
    rvResult.setLayoutManager(new LinearLayoutManager(getActivity()));
    result = ((ActivityCreateFood) getActivity()).customFood;
    return view;
  }

  private void updateUI() {
    adapter = new ResultAdapter(result);
    rvResult.setAdapter(adapter);
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

package com.wsoteam.diet.presentation.search.sections.custom.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import com.wsoteam.diet.BranchOfAnalyzer.CustomFood.CustomFood;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.networking.food.POJO.MeasurementUnit;
import com.wsoteam.diet.common.networking.food.POJO.Result;
import com.wsoteam.diet.presentation.search.sections.custom.ActivityChangeMainPortion;
import com.wsoteam.diet.presentation.search.sections.custom.ActivityCreateFood;
import com.wsoteam.diet.presentation.search.sections.custom.ActivityCreatePortion;
import com.wsoteam.diet.presentation.search.sections.custom.SayForward;
import com.wsoteam.diet.presentation.search.sections.custom.fragments.controller.IPortionsAdapter;
import com.wsoteam.diet.presentation.search.sections.custom.fragments.controller.PortionsAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FragmentPortions extends Fragment implements SayForward {
  private final static String TAG = "FragmentPortions";
  @BindView(R.id.rvPortions)
  RecyclerView rvPortions;
  private Unbinder unbinder;
  private PortionsAdapter adapter;
  private final int RC_CREATE_PORTION = 9090;
  private final int RC_CHANGE_MAIN_PORTION = 9000;
  private Result result;

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
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_portions, container, false);
    unbinder = ButterKnife.bind(this, view);
    result = ((ActivityCreateFood) getActivity()).customFood;
    rvPortions.setLayoutManager(new LinearLayoutManager(getActivity()));
    updateList();
    return view;
  }


  private void updateList() {
    adapter = new PortionsAdapter(new IPortionsAdapter() {
      @Override
      public void createPortion() {
        startActivityForResult(new Intent(getActivity(), ActivityCreatePortion.class)
            .putExtra(Config.TAG_IS_LIQUID, result.isLiquid()), RC_CREATE_PORTION);
      }

      @Override public void changeMainPortion() {
        startActivityForResult(new Intent(getActivity(), ActivityChangeMainPortion.class)
            .putExtra(Config.TAG_IS_LIQUID, result.isLiquid())
            .putExtra(Config.SIZE_MAIN_PORTION, result.getPortion()), RC_CHANGE_MAIN_PORTION);
      }
    }, result, getActivity());
    rvPortions.setAdapter(adapter);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == RC_CREATE_PORTION && resultCode == RESULT_OK) {
      addNewPortion(data.getSerializableExtra(Config.NEW_MEASURMENT));
    } else if (requestCode == RC_CHANGE_MAIN_PORTION && resultCode == RESULT_OK) {
      changeMainPortion(data.getDoubleExtra(Config.NEW_SIZE_MAIN_PORTION, 0));
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void addNewPortion(Serializable serializableExtra) {
    MeasurementUnit measurementUnit = (MeasurementUnit) serializableExtra;
    if (result.getMeasurementUnits() == null) {
      List<MeasurementUnit> units = new ArrayList<>();
      units.add(measurementUnit);
      result.setMeasurementUnits(units);
    } else {
      result.getMeasurementUnits().add(measurementUnit);
    }
    updateList();
  }

  private void changeMainPortion(double newMainPortion) {
    result.setPortion(newMainPortion);
    updateList();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}

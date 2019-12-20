package com.wsoteam.diet.presentation.search.sections.custom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.wsoteam.diet.R;
import com.wsoteam.diet.presentation.search.sections.custom.SayForward;

public class FragmentResult extends Fragment implements SayForward {

  Unbinder unbinder;
  private final String EMPTY_FIELD = "-";
  private final double EMPTY_PARAM = -1;
  @BindView(R.id.rvResult) RecyclerView rvResult;

  @Override
  public boolean forward() {
    return true;
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser && isResumed()) {

    }
  }

  @Override public boolean checkForwardPossibility() {
    return false;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
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

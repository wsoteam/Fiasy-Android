package com.wsoteam.diet.presentation.search.sections;

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
import com.wsoteam.diet.R;
import com.wsoteam.diet.presentation.search.sections.controller.SectionAdapter;

public class SectionFragment extends Fragment {
  @BindView(R.id.rvSections) RecyclerView rvSections;
  private SectionAdapter adapter;
  private Unbinder unbinder;

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_section, container, false);
    unbinder = ButterKnife.bind(this, view);
    rvSections.setLayoutManager(new LinearLayoutManager(getContext()));
    updateUI();

    return view;
  }

  private void updateUI() {
    adapter = new SectionAdapter(getContext());
    rvSections.setAdapter(adapter);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}

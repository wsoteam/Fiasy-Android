package com.wsoteam.diet.presentation.search.sections.custom.fragments.controller.result;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;
import com.wsoteam.diet.R;

public class BodyVH extends RecyclerView.ViewHolder {
  public BodyVH(LayoutInflater layoutInflater, ViewGroup viewGroup) {
    super(layoutInflater.inflate(R.layout.item_body_result, viewGroup, false));
    ButterKnife.bind(this, itemView);
  }
}

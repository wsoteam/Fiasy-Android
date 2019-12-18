package com.wsoteam.diet.presentation.search.sections.custom.fragments.controller;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.networking.food.POJO.MeasurementUnit;

public class CustomPortionsVH extends RecyclerView.ViewHolder {
  @BindView(R.id.tvNumber) TextView tvNumber;
  @BindView(R.id.tvTitle) TextView tvTitle;
  @BindView(R.id.tvWeight) TextView tvWeight;

  public CustomPortionsVH(LayoutInflater layoutInflater, ViewGroup viewGroup) {
    super(layoutInflater.inflate(R.layout.item_portions_custom, viewGroup, false));
    ButterKnife.bind(this, itemView);
  }

  public void bind(int number, MeasurementUnit measurementUnit, boolean isLiquid) {
    String unit = itemView.getResources().getString(R.string.cst_g);
    if (isLiquid) {
      unit = itemView.getResources().getString(R.string.cst_ml);
    }
    tvWeight.setText(String.valueOf(measurementUnit.getAmount()) + " " + unit);
    tvNumber.setText(String.valueOf(number) + ".");
    tvTitle.setText(measurementUnit.getName());
  }

  @OnClick(R.id.ibDelete) public void onViewClicked() {
  }
}

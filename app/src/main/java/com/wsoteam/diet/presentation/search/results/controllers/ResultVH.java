package com.wsoteam.diet.presentation.search.results.controllers;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.networking.food.POJO.Result;

public class ResultVH extends RecyclerView.ViewHolder {
  @BindView(R.id.tvTitle) TextView tvTitle;
  @BindView(R.id.tvPortion) TextView tvPortion;
  @BindView(R.id.tvKcal) TextView tvKcal;
  @BindView(R.id.tbSelect) ToggleButton tbSelect;
  private ClickListener clickListener;

  public ResultVH(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup) {
    super(layoutInflater.inflate(R.layout.item_search_result, viewGroup, false));
    ButterKnife.bind(this, itemView);
  }

  public void bind(Result food, boolean isChecked,
      ClickListener clickListener) {
    this.clickListener = clickListener;
    tvTitle.setText(food.getName());
    tvPortion.setText(itemView.getResources().getString(R.string.srch_default_portion));
    tvKcal.setText(String.valueOf(Math.round(food.getCalories() * 100)) + " Ккал");
    if (food.getBrand() != null && !food.getBrand().getName().equals("")) {
      tvTitle.append(" (" + food.getBrand().getName() + ")");
    }
    tbSelect.setOnCheckedChangeListener(null);
    tbSelect.setChecked(isChecked);
    tbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        clickListener.click(getAdapterPosition(), b);
      }
    });
  }
}

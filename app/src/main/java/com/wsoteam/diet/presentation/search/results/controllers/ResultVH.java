package com.wsoteam.diet.presentation.search.results.controllers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.wsoteam.diet.R;
import com.wsoteam.diet.common.networking.food.POJO.Result;
import com.wsoteam.diet.presentation.search.basket.db.HistoryEntity;

public class ResultVH extends RecyclerView.ViewHolder implements View.OnClickListener {
  @BindView(R.id.tvTitle) TextView tvTitle;
  @BindView(R.id.tvPortion) TextView tvPortion;
  @BindView(R.id.tvKcal) TextView tvKcal;
  @BindView(R.id.tbSelect) ToggleButton tbSelect;
  private ClickListener clickListener;

  public ResultVH(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup) {
    super(layoutInflater.inflate(R.layout.item_search_result, viewGroup, false));
    ButterKnife.bind(this, itemView);
    itemView.setOnClickListener(this);
  }

  @Override public void onClick(View view) {
    clickListener.open(getAdapterPosition());
  }

  public void bind(Result food, boolean isChecked,
      ClickListener clickListener) {
    this.clickListener = clickListener;
    tvTitle.setText(food.getName());
    tvPortion.setText(itemView.getResources().getString(R.string.srch_default_portion));
    tvKcal.setText(String.valueOf(Math.round(food.getCalories() * 100)) + " " + itemView.getContext().getResources().getString(R.string.tvKkal));

      if (food.getBrand() != null && food.getBrand().getName() != null && !food.getBrand().getName().equals("")) {
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

  public void bind(Result food, boolean isChecked,
                   ClickListener clickListener, boolean hideTbSelect) {
    bind(food, isChecked, clickListener);
    tbSelect.setVisibility(hideTbSelect ? View.GONE : View.VISIBLE);

  }

  public void bindHistoryEntity(HistoryEntity historyEntity, boolean isChecked, ClickListener clickListener) {
    this.clickListener = clickListener;
    tvTitle.setText(historyEntity.getName());
    if (historyEntity.isLiquid()){
      tvPortion.setText(String.valueOf(historyEntity.getCountPortions() * historyEntity.getSizePortion()) + " " + itemView.getResources().getString(R.string.srch_ml));
    }else {
      tvPortion.setText(String.valueOf(historyEntity.getCountPortions() * historyEntity.getSizePortion()) + " " + itemView.getResources().getString(R.string.srch_gramm));
    }
    tvKcal.setText(String.valueOf(Math.round(historyEntity.getCalories()) + " " + itemView.getContext().getResources().getString(R.string.tvKkal)));
    if (historyEntity.getBrand() != null && !historyEntity.getBrand().equals("")) {
      tvTitle.append(" (" + historyEntity.getBrand() + ")");
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

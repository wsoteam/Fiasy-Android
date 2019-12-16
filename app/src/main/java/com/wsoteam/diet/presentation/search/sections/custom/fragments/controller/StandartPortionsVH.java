package com.wsoteam.diet.presentation.search.sections.custom.fragments.controller;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wsoteam.diet.R;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StandartPortionsVH extends RecyclerView.ViewHolder {
    @BindView(R.id.tvNumber)
    TextView tvNumber;
    @BindView(R.id.rvTitle)
    TextView rvTitle;
    @BindView(R.id.tvWeight)
    TextView tvWeight;
    @BindView(R.id.tvProp)
    TextView tvProp;

    public StandartPortionsVH(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        super(layoutInflater.inflate(R.layout.item_portions_standart, viewGroup, false));
        ButterKnife.bind(this, itemView);
    }
}

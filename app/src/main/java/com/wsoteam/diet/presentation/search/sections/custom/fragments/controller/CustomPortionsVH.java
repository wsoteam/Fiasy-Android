package com.wsoteam.diet.presentation.search.sections.custom.fragments.controller;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wsoteam.diet.R;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;

public class CustomPortionsVH extends RecyclerView.ViewHolder {
    public CustomPortionsVH(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        super(layoutInflater.inflate(R.layout.item_portions_custom, viewGroup, false));
        ButterKnife.bind(this, itemView);
    }
}

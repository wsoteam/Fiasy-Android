package com.wsoteam.diet.presentation.search.sections.custom.fragments.controller;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wsoteam.diet.R;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;

public class AddPortionsVH extends RecyclerView.ViewHolder {
    public AddPortionsVH(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        super(layoutInflater.inflate(R.layout.item_portions_add, viewGroup, false));
        ButterKnife.bind(this, itemView);
    }
}

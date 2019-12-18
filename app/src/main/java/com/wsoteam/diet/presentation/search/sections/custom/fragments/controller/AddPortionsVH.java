package com.wsoteam.diet.presentation.search.sections.custom.fragments.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wsoteam.diet.R;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;

public class AddPortionsVH extends RecyclerView.ViewHolder implements View.OnClickListener {
    private IAddPortionsVH iAddPortionsVH;
    public AddPortionsVH(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        super(layoutInflater.inflate(R.layout.item_portions_add, viewGroup, false));
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        iAddPortionsVH.addPortion();
    }

    public void bind(IAddPortionsVH iAddPortionsVH) {
        this.iAddPortionsVH = iAddPortionsVH;
    }
}

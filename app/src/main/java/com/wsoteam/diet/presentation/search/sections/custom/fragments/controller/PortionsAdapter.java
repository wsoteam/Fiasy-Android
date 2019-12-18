package com.wsoteam.diet.presentation.search.sections.custom.fragments.controller;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wsoteam.diet.App;
import com.wsoteam.diet.common.networking.food.POJO.MeasurementUnit;
import com.wsoteam.diet.common.networking.food.POJO.Result;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PortionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int HEADER_TYPE = 0;
    private final int MAIN_TYPE = 1;
    private final int STANDART_TYPE = 2;
    private final int ADD_TYPE = 3;
    private IPortions iPortions;
    private Result result;
    private int COUNT_UTILITY_VH = 3;

    public PortionsAdapter(IPortions iPortions, Result result) {
        this.iPortions = iPortions;
        this.result = result;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(App.getContext());
        switch (viewType) {
            case HEADER_TYPE:
                return new HeaderPortionsVH(layoutInflater, parent);
            case MAIN_TYPE:
                return new StandartPortionsVH(layoutInflater, parent);
            case STANDART_TYPE:
                return new CustomPortionsVH(layoutInflater, parent);
            case ADD_TYPE:
                return new AddPortionsVH(layoutInflater, parent);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_TYPE;
        } else if (position == 1) {
            return MAIN_TYPE;
        } else if (position == getItemCount() - 1) {
            return ADD_TYPE;
        } else {
            return STANDART_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        int countPortions = 0;
        if (result != null && result.getMeasurementUnits() != null) {
            countPortions = result.getMeasurementUnits().size();
        }
        return countPortions + COUNT_UTILITY_VH;
    }
}

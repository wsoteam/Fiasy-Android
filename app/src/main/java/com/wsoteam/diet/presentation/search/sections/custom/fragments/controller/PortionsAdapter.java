package com.wsoteam.diet.presentation.search.sections.custom.fragments.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wsoteam.diet.common.networking.food.POJO.Result;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PortionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int HEADER_TYPE = 0;
    private final int MAIN_TYPE = 1;
    private final int CUSTOM_TYPE = 2;
    private final int ADD_TYPE = 3;
    private IPortionsAdapter iPortions;
    private Result result;
    private int COUNT_UTILITY_VH = 3;
    private int COUNT_UTILITY_VH_UNTIL_CUSTOM_PORTION = 2;
    private Context context;

    public PortionsAdapter(IPortionsAdapter iPortions, Result result, Context context) {
        this.iPortions = iPortions;
        this.result = result;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        switch (viewType) {
            case HEADER_TYPE:
                return new HeaderPortionsVH(layoutInflater, parent);
            case MAIN_TYPE:
                return new StandartPortionsVH(layoutInflater, parent);
            case CUSTOM_TYPE:
                return new CustomPortionsVH(layoutInflater, parent);
            case ADD_TYPE:
                return new AddPortionsVH(layoutInflater, parent);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case HEADER_TYPE:
                ((HeaderPortionsVH) holder).bind();
                break;
            case MAIN_TYPE:
                ((StandartPortionsVH) holder).bind(result.getPortion(), getItemCount() == COUNT_UTILITY_VH, new IStandartPortion(){
                    @Override public void change() {
                        iPortions.changeMainPortion();
                    }
                });
                break;
            case CUSTOM_TYPE:
                ((CustomPortionsVH) holder).bind(position, result.getMeasurementUnits().get(position - COUNT_UTILITY_VH_UNTIL_CUSTOM_PORTION), result.isLiquid(), new ICustomPortionsVH(){
                    @Override public void deleteItem(int position) {
                        dropItem(position);
                    }
                });
                break;
            case ADD_TYPE:
                ((AddPortionsVH) holder).bind(new IAddPortionsVH(){
                    @Override
                    public void addPortion() {
                        iPortions.createPortion();
                    }
                });
                break;
        }
    }

    private void dropItem(int position) {
        int currentPosition = position - COUNT_UTILITY_VH_UNTIL_CUSTOM_PORTION;
        result.getMeasurementUnits().remove(currentPosition);
        notifyItemRemoved(position);
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
            return CUSTOM_TYPE;
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

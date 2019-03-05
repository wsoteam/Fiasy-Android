package com.wsoteam.diet.MainScreen.Controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wsoteam.diet.BranchOfAnalyzer.POJOEating.Eating;

import java.util.List;

public class EatingAdapter extends RecyclerView.Adapter<EatingViewHolder> {
    List<List<Eating>> allEatingGroups;
    Context context;

    public EatingAdapter(List<List<Eating>> allEatingGroups, Context context) {
        this.allEatingGroups = allEatingGroups;
        this.context = context;
    }

    @NonNull
    @Override
    public EatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new EatingViewHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull EatingViewHolder holder, int position) {
        String nameOfGroup = new String();
        switch (position) {
            case 0:
                nameOfGroup = "Завтрак";
                break;
            case 1:
                nameOfGroup = "Обед";
                break;
            case 2:
                nameOfGroup = "Ужин";
                break;
            case 3:
                nameOfGroup = "Перекус";
                break;
        }
        holder.bind(allEatingGroups.get(position), context, nameOfGroup);
    }

    @Override
    public int getItemCount() {
        return allEatingGroups.size();
    }
}

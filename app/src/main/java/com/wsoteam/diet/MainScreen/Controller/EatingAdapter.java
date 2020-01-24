package com.wsoteam.diet.MainScreen.Controller;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.model.Breakfast;
import com.wsoteam.diet.model.Dinner;
import com.wsoteam.diet.model.Eating;

import com.wsoteam.diet.model.Lunch;
import com.wsoteam.diet.model.Snack;
import com.wsoteam.diet.model.Water;
import java.util.Calendar;
import java.util.List;

public class EatingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private static final int TYPE_BREAKFAST = 0;
  private static final int TYPE_LUNCH = 1;
  private static final int TYPE_DINNER = 2;
  private static final int TYPE_SNACKS = 3;
  private static final int TYPE_WATER = 4;
  private static final long DAY = 86400000l;
  private List<List<Eating>> allEatingGroups;
  private Context context;
  private String data;

  private UpdateCallback updateCallback;

  public EatingAdapter(List<List<Eating>> allEatingGroups, Context context, String data,
      UpdateCallback updateCallback) {
    this.allEatingGroups = allEatingGroups;
    this.context = context;
    this.data = data;
    this.updateCallback = updateCallback;
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater layoutInflater = LayoutInflater.from(context);
    switch (viewType) {
      case TYPE_BREAKFAST:
      case TYPE_LUNCH:
      case TYPE_DINNER:
      case TYPE_SNACKS:
        return new EatingViewHolder(layoutInflater, parent, context, data, updateCallback);
      case TYPE_WATER:
        return new WaterViewHolder(parent, data);
      default:
        throw new IllegalArgumentException("Invalid view type");
    }
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    String nameOfGroup = "";
    switch (position) {
      case 0:
        nameOfGroup = context.getString(R.string.breakfast);
        break;
      case 1:
        nameOfGroup = context.getString(R.string.lunch);
        break;
      case 2:
        nameOfGroup = context.getString(R.string.dinner);
        break;
      case 3:
        nameOfGroup = context.getString(R.string.snack);
        break;
      case 4:
        nameOfGroup = context.getString(R.string.water);
        break;
    }

    if (holder instanceof EatingViewHolder) {
      ((EatingViewHolder) holder).bind(allEatingGroups.get(position), context, nameOfGroup,
          new IEatingVH() {
            @Override public void copyToday(int type) {
              copyEatingsToday(type);
            }

            @Override public void copyTomorrow(int type) {
              copyEatingsTomorrow(type);
            }
          });
    } else if (holder instanceof WaterViewHolder) {
      ((WaterViewHolder) holder).bind(allEatingGroups.get(position).size() == 0 ? null :
          (Water) (allEatingGroups.get(position).get(0)), context, nameOfGroup);
    }
  }

  private void copyEatingsToday(int type) {
    Calendar calendar = Calendar.getInstance();
    List<Eating> eatings = allEatingGroups.get(type);
    for (Eating eating : eatings) {
      eating.setDay(calendar.get(Calendar.DAY_OF_MONTH));
      eating.setMonth(calendar.get(Calendar.MONTH));
      eating.setYear(calendar.get(Calendar.YEAR));
      pushInDb(eating, type);
    }
  }

  private void pushInDb(Eating eating, int type) {
    switch (type){
      case TYPE_BREAKFAST:
        WorkWithFirebaseDB.addBreakfast(new Breakfast(eating));
        break;
      case TYPE_LUNCH:
        WorkWithFirebaseDB.addLunch(new Lunch(eating));
        break;
      case TYPE_DINNER:
        WorkWithFirebaseDB.addDinner(new Dinner(eating));
        break;
      case TYPE_SNACKS:
        WorkWithFirebaseDB.addSnack(new Snack(eating));
        break;
    }
  }

  private void copyEatingsTomorrow(int type) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(calendar.getTimeInMillis() + DAY);
    List<Eating> eatings = allEatingGroups.get(type);
    for (Eating eating : eatings) {
      eating.setDay(calendar.get(Calendar.DAY_OF_MONTH));
      eating.setMonth(calendar.get(Calendar.MONTH));
      eating.setYear(calendar.get(Calendar.YEAR));
      pushInDb(eating, type);
    }
  }

  @Override
  public int getItemViewType(int position) {
    return position;
  }

  @Override
  public int getItemCount() {
    return allEatingGroups.size();
  }

  interface EatingUpdateCaqllback {
    void update(Eating eating, int mealPosition);
  }
}

package com.wsoteam.diet.presentation.search.sections.custom.fragments.controller.result;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.wsoteam.diet.App;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.networking.food.POJO.MeasurementUnit;
import com.wsoteam.diet.common.networking.food.POJO.Result;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private Result result;
  private final int TYPE_HEADER = 0;
  private final int TYPE_BODY = 1;
  private final int FIRST_HEADER_POS = 0;
  private final int SECOND_HEADER_POS = 4;
  private int DEFAULT_THIRD_HEADER_POS = 6;
  private int third_header_pos;
  private String HEADER_DATA = "";
  private List<String> data;
  private List<String> labels;

  public ResultAdapter(Result result) {
    this.result = result;
    data = new ArrayList<>();
    labels = new ArrayList<>();
    setHeadersPositions();
    createArrays();
  }

  private void setHeadersPositions() {
    third_header_pos = DEFAULT_THIRD_HEADER_POS;
    if (result.getMeasurementUnits() != null) {
      third_header_pos += result.getMeasurementUnits().size();
    }
  }

  private void createArrays() {
    String unitWeight = App.getContext().getResources().getString(R.string.cst_g);
    if (result.isLiquid()) {
      unitWeight = App.getContext().getResources().getString(R.string.cst_ml);
    }
    labels =
        Arrays.asList(App.getContext().getResources().getStringArray(R.array.cst_result_labels));
    labels = new ArrayList<>(labels);
    data.add(HEADER_DATA);
    data.add(result.getBrand().getName());
    data.add(result.getName());
    data.add((String) result.getBarcode());
    data.add(HEADER_DATA);
    data.add(String.valueOf((int) result.getPortion()) + " " + unitWeight);
    if (result.getMeasurementUnits() != null) {
      for (MeasurementUnit unit : result.getMeasurementUnits()) {
        labels.add(DEFAULT_THIRD_HEADER_POS, unit.getName());
        data.add(String.valueOf((int) unit.getAmount()) + " " + unitWeight);
      }
    }
    data.add(HEADER_DATA);
    data.add(String.valueOf((int) result.getCalories()));
    data.add(String.valueOf((int) result.getFats()));
    data.add(String.valueOf((int) result.getProteins()));
    data.add(String.valueOf((int) result.getCarbohydrates()));

    labels.add(third_header_pos, App.getContext()
        .getResources()
        .getString(R.string.cst_food_price, (int) result.getPortion()));
  }

  @NonNull @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    switch (viewType) {
      case TYPE_HEADER:
        return new HeaderVH(inflater, parent);
      case TYPE_BODY:
        return new BodyVH(inflater, parent);
      default:
        throw new IllegalArgumentException("Invalid view type");
    }
  }

  @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    switch (holder.getItemViewType()) {
      case TYPE_HEADER:
        ((HeaderVH) holder).bind(labels.get(position));
        break;
      case TYPE_BODY:
        ((BodyVH) holder).bind(labels.get(position), data.get(position),
            position == data.size() - 1);
        break;
    }
  }

  @Override public int getItemViewType(int position) {
    if (position == FIRST_HEADER_POS
        || position == SECOND_HEADER_POS
        || position == third_header_pos) {
      return TYPE_HEADER;
    } else {
      return TYPE_BODY;
    }
  }

  @Override public int getItemCount() {
    return data.size();
  }
}

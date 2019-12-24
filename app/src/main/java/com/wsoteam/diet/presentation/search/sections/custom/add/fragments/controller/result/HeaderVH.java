package com.wsoteam.diet.presentation.search.sections.custom.add.fragments.controller.result;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.wsoteam.diet.R;

public class HeaderVH extends RecyclerView.ViewHolder {
  @BindView(R.id.tvTitle) TextView tvTitle;

  public HeaderVH(LayoutInflater layoutInflater, ViewGroup viewGroup) {
    super(layoutInflater.inflate(R.layout.item_header_result, viewGroup, false));
    ButterKnife.bind(this, itemView);
  }

  public void bind(String s) {
    tvTitle.setText(s);
  }
}

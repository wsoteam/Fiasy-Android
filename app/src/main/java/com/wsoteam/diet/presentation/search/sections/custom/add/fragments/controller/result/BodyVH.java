package com.wsoteam.diet.presentation.search.sections.custom.add.fragments.controller.result;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.legacy.widget.Space;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.material.textfield.TextInputLayout;
import com.wsoteam.diet.R;

public class BodyVH extends RecyclerView.ViewHolder {
  @BindView(R.id.edtText) EditText edtText;
  @BindView(R.id.tilText) TextInputLayout tilText;
  @BindView(R.id.space) Space space;

  public BodyVH(LayoutInflater layoutInflater, ViewGroup viewGroup) {
    super(layoutInflater.inflate(R.layout.item_body_result, viewGroup, false));
    ButterKnife.bind(this, itemView);
  }

  public void bind(String label, String data, boolean isLast) {
    if (label.replaceAll("\\s+", " ").trim().equals("")) {
      tilText.setHint("-");
    } else {
      tilText.setHint(label);
    }
    edtText.setText(data);
    if (isLast){
      space.setVisibility(View.VISIBLE);
    }
  }
}

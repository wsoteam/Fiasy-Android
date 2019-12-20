package com.wsoteam.diet.presentation.search.sections.custom.fragments.controller.result;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.material.textfield.TextInputLayout;
import com.wsoteam.diet.R;

public class BodyVH extends RecyclerView.ViewHolder {
  @BindView(R.id.edtText) EditText edtText;
  @BindView(R.id.tilText) TextInputLayout tilText;

  public BodyVH(LayoutInflater layoutInflater, ViewGroup viewGroup) {
    super(layoutInflater.inflate(R.layout.item_body_result, viewGroup, false));
    ButterKnife.bind(this, itemView);
  }

  public void bind(String label, String data) {
    edtText.setText(data);
    if (label.equals("")) {
      tilText.setHelperText("-");
    } else {
      tilText.setHelperText(label);
    }
  }
}

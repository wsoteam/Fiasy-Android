package com.wsoteam.diet.presentation.measurment.help;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.wsoteam.diet.R;

public class HelpActivity extends AppCompatActivity {
  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_meas_help);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.ibBack) public void onViewClicked() {
    onBackPressed();
  }
}
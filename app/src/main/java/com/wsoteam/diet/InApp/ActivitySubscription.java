package com.wsoteam.diet.InApp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.wsoteam.diet.ABConfig;
import com.wsoteam.diet.Authenticate.POJO.Box;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.InApp.Fragments.FragmentE;
import com.wsoteam.diet.InApp.Fragments.FragmentG;
import com.wsoteam.diet.InApp.bigtest.FragmentA;
import com.wsoteam.diet.InApp.bigtest.FragmentB;
import com.wsoteam.diet.InApp.bigtest.FragmentC;
import com.wsoteam.diet.InApp.bigtest.FragmentD;
import com.wsoteam.diet.InApp.bigtest.FragmentF;
import com.wsoteam.diet.InApp.bigtest.FragmentH;
import com.wsoteam.diet.InApp.bigtest.FragmentI;
import com.wsoteam.diet.R;

public class ActivitySubscription extends AppCompatActivity {
  private String abVersion;
  private Box box;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_subscription);


//    abVersion = getSharedPreferences(ABConfig.KEY_FOR_SAVE_STATE, MODE_PRIVATE).
//        getString(ABConfig.KEY_FOR_SAVE_STATE, "default");
    box = (Box) getIntent().getSerializableExtra(Config.TAG_BOX);


    getSupportFragmentManager().beginTransaction().add(R.id.clContainer,
            FragmentG.newInstance(box)).commit();
//    switch (abVersion) {
//      default:
//      case ABConfig.A:
//        getSupportFragmentManager().beginTransaction().add(R.id.clContainer,
//            FragmentA.newInstance(box)).commit();
//        break;
//      case ABConfig.B:
//        getSupportFragmentManager().beginTransaction().add(R.id.clContainer,
//            FragmentB.newInstance(box)).commit();
//        break;
//      case ABConfig.C:
//        getSupportFragmentManager().beginTransaction().add(R.id.clContainer,
//            FragmentC.newInstance(box)).commit();
//        break;
//      case ABConfig.D:
//        getSupportFragmentManager().beginTransaction().add(R.id.clContainer,
//            FragmentD.newInstance(box)).commit();
//        break;
//      case ABConfig.E:
//        getSupportFragmentManager().beginTransaction().add(R.id.clContainer,
//            FragmentE.newInstance(box)).commit();
//        break;
//      case ABConfig.F:
//        getSupportFragmentManager().beginTransaction().add(R.id.clContainer,
//            FragmentF.newInstance(box)).commit();
//        break;
//      case ABConfig.G:
//        getSupportFragmentManager().beginTransaction().add(R.id.clContainer,
//            FragmentG.newInstance(box)).commit();
//        break;
//      case ABConfig.H:
//        getSupportFragmentManager().beginTransaction().add(R.id.clContainer,
//            FragmentH.newInstance(box)).commit();
//        break;
//      case ABConfig.I:
//        getSupportFragmentManager().beginTransaction().add(R.id.clContainer,
//                FragmentI.newInstance(box)).commit();
//        break;
//    }

  }

  @Override public void onBackPressed() {
    if (isStopVersion()) {
      finishAffinity();
    } else {
      super.onBackPressed();
    }
  }

  private boolean isStopVersion() {
    String version = getSharedPreferences(ABConfig.KEY_FOR_SAVE_STATE, MODE_PRIVATE).
        getString(ABConfig.KEY_FOR_SAVE_STATE, "default");
    return version.equals(ABConfig.C) // 3
        || version.equals(ABConfig.D) // 4
        || version.equals(ABConfig.F) // 6
        || version.equals(ABConfig.H); // 8
  }
}


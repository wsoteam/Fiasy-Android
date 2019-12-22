package com.wsoteam.diet.InApp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.wsoteam.diet.ABConfig;
import com.wsoteam.diet.Authenticate.POJO.Box;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.InApp.Fragments.FragmentA;
import com.wsoteam.diet.InApp.Fragments.FragmentB;
import com.wsoteam.diet.InApp.Fragments.FragmentC;
import com.wsoteam.diet.InApp.Fragments.FragmentD;
import com.wsoteam.diet.InApp.Fragments.FragmentE;
import com.wsoteam.diet.InApp.Fragments.FragmentF;
import com.wsoteam.diet.R;
import com.wsoteam.diet.presentation.premium.PremiumFeaturesActivity;


public class ActivitySubscription extends AppCompatActivity {
    private String abVersion;
    private Box box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        abVersion = getSharedPreferences(ABConfig.KEY_FOR_SAVE_STATE, MODE_PRIVATE).
                getString(ABConfig.KEY_FOR_SAVE_STATE, "default");
        box = (Box) getIntent().getSerializableExtra(Config.TAG_BOX);

        if (abVersion.equals(ABConfig.A_VERSION)) {
            getSupportFragmentManager().beginTransaction().add(R.id.clContainer,
                    FragmentD.newInstance(box)).commit();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.clContainer,
                    FragmentE.newInstance(box)).commit();
        }
    }
}


package com.wsoteam.diet.InApp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wsoteam.diet.ABConfig;
import com.wsoteam.diet.Authenticate.POJO.Box;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.InApp.Fragments.FragmentD;
import com.wsoteam.diet.InApp.Fragments.FragmentE;
import com.wsoteam.diet.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


public class ActivitySubscription extends AppCompatActivity {
    private String abVersion;
    private Box box;

    public static Intent open(Context context, Class<? extends Fragment> target, Box box) {
        return new Intent(context, ActivitySubscription.class)
                .putExtra("targetClass  ", target.getName())
                .putExtra(Config.TAG_BOX, box);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        box = (Box) getIntent().getSerializableExtra(Config.TAG_BOX);

        if (getIntent().hasExtra("targetClass")) {
            final String clazzName = getIntent().getStringExtra("targetClass");
            try {
                final Class<? extends Fragment> fragmentClass =
                        (Class<? extends Fragment>) Class.forName(clazzName);

                final Bundle bundle = new Bundle();
                bundle.putSerializable(Config.TAG_BOX, box);

                final Fragment fragment = fragmentClass.newInstance();
                fragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.clContainer, fragment).commit();

                return;
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }

        abVersion = getSharedPreferences(ABConfig.KEY_FOR_SAVE_STATE, MODE_PRIVATE).
                getString(ABConfig.KEY_FOR_SAVE_STATE, "default");

        if (abVersion.equals(ABConfig.A_VERSION)) {
            getSupportFragmentManager().beginTransaction().add(R.id.clContainer,
                    FragmentD.newInstance(box)).commit();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.clContainer,
                    FragmentE.newInstance(box)).commit();
        }
    }
}


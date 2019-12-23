package com.wsoteam.diet.presentation.auth;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.wsoteam.diet.R;
import com.wsoteam.diet.presentation.ResetPasswordFragment;

public class MainAuthNewActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_auth_new);

    final Fragment fragment;

    if("resetPassword".equals(getIntent().getStringExtra("mode"))){
      final Bundle bundle = new Bundle();
      bundle.putAll(getIntent().getExtras());

      fragment = new ResetPasswordFragment();
      fragment.setArguments(bundle);
    } else {
      fragment = new AuthFirstFragment();
    }

    getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.container, fragment)
        .commit();
  }

  public final void signIn(){
    if(getSupportFragmentManager().getBackStackEntryCount() > 0){
      getSupportFragmentManager().popBackStack();
    } else {
      getSupportFragmentManager()
          .beginTransaction()
          .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
          .replace(R.id.container, new AuthFirstFragment())
          .commit();
    }
  }
}

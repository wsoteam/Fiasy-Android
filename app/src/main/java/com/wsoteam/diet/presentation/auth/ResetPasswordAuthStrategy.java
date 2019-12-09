package com.wsoteam.diet.presentation.auth;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wsoteam.diet.BuildConfig;
import com.wsoteam.diet.utils.RxFirebase;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ResetPasswordAuthStrategy extends AuthStrategy {

  private String code;
  private String password;

  public ResetPasswordAuthStrategy(Fragment fragment) {
    super(fragment);
  }

  public Completable sendVerificationCode(String email) {
    ActionCodeSettings settings = ActionCodeSettings.newBuilder()
            .setAndroidPackageName(BuildConfig.APPLICATION_ID, false, BuildConfig.VERSION_NAME)
            .setHandleCodeInApp(true)
            .setUrl("https://fiasyapp.page.link/reset")
            .build();

    return RxFirebase.completable(FirebaseAuth.getInstance().sendPasswordResetEmail(email, settings));
  }

  public Single<String> checkValidCode(String code) {
    return RxFirebase.from(FirebaseAuth.getInstance()
        .verifyPasswordResetCode(code));
  }

  @Override public void enter() {
    disposeOnRelease(RxFirebase.from(FirebaseAuth.getInstance()
        .confirmPasswordReset(code, password))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess(task -> {
          final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

          if (user != null) onAuthenticated(new AuthenticationResult(user, null, false));
        })
        .subscribe());
  }
}

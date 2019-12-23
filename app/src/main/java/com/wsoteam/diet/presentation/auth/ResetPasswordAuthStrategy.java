package com.wsoteam.diet.presentation.auth;

import android.util.Log;
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
  private String email;
  private String password;

  public ResetPasswordAuthStrategy(Fragment fragment) {
    super(fragment);
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Completable sendVerificationCode(String email) {
    ActionCodeSettings settings = ActionCodeSettings.newBuilder()
        .setAndroidPackageName(BuildConfig.APPLICATION_ID, false, BuildConfig.VERSION_NAME)
        .setHandleCodeInApp(true)
        .setUrl("https://fiasyapp.page.link/reset")
        .build();

    return RxFirebase.completable(
        FirebaseAuth.getInstance().sendPasswordResetEmail(email, settings));
  }

  public Single<String> checkValidCode(String code) {
    return RxFirebase.from(FirebaseAuth.getInstance()
        .verifyPasswordResetCode(code));
  }

  @Override public void enter() {
    disposeOnRelease(RxFirebase.completable(FirebaseAuth.getInstance()
        .confirmPasswordReset(code, password))
        .observeOn(AndroidSchedulers.mainThread())
        .andThen(RxFirebase.from(FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)))
        .doOnEvent((r, e) -> {
          email = null;
          code = null;
          password = null;
        })
        .subscribe(authResult -> {
          final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

          if (BuildConfig.DEBUG) {
            Log.d("ResetPassword", "reset sucessfull, user=" + user);
          }

          if (user != null) onAuthenticated(new AuthenticationResult(user, null, false));
        }, error -> {
          error.printStackTrace();

          onAuthException(error);
        }));
  }
}

package com.wsoteam.diet.presentation.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wsoteam.diet.App;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.EntryPoint.ActivitySplash;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.presentation.profile.questions.QuestionsActivity;
import com.wsoteam.diet.utils.RxFirebase;

import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class AuthStrategy {
  public final static int AUTH_REQUEST = 100;

  private final Fragment fragment;
  private final CompositeDisposable disposables = new CompositeDisposable();

  private final MutableLiveData<AuthenticationResult> userStream = new MutableLiveData<>();

  public static void signOut(Context context){
    FirebaseAuth.getInstance().signOut();
    LoginManager.getInstance().logOut();
    GoogleAuthStrategy.signOut(context);
    UserDataHolder.clearObject();

    final String[] clearPrefs = new String[] {
        Config.ONBOARD_PROFILE_ACTIVITY,
        Config.ONBOARD_PROFILE_HEIGHT,
        Config.ONBOARD_PROFILE_NAME,
        Config.ONBOARD_PROFILE_SEX,
        Config.ONBOARD_PROFILE_WEIGHT,
        Config.ONBOARD_PROFILE_YEARS,
        Config.ONBOARD_PROFILE_PURPOSE,
    };

    final SharedPreferences.Editor editor = context
        .getSharedPreferences(Config.ONBOARD_PROFILE, Context.MODE_PRIVATE)
        .edit();

    for (String key : clearPrefs) {
      editor.remove(key);
    }

    editor.apply();
  }

  public AuthStrategy(Fragment fragment) {
    this.fragment = fragment;
  }

  protected Activity getActivity() {
    return fragment.requireActivity();
  }

  protected Fragment getFragment() {
    return fragment;
  }

  protected void disposeOnRelease(Disposable disposable) {
    disposables.add(disposable);
  }

  protected void startActivityForResult(Intent intent) {
    fragment.startActivityForResult(intent, AUTH_REQUEST);
  }

  public boolean isAuthRequest(int requestCode) {
    return requestCode == AUTH_REQUEST;
  }

  protected void handleCredentials(AuthCredential credential) {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//    credential. TODO

    if (user == null) {
      Log.d("kkk","handle cred == null");
      disposeOnRelease(RxFirebase.from(FirebaseAuth.getInstance()
              .signInWithCredential(credential))
              .subscribe(this::onAuthenticated, this::onAuthException));
    } else {

      Log.d("kkk","handle cred elese");
      user.linkWithCredential(credential).addOnFailureListener(e -> {
        Log.d("kkk","on failure", e);

        disposeOnRelease(RxFirebase.from(FirebaseAuth.getInstance()
                .signInWithCredential(credential))
                .subscribe(this::onAuthenticated, this::onAuthException));
      }).addOnCompleteListener(task1 -> {
        if (task1.isSuccessful()) {
          QuestionsActivity.setFlagQuestionsStart(App.getContext());
          Log.d("kkk","complete isSucc");
          onAuthenticated(Objects.requireNonNull(task1.getResult()));
        }
      });


    }
  }

  /**
   * Public usage to observe user
   */
  public LiveData<AuthenticationResult> liveAuthResult() {
    return userStream;
  }

  public void onActivityResult(Intent resultData, int status) {

  }

  protected void onAuthenticated(AuthenticationResult result){
    userStream.postValue(result);
  }

  protected void onAuthenticated(AuthResult result) {
    onAuthenticated(new AuthenticationResult(result.getUser(), null,
        result.getAdditionalUserInfo().isNewUser()));
  }

  protected void onAuthException(Throwable throwable) {
    userStream.postValue(new AuthenticationResult(null, throwable, false));
  }

  public abstract void enter();

  public void release() {
    disposables.clear();
  }

  public static class AuthenticationResult {
    private final FirebaseUser user;
    private final Throwable error;
    private final boolean isNewUser;

    public AuthenticationResult(FirebaseUser user, Throwable error, boolean isNewUser) {
      this.user = user;
      this.error = new Throwable(error);
      this.isNewUser = isNewUser;
    }

    public boolean isNewUser() {
      return isNewUser;
    }

    public boolean isSuccessful() {
      return user != null;
    }

    public FirebaseUser user() {
      return user;
    }

    public Throwable error() {
      return error.getCause();
    }
  }
}

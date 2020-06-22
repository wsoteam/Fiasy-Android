package com.wsoteam.diet.presentation.auth;

import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wsoteam.diet.App;
import com.wsoteam.diet.presentation.profile.questions.QuestionsActivity;
import com.wsoteam.diet.utils.RxFirebase;

import java.util.Objects;

public class EmailLoginAuthStrategy extends AuthStrategy {

  private Account account;

  public EmailLoginAuthStrategy(Fragment fragment) {
    super(fragment);
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  @Override public void enter() {
    if (account == null) {
      throw new IllegalStateException("account not given");
    }

    Task<AuthResult> authTask;

    if (account.isExisting()) {
      authTask = FirebaseAuth.getInstance()
          .signInWithEmailAndPassword(account.email, account.password);
    } else {

      AuthCredential credential = EmailAuthProvider.getCredential(account.email, account.password);
      FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

      if (user == null){
        authTask = FirebaseAuth.getInstance()
          .createUserWithEmailAndPassword(account.email, account.password);
      }else {
        authTask = user.linkWithCredential(credential).addOnCompleteListener(task1 -> {
          if (task1.isSuccessful()) {
            QuestionsActivity.setFlagQuestionsStart(App.getContext());
          }
        });
      }

    }

    disposeOnRelease(RxFirebase.from(authTask).subscribe(
        this::onAuthenticated,
        this::onAuthException
    ));
  }

  public static class Account {
    private final String email;
    private final String password;
    private final boolean existing;

    public Account(String email, String password, boolean existing) {
      this.email = email.trim();
      this.password = password.trim();
      this.existing = existing;
    }

    public String getEmail() {
      return email;
    }

    public String getPassword() {
      return password;
    }

    public boolean isExisting() {
      return existing;
    }
  }
}

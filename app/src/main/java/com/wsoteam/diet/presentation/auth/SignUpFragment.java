package com.wsoteam.diet.presentation.auth;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import com.wsoteam.diet.R;
import com.wsoteam.diet.presentation.auth.EmailLoginAuthStrategy.Account;
import com.wsoteam.diet.utils.IntentUtils;
import com.wsoteam.diet.utils.RichTextUtils;

import static android.text.TextUtils.concat;

public class SignUpFragment extends SignInFragment {

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_auth_signup, container, false);
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final TextView privacyView = view.findViewById(R.id.privacyPolicy);

    if (privacyView != null) {
      final RichTextUtils.RichText actionOpenPrivacyPolicy =
          new RichTextUtils.RichText(getString(R.string.sign_up_privacy_policy_link_span_text))
              .colorRes(requireContext(), R.color.blue)
              .underline()
              .onClick(v -> IntentUtils.openWebLink(v.getContext(),
                  "http://fiasy.com/PrivacyPolice.html"));

      privacyView.setMovementMethod(LinkMovementMethod.getInstance());
      privacyView.setText(concat(getString(R.string.sign_up_privacy_policy_title), " ",
          actionOpenPrivacyPolicy.text()));
    }
  }

  @Override protected Account getUserInputAccount() {
    final Account account = super.getUserInputAccount();
    return new Account(account.getEmail(), account.getPassword(), false);
  }

  @Override protected boolean validateForm(boolean displayError) {
    if (super.validateForm(displayError)) {
      String prev = null;

      for (TextInputLayout target : getFormInputs()) {
        if (target.getId() == R.id.password ||
            target.getId() == R.id.password2) {

          final String current = target.getEditText().getText().toString();

          if (prev != null && !prev.equals(current)) {

            if (displayError) {
              target.setError(getString(R.string.constraint_error_passwords_must_match));
            }

            return false;
          }

          prev = current;
        }
      }
      return true;
    } else {
      return false;
    }
  }
}

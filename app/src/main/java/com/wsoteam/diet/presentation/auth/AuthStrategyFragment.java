package com.wsoteam.diet.presentation.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.wsoteam.diet.BuildConfig;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.EntryPoint.ActivitySplash;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.common.Analytics.EventProperties;
import com.wsoteam.diet.common.Analytics.Events;
import com.wsoteam.diet.common.Analytics.UserProperty;
import com.wsoteam.diet.presentation.profile.questions.QuestionsActivity;
import com.wsoteam.diet.utils.NetworkService;
import com.wsoteam.diet.utils.RichTextUtils.RichText;
import com.wsoteam.diet.views.InAppNotification;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

public abstract class AuthStrategyFragment extends Fragment {

    protected static final SparseArrayCompat<Class<? extends AuthStrategy>> strategy =
            new SparseArrayCompat<Class<? extends AuthStrategy>>() {{
                put(R.id.auth_strategy_google, GoogleAuthStrategy.class);
                put(R.id.auth_strategy_facebook, FacebookAuthStrategy.class);
                put(R.id.auth_strategy_login, EmailLoginAuthStrategy.class);
                put(R.id.auth_strategy_reset, ResetPasswordAuthStrategy.class);
            }};

    private AuthStrategy authStrategy;
    private InAppNotification notification;

    private final CompositeDisposable disposables = new CompositeDisposable();

    protected final Observer<AuthStrategy.AuthenticationResult> userObserver =
            authenticationResult -> {
                if (authenticationResult == null) return;

                if (authenticationResult.isSuccessful()) {
                    final FirebaseUser user = authenticationResult.user();

                    if (authenticationResult.isNewUser()) {
                        disposables.add(NetworkService.getInstance().getApi()
                            .sign2Newsletters(user.getEmail(), "android")
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe((responseBody, throwable) -> {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                }
                                onAuthorized(user, authenticationResult.isNewUser());
                            }));
                    } else {
                        onAuthorized(user, authenticationResult.isNewUser());
                    }
                } else {
                    onAuthException(authenticationResult.error());
                }
            };

    protected <T extends AuthStrategy> T getStrategyByType(Class<T> strategyType) {
        final AuthStrategy strategy;
        if (strategyType == GoogleAuthStrategy.class) {
            Events.logPushButtonReg(EventProperties.enter_push_button_google);
            strategy = new GoogleAuthStrategy(this);
        } else if (strategyType == FacebookAuthStrategy.class) {
            Events.logPushButtonReg(EventProperties.enter_push_button_fb);
            strategy = new FacebookAuthStrategy(this);
        } else if (strategyType == EmailLoginAuthStrategy.class) {
            strategy = new EmailLoginAuthStrategy(this);
        } else {
            strategy = null;
        }

        return (T) strategy;
    }

    protected void bindAuthStrategies() {
        if (getView() == null) {
            return;
        }

        for (int strategyViewIndex = 0; strategyViewIndex < strategy.size(); strategyViewIndex++) {

            final int viewId = strategy.keyAt(strategyViewIndex);

            final View authStrategyButton = getView().findViewById(viewId);

            if (authStrategyButton == null) {
                continue;
            }

            final Class<? extends AuthStrategy> strategyType = strategy.get(viewId);
            getView().findViewById(viewId).setOnClickListener(v -> authorize(strategyType));
        }
    }

    protected void prepareAuthStrategy(AuthStrategy strategy) {
        final InAppNotification notification = getNotification();
        notification.setText(getString(R.string.auth_state_logging_in));
        notification.setProgressVisible(true, false);
        notification.show(getView(), InAppNotification.DURATION_FOREVER);
    }

    protected void onAuthorized(FirebaseUser user, boolean newUser) {
        if (BuildConfig.DEBUG) {
            Log.d("AuthStrategy", "Logged in as: " + user.getDisplayName());
        }

        final InAppNotification notification = getNotification();
        notification.setProgressVisible(false, true);

        if (TextUtils.isEmpty(user.getDisplayName())) {
            notification.setText(getString(R.string.welcome));
        } else {
            notification.setText(TextUtils.concat(getString(R.string.hello), new RichText(user.getDisplayName())
                    .bold()
                    .text()));
        }

        notification.show(getView(), InAppNotification.DURATION_QUICK);

        WorkWithFirebaseDB.setFirebaseStateListener();

        if (getActivity() != null) {
            final Activity activity = requireActivity();

            if (newUser) {
                activity.startActivity(new Intent(requireContext(), QuestionsActivity.class));
            } else {
                activity.startActivity(new Intent(requireContext(), ActivitySplash.class));
            }

            activity.finish();
        }

        if (newUser) {
            final String type;
            final String provider;

            if (getAuthStrategy() instanceof FacebookAuthStrategy) {
                type = EventProperties.registration_facebook;
                provider = UserProperty.registration_fb;
            } else if (getAuthStrategy() instanceof GoogleAuthStrategy) {
                type = EventProperties.registration_google;
                provider = UserProperty.registration_google;
            } else {
                type = EventProperties.registration_email;
                provider = UserProperty.registration_email;
            }

            Events.logRegistration(type);
            UserProperty.setUserProvider(provider);
            getActivity().getSharedPreferences(Config.IS_NEED_SHOW_LOADING_SPLASH, MODE_PRIVATE).edit().putBoolean(Config.IS_NEED_SHOW_LOADING_SPLASH, true).commit();

        } else {
            final String type;

            if (getAuthStrategy() instanceof FacebookAuthStrategy) {
                type = EventProperties.enter_facebook;
            } else if (getAuthStrategy() instanceof GoogleAuthStrategy) {
                type = EventProperties.enter_google;
            } else {
                type = EventProperties.enter_email;
            }
            getActivity().getSharedPreferences(Config.IS_NEED_SHOW_LOADING_SPLASH, MODE_PRIVATE).edit().putBoolean(Config.IS_NEED_SHOW_LOADING_SPLASH, true).commit();
            if (type == null || type.equals("")) {
                Events.logEnter(EventProperties.enter_unknown);
            } else {
                Events.logEnter(type);
            }
        }
    }

    protected void onAuthException(Throwable error) {
        error.printStackTrace();

        handleDefaultErrors(error);
    }

    protected void handleDefaultErrors(Throwable error) {
        getNotification().setProgressVisible(false, false);

        final InAppNotification notification = getNotification();

        if (error instanceof FirebaseAuthUserCollisionException) {
            notification.setText(getString(R.string.auth_user_using_existing_account));
        } else if (error instanceof FirebaseAuthInvalidCredentialsException) {
            notification.setText(getString(R.string.session_expired));
        } else if (error instanceof FirebaseAuthInvalidUserException) {
            notification.setText(getString(R.string.auth_user_not_found));
        } else if (error instanceof IOException) {
            notification.setText(getString(R.string.no_internet));
        } else {
            notification.setText(getString(R.string.failed_logIn));
        }

        notification.show(getView(), InAppNotification.DURATION_LONG);
    }

    protected void authorize(Class<? extends AuthStrategy> strategyType) {
        if (getNotification().isAttached()) {
            getNotification().dismiss();
        }

        final AuthStrategy targetStrategy;

        if (getAuthStrategy() != null && getAuthStrategy().getClass() == strategyType) {
            targetStrategy = getAuthStrategy();
        } else {
            targetStrategy = getStrategyByType(strategyType);
            setAuthStrategy(targetStrategy);
        }

        if (targetStrategy != null) {
            prepareAuthStrategy(targetStrategy);

            targetStrategy.enter();
        } else if (BuildConfig.DEBUG) {
            Log.e("AuthStrategyFragment", strategyType.getSimpleName() + " not implemented");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (authStrategy != null && authStrategy.isAuthRequest(requestCode)) {
            authStrategy.onActivityResult(data, resultCode);
        }
    }

    protected void releaseCurrentAuthStrategy() {
        if (authStrategy != null) {
            authStrategy.liveAuthResult().removeObserver(userObserver);
            authStrategy.release();
            authStrategy = null;
        }
    }

    protected void setAuthStrategy(AuthStrategy authStrategy) {
        releaseCurrentAuthStrategy();

        if (authStrategy == null) {
            return;
        }

        this.authStrategy = authStrategy;
        this.authStrategy.liveAuthResult().observe(getViewLifecycleOwner(), userObserver);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        // Facebook and third-party auth providers, might destroy instance of fragment,
        // therefore we have to restore auth-strategy instance

        if (savedInstanceState != null) {
            final String className = savedInstanceState.getString("currentAuthStrategy");

            if (TextUtils.isEmpty(className)) {
                return;
            }

            assert className != null;

            try {
                //noinspection unchecked
                Class<? extends AuthStrategy> currentAuthStrategy =
                        (Class<? extends AuthStrategy>) Class.forName(className);

                setAuthStrategy(getStrategyByType(currentAuthStrategy));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    protected void removeCurrentNotification() {
        if (notification != null && notification.isAttached()) {
            notification.dismiss();
            notification = null;
        }
    }

    protected InAppNotification getNotification() {
        removeCurrentNotification();

        return notification = new InAppNotification(requireContext());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (authStrategy != null) {
            outState.putString("currentAuthStrategy", authStrategy.getClass().getCanonicalName());
        }

        super.onSaveInstanceState(outState);
    }

    public AuthStrategy getAuthStrategy() {
        return authStrategy;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        releaseCurrentAuthStrategy();
        disposables.clear();
    }
}

package com.wsoteam.diet.EntryPoint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.amplitude.api.Amplitude;
import com.amplitude.api.Identify;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.facebook.login.LoginManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.wsoteam.diet.ABConfig;
import com.wsoteam.diet.AmplitudaEvents;
import com.wsoteam.diet.Amplitude.SetUserProperties;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.FirebaseUserProperties;
import com.wsoteam.diet.InApp.IDs;
import com.wsoteam.diet.InApp.properties.CheckAndSetPurchase;
import com.wsoteam.diet.InApp.properties.EmptySubInfo;
import com.wsoteam.diet.InApp.properties.SingletonMakePurchase;
import com.wsoteam.diet.MainScreen.MainActivity;
import com.wsoteam.diet.POJOProfile.Profile;
import com.wsoteam.diet.POJOProfile.SubInfo;
import com.wsoteam.diet.POJOProfile.TrackInfo;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.POJO.UserData;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.common.Analytics.UserProperty;
import com.wsoteam.diet.common.promo.POJO.UserPromo;
import com.wsoteam.diet.presentation.auth.AuthStrategy;
import com.wsoteam.diet.presentation.global.BaseActivity;
import com.wsoteam.diet.presentation.intro_tut.NewIntroActivity;
import com.wsoteam.diet.presentation.profile.questions.QuestionsActivity;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wsoteam.diet.Sync.WorkWithFirebaseDB.getUserData;

public class ActivitySplash extends BaseActivity {
    private final String TAG_FIRST_RUN = "TAG_FIRST_RUN";
    @Nullable
    @BindView(R.id.auth_first_iv_image)
    ImageView authFirstIvImage;
    @Nullable
    @BindView(R.id.tvSplashText)
    ImageView tvSplashText;
    private FirebaseUser user;
    private BillingClient mBillingClient;
    private SharedPreferences isBuyPrem;
    private ImageView loader;
    private TextView tvSubTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSharedPreferences(Config.IS_NEED_SHOW_LOADING_SPLASH, MODE_PRIVATE).getBoolean(
                Config.IS_NEED_SHOW_LOADING_SPLASH, false)) {
            showLoadingScreen();
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_splash);
        }
        ButterKnife.bind(this);

        //FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(getApplicationContext());
        checkFirstLaunch();
        checkRegistrationAndRun();
    }


    private void showLoadingScreen() {
        setContentView(R.layout.activity_questions_calculations);
        loader = findViewById(R.id.loader);
        tvSubTitle = findViewById(R.id.tvSubTitle);
        tvSubTitle.setText(getString(R.string.personal_diary));
        RotateAnimation rotate =
                new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatMode(Animation.INFINITE);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());

        loader.startAnimation(rotate);
    }

    private void checkRegistrationAndRun() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            try {
                SetUserProperties.setUserProperties(Adjust.getAttribution());
                setTrackInfoInDatabase(Adjust.getAttribution());
            } catch (Exception e) {

            }
            FirebaseAnalytics.getInstance(this)
                    .setUserProperty(FirebaseUserProperties.REG_STATUS, FirebaseUserProperties.reg);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            checkUser(myRef, true);
        } else {
            onUserNotAuthorized();
        }
    }

    private void checkUser(DatabaseReference myRef, boolean deeper) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserData user = getUserData(dataSnapshot);

                if (user == null) {
                    if (deeper) {
                        checkUser(myRef, false);
                    } else {
                        AuthStrategy.signOut(ActivitySplash.this);
                    }
                    return;
                }

                new UserDataHolder().bindObjectWithHolder(user);

                UserProperty.setUserProperties(UserDataHolder.getUserData().getProfile(), ActivitySplash.this);
                onSignedIn();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                UserDataHolder.clearObject();

                onUserNotAuthorized();
            }
        });
    }

    private void onUserNotAuthorized() {
        FirebaseAnalytics.getInstance(this)
                .setUserProperty(FirebaseUserProperties.REG_STATUS, FirebaseUserProperties.un_reg);
        //startActivity(new Intent(this, NewIntroActivity.class).putExtra(Config.CREATE_PROFILE, true));
        //startActivity(new Intent(this, QuestionsActivity.class).putExtra(Config.CREATE_PROFILE, true));
        //startActivity(new Intent(this, AfterQuestionsActivity.class).putExtra(Config.CREATE_PROFILE, true));

        startActivity(new Intent(this, NewIntroActivity.class).putExtra(Config.CREATE_PROFILE, true));

        finish();
    }

    private void onSignedIn() {
        checkBilling();

        if (QuestionsActivity.hasNotAskedQuestionsLeft(this)) {
            startActivity(new Intent(this, QuestionsActivity.class));
        } else {
            if (getSharedPreferences(Config.IS_NEED_SHOW_LOADING_SPLASH, MODE_PRIVATE).getBoolean(
                    Config.IS_NEED_SHOW_LOADING_SPLASH, false)) {
                new FalseWait().execute();
            } else {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }
    }

    private void openMainScreen() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void setTrackInfoInDatabase(AdjustAttribution aa) {
        TrackInfo trackInfo = new TrackInfo();
        trackInfo.setTt(aa.trackerToken);
        trackInfo.setTn(aa.trackerName);
        trackInfo.setNet(aa.network);
        trackInfo.setCam(aa.campaign);
        trackInfo.setCre(aa.creative);
        trackInfo.setCl(aa.clickLabel);
        trackInfo.setAdid(aa.adid);
        trackInfo.setAdg(aa.adgroup);
        WorkWithFirebaseDB.setTrackInfo(trackInfo);
    }

    private void checkBilling() {
        if (isHasPromo()) {
            changePremStatus(true);
        } else {
            if (SingletonMakePurchase.getInstance().isMakePurchaseNow()) {
                changePremStatus(true);
            } else if (UserDataHolder.getUserData() != null
                    && UserDataHolder.getUserData().getSubInfo() == null) {
                //unknown status premium or new user
                setSubInfoWithGooglePlayInfo();
            } else if (UserDataHolder.getUserData() != null
                    && UserDataHolder.getUserData().getSubInfo() != null
                    && !UserDataHolder.getUserData().getSubInfo().getProductId().equals(IDs.EMPTY_SUB)) {
                //user have premium status, check time of premium
                SubInfo subInfo = UserDataHolder.getUserData().getSubInfo();
                if (subInfo.getPaymentState() == 0) {
                    changePremStatus(false);
                    UserProperty.setPremStatus(UserProperty.preferential);
                    new CheckAndSetPurchase(this).execute(subInfo.getProductId(), subInfo.getPurchaseToken(),
                            subInfo.getPackageName());
                } else if (subInfo.getPaymentState() != 0) {
                    compareTime(subInfo);
                }
            } else if (UserDataHolder.getUserData() != null
                    && UserDataHolder.getUserData().getSubInfo() != null
                    && UserDataHolder.getUserData().getSubInfo().getProductId().equals(IDs.EMPTY_SUB)) {
                UserProperty.setPremStatus(UserProperty.not_buy);
                changePremStatus(false);
            }
        }
    }

    private boolean isHasPromo() {
        if (UserDataHolder.getUserData() != null && UserDataHolder.getUserData().getUserPromo() != null) {
            UserPromo userPromo = UserDataHolder.getUserData().getUserPromo();
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime <= userPromo.getStartActivated() + userPromo.getDuration()) {
                return true;
            } else {
                WorkWithFirebaseDB.setEmptyUserPromo();
                changePremStatus(false);
                return false;
            }
        } else {
            return false;
        }
    }

    private void compareTime(SubInfo subInfo) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime > subInfo.getExpiryTimeMillis()) {
            new CheckAndSetPurchase(this).execute(subInfo.getProductId(), subInfo.getPurchaseToken(),
                    subInfo.getPackageName());
        } else {
            if (subInfo.getPaymentState() == 1) {
                UserProperty.setPremStatus(UserProperty.buy);
            } else if (subInfo.getPaymentState() == 2) {
                UserProperty.setPremStatus(UserProperty.trial);
            }
            changePremStatus(true);
        }
    }

    private void setSubInfoWithGooglePlayInfo() {
        mBillingClient = BillingClient.newBuilder(this).setListener((responseCode, purchases) -> {
            if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
                //сюда мы попадем когда будет осуществлена покупка

            }
        }).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    List<Purchase> purchasesList = queryPurchases();
                    if (purchasesList.size() > 0) {
                        changePremStatus(true);
                        setSubInfo(purchasesList.get(0));
                    } else {
                        UserProperty.setPremStatus(UserProperty.not_buy);
                        EmptySubInfo.setEmptySubInfo();
                        changePremStatus(false);
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }

    private void setSubInfo(Purchase purchase) {
        new CheckAndSetPurchase(this).execute(purchase.getSku(), purchase.getPurchaseToken(),
                purchase.getPackageName());
    }

    private void getABVersion() {
        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setDefaults(R.xml.default_config);

        firebaseRemoteConfig.fetch(3600).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseRemoteConfig.activateFetched();
                Amplitude.getInstance().logEvent("norm_ab");
            } else {
                Amplitude.getInstance().logEvent("crash_ab");
            }
            setABTestConfig(firebaseRemoteConfig.getString(ABConfig.REQUEST_STRING));
        });
    }

    private void setABTestConfig(String responseString) {
        Identify abStatus = new Identify().set(ABConfig.AB_VERSION, responseString);
        Amplitude.getInstance().identify(abStatus);
        getSharedPreferences(ABConfig.KEY_FOR_SAVE_STATE, MODE_PRIVATE).
                edit().putString(ABConfig.KEY_FOR_SAVE_STATE, responseString).
                apply();
    }

    private void checkFirstLaunch() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        if (!sharedPreferences.getBoolean(TAG_FIRST_RUN, false)) {
            Calendar calendar = Calendar.getInstance();
            String day = String.valueOf(calendar.get(Calendar.DAY_OF_YEAR));
            String week = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);

            UserProperty.setDate(day, week, month);

            getSharedPreferences(Config.IS_NEED_SHOW_ONBOARD, MODE_PRIVATE).
                    edit().putBoolean(Config.IS_NEED_SHOW_ONBOARD, true).
                    apply();

            getSharedPreferences(Config.STARTING_POINT, MODE_PRIVATE).edit().putLong(Config.STARTING_POINT, getTime()).apply();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(TAG_FIRST_RUN, true);
            editor.apply();
        }
    }

    private long getTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    private List<Purchase> queryPurchases() {
        Purchase.PurchasesResult purchasesResult =
                mBillingClient.queryPurchases(BillingClient.SkuType.SUBS);
        return purchasesResult.getPurchasesList();
    }

    private void changePremStatus(boolean isPremUser) {
        getSharedPreferences(Config.STATE_BILLING, MODE_PRIVATE).edit()
                .putBoolean(Config.STATE_BILLING, isPremUser)
                .apply();
    }

    public class FalseWait extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getSharedPreferences(Config.IS_NEED_SHOW_LOADING_SPLASH, MODE_PRIVATE).edit()
                    .putBoolean(Config.IS_NEED_SHOW_LOADING_SPLASH, false)
                    .commit();
            openMainScreen();
        }
    }
}
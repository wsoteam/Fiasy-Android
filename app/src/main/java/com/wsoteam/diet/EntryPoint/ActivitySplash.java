package com.wsoteam.diet.EntryPoint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.amplitude.api.Amplitude;
import com.amplitude.api.Identify;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.wsoteam.diet.ABConfig;
import com.wsoteam.diet.Amplitude.SetUserProperties;
import com.wsoteam.diet.App;
import com.wsoteam.diet.BuildConfig;
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
import com.wsoteam.diet.presentation.auth.AuthStrategy;
import com.wsoteam.diet.presentation.global.BaseActivity;
import com.wsoteam.diet.presentation.intro_tut.NewIntroActivity;
import com.wsoteam.diet.presentation.profile.questions.QuestionsActivity;
import com.wsoteam.diet.utils.RxFirebase;
import com.wsoteam.diet.utils.UserNotAuthorized;
import com.wsoteam.diet.views.SplashBackground;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.widget.Toast.LENGTH_SHORT;
import static com.wsoteam.diet.Sync.WorkWithFirebaseDB.getUserData;

public class ActivitySplash extends BaseActivity {
  private final String TAG_FIRST_RUN = "TAG_FIRST_RUN";

  private BillingClient mBillingClient;
  private View noticeContainer;
  private View retryFrame;

  private final CompositeDisposable disposables = new CompositeDisposable();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getSharedPreferences(Config.IS_NEED_SHOW_LOADING_SPLASH, MODE_PRIVATE).getBoolean(
        Config.IS_NEED_SHOW_LOADING_SPLASH, false)) {
      showLoadingScreen();
    } else {
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
          WindowManager.LayoutParams.FLAG_FULLSCREEN);
      setContentView(R.layout.activity_splash_v2);

      findViewById(R.id.root).setBackground(new SplashBackground());

      retryFrame = findViewById(R.id.retry_frame);
      retryFrame.setVisibility(View.GONE);
      retryFrame.setOnClickListener(v -> {
        if (checkUserNetworkAvailable()) {
          checkRegistrationAndRun();

          Toast.makeText(v.getContext(),
              "Так интенрнет появился, пробуем снова", LENGTH_SHORT).show();
        }
      });

      noticeContainer = findViewById(R.id.notice_container);
      noticeContainer.setVisibility(View.GONE);
    }

    if (BuildConfig.DEBUG) {
      Log.d("ActivitySplash", "Inflated in " + (System.currentTimeMillis() - App.instance.now));
    }
  }

  @Override protected void onStart() {
    super.onStart();

    checkFirstLaunch();

    if (checkUserNetworkAvailable()) {
      checkRegistrationAndRun();
    }
  }

  private boolean checkUserNetworkAvailable() {
    if (retryFrame == null) {
      return true;
    }

    if (!hasNetwork()) {
      retryFrame.setVisibility(View.VISIBLE);
      noticeContainer.setVisibility(View.VISIBLE);
      return false;
    } else {
      retryFrame.setVisibility(View.GONE);
      noticeContainer.setVisibility(View.GONE);
      return true;
    }
  }

  private boolean hasNetwork() {
    final NetworkInfo activeNetwork =
        ContextCompat.getSystemService(this, ConnectivityManager.class)
            .getActiveNetworkInfo();

    return activeNetwork != null && activeNetwork.isConnected();
  }

  private void showLoadingScreen() {
    setContentView(R.layout.activity_questions_calculations);

    final ImageView loader = findViewById(R.id.loader);
    final TextView tvSubTitle = findViewById(R.id.tvSubTitle);
    tvSubTitle.setText(getString(R.string.personal_diary));

    final RotateAnimation rotate =
        new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f);
    rotate.setDuration(1000);
    rotate.setRepeatMode(Animation.INFINITE);
    rotate.setRepeatCount(Animation.INFINITE);
    rotate.setInterpolator(new LinearInterpolator());

    loader.startAnimation(rotate);
  }

  private void checkRegistrationAndRun() {
    final Disposable d = Single.create(new SetupUserProperties(getApplicationContext()))
        .flatMap(firebaseUser -> {
          FirebaseDatabase database = FirebaseDatabase.getInstance();
          DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
              child(FirebaseAuth.getInstance().getCurrentUser().getUid());

          return RxFirebase.just(myRef)
              .flatMap(dataSnapshot -> {
                final UserData user = getUserData(dataSnapshot);

                if (user == null) {
                  return Single.error(new UserNotAuthorized());
                } else {
                  return Single.just(user);
                }
              });
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        //.delay(15000, TimeUnit.MILLISECONDS)
        .subscribe(
            user -> {
              new UserDataHolder().bindObjectWithHolder(user);

              setUserProperties(user.getProfile());

              onSignedIn();
            },

            error -> {
              AuthStrategy.signOut(ActivitySplash.this);

              onUserNotAuthorized();
            }
        );

    disposables.add(d);
  }

  private void setUserProperties(Profile profile) {
    try {
      String goal = "", active = "", sex;
      String userStressLevel = profile.getExerciseStress();
      String userGoal = profile.getDifficultyLevel();

      String age = String.valueOf(profile.getAge());
      String weight = String.valueOf(profile.getWeight());
      String height = String.valueOf(profile.getHeight());

      if (userStressLevel.equalsIgnoreCase(getResources().getString(R.string.level_none))) {
        active = UserProperty.q_active_status1;
      } else if (userStressLevel.equalsIgnoreCase(getResources().getString(R.string.level_easy))) {
        active = UserProperty.q_active_status2;
      } else if (userStressLevel.equalsIgnoreCase(
          getResources().getString(R.string.level_medium))) {
        active = UserProperty.q_active_status3;
      } else if (userStressLevel.equalsIgnoreCase(getResources().getString(R.string.level_hard))) {
        active = UserProperty.q_active_status4;
      } else if (userStressLevel.equalsIgnoreCase(
          getResources().getString(R.string.level_up_hard))) {
        active = UserProperty.q_active_status5;
      } else if (userStressLevel.equalsIgnoreCase(getResources().getString(R.string.level_super))) {
        active = UserProperty.q_active_status6;
      } else if (userStressLevel.equalsIgnoreCase(
          getResources().getString(R.string.level_up_super))) {
        active = UserProperty.q_active_status7;
      }

      if (userGoal.equalsIgnoreCase(getResources().getString(R.string.dif_level_easy))) {
        goal = UserProperty.q_goal_status1;
      } else if (userGoal.equalsIgnoreCase(getResources().getString(R.string.dif_level_normal))) {
        goal = UserProperty.q_goal_status2;
      } else if (userGoal.equalsIgnoreCase(getResources().getString(R.string.dif_level_hard))) {
        goal = UserProperty.q_goal_status3;
      } else if (userGoal.equalsIgnoreCase(getResources().getString(R.string.dif_level_hard_up))) {
        goal = UserProperty.q_goal_status4;
      }

      if (profile.isFemale()) {
        sex = UserProperty.q_male_status_female;
      } else {
        sex = UserProperty.q_male_status_male;
      }
      UserProperty.setUserProperties(sex, height, weight, age, active, goal,
          FirebaseAuth.getInstance().getCurrentUser().getUid());
      UserProperty.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    } catch (Exception ex) {
      Log.e("LOL", ex.getLocalizedMessage());
    }
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

  private void checkBilling() {
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

    App.getInstance().setupOnDemand();

    if (!sharedPreferences.getBoolean(TAG_FIRST_RUN, false)) {
      Calendar calendar = Calendar.getInstance();
      String day = String.valueOf(calendar.get(Calendar.DAY_OF_YEAR));
      String week = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));
      String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);

      UserProperty.setDate(day, week, month);

      getSharedPreferences(Config.IS_NEED_SHOW_ONBOARD, MODE_PRIVATE).
          edit().putBoolean(Config.IS_NEED_SHOW_ONBOARD, true).
          apply();

      getSharedPreferences(Config.STARTING_POINT, MODE_PRIVATE).edit()
          .putLong(Config.STARTING_POINT, getTime())
          .apply();

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

  @Override protected void onDestroy() {
    super.onDestroy();
    disposables.clear();
  }

  public static class SetupUserProperties implements SingleOnSubscribe<FirebaseUser> {

    private final Context context;

    public SetupUserProperties(Context context) {
      this.context = context;
    }

    @Override public void subscribe(SingleEmitter<FirebaseUser> emitter) throws Exception {
      final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
      if (user != null) {
        try {
          SetUserProperties.setUserProperties(Adjust.getAttribution());
          setTrackInfoInDatabase(Adjust.getAttribution());
        } catch (Exception e) {

        }
        FirebaseAnalytics.getInstance(context)
            .setUserProperty(FirebaseUserProperties.REG_STATUS, FirebaseUserProperties.reg);

        emitter.onSuccess(user);
      } else {
        emitter.onError(new UserNotAuthorized());
      }
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
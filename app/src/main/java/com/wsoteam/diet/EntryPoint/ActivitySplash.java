package com.wsoteam.diet.EntryPoint;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.amplitude.api.Amplitude;
import com.amplitude.api.Identify;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.wsoteam.diet.ABConfig;
import com.wsoteam.diet.AmplitudaEvents;
import com.wsoteam.diet.App;
import com.wsoteam.diet.Authenticate.POJO.Box;
import com.wsoteam.diet.BuildConfig;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.FirebaseUserProperties;
import com.wsoteam.diet.InApp.ActivitySubscription;
import com.wsoteam.diet.InApp.IDs;
import com.wsoteam.diet.InApp.properties.CheckAndSetPurchase;
import com.wsoteam.diet.InApp.properties.EmptySubInfo;
import com.wsoteam.diet.InApp.properties.SingletonMakePurchase;
import com.wsoteam.diet.MainScreen.DeepLink;
import com.wsoteam.diet.MainScreen.MainActivity;
import com.wsoteam.diet.POJOProfile.SubInfo;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.POJO.UserData;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.common.Analytics.ABLiveData;
import com.wsoteam.diet.common.Analytics.EventProperties;
import com.wsoteam.diet.common.Analytics.UserProperty;
import com.wsoteam.diet.common.promo.POJO.UserPromo;
import com.wsoteam.diet.model.ModelFactory;
import com.wsoteam.diet.presentation.auth.AuthStrategy;
import com.wsoteam.diet.presentation.global.BaseActivity;
import com.wsoteam.diet.presentation.intro_tut.NewIntroActivity;
import com.wsoteam.diet.presentation.profile.questions.QuestionsActivity;
import com.wsoteam.diet.utils.DynamicUnitUtils;
import com.wsoteam.diet.utils.RxFirebase;
import com.wsoteam.diet.utils.UserNotAuthorized;
import com.wsoteam.diet.views.SplashBackground;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.wsoteam.diet.Sync.WorkWithFirebaseDB.getUserData;

public class ActivitySplash extends BaseActivity {
  private final String TAG_FIRST_RUN = "TAG_FIRST_RUN";

  private BillingClient mBillingClient;
  private View noticeContainer;
  private View retryFrame;

  private View retryFrameFirst;
  private View retryFrameChecking;
  private View retryFrameBad;


  private boolean isNoticeContainerHide = false;


  private final CompositeDisposable disposables = new CompositeDisposable();

  private void test(){

  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getABVersion();

    test();

    Intent intent = getIntent();
    String action = intent.getAction();
    Uri data = intent.getData();
    if (Intent.ACTION_VIEW.equals(action) && data != null ){
      DeepLink.prepareUri(this, data);
      intent.setData(null);
    }

    if (getSharedPreferences(Config.IS_NEED_SHOW_LOADING_SPLASH, MODE_PRIVATE).getBoolean(
        Config.IS_NEED_SHOW_LOADING_SPLASH, false)) {
      showLoadingScreen();
    } else {
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
          WindowManager.LayoutParams.FLAG_FULLSCREEN);
      setContentView(R.layout.activity_splash_v2);

      findViewById(R.id.root).setBackground(new SplashBackground());

      retryFrame = findViewById(R.id.retry_frame);
      retryFrameFirst = retryFrame.findViewById(R.id.retry_frame_first);
      retryFrameChecking = retryFrame.findViewById(R.id.retry_frame_checking);
      retryFrameBad  = retryFrame.findViewById(R.id.retry_frame_bad_internet);

      retryFrame.setVisibility(View.GONE);
      retryFrame.setOnClickListener(v -> {

        ObjectAnimator animation = ObjectAnimator.ofFloat(noticeContainer, "translationY", DynamicUnitUtils.convertDpToPixels(-200));
        animation.setDuration(400);
        animation.start();
        isNoticeContainerHide = true;

        retryFrameFirst.setVisibility(View.GONE);
        retryFrameBad.setVisibility(View.GONE);
        retryFrameChecking.setVisibility(View.VISIBLE);

        if (checkUserNetworkAvailable()) {
          checkRegistrationAndRun();

//          Toast.makeText(v.getContext(),
//              "Так интенрнет появился, пробуем снова", LENGTH_SHORT).show();
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
    Log.d("kkk", "addOnCompleteListener 11");
    if (checkUserNetworkAvailable()) {
      Log.d("kkk", "addOnCompleteListener 222");
      final FirebaseAuth mAuth = FirebaseAuth.getInstance();
      final FirebaseUser user = mAuth.getCurrentUser();

      if (user == null){
        mAuth.signInAnonymously()
                .addOnFailureListener(e -> {
                  Log.d("kkk", "addOnFailure");
                })
                .addOnCompleteListener(task -> {

                  Log.d("kkk", "addOnCompleteListener");

                  if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("kkk", "signInAnonymously:success");
                    Log.d("kkk", "user id " + mAuth.getCurrentUser().getUid());
                    WorkWithFirebaseDB.putProfileValue(ModelFactory.getDefaultProfile());
                    Log.d("kkk", "signInAnonymous-------------------");
                    checkRegistrationAndRun();
                  } else {
                    // If sign in fails, display a message to the user.
                    Log.w("kkk", "signInAnonymously:failure", task.getException());

                  }

                });
      }else {
        Log.d("kkk", "user id " + mAuth.getCurrentUser().getUid());
        Log.d("kkk", "user id " + mAuth.getCurrentUser().getUid());
//        AuthCredential credential = EmailAuthProvider.getCredential("testll@ttt.tt", "123456");
//        Log.d("kkk", "user id " + mAuth.getCurrentUser().getUid());
//        mAuth.getCurrentUser().linkWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                  @Override
//                  public void onComplete(@NonNull Task<AuthResult> task) {
//                    if (task.isSuccessful()) {
//                      Log.d("kkk", "linkWithCredential:success");
//                      FirebaseUser user = task.getResult().getUser();
//                    } else {
//                      Log.w("kkk", "linkWithCredential:failure", task.getException());
//                      Toast.makeText(ActivitySplash.this, "Authentication failed.",
//                              Toast.LENGTH_SHORT).show();
//
//                    }
//
//                    // ...
//                  }
//                });

        Log.d("kkk", "user id " + mAuth.getCurrentUser().getUid());
        checkRegistrationAndRun();
      }
    }
  }

  private boolean checkUserNetworkAvailable() {
    if (retryFrame == null) {
      return true;
    }

    if (!hasNetwork()) {
      retryFrame.setVisibility(View.VISIBLE);
      noticeContainer.setVisibility(View.VISIBLE);

      if (isNoticeContainerHide){
        new Handler().postDelayed(this::openView, (new Random().nextInt(5 - 2 + 1) + 2) * 1000);
      }

      return false;
    } else {
//      retryFrame.setVisibility(View.GONE);
      noticeContainer.setVisibility(View.GONE);
      return true;
    }
  }

  private void openView(){
    ObjectAnimator animation = ObjectAnimator.ofFloat(noticeContainer, "translationY", DynamicUnitUtils.convertDpToPixels(0));
    animation.setDuration(400);
    animation.start();
    isNoticeContainerHide = false;

    retryFrameChecking.setVisibility(View.GONE);
    retryFrameBad.setVisibility(View.VISIBLE);

  }

  private boolean hasNetwork() {
    final NetworkInfo activeNetwork =
        ContextCompat.getSystemService(this, ConnectivityManager.class)
            .getActiveNetworkInfo();

    return activeNetwork != null && activeNetwork.isConnected();
  }

  private void showLoadingScreen() {
    setContentView(R.layout.activity_questions_calculations);

    final TextView tvSubTitle = findViewById(R.id.tvSubTitle);
    tvSubTitle.setText(getString(R.string.personal_diary));
    ImageView imageView = findViewById(R.id.loader);
    Drawable drawable = imageView.getDrawable();
    if (drawable instanceof Animatable) {
      ((Animatable) drawable).start();
    }
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
        .delay(1500, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            user -> {
              new UserDataHolder().bindObjectWithHolder(user);

              UserProperty.setUserProperties(UserDataHolder.getUserData().getProfile(), ActivitySplash.this, false);

              onSignedIn();

            },

            error -> {
              AuthStrategy.signOut(ActivitySplash.this);

//              new Handler().postDelayed(() -> {
//                WorkWithFirebaseDB.putProfileValue(ModelFactory.getDefaultProfile());
//                checkRegistrationAndRun();
//              }, 3000);

//              onUserNotAuthorized();
            }
        );

    disposables.add(d);
  }

  private void startPrem(){
    Box box = new Box();
    box.setSubscribe(false);
    box.setOpenFromPremPart(true);
    box.setOpenFromIntrodaction(false);
    //TODO trial_from_*
    box.setComeFrom(AmplitudaEvents.view_prem_content);
    box.setBuyFrom(EventProperties.trial_from_articles);
    Intent intent = new Intent(this, ActivitySubscription.class).putExtra(Config.TAG_BOX, box);
    startActivity(intent);
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
    ABLiveData.getInstance().setData(responseString);
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
      final FirebaseAuth mAuth = FirebaseAuth.getInstance();
      final FirebaseUser user = mAuth.getCurrentUser();
      if (user != null) {
        String id = user.getUid();
        FirebaseAnalytics.getInstance(context)
            .setUserProperty(FirebaseUserProperties.REG_STATUS, FirebaseUserProperties.reg);

        emitter.onSuccess(user);
      } else {
        Log.d("kkk", "else");
        emitter.tryOnError(new UserNotAuthorized());

      }
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
          .apply();
      openMainScreen();
    }
  }
}
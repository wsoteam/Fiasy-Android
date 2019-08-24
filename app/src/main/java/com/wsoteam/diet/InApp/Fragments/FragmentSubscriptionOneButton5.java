package com.wsoteam.diet.InApp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.wsoteam.diet.Authenticate.POJO.Box;
import com.wsoteam.diet.BuildConfig;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.EntryPoint.ActivitySplash;
import com.wsoteam.diet.EventsAdjust;
import com.wsoteam.diet.InApp.IDs;
import com.wsoteam.diet.InApp.properties.CheckAndSetPurchase;
import com.wsoteam.diet.InApp.properties.SingletonMakePurchase;
import com.wsoteam.diet.OtherActivity.ActivityPrivacyPolicy;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.Analytics.EventProperties;
import com.wsoteam.diet.common.Analytics.Events;
import com.wsoteam.diet.utils.IntentUtils;
import java.util.ArrayList;
import java.util.List;

public class FragmentSubscriptionOneButton5 extends Fragment
    implements PurchasesUpdatedListener {


  @BindView(R.id.price) TextView priceTextView;
  @BindView(R.id.title) TextView titleTextView;

  private BillingClient billingClient;
  private static final String TAG = "inappbilling";
  private String currentSKU = IDs.ID_ONE_WEEK, currentPrice = "99р";
  private SharedPreferences sharedPreferences;
  Unbinder unbinder;
  private static final String TAG_BOX = "TAG_BOX";
  private Box box;
  private final String BUY_NOW = "BUY_NOW";

  public static FragmentSubscriptionOneButton5 newInstance(Box box) {
    Bundle bundle = new Bundle();
    bundle.putSerializable(TAG_BOX, box);
    FragmentSubscriptionOneButton5 fragment =
        new FragmentSubscriptionOneButton5();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_subscription_5, container, false);
    unbinder = ButterKnife.bind(this, view);
    box = (Box) getArguments().getSerializable(TAG_BOX);

    Window w = getActivity().getWindow(); // in Activity's onCreate() for instance
    //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    w.getDecorView().setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    w.setNavigationBarColor(Color.parseColor("#30000000"));
    w.setStatusBarColor(Color.parseColor("#382c2c2d"));

    titleTextView.setText(setColor(R.string.prem_5_title, "#ef7d02", 25, 32));
    priceTextView.setText(setColor(R.string.prem_2_price, "#ef7d02", 11, 26 ));

    //Glide.with(requireContext())
    //        .load(R.drawable.bacgroud_premium_2)
    //        .apply(new RequestOptions()
    //                .skipMemoryCache(true)
    //                .centerInside())
    //        .into(backgroundImage);

    billingClient = BillingClient.newBuilder(requireContext())
        .setListener(this)
        .build();
    billingClient.startConnection(new BillingClientStateListener() {
      @Override
      public void onBillingSetupFinished(int responseCode) {
        if (responseCode == BillingClient.BillingResponse.OK) {
          getSKU();
        }
      }

      @Override
      public void onBillingServiceDisconnected() {

      }
    });
    return view;
  }

  private Spannable setColor(int stringID, String color, int start, int end){
    Spannable stringSpan = new SpannableString(getString(stringID));
    stringSpan.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    return stringSpan;
  }


  private void getSKU() {
    List<String> skuList = new ArrayList<>();
    skuList.add(Config.ONE_YEAR_PRICE_TRIAL);

    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
    params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);

    billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
      @Override
      public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {

        //if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
        //  Log.d(TAG, "onSkuDetailsResponse: OK");
        //
        //
        //} else {
        //  Log.d(TAG, "onSkuDetailsResponse: FAIL");
        //}

      }
    });
  }

  private void buy(String sku) {
    BillingFlowParams mParams = BillingFlowParams.newBuilder()
        .setSku(sku)
        .setType(BillingClient.SkuType.SUBS)
        .build();

    billingClient.launchBillingFlow(getActivity(), mParams);
  }

  @Override
  public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
    if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
      //send data about purchase into firebase (and save into profile subInfo)
      SingletonMakePurchase.getInstance().setMakePurchaseNow(true);
      final Purchase p = purchases.get(0);

      if (BuildConfig.DEBUG) {
        Log.d("Fiasy", "Purchased, " + p.toString());
      }

      new CheckAndSetPurchase(getActivity()).execute(p.getSku(), p.getPurchaseToken(), p.getPackageName(), BUY_NOW);

      Adjust.trackEvent(new AdjustEvent(EventsAdjust.buy_trial));
      Events.logBuy(box.getBuyFrom());

      logTrial();

      requireContext().getSharedPreferences(Config.STATE_BILLING, Context.MODE_PRIVATE).
          edit().
          putBoolean(Config.STATE_BILLING, true).
          apply();

      sharedPreferences = requireContext().getSharedPreferences(Config.ALERT_BUY_SUBSCRIPTION,
          Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putBoolean(Config.ALERT_BUY_SUBSCRIPTION, true);
      editor.apply();

      if (box.isOpenFromIntrodaction()) {
        box.setSubscribe(true);
      }

      IntentUtils.openMainActivity(requireContext());
    }
  }


  @OnClick({R.id.btnClose, R.id.btnBack, R.id.btnBuyPrem, R.id.tvPrivacyPolicy})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.btnBuyPrem:
        Events.logPushButton(EventProperties.push_button_next);
        buy(currentSKU);
        break;
      case R.id.btnBack:
        Events.logPushButton(EventProperties.push_button_back);
        getActivity().onBackPressed();
        break;
      case R.id.btnClose: {
        Events.logPushButton(EventProperties.push_button_close);
        if (box.isOpenFromIntrodaction()) {
          Events.logMoveQuestions(EventProperties.question_close);
        }
        if (box.isOpenFromIntrodaction()) {
          final Intent intent = new Intent(getContext(), ActivitySplash.class);
          startActivity(intent);
          getActivity().finish();
        } else {
          getActivity().onBackPressed();
        }
      }
      break;
      case R.id.tvPrivacyPolicy:
        Events.logPushButton(EventProperties.push_button_privacy);
        Intent intent = new Intent(getActivity(), ActivityPrivacyPolicy.class);
        startActivity(intent);
        break;

    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  private void logTrial() {
    AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(getActivity());
    Bundle params = new Bundle();
    params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "RUB");
    appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_START_TRIAL, 990, params);
  }
}
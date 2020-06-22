package com.wsoteam.diet.ads;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.wsoteam.diet.App;
import com.wsoteam.diet.R;
import com.wsoteam.diet.utils.Subscription;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class FiasyAds {

    public static final int NATIVE_STEP_IN_RECYCLER = 6;


    private static InterstitialAd mInterstitialAd;
//    private static UnifiedNativeAdView adView;
    private static UnifiedNativeAd nativeAd;

    private static boolean isRefreshLocked = false;

    public static MutableLiveData<Boolean> adStatus= new MutableLiveData<>(true);

    private static MutableLiveData<UnifiedNativeAd> nativeAdView = new MutableLiveData<>();


    public static LiveData<UnifiedNativeAd> getLiveDataAdView(){
        return nativeAdView;
    }

    public static void init(Context context){
        if (Subscription.check(context)) return;

        MobileAds.initialize(context.getApplicationContext(), initializationStatus -> {

            Log.d("TAGi", initializationStatus.toString());
            AdWorker.INSTANCE.loadNative(context, isLoaded ->{

                return null;
            } );
            refreshAd(context);
        });
        initInterstitial(context);
    }

    public static void refreshAd(Context context) {
        if (Subscription.check(context)) return;

        isRefreshLocked = true;

        AdLoader.Builder builder = new AdLoader.Builder(context.getApplicationContext(), /*ADMOB_AD_UNIT_ID*/ context.getString(R.string.admob_native));

        // OnUnifiedNativeAdLoadedListener implementation.
        builder.forUnifiedNativeAd(unifiedNativeAd -> {
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            if (nativeAd != null) {
                nativeAd.destroy();
            }
            nativeAd = unifiedNativeAd;
            Log.d("kkk", "++" + unifiedNativeAd);
            nativeAdView.setValue(unifiedNativeAd);
            isRefreshLocked = false;

        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(false)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                isRefreshLocked = false;
//                Toast.makeText(context, "Failed to load native ad: "
//                        + errorCode, Toast.LENGTH_SHORT).show();
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }


    private static void initInterstitial(Context context){
        mInterstitialAd = new InterstitialAd(context.getApplicationContext());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d("TAGi", "onAdLoaded() ");
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d("TAGi", "onAdFailedToLoad(errorCode " + errorCode + ")");
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                Log.d("TAGi", "onAdOpened()");
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                Log.d("TAGi", "onAdClicked()");
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                Log.d("TAGi", "onAdLeftApplication()");
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                Log.d("TAGi", " onAdClosed()");
                // Code to be executed when the interstitial ad is closed.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        mInterstitialAd.setAdUnitId(context.getString(R.string.admob_interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    public static void openInter(){
        Log.d("TAGi", "openInter() 1");
        if (Subscription.check(App.getContext())) {
            adStatus.setValue(false);
            return;
        }

        if (mInterstitialAd == null) return;

        Log.d("TAGi", "openInter() 2");
        if (mInterstitialAd.isLoaded()) {
            Log.d("TAGi", "openInter() 3");
            mInterstitialAd.show();
        } else {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            Log.d("TAGi", "The interstitial wasn't loaded yet.");
        }

    }

}

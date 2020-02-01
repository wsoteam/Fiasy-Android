package com.wsoteam.diet;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;
import androidx.room.Room;
import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.amplitude.api.Amplitude;
import com.bugsee.library.Bugsee;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.onesignal.OneSignal;
import com.wsoteam.diet.BranchOfAnalyzer.POJOFoodSQL.FoodDatabase;
import java.util.HashMap;

import static com.adjust.sdk.AdjustConfig.ENVIRONMENT_PRODUCTION;
import static com.wsoteam.diet.EventsAdjust.app_token;

public class App extends MultiDexApplication {

    public static App instance;
    private static FirebaseAnalytics analytics;

    private FoodDatabase foodDatabase;
    private boolean setupOnDemand = true;

    public long now;
    private static Context context;

    public static App getInstance() {
        return instance;
    }

    public static FirebaseAnalytics getFAInstance(){
        return analytics;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Bugsee.launch(this, "b9f4ece5-898c-48fe-9938-ef42d8593a95");
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        FirebaseRemoteConfig.getInstance().setDefaults(new HashMap<String, Object>() {{
            put("premium_theme", "dark");
            put("premium_with_trial", true);
        }});

        FirebaseRemoteConfig.getInstance().fetchAndActivate();

        Amplitude.getInstance().trackSessionEvents(true);
        Amplitude.getInstance().initialize(this, "b148a2e64cc862b4efb10865dfd4d579")
                .enableForegroundTracking(this);
        instance = this;
        analytics = FirebaseAnalytics.getInstance(this);
        foodDatabase = Room.databaseBuilder(this, FoodDatabase.class, "foodDB.db")
                .fallbackToDestructiveMigration()
                .build();
        //SetUserProperties.setUserProperties(Adjust.getAttribution());

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        if (BuildConfig.DEBUG) {
            OneSignal.idsAvailable((userId, registrationId) -> {
                Log.d("OneSignalId", "user_id=" + userId);
            });
        }

        context = this;
    }


    public void setupOnDemand() {
        if (setupOnDemand) {
            setupOnDemand = false;
            Adjust.onCreate(new AdjustConfig(this, app_token, ENVIRONMENT_PRODUCTION));
            registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
        }
    }

    public static Context getContext() {
        return context;
    }

    public FoodDatabase getFoodDatabase() {
        return foodDatabase;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private static final class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
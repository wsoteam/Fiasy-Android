package com.wsoteam.diet;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.amplitude.api.Amplitude;
import com.bugsee.library.Bugsee;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.onesignal.OneSignal;
import com.wsoteam.diet.BranchOfAnalyzer.POJOFoodSQL.FoodDatabase;

import java.util.HashMap;

import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import static com.adjust.sdk.AdjustConfig.ENVIRONMENT_PRODUCTION;
import static com.wsoteam.diet.EventsAdjust.app_token;

public class App extends MultiDexApplication {

    public static App instance;

    private FoodDatabase foodDatabase;
    private boolean setupOnDemand = true;

    public long now;
    private static Context context;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Bugsee.launch(this, "ec43078d-e5d9-4e97-9912-4f293e315bdf");

        FirebaseRemoteConfig.getInstance().setDefaults(new HashMap<String, Object>() {{
            put("premium_theme", "dark");
            put("premium_with_trial", true);
        }});

        FirebaseRemoteConfig.getInstance().fetchAndActivate();

        Amplitude.getInstance().trackSessionEvents(true);
        Amplitude.getInstance().initialize(this, "b148a2e64cc862b4efb10865dfd4d579")
                .enableForegroundTracking(this);
        instance = this;
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

            Bugsee.launch(this, "ec43078d-e5d9-4e97-9912-4f293e315bdf");

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
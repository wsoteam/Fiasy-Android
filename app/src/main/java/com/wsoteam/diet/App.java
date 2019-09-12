package com.wsoteam.diet;

import android.app.Activity;
import android.os.Bundle;

import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.amplitude.api.Amplitude;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.orm.SugarContext;
import com.wsoteam.diet.BranchOfAnalyzer.POJOFoodSQL.FoodDatabase;

import io.intercom.android.sdk.Intercom;

public class App extends MultiDexApplication {
    public static App instance;

    private FoodDatabase foodDatabase;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);

        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Bugsee.launch(this, "ec43078d-e5d9-4e97-9912-4f293e315bdf");

        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Adjust.onCreate(new AdjustConfig(this, EventsAdjust.app_token, AdjustConfig.ENVIRONMENT_PRODUCTION));
        registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
        Amplitude.getInstance().trackSessionEvents(true);
        Amplitude.getInstance().initialize(this, "b148a2e64cc862b4efb10865dfd4d579")
                .enableForegroundTracking(this);
        Intercom.initialize(this, "android_sdk-bceadc40bc17510359f5ad43a72281735676eea2", "dr8zfmz4");
        instance = this;
        foodDatabase = Room.databaseBuilder(this, FoodDatabase.class, "foodDB.db").build();
        //SetUserProperties.setUserProperties(Adjust.getAttribution());
    }

    public FoodDatabase getFoodDatabase() {
        return foodDatabase;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
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
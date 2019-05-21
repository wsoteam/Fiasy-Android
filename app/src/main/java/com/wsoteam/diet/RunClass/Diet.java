package com.wsoteam.diet.RunClass;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.amplitude.api.Amplitude;
import com.bugsee.library.Bugsee;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.orm.SugarContext;
import com.revenuecat.purchases.Purchases;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.EventsAdjust;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

public class Diet extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAuth.getInstance().getCurrentUser().getUid();
        Purchases.setDebugLogsEnabled(true);
        Purchases.configure(this, "SMoEoVfrXlUdjqmquUweDcrGuheoWjmv", "sdfsdf");
        SugarContext.init(this);
        YandexMetricaConfig.Builder configBuilder = YandexMetricaConfig.newConfigBuilder(Config.YANDEX_API_KEY);
        YandexMetrica.activate(getApplicationContext(), configBuilder.build());
        YandexMetrica.enableActivityAutoTracking(this);
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Bugsee.launch(this, "b9f4ece5-898c-48fe-9938-ef42d8593a95");
        Adjust.onCreate(new AdjustConfig(this, EventsAdjust.app_token, AdjustConfig.ENVIRONMENT_PRODUCTION));
        registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
        Amplitude.getInstance().trackSessionEvents(true);
        Amplitude.getInstance().initialize(this, "b148a2e64cc862b4efb10865dfd4d579")
                .enableForegroundTracking(this);
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



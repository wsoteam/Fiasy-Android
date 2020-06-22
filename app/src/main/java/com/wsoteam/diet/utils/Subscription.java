package com.wsoteam.diet.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.wsoteam.diet.Config;

import androidx.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;

public class Subscription {

    public static boolean check(@Nullable Context context) {
        if (context == null) {
            Log.e("Subscription", "context == null");
            return false;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.STATE_BILLING, MODE_PRIVATE);
        return sharedPreferences.getBoolean(Config.STATE_BILLING, false);
//        return true;
    }

    public static void setVisibility(View view){
        if (Subscription.check(view.getContext())){
            view.setVisibility(View.GONE);
        }else {
            view.setVisibility(View.VISIBLE);
        }
    }
}

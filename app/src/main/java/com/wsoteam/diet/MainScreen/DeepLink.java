package com.wsoteam.diet.MainScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.NonNull;

public class DeepLink {

    public class Start{

        public static final String PREMIUM = "premium";
        public static final String RECIPE = "recipe";
        public static final String ARTICLE = "article";
        public static final String NUTRITIONIST = "nutritionist";
        public static final String DIETS = "diets";
        public static final String MEASUREMENT = "measurements";
        public static final String ADD_FOOD = "addfood";

    }


    private static final String APP_PREFERENCES = "DEEP_LINK";
    private static final String ACTION = "ACTION";


    public static void addAction(Context context, String action){
        SharedPreferences preferences =
                context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ACTION, action);
        editor.apply();

    }

    public static void deleteAction(Context context){
        SharedPreferences preferences =
                context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(ACTION);
        editor.apply();

    }

    public static String getAction(Context context){
        SharedPreferences preferences =
                context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if(preferences.contains(ACTION)) {
            return preferences.getString(ACTION, "");
        } else return null;
    }

    public static boolean prepareUri(Context context,@NonNull Uri dat){

        String host = dat.getHost();
        if (host != null)
        switch (host){
            case Start.PREMIUM:
            case Start.ARTICLE:
            case Start.NUTRITIONIST:
            case Start.DIETS:
            case Start.MEASUREMENT:
            case Start.ADD_FOOD:
            case Start.RECIPE:{
                addAction(context, host);
                return true;
            }
        }
        return false;
    }
}

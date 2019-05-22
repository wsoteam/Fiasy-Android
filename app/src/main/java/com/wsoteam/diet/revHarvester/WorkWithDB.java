package com.wsoteam.diet.revHarvester;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.POJOProfile.CheckInfo.CheckHistory;
import com.wsoteam.diet.POJOProfile.SubInfo;

public class WorkWithDB {
    private static final String TAG = "HARVESTER";
    public static void setSubInfo(SubInfo subInfo, String id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(id).child("subInfo");
        myRef.setValue(subInfo);
        Log.e(TAG, id + " -- set info");
    }

    public static void setNewCheckHistory(CheckHistory checkHistory, String id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                child(id).child("checkHistory");
        myRef.setValue(checkHistory);
        Log.e(TAG, id + " -- set new check");
    }
}

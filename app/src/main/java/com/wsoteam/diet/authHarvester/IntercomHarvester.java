package com.wsoteam.diet.authHarvester;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.POJOProfile.SubInfo;
import com.wsoteam.diet.Sync.POJO.UserData;
import com.wsoteam.diet.authHarvester.POJO.AllUsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

public class IntercomHarvester {
    public static AllUsers users;

    public static void start(Context context) {
        Log.e("LOL", "start read");
        users = getAllUsers(context);
        markUsers();
        for (int i = 0; i < users.getUsers().size(); i++) {
            if (users.getUsers().get(i).isTrial()) {
                Log.e("LOL", "KEK");
            }
        }

    }


    public static void write(Context context) {
        int count = 0;

        for (int i = 0; i < users.getUsers().size(); i++) {
            if (users.getUsers().get(i).isTrial()){
                count ++;
            }
        }
        Log.e("LOL", String.valueOf(count) + "  count");
    }


    private static void markUsers() {
        for (int i = 0; i < users.getUsers().size(); i++) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(Config.NAME_OF_USER_DATA_LIST_ENTITY).
                    child(users.getUsers().get(i).getLocalId()).child("subInfo");

            int finalI = i;
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        SubInfo subInfo = dataSnapshot.getValue(SubInfo.class);
                        if (subInfo.getProductId() != null){
                            users.getUsers().get(finalI).setTrial(true);
                            Log.e("LOL", "KEK");
                        }
                    }catch (Exception ex){
                        Log.e("LOL", ex.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }


    private static String readJsonAllUsers(Context context) {
        Log.e("LOL", "start read");
        String json = "";
        try {
            InputStream inputStream = context.getAssets().open("save_file.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("LOL", "fin read");
        return json;
    }

    private static AllUsers getAllUsers(Context context) {
        String json = readJsonAllUsers(context);
        Log.e("LOL", "start ser");
        AllUsers users = new AllUsers();
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<AllUsers> globalJsonAdapter = moshi.adapter(AllUsers.class);
        try {
            users = globalJsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("LOL", "not read json");
        }
        Log.e("LOL", "fin ser");
        return users;
    }

}

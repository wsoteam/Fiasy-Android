package com.wsoteam.diet.authHarvester;

import android.content.Context;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.wsoteam.diet.authHarvester.POJO.AllUsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class IntercomHarvester {
    public static AllUsers users;

    public static void start(Context context){
        Log.e("LOL", "start read");
        users = getAllUsers(context);
        Log.e("LOL", String.valueOf(users.getUsers().size()));

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

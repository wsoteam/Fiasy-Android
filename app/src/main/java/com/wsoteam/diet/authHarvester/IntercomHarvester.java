package com.wsoteam.diet.authHarvester;

import android.content.Context;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.authHarvester.POJO.AllUsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.UserAttributes;
import io.intercom.android.sdk.identity.Registration;

public class IntercomHarvester {

    public static void startAdding(Context context) {
        AllUsers users = getAllUsers(context);
        //Log.e("LOL", users.getUsers().get(0).getProviderUserInfo().toString());
        //addUsersInIntercom(users);
        //checkUserForUID(users);

    }

    private static void checkUserForUID(AllUsers users) {
        int count= 0;
        for (int i = 0; i < users.getUsers().size(); i++) {
            if (users.getUsers().get(i).getLocalId() == null){
                count +=1;
            }
        }
        Log.e("LOL", String.valueOf(count));
    }

    private static void addUsersInIntercom(AllUsers users) {
        int count = 0;
        for (int i = 0; i < 50; i++) {
            if (users.getUsers().get(i).getEmail() != null) {
                //count += 1;
                addUser(users.getUsers().get(i).getEmail(), users.getUsers().get(i).getLocalId());
            } else if (!users.getUsers().get(i).getProviderUserInfo().equals("[]")
                    && users.getUsers().get(i).getProviderUserInfo().size() > 0 && users.getUsers().get(i).getProviderUserInfo().get(0).getEmail() != null) {
                addUser(users.getUsers().get(i).getProviderUserInfo().get(0).getEmail(), users.getUsers().get(i).getLocalId());
                //count += 1;
            }
            Log.e("LOL", String.valueOf(i) + " - added in intercom");
        }
        Log.e("LOL", String.valueOf(count));
    }

    private static void addUser(String email, String uid) {
        Intercom.client().logout();
        Registration registration = Registration.create().withUserId(uid);
        Intercom.client().registerIdentifiedUser(registration);
        UserAttributes userAttributes = new UserAttributes.Builder()
                .withEmail(email)
                .build();
        Intercom.client().updateUser(userAttributes);
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
}

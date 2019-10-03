package com.wsoteam.diet.authHarvester;

import android.content.Context;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.authHarvester.POJO.AllUsers;

import com.wsoteam.diet.authHarvester.POJO.User;
import com.wsoteam.diet.authHarvester.POJO.intercom.InterUser;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.UserAttributes;
import io.intercom.android.sdk.identity.Registration;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IntercomHarvester {

    public static void startAdding(Context context) {
        AllUsers allUsers = getAllUsers(context);
        List<InterUser> interUsers = getAllUsersItercom(context);
        getAndSetEmails(interUsers, allUsers);
        //AllUsers users = getAllUsers(context);
        //Log.e("LOL", users.getUsers().get(0).getProviderUserInfo().toString());
        //addUsersInIntercom(users);
        //checkUserForUID(users);
    }

    private static void getAndSetEmails(List<InterUser> interUsers, AllUsers allUsers) {
        int counter = 0;
        List<User> usersFB = allUsers.getUsers();
        HashMap<String, User> userHashMap = new HashMap<>();
        for (int i = 0; i < usersFB.size(); i++) {
            userHashMap.put(usersFB.get(i).getLocalId(), usersFB.get(i));
        }

        Log.e("LOL", "hashmap created!!!");

        for (int i = 0; i < interUsers.size(); i++) {
            if (interUsers.get(i).getEmail().equals("")){
                User user = userHashMap.get(interUsers.get(i).getUserID());
                try {
                    if (user.getEmail() != null) {
                        //updateUser(interUsers.get(i).getUserID(), user.getEmail());
                        counter++;
                    }
                }catch (Exception ex){
                    try {
                        if (!user.getProviderUserInfo().equals("[]") && user.getProviderUserInfo().size() > 0 ){
                            counter++;
                        }
                    }catch (Exception exc){

                    }
                }
            }
        }

        Log.e("LOL", "COUNT -- " + String.valueOf(counter));
    }

    private static void updateUser(String userID, String email) {
    }

    private static void emptyEmails(List<InterUser> interUsers) {
        int count = 0;
        for (int i = 0; i < interUsers.size(); i++) {
            if (interUsers.get(i).getEmail().equals("")){
                count++;
            }
        }
        Log.e("LOL", "withoutEmails -- " + String.valueOf(count));
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

    private static List<InterUser> getAllUsersItercom(Context context) {
        String json = readJsonAllUsersIntercom(context);
        Log.e("LOL", "start ser");
        List<InterUser> interUsers = new ArrayList<>();
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, InterUser.class);
        JsonAdapter<List<InterUser>> globalJsonAdapter = moshi.adapter(type);
        try {
            interUsers = globalJsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("LOL", "not read json");
        }
        Log.e("LOL", "fin ser");
        Log.e("LOL", String.valueOf(interUsers.size()));
        return interUsers;
    }

    private static String readJsonAllUsersIntercom(Context context) {
        Log.e("LOL", "start read");
        String json = "";
        try {
            InputStream inputStream = context.getAssets().open("csvjson.json");
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

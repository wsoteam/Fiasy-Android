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
import com.squareup.moshi.Types;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.POJOProfile.SubInfo;
import com.wsoteam.diet.Sync.POJO.UserData;
import com.wsoteam.diet.authHarvester.POJO.AllUsers;
import com.wsoteam.diet.authHarvester.POJO.User;
import com.wsoteam.diet.authHarvester.POJO.intercom.InterUser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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
        createInterUsers(users, context);
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
                        if (subInfo.getProductId() != null && !subInfo.getProductId().equals("empty") && !subInfo.getOrderId().equals("empty")) {
                            users.getUsers().get(finalI).setTrial(true);
                            Log.e("LOL", "KEK");
                        }
                    } catch (Exception ex) {
                        Log.e("LOL", ex.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    private static void saveString(String jsonString, Context context) {
        try {
            OutputStreamWriter
                    outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(jsonString);
            outputStreamWriter.close();
            Log.e("LOL", "saved");
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private static void createInterUsers(AllUsers allUsers, Context context) {
        int counterWithoutEmail = 0;
        int counterWithoutName = 0;
        List<User> usersFB = allUsers.getUsers();
        List<InterUser> userList = new ArrayList<>();
        for (int i = 0; i < usersFB.size(); i++) {
            if (usersFB.get(i).isTrial()) {
                InterUser interUser = new InterUser();
                if (usersFB.get(i).getEmail() != null && !usersFB.get(i).getEmail().equalsIgnoreCase("")) {
                    interUser.setUserID("");
                    interUser.setEmail(usersFB.get(i).getEmail());
                } else {
                    if (!usersFB.get(i).getProviderUserInfo().equals("[]") && usersFB.get(i).getProviderUserInfo().size() > 0 && usersFB.get(i).getProviderUserInfo().get(0).getEmail() != null) {
                        interUser.setEmail(usersFB.get(i).getProviderUserInfo().get(0).getEmail());
                    } else {
                        counterWithoutEmail++;
                    }
                }

                if (usersFB.get(i).getDisplayName() != null && !usersFB.get(i).getDisplayName().equalsIgnoreCase("")) {
                    interUser.setName(usersFB.get(i).getDisplayName());
                } else {
                    if (!usersFB.get(i).getProviderUserInfo().equals("[]") && usersFB.get(i).getProviderUserInfo().size() > 0 && usersFB.get(i).getProviderUserInfo().get(0).getDisplayName() != null) {
                        interUser.setName(usersFB.get(i).getProviderUserInfo().get(0).getDisplayName());
                    } else {
                        counterWithoutName++;
                    }
                }

                userList.add(interUser);
            }
        }
        Log.e("LOL", "write start...");
        writeList(userList, context);
        Log.e("LOL", "without email " + String.valueOf(counterWithoutEmail));
        Log.e("LOL", "without name " + String.valueOf(counterWithoutName));
    }

    private static void writeList(List<InterUser> userList, Context context) {
        Log.e("LOL", "ser");
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, InterUser.class);
        JsonAdapter<List<InterUser>> adapter = moshi.adapter(type);
        String jsonString = adapter.toJson(userList);
        saveString(jsonString, context);
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

package com.wsoteam.diet.authHarvester;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.wsoteam.diet.authHarvester.POJO.AllUsers;
import com.wsoteam.diet.authHarvester.POJO.User;
import com.wsoteam.diet.authHarvester.POJO.intercom.InterUser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class kek {

    public static void startAdding(Context context) {
        AllUsers allUsers = getAllUsers(context);
        //List<InterUser> interUsers = getAllUsersItercom(context);
        //getAndSetEmailsFix(interUsers, allUsers, context);
        Log.e("LOL", "start");
        createInterUsers(allUsers, context);
    }

    private static void checkEmptyModels(AllUsers allUsers) {
        int counter = 0;
        Log.e("LOL", "SIZE -- " + String.valueOf(allUsers.getUsers().size()));
        for (int i = 0; i < allUsers.getUsers().size(); i++) {
            if (allUsers.getUsers().get(i).getEmail() != null) {
                counter++;
            } else if (!allUsers.getUsers().get(i).getProviderUserInfo().equals("[]")
                    && allUsers.getUsers().get(i).getProviderUserInfo().size() > 0
                    && allUsers.getUsers().get(i).getProviderUserInfo().get(0).getEmail() != null) {
                counter++;
            }
        }
        Log.e("LOL", "NOT EMPTY -- " + String.valueOf(counter));
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
            if (interUsers.get(i).getEmail().equals("")) {
                User user = userHashMap.get(interUsers.get(i).getUserID());
                try {
                    if (user.getEmail() != null) {
                        //updateUser(interUsers.get(i).getUserID(), user.getEmail());
                        counter++;
                    }
                } catch (Exception ex) {
                    try {
                        if (!user.getProviderUserInfo().equals("[]") && user.getProviderUserInfo().size() > 0) {
                            counter++;
                        }
                    } catch (Exception exc) {

                    }
                }
            }
        }

        Log.e("LOL", "COUNT -- " + String.valueOf(counter));
    }

    private static void createInterUsers(AllUsers allUsers, Context context) {
        int counterWithoutEmail = 0;
        int counterWithoutName = 0;
        List<User> usersFB = allUsers.getUsers();
        List<InterUser> userList = new ArrayList<>();
        for (int i = 0; i < usersFB.size(); i++) {
            InterUser interUser = new InterUser();
            if (usersFB.get(i).getEmail() != null && !usersFB.get(i).getEmail().equalsIgnoreCase("")){
                interUser.setUserID("");
                interUser.setEmail(usersFB.get(i).getEmail());
            }else {
                if (!usersFB.get(i).getProviderUserInfo().equals("[]") && usersFB.get(i).getProviderUserInfo().size() > 0 && usersFB.get(i).getProviderUserInfo().get(0).getEmail() != null){
                    interUser.setEmail(usersFB.get(i).getProviderUserInfo().get(0).getEmail());
                }else {
                    counterWithoutEmail ++;
                }
            }

            if (usersFB.get(i).getDisplayName() != null && !usersFB.get(i).getDisplayName().equalsIgnoreCase("")){
                interUser.setName(usersFB.get(i).getDisplayName());
            }else {
                if (!usersFB.get(i).getProviderUserInfo().equals("[]") && usersFB.get(i).getProviderUserInfo().size() > 0 && usersFB.get(i).getProviderUserInfo().get(0).getDisplayName() != null){
                    interUser.setName(usersFB.get(i).getProviderUserInfo().get(0).getDisplayName());
                }else {
                    counterWithoutName ++;
                }
            }

            userList.add(interUser);
        }
        Log.e("LOL", "write start...");
        writeList(userList, context);
        Log.e("LOL", "without email " + String.valueOf(counterWithoutEmail));
        Log.e("LOL", "without name " + String.valueOf(counterWithoutName));
    }

    private static void getAndSetEmailsFix(List<InterUser> interUsers, AllUsers allUsers,
                                           Context context) {
        int counter = 0;
        int counter1 = 0;
        int counter2 = 0;
        List<User> usersFB = allUsers.getUsers();
        List<InterUser> userList = new ArrayList<>();
        for (int i = 0; i < interUsers.size(); i++) {
            if (interUsers.get(i).getEmail().equals("")) {
                counter++;
                User user = find(interUsers.get(i), usersFB);
                if (user != null) {
                    if (user.getEmail() != null) {
                        userList.add(createUser(user.getLocalId(), user.getEmail()));
                    } else if (!user.getProviderUserInfo().equals("[]")
                            && user.getProviderUserInfo().size() > 0
                            && user.getProviderUserInfo().get(0).getEmail() != null) {
                        userList.add(createUser(user.getLocalId(), user.getProviderUserInfo().get(0).getEmail()));
                    }
                }

            }
        }
        writeList(userList, context);
        Log.e("LOL", "with email " + String.valueOf(userList.size()));
    }

    private static void writeList(List<InterUser> userList, Context context) {
        Log.e("LOL", "ser");
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, InterUser.class);
        JsonAdapter<List<InterUser>> adapter = moshi.adapter(type);
        String jsonString = adapter.toJson(userList);
        saveString(jsonString, context);
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

    public class AsyncWrite extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            return null;
        }

    }

    private static InterUser createUser(String localId, String email) {
        InterUser interUser = new InterUser();
        interUser.setEmail(email);
        interUser.setUserID(localId);
        return interUser;
    }

    private static void setEmail(String id, String email) {

    }

    private static User find(InterUser interUser, List<User> usersFB) {
        User user;
        int counter = 0;
        for (int i = 0; i < usersFB.size(); i++) {
            if (usersFB.get(i).getLocalId().equals(interUser.getUserID())) {
                user = usersFB.get(i);
                return user;
            }
        }

        return null;
    }

    private static void updateUser(String userID, String email) {
    }

    private static void emptyEmails(List<InterUser> interUsers) {
        int count = 0;
        for (int i = 0; i < interUsers.size(); i++) {
            if (interUsers.get(i).getEmail().equals("")) {
                count++;
            }
        }
        Log.e("LOL", "withoutEmails -- " + String.valueOf(count));
    }

    private static void checkUserForUID(AllUsers users) {
        int count = 0;
        for (int i = 0; i < users.getUsers().size(); i++) {
            if (users.getUsers().get(i).getLocalId() == null) {
                count += 1;
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
                    && users.getUsers().get(i).getProviderUserInfo().size() > 0
                    && users.getUsers().get(i).getProviderUserInfo().get(0).getEmail() != null) {
                addUser(users.getUsers().get(i).getProviderUserInfo().get(0).getEmail(),
                        users.getUsers().get(i).getLocalId());
                //count += 1;
            }
            Log.e("LOL", String.valueOf(i) + " - added in intercom");
        }
        Log.e("LOL", String.valueOf(count));
    }

    private static void addUser(String email, String uid) {
        /*Intercom.client().logout();
        Registration registration = Registration.create().withUserId(uid);
        Intercom.client().registerIdentifiedUser(registration);
        UserAttributes userAttributes = new UserAttributes.Builder()
                .withEmail(email)
                .build();
        Intercom.client().updateUser(userAttributes);*/
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

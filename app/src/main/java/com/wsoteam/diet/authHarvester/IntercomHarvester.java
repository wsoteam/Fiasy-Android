package com.wsoteam.diet.authHarvester;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.wsoteam.diet.App;
import com.wsoteam.diet.BranchOfAnalyzer.POJOFoodSQL.FoodDAO;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.authHarvester.POJO.AllUsers;

import com.wsoteam.diet.authHarvester.POJO.User;
import com.wsoteam.diet.authHarvester.POJO.intercom.InterUser;
import com.wsoteam.diet.common.backward.OldFavoriteConverter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.UserAttributes;
import io.intercom.android.sdk.identity.Registration;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IntercomHarvester {

  public static void startAdding(Context context) {
    AllUsers allUsers = getAllUsers(context);
    List<InterUser> interUsers = getAllUsersItercom(context);
    getAndSetEmailsFix(interUsers, allUsers);
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

  private static void getAndSetEmailsFix(List<InterUser> interUsers, AllUsers allUsers) {
    int counter = 0;
    int counter1 = 0;
    int counter2 = 0;
    List<User> usersFB = allUsers.getUsers();
    List<InterUser> userList = new ArrayList<>();
    for (int i = 0; i < interUsers.size(); i++) {
      if (interUsers.get(i).getEmail().equals("")) {
        counter++;
        if (counter < 15) {
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
    }
    writeList(userList);
    Log.e("LOL", "with email " + String.valueOf(counter));
  }

  private static void writeList(List<InterUser> userList){
    Moshi moshi = new Moshi.Builder().build();
    Type type = Types.newParameterizedType(List.class, InterUser.class);
    JsonAdapter<List<InterUser>> adapter = moshi.adapter(type);
    String jsonString = adapter.toJson(userList);
    saveString(jsonString);
  }

  private static void saveString(String jsonString) {
    Log.e("LOL", jsonString);
  }

  /*public class AsyncWrite extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... strings) {
      rewriteDB(strings[0]);
      return null;
    }

    private void rewriteDB(String string) {
      try {
        String outFileName = context.getFilesDir().getParent() + "/databases/" + "okk";
        OutputStream outputStream = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
          outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        myInput.close();
        OldFavoriteConverter.run();
        Log.d(TAG, "DB rewrited");
      } catch (IOException e) {
        Log.d(TAG, e.toString());
        e.printStackTrace();
      }
    }

    private boolean isEmptyDB(FoodDAO foodDAO) {
      boolean isEmpty = true;
      if (foodDAO.getById(1) != null) {
        isEmpty = false;
      }
      return isEmpty;
    }
  }*/

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

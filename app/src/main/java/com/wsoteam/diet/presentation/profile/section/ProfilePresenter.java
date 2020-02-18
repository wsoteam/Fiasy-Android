package com.wsoteam.diet.presentation.profile.section;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.amplitude.api.Amplitude;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wsoteam.diet.AmplitudaEvents;
import com.wsoteam.diet.DietPlans.POJO.DietPlan;
import com.wsoteam.diet.POJOProfile.Profile;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.model.Eating;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static java.util.Map.Entry.comparingByKey;

@InjectViewState
public class ProfilePresenter extends MvpPresenter<ProfileView> {
  private Calendar calendar;
  private Profile profile;
  private Context context;
  private long oneWeek = 604799999;
  private long oneDay = 86400000;
  private long currentTime;
  private SortedMap<Long, Integer> calories;

  public ProfilePresenter(Context context) {
    this.context = context;
  }

  public void attachPresenter() {
    calendar = Calendar.getInstance();
    calendar.setFirstDayOfWeek(Calendar.MONDAY);
    currentTime = calendar.getTimeInMillis();
    bindFields();
    Single.fromCallable(() -> {
      SortedMap<Long, Integer> calories = dateSort();
      return calories;
    })
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(t -> updateUI(t), Throwable::printStackTrace);
  }

  private void updateUI(SortedMap<Long, Integer> calories) {
    this.calories = calories;
    bindCircleProgressBar(calories);
    prepareWeekGraphs(calories, getWeekInterval(0));
  }

  public void getWeekGraph(int position) {
    Log.e("LOL", "position" + String.valueOf(position));
    prepareWeekGraphs(calories, getWeekInterval(position));
  }

  private long[] getWeekInterval(int position) {
    Log.e("KEK", String.valueOf(position));
    calendar.setTimeInMillis(currentTime);
    Log.e("KEK", "getTimeInMillis" + String.valueOf(calendar.getTimeInMillis()));
    long[] weekInterval = new long[7];
    int week = calendar.get(Calendar.WEEK_OF_YEAR);
    Log.e("KEK","week" +  String.valueOf(week));
    calendar.set(Calendar.WEEK_OF_YEAR, week + position);
    Log.e("KEK", "WEEK_OF_YEAR" + String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
    calendar.set(Calendar.DAY_OF_WEEK,  calendar.getFirstDayOfWeek());
    Log.e("KEK", "DAY_OF_WEEK" + String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
    for (int i = 0; i < 7; i++) {
      weekInterval[i] = calendar.getTimeInMillis() + oneDay * i;
    }
    String line = "";
    for (int i = 0; i < weekInterval.length; i++) {
      line += "Time -- " + String.valueOf(weekInterval[i]);
    }
    Log.e("LOL", line);
    return weekInterval;
  }

  private void prepareWeekGraphs(SortedMap<Long, Integer> calories, long[] interval) {
    //Calendar insideCalendar = Calendar.getInstance();
    int[] colors = new int[interval.length];
    List<BarEntry> pairs = new ArrayList<>();
    int kcal = 0;
    for (int i = 0; i < interval.length; i++) {
      if (calories.get(interval[i]) != null) {
        kcal = calories.get(interval[i]);
      } else {
        kcal = 0;
      }
      colors[i] = getColor(interval[i]);
      calendar.setTimeInMillis(interval[i]);
      pairs.add(new BarEntry(i, kcal));
    }

    getViewState().drawWeekGraphs(pairs, colors, getBottomText(interval), getTopText(interval));
  }

  private String getBottomText(long[] interval) {
    String bottomText = "";
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(interval[0]);
    String firstDay = getNumber(calendar.get(Calendar.DAY_OF_MONTH));
    String firstMonth = getNumber(calendar.get(Calendar.MONTH) + 1);
    bottomText = firstDay + "." + firstMonth + " - ";

    calendar.setTimeInMillis(interval[interval.length - 1]);
    String secondDay = getNumber(calendar.get(Calendar.DAY_OF_MONTH));
    String secondMonth = getNumber(calendar.get(Calendar.MONTH) + 1);
    bottomText += secondDay + "." + secondMonth;
    return bottomText;
  }

  private String getNumber(int i) {
    String number;
    if (String.valueOf(i).length() == 1) {
      number = "0" + String.valueOf(i);
    } else {
      number = String.valueOf(i);
    }
    return number;
  }

  private String getTopText(long[] interval) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(interval[0]);
    return String.valueOf(calendar.get(Calendar.YEAR));
  }

  private int getColor(Long time) {
    calendar.setTimeInMillis(currentTime);
    if (time == calendar.getTimeInMillis()) {
      return context.getResources().getColor(R.color.color_bar);
    } else if (time < calendar.getTimeInMillis()) {
      return context.getResources().getColor(R.color.color_bar_past);
    } else {
      return context.getResources().getColor(R.color.color_bar_future);
    }
  }

  private void bindCircleProgressBar(SortedMap<Long, Integer> calories) {
    Integer currentCalories = calories.get(getCurrentDate());
    if (currentCalories == null) {
      getViewState().bindCircleProgressBar(0);
    } else if (profile != null && currentCalories != null) {
      double percentLoading = ((double) currentCalories / profile.getMaxKcal());
      getViewState().bindCircleProgressBar((float) percentLoading * 100);
    }
  }

  private long getCurrentDate() {
    Calendar calendarForWork = calendar;
    calendarForWork.set(Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    return calendarForWork.getTimeInMillis();
  }

  public SortedMap<Long, Integer> dateSort() {
    HashMap<Long, Integer> calories = new HashMap<>();
    Iterator caloriesIterator;

    HashMap<String, Eating> eatings = getAllEatingLists();
    Iterator eatingIterator = eatings.entrySet().iterator();

    while (eatingIterator.hasNext()) {
      Map.Entry pair = (Map.Entry) eatingIterator.next();
      Eating eating = (Eating) pair.getValue();
      Long timeInMillis = formDate(eating.getDay(), eating.getMonth(), eating.getYear());
      boolean isNeedCreateNewDate = true;

      if (calories.size() == 0) {
        calories.put(timeInMillis,(int) eating.getCalories());
        isNeedCreateNewDate = false;
      } else {
        caloriesIterator = calories.entrySet().iterator();
        while (caloriesIterator.hasNext()) {
          Map.Entry entry = (Map.Entry) caloriesIterator.next();
          Long key = (Long) entry.getKey();
          Integer sumCalories = (Integer) entry.getValue();
          if (key.equals(timeInMillis)) {
            entry.setValue(sumCalories + eating.getCalories());
            isNeedCreateNewDate = false;
          }
        }
      }

      if (isNeedCreateNewDate) {
        calories.put(timeInMillis,(int) eating.getCalories());
      }
    }
    return increaseSort(calories);
  }

  void bindFields() {
    if (UserDataHolder.getUserData() != null && UserDataHolder.getUserData().getProfile() != null) {
      profile = UserDataHolder.getUserData().getProfile();
      DietPlan plan = UserDataHolder.getUserData().getPlan();
      getViewState().fillViewsIfProfileNotNull(profile, plan);
    }
  }

  private SortedMap<Long, Integer> increaseSort(HashMap<Long, Integer> calories) {
    SortedMap<Long, Integer> sortedMap = new TreeMap<>();
    Iterator iterator = calories.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry entry = (Map.Entry) iterator.next();
      Long date = (Long) entry.getKey();
      Integer kcal = (Integer) entry.getValue();
      sortedMap.put(date, kcal);
    }
    return sortedMap;
  }

  private void logSorted(SortedMap<Long, Integer> calories) {
    Iterator iterator = calories.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry entry = (Map.Entry) iterator.next();
      Log.e("LOL", "Date -- " + String.valueOf(entry.getKey()) + ", Value -- " + String.valueOf(
          entry.getValue()));
    }
  }

  private void logCalories(HashMap<String, Integer> calories) {
    Iterator iterator = calories.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry entry = (Map.Entry) iterator.next();
      Log.e("LOL", "Date -- " + String.valueOf(entry.getKey()) + ", Value -- " + String.valueOf(
          entry.getValue()));
    }
  }

  private HashMap<String, Eating> getAllEatingLists() {
    HashMap<String, Eating> eatings = new HashMap<>();
    if (UserDataHolder.getUserData() != null
        && UserDataHolder.getUserData().getBreakfasts() != null) {
      eatings.putAll(UserDataHolder.getUserData().getBreakfasts());
    }
    if (UserDataHolder.getUserData() != null && UserDataHolder.getUserData().getLunches() != null) {
      eatings.putAll(UserDataHolder.getUserData().getLunches());
    }
    if (UserDataHolder.getUserData() != null && UserDataHolder.getUserData().getDinners() != null) {
      eatings.putAll(UserDataHolder.getUserData().getDinners());
    }
    if (UserDataHolder.getUserData() != null && UserDataHolder.getUserData().getSnacks() != null) {
      eatings.putAll(UserDataHolder.getUserData().getSnacks());
    }
    return eatings;
  }

  private Long formDate(int day, int month, int year) {
    Calendar calendarForWork = calendar;
    calendarForWork.set(year, month, day);
    return calendarForWork.getTimeInMillis();
  }

  public void uploadPhoto(Bitmap bitmap) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
    String avatarName = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef =
        storage.getReference().child(Config.AVATAR_PATH + avatarName + Config.AVATAR_EXTENSION);
    storageRef.putBytes(bos.toByteArray())
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
          @Override
          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
              @Override
              public void onSuccess(Uri uri) {
                WorkWithFirebaseDB.setPhotoURL(uri.toString());
              }
            });
          }
        });
  }

  public void getMonthGraph(int counterMove) {
    prepareMonthGraphs(calories, getMonthIntervals(counterMove));
  }

  private void prepareMonthGraphs(SortedMap<Long, Integer> calories, long[] monthIntervals) {
    int[] colors = new int[monthIntervals.length / 2];
    List<BarEntry> pairs = new ArrayList<>();
    for (int i = 0, j = 0; i < monthIntervals.length; i += 2, j++) {
      int count = 0;
      int sumKcal = 0;
      Iterator iterator = calories.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry entry = (Map.Entry) iterator.next();
        Long key = (Long) entry.getKey();
        if (key >= monthIntervals[i] && key <= monthIntervals[i + 1]) {
          count++;
          sumKcal += (int) entry.getValue();
        }
      }
      if (count != 0) {
        pairs.add(new BarEntry(j, sumKcal / count));
      } else {
        pairs.add(new BarEntry(j, 0));
      }
      colors[j] = getColor(monthIntervals[i]);
    }
    calendar.setTimeInMillis(monthIntervals[0]);

    SimpleDateFormat dateFormat = new SimpleDateFormat( "LLLL", Locale.getDefault());
    String nameMonth = dateFormat.format(calendar.getTime());
    nameMonth = nameMonth.substring(0,1).toUpperCase() + nameMonth.substring(1).toLowerCase();

    getViewState().drawMonthGraphs(pairs, colors, nameMonth,
        String.valueOf(calendar.get(Calendar.YEAR)), getNamesMonthIntervals(monthIntervals));
  }

  private ArrayList<String> getNamesMonthIntervals(long[] monthIntervals) {
    ArrayList<String> namesIntervals = new ArrayList<>();
    for (int i = 0; i < monthIntervals.length; i += 2) {
      calendar.setTimeInMillis(monthIntervals[i]);
      String name = getNumber(calendar.get(Calendar.DAY_OF_MONTH)) + " - ";
      calendar.setTimeInMillis(monthIntervals[i + 1]);
      name += getNumber(calendar.get(Calendar.DAY_OF_MONTH));
      namesIntervals.add(name);
    }
    return namesIntervals;
  }

  public long[] getMonthIntervals(int counterMove) {
    calendar.setTimeInMillis(currentTime);
    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + counterMove);
    long[] monthIntervals = new long[calendar.getActualMaximum(Calendar.WEEK_OF_MONTH) * 2];
    for (int i = 0, j = 1; i < monthIntervals.length; i += 2, j++) {
      calendar.set(Calendar.WEEK_OF_MONTH, j);
      if (j == 1) {
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
      } else {
        calendar.set(Calendar.DAY_OF_WEEK, 2);
      }
      monthIntervals[i] = calendar.getTimeInMillis();

      if (j == monthIntervals.length / 2) {
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
      } else {
        calendar.set(Calendar.DAY_OF_WEEK, 1);
      }
      monthIntervals[i + 1] = calendar.getTimeInMillis();
    }
    return monthIntervals;
  }

  public void getYearGraph(int counterMove) {
    prepareYearGraphs(calories, getYearInterval(counterMove));
  }

  private void prepareYearGraphs(SortedMap<Long, Integer> calories, long[] interval) {
    Calendar insideCalendar = Calendar.getInstance();
    int[] colors = new int[interval.length / 2];
    List<BarEntry> pairs = new ArrayList<>();
    for (int i = 0, j = 0; i < interval.length; i += 2, j++) {
      int count = 0;
      int sumKcal = 0;
      Iterator iterator = calories.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry entry = (Map.Entry) iterator.next();
        Long key = (Long) entry.getKey();
        if (key >= interval[i] && key <= interval[i + 1]) {
          count++;
          sumKcal += (int) entry.getValue();
        }
      }

      if (count != 0) {
        pairs.add(new BarEntry(j, sumKcal / count));
      } else {
        pairs.add(new BarEntry(j, 0));
      }
      colors[j] = getColor(interval[i]);
    }
    insideCalendar.setTimeInMillis(interval[0]);
    getViewState().drawYearGraphs(pairs, colors, String.valueOf(insideCalendar.get(Calendar.YEAR)),
        "");
  }

  public long[] getYearInterval(int counterMove) {
    calendar.setTimeInMillis(currentTime);
    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + counterMove);
    long[] yearIntervals = new long[24];

    for (int i = 0, j = 0; i < yearIntervals.length; i += 2, j++) {
      calendar.set(Calendar.MONTH, j);
      calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
      yearIntervals[i] = calendar.getTimeInMillis();

      calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
      yearIntervals[i + 1] = calendar.getTimeInMillis();
    }
    return yearIntervals;
  }
}

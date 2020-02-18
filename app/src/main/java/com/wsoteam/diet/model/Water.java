package com.wsoteam.diet.model;

import androidx.annotation.NonNull;
import com.google.firebase.database.Exclude;
import java.io.Serializable;

public class Water extends Eating implements Serializable {

  private int day;
  private int month;
  private int year;
  private float waterCount;
  @Exclude
  private String key;

  public Water() {
  }

  public Water(int day, int month, int year, float waterCount) {
    this.day = day;
    this.month = month;
    this.year = year;
    this.waterCount = waterCount;
  }

  public Water(String name, String urlOfImages, int calories, int carbohydrates, int protein,
      int fat, int weight, int day, int month, int year) {
    super(name, urlOfImages, calories, carbohydrates, protein, fat, weight, day, month, year);
  }

  @Override public int getDay() {
    return day;
  }

  @Override public void setDay(int day) {
    this.day = day;
  }

  @Override public int getMonth() {
    return month;
  }

  @Override public void setMonth(int month) {
    this.month = month;
  }

  @Override public int getYear() {
    return year;
  }

  @Override public void setYear(int year) {
    this.year = year;
  }

  public float getWaterCount() {
    return waterCount;
  }

  public void setWaterCount(float waterCount) {
    this.waterCount = waterCount;
  }

  @Exclude
  @Override public String getName() {
    return super.getName();
  }

  @Exclude
  @Override public String getUrlOfImages() {
    return super.getUrlOfImages();
  }


  @Exclude
  @Override public int getWeight() {
    return super.getWeight();
  }

  @Exclude
  public String getKey() {
    return key;
  }

  @Exclude
  public void setKey(String key) {
    this.key = key;
  }

  @NonNull @Override public String toString() {
    return "[ " + waterCount + " - " + day + "." + month + "." + year + " ]";
  }
}

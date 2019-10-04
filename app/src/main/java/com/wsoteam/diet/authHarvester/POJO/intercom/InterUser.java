package com.wsoteam.diet.authHarvester.POJO.intercom;

import com.squareup.moshi.Json;

public class InterUser {

  @Json(name = "Email")
  private String email;
  @Json(name = "User ID")
  private String userID;
  @Json(name = "iOS sessions")
  private int iOS;
  @Json(name = "Android sessions")
  private int android;
  @Json(name = "id")
  private String smallId;

  public String getSmallId() {
    return smallId;
  }

  public void setSmallId(String smallId) {
    this.smallId = smallId;
  }

  public int getAndroid() {
    return android;
  }

  public void setAndroid(int android) {
    this.android = android;
  }

  public int getiOS() {
    return iOS;
  }

  public void setiOS(int iOS) {
    this.iOS = iOS;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public InterUser withEmail(String email) {
    this.email = email;
    return this;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public InterUser withUserID(String userID) {
    this.userID = userID;
    return this;
  }

}
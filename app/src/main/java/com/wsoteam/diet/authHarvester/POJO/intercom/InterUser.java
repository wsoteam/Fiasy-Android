package com.wsoteam.diet.authHarvester.POJO.intercom;

import com.squareup.moshi.Json;

public class InterUser {

  @Json(name = "Email")
  private String email;
  @Json(name = "User ID")
  private String userID;
  @Json(name = "iOS sessions")
  private int ios;

  public int getIos() {
    return ios;
  }

  public void setIos(int ios) {
    this.ios = ios;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }
}
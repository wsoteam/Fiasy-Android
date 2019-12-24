package com.wsoteam.diet.authHarvester.POJO.intercom;

import com.squareup.moshi.Json;

public class InterUser {

    @Json(name = "Email")
    private String email;
    @Json(name = "User ID")
    private String userID;
    @Json(name = "Display Name")
    private String name;

    public InterUser() {
    }

    public InterUser(String email, String userID, String name) {
        this.email = email;
        this.userID = userID;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
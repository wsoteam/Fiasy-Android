
package com.wsoteam.diet.authHarvester.POJO;

import java.util.List;
import com.squareup.moshi.Json;

public class User {

    @Json(name = "localId")
    private String localId;
    @Json(name = "email")
    private String email;
    @Json(name = "emailVerified")
    private Boolean emailVerified;
    @Json(name = "passwordHash")
    private String passwordHash;
    @Json(name = "salt")
    private String salt;
    @Json(name = "lastSignedInAt")
    private String lastSignedInAt;
    @Json(name = "createdAt")
    private String createdAt;
    @Json(name = "providerUserInfo")
    private List<ProviderUserInfo> providerUserInfo = null;
    @Json(name = "displayName")
    private String displayName;
    @Json(name = "photoUrl")
    private String photoUrl;
    private boolean isTrial;

    /**
     * No args constructor for use in serialization
     * 
     */
    public User() {
    }

    public User(String localId, String email, Boolean emailVerified, String passwordHash, String salt, String lastSignedInAt, String createdAt, List<ProviderUserInfo> providerUserInfo, String displayName, String photoUrl, boolean isTrial) {
        this.localId = localId;
        this.email = email;
        this.emailVerified = emailVerified;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.lastSignedInAt = lastSignedInAt;
        this.createdAt = createdAt;
        this.providerUserInfo = providerUserInfo;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.isTrial = isTrial;
    }



    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getLastSignedInAt() {
        return lastSignedInAt;
    }

    public void setLastSignedInAt(String lastSignedInAt) {
        this.lastSignedInAt = lastSignedInAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<ProviderUserInfo> getProviderUserInfo() {
        return providerUserInfo;
    }

    public void setProviderUserInfo(List<ProviderUserInfo> providerUserInfo) {
        this.providerUserInfo = providerUserInfo;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isTrial() {
        return isTrial;
    }

    public void setTrial(boolean trial) {
        isTrial = trial;
    }
}

package com.wsoteam.diet.common.promo.POJO;

import java.io.Serializable;

public class Promo implements Serializable {
    private String id;
    private int type;
    private long duration;
    private boolean isActivated;
    private String userOwner;


    public Promo() {
    }

    public Promo(String id, int type, long duration, boolean isActivated, String userOwner) {
        this.id = id;
        this.type = type;
        this.duration = duration;
        this.isActivated = isActivated;
        this.userOwner = userOwner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public String getUserOwner() {
        return userOwner;
    }

    public void setUserOwner(String userOwner) {
        this.userOwner = userOwner;
    }
}

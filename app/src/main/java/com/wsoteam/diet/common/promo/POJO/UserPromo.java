package com.wsoteam.diet.common.promo.POJO;

public class UserPromo {
    private String id;
    private long startActivated;
    private long duration;
    private int type;

    public UserPromo() {
    }

    public UserPromo(String id, long startActivated, long duration, int type) {
        this.id = id;
        this.startActivated = startActivated;
        this.duration = duration;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getStartActivated() {
        return startActivated;
    }

    public void setStartActivated(long startActivated) {
        this.startActivated = startActivated;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

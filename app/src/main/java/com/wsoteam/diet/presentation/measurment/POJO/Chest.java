package com.wsoteam.diet.presentation.measurment.POJO;

import java.io.Serializable;

public class Chest extends Meas implements Serializable {
    public Chest() {
    }

    public Chest(String key, long timeInMillis, int meas) {
        super(key, timeInMillis, meas);
    }
}
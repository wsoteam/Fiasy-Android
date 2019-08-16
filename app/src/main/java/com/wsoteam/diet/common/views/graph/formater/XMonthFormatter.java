package com.wsoteam.diet.common.views.graph.formater;

import android.util.Log;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.wsoteam.diet.POJOSExercises.Ex;

import java.util.ArrayList;

public class XMonthFormatter extends ValueFormatter {
    ArrayList<String> weeks;

    public XMonthFormatter(ArrayList<String> weeks) {
        this.weeks = weeks;
    }

    @Override
    public String getFormattedValue(float value) {
        try {
            return weeks.get((int) value);
        } catch (Exception e){

        }
        return "";
    }
}
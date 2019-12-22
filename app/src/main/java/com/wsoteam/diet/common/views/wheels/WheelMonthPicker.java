package com.wsoteam.diet.common.views.wheels;

import android.content.Context;
import android.util.AttributeSet;

import com.wsoteam.diet.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class WheelMonthPicker extends WheelPicker implements IWheelMonthPicker {
    private int mSelectedMonth;

    public WheelMonthPicker(Context context) {
        this(context, null);
    }

    public WheelMonthPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

//        List<Integer> data = new ArrayList<>();
//        for (int i = 1; i <= 12; i++)
//            data.add(i);
//        super.setData(data);
        super.setData(Arrays.asList(context.getResources().getStringArray(R.array.names_months_meas)));

        mSelectedMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        updateSelectedYear();
    }

    private void updateSelectedYear() {
        setSelectedItemPosition(mSelectedMonth - 1);
    }

    @Override
    public void setData(List data) {
        throw new UnsupportedOperationException("You can not invoke setData in WheelMonthPicker");
    }

    @Override
    public int getSelectedMonth() {
        return mSelectedMonth;
    }

    @Override
    public void setSelectedMonth(int month) {
        mSelectedMonth = month;
        updateSelectedYear();
    }

    @Override
    public int getCurrentMonth() {
        return getCurrentItemPosition() - 1;
    }
}
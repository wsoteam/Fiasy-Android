package com.github.jhonnyx2012.horizontalpicker;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * Created by Jhonny Barrios on 22/02/2017.
 */

public class HorizontalPickerRecyclerView extends RecyclerView implements OnItemClickedListener, View.OnClickListener {

    private HorizontalPickerAdapter adapter;
    private int lastPosition;
    private LinearLayoutManager layoutManager;
    private float itemWidth;
    private HorizontalPickerListener listener;
    private int offset;
    private boolean forceScroll = false;
    private int newPosition = -1;
    private OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    listener.onStopDraggingPicker();
                    selectMonth((int) ((computeHorizontalScrollOffset() / itemWidth) + 3.5));
                    if (forceScroll && newPosition != -1 && newPosition != lastPosition) {
                        int position = newPosition;
                        selectItem(true, position);
                        lastPosition = position;
                        forceScroll = false;
                    }
                    break;
                case SCROLL_STATE_DRAGGING:
                    listener.onDraggingPicker();
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    public HorizontalPickerRecyclerView(Context context) {
        super(context);
    }

    public HorizontalPickerRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalPickerRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(Context context, final int daysToPlus, final int initialOffset, final int mBackgroundColor, final int mDateSelectedColor, final int mDateSelectedTextColor, final int mTodayDateTextColor, final int mTodayDateBackgroundColor, final int mDayOfWeekTextColor, final int mUnselectedDayTextColor) {
        this.offset = initialOffset;
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        setLayoutManager(layoutManager);
        post(() -> {
            itemWidth = getMeasuredWidth() / 7;
            adapter = new HorizontalPickerAdapter((int) itemWidth, HorizontalPickerRecyclerView.this, getContext(), daysToPlus, initialOffset, mBackgroundColor, mDateSelectedColor, mDateSelectedTextColor, mTodayDateTextColor,
                    mTodayDateBackgroundColor,
                    mDayOfWeekTextColor,
                    mUnselectedDayTextColor);
            setAdapter(adapter);
            LinearSnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(HorizontalPickerRecyclerView.this);
            removeOnScrollListener(onScrollListener);
            addOnScrollListener(onScrollListener);
        });
    }

    private void selectMonth(int position) {
        listener.onMonthSelected(adapter.getItem(position));
    }

    private void selectItem(boolean isSelected, int position) {
        newPosition = position;
        adapter.setSelectedPosition(position);
        adapter.notifyDataSetChanged();
        if (isSelected) {
            listener.onDateSelected(adapter.getItem(position));
        }
    }

    public void setListener(HorizontalPickerListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClickView(View v, int adapterPosition) {
        smoothScrollToPosition(adapterPosition);
    }

    @Override
    public void onClick(View v) {
        setDate(new DateTime());
    }

    @Override
    public void smoothScrollToPosition(int position) {
        newPosition = position;
        final RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(getContext());
        smoothScroller.setTargetPosition(newPosition);
        ((CenterSmoothScroller) smoothScroller).setCalculatedDtToFitListener((smoothScroller1, dt) -> {
            if (dt == 0) {
                selectMonth((int) ((computeHorizontalScrollOffset() / itemWidth) + 3.5));
                selectItem(true, smoothScroller1.getTargetPosition());
                lastPosition = smoothScroller1.getTargetPosition();
            }
        });
        post(() -> {
            forceScroll = true;
            layoutManager.startSmoothScroll(smoothScroller);
        });
    }

    public void setDate(DateTime date) {
        DateTime today = new DateTime().withTime(0, 0, 0, 0);
        int difference = Days.daysBetween(date, today).getDays() * (date.getYear() < today.getMillis() ? -1 : 1);
        int positionsToScroll = offset + difference;
        if (positionsToScroll >= 0)
            smoothScrollToPosition(positionsToScroll);
    }

    public void plusDay() {
        smoothScrollToPosition(newPosition + 1);
    }

    public void minusDay() {
        newPosition = newPosition - 1 > offset ? offset : newPosition - 1;
        smoothScrollToPosition(newPosition);
    }

    private static class CenterSmoothScroller extends LinearSmoothScroller {

        private SmoothScrollListener listener;

        CenterSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            int padding = (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
            listener.calculatedDtToFit(this, padding);
            return padding;
        }

        void setCalculatedDtToFitListener(SmoothScrollListener listener) {
            this.listener = listener;
        }

        interface SmoothScrollListener {
            void calculatedDtToFit(CenterSmoothScroller smoothScroller, int dt);
        }
    }
}
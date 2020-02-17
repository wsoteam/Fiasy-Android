/*
 * Copyright 2018 Keval Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance wit
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 *  the specific language governing permissions and limitations under the License.
 */

package com.wsoteam.diet.common.views.ruler;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wsoteam.diet.R;

final class VerticalRulerView extends View {
    private final String CM_APPEND = " " + getContext().getString(R.string.growth_unit);
    private int mViewWidth;
    private Paint mIndicatorPaint;
    private Paint mLongIndicatorPaint;
    private Paint mTextPaint;
    private int mIndicatorInterval = 14;
    private int mMinValue = 0;
    private int mMaxValue = 100;
    private int mTextOffset = 60;
    private float mLongIndicatorHeightRatio = 0.4f;
    private float mMiddleIndicatorHeightRatio = 0.3f;
    private float mShortIndicatorHeightRatio = 0.1f;
    private int mLongIndicatorWidth = 0;
    private int mMiddleIndicatorWidth = 0;
    private int mShortIndicatorWidth = 0;
    @ColorInt
    private int mTextColor = Color.BLACK;
    @ColorInt
    private int mIndicatorColor = Color.BLACK;
    @Dimension
    private int mTextSize = 36;
    @Dimension
    private float mIndicatorWidthPx = 4f;
    @Dimension
    private float mMiddleShortIndicatorWidthPx = 3f;

    public VerticalRulerView(@NonNull final Context context) {
        super(context);
        parseAttr(null);
    }

    public VerticalRulerView(@NonNull final Context context,
                             @Nullable final AttributeSet attrs) {
        super(context, attrs);
        parseAttr(attrs);
    }

    public VerticalRulerView(@NonNull final Context context,
                             @Nullable final AttributeSet attrs,
                             final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttr(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerticalRulerView(@NonNull final Context context,
                             @Nullable final AttributeSet attrs,
                             int defStyleAttr,
                             int defStyleRes) {

        super(context, attrs, defStyleAttr, defStyleRes);
        parseAttr(attrs);
    }

    private void parseAttr(@Nullable AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attributeSet,
                    R.styleable.RulerView,
                    0,
                    0);

            try { //Parse params
                mTextOffset = RulerViewUtils.sp2px(getContext(), 10);
                if (a.hasValue(R.styleable.RulerView_ruler_text_color)) {
                    mTextColor = a.getColor(R.styleable.RulerView_ruler_text_color, Color.BLACK);
                }

                if (a.hasValue(R.styleable.RulerView_ruler_text_size)) {
                    mTextSize = a.getDimensionPixelSize(R.styleable.RulerView_ruler_text_size, 14);
                }

                if (a.hasValue(R.styleable.RulerView_indicator_color)) {
                    mIndicatorColor = a.getColor(R.styleable.RulerView_indicator_color, Color.BLACK);
                }

                if (a.hasValue(R.styleable.RulerView_indicator_width)) {
                    mIndicatorWidthPx = a.getDimensionPixelSize(R.styleable.RulerView_indicator_width,
                            4);
                }

                if (a.hasValue(R.styleable.RulerView_indicator_interval)) {
                    mIndicatorInterval = a.getDimensionPixelSize(R.styleable.RulerView_indicator_interval,
                            4);
                }

                if (a.hasValue(R.styleable.RulerView_long_height_height_ratio)) {
                    mLongIndicatorHeightRatio = a.getFraction(R.styleable.RulerView_long_height_height_ratio,
                            1, 1, 0.4f);
                }
                if (a.hasValue(R.styleable.RulerView_middle_height_height_ratio)) {
                    mMiddleIndicatorHeightRatio = a.getFraction(R.styleable.RulerView_middle_height_height_ratio,
                            1, 1, 0.3f);
                }
                if (a.hasValue(R.styleable.RulerView_short_height_height_ratio)) {
                    mShortIndicatorHeightRatio = a.getFraction(R.styleable.RulerView_short_height_height_ratio,
                            1, 1, 0.1f);
                }
                setIndicatorHeight(mLongIndicatorHeightRatio, mMiddleIndicatorHeightRatio, mShortIndicatorHeightRatio);

                if (a.hasValue(R.styleable.RulerView_min_value)) {
                    mMinValue = a.getInteger(R.styleable.RulerView_min_value, 0);
                }
                if (a.hasValue(R.styleable.RulerView_max_value)) {
                    mMaxValue = a.getInteger(R.styleable.RulerView_max_value, 100);
                }
                setValueRange(mMinValue, mMaxValue);
            } finally {
                a.recycle();
            }
        }
        refreshPaint();
    }

    private void refreshPaint() {
        mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorPaint.setColor(mIndicatorColor);
        mIndicatorPaint.setStrokeWidth(mMiddleShortIndicatorWidthPx);
        mIndicatorPaint.setStyle(Paint.Style.STROKE);

        mLongIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLongIndicatorPaint.setColor(Color.parseColor("#34847c"));
        mLongIndicatorPaint.setStrokeWidth(mIndicatorWidthPx);
        mLongIndicatorPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTypeface(Typeface.create("Roboto", Typeface.BOLD));
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int value = 1; value < mMaxValue - mMinValue; value++) {

            if (value % 10 == 0) {
                drawLongIndicator(canvas, value);
                drawValueText(canvas, value);
            } else if (value % 5 == 0) {
                drawMiddleIndicator(canvas, value);
            } else {
                drawSmallIndicator(canvas, value);
            }
        }

        drawSmallIndicator(canvas, 0);

        drawSmallIndicator(canvas, getWidth());
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int viewHeight = (mMaxValue - mMinValue - 1) * mIndicatorInterval;

        updateIndicatorHeight(mLongIndicatorHeightRatio, mMiddleIndicatorHeightRatio, mShortIndicatorHeightRatio);

        this.setMeasuredDimension(mViewWidth, viewHeight);
    }

    private void updateIndicatorHeight(final float longIndicatorHeightRatio,
                                       final float middleIndicatorHeightRatio,
                                       final float shortIndicatorHeightRatio) {
        mLongIndicatorWidth = (int) (mViewWidth * longIndicatorHeightRatio);
        mMiddleIndicatorWidth = (int) (mViewWidth * middleIndicatorHeightRatio);
        mShortIndicatorWidth = (int) (mViewWidth * shortIndicatorHeightRatio);
    }

    private void drawSmallIndicator(@NonNull final Canvas canvas,
                                    final int value) {
        canvas.drawLine(getWidth(),
                mIndicatorInterval * value,
                getWidth() - mShortIndicatorWidth,
                mIndicatorInterval * value,
                mIndicatorPaint);
    }

    private void drawMiddleIndicator(@NonNull final Canvas canvas,
                                     final int value) {
        canvas.drawLine(getWidth(),
                mIndicatorInterval * value,
                getWidth() - mMiddleIndicatorWidth,
                mIndicatorInterval * value,
                mIndicatorPaint);
    }

    private void drawLongIndicator(@NonNull final Canvas canvas,
                                   final int value) {
        canvas.drawLine(getWidth(),
                mIndicatorInterval * value,
                getWidth() - mLongIndicatorWidth,
                mIndicatorInterval * value,
                mLongIndicatorPaint);
    }

    private void drawValueText(@NonNull final Canvas canvas,
                               final int value) {
        Rect bounds = new Rect();
        mTextPaint.getTextBounds("100", 0, 3, bounds);
        int height = bounds.height();
        canvas.drawText((248 - value) + CM_APPEND,
                getWidth() - mLongIndicatorWidth - mTextPaint.getTextSize() - mTextOffset,
                mIndicatorInterval * value + (float) height / 2,
                mTextPaint);
    }

    @CheckResult
    @ColorInt
    int getTextColor() {
        return mIndicatorColor;
    }

    void setTextColor(@ColorInt final int color) {
        mTextColor = color;
        refreshPaint();
    }

    @CheckResult
    float getTextSize() {
        return mTextSize;
    }

    void setTextSize(final int textSizeSp) {
        mTextSize = RulerViewUtils.sp2px(getContext(), textSizeSp);
        refreshPaint();
    }

    @CheckResult
    @ColorInt
    int getIndicatorColor() {
        return mIndicatorColor;
    }

    void setIndicatorColor(@ColorInt final int color) {
        mIndicatorColor = color;
        refreshPaint();
    }

    @CheckResult
    float getIndicatorWidth() {
        return mIndicatorWidthPx;
    }

    void setIndicatorWidth(final int widthPx) {
        mIndicatorWidthPx = widthPx;
        refreshPaint();
    }

    @CheckResult
    int getMinValue() {
        return mMinValue;
    }

    @CheckResult
    int getMaxValue() {
        return mMaxValue;
    }

    void setValueRange(final int minValue, final int maxValue) {
        mMinValue = minValue;
        mMaxValue = maxValue;
        invalidate();
    }

    @CheckResult
    int getIndicatorIntervalWidth() {
        return mIndicatorInterval;
    }

    void setIndicatorIntervalDistance(final int indicatorIntervalPx) {
        if (indicatorIntervalPx <= 0)
            throw new IllegalArgumentException("Interval cannot be negative or zero.");

        mIndicatorInterval = indicatorIntervalPx;
        invalidate();
    }

    @CheckResult
    float getLongIndicatorHeightRatio() {
        return mLongIndicatorHeightRatio;
    }

    @CheckResult
    float getShortIndicatorHeightRatio() {
        return mShortIndicatorHeightRatio;
    }

    void setIndicatorHeight(final float longHeightRatio,
                            final float middleHeightRatio,
                            final float shortHeightRatio) {

        if (shortHeightRatio < 0 || shortHeightRatio > 1) {
            throw new IllegalArgumentException("Sort indicator height must be between 0 to 1.");
        }

        if (longHeightRatio < 0 || longHeightRatio > 1) {
            throw new IllegalArgumentException("Long indicator height must be between 0 to 1.");
        }

        if (shortHeightRatio > longHeightRatio) {
            throw new IllegalArgumentException("Long indicator height cannot be less than sort indicator height.");
        }

        mLongIndicatorHeightRatio = longHeightRatio;
        mMiddleIndicatorHeightRatio = middleHeightRatio;
        mShortIndicatorHeightRatio = shortHeightRatio;

        updateIndicatorHeight(mLongIndicatorHeightRatio, mMiddleIndicatorHeightRatio, mShortIndicatorHeightRatio);

        invalidate();
    }
}

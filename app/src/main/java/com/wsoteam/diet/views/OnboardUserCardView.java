package com.wsoteam.diet.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import com.wsoteam.diet.R;

public class OnboardUserCardView extends LinearLayout {

  private final TextView title;
  private final TextView value;

  public OnboardUserCardView(Context context) {
    this(context, null);
  }

  public OnboardUserCardView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    setOrientation(VERTICAL);
    setGravity(Gravity.CENTER);

    title = new AppCompatTextView(context);
    title.setGravity(Gravity.CENTER_HORIZONTAL);
    title.setTextSize(16);
    title.setTextColor(0xc0000000);

    value = new AppCompatTextView(context);
    value.setGravity(Gravity.CENTER_HORIZONTAL);
    value.setTextSize(18);
    value.setTextColor(0xffef7d02);

    addView(title);
    addView(value);

    final TypedArray array =
        context.obtainStyledAttributes(attrs, R.styleable.OnboardUserCardView);

    title.setText(array.getString(R.styleable.OnboardUserCardView_text));
    value.setText(array.getString(R.styleable.OnboardUserCardView_value));

    array.recycle();
  }

  public void setValue(String value){
    this.value.setText(value);
  }
}

package com.wsoteam.diet.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;

public class Img {
    public static void setBackGround(Drawable drawable, View background){
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bitmap = bitmapDrawable.getBitmap();
            if(bitmap != null) {
                Palette p = Palette.from(bitmap).generate();
                int mainColor = p.getMutedColor(0);
                int alphaColor = 191;
                background.setBackgroundColor(ColorUtils.setAlphaComponent(mainColor, alphaColor));
            }
        }
    }
}

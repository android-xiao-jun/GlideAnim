package com.span;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

public class UrlDrawable extends BitmapDrawable {
    @Nullable
    private Drawable drawable;

    @Override
    public void draw(Canvas canvas) {
        if (drawable != null) drawable.draw(canvas);
    }

    public void setDrawable(@Nullable Drawable drawable) {
        this.drawable = drawable;
        if (drawable == null) {
            return;
        }
        setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }
}
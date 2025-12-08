package com.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.lang.ref.WeakReference;

public class GlideImageGetter implements Html.ImageGetter {

    private final WeakReference<TextView> weakReferenceTextView;
    private final Context context;
    private int width = 60;
    private int height = 60;

    public GlideImageGetter(TextView textView, Context context) {
        this.weakReferenceTextView = new WeakReference<>(textView);// 防止内存泄漏
        this.context = context.getApplicationContext(); // 防止内存泄漏
    }


    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public Drawable getDrawable(String source) {
        UrlDrawable urlDrawable = new UrlDrawable();
        urlDrawable.setBounds(0, 0, width, height);

        Glide.with(context)
                .asBitmap()
                .load(source)
                .centerCrop()
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), resource);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        urlDrawable.setDrawable(drawable);
                        TextView textView = weakReferenceTextView.get();
                        if (textView!=null){
                            textView.post(() -> textView.setText(textView.getText()));
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        urlDrawable.setDrawable(errorDrawable);
                        TextView textView = weakReferenceTextView.get();
                        if (textView!=null) {
                            textView.post(() -> textView.setText(textView.getText()));
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });

        return urlDrawable;
    }


}
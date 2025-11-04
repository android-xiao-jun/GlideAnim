package com.nine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class NinePatchTarget extends CustomTarget<File> {

    private final Context context;
    private final View imageView;

    public NinePatchTarget(View imageView) {
        this.context = imageView.getContext();
        this.imageView = imageView;
    }

    @Override
    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
        try (InputStream is = new FileInputStream(resource)) {
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            byte[] chunk = bitmap.getNinePatchChunk();
            if (NinePatch.isNinePatchChunk(chunk)) {
                NinePatchChunk deserialize = NinePatchChunk.deserialize(chunk);
                NinePatchDrawable drawable = new NinePatchDrawable(
                        context.getResources(),
                        bitmap,
                        chunk,
                        deserialize==null?null :deserialize.mPaddings,
                        null
                );
                imageView.setBackground(drawable);
            } else {
                Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
                imageView.setBackground(drawable);
            }
            onLoaded(bitmap); // 可选回调
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadCleared(@Nullable Drawable placeholder) {}

    /**
     * 图片加载完成回调
     */
    public void onLoaded(Bitmap bitmap){

    };
}

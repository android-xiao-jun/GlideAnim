package com.nine;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;

/**
 * 加载 .9 图背景的 Glide Target。
 *
 * <p>解码与 chunk 解析已下沉到 {@link NinePatchStreamDecoder}，结果由 Glide 缓存管理。
 * 这里只负责把缓存命中的 {@link NinePatchResource} 渲染为独立的背景 Drawable。
 */
public class NinePatchTargetView extends CustomViewTarget<View, NinePatchResource> {

    private final View imageView;

    public NinePatchTargetView(View imageView) {
        super(imageView);
        this.imageView = imageView;
    }

    @Override
    public void onResourceReady(@NonNull NinePatchResource resource,
                                @Nullable Transition<? super NinePatchResource> transition) {
        // 每个 View 生成独立 Drawable，避免共享缓存实例造成的 bounds 冲突
        imageView.setBackground(resource.newDrawable(imageView.getResources()));
        onLoaded(resource.getBitmap()); // 可选回调
    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        imageView.setBackground(errorDrawable);
    }

    @Override
    protected void onResourceLoading(@Nullable Drawable placeholder) {
        super.onResourceLoading(placeholder);
        imageView.setBackground(placeholder);
    }

    @Override
    protected void onResourceCleared(@Nullable Drawable placeholder) {

    }

    /**
     * 图片加载完成回调
     */
    public void onLoaded(Bitmap bitmap) {

    }
}

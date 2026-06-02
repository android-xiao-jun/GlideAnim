package com.nine;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.Resource;

/**
 * 可被 Glide 缓存的 .9 图解码结果。
 *
 * <p>持有解码后的 Bitmap 与 .9 chunk 信息，由 Glide 的内存/磁盘缓存统一管理；
 * 解码（含 chunk 解析）只在首次加载时进行，后续相同 URL 直接命中缓存。
 *
 * <p>每次展示通过 {@link #newDrawable(Resources)} 生成独立的 Drawable，
 * 避免多个 View 共享同一 Drawable 实例时的 bounds 冲突。
 */
public class NinePatchResource implements Resource<NinePatchResource> {

    private final Bitmap bitmap;
    @Nullable
    private final byte[] chunk;
    @Nullable
    private final Rect paddings;

    public NinePatchResource(@NonNull Bitmap bitmap, @Nullable byte[] chunk, @Nullable Rect paddings) {
        this.bitmap = bitmap;
        this.chunk = chunk;
        this.paddings = paddings;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * 基于缓存的 Bitmap 生成新的 Drawable，确保每个 View 拥有独立的绘制状态。
     */
    public Drawable newDrawable(Resources resources) {
        if (chunk != null && NinePatch.isNinePatchChunk(chunk)) {
            return new NinePatchDrawable(resources, bitmap, chunk, paddings, null);
        }
        return new BitmapDrawable(resources, bitmap);
    }

    @NonNull
    @Override
    public Class<NinePatchResource> getResourceClass() {
        return NinePatchResource.class;
    }

    @NonNull
    @Override
    public NinePatchResource get() {
        return this;
    }

    @Override
    public int getSize() {
        return bitmap == null ? 0 : bitmap.getByteCount();
    }

    @Override
    public void recycle() {
        // 不主动 recycle：缓存命中的 Bitmap 可能仍被已展示的 View 背景引用，
        // 交由 GC 在无引用后回收，避免 "Canvas: trying to use a recycled bitmap" 崩溃。
    }
}

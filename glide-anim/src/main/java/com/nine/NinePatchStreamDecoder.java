package com.nine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * 将图片流解码为可被 Glide 缓存的 {@link NinePatchResource}。
 *
 * <p>解码过程（含 .9 chunk 解析）只在首次加载时执行，结果进入 Glide 的内存/磁盘缓存，
 * 后续相同 URL 直接命中缓存，无需重复读取文件与解码。
 */
public class NinePatchStreamDecoder implements ResourceDecoder<InputStream, NinePatchResource> {

    @Override
    public boolean handles(@NonNull InputStream source, @NonNull Options options) {
        return true;
    }

    @Nullable
    @Override
    public Resource<NinePatchResource> decode(@NonNull InputStream source, int width, int height,
                                              @NonNull Options options) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(source);
        if (bitmap == null) {
            return null;
        }
        bitmap.setDensity(DisplayMetrics.DENSITY_XXHIGH);

        byte[] chunk = bitmap.getNinePatchChunk();
        Rect paddings = null;
        if (NinePatch.isNinePatchChunk(chunk)) {
            NinePatchChunk ninePatchChunk = NinePatchChunk.deserialize(chunk);
            paddings = ninePatchChunk == null ? null : ninePatchChunk.mPaddings;
        }
        return new NinePatchResource(bitmap, chunk, paddings);
    }
}

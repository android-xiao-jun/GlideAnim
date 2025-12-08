/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Span that replaces the text it's attached to with a {@link Drawable} that can be aligned with
 * the bottom or with the baseline of the surrounding text. The drawable can be constructed from
 * varied sources:
 * <ul>
 * <li>{@link Bitmap} - see {@link #ImageUrlSpan(Context, Bitmap)} and
 * {@link #ImageUrlSpan(Context, Bitmap, int)}
 * </li>
 * <li>{@link Drawable} - see {@link #ImageUrlSpan(Drawable, int)}</li>
 * <li>resource id - see {@link #ImageUrlSpan(Context, int, int)}</li>
 * <li>{@link Uri} - see {@link #ImageUrlSpan(Context, Uri, int,TextView)}</li>
 * </ul>
 * The default value for the vertical alignment is {@link DynamicDrawableSpan#ALIGN_BOTTOM}
 * <p>
 * For example, an <code>ImagedSpan</code> can be used like this:
 * <pre>
 * SpannableString string = new SpannableString("Bottom: span.\nBaseline: span.");
 * // using the default alignment: ALIGN_BOTTOM
 * string.setSpan(new ImageUrlSpan(this, R.mipmap.ic_launcher), 7, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
 * string.setSpan(new ImageUrlSpan(this, R.mipmap.ic_launcher, DynamicDrawableSpan.ALIGN_BASELINE),
 * 22, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
 * </pre>
 * <img src="{@docRoot}reference/android/images/text/style/ImageUrlSpan.png" />
 * <figcaption>Text with <code>ImageUrlSpan</code>s aligned bottom and baseline.</figcaption>
 */
public class ImageUrlSpan extends DynamicDrawableSpan {

    private Drawable mDrawable;
    private Uri mContentUri;
    private int mResourceId;
    private Context mContext;
    private String mSource;

    @Nullable
    private WeakReference<TextView> weakReferenceTextView = null;
    private int width = 60;
    private int height = 60;

    /**
     * @deprecated Use {@link #ImageUrlSpan(Context, Bitmap)} instead.
     */
    @Deprecated
    public ImageUrlSpan(@NonNull Bitmap b) {
        this(null, b, ALIGN_BOTTOM);
    }

    /**
     * @deprecated Use {@link #ImageUrlSpan(Context, Bitmap, int)} instead.
     */
    @Deprecated
    public ImageUrlSpan(@NonNull Bitmap b, int verticalAlignment) {
        this(null, b, verticalAlignment);
    }

    /**
     * Constructs an {@link ImageUrlSpan} from a {@link Context} and a {@link Bitmap} with the default
     * alignment {@link DynamicDrawableSpan#ALIGN_BOTTOM}
     *
     * @param context context used to create a drawable from {@param bitmap} based on the display
     *                metrics of the resources
     * @param bitmap  bitmap to be rendered
     */
    public ImageUrlSpan(@NonNull Context context, @NonNull Bitmap bitmap) {
        this(context, bitmap, ALIGN_BOTTOM);
    }

    /**
     * Constructs an {@link ImageUrlSpan} from a {@link Context}, a {@link Bitmap} and a vertical
     * alignment.
     *
     * @param context           context used to create a drawable from {@param bitmap} based on
     *                          the display metrics of the resources
     * @param bitmap            bitmap to be rendered
     * @param verticalAlignment one of {@link DynamicDrawableSpan#ALIGN_BOTTOM} or
     *                          {@link DynamicDrawableSpan#ALIGN_BASELINE}
     */
    public ImageUrlSpan(@NonNull Context context, @NonNull Bitmap bitmap, int verticalAlignment) {
        super(verticalAlignment);
        mContext = context;
        mDrawable = context != null
                ? new BitmapDrawable(context.getResources(), bitmap)
                : new BitmapDrawable(bitmap);
        int width = mDrawable.getIntrinsicWidth();
        int height = mDrawable.getIntrinsicHeight();
        mDrawable.setBounds(0, 0, width > 0 ? width : 0, height > 0 ? height : 0);
    }

    /**
     * Constructs an {@link ImageUrlSpan} from a drawable with the default
     * alignment {@link DynamicDrawableSpan#ALIGN_BOTTOM}.
     *
     * @param drawable drawable to be rendered
     */
    public ImageUrlSpan(@NonNull Drawable drawable) {
        this(drawable, ALIGN_BOTTOM);
    }

    /**
     * Constructs an {@link ImageUrlSpan} from a drawable and a vertical alignment.
     *
     * @param drawable          drawable to be rendered
     * @param verticalAlignment one of {@link DynamicDrawableSpan#ALIGN_BOTTOM} or
     *                          {@link DynamicDrawableSpan#ALIGN_BASELINE}
     */
    public ImageUrlSpan(@NonNull Drawable drawable, int verticalAlignment) {
        super(verticalAlignment);
        mDrawable = drawable;
    }

    /**
     * Constructs an {@link ImageUrlSpan} from a drawable and a source with the default
     * alignment {@link DynamicDrawableSpan#ALIGN_BOTTOM}
     *
     * @param drawable drawable to be rendered
     * @param source   drawable's Uri source
     */
    public ImageUrlSpan(@NonNull Drawable drawable, @NonNull String source) {
        this(drawable, source, ALIGN_BOTTOM);
    }

    /**
     * Constructs an {@link ImageUrlSpan} from a drawable, a source and a vertical alignment.
     *
     * @param drawable          drawable to be rendered
     * @param source            drawable's uri source
     * @param verticalAlignment one of {@link DynamicDrawableSpan#ALIGN_BOTTOM} or
     *                          {@link DynamicDrawableSpan#ALIGN_BASELINE}
     */
    public ImageUrlSpan(@NonNull Drawable drawable, @NonNull String source, int verticalAlignment) {
        super(verticalAlignment);
        mDrawable = drawable;
        mSource = source;
    }

    /**
     * Constructs an {@link ImageUrlSpan} from a {@link Context} and a {@link Uri} with the default
     * alignment {@link DynamicDrawableSpan#ALIGN_BOTTOM}. The Uri source can be retrieved via
     * {@link #getSource()}
     *
     * @param context context used to create a drawable from {@param bitmap} based on the display
     *                metrics of the resources
     * @param uri     {@link Uri} used to construct the drawable that will be rendered
     */
    public ImageUrlSpan(@NonNull Context context, @NonNull Uri uri) {
        this(context, uri, ALIGN_BOTTOM, null);
    }


    /**
     * Constructs an {@link ImageUrlSpan} from a {@link Context} and a {@link Uri} with the default
     * alignment {@link DynamicDrawableSpan#ALIGN_BOTTOM}. The Uri source can be retrieved via
     * {@link #getSource()}
     *
     * @param context context used to create a drawable from {@param bitmap} based on the display
     *                metrics of the resources
     * @param uri     {@link Uri} used to construct the drawable that will be rendered
     * @param tv      text reference refresh
     */
    public ImageUrlSpan(@NonNull Context context, @NonNull Uri uri, @Nullable TextView tv) {
        this(context, uri, ALIGN_BOTTOM, tv);
    }

    /**
     * Constructs an {@link ImageUrlSpan} from a {@link Context}, a {@link Uri} and a vertical
     * alignment. The Uri source can be retrieved via {@link #getSource()}
     *
     * @param context           context used to create a drawable from {@param bitmap} based on
     *                          the display
     *                          metrics of the resources
     * @param uri               {@link Uri} used to construct the drawable that will be rendered.
     * @param verticalAlignment one of {@link DynamicDrawableSpan#ALIGN_BOTTOM} or
     *                          {@link DynamicDrawableSpan#ALIGN_BASELINE}
     * @param tv                text reference refresh
     */
    public ImageUrlSpan(@NonNull Context context, @NonNull Uri uri, int verticalAlignment, @Nullable TextView tv) {
        super(verticalAlignment);
        mContext = context;
        mContentUri = uri;
        mSource = uri.toString();

        if (tv != null) {
            this.weakReferenceTextView = new WeakReference<>(tv);// 防止内存泄漏
            this.mContext = context.getApplicationContext(); // 防止内存泄漏
        }
    }

    /**
     * Constructs an {@link ImageUrlSpan} from a {@link Context} and a resource id with the default
     * alignment {@link DynamicDrawableSpan#ALIGN_BOTTOM}
     *
     * @param context    context used to retrieve the drawable from resources
     * @param resourceId drawable resource id based on which the drawable is retrieved
     */
    public ImageUrlSpan(@NonNull Context context, @DrawableRes int resourceId) {
        this(context, resourceId, ALIGN_BOTTOM);
    }

    /**
     * Constructs an {@link ImageUrlSpan} from a {@link Context}, a resource id and a vertical
     * alignment.
     *
     * @param context           context used to retrieve the drawable from resources
     * @param resourceId        drawable resource id based on which the drawable is retrieved.
     * @param verticalAlignment one of {@link DynamicDrawableSpan#ALIGN_BOTTOM} or
     *                          {@link DynamicDrawableSpan#ALIGN_BASELINE}
     */
    public ImageUrlSpan(@NonNull Context context, @DrawableRes int resourceId,
                        int verticalAlignment) {
        super(verticalAlignment);
        mContext = context;
        mResourceId = resourceId;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public Drawable getDrawable() {
        Drawable drawable = null;

        if (mDrawable != null) {
            drawable = mDrawable;
        } else if (mContentUri != null && weakReferenceTextView != null) {
            UrlDrawable urlDrawable = new UrlDrawable();
            urlDrawable.setBounds(0, 0, width, height);
            Glide.with(mContext)
                    .asBitmap()
                    .load(mContentUri)
                    .override(width, height)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            BitmapDrawable b = new BitmapDrawable(mContext.getResources(), resource);
                            b.setBounds(0, 0, b.getIntrinsicWidth(), b.getIntrinsicHeight());
                            urlDrawable.setDrawable(b);
                            TextView tv = weakReferenceTextView == null ? null : weakReferenceTextView.get();
                            if (tv != null) {
                                tv.setText(tv.getText());
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
            drawable = urlDrawable;
        }else if (mContentUri != null){
            Bitmap bitmap = null;
            try {
                InputStream is = mContext.getContentResolver().openInputStream(
                        mContentUri);
                bitmap = BitmapFactory.decodeStream(is);
                drawable = new BitmapDrawable(mContext.getResources(), bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                is.close();
            } catch (Exception e) {
                Log.e("ImageSpan", "Failed to loaded content " + mContentUri, e);
            }
        }else {
            try {
                drawable = mContext.getDrawable(mResourceId);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
            } catch (Exception e) {
                Log.e("ImageUrlSpan", "Unable to find resource: " + mResourceId);
            }
        }

        return drawable;
    }

    /**
     * Returns the source string that was saved during construction.
     *
     * @return the source string that was saved during construction
     * @see #ImageUrlSpan(Drawable, String)
     * @see #ImageUrlSpan(Context, Uri)
     */
    @Nullable
    public String getSource() {
        return mSource;
    }
}

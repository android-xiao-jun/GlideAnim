package com.test.android.demo;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

/**
 * desc:图片 （支持格式 Lottie 的 svga,apng,gif）
 * verson:
 */
public class AvatarView extends FrameLayout {

    @Nullable
    private ImageView viewChild;
    private String avatarPendant;

    public AvatarView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {

    }

    public void loadData(String url) {
        final String oldStr = avatarPendant;
        avatarPendant = url;
        boolean change = !TextUtils.equals(oldStr, avatarPendant);
        ImageView localView = viewChild;

        if (url == null || TextUtils.isEmpty(url)) {
            if (localView != null) {
                localView.setVisibility(GONE);
            }
            return;
        }
        int index = url.indexOf("?");
        String urlSub = url;
        if (index != -1) {
            urlSub = url.substring(0, index);
        }
        if (localView == null || change) {
            if (localView != null) {
                removeView(localView);
            }
           if (urlSub.endsWith(".svga")) {
                localView = createViewSVGA();
            } else {
                localView = createView();
            }
            addView(localView);
            viewChild = localView;
        }
        if (urlSub.endsWith(".gif")) {
            Glide.with(this).asGif().load(url).into(localView);
        } else {
            Glide.with(this).load(url).into(localView);
        }
        localView.setVisibility(VISIBLE);
    }

    private ImageView createView() {
        ImageView view = new ImageView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return view;
    }

    private ImageView createViewSVGA() {
        com.opensource.svgaplayer.SVGAImageView imageView = new com.opensource.svgaplayer.SVGAImageView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
        return imageView;
    }
}

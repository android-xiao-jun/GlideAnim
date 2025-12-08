package com.test.android.demo;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.nine.NinePatchTarget;
import com.span.GlideImageGetter;
import com.span.ImageUrlSpan;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AvatarView mAvatarView1 = findViewById(R.id.mAvatarView1);
        mAvatarView1.loadData("https://xxx/app/gitf/test/jchnuhfjwck.svga");


        AvatarView mAvatarView2 = findViewById(R.id.mAvatarView2);
        mAvatarView2.loadData("https://xxx/app/gitf/test/9dikcjhojl.png");//apng


        FrameLayout image2 = findViewById(R.id.image2);
        Glide.with(this).asFile().load("https://xxx/live/temp/livekit2_bg_privilege_temp_2.9.png").into(new NinePatchTarget(image2));

        FrameLayout image3 = findViewById(R.id.image3);
        Glide.with(this).asFile().load("https://xxx/live/temp/livekit2_bg_privilege_temp_3.9.png").into(new NinePatchTarget(image3));


        TextView textview = findViewById(R.id.textview);
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append("这是一段文本");
        sb.append(" ");
        sb.append("图片在前面");
        ImageUrlSpan imageUrlSpan = new ImageUrlSpan(this, Uri.parse("https://xxx/live/gameCoin.png"), textview);
        sb.setSpan(imageUrlSpan, 5, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        ImageUrlSpan imageUrlSpan2 = new ImageUrlSpan(this, Uri.parse("https://xxx/live/notice.png"), textview);
        sb.setSpan(imageUrlSpan2, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textview.setText(sb);


        TextView textviewHtml = findViewById(R.id.textviewHtml);
        Spanned spannedHtml = Html.fromHtml("<p style='vertical-align: middle;'> <img src='https://xxx/live/gameCoin.png'/> 图片在前面 <img src='https://xxx/live/notice.png'/> </p>",new GlideImageGetter(textviewHtml,this),null);
        textviewHtml.setText(spannedHtml);

    }

}

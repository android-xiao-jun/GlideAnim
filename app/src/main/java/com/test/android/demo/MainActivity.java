package com.test.android.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.nine.NinePatchTarget;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AvatarView mAvatarView1 = findViewById(R.id.mAvatarView1);
        mAvatarView1.loadData("https://xx/app/gitf/test/jchnuhfjwck.svga");


        AvatarView mAvatarView2 = findViewById(R.id.mAvatarView2);
        mAvatarView2.loadData("https://xx/app/gitf/test/9dikcjhojl.png");//apng


        FrameLayout image2 = findViewById(R.id.image2);
        Glide.with(this).asFile().load("https://xx/live/temp/livekit2_bg_privilege_temp_2.9.png").into(new NinePatchTarget(image2));

        FrameLayout image3 = findViewById(R.id.image3);
        Glide.with(this).asFile().load("https://xx/live/temp/livekit2_bg_privilege_temp_2.9.png").into(new NinePatchTarget(image3));
    }

}

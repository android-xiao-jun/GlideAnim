package com.test.android.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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

    }

}

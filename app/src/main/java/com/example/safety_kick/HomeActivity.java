package com.example.safety_kick;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.run_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RunInfoActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.mypage_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                Toast.makeText(HomeActivity.this, "로그인을 해주세요!!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.find_kick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                Toast.makeText(HomeActivity.this, "로그인을 해주세요!!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        ImageView findKick = findViewById(R.id.find_kick);
        String gifUrl = "https://cdn.dribbble.com/users/3632750/screenshots/6798569/isometric_smartphone_gps.gif";

        Glide.with(this).asGif().load(gifUrl).into(findKick);


        findViewById(R.id.qr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                Toast.makeText(HomeActivity.this, "로그인을 해주세요!!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        ImageView qr = findViewById(R.id.qr);
        String qrUrl = "https://storage.googleapis.com/support-kms-prod/mQmcrC93Ryi2U4x5UdZNeyHQMybbyk71yCVm";

        Glide.with(this).asGif().load(qrUrl).into(qr);


        findViewById(R.id.service_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ServiceActivity.class);
                startActivity(intent);
            }
        });
    }
}

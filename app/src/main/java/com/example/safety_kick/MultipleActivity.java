package com.example.safety_kick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MultipleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple);

        ImageView previousButton = findViewById(R.id.previousButton);
        ImageView nextButton = findViewById(R.id.nextButton);

        // 로고
        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MultipleActivity.this, LoginSuccessActivity.class);
                startActivity(intent);
            }
        });

        // 이전 버튼 클릭 시 헬멧 인식 화면으로 이동
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MultipleActivity.this, HelmetActivity.class);
                startActivity(intent);
            }
        });


        // 다음 버튼 클릭 시 대여화면으로 이동
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MultipleActivity.this, RentActivity.class);
                startActivity(intent);
            }
        });
    }
}


package com.example.safety_kick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class HelmetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helmet);

        ImageView previousButton = findViewById(R.id.previousButton);
        ImageView nextButton = findViewById(R.id.nextButton);

        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelmetActivity.this, LoginSuccessActivity.class);
                startActivity(intent);
            }
        });

        // 이전 버튼 클릭 시 액션
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이전 화면으로 이동
                Intent intent = new Intent(HelmetActivity.this, AlcoholActivity.class);
                startActivity(intent);
            }
        });


        // 다음 버튼 클릭 시 액션
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 다음 화면으로 이동
                Intent intent = new Intent(HelmetActivity.this, MultipleActivity.class);
                startActivity(intent);
            }
        });
    }
}

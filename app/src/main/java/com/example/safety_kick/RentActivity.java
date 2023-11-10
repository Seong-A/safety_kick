package com.example.safety_kick;


import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RentActivity extends AppCompatActivity {
    private TextView messageTextView;
    private long startTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_success);

        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RentActivity.this, LoginSuccessActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.rent_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 킥보드 대여 버튼을 클릭하면 시간 측정 시작
                startTimeMillis = SystemClock.elapsedRealtime();
                Intent intent = new Intent(RentActivity.this, ReturnActivity.class);

                // 경과 시간을 밀리초 단위로 전달
                intent.putExtra("START_TIME", startTimeMillis);
                startActivity(intent);
            }
        });

    }
}
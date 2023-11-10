package com.example.safety_kick;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReturnActivity extends AppCompatActivity {
    private TextView messageTextView;
    private DatabaseReference rentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);

        rentsRef = FirebaseDatabase.getInstance().getReference().child("rents");

        // 이전에 RentActivity에서 전달한 시간 데이터를 가져옴
        Intent intent = getIntent();
        String elapsedTime = intent.getStringExtra("ELAPSED_TIME");

        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReturnActivity.this, LoginSuccessActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.return_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();

                if (user != null) {
                    // rent_id 자동 생성
                    String rentId = rentsRef.push().getKey();

                    // 현재 시간을 가져옴
                    long endTimeMillis = SystemClock.elapsedRealtime();
                    // RentActivity에서 전달한 시작 시간을 가져옴
                    long startTimeMillis = getIntent().getLongExtra("START_TIME", 0);

                    // 경과 시간을 계산 (밀리초 단위)
                    long elapsedTimeMillis = endTimeMillis - startTimeMillis;
                    // 경과 시간을 계산 (분 단위)
                    int elapsedMinutes = (int) (elapsedTimeMillis / 60000); // 1분은 60,000밀리초
                    // 남은 초를 계산
                    double remainingSeconds = (double) (elapsedTimeMillis % 60000) / 1000; // 초로 변환

                    // 문자열로 변환
                    String elapsedTimeString = String.format("%d:%.2f", elapsedMinutes, remainingSeconds);
                    String[] timeComponents = elapsedTimeString.split(":");
                    int minutes = Integer.parseInt(timeComponents[0]);
                    // 1분에 100원 요금 계산
                    double feeValue = minutes * 100;

                    // 현재 로그인된 사용자의 이메일을 가져와서 Firebase에 저장
                    String userEmail = user.getEmail();
                    rentsRef.child(rentId).child("email").setValue(userEmail);
                    rentsRef.child(rentId).child("time").setValue(elapsedTimeString);
                    rentsRef.child(rentId).child("fee").setValue(String.valueOf(feeValue));

                    // PaymentActivity로 전환
                    Intent paymentIntent = new Intent(ReturnActivity.this, PaymentActivity.class);
                    paymentIntent.putExtra("ELAPSED_TIME", elapsedTimeMillis);
                    startActivity(paymentIntent);

                    finish(); // 현재 액티비티 종료
                } else {
                    // 사용자가 로그인되어 있지 않다면 어떻게 처리할지 여기에 추가 로직을 구현할 수 있어요.
                }
            }
        });

    }
}


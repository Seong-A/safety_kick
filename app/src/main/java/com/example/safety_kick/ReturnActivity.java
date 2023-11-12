package com.example.safety_kick;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReturnActivity extends AppCompatActivity {
    private TextView messageTextView;
    private DatabaseReference rentsRef;
    private DatabaseReference qrcodeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);

        rentsRef = FirebaseDatabase.getInstance().getReference().child("rents");
        qrcodeRef = FirebaseDatabase.getInstance().getReference().child("qrcode").child("qrcode1");

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
                    String rid = rentsRef.push().getKey();

                    qrcodeRef.child("qid").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String qid = dataSnapshot.getValue(String.class);

                                // 현재 시간 및 날짜 가져오기
                                long endTimeMillis = SystemClock.elapsedRealtime();
                                long startTimeMillis = getIntent().getLongExtra("START_TIME", 0);
                                long elapsedTimeMillis = endTimeMillis - startTimeMillis;

                                // 경과 시간을 계산 (분 단위)
                                int elapsedMinutes = (int) (elapsedTimeMillis / 60000); // 1분은 60,000밀리초
                                // 남은 초를 계산
                                double remainingSeconds = (double) (elapsedTimeMillis % 60000) / 1000; // 초로 변환

                                // 문자열로 변환
                                String elapsedTimeString = String.format("%d:%.2f", elapsedMinutes, remainingSeconds);

                                // 현재 로그인된 사용자의 이메일을 가져와서 Firebase에 저장
                                String userEmail = user.getEmail();
                                rentsRef.child(rid).child("email").setValue(userEmail);
                                rentsRef.child(rid).child("time").setValue(elapsedTimeString);
                                rentsRef.child(rid).child("fee").setValue(String.valueOf(elapsedMinutes * 100));
                                rentsRef.child(rid).child("qid").setValue(qid);

                                // 현재 날짜를 Firebase에 저장
                                String currentDate = getCurrentDate();
                                rentsRef.child(rid).child("date").setValue(currentDate);

                                // PaymentActivity로 전환
                                Intent paymentIntent = new Intent(ReturnActivity.this, PaymentActivity.class);
                                paymentIntent.putExtra("ELAPSED_TIME", elapsedTimeMillis);
                                startActivity(paymentIntent);

                                finish(); // 현재 액티비티 종료
                            } else {
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("FirebaseWrite", "Database write failed: " + databaseError.getMessage());
                        }
                    });
                }
            }
        });
    }

    // 현재 날짜를 문자열로 반환하는 메서드
    private String getCurrentDate() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(currentDate);
    }
}

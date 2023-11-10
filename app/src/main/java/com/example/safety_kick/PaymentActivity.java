package com.example.safety_kick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PaymentActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        TextView userTextView = findViewById(R.id.user_name);
        TextView elapsedTimeTextView = findViewById(R.id.time);
        TextView moneyTextView = findViewById(R.id.money);

        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, LoginSuccessActivity.class);
                startActivity(intent);
            }
        });

        // RentActivity에서 전달한 경과 시간을 받아옴
        Intent intent = getIntent();
        long elapsedTimeMillis = intent.getLongExtra("ELAPSED_TIME", 0);

        // 경과 시간을 형식화하여 문자열로 변환
        String formattedElapsedTime = formatElapsedTime(elapsedTimeMillis);

        // 텍스트뷰에 경과 시간 표시
        elapsedTimeTextView.setText("경과 시간: " + formattedElapsedTime);
        // 계산된 돈을 텍스트뷰에 표시
        double money = calculateMoney(elapsedTimeMillis);
        moneyTextView.setText("요금: " + money + "원");

        checkAndUpdateUserName(userTextView);
    }

    private void checkAndUpdateUserName(final TextView userTextView) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            databaseReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        if (name != null) {
                            String welcomeMessage = "🤍" + name + "🤍";
                            userTextView.setText(name);
                            userTextView.setText(welcomeMessage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(PaymentActivity.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // 경과 시간을 형식화하여 문자열로 반환
    private String formatElapsedTime(long elapsedTime) {
        long minutes = (elapsedTime / 1000) / 60;
        long seconds = (elapsedTime / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // 요금 계산 메서드
    private double calculateMoney(long elapsedTime) {
        // elapsedTime을 분 단위로 변환
        double elapsedMinutes = elapsedTime / (1000 * 60);

        // 요금 계산 (예: 1분에 100원)
        return elapsedMinutes * 100;
    }
}
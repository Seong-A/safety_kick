package com.example.safety_kick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class LoginSuccessActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        TextView userTextView = findViewById(R.id.user_name);

        findViewById(R.id.user_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginSuccessActivity.this, MypageActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.mypage_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginSuccessActivity.this, MypageActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.find_kick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginSuccessActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.qr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new IntentIntegrator(LoginSuccessActivity.this).initiateScan();
            }
        });

        findViewById(R.id.service_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginSuccessActivity.this, ServiceActivity.class);
                startActivity(intent);
            }
        });


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
                            String welcomeMessage = name + "님, 환영방구뿡뿡이~";
                            userTextView.setText(name);
                            userTextView.setText(welcomeMessage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(LoginSuccessActivity.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // QR 코드 스캔 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // Scanning canceled
            } else {
                // Scanning successful
                String scannedData = result.getContents();
                Log.d("QR_CODE", "Scanned Data: " + scannedData);
                checkAndBorrowItem(scannedData);
            }
        }
    }

    private void checkAndBorrowItem(String scannedData) {
        DatabaseReference qrCodeRef = databaseReference.child("qrcode").child(scannedData);
        qrCodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 데이터를 찾았을 경우
                    String qrcode1 = dataSnapshot.child("qrcode1").getValue(String.class);

                    if (qrcode1 != null) {
                        //킥보드 빌릴 수 있는 경우
                        Intent intent = new Intent(LoginSuccessActivity.this, RentActivity.class);
                        intent.putExtra("qrcode1", qrcode1);
                        startActivity(intent);

                    } else {
                        // 킥보드의 이름이 없는 경우 또는 다른 필요한 데이터가 없는 경우 처리
                        Toast.makeText(LoginSuccessActivity.this, "킥보드 정보가 부족합니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 데이터를 찾지 못한 경우
                    Toast.makeText(LoginSuccessActivity.this, "해당 QR 코드에 대한 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginSuccessActivity.this, "데이터베이스에서 데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
package com.example.safety_kick;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

        findViewById(R.id.run_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginSuccessActivity.this, RunInfoActivity.class);
                startActivity(intent);
            }
        });

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

        ImageView findKick = findViewById(R.id.find_kick);
        String gifUrl = "https://cdn.dribbble.com/users/3632750/screenshots/6798569/isometric_smartphone_gps.gif";

        Glide.with(this).asGif().load(gifUrl).into(findKick);

        findViewById(R.id.qr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new IntentIntegrator(LoginSuccessActivity.this).initiateScan();
            }
        });

        ImageView qr = findViewById(R.id.qr);
        String qrUrl = "https://storage.googleapis.com/support-kms-prod/mQmcrC93Ryi2U4x5UdZNeyHQMybbyk71yCVm";

        Glide.with(this).asGif().load(qrUrl).into(qr);


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
                            String welcomeMessage = name + "님, 환영합니다~";
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
                // 스캔 결과가 없는 경우
            } else {
                // 스캔 결과가 있는 경우
                checkAndBorrowItem(result.getContents());
            }
        }
    }

    private void checkAndBorrowItem(String scannedData) {
        DatabaseReference qrcodeRef = databaseReference.child("qrcode").child("qrcode1");

        qrcodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String qid = dataSnapshot.child("qid").getValue(String.class);
                    String Latitude = dataSnapshot.child("latitude").getValue(String.class);
                    String Longitude = dataSnapshot.child("longitude").getValue(String.class);

                    if (qid != null && qid.equals(scannedData)) {
                        Intent intent = new Intent(LoginSuccessActivity.this, AlcoholActivity.class);
                        intent.putExtra("latitude", Latitude);
                        intent.putExtra("longitude", Longitude);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginSuccessActivity.this, "No matching QR code found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginSuccessActivity.this, "QR code data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginSuccessActivity.this, "Failed to retrieve data from the database.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

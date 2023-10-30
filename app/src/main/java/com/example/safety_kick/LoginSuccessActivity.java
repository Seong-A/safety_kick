package com.example.safety_kick;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
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

import java.util.HashMap;


public class LoginSuccessActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private BluetoothAdapter bluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            // 장치가 Bluetooth를 지원하지 않는 경우 처리
            Toast.makeText(this, "장치가 Bluetooth를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Bluetooth 활성화 확인
        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth 비활성화 상태이므로 사용자에게 활성화 요청
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

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

        // 위치권한
        String[] permission_list = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };


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
                    String id = dataSnapshot.child("id").getValue(String.class);
                    String startLatitude = dataSnapshot.child("start_latitude").getValue(String.class);
                    String startLongitude = dataSnapshot.child("start_longitude").getValue(String.class);

                    if (id != null && id.equals(scannedData)) {
                        // Matching QR code found
                        // Navigate to a specific activity for this ID
                        Intent intent = new Intent(LoginSuccessActivity.this, RentActivity.class);
                        // Pass the latitude and longitude to the next activity if needed
                        intent.putExtra("latitude", startLatitude);
                        intent.putExtra("longitude", startLongitude);
                        startActivity(intent);
                    } else {
                        // No matching QR code found
                        Toast.makeText(LoginSuccessActivity.this, "No matching QR code found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // "qrcode1" does not exist
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

package com.example.safety_kick;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity implements LocationListener {

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String selectedCardName;

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 위치 매니저 초기화
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (locationManager != null) {
                // 위치 업데이트 요청
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            } else {
                Toast.makeText(this, "Failed to initialize LocationManager", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 권한이 없으면 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }



        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // 위치가 변경될 때 호출되는 메서드
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // 데이터베이스에서 'qrcode1' 아래의 값을 업데이트합니다.
                updateQRCodeLocation(String.valueOf(latitude), String.valueOf(longitude));

                // 위치 정보를 더이상 받지 않도록 리스너를 해제합니다.
                locationManager.removeUpdates(this);
            }
        };


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

        findViewById(R.id.payment_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationUpdate();
                double money = calculateMoney(elapsedTimeMillis);
                String currentDateAndTime = getCurrentDateAndTime();

                Intent intent = new Intent(PaymentActivity.this, PaymentListActivity.class);
                intent.putExtra("PAYMENT_AMOUNT", money);
                intent.putExtra("PAYMENT_DATE", currentDateAndTime);
                intent.putExtra("CARD_NAME", selectedCardName);
                startActivity(intent);
            }
        });
    }

    private String getCurrentDateAndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
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

    private void updateQRCodeLocation(String latitude, String longitude) {

            DatabaseReference qrcode1Ref = databaseReference.child("qrcode").child("qrcode1");

            // 값을 업데이트합니다.
            qrcode1Ref.child("latitude").setValue(latitude);
            qrcode1Ref.child("longitude").setValue(longitude);

            Toast.makeText(PaymentActivity.this, "위치가 성공적으로 업데이트되었습니다", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLocationChanged(Location location) {
        // 위치가 변경될 때 호출되는 메서드
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // 데이터베이스에서 'qrcode1' 아래의 값을 업데이트합니다.
        updateQRCodeLocation(String.valueOf(latitude), String.valueOf(longitude));

        // 위치 정보를 더이상 받지 않도록 리스너를 해제합니다.
        locationManager.removeUpdates(this);
    }

    private void requestLocationUpdate() {
        if (locationManager != null) {
            // 위치 업데이트를 요청합니다.
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } else {
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

    public void showCardMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.context_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // 메뉴 아이템 클릭 시 처리할 로직 추가
                Toast.makeText(PaymentActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                setCardName(item.getTitle().toString());
                selectedCardName = item.getTitle().toString();
                return true;
            }
        });

        popupMenu.show();
    }

    private void setCardName(String cardName) {
        EditText cardNameEditText = findViewById(R.id.CardName);
        cardNameEditText.setText(cardName);
    }

}
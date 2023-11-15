package com.example.safety_kick;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RentListActivity extends AppCompatActivity {
    private DatabaseReference rentsRef;
    private LinearLayout rentListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_list);

        // Firebase 데이터베이스 및 참조 초기화
        rentsRef = FirebaseDatabase.getInstance().getReference().child("rents");
        rentListLayout = findViewById(R.id.rentListLayout);

        // 로고
        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RentListActivity.this, LoginSuccessActivity.class);
                startActivity(intent);
            }
        });

        getDataFromFirebase();
    }

    // 데이터베이스에서 데이터 가져오기
    private void getDataFromFirebase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            // 해당 사용자의 주행내역 가져오기
            String userEmail = user.getEmail();

            rentsRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int rentCount = (int) snapshot.getChildrenCount();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Rent rent = dataSnapshot.getValue(Rent.class);
                        if (rent != null) {
                            String date = rent.getDate();
                            String time = rent.getTime();
                            String fee = rent.getFee();

                            addRentTextView(date, time, fee);
                        }
                    }

                    // 동적으로 계산된 높이를 rentListLayout에 설정
                    setRentListLayoutHeight(rentCount);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 데이터 가져오기에 실패한 경우 처리
                }
            });
        }
    }

    // 대여기록
    private void addRentTextView(String date, String time, String fee) {
        // 각 대여 기록을 위한 새 LinearLayout 생성
        LinearLayout newRentLayout = new LinearLayout(this);
        newRentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        newRentLayout.setOrientation(LinearLayout.VERTICAL);
        newRentLayout.setBackgroundResource(R.drawable.edit_background4);
        newRentLayout.setGravity(Gravity.CENTER);

        // 날짜, 시간 및 요금에 대한 TextView 생성
        TextView dateTextView = new TextView(this);
        dateTextView.setText("이용날짜 : " + date);
        dateTextView.setTextSize(18);
        dateTextView.setTextColor(ContextCompat.getColor(this, R.color.black));
        dateTextView.setTypeface(null, Typeface.BOLD);
        dateTextView.setPadding(50, 20, 10, 20);

        TextView timeTextView = new TextView(this);
        timeTextView.setText("이용시간 : " + time);
        timeTextView.setTextSize(15);
        timeTextView.setTextColor(ContextCompat.getColor(this, R.color.black));
        timeTextView.setPadding(50, 20, 10, 20);

        TextView feeTextView = new TextView(this);
        feeTextView.setText("이용요금 : " + fee);
        feeTextView.setTextSize(15);
        feeTextView.setTextColor(ContextCompat.getColor(this, R.color.black));
        feeTextView.setPadding(50, 20, 10, 20);

        newRentLayout.addView(dateTextView);
        newRentLayout.addView(timeTextView);
        newRentLayout.addView(feeTextView);

        // 새 LinearLayout을 rentListLayout에 추가
        rentListLayout.addView(newRentLayout);

        // 대여 기록 간의 여백 추가
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) newRentLayout.getLayoutParams();
        layoutParams.setMargins(30, 30, 30, 20);
        newRentLayout.setLayoutParams(layoutParams);
    }


    private void setRentListLayoutHeight(int rentCount) {
        // 최소한의 높이 설정
        int minHeight = 400;
        int calculatedHeight = minHeight + (rentCount - 1) * 150;
        rentListLayout.setMinimumHeight(calculatedHeight);
    }

}

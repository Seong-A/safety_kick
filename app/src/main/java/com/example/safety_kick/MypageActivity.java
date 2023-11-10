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

public class MypageActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        TextView userTextView = findViewById(R.id.user_welcome);

        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, LoginSuccessActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.user_modify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the ModifyUserInfoActivity
                Intent intent = new Intent(MypageActivity.this, ModifyUserInfoActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.rent_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, RentListActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.payment_method).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, PaymentListActivity.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
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
                            String welcomeMessage = "Welcome!!!" + "   🤍" + name + "🤍";
                            userTextView.setText(name);
                            userTextView.setText(welcomeMessage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MypageActivity.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MypageActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

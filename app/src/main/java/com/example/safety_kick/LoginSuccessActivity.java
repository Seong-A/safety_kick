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
                Intent intent = new Intent(LoginSuccessActivity.this, MypageActivity.class);
                startActivity(intent);
            }
        });

        checkAndUpdateUserName(userTextView);
    }


    private void checkAndUpdateUserName(final TextView userTextView) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user !=null) {
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
}


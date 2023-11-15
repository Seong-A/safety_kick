package com.example.safety_kick;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ServiceActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        firebaseAuth = FirebaseAuth.getInstance();

        // 로고
        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(ServiceActivity.this, LoginSuccessActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ServiceActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });


    }
}

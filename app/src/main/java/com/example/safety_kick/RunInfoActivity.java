package com.example.safety_kick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RunInfoActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_info);

        firebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(RunInfoActivity.this, LoginSuccessActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(RunInfoActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
}

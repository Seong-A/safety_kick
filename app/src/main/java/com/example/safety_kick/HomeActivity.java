package com.example.safety_kick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.safety_kick.R;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    //private TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
//                startActivity(intent);
//            }
//        });

//        Intent intent = getIntent();
//        String name = intent.getStringExtra("name");
//        nameTextView = findViewById(R.id.nameTextView);
//        if (name != null) {
//            nameTextView.setText("Welcome, " + name + "!");
//        }
    }
}

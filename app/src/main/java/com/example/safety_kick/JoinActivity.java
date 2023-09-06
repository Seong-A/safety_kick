package com.example.safety_kick;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.safety_kick.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JoinActivity extends AppCompatActivity {

    private EditText nameEdit;
    private EditText emailEdit;
    private EditText passwordEdit;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        nameEdit = findViewById(R.id.name_edit);
        emailEdit = findViewById(R.id.email_edit);
        passwordEdit = findViewById(R.id.password_edit);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.signUp_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEdit.getText().toString().trim();
                String email = emailEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(JoinActivity.this, task -> {
                            if(task.isSuccessful()){
                                User user = new User(name, email,password); // User class should be defined
                                String uid = task.getResult().getUser().getUid();
                                databaseReference.child("users").child(uid).child(name).setValue(user);

                                Toast.makeText(JoinActivity.this, "회원가입을 축하합니다!!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(JoinActivity.this, "회원가입 실패ㅠㅠ", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            public void writeNewUser(String uid, String name, String email, String password) {
                User user = new User(name, email,password); // User class should be defined
                databaseReference.child("users").child(uid).child(name).setValue(user);
            }
        });
    }


}

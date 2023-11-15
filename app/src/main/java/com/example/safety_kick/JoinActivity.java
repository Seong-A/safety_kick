package com.example.safety_kick;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JoinActivity extends AppCompatActivity {

    private EditText nameEdit;
    private EditText emailEdit;
    private EditText passwordEdit;
    private EditText phoneEdit;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        nameEdit = findViewById(R.id.name_edit);
        emailEdit = findViewById(R.id.email_edit);
        passwordEdit = findViewById(R.id.password_edit);
        phoneEdit = findViewById(R.id.phone_edit);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // 이름
        nameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    nameEdit.setBackgroundResource(R.drawable.edit_background);
                } else {
                    nameEdit.setBackgroundResource(R.drawable.btn_background3);
                }
            }
        });

        // 이메일
        emailEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    emailEdit.setBackgroundResource(R.drawable.edit_background);
                } else {
                    emailEdit.setBackgroundResource(R.drawable.btn_background3);
                }
            }
        });

        // 비밀번호
        passwordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        passwordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Change the input type when the passwordEdit gains focus
                    passwordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    passwordEdit.setBackgroundResource(R.drawable.edit_background);
                } else {
                    // Change it back to the password input type when it loses focus
                    passwordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordEdit.setBackgroundResource(R.drawable.btn_background3);
                }
            }
        });

        // 전화번호
        phoneEdit.setInputType(InputType.TYPE_CLASS_PHONE);

        phoneEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    phoneEdit.setBackgroundResource(R.drawable.edit_background);
                } else {
                    phoneEdit.setBackgroundResource(R.drawable.btn_background3);
                }
            }
        });

        // 회원가입버튼
        findViewById(R.id.signUp_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEdit.getText().toString().trim();
                String email = emailEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();
                String phone = phoneEdit.getText().toString().trim();

                // 비밀번호 형식 검증
                if (!isValidPassword(password)) {
                    Toast.makeText(JoinActivity.this, "비밀번호는 영문과 숫자의 조합으로 8자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 전화번호 형식 검증
                if (!isValidPhoneNumber(phone)) {
                    Toast.makeText(JoinActivity.this, "올바른 전화번호 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(JoinActivity.this, task -> {
                            if (task.isSuccessful()) {
                                User user = new User(name, email, password, phone);
                                String uid = task.getResult().getUser().getUid();
                                databaseReference.child(uid).setValue(user);

                                Toast.makeText(JoinActivity.this, "회원가입을 축하합니다!!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(JoinActivity.this, "회원가입 실패ㅠㅠ", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
    private boolean isValidPassword(String password) {
        // 비밀번호는 영문과 숫자의 조합으로 8자 이상이어야 함
        return password.matches("^(?=.*[a-zA-Z])(?=.*[0-9]).{8,}$");
    }

    // 전화번호는 010-0000-0000 형식이어야 함
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{3}-\\d{4}-\\d{4}");
    }

}

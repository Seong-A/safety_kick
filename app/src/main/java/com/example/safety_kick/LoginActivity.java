package com.example.safety_kick;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    private EditText emailEdit;
    private EditText passwordEdit;
    private FirebaseAuth firebaseAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private Button google_btn;
    private GoogleSignInAccount gsa;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEdit = findViewById(R.id.email_edit);
        passwordEdit = findViewById(R.id.password_edit);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        checkAndUpdateUserName();
    }

    private void checkAndUpdateUserName() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = databaseReference.child("users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String displayName = dataSnapshot.child("displayName").getValue(String.class);
                        if (displayName != null) {
                            Button userButton = findViewById(R.id.user_btn);
                            userButton.setText(displayName);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(LoginActivity.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                }
            });
        }




        //일반 로그인
        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, LoginSuccessActivity.class);
                                startActivity(intent);
                                // TODO: Navigate to the next screen
                            } else {
                                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        //회원가입
        findViewById(R.id.signUp_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });


        // 구글
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        google_btn = findViewById(R.id.google_btn);
        google_btn.setOnClickListener(view -> {
            // 기존에 로그인 했던 계정을 확인한다.
            gsa = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);
            if (gsa != null) // 로그인 되있는 경우
                Toast.makeText(LoginActivity.this, R.string.status_login, Toast.LENGTH_SHORT).show();
            else
                signIn();
        });

    }



    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /* 사용자 정보 가져오기 */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            if (acct != null) {
                firebaseAuthWithGoogle(acct.getIdToken());

                String personName = acct.getDisplayName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();

                Log.d(TAG, "handleSignInResult:personName "+personName);
                Log.d(TAG, "handleSignInResult:personEmail "+personEmail);
                Log.d(TAG, "handleSignInResult:personId "+personId);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        Toast.makeText(LoginActivity.this, R.string.success_login, Toast.LENGTH_SHORT).show();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
//                            updateUI(user);
                    } else {

                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, R.string.failed_login, Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                    }
                });
    }
    private void updateUI(FirebaseUser user) {

    }



    }


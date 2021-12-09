package com.bhaskar.attendancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneNo extends AppCompatActivity {

    private Button verifyBtn;
    private TextInputLayout verificationCodeEnteredByUser;
    private ProgressBar progressBar;

    private String name, username, email, phoneNo, password, role;
    private String verificationCodeEnteredBySystem;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_no);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        verifyBtn = findViewById(R.id.verify_btn);
        verificationCodeEnteredByUser = findViewById(R.id.verification_code);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        name = getIntent().getStringExtra("name");
        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        phoneNo = getIntent().getStringExtra("phoneNo");
        password = getIntent().getStringExtra("password");
        role = getIntent().getStringExtra("role");

        sendVerificationCodeToUser(phoneNo);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = verificationCodeEnteredByUser.getEditText().getText().toString();
                if (code.isEmpty() || code.length() < 6) {
                    verificationCodeEnteredByUser.setError("Wrong OTP");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        });

    }

    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNo,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeEnteredBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeEnteredBySystem, codeByUser);
        signInByUserCredentials(credential);
    }

    private void signInByUserCredentials(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(VerifyPhoneNo.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(VerifyPhoneNo.this,"code verified.",Toast.LENGTH_LONG).show();
                    storeDataInDB(name, username, email, phoneNo, password);
                } else {
                    Toast.makeText(VerifyPhoneNo.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void storeDataInDB(String name, String username, String email, String phoneNo, String password) {
        if(name.isEmpty() || username.isEmpty() || phoneNo.isEmpty() || password.isEmpty()){
            Toast.makeText(VerifyPhoneNo.this,"User Registration Failed!!",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), SignUp.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else{
            /*reference = FirebaseDatabase.getInstance().getReference("users");
            UserHelperClass userHelperClass = new UserHelperClass(name, username, email, phoneNo, password, role);
            reference.child(username).setValue(userHelperClass);

            Toast.makeText(VerifyPhoneNo.this,"Congratulation! You have successfully Signed Up.",Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getApplicationContext(), HomePage.class);
            intent.putExtra("username", username);
            intent.putExtra("role", role);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);*/
        }
    }


}

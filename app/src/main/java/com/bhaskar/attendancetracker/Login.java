package com.bhaskar.attendancetracker;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private Button callSignUpBtn, loginBtn, forgotPwdBtn;
    private TextView welcomeText, sloganText;
    private TextInputLayout username, password;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.GONE);

        forgotPwdBtn = findViewById(R.id.forget_pwd_btn);
        callSignUpBtn = findViewById(R.id.signup_btn);
        loginBtn = findViewById(R.id.login_btn);
        welcomeText = findViewById(R.id.welcome_back_text);
        sloganText = findViewById(R.id.slogan_name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);


        callSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                Pair[] pairs = new Pair[6];
                pairs[0] = new Pair<View, String>(welcomeText, "tran_welcome");
                pairs[1] = new Pair<View, String>(sloganText, "tran_slogan");
                pairs[2] = new Pair<View, String>(username, "tran_username");
                pairs[3] = new Pair<View, String>(password, "tran_password");
                pairs[4] = new Pair<View, String>(loginBtn, "tran_login_signup");
                pairs[5] = new Pair<View, String>(callSignUpBtn, "tran_signup_login");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
                    startActivity(intent, activityOptions.toBundle());
                } else {
                    startActivity(intent);
                }
            }
        });

        forgotPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if (validateUsername()) {
                    forgetPassword();
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


    }

    private boolean validateUsername() {
        String val = username.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";
        if (val.isEmpty()) {
            username.setError("Field can't be empty");
        } else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private boolean validatePassword() {
        String val = password.getEditText().getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";
        if (val.isEmpty()) {
            password.setError("Field can't be empty");
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    public void loginUser(View view) {
        progressBar.setVisibility(View.VISIBLE);
        if (!validateUsername() | !validatePassword()) {
            progressBar.setVisibility(View.GONE);
            return;
        }
        isUser();
    }

    private void isUser() {
        final String userEnteredUsername = username.getEditText().getText().toString().trim();
        final String userEnteredPassword = password.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username.setError(null);
                    username.setErrorEnabled(false);
                    String passwordFromDB = dataSnapshot.child(userEnteredUsername).child("password").getValue(String.class);
                    String roleFromDB = dataSnapshot.child(userEnteredUsername).child("role").getValue(String.class);
                    if (passwordFromDB.equals(userEnteredPassword)) {
                        password.setError(null);
                        password.setErrorEnabled(false);
                        Intent intent = new Intent(getApplicationContext(), HomePage.class);
                        intent.putExtra("username", userEnteredUsername);
                        intent.putExtra("role", roleFromDB);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        password.setError("Wrong Password!!");
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    username.setError("No such User Exists");
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void forgetPassword() {

        final String userEnteredUsername = username.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username.setError(null);
                    username.setErrorEnabled(false);

                    String passwordFromDB = dataSnapshot.child(userEnteredUsername).child("password").getValue(String.class);
                    String favouriteColourFromDB = dataSnapshot.child(userEnteredUsername).child("favouriteColour").getValue(String.class);
                    String favouriteFoodFromDB = dataSnapshot.child(userEnteredUsername).child("favouriteFood").getValue(String.class);
                    String favouriteActorFromDB = dataSnapshot.child(userEnteredUsername).child("favouriteActor").getValue(String.class);

                    Intent intent = new Intent(getApplicationContext(), ForgetPassword.class);
                    intent.putExtra("password", passwordFromDB);
                    intent.putExtra("favouriteColour", favouriteColourFromDB);
                    intent.putExtra("favouriteFood", favouriteFoodFromDB);
                    intent.putExtra("favouriteActor", favouriteActorFromDB);
                    startActivity(intent);
                    progressBar.setVisibility(View.GONE);

                } else {
                    username.setError("No such User Exists");
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

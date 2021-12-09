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

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ViewProfile extends AppCompatActivity {

    private Button goToHomePageBtn, updateProfileBtn;
    private String username, name, email, phoneNo, password, role;
    private TextInputLayout nameText, usernameText, emailText, phoneNoText, passwordText;
    private DatabaseReference reference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        username = getIntent().getStringExtra("username");
        role = getIntent().getStringExtra("role");

        goToHomePageBtn = findViewById(R.id.view_profile_go_to_home_page_btn);
        updateProfileBtn = findViewById(R.id.view_profile_update_btn);
        nameText = findViewById(R.id.view_profile_name);
        usernameText = findViewById(R.id.view_profile_username);
        emailText = findViewById(R.id.view_profile_email);
        phoneNoText = findViewById(R.id.view_profile_phoneNo);
        passwordText = findViewById(R.id.view_profile_password);
        progressBar = findViewById(R.id.view_profile_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        reference = FirebaseDatabase.getInstance().getReference("users");

        goToHomePage();
        populateProfileDetails();
        updateProfile();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomePage.class);
        intent.putExtra("username", username);
        intent.putExtra("role", role);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void updateProfile() {
        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileDetails();
            }
        });
    }

    private void updateProfileDetails() {
        progressBar.setVisibility(View.VISIBLE);
        if (!validateName() | !validateUsername() | !validateEmail() | !validatePhoneNo() | !validatePassword()) {
            return;
        }
        if (isUsernameChanged()) {
            Toast.makeText(ViewProfile.this, "Username cannot be changed!!", Toast.LENGTH_SHORT).show();
        } else {
            boolean dataUpdated = false;
            if(isNameChanged() ){
                dataUpdated = true;
            }
            if(isEmailChanged()){
                dataUpdated = true;
            }
            if(isPhoneNoChanged()){
                dataUpdated = true;
            }
            if(isPasswordChanged()){
                dataUpdated = true;
            }
            if (dataUpdated) {
                Toast.makeText(ViewProfile.this, "Data updated Successfully!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ViewProfile.this, "Data is Same. Nothing to Update", Toast.LENGTH_SHORT).show();
            }
        }
        progressBar.setVisibility(View.GONE);
    }

    private boolean isPasswordChanged() {
        String userEnteredPassword = passwordText.getEditText().getText().toString();
        if (password.equals(userEnteredPassword)) {
            return false;
        } else {
            reference.child(username).child("password").setValue(userEnteredPassword);
            password = userEnteredPassword;
            return true;
        }
    }

    private boolean isPhoneNoChanged() {
        String userEnteredPhoneNo = phoneNoText.getEditText().getText().toString();
        if (phoneNo.equals(userEnteredPhoneNo)) {
            return false;
        } else {
            reference.child(username).child("phoneNo").setValue(userEnteredPhoneNo);
            phoneNo = userEnteredPhoneNo;
            return true;
        }
    }

    private boolean isEmailChanged() {
        String userEnteredEmail = emailText.getEditText().getText().toString();
        if (email.equals(userEnteredEmail)) {
            return false;
        } else {
            reference.child(username).child("email").setValue(userEnteredEmail);
            email = userEnteredEmail;
            return true;
        }
    }

    private boolean isUsernameChanged() {
        String userEnteredUsername = usernameText.getEditText().getText().toString();
        if (username.equals(userEnteredUsername)) {
            return false;
        } else {
            //reference.child(username).child("username").setValue(userEnteredUsername);
            //username = userEnteredUsername;
            return true;
        }
    }

    private boolean isNameChanged() {
        String userEnteredName = nameText.getEditText().getText().toString();
        if (name.equals(userEnteredName)) {
            return false;
        } else {
            reference.child(username).child("name").setValue(userEnteredName);
            name = userEnteredName;
            return true;
        }
    }

    private boolean validateName() {
        String val = nameText.getEditText().getText().toString();
        if (val.isEmpty()) {
            nameText.setError("Field can't be empty");
        } else {
            nameText.setError(null);
            nameText.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private boolean validateUsername() {
        String val = usernameText.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";
        if (val.isEmpty()) {
            usernameText.setError("Field can't be empty");
        } else if (val.length() >= 15) {
            usernameText.setError("Length is too long");
        } else if (!val.matches(noWhiteSpace)) {
            usernameText.setError("White Spaces are not allowed");
        } else {
            usernameText.setError(null);
            usernameText.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private boolean validateEmail() {
        String val = emailText.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            emailText.setError("Field can't be empty");
        } else if (!val.matches(emailPattern)) {
            emailText.setError("Invalid email address");
        } else {
            emailText.setError(null);
            emailText.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private boolean validatePhoneNo() {
        String val = phoneNoText.getEditText().getText().toString();
        if (val.isEmpty()) {
            phoneNoText.setError("Field can't be empty");
        } else if (val.length() != 10) {
            phoneNoText.setError("Enter 10 digit mobile no");
        } else {
            phoneNoText.setError(null);
            phoneNoText.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private boolean validatePassword() {
        String val = passwordText.getEditText().getText().toString();
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
            passwordText.setError("Field can't be empty");
        } else if (!val.matches(passwordVal)) {
            passwordText.setError("Password too weak!");
        } else {
            passwordText.setError(null);
            passwordText.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private void populateProfileDetails() {

        Query checkUser = reference.orderByChild("username").equalTo(username);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    name = dataSnapshot.child(username).child("name").getValue(String.class);
                    email = dataSnapshot.child(username).child("email").getValue(String.class);
                    phoneNo = dataSnapshot.child(username).child("phoneNo").getValue(String.class);
                    password = dataSnapshot.child(username).child("password").getValue(String.class);
                    if (name != null && !name.isEmpty()) {
                        nameText.getEditText().setText(name);
                    }
                    if (email != null && !email.isEmpty()) {
                        emailText.getEditText().setText(email);
                    }
                    if (phoneNo != null && !phoneNo.isEmpty()) {
                        phoneNoText.getEditText().setText(phoneNo);
                    }
                    if (password != null && !password.isEmpty()) {
                        passwordText.getEditText().setText(password);
                    }
                    if (username != null && !username.isEmpty()) {
                        usernameText.getEditText().setText(username);
                    }
                } else {
                    Toast.makeText(ViewProfile.this, "Data Not Found!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void goToHomePage() {
        goToHomePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                intent.putExtra("username", username);
                intent.putExtra("role", role);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}

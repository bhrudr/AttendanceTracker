package com.bhaskar.attendancetracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ChangeSignUpPassword extends AppCompatActivity {

    private Button goToHomePageBtn, updateSignUpPwdBtn;
    private String username, role, secIncrgSignUpPassword, irAdminSignUpPassword;
    private TextInputLayout irAdminSignUpPasswordText, secIncrgSignUpPasswordText;
    private DatabaseReference reference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_sign_up_password);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        username = getIntent().getStringExtra("username");
        role = getIntent().getStringExtra("role");

        goToHomePageBtn = findViewById(R.id.cng_sign_up_pwd_go_to_home_page_btn);
        updateSignUpPwdBtn = findViewById(R.id.cng_sign_up_pwd_update_btn);
        irAdminSignUpPasswordText = findViewById(R.id.cng_sign_up_pwd_ir_admin_pwd_txt);
        secIncrgSignUpPasswordText = findViewById(R.id.cng_sign_up_pwd_sec_inchrg_pwd_txt);
        progressBar = findViewById(R.id.cng_sign_up_pwd_progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        reference = FirebaseDatabase.getInstance().getReference("userRolePassword");

        goToHomePage();
        populateSignUpPasswordDetails();
        updateSignUpPasswordDetails();
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

    private void updateSignUpPasswordDetails() {
        updateSignUpPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!validateIRAdminSignUpPassword() | !validateSecIncrgSignUpPassword()) {
                    return;
                }
                boolean dataUpdated = false;
                if (isIRAdminSignUpPasswordChanged()) {
                    dataUpdated = true;
                }
                if (isSecIncrgSignUpPasswordChanged()) {
                    dataUpdated = true;
                }
                if (dataUpdated) {
                    Toast.makeText(ChangeSignUpPassword.this, "Data updated Successfully!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChangeSignUpPassword.this, "Data is Same. Nothing to Update", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean isSecIncrgSignUpPasswordChanged() {
        String userEnteredPassword = secIncrgSignUpPasswordText.getEditText().getText().toString();
        if (secIncrgSignUpPassword.equals(userEnteredPassword)) {
            return false;
        } else {
            reference = FirebaseDatabase.getInstance().getReference("userRolePassword");
            reference.child("section_incharge").child("password").setValue(userEnteredPassword);
            secIncrgSignUpPassword = userEnteredPassword;
            return true;
        }
    }

    private boolean isIRAdminSignUpPasswordChanged() {
        String userEnteredPassword = irAdminSignUpPasswordText.getEditText().getText().toString();
        if (irAdminSignUpPassword.equals(userEnteredPassword)) {
            return false;
        } else {
            reference = FirebaseDatabase.getInstance().getReference("userRolePassword");
            reference.child("ir_admin").child("password").setValue(userEnteredPassword);
            irAdminSignUpPassword = userEnteredPassword;
            return true;
        }
    }

    private boolean validateSecIncrgSignUpPassword() {
        String val = secIncrgSignUpPasswordText.getEditText().getText().toString();
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
            secIncrgSignUpPasswordText.setError("Field can't be empty");
        } else if (!val.matches(passwordVal)) {
            secIncrgSignUpPasswordText.setError("Password too weak!");
        } else {
            secIncrgSignUpPasswordText.setError(null);
            secIncrgSignUpPasswordText.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private boolean validateIRAdminSignUpPassword() {
        String val = irAdminSignUpPasswordText.getEditText().getText().toString();
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
            irAdminSignUpPasswordText.setError("Field can't be empty");
        } else if (!val.matches(passwordVal)) {
            irAdminSignUpPasswordText.setError("Password too weak!");
        } else {
            irAdminSignUpPasswordText.setError(null);
            irAdminSignUpPasswordText.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private void populateSignUpPasswordDetails() {
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    UserRolePassword userRolePassword = dataSnapshot.getValue(UserRolePassword.class);
                    if (userRolePassword != null) {
                        if (userRolePassword.getRole().equals("ir_admin")) {
                            irAdminSignUpPasswordText.getEditText().setText(userRolePassword.getPassword());
                            irAdminSignUpPassword = userRolePassword.getPassword();
                        } else if (userRolePassword.getRole().equals("section_incharge")) {
                            secIncrgSignUpPasswordText.getEditText().setText(userRolePassword.getPassword());
                            secIncrgSignUpPassword = userRolePassword.getPassword();
                        }
                    }
                } else {
                    Toast.makeText(ChangeSignUpPassword.this, "Data Not Found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

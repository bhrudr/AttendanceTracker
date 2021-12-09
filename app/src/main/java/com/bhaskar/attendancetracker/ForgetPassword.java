package com.bhaskar.attendancetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class ForgetPassword extends AppCompatActivity {

    private TextInputLayout favouriteColourText, favouriteFoodText, favouriteActorText;
    private TextView passwordText;
    private String favouriteColourInDB, favouriteFoodInDB, favouriteActorInDB, passwordInDB;
    private Button verifyUserBtn, goToLoginBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        favouriteColourInDB = getIntent().getStringExtra("favouriteColour");
        favouriteFoodInDB = getIntent().getStringExtra("favouriteFood");
        favouriteActorInDB = getIntent().getStringExtra("favouriteActor");
        passwordInDB = getIntent().getStringExtra("password");

        favouriteColourText = findViewById(R.id.forget_password_favourite_colour);
        favouriteFoodText = findViewById(R.id.forget_password_favourite_food);
        favouriteActorText = findViewById(R.id.forget_password_favourite_actor);
        verifyUserBtn = findViewById(R.id.forget_password_verify_btn);
        goToLoginBtn = findViewById(R.id.forget_password_login_btn);
        passwordText = findViewById(R.id.forget_password_password_text);
        progressBar = findViewById(R.id.forget_password_progress_bar);

        progressBar.setVisibility(View.GONE);
        passwordText.setVisibility(View.GONE);
        goToLoginBtn.setVisibility(View.GONE);

        goToLogin();
        verifyUser();
    }

    private void verifyUser() {
        verifyUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!validateFavouriteFood() | !validateFavouriteColour() | !validateFavouriteActor()) {
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                progressBar.setVisibility(View.INVISIBLE);
                favouriteActorText.setVisibility(View.INVISIBLE);
                favouriteColourText.setVisibility(View.INVISIBLE);
                favouriteFoodText.setVisibility(View.INVISIBLE);
                verifyUserBtn.setVisibility(View.INVISIBLE);

                passwordText.setVisibility(View.VISIBLE);
                goToLoginBtn.setVisibility(View.VISIBLE);
                passwordText.setText(passwordInDB);
            }
        });
    }

    private boolean validateFavouriteActor() {
        String val = favouriteActorText.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            favouriteActorText.setError("Field can't be empty");
            return false;
        } else if(val.equals(favouriteActorInDB)){
            favouriteActorText.setError(null);
            favouriteActorText.setErrorEnabled(false);
            return true;
        } else {
            favouriteActorText.setError("Favourite Actor is Wrong");
            return false;
        }
    }

    private boolean validateFavouriteColour() {
        String val = favouriteColourText.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            favouriteColourText.setError("Field can't be empty");
            return false;
        } else if(val.equals(favouriteColourInDB)){
            favouriteColourText.setError(null);
            favouriteColourText.setErrorEnabled(false);
            return true;
        } else {
            favouriteColourText.setError("Favourite Colour is Wrong");
            return false;
        }
    }

    private boolean validateFavouriteFood() {
        String val = favouriteFoodText.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            favouriteFoodText.setError("Field can't be empty");
            return false;
        } else if(val.equals(favouriteFoodInDB)){
            favouriteFoodText.setError(null);
            favouriteFoodText.setErrorEnabled(false);
            return true;
        } else {
            favouriteFoodText.setError("Favourite Food is Wrong");
            return false;
        }
    }

    private void goToLogin() {
        goToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}

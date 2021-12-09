package com.bhaskar.attendancetracker;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {

    private TextInputLayout regName, regUsername, regEmail, regPhoneNo, regPassword, userRolePassword, regFavouriteColour, regFavouriteFood, regFavouriteActor;
    private Button regBtn, regToLoginBtn;
    private TextView welcomeText, sloganText;
    private Spinner userRoleSpinner;
    private LinearLayout userRolePasswordLinearLayout;
    private String selectedUserRole;
    private ProgressBar progressBar;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        regName = findViewById(R.id.reg_name);
        regUsername = findViewById(R.id.reg_username);
        regEmail = findViewById(R.id.reg_email);
        regPhoneNo = findViewById(R.id.reg_phoneNo);
        regPassword = findViewById(R.id.reg_password);
        regBtn = findViewById(R.id.reg_btn);
        regToLoginBtn = findViewById(R.id.reg_login_btn);
        welcomeText = findViewById(R.id.welcome_text);
        sloganText = findViewById(R.id.slogan_name);
        userRoleSpinner = findViewById(R.id.user_role_spinner);
        userRolePasswordLinearLayout = findViewById(R.id.user_role_password_linear_layout);
        userRolePassword = findViewById(R.id.user_role_password);
        regFavouriteColour = findViewById(R.id.reg_favourite_colour);
        regFavouriteFood = findViewById(R.id.reg_favourite_food);
        regFavouriteActor = findViewById(R.id.reg_favourite_actor);
        progressBar = findViewById(R.id.sign_up_progress_bar);
        progressBar.setVisibility(View.GONE);

        spinnerItemSelect();

    }

    private void spinnerItemSelect() {
        userRoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getId() == R.id.user_role_spinner) {
                    //String selectedUserRole = parent.getSelectedItem().toString();
                    selectedUserRole = parent.getItemAtPosition(position).toString();
                    if (!selectedUserRole.equals("site_engineer")) {
                        userRolePasswordLinearLayout.setVisibility(View.VISIBLE);
                    } else {
                        userRolePasswordLinearLayout.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean validateName() {
        String val = regName.getEditText().getText().toString();
        if (val.isEmpty()) {
            regName.setError("Field can't be empty");
        } else {
            regName.setError(null);
            regName.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private boolean validateUsername() {
        String val = regUsername.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";
        if (val.isEmpty()) {
            regUsername.setError("Field can't be empty");
        } else if (val.length() >= 15) {
            regUsername.setError("Length is too long");
        } else if (!val.matches(noWhiteSpace)) {
            regUsername.setError("White Spaces are not allowed");
        } else {
            regUsername.setError(null);
            regUsername.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private boolean validateEmail() {
        String val = regEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            regEmail.setError("Field can't be empty");
        } else if (!val.matches(emailPattern)) {
            regEmail.setError("Invalid email address");
        } else {
            regEmail.setError(null);
            regEmail.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private boolean validatePhoneNo() {
        String val = regPhoneNo.getEditText().getText().toString();
        if (val.isEmpty()) {
            regPhoneNo.setError("Field can't be empty");
        } else if (val.length()!=10) {
            regPhoneNo.setError("Enter 10 digit phone no");
        } else {
            regPhoneNo.setError(null);
            regPhoneNo.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private boolean validatePassword() {
        String val = regPassword.getEditText().getText().toString();
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
            regPassword.setError("Field can't be empty");
        } else if (!val.matches(passwordVal)) {
            regPassword.setError("Password too weak!");
        } else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    public void registerUser(View view) {
        progressBar.setVisibility(View.VISIBLE);
        if (!validateName() | !validateUsername() | !validateEmail() | !validatePhoneNo() | !validatePassword() | !validateUserRole() |
                !validateFavouriteFood() | !validateFavouriteColour() | !validateFavouriteActor()) {
            progressBar.setVisibility(View.GONE);
            return;
        }
        isUser();
    }

    private boolean validateFavouriteActor() {
        String val = regFavouriteActor.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            regFavouriteActor.setError("Field can't be empty");
            return false;
        } else {
            regFavouriteActor.setError(null);
            regFavouriteActor.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateFavouriteColour() {
        String val = regFavouriteColour.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            regFavouriteColour.setError("Field can't be empty");
            return false;
        } else {
            regFavouriteColour.setError(null);
            regFavouriteColour.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateFavouriteFood() {
        String val = regFavouriteFood.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            regFavouriteFood.setError("Field can't be empty");
            return false;
        } else {
            regFavouriteFood.setError(null);
            regFavouriteFood.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateUserRole() {
        String userEnteredPassword = userRolePassword.getEditText().getText().toString().trim();
        if (!selectedUserRole.equals("site_engineer")) {
            if (userEnteredPassword.isEmpty()) {
                userRolePassword.setError("Field can't be empty");
            } else {
                userRolePassword.setError(null);
                userRolePassword.setErrorEnabled(false);
                return true;
            }
        } else {
            userRolePassword.setError(null);
            userRolePassword.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private void isUserRolePasswordValid() {
        if (selectedUserRole.equals("site_engineer")) {
            userRolePassword.setError(null);
            userRolePassword.setErrorEnabled(false);
            storeUserDataInDB();
        } else {
            final String userEnteredPassword = userRolePassword.getEditText().getText().toString().trim();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userRolePassword");
            Query checkUser = reference.orderByChild("role").equalTo(selectedUserRole);
            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //Toast.makeText(SignUp.this, "Role Password exist ", Toast.LENGTH_SHORT).show();
                        String rolePasswordInDB = dataSnapshot.child(selectedUserRole).child("password").getValue(String.class);
                        //Toast.makeText(SignUp.this, "Role Password: " +rolePasswordInDB, Toast.LENGTH_SHORT).show();
                        if (userEnteredPassword.equals(rolePasswordInDB)) {
                            userRolePassword.setError(null);
                            userRolePassword.setErrorEnabled(false);
                            storeUserDataInDB();
                        } else {
                            userRolePassword.setError("Wrong Password");
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(SignUp.this, "Invalid Role", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    public void goToLoginPage(View view) {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        Pair[] pairs = new Pair[6];
        pairs[0] = new Pair<View, String>(welcomeText, "tran_welcome");
        pairs[1] = new Pair<View, String>(sloganText, "tran_slogan");
        pairs[2] = new Pair<View, String>(regUsername, "tran_username");
        pairs[3] = new Pair<View, String>(regPassword, "tran_password");
        pairs[4] = new Pair<View, String>(regBtn, "tran_login_signup");
        pairs[5] = new Pair<View, String>(regToLoginBtn, "tran_signup_login");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(SignUp.this, pairs);
            startActivity(intent, activityOptions.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private void isUser() {
        String userEnteredUsername = regUsername.getEditText().getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    regUsername.setError("User Already Exists");
                    progressBar.setVisibility(View.GONE);
                } else {
                    regUsername.setError(null);
                    regUsername.setErrorEnabled(false);
                    isUserRolePasswordValid();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void storeUserDataInDB() {
        String name = regName.getEditText().getText().toString();
        String username = regUsername.getEditText().getText().toString().trim();
        String email = regEmail.getEditText().getText().toString();
        String phoneNo = regPhoneNo.getEditText().getText().toString();
        String password = regPassword.getEditText().getText().toString().trim();
        String favouriteColour = regFavouriteColour.getEditText().getText().toString().trim();
        String favouriteFood = regFavouriteFood.getEditText().getText().toString().trim();
        String favouriteActor = regFavouriteActor.getEditText().getText().toString().trim();

        UserHelperClass userHelperClass = new UserHelperClass(name, username, email, phoneNo, password,selectedUserRole,favouriteColour,favouriteFood,favouriteActor);
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(username).setValue(userHelperClass);
        Toast.makeText(this,"Congratulation! You have successfully Signed Up.",Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);

        Intent intent = new Intent(getApplicationContext(), HomePage.class);
        intent.putExtra("username", username);
        intent.putExtra("role", selectedUserRole);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        /*Intent intent = new Intent(getApplicationContext(), VerifyPhoneNo.class);
        intent.putExtra("name", name);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("phoneNo", phoneNo);
        intent.putExtra("password", password);
        intent.putExtra("role", selectedUserRole);
        intent.putExtra("favouriteColour", favouriteColour);
        intent.putExtra("favouriteFood", favouriteFood);
        intent.putExtra("favouriteActor", favouriteActor);
        startActivity(intent);*/
    }

}

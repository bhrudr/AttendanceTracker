package com.bhaskar.attendancetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class HomePage extends AppCompatActivity {

    private Button signOutBtn, viewProfileBtn, addOrDeleteEmployeeBtn, giveAttendanceBtn, editAttendanceBtn,
            viewAttendanceReportBtn, viewAndAssignEmployeeBtn, approveAttendanceBtn, changeSignUpPasswordBtn, collectAttendanceBtn;
    private String username, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home_page);

        username = getIntent().getStringExtra("username");
        role = getIntent().getStringExtra("role");
        //Toast.makeText(HomePage.this,"welcome "+role,Toast.LENGTH_SHORT).show();

        viewProfileBtn = findViewById(R.id.home_page_view_profile_btn);
        addOrDeleteEmployeeBtn = findViewById(R.id.home_page_add_or_delete_employee_btn);
        giveAttendanceBtn = findViewById(R.id.home_page_give_attendance_btn);
        viewAttendanceReportBtn = findViewById(R.id.home_page_view_attendance_report_btn);
        viewAndAssignEmployeeBtn = findViewById(R.id.home_page_view_and_assign_employee_btn);
        editAttendanceBtn = findViewById(R.id.home_page_edit_attendance_btn);
        approveAttendanceBtn = findViewById(R.id.home_page_approve_attendance_btn);
        signOutBtn = findViewById(R.id.home_page_sign_out_btn);
        changeSignUpPasswordBtn = findViewById(R.id.home_page_cng_sign_up_pwd_btn);
        collectAttendanceBtn = findViewById(R.id.home_page_collect_attendance_btn);

        signOut();
        viewProfile();
        giveAttendance();
        viewAttendanceReport();
        editAttendance();
        if (role.equals("section_incharge") || role.equals("ir_admin")) {
            approveAttendance();
            viewAllEmployee();
        }
        if (role.equals("section_incharge")) {
            addOrDeleteEmployeeBtn.setVisibility(View.INVISIBLE);
            changeSignUpPasswordBtn.setVisibility(View.INVISIBLE);
            collectAttendanceBtn.setVisibility(View.INVISIBLE);
        } else if (role.equals("ir_admin")) {
            collectAttendance();
            addOrDeleteEmployee();
            changeSignUpPassword();
        } else {
            approveAttendanceBtn.setVisibility(View.INVISIBLE);
            viewAndAssignEmployeeBtn.setVisibility(View.INVISIBLE);
            addOrDeleteEmployeeBtn.setVisibility(View.INVISIBLE);
            changeSignUpPasswordBtn.setVisibility(View.INVISIBLE);
            collectAttendanceBtn.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Exit ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HomePage.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void collectAttendance() {
        collectAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CollectAttendance.class);
                intent.putExtra("username", username);
                intent.putExtra("role", role);
                startActivity(intent);
                //finish();
            }
        });
    }

    private void changeSignUpPassword() {
        changeSignUpPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeSignUpPassword.class);
                intent.putExtra("username", username);
                intent.putExtra("role", role);
                startActivity(intent);
                //finish();
            }
        });
    }

    private void signOut() {
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void approveAttendance() {
        approveAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ApproveAttendance.class);
                intent.putExtra("username", username);
                intent.putExtra("role", role);
                startActivity(intent);
                //finish();
            }
        });
    }

    private void editAttendance() {
        editAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditAttendance.class);
                intent.putExtra("username", username);
                intent.putExtra("role", role);
                startActivity(intent);
                //finish();
            }
        });
    }

    private void viewProfile() {
        viewProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewProfile.class);
                intent.putExtra("username", username);
                intent.putExtra("role", role);
                startActivity(intent);
                //finish();
            }
        });
    }

    private void addOrDeleteEmployee() {
        addOrDeleteEmployeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddEmployee.class);
                intent.putExtra("username", username);
                intent.putExtra("role", role);
                startActivity(intent);
                //finish();
            }
        });
    }

    private void giveAttendance() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        final String todayDate = dayOfMonth + "_" + (month + 1) + "_" + year;
        giveAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GiveAttendance.class);
                intent.putExtra("username", username);
                intent.putExtra("role", role);
                intent.putExtra("date", todayDate);
                startActivity(intent);
                //finish();
            }
        });
    }

    private void viewAttendanceReport() {
        viewAttendanceReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewAttendanceReport.class);
                intent.putExtra("username", username);
                intent.putExtra("role", role);
                startActivity(intent);
                //finish();
            }
        });
    }

    private void viewAllEmployee() {
        viewAndAssignEmployeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewAllEmployee.class);
                intent.putExtra("username", username);
                intent.putExtra("role", role);
                startActivity(intent);
                //finish();
            }
        });
    }
}

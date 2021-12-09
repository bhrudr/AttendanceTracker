package com.bhaskar.attendancetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteEmployee extends AppCompatActivity {

    private TextView employeeNameText, tokenNoText, workUnderText;
    private Button goToHomePageBtn, deleteEmployeeBtn;
    private String username, role, employeeName, tokenNo, workUnder;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_employee);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        username = getIntent().getStringExtra("username");
        role = getIntent().getStringExtra("role");
        employeeName = getIntent().getStringExtra("employeeName");
        tokenNo = getIntent().getStringExtra("tokenNo");
        workUnder = getIntent().getStringExtra("workUnder");

        reference = FirebaseDatabase.getInstance().getReference("employees");

        goToHomePageBtn = findViewById(R.id.delete_employee_go_to_home_page_btn);
        deleteEmployeeBtn = findViewById(R.id.delete_employee_btn);
        employeeNameText = findViewById(R.id.delete_employee_name_text);
        tokenNoText = findViewById(R.id.delete_employee_token_no_text);
        workUnderText = findViewById(R.id.delete_employee_work_under_text);

        employeeNameText.setText(employeeName);
        tokenNoText.setText(tokenNo);
        workUnderText.setText(workUnder);

        goToHomePage();
        deleteEmployee();
    }

    private void deleteEmployee() {
        deleteEmployeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operComfirmDialogue();
            }
        });
    }

    private void operComfirmDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Delete Employee Details?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reference.child(tokenNo).setValue(null);
                        Toast.makeText(DeleteEmployee.this, "Employee Deleted Successfully!!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), AddEmployee.class);
                        intent.putExtra("username", username);
                        intent.putExtra("role", role);
                        startActivity(intent);
                        finish();
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

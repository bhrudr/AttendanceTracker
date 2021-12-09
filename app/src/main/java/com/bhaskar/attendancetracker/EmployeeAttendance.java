package com.bhaskar.attendancetracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.util.Calendar;

public class EmployeeAttendance extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView selectDateText, attendanceTimeReportedText, attendanceTimeReleasedText,
            attendanceTimeOTRequisitionFromText, attendanceTimeOTRequisitionToText, attendanceEmployeeName, attendanceEmployeeTokenNo;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeSetListenerReported, onTimeSetListenerReleased,
            onTimeSetListenerOTRequisitionFrom, onTimeSetListenerOTRequisitionTo;
    private Spinner attendanceTypeSpinner;
    private LinearLayout attendanceTimeLinearLayout, attendanceTimeReleasedLinearLayout;
    private String username, role, employeeName, tokenNo, date, todayDate, attendanceType, description,
            attendanceTimeReported, attendanceTimeReleased, attendanceTimeOTRequisitionFrom, attendanceTimeOTRequisitionTo;
    private TextInputLayout attendanceDesc;
    private Button attendanceSubmitBtn;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_attendance);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        username = getIntent().getStringExtra("username");
        role = getIntent().getStringExtra("role");
        employeeName = getIntent().getStringExtra("employeeName");
        tokenNo = getIntent().getStringExtra("tokenNo");
        date = getIntent().getStringExtra("date");


        selectDateText = findViewById(R.id.attendance_date_text);
        attendanceTimeReportedText = findViewById(R.id.attendance_time_reported_text);
        attendanceTimeReleasedText = findViewById(R.id.attendance_time_released_text);
        attendanceTimeOTRequisitionFromText = findViewById(R.id.attendance_time_ot_requisition_from_text);
        attendanceTimeOTRequisitionToText = findViewById(R.id.attendance_time_ot_requisition_to_text);
        attendanceTypeSpinner = findViewById(R.id.attendance_type_spinner);
        attendanceTimeLinearLayout = findViewById(R.id.attendance_time_linear_layout);
        attendanceTimeReleasedLinearLayout = findViewById(R.id.attendance_time_released_layout);
        attendanceEmployeeName = findViewById(R.id.attendance_employee_name_text);
        attendanceEmployeeTokenNo = findViewById(R.id.attendance_token_no_text);
        attendanceDesc = findViewById(R.id.attendance_desc);
        attendanceSubmitBtn = findViewById(R.id.attendance_submit_btn);
        progressBar = findViewById(R.id.employee_attendance_progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        attendanceTimeReleasedLinearLayout.setVisibility(View.INVISIBLE);

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        todayDate = dayOfMonth + "_" + (month + 1) + "_" + year;

        selectDateText.setText(date);
        attendanceEmployeeName.setText(employeeName);
        attendanceEmployeeTokenNo.setText(tokenNo);

        populateAttendanceSpinner();
        attendanceTypeSpinner.setOnItemSelectedListener(this);

        reportedTimePicker();
        releasedTimePicker();
        otRequisitionFromTimePicker();
        otRequisitionToTimePicker();
        if (!date.equals(todayDate)) {
            attendanceTimeReleasedText.setText("19:0");
            attendanceTimeReleasedLinearLayout.setVisibility(View.VISIBLE);
            attendanceTimeLinearLayout.setVisibility(View.INVISIBLE);
            attendanceTypeSpinner.setVisibility(View.INVISIBLE);
            attendanceDesc.setVisibility(View.INVISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void reportedTimePicker() {

        attendanceTimeReportedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(EmployeeAttendance.this,
                        onTimeSetListenerReported,
                        hourOfDay, minute, true);
                dialog.show();
            }
        });
        onTimeSetListenerReported = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String selectedTime = hourOfDay + ":" + minute;
                attendanceTimeReportedText.setText(selectedTime);
            }
        };
    }

    private void releasedTimePicker() {
        attendanceTimeReleasedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(EmployeeAttendance.this,
                        onTimeSetListenerReleased,
                        hourOfDay, minute, true);
                dialog.show();
            }
        });
        onTimeSetListenerReleased = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String selectedTime = hourOfDay + ":" + minute;
                attendanceTimeReleasedText.setText(selectedTime);
            }
        };
    }

    private void otRequisitionFromTimePicker() {
        attendanceTimeOTRequisitionFromText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(EmployeeAttendance.this,
                        onTimeSetListenerOTRequisitionFrom,
                        hourOfDay, minute, true);
                dialog.show();
            }
        });
        onTimeSetListenerOTRequisitionFrom = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String selectedTime = hourOfDay + ":" + minute;
                attendanceTimeOTRequisitionFromText.setText(selectedTime);
            }
        };
    }

    private void otRequisitionToTimePicker() {
        attendanceTimeOTRequisitionToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(EmployeeAttendance.this,
                        onTimeSetListenerOTRequisitionTo,
                        hourOfDay, minute, true);
                dialog.show();
            }
        });
        onTimeSetListenerOTRequisitionTo = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String selectedTime = hourOfDay + ":" + minute;
                attendanceTimeOTRequisitionToText.setText(selectedTime);
            }
        };
    }

    private void populateAttendanceSpinner() {
        /*ArrayAdapter<String> attendanceTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_selectable_list_item,
                getResources().getStringArray(R.array.attendance_spinner));*/
        //attendanceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> attendanceTypeAdapter = new ArrayAdapter<>(this,
                R.layout.attedance_type_spinner_layout,
                getResources().getStringArray(R.array.attendance_spinner));
        attendanceTypeAdapter.setDropDownViewResource(R.layout.attendance_type_spinner_dropdown_layout);
        attendanceTypeSpinner.setAdapter(attendanceTypeAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.attendance_type_spinner) {
            //String selectedAttendanceType = parent.getSelectedItem().toString();
            attendanceType = parent.getItemAtPosition(position).toString();
            if (date.equals(todayDate)) {
                if (!attendanceType.equals("present")) {
                    //Toast.makeText(this, "Selected " + selectedAttendanceType, Toast.LENGTH_SHORT).show();
                    attendanceTimeLinearLayout.setVisibility(View.INVISIBLE);
                    attendanceTimeReportedText.setText("NA");
                    attendanceTimeReleasedText.setText("NA");
                    attendanceTimeOTRequisitionFromText.setText("NA");
                    attendanceTimeOTRequisitionToText.setText("NA");
                } else {
                    attendanceTimeLinearLayout.setVisibility(View.VISIBLE);
                    attendanceTimeReportedText.setText("8:0");
                    attendanceTimeOTRequisitionFromText.setText("17:0");
                    attendanceTimeOTRequisitionToText.setText("19:0");
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void doSubmit(View view) {
        progressBar.setVisibility(View.VISIBLE);
        if (!date.equals(todayDate)) {
            openUpdateComfirmDialogue();
        } else {
            if (!validateAttendanceTime() | !validateDescription()) {
                progressBar.setVisibility(View.GONE);
                return;
            } else {
                //Toast.makeText(this, "Data Can be stored", Toast.LENGTH_SHORT).show();
                isDataExist();
            }
        }
        progressBar.setVisibility(View.GONE);
    }

    private boolean validateAttendanceTime() {
        attendanceTimeReported = attendanceTimeReportedText.getText().toString();
        attendanceTimeReleased = attendanceTimeReleasedText.getText().toString();
        attendanceTimeOTRequisitionFrom = attendanceTimeOTRequisitionFromText.getText().toString();
        attendanceTimeOTRequisitionTo = attendanceTimeOTRequisitionToText.getText().toString();
        if (attendanceTimeReported.equals("NA") && attendanceTimeReleased.equals("NA")
                && attendanceTimeOTRequisitionFrom.equals("NA") && attendanceTimeOTRequisitionTo.equals("NA")) {
            return true;
        } else if (!attendanceTimeReported.equals("NA") && !attendanceTimeReleased.equals("NA")
                && !attendanceTimeOTRequisitionFrom.equals("NA") && !attendanceTimeOTRequisitionTo.equals("NA")) {
            return true;
        } else if (!attendanceTimeReported.equals("NA") && !attendanceTimeOTRequisitionFrom.equals("NA")
                && !attendanceTimeOTRequisitionTo.equals("NA")) {
            return true;
        } else {
            Toast.makeText(this, "Other Time Fields are mandatory", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean validateDescription() {
        description = attendanceDesc.getEditText().getText().toString();
        if (description.isEmpty()) {
            attendanceDesc.setError("Description can not be Empty");
            return false;
        } else {
            attendanceDesc.setError(null);
            attendanceDesc.setErrorEnabled(false);
            return true;
        }
    }

    private void storeDataInDB() {
        if (employeeName.isEmpty() || username.isEmpty() || tokenNo.isEmpty() || date.isEmpty()
                || attendanceType.isEmpty() || description.isEmpty() || attendanceTimeReported.isEmpty()
                || attendanceTimeReleased.isEmpty() || attendanceTimeOTRequisitionFrom.isEmpty() || attendanceTimeOTRequisitionTo.isEmpty()) {
            Toast.makeText(this, "Field can't be empty!!", Toast.LENGTH_SHORT).show();
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference(date);
            String status = "UNAPPROVED", approvedBy = "NA", editedBy = "NA", collectedBy = "NA";
            Attendance attendance = new Attendance(employeeName, tokenNo, username, date, attendanceType,
                    attendanceTimeReported, attendanceTimeReleased, attendanceTimeOTRequisitionFrom,
                    attendanceTimeOTRequisitionTo, description, username, approvedBy, status, editedBy, collectedBy);
            databaseReference.child(tokenNo).setValue(attendance);

            databaseReference = FirebaseDatabase.getInstance().getReference("employees");
            databaseReference.child(tokenNo).child("attendanceLastGivenOn").setValue(date);

            Toast.makeText(this, "Congratulation! You have successfully Submitted the Attendance.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), GiveAttendance.class);
            intent.putExtra("username", username);
            intent.putExtra("role", role);
            intent.putExtra("date", date);
            startActivity(intent);
            //finish();
        }
    }

    private void isDataExist() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(date);
        Query checkUser = reference.orderByChild("tokenNo").equalTo(tokenNo);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(EmployeeAttendance.this, "Data Already Submitted", Toast.LENGTH_SHORT).show();
                } else {
                    openComfirmDialogue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void openComfirmDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to SUBMIT?")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        storeDataInDB();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openUpdateComfirmDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to SUBMIT?")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateDataInDB();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateDataInDB() {
        attendanceTimeReleased = attendanceTimeReleasedText.getText().toString();
        databaseReference = FirebaseDatabase.getInstance().getReference(date);
        databaseReference.child(tokenNo).child("releasedTime").setValue(attendanceTimeReleased);

        Toast.makeText(this, "Congratulation! You have successfully Submitted the Attendance.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(), GiveAttendance.class);
        intent.putExtra("username", username);
        intent.putExtra("role", role);
        intent.putExtra("date", date);
        startActivity(intent);
    }
}

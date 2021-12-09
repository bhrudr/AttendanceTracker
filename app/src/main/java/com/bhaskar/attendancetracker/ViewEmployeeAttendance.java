package com.bhaskar.attendancetracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class ViewEmployeeAttendance extends AppCompatActivity {
    private TextView selectDateText, attendanceTimeReportedText, attendanceTimeReleasedText,
            attendanceTimeOTRequisitionFromText, attendanceTimeOTRequisitionToText, attendanceEmployeeName,
            attendanceEmployeeTokenNo;
    private TextInputLayout attendanceDescTextInputLayout;
    private Spinner attendanceTypeSpinner;
    private String username, role, employeeName, tokenNo, date, selectedAttendanceType;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeSetListenerReported, onTimeSetListenerReleased,
            onTimeSetListenerOTRequisitionFrom, onTimeSetListenerOTRequisitionTo;
    private Button updateAttendanceBtn;
    private LinearLayout attendanceTimeLinearLayout;
    private DatabaseReference reference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_employee_attendance);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        username = getIntent().getStringExtra("username");
        role = getIntent().getStringExtra("role");
        employeeName = getIntent().getStringExtra("employeeName");
        tokenNo = getIntent().getStringExtra("tokenNo");
        date = getIntent().getStringExtra("date");

        selectDateText = findViewById(R.id.view_employee_attendance_date_text);
        attendanceTimeReportedText = findViewById(R.id.view_employee_attendance_time_reported_text);
        attendanceTimeReleasedText = findViewById(R.id.view_employee_attendance_time_released_text);
        attendanceTimeOTRequisitionFromText = findViewById(R.id.view_employee_attendance_time_ot_requi_from_text);
        attendanceTimeOTRequisitionToText = findViewById(R.id.view_employee_attendance_time_ot_requi_to_text);
        attendanceTypeSpinner = findViewById(R.id.view_employee_attendance_attendance_type_spinner);
        attendanceEmployeeName = findViewById(R.id.view_employee_attendance_name_text);
        attendanceEmployeeTokenNo = findViewById(R.id.view_employee_attendance_token_no_text);
        attendanceDescTextInputLayout = findViewById(R.id.view_employee_attendance_attendance_desc);
        updateAttendanceBtn = findViewById(R.id.view_employee_attendance_update_btn);
        attendanceTimeLinearLayout = findViewById(R.id.view_employee_attendance_attendance_time_linear_layout);
        progressBar = findViewById(R.id.view_employee_attendance_progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        selectDateText.setText(date);
        attendanceEmployeeName.setText(employeeName);
        attendanceEmployeeTokenNo.setText(tokenNo);

        populateAttendanceSpinner();
        reportedTimePicker();
        releasedTimePicker();
        otRequisitionFromTimePicker();
        otRequisitionToTimePicker();
        viewAttendance();
        updateAttendance();
        progressBar.setVisibility(View.GONE);
    }

    private void updateAttendance() {
        updateAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
                if (!validateAttendanceTime() | !validateDescription()) {

                } else {
                    isDataExist();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean validateAttendanceTime() {
        String attendanceTimeReported = attendanceTimeReportedText.getText().toString();
        String attendanceTimeReleased = attendanceTimeReleasedText.getText().toString();
        String attendanceTimeOTRequisitionFrom = attendanceTimeOTRequisitionFromText.getText().toString();
        String attendanceTimeOTRequisitionTo = attendanceTimeOTRequisitionToText.getText().toString();
        if (attendanceTimeReported.equals("NA") && attendanceTimeReleased.equals("NA")
                && attendanceTimeOTRequisitionFrom.equals("NA") && attendanceTimeOTRequisitionTo.equals("NA")) {
            return true;
        } else if (!attendanceTimeReported.equals("NA") && !attendanceTimeReleased.equals("NA")
                && !attendanceTimeOTRequisitionFrom.equals("NA") && !attendanceTimeOTRequisitionTo.equals("NA")) {
            return true;
        } else {
            Toast.makeText(this, "Other Time Fields are mandatory", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean validateDescription() {
        String description = attendanceDescTextInputLayout.getEditText().getText().toString();
        if (description.isEmpty()) {
            attendanceDescTextInputLayout.setError("Description can not be Empty");
            return false;
        } else {
            attendanceDescTextInputLayout.setError(null);
            attendanceDescTextInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    private void isDataExist() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(date);
        Query checkUser = reference.orderByChild("tokenNo").equalTo(tokenNo);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String statusInDB = dataSnapshot.child(tokenNo).child("status").getValue(String.class);
                    String releasedTimeInDB = dataSnapshot.child(tokenNo).child("releasedTime").getValue(String.class);
                    String attendanceTypeInDB = dataSnapshot.child(tokenNo).child("attendanceType").getValue(String.class);
                    String reportedTimeInDB = dataSnapshot.child(tokenNo).child("reportedTime").getValue(String.class);
                    String otRequisitionFromTimeInDB = dataSnapshot.child(tokenNo).child("otRequisitionFromTime").getValue(String.class);
                    String otRequisitionToTimeInDB = dataSnapshot.child(tokenNo).child("otRequisitionToTime").getValue(String.class);
                    String descriptionInDB = dataSnapshot.child(tokenNo).child("description").getValue(String.class);
                    String editedByInDB = dataSnapshot.child(tokenNo).child("editedBy").getValue(String.class);

                    boolean dataChanged = false;

                    if(isReleaseTimeChanged(releasedTimeInDB)) dataChanged = true;
                    if(isAttendenceTypeChanged(attendanceTypeInDB)) dataChanged = true;
                    if(isReportedTimeChanged(reportedTimeInDB)) dataChanged = true;
                    if(isOtRequisitionFromTimeChanged(otRequisitionFromTimeInDB)) dataChanged = true;
                    if(isOtRequisitionToTimeChanged(otRequisitionToTimeInDB)) dataChanged = true;
                    if(isDescriptionChanged(descriptionInDB)) dataChanged = true;

                    if (dataChanged) {
                        changeEditedBy(editedByInDB);
                        if (statusInDB.equals("UNAPPROVED")) {
                            changeStatus();
                        } else if(statusInDB.equals("APPROVED")){
                            changeApprovedBy();
                        }
                        Toast.makeText(ViewEmployeeAttendance.this, "Data updated Successfully!!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ViewAttendanceReport.class);
                        intent.putExtra("username", username);
                        intent.putExtra("role", role);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ViewEmployeeAttendance.this, "Data is Same, can't be updated.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ViewEmployeeAttendance.this, "No Data Found. Nothing to Update.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void changeApprovedBy() {
        reference = FirebaseDatabase.getInstance().getReference(date);
        reference.child(tokenNo).child("approvedBy").setValue(username);
    }

    private void changeStatus() {
        reference = FirebaseDatabase.getInstance().getReference(date);
        reference.child(tokenNo).child("status").setValue("APPROVED");
        reference.child(tokenNo).child("approvedBy").setValue(username);
    }

    private void changeEditedBy(String editedByInDB) {
        if(!editedByInDB.equals(username)) {
            reference = FirebaseDatabase.getInstance().getReference(date);
            reference.child(tokenNo).child("editedBy").setValue(username);
        }
    }

    private boolean isDescriptionChanged(String descriptionInDB) {
        String description = attendanceDescTextInputLayout.getEditText().getText().toString();
        if (description.equals(descriptionInDB)) {
            return false;
        } else {
            reference = FirebaseDatabase.getInstance().getReference(date);
            reference.child(tokenNo).child("description").setValue(description);
            return true;
        }
    }

    private boolean isOtRequisitionToTimeChanged(String otRequisitionToTimeInDB) {
        String otRequisitionToTime = attendanceTimeOTRequisitionToText.getText().toString();
        if (otRequisitionToTime.equals(otRequisitionToTimeInDB)) {
            return false;
        } else {
            reference = FirebaseDatabase.getInstance().getReference(date);
            reference.child(tokenNo).child("otRequisitionToTime").setValue(otRequisitionToTime);
            return true;
        }
    }

    private boolean isOtRequisitionFromTimeChanged(String otRequisitionFromTimeInDB) {
        String otRequisitionFromTime = attendanceTimeOTRequisitionFromText.getText().toString();
        if (otRequisitionFromTime.equals(otRequisitionFromTimeInDB)) {
            return false;
        } else {
            reference = FirebaseDatabase.getInstance().getReference(date);
            reference.child(tokenNo).child("otRequisitionFromTime").setValue(otRequisitionFromTime);
            return true;
        }
    }

    private boolean isReportedTimeChanged(String reportedTimeInDB) {
        String reportedTime = attendanceTimeReportedText.getText().toString();
        if (reportedTime.equals(reportedTimeInDB)) {
            return false;
        } else {
            reference = FirebaseDatabase.getInstance().getReference(date);
            reference.child(tokenNo).child("reportedTime").setValue(reportedTime);
            return true;
        }
    }

    private boolean isAttendenceTypeChanged(String attendanceTypeInDB) {
        if (selectedAttendanceType.equals(attendanceTypeInDB)) {
            return false;
        } else {
            reference = FirebaseDatabase.getInstance().getReference(date);
            reference.child(tokenNo).child("attendanceType").setValue(selectedAttendanceType);
            return true;
        }
    }

    private boolean isReleaseTimeChanged(String releasedTimeInDB) {
        String releasedTime = attendanceTimeReleasedText.getText().toString();
        if (releasedTime.equals(releasedTimeInDB)) {
            return false;
        } else {
            reference = FirebaseDatabase.getInstance().getReference(date);
            reference.child(tokenNo).child("releasedTime").setValue(releasedTime);
            return true;
        }
    }

    private void reportedTimePicker() {

        attendanceTimeReportedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(ViewEmployeeAttendance.this,
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
                TimePickerDialog dialog = new TimePickerDialog(ViewEmployeeAttendance.this,
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
                TimePickerDialog dialog = new TimePickerDialog(ViewEmployeeAttendance.this,
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
                TimePickerDialog dialog = new TimePickerDialog(ViewEmployeeAttendance.this,
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

        attendanceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getId() == R.id.view_employee_attendance_attendance_type_spinner) {
                    //String selectedAttendanceType = parent.getSelectedItem().toString();
                    selectedAttendanceType = parent.getItemAtPosition(position).toString();
                    if (!selectedAttendanceType.equals("present")) {
                        //Toast.makeText(this, "Selected " + selectedAttendanceType, Toast.LENGTH_SHORT).show();
                        attendanceTimeLinearLayout.setVisibility(View.INVISIBLE);
                        attendanceTimeReportedText.setText("NA");
                        attendanceTimeReleasedText.setText("NA");
                        attendanceTimeOTRequisitionFromText.setText("NA");
                        attendanceTimeOTRequisitionToText.setText("NA");
                    } else {
                        attendanceTimeLinearLayout.setVisibility(View.VISIBLE);
                        //attendanceTimeReportedText.setText("NA");
                        //attendanceTimeReleasedText.setText("NA");
                        //attendanceTimeOTRequisitionFromText.setText("NA");
                        //attendanceTimeOTRequisitionToText.setText("NA");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void viewAttendance() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(date);

        Query checkUser = reference.orderByChild("tokenNo").equalTo(tokenNo);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Toast.makeText(ViewEmployeeAttendance.this, "Data Found!!", Toast.LENGTH_SHORT).show();
                    String attendanceType = dataSnapshot.child(tokenNo).child("attendanceType").getValue(String.class);
                    String reportedTime = dataSnapshot.child(tokenNo).child("reportedTime").getValue(String.class);
                    String releasedTime = dataSnapshot.child(tokenNo).child("releasedTime").getValue(String.class);
                    String otRequisitionFromTime = dataSnapshot.child(tokenNo).child("otRequisitionFromTime").getValue(String.class);
                    String otRequisitionToTime = dataSnapshot.child(tokenNo).child("otRequisitionToTime").getValue(String.class);
                    String description = dataSnapshot.child(tokenNo).child("description").getValue(String.class);
                    if (attendanceType.equals("absent"))
                        attendanceTypeSpinner.setSelection(1);
                    else if (attendanceType.equals("onLeave"))
                        attendanceTypeSpinner.setSelection(2);
                    else if (attendanceType.equals("paidLeave"))
                        attendanceTypeSpinner.setSelection(3);
                    else if (attendanceType.equals("weeklyOff"))
                        attendanceTypeSpinner.setSelection(4);
                    attendanceTimeReleasedText.setText(releasedTime);
                    attendanceDescTextInputLayout.getEditText().setText(description);
                    attendanceTimeReportedText.setText(reportedTime);
                    attendanceTimeOTRequisitionFromText.setText(otRequisitionFromTime);
                    attendanceTimeOTRequisitionToText.setText(otRequisitionToTime);
                } else {
                    Toast.makeText(ViewEmployeeAttendance.this, "Data Not Found!!", Toast.LENGTH_SHORT).show();
                    attendanceDescTextInputLayout.getEditText().setText("");
                    attendanceTimeReportedText.setText("NA");
                    attendanceTimeReleasedText.setText("NA");
                    attendanceTimeOTRequisitionFromText.setText("NA");
                    attendanceTimeOTRequisitionToText.setText("NA");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

package com.bhaskar.attendancetracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ApproveAttendance extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttendanceAdapter adapter;
    private TextInputLayout searchText;
    private TextView selectDateText;
    private Button goToHomePageBtn;
    private String username, role, selectedDate;
    private List<Attendance> attendanceList, filteredAttendanceList;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private ProgressBar progressBar;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_attendance);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar = findViewById(R.id.approve_attendance_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        username = getIntent().getStringExtra("username");
        role = getIntent().getStringExtra("role");
        filteredAttendanceList = new ArrayList<Attendance>();
        attendanceList = new ArrayList<Attendance>();

        recyclerView = findViewById(R.id.approve_attendance_recyclerView);
        searchText = findViewById(R.id.approve_attendance_search_text);
        selectDateText = findViewById(R.id.approve_attendance_date_text);
        goToHomePageBtn = findViewById(R.id.approve_attendance_go_to_home_page_btn);
        goToHomePage();
        attendanceReportDatePicker();
        //AttendanceRecycler();
        searchAttendance();
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


    private void searchAttendance() {
        searchText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterAttendanceList(s.toString());
            }
        });
    }

    private void filterAttendanceList(String text) {
        filteredAttendanceList = new ArrayList<Attendance>();
        for (Attendance item : attendanceList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredAttendanceList.add(item);
            }
        }
        adapter.filterAttendanceList(filteredAttendanceList);
    }

    private void goToHomePage() {
        goToHomePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                intent.putExtra("username", username);
                intent.putExtra("role", role);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void attendanceReportDatePicker() {
        final Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        selectedDate = dayOfMonth + "_" + (month + 1) + "_" + year;
        //selectDateText.setText(selectedDate);
        selectDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(ApproveAttendance.this,
                        R.style.Theme_AppCompat_DayNight_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, dayOfMonth);
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                long today = cal.getTimeInMillis();
                final long oneDay = 24 * 60 * 60 * 1000L;
                dialog.getDatePicker().setMinDate(today - oneDay*2);
                dialog.getDatePicker().setMaxDate(today - oneDay);
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                selectedDate = dayOfMonth + "_" + month + "_" + year;
                selectDateText.setText(selectedDate);
                AttendanceRecycler();
            }
        };
    }

    private void AttendanceRecycler() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        attendanceList = new ArrayList<>();
        adapter = new AttendanceAdapter(attendanceList);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(selectedDate);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    //Toast.makeText(ViewAttendanceReport.this, "Data Found", Toast.LENGTH_SHORT).show();
                    Attendance attendance = dataSnapshot.getValue(Attendance.class);
                    if (attendance != null && attendance.getStatus().equals("UNAPPROVED")) {
                        if(!(attendance.getAttendanceType().equals("present") && attendance.getReleasedTime().equals("NA"))) {
                            adapter.notifyDataSetChanged();
                            attendanceList.add(attendance);
                            filteredAttendanceList.add(attendance);
                        }
                    }
                } else {
                    Toast.makeText(ApproveAttendance.this, "Data Not Found", Toast.LENGTH_SHORT).show();
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
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        if (role.equals("ir_admin") || role.equals("section_incharge")) {
            adapter.setOnItemClickListener(new AttendanceAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (!filteredAttendanceList.isEmpty() && filteredAttendanceList.size() > position) {
                        if(filteredAttendanceList.get(position).getStatus().equals("UNAPPROVED")) {
                            operComfirmDialogue(filteredAttendanceList.get(position).getTokenNo());
                        } else{
                            Toast.makeText(ApproveAttendance.this, "Already Approved!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ApproveAttendance.this, "Searched List Fail!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(ApproveAttendance.this, "Role "+role, Toast.LENGTH_SHORT).show();
        }
    }

    private void operComfirmDialogue(final String tokenNo) {
        reference = FirebaseDatabase.getInstance().getReference(selectedDate);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to APPROVE ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reference.child(tokenNo).child("status").setValue("APPROVED");
                        reference.child(tokenNo).child("approvedBy").setValue(username);
                        Toast.makeText(ApproveAttendance.this, "Attendance Approved Successfully!!", Toast.LENGTH_SHORT).show();
                        AttendanceRecycler();
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

}

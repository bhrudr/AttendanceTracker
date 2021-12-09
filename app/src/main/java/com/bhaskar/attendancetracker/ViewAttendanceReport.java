package com.bhaskar.attendancetracker;

import android.app.DatePickerDialog;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ViewAttendanceReport extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttendanceAdapter adapter;
    private TextInputLayout searchText;
    private TextView selectDateText;
    private Button goToHomePageBtn, exportDataBtn;
    private String username, role, selectedDate;
    private List<Attendance> attendanceList, filteredAttendanceList;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance_report);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar = findViewById(R.id.view_attendance_report_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        username = getIntent().getStringExtra("username");
        role = getIntent().getStringExtra("role");
        filteredAttendanceList = new ArrayList<Attendance>();

        recyclerView = findViewById(R.id.view_attendance_report_recyclerView);
        searchText = findViewById(R.id.view_attendance_report_search_text);
        selectDateText = findViewById(R.id.view_attendance_report_date_text);
        goToHomePageBtn = findViewById(R.id.view_attendance_report_go_to_home_page_btn);
        exportDataBtn = findViewById(R.id.view_attendance_report_export_data_btn);
        goToHomePage();
        attendanceReportDatePicker();
        AttendanceRecycler();
        searchAttendance();
        exportData();
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
        selectDateText.setText(selectedDate);
        selectDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(ViewAttendanceReport.this,
                        R.style.Theme_AppCompat_DayNight_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, dayOfMonth);
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                long today = cal.getTimeInMillis();
                //final long oneDay = 24 * 60 * 60 * 1000L;
                //dialog.getDatePicker().setMinDate(today - oneDay*2);
                dialog.getDatePicker().setMaxDate(today);
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
                    if (attendance != null) {
                        adapter.notifyDataSetChanged();
                        attendanceList.add(attendance);
                        filteredAttendanceList.add(attendance);
                    }
                } else {
                    Toast.makeText(ViewAttendanceReport.this, "Data Not Found", Toast.LENGTH_SHORT).show();
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
        if (role.equals("ir_admin")) {
            adapter.setOnItemClickListener(new AttendanceAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (!filteredAttendanceList.isEmpty() && filteredAttendanceList.size() > position) {
                        //adapter.notifyItemChanged(position);
                        if(filteredAttendanceList.get(position).getStatus().equals("COLLECTED")){
                            Toast.makeText(ViewAttendanceReport.this, "Attendance is COLLECTED can't be modified!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), ViewEmployeeAttendance.class);
                            intent.putExtra("username", username);
                            intent.putExtra("role", role);
                            intent.putExtra("employeeName", filteredAttendanceList.get(position).getName());
                            intent.putExtra("tokenNo", filteredAttendanceList.get(position).getTokenNo());
                            intent.putExtra("date", filteredAttendanceList.get(position).getDate());
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(ViewAttendanceReport.this, "Searched List Fail!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void exportData() {
        exportDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                export();
            }
        });
    }

    private void export() {
        progressBar.setVisibility(View.VISIBLE);
        if (attendanceList.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ViewAttendanceReport.this, "Nothing To Export!!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            StringBuilder data = new StringBuilder();
            data.append("Name,TokenNo,WorkUnder,Date,Attendance Type,Reported Time,OT Requisition From Time," +
                    "OT Requisition To Time,Released Time,Description,Given By,Edited By,Approved By,Collected By,Status");
            data.append("\n");
            for (Attendance attendance : attendanceList) {
                data.append("\n" + attendance.getName() + "," + attendance.getTokenNo() + "," + attendance.getWorkUnder() + "," + attendance.getDate()
                        + "," + attendance.getAttendanceType() + "," + attendance.getReportedTime() + "," + attendance.getOtRequisitionFromTime()
                        + "," + attendance.getOtRequisitionToTime() + "," + attendance.getReleasedTime() + "," + attendance.getDescription()
                        + "," + attendance.getGivenBy()+ "," + attendance.getEditedBy() + "," + attendance.getApprovedBy()+ "," + attendance.getCollectedBy()
                        + "," + attendance.getStatus());
            }

            try {
                FileOutputStream out = openFileOutput(selectedDate+"_AttendanceReport.csv", Context.MODE_PRIVATE);
                out.write(data.toString().getBytes());
                out.close();

                Context context = getApplicationContext();
                File fileLocation = new File(getFilesDir(), selectedDate+"_AttendanceReport.csv");
                Uri path = FileProvider.getUriForFile(context, "com.bhaskar.attendancetracker.fileprovider", fileLocation);
                Intent fileIntent = new Intent(Intent.ACTION_SEND);
                fileIntent.setType("text/csv");
                fileIntent.putExtra(Intent.EXTRA_SUBJECT, selectedDate+"_AttendanceReport.csv");
                fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(fileIntent, "Send mail"));

            } catch (Exception e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ViewAttendanceReport.this, "Export Successful!!", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.bhaskar.attendancetracker;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GiveAttendance extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmployeeAdapter adapter;
    private TextInputLayout searchText;
    private Button goToHomePageBtn;
    private String username, role, todayDate, selectedDate;
    private List<Employee> employees, filteredEmployees;
    private ProgressBar progressBar;
    private TextView selectDateText;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_attendance);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        username = getIntent().getStringExtra("username");
        role = getIntent().getStringExtra("role");
        selectedDate = getIntent().getStringExtra("date");

        recyclerView = findViewById(R.id.give_attendance_recyclerView);
        searchText = findViewById(R.id.give_attendance_search_text);
        goToHomePageBtn = findViewById(R.id.give_attendance_go_to_home_page_btn);
        progressBar = findViewById(R.id.give_attendance_progress_bar);
        selectDateText = findViewById(R.id.give_attendance_date_text);

        progressBar.setVisibility(View.VISIBLE);
        filteredEmployees = new ArrayList<Employee>();
        attendanceDatePicker();
        EmployeeRecycler();
        goToHomePage();
        searchEmployee();
        progressBar.setVisibility(View.GONE);
    }

    private void attendanceDatePicker() {
        final Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        todayDate = dayOfMonth + "_" + (month + 1) + "_" + year;
        selectDateText.setText(selectedDate);
        selectDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(GiveAttendance.this,
                        R.style.Theme_AppCompat_DayNight_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, dayOfMonth);
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                long today = cal.getTimeInMillis();
                final long oneDay = 24 * 60 * 60 * 1000L;
                dialog.getDatePicker().setMinDate(today - oneDay);
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
                EmployeeRecycler();
            }
        };
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomePage.class);
        intent.putExtra("username", username);
        intent.putExtra("role", role);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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

    private void searchEmployee() {
        searchText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterEmployees(s.toString());
            }
        });
    }

    private void filterEmployees(String text) {
        filteredEmployees = new ArrayList<Employee>();
        for (Employee item : employees) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredEmployees.add(item);
            }
        }
        adapter.filterEmployeeList(filteredEmployees);
    }

    private void EmployeeRecycler() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        employees = new ArrayList<>();
        filteredEmployees = new ArrayList<Employee>();
        adapter = new EmployeeAdapter(employees);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("employees");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Employee employee = dataSnapshot.getValue(Employee.class);
                    if (employee != null && employee.getWorkUnder().equals(username)) {
                        if(selectedDate.equals(todayDate)) {
                            populateEmployeeListToday(employee);
                        } else {
                            populateEmployeeList(employee);
                        }
                    }
                } else {
                    Toast.makeText(GiveAttendance.this, "Data Not Found", Toast.LENGTH_LONG).show();
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
        adapter.setOnItemClickListener(new EmployeeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (!filteredEmployees.isEmpty() && filteredEmployees.size() > position) {
                    adapter.notifyItemChanged(position);
                    Intent intent = new Intent(getApplicationContext(), EmployeeAttendance.class);
                    intent.putExtra("username", username);
                    intent.putExtra("role", role);
                    intent.putExtra("date",selectedDate);
                    intent.putExtra("employeeName", filteredEmployees.get(position).getName());
                    intent.putExtra("tokenNo", filteredEmployees.get(position).getTokenNo());
                    startActivity(intent);
                } else {
                    Toast.makeText(GiveAttendance.this, "Searched List Fail!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    private void populateEmployeeListToday(final Employee employee) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(selectedDate);
        Query checkUser = reference.orderByChild("tokenNo").equalTo(employee.getTokenNo());
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //String attendanceType = dataSnapshot.child(employee.getTokenNo()).child("attendanceType").getValue(String.class);
                } else {
                    adapter.notifyDataSetChanged();
                    employees.add(employee);
                    filteredEmployees.add(employee);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populateEmployeeList(final Employee employee) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(selectedDate);
        Query checkUser = reference.orderByChild("tokenNo").equalTo(employee.getTokenNo());
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String attendanceType = dataSnapshot.child(employee.getTokenNo()).child("attendanceType").getValue(String.class);
                    String releasedTime = dataSnapshot.child(employee.getTokenNo()).child("releasedTime").getValue(String.class);
                    if(attendanceType.equals("present") && releasedTime.equals("NA")){
                        adapter.notifyDataSetChanged();
                        employees.add(employee);
                        filteredEmployees.add(employee);
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

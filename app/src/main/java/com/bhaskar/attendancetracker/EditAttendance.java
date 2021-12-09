package com.bhaskar.attendancetracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditAttendance extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmployeeAdapter adapter;
    private TextInputLayout searchText;
    private Button goToHomePageBtn;
    private String username, role;
    private List<Employee> employees, filteredEmployees;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_attendance);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        username = getIntent().getStringExtra("username");
        role = getIntent().getStringExtra("role");


        recyclerView = findViewById(R.id.edit_attendance_recyclerView);
        searchText = findViewById(R.id.edit_attendance_search_text);
        goToHomePageBtn = findViewById(R.id.edit_attendance_go_to_home_page_btn);
        progressBar = findViewById(R.id.edit_attendance_progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        filteredEmployees = new ArrayList<Employee>();
        EmployeeRecycler();
        goToHomePage();
        searchEmployee();
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
        adapter = new EmployeeAdapter(employees);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("employees");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Employee employee = dataSnapshot.getValue(Employee.class);
                    if (employee != null && employee.getWorkUnder().equals(username)) {
                        //populateEmployeeList(employee, adapter, employees, filteredEmployees);
                        adapter.notifyDataSetChanged();
                        employees.add(employee);
                        filteredEmployees.add(employee);
                    }
                } else {
                    Toast.makeText(EditAttendance.this, "Data Not Found", Toast.LENGTH_LONG).show();
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
                    Intent intent = new Intent(getApplicationContext(), EditEmployeeAttendance.class);
                    intent.putExtra("username", username);
                    intent.putExtra("role", role);
                    intent.putExtra("employeeName", filteredEmployees.get(position).getName());
                    intent.putExtra("tokenNo", filteredEmployees.get(position).getTokenNo());
                    startActivity(intent);
                } else {
                    Toast.makeText(EditAttendance.this, "Searched List Fail!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        progressBar.setVisibility(View.GONE);
    }
}

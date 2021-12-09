package com.bhaskar.attendancetracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.List;

public class ViewAllEmployee extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmployeeAdapter adapter;
    private TextInputLayout searchText;
    private String username, role;
    private List<Employee> employees, filteredEmployees;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_employee);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        username = getIntent().getStringExtra("username");
        role = getIntent().getStringExtra("role");

        recyclerView = findViewById(R.id.view_all_employee_recyclerView);
        searchText = findViewById(R.id.view_all_employee_search_text);
        progressBar = findViewById(R.id.view_all_employee_progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        filteredEmployees = new ArrayList<Employee>();
        EmployeeRecycler();

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
                    if (employee != null) {
                        if (role.equals("ir_admin")) {
                            adapter.notifyDataSetChanged();
                            employees.add(employee);
                            filteredEmployees.add(employee);
                        } else if(role.equals("section_incharge")){
                            if(employee.getAssignedTo().equals(username)){
                                adapter.notifyDataSetChanged();
                                employees.add(employee);
                                filteredEmployees.add(employee);
                            }
                        }
                    }
                } else {
                    Toast.makeText(ViewAllEmployee.this, "Data Not Found", Toast.LENGTH_LONG).show();
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
                    //adapter.notifyItemChanged(position);
                    Intent intent = new Intent(getApplicationContext(), AssignEmployee.class);
                    intent.putExtra("username", username);
                    intent.putExtra("role", role);
                    intent.putExtra("employeeName", filteredEmployees.get(position).getName());
                    intent.putExtra("tokenNo", filteredEmployees.get(position).getTokenNo());
                    intent.putExtra("workUnder", filteredEmployees.get(position).getWorkUnder());
                    startActivity(intent);
                } else {
                    Toast.makeText(ViewAllEmployee.this, "Searched List Fail!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void goToHomePage(View view) {
        Intent intent = new Intent(getApplicationContext(), HomePage.class);
        intent.putExtra("username", username);
        intent.putExtra("role", role);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

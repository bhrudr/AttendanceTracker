package com.bhaskar.attendancetracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import java.util.List;

public class AddEmployee extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmployeeAdapter adapter;
    private TextInputLayout employeeName, employeeTokenNo, searchText;
    private Button goToHomePageBtn;
    private String username, role;
    private List<Employee> employees, filteredEmployees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        username = getIntent().getStringExtra("username");
        role = getIntent().getStringExtra("role");

        recyclerView = findViewById(R.id.add_employee_recyclerView);
        employeeName = findViewById(R.id.add_employee_employee_name);
        employeeTokenNo = findViewById(R.id.add_employee_employee_token);
        searchText = findViewById(R.id.add_employee_search_text);
        goToHomePageBtn = findViewById(R.id.add_employee_go_to_home_page_btn);
        goToHomePage();
        filteredEmployees = new ArrayList<Employee>();
        EmployeeRecycler();
        searchEmployee();
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
                        adapter.notifyDataSetChanged();
                        employees.add(employee);
                        filteredEmployees.add(employee);
                    }
                } else {
                    Toast.makeText(AddEmployee.this, "Data Not Found", Toast.LENGTH_LONG).show();
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
                    Intent intent = new Intent(getApplicationContext(), DeleteEmployee.class);
                    intent.putExtra("username", username);
                    intent.putExtra("role", role);
                    intent.putExtra("employeeName", filteredEmployees.get(position).getName());
                    intent.putExtra("tokenNo", filteredEmployees.get(position).getTokenNo());
                    intent.putExtra("workUnder", filteredEmployees.get(position).getWorkUnder());
                    startActivity(intent);
                } else {
                    Toast.makeText(AddEmployee.this, "Searched List Fail!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addEmployee(View view) {
        final String tokenNo = employeeTokenNo.getEditText().getText().toString();

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("employees");

        Query checkUser = reference.orderByChild("tokenNo").equalTo(tokenNo);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    employeeTokenNo.setError("Token No Already Exists");
                } else {
                    employeeTokenNo.setError(null);
                    employeeTokenNo.setErrorEnabled(false);
                    storeEmployeeDataInDB();
                    Toast.makeText(AddEmployee.this, "Employee added Successfully!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void storeEmployeeDataInDB() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("employees");
        String name = employeeName.getEditText().getText().toString();
        String tokenNo = employeeTokenNo.getEditText().getText().toString();
        String attendanceLastGivenOn = "NA";
        reference.child(tokenNo).setValue(new Employee(name, tokenNo, username, username, username, attendanceLastGivenOn));
        employeeName.getEditText().setText("");
        employeeTokenNo.getEditText().setText("");
    }
}

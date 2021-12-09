package com.bhaskar.attendancetracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AssignEmployee extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private TextInputLayout searchText;
    private String username, role, tokenNo, employeeName;
    private List<UserHelperClass> userList, filteredUserList;
    private DatabaseReference reference;
    private Button goToHomePageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_employee);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        username = getIntent().getStringExtra("username");
        role = getIntent().getStringExtra("role");
        tokenNo = getIntent().getStringExtra("tokenNo");
        employeeName = getIntent().getStringExtra("employeeName");

        recyclerView = findViewById(R.id.assign_employee_user_recyclerView);
        searchText = findViewById(R.id.assign_employee_user_search_text);
        goToHomePageBtn = findViewById(R.id.assign_employee_go_to_home_page_btn);

        filteredUserList = new ArrayList<UserHelperClass>();
        EmployeeRecycler();
        searchTextFunction();
        goToHomePage();
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

    private void searchTextFunction() {
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
        filteredUserList = new ArrayList<UserHelperClass>();
        for (UserHelperClass item : userList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredUserList.add(item);
            }
        }
        adapter.filterUserList(filteredUserList);
    }

    private void EmployeeRecycler() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        userList = new ArrayList<>();
        adapter = new UserAdapter(userList);
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    UserHelperClass userHelperClass = dataSnapshot.getValue(UserHelperClass.class);
                    if (userHelperClass != null) {
                        if (role.equals("ir_admin")) {
                            if (userHelperClass.getRole().equals("section_incharge") || userHelperClass.getRole().equals("site_engineer")) {
                                adapter.notifyDataSetChanged();
                                userList.add(userHelperClass);
                                filteredUserList.add(userHelperClass);
                            }
                        } else if (role.equals("section_incharge")) {
                            if (userHelperClass.getRole().equals("site_engineer")) {
                                adapter.notifyDataSetChanged();
                                userList.add(userHelperClass);
                                filteredUserList.add(userHelperClass);
                            }
                        }
                    }
                } else {
                    Toast.makeText(AssignEmployee.this, "Data Not Found", Toast.LENGTH_LONG).show();
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
        adapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (!filteredUserList.isEmpty() && filteredUserList.size() > position) {
                    adapter.notifyItemChanged(position);
                    operComfirmDialogue(filteredUserList.get(position));
                } else {
                    Toast.makeText(AssignEmployee.this, "Searched List Fail!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void operComfirmDialogue(final UserHelperClass userHelperClass) {
        reference = FirebaseDatabase.getInstance().getReference("employees");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Assign "+ employeeName + " to "+userHelperClass.getName()+" ?")
                .setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(userHelperClass.getRole().equals("section_incharge")){
                            reference.child(tokenNo).child("assignedTo").setValue(userHelperClass.getUsername());
                        }
                        reference.child(tokenNo).child("workUnder").setValue(userHelperClass.getUsername());
                        Toast.makeText(AssignEmployee.this, "Employee Assigend Successfully!!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ViewAllEmployee.class);
                        intent.putExtra("username", username);
                        intent.putExtra("role", role);
                        startActivity(intent);
                        //finish();
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

}

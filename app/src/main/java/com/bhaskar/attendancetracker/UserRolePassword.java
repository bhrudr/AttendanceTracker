package com.bhaskar.attendancetracker;

public class UserRolePassword {
    private String role, password;

    public UserRolePassword(String role, String password) {
        this.role = role;
        this.password = password;
    }

    public UserRolePassword() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

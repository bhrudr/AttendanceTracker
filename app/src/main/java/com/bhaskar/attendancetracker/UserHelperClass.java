package com.bhaskar.attendancetracker;

public class UserHelperClass {

    private String name, username, email, phoneNo, password, role, favouriteColour, favouriteFood, favouriteActor;

    public UserHelperClass() {

    }

    public UserHelperClass(String name, String username, String email, String phoneNo, String password, String role,
                           String favouriteColour, String favouriteFood, String favouriteActor) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.phoneNo = phoneNo;
        this.password = password;
        this.role = role;
        this.favouriteColour = favouriteColour;
        this.favouriteFood = favouriteFood;
        this.favouriteActor = favouriteActor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFavouriteColour() {
        return favouriteColour;
    }

    public void setFavouriteColour(String favouriteColour) {
        this.favouriteColour = favouriteColour;
    }

    public String getFavouriteFood() {
        return favouriteFood;
    }

    public void setFavouriteFood(String favouriteFood) {
        this.favouriteFood = favouriteFood;
    }

    public String getFavouriteActor() {
        return favouriteActor;
    }

    public void setFavouriteActor(String favouriteActor) {
        this.favouriteActor = favouriteActor;
    }
}

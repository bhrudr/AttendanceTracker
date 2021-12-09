package com.bhaskar.attendancetracker;

public class Employee {

    private String name, tokenNo, workUnder, assignedTo, addedBy, attendanceLastGivenOn;

    public Employee(String name, String tokenNo, String workUnder, String assignedTo, String addedBy, String attendanceLastGivenOn) {
        this.name = name;
        this.tokenNo = tokenNo;
        this.workUnder = workUnder;
        this.assignedTo = assignedTo;
        this.addedBy = addedBy;
        this.attendanceLastGivenOn = attendanceLastGivenOn;
    }

    public Employee() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTokenNo() {
        return tokenNo;
    }

    public void setTokenNo(String tokenNo) {
        this.tokenNo = tokenNo;
    }

    public String getWorkUnder() {
        return workUnder;
    }

    public void setWorkUnder(String workUnder) {
        this.workUnder = workUnder;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getAttendanceLastGivenOn() {
        return attendanceLastGivenOn;
    }

    public void setAttendanceLastGivenOn(String attendanceLastGivenOn) {
        this.attendanceLastGivenOn = attendanceLastGivenOn;
    }
}

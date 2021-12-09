package com.bhaskar.attendancetracker;

public class Attendance {
    private String name, tokenNo, workUnder, date,
            attendanceType, reportedTime, releasedTime, otRequisitionFromTime,
            otRequisitionToTime, description, givenBy, approvedBy, status, editedBy, collectedBy;

    public Attendance(String name, String tokenNo, String workUnder, String date, String attendanceType, String reportedTime,
                      String releasedTime, String otRequisitionFromTime, String otRequisitionToTime, String description,
                      String givenBy, String approvedBy, String status, String editedBy, String collectedBy) {
        this.name = name;
        this.tokenNo = tokenNo;
        this.workUnder = workUnder;
        this.date = date;
        this.attendanceType = attendanceType;
        this.reportedTime = reportedTime;
        this.releasedTime = releasedTime;
        this.otRequisitionFromTime = otRequisitionFromTime;
        this.otRequisitionToTime = otRequisitionToTime;
        this.description = description;
        this.givenBy = givenBy;
        this.approvedBy = approvedBy;
        this.status = status;
        this.editedBy = editedBy;
        this.collectedBy = collectedBy;
    }

    public Attendance() {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(String attendanceType) {
        this.attendanceType = attendanceType;
    }

    public String getReportedTime() {
        return reportedTime;
    }

    public void setReportedTime(String reportedTime) {
        this.reportedTime = reportedTime;
    }

    public String getReleasedTime() {
        return releasedTime;
    }

    public void setReleasedTime(String releasedTime) {
        this.releasedTime = releasedTime;
    }

    public String getOtRequisitionFromTime() {
        return otRequisitionFromTime;
    }

    public void setOtRequisitionFromTime(String otRequisitionFromTime) {
        this.otRequisitionFromTime = otRequisitionFromTime;
    }

    public String getOtRequisitionToTime() {
        return otRequisitionToTime;
    }

    public void setOtRequisitionToTime(String otRequisitionToTime) {
        this.otRequisitionToTime = otRequisitionToTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGivenBy() {
        return givenBy;
    }

    public void setGivenBy(String givenBy) {
        this.givenBy = givenBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(String editedBy) {
        this.editedBy = editedBy;
    }

    public String getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(String collectedBy) {
        this.collectedBy = collectedBy;
    }
}

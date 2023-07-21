package com.example.boattravelling;

import java.io.Serializable;

public class RequestDetails implements Serializable {
    private String location;
    private String workType;
    private int timeEstimate;
    private String date;
    private boolean craneRequired;
    private boolean roughWeather;

    public RequestDetails(String location, String workType, int timeEstimate, String date, boolean craneRequired, boolean roughWeather) {
        this.location = location;
        this.workType = workType;
        this.timeEstimate = timeEstimate;
        this.date = date;
        this.craneRequired = craneRequired;
        this.roughWeather = roughWeather;
    }

    // Define getters and setters for the request details

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public int getTimeEstimate() {
        return timeEstimate;
    }

    public void setTimeEstimate(int timeEstimate) {
        this.timeEstimate = timeEstimate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCraneRequired() {
        return craneRequired;
    }

    public void setCraneRequired(boolean craneRequired) {
        this.craneRequired = craneRequired;
    }

    public boolean isRoughWeather() {
        return roughWeather;
    }

    public void setRoughWeather(boolean roughWeather) {
        this.roughWeather = roughWeather;
    }
}

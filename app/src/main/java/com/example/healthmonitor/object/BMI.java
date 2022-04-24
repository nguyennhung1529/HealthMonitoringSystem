package com.example.healthmonitor.object;

import java.util.Date;

public class BMI {
    private float bmi;
    private String status;
    private String date;

    @Override
    public String toString() {
        return "BMI{" +
                "bmi=" + bmi +
                ", status='" + status + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public BMI(float bmi, String status, String date) {
        this.bmi = bmi;
        this.status = status;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BMI(float bmi, String status) {
        this.bmi = bmi;
        this.status = status;
    }

    public BMI() {
    }


    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

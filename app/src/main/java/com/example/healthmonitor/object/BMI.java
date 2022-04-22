package com.example.healthmonitor.object;

import java.util.Date;

public class BMI {
    private String bmi;
    private String status;

    public BMI( String bmi, String status) {
        this.bmi = bmi;
        this.status = status;
    }

    public BMI() {
    }


    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

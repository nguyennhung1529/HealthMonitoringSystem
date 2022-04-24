package com.example.healthmonitor.object;

import java.util.Date;

public class Water {
    public String date;
    public int value;

    public Water(String date, int value) {
        this.date = date;
        this.value = value;
    }

    public Water() {
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Water{" +
                "date=" + date +
                ", value=" + value +
                '}';
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

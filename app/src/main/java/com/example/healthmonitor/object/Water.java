package com.example.healthmonitor.object;

import java.util.Date;

public class Water {
    public String date;
    public int value;

    public Water() {
    }

    public Water(String date, int value) {
        this.date = date;
        this.value = value;
    }

    public String getDate() {
        return date;
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

    @Override
    public String toString() {
        return "Water{" +
                "date=" + date +
                ", value=" + value +
                '}';
    }
}

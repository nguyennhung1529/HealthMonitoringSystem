package com.example.healthmonitor.object;

import java.util.Date;

public class Weight {
    private float weight;
    private String date;
    private String note;

    public Weight() {
    }

    public Weight(float weight, String date) {
        this.weight = weight;
        this.date = date;
    }

    public Weight(float weight, String date, String note) {
        this.weight = weight;
        this.date = date;
        this.note = note;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Weight{" +
                "weight=" + weight +
                ", date='" + date + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}

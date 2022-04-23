package com.example.healthmonitor.object;

import java.util.Date;

public class Weight {
    private float weight;
    private Date date;

    public Weight() {
    }

    public Weight(float weight, Date date) {
        this.weight = weight;
        this.date = date;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Weight{" +
                "weight=" + weight +
                ", date=" + date +
                '}';
    }
}

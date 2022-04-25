package com.example.healthmonitor.object;

public class Data {
    private float weight;
    private int height;
    private String note;
    private String date;

    public Data() {
    }

    public Data(int height, float weight, String note, String date) {
        this.weight = weight;
        this.date = date;
        this.note = note;
        this.height = height;
    }

    public void setData(Data userDetails) {
        this.weight = userDetails.getWeight();
        this.date = userDetails.getDate();
        this.note = userDetails.getNote();
        this.height = userDetails.getHeight();
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

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Data{" +
                "weight=" + weight +
                ", date='" + date + '\'' +
                ", note='" + note + '\'' +
                ", height=" + height +
                '}';
    }
}

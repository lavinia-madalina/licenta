package com.example.licenta;
public class Subject {
    private String id;
    private String name;
    private double average;

    public Subject(String id, String name, double average) {
        this.id = id;
        this.name = name;
        this.average = average;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getAverage() {
        return average;
    }
}
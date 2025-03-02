package com.example.licenta;

public class GradeSubject {
    private String id;
    private String name;
    private double average;

    public GradeSubject(String id, String name, double average) {
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

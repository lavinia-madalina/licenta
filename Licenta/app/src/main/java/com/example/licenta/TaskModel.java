package com.example.licenta;

import com.google.firebase.Timestamp;

public class TaskModel {
    private String name;
    private String description;
    private Timestamp date;
    private String room;

    // Constructor to initialize the TaskModel
    public TaskModel(String name, String description, Timestamp date, String room) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.room = room;
    }

    // Getters and setters (optional if needed for Firestore)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}

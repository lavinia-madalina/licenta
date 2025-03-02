package com.example.licenta;
public class CalendarSlot {
    private String day;
    private String hour;
    private String subjectName;
    private String room;

    public CalendarSlot(String day, String hour, String subjectName, String room) {
        this.day = day;
        this.hour = hour;
        this.subjectName = subjectName;
        this.room = room;
    }

    public String getDay() { return day; }
    public String getHour() { return hour; }
    public String getSubjectName() { return subjectName; }
    public String getRoom() { return room; }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}

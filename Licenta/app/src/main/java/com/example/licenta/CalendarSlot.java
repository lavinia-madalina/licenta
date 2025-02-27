package com.example.licenta;

public class CalendarSlot {

    private String day; // Ziua săptămânii (ex. "luni", "marți", etc.)
    private String hour; // Ora (ex. "08:00", "09:00", etc.)
    private String subject; // Materia asociată (sau null dacă nu este nicio materie)

    public CalendarSlot(String day, String hour, String subject) {
        this.day = day;
        this.hour = hour;
        this.subject = subject;
    }

    // Getteri și setteri
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}

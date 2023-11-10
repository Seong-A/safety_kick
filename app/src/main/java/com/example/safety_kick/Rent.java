package com.example.safety_kick;

public class Rent {
    private String email;
    private String date;
    private String fee;
    private String time;

    public Rent() {

    }

    public Rent(String email, String date, String fee, String time) {
        this.email = email;
        this.date = date;
        this.fee = fee;
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() { return date; }

    public String getFee() {
        return fee;
    }

    public String getTime() {
        return time;
    }
}

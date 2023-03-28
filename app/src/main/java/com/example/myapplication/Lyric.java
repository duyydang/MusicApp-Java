package com.example.myapplication;

public class Lyric {
    private double startTime;
    private String text;

    public Lyric(double startTime, String text) {
        this.startTime = startTime;
        this.text = text;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

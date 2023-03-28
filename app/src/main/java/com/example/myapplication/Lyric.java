package com.example.myapplication;

public class Lyric {
    private int startTime;
    private String text;

    public Lyric(int startTime, String text) {
        this.startTime = startTime;
        this.text = text;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

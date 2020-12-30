package com.example.playertazkerehadiyeh.model;

public class CurrentPlayerTime {

    String currentTime ;
    long progress;
    long duration;
    String total;

    public CurrentPlayerTime() {
    }

    public CurrentPlayerTime(String currentTime, long progress, String total) {
        this.currentTime = currentTime;
        this.progress = progress;
        this.total = total;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}

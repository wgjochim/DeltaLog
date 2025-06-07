package com.deltalog;

public class WorkoutSession {
    public int id;
    public String name;
    public String startTime;
    public String duration;

    public WorkoutSession(int id, String name, String startTime, String duration) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
    }
}
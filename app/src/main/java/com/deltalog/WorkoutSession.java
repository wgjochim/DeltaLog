package com.deltalog;

public class WorkoutSession {
    public int workoutId;
    public String name;
    public String startTime;
    public String duration;

    public WorkoutSession(int workoutId, String name, String startTime, String duration) {
        this.workoutId = workoutId;
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
    }
}

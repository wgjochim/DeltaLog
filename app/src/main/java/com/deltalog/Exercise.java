package com.deltalog;

import java.util.List;

public class Exercise {
    long id;
    String name;
    List<ExerciseSet> sets;
    public Exercise(long id, String name, List<ExerciseSet> sets) {
        this.id = id;
        this.name = name;
        this.sets = sets;
    }
}
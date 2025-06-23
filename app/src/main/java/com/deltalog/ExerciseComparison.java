package com.deltalog;

import java.util.List;

public class ExerciseComparison {
    public String name;
    public List<ExerciseSetComparison> differences;

    public ExerciseComparison(String name, List<ExerciseSetComparison> differences) {
        this.name = name;
        this.differences = differences;
    }
}

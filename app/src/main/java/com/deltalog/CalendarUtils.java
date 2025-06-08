package com.deltalog;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class CalendarUtils {
    public static List<LocalDate> generateMonthDays(YearMonth yearMonth) {
        List<LocalDate> days = new ArrayList<>();

        LocalDate firstDay = yearMonth.atDay(1);
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday

        int daysInMonth = yearMonth.lengthOfMonth();

        // Add placeholders
        for (int i = 1; i < firstDayOfWeek; i++) {
            days.add(null);
        }

        // Add actual days
        for (int i = 1; i <= daysInMonth; i++) {
            days.add(yearMonth.atDay(i));
        }

        return days;
    }
}

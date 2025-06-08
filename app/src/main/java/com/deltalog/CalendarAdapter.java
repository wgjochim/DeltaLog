package com.deltalog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayViewHolder> {
    private List<LocalDate> days;
    private Set<LocalDate> workoutDays;

    public CalendarAdapter(List<LocalDate> days, Set<LocalDate> workoutDays) {
        this.days = days;
        this.workoutDays = workoutDays;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_day_item, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        LocalDate date = days.get(position);
        if (date != null) {
            holder.dayNumber.setText(String.valueOf(date.getDayOfMonth()));
            holder.dayNumber.setVisibility(View.VISIBLE);

            if (workoutDays.contains(date)) {
                holder.dayCircle.setBackgroundResource(R.drawable.circle_green);
            } else {
                holder.dayCircle.setBackgroundResource(R.drawable.circle_gray);
            }
        } else {
            holder.dayNumber.setVisibility(View.INVISIBLE);
            holder.dayCircle.setBackgroundResource(0);
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView dayNumber;
        View dayCircle;

        DayViewHolder(View itemView) {
            super(itemView);
            dayNumber = itemView.findViewById(R.id.dayNumber);
            dayCircle = itemView.findViewById(R.id.dayCircle);
        }
    }
}

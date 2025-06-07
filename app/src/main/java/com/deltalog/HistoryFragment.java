package com.deltalog;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        LinearLayout historyContainer = view.findViewById(R.id.history_container);

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        List<WorkoutSession> workoutHistory = dbHelper.getWorkoutHistory();

        for (WorkoutSession session : workoutHistory) {
            View card = createWorkoutCard(session, inflater, historyContainer);
            historyContainer.addView(card);
        }

        return view;
    }

    private View createWorkoutCard(WorkoutSession session, LayoutInflater inflater, ViewGroup parent) {
        CardView cardView = new CardView(getContext());
        cardView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        cardView.setCardElevation(8);
        cardView.setRadius(16);
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(32, 32, 32, 32);
        cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.foreground_gray));

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        // Horizontal layout for name + duration
        LinearLayout nameRow = new LinearLayout(getContext());
        nameRow.setOrientation(LinearLayout.HORIZONTAL);
        nameRow.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView name = new TextView(getContext());
        name.setText(session.name);
        name.setTextSize(18);
        name.setTypeface(null, Typeface.BOLD);
        name.setTextColor(Color.WHITE);
        name.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f
        ));

        TextView duration = new TextView(getContext());
        duration.setText(session.duration + " min");
        duration.setTextSize(16);
        duration.setTextColor(Color.WHITE);
        duration.setTypeface(null, Typeface.BOLD);
        duration.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        nameRow.addView(name);
        nameRow.addView(duration);

        TextView date = new TextView(getContext());
        String formattedDate = formatDateToPrettyDate(session.startTime);
        date.setText(formattedDate);
        date.setTextSize(16);
        date.setTextColor(ContextCompat.getColor(getContext(), R.color.hint_gray));

        layout.addView(nameRow);
        layout.addView(date);
        cardView.addView(layout);

        Log.d("FORMAT_DATE", "startTime: " + session.startTime);

        return cardView;
    }

    private String formatDateToPrettyDate(String startTime) {
        try {
            // Parse using the known format: dd:MM:yyyy
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd:MM:yyyy", Locale.getDefault());
            Date date = inputFormat.parse(startTime);

            // Extract day and month
            SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());
            int day = Integer.parseInt(dayFormat.format(date));
            String suffix = getDaySuffix(day);

            SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
            String month = monthFormat.format(date);

            return month + " " + day + suffix;
        } catch (ParseException e) {
            return startTime; // fallback if parsing fails
        }
    }


    // Helper to get the ordinal suffix
    private String getDaySuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }

}
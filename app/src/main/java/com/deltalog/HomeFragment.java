package com.deltalog;

import static com.deltalog.CalendarUtils.generateMonthDays;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HomeFragment extends Fragment {

    private DrawerLayout drawerLayout;
    private RecyclerView calendarRecyclerView;
    private TextView monthTitle;
    private YearMonth currentMonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        drawerLayout = view.findViewById(R.id.drawer_layout);
        ImageView drawerMenuIcon = view.findViewById(R.id.drawer_menu);
        NavigationView navView = view.findViewById(R.id.nav_view);

        drawerMenuIcon.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.END));

        navView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.nav_credits) {
                drawerLayout.closeDrawer(Gravity.END);
                startActivity(new Intent(getContext(), CreditsActivity.class));
                return true;
            }
            return false;
        });

        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthTitle = view.findViewById(R.id.monthTitle);

        currentMonth = YearMonth.now();
        loadCalendar(currentMonth);

        OnSwipeTouchListener swipeListener = new OnSwipeTouchListener(getContext()) {
            public void onSwipeLeft() {
                currentMonth = currentMonth.plusMonths(1);
                loadCalendar(currentMonth);
            }

            public void onSwipeRight() {
                currentMonth = currentMonth.minusMonths(1);
                loadCalendar(currentMonth);
            }
        };


        view.setOnTouchListener(swipeListener);
        calendarRecyclerView.setOnTouchListener(swipeListener);

        TextView tvWorkouts = view.findViewById(R.id.text_Workout);
        TextView tvTotalDuration = view.findViewById(R.id.text_Total_Duration);
        TextView tvAvgDuration = view.findViewById(R.id.text_Avg_Duration);

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        List<String> durations = dbHelper.getThisWeeksDurations();

        int totalMinutes = 0;
        for (String dur : durations) {
            try {
                totalMinutes += Integer.parseInt(dur.replace(" min", "").trim());
            } catch (NumberFormatException e) {
                e.printStackTrace(); // Invalid format
            }
        }

        int count = durations.size();
        int avgMinutes = count > 0 ? totalMinutes / count : 0;

        tvWorkouts.setText("Workouts: " + count);

        // Only show durations if > 0, else placeholder
        tvTotalDuration.setText("Total Duration: " + (totalMinutes > 0 ? totalMinutes + " min" : "–"));
        tvAvgDuration.setText("Avg Duration: " + (avgMinutes > 0 ? avgMinutes + " min" : "–"));


        LinearLayout layoutLastWorkout = view.findViewById(R.id.layout_Last_Workout);

        int lastWorkoutId = dbHelper.getLatestWorkoutIdOverall();

        if (lastWorkoutId != -1) {
            List<Exercise> exercises = dbHelper.getExercisesForWorkout(lastWorkoutId);
            List<ExerciseComparison> comparisons = dbHelper.compareAllExercisesWithPrevious(lastWorkoutId);

            for (Exercise exercise : exercises) {
                TextView exerciseTitle = new TextView(requireContext());
                exerciseTitle.setText(exercise.name);
                exerciseTitle.setTextSize(16);
                exerciseTitle.setTypeface(null, Typeface.BOLD);
                exerciseTitle.setPadding(0, 16, 0, 4);
                exerciseTitle.setTextColor(Color.WHITE);
                layoutLastWorkout.addView(exerciseTitle);

                // Find matching comparison by exercise name
                ExerciseComparison comparison = null;
                if (comparisons != null) {
                    for (ExerciseComparison c : comparisons) {
                        if (c.name.equals(exercise.name)) {
                            comparison = c;
                            break;
                        }
                    }
                }

                int setNumber = 1;

                int darkGreen = Color.parseColor("#4CAF50");
                int darkRed = Color.parseColor("#8B0000");

                for (ExerciseSet set : exercise.sets) {
                    SpannableStringBuilder sb = new SpannableStringBuilder();

                    String setPrefix = "Set " + setNumber + ": " + set.weight + "kg";
                    sb.append(setPrefix);

                    if (comparison != null && comparison.differences.size() >= setNumber) {
                        ExerciseSetComparison setComp = comparison.differences.get(setNumber - 1);

                        // Weight diff part
                        if (setComp.weightDiff != 0) {
                            String weightDiffStr = String.format(" (%+.1f)", setComp.weightDiff);
                            int start = sb.length();
                            sb.append(weightDiffStr);
                            int end = sb.length();

                            int color = setComp.weightDiff > 0 ? darkGreen : darkRed;
                            sb.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                    sb.append(" x " + set.reps);

                    if (comparison != null && comparison.differences.size() >= setNumber) {
                        ExerciseSetComparison setComp = comparison.differences.get(setNumber - 1);

                        // Reps diff part
                        if (setComp.repsDiff != 0) {
                            String repsDiffStr = String.format(" (%+d)", setComp.repsDiff);
                            int start = sb.length();
                            sb.append(repsDiffStr);
                            int end = sb.length();

                            int color = setComp.repsDiff > 0 ? darkGreen : darkRed;
                            sb.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                    sb.append(" reps");

                    TextView setView = new TextView(requireContext());
                    setView.setText(sb);
                    setView.setTextSize(14);
                    setView.setPadding(8, 0, 0, 2);
                    setView.setTextColor(Color.WHITE);
                    layoutLastWorkout.addView(setView);

                    setNumber++;
                }
            }
        } else {
            TextView emptyView = new TextView(requireContext());
            emptyView.setText("No past workout found.");
            emptyView.setTextSize(14);
            emptyView.setPadding(0, 8, 0, 0);
            layoutLastWorkout.addView(emptyView);
        }



        return view;
    }

    private Set<LocalDate> fetchWorkoutDatesFromDb(YearMonth month) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        return dbHelper.getWorkoutDatesForMonth(month);
    }

    private void loadCalendar(YearMonth month) {
        List<LocalDate> days = generateMonthDays(month);
        Set<LocalDate> workoutDays = fetchWorkoutDatesFromDb(month);
        CalendarAdapter adapter = new CalendarAdapter(days, workoutDays);

        calendarRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        calendarRecyclerView.setAdapter(adapter);

        monthTitle.setText(month.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + month.getYear());
    }
}
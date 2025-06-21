package com.deltalog;

import static com.deltalog.CalendarUtils.generateMonthDays;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
package com.deltalog;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import java.util.HashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProgressFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        LinearLayout cardContainer = view.findViewById(R.id.exercise_card_container);
        EditText searchInput = view.findViewById(R.id.search_input);
        HashMap<String, View> cardMap = new HashMap<>();

        // Load initial weight graphs
        loadGraphs(dbHelper, inflater, cardContainer, cardMap, false);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String query = s.toString().toLowerCase().trim();
                for (String name : cardMap.keySet()) {
                    View card = cardMap.get(name);
                    if (name.startsWith(query)) {
                        card.setVisibility(View.VISIBLE);
                    } else {
                        card.setVisibility(View.GONE);
                    }
                }
            }
        });

        SwitchCompat modeSwitch = view.findViewById(R.id.mode_switch);
        modeSwitch.setChecked(false);
        modeSwitch.setThumbTintList(ColorStateList.valueOf(Color.parseColor("#2196F3")));
        modeSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#802196F3")));

        modeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                modeSwitch.setThumbTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
                modeSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#80F44336")));
                loadGraphs(dbHelper, inflater, cardContainer, cardMap, true); // reps
            } else {
                modeSwitch.setThumbTintList(ColorStateList.valueOf(Color.parseColor("#2196F3")));
                modeSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#802196F3")));
                loadGraphs(dbHelper, inflater, cardContainer, cardMap, false); // weights
            }
        });

        return view;
    }


    private void loadGraphs(DatabaseHelper dbHelper, LayoutInflater inflater, LinearLayout container,
                            HashMap<String, View> cardMap, boolean showReps) {

        container.removeAllViews(); // clear previous

        List<String> exerciseNames = dbHelper.getAllUniqueExerciseNames();

        for (String exerciseName : exerciseNames) {
            List<? extends Number> values = showReps
                    ? dbHelper.getLastTenSetOneRepsForExercise(exerciseName)
                    : dbHelper.getLastTenSetOneWeightsForExercise(exerciseName);

            Collections.reverse(values);

            View cardView = inflater.inflate(R.layout.exercise_card_item, container, false);
            TextView nameView = cardView.findViewById(R.id.text_exercise_name);
            LineChart chart = cardView.findViewById(R.id.line_chart);

            nameView.setText(exerciseName);

            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                entries.add(new Entry(i, values.get(i).floatValue()));
            }

            LineDataSet dataSet = new LineDataSet(entries, "Progress");
            dataSet.setLineWidth(2f);
            dataSet.setColor(showReps ? Color.RED : Color.BLUE);
            dataSet.setCircleRadius(3f);
            dataSet.setCircleColor(Color.WHITE);
            dataSet.setDrawValues(false);

            chart.setData(new LineData(dataSet));
            chart.setBackgroundColor(Color.TRANSPARENT);
            chart.getDescription().setEnabled(false);
            chart.getLegend().setEnabled(false);
            chart.getAxisRight().setEnabled(false);

            XAxis xAxis = chart.getXAxis();
            xAxis.setEnabled(false);

            chart.getAxisLeft().setTextColor(Color.WHITE);
            chart.getAxisLeft().setAxisLineColor(Color.WHITE);

            chart.setTouchEnabled(false);
            chart.setScaleEnabled(false);
            chart.setPinchZoom(false);
            chart.invalidate();

            container.addView(cardView);
            cardMap.put(exerciseName.toLowerCase(), cardView);
        }
    }
}

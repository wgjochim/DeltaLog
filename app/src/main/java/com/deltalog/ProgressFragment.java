package com.deltalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Collections;
import java.util.List;

public class ProgressFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        LinearLayout cardContainer = view.findViewById(R.id.exercise_card_container);

        List<String> exerciseNames = dbHelper.getAllUniqueExerciseNames();

        // For each exercise, add a card dynamically
        for (String exerciseName : exerciseNames) {
            List<Float> weights = dbHelper.getLastTenSetOneWeightsForExercise(exerciseName);

            Collections.reverse(weights);

            View cardView = inflater.inflate(R.layout.exercise_card_item, cardContainer, false);

            TextView nameView = cardView.findViewById(R.id.text_exercise_name);
            TextView weightView = cardView.findViewById(R.id.text_weight_list);

            nameView.setText(exerciseName);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < weights.size(); i++) {
                sb.append(weights.get(i)).append(" kg\n");
            }
            weightView.setText(sb.toString().trim());

            cardContainer.addView(cardView);
        }

        return view;
    }
}

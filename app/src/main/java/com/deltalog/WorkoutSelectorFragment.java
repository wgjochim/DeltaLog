package com.deltalog;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class WorkoutSelectorFragment extends Fragment {

    private LinearLayout workoutListContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_workout_selector, container, false);

        workoutListContainer = rootView.findViewById(R.id.workout_list_container);
        TextView createWorkoutButton = rootView.findViewById(R.id.createWorkoutText);

        // Loads in all the Workout Cards
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        Cursor cursor = dbHelper.getAllWorkoutTypes();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String workoutName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORKOUT_NAME));
                int workoutTypeId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORKOUT_TYPE_ID));
                View card = createWorkoutCard(workoutName, workoutTypeId);
                workoutListContainer.addView(card);
            } while (cursor.moveToNext());
            cursor.close();
        }

        createWorkoutButton.setOnClickListener(v -> showCreateWorkoutDialog());

        return rootView;
    }


    private void showCreateWorkoutDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_create_workout, null);

        EditText workoutNameInput = dialogView.findViewById(R.id.workoutNameInput);

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("Create", (d, which) -> {
                    String workoutName = workoutNameInput.getText().toString().trim();
                    if (!workoutName.isEmpty()) {
                        long newId = dbHelper.insertWorkoutType(workoutName);
                        if (newId != -1) {
                            View card = createWorkoutCard(workoutName, (int) newId);
                            workoutListContainer.addView(card);

                            Toast.makeText(getContext(), "Workout \"" + workoutName + "\" created!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to create workout.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Workout name cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.foreground_gray))
            );
        }
    }


    private void showEditWorkoutDialog(int workoutTypeId, TextView nameText) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_workout, null); // reuse the same layout
        EditText workoutNameInput = dialogView.findViewById(R.id.workoutNameInput);

        workoutNameInput.setText(nameText.getText().toString());

        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlerdialogBackground)
                .setView(dialogView)
                .setPositiveButton("Save", (d, which) -> {
                    String newName = workoutNameInput.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                        int rows = dbHelper.updateWorkoutTypeName(workoutTypeId, newName);
                        if (rows > 0) {
                            nameText.setText(newName); // update UI
                            Toast.makeText(getContext(), "Workout updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Update failed.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Workout name cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }



    // Creates Card with workout names in them. Is called in OnCreate with all saved names
    private View createWorkoutCard(String workoutName, int workoutTypeId) {
        CardView card = new CardView(requireContext());
        card.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        card.setRadius(24);
        card.setCardElevation(6);
        card.setUseCompatPadding(true);
        card.setContentPadding(32, 32, 32, 32);
        card.setCardBackgroundColor(getResources().getColor(R.color.foreground_gray, null));

        TextView nameText = new TextView(requireContext());
        nameText.setText(workoutName);
        nameText.setTextSize(18);
        nameText.setTextColor(getResources().getColor(R.color.white, null));

        card.addView(nameText);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), WorkoutActivity.class);
            intent.putExtra(WorkoutActivity.WORKOUT_TYPE, workoutTypeId);
            startActivity(intent);
        });

        card.setOnLongClickListener(v -> {
            showEditWorkoutDialog(workoutTypeId, nameText);
            return true;
        });

        return card;
    }
}
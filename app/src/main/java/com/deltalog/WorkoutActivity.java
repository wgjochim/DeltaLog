package com.deltalog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkoutActivity extends AppCompatActivity {

    public static final String WORKOUT_TYPE = "workout_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        int workoutTypeId = getIntent().getIntExtra(WORKOUT_TYPE, -1);

        TextView addExerciseText = findViewById(R.id.addExerciseText);
        addExerciseText.setOnClickListener(v -> showAddExerciseDialog());

        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> showDiscardWorkoutDialog());

        TextView timeText = findViewById(R.id.timeText);
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        timeText.setText(currentTime);
    }



    // Add Exercise Dialog

    private void showAddExerciseDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_exercise, null);

        EditText exerciseNameInput = dialogView.findViewById(R.id.exerciseNameInput);
        LinearLayout setSelectorContainer = dialogView.findViewById(R.id.setSelectorContainer);
        TextView cancelButton = dialogView.findViewById(R.id.cancelButton);
        TextView confirmButton = dialogView.findViewById(R.id.confirmButton);

        final int[] selectedSets = {3}; // Default selection

        // Create set selector items
        for (int i = 1; i <= 4; i++) {
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            itemLayout.setPadding(16, 0, 16, 0);

            TextView numberText = new TextView(this);
            numberText.setText(String.valueOf(i));
            numberText.setTextSize(18);
            numberText.setTextColor(getResources().getColor(R.color.white, null));
            numberText.setGravity(Gravity.CENTER);

            View circleView = new View(this);
            int size = (int) (16 * getResources().getDisplayMetrics().density); // Smaller circle
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.topMargin = 4;
            circleView.setLayoutParams(params);
            circleView.setBackgroundResource(i == selectedSets[0] ? R.drawable.circle_selected : R.drawable.circle_unselected);

            int finalI = i;
            itemLayout.setOnClickListener(v -> {
                selectedSets[0] = finalI;

                for (int j = 0; j < setSelectorContainer.getChildCount(); j++) {
                    LinearLayout item = (LinearLayout) setSelectorContainer.getChildAt(j);
                    View circle = item.getChildAt(1);
                    circle.setBackgroundResource(R.drawable.circle_unselected);
                }

                circleView.setBackgroundResource(R.drawable.circle_selected);
            });

            itemLayout.addView(numberText);
            itemLayout.addView(circleView);
            setSelectorContainer.addView(itemLayout);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Set custom background color
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(this, R.color.foreground_gray))
            );
        }

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        confirmButton.setOnClickListener(v -> {
            String exerciseName = exerciseNameInput.getText().toString().trim();
            int sets = selectedSets[0];

            if (!exerciseName.isEmpty()) {
                dialog.dismiss();

                // Container to insert into
                LinearLayout container = findViewById(R.id.exerciseCardContainer);

                // Card layout
                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setBackground(ContextCompat.getDrawable(this, R.drawable.card_background));
                card.setPadding(48, 48, 48, 48); // Your updated padding
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                cardParams.setMargins(0, 24, 0, 0);
                card.setLayoutParams(cardParams);

                // Exercise title
                TextView nameText = new TextView(this);
                nameText.setText(exerciseName);
                nameText.setTextColor(ContextCompat.getColor(this, R.color.white));
                nameText.setTextSize(18);
                nameText.setTypeface(null, Typeface.BOLD);
                card.addView(nameText);

                // Container for set rows
                LinearLayout setsContainer = new LinearLayout(this);
                setsContainer.setOrientation(LinearLayout.VERTICAL);
                setsContainer.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                card.addView(setsContainer);

                Runnable addSetRow = () -> {
                    LinearLayout row = new LinearLayout(this);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setGravity(Gravity.CENTER_VERTICAL);
                    row.setPadding(0, 16, 0, 0);

                    TextView setLabel = new TextView(this);
                    setLabel.setText("Set " + (setsContainer.getChildCount() + 1) + ": ");
                    setLabel.setTextColor(ContextCompat.getColor(this, R.color.white));
                    setLabel.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                    EditText weightInput = new EditText(this);
                    weightInput.setHint("Weight");
                    weightInput.setTextSize(13);
                    weightInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                    weightInput.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
                    weightInput.setTextColor(ContextCompat.getColor(this, R.color.white));
                    weightInput.setHintTextColor(ContextCompat.getColor(this, R.color.hint_gray));
                    weightInput.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                    EditText repsInput = new EditText(this);
                    repsInput.setHint("Reps");
                    repsInput.setTextSize(13);
                    repsInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                    repsInput.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
                    repsInput.setTextColor(ContextCompat.getColor(this, R.color.white));
                    repsInput.setHintTextColor(ContextCompat.getColor(this, R.color.hint_gray));
                    repsInput.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                    TextView removeBtn = new TextView(this);
                    removeBtn.setText("✕");
                    removeBtn.setTextColor(ContextCompat.getColor(this, R.color.hint_gray));
                    removeBtn.setTextSize(18);
                    removeBtn.setPadding(16, 0, 0, 0);

                    // OnClick logic with confirmation if edited
                    removeBtn.setOnClickListener(view -> {
                        String weightText = weightInput.getText().toString().trim();
                        String repsText = repsInput.getText().toString().trim();
                        boolean isEdited = !weightText.isEmpty() || !repsText.isEmpty();

                        int remainingSets = setsContainer.getChildCount();

                        Runnable updateSetLabels = () -> {
                            for (int i = 0; i < setsContainer.getChildCount(); i++) {
                                LinearLayout setRow = (LinearLayout) setsContainer.getChildAt(i);
                                TextView label = (TextView) setRow.getChildAt(0); // Assumes label is the first child
                                label.setText("Set " + (i + 1) + ": ");
                            }
                        };

                        if (remainingSets == 1) {
                            // If only one set remains, prompt to delete entire exercise
                            new AlertDialog.Builder(this)
                                    .setTitle("Delete Exercise")
                                    .setMessage("This is the last set. Do you want to delete the entire exercise?")
                                    .setPositiveButton("Delete", (dialogInterface, which) -> {
                                        // Remove the whole exercise card
                                        LinearLayout exerciseCardContainer = findViewById(R.id.exerciseCardContainer);
                                        View exerciseCard = (View) setsContainer.getParent(); // Card is parent of setsContainer
                                        exerciseCardContainer.removeView(exerciseCard);
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        } else {
                            Runnable removeAndUpdate = () -> {
                                setsContainer.removeView(row);
                                updateSetLabels.run();
                            };

                            if (isEdited) {
                                new AlertDialog.Builder(this)
                                        .setTitle("Delete Set")
                                        .setMessage("Are you sure you want to delete the set?")
                                        .setPositiveButton("Delete", (dialogInterface, which) -> removeAndUpdate.run())
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            } else {
                                removeAndUpdate.run();
                            }
                        }
                    });


                    row.addView(setLabel);
                    row.addView(weightInput);
                    row.addView(repsInput);
                    row.addView(removeBtn);

                    setsContainer.addView(row);
                };

                // Create the initial number of set rows
                for (int i = 0; i < sets; i++) {
                    addSetRow.run();
                }

                // Add "+" button
                TextView addRowButton = new TextView(this);
                addRowButton.setText("+ Add Set");
                addRowButton.setTextColor(ContextCompat.getColor(this, R.color.hint_gray));
                addRowButton.setTextSize(14);
                addRowButton.setPadding(0, 24, 0, 0);
                addRowButton.setOnClickListener(view -> addSetRow.run());

                card.addView(addRowButton);

                // Add to container
                container.addView(card);


            } else {
                Toast.makeText(this, "Exercise name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Change appearance of confirm button based on input
        exerciseNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = !s.toString().trim().isEmpty();
                confirmButton.setTextColor(ContextCompat.getColor(
                        WorkoutActivity.this,
                        hasText ? R.color.white : R.color.hint_gray
                ));
                confirmButton.setTypeface(null, hasText ? Typeface.BOLD : Typeface.NORMAL);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        dialog.show();
    }

    // End of Add Exercise


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showDiscardWorkoutDialog();}
    private void showDiscardWorkoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Discard Workout?")
                .setMessage("Are you sure you want to discard your workout?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }

}
package com.deltalog;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
    }



    // Add Exercise Dialog 49-147
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
                Toast.makeText(this, "Exercise: " + exerciseName + " | Sets: " + sets, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                // Add database/save logic here
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

    // Add Exercise Dialog 49-147



}
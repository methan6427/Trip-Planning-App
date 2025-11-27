package com.example.tripplanningapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity {
    private EditText titleEdit, locationEdit, dateEdit, notesEdit;
    private CheckBox reservationCheck;
    private RadioGroup categoryRadioGroup;
    private ImageView taskImageView;
    private SharedPreferencesHelper prefsHelper;
    private String taskId;
    private String imagePath = "";

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        prefsHelper = new SharedPreferencesHelper(this);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        android.net.Uri selectedImageUri = result.getData().getData();
                        getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        imagePath = selectedImageUri.toString();
                        taskImageView.setImageURI(selectedImageUri);
                        taskImageView.setVisibility(android.view.View.VISIBLE);
                    }
                });

        titleEdit = findViewById(R.id.titleEdit);
        locationEdit = findViewById(R.id.locationEdit);
        dateEdit = findViewById(R.id.dateEdit);
        notesEdit = findViewById(R.id.notesEdit);
        reservationCheck = findViewById(R.id.reservationCheck);
        categoryRadioGroup = findViewById(R.id.categoryRadioGroup);
        taskImageView = findViewById(R.id.taskImageView);
        Button updateButton = findViewById(R.id.updateButton);
        Button addImageButton = findViewById(R.id.addImageButton);

        taskId = getIntent().getStringExtra("task_id");
        String title = getIntent().getStringExtra("task_title");
        String location = getIntent().getStringExtra("task_location");
        String date = getIntent().getStringExtra("task_date");
        String notes = getIntent().getStringExtra("task_notes");
        boolean reservation = getIntent().getBooleanExtra("task_reservation", false);
        String category = getIntent().getStringExtra("task_category");
        imagePath = getIntent().getStringExtra("task_image_path");

        titleEdit.setText(title);
        locationEdit.setText(location);
        dateEdit.setText(date);
        notesEdit.setText(notes);
        reservationCheck.setChecked(reservation);

        if (imagePath != null && !imagePath.isEmpty()) {
            taskImageView.setImageURI(android.net.Uri.parse(imagePath));
            taskImageView.setVisibility(android.view.View.VISIBLE);
        }

        for (int i = 0; i < categoryRadioGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) categoryRadioGroup.getChildAt(i);
            if (rb.getText().toString().equals(category)) {
                rb.setChecked(true);
                break;
            }
        }

        dateEdit.setFocusable(false);
        dateEdit.setClickable(true);
        dateEdit.setOnClickListener(v -> showDatePicker());

        addImageButton.setOnClickListener(v -> openImageChooser());
        updateButton.setOnClickListener(v -> updateTask());
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    dateEdit.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void updateTask() {
        String title = titleEdit.getText().toString().trim();
        String location = locationEdit.getText().toString().trim();
        String date = dateEdit.getText().toString().trim();
        String notes = notesEdit.getText().toString().trim();
        boolean requiresReservation = reservationCheck.isChecked();

        int selectedId = categoryRadioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadio = findViewById(selectedId);
        String category = selectedRadio.getText().toString();

        if (title.isEmpty() || location.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        TaskModel task = new TaskModel(taskId, title, location, date, notes, requiresReservation, category, imagePath);
        prefsHelper.updateTask(task);
        Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}

package com.example.tripplanningapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;


public class AddTaskActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText titleEdit, locationEdit, dateEdit, notesEdit;
    private CheckBox reservationCheck;
    private RadioGroup categoryRadioGroup;
    private ImageView taskImageView;
    private SharedPreferencesHelper prefsHelper;
    private String selectedDate = "";
    private String imagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        prefsHelper = new SharedPreferencesHelper(this);

        titleEdit = findViewById(R.id.titleEdit);
        locationEdit = findViewById(R.id.locationEdit);
        dateEdit = findViewById(R.id.dateEdit);
        notesEdit = findViewById(R.id.notesEdit);
        reservationCheck = findViewById(R.id.reservationCheck);
        categoryRadioGroup = findViewById(R.id.categoryRadioGroup);
        Button saveButton = findViewById(R.id.saveButton);
        Button addImageButton = findViewById(R.id.addImageButton);
        taskImageView = findViewById(R.id.taskImageView);

        dateEdit.setFocusable(false);
        dateEdit.setClickable(true);
        dateEdit.setOnClickListener(v -> showDatePicker());

        addImageButton.setOnClickListener(v -> openImageChooser());
        saveButton.setOnClickListener(v -> saveTask());
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imagePath = selectedImageUri.toString();
            taskImageView.setImageURI(selectedImageUri);
            taskImageView.setVisibility(android.view.View.VISIBLE);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    dateEdit.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveTask() {
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

        TaskModel task = new TaskModel();
        task.setTitle(title);
        task.setLocation(location);
        task.setDate(date);
        task.setNotes(notes);
        task.setRequiresReservation(requiresReservation);
        task.setCategory(category);
        task.setImagePath(imagePath);

        prefsHelper.addTask(task);
        Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
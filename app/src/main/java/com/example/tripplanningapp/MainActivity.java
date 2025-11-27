package com.example.tripplanningapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private SharedPreferencesHelper prefsHelper;
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsHelper = new SharedPreferencesHelper(this);

        searchBar = findViewById(R.id.searchBar);
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadTasks();

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        List<TaskModel> tasks = prefsHelper.loadTasks();

        if (adapter == null) {
            adapter = new TaskAdapter(tasks);
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(task -> {
                Intent intent = new Intent(MainActivity.this, EditTaskActivity.class);
                intent.putExtra("task_id", task.getId());
                intent.putExtra("task_title", task.getTitle());
                intent.putExtra("task_location", task.getLocation());
                intent.putExtra("task_date", task.getDate());
                intent.putExtra("task_notes", task.getNotes());
                intent.putExtra("task_reservation", task.isRequiresReservation());
                intent.putExtra("task_category", task.getCategory());
                intent.putExtra("task_image_path", task.getImagePath());
                startActivity(intent);
            });

            adapter.setOnItemLongClickListener(task -> {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete Task")
                        .setMessage("Delete \"" + task.getTitle() + "\"?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            prefsHelper.deleteTask(task.getId());
                            loadTasks();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        } else {
            adapter.updateTasks(tasks);
        }
    }
}
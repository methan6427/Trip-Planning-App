package com.example.tripplanningapp;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesHelper {
    private static final String PREF_NAME = "JapanTripPrefs";
    private static final String KEY_TASKS = "tasks";
    private SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveTasks(List<TaskModel> tasks) {
        JSONArray jsonArray = new JSONArray();
        for (TaskModel task : tasks) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", task.getId());
                jsonObject.put("title", task.getTitle());
                jsonObject.put("location", task.getLocation());
                jsonObject.put("date", task.getDate());
                jsonObject.put("notes", task.getNotes());
                jsonObject.put("requiresReservation", task.isRequiresReservation());
                jsonObject.put("category", task.getCategory());
                jsonObject.put("imagePath", task.getImagePath());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        sharedPreferences.edit().putString(KEY_TASKS, jsonArray.toString()).apply();
    }

    public List<TaskModel> loadTasks() {
        List<TaskModel> tasks = new ArrayList<>();
        String tasksJson = sharedPreferences.getString(KEY_TASKS, "[]");
        try {
            JSONArray jsonArray = new JSONArray(tasksJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                TaskModel task = new TaskModel(
                        jsonObject.getString("id"),
                        jsonObject.getString("title"),
                        jsonObject.getString("location"),
                        jsonObject.getString("date"),
                        jsonObject.getString("notes"),
                        jsonObject.getBoolean("requiresReservation"),
                        jsonObject.getString("category"),
                        jsonObject.optString("imagePath", "")
                );
                tasks.add(task);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void addTask(TaskModel task) {
        List<TaskModel> tasks = loadTasks();
        tasks.add(task);
        saveTasks(tasks);
    }

    public void updateTask(TaskModel updatedTask) {
        List<TaskModel> tasks = loadTasks();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(updatedTask.getId())) {
                tasks.set(i, updatedTask);
                break;
            }
        }
        saveTasks(tasks);
    }

    public void deleteTask(String taskId) {
        List<TaskModel> tasks = loadTasks();
        tasks.removeIf(task -> task.getId().equals(taskId));
        saveTasks(tasks);
    }
}
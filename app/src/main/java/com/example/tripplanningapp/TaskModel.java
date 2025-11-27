package com.example.tripplanningapp;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskModel implements Parcelable {
    private String id;
    private String title;
    private String location;
    private String date;
    private String notes;
    private boolean requiresReservation;
    private String category;
    private String imagePath;

    public TaskModel() {
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public TaskModel(String id, String title, String location, String date, String notes, boolean requiresReservation, String category, String imagePath) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.notes = notes;
        this.requiresReservation = requiresReservation;
        this.category = category;
        this.imagePath = imagePath;
    }

    protected TaskModel(Parcel in) {
        id = in.readString();
        title = in.readString();
        location = in.readString();
        date = in.readString();
        notes = in.readString();
        requiresReservation = in.readByte() != 0;
        category = in.readString();
        imagePath = in.readString();
    }

    public static final Creator<TaskModel> CREATOR = new Creator<TaskModel>() {
        @Override
        public TaskModel createFromParcel(Parcel in) {
            return new TaskModel(in);
        }

        @Override
        public TaskModel[] newArray(int size) {
            return new TaskModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isRequiresReservation() {
        return requiresReservation;
    }

    public void setRequiresReservation(boolean requiresReservation) {
        this.requiresReservation = requiresReservation;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(location);
        dest.writeString(date);
        dest.writeString(notes);
        dest.writeByte((byte) (requiresReservation ? 1 : 0));
        dest.writeString(category);
        dest.writeString(imagePath);
    }
}

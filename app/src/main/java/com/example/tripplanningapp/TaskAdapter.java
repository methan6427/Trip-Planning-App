package com.example.tripplanningapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<TaskModel> tasks;
    private List<TaskModel> tasksFiltered;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(TaskModel task);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(TaskModel task);
    }

    public TaskAdapter(List<TaskModel> tasks) {
        this.tasks = tasks;
        this.tasksFiltered = new ArrayList<>(tasks);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }


    @Override
    public TaskViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder( TaskViewHolder holder, int position) {
        TaskModel task = tasksFiltered.get(position);
        holder.titleText.setText(task.getTitle());
        holder.locationText.setText(task.getLocation());
        holder.dateText.setText(task.getDate());
        holder.categoryText.setText(task.getCategory());

        String imagePath = task.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                holder.categoryImage.setImageURI(android.net.Uri.parse(imagePath));
            } catch (Exception e) {
                android.util.Log.e("TaskAdapter", "Error loading image", e);
                holder.categoryImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            holder.categoryImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        if (task.isRequiresReservation()) {
            holder.reservationText.setVisibility(View.VISIBLE);
        } else {
            holder.reservationText.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(task);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(task);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return tasksFiltered.size();
    }

    public void updateTasks(List<TaskModel> newTasks) {
        this.tasks = newTasks;
        this.tasksFiltered = new ArrayList<>(newTasks);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        tasksFiltered.clear();
        if (query.isEmpty()) {
            tasksFiltered.addAll(tasks);
        } else {
            String lowerQuery = query.toLowerCase();
            for (TaskModel task : tasks) {
                if (task.getTitle().toLowerCase().contains(lowerQuery) ||
                        task.getLocation().toLowerCase().contains(lowerQuery)) {
                    tasksFiltered.add(task);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView titleText;
        TextView locationText;
        TextView dateText;
        TextView categoryText;
        TextView reservationText;

        public TaskViewHolder( View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            titleText = itemView.findViewById(R.id.titleText);
            locationText = itemView.findViewById(R.id.locationText);
            dateText = itemView.findViewById(R.id.dateText);
            categoryText = itemView.findViewById(R.id.categoryText);
            reservationText = itemView.findViewById(R.id.reservationText);
        }
    }
}

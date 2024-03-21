package com.example.task_41p.adapter;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.task_41p.R;

public class TaskViewHolder extends RecyclerView.ViewHolder {

    TextView taskTitle;
    TextView taskDescription;
    TextView taskDueDate;
    TextView taskOverdueIndicator;


    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        taskTitle = itemView.findViewById(R.id.task_title);
        taskDescription = itemView.findViewById(R.id.task_description);
        taskDueDate = itemView.findViewById(R.id.task_due_date);
        taskOverdueIndicator = itemView.findViewById(R.id.task_overdue_indicator);

    }
}

package com.example.task_41p;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.task_41p.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Calendar;
import java.util.Locale;

public class TaskDetailActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dueDateEditText;
    private Button deleteButton;
    private Button saveButton;
    private FloatingActionButton editFab;
    public static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";
    private long taskId;
    private Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        titleEditText = findViewById(R.id.task_detail_title);
        descriptionEditText = findViewById(R.id.task_detail_description);
        dueDateEditText = findViewById(R.id.task_detail_due_date);
        deleteButton = findViewById(R.id.task_detail_delete_button);
        saveButton = findViewById(R.id.task_detail_save_button);
        editFab = findViewById(R.id.task_detail_edit_fab);

        dueDateEditText.setOnClickListener(v -> showDatePickerDialog());

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        taskId = getIntent().getIntExtra(EXTRA_TASK_ID, -1);
        if (taskId == -1) {
            finish();
            return;
        }

        taskViewModel.getTaskById(taskId).observe(this, task -> {
            if (task != null) {
                currentTask = task;
                titleEditText.setText(task.getTitle());
                descriptionEditText.setText(task.getDescription());
                dueDateEditText.setText(task.getDueDate());
            }
        });

        editFab.setOnClickListener(view -> toggleEditMode(true));
        saveButton.setOnClickListener(view -> saveTask());
        deleteButton.setOnClickListener(view -> deleteTask());
    }

    private void toggleEditMode(boolean isEditMode) {
        titleEditText.setEnabled(isEditMode);
        descriptionEditText.setEnabled(isEditMode);
        dueDateEditText.setEnabled(isEditMode);

        deleteButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        saveButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        editFab.setVisibility(isEditMode ? View.GONE : View.VISIBLE);
    }

    private void showDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    dueDateEditText.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveTask() {
        currentTask.setTitle(titleEditText.getText().toString());
        currentTask.setDescription(descriptionEditText.getText().toString());
        currentTask.setDueDate(dueDateEditText.getText().toString());
        taskViewModel.update(currentTask);
        toggleEditMode(false);
    }

    private void deleteTask() {
        taskViewModel.delete(currentTask);
        finish();
    }
}

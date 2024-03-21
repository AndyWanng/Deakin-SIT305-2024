package com.example.task_41p;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.task_41p.database.TaskRepository;
import com.example.task_41p.model.Task;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private LiveData<List<Task>> allTasks;
    private LiveData<List<Task>> sortedTasks;
    private TaskRepository repository;
    private MutableLiveData<Boolean> sortOrderAsc;

    public TaskViewModel(Application application) {
        super(application);
        repository = new TaskRepository(application);
        sortOrderAsc = new MutableLiveData<>(true);
    }
    public LiveData<List<Task>> getSortedTasks() {
        return sortedTasks;
    }
    public void toggleSortOrder() {
        Boolean currentOrder = sortOrderAsc.getValue();
        sortOrderAsc.setValue(currentOrder == null || !currentOrder);
    }

    public LiveData<List<Task>> getAllTasks() {
        return Transformations.switchMap(sortOrderAsc, order -> {
            if (Boolean.TRUE.equals(order)) {
                return repository.getTasksSortedByDateAsc();
            } else {
                return repository.getTasksSortedByDateDesc();
            }
        });
    }

    public void insert(Task task) {
        repository.insert(task);
    }
    public void update(Task task) {
        repository.update(task);
    }
    public void delete(Task task) {
        repository.delete(task);
    }
    public LiveData<Task> getTaskById(long taskId) {
        return repository.getTaskById(taskId);
    }

}

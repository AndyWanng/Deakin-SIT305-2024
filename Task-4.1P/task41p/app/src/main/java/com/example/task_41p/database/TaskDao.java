package com.example.task_41p.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import com.example.task_41p.model.Task;

@Dao
public interface TaskDao {


    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    LiveData<Task> getTaskById(long taskId);


    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    LiveData<List<Task>> getAllTasks();
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    LiveData<List<Task>> getTasksSortedByDateAsc();

    @Query("SELECT * FROM tasks ORDER BY dueDate DESC")
    LiveData<List<Task>> getTasksSortedByDateDesc();
}

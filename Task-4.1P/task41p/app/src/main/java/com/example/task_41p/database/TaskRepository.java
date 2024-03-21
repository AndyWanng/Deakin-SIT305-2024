package com.example.task_41p.database;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.example.task_41p.model.Task;
import java.util.List;

public class TaskRepository {

    private TaskDao taskDao;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<Task>> tasksSortedByDateAsc;
    private LiveData<List<Task>> tasksSortedByDateDesc;

    public TaskRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        taskDao = database.taskDao();
        allTasks = taskDao.getAllTasks();
    }
    public LiveData<List<Task>> getTasksSortedByDateAsc() {
        return taskDao.getTasksSortedByDateAsc();
    }

    public LiveData<List<Task>> getTasksSortedByDateDesc() {
        return taskDao.getTasksSortedByDateDesc();
    }

    public void insert(Task task) {
        new InsertTaskAsyncTask(taskDao).execute(task);
    }

    public void update(Task task) {
        new UpdateTaskAsyncTask(taskDao).execute(task);
    }

    public void delete(Task task) {
        new DeleteTaskAsyncTask(taskDao).execute(task);
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    private static class InsertTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;

        private InsertTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.insert(tasks[0]);
            return null;
        }
    }

    private static class UpdateTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;

        private UpdateTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.update(tasks[0]);
            return null;
        }
    }

    private static class DeleteTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;

        private DeleteTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.delete(tasks[0]);
            return null;
        }
    }

    public LiveData<Task> getTaskById(long taskId) {
        return taskDao.getTaskById(taskId);
    }

}

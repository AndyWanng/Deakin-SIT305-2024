package com.example.AskMe.sql;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.AskMe.MainActivity;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_CONVERSATIONS_TABLE = "CREATE TABLE conversations ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";

    private static final String CREATE_MESSAGES_TABLE = "CREATE TABLE messages ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "conversation_id INTEGER, "
            + "type INTEGER, "
            + "content TEXT, "
            + "FOREIGN KEY(conversation_id) REFERENCES conversations(id))";

    public MyDatabaseHelper(String name, int version) {
        super(MainActivity.Instance.getApplicationContext(), name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONVERSATIONS_TABLE);
        db.execSQL(CREATE_MESSAGES_TABLE);
        Log.i("MyDatabaseHelper", "Database created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS chatList");
            onCreate(db);
        }
    }
}

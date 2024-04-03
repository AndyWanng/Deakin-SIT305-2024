package com.example.task51c_youtube;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.task51c_youtube.entity.Playlist;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    public static void init(Context context) {
        instance = new DatabaseHelper(context);
    }

    public static DatabaseHelper getInstance() {
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, "YoutubePlaylist.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("drop Table if exists users");
        sqLiteDatabase.execSQL("drop Table if exists playlist");
        sqLiteDatabase.execSQL("CREATE TABLE users (username TEXT PRIMARY KEY, name TEXT, password TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE playlist (id INTEGER PRIMARY KEY, username TEXT, link TEXT NOT NULL, FOREIGN KEY (username) REFERENCES users(username))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop Table if exists users");
        sqLiteDatabase.execSQL("drop Table if exists playlist");
    }

    public Boolean insertUser(String username, String name, String password) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("name", name);
        contentValues.put("password", password);
        long result = DB.insert("users", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean verifyUser(String username, String password) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from users where username = ? and password = ?", new String[]{username, password});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean checkIfUserExists(String username) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from users where username = ?", new String[]{username});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("Range")
    public Playlist getUserPlayList(String username) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from playlist where username = ?", new String[]{username});
        Playlist playlist = new Playlist();
        playlist.setUserId(username);
        List<String> links = new ArrayList<>();
        while (cursor.moveToNext()) {
            links.add(cursor.getString(cursor.getColumnIndex("link")));
        }
        playlist.setLinks(links);
        return playlist;
    }

    public Boolean addToPlaylist(String username, String link) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("link", link);
        long result = DB.insert("playlist", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

}
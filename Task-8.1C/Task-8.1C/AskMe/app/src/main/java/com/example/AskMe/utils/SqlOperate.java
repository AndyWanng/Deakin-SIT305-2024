package com.example.AskMe.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.AskMe.sql.MyDatabaseHelper;

public class SqlOperate {
    private MyDatabaseHelper dbHelper;

    public SqlOperate() {
        dbHelper = new MyDatabaseHelper("chat.db", 2);
    }

    public long addConversation() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("timestamp", System.currentTimeMillis());
        long id = db.insert("conversations", null, values);
        return id;
    }

    public void addMessage(long conversationId, int type, String content) {
        // 确保使用正确的对话 ID
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("conversation_id", conversationId);
        contentValues.put("type", type);
        contentValues.put("content", content);
        db.insert("messages", null, contentValues);
    }

    public Cursor getAllConversations() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT * FROM conversations ORDER BY timestamp DESC", null);
    }

    public Cursor getMessages(long conversationId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query("messages", null, "conversation_id = ?", new String[] { String.valueOf(conversationId) }, null, null, "id ASC");
    }

    public void deleteConversation(long conversationId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("messages", "conversation_id = ?", new String[] { String.valueOf(conversationId) });
        db.delete("conversations", "id = ?", new String[] { String.valueOf(conversationId) });
    }
}

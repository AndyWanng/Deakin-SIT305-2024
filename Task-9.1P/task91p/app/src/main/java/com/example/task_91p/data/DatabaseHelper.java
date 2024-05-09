package com.example.task_91p.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.task_91p.model.Item;
import com.example.task_91p.util.Util;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, Util.DATABASE_NAME, factory, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_ITEM_TABLE = "CREATE TABLE " + Util.TABLE_NAME + "(" + Util.ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " + Util.POST_TYPE + " TEXT , " + Util.NAME + " TEXT , " + Util.PHONE + " TEXT , " + Util.DESCRIPTION + " TEXT , " +  Util.DATE + " TEXT , " + Util.LOCATION + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DROP_ITEM_TABLE = "DROP TABLE IF EXISTS '" + Util.TABLE_NAME + "'";
        sqLiteDatabase.execSQL(DROP_ITEM_TABLE);
        onCreate(sqLiteDatabase);
    }
    public long insertItem (Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.POST_TYPE, item.getPost_type());
        contentValues.put(Util.NAME, item.getName());
        contentValues.put(Util.PHONE,item.getPhone());
        contentValues.put(Util.DESCRIPTION,item.getDescription());
        contentValues.put(Util.DATE,item.getDate());
        contentValues.put(Util.LOCATION,item.getLocation());
        long newRowId = db.insert(Util.TABLE_NAME, null, contentValues);
        db.close();
        return newRowId;
    }

    public List<Item> fetchAllItems (){
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectAll = "select * from " + Util.TABLE_NAME;
        Cursor cursor1 = db.rawQuery(selectAll,null);
        if (cursor1.moveToFirst()) {
            do {
                Item item = new Item();
                item.setItem_id(cursor1.getInt(0));
                item.setPost_type(cursor1.getString(1));
                item.setName(cursor1.getString(2));
                item.setPhone(cursor1.getString(3));
                item.setDescription(cursor1.getString(4));
                item.setDate(cursor1.getString(5));
                item.setLocation(cursor1.getString(6));
                itemList.add(item);
            } while (cursor1.moveToNext());
        }
        return itemList;
    }
    public String fetchItem(int Item_id) {
        String postType, name, phone,description,date,location;
        String result = "0";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + Util.TABLE_NAME + " where " + Util.ITEM_ID + " = '" + Integer.toString(Item_id) + "'";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                postType = cursor.getString(1);
                name = cursor.getString(2);
                phone = Integer.toString(cursor.getInt(3));
                description = cursor.getString(4);
                date = cursor.getString(5);
                location = cursor.getString(6);
                result = postType + "\n Name: " + name + "\n Phone No. " + phone + "\n Description: " + description + "\n Date: " + date + "\n Location: " + location;
            }while(cursor.moveToNext());
        }
        db.close();
        return result;
    }

    public void deleteEntry(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM items where  item_id = '" + ID + "'";
        Cursor cursor = db.rawQuery(query,null);
        db.close();
    }
}

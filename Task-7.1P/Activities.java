package com.example.task_71p.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.task_71p.model.Item;
import com.example.task_71p.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper class for managing and manipulating database operations.
 * This class extends SQLiteOpenHelper.
 */
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
        String query = "DELETE FROM items WHERE item_id = ?";
        db.execSQL(query, new String[]{ID});
        db.close();
    }
}


package com.example.task_71p.model;

public class Item {
    private int item_id;
    private String name, description, location, date, post_type, phone;

    public Item() {
    }

    public Item(String name, String description, String location, String date, String post_type, String phone) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
        this.post_type = post_type;
        this.phone = phone;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}


package com.example.task_71p.util;

public class Util {
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME = "item_dp";
    public static final String TABLE_NAME = "items";

    public static final String ITEM_ID = "item_id";
    public static final String PHONE = "phone";
    public static final String POST_TYPE = "post_type";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String LOCATION = "location";
    public static final String DATE = "date";

}


package com.example.task_71p;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    Button newAdvert;
    Button showAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newAdvert = findViewById(R.id.newAdvert);
        showAll = findViewById(R.id.showAll);

        newAdvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewAdvertActivity.class);
                startActivity(intent);
            }
        });

        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this, ShowAllActivity.class);
                startActivity(intent1);
            }
        });

    }
}


package com.example.task_71p;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task_71p.data.DatabaseHelper;
import com.example.task_71p.model.Item;

import java.util.Calendar;
import java.util.Locale;

public class NewAdvertActivity extends AppCompatActivity {
    Button save;
    TextView name, phone, description, date, location2;
    boolean postTypeClicked = false;
    RadioButton lost, found;
    Item item;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_add);

        item = new Item();
        lost = findViewById(R.id.lost);
        found = findViewById(R.id.found);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone1);
        description = findViewById(R.id.description);
        date = findViewById(R.id.date);
        date.setOnClickListener(v -> showDatePickerDialog());
        location2 = findViewById(R.id.location2);
        save = findViewById(R.id.save);
        db = new DatabaseHelper(this);

        lost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postTypeClicked == false) {
                    postTypeClicked = true;
                    item.setPost_type("Lost");

                } else if (postTypeClicked && item.getPost_type().equals("Found")) {
                    lost.setChecked(false);
                    Toast.makeText(NewAdvertActivity.this, "Found already checked", Toast.LENGTH_LONG).show();
                    item.setPost_type("Found");
                } else if (postTypeClicked && item.getPost_type().equals("Lost")) {
                    lost.setChecked(false);
                    postTypeClicked = false;
                    item.setPost_type("");
                }
            }
        });

        found.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postTypeClicked == false) {
                    postTypeClicked = true;
                    item.setPost_type("Found");
                } else if (postTypeClicked && item.getPost_type().equals("Lost")) {
                    found.setChecked(false);
                    Toast.makeText(NewAdvertActivity.this, "Lost already checked", Toast.LENGTH_LONG).show();
                    item.setPost_type("Lost");
                } else if (postTypeClicked && item.getPost_type().equals("Found")) {
                    found.setChecked(false);
                    postTypeClicked = false;
                    item.setPost_type("");
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (name.getText().toString().isEmpty()) {
                        Toast.makeText(NewAdvertActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                    } else {
                        item.setName(name.getText().toString());
                    }

                    if (phone.getText().toString().isEmpty()) {
                        Toast.makeText(NewAdvertActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                    } else {
                        item.setPhone(phone.getText().toString());
                    }

                    if (description.getText().toString().isEmpty()) {
                        Toast.makeText(NewAdvertActivity.this, " please enter a description", Toast.LENGTH_SHORT).show();
                    } else {
                        item.setDescription(description.getText().toString());
                    }

                    if (date.getText().toString().isEmpty()) {
                        Toast.makeText(NewAdvertActivity.this, "Please enter date", Toast.LENGTH_SHORT).show();
                        showDatePickerDialog();
                    } else {
                        item.setDate(date.getText().toString());
                    }
                    if (location2.getText().toString().isEmpty()) {
                        Toast.makeText(NewAdvertActivity.this, "Please enter the location where item was found or lost at", Toast.LENGTH_SHORT).show();
                    } else {
                        item.setLocation(location2.getText().toString());
                    }
                    if (postTypeClicked == false) {
                        Toast.makeText(NewAdvertActivity.this, "Please select Post Type", Toast.LENGTH_SHORT).show();
                    }


                    boolean allTrue = !name.getText().toString().isEmpty() && !phone.getText().toString().isEmpty() && !description.getText().toString().isEmpty() && !date.getText().toString().isEmpty() && !location2.getText().toString().isEmpty() && !(item.getPost_type().isEmpty() || item.getPost_type().equals(""));
                    if (postTypeClicked && allTrue) {

                        postTypeClicked = false;
                        long result = db.insertItem(item);

                        if (result > 0) {

                            Intent intent = new Intent(NewAdvertActivity.this, MainActivity.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(NewAdvertActivity.this, "didn't get saved", Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (Exception e) {
                    Log.d("reached", e.getMessage());
                }
            }
        });
    }
    private void showDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    date.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }
}


package com.example.task_71p;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task_71p.data.DatabaseHelper;

public class RemoveItemActivity extends AppCompatActivity {
    TextView details;
    Button remove;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.remove_item);

        details = findViewById(R.id.details);
        remove = findViewById(R.id.remove);
        db = new DatabaseHelper(this);

        Intent intent = getIntent();
        int ITEM_ID = intent.getIntExtra("position",0);

        try{
            String detail= db.fetchItem(ITEM_ID);
            details.setText(detail);
        }catch(Exception e){
            Log.d("REACHED",e.getMessage());
        }

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    db.deleteEntry(Integer.toString(ITEM_ID));
                    Intent intent1 = new Intent(RemoveItemActivity.this,MainActivity.class);
                    startActivity(intent1);
                }catch (Exception e){
                    Log.d("REACHED",e.getMessage());
                }

            }
        });
    }
}


package com.example.task_71p;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.task_71p.data.DatabaseHelper;
import com.example.task_71p.model.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowAllActivity extends AppCompatActivity {
    ListView itemsListView;
    ImageButton sortButton;
    ArrayList<String> itemArrayList;
    ArrayAdapter<String> adapter;
    List<Item> itemList = new ArrayList<>();
    boolean isSortedAscending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_all);

        itemsListView = findViewById(R.id.itemsListView);
        sortButton = findViewById(R.id.sortButton);
        itemArrayList = new ArrayList<>();
        DatabaseHelper db = new DatabaseHelper(this);

        itemList = db.fetchAllItems();

        updateListView(itemList);
        setupSearchFilter();

        itemsListView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                Intent intent = new Intent(ShowAllActivity.this, RemoveItemActivity.class);
                String selectedDescription = itemArrayList.get(position);
                for (Item item : itemList) {
                    if (item.getDescription().equals(selectedDescription)) {
                        int ITEM_ID = item.getItem_id();
                        intent.putExtra("position", ITEM_ID);
                        break;
                    }
                }
                startActivity(intent);
            } catch (Exception e) {
                Log.d("ERROR", e.getMessage());
            }
        });

        sortButton.setOnClickListener(v -> {
            isSortedAscending = !isSortedAscending;
            if (isSortedAscending) {
                Collections.sort(itemList, (i1, i2) -> i1.getDate().compareTo(i2.getDate()));
            } else {
                Collections.sort(itemList, (i1, i2) -> i2.getDate().compareTo(i1.getDate()));
            }
            updateListView(itemList);
        });
    }

    private void updateListView(List<Item> itemList) {
        itemArrayList.clear();
        for (Item item : itemList) {
            itemArrayList.add(item.getDescription());
        }
        if (adapter == null) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemArrayList);
            itemsListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void setupSearchFilter() {
        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        List<Item> filteredList = new ArrayList<>();
        for (Item item : itemList) {
            if (item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        updateListView(filteredList);
    }

}

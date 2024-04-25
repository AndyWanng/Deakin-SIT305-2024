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
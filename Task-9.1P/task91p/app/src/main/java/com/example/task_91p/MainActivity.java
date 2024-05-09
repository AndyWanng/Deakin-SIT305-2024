package com.example.task_91p;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.places.api.Places;

public class MainActivity extends AppCompatActivity {
    Button newAdvert;
    Button showAll;
    Button showOnMapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newAdvert = findViewById(R.id.newAdvert);
        showAll = findViewById(R.id.showAll);
        showOnMapBtn = findViewById(R.id.showOnMapBtn);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCqmbNESFmZpbSQPw1s7-LWbYyOGfMNCAE");
        }

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

        showOnMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, DisplayMap.class);
                startActivity(intent2);
            }
        });
    }
}

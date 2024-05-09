package com.example.task_91p;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task_91p.data.DatabaseHelper;

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


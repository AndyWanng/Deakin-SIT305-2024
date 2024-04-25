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

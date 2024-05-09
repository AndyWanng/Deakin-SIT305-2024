package com.example.AskMe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.AskMe.chatView.ChatListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.InvocationTargetException;

public class MainActivity extends AppCompatActivity {

    public FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;
    public static MainActivity Instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Instance = this;
        setContentView(R.layout.activity_main);
        AppPermission.requestPermissions(this);

        frameLayout = findViewById(R.id.frameLayout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_chat:
                        loadChatListFragment();
                        return true;
                }
                return false;
            }
        });

        loadChatListFragment();

        showNameInputDialog();
    }

    private void loadChatListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new ChatListFragment());
        fragmentTransaction.commit();
    }

    public void loadSingleAppActivity(Class appClass) {
        try {


            frameLayout.removeAllViews();
            Object viewRes = appClass.getMethod("Load", Context.class).invoke(null, MainActivity.this);
            frameLayout.addView(((View) viewRes));
        }
        catch (NoSuchMethodException e){
            Log.e("NoSuchMethodException","exec app "+appClass.toString());
        } catch (InvocationTargetException e) {
            Log.e("NoSuchMethodException","exec app "+appClass.toString());
        } catch (IllegalAccessException e) {
            Log.e("NoSuchMethodException","exec app "+appClass.toString());
        }

    }

    private void saveUserName(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserName", name);
        editor.apply();
    }

    private void showNameInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter your name here");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                saveUserName(name);
            }
        });

        builder.setCancelable(false);

        builder.show();
    }
}

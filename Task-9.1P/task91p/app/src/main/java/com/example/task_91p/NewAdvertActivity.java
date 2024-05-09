package com.example.task_91p;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.task_91p.data.DatabaseHelper;
import com.example.task_91p.model.Item;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
public class NewAdvertActivity extends AppCompatActivity {
    Button save, getCurrentLocationBtn;
    TextView name, phone, description, date, location2;
    boolean postTypeClicked = false;
    RadioButton lost, found;
    Item item;
    DatabaseHelper db;
    private FusedLocationProviderClient fusedLocationClient;
    PlacesClient placesClient;

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
        getCurrentLocationBtn = findViewById(R.id.getCurrentLocationBtn);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        placesClient = Places.createClient(this);
        location2.setOnClickListener(v -> startAutocompleteIntent());
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

                try{
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
                        Toast.makeText(NewAdvertActivity.this, "Please select Post Type", Toast.LENGTH_SHORT).show();}


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

                }catch (Exception e){
                    Log.d("reached",e.getMessage());
                }
            }
        });

        getCurrentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(NewAdvertActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    ActivityCompat.requestPermissions(NewAdvertActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 0);
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

    private final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            (ActivityResultCallback<ActivityResult>) result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);

                        Log.d("reached", "Place: " + place.getAddressComponents());
                        fillLocation(place);
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    Log.d("reached", "User canceled autocomplete");
                }
            });



    private void startAutocompleteIntent() {
        List<String> countries = new ArrayList<String>();
        countries.add("AU");
        List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS_COMPONENTS,
                Place.Field.LAT_LNG, Place.Field.VIEWPORT);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setCountries(countries)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build(this);
        startAutocomplete.launch(intent);
    }

    private void fillLocation(Place place) {

        AddressComponents components = place.getAddressComponents();
        StringBuilder address = new StringBuilder();
        if (components != null) {
            for (AddressComponent component : components.asList()) {
                String type = component.getTypes().get(0);
                switch (type) {
                    case "sublocality_level_3": {
                        address.insert(0, component.getName());
                        break;
                    }
                    case "sublocality_level_2": {
                        address.append(" ").append(component.getName());
                        break;
                    }

                    case "sublocality_level_1": {
                        address.append(", ");
                        address.append(component.getShortName());
                        break;
                    }

                    case "locality": {
                        address.append(", ");
                        address.append(component.getName());
                        break;
                    }

                    case "administrative_area_level_2": {
                        address.append(", ");
                        address.append(component.getName());
                        break;
                    }

                    case "administrative_area_level_1":
                        address.append(", ");
                        address.append(component.getName());
                        break;

                    case "country": {
                        address.append(", ");
                        address.append(component.getName());
                        break;
                    }


                    case "postal_code": {
                        address.append(", ");
                        address.append(component.getName());
                        break;
                    }
                }
            }
        }

        location2.setText(address.toString());
    }


    public void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            Double latitude = location.getLatitude();
                            Double longitude = location.getLongitude();
                            try {
                                Geocoder geocoder;
                                List<Address> addresses;
                                geocoder = new Geocoder(NewAdvertActivity.this, Locale.getDefault());

                                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                                String addressx = addresses.get(0).getAddressLine(0);
                                location2.setText(addressx);
                            }catch (IOException e){
                                Log.d("reached",e.getMessage());
                            }
                        }
                    }
                });
    }

}
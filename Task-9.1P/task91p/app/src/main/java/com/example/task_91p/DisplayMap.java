package com.example.task_91p;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import com.example.task_91p.data.DatabaseHelper;
import com.example.task_91p.model.Item;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DisplayMap extends FragmentActivity implements OnMapReadyCallback {
    private List<Item> itemList;
    private DatabaseHelper db;
    private GoogleMap map;
    private List<LatLng> latLngList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);

        db = new DatabaseHelper(DisplayMap.this);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        new FetchItemsTask().execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (latLngList != null) {
            for (LatLng latLng : latLngList) {
                map.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            }
            if (!latLngList.isEmpty()) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(0), 10.0f));
            }
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            itemList = db.fetchAllItems();
            latLngList = new ArrayList<>();
            if (itemList != null) {
                Geocoder coder = new Geocoder(DisplayMap.this);
                for (Item item : itemList) {
                    LatLng latLng = getLocationFromAddress(coder, item.getLocation());
                    if (latLng != null) {
                        latLngList.add(latLng);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (map != null && !latLngList.isEmpty()) {
                for (LatLng latLng : latLngList) {
                    map.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                }
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(0), 10.0f));
            }
        }
    }

    private LatLng getLocationFromAddress(Geocoder coder, String strAddress) {
        try {
            List<Address> address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.isEmpty()) {
                return null;
            }
            Address location = address.get(0);
            return new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException e) {
            Log.e("getLocationFromAddress", "Failed to get location", e);
            return null;
        }
    }
}


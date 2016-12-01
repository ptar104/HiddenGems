package com.capstone.petros.hiddengems;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HashMap<Marker, GemInformation> mapForGemInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapForGemInformation = new HashMap<>();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Mckeldin and move the camera
        LatLng mcK = new LatLng(38.985910, -76.945281);
        Marker mckMarker = mMap.addMarker(new MarkerOptions()
                .position(mcK)
                .title("The Library")
                .snippet("We Read Here"));


        // Temp Marker for Ikes Pizza
        LatLng ikePizza = new LatLng(38.985910, -76.943);
        Marker ikePizzaMarker = mMap.addMarker(new MarkerOptions()
                .position(ikePizza)
                .title("Ike's Pizza")
                .snippet("We Eat Here"));

        // Zoom to Mckeldin Position
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(mcK, 16);
        mMap.animateCamera(yourLocation);
    }
}

package com.capstone.petros.hiddengems;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    final int CREATE_GEM = 1337;

    final private String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        this.getWindow().setStatusBarColor(getColor(R.color.colorPrimaryDark));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Button addGemButton = (Button)findViewById(R.id.addGemButton);
        addGemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, AddGemActivity.class);

                startActivityForResult(intent, CREATE_GEM);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_OK) {
            // Notify data changed, update map
        }
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
        mMap.setOnMarkerClickListener(this);

        // Add a marker in Ike's Pizza and move the camera
        LatLng mcK = new LatLng(38.991090, -76.934092); // Static hard-coded "Ike's pizza"
        mMap.addMarker(new MarkerOptions().position(mcK).title("Ike's Pizza").icon(BitmapDescriptorFactory.fromResource(R.drawable.gem)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mcK));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mcK, 18.0f));
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Log.i(TAG, "Tapped marker");

        Intent intent  = new Intent(MapsActivity.this, GemInfoActivity.class);

        startActivity(intent);

        return true;
    }
}

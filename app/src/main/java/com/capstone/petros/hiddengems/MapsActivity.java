package com.capstone.petros.hiddengems;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    final int CREATE_GEM = 1337;

    final private String TAG = "MapsActivity";

    PopupWindow _popupWindow;
    ArrayList<GemInformation> gems = new ArrayList<GemInformation>(); // Temporary datastore for all gems - not persistent

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

        if (requestCode == CREATE_GEM && resultCode == RESULT_OK) {
            // Notify data changed, update map
            GemInformation newGem = (GemInformation)data.getSerializableExtra("newGem");
            LatLng newGemLocation = newGem.getLocation();

            this.gems.add(newGem);

            mMap.addMarker(new MarkerOptions().position(newGemLocation).title(newGem.getGemName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.gem))); // TODO: Change to shiny gem up until createdTime reaches x seconds
            mMap.moveCamera(CameraUpdateFactory.newLatLng(newGemLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newGemLocation, 18.0f));
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

        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.gem_popup, null);
        _popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        // Need a view as an anchor- was button before, not sure how to use Marker
        View map = findViewById(R.id.map);
        _popupWindow.showAsDropDown(map, 50, -20);

        return true;
    }

    public void onMoreInfoClick(View v) {
        Intent intent  = new Intent(MapsActivity.this, GemInfoActivity.class);


        // TODO: Determine which gem is clicked - may be a costly process of iterating through all existing gems and finding a matching location
        startActivity(intent);
    }

    public void onButtonClickClose(View v) {
        _popupWindow.dismiss();
    }
}

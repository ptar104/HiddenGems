package com.capstone.petros.hiddengems;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    final int CREATE_GEM = 1337;

    final private String TAG = "MapsActivity";

    PopupWindow _popupWindow;
    PopupWindow _keyWindow;
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
        } else if (resultCode == RESULT_CANCELED) {
            if (data != null && data.hasExtra("updatedGem")) {
                // TODO: update gem
                GemInformation gem = (GemInformation) data.getSerializableExtra("updatedGem");
            }
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

        initDemoGems();
    }

    public void onKeyClick(View v){
        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.map_key, null);
        _keyWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        // Show in center
        _keyWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

    }

    public void onKeyClickClose(View v) {
        _keyWindow.dismiss();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Log.i(TAG, "Tapped marker with location: " + marker.getPosition()); // TODO: Decide whether we want this approach or assign UUID to each gem and add to marker as tag

        // Dismiss popup before opening new one
        if (_popupWindow != null) {
            _popupWindow.dismiss();
        }
        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.gem_popup, null);
        _popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView gemName = (TextView) popupView.findViewById(R.id.titleText);
        TextView subTitleText = (TextView) popupView.findViewById(R.id.titleText);

//        gemName.setText();

        // Set position as tag on moreInfoButton
        Button moreInfoButton = (Button) popupView.findViewById(R.id.moreButton);
        moreInfoButton.setTag(marker.getPosition());

        View map = findViewById(R.id.map);
        // Show in center for now
        _popupWindow.showAtLocation(map, Gravity.CENTER, 0, 0);

        return true;
    }

    public void onMoreInfoClick(View v) {
        Intent intent  = new Intent(MapsActivity.this, GemInfoActivity.class);

        LatLng gemLocation = (LatLng) v.getTag();

        Log.i(TAG, "Got LatLng: " + gemLocation);
        // Find matching gem
        GemInformation match = null;
        for (GemInformation currGem : this.gems) {
            Log.i(TAG, currGem.getGemName() + currGem.getLocation());
            if (currGem.getLocation().toString().compareTo(gemLocation.toString()) == 0) { // Match found
                Log.i(TAG, "Match found: " + currGem.getGemName());
                match = currGem;
                break;
            }
        }

        if (match != null) {
            intent.putExtra("currGem", match);
            startActivity(intent);
        }

        // TODO: Determine which gem is clicked - may be a costly process of iterating through all existing gems and finding a matching location

    }

    public void onButtonClickClose(View v) {
        _popupWindow.dismiss();
    }

    public void initDemoGems() {
        // Spawn Ike's Pizza Gem
        ArrayList<GemInformation.Category> categories = new ArrayList<>();
        categories.add(GemInformation.Category.RESTAURANT);

        GemInformation gem = new GemInformation(4, "Yummy!", "Ike's pizza has been a standby in DC for over " +
                "20 years", categories, 38.985910, -76.943);

        gem.setGemName("Ike's Pizza");
        gem.addReview("Better than my mom's food");
        gem.addReview("You HAVE to check out this place.");
        gem.addReview("#tbt Napoli");

        Log.i(TAG, "Initialized Ike's with" +  gem.getLocation());
        this.gems.add(gem);

        for (GemInformation currGem : this.gems) {
            mMap.addMarker(new MarkerOptions().position(currGem.getLocation()).title(currGem.getGemName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.gem)));
            // Add a marker in Ike's Pizza and move the camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currGem.getLocation()));
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(this.gems.get(0).getLocation(), 18.0f)); // Show Ike's
    }
}

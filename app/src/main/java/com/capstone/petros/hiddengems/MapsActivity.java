package com.capstone.petros.hiddengems;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    final int CREATE_GEM = 1337;
    final int REVIEW_ADDED = 2;


    final private String TAG = "MapsActivity";

    PopupWindow _popupWindow;
    PopupWindow _keyWindow;
    ArrayList<GemInformation> gems = new ArrayList<GemInformation>(); // Temporary datastore for all gems - not persistent
    static GemInformation currGem;

    // Location stuff
    LocationManager locationManager = null;
    LocationListener locationListener;
    Location currUserLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // The default staring location:
        currUserLoc = new Location("");
        currUserLoc.setLatitude(38.991090);
        currUserLoc.setLongitude(-76.934092);

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

        // Add the location updates here.
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if(location!=null){
                    currUserLoc = location;
                    populateGems();
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
    }
    protected void onStop(){
        super.onStop();
        try {
            locationManager.removeUpdates(locationListener);
        } catch (SecurityException e) {
            // User does not have location on - that is quite essential to app tho!
            Toast.makeText(this, "Please allow this application to access your location data",
                    Toast.LENGTH_LONG);
        }
        locationManager = null;
    }

    protected void onResume() {
        super.onResume();
        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        // Every 10 seconds, more than 20 meters (Don't want to update too often to help conserve battery)
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 20, locationListener);
        }
        catch (SecurityException e) {
            Toast.makeText(this, "Please allow this application to access your location data",
                    Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // NOTE: since I(Peter) added bitmaps to GemInformation, it is no longer
        // serializable, as bitmaps are not serializable by default.
        // Instead, I am using the static "currGem" field to pass the gem around.
        // And activity can get and set the currGem, and this can be used to edit the gem
        // in question directly in the functions, and pass a gem around.

        // I am commenting out, not deleting, the "Intent Extra" code, in case we need it later.

        if (requestCode == CREATE_GEM && resultCode == RESULT_OK) {
            // Notify data changed, update map
            //GemInformation newGem = (GemInformation)data.getSerializableExtra("newGem");
            LatLng newGemLocation = currGem.getLocation();

            this.gems.add(currGem);

            populateGems(); // TODO: shiny gems
            mMap.moveCamera(CameraUpdateFactory.newLatLng(newGemLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newGemLocation, 18.0f));

        // Save review of gem after added.
        } else if (requestCode == REVIEW_ADDED && resultCode == RESULT_OK) {
            // If the gem was updated, it would be changed directly now.
            /*
            GemInformation updatedGem = (GemInformation)data.getSerializableExtra("updatedGem");
            int index = 0;

            if (updatedGem != null) {
                for (int i = 0; i < gems.size(); i++) {
                    if (updatedGem.getLocation().toString().compareTo(gems.get(i).toString()) == 0) {
                        index = i;
                        break;
                    }
                }
                gems.add(index, updatedGem);
            }
            */
        } else if (resultCode == RESULT_CANCELED) {
            // See above...
            /*
            if (data != null && data.hasExtra("updatedGem")) {
                // TODO: update gem
                GemInformation gem = (GemInformation) data.getSerializableExtra("updatedGem");
            }
            */
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

        GemInformation match = findGemWithLocation(marker.getPosition());

        if (match == null) {
            Toast.makeText(MapsActivity.this, "Gem not found. Please try again later.", Toast.LENGTH_SHORT).show();
            return false;
        }

        TextView gemName = (TextView) popupView.findViewById(R.id.titleText);
        TextView subTitleText = (TextView) popupView.findViewById(R.id.subTitleText);

        gemName.setText(match.getGemName());
        String subtitle = match.getQuickDescription() + " - ";
        if(match.getPrice() != 0){
            for(int i = 0; i < match.getPrice(); i++)
                subtitle += "$";
            subtitle += " - ";
        }
        Location gemLoc = new Location("");
        gemLoc.setLatitude(match.getLocation().latitude);
        gemLoc.setLongitude(match.getLocation().longitude);
        float distanceInMeters = currUserLoc.distanceTo(gemLoc);
        float distanceInMiles = 0.000621371f * distanceInMeters;
        String distanceString = String.format("%.2f",distanceInMiles);
        subtitle += distanceString + " mi";
        subTitleText.setText(subtitle);

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
        GemInformation match = findGemWithLocation(gemLocation);

        _popupWindow.dismiss();

        if (match != null) {
            //intent.putExtra("currGem", match);
            setCurrGem(match);
            startActivityForResult(intent, REVIEW_ADDED);
        }

        // TODO: Determine which gem is clicked - may be a costly process of iterating through all existing gems and finding a matching location

    }

    public GemInformation findGemWithLocation(LatLng gemLocation) {
        GemInformation match = null;
        for (GemInformation currGem : this.gems) {
            Log.i(TAG, currGem.getGemName() + currGem.getLocation());
            if (currGem.getLocation().toString().compareTo(gemLocation.toString()) == 0) { // Match found
                Log.i(TAG, "Match found: " + currGem.getGemName());
                match = currGem;
                break;
            }
        }
        return match;
    }

    public void onButtonClickClose(View v) {
        _popupWindow.dismiss();
    }

    public void initDemoGems() {
        // Spawn Ike's Pizza Gem
        GemInformation.Category category = GemInformation.Category.RESTAURANT;

        // Ike's Pizza
        GemInformation gem = new GemInformation(4, "Yummy!", "Ike's pizza has been a standby in DC for over " +
                "20 years", "Casual Pizza", category, 38.991090, -76.934092, 2);

        gem.setGemName("Ike's Pizza");
        gem.addReview("Better than my mom's food");
        gem.addReview("You HAVE to check out this place.");
        gem.addReview("#tbt Napoli");

        gem.setBitmap1(BitmapFactory.decodeResource(getResources(), R.drawable.pizza1));
        gem.setBitmap2(BitmapFactory.decodeResource(getResources(), R.drawable.pizza2));

        Log.i(TAG, "Initialized Ike's with" +  gem.getLocation());
        this.gems.add(gem);

        GemInformation gem2 = new GemInformation(5, "A lot of memories!", "Great place for nostalgia!",
                "Historical Landmark", GemInformation.Category.HISTORIC, 38.991890, -76.93403, 1);
        gem2.setGemName("CP Monument");
        gem2.addReview("Fun to play at");
        gem2.addReview("Brings back memories");
        gem2.setBitmap1(BitmapFactory.decodeResource(getResources(), R.drawable.monument1));
        gem2.setBitmap2(BitmapFactory.decodeResource(getResources(), R.drawable.monument2));
        this.gems.add(gem2);

        GemInformation gem3 = new GemInformation(4, "Great place to dance", "Best place to dance in the area!",
                "Dance Club", GemInformation.Category.ENTERTAINMENT, 38.991690, -76.93491, 3);
        gem3.setGemName("Dance NOW!");
        gem3.addReview("Great place to have fun. Great people.");
        gem3.addReview("I met my boyfriend here!!");
        gem3.setBitmap1(BitmapFactory.decodeResource(getResources(), R.drawable.dance1));
        gem3.setBitmap2(BitmapFactory.decodeResource(getResources(), R.drawable.dance2));
        this.gems.add(gem3);


        // McKeldin Library
        GemInformation gem4 = new GemInformation(5, "I love the Starbucks inside the footnotes cafe!", "The biggest library on campus with an abundance of helpful resources for optimized performance in school." +
                " 20 years", "Campus hub library", GemInformation.Category.OTHER, 38.991288, -76.9347, 0);

        gem4.setGemName("McKeldin Library");
        gem4.addReview("They don't accept Starbucks gift cards...");
        gem4.addReview("2nd floor is too loud to get anything done unless it's a group project");
        gem4.addReview("The 3D printer here is affordable, and they have many regular printers too!");
        gem4.addReview("This place saved me so many times - you can borrow macbook chargers and bike pumps.");
        gem4.setBitmap1(BitmapFactory.decodeResource(getResources(), R.drawable.library1));
        gem4.setBitmap2(BitmapFactory.decodeResource(getResources(), R.drawable.library2));
        this.gems.add(gem4);

        populateGems();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(this.gems.get(0).getLocation(), 18.0f)); // Show Ike's
    }

    public void populateGems(){
        mMap.clear();
        for (GemInformation currGem : this.gems) {
            // Only add if it's within a mile
            Location gemLoc = new Location("");
            gemLoc.setLatitude(currGem.getLocation().latitude);
            gemLoc.setLongitude(currGem.getLocation().longitude);
            if(currUserLoc.distanceTo(gemLoc) <= 1609.34) { // Returns in meters
                BitmapDescriptor gemIcon = null;
                switch(currGem.getCategory()){
                    case RESTAURANT:
                        gemIcon = BitmapDescriptorFactory.fromResource(R.drawable.color_ruby);
                        break;
                    case HISTORIC:
                        gemIcon = BitmapDescriptorFactory.fromResource(R.drawable.color_tallgem);
                        break;
                    case ENTERTAINMENT:
                        gemIcon = BitmapDescriptorFactory.fromResource(R.drawable.color_fatgem);
                        break;
                    case OTHER:
                        gemIcon = BitmapDescriptorFactory.fromResource(R.drawable.color_diamond);
                        break;
                }
                mMap.addMarker(new MarkerOptions().position(currGem.getLocation()).title(currGem.getGemName()).icon(gemIcon));
            }
        }
    }

    public static void setCurrGem(GemInformation gem){
        currGem = gem;
    }

    public static GemInformation getCurrGem(){
        return currGem;
    }


}

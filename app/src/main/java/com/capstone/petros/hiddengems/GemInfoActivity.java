package com.capstone.petros.hiddengems;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import android.net.Uri;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class GemInfoActivity extends AppCompatActivity {
    Activity act = this;
    ListView reviewsList;
    final int CREATE_REVIEW = 828;
    GemInformation currGem = null;
    ArrayAdapter mAdapter;
    ArrayList<String> reviews = new ArrayList<String>();

    // Location stuff
    LocationManager locationManager = null;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gem_info);

        reviewsList = (ListView)findViewById(R.id.reviewsList);
        mAdapter = new ArrayAdapter<String>(this, R.layout.review_item, reviews);
        reviewsList.setAdapter(mAdapter);

        // Initialize currGem reference
        currGem = (GemInformation)getIntent().getSerializableExtra("currGem");

        // Populate reviews
        if (currGem.getReviews().size() > 0) {
            reviews.addAll(currGem.getReviews());
        }

        mAdapter.notifyDataSetChanged();

//        //Update the UI...
        TextView title = (TextView)findViewById(R.id.textViewGemTitle);
        title.setText(currGem.getGemName().toUpperCase());

        // Set typeface
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Lato-Semibold.ttf");
        title.setTypeface(type);

        TextView quickInfo = (TextView)findViewById(R.id.textViewQuickInfo);
        String quickInfoText = currGem.getQuickDescription() + " - ";
        if(currGem.getPrice() != 0){
            for(int i = 0; i < currGem.getPrice(); i++)
                quickInfoText += "$";
            quickInfoText += " - ";
        }
        quickInfoText += "[Getting Location...] mi"; //Until the location callback gets the location
        quickInfo.setText(quickInfoText);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if(location!=null){
                    TextView quickInfo = (TextView)findViewById(R.id.textViewQuickInfo);
                    String currentText = (String)quickInfo.getText();
                    currentText = currentText.substring(0,currentText.lastIndexOf("-")+1); //cut off the end
                    Location gemLoc = new Location("");
                    gemLoc.setLatitude(currGem.getLocation().latitude);
                    gemLoc.setLongitude(currGem.getLocation().longitude);
                    float distanceInMeters = location.distanceTo(gemLoc);
                    float distanceInMiles = 0.000621371f * distanceInMeters;
                    String distanceString = String.format("%.2f",distanceInMiles);
                    currentText += " "+distanceString + " mi";
                    quickInfo.setText(currentText);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };


        TextView reviewsText = (TextView)findViewById(R.id.textViewNumberOfGems);
        reviewsText.setText(currGem.getRating()+" gems / 5 gems"); // TODO: Update with images.
        TextView description = (TextView)findViewById(R.id.textViewDescription);
        description.setText(currGem.getDescription());

        final ImageButton backButton = (ImageButton)findViewById(R.id.detailsBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnWithUpdatedGem();
            }
        });

        final Button navigateButton = (Button)findViewById(R.id.detailsNavigateButton);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchNavigation(view);
            }
        });

        final Button reviewButton = (Button) findViewById(R.id.reviewButton);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GemInfoActivity.this, ReviewActivity.class);
                intent.putExtra("currGem", currGem);
                startActivityForResult(intent, CREATE_REVIEW);
            }
        });
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
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
        catch (SecurityException e) {
            Toast.makeText(this, "Please allow this application to access your location data",
                    Toast.LENGTH_LONG);
        }
    }

    public void launchNavigation(View button) {
        Uri navigationUri = Uri.parse("google.navigation:q=" + currGem.getLocation().latitude+","+
        currGem.getLocation().longitude+"&mode=w");
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigationUri);
        navigationIntent.setPackage("com.google.android.apps.maps");
        startActivity(navigationIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        returnWithUpdatedGem();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_REVIEW && resultCode == RESULT_OK) {
            currGem = (GemInformation)data.getSerializableExtra("updatedGem");

            int numReviews = currGem.getReviews().size();
            int oldNumReviews = reviews.size(); // If they don't type anything, still add the gem number
            if(oldNumReviews != numReviews) {
                String newReview = currGem.getReviews().get(numReviews - 1); // Index should never be out of bounds because at least one review exists
                reviews.add(newReview);
                mAdapter.notifyDataSetChanged();
                Toast.makeText(this, "New review: " + newReview, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Review accepted", Toast.LENGTH_SHORT).show();
            }



            // Update rating of gem
            TextView reviewsText = (TextView)findViewById(R.id.textViewNumberOfGems);
            reviewsText.setText(currGem.getRating()+" gems / 5 gems"); // TODO: Update with images.
        }
    }

    // Menu back or regular back button pressed
    public void returnWithUpdatedGem() {
        Intent data = new Intent();
        data.putExtra("updatedGem", (Serializable) currGem); // May be null, will be checked in MapsActivity
        setResult(RESULT_OK, data);
        finish();
    }
}

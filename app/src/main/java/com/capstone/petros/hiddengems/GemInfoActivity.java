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
import android.widget.ImageView;
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

    private static final int GEM_BLUE = Color.argb(255,52,206,251);
    private static final int GEM_GREY = Color.argb(255,196,196,196);

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
        currGem = MapsActivity.getCurrGem();
        //currGem = (GemInformation)getIntent().getSerializableExtra("currGem");

        if(currGem == null){
            Intent data = new Intent();
            setResult(RESULT_CANCELED, data);
            finish();
            return;
        }

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

        // Set the quick info
        TextView quickInfo = (TextView)findViewById(R.id.textViewQuickInfo);
        String quickInfoText = currGem.getQuickDescription() + " - ";
        if(currGem.getPrice() != 0){
            for(int i = 0; i < currGem.getPrice(); i++)
                quickInfoText += "$";
            quickInfoText += " - ";
        }
        quickInfoText += "[Getting Location...] mi"; //Until the location callback gets the location
        quickInfo.setText(quickInfoText);

        // Set other information.
        setGemRating();
        TextView description = (TextView)findViewById(R.id.textViewDescription);
        description.setText(currGem.getDescription());

        // Set the images.
        ImageView iv1 = (ImageView)findViewById(R.id.imageView1);
        if(currGem.getBitmap1() != null) {
            iv1.setImageBitmap(currGem.getBitmap1());
            iv1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        ImageView iv2 = (ImageView)findViewById(R.id.imageView2);
        if(currGem.getBitmap2() != null) {
            iv2.setImageBitmap(currGem.getBitmap2());
            iv2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

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
                // Getting gem from MapsActivity instead of passing it around.
                //intent.putExtra("currGem", currGem);
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
            // Edited directly...
            //currGem = (GemInformation)data.getSerializableExtra("updatedGem");

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
            setGemRating();
        }
        if (requestCode == RESULT_CANCELED){
            Intent data2 = new Intent();
            setResult(RESULT_CANCELED, data2);
            finish();
        }
    }

    public void setGemRating(){
        double rating = currGem.getRating();
        ImageView gem1 = (ImageView)findViewById(R.id.gemInfoReview1);
        ImageView gem2 = (ImageView)findViewById(R.id.gemInfoReview2);
        ImageView gem3 = (ImageView)findViewById(R.id.gemInfoReview3);
        ImageView gem4 = (ImageView)findViewById(R.id.gemInfoReview4);
        ImageView gem5 = (ImageView)findViewById(R.id.gemInfoReview5);

        ImageView gemHalf1 = (ImageView)findViewById(R.id.gemInfoReviewHalf1);
        ImageView gemHalf2 = (ImageView)findViewById(R.id.gemInfoReviewHalf2);
        ImageView gemHalf3 = (ImageView)findViewById(R.id.gemInfoReviewHalf3);
        ImageView gemHalf4 = (ImageView)findViewById(R.id.gemInfoReviewHalf4);
        ImageView gemHalf5 = (ImageView)findViewById(R.id.gemInfoReviewHalf5);

        if(rating < 0.25){
            // All grey
            gem1.setVisibility(View.VISIBLE);
            gem1.setColorFilter(GEM_GREY);
            gem2.setVisibility(View.VISIBLE);
            gem2.setColorFilter(GEM_GREY);
            gem3.setVisibility(View.VISIBLE);
            gem3.setColorFilter(GEM_GREY);
            gem4.setVisibility(View.VISIBLE);
            gem4.setColorFilter(GEM_GREY);
            gem5.setVisibility(View.VISIBLE);
            gem5.setColorFilter(GEM_GREY);

            gemHalf1.setVisibility(View.GONE);
            gemHalf2.setVisibility(View.GONE);
            gemHalf3.setVisibility(View.GONE);
            gemHalf4.setVisibility(View.GONE);
            gemHalf5.setVisibility(View.GONE);
        }
        else if(rating >= 0.25 && rating < 0.75){
            // .5 gem
            gem1.setVisibility(View.GONE);
            gem2.setVisibility(View.VISIBLE);
            gem2.setColorFilter(GEM_GREY);
            gem3.setVisibility(View.VISIBLE);
            gem3.setColorFilter(GEM_GREY);
            gem4.setVisibility(View.VISIBLE);
            gem4.setColorFilter(GEM_GREY);
            gem5.setVisibility(View.VISIBLE);
            gem5.setColorFilter(GEM_GREY);

            gemHalf1.setVisibility(View.VISIBLE);
            gemHalf2.setVisibility(View.GONE);
            gemHalf3.setVisibility(View.GONE);
            gemHalf4.setVisibility(View.GONE);
            gemHalf5.setVisibility(View.GONE);
        }
        else if(rating >= 0.75 && rating < 1.25){
            // 1 gem
            gem1.setVisibility(View.VISIBLE);
            gem1.setColorFilter(GEM_BLUE);
            gem2.setVisibility(View.VISIBLE);
            gem2.setColorFilter(GEM_GREY);
            gem3.setVisibility(View.VISIBLE);
            gem3.setColorFilter(GEM_GREY);
            gem4.setVisibility(View.VISIBLE);
            gem4.setColorFilter(GEM_GREY);
            gem5.setVisibility(View.VISIBLE);
            gem5.setColorFilter(GEM_GREY);

            gemHalf1.setVisibility(View.GONE);
            gemHalf2.setVisibility(View.GONE);
            gemHalf3.setVisibility(View.GONE);
            gemHalf4.setVisibility(View.GONE);
            gemHalf5.setVisibility(View.GONE);
        }
        else if(rating >= 1.25 && rating < 1.75){
            // 1.5 gem
            gem1.setVisibility(View.VISIBLE);
            gem1.setColorFilter(GEM_BLUE);
            gem2.setVisibility(View.GONE);
            gem3.setVisibility(View.VISIBLE);
            gem3.setColorFilter(GEM_GREY);
            gem4.setVisibility(View.VISIBLE);
            gem4.setColorFilter(GEM_GREY);
            gem5.setVisibility(View.VISIBLE);
            gem5.setColorFilter(GEM_GREY);

            gemHalf1.setVisibility(View.GONE);
            gemHalf2.setVisibility(View.VISIBLE);
            gemHalf3.setVisibility(View.GONE);
            gemHalf4.setVisibility(View.GONE);
            gemHalf5.setVisibility(View.GONE);
        }
        else if(rating >= 1.75 && rating < 2.25){
            // 2 gem
            gem1.setVisibility(View.VISIBLE);
            gem1.setColorFilter(GEM_BLUE);
            gem2.setVisibility(View.VISIBLE);
            gem2.setColorFilter(GEM_BLUE);
            gem3.setVisibility(View.VISIBLE);
            gem3.setColorFilter(GEM_GREY);
            gem4.setVisibility(View.VISIBLE);
            gem4.setColorFilter(GEM_GREY);
            gem5.setVisibility(View.VISIBLE);
            gem5.setColorFilter(GEM_GREY);

            gemHalf1.setVisibility(View.GONE);
            gemHalf2.setVisibility(View.GONE);
            gemHalf3.setVisibility(View.GONE);
            gemHalf4.setVisibility(View.GONE);
            gemHalf5.setVisibility(View.GONE);
        }
        else if(rating >= 2.25 && rating < 2.75){
            // 2.5 gem
            gem1.setVisibility(View.VISIBLE);
            gem1.setColorFilter(GEM_BLUE);
            gem2.setVisibility(View.VISIBLE);
            gem2.setColorFilter(GEM_BLUE);
            gem3.setVisibility(View.GONE);
            gem4.setVisibility(View.VISIBLE);
            gem4.setColorFilter(GEM_GREY);
            gem5.setVisibility(View.VISIBLE);
            gem5.setColorFilter(GEM_GREY);

            gemHalf1.setVisibility(View.GONE);
            gemHalf2.setVisibility(View.GONE);
            gemHalf3.setVisibility(View.VISIBLE);
            gemHalf4.setVisibility(View.GONE);
            gemHalf5.setVisibility(View.GONE);
        }
        else if(rating >= 2.75 && rating < 3.25){
            // 3 gem
            gem1.setVisibility(View.VISIBLE);
            gem1.setColorFilter(GEM_BLUE);
            gem2.setVisibility(View.VISIBLE);
            gem2.setColorFilter(GEM_BLUE);
            gem3.setVisibility(View.VISIBLE);
            gem3.setColorFilter(GEM_BLUE);
            gem4.setVisibility(View.VISIBLE);
            gem4.setColorFilter(GEM_GREY);
            gem5.setVisibility(View.VISIBLE);
            gem5.setColorFilter(GEM_GREY);

            gemHalf1.setVisibility(View.GONE);
            gemHalf2.setVisibility(View.GONE);
            gemHalf3.setVisibility(View.GONE);
            gemHalf4.setVisibility(View.GONE);
            gemHalf5.setVisibility(View.GONE);
        }
        else if(rating >= 3.25 && rating < 3.75){
            // 3.5 gem
            gem1.setVisibility(View.VISIBLE);
            gem1.setColorFilter(GEM_BLUE);
            gem2.setVisibility(View.VISIBLE);
            gem2.setColorFilter(GEM_BLUE);
            gem3.setVisibility(View.VISIBLE);
            gem3.setColorFilter(GEM_BLUE);
            gem4.setVisibility(View.GONE);
            gem5.setVisibility(View.VISIBLE);
            gem5.setColorFilter(GEM_GREY);

            gemHalf1.setVisibility(View.GONE);
            gemHalf2.setVisibility(View.GONE);
            gemHalf3.setVisibility(View.GONE);
            gemHalf4.setVisibility(View.VISIBLE);
            gemHalf5.setVisibility(View.GONE);
        }
        else if(rating >= 3.75 && rating < 4.25){
            // 4 gem
            gem1.setVisibility(View.VISIBLE);
            gem1.setColorFilter(GEM_BLUE);
            gem2.setVisibility(View.VISIBLE);
            gem2.setColorFilter(GEM_BLUE);
            gem3.setVisibility(View.VISIBLE);
            gem3.setColorFilter(GEM_BLUE);
            gem4.setVisibility(View.VISIBLE);
            gem4.setColorFilter(GEM_BLUE);
            gem5.setVisibility(View.VISIBLE);
            gem5.setColorFilter(GEM_GREY);

            gemHalf1.setVisibility(View.GONE);
            gemHalf2.setVisibility(View.GONE);
            gemHalf3.setVisibility(View.GONE);
            gemHalf4.setVisibility(View.GONE);
            gemHalf5.setVisibility(View.GONE);
        }
        else if(rating >= 4.25 && rating < 4.75){
            // 4.5 gem
            gem1.setVisibility(View.VISIBLE);
            gem1.setColorFilter(GEM_BLUE);
            gem2.setVisibility(View.VISIBLE);
            gem2.setColorFilter(GEM_BLUE);
            gem3.setVisibility(View.VISIBLE);
            gem3.setColorFilter(GEM_BLUE);
            gem4.setVisibility(View.VISIBLE);
            gem4.setColorFilter(GEM_BLUE);
            gem5.setVisibility(View.GONE);

            gemHalf1.setVisibility(View.GONE);
            gemHalf2.setVisibility(View.GONE);
            gemHalf3.setVisibility(View.GONE);
            gemHalf4.setVisibility(View.GONE);
            gemHalf5.setVisibility(View.VISIBLE);
        }
        else{
            // 5 gem
            gem1.setVisibility(View.VISIBLE);
            gem1.setColorFilter(GEM_BLUE);
            gem2.setVisibility(View.VISIBLE);
            gem2.setColorFilter(GEM_BLUE);
            gem3.setVisibility(View.VISIBLE);
            gem3.setColorFilter(GEM_BLUE);
            gem4.setVisibility(View.VISIBLE);
            gem4.setColorFilter(GEM_BLUE);
            gem5.setVisibility(View.VISIBLE);
            gem5.setColorFilter(GEM_BLUE);

            gemHalf1.setVisibility(View.GONE);
            gemHalf2.setVisibility(View.GONE);
            gemHalf3.setVisibility(View.GONE);
            gemHalf4.setVisibility(View.GONE);
            gemHalf5.setVisibility(View.GONE);
        }
    }

    // Menu back or regular back button pressed
    public void returnWithUpdatedGem() {
        Intent data = new Intent();
        // Getting gem from MapsActivity instead of passing it around.
        //data.putExtra("updatedGem", (Serializable) currGem); // May be null, will be checked in MapsActivity
        setResult(RESULT_OK, data);
        finish();
    }
}

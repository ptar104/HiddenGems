package com.capstone.petros.hiddengems;

import android.content.Intent;
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
    ListView reviewsList;
    final int CREATE_REVIEW = 828;
    GemInformation currGem = null;
    GemInformation updatedGem = null; // Temporary datastore for gems with added reviews, to update gem if user returns to main map view
    ArrayAdapter mAdapter;
    ArrayList<String> reviews = new ArrayList<String>();

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
        title.setText(currGem.getGemName());
//        TextView quickInfo = (TextView)findViewById(R.id.textViewQuickInfo);
//        quickInfo.setText("Casual Pizza - $$ - .1 mi"); // TODO: NEED TO ADD THIS TO GEM INFO,
//                                                        // AND CALCULATE DISTANCE.
//        reviews.setText(gem.getRating()+" gems / 5 gems"); // TODO: Update with images.
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
            updatedGem = (GemInformation)data.getSerializableExtra("updatedGem");

            int numReviews = updatedGem.getReviews().size();
            String newReview = updatedGem.getReviews().get(numReviews - 1); // Index should never be out of bounds because at least one review exists
            reviews.add(newReview);
            mAdapter.notifyDataSetChanged();
            Toast.makeText(this, "New review: " + newReview, Toast.LENGTH_SHORT).show();

            // Add review for gem
        }
    }

    // Menu back or regular back button pressed
    public void returnWithUpdatedGem() {
        Intent data = new Intent();
        data.putExtra("updatedGem", (Serializable) updatedGem); // May be null, will be checked in MapsActivity
        setResult(RESULT_CANCELED, data);
        finish();
    }
}

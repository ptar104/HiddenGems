package com.capstone.petros.hiddengems;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.net.Uri;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class GemInfoActivity extends AppCompatActivity {

    final int CREATE_REVIEW = 828;
    GemInformation gem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gem_info);


        // Because we haven't sent in the gem info yet, I'm just going to make a fake gem
        // for Ike's pizza.
        ArrayList<GemInformation.Category> al = new ArrayList<>();
        al.add(GemInformation.Category.RESTAURANT);
        gem = new GemInformation(4, "Yummy!", "Ike's pizza has been a standby in DC for over " +
                "20 years", al, 38.985910, -76.943);
        gem.setGemName("Ike's Pizza");

        //Update the UI...
        TextView title = (TextView)findViewById(R.id.textViewGemTitle);
        title.setText(gem.getGemName());
        TextView quickInfo = (TextView)findViewById(R.id.textViewQuickInfo);
        quickInfo.setText("Casual Pizza - $$ - .1 mi"); // TODO: NEED TO ADD THIS TO GEM INFO,
                                                        // AND CALCULATE DISTANCE.
        TextView reviews = (TextView)findViewById(R.id.textViewNumberOfGems);
        reviews.setText(gem.getRating()+" gems / 5 gems"); // TODO: Update with images.
        TextView description = (TextView)findViewById(R.id.textViewDescription);
        description.setText(gem.getDescription());
        // And now go through arraylist of reviews!!
        for(String s : gem.getReviews()){
            // TODO: Yoshi said he took care of updating the reviews.
        }

        final ImageButton backButton = (ImageButton)findViewById(R.id.detailsBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

                startActivityForResult(intent, CREATE_REVIEW);
            }
        });
    }

    public void launchNavigation(View button){
        Uri navigationUri = Uri.parse("google.navigation:q="+gem.getLocation().latitude+","+
        gem.getLocation().longitude+"&mode=w");
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigationUri);
        navigationIntent.setPackage("com.google.android.apps.maps");
        startActivity(navigationIntent);
    }
}

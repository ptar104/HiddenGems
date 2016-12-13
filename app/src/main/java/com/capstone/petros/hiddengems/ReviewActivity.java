package com.capstone.petros.hiddengems;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by simon on 12/4/16.
 */

public class ReviewActivity extends AppCompatActivity {
    int selectedRating = 0; // Default value so it can be distinguished

    EditText userReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        userReview = (EditText)findViewById(R.id.userReview);

        TextView title = (TextView)findViewById(R.id.textViewGemTitle);
        title.setText(title.getText().toString().toUpperCase());

        // Set typeface
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Lato-Semibold.ttf");
        title.setTypeface(type);

        // Gem images
        final ImageView gemRating1 = (ImageView)findViewById(R.id.gemRating1);
        final ImageView gemRating2 = (ImageView)findViewById(R.id.gemRating2);
        final ImageView gemRating3 = (ImageView)findViewById(R.id.gemRating3);
        final ImageView gemRating4 = (ImageView)findViewById(R.id.gemRating4);
        final ImageView gemRating5 = (ImageView)findViewById(R.id.gemRating5);

        gemRating1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedRating = 1;
            }
        });

        gemRating2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedRating = 2;
            }
        });

        gemRating3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedRating = 3;
            }
        });

        gemRating4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedRating = 4;
            }
        });

        gemRating5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedRating = 5;
            }
        });

        final ImageButton backButton = (ImageButton)findViewById(R.id.addGemBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        final Button submitButton = (Button)findViewById(R.id.submitReviewButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Form Validation */
                if (selectedRating == 0) {
                    Toast.makeText(ReviewActivity.this, "Please select a rating first.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Fetch current gem object
                // Getting gem from MapsActivity instead of passing it around.
                //GemInformation currGem = (GemInformation)getIntent().getSerializableExtra("currGem");
                GemInformation currGem = MapsActivity.getCurrGem();

                // Update existing gem
                currGem.addRating(selectedRating);

                if(userReview.getText().toString().length() != 0)
                    currGem.addReview(userReview.getText().toString());

                // Prepare result
                Intent data = new Intent();
                //data.putExtra("updatedGem", currGem);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }



}

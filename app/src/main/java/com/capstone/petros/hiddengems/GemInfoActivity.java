package com.capstone.petros.hiddengems;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class GemInfoActivity extends AppCompatActivity {

    final int CREATE_REVIEW = 828;
    GemInformation currGem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gem_info);

        // Initialize currGem reference
        currGem = (GemInformation)getIntent().getSerializableExtra("currGem");

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
                finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_REVIEW && resultCode == RESULT_OK) {
            GemInformation updatedGem = (GemInformation)data.getSerializableExtra("updatedGem");

            int numReviews = updatedGem.getReviews().size();
            String newReview = updatedGem.getReviews().get(numReviews - 1); // Index should never be out of bounds because at least one review exists
            Toast.makeText(this, "New review: " + newReview, Toast.LENGTH_SHORT).show();
        }
    }
}

package com.capstone.petros.hiddengems;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class GemInfoActivity extends AppCompatActivity {

    final int CREATE_REVIEW = 828;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gem_info);

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
}

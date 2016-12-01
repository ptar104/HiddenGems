package com.capstone.petros.hiddengems;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class AddGemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gem);

        final ImageButton backButton = (ImageButton)findViewById(R.id.addGemBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final Button submitButton = (Button)findViewById(R.id.submitGemButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate form and save new object, pass back as parcelable

                Intent data = new Intent();

                setResult(RESULT_OK, data);
                finish();
            }
        });
    }
}

package com.capstone.petros.hiddengems;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class AddGemActivity extends AppCompatActivity {
    private String TAG = "AddGemActivity";

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

                final EditText gemName = (EditText)findViewById(R.id.addGemName);
                final EditText description = (EditText)findViewById(R.id.addGemDescription);

                Intent data = new Intent();

                GemInformation gem = new GemInformation();

                gem.setTitle(gemName.getText().toString());
                gem.setDescription(description.getText().toString());

                Log.i(TAG, gem.toString());

                setResult(RESULT_OK, data);
                finish();
            }
        });
    }
}

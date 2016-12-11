package com.capstone.petros.hiddengems;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;

import static android.R.attr.type;

public class AddGemActivity extends AppCompatActivity implements OnConnectionFailedListener {
    private String TAG = "AddGemActivity";
    int PLACE_PICKER_REQUEST = 1;
    EditText gemName;
    EditText description;
    CheckBox restaurantCheck;
    CheckBox historicCheck;
    CheckBox entertainmentCheck;
    CheckBox otherCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gem);

        gemName = (EditText)findViewById(R.id.addGemName);
        description = (EditText)findViewById(R.id.addGemDescription);
        restaurantCheck = (CheckBox)findViewById(R.id.restaurantCheck);
        historicCheck = (CheckBox)findViewById(R.id.historicCheck);
        entertainmentCheck = (CheckBox)findViewById(R.id.entertainmentCheck);
        otherCheck = (CheckBox)findViewById(R.id.otherCheck);

        TextView title = (TextView)findViewById(R.id.textViewGemTitle);
        title.setText(title.getText().toString().toUpperCase());

        // Set typeface
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Lato-Semibold.ttf");
        title.setTypeface(type);


        final ImageButton backButton = (ImageButton)findViewById(R.id.addGemBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        final Button submitButton = (Button)findViewById(R.id.submitGemButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                /* Validate form and prompt for location using Google Places Picker */

                // Check gem name
                if (gemName.getText().toString().length() == 0) {
                    Toast.makeText(AddGemActivity.this, "Please specify a gem name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check gem description
                if (description.getText().toString().length() == 0) {
                    Toast.makeText(AddGemActivity.this, "Please specify a gem description.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!restaurantCheck.isChecked() && !historicCheck.isChecked() && !entertainmentCheck.isChecked() && !otherCheck.isChecked()) {
                    Toast.makeText(AddGemActivity.this, "Please select at least one gem type.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try  {
                    startActivityForResult(builder.build(AddGemActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();



                Intent result = new Intent();

                String title = gemName.getText().toString();
                String descriptionText = description.getText().toString();

                GemInformation gem = new GemInformation();

                gem.setGemName(title);
                gem.setDescription(descriptionText);

                ArrayList<GemInformation.Category> categories = new ArrayList<GemInformation.Category>();

                if (restaurantCheck.isChecked())
                    categories.add(GemInformation.Category.RESTAURANT);
                if (historicCheck.isChecked())
                    categories.add(GemInformation.Category.HISTORIC);
                if (entertainmentCheck.isChecked())
                    categories.add(GemInformation.Category.ENTERTAINMENT);
                if (otherCheck.isChecked())
                    categories.add(GemInformation.Category.OTHER);

                gem.setCategory(categories);
                gem.setLocation(place.getLatLng().latitude, place.getLatLng().longitude);

                Log.i(TAG, gem.toString());

                result.putExtra("newGem", (Serializable) gem);

                setResult(RESULT_OK, result);
                finish();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        if (connectionResult.getErrorMessage()) {
//            Log.i(TAG, connectionResult.getErrorMessage());
//        }
    }
}

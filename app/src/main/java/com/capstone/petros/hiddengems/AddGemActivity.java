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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    EditText quickDescription;
    RadioGroup category;
    int price = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gem);

        gemName = (EditText)findViewById(R.id.addGemName);
        description = (EditText)findViewById(R.id.addGemDescription);
        quickDescription = (EditText)findViewById(R.id.addGemQuickDescription);
        category = (RadioGroup)findViewById(R.id.radioGroupType);

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
                String quickDescriptionText = quickDescription.getText().toString();

                GemInformation gem = new GemInformation();

                gem.setGemName(title);
                gem.setDescription(descriptionText);
                gem.setQuickDescription(quickDescriptionText);


                if (category.getCheckedRadioButtonId() == R.id.restaurantCheck)
                    gem.setCategory(GemInformation.Category.RESTAURANT);
                if (category.getCheckedRadioButtonId() == R.id.historicCheck)
                    gem.setCategory(GemInformation.Category.HISTORIC);
                if (category.getCheckedRadioButtonId() == R.id.entertainmentCheck)
                    gem.setCategory(GemInformation.Category.ENTERTAINMENT);
                if (category.getCheckedRadioButtonId() == R.id.otherCheck)
                    gem.setCategory(GemInformation.Category.OTHER);


                gem.setLocation(place.getLatLng().latitude, place.getLatLng().longitude);

                gem.setPrice(price);

                Log.i(TAG, gem.toString());

                result.putExtra("newGem", (Serializable) gem);

                setResult(RESULT_OK, result);
                finish();
            }
        }
    }

    private void getDollarSignViews(ImageView [] signs){
        signs[0] = (ImageView)findViewById(R.id.dollarSign0);
        signs[1] = (ImageView)findViewById(R.id.dollarSign1);
        signs[2] = (ImageView)findViewById(R.id.dollarSign2);
        signs[3] = (ImageView)findViewById(R.id.dollarSign3);
        signs[4] = (ImageView)findViewById(R.id.dollarSign4);

    }

    // These methods are called when one of the dollar signs are clicked to
    // properly change the text, shading, and record the last button clicked
    public void dollarSign0Clicked(View view){
        ImageView[] signs = new ImageView[5];
        getDollarSignViews(signs);
        price = 0;
        signs[0].setAlpha(255);
        signs[1].setAlpha(160);
        signs[2].setAlpha(160);
        signs[3].setAlpha(160);
        signs[4].setAlpha(160);
        ((TextView)findViewById(R.id.dollarSignText)).setText("...completely free!");
    }

    public void dollarSign1Clicked(View view){
        ImageView[] signs = new ImageView[5];
        getDollarSignViews(signs);
        price = 1;
        signs[0].setAlpha(160);
        signs[1].setAlpha(255);
        signs[2].setAlpha(160);
        signs[3].setAlpha(160);
        signs[4].setAlpha(160);
        ((TextView)findViewById(R.id.dollarSignText)).setText("...less than $10");
    }

    public void dollarSign2Clicked(View view){
        ImageView[] signs = new ImageView[5];
        getDollarSignViews(signs);
        price = 2;
        signs[0].setAlpha(160);
        signs[1].setAlpha(255);
        signs[2].setAlpha(255);
        signs[3].setAlpha(160);
        signs[4].setAlpha(160);
        ((TextView)findViewById(R.id.dollarSignText)).setText("...between $10 and $20");
    }

    public void dollarSign3Clicked(View view){
        ImageView[] signs = new ImageView[5];
        getDollarSignViews(signs);
        price = 3;
        signs[0].setAlpha(160);
        signs[1].setAlpha(255);
        signs[2].setAlpha(255);
        signs[3].setAlpha(255);
        signs[4].setAlpha(160);
        ((TextView)findViewById(R.id.dollarSignText)).setText("...between $20 and $50");
    }

    public void dollarSign4Clicked(View view){
        ImageView[] signs = new ImageView[5];
        getDollarSignViews(signs);
        price = 4;
        signs[0].setAlpha(160);
        signs[1].setAlpha(255);
        signs[2].setAlpha(255);
        signs[3].setAlpha(255);
        signs[4].setAlpha(255);
        ((TextView)findViewById(R.id.dollarSignText)).setText("...greater than $50");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        if (connectionResult.getErrorMessage()) {
//            Log.i(TAG, connectionResult.getErrorMessage());
//        }
    }



}

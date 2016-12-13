package com.capstone.petros.hiddengems;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import static android.R.attr.type;

public class AddGemActivity extends AppCompatActivity implements OnConnectionFailedListener {
    private String TAG = "AddGemActivity";
    int PLACE_PICKER_REQUEST = 1;
    private static int RESULT_LOAD_IMAGE_ONE = 88;
    private static int RESULT_LOAD_IMAGE_TWO = 89;
    EditText gemName;
    EditText description;
    EditText quickDescription;
    RadioGroup category;
    int price = -1;
    boolean bitmap1Changed = false, bitmap2Changed = false;



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

                // Check gem description
                if (quickDescription.getText().toString().length() == 0) {
                    Toast.makeText(AddGemActivity.this, "Please specify what type of place this gem is.",
                            Toast.LENGTH_SHORT).show();
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

                // Getting the bitmaps from the image views
                //TODO: Set if not changed to defaults
                if(bitmap1Changed)
                    gem.setBitmap1(((BitmapDrawable)(((ImageView)findViewById(R.id.newGemImage1)).getDrawable())).getBitmap());
                else gem.setBitmap1(((BitmapDrawable)(((ImageView)findViewById(R.id.newGemImage1)).getDrawable())).getBitmap());
                if(bitmap2Changed)
                    gem.setBitmap2(((BitmapDrawable)(((ImageView)findViewById(R.id.newGemImage2)).getDrawable())).getBitmap());
                else gem.setBitmap2(((BitmapDrawable)(((ImageView)findViewById(R.id.newGemImage2)).getDrawable())).getBitmap());


                Log.i(TAG, gem.toString());

                // Getting gem from MapsActivity instead of passing it around.
                //result.putExtra("newGem", (Serializable) gem);
                MapsActivity.setCurrGem(gem);

                setResult(RESULT_OK, result);
                finish();
            }
        }
        // This code taken from Jon Froehlich's ImpressionistPainter skeleton. All credit goes to him.
        if ((requestCode == RESULT_LOAD_IMAGE_ONE || requestCode == RESULT_LOAD_IMAGE_TWO)
                && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ImageView imageView = null;
                if(requestCode == RESULT_LOAD_IMAGE_ONE) {
                    imageView = (ImageView) findViewById(R.id.newGemImage1);
                    bitmap1Changed = true;
                }
                else {
                    imageView = (ImageView) findViewById(R.id.newGemImage2);
                    bitmap2Changed = true;
                }

                // destroy the drawing cache to ensure that when a new image is loaded, its cached
                imageView.destroyDrawingCache();
                imageView.setImageBitmap(bitmap);
                imageView.setDrawingCacheEnabled(true);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } catch (IOException e) {
                e.printStackTrace();
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
        signs[0].setAlpha(1.0f);
        signs[1].setImageResource(R.drawable.greydollar);
        signs[2].setImageResource(R.drawable.greydollar);
        signs[3].setImageResource(R.drawable.greydollar);
        signs[4].setImageResource(R.drawable.greydollar);
        ((TextView)findViewById(R.id.dollarSignText)).setText("...completely free!");
    }

    public void dollarSign1Clicked(View view){
        ImageView[] signs = new ImageView[5];
        getDollarSignViews(signs);
        price = 1;
        signs[0].setAlpha(.5f);
        signs[1].setImageResource(R.drawable.bluedollar);
        signs[2].setImageResource(R.drawable.greydollar);
        signs[3].setImageResource(R.drawable.greydollar);
        signs[4].setImageResource(R.drawable.greydollar);
        ((TextView)findViewById(R.id.dollarSignText)).setText("...less than $10");
    }

    public void dollarSign2Clicked(View view){
        ImageView[] signs = new ImageView[5];
        getDollarSignViews(signs);
        price = 2;
        signs[0].setAlpha(.5f);
        signs[1].setImageResource(R.drawable.bluedollar);
        signs[2].setImageResource(R.drawable.bluedollar);
        signs[3].setImageResource(R.drawable.greydollar);
        signs[4].setImageResource(R.drawable.greydollar);
        ((TextView)findViewById(R.id.dollarSignText)).setText("...between $10 and $20");
    }

    public void dollarSign3Clicked(View view){
        ImageView[] signs = new ImageView[5];
        getDollarSignViews(signs);
        price = 3;
        signs[0].setAlpha(.5f);
        signs[1].setImageResource(R.drawable.bluedollar);
        signs[2].setImageResource(R.drawable.bluedollar);
        signs[3].setImageResource(R.drawable.bluedollar);
        signs[4].setImageResource(R.drawable.greydollar);
        ((TextView)findViewById(R.id.dollarSignText)).setText("...between $20 and $50");
    }

    public void dollarSign4Clicked(View view){
        ImageView[] signs = new ImageView[5];
        getDollarSignViews(signs);
        price = 4;
        signs[0].setAlpha(.5f);
        signs[1].setImageResource(R.drawable.bluedollar);
        signs[2].setImageResource(R.drawable.bluedollar);
        signs[3].setImageResource(R.drawable.bluedollar);
        signs[4].setImageResource(R.drawable.bluedollar);
        ((TextView)findViewById(R.id.dollarSignText)).setText("...greater than $50");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        if (connectionResult.getErrorMessage()) {
//            Log.i(TAG, connectionResult.getErrorMessage());
//        }
    }

    // NOTE: The following 2 functions taken from Jon Froehlich's ImpressionistPainter
    // skeleton. All credit goes to him.

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * From: http://stackoverflow.com/a/33292700
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            // For API 23+ you need to request the read/write permissions even if they are already in your manifest.
            // See: http://developer.android.com/training/permissions/requesting.html
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /**
     * Loads an image from the Gallery into the ImageView
     *
     * @param v
     */
    public void gemImageClicked(View v){
        // Without this call, the app was crashing in the onActivityResult method when trying to read from file system
        verifyStoragePermissions(this);

        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        ImageView iv = (ImageView)v;
        if(iv.getId() == R.id.newGemImage1)
            startActivityForResult(i, RESULT_LOAD_IMAGE_ONE);
        else startActivityForResult(i, RESULT_LOAD_IMAGE_TWO);
    }


}

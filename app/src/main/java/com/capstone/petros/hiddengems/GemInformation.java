package com.capstone.petros.hiddengems;

import android.graphics.Bitmap;
import android.location.Location;
import android.text.format.Time;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by simon on 11/30/16.
 */

public class GemInformation implements Serializable {

    public enum Category implements Serializable {
        RESTAURANT, HISTORIC, ENTERTAINMENT, OTHER
    }

    private int rating; // Constantly updated total sum of all ratings based on number of gems
    private double avgRating; // rating/ratingList.size()
    private ArrayList<Integer> ratingList; // All rating values
    private ArrayList<String> reviews;  // All review descriptions - Not sparse, so not 1:1, but our reviews list only shows review explanations, not number of gems so it's fine.
    private String description; // Gem description - singular.
    private Category category; // Array of Category enums as defined above
    private String gemName; // Name of gem
//    private Time timeCreated; // To keep track of shiny gems and its expirations
    private Double latitude; // Decided to use LatLng instead of Location because that's what we get back from Places API
    private Double longitude;
    private String quickDescription; // What kind of place is it - pizza joint, statue, etc...
    private int price; // How much will going to this gem cost ya?

    private Bitmap bitmap1; // The two images
    private Bitmap bitmap2;

    // Constructor
    public GemInformation() {
        this.rating = 0;
        this.avgRating = 0;
        this.ratingList = new ArrayList<Integer>();
        this.reviews = new ArrayList<String>();
        this.description = "";
        this.category = Category.RESTAURANT;
//        this.timeCreated = new Time();
//        this.timeCreated.setToNow();
//        this.location = null;
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    // Constructor to set rating, review, description, category
    public GemInformation(int rating, String review, String description, String quickDescription,
                          Category category, Double latitude, Double longitude, int price) {
        this.rating = rating;
        this.avgRating = rating;
        this.ratingList = new ArrayList<>();
        this.ratingList.add(rating);
        this.reviews = new ArrayList<>();
        this.reviews.add(review);
        this.setDescription(description);
        this.setQuickDescription(quickDescription);
        this.setCategory(category);
        this.latitude = latitude;
        this.longitude = longitude;
        this.setPrice(price);
    }

    // Rating added to arraylist and avg rating recalculated.
    public void addRating(int num) {
        // Num based on onTouch rating of user
        rating += num;
        ratingList.add(num);
        updateRating();
    }

    // Updates gem name
    public void setGemName(String title) {
        this.gemName = title;
    }

    public String getGemName() {
        return this.gemName;
    }

    // Updates avg rating
    public void updateRating() {
        avgRating = (double) rating / ratingList.size();
    }

    // Getter for avgRating
    public double getRating() {
        return avgRating;
    }

    // Add Review to ArrayList
    public void addReview(String s) {
        reviews.add(s);
    }

    // Getter for reviews
    public ArrayList<String> getReviews() {
        return reviews;
    }

    // Set description of restaurant
    public void setDescription(String s) {
        description = s;
    }

    // Get the description
    public String getDescription() {
        return description;
    }

    // Set category of Gem
    public void setCategory(Category category) {
        this.category = category;
    }

    // Get category of Gem
    public Category getCategory() { return category; }

    public void setLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng getLocation() {
        return new LatLng(latitude,longitude);
    }

    public String getQuickDescription() {
        return quickDescription;
    }

    public void setQuickDescription(String quickDescription) {
        this.quickDescription = quickDescription;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Bitmap getBitmap1() {
        return bitmap1;
    }

    public void setBitmap1(Bitmap bitmap1) {
        this.bitmap1 = bitmap1;
    }

    public Bitmap getBitmap2() {
        return bitmap2;
    }

    public void setBitmap2(Bitmap bitmap2) {
        this.bitmap2 = bitmap2;
    }

    @Override
    public String toString() {
        return "Gem Title: \"" + this.gemName +  "\"\nGem description: \"" +  this.description + "\"";
    }
}
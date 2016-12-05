package com.capstone.petros.hiddengems;

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

    public enum Category {
        RESTAURANT, HISTORIC, ENTERTAINMENT, OTHER
    }

    private int rating;
    private double avgRating;
    private ArrayList<Integer> ratingList;
    private ArrayList<String> reviews;
    private String description;
    private ArrayList<Category> category;
    private String gemName;
    private Time timeCreated;
    private LatLng location;


    // Constructor
    public GemInformation() {
        this.rating = 0;
        this.avgRating = 0;
        this.ratingList = new ArrayList<Integer>();
        this.reviews = new ArrayList<String>();
        this.description = "";
        this.category = new ArrayList<Category>();
        this.timeCreated = new Time();
        this.timeCreated.setToNow();
        this.location = null;
    }

    // Constructor to set rating, review, description, category
    public GemInformation(int rating, String review, String description, ArrayList<Category> category, LatLng location) {
        this.rating = rating;
        this.avgRating = rating;
        this.ratingList = new ArrayList<>();
        this.ratingList.add(rating);
        this.reviews = new ArrayList<>();
        this.reviews.add(review);
        this.setDescription(description);
        this.setCategory(category);
        this.location = location;
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
        avgRating = rating / ratingList.size();
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

    // Set category of Gem
    public void setCategory(ArrayList<Category> category) {
        this.category = category;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public LatLng getLocation() {
        return this.location;
    }

    @Override
    public String toString() {
        return "Gem Title: \"" + this.gemName +  "\"\nGem description: \"" +  this.description + "\"";
    }
}

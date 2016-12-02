package com.capstone.petros.hiddengems;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by simon on 11/30/16.
 */

public class GemInformation {

    public enum Category {
        RESTAURANT, HISTORIC, ENTERTAINMENT, OTHER
    }

    int rating;
    double avgRating;
    ArrayList<Integer> ratingList;
    ArrayList<String> reviews;
    String description;
    ArrayList<Category> category;


    // Constructor
    public GemInformation() {
        rating = 0;
        avgRating = 0;
        ratingList = new ArrayList<Integer>();
        reviews = new ArrayList<String>();
        description = "";
        category = new ArrayList<Category>();
    }

    // Constructor to set rating, review, description, category
    public GemInformation(int rating, String review, String description, ArrayList<Category> category) {
        this.rating = rating;
        this.avgRating = rating;
        ratingList = new ArrayList<>();
        ratingList.add(rating);
        reviews = new ArrayList<>();
        reviews.add(review);
        setDescription(description);
        setCategory(category);
    }

    // Rating added to arraylist and avg rating recalculated.
    public void addRating(int num) {
        // Num based on onTouch rating of user
        rating += num;
        ratingList.add(num);
        updateRating();
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

}

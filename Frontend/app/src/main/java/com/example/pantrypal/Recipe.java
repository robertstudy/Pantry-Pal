package com.example.pantrypal;

import android.graphics.Bitmap;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * The Recipe class represents a singular recipe and its associated information.
 *
 * It currently used as a way to store and display recipes using a recycler view on the Home fragment,
 * contained in a List within the RecipeRVAdapter.
 * @author Adrian Boziloff
 */
public class Recipe {
    int id;
    int uid;
    String name;
    JSONArray directions;
    int total_time;
    int calories;
    Bitmap imageBitmap;
    float avgRating;

    /**
     * Class constructor specifying the recipe id, name, directions, and prep time
     */
    public Recipe(int id, int uid, String name, JSONArray directions, int total_time, int calories, double avgRating) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.directions = directions;
        this.total_time = total_time;
        this.calories = calories;
        this.imageBitmap = null;
        this.avgRating = (float)avgRating;
    }

    /**
     * Class constructor specifying the recipe id, name, directions, prep time, and image bitmap
     */
    public Recipe(int id, int uid, String name, JSONArray directions, int total_time, int calories, Bitmap bitmap, double avgRating) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.directions = directions;
        this.total_time = total_time;
        this.calories = calories;
        this.imageBitmap = bitmap;
        this.avgRating = (float)avgRating;
    }

    /**
     * Returns the recipe's id
     * @return      the recipe id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the recipe's user id
     * @return      the recipe user id
     */
    public int getUid() {
        return uid;
    }

    /**
     * Returns the recipe's name
     * @return      the recipe name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the recipe's directions
     * @return      the recipe directions
     */
    public JSONArray getDirections(){
        return directions;
    }

    /**
     * Returns the recipe's total time
     * @return      the recipe total time
     */
    public int getTotalTime(){
        return total_time;
    }

    /**
     * Returns the recipe's total calories
     * @return      the recipe total calorie count
     */
    public int getCalories(){
        return calories;
    }

    /**
     * Returns the recipe's image bitmap
     * @return      the recipe image bitmap
     */
    public Bitmap getImageBitmap() { return imageBitmap; }

    /**
     * Sets the recipe's image bitmap
     * @param bitmap    the recipe image bitmap
     */
    public void setImageBitmap(Bitmap bitmap) { this.imageBitmap = bitmap; }

    /**
     * Returns the recipe's average rating
     * @return      the recipe average rating
     */
    public float getAvgRating(){
        return avgRating;
    }
}

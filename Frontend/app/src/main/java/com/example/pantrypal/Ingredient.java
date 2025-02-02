package com.example.pantrypal;

import android.graphics.Bitmap;

/**
 * Stores all information for each ingredient that can be used in recipes, especially all nutrition
 * information we pull from ther Open Food Facts API website.
 * @author Michael Linker
 */
public class Ingredient {
    /**
     * Name of ingredient
     */
    String name;
    /**
     * ID we use to search information from Opend Food Facts API
     */
    String offid;
    /**
     * Ingredient ID in database
     */
    int iid;
    /**
     * Bitmap of image associated with this ingredient
     */
    Bitmap imageBitmap;

    /**
     * Contructor
     * @param name Name of Ingredient to be created
     * @param offid Offid of ingredient to be created
     * @param iid ID of ingredient to be made
     */
    public Ingredient(String name, String offid, int iid) {
        this.name = name;
        this.offid = offid;
        this.iid = iid;
        this.imageBitmap = null;
    }

    /**
     * Constructor with image
     * @param name Name of Ingredient to be created
     * @param offid Offid of ingredient to be created
     * @param iid ID of ingredient to be made
     * @param bitmap BITMAP of image to be stored with ingredient
     */
    public Ingredient(String name, String offid, int iid, Bitmap bitmap) {
        this.name = name;
        this.offid = offid;
        this.iid = iid;
        this.imageBitmap = bitmap;
    }

    /**
     *
     * @return The Name of the Ingredient
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return the offid of the ingredient
     */
    public String getOffid() {
        return offid;
    }

    /**
     *
     * @return the ID of the ingredient
     */
    public int getIid() {
        return iid;
    }

    /**
     *
     * @return bitmap of the image for this ingredient
     */

    public Bitmap getImageBitmap() { return imageBitmap; }

    /**
     *
     * @param bitmap set the image of this ingredient
     */
    public void setImageBitmap(Bitmap bitmap) { this.imageBitmap = bitmap; }
}

package com.example.pantrypal;

/**
 * The Review class represents a review and its associated information.
 *
 * It currently used as a way to store and display reviews under a recipe
 * using a recycler view on the view recipe activity
 * @author Adrian Boziloff
 */
public class Review {
    private int rateid;
    private int uid;
    private int rid;
    private float rating;
    private String comment;
    private String username;

    /**
     * Class constructor specifying the review rating id, user id, recipe id, the star rating,
     * the associated user comment, and the username of the author
     */
    public Review(int rateid, int uid, int rid, float rating, String comment, String username) {
        this.rateid = rateid;
        this.uid = uid;
        this.rid = rid;
        this.rating = rating;
        this.comment = comment;
        this.username = username;
    }

    public int getRateid() {
        return rateid;
    }

    public int getUid() {
        return uid;
    }

    public int getRid() {
        return rid;
    }

    public float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getUsername() { return username; }
}

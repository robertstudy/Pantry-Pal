package com.example.pantrypal;

import java.util.List;

public class User {
    private int uid;
    private String username;
    private String password;
    private List<Double> geoLocation;
    private List<String> favoriteRecipes;
    private boolean isActive;

    public User(int uid, String username, String password, List<Double> geoLocation, List<String> favoriteRecipes, boolean isActive) {
        this.uid = uid;
        this.username = username;
        this.password = password;
        this.geoLocation = geoLocation;
        this.favoriteRecipes = favoriteRecipes;
        this.isActive = isActive;
    }

    public int getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Double> getGeoLocation() {
        return geoLocation;
    }

    public List<String> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    public boolean isActive() {
        return isActive;
    }
}
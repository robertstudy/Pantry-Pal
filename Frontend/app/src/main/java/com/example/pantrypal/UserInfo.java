package com.example.pantrypal;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    public static int uid;
    public static String username;
    public static String password;
    public static List<Integer> favRecipes = new ArrayList<>();
    public static JSONObject userInfoJSON;
    public static int recipesPosted;
    public static boolean searchingByFav = false;
    public static boolean searchingByUMade = false;

    public static void getUserInfo(Context context){
        JsonObjectRequest uidRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL.USERS.concat("/" + username),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        userInfoJSON = response;
                        try {
                            uid = response.getInt("uid");
                            recipesPosted = response.getInt("recipesPosted");
                            updateFavorites(context);
                        } catch (JSONException e) {
                            Log.e("USER", "Failed to get user id");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Failed to get user id", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        VolleySingleton.getInstance(context).addToRequestQueue(uidRequest);
    }

    public static void updateFavorites(Context context){
        JsonArrayRequest favRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL.FAVORITES.concat("/user/" + UserInfo.uid),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        favRecipes.clear();
                        for (int i = 0; i < response.length(); i++){
                            try {
                                favRecipes.add(response.getJSONObject(i).getInt("rid"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        //Log.e("FAV", favRecipes.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Failed to get favorite id", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        VolleySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(favRequest);
    }
}

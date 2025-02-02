package com.example.pantrypal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecipeRVAdapter extends RecyclerView.Adapter<RecipeRVAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Recipe> recipeList;

    public void setFilteredList(ArrayList<Recipe> filteredList){
        this.recipeList = filteredList;
        notifyDataSetChanged();
    }

    public RecipeRVAdapter(Context context, ArrayList<Recipe> recipeList){
        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // builds the recipe card views using the recipe_card xml file
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recipe_card, parent, false);

        return new RecipeRVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeRVAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.recipe_name.setText(recipeList.get(position).getName());
        int recipe_time_hrs = recipeList.get(position).getTotalTime() / 60;
        int recipe_time_mins = recipeList.get(position).getTotalTime() % 60;
        String time_str = "";
        if (recipe_time_hrs > 0){
            if (recipe_time_hrs == 1){
                time_str = time_str + recipe_time_hrs + " hr ";
            } else {
                time_str = time_str + recipe_time_hrs + " hrs ";
            }
        }
        if (recipe_time_mins == 1){
            time_str = time_str + recipe_time_mins + " min";
        } else {
            time_str = time_str + recipe_time_mins + " mins";
        }
        holder.total_time.setText(time_str);
        String calories_text = recipeList.get(position).getCalories() + " cal/serv";
        holder.calories.setText(calories_text);
        holder.recipe_image.setImageBitmap(recipeList.get(position).getImageBitmap());
        holder.recipe_rating.setRating(recipeList.get(position).getAvgRating());
        holder.rating_num.setText(String.valueOf(recipeList.get(position).getAvgRating()));

        holder.editRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditRecipeActivity.class);

                intent.putExtra("rid", recipeList.get(position).getId());

                context.startActivity(intent);
            }
        });
        if (recipeList.get(position).getUid() != UserInfo.uid){
            holder.editRecipeButton.setVisibility(View.GONE);
        }
        holder.visitRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewRecipeActivity.class);

                intent.putExtra("rid", recipeList.get(position).getId());

                context.startActivity(intent);
            }
        });
        if (UserInfo.favRecipes.contains(recipeList.get(position).getId())){
            holder.favoriteRecipeButton.setImageResource(R.drawable.baseline_favorite_24);
        } else {
            holder.favoriteRecipeButton.setImageResource(R.drawable.round_favorite_border_24);
        }
        holder.favoriteRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!UserInfo.favRecipes.contains(recipeList.get(position).getId())){
                        addFavorite(recipeList.get(position).getId());
                        holder.favoriteRecipeButton.setImageResource(R.drawable.baseline_favorite_24);
                    } else {
                        removeFavorite(recipeList.get(position).getId());
                        holder.favoriteRecipeButton.setImageResource(R.drawable.round_favorite_border_24);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void addFavorite(int rid) throws JSONException {
        JSONObject favorite = new JSONObject();
        favorite.put("fid", 0);
        favorite.put("uid", UserInfo.uid);
        favorite.put("rid", rid);
        StringRequest addFavoriteRequest = new StringRequest(
                Request.Method.POST,
                URL.FAVORITES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UserInfo.updateFavorites(context);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Failed to add recipe as a favorite", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public byte[] getBody() {
                return favorite.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        VolleySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(addFavoriteRequest);
    }

    public void removeFavorite(int rid) throws JSONException {
        // get fid of the favorite
        JsonArrayRequest fidRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL.FAVORITES.concat("/user/" + UserInfo.uid),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int fid = -1;
                        for (int i = 0; i < response.length(); i++){
                            try {
                                if (response.getJSONObject(i).getInt("rid") == rid){
                                    fid = response.getJSONObject(i).getInt("fid");
                                    break;
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        deleteFav(fid);
                        UserInfo.updateFavorites(context);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Failed to get favorite id", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        VolleySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(fidRequest);
    }

    private void deleteFav(int fid){
        // delete favorite by fid
        StringRequest removeRequest = new StringRequest(
                Request.Method.DELETE,
                URL.FAVORITES.concat("/" + fid),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UserInfo.updateFavorites(context);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Failed to remove recipe as favorite", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        VolleySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(removeRequest);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // assigning the different elements of the recipe card to variables
        TextView recipe_name;
        TextView total_time;
        TextView calories;
        TextView rating_num;
        RatingBar recipe_rating;
        ImageView recipe_image;
        FloatingActionButton editRecipeButton;
        FloatingActionButton favoriteRecipeButton;
        FloatingActionButton visitRecipeButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recipe_image = itemView.findViewById(R.id.recipe_image);
            recipe_name = itemView.findViewById(R.id.recipe_name);
            total_time = itemView.findViewById(R.id.total_time);
            calories = itemView.findViewById(R.id.calories);
            recipe_rating = itemView.findViewById(R.id.recipe_rating);
            editRecipeButton = itemView.findViewById(R.id.edit_recipe_button);
            favoriteRecipeButton = itemView.findViewById(R.id.favorite_recipe_button);
            visitRecipeButton = itemView.findViewById(R.id.visit_recipe_button);
            rating_num = itemView.findViewById(R.id.recipe_card_rating);
        }
    }
}

package com.example.pantrypal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MiniRecipeRVAdapter extends RecyclerView.Adapter<MiniRecipeRVAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Recipe> recipeList;

    public MiniRecipeRVAdapter(Context context, ArrayList<Recipe> recipeList){
        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public MiniRecipeRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // builds the recipe card views using the recipe_card xml file
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recipe_mini_card, parent, false);

        return new MiniRecipeRVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiniRecipeRVAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // assigning values to the cards we created in the recipe_card layout file
        // based on the position of the recycler view

        holder.recipe_name.setText(recipeList.get(position).getName());
        holder.recipe_image.setImageBitmap(recipeList.get(position).getImageBitmap());
        holder.rating.setRating(recipeList.get(position).getAvgRating());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewRecipeActivity.class);
                intent.putExtra("rid", recipeList.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // assigning the different elements of the recipe card to variables
        TextView recipe_name;
        ImageView recipe_image;
        RatingBar rating;
        ConstraintLayout card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recipe_image = itemView.findViewById(R.id.recipe_mini_card_image);
            recipe_name = itemView.findViewById(R.id.recipe_mini_name);
            rating = itemView.findViewById(R.id.mini_r_rating);
            card = itemView.findViewById(R.id.recipe_mini_card_constraint);
        }
    }
}

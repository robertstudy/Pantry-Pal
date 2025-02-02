package com.example.pantrypal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReviewRVAdapter extends RecyclerView.Adapter<ReviewRVAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Review> reviewList;

    public ReviewRVAdapter(Context context, ArrayList<Review> reviewList){
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.review_card, parent, false);

        return new ReviewRVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewRVAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.review_username.setText(reviewList.get(position).getUsername());
        holder.review_text.setText(reviewList.get(position).getComment());
        holder.review_stars.setRating(reviewList.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView review_username;
        TextView review_text;
        RatingBar review_stars;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            review_username = itemView.findViewById(R.id.review_username);
            review_text = itemView.findViewById(R.id.review_text);
            review_stars = itemView.findViewById(R.id.review_stars);
        }
    }
}
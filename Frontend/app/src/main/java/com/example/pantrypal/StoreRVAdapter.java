package com.example.pantrypal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StoreRVAdapter extends RecyclerView.Adapter<StoreRVAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<Store> storeList;

    public StoreRVAdapter(Context context, ArrayList<Store> storeList){
        this.context = context;
        this.storeList = storeList;
    }

    @NonNull
    @Override
    public StoreRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // builds the recipe card views using the recipe_card xml file
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.store_card, parent, false);

        return new StoreRVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreRVAdapter.MyViewHolder holder, int position) {
        holder.store_name.setText(storeList.get(position).getName());
        holder.store_rating.setText(storeList.get(position).getRating());
        holder.store_address.setText(storeList.get(position).getAddress());
        holder.store_distance.setText(storeList.get(position).getDistance());
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // assigning the different elements of the recipe card to variables
        TextView store_name;
        TextView store_rating;
        TextView store_address;
        TextView store_distance;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            store_name = itemView.findViewById(R.id.store_name);
            store_rating = itemView.findViewById(R.id.store_rating);
            store_address = itemView.findViewById(R.id.store_address);
            store_distance = itemView.findViewById(R.id.store_distance);
        }
    }
}

package com.example.pantrypal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Adapter To display and update the ingredient cards in IngredientListView
 * @author Michael Linker
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Ingredient> ingredientList;
    private OnClickListener onClickListener;

    public IngredientAdapter(Context context, ArrayList<Ingredient> ingredientList){
        this.context = context;
        this.ingredientList = ingredientList;
    }

    @NonNull
    @Override
    public IngredientAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // builds the ingredient card views using the ingredient_card xml file
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ingredient_card, parent, false);

        return new IngredientAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // assigning values to the cards we created in the ingredient_card layout file
        // based on the position of the recycler view
        holder.ingredient_name.setText(ingredientList.get(position).getName());
//        holder.ingredient_offid.setText("Offid: " + ingredientList.get(position).getOffid());

        //Adding delete and edit button click listeners on each card
        holder.delete.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, ingredientList.get(position), 1);
            }
        });

        holder.edit.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, ingredientList.get(position), 2);
            }
        });

        holder.getInformation.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, ingredientList.get(position), 3);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    // Interface for the click listener
    public interface OnClickListener {
        void onClick(int position, Ingredient ingredient, int buttonID);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        // assigning the different elements of the recipe card to variables
        TextView ingredient_name;
//        TextView ingredient_offid;
        ImageView ingredient_image;
        Button delete;
        Button edit;
        Button getInformation;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredient_name = itemView.findViewById(R.id.ingredient_name);
//            ingredient_offid = itemView.findViewById(R.id.ingredient_calories);
            delete = itemView.findViewById(R.id.i_delete);
            edit = itemView.findViewById(R.id.i_edit);
            getInformation = itemView.findViewById(R.id.i_information);

            delete.setOnClickListener(view -> {
//                    if (onClickListener != null) {
//                        onClickListener.onClick(getBindingAdapterPosition(), ingredientList.get(getBindingAdapterPosition()));
//                    }
            });
            edit.setOnClickListener(view -> {
//                    if (onClickListener != null) {
//                        onClickListener.onClick(getBindingAdapterPosition(), ingredientList.get(getBindingAdapterPosition()));
//                    }
            });
            getInformation.setOnClickListener(view -> {
//                    if (onClickListener != null) {
//                        onClickListener.onClick(getBindingAdapterPosition(), ingredientList.get(getBindingAdapterPosition()));
//                    }
            });
        }
    }
}

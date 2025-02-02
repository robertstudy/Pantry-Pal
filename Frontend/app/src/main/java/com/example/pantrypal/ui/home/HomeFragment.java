package com.example.pantrypal.ui.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.pantrypal.R;
import com.example.pantrypal.Recipe;
import com.example.pantrypal.RecipeRVAdapter;
import com.example.pantrypal.URL;
import com.example.pantrypal.VolleySingleton;
import com.example.pantrypal.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Fragment within the HomeActivity which displays all recipes in the database
 * as cards in a RecyclerView.
 * @author Adrian Boziloff
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ArrayList<Recipe> recipeList = new ArrayList<>();
    private RecipeRVAdapter recipeRVAdapter;
    private RecyclerView recipeRV;

    /**
     * Called when the fragment is created.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    /**
     * Called after onCreate(). Initializes the fragment's views and their
     * respective properties and Listeners.
     *
     * Also initializes the recipe RecyclerView and it's adapter to display recipe
     * cards. Calls getRecipes() at the end to receive recipe database
     * data after initializing.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the created view
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recipeRV = root.findViewById(R.id.recipe_view);

        recipeRVAdapter = new RecipeRVAdapter(this.getContext(), recipeList);
        recipeRV.setAdapter(recipeRVAdapter);
        recipeRV.setLayoutManager(new LinearLayoutManager(this.getContext()));

        getRecipes();

        return root;
    }

    /**
     * Creates a JSON array request for all recipes from the database.
     * On success, recipeList is refreshed with the received recipes;
     * on failure, recipeList is refreshed with no recipes.
     */
    private void getRecipes(){
        JsonArrayRequest recipeRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL.RECIPES,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        recipeList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject recipe = response.getJSONObject(i);
                                double avgRating = 0;
                                if (!recipe.isNull("averageRating")){
                                    avgRating = recipe.getDouble("averageRating");
                                }
                                recipeList.add(new Recipe(recipe.getInt("rid"),
                                        recipe.getInt("uid"),
                                        recipe.getString("rname"),
                                        recipe.getJSONArray("directions"),
                                        recipe.getInt("totalTime"),
                                        recipe.getInt("caloriesPerServing"),
                                        avgRating));
                                recipeRVAdapter = new RecipeRVAdapter(getContext(), recipeList);
                                recipeRV.setAdapter(recipeRVAdapter);
                                recipeRV.setLayoutManager(new LinearLayoutManager(getContext()));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Data error", Toast.LENGTH_SHORT).show();
                        }
                        recipeList.sort(Comparator.comparing(Recipe::getAvgRating).reversed());
                        recipeRVAdapter = new RecipeRVAdapter(getContext(), recipeList);
                        recipeRV.setAdapter(recipeRVAdapter);
                        recipeRV.setLayoutManager(new LinearLayoutManager(getContext()));
                        for (int i = 0; i < recipeList.size(); i++){
                            makeImageRequest(recipeList.get(i).getId(), i);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Failed to get data", Toast.LENGTH_SHORT).show();
                }
        });

        VolleySingleton.getInstance(this.getContext().getApplicationContext()).addToRequestQueue(recipeRequest);
    }

    /**
     * Requests a recipe's image from the database.
     *
     * @param rid recipe id
     * @param index index of recipe in recipeList
     */
    private void makeImageRequest(int rid, int index) {
        String realUrl = URL.RECIPE_IMAGE + "/" + rid;
        ImageRequest imageRequest = new ImageRequest(
                realUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        // Display the image in the ImageView
                        // ingredients.get(0).setImageBitmap(response);
                        // Log.d("tag", response.toString());
                        recipeList.get(index).setImageBitmap(response);

                        recipeRVAdapter = new RecipeRVAdapter(getContext(), recipeList);
                        recipeRV.setAdapter(recipeRVAdapter);
                        recipeRV.setLayoutManager(new LinearLayoutManager(getContext()));                    }
                },
                0, // Width, set to 0 to get the original width
                0, // Height, set to 0 to get the original height
                ImageView.ScaleType.FIT_XY, // ScaleType
                Bitmap.Config.RGB_565, // Bitmap config

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors here
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(this.getContext().getApplicationContext()).addToRequestQueue(imageRequest);
    }
}
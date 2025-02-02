package com.example.pantrypal.ui.profile;

import android.content.Intent;
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
import com.example.pantrypal.HomeActivity;
import com.example.pantrypal.IngredientEditActivity;
import com.example.pantrypal.MiniRecipeRVAdapter;
import com.example.pantrypal.R;
import com.example.pantrypal.Recipe;
import com.example.pantrypal.RecipeRVAdapter;
import com.example.pantrypal.URL;
import com.example.pantrypal.UploadIIngredientImageActivity;
import com.example.pantrypal.UserAccountSettingsActivity;
import com.example.pantrypal.UserInfo;
import com.example.pantrypal.ViewRecipeActivity;
import com.example.pantrypal.VolleySingleton;
import com.example.pantrypal.databinding.FragmentProfileBinding;
import com.example.pantrypal.ui.search.SearchFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private TextView profileTitle;
    private ArrayList<Recipe> userRecipeList = new ArrayList<>();
    private ArrayList<Recipe> favRecipeList = new ArrayList<>();
    private MiniRecipeRVAdapter userRecipeRVAdapter;
    private RecyclerView userRecipeRV;
    private MiniRecipeRVAdapter favRecipeRVAdapter;
    private RecyclerView favRecipeRV;
    private FloatingActionButton settingsBtn;
    private TextView recipes_posted;
    private TextView user_rating;
    private TextView fav_recipes_title;
    private TextView user_recipes_title;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        profileTitle = root.findViewById(R.id.profile_fragment_title);
        profileTitle.setText("Hello " + UserInfo.username);
        settingsBtn = root.findViewById(R.id.settings_button);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserAccountSettingsActivity.class);
                startActivity(intent);            }
        });
        recipes_posted = root.findViewById(R.id.num_recipes_info);
        recipes_posted.setText(String.valueOf(UserInfo.recipesPosted));
        user_rating = root.findViewById(R.id.avg_rating_info);
        fav_recipes_title = root.findViewById(R.id.fav_recipes_title);
        fav_recipes_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.putExtra("fragment", "favorites");
                UserInfo.searchingByFav = true;
                getContext().startActivity(intent);
            }
        });
        user_recipes_title = root.findViewById(R.id.user_recipes_title);
        user_recipes_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.putExtra("fragment", "user made");
                UserInfo.searchingByUMade = true;
                getContext().startActivity(intent);
            }
        });

        userRecipeRV = root.findViewById(R.id.user_recipe_mini_view);

        userRecipeRVAdapter = new MiniRecipeRVAdapter(this.getContext(), userRecipeList);
        userRecipeRV.setAdapter(userRecipeRVAdapter);
        userRecipeRV.setLayoutManager(new LinearLayoutManager(this.getContext()));

        favRecipeRV = root.findViewById(R.id.fav_recipe_mini_view);

        favRecipeRVAdapter = new MiniRecipeRVAdapter(this.getContext(), favRecipeList);
        favRecipeRV.setAdapter(favRecipeRVAdapter);
        favRecipeRV.setLayoutManager(new LinearLayoutManager(this.getContext()));

        getUserRecipes();
        getFavs();
        getAvgUserRating();

        return root;
    }

    private void getAvgUserRating(){
        JsonObjectRequest ratingRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL.RATINGS.concat("/user/avg/" + UserInfo.uid),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            user_rating.setText(String.valueOf(response.getInt("avgRating")));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Unable to get average user rating", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        VolleySingleton.getInstance(this.getContext().getApplicationContext()).addToRequestQueue(ratingRequest);
    }

    private void getUserRecipes(){
        JsonArrayRequest recipeRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL.RECIPES.concat("/user/" + UserInfo.uid),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        userRecipeList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject recipe = response.getJSONObject(i);
                                double avgRating = 0;
                                if (!recipe.isNull("averageRating")){
                                    avgRating = recipe.getDouble("averageRating");
                                }
                                Recipe r = new Recipe(recipe.getInt("rid"),
                                        recipe.getInt("uid"),
                                        recipe.getString("rname"),
                                        recipe.getJSONArray("directions"),
                                        recipe.getInt("totalTime"),
                                        recipe.getInt("caloriesPerServing"),
                                        avgRating);
                                userRecipeList.add(r);
                                makeUserImageRequest(i, recipe.getInt("rid"));
                            }
                        } catch (JSONException e) {
                            Log.e("RECIPE", e.getMessage());
                            Toast.makeText(getContext(), "Data error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getContext(), "Failed to get data", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(this.getContext().getApplicationContext()).addToRequestQueue(recipeRequest);
    }

    private void getFavs(){
        JsonArrayRequest favRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL.FAVORITES.concat("/user/" + UserInfo.uid),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Integer> favorites = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject fav = response.getJSONObject(i);
                                favorites.add(fav.getInt("rid"));
                            }
                        } catch (JSONException e) {
                            Log.e("RECIPE", e.getMessage());
                            Toast.makeText(getContext(), "Data error", Toast.LENGTH_SHORT).show();
                        }
                        getFavRecipes(favorites);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Failed to get data", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(this.getContext().getApplicationContext()).addToRequestQueue(favRequest);
    }

    private void getFavRecipes(ArrayList<Integer> favList){
        JsonArrayRequest recipeRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL.RECIPES,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        favRecipeList.clear();
                        int image_iter = 0;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject recipe = response.getJSONObject(i);
                                if (!favList.contains(recipe.getInt("rid"))){
                                    continue;
                                }
                                double avgRating = 0;
                                if (!recipe.isNull("averageRating")){
                                    avgRating = recipe.getDouble("averageRating");
                                }
                                Recipe r = new Recipe(recipe.getInt("rid"),
                                        recipe.getInt("uid"),
                                        recipe.getString("rname"),
                                        recipe.getJSONArray("directions"),
                                        recipe.getInt("totalTime"),
                                        recipe.getInt("caloriesPerServing"),
                                        avgRating);
                                favRecipeList.add(r);
                                makeFavImageRequest(image_iter++, recipe.getInt("rid"));
                            }
                        } catch (JSONException e) {
                            Log.e("RECIPE", e.getMessage());
                            Toast.makeText(getContext(), "Data error", Toast.LENGTH_SHORT).show();
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
     * @param index image index
     */
    private void makeUserImageRequest(int index, int rid) {
        String realUrl = URL.RECIPE_IMAGE + "/" + rid;
        ImageRequest imageRequest = new ImageRequest(
                realUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        userRecipeList.get(index).setImageBitmap(response);

                        userRecipeRVAdapter = new MiniRecipeRVAdapter(getContext(), userRecipeList);
                        userRecipeRV.setAdapter(userRecipeRVAdapter);
                        userRecipeRV.setLayoutManager(new LinearLayoutManager(getContext()));                    }
                },
                0, // Width, set to 0 to get the original width
                0, // Height, set to 0 to get the original height
                ImageView.ScaleType.FIT_XY, // ScaleType
                Bitmap.Config.RGB_565, // Bitmap config

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }
        );
        VolleySingleton.getInstance(this.getContext().getApplicationContext()).addToRequestQueue(imageRequest);
    }

    private void makeFavImageRequest(int index, int rid) {
        String realUrl = URL.RECIPE_IMAGE + "/" + rid;
        ImageRequest imageRequest = new ImageRequest(
                realUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        favRecipeList.get(index).setImageBitmap(response);

                        favRecipeRVAdapter = new MiniRecipeRVAdapter(getContext(), favRecipeList);
                        favRecipeRV.setAdapter(favRecipeRVAdapter);
                        favRecipeRV.setLayoutManager(new LinearLayoutManager(getContext()));                    }
                },
                0, // Width, set to 0 to get the original width
                0, // Height, set to 0 to get the original height
                ImageView.ScaleType.FIT_XY, // ScaleType
                Bitmap.Config.RGB_565, // Bitmap config

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }
        );
        VolleySingleton.getInstance(this.getContext().getApplicationContext()).addToRequestQueue(imageRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
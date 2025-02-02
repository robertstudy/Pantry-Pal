package com.example.pantrypal.ui.search;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.pantrypal.R;
import com.example.pantrypal.Recipe;
import com.example.pantrypal.RecipeRVAdapter;
import com.example.pantrypal.URL;
import com.example.pantrypal.UserInfo;
import com.example.pantrypal.VolleySingleton;
import com.example.pantrypal.databinding.FragmentSearchBinding;
import com.example.pantrypal.ui.home.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.RangeSlider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Fragment within the HomeActivity which allows the user to search
 * and filter through the recipe database. It currently handles searching by partial name as
 * well as filtering by certain diet types and a calorie range.
 * @author Adrian Boziloff
 */
public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private ArrayList<Recipe> recipeList = new ArrayList<>();
    private RecipeRVAdapter recipeRVAdapter;
    private RecyclerView recipeRV;
    private SearchView searchBar;
    private TextView noRecipesText;
    private RadioGroup dietGroup;
    private FloatingActionButton clearFocusButton;
    private ConstraintLayout searchLayout;
    private LinearLayout filterLayout;
    private EditText searchEditText;
    private RangeSlider calorieRange;
    private TextView resetFilters;
    private String dietType;
    private CheckBox topRatedBtn;
    private CheckBox favBtn;
    private CheckBox uMadeBtn;

    /**
     * Called prior to the creation of the fragment's view. Currently just calls
     * the default Fragment onCreate() method, since our view initialization will
     * happen in onCreateView().
     *
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

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        noRecipesText = root.findViewById(R.id.no_recipes_text);
        searchBar = root.findViewById(R.id.recipe_search_bar);
        searchEditText = searchBar.findViewById(androidx.appcompat.R.id.search_src_text);
        clearFocusButton = root.findViewById(R.id.clear_focus_button);
        clearFocusButton.setVisibility(View.GONE);
        searchLayout = root.findViewById(R.id.search_bar_layout);
        filterLayout = root.findViewById(R.id.filter_layout);
        filterLayout.setVisibility(View.GONE);
        calorieRange = root.findViewById(R.id.calorie_range_slider);
        calorieRange.setValues(0f, 1000f);
        dietGroup = root.findViewById(R.id.diet_radio_group);
        dietType = "";
        resetFilters = root.findViewById(R.id.reset_filters_text_button);
        topRatedBtn = root.findViewById(R.id.top_rating_btn);
        favBtn = root.findViewById(R.id.favorited_btn);
        uMadeBtn = root.findViewById(R.id.user_made_btn);

        calorieRange.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                getRecipes();
            }
        });

        searchBar.clearFocus();
        searchBar.setIconifiedByDefault(false);
        searchBar.setQueryHint("Find Recipes...");

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                Transition transition = new AutoTransition();
                transition.setDuration(200);
                TransitionManager.beginDelayedTransition(searchLayout, transition);
                if (isFocused){
                    clearFocusButton.setVisibility(View.VISIBLE);
                    filterLayout.setVisibility(View.VISIBLE);
                } else {
                    clearFocusButton.setVisibility(View.GONE);
                    filterLayout.setVisibility(View.GONE);
                }
            }
        });

        clearFocusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar.clearFocus();
            }
        });

        dietGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.vegan_check){
                    dietType = "vegan";
                } else if (i == R.id.vegetarian_check){
                    dietType = "vegetarian";
                } else if (i == R.id.pescatarian_check){
                    dietType = "pescatarian";
                } else if (i == R.id.keto_check){
                    dietType = "keto";
                } else {
                    dietType = "";
                }
                getRecipes();
            }
        });

        topRatedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterList(searchBar.getQuery().toString());
            }
        });
        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterList(searchBar.getQuery().toString());
            }
        });
        uMadeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterList(searchBar.getQuery().toString());
            }
        });

        resetFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dietGroup.clearCheck();
                topRatedBtn.setChecked(false);
                favBtn.setChecked(false);
                uMadeBtn.setChecked(false);
                calorieRange.setValues(0f, 1000f);
                getRecipes();
                filterList(searchBar.getQuery().toString());
            }
        });

        recipeRV = root.findViewById(R.id.search_recipe_view);

        recipeRVAdapter = new RecipeRVAdapter(this.getContext(), recipeList);
        recipeRV.setAdapter(recipeRVAdapter);
        recipeRV.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recipeRV.setVisibility(View.VISIBLE);
        recipeRVAdapter.setFilteredList(recipeList);

        getRecipes();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        searchBar.setQuery("", false);
        dietGroup.clearCheck();
        favBtn.setChecked(false);
        uMadeBtn.setChecked(false);
        calorieRange.setValues(0f, 1000f);
        if (UserInfo.searchingByFav){
            favBtn.setChecked(true);
            UserInfo.searchingByFav = false;
        }
        if (UserInfo.searchingByUMade){
            uMadeBtn.setChecked(true);
            UserInfo.searchingByUMade = false;
        }
    }

    /**
     * Filters recipeList for recipes with names that contain the provided string,
     * excluding all non-matching recipes. This means that currently searching by recipe
     * name is handled on the front end.
     * When no recipes are found with matching names, noRecipesText is made visible.
     * @param text      search input text
     */
    private void filterList(String text) {
        Log.e("RECIPES", "--------------------------------------");
        ArrayList<Recipe> filteredList = new ArrayList<>();
        for (Recipe recipe : recipeList) {
            if (favBtn.isChecked() && !UserInfo.favRecipes.contains(recipe.getId())){
                Log.e("RECIPES", "skipped");
                continue;
            }
            if (uMadeBtn.isChecked() && UserInfo.uid != recipe.getUid()){
                continue;
            }
            if (recipe.getName().toLowerCase().contains(text.toLowerCase())) {
                Log.e("RECIPES", "added");
                filteredList.add(recipe);
            }
        }
        if (filteredList.isEmpty()) {
            noRecipesText.setVisibility(View.VISIBLE);
        } else {
            noRecipesText.setVisibility(View.INVISIBLE);
        }
        Log.e("RECIPES", filteredList.toString());
        recipeRVAdapter.setFilteredList(filteredList);
        recipeRVAdapter = new RecipeRVAdapter(getContext(), filteredList);
        recipeRV.setAdapter(recipeRVAdapter);
        recipeRV.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Creates a JSON array request for all recipes from the database which contain the selected
     * filters. On success, recipeList is refreshed with the received recipes; on failure, recipeList
     * is refreshed with no recipes.
     */
    private void getRecipes(){
        String filterParams = "/filter?";
        if (dietType.compareTo("") != 0){
            filterParams += "dietType=" + dietType + "&";
        }
        if (topRatedBtn.isChecked()) {
            filterParams += "averageRating=4.0&";
        }
        filterParams += "minCalories=" + Math.round(calorieRange.getValues().get(0)) + "&";
        filterParams += "maxCalories=" + Math.round(calorieRange.getValues().get(1)) + "&";
        JsonArrayRequest recipeRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL.RECIPES + filterParams,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            recipeList.clear();
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
                                makeImageRequest(recipe.getInt("rid"), i);
                            }
                            searchBar.setQuery(searchBar.getQuery(), true);
                            if (recipeList.isEmpty()) {
                                noRecipesText.setVisibility(View.VISIBLE);
                            } else {
                                noRecipesText.setVisibility(View.INVISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Data error", Toast.LENGTH_SHORT).show();
                        }
                        filterList(searchBar.getQuery().toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                recipeList.clear();
                recipeRVAdapter = new RecipeRVAdapter(getContext(), recipeList);
                recipeRV.setAdapter(recipeRVAdapter);
                recipeRV.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });

        VolleySingleton.getInstance(this.getContext().getApplicationContext()).addToRequestQueue(recipeRequest);
    }

    /**
     * Requests a recipe's image from the database.
     *
     * @param index image index
     */
    private void makeImageRequest(int rid, int index) {
        String realUrl = URL.RECIPE_IMAGE + "/" + rid;
        ImageRequest imageRequest = new ImageRequest(
                realUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        // Display the image in the ImageView
//                        ingredients.get(0).setImageBitmap(response);
//                        Log.d("tag", response.toString());
                        recipeList.get(index).setImageBitmap(response);

                        recipeRVAdapter = new RecipeRVAdapter(getContext(), recipeList);
                        recipeRV.setAdapter(recipeRVAdapter);
                        recipeRV.setLayoutManager(new LinearLayoutManager(getContext()));
                        filterList(searchBar.getQuery().toString());
                    }
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
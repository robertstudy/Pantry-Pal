package com.example.pantrypal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecipeStepListView extends AppCompatActivity {
    private LinearLayout linearLayout;
    private FloatingActionButton backButton;
    LayoutInflater myInflator;
    ArrayList<View> stepViews;
    Context context;
    private static String IMAGE_URL = "http://coms-3090-007.class.las.iastate.edu:8080/images/recipe/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_step_list_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        stepViews = new ArrayList<View>();
        context = getApplicationContext();
        int id = getIntent().getIntExtra("rid", 4);

        linearLayout = findViewById(R.id.linearLayout);
        backButton = findViewById(R.id.return_button5);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipeStepListView.this, ViewRecipeActivity.class);
                intent.putExtra("rid",id);
                startActivity(intent);
            }
        });



//        View view = this.scrollView; // returns base view of the fragment
////        if (view == null)
////            return;
////        if (!(view instanceof ViewGroup))
//
//        ViewGroup viewGroup = (ViewGroup) view;
//        View popup = View.inflate(viewGroup.getContext(), R.layout.recipe_step, viewGroup);
//        popup.setLayoutParams(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT)
//        );
        myInflator = getLayoutInflater();
//        for (int i = 0; i < 5; i++) {
//            View myView = myInflator.inflate(R.layout.recipe_step_with_image, (ViewGroup) getCurrentFocus(), false);
//            linearLayout.addView(myView);
//        }

        getRecipe(id);

    }


    private void getRecipe(int rid) {
        JsonObjectRequest recipeRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL.RECIPES + "/" + rid,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            TextView ititle = new TextView(context);
                            ititle.setLayoutParams( new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ));
                            ititle.setText("Ingredients:");
                            ititle.setTextSize(30);
                            linearLayout.addView(ititle);
                            JSONArray directions = response.getJSONArray("directions");
                            JSONArray ingredients = response.getJSONArray("ingredientList");
//                            for (int i = 0; i < ingredients.length(); i++) {
//                                View myView = myInflator.inflate(R.layout.recipe_ingredient_row_view, (ViewGroup) getCurrentFocus(), false);
//                                TextView ingredientText = myView.findViewById(R.id.ingredient_text);
//                                ingredientText.setText((String)ingredients.get(i));
//                                ingredientText.setPadding(5,0,0,-4);
////                                stepViews.add(myView);
//                                linearLayout.addView(myView);
//                            }
                            TextView ingredientList = new TextView(context);
                            ingredientList.setLayoutParams( new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ));
                            for (int i = 0; i < ingredients.length(); i++) {
                                if (i != 0)
                                    ingredientList.setText(ingredientList.getText() + ", " + (String)ingredients.get(i));
                                if (i == 0)
                                    ingredientList.setText((String)ingredients.get(i));
                            }
                            ingredientList.setTextSize(22);
                            linearLayout.addView(ingredientList);

                            TextView stitle = new TextView(context);
                            stitle.setLayoutParams( new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ));
                            stitle.setText("Directions:");
                            stitle.setTextSize(30);
                            linearLayout.addView(stitle);
                            for (int i = 0; i < directions.length(); i++) {
                                View myView = myInflator.inflate(R.layout.recipe_step_with_image, (ViewGroup) getCurrentFocus(), false);
                                TextView stepText = myView.findViewById(R.id.step_text);
                                stepText.setText("Step " + (i + 1) + ": " + (String)directions.get(i));
                                stepViews.add(myView);
                                linearLayout.addView(myView);
                                makeImageRequest(rid,i);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Data error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Failed to get recipe data", Toast.LENGTH_SHORT).show();
                Log.e("Recipe Error", error.toString());
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(recipeRequest);
    }

    private void makeImageRequest(int index, int step) {
        int imageIdNum = index;
        String realUrl = IMAGE_URL + "step/" + index + "/" + (step + 1);
        ImageRequest imageRequest = new ImageRequest(
                realUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        // Display the image in the ImageView
//                        ingredients.get(0).setImageBitmap(response);
//                        Log.d("tag", response.toString());
                        ImageView imageView = stepViews.get((step)).findViewById(R.id.imageView2);
                        imageView.setImageBitmap(response);
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
                        ImageView imageView = stepViews.get(step).findViewById(R.id.imageView2);
                        imageView.setVisibility(View.GONE);
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(imageRequest);
    }



}
package com.example.pantrypal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * In this view you can see every ingredient that we have to offer, and have information for.
 * From this screen you can scroll down the recycler view for the list of ingredients.
 * @author Michael Linker
 */
public class IngredientListView extends AppCompatActivity {

    ArrayList<Ingredient> ingredients =  new ArrayList<>();
    private IngredientAdapter ingredientAdapter;
    private RecyclerView ingredientRV;
    private Context context;

    public static final String URL_IMAGE = "http://coms-3090-007.class.las.iastate.edu:8080/images/ingredient";

    /**
     * This method is called when the page is launched, It insantiates the Recycler View,
     * Adapter, and all the the UI elements.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingridient_list_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        context = getApplicationContext();

       getListOfIngredients();

        FloatingActionButton addIngredient = findViewById(R.id.add_ingredient_button);
        FloatingActionButton homeButton = findViewById(R.id.return_button3);


        //COMMENT OUT REMOVE WHEN U HAVE REAL DATA AGAIN ::::
//        ingredients.add(new Ingredient("Potato", "Potato", 0));
//        ingredients.add(new Ingredient("Tomato", "Potato", 1));
//        refreshRecyleView();


        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IngredientListView.this, IngredientCreateScreen.class);
                startActivity(intent);
            }
        });

//        menuButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (sideView.getZ() == 100) {
//                    sideView.setZ(0);
//                    sideView.setVisibility(View.INVISIBLE);
//                    menuButton.setTranslationX(00);
//                }else {
//                    sideView.setBackgroundColor(getResources().getColor(R.color.white));
//                    sideView.setZ(100);
//                    sideView.setVisibility(View.VISIBLE);
//                    menuButton.setTranslationX(-315);
//
//                }
//            }
//        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IngredientListView.this, HomeActivity.class);
                startActivity(intent);
            }
        });

//        chatButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(IngredientListView.this, AdminChatActivity.class);
//                startActivity(intent);
//            }
//        });


    }

    /**
     * Gets Array of Ingredients from the database and updates the ingredients arrayList
     * and refreshed the recycler view if any new data is added
     */
    private void getListOfIngredients() {
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(
                Request.Method.GET,
                URL.INGREDIENTS,
                null, // Pass null as the request body since it's a GET request
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());

                        // Parse the JSON array and add data to the adapter
                        ingredients.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String name = jsonObject.getString("iname");
                                String offid = jsonObject.getString("offid");
                                int iid = jsonObject.getInt("iid");
                                Log.d("Volley Response", name);

                                // Create a ListItemObject and add it to the adapter
                                Ingredient item = new Ingredient(name, offid, iid);

                                ingredients.add(item);
                                makeImageRequest(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                       refreshRecyleView();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
//                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
//                params.put("param1", "value1");
//                params.put("param2", "value2");
                return params;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrReq);
    }

    /**
     * Sends an ingredient to delete to the backend, and displays on screen the success or failure
     * @param iid ID of ingredient to be deleted
     */
    private void deleteIngredient(int iid){
        StringRequest deleteRequest = new StringRequest(
                Request.Method.DELETE,
                URL.INGREDIENTS + "/" + iid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Ingredient deleted", Toast.LENGTH_SHORT).show();
                        getListOfIngredients();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Failed to delete Ingredient", Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(deleteRequest);
    }

    /**
     * Gets an image if it from the database each ingredient
     * @param index Current Ingredient Index in recycler view
     */
    private void makeImageRequest(int index) {
        int imageIdNum = index + 1;
        String realUrl = URL_IMAGE + imageIdNum;
        ImageRequest imageRequest = new ImageRequest(
                realUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        // Display the image in the ImageView
//                        ingredients.get(0).setImageBitmap(response);
//                        Log.d("tag", response.toString());
                        ingredients.get(index).setImageBitmap(response);
                        refreshRecyleView();
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(imageRequest);
    }

    /**
     * Runs all the neccesarry code to refresh the recycer view anytime data is added to the ingredients
     * arraylist.
     */
    void refreshRecyleView() {
        ingredientRV = findViewById(R.id.recipe_step_view);
        ingredientRV.setLayoutManager(new LinearLayoutManager(context));
        ingredientAdapter = new IngredientAdapter(context, ingredients);
        ingredientRV.setAdapter(ingredientAdapter);
        ingredientRV.refreshDrawableState();
        Log.d("Here", ingredientAdapter.toString());

        ingredientAdapter.setOnClickListener(new IngredientAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Ingredient ingredient, int buttonId) {
                Log.d("CLICKED", ingredient.name);
                if (buttonId == 1) {
                    deleteIngredient(ingredient.iid);
                }else if (buttonId == 2) {
                    Intent intent = new Intent(IngredientListView.this, IngredientEditActivity.class);
                    Log.d("int", String.valueOf(ingredient.iid));
                    intent.putExtra("iid", ingredient.iid);
                    intent.putExtra("name", ingredient.getName());

                    startActivity(intent);
                }else if (buttonId == 3) {
                    Intent intent = new Intent(IngredientListView.this, NutritionInformationActivity.class);
                    Log.d("int", String.valueOf(ingredient.iid));
                    intent.putExtra("id", ingredient.iid);
                    intent.putExtra("name", ingredient.getName());

                    startActivity(intent);
                }
            }
        });
    }

}
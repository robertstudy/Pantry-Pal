package com.example.pantrypal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Able to view all information we have stored in the database about this ingredient
 * @author Michael Linker
 */
public class NutritionInformationActivity extends AppCompatActivity {

    TextView energyPer100g;
    TextView fatPer100g;
    TextView saturatedFatPer100g ;
    TextView carbohydratesPer100g ;
    TextView sugarsPer100g;
    TextView proteinPer100g;
    TextView saltPer100g;
    TextView fiberPer100g;
    TextView title ;
    ImageView imageView ;
    private static String IMAGE_URL = "http://coms-3090-007.class.las.iastate.edu:8080/images/ingredient/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nutrition_information);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


         energyPer100g = findViewById(R.id.ingredient_information);
         fatPer100g = findViewById(R.id.ingredient_information2);
         saturatedFatPer100g = findViewById(R.id.ingredient_information3);
         carbohydratesPer100g = findViewById(R.id.ingredient_information4);
         sugarsPer100g = findViewById(R.id.ingredient_information5);
         proteinPer100g = findViewById(R.id.ingredient_information6);
         saltPer100g = findViewById(R.id.ingredient_information7);
         fiberPer100g = findViewById(R.id.ingredient_information8);
         title = findViewById(R.id.ingredient_title);
         imageView = findViewById(R.id.ingredient_info_imageview);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 1);
        String titleText;
        try {
            titleText = intent.getStringExtra("name");
        } catch (Throwable e) {
            titleText = "Ingredient Name";
        }
        title.setText(titleText);
        FloatingActionButton homeButton = findViewById(R.id.return_button4);


        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NutritionInformationActivity.this, IngredientListView.class);
                startActivity(intent);
            }
        });

        getIngredientInfo(id);
        makeImageRequest(id);

    }

    private void makeImageRequest(int index) {
        int imageIdNum = index;
        String realUrl = IMAGE_URL + imageIdNum;
        ImageRequest imageRequest = new ImageRequest(
                realUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        // Display the image in the ImageView
//                        ingredients.get(0).setImageBitmap(response);
//                        Log.d("tag", response.toString());
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
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(imageRequest);
    }


    private void getIngredientInfo(int rid){
        JsonObjectRequest recipeRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL.INGREDIENTS + "/" + rid,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Random random = new Random();
                            String energyPer100gS = ((response.getString("energyPer100g") == null ? response.getString("energyPer100g") : String.valueOf(Math.abs(random.nextInt() % 20))));
                            String fatPer100gS = ( (response.getString("fatPer100g") == null ? response.getString("fatPer100g") : String.valueOf(Math.abs(random.nextInt() % 20))));
                            String saturatedFatPer100gS = ( (response.getString("saturatedFatPer100g") == null ? response.getString("saturatedFatPer100g") : String.valueOf(Math.abs(random.nextInt() % 20))));
                            String carbohydratesPer100gS = ( (response.getString("carbohydratesPer100g") == null ? response.getString("carbohydratesPer100g") : String.valueOf(Math.abs(random.nextInt() % 20))));
                            String sugarsPer100gS = ((response.getString("sugarsPer100g") == null ? response.getString("sugarsPer100g") : String.valueOf(Math.abs(random.nextInt() % 20))));
                            String proteinPer100gS = ((response.getString("proteinPer100g") == null ? response.getString("proteinPer100g") : String.valueOf(Math.abs(random.nextInt() % 20))));
                            String saltPer100gS = ((response.getString("saltPer100g") == null ? response.getString("saltPer100") : String.valueOf(Math.abs(random.nextInt() % 20))));
                            String fiberPer100gS = ((response.getString("fiberPer100g") == null ? response.getString("fiberPer100") : String.valueOf(Math.abs(random.nextInt() % 20))));
                            energyPer100g.setText("Calories: " + energyPer100gS + "g");
                            fatPer100g.setText("Fat: " + fatPer100gS + "g");
                            saturatedFatPer100g.setText("Saturated Fat: " + saturatedFatPer100gS + "g");
                            carbohydratesPer100g.setText("Carbs: " + carbohydratesPer100gS + "g");
                            sugarsPer100g.setText("Sugar: " + sugarsPer100gS + "g");
                            proteinPer100g.setText("Protein: " + proteinPer100gS + "g");
                            saltPer100g.setText("Salt: " + saltPer100gS + "g");
                            fiberPer100g.setText("Fiber: " + fiberPer100gS + "g");
                            Log.d("ID:", String.valueOf(rid));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Data error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Failed to get Ingredient data", Toast.LENGTH_SHORT).show();
                Log.e("Recipe Error", error.toString());
            }
        });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(recipeRequest);
    }


}
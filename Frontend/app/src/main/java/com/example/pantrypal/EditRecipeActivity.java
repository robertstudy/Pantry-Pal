package com.example.pantrypal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

public class EditRecipeActivity extends AppCompatActivity {

    private FloatingActionButton saveButton;
    private FloatingActionButton returnButton;
    private FloatingActionButton deleteButton;
    private FloatingActionButton uploadImage;
    private EditText rnameEdit;
    private EditText directionsEdit;
    private EditText prepTimeEdit;
    private int recipeId;
    private ImageView imageView;

    private static String IMAGE_URL = "http://coms-3090-007.class.las.iastate.edu:8080/images/recipe/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);
        EdgeToEdge.enable(this);

        saveButton = findViewById(R.id.save_button);
        returnButton = findViewById(R.id.return_button);
        deleteButton = findViewById(R.id.delete_button);
        uploadImage = findViewById(R.id.r_upload_image);
        rnameEdit = findViewById(R.id.edit_rname);
        directionsEdit = findViewById(R.id.edit_directions);
        prepTimeEdit = findViewById(R.id.edit_prep_time);
        imageView = findViewById(R.id.create_recipe_image);

        Intent intent = getIntent();
        recipeId = intent.getIntExtra("rid", 1);
        getRecipeInfo(recipeId);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String rname = rnameEdit.getText().toString().trim();
                final String directions = directionsEdit.getText().toString().trim();
                final String prepTime = prepTimeEdit.getText().toString().trim();

                updateRecipe(rname, directions, Integer.parseInt(prepTime));
            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditRecipeActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecipe(recipeId);
            }
        });
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditRecipeActivity.this, UploadIRecipeImageActivity.class);
                intent.putExtra("id", recipeId);
                startActivity(intent);
            }
        });
    }

    private void updateRecipe(String rname, String directions, int prepTime){
        JSONObject recipeInfo = new JSONObject();
        try {
            //recipeInfo.put("rid", 0);
            recipeInfo.put("rname", rname);
            recipeInfo.put("directions", directions);
            recipeInfo.put("prepTime", prepTime);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        StringRequest recipeEditRequest = new StringRequest(
                Request.Method.PUT,
                URL.RECIPES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Recipe Updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditRecipeActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Recipe could not be edited", Toast.LENGTH_SHORT).show();
                        Log.e("Recipe Error", recipeInfo.toString());
                    }
                }
        ){
            @Override
            public byte[] getBody() {
                return recipeInfo.toString().getBytes(); // Send your recipeInfo JSON object as the request body
            }

            @Override
            public String getBodyContentType() {
                return "application/json";  // Specify the content type if your API requires it
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(recipeEditRequest);
    }

    private void getRecipeInfo(int rid){
        JsonObjectRequest recipeRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL.RECIPES + "/" + rid,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            rnameEdit.setText(response.getString("rname"));
                            directionsEdit.setText(response.getString("directions"));
                            prepTimeEdit.setText(response.getString("prepTime"));
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

    private void deleteRecipe(int rid){
        StringRequest deleteRequest = new StringRequest(
                Request.Method.DELETE,
                URL.RECIPES + "/" + rid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Recipe deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditRecipeActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Failed to delete recipe", Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(deleteRequest);
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
}
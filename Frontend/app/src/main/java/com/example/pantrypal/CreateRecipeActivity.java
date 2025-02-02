package com.example.pantrypal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CreateRecipeActivity extends AppCompatActivity {

    private FloatingActionButton uploadButton;
    private FloatingActionButton returnButton;
    private EditText rnameEdit;
    private EditText servings;
    private EditText calPerServing;
    private EditText prepTimeHr;
    private EditText prepTimeMin;
    private EditText totalTimeHr;
    private EditText totalTimeMin;
    private LinearLayout ingredientLayout;
    private FloatingActionButton addIngredientBtn;
    private LinearLayout stepsLayout;
    private FloatingActionButton addStepBtn;
    private FloatingActionButton uploadImgBtn;
    private ImageView recipeImage;
    private Uri selectiedUri;
    private ActivityResultLauncher<String> mGetContent;

    /**
     * Called when the activity is created. Initializes all views and necessary
     * components of the class.
     *
     * @param savedInstanceState If the activity is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);
        EdgeToEdge.enable(this);

        uploadButton = findViewById(R.id.upload_button);
        returnButton = findViewById(R.id.return_button);
        rnameEdit = findViewById(R.id.create_rname);
        servings = findViewById(R.id.create_yields);
        calPerServing = findViewById(R.id.create_cal);
        prepTimeHr = findViewById(R.id.create_prep_hr);
        prepTimeMin = findViewById(R.id.create_prep_min);
        totalTimeHr = findViewById(R.id.create_tot_hr);
        totalTimeMin = findViewById(R.id.create_tot_min);
        ingredientLayout = findViewById(R.id.ingredient_list_layout);
        addIngredientBtn = findViewById(R.id.add_ingredient_btn);
        stepsLayout = findViewById(R.id.steps_list_layout);
        addStepBtn = findViewById(R.id.add_step_btn);
        uploadImgBtn = findViewById(R.id.upload_recipe_img_btn);
        recipeImage = findViewById(R.id.create_recipe_image);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadButton.setClickable(false);
                final String rname = rnameEdit.getText().toString().trim();
                final int totalTime = (Integer.parseInt(totalTimeHr.getText().toString().trim()) * 60)
                        + Integer.parseInt(totalTimeMin.getText().toString().trim());
                final int prepTime = (Integer.parseInt(prepTimeHr.getText().toString().trim()) * 60)
                        + Integer.parseInt(prepTimeMin.getText().toString().trim());
                final int cookTime = totalTime - prepTime;
                final int serv = Integer.parseInt(servings.getText().toString().trim());
                final int cal = Integer.parseInt(calPerServing.getText().toString().trim());

                uploadRecipe(rname, totalTime, prepTime, cookTime, serv, cal);
            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateRecipeActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        addIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIngredient();
            }
        });

        addStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStep();
            }
        });

        // select image from gallery
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectiedUri = uri;
                        recipeImage.setImageURI(uri);
                    }
                });

        uploadImgBtn.setOnClickListener(v -> mGetContent.launch("image/*"));
    }

    private void addIngredient(){
        View ingredientView = getLayoutInflater().inflate(R.layout.recipe_ingredient_row, null, false);

        EditText editAmount = ingredientView.findViewById(R.id.edit_amount);
        EditText editName = ingredientView.findViewById(R.id.edit_ingredient);
        ImageView deleteBtn = ingredientView.findViewById(R.id.delete_ingredient_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientLayout.removeView(ingredientView);
            }
        });

        ingredientLayout.addView(ingredientView);
    }

    private void addStep(){
        View stepView = getLayoutInflater().inflate(R.layout.recipe_step, null, false);

        EditText editStep = stepView.findViewById(R.id.edit_step);
        ImageView deleteBtn = stepView.findViewById(R.id.delete_step_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepsLayout.removeView(stepView);
            }
        });

        stepsLayout.addView(stepView);
    }

    private void uploadRecipe(String rname, int totalTime, int prepTime, int cookTime, int serv, int calPerServ){
        JSONObject recipeInfo = new JSONObject();
        JSONArray directionList = new JSONArray();
        JSONArray ingredientList = new JSONArray();
        JSONArray amountsList = new JSONArray();
        for (int i = 0; i < ingredientLayout.getChildCount(); i++){
            View v = ingredientLayout.getChildAt(i);
            EditText amount = v.findViewById(R.id.edit_amount);
            amountsList.put(amount.getText().toString().trim());
        }
        for (int i = 0; i < ingredientLayout.getChildCount(); i++){
            View v = ingredientLayout.getChildAt(i);
            EditText name = v.findViewById(R.id.edit_ingredient);
            ingredientList.put(name.getText().toString().trim());
        }
        for (int i = 0; i < stepsLayout.getChildCount(); i++){
            View v = stepsLayout.getChildAt(i);
            EditText step = v.findViewById(R.id.edit_step);
            directionList.put(step.getText().toString().trim());
        }
        try {
            recipeInfo.put("rid", 0);
            recipeInfo.put("rname", rname);
            recipeInfo.put("directions", directionList);
            recipeInfo.put("ingredientList", ingredientList);
            recipeInfo.put("amountsList", amountsList);
            recipeInfo.put("totalTime", totalTime);
            recipeInfo.put("prepTime", prepTime);
            recipeInfo.put("cookTime", cookTime);
            recipeInfo.put("uid", UserInfo.uid);
            recipeInfo.put("servings", serv);
            recipeInfo.put("caloriesPerServing", calPerServ);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        JsonObjectRequest recipePostRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL.RECIPES,
                recipeInfo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        uploadButton.setClickable(true);
                        try {
                            uploadImage(response.getInt("rid"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        Toast.makeText(getApplicationContext(), "Recipe Uploaded", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateRecipeActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        uploadButton.setClickable(true);
                        Log.e("VOLLEY", error.toString());
                        Toast.makeText(getApplicationContext(), "Recipe could not be made", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(recipePostRequest);
    }

    /**
     * Uploads an image to a remote server using a multipart Volley request.
     *
     * This method creates and executes a multipart request using the Volley library to upload
     * an image to the database. The image data is sent as a byte array and the
     * request is configured to handle multipart/form-data content type. The server is expected
     * to accept the image with a specific key ("image") in the request.
     *
     */
    private void uploadImage(int rid){
        Log.d("URI path", selectiedUri.getLastPathSegment());
        byte[] imageData = convertImageUriToBytes(selectiedUri);
        Log.d("URI", selectiedUri.toString());
        MultipartRequest multipartRequest = new MultipartRequest(
                Request.Method.POST,
                URL.IMAGES.concat("/recipe/" + rid),
                imageData,
                selectiedUri.getLastPathSegment().replace(':', '_'),
                response -> {
                    // Handle response
                    Log.d("Upload", "Response: " + response);
                },
                error -> {
                    // Handle error
                    Log.e("Upload", "Error: " + error.getMessage());
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);
    }

    /**
     * Converts the given image URI to a byte array.
     *
     * This method takes a URI pointing to an image and converts it into a byte array. The conversion
     * involves opening an InputStream from the content resolver using the provided URI, and then
     * reading the content into a byte array. This byte array represents the binary data of the image,
     * which can be used for various purposes such as uploading the image to a server.
     *
     * @param imageUri The URI of the image to be converted. This should be a content URI that points
     *                 to an image resource accessible through the content resolver.
     * @return A byte array representing the image data, or null if the conversion fails.
     *
     */
    private byte[] convertImageUriToBytes(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            return byteBuffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

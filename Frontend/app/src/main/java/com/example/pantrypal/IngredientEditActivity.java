package com.example.pantrypal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * On this screen users can edit an ingredient to more accurately fit into their recipes
 * @author Michael Linker
 */
public class IngredientEditActivity extends AppCompatActivity {

    private int iid;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingredient_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        iid = getIntent().getIntExtra("iid", 3);
        name = getIntent().getStringExtra("name");
        Log.d("int", String.valueOf(iid));

        Button save = findViewById(R.id.i_save);
        Button uploadImage = findViewById(R.id.i_upload_image);
        FloatingActionButton back = findViewById(R.id.return_button2);
        EditText nameField = findViewById(R.id.i_edittext);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateIngredient(name, nameField.getText().toString().trim(), iid);
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IngredientEditActivity.this, UploadIIngredientImageActivity.class);
                intent.putExtra("id", iid);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IngredientEditActivity.this, IngredientListView.class);
                startActivity(intent);
            }
        });

    }

    private void updateIngredient(String iname, String offid, int iid){
        JSONObject ingredientInfo = new JSONObject();
        try {
            ingredientInfo.put("iname", iname);
            ingredientInfo.put("offid", offid);
            ingredientInfo.put("iid", iid);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Ask backend to change response to JSON object so we can use JSONObjectRequest for these
        // and also to stay consistent
        StringRequest ingredientEditRequest = new StringRequest(
                Request.Method.PUT,
                URL.INGREDIENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Ingredient Updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(IngredientEditActivity.this, IngredientListView.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Ingredient could not be edited", Toast.LENGTH_SHORT).show();
                        Log.e("Ingredient Error", ingredientInfo.toString());
                    }
                }
        ){
            @Override
            public byte[] getBody() {
                return ingredientInfo.toString().getBytes(); // Send your recipeInfo JSON object as the request body
            }

            @Override
            public String getBodyContentType() {
                return "application/json";  // Specify the content type if your API requires it
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(ingredientEditRequest);
    }
}
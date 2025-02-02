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
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Users on this screen can add an ingredient to the database to be used in future recipes.
 * @author Michael Linker
 */
public class IngredientCreateScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingredient_create_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText iname = findViewById(R.id.edit_iname);
        Button addIngredient = findViewById(R.id.add_ingredient);
        FloatingActionButton backButton = findViewById(R.id.back_button);

        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createIngredient(iname.getText().toString().trim(), iname.getText().toString().trim());
                Intent intent = new Intent(IngredientCreateScreen.this, IngredientListView.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IngredientCreateScreen.this, IngredientListView.class);
                startActivity(intent);
            }
        });
    }

    private void createIngredient(String name, String offid) {
        JSONObject ingredientInfo = new JSONObject();
        try {
            ingredientInfo.put("iname", name);
            ingredientInfo.put("offid", offid);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest ingredientCreateRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL.INGREDIENTS,
                ingredientInfo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Ingredient Added", Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "ingredient not allowed", Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(ingredientCreateRequest);
    }
}
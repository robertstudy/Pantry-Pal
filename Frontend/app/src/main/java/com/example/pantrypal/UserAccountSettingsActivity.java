package com.example.pantrypal;

import static com.example.pantrypal.UserInfo.username;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.pantrypal.ui.profile.ProfileFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserAccountSettingsActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_account_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        TextView userIdText = findViewById(R.id.userd_id_text);
        TextView usernameText = findViewById(R.id.user_name_title);
        TextView passwordText = findViewById(R.id.password_text);
        Button helpButton = findViewById(R.id.help_button);
        Button resetButton = findViewById(R.id.reset_button);
        Button deleteButton = findViewById(R.id.delete_button);
        FloatingActionButton backButton = findViewById(R.id.return_button6);
        FloatingActionButton revealPasswordButton = findViewById(R.id.reveal_password);

        // Set placeholder text

        userIdText.setText("User ID: " + UserInfo.uid);
        usernameText.setText("" + UserInfo.username);
        passwordText.setText("" + UserInfo.password);

        // Initially hide password
        passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Toggle password visibility
        revealPasswordButton.setOnClickListener(view -> {
            if (isPasswordVisible) {
                // Hide password
                passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                // Show password
                passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            isPasswordVisible = !isPasswordVisible;
            // Move cursor to the end of the text
        });

        // Help Button: Navigate to AdminChatActivity
        helpButton.setOnClickListener(view -> {
            Intent intent = new Intent(UserAccountSettingsActivity.this, AdminChatActivity.class);
            startActivity(intent);
        });

        resetButton.setOnClickListener(view -> {
            Intent intent = new Intent(UserAccountSettingsActivity.this, ForgotPasswordActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // Delete Button: Show confirmation dialog
        deleteButton.setOnClickListener(view -> {
            new AlertDialog.Builder(UserAccountSettingsActivity.this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(UserAccountSettingsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        deleteUser();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Back Button: Navigate to HomeActivity
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(UserAccountSettingsActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void deleteUser(){
        StringRequest deleteRequest = new StringRequest(
                Request.Method.DELETE,
                URL.USERS + "/" + UserInfo.uid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "User deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserAccountSettingsActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Failed to delete user", Toast.LENGTH_SHORT).show();
                        Log.e("ERROR", error.toString());
                    }
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(deleteRequest);
    }



}
package com.example.pantrypal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pantrypal.databinding.ActivityHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Main activity with which the user interacts with the app.
 * @author Adrian Boziloff
 */
public class HomeActivity extends AppCompatActivity implements WebSocketListener {

    private ActivityHomeBinding binding;
    private FloatingActionButton deleteUserButton;
    private FloatingActionButton ingredientViewButton;
    private FrameLayout notificationContainer;
    private BottomNavigationView navView;

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
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        WebSocketManager.getInstance().connectWebSocket(URL.WEBSOCKET + "/notifications");
        WebSocketManager.getInstance().setWebSocketListener(HomeActivity.this);

        navView = findViewById(R.id.nav_view);
        deleteUserButton = findViewById(R.id.delete_user_button);
        ingredientViewButton = findViewById(R.id.ingredient_view_button);
        notificationContainer = findViewById(R.id.notification_container);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        getUserId();

        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser();
            }
        });

        ingredientViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, IngredientListView.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getStringExtra("fragment") != null){
            navView.setSelectedItemId(R.id.navigation_search);
        }
    }

    /**
     * Called when the notification websocket recieves a message
     *
     * @param message The received WebSocket message.
     */
    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(() -> {
            showNotification(message);
        });
    }
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) { }
    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) { }
    @Override
    public void onWebSocketError(Exception ex) { }

    /**
     * Creates a recipe notification card and displays it temporarily.
     * The card includes the uploaded recipe's name.
     * @param message notification message
     */
    private void showNotification(String message) {
        // Inflate a new instance of the notification card
        LayoutInflater inflater = LayoutInflater.from(this);
        View notificationView = inflater.inflate(R.layout.recipe_notification, notificationContainer, false);
        TextView notificationText = notificationView.findViewById(R.id.store_address);

        // Ignore message notification timestamp
        String text = "";
        String[] arr = message.split(" ");
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                text += arr[i] + " ";
            }
        }

        notificationText.setText(text);

        notificationContainer.addView(notificationView);

        notificationView.postDelayed(() -> {
            notificationView.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> notificationContainer.removeView(notificationView));
        }, 3000);
    }

    /**
     * Requests the user's ID from the database and saves it into the userId variable
     */
    private void getUserId() {
        JsonObjectRequest recipeRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL.USERS.concat("/" + UserInfo.username),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            UserInfo.uid = response.getInt("uid");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Data error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Failed to get user data", Toast.LENGTH_SHORT).show();
//                Log.e("ERROR", username);
            }
        });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(recipeRequest);
    }

    /**
     * Deletes the current user from the database and returns to the login activity
     */
    private void deleteUser(){
        StringRequest deleteRequest = new StringRequest(
                Request.Method.DELETE,
                URL.USERS + "/" + UserInfo.uid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "User deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
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
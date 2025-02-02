package com.example.pantrypal;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private Button signupButton;
    private Button forgotPasswordButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView mainText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EdgeToEdge.enable(this);
        // -------------------------------------------
        // All this stuff was added by default. Idk what it does so I'm leaving it for now.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // -------------------------------------------

        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.signup_button);
        forgotPasswordButton = findViewById(R.id.forgot_password_button);
        usernameEditText = findViewById(R.id.login_username_edt);
        passwordEditText = findViewById(R.id.login_password_edt);
        mainText = findViewById(R.id.msgText);
        mainText.setText("Pantry Pal");

        createNotificationChannel();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        // go to sign up page
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notification_channel";
            String description = "notification channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notif_channel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void login(){
        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        // short animation that shakes the object horizontally to signify an error
        AnimatorSet errorShake = new AnimatorSet();
        ObjectAnimator shakeLeft = ObjectAnimator.ofFloat(passwordEditText, "translationX", -20);
        ObjectAnimator shakeRight = ObjectAnimator.ofFloat(passwordEditText, "translationX", 20);
        ObjectAnimator shakeCenter = ObjectAnimator.ofFloat(passwordEditText, "translationX", 0);
        errorShake.play(shakeLeft).before(shakeRight);
        errorShake.play(shakeCenter).after(shakeRight);
        errorShake.setDuration(50);

        JsonObjectRequest loginRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL.USERS.concat("/" + username),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.opt("password") != null && response.opt("password").equals(password)) {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            UserInfo.username = username;
                            UserInfo.password = password;
                            UserInfo.getUserInfo(getApplicationContext());
                            try {
                                UserInfo.uid = response.getInt("uid");
                            } catch (JSONException e){
                                Toast.makeText(getApplicationContext(), "Failed to get user id", Toast.LENGTH_SHORT).show();
                            }
                            startActivity(intent);
                        } else {
                            AnimatorSet passShake = new AnimatorSet();
                            passShake.playSequentially(errorShake.clone(), errorShake.clone());
                            passShake.start();
                            passwordEditText.setTextColor(Color.parseColor("#ff9393"));
                            passwordEditText.setBackgroundColor(Color.parseColor("#ffdede"));
                            passwordEditText.setTextColor(Color.parseColor("#ff9393"));
                            Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "No such username exists", Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(loginRequest);
    }
}

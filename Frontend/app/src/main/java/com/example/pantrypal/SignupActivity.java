package com.example.pantrypal;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText;  // define username edittext variable
    private EditText passwordEditText;  // define password edittext variable
    private EditText confirmEditText;   // define confirm edittext variable
    private Button signupButton;        // define signup button variable
    private Button menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        EdgeToEdge.enable(this);

        // initialize UI elements
        usernameEditText = findViewById(R.id.signup_username_edt);  // link to username edit text in the Signup activity XML
        passwordEditText = findViewById(R.id.signup_password_edt);  // link to password edit text in the Signup activity XML
        confirmEditText = findViewById(R.id.signup_confirm_edt);    // link to confirm edit text in the Signup activity XML
        signupButton = findViewById(R.id.signup_btn);  // link to signup button in the Signup activity XML
        menuButton = findViewById(R.id.menu_button);

        // click listener on signup button pressed
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameEditText.getText().toString().trim();
                final String password = passwordEditText.getText().toString().trim();
                final String confirmPass = confirmEditText.getText().toString().trim();

                // short animation that shakes the object horizontally to signify an error
                AnimatorSet errorShake = new AnimatorSet();
                ObjectAnimator shakeLeft = ObjectAnimator.ofFloat(confirmEditText, "translationX", -20);
                ObjectAnimator shakeRight = ObjectAnimator.ofFloat(confirmEditText, "translationX", 20);
                ObjectAnimator shakeCenter = ObjectAnimator.ofFloat(confirmEditText, "translationX", 0);
                errorShake.play(shakeLeft).before(shakeRight);
                errorShake.play(shakeCenter).after(shakeRight);
                errorShake.setDuration(50);

                AnimatorSet passShake = new AnimatorSet();
                passShake.playSequentially(errorShake.clone(), errorShake.clone());

                if (password.equals(confirmPass)){
                    signup(username, password);
                } else {
                    passShake.start();
                    confirmEditText.setTextColor(Color.parseColor("#ff9393"));
                    confirmEditText.setBackgroundColor(Color.parseColor("#ffdede"));
                    confirmEditText.setTextColor(Color.parseColor("#ff9393"));
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);  // go to MainActivity
            }
        });
    }

    private void signup(String username, String password){
        JSONObject signupInfo = new JSONObject();
        try {
            signupInfo.put("username", username);
            signupInfo.put("password", password);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest signupRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL.USERS,
                signupInfo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                        UserInfo.username = username;
                        UserInfo.password = password;
                        UserInfo.getUserInfo(getApplicationContext());
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Username taken", Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(signupRequest);
    }
}
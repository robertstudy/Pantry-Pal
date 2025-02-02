package com.example.pantrypal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewRecipeActivity extends AppCompatActivity {

    private FloatingActionButton returnButton;
    private ScrollView scrollView;
    private TextView rname;
    private TextView ingredients;
    private TextView yield;
    private TextView preptime;
    private TextView totaltime;
    private TextView calserv;
    private ImageView imageView;
    private RatingBar avg_rating;
    private TextView reviews_title;
    private TextView rating_val;
    private TextView ratings_num;
    private TextView write_review_btn;
    private LinearLayout write_review_layout;
    private EditText edit_review;
    private RatingBar review_rating;
    private boolean reviewWriteOpen = false;
    private Button postReviewBtn;
    private Button viewStepsBtn;
    private ArrayList<Review> reviewList = new ArrayList<>();
    private TextView reviewNum;
    private ReviewRVAdapter reviewRVAdapter;
    private RecyclerView reviewRV;
    private int recipeId;
    private static String IMAGE_URL = "http://coms-3090-007.class.las.iastate.edu:8080/images/recipe/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);
        EdgeToEdge.enable(this);

        returnButton = findViewById(R.id.return_button);
        scrollView = findViewById(R.id.view_recipe_scroll);
        rname = findViewById(R.id.view_rname);
        ingredients = findViewById(R.id.ingredients_text);
        yield = findViewById(R.id.yields_text);
        preptime = findViewById(R.id.prep_time_text);
        totaltime = findViewById(R.id.total_time_text);
        calserv = findViewById(R.id.cal_text);
        imageView = findViewById(R.id.create_recipe_image);
        reviewNum = findViewById(R.id.reviews_num_text);
        avg_rating = findViewById(R.id.recipe_avg_rating);
        rating_val = findViewById(R.id.rating_val);
        ratings_num = findViewById(R.id.ratings_num);
        ratings_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRatings();
            }
        });
        write_review_btn = findViewById(R.id.write_review_btn);
        write_review_btn.setText("Write a review");
        write_review_layout = findViewById(R.id.write_review_layout);
        write_review_layout.setVisibility(View.GONE);
        postReviewBtn = findViewById(R.id.post_review_button);
        viewStepsBtn = findViewById(R.id.button);
        edit_review = findViewById(R.id.edit_review_text);
        review_rating = findViewById(R.id.review_rating);

        reviews_title = findViewById(R.id.reviews_title);

        reviewRV = findViewById(R.id.review_view);

        reviewRVAdapter = new ReviewRVAdapter(getApplicationContext(), reviewList);
        reviewRV.setAdapter(reviewRVAdapter);
        reviewRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Intent intent = getIntent();
        recipeId = intent.getIntExtra("rid", 1);

        getRecipeInfo(recipeId);
        makeImageRequest(recipeId);
        getReviews(recipeId);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewRecipeActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        viewStepsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewRecipeActivity.this, RecipeStepListView.class);
                intent.putExtra("rid", recipeId);
                startActivity(intent);
            }
        });

        write_review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!reviewWriteOpen){
                    write_review_layout.setVisibility(View.VISIBLE);
                    write_review_btn.setText("Cancel");
                    reviewWriteOpen = true;
                } else {
                    write_review_layout.setVisibility(View.GONE);
                    write_review_btn.setText("Write a review");
                    reviewWriteOpen = false;
                }
            }
        });
        postReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    postReview();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void postReview() throws JSONException {
        JSONObject review = new JSONObject();
        review.put("rateid", 0);
        review.put("uid", UserInfo.uid);
        review.put("rid", recipeId);
        review.put("starsRating", review_rating.getRating());
        review.put("comment", edit_review.getText().toString());
        review.put("username", UserInfo.username);
        StringRequest addReviewRequest = new StringRequest(
                Request.Method.POST,
                URL.RATINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getReviews(recipeId);
                        write_review_layout.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Failed to post review", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public byte[] getBody() {
                return review.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(addReviewRequest);
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
                            rname.setText(response.getString("rname"));
                            if (response.getInt("uid") == UserInfo.uid){
                                write_review_btn.setVisibility(View.GONE);
                            }
                            double avgRating = 0;
                            if (!response.isNull("averageRating")){
                                avgRating = response.getDouble("averageRating");
                            }
                            avg_rating.setRating((float)avgRating);
                            rating_val.setText(String.valueOf(avgRating));
                            parseIngredients(response.getJSONArray("amountsList"),
                                    response.getJSONArray("ingredientList"));
                            parseRecipeInfo(response.getInt("servings"),
                                    response.getInt("prepTime"),
                                    response.getInt("totalTime"),
                                    response.getInt("caloriesPerServing"));
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

    private void goToRatings(){
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, reviews_title.getBottom() + 2000);
            }
        });
    }

    private void getReviews(int rid){
        JsonArrayRequest reviewRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL.RATINGS.concat("/recipe/" + rid),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        reviewList.clear();
                        for (int i = 0; i < response.length(); i++){
                            try {
                                JSONObject review = response.getJSONObject(i);
                                String comment = "";
                                String username = "User";
                                if (!review.isNull("comment")){
                                    comment = review.getString("comment");
                                }
                                if (!review.isNull("username")){
                                    username = review.getString("username");
                                }
                                reviewList.add(new Review(review.getInt("rateid"),
                                        review.getInt("uid"),
                                        review.getInt("rid"),
                                        (float)review.getDouble("starsRating"),
                                        comment,
                                        username));
                                reviewRVAdapter = new ReviewRVAdapter(getApplicationContext(), reviewList);
                                reviewRV.setAdapter(reviewRVAdapter);
                                reviewRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        reviewNum.setText("(" + reviewList.size() + ")");
                        if (reviewList.size() == 1){
                            ratings_num.setText(reviewList.size() + " Rating");
                        } else {
                            ratings_num.setText(reviewList.size() + " Ratings");
                        }
                        for (Review r : reviewList){
                            if (r.getUid() == UserInfo.uid){
                                write_review_btn.setVisibility(View.GONE);
                                break;
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Failed to get review data", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(reviewRequest);
    }

    private void parseIngredients(JSONArray amts, JSONArray ingrts) throws JSONException {
        SpannableStringBuilder ispannable = new SpannableStringBuilder();

        for (int i = 0; i < ingrts.length(); i++){
            SpannableString spannableAmount = new SpannableString(amts.getString(i));
            spannableAmount.setSpan(
                    new StyleSpan(Typeface.BOLD), // Apply bold style
                    0,
                    amts.getString(i).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            ispannable.append(spannableAmount);
            ispannable.append(" " + ingrts.getString(i));
            ispannable.append("\n");
        }

        ingredients.setText(ispannable, TextView.BufferType.SPANNABLE);
    }

    private void parseRecipeInfo(int yieldNum, int prepNum, int totalNum, int calNum) throws JSONException {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        SpannableStringBuilder spanBuild = new SpannableStringBuilder();
        SpannableString span = new SpannableString("YIELDS: " + yieldNum + " serving(s)");
        // YIELD
        span.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                8 + String.valueOf(yieldNum).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        span.setSpan(
                new ForegroundColorSpan(color),
                8,
                8 + String.valueOf(yieldNum).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        spanBuild.append(span);
        yield.setText(spanBuild, TextView.BufferType.SPANNABLE);

        // PREP TIME
        spanBuild.clear();
        if (minToHrMin(prepNum)[0] == 0){
            span = new SpannableString("PREP TIME: " + prepNum + " mins");
        } else {
            span = new SpannableString("PREP TIME: " + minToHrMin(prepNum)[0] + " hrs " + minToHrMin(prepNum)[1] + " mins");
        }
        span.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                11,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        if (minToHrMin(prepNum)[0] == 0){
            span.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    11,
                    11 + String.valueOf(prepNum).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            span.setSpan(
                    new ForegroundColorSpan(color),
                    11,
                    11 + String.valueOf(prepNum).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        } else {
            span.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    11,
                    11 + String.valueOf(minToHrMin(prepNum)[0]).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            span.setSpan(
                    new ForegroundColorSpan(color),
                    11,
                    11 + String.valueOf(minToHrMin(prepNum)[0]).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            span.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    16 + String.valueOf(minToHrMin(prepNum)[0]).length(),
                    16 + String.valueOf(minToHrMin(prepNum)[0]).length() + String.valueOf(minToHrMin(prepNum)[1]).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            span.setSpan(
                    new ForegroundColorSpan(color),
                    16 + String.valueOf(minToHrMin(prepNum)[0]).length(),
                    16 + String.valueOf(minToHrMin(prepNum)[0]).length() + String.valueOf(minToHrMin(prepNum)[1]).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        spanBuild.append(span);
        preptime.setText(spanBuild, TextView.BufferType.SPANNABLE);

        // TOTAL TIME
        spanBuild.clear();
        if (minToHrMin(totalNum)[0] == 0){
            span = new SpannableString("TOTAL TIME: " + prepNum + " mins");
        } else {
            span = new SpannableString("TOTAL TIME: " + minToHrMin(totalNum)[0] + " hrs " + minToHrMin(totalNum)[1] + " mins");
        }
        span.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                12,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        if (minToHrMin(totalNum)[0] == 0){
            span.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    12,
                    12 + String.valueOf(totalNum).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            span.setSpan(
                    new ForegroundColorSpan(color),
                    12,
                    12 + String.valueOf(totalNum).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        } else {
            span.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    12,
                    12 + String.valueOf(minToHrMin(totalNum)[0]).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            span.setSpan(
                    new ForegroundColorSpan(color),
                    12,
                    12 + String.valueOf(minToHrMin(totalNum)[0]).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            span.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    17 + String.valueOf(minToHrMin(totalNum)[0]).length(),
                    17 + String.valueOf(minToHrMin(totalNum)[0]).length() + String.valueOf(minToHrMin(totalNum)[1]).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            span.setSpan(
                    new ForegroundColorSpan(color),
                    17 + String.valueOf(minToHrMin(totalNum)[0]).length(),
                    17 + String.valueOf(minToHrMin(totalNum)[0]).length() + String.valueOf(minToHrMin(totalNum)[1]).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        spanBuild.append(span);
        totaltime.setText(spanBuild, TextView.BufferType.SPANNABLE);

        // CAL/SERV
        spanBuild.clear();
        span = new SpannableString("CAL/SERV: " + calNum + " cal");
        span.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                10 + String.valueOf(calNum).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        span.setSpan(
                new ForegroundColorSpan(color),
                10,
                10 + String.valueOf(calNum).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        spanBuild.append(span);
        calserv.setText(spanBuild, TextView.BufferType.SPANNABLE);
    }

    private int[] minToHrMin(int minutes){
        return new int[]{minutes / 60, minutes % 60};
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

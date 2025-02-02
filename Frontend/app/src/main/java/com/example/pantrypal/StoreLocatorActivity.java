package com.example.pantrypal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Activity which the user can use to find nearby stores and their relevant data.
 *
 * The activity's main features are a map displaying markers on nearby stores and
 * a RecyclerView which contains cards displaying information related to the stores.
 * @author Adrian Boziloff
 */
public class StoreLocatorActivity extends AppCompatActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient fusedLocationClient;
    private SupportMapFragment mapFragment;
    private GoogleMapOptions mapOptions;
    private GoogleMap map;
    private RecyclerView storeRV;
    private StoreRVAdapter storeRVAdapter;
    private ArrayList<Store> storeList = new ArrayList<>();
    private Context context;
    private FloatingActionButton returnButton;

    // request code for requesting permissions
    // not sure what this means so i'll leave it at 1
    private int PERMISSION_ID = 1;

    /**
     * Called prior to the creation of the fragment's view. Initialize the activity's
     * views and their respective properties.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_locator);
        EdgeToEdge.enable(this);
        context = this;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapOptions = new GoogleMapOptions();
        mapOptions.zoomControlsEnabled(true);

        mapFragment = SupportMapFragment.newInstance(mapOptions);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

        storeRV = findViewById(R.id.store_view);

        storeRVAdapter = new StoreRVAdapter(this, storeList);
        storeRV.setAdapter(storeRVAdapter);
        storeRV.setLayoutManager(new LinearLayoutManager(this));

        returnButton = findViewById(R.id.exit_stores_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreLocatorActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Called when the Google Maps fragment is created. Use to initialize
     * the map properties as well make location and store data requests.
     *
     * @param googleMap     the map object that can be configured
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setBuildingsEnabled(true);
        getLastLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                // Permission denied, handle gracefully
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Send a PUT request to the user database to update the user's location
     * data, given a latitude and longitude.
     *
     * @param latitude      user latitude
     * @param longitude     user longitude
     */
    void updateUserLocation(double latitude, double longitude){
        JSONObject userInfo = new JSONObject();
        try {
            // temporary. eventually will take actual user's info
            userInfo.put("username", UserInfo.username);
            userInfo.put("password", UserInfo.password);
            JSONArray geolocation = new JSONArray();
            geolocation.put(latitude);
            geolocation.put(longitude);
            userInfo.put("geoLocation", geolocation);
            JSONArray favoriteRecipes = new JSONArray();
            userInfo.put("favoriteRecipes", favoriteRecipes);
            userInfo.put("isActive", true);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        StringRequest locationUpdateRequest = new StringRequest(
                Request.Method.PUT,
                URL.USERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getStores();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { }
                }
        ){
            @Override
            public byte[] getBody() {
                return userInfo.toString().getBytes(); // Send your recipeInfo JSON object as the request body
            }

            @Override
            public String getBodyContentType() {
                return "application/json";  // Specify the content type if your API requires it
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(locationUpdateRequest);
    }

    /**
     * Use backend store location method to request nearby stores and their
     * respective data. Updates the map with new markers and the RecyclerView
     * with updated stores upon a successful response.
     */
    void getStores(){
        JsonArrayRequest recipeRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL.USERS + "/locate/" + UserInfo.uid,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject store = response.getJSONObject(i);
                                LatLng location = new LatLng(Double.parseDouble(store.getString("latitude")), Double.parseDouble(store.getString("longitude")));
                                boundsBuilder.include(location);
                                map.addMarker(new MarkerOptions()
                                        .position(location)
                                        .title(store.getString("name")));
                                storeList.add(new Store(store.getString("name"),
                                        store.getString("address"),
                                        store.getString("rating"),
                                        store.getString("distance"),
                                        store.getString("status"),
                                        store.getString("directionsLink")));
                                storeRVAdapter = new StoreRVAdapter(context, storeList);
                                storeRV.setAdapter(storeRVAdapter);
                                storeRV.setLayoutManager(new LinearLayoutManager(context));
                            }
                            LatLngBounds cameraBounds = boundsBuilder.build();
                            map.animateCamera(CameraUpdateFactory.newLatLngBounds(cameraBounds, 100));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Data error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Failed to get stores", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(recipeRequest);
    }

    /**
     * Get the user's last known location and update it in the database. Will check
     * if location permissions are active and if user location is enabled prior to
     * attempting a location check. If not, will request permissions.
     */
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                // get last location from fused location client
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    updateUserLocation(location.getLatitude(), location.getLongitude());
                                    LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                                    /*
                                    map.addMarker(new MarkerOptions()
                                            .position(latlng)
                                            .title("Current Location"));
                                     */
                                }
                            }
                        });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available, request for permissions
            requestPermissions();
        }
    }

    /**
     * Check the user's coarse and fine location permissions to see if they are enabled
     * @return whether or not coarse and fine location permissions are enabled
     */
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location on Android 10.0 and higher, use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Request coarse and fine location permissions from user
     */
    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID);
    }

    /**
     * Check if the user has their location services enabled on their device
     * @return whether or not user location is enabled
     */
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}

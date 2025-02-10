package entities.Users;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private final String success = "{\"message\":\"success\"}";
    private final String failure = "{\"message\":\"failure\"}";

    @Operation(summary = "Create a new user", description = "Creates a new user in the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data or username already exists")
    })
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) {
        if (user == null || user.getUsername() == null || userRepository.findByUsername(user.getUsername()) != null) {
            return new ResponseEntity<>(failure, HttpStatus.BAD_REQUEST);
        }
        userRepository.save(user);
        return new ResponseEntity<>(success, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all users.")
    @ApiResponse(responseCode = "200", description = "List of all users retrieved successfully")
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Operation(summary = "Get user by username", description = "Retrieves details of a specific user by username.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Get user by user ID", description = "Retrieves details of a specific user by user ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/uid/{uid}")
    public ResponseEntity<User> getUserByUid(@PathVariable int uid) {
        User user = userRepository.findByUid(uid);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Update a user", description = "Updates details of an existing user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody User user) {

        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        existingUser.setPassword(user.getPassword());
        existingUser.setGeoLocation(user.getGeoLocation());
        existingUser.setFavoriteRecipes(user.getFavoriteRecipes());
        existingUser.setIsActive(user.getIsActive());
        existingUser.setRecipesPosted(user.getRecipesPosted());
        userRepository.save(existingUser);

        return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
    }

    @Operation(summary = "Reset user password", description = "Resets a user's password.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PatchMapping("/resetpassword")
    public ResponseEntity<String> resetUserPassword(@RequestBody Map<String, Object> requestBody) {

        User user = new ObjectMapper().convertValue(requestBody.get("user"), User.class);
        String newPassword = (String) requestBody.get("newPassword");
        User existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        if (!existingUser.getPassword().equals(user.getPassword())) {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        existingUser.setPassword(newPassword);
        userRepository.save(existingUser);

        return new ResponseEntity<>("User password updated successfully", HttpStatus.OK);
    }

    @Operation(summary = "Forgot password", description = "Allows a user to reset their password by providing the username and a new password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request format")
    })
    @PatchMapping("/forgotpassword")
    public ResponseEntity<String> forgotUserPassword(@RequestBody Map<String, Object> requestBody) {

        User user = new ObjectMapper().convertValue(requestBody.get("user"), User.class);
        String newPassword = (String) requestBody.get("newPassword");
        User existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        existingUser.setPassword(newPassword);

        // Save the updated user in the repository
        userRepository.save(existingUser);

        return new ResponseEntity<>("User password updated successfully", HttpStatus.OK);
    }

    @Operation(summary = "Delete a user by username", description = "Deletes a user from the database based on their username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping
    public ResponseEntity<String> deleteUserByUsername(@RequestBody User user) {

        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        if (!existingUser.getPassword().equals(user.getPassword())) {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        userRepository.delete(existingUser);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }

    @Operation(summary = "Delete a user by UID", description = "Deletes a user from the database based on their UID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{uid}")
    public ResponseEntity<String> deleteUserByUid(@PathVariable int uid) {
        User existingUser = userRepository.findByUid(uid);
        if (existingUser == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        userRepository.delete(existingUser);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }

    @Operation(summary = "Find local stores for a user",
            description = "Finds nearby grocery stores or supermarkets based on the user's geolocation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of nearby stores provided"),
            @ApiResponse(responseCode = "400", description = "Bad request (e.g., invalid geolocation)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/locate/{uid}")
    public ResponseEntity<List<Map<String, String>>> getLocalStoresWithLinks(@PathVariable int uid) {

        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("API_KEY");

        User user = userRepository.findByUid(uid);
        if (user == null || user.getGeoLocation() == null || user.getGeoLocation().size() < 2) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        double latitude = user.getGeoLocation().get(0);
        double longitude = user.getGeoLocation().get(1);

        String baseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
        String uri = String.format("%s?location=%f,%f&radius=5000&type=grocery_or_supermarket&key=%s",
                baseUrl, latitude, longitude, apiKey);

        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                ObjectMapper mapper = new ObjectMapper();
                JsonNode responseBody = mapper.readTree(response.toString());
                List<Map<String, String>> storesWithLinks = new ArrayList<>();

                responseBody.get("results").forEach(result -> {
                    String storeName = result.get("name").asText();
                    String address = result.has("vicinity") ? result.get("vicinity").asText() : "Address not available";
                    double storeLat = result.get("geometry").get("location").get("lat").asDouble();
                    double storeLng = result.get("geometry").get("location").get("lng").asDouble();
                    String rating = result.has("rating") ? result.get("rating").asText() : "No rating";
                    String userRatingsTotal = result.has("user_ratings_total") ? result.get("user_ratings_total").asText() : "0";

                    double distance = calculateHaversineDistance(latitude, longitude, storeLat, storeLng);

                    String openStatus = "Unknown";
                    if (result.has("opening_hours") && result.get("opening_hours").has("open_now")) {
                        openStatus = result.get("opening_hours").get("open_now").asBoolean() ? "Open" : "Closed";
                    }

                    String directionsUrl = String.format("https://www.google.com/maps/dir/?api=1&destination=%f,%f", storeLat, storeLng);

                    Map<String, String> storeData = new HashMap<>();
                    storeData.put("name", storeName);
                    storeData.put("address", address);
                    storeData.put("latitude", String.valueOf(storeLat));
                    storeData.put("longitude", String.valueOf(storeLng));
                    storeData.put("rating", rating);
                    storeData.put("userRatingsTotal", userRatingsTotal);
                    storeData.put("distance", String.format("%.2f miles", distance));
                    storeData.put("status", openStatus); 
                    storeData.put("directionsLink", directionsUrl);

                    storesWithLinks.add(storeData);
                });

                return ResponseEntity.ok(storesWithLinks);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private double calculateHaversineDistance(double lat1, double lng1, double lat2, double lng2) {
        final int EARTH_RADIUS = 3959;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
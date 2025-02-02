package entities.Recipes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Ingredients.Ingredient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RecipeService {

    public static void addOpenFoodFactsData(Ingredient ingredient) {
        String formattedName = ingredient.getIname().replaceAll(" ", "-").toLowerCase();
        ingredient.setOffid(formattedName);
        String url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=" + formattedName + "&search_simple=1&json=1";

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode responseBody = mapper.readTree(response.toString());

                    // Check if we received any products
                    if (responseBody.has("products") && responseBody.get("products").isArray()) {
                        JsonNode products = responseBody.get("products");

                        // Initialize accumulators and counters
                        double totalEnergy = 0.0, totalFat = 0.0, totalSaturatedFat = 0.0, totalCarbs = 0.0, totalSugars = 0.0;
                        double totalProtein = 0.0, totalSalt = 0.0, totalFiber = 0.0;
                        int productCount = 0;

                        for (JsonNode product : products) {
                            JsonNode nutriments = product.path("nutriments");

                            // Only process products with available nutriments
                            if (nutriments != null && nutriments.isObject()) {
                                totalEnergy += getNutrientValue(nutriments, "energy_100g");
                                totalFat += getNutrientValue(nutriments, "fat_100g");
                                totalSaturatedFat += getNutrientValue(nutriments, "saturated-fat_100g");
                                totalCarbs += getNutrientValue(nutriments, "carbohydrates_100g");
                                totalSugars += getNutrientValue(nutriments, "sugars_100g");
                                totalProtein += getNutrientValue(nutriments, "proteins_100g");
                                totalSalt += getNutrientValue(nutriments, "salt_100g");
                                totalFiber += getNutrientValue(nutriments, "fiber_100g");

                                productCount++;
                            }
                        }

                        if (productCount > 0) {
                            // Set averaged values with rounding to two decimal places
                            ingredient.setEnergyPer100g(roundToTwoDecimalPlaces(totalEnergy / productCount));
                            ingredient.setFatPer100g(roundToTwoDecimalPlaces(totalFat / productCount));
                            ingredient.setSaturatedFatPer100g(roundToTwoDecimalPlaces(totalSaturatedFat / productCount));
                            ingredient.setCarbohydratesPer100g(roundToTwoDecimalPlaces(totalCarbs / productCount));
                            ingredient.setSugarsPer100g(roundToTwoDecimalPlaces(totalSugars / productCount));
                            ingredient.setProteinPer100g(roundToTwoDecimalPlaces(totalProtein / productCount));
                            ingredient.setSaltPer100g(roundToTwoDecimalPlaces(totalSalt / productCount));
                            ingredient.setFiberPer100g(roundToTwoDecimalPlaces(totalFiber / productCount));

                            System.out.println("Average nutritional data successfully added for " + ingredient.getIname());
                        } else {
                            System.out.println("No nutritional data available for this product.");
                        }
                    } else {
                        System.out.println("No products found for the specified ingredient.");
                    }
                }
            } else {
                System.out.println("Failed to connect to Open Food Facts API. Response Code: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            System.out.println("Error occurred while fetching data from Open Food Facts: " + e.getMessage());
        }
    }

    // Helper method for rounding to 2 decimal places
    private static double roundToTwoDecimalPlaces(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // Helper method for nutrient extraction
    private static double getNutrientValue(JsonNode nutriments, String nutrientName) {
        JsonNode nutrientNode = nutriments.get(nutrientName);
        return (nutrientNode != null && nutrientNode.isNumber()) ? nutrientNode.asDouble() : 0.0;
    }
}
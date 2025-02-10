package entities;
import entities.Users.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import junit.framework.Assert;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort; // SBv3

import java.io.File;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class SystemTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.registerParser("text/plain", Parser.JSON);
    }

    @Test
    public void testGetUsers() {
        given()
                .when()
                .get("/users")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", greaterThan(0));
    }

    @Test
    public void testGetBobUsers() {

        int userId = 1;

        given()
                .when()
                .get("/users/uid/" + userId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("username", equalTo("Bob"));
    }

    @Test
    public void testPostUsers() throws JSONException {

        JSONObject newUser = new JSONObject();
        newUser.put("username", "newUser1234");
        newUser.put("password", "123456789");

        Response response = given()
                .contentType("application/json")
                .body(newUser.toString())
                .when()
                .post("/users");

        response.then()
                .statusCode(HttpStatus.CREATED.value()); // HTTP 201 Created
    }

    @Test
    public void testUpdateUsers() throws JSONException {
        int userId = 1;

        JSONObject updatedUser = new JSONObject();
        updatedUser.put("username", "Bob");
        updatedUser.put("password", "ChangedPassword");

        given()
                .contentType("application/json")
                .body(updatedUser.toString())
                .when()
                .put("/users")
                .then()
                .statusCode(HttpStatus.OK.value());

        given()
                .when()
                .get("/users/uid/" + userId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("password", equalTo("ChangedPassword"));
    }

    @Test
    public void testUpdateUsersPasswordReset() throws JSONException {

        JSONObject updatedUser = new JSONObject();
        updatedUser.put("username", "ResetPasswordUser");
        updatedUser.put("password", "123456789");

        given()
                .contentType("application/json")
                .body(updatedUser.toString())
                .when()
                .post("/users")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        JSONObject user = new JSONObject();
        user.put("username", "ResetPasswordUser");
        user.put("password", "123456789");

        JSONObject requestData = new JSONObject();
        requestData.put("user", user);
        requestData.put("newPassword", "changed12345");

        given()
                .contentType("application/json")
                .body(requestData.toString())
                .when()
                .patch("/users/resetpassword")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testUpdateUsersPasswordForgot() throws JSONException {

        JSONObject updatedUser = new JSONObject();
         updatedUser.put("username", "ForgotPasswordUser");
         updatedUser.put("password", "123456789");

         given()
         .contentType("application/json")
         .body(updatedUser.toString())
         .when()
         .post("/users")
         .then()
         .statusCode(HttpStatus.CREATED.value());

        JSONObject user = new JSONObject();
        user.put("username", "ForgotPasswordUser");

        JSONObject requestData = new JSONObject();
        requestData.put("user", user);
        requestData.put("newPassword", "changed12345");

        given()
                .contentType("application/json")
                .body(requestData.toString())
                .when()
                .patch("/users/forgotpassword")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testDeleteUsersByUid() throws JSONException {
        JSONObject newUser = new JSONObject();
        newUser.put("username", "userToDelete");
        newUser.put("password", "123456789");

        Response createResponse = given()
                .contentType("application/json")
                .body(newUser.toString())
                .when()
                .post("/users");

        createResponse.then()
                .statusCode(HttpStatus.CREATED.value()); 

        Response response = given()
                .when()
                .get("/users/" + "userToDelete");

        response.then()
                .statusCode(HttpStatus.OK.value()); // HTTP 200 OK

        int userId = response.jsonPath().getInt("uid");

        Response deleteResponse = given()
                .when()
                .delete("/users/" + userId);

        deleteResponse.then()
                .statusCode(HttpStatus.OK.value());

        Response fetchResponse = given()
                .when()
                .get("/users/" + userId);

        fetchResponse.then()
                .statusCode(HttpStatus.NOT_FOUND.value()); // HTTP 404 Not Found
    }

    @Test
    public void testDeleteUsersByUserName() throws JSONException {
        JSONObject newUser = new JSONObject();
        newUser.put("username", "userToDelete");
        newUser.put("password", "123456789");

        Response createResponse1 = given()
                .contentType("application/json")
                .body(newUser.toString())
                .when()
                .post("/users");

        createResponse1.then()
                .statusCode(HttpStatus.CREATED.value()); // HTTP 201 Created

        JSONObject deleteUser = new JSONObject();
        newUser.put("username", "userToDelete");

        Response deleteResponse = given()
                .contentType("application/json")
                .body(newUser.toString())
                .when()
                .delete("/users");

        deleteResponse.then()
                .statusCode(HttpStatus.OK.value());

        Response fetchResponse = given()
                .when()
                .get("/users/" + "userToDelete");

        fetchResponse.then()
                .statusCode(HttpStatus.NOT_FOUND.value()); // HTTP 404 Not Found
    }

    @Test
    public void testGetAllRecipes() {
        given()
                .when()
                .get("/recipes")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", greaterThan(0)); 
    }

    @Test
    public void testGetRecipeByRid() {
        int recipeId = 1;

        given()
                .when()
                .get("/recipes/" + recipeId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("rname", equalTo("Updated Hot Dogs"));
    }

    @Test
    public void testCreateRecipe() throws JSONException {
        JSONObject newRecipe = new JSONObject();
        newRecipe.put("rname", "Test Recipe");
        newRecipe.put("servings", 4);

        given()
                .contentType("application/json")
                .body(newRecipe.toString())
                .when()
                .post("/recipes")
                .then()
                .statusCode(HttpStatus.CREATED.value()); 
    }

    @Test
    public void testUpdateRecipe() throws JSONException {
        JSONObject updatedRecipe = new JSONObject();
        updatedRecipe.put("rid", 1);
        updatedRecipe.put("rname", "Updated Hot Dogs");
        updatedRecipe.put("servings", 6);

        given()
                .contentType("application/json")
                .body(updatedRecipe.toString())
                .when()
                .put("/recipes")
                .then()
                .statusCode(HttpStatus.OK.value()); 
    }

    @Test
    public void testDeleteRecipe() throws JSONException {

        int rid = 2;

        Response deleteResponse = given()
                .when()
                .delete("/recipes/" + rid);

        deleteResponse.then()
                .statusCode(HttpStatus.OK.value());

        Response fetchResponse = given()
                .when()
                .get("/recipes/" + rid);

        fetchResponse.then()
                .statusCode(HttpStatus.NOT_FOUND.value()); // HTTP 404 Not Found
    }

    @Test
    public void testGetIngredients() {
        given()
                .when()
                .get("/ingredients")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", greaterThan(0)); 
    }

    @Test
    public void testPostIngredient() throws JSONException {
        JSONObject newIngredient = new JSONObject();
        newIngredient.put("iname", "spinach");

        Response response = given()
                .contentType("application/json")
                .body(newIngredient.toString())
                .when()
                .post("/ingredients");

        response.then()
                .statusCode(HttpStatus.CREATED.value()); // HTTP 201 Created
    }

    @Test
    public void testPartialUpdateIngredient() throws JSONException {
        JSONObject partialUpdate = new JSONObject();
        partialUpdate.put("iname", "spinach");
        partialUpdate.put("energyPer100g", "46");

        given()
                .contentType("application/json")
                .body(partialUpdate.toString())
                .when()
                .patch("/ingredients")
                .then()
                .statusCode(HttpStatus.OK.value()); // HTTP 200 OK
    }

    @Test
    public void testCreateIngredientWithInvalidData() throws JSONException {
        JSONObject newIngredient = new JSONObject();
        newIngredient.put("iname", "");  

        Response response = given()
                .contentType("application/json")
                .body(newIngredient.toString())
                .when()
                .post("/ingredients");

        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value());  
    }

    @Test
    @Order(1)
    public void testPostFavorite() throws JSONException {
        JSONObject newFavorite = new JSONObject();
        newFavorite.put("uid", 1);
        newFavorite.put("rid", 1);

        Response response = given()
                .contentType("application/json")
                .body(newFavorite.toString())
                .when()
                .post("/favorites");

        response.then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testPostFavoriteWrong() throws JSONException {

        JSONObject newFavorite = new JSONObject();
        newFavorite.put("uid", 1);
        newFavorite.put("rid", 2);

        Response response1 = given()
                .contentType("application/json")
                .body(newFavorite.toString())
                .when()
                .post("/favorites");

        Response response2 = given()
                .contentType("application/json")
                .body(newFavorite.toString())
                .when()
                .post("/favorites");

        response2.then()
                .statusCode(HttpStatus.CONFLICT.value()); // HTTP 409 Conflict
    }

    @Test
    public void testUploadRecipeImage() throws Exception {
        
        File imageFile = new File("XXXXXXXXXX");

        
        Assert.assertTrue("File does not exist at the specified path", imageFile.exists());

        given()
                .multiPart("image", imageFile)
                .contentType(ContentType.MULTIPART)
                .when()
                .post("/images/recipe/1") 
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testUpdateRecipeImage() throws Exception {
        File imageFile = new File("XXXXXXXXXX");

        Assert.assertTrue("File does not exist at the specified path", imageFile.exists());

        given()
                .multiPart("image", imageFile)  
                .contentType(ContentType.MULTIPART)
                .when()
                .post("/images/recipe/2")  
                .then()
                .statusCode(HttpStatus.OK.value());

        given()
                .multiPart("image", imageFile) 
                .contentType(ContentType.MULTIPART) 
                .when()
                .patch("/images/recipe/2")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testUploadIngredientImage() throws Exception {
        
        File imageFile = new File("XXXXXXXXXX");

        Assert.assertTrue("File does not exist at the specified path", imageFile.exists());

        given()
                .multiPart("image", imageFile)  
                .contentType(ContentType.MULTIPART)
                .when()
                .post("/images/ingredient/1")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testUpdateIngredientImage() throws Exception {
        
        File imageFile = new File("XXXXXXXXXX");

        Assert.assertTrue("", imageFile.exists());

        given()
                .multiPart("image", imageFile)  
                .contentType(ContentType.MULTIPART)  
                .when()
                .post("/images/ingredient/2")  
                .then()
                .statusCode(HttpStatus.OK.value());

        given()
                .multiPart("image", imageFile)  
                .contentType(ContentType.MULTIPART)  
                .when()
                .patch("/images/ingredient/2")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

}
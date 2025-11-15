package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestCase{

    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();


    @BeforeEach
    public void loginUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseLogin = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData
        );

        this.cookie = this.getCookie(responseLogin, "auth_sid");
        this.header = this.getHeader(responseLogin,"x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseLogin,"user_id");
    }

    @Test
    @Description("Try to delete protected user with ID 2")
    @DisplayName("Test delete protected user")
    public void testDeleteUserId2() {
        Response response = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userIdOnAuth,
                this.header,
                this.cookie

        );
        //System.out.println(response.asString());
        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.asserJsonByName(response, "error", "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

    }

    @Test
    @Description("Create user, authorize, delete and verify deletion")
    @DisplayName("Test positive user deletion")
    public void testDeleteJustCreatedUser() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response createResponse = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        String userId = createResponse.jsonPath().getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response loginResponse = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData
        );

        String header = this.getHeader(loginResponse, "x-csrf-token");
        String cookie = this.getCookie(loginResponse, "auth_sid");

        Response responseDelete = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie
        );
        //System.out.println(responseDelete.statusCode());
        //System.out.println(responseDelete.asString());
        Assertions.assertResponseCodeEquals(responseDelete, 200);

        Response getUserResponse = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie
        );

        System.out.println(getUserResponse.asString());
        Assertions.assertResponseCodeEquals(getUserResponse, 404);
        Assertions.assertResponseTextEquals(getUserResponse, "User not found");

    }

    @Test
    @Description("Try to delete user being authorized as another user")
    @DisplayName("Test delete user as other user")
    public void testDeleteUserAsOtherUser() {
        Map<String, String> user1Data = DataGenerator.getRegistrationData();
        Response createUser1Response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                user1Data
        );

        String user1Id = createUser1Response.jsonPath().getString("id");

        Map<String, String> user2Data = DataGenerator.getRegistrationData();
        Response createUser2Response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                user2Data
        );

        Map<String, String> authData = new HashMap<>();
        authData.put("email", user2Data.get("email"));
        authData.put("password", user2Data.get("password"));

        Response loginResponse = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData
        );

        String header = this.getHeader(loginResponse, "x-csrf-token");
        String cookie = this.getCookie(loginResponse, "auth_sid");

        Response deleteResponse = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + user1Id,
                header,
                cookie
        );

        //System.out.println(deleteResponse.asString());
        Assertions.assertResponseCodeEquals(deleteResponse, 400);
        Assertions.asserJsonByName(deleteResponse, "error", "This user can only delete their own account.");

    }



}

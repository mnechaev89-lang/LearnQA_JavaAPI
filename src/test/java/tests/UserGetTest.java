package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import lib.Assertions;
import lib.ApiCoreRequests;

import java.util.HashMap;
import java.util.Map;

@Epic("User data cases")
@Feature("User Data Retrieval")

public class UserGetTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testGetUserDataNotAuth() { // неавторизованный пользователь (только username)
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        Assertions.assertJsonHasField(responseUserData, "username"); // проверем что есть username
        Assertions.assertJsonHasNotField(responseUserData, "firstName"); // проверяем что нет firstName, lastName и email
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Test
    public void testGetUserDetailsAuthAsSameUser() { // С авторизацией: Все поля (username, firstName, lastName, email)
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com"); // готовим данные для авторизации
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured // авторизовываемся под пользователем vinkotov@example.com
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
        //System.out.println(responseGetAuth.asString());

        String header = this.getHeader(responseGetAuth, "x-csrf-token"); // получаем header и cookie
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = RestAssured // Получение данных с авторизацией
                .given()
                .header("x-csrf-token", header) // передаем токен
                .cookie("auth_sid", cookie) // передаем куки
                .get("https://playground.learnqa.ru/api/user/2") // запрашиваем данные своего пользователя (так как id нашего пользователя - 2)
                .andReturn();

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields); // проверяем, что авторизованный пользователь получает все данные
    }

    @Test
    @Description("This test verifies authorization with another id user")
    @DisplayName("Authorization test with other id")
    public void testGetUserDetailsAuthAsOtherUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData
                );
        String header = this.getHeader(responseGetAuth, "x-csrf-token"); // получаем header и cookie
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/1",  // ← Другой ID (не 2)
                header,
                cookie
        );
        //System.out.println(responseUserData.asString());
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");


    }



}

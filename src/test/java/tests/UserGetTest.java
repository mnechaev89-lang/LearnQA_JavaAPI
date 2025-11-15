package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;
import lib.Assertions;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {

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

}

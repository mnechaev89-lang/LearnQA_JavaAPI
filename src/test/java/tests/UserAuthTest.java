package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.HashMap;
import java.util.Map;
import lib.Assertions;
import lib.ApiCoreRequests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;

@Epic("Authorisation cases") // означает, что последующие тесты принадлежат большой общей части "Authorisation cases"
@Feature("Authorization") // название фичи
public class UserAuthTest extends BaseTestCase {

    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();


    @BeforeEach //этот метод выполняется ПЕРЕД КАЖДЫМ тестом в классе
    public void loginUser(){
        Map<String, String> authData = new HashMap<>();
        //Логинимся под пользователем vinkotov@example.com с паролем 1234
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);


        //Сохраняем данные авторизации для использования в тестах
        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth,"x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");
    }


    @Test // Проверить, что при правильной авторизации система возвращает правильный user_id
    @Description("This test successfully authorize user by email and password") // описание теста. показывает в отчете, что именно тест проверяет
    @DisplayName("Test positive auth user") //название теста в отчете
    public void testAuthUser(){

        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/auth",
                        this.header,
                        this.cookie
                );

        Assertions.asserJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);//Вызоваем метод asserJsonByName и проверь, что в ответе responseCheckAuth есть поле 'user_id' и оно равно значению this.userIdOnAuth"
    }

    @Description("This test checks authorization status w/o sending auth cookie or token")
    @DisplayName("Test negative auth user")
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAythUser(String condition){ // Проверяем, что при НЕПОЛНОЙ авторизации система не узнает пользователя
        if(condition.equals("cookie")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie(
                    "https://playground.learnqa.ru/api/user/auth",
                    this.cookie
            );
            Assertions.asserJsonByName(responseForCheck, "user_id", 0);
        } else if (condition.equals("headers")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithToken(
                    "https://playground.learnqa.ru/api/user/auth",
                    this.header
            );
            Assertions.asserJsonByName(responseForCheck, "user_id", 0);
        } else {
            throw new IllegalArgumentException("Condition value is not known" + condition);
        }
    }
}
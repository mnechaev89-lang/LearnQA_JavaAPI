package tests;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import lib.ApiCoreRequests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Authorisation cases")
@Feature("Authorization")

public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test // Пытаемся зарегистрировать пользователя с уже существующим email
    public void testCreateUserWithExistingEmail(){ //Проверияем, что система правильно реагирует на попытку регистрации с уже существующим email
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData); //заполняет остальные поля стандартными значениями, но email оставляет нашим

        Response responseCreateAuth = RestAssured //Сохраняем ответ сервера в переменную responseCreateAuth
                .given()
                .body(userData) //Передаем наши данные (с существующим email)
                .post("https://playground.learnqa.ru/api/user/")// Отправляем POST-запрос на регистрацию пользователя
                .andReturn();

        // Ожидаем ошибку 400 и определенный текст
        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test //Проверить, что регистрация нового пользователя работает корректно
    public void testCreateUserSuccessfully(){
        // Регистрируем нового пользователя
        String email = DataGenerator.getRandomEmail(); //создает УНИКАЛЬНЫЙ email на основе времени
        // подготовка данных (еще не отправка)
        Map<String, String> userData = DataGenerator.getRegistrationData(); //создает полный набор данных для регистрации
        // отправка на сервер
        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        // Ожидаем успех (200) и что в ответе есть поле "id"
        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);// responseCreateAuth - это уже ОТВЕТ от сервера
        Assertions.assertJsonHasField(responseCreateAuth, "id"); // проверяем, что в ответе есть поле "id" - сервер создал пользователя и присвоил ему ID
    }


    @Test
    @Description("This test tries to register a user without symbol '@' on email")
    @DisplayName("Test registration without symbol '@'")

    public void testCreateUserWithInvalidEmail() {
        String invalidEmail = "learnqablablabla.com";
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("email", invalidEmail);

        Response response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );
        //System.out.println(response.asString());
        //System.out.println(response.statusCode());

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(
                response,
                "Invalid email format"

        );

    }

    @Description("This test without one fields for registration")
    @DisplayName("Test without one fields for registration")
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})

    public void testCreateUserWithoutField(String fieldName) {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.remove(fieldName);

        Response response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );
        //System.out.println(response.statusCode());
        //System.out.println(response.asString());
        System.out.println("Отсутствуцюший параметр: " + fieldName);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The following required params are missed: " + fieldName);
    }


    @Test
    @Description("This test tries to register a user without one symbol on firstName")
    @DisplayName("Test registration without one symbol on firstName")

    public void testCreateUserWithShortName() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("firstName", "x");

        Response response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );
        //System.out.println(response.asString());
        //System.out.println(response.statusCode());
        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too short");
    }

    @Test
    @Description("This test tries to register a user without 250 symbol on firstName")
    @DisplayName("Test registration without 250 symbol on firstName")

    public void testCreateUserWithLongName() {
        String longName = "aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeeeffffffffffgggggggggghhhhhhhhhhiiiiiiiiiijjjjjjjjjjkkkkkkkkkkllllllllllmmmmmmmmmmnnnnnnnnnnooooooooooppppppppppqqqqqqqqqqrrrrrrrrrrssssssssssttttttttttuuuuuuuuuuvvvvvvvvvvwwwwwwwwwwxxxxxxxxxxyyyyyyyyyyz";
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("firstName", longName);

        Response response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );
        //System.out.println(response.asString());
        //System.out.println(response.statusCode());

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too long");
    }

}

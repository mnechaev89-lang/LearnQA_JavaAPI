package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {
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
        Assertions.assertJsonHasKey(responseCreateAuth, "id"); // проверяем, что в ответе есть поле "id" - сервер создал пользователя и присвоил ему ID
    }


}

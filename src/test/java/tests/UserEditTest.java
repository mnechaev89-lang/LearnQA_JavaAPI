package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {
    @Test // создание пользователя
    public void testEditJustCreatedTest() {
        Map<String, String>  userData = DataGenerator.getRegistrationData(); //Создаем тестовые данные для нового пользователя (случайный email, пароль и т.д.)

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateAuth.getString("id"); //Сохраняем ID созданного пользователя из ответа сервера

        //LOGIN
        //Берем email и пароль из данных созданного пользователя
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
        //Получаем токены авторизации (cookie и header), которые нужны для последующих запросов


        //EDIT
        //Готовим новые данные - хотим изменить имя на "Changed Name"
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                //Передаем авторизацию через header и cookie
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth,"auth_sid"))
                .body(editData) //Отправляем только изменяемые поля (в данном случае только firstName)
                .put("https://playground.learnqa.ru/api/user/" + userId) //редактируем конкретного пользователя по ID
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                //Снова передаем авторизацию (также нужна для доступа к данным)
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId) //Запрашиваем обновленные данные пользователя через GET-запрос
                .andReturn();

        Assertions.asserJsonByName(responseUserData, "firstName", newName); //Проверяем, что имя действительно изменилось на "Changed Name"

    }

}

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

@Epic("User edit cases")
@Feature("User Editing")

public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

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

    @Test
    @Description("Try to edit user without authorization")
    @DisplayName("Test edit user without auth")
    public void testEditUserWithoutAuth() {
            Map<String, String> userData = DataGenerator.getRegistrationData();
            Response createResponse = apiCoreRequests.makePostRequest(
                    "https://playground.learnqa.ru/api/user/",
                    userData
            );

            String userId = createResponse.jsonPath().getString("id");

            Map<String, String> editData = new HashMap<>();
            editData.put("firstName", "New Name");
            Response editResponse = apiCoreRequests.makePutRequest(
                    "https://playground.learnqa.ru/api/user/" + userId,
                    editData
            );
            System.out.println(editResponse.asString());

            Assertions.assertResponseCodeEquals(editResponse, 400);
            Assertions.asserJsonByName(editResponse,"error", "Auth token not supplied");
        }

    @Test
    @Description("Try to edit user being authorized as another user")
    @DisplayName("Test edit user as other user")
    public void testEditUserAsOtherUser() {
        Map<String, String> user1Data = DataGenerator.getRegistrationData();
        Response createUser1 = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                user1Data
        );

        String user1Id = createUser1.jsonPath().getString("id");

        Map<String, String> userData2 = DataGenerator.getRegistrationData();
        Response createUser2Response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData2
        );

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData2.get("email"));
        authData.put("password", userData2.get("password"));

        Response loginResponse = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData
        );

        String header = this.getHeader(loginResponse, "x-csrf-token");
        String cookie = this.getCookie(loginResponse, "auth_sid");

        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "Bla Name");

        Response editResponse = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + user1Id,  // Чужой ID!
                header,
                cookie,
                editData
        );
        //System.out.println(editResponse.asString());
        Assertions.assertResponseCodeEquals(editResponse, 400);
        Assertions.asserJsonByName(editResponse, "error", "This user can only edit their own data.");
    }


    @Test
    @Description("Try to edit email to invalid format without @ symbol")
    @DisplayName("Test edit user with invalid email")
    public void testEditUserWithInvalidEmail() {
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

        Map<String, String> editData = new HashMap<>();
        editData.put("email", "incorrectFormatEmail.com");

        Response editResponse = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie,
                editData
        );
        //System.out.println(editResponse.asString());

        Assertions.assertResponseCodeEquals(editResponse, 400);
        Assertions.asserJsonByName(editResponse,"error", "Invalid email format");
    }

    @Test
    @Description("Try to edit firstName to change to one character")
    @DisplayName("Test edit user with too short firstName")
    public void testEditUserWithShortFirstName() {
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

        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "x");  // Всего 1 символ!

        Response editResponse = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie,
                editData
        );

        //System.out.println(editResponse.asString());
        Assertions.assertResponseCodeEquals(editResponse, 400);
        Assertions.asserJsonByName(editResponse,"error", "The value for field `firstName` is too short");
    }




}

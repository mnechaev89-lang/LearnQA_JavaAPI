package lib;

import groovyjarjarantlr4.v4.codegen.model.SrcOp;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
    @Step("Make a GET-request with token and auth cookie") //аннотация Allure для красивого отображения шагов в отчетах
    public Response makeGetRequest(String url, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured()) // Подключаем Allure отчетность. Без этого в отчетах не будет детальной информации о HTTP запросах
                .header(new Header("x-csrf-token", token)) // Добавляем токен в заголовки
                .cookie("auth_sid", cookie) // Добавляем куку для авторизации
                .get(url) // Отправляем GET запрос по URL
                .andReturn(); // Возвращаем ответ

    }

    @Step("Make a GET-request with auth cookie only") //Отправляет GET запрос только с кукой
    public Response makeGetRequestWithCookie(String url, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();

    }

    @Step("Make a GET-request with token only") //Отправляет GET запрос только с токеном
    public Response makeGetRequestWithToken(String url, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();

    }

    @Step("Make a POST-request") // Отправляет POST запрос с данными в формате JSON
    public Response makePostRequest(String url, Map<String, String> authData) {
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();

    }

    @Step("Make a PUT-request with token and auth cookie") // отправлем PUT запрос с токеном и куки
    public Response makePutRequest(String url, String token, String cookie, Map<String, String> editData) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .body(editData)
                .put(url)
                .andReturn();
    }

    @Step("Make a PUT-request without auth") // отправлем PUT запрос без токена и куки
    public Response makePutRequest(String url, Map<String, String> editData) {
        return given()
                .filter(new AllureRestAssured())
                .body(editData)
                .put(url)
                .andReturn();
    }
}




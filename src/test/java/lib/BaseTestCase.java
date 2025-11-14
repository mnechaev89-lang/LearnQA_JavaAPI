package lib;

import io.restassured.http.Headers;
import io.restassured.response.Response;
import java.util.Map;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTestCase {
    // Метод для получения заголовка из ответа
    protected String getHeader (Response Response, String name) {
        Headers headers = Response.getHeaders(); // Получаем все заголовки
        // Проверяем, что заголовок существует
        assertTrue(headers.hasHeaderWithName(name), "Response doesn't have header with name " + name);
        return headers.getValue(name); // Возвращаем значение заголовка
    }
    // Метод для получения cookies из ответа
    protected String getCookie (Response Response, String name) {
        Map<String, String> cookies = Response.getCookies(); // возвращает ВСЕ cookies, которые сервер отправил в ответе

        assertTrue(cookies.containsKey(name), "Response doesn't have cookie with name " + name); //Проверяем, что нужная cookie существует
        return cookies.get(name);


    }
    // Метод для получения числа из JSON
    protected int getIntFromJson(Response Response, String name) {
        Response.then().assertThat().body("$", hasKey(name));
        return Response.jsonPath().getInt(name);// получаем name как число из ответа
    }
}

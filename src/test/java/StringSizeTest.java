import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringSizeTest {

    @Test
    public void testCookieValue() {

        Response responseCookie = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();


        String cookieValue = responseCookie.getCookie("HomeWork");
        System.out.println("Куки: " + cookieValue);

        assertEquals("hw_value", cookieValue, "Cookie HomeWork должна быть 'hw_value'");


    }
}

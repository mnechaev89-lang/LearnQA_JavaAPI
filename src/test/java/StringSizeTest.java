import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringSizeTest {

    @Test
    public void testHeaderValue() {
        Map<String, String> headers = new HashMap<>();

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        //Headers responseHeader = response.getHeaders();
        //System.out.println("Headers response: " + "\n" + responseHeader);
        String headerValue = response.getHeader("x-secret-homework-header");
        System.out.println("Значение заголовка 'x-secret-homework-header': " + headerValue);

        assertEquals("Some secret value", headerValue, "Header x-secret-homework-header должна быть 'Some secret value'");


    }
}

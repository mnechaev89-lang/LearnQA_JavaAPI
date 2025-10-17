import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.Test;

public class HelloWorldTest {

    @Test
    public void testHelloWorld(){
        Response resp = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        resp.prettyPrint();

    }
}

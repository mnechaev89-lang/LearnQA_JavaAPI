import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


public class HelloWorldTest {

    @Test
    public void testRestAssured(){
        Response resp = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String redirectUrl = resp.getHeader("Location");
        System.out.println("Редиректит на URL : " + redirectUrl);

    }
}

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class HelloWorldTest {

    @Test
    public void testRestAssured(){
        String URL = "https://playground.learnqa.ru/api/long_redirect";
        Response resp = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get(URL)
                .andReturn();
        int statusCode = resp.getStatusCode();

        while(statusCode >= 300 && statusCode < 400) {
            System.out.println(URL + " со статусом " + statusCode);

            String UrlNext = resp.getHeader("Location");
            URL = UrlNext;

            resp = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(URL)
                    .andReturn();
            statusCode = resp.getStatusCode();

        }
        resp = RestAssured
                .given()
                .redirects()
                .follow(true)
                .when()
                .get(URL)
                .andReturn();

        statusCode = resp.getStatusCode();
        System.out.println("Финальный URL " + URL + " со статусом " + statusCode);

    }
}

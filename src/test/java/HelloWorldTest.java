import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {

    @Test
    public void testRestAssured() throws InterruptedException {
        String URL = "https://playground.learnqa.ru/ajax/api/longtime_job";

        JsonPath resp = RestAssured
                .get(URL)
                .jsonPath();
        String token = resp.get("token");
        int time = resp.get("seconds");

        Map<String, String> params = new HashMap<>();
        params.put("token", token);

        JsonPath requestBefore = RestAssured
                .given()
                .queryParams(params)
                .get(URL)
                .jsonPath();
        String statusBefore = requestBefore.get("status");
        System.out.println("Статус до готовности задачи: " + statusBefore);

        Thread.sleep(time * 1000);
        JsonPath requestAfter = RestAssured
                .given()
                .queryParams(params)
                .get(URL)
                .jsonPath();
        String statusAfter = requestAfter.get("status");
        String result = requestAfter.get("result");
        System.out.println("Статус после выполнения задачи: " + statusAfter);
        System.out.println("Результат: " + result);





    }
}

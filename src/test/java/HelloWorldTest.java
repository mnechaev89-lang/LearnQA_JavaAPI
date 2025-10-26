import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {

    @Test
    public void testRestAssured(){


        JsonPath resp = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        String message2 = resp.get("messages[1].message");
        String time2 = resp.get("messages[1].timestamp");
        System.out.println(message2 + ": " + time2);

    }
}

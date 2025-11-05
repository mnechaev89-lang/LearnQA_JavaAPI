import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringSizeTest {

    @Test
        public void testCookieValue() {

            Response response = RestAssured
                    .get("https://playground.learnqa.ru/api/homework_cookie")
                    .andReturn();

            //System.out.println(response.getCookies());
            String nameCookie = "HomeWork";
            String valueCookie = "hw_value";
            assertTrue(response.getCookies().containsKey(nameCookie), "Отсутствует cookie 'HomeWork'");
            assertTrue(response.getCookies().containsValue(valueCookie), "Отсутствует значение 'hw_value' у куки 'HomeWork'");
    }
}

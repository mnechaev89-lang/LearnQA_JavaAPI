import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUserAgent {

    @ParameterizedTest
    @CsvSource({
            "'Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30', Mobile, No, Android",
            "'Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1', Mobile, Chrome, iOS",
            "'Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', Googlebot, Unknown, Unknown",
            "'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0', Web, Chrome, No",
            "'Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1', Mobile, No, iPhone"
    })
    public void testUserAgent(String userAgent, String expectedPlatform, String expectedBrowser, String expectedDevice) {
        JsonPath response = RestAssured
                .given()
                .header("User-Agent", userAgent)  // Исправлено: header вместо queryParams
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .jsonPath();

        String actualPlatform = response.getString("platform");
        String actualBrowser = response.getString("browser");
        String actualDevice = response.getString("device");



        if (!expectedPlatform.equals(actualPlatform) ||
                !expectedBrowser.equals(actualBrowser) ||
                !expectedDevice.equals(actualDevice)) {

            System.out.println("Ошибка в User-Agent: " + userAgent + "\n");
            assertEquals(expectedPlatform, actualPlatform, "Неверная платформа. Должна быть: '" + expectedPlatform + "' ,а фактическая: '" + actualPlatform + "'");
            assertEquals(expectedBrowser, actualBrowser, "Неверный браузер. Должна быть: '" + expectedBrowser + "' ,а фактическая: '" + actualBrowser + "'");
            assertEquals(expectedDevice, actualDevice, "Неверный девайс. Должна быть: '" + expectedDevice + "''' ,а фактическая: '" + actualDevice + "'");

        }
    }
}
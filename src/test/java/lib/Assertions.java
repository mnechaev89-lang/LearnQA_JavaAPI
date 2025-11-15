package lib;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    public static void asserJsonByName(Response Response, String name, int expectedValue){ //3 параметра - когда нужно: взять ответ → найти конкретное поле → сравнить с ожидаемым значением
    Response.then().assertThat().body("$", hasKey(name));// Проверяем, есть ли поле с именем name

    int value = Response.jsonPath().getInt(name); // Получаем значение name как число
    assertEquals(expectedValue, value, "JSON value is not equal to expected value");

    }

    public static void asserJsonByName(Response Response, String name, String expectedValue){ //3 параметра - когда нужно: взять ответ → найти конкретное поле → сравнить с ожидаемым значением
        Response.then().assertThat().body("$", hasKey(name));// Проверяем, есть ли поле с именем name

        String value = Response.jsonPath().getString(name); // Получаем значение name как строку
        assertEquals(expectedValue, value, "JSON value is not equal to expected value");

    }

    public static void assertResponseTextEquals(Response Response, String expectedAnswer) { //Сравнивает весь текст ответа с ожидаемым текстом
        assertEquals(
                expectedAnswer,
                Response.asString(), // Превращаем весь ответ в строку
                "Response text is not as expected"
        );
    }

    public static void assertResponseCodeEquals(Response Response, int expectedStatusCode){ // Проверяет статус код
        assertEquals(
                expectedStatusCode,
                Response.statusCode(), // Получаем код статуса HTTP
                "Response status code is not as expected"
        );
    }

    public static void assertJsonHasField(Response Response, String expectedFieldName) {
        Response.then().assertThat().body("$", hasKey(expectedFieldName)); //проверяет что поле существует, но не проверяет его значение.
    }

    public static void assertJsonHasFields(Response Response, String[] expectedFieldNames) {
        for(String expectedFieldName : expectedFieldNames) {
            Assertions.assertJsonHasField(Response, expectedFieldName);
        }
    }

    public static void assertJsonHasNotField(Response Response, String unexpectedFieldName) {
        Response.then().assertThat().body("$", not(hasKey(unexpectedFieldName)));
    }


}

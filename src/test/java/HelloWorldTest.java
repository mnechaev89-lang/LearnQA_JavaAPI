import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {

    @Test
    public void testRestAssured() {
        String URL1 = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
        String URL2 = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";

                String[] passwords = {"password","123456","123456789","12345678","12345","football","qwerty","abc123","football",
                "monkey","111111","letmein","1234","1234567890","1234567","dragon","baseball","sunshine",
                "iloveyou","trustno1","princess","adobe123","secret","18atcskd2w","adobe123[a]","mynoob","123321","123123","welcome","login","admin","solo",
                "1q2w3e4r","master","666666","1q2w3e","photoshop","1q2w3e4r5t","google","3rjs1la7qe","photoshop[a]","987654321","1qaz2wsx","qwertyuiop","ashley","mustang","121212",
                "starwars","654321","bailey","access","zxcvbnm","flower","555555","passw0rd","shadow","lovely","7777777",
                "michael","!@#$%^&*","jesus","password1","superman","hello","charlie","888888","696969",
                "hottie","freedom","aa123456","qazwsx","ninja","azerty","loveme","whatever","donald",
                "batman","zaq1zaq1","Football","000000","123qwe","qwerty123"};

        String login = "super_admin";

        for(String password : passwords) {
            Map<String, String> loginPassword = new HashMap<>();
            loginPassword.put("login", login);
            loginPassword.put("password", password);

            //System.out.println("Используемый в этот раз пароль: " + password);

            Response resp = RestAssured
                    .given()
                    .body(loginPassword)
                    .when()
                    .post(URL1)
                    .andReturn();
            String authCookie = resp.getCookie("auth_cookie");
            //System.out.println("Куки с первого URL: " + authCookie);

            Response resp2 = RestAssured
                    .given()
                    .cookie("auth_cookie", authCookie)
                    .when()
                    .post(URL2)
                    .andReturn();
            //System.out.println("Ответ от URL2: " + resp2.print());

            //resp2.print();
            String resp2Body = resp2.getBody().asString();
            if (resp2Body.equals("You are authorized")) {
                System.out.println("Верный пароль: " + password);

                System.out.println("Сообщение от сервера:");
                resp2.print();
                break;
            }
        }


    }
}

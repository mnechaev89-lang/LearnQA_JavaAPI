package lib;

import org.junit.jupiter.api.DisplayNameGenerator;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DataGenerator {
    // Генерирует случайный email на основе текущего времени
    public static String getRandomEmail() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        return "learnqa" + timestamp + "@example.com";
    }
    // Создает данные для регистрации со случайным email
    public static Map<String, String> getRegistrationData() {
        Map<String, String> data = new HashMap<>();
        data.put("email", DataGenerator.getRandomEmail());
        data.put("password", "123");
        data.put("username", "learnqa");
        data.put("firstName", "learnqa");
        data.put("lastName", "learnqa");
        return data;
    }
    // Создает данные для регистрации, но позволяет переопределить некоторые значения
    public static Map<String, String> getRegistrationData(Map<String, String> nonDefaultValues) {
        // Берем данные по умолчанию
        Map<String, String> defaultValues = DataGenerator.getRegistrationData(); //Теперь в defaultValues лежат все стандартные значения.
        // Объединяем с переданными значениями
        Map<String, String> userData = new HashMap<>();//Создаем новую пустую "анкету"
        String[] keys = {"email", "password", "username", "firstName", "lastName"};//Определяем какие поля нам нужны
        for (String key : keys) {
            if (nonDefaultValues.containsKey(key)) {
                // Если в переданных значениях ЕСТЬ этот ключ - берем оттуда
                userData.put(key, nonDefaultValues.get(key));
            } else {
                // Если в переданных значениях НЕТ этого ключа - берем стандартное
                userData.put(key, defaultValues.get(key));
            }

        }
        return userData;
    }
}

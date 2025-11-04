import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringSizeTest {

    @Test
    public void testStringSize() {

        String sentence = "Hello, world";
        int size = 15;

        assertEquals(size, sentence.length());
    }
}

import org.junit.Test;
import ru.aloyenz.ancientcaves.noise.WhiteNoiseGenerator;

public class WhiteNoiseTest {

    WhiteNoiseGenerator generator = new WhiteNoiseGenerator(123);

    @Test
    public void testGenerator() {
        for (int x = 0; x <= 255*255; x++) {
            System.out.println(x + " " + generator.noise(x));
        }
    }
}

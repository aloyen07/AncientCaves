import org.junit.Test;
import ru.aloyenz.ancientcaves.AncientCaves;
import ru.aloyenz.ancientcaves.noise.PerlinNoiseGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PerlinNoiseTest {

    private long seed = System.currentTimeMillis();
    private AncientCaves ancientCaves = new AncientCaves();
    private PerlinNoiseGenerator generator = new PerlinNoiseGenerator(seed);

    public PerlinNoiseTest() throws IOException {
    }

    @Test
    public void testCpuPerlinNoise() {
        for (int ymax = 0; ymax <= 255; ymax++) {
            long start = System.currentTimeMillis();
            for (int y = 0; y <= ymax; y++) {
                for (int x = 0; x <= 15; x++) {
                    for (int z = 0; z <= 15; z++) {
                        generator.noise(x, y, z, 5, 0.0001D, 0.0001D, true);
                    }
                }
            }
            long end = System.currentTimeMillis();

            System.out.println("[CPUPN ] Generated " + ymax + " layers. Time elapsed: " + (end-start) + "ms.");
        }
    }

    public double[] listToInternalDouble(List<Double> xIn) {
        double[] out = new double[xIn.size()];

        for (int i = 0; i < xIn.size(); i++) {
            out[i] = xIn.get(i);
        }

        return out;
    }

    @Test
    public void testAsyncPerlinNoise() {
        long taskStart = System.currentTimeMillis();
        for (int ymax = 0; ymax <= 255; ymax++) {
            long start = System.currentTimeMillis();
                double[] xs = new double[16 * 16 * 256];
                double[] ys = new double[16 * 16 * 256];
                double[] zs = new double[16 * 16 * 256];
                for (int y = 0; y <= ymax; y++) {
                    for (int x = 0; x <= 15; x++) {
                        for (int z = 0; z <= 15; z++) {
                            xs[x * y * z] = x;
                            ys[x * y * z] = y;
                            zs[x * y * z] = z;
                        }
                    }
                }

                generator.generateMassiveAsyncronosly(xs,
                        ys,
                        zs,
                        5,
                        0.0001D,
                        0.0001D,
                        true, 32*(64*2), true);

                long end = System.currentTimeMillis();

                System.out.println("[ACPUPN] Generated " + ymax + " layers. Time elapsed: " + (end - start) + "ms.");

        }

        System.out.println("[ACPUPN] Task ended. Time elapsed: " + (System.currentTimeMillis() - taskStart) + "ms.");
    }

    private double[] toInternalDouble(Double[] in) {
        double[] xz = new double[in.length];

        int id = 0;
        for (Double db : in) {
            xz[id] = db;
            id += 1;
        }

        return xz;
    }

    @Test
    public void testGpuPerlinNoise() {
        for (int ymax = 0; ymax <= 255; ymax++) {
            long start = System.currentTimeMillis();
            double[] xs = new double[16*16*256];
            double[] ys = new double[16*16*256];
            double[] zs = new double[16*16*256];
            for (int y = 0; y <= ymax; y++) {
                for (int x = 0; x <= 15; x++) {
                    for (int z = 0; z <= 15; z++) {
                        xs[x*y*z] = x;
                        ys[x*y*z] = y;
                        zs[x*y*z] = z;
                    }
                }
            }

            generator.generateMassive(xs,
                    ys,
                    zs,
                    5,
                    0.0001D,
                    0.0001D,
                    true, false, true, seed);

            long end = System.currentTimeMillis();

            System.out.println("[GPUPN ] Generated " + ymax + " layers. Time elapsed: " + (end-start) + "ms.");
        }
    }
}

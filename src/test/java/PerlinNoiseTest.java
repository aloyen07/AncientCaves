import ru.aloyenz.ancientcaves.noise.PerlinNoiseGenerator;

import java.util.ArrayList;
import java.util.List;

public class PerlinNoiseTest {

    private long seed = System.currentTimeMillis();
    private PerlinNoiseGenerator generator = new PerlinNoiseGenerator(seed);

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

            System.out.println("[CPUPN ] Generated " + ymax + " layers. Time elapsed: " + (start-end) + "ms.");
        }
    }

    public void testAsyncPerlinNoise() {
        for (int ymax = 0; ymax <= 255; ymax++) {
            long start = System.currentTimeMillis();
            List<Double> xs = new ArrayList<>();
            List<Double> ys = new ArrayList<>();
            List<Double> zs = new ArrayList<>();
            for (int y = 0; y <= ymax; y++) {
                for (int x = 0; x <= 15; x++) {
                    for (int z = 0; z <= 15; z++) {
                        xs.add((double) x);
                        ys.add((double) y);
                        zs.add((double) z);
                    }
                }
            }

            generator.generateMassive(toInternalDouble(xs.toArray(new Double[0])),
                    toInternalDouble(ys.toArray(new Double[0])),
                    toInternalDouble(zs.toArray(new Double[0])),
                    5,
                    0.0001D,
                    0.0001D,
                    true, true, false, seed);

            long end = System.currentTimeMillis();

            System.out.println("[ACPUPN] Generated " + ymax + " layers. Time elapsed: " + (start-end) + "ms.");
        }
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

    public void testGpuPerlinNoise() {
        for (int ymax = 0; ymax <= 255; ymax++) {
            long start = System.currentTimeMillis();
            List<Double> xs = new ArrayList<>();
            List<Double> ys = new ArrayList<>();
            List<Double> zs = new ArrayList<>();
            for (int y = 0; y <= ymax; y++) {
                for (int x = 0; x <= 15; x++) {
                    for (int z = 0; z <= 15; z++) {
                        xs.add((double) x);
                        ys.add((double) y);
                        zs.add((double) z);
                    }
                }
            }

            generator.generateMassive(toInternalDouble(xs.toArray(new Double[0])),
                    toInternalDouble(ys.toArray(new Double[0])),
                    toInternalDouble(zs.toArray(new Double[0])),
                    5,
                    0.0001D,
                    0.0001D,
                    true, false, true, seed);

            long end = System.currentTimeMillis();

            System.out.println("[GPUPN ] Generated " + ymax + " layers. Time elapsed: " + (start-end) + "ms.");
        }
    }
}

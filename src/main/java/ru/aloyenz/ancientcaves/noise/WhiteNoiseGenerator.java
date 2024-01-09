package ru.aloyenz.ancientcaves.noise;

import java.util.Random;

public class WhiteNoiseGenerator extends NoiseGenerator {

    private final long seed;

    public WhiteNoiseGenerator(long seed) {
        this.seed = seed;
    }

    @Override
    public double noise(double x, double y, double z) {
        return noise((long) Math.floor(x), (long) Math.floor(y), (long) Math.floor(z));
    }

    public double noise(long x, long y, long z) {
        int n = (int) ((seed) ^ (1619 * (x)));
        n ^= (int) (31337 * y);
        n ^= (int) (6971 * z);

        return (n * n * n * 60493) / 2147483648.0;
    }
}

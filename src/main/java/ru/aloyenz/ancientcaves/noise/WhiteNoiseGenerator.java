package ru.aloyenz.ancientcaves.noise;

import java.util.Random;

public class WhiteNoiseGenerator extends NoiseGenerator {

    private final long seed;

    public WhiteNoiseGenerator(Long seed) {
        this.seed = seed;
    }

    @Override
    public double noise(double x, double y, double z) {
        Random random = new Random();
        return 0;
    }
}

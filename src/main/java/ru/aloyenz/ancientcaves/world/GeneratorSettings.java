package ru.aloyenz.ancientcaves.world;

public class GeneratorSettings {

    private final double xSizeDiv;
    private final double ySizeDiv;
    private final double zSizeDiv;
    private final int octaves;
    private final double frequency;
    private final double amplitude;
    private final boolean normalized;
    private final double multiplier;

    public GeneratorSettings(double xSizeDiv,
                             double ySizeDiv,
                             double zSizeDiv,
                             int octaves,
                             double frequency,
                             double amplitude,
                             boolean normalized,
                             double multiplier) {
        this.xSizeDiv = xSizeDiv;
        this.ySizeDiv = ySizeDiv;
        this.zSizeDiv = zSizeDiv;
        this.octaves = octaves;
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.normalized = normalized;
        this.multiplier = multiplier;
    }

    public double getXSizeDiv() {
        return xSizeDiv;
    }

    public double getYSizeDiv() {
        return ySizeDiv;
    }

    public double getZSizeDiv() {
        return zSizeDiv;
    }

    public int getOctaves() {
        return octaves;
    }

    public double getFrequency() {
        return frequency;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public boolean isNormalized() {
        return normalized;
    }

    public double getMultiplier() {
        return multiplier;
    }
}

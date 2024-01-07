package ru.aloyenz.ancientcaves.world;

import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public enum BiomeGeneratorSettings {

    PLAINS(Biomes.PLAINS,
            new GeneratorSettings(60, 160, 60, 16, 0.01D, 0.001D,
                    false, 4D),
            new GeneratorSettings(30, 80, 30, 6, 0.01D, 0.001D,
                    false, 2D),
            new GeneratorSettings(15, 40, 15, 5, 0.01D, 0.001D,
                    false, 1D),
            1.5D,
            new TerrainTopBlockArray().addBlock(Blocks.GRASS).addBlocks(Blocks.DIRT, 3)
    );

    BiomeGeneratorSettings(Biome linkedBiome,
                           GeneratorSettings upperNoiseGeneratorSettings,
                           GeneratorSettings downNoiseGeneratorSettings,
                           GeneratorSettings decoratorNoiseGeneratorSettings,
                           double applyRange,
                           TerrainTopBlockArray blocks) {
        this.linkedBiome = linkedBiome;
        this.upperNoiseGeneratorSettings = upperNoiseGeneratorSettings;
        this.downNoiseGeneratorSettings = downNoiseGeneratorSettings;
        this.decoratorNoiseGeneratorSettings = decoratorNoiseGeneratorSettings;
        this.applyRange = applyRange;
        this.blocks = blocks;
    }

    private static BiomeGeneratorSettings getOrDefault(@Nullable BiomeGeneratorSettings settings) {
        if (settings == null) {
            return BiomeGeneratorSettings.PLAINS;
        } else {
            return settings;
        }
    }

    public static BiomeGeneratorSettings getFromBiome(Biome biome) {
        // TODO: Fix Crash Caused by: java.lang.IllegalArgumentException: No enum constant ru.aloyenz.ancientcaves.world.BiomeGeneratorSettings.FORESTHILLS
        return getOrDefault(BiomeGeneratorSettings.valueOf(biome.getBiomeName().toUpperCase()));
    }

    private final Biome linkedBiome;
    private final GeneratorSettings upperNoiseGeneratorSettings;
    private final GeneratorSettings downNoiseGeneratorSettings;
    private final GeneratorSettings decoratorNoiseGeneratorSettings;
    private final double applyRange;
    private final TerrainTopBlockArray blocks;

    public Biome getLinkedBiome() {
        return linkedBiome;
    }

    public GeneratorSettings getUpperNoiseGeneratorSettings() {
        return upperNoiseGeneratorSettings;
    }

    public GeneratorSettings getDownNoiseGeneratorSettings() {
        return downNoiseGeneratorSettings;
    }

    public GeneratorSettings getDecoratorNoiseGeneratorSettings() {
        return decoratorNoiseGeneratorSettings;
    }

    public double getApplyRange() {
        return applyRange;
    }

    public TerrainTopBlockArray getBlocks() {
        return blocks;
    }
}

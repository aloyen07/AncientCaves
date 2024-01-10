package ru.aloyenz.ancientcaves.world;

import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;
import java.util.HashMap;

public class BiomeGeneratorSettings {

    private static final HashMap<String, BiomeGeneratorSettings> settings = new HashMap<>();

    public static final BiomeGeneratorSettings UNDEFINED = new BiomeGeneratorSettings(Biomes.PLAINS,
            new GeneratorSettings(270, 40, 270, 14, 0.01D, 0.001D,
                    false, 4D), // Upper

            new GeneratorSettings(130, 40, 130, 6, 0.01D, 0.001D,
                    false, -4D), // Lower

            new GeneratorSettings(130, 40, 130, 5, 0.01D, 0.001D,
                    false, -4D), // Decorator

            1D, // applyRange

            TerrainTopBlockArray.builder().addBlock(Blocks.GRASS).addBlocks(Blocks.DIRT, 3).build(),
            TerrainTopBlockArray.builder().addBlocks(Blocks.DIRT, 4).build()
    );

//    public static final BiomeGeneratorSettings PLAINS = new BiomeGeneratorSettings(Biomes.PLAINS,
//            new GeneratorSettings(60, 160, 60, 16, 0.01D, 0.001D,
//                    false, 4D),
//            new GeneratorSettings(30, 80, 30, 6, 0.01D, 0.001D,
//                    false, 2D),
//            new GeneratorSettings(15, 40, 15, 5, 0.01D, 0.001D,
//                    false, 1D),
//            1.5D,
//            TerrainTopBlockArray.builder().addBlock(Blocks.GRASS).addBlocks(Blocks.DIRT, 3).build(),
//            TerrainTopBlockArray.builder().addBlocks(Blocks.DIRT, 4).build()
//    );

    private BiomeGeneratorSettings(@Nullable Biome linkedBiome,
                           GeneratorSettings upperNoiseGeneratorSettings,
                           GeneratorSettings downNoiseGeneratorSettings,
                           GeneratorSettings decoratorNoiseGeneratorSettings,
                           double applyRange,
                           TerrainTopBlockArray surfaceBlocks,
                           TerrainTopBlockArray underwaterBlocks) {
        this.linkedBiome = linkedBiome;
        this.upperNoiseGeneratorSettings = upperNoiseGeneratorSettings;
        this.downNoiseGeneratorSettings = downNoiseGeneratorSettings;
        this.decoratorNoiseGeneratorSettings = decoratorNoiseGeneratorSettings;
        this.applyRange = applyRange;
        this.surfaceBlocks = surfaceBlocks;
        this.underwaterBlocks = underwaterBlocks;

        if (linkedBiome != null) {
            settings.put(linkedBiome.getBiomeName(), this);
        } else {
            settings.put("UNDEFINED", this);
        }
    }

    public static BiomeGeneratorSettings getFromBiome(Biome biome) {
        if (settings.get(biome.getBiomeName()) == null) {
            return UNDEFINED;
        } else {
            return settings.get(biome.getBiomeName());
        }
    }

    @Nullable
    private final Biome linkedBiome;
    private final GeneratorSettings upperNoiseGeneratorSettings;
    private final GeneratorSettings downNoiseGeneratorSettings;
    private final GeneratorSettings decoratorNoiseGeneratorSettings;
    private final double applyRange;
    private final TerrainTopBlockArray surfaceBlocks;
    private final TerrainTopBlockArray underwaterBlocks;

    @Nullable
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

    public TerrainTopBlockArray getSurfaceBlocks() {
        return surfaceBlocks;
    }

    public TerrainTopBlockArray getUnderwaterBlocks() {
        return this.underwaterBlocks;
    }
}

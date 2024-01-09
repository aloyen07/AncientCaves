package ru.aloyenz.ancientcaves.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import ru.aloyenz.ancientcaves.noise.PerlinNoiseGenerator;

import java.util.List;
import java.util.Random;

public class LandscapeGenerator {

    public final static int smoothLayerSize = 32;
    public final static int waterLevel = 105;

    private final PerlinNoiseGenerator baseNoiseGenerator;
    private final PerlinNoiseGenerator minusNoiseGenerator;
    private final PerlinNoiseGenerator decoratorNoiseGenerator;


    private final IBlockState stone = Blocks.STONE.getDefaultState();
    private final IBlockState air = Blocks.AIR.getDefaultState();
    private final IBlockState water = Blocks.WATER.getDefaultState();

    public static final int baseLandscapeStart = AncientCavesGenerator.solidStoneHeight + smoothLayerSize;
    private static final int baseLandscapeEnd = 256 - AncientCavesGenerator.solidStoneHeight - smoothLayerSize;

    public LandscapeGenerator(Long seed) {
        Random random = new Random(seed);

        this.baseNoiseGenerator = new PerlinNoiseGenerator(nextLong(random, seed));
        this.minusNoiseGenerator = new PerlinNoiseGenerator(nextLong(random, seed));
        this.decoratorNoiseGenerator = new PerlinNoiseGenerator(nextLong(random, seed));
    }

    private long nextLong(Random rng, long bound) { // TODO: Refactor this
        // error checking and 2^x checking removed for simplicity.
        long bits, val;
        do {
            bits = (rng.nextLong() << 1) >>> 1;
            val = bits % bound;
        } while (bits-val+(bound-1) < 0L);
        return val;
    }

    /**
     * Generates a base cave-landscape.
     * Uses 4 types of PerlinNoise:
     *  - Upper - generate up smooth-layer (32 blocks)
     *  - Downer - generate down smooth-layer (32 blocks)
     *  - Base - generate base cave-landscape (other layers)
     *  - Minus - minuses blocks from base cave-landscape (for a more beautiful look)
     *
     * @param chunkIn - Chunk to process.
     * @param chunkX - Chunk X coordinate.
     * @param chunkZ - Chunk Z coordinate.
     */
    public ChunkPrimer processChunk(ChunkPrimer chunkIn, int chunkX, int chunkZ, Biome[] biomes) {

//        BiomeProvider biomeProvider = world.getBiomeProvider();
//        Biome[] biomes = biomeProvider.getBiomes(new Biome[]{}, chunkX, chunkZ, 16, 16, true);
        for (int x = 0; x <= 15; x++) {
            for (int y = baseLandscapeStart; y <= baseLandscapeEnd; y++) {
                for (int z = 0; z <= 15; z++) {
                    BiomeGeneratorSettings settings = BiomeGeneratorSettings.getFromBiome(biomes[x*z]);
                    GeneratorSettings baseNoiseSettings = settings.getUpperNoiseGeneratorSettings();
                    GeneratorSettings minusNoiseSettings = settings.getDownNoiseGeneratorSettings();
                    GeneratorSettings decoratorNoiseSettings = settings.getDecoratorNoiseGeneratorSettings();
                    double plex = baseNoiseGenerator.noise(
                            (x + 16*chunkX) / baseNoiseSettings.getXSizeDiv(),
                            y / baseNoiseSettings.getYSizeDiv(),
                            (z + 16*chunkZ) / baseNoiseSettings.getZSizeDiv(),
                            baseNoiseSettings.getOctaves(),
                            baseNoiseSettings.getFrequency(),
                            baseNoiseSettings.getAmplitude(),
                            baseNoiseSettings.isNormalized()) * baseNoiseSettings.getMultiplier() -
                                  minusNoiseGenerator.noise(
                                          (x + 16*chunkX) / minusNoiseSettings.getXSizeDiv(),
                                          y / minusNoiseSettings.getYSizeDiv(),
                                          (z + 16*chunkZ) / minusNoiseSettings.getZSizeDiv(),
                                          minusNoiseSettings.getOctaves(),
                                          minusNoiseSettings.getFrequency(),
                                          minusNoiseSettings.getAmplitude(),
                                          minusNoiseSettings.isNormalized()) * minusNoiseSettings.getMultiplier() -
                                  decoratorNoiseGenerator.noise(
                                          (x + 16*chunkX) / decoratorNoiseSettings.getXSizeDiv(),
                                          y / decoratorNoiseSettings.getYSizeDiv(),
                                          (z + 16*chunkZ) / decoratorNoiseSettings.getZSizeDiv(),
                                          decoratorNoiseSettings.getOctaves(),
                                          decoratorNoiseSettings.getFrequency(),
                                          decoratorNoiseSettings.getAmplitude(),
                                          decoratorNoiseSettings.isNormalized())  * decoratorNoiseSettings.getMultiplier();

                    if (plex >= settings.getApplyRange()) {
                        chunkIn.setBlockState(x, y, z, stone);
                    }
                }
            }
        }

        // Generating scapes
        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                BiomeGeneratorSettings settings = BiomeGeneratorSettings.getFromBiome(biomes[x*z]);
                GeneratorSettings baseNoiseSettings = settings.getUpperNoiseGeneratorSettings();
                GeneratorSettings minusNoiseSettings = settings.getDownNoiseGeneratorSettings();
                GeneratorSettings decoratorNoiseSettings = settings.getDecoratorNoiseGeneratorSettings();

                double minimalPlex = 0 - minusNoiseSettings.getMultiplier() - decoratorNoiseSettings.getMultiplier();
                double rawPlex = baseNoiseGenerator.noise(
                        (x + 16*chunkX) / baseNoiseSettings.getXSizeDiv(),
                        baseLandscapeStart / baseNoiseSettings.getYSizeDiv(),
                        (z + 16*chunkZ) / baseNoiseSettings.getZSizeDiv(),
                        baseNoiseSettings.getOctaves(),
                        baseNoiseSettings.getFrequency(),
                        baseNoiseSettings.getAmplitude(),
                        baseNoiseSettings.isNormalized()) * baseNoiseSettings.getMultiplier() -
                        minusNoiseGenerator.noise(
                                (x + 16*chunkX) / minusNoiseSettings.getXSizeDiv(),
                                baseLandscapeStart / minusNoiseSettings.getYSizeDiv(),
                                (z + 16*chunkZ) / minusNoiseSettings.getZSizeDiv(),
                                minusNoiseSettings.getOctaves(),
                                minusNoiseSettings.getFrequency(),
                                minusNoiseSettings.getAmplitude(),
                                minusNoiseSettings.isNormalized()) * minusNoiseSettings.getMultiplier() -
                        decoratorNoiseGenerator.noise(
                                (x + 16*chunkX) / decoratorNoiseSettings.getXSizeDiv(),
                                baseLandscapeStart / decoratorNoiseSettings.getYSizeDiv(),
                                (z + 16*chunkZ) / decoratorNoiseSettings.getZSizeDiv(),
                                decoratorNoiseSettings.getOctaves(),
                                decoratorNoiseSettings.getFrequency(),
                                decoratorNoiseSettings.getAmplitude(),
                                decoratorNoiseSettings.isNormalized())  * decoratorNoiseSettings.getMultiplier();

                double absRawPlex = rawPlex + Math.abs(minimalPlex);
                double absApplyRange = settings.getApplyRange() + Math.abs(minimalPlex);
                // Необходимо привести rawPlex в значение от 0 до 1, где 1 - applyRange
                double plex = Math.min(absRawPlex, absApplyRange) / absApplyRange;

                int yPaster = (int) (AncientCavesGenerator.solidStoneHeight + (smoothLayerSize*plex));
                for (int y = yPaster; y >= AncientCavesGenerator.solidStoneHeight; y--) {
                    chunkIn.setBlockState(x, y, z, stone);
                }
            }
        }

        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                BiomeGeneratorSettings settings = BiomeGeneratorSettings.getFromBiome(biomes[x*z]);
                GeneratorSettings baseNoiseSettings = settings.getUpperNoiseGeneratorSettings();
                GeneratorSettings minusNoiseSettings = settings.getDownNoiseGeneratorSettings();
                GeneratorSettings decoratorNoiseSettings = settings.getDecoratorNoiseGeneratorSettings();

                double minimalPlex = 0 - minusNoiseSettings.getMultiplier() - decoratorNoiseSettings.getMultiplier();
                double rawPlex = baseNoiseGenerator.noise(
                        (x + 16*chunkX) / baseNoiseSettings.getXSizeDiv(),
                        baseLandscapeEnd / baseNoiseSettings.getYSizeDiv(),
                        (z + 16*chunkZ) / baseNoiseSettings.getZSizeDiv(),
                        baseNoiseSettings.getOctaves(),
                        baseNoiseSettings.getFrequency(),
                        baseNoiseSettings.getAmplitude(),
                        baseNoiseSettings.isNormalized()) * baseNoiseSettings.getMultiplier() -
                        minusNoiseGenerator.noise(
                                (x + 16*chunkX) / minusNoiseSettings.getXSizeDiv(),
                                baseLandscapeEnd / minusNoiseSettings.getYSizeDiv(),
                                (z + 16*chunkZ) / minusNoiseSettings.getZSizeDiv(),
                                minusNoiseSettings.getOctaves(),
                                minusNoiseSettings.getFrequency(),
                                minusNoiseSettings.getAmplitude(),
                                minusNoiseSettings.isNormalized()) * minusNoiseSettings.getMultiplier() -
                        decoratorNoiseGenerator.noise(
                                (x + 16*chunkX) / decoratorNoiseSettings.getXSizeDiv(),
                                baseLandscapeEnd / decoratorNoiseSettings.getYSizeDiv(),
                                (z + 16*chunkZ) / decoratorNoiseSettings.getZSizeDiv(),
                                decoratorNoiseSettings.getOctaves(),
                                decoratorNoiseSettings.getFrequency(),
                                decoratorNoiseSettings.getAmplitude(),
                                decoratorNoiseSettings.isNormalized())  * decoratorNoiseSettings.getMultiplier();

                double absRawPlex = rawPlex + Math.abs(minimalPlex);
                double absApplyRange = settings.getApplyRange() + Math.abs(minimalPlex);
                // Необходимо привести rawPlex в значение от 0 до 1, где 1 - applyRange
                double plex = Math.min(absRawPlex, absApplyRange) / absApplyRange;

                int yPaster = (int) (baseLandscapeEnd + (smoothLayerSize - (smoothLayerSize*plex)));
                for (int y = yPaster; y <= 256 - AncientCavesGenerator.solidStoneHeight; y++) {
                    chunkIn.setBlockState(x, y, z, stone);
                }
            }
        }

        // Placing nature blocks
        // TODO: Fix air-replace for nature-blocks
        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                boolean trigger = false;
                BiomeGeneratorSettings settings = BiomeGeneratorSettings.getFromBiome(biomes[x*z]);
                for (int y = 256; y >= 0; y--) {
                    IBlockState blockState = chunkIn.getBlockState(x, y, z);
                    if (blockState.equals(air) && !trigger) {
                        trigger = true;
                        continue;
                    }

                    if (blockState.equals(stone) && trigger) {
                        trigger = false;
                        List<IBlockState> blocks;

                        if (y >= waterLevel) {
                            blocks = settings.getSurfaceBlocks().getBlocks();
                        } else {
                            blocks = settings.getUnderwaterBlocks().getBlocks();
                        }

                        int diff = 0;
                        for (IBlockState block : blocks) {
                            chunkIn.setBlockState(x, y - diff, z, block);
                            diff += 1;
                        }
                    }
                }
            }
        }

//        // Adding a water
//        for (int x = 0; x <= 15; x++) {
//            for (int z = 0; z <= 15; z++) {
//                for (int y = waterLevel;
//                     y >= AncientCavesGenerator.solidStoneHeight-1; y--) {
//                     if (chunkIn.getBlockState(x, y, z).getBlock().equals(air.getBlock())) {
//                         chunkIn.setBlockState(x, y, z, water);
//                     }
//                }
//            }
//        }

        return chunkIn;
    }
}

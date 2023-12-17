package ru.aloyenz.ancientcaves.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import ru.aloyenz.ancientcaves.AncientCaves;
import ru.aloyenz.ancientcaves.noise.PerlinNoiseGenerator;

import java.util.Random;

public class LandscapeGenerator {

    public final static int smoothLayerSize = 32;
    public final static int waterLevel = 105;

    private final PerlinNoiseGenerator baseNoiseGenerator;
    private final PerlinNoiseGenerator minusNoiseGenerator;
    private final PerlinNoiseGenerator upSmootherNoiseGenerator;
    private final PerlinNoiseGenerator downSmootherNoiseGenerator;


    private final IBlockState stone = Blocks.STONE.getDefaultState();
    private final IBlockState air = Blocks.AIR.getDefaultState();
    private final IBlockState water = Blocks.WATER.getDefaultState();
    private final IBlockState dirtBlock = Blocks.DIRT.getDefaultState();

    public static final int baseLandscapeStart = AncientCavesGenerator.solidStoneHeight + smoothLayerSize;
    private static final int baseLandscapeEnd = 256 - AncientCavesGenerator.solidStoneHeight - smoothLayerSize;

    public LandscapeGenerator(Long seed) {
        Random random = new Random(seed);

        this.baseNoiseGenerator = new PerlinNoiseGenerator(nextLong(random, seed));
        this.minusNoiseGenerator = new PerlinNoiseGenerator(nextLong(random, seed));
        this.upSmootherNoiseGenerator = new PerlinNoiseGenerator(nextLong(random, seed));
        this.downSmootherNoiseGenerator = new PerlinNoiseGenerator(nextLong(random, seed));
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
    public ChunkPrimer processChunk(ChunkPrimer chunkIn, int chunkX, int chunkZ, World world) {

        // Settings for higher noise generator
        int xSize = 180;
        int ySize = 50;
        int zSize = 180;
        int smoothModificator = 40;
        int octaves = 5;
        double frequency = 0.01D;
        double amplitude = 0.001D;
        double applyRange = 0.5D;

        // Settings for lower noise generator
        int mXSize = 130;
        int mYSize = 35;
        int mZSize = 130;
        int mSmoothModificator = 20;
        int mOctaves = 6;
        double mFrequency = 0.01D;
        double mAmplitude = 0.001D;
        double mApplyRange = 0.2D;

        // Settings for up-smoother generator
        int usXSize = 60;
        int usZSize = 60;
        int usOctaves = 6;
        double usFrequency = 0.01D;
        double usAmplitude = 0.1D;
        double usApplyRange = 0.8D;
        int usMaximumSize = 10;

        // Settings for down-smoother generator
        int dsXSize = 80;
        int dsZSize = 80;
        int dsOctaves = 6;
        double dsFrequency = 0.001D;
        double dsAmplitude = 0.1D;
        double dsApplyRange = 0.8D;
        int dsMaximumSize = 10;

        // Clean-up settings
        int endBorder = 10;
        int cleanUpTrigger = 8;

        BiomeProvider biomeProvider = world.getBiomeProvider();
        Biome[] biomes = biomeProvider.getBiomes(new Biome[]{}, chunkX, chunkZ, 16, 16, true);

        // Generating a base-landscape
//        long startTime = System.currentTimeMillis();
        double[] upperLandscapeNoiseX = new double[16*16*(baseLandscapeEnd-baseLandscapeStart+1)];
        double[] upperLandscapeNoiseY = new double[16*16*(baseLandscapeEnd-baseLandscapeStart+1)];
        double[] upperLandscapeNoiseZ = new double[16*16*(baseLandscapeEnd-baseLandscapeStart+1)];
        double[][] upperLandscapeNoiseResult = new double[1][16*16*(baseLandscapeEnd-baseLandscapeStart+1)];
        double[] minusLandscapeNoiseX = new double[16*16*(baseLandscapeEnd-baseLandscapeStart+1)];
        double[] minusLandscapeNoiseY = new double[16*16*(baseLandscapeEnd-baseLandscapeStart+1)];
        double[] minusLandscapeNoiseZ = new double[16*16*(baseLandscapeEnd-baseLandscapeStart+1)];
        double[][] minusLandscapeNoiseResult = new double[1][16*16*(baseLandscapeEnd-baseLandscapeStart+1)];
        int bz = 0;
        for (int x = 0; x <= 15; x++) {
            for (int y = baseLandscapeStart; y <= baseLandscapeEnd; y++) {
                for (int z = 0; z <= 15; z++) {
//                    if (baseNoiseGenerator.noise(
//                            (double) (x + (chunkX*16))/xSize, (double) y/ySize, (double) (z + (chunkZ*16))/zSize,
//                            octaves, frequency, amplitude) <= applyRange) {
//
//                        if (!(minusNoiseGenerator.noise(
//                                (double) (x + (chunkX * 16)) / mXSize, (double) y / mYSize, (double) (z + (chunkZ * 16)) / mZSize,
//                                mOctaves, mFrequency, mAmplitude) <= mApplyRange)) {
//                            chunkIn.setBlockState(x, y, z, stone);
//
//                        }
//                    }

                    upperLandscapeNoiseX[bz] = (double) (x + (chunkX*16))/xSize;
                    upperLandscapeNoiseY[bz] = (double) y/ySize;
                    upperLandscapeNoiseZ[bz] = (double) (z + (chunkZ*16))/zSize;
                    minusLandscapeNoiseX[bz] = (double) (x + (chunkX * 16)) / mXSize;
                    minusLandscapeNoiseY[bz] = (double) y / mYSize;
                    minusLandscapeNoiseZ[bz] = (double) (z + (chunkZ * 16)) / mZSize;
                    bz += 1;
                }
            }
        }

        upperLandscapeNoiseResult[0] = baseNoiseGenerator.generateMassiveAsyncronosly(
                upperLandscapeNoiseX, upperLandscapeNoiseY, upperLandscapeNoiseZ,
                octaves, frequency, amplitude, true, AncientCaves.taskCounter, true);

        minusLandscapeNoiseResult[0] = minusNoiseGenerator.generateMassiveAsyncronosly(
                minusLandscapeNoiseX, minusLandscapeNoiseY, minusLandscapeNoiseZ,
                octaves, frequency, amplitude, true, AncientCaves.taskCounter, true);

        double[] tester = upperLandscapeNoiseResult[0];
        bz = 0;
        for (int x = 0; x <= 15; x++) {
            for (int y = baseLandscapeStart; y <= baseLandscapeEnd; y++) {
                for (int z = 0; z <= 15; z++) {
                    if (upperLandscapeNoiseResult[0][bz] <= applyRange) {
//                        if (!(minusLandscapeNoiseResult[0][bz] <= mApplyRange)) {
//                            chunkIn.setBlockState(x, y, z, stone);
//                        }

                        chunkIn.setBlockState(x, y, z, stone);
                    }
                    bz += 1;
                }
            }
        }
//        AncientCaves.getLogger().info("Generated chunkBase in " + (startTime - System.currentTimeMillis()) + " ms.");

        // Generating downer-scape
        double smoothStep = (double) smoothModificator/(smoothLayerSize - endBorder);
        double mSmoothStep = (double) mSmoothModificator/(smoothLayerSize - endBorder);

        for (int y = AncientCavesGenerator.solidStoneHeight; y <= baseLandscapeStart; y++) {
            double modificator = Math.min(smoothModificator,
                        (baseLandscapeStart - y))*smoothStep;
            double mModificator = Math.min(mSmoothModificator,
                    (baseLandscapeStart - y))*smoothStep;
            for (int x = 0; x <= 15; x++) {
                for (int z = 0; z <= 15; z++) {
                    if (baseNoiseGenerator.noise(
                            (double) (x + (chunkX*16))/xSize, (double) y/(ySize - modificator), (double) (z + (chunkZ*16))/zSize,
                            octaves, frequency, amplitude) <= applyRange ) {
                        chunkIn.setBlockState(x, y, z, stone);
                        if (!(minusNoiseGenerator.noise(
                                (double) (x + (chunkX*16))/mXSize, (double) y/(mYSize - mModificator), (double) (z + (chunkZ*16))/mZSize,
                                mOctaves, mFrequency, mAmplitude) <= mApplyRange)) {
                            chunkIn.setBlockState(x, y, z, air);
                        }
                    }
                }
            }
        }

        // Clean up smooth-empties
        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                boolean cleanUp = false;
                int repr = 0;
                for (int y = baseLandscapeStart; y >= AncientCavesGenerator.solidStoneHeight; y--) {
                    if (!chunkIn.getBlockState(x, y, z).getBlock().equals(air)) {
                        repr += 1;
//                    } else {
//                        repr = 0;
                    }
                    if (repr >= cleanUpTrigger) {
                        cleanUp = true;
                    }
                    if (cleanUp) {
                        chunkIn.setBlockState(x, y, z, stone);
                    }
                }
            }
        }

        // Adding a water
        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                for (int y = waterLevel;
                     y >= AncientCavesGenerator.solidStoneHeight-1; y--) {
                     if (chunkIn.getBlockState(x, y, z).getBlock().equals(air.getBlock())) {
                         chunkIn.setBlockState(x, y, z, water);
                     }
                }
            }
        }


        // Generating upper-scape
        for (int y = baseLandscapeEnd; y <= 256 - AncientCavesGenerator.solidStoneHeight; y++) {
            double modificator = Math.min(smoothModificator,
                    (y - baseLandscapeEnd) * smoothStep);
            double mModificator = Math.min(mSmoothModificator,
                    (y - baseLandscapeEnd) * mSmoothStep);
            for (int x = 0; x <= 15; x++) {
                for (int z = 0; z <= 15; z++) {
                    if (baseNoiseGenerator.noise(
                            (double) (x + (chunkX * 16)) / xSize, (double) y / (ySize - modificator), (double) (z + (chunkZ * 16)) / zSize,
                            octaves, frequency, amplitude) <= applyRange) {
                        if (!(baseNoiseGenerator.noise(
                                (double) (x + (chunkX * 16)) / mXSize, (double) y / (mYSize - mModificator), (double) (z + (chunkZ * 16)) / mZSize,
                                mOctaves, mFrequency, mAmplitude) <= mApplyRange)) {
                            chunkIn.setBlockState(x, y, z, stone);
                        }
                    }
                }
            }
        }

        // Clean up smooth-empties
        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                boolean cleanUp = false;
                int repr = 0;
                for (int y = baseLandscapeEnd; y <= 256 - AncientCavesGenerator.solidStoneHeight ; y++) {
                    if (!chunkIn.getBlockState(x, y, z).getBlock().equals(air)) {
                        repr += 1;
//                    } else {
//                        repr = 0;
                    }
                    if (repr >= cleanUpTrigger) {
                        cleanUp = true;
                    }
                    if (cleanUp) {
                        chunkIn.setBlockState(x, y, z, stone);
                    }
                }
            }
        }

        // Generating up-smoother and down-smoother terrain
        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                // TODO: Убрать сиськи на потолке и полу
                double dsValue = downSmootherNoiseGenerator.noise(
                        (double) (x + chunkX * 16)/dsXSize, 0, (double) (z + chunkZ * 16)/dsZSize,
                        dsOctaves, dsFrequency, dsAmplitude);
                double usValue = upSmootherNoiseGenerator.noise(
                        (double) (x + chunkX * 16)/usXSize, 0, (double) (z + chunkZ * 16)/usZSize,
                        usOctaves, usFrequency, usAmplitude);

                if (dsValue <= dsApplyRange) {
                    for (int y = AncientCavesGenerator.solidStoneHeight;
                         y <= AncientCavesGenerator.solidStoneHeight + dsValue*dsMaximumSize;
                         y++) {
                        chunkIn.setBlockState(x, y, z, stone);
                    }
                }
                if (usValue <= usApplyRange) {
                    for (int y = 256 - AncientCavesGenerator.solidStoneHeight;
                         y >= 256 - AncientCavesGenerator.solidStoneHeight - dsValue*usMaximumSize;
                         y--) {
                        chunkIn.setBlockState(x, y, z, stone);
                    }
                }
            }
        }

        // Placing nature blocks
        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                boolean trigger = false;
                boolean waterDecorate = false;
                Biome biome = biomes[x * z];
                for (int y = 256; y >= 0; y--) {
                    IBlockState blockState = chunkIn.getBlockState(x, y, z);
                    if (blockState.equals(air) && !trigger) {
                        trigger = true;
                        continue;
                    }

                    if (blockState.equals(water) && !waterDecorate) {
                        waterDecorate = true;
                        continue;
                    }

                    if (blockState.equals(stone) && trigger && !waterDecorate) {
                        trigger = false;
                        chunkIn.setBlockState(x, y, z, biome.topBlock);
                        for (int i = 1; i <= 3; i++) {
                            if (chunkIn.getBlockState(x, y-i, z).equals(stone)) {
                                chunkIn.setBlockState(x, y-i, z, dirtBlock);
                            }
                        }
                    }
                    if (blockState.equals(stone) && waterDecorate) {
                        waterDecorate = false;
                        trigger = false;
                        for (int i = 0; i <= 3; i++) {
                            if (chunkIn.getBlockState(x, y-1, z).equals(stone)) {
                                chunkIn.setBlockState(x, y - i, z, dirtBlock);
                            }
                        }
                    }
                }
            }
        }

        return chunkIn;
    }
}

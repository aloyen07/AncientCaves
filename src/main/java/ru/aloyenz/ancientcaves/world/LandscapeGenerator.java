package ru.aloyenz.ancientcaves.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import ru.aloyenz.ancientcaves.noise.PerlinNoiseGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LandscapeGenerator {

    public final static int smoothLayerSize = 32;
    public final static int waterLevel = 120;

    private final Random random;
    private final PerlinNoiseGenerator upNoiseGenerator;
    private final PerlinNoiseGenerator downNoiseGenerator;

    private final PerlinNoiseGenerator baseNoiseGenerator;
    private final PerlinNoiseGenerator minusNoiseGenerator;


    private final IBlockState stone = Blocks.STONE.getDefaultState();
    private final IBlockState air = Blocks.AIR.getDefaultState();
    private final IBlockState water = Blocks.WATER.getDefaultState();

    private final int baseLandscapeStart = AncientCavesGenerator.solidStoneHeight + smoothLayerSize;
    private final int baseLandscapeEnd = 256 - AncientCavesGenerator.solidStoneHeight - smoothLayerSize;

    public LandscapeGenerator(Long seed) {
        this.random = new Random(seed);

        new NoiseGeneratorPerlin(random, 5);

        this.upNoiseGenerator = new PerlinNoiseGenerator(nextLong(random, seed));
        this.downNoiseGenerator = new PerlinNoiseGenerator(nextLong(random, seed));
        this.baseNoiseGenerator = new PerlinNoiseGenerator(nextLong(random, seed));
        this.minusNoiseGenerator = new PerlinNoiseGenerator(nextLong(random, seed));
    }

    long nextLong(Random rng, long bound) {
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

        int xSize = 90;
        int ySize = 50;
        int zSize = 90;
        int smoothModificator = 40;
        int octaves = 5;
        double frequency = 0.001D;
        double amplitude = 0.001D;
        double applyRange = 0.35D;
        int endBorder = 10;
        int cleanUpTrigger = 8;
        int smoothMinimumSideCombo = 4;

        // Generating a base-landscape
        for (int x = 0; x <= 15; x++) {
            for (int y = baseLandscapeStart; y <= baseLandscapeEnd; y++) {
                for (int z = 0; z <= 15; z++) {
                    if (baseNoiseGenerator.noise(
                            (double) (x + (chunkX*16))/xSize, (double) y/ySize, (double) (z + (chunkZ*16))/zSize,
                            octaves, frequency, amplitude) <= applyRange) {
                        chunkIn.setBlockState(x, y, z, stone);
                    }
                }
            }
        }

        // Generating downer-scape
        double smoothStep = (double) smoothModificator/(smoothLayerSize - endBorder);
        for (int x = 0; x <= 15; x++) {
            for (int y = AncientCavesGenerator.solidStoneHeight; y <= baseLandscapeStart; y++) {
                for (int z = 0; z <= 15; z++) {
                    double modificator = Math.min(smoothModificator,
                            (baseLandscapeStart - y))*smoothStep;
                    if (baseNoiseGenerator.noise(
                            (double) (x + (chunkX*16))/xSize, (double) y/(ySize - modificator), (double) (z + (chunkZ*16))/zSize,
                            octaves, frequency, amplitude) <= applyRange ) {
                        chunkIn.setBlockState(x, y, z, stone);
                        if (y == baseLandscapeStart) {
                            chunkIn.setBlockState(x, y, z, Blocks.NETHER_BRICK.getDefaultState());
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

//        // Smoothing up cliffs
//        for (int x = 0; x <= 15; x++) {
//            for (int z = 0; z <= 15; z++) {
//                List<Integer> sidesCombo = new ArrayList<>();
//                List<Block> oldSides = new ArrayList<>();
//                for (int i = 0; i <= 3; i++) {
//                    sidesCombo.add(0);
//                }
//
//                for (int y = baseLandscapeStart; y >= AncientCavesGenerator.solidStoneHeight; y--) {
//                    if (chunkIn.getBlockState(x, y, z).getBlock().equals(Blocks.AIR)) {
//                        List<Block> sides = new ArrayList<>();
//                        sides.add(world.getBlockState(new BlockPos(x+1, y, z)).getBlock());
//                        sides.add(world.getBlockState(new BlockPos(x, y, z+1)).getBlock());
//                        sides.add(world.getBlockState(new BlockPos(x-1, y, z)).getBlock());
//                        sides.add(world.getBlockState(new BlockPos(x, y, z-1)).getBlock());
//
//                        if (oldSides.isEmpty()) {
//                            oldSides = sides;
//                            continue;
//                        }
//
//                        for (int i = 0; i <= 3; i++) {
//                            if (oldSides.get(i).equals(sides.get(i))) {
//                                sidesCombo.set(i, sidesCombo.get(i)+1);
//                            } else {
//                                if (sidesCombo.get(i) >= smoothMinimumSideCombo) {
//                                    int combo = sidesCombo.get(i);
//                                    for (int ys = y+combo; ys >= combo/2; ys--) {
//                                        chunkIn.setBlockState(x, ys, z, stone);
//                                    }
//                                }
//                                sidesCombo.set(i, 0);
//                            }
//                        }
//
//                        oldSides = sides;
//                    }
//                }
//            }
//        }

        return chunkIn;
    }
}

package ru.aloyenz.ancientcaves.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import ru.aloyenz.ancientcaves.noise.WhiteNoiseGenerator;
import ru.aloyenz.ancientcaves.world.biomes.BiomeGetter;

import java.util.Random;

public class AncientCavesPopulator {

    private int x;
    private int z;

    private Biome[] biomes = new Biome[]{};

    private Random treeRandom;
    private Random flowerRandom;

    private WhiteNoiseGenerator flowerGenerator;
    private WhiteNoiseGenerator treeGenerator;

    private final IBlockState grassBlock = Blocks.GRASS.getDefaultState();

    public AncientCavesPopulator(Long seed) {
        Random random = new Random(seed);

        this.treeRandom = new Random(nextLong(random, seed));
        this.flowerRandom = new Random(nextLong(random, seed));
        this.flowerGenerator = new WhiteNoiseGenerator(nextLong(random, seed));
        this.treeGenerator = new WhiteNoiseGenerator(nextLong(random, seed));
    }

    private long nextLong(Random rng, long bound) { // TODO: Refactor this
        long bits, val;
        do {
            bits = (rng.nextLong() << 1) >>> 1;
            val = bits % bound;
        } while (bits-val+(bound-1) < 0L);
        return val;
    }

    public void setWorkingWith(int x, int z, World world) {
        this.biomes = world.getBiomeProvider().getBiomes(biomes, x*16, z*16, 16, 16);
        this.x = x;
        this.z = z;
    }

    public void plantFlowers(World world) {
//        for (int xz = 0; xz <= 15; xz++) {
//            for (int zz = 0; zz <= 15; zz++) {
//                BiomeGetter getter = (BiomeGetter) biomes[xz*zz];
//
//                for (int yz = 255; yz >= 0; yz--) {
//                    System.out.println();
//                    if (world.getBlockState(new BlockPos((x*16) + xz, yz, (z*16) + zz)).equals(grassBlock)) {
//                        double value = Math.abs(flowerGenerator.noise(xz, yz, zz));
//                        for (Biome.FlowerEntry entry : getter.ancientCaves$getFlowerEntries()) {
//                            double percentValue = (double) entry.itemWeight /100;
//                            if (percentValue <= value) {
//                                world.setBlockState(new BlockPos((x*16) + xz, yz+1, (z*16) + zz), entry.state);
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}

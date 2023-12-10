package ru.aloyenz.ancientcaves.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import ru.aloyenz.ancientcaves.noise.PerlinNoiseGenerator;

import java.util.Random;

public class AncientCavesPopulator {

    private int x;
    private int z;

    private final Random chunkFlowerRandom;
    private final PerlinNoiseGenerator chunkFlowerGenerator;

    private BiomeProvider biomeProvider;
    private Biome[] biomes = new Biome[]{};

    private final IBlockState grassBlock = Blocks.GRASS.getDefaultState();

    public AncientCavesPopulator(Long seed) {
        Random random = new Random(seed);

        this.chunkFlowerRandom = random;
        this.chunkFlowerGenerator = new PerlinNoiseGenerator(nextLong(random, seed));
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
        this.biomeProvider = world.getBiomeProvider();
        this.biomes = biomeProvider.getBiomes(biomes, x, z, 16, 16, true);
        this.x = x;
        this.z = z;
    }

    public void plantFlowers(World world) {
        // Planting a flowers...
        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                for (int y = 255; y >= 0; y--) {
                    if (world.getBlockState(new BlockPos(x, y, z)).equals(grassBlock)) {
                        if (this.chunkFlowerGenerator.noise(x*16, y, z*16, 0.0003F, 0.0003F, false) >= 0.5) {
                            biomes[x*z].plantFlower(world, chunkFlowerRandom, new BlockPos(x, y+1, z));
                        }
                    }
                }
            }
        }
    }
}

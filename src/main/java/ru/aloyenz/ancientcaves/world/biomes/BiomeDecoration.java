package ru.aloyenz.ancientcaves.world.biomes;

import net.minecraft.world.chunk.ChunkPrimer;
import ru.aloyenz.ancientcaves.noise.PerlinNoiseGenerator;

import java.util.Random;

public abstract class BiomeDecoration {

    public final PerlinNoiseGenerator generator;
    public final ChunkPrimer primer;
    public final Random random;

    public BiomeDecoration(PerlinNoiseGenerator generator, ChunkPrimer primer, Random random) {
        this.generator = generator;
        this.primer = primer;
        this.random = random;
    }

    public abstract ChunkPrimer decorate();
}

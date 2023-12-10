package ru.aloyenz.ancientcaves.world.biomes.decorations;

import net.minecraft.world.chunk.ChunkPrimer;
import ru.aloyenz.ancientcaves.noise.PerlinNoiseGenerator;
import ru.aloyenz.ancientcaves.world.biomes.BiomeDecoration;

import java.util.Random;

public class UndefinedDecorator extends BiomeDecoration {

    public UndefinedDecorator(PerlinNoiseGenerator generator, ChunkPrimer primer, Random random) {
        super(generator, primer, random);
    }

    @Override
    public ChunkPrimer decorate() {
        return primer;
    }
}

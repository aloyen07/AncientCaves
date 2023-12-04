package ru.aloyenz.ancientcaves.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;

public class AncientCavesWorldType extends WorldType {

    public AncientCavesWorldType() {
        super("ac");
    }

    @Override
    public BiomeProvider getBiomeProvider(World world) {
        return WorldType.DEFAULT.getBiomeProvider(world);
    }

    @Override
    public IChunkGenerator getChunkGenerator(World world, String generatorOptions) {
        return new AncientCavesGenerator(world);
    }
}

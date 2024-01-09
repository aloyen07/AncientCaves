package ru.aloyenz.ancientcaves.mixin;

import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.aloyenz.ancientcaves.world.biomes.BiomeGetter;

import java.util.List;

@Mixin(value = Biome.class)
public abstract class BiomeGetters implements BiomeGetter {

    @Shadow protected List<Biome.FlowerEntry> flowers;

    @Override
    public List<Biome.FlowerEntry> ancientCaves$getFlowerEntries() {
        return flowers;
    }
}

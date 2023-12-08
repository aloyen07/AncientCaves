package ru.aloyenz.ancientcaves.world;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class AncientCavesWorldProvider extends WorldProvider {
    public static DimensionType ANCIENT_CAVES;

    public static void registerIfNeed(){
        if(ANCIENT_CAVES != null)return;
        ANCIENT_CAVES = DimensionType.register("ancientcaves", "_ancientcaves", 912,
                AncientCavesWorldProvider.class, false);
        DimensionManager.registerDimension(912, ANCIENT_CAVES);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {
        return new Vec3d(0.1D, 0.1D, 0.1D);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getSkyColor(net.minecraft.entity.Entity cameraEntity, float partialTicks)
    {
        return new Vec3d(0.1D, 0.1D, 0.1D);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public float getCloudHeight()
    {
        return 10000;
    }

    @SideOnly(Side.CLIENT)
    public boolean isSkyColored()
    {
        return false;
    }

//    @Override
//    protected void init() {
//        this.hasSkyLight = false;
//    }

    @Override
    public boolean hasSkyLight() {
        return false;
    }


    public int getAverageGroundLevel() {
        return 128;
    }


    @Override
    public DimensionType getDimensionType() {
        return ANCIENT_CAVES;
    }


    @Override
    public IChunkGenerator createChunkGenerator() {
        return new AncientCavesGenerator(world);
    }

    @Override
    public BiomeProvider getBiomeProvider() {
        return WorldType.DEFAULT.getBiomeProvider(world);
    }

    public boolean isNether()
    {
        return true;
    }

    public boolean canDoLightning(net.minecraft.world.chunk.Chunk chunk)
    {
        return false;
    }

    public boolean canDoRainSnowIce(net.minecraft.world.chunk.Chunk chunk)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public float getSunBrightness(float par1)
    {
        return 0;
    }

    public float getSunBrightnessFactor(float par1)
    {
        return 0;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
        return null;
    }


}

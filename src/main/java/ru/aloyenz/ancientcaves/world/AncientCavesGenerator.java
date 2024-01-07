package ru.aloyenz.ancientcaves.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.Logger;
import ru.aloyenz.ancientcaves.AncientCaves;
import ru.aloyenz.ancientcaves.noise.PerlinNoiseGenerator;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class AncientCavesGenerator implements IChunkGenerator {

    private final World world;

    private final Random random;

    private final IBlockState bedrock = Blocks.BEDROCK.getDefaultState();
    private final IBlockState stone = Blocks.STONE.getDefaultState();

    private final LandscapeGenerator landscapeGenerator;
    private final AncientCavesPopulator populator;

    private Biome[] biomes = new Biome[]{};

    public static final int solidStoneHeight = 64;

    public AncientCavesGenerator(World worldIn) {
        this.world = worldIn;
        this.random = new Random(worldIn.getSeed());
        this.landscapeGenerator = new LandscapeGenerator(worldIn.getSeed());
        this.populator = new AncientCavesPopulator(worldIn.getSeed());
        DimensionManager.setWorld(912, (WorldServer) worldIn,
                Objects.requireNonNull(worldIn.getMinecraftServer()));
    }

    public ChunkPrimer generateBedrock(ChunkPrimer chunkIn, Random random) {
        for (int downer = 0; downer <= 1; downer++) {
            // 0 = Down layer, 1 = Upper layer
            int layer = 0;
            while (layer <= 4) {
                int y;
                int chance = 100/(layer+1);
                if (downer == 0) {
                    y = layer;
                } else {
                    y = 255 - layer;
                }


                for (int x = 0; x <= 15; x++) {
                    for (int z = 0; z <= 15; z++) {
                        // 0.03125D, 1.0D, 0.03125D
                        if (random.nextInt(100) <= chance) {
                            chunkIn.setBlockState(x, y, z, bedrock);
                        }
                    }
                }
                layer++;
            }
        }

        // TODO: Сделать генерацию привязанной к сиду и неменяющейся от поведения игрока
        return chunkIn;
    }

    public ChunkPrimer generateSolidStoneLayers(ChunkPrimer chunkIn) {
        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                for (int y = 0; y <= solidStoneHeight; y++) {
                    chunkIn.setBlockState(x, y, z, stone);
                }
                for (int y = 255; y >= (255 - solidStoneHeight); y--) {
                    chunkIn.setBlockState(x, y, z, stone);
                }
            }
        }

        return chunkIn;
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        ChunkPrimer chunkPrimer = new ChunkPrimer();
        biomes = world.getBiomeProvider().getBiomes(biomes, x*16, z*16, 16, 16);

        chunkPrimer = generateSolidStoneLayers(chunkPrimer);
        chunkPrimer = landscapeGenerator.processChunk(chunkPrimer, x, z, biomes);
        chunkPrimer = generateBedrock(chunkPrimer, random);

        Chunk chunk = new Chunk(world, chunkPrimer, x, z);
        return chunk;
    }

    @Override
    public void populate(int x, int z) {
//        populator.setWorkingWith(x, z, world);
//        populator.plantFlowers(world);
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return null;
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {

    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }
}

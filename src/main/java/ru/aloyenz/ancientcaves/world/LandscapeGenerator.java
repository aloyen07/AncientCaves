package ru.aloyenz.ancientcaves.world;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.*;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import ru.aloyenz.ancientcaves.noise.PerlinNoiseGenerator;

import java.util.List;
import java.util.Random;

public class LandscapeGenerator {

    public final static int smoothLayerSize = 32;
    public final static int waterLevel = 105;

    private final PerlinNoiseGenerator baseNoiseGenerator;
    private final PerlinNoiseGenerator minusNoiseGenerator;
    private final PerlinNoiseGenerator decoratorNoiseGenerator;


    private final IBlockState stone = Blocks.STONE.getDefaultState();
    private final IBlockState air = Blocks.AIR.getDefaultState();
    private final IBlockState water = Blocks.WATER.getDefaultState();
    private final IBlockState grass = Blocks.GRASS.getDefaultState();


    private final boolean mapFeaturesEnabled = true;
    private MapGenStronghold strongholdGenerator;
    private MapGenVillage villageGenerator;
    private MapGenMineshaft mineshaftGenerator;
    private MapGenScatteredFeature scatteredFeatureGenerator;
    private StructureOceanMonument oceanMonumentGenerator;
    // TODO: Fix it
    //private WoodlandMansion woodlandMansionGenerator;

    private final float extraTreeChance = 0.1F;
    private final Random treeDecoratorRandom;
    private final Random treePosRandom;
    private final int maxTreePlaceAttempts = 1000;

    public static final int baseLandscapeStart = AncientCavesGenerator.solidStoneHeight + smoothLayerSize;
    private static final int baseLandscapeEnd = 256 - AncientCavesGenerator.solidStoneHeight - smoothLayerSize;

    private final Random rand;
    private final ChunkGeneratorSettings settings;

    public LandscapeGenerator(Long seed, IChunkGenerator generator) {
        Random random = new Random(seed);

        this.baseNoiseGenerator = new PerlinNoiseGenerator(nextLong(random, seed));
        this.minusNoiseGenerator = new PerlinNoiseGenerator(nextLong(random, seed));
        this.decoratorNoiseGenerator = new PerlinNoiseGenerator(nextLong(random, seed));
        this.rand = new Random(nextLong(random, seed));
        this.treeDecoratorRandom = new Random(nextLong(random, seed));
        this.treePosRandom = new Random(nextLong(random, seed));

        ChunkGeneratorSettings.Factory factory = new ChunkGeneratorSettings.Factory();
        factory.seaLevel = 105;
        this.settings = factory.build();

        this.strongholdGenerator = new MapGenStronghold();
        this.villageGenerator = new MapGenVillage();
        this.mineshaftGenerator = new MapGenMineshaft();
        this.scatteredFeatureGenerator = new MapGenScatteredFeature();
        this.oceanMonumentGenerator = new StructureOceanMonument();

        strongholdGenerator = (MapGenStronghold)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(strongholdGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.STRONGHOLD);
        villageGenerator = (MapGenVillage)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(villageGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.VILLAGE);
        mineshaftGenerator = (MapGenMineshaft)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(mineshaftGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.MINESHAFT);
        scatteredFeatureGenerator = (MapGenScatteredFeature)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(scatteredFeatureGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.SCATTERED_FEATURE);
        oceanMonumentGenerator = (StructureOceanMonument)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(oceanMonumentGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.OCEAN_MONUMENT);
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
    public ChunkPrimer processChunk(ChunkPrimer chunkIn, int chunkX, int chunkZ, Biome[] biomes, World world) {

//        BiomeProvider biomeProvider = world.getBiomeProvider();
//        Biome[] biomes = biomeProvider.getBiomes(new Biome[]{}, chunkX, chunkZ, 16, 16, true);
        for (int x = 0; x <= 15; x++) {
            for (int y = baseLandscapeStart; y <= baseLandscapeEnd; y++) {
                for (int z = 0; z <= 15; z++) {
                    BiomeGeneratorSettings settings = BiomeGeneratorSettings.getFromBiome(biomes[x*z]);
                    GeneratorSettings baseNoiseSettings = settings.getUpperNoiseGeneratorSettings();
                    GeneratorSettings minusNoiseSettings = settings.getDownNoiseGeneratorSettings();
                    GeneratorSettings decoratorNoiseSettings = settings.getDecoratorNoiseGeneratorSettings();
                    double plex = baseNoiseGenerator.noise(
                            (x + 16*chunkX) / baseNoiseSettings.getXSizeDiv(),
                            y / baseNoiseSettings.getYSizeDiv(),
                            (z + 16*chunkZ) / baseNoiseSettings.getZSizeDiv(),
                            baseNoiseSettings.getOctaves(),
                            baseNoiseSettings.getFrequency(),
                            baseNoiseSettings.getAmplitude(),
                            baseNoiseSettings.isNormalized()) * baseNoiseSettings.getMultiplier() -
                                  minusNoiseGenerator.noise(
                                          (x + 16*chunkX) / minusNoiseSettings.getXSizeDiv(),
                                          y / minusNoiseSettings.getYSizeDiv(),
                                          (z + 16*chunkZ) / minusNoiseSettings.getZSizeDiv(),
                                          minusNoiseSettings.getOctaves(),
                                          minusNoiseSettings.getFrequency(),
                                          minusNoiseSettings.getAmplitude(),
                                          minusNoiseSettings.isNormalized()) * minusNoiseSettings.getMultiplier() -
                                  decoratorNoiseGenerator.noise(
                                          (x + 16*chunkX) / decoratorNoiseSettings.getXSizeDiv(),
                                          y / decoratorNoiseSettings.getYSizeDiv(),
                                          (z + 16*chunkZ) / decoratorNoiseSettings.getZSizeDiv(),
                                          decoratorNoiseSettings.getOctaves(),
                                          decoratorNoiseSettings.getFrequency(),
                                          decoratorNoiseSettings.getAmplitude(),
                                          decoratorNoiseSettings.isNormalized())  * decoratorNoiseSettings.getMultiplier();

                    if (plex >= settings.getApplyRange()) {
                        chunkIn.setBlockState(x, y, z, stone);
                    }
                }
            }
        }

        // Generating scapes
        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                BiomeGeneratorSettings settings = BiomeGeneratorSettings.getFromBiome(biomes[x*z]);
                GeneratorSettings baseNoiseSettings = settings.getUpperNoiseGeneratorSettings();
                GeneratorSettings minusNoiseSettings = settings.getDownNoiseGeneratorSettings();
                GeneratorSettings decoratorNoiseSettings = settings.getDecoratorNoiseGeneratorSettings();

                double minimalPlex = 0 - minusNoiseSettings.getMultiplier() - decoratorNoiseSettings.getMultiplier();
                double rawPlex = baseNoiseGenerator.noise(
                        (x + 16*chunkX) / baseNoiseSettings.getXSizeDiv(),
                        baseLandscapeStart / baseNoiseSettings.getYSizeDiv(),
                        (z + 16*chunkZ) / baseNoiseSettings.getZSizeDiv(),
                        baseNoiseSettings.getOctaves(),
                        baseNoiseSettings.getFrequency(),
                        baseNoiseSettings.getAmplitude(),
                        baseNoiseSettings.isNormalized()) * baseNoiseSettings.getMultiplier() -
                        minusNoiseGenerator.noise(
                                (x + 16*chunkX) / minusNoiseSettings.getXSizeDiv(),
                                baseLandscapeStart / minusNoiseSettings.getYSizeDiv(),
                                (z + 16*chunkZ) / minusNoiseSettings.getZSizeDiv(),
                                minusNoiseSettings.getOctaves(),
                                minusNoiseSettings.getFrequency(),
                                minusNoiseSettings.getAmplitude(),
                                minusNoiseSettings.isNormalized()) * minusNoiseSettings.getMultiplier() -
                        decoratorNoiseGenerator.noise(
                                (x + 16*chunkX) / decoratorNoiseSettings.getXSizeDiv(),
                                baseLandscapeStart / decoratorNoiseSettings.getYSizeDiv(),
                                (z + 16*chunkZ) / decoratorNoiseSettings.getZSizeDiv(),
                                decoratorNoiseSettings.getOctaves(),
                                decoratorNoiseSettings.getFrequency(),
                                decoratorNoiseSettings.getAmplitude(),
                                decoratorNoiseSettings.isNormalized())  * decoratorNoiseSettings.getMultiplier();

                double absRawPlex = rawPlex + Math.abs(minimalPlex);
                double absApplyRange = settings.getApplyRange() + Math.abs(minimalPlex);
                // Необходимо привести rawPlex в значение от 0 до 1, где 1 - applyRange
                double plex = Math.min(absRawPlex, absApplyRange) / absApplyRange;

                int yPaster = (int) (AncientCavesGenerator.solidStoneHeight + (smoothLayerSize*plex));
                for (int y = yPaster; y >= AncientCavesGenerator.solidStoneHeight; y--) {
                    chunkIn.setBlockState(x, y, z, stone);
                }
            }
        }

        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                BiomeGeneratorSettings settings = BiomeGeneratorSettings.getFromBiome(biomes[x*z]);
                GeneratorSettings baseNoiseSettings = settings.getUpperNoiseGeneratorSettings();
                GeneratorSettings minusNoiseSettings = settings.getDownNoiseGeneratorSettings();
                GeneratorSettings decoratorNoiseSettings = settings.getDecoratorNoiseGeneratorSettings();

                double minimalPlex = 0 - minusNoiseSettings.getMultiplier() - decoratorNoiseSettings.getMultiplier();
                double rawPlex = baseNoiseGenerator.noise(
                        (x + 16*chunkX) / baseNoiseSettings.getXSizeDiv(),
                        baseLandscapeEnd / baseNoiseSettings.getYSizeDiv(),
                        (z + 16*chunkZ) / baseNoiseSettings.getZSizeDiv(),
                        baseNoiseSettings.getOctaves(),
                        baseNoiseSettings.getFrequency(),
                        baseNoiseSettings.getAmplitude(),
                        baseNoiseSettings.isNormalized()) * baseNoiseSettings.getMultiplier() -
                        minusNoiseGenerator.noise(
                                (x + 16*chunkX) / minusNoiseSettings.getXSizeDiv(),
                                baseLandscapeEnd / minusNoiseSettings.getYSizeDiv(),
                                (z + 16*chunkZ) / minusNoiseSettings.getZSizeDiv(),
                                minusNoiseSettings.getOctaves(),
                                minusNoiseSettings.getFrequency(),
                                minusNoiseSettings.getAmplitude(),
                                minusNoiseSettings.isNormalized()) * minusNoiseSettings.getMultiplier() -
                        decoratorNoiseGenerator.noise(
                                (x + 16*chunkX) / decoratorNoiseSettings.getXSizeDiv(),
                                baseLandscapeEnd / decoratorNoiseSettings.getYSizeDiv(),
                                (z + 16*chunkZ) / decoratorNoiseSettings.getZSizeDiv(),
                                decoratorNoiseSettings.getOctaves(),
                                decoratorNoiseSettings.getFrequency(),
                                decoratorNoiseSettings.getAmplitude(),
                                decoratorNoiseSettings.isNormalized())  * decoratorNoiseSettings.getMultiplier();

                double absRawPlex = rawPlex + Math.abs(minimalPlex);
                double absApplyRange = settings.getApplyRange() + Math.abs(minimalPlex);
                // Необходимо привести rawPlex в значение от 0 до 1, где 1 - applyRange
                double plex = Math.min(absRawPlex, absApplyRange) / absApplyRange;

                int yPaster = (int) (baseLandscapeEnd + (smoothLayerSize - (smoothLayerSize*plex)));
                for (int y = yPaster; y <= 256 - AncientCavesGenerator.solidStoneHeight; y++) {
                    chunkIn.setBlockState(x, y, z, stone);
                }
            }
        }

        // Placing nature blocks
        // TODO: Fix air-replace for nature-blocks
        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                boolean trigger = false;
                BiomeGeneratorSettings settings = BiomeGeneratorSettings.getFromBiome(biomes[x*z]);
                for (int y = 256; y >= 0; y--) {
                    IBlockState blockState = chunkIn.getBlockState(x, y, z);
                    if (blockState.equals(air) && !trigger) {
                        trigger = true;
                        continue;
                    }

                    if (blockState.equals(stone) && trigger) {
                        trigger = false;
                        List<IBlockState> blocks;

                        if (y >= waterLevel) {
                            blocks = settings.getSurfaceBlocks().getBlocks();
                        } else {
                            blocks = settings.getUnderwaterBlocks().getBlocks();
                        }

                        int diff = 0;
                        for (IBlockState block : blocks) {
                            if (!chunkIn.getBlockState(x, y - diff, z).equals(air)) {
                                chunkIn.setBlockState(x, y - diff, z, block);
                                diff += 1;
                            } else {
                                break;
                            }
                        }
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

        return chunkIn;
    }


    public void populate(int x, int z, World world, Biome[] biomes, IChunkGenerator generator) {

        boolean flag = false;
        BlockPos blockpos = new BlockPos(x*16, 0, z*16);
        ChunkPos chunkpos = new ChunkPos(x, z);

        Biome biome = biomes[0];

        ForgeEventFactory.onChunkPopulate(true, generator, world, this.rand, x, z, flag);

        if (this.mapFeaturesEnabled) {
            if (this.settings.useMineShafts) {
                this.mineshaftGenerator.generateStructure(world, this.rand, chunkpos);
            }

            if (this.settings.useVillages) {
                flag = this.villageGenerator.generateStructure(world, this.rand, chunkpos);
            }

            if (this.settings.useStrongholds) {
                this.strongholdGenerator.generateStructure(world, this.rand, chunkpos);
            }

            if (this.settings.useTemples) {
                this.scatteredFeatureGenerator.generateStructure(world, this.rand, chunkpos);
            }

            if (this.settings.useMonuments) {
                this.oceanMonumentGenerator.generateStructure(world, this.rand, chunkpos);
            }

//            if (this.settings.useMansions) {
//                this.woodlandMansionGenerator.generateStructure(world, this.rand, chunkpos);
//            }
        }

        BlockFalling.fallInstantly = true;

        biomes[0].decorate(world, rand, new BlockPos(x * 16, 0, z * 16));

        if (this.settings.useDungeons) {
            if (net.minecraftforge.event.terraingen.TerrainGen.populate(generator, world, this.rand, x, z, flag,
                    PopulateChunkEvent.Populate.EventType.DUNGEON)) {
                for (int i = 0; i < this.settings.dungeonChance; i++) {
                    int xt = this.rand.nextInt(16) + 8;
                    int yt = this.rand.nextInt(256);
                    int zt = this.rand.nextInt(16) + 8;
                    (new WorldGenDungeons()).generate(world, this.rand, blockpos.add(xt, yt, zt));
                }
            }
        }

        if (biome != Biomes.DESERT && biome != Biomes.DESERT_HILLS && this.settings.useWaterLakes && !flag &&
                this.rand.nextInt(this.settings.waterLakeChance) == 0) {
            if (net.minecraftforge.event.terraingen.TerrainGen.populate(generator, world, this.rand, x, z, flag,
                    PopulateChunkEvent.Populate.EventType.LAKE)) {
                int xt = this.rand.nextInt(16) + 8;
                int yt = this.rand.nextInt(256);
                int zt = this.rand.nextInt(16) + 8;
                (new WorldGenLakes(Blocks.WATER)).generate(world, this.rand, blockpos.add(xt, yt, zt));
            }
        }

        if (!flag && this.rand.nextInt(this.settings.lavaLakeChance / 10) == 0 && this.settings.useLavaLakes) {
            if (net.minecraftforge.event.terraingen.TerrainGen.populate(generator, world, this.rand, x, z, flag,
                    PopulateChunkEvent.Populate.EventType.LAVA)) {
                int xt = this.rand.nextInt(16) + 8;
                int yt = this.rand.nextInt(this.rand.nextInt(248) + 8);
                int zt = this.rand.nextInt(16) + 8;

                if (yt < world.getSeaLevel() || this.rand.nextInt(this.settings.lavaLakeChance / 8) == 0) {
                    (new WorldGenLakes(Blocks.LAVA)).generate(world, this.rand, blockpos.add(xt, yt, zt));
                }
            }
        }

        int trees = biome.decorator.treesPerChunk*2;

        if (treeDecoratorRandom.nextFloat() < this.extraTreeChance) {
            trees++;
        }

        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(world, treeDecoratorRandom, chunkpos,
                DecorateBiomeEvent.Decorate.EventType.TREE)) {
            for (int tree = 0; tree < trees; tree++) {
                WorldGenAbstractTree worldgenabstracttree = biome.getRandomTreeFeature(treeDecoratorRandom);
                worldgenabstracttree.setDecorationDefaults();

                for (int i = 0; i <= maxTreePlaceAttempts; i++) {
                    int xr = treePosRandom.nextInt(15);
                    int yr = treePosRandom.nextInt(baseLandscapeEnd - waterLevel + 1) + baseLandscapeEnd;
                    int zr = treePosRandom.nextInt(15);
                    boolean generated = false;

                    for (int yp = yr; yp >= waterLevel; yp--) {
                        BlockPos pos = new BlockPos(xr + (chunkpos.x * 16), yp, zr + (chunkpos.z * 16));
                        IBlockState blockState = world.getBlockState(pos);
                        if (blockState.equals(grass)) {
                            worldgenabstracttree.generateSaplings(world, treeDecoratorRandom, pos);
                            generated = true;
                            break;
                        }
                    }

                    if (generated) {
                        break;
                    }
                }
            }
        }

        BlockFalling.fallInstantly = false;
    }
}

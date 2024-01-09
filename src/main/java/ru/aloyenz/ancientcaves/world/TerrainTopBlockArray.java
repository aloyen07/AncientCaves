package ru.aloyenz.ancientcaves.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import java.util.ArrayList;
import java.util.List;

public class TerrainTopBlockArray {

    private final List<IBlockState> blocks;

    private TerrainTopBlockArray(List<IBlockState> blocks) {
        this.blocks = blocks;
    }

    public List<IBlockState> getBlocks() {
        return this.blocks;
    }


    public static TerrainTopBlockArrayBuilder builder() {
        return new TerrainTopBlockArrayBuilder();
    }

    public static class TerrainTopBlockArrayBuilder {

        private final List<IBlockState> blocks;

        private TerrainTopBlockArrayBuilder() {
            this.blocks = new ArrayList<>();
        }

        public TerrainTopBlockArrayBuilder addBlock(Block block) {
            this.blocks.add(block.getDefaultState());
            return this;
        }

        public TerrainTopBlockArrayBuilder addBlocks(Block block, int size) {
            for (int i = 0; i < size; i++) {
                this.blocks.add(block.getDefaultState());
            }
            return this;
        }

        public TerrainTopBlockArray build() {
            return new TerrainTopBlockArray(this.blocks);
        }
    }
}

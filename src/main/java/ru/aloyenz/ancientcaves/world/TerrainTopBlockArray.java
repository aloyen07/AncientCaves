package ru.aloyenz.ancientcaves.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import java.util.ArrayList;
import java.util.List;

public class TerrainTopBlockArray {

    private final List<IBlockState> blocks;

    public TerrainTopBlockArray() {
        blocks = new ArrayList<>();
    }

    public TerrainTopBlockArray addBlock(Block block) {
        this.blocks.add(block.getDefaultState());
        return this;
    }

    public TerrainTopBlockArray addBlocks(Block block, int size) {
        for (int i = 0; i < size; i++) {
            this.blocks.add(block.getDefaultState());
        }
        return this;
    }
}

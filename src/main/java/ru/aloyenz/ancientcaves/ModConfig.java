package ru.aloyenz.ancientcaves;

import net.minecraftforge.common.config.Configuration;

public class ModConfig {

    private final Configuration configuration;

    public ModConfig() {
        this.configuration = new Configuration(AncientCaves.getMcDir());
        // TODO
    }
}

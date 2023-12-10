package ru.aloyenz.ancientcaves;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import ru.aloyenz.ancientcaves.proxy.CommonProxy;
import ru.aloyenz.ancientcaves.world.AncientCavesWorldProvider;

@Mod(modid = AncientCaves.MODID, name = AncientCaves.NAME, version = AncientCaves.VERSION)
public class AncientCaves
{
    public static final String MODID = "ancientcaves";
    public static final String NAME = "Ancient Caves Mod";
    public static final String VERSION = "1.0";

    public static final boolean generateNormalWorld = false;

//    public static final AncientCavesWorldType worldType = new AncientCavesWorldType();

    @SidedProxy(serverSide = "ru.aloyenz.ancientcaves.proxy.ServerProxy",
                clientSide = "ru.aloyenz.ancientcaves.proxy.ClientProxy")
    private static CommonProxy proxy;

    private static Logger logger;

    public static Logger getLogger() {
        return logger;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        logger = event.getModLog();
        AncientCavesWorldProvider.registerIfNeed();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}

package ru.aloyenz.ancientcaves;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.jocl.*;
import ru.aloyenz.ancientcaves.proxy.CommonProxy;
import ru.aloyenz.ancientcaves.world.AncientCavesWorldProvider;
import sun.misc.ClassLoaderUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Mod(modid = AncientCaves.MODID, name = AncientCaves.NAME, version = AncientCaves.VERSION)
public class AncientCaves
{
    public static final String MODID = "ancientcaves";
    public static final String NAME = "Ancient Caves Mod";
    public static final String VERSION = "1.0";


    private static AncientCaves INSTANCE;

    public static AncientCaves getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("Main class is not loaded yet!");
        } else {
            return INSTANCE;
        }
    }


    public static final boolean generateNormalWorld = false;

//    public static final AncientCavesWorldType worldType = new AncientCavesWorldType();

    @SidedProxy(serverSide = "ru.aloyenz.ancientcaves.proxy.ServerProxy",
                clientSide = "ru.aloyenz.ancientcaves.proxy.ClientProxy")
    private static CommonProxy proxy;

    private static Logger logger;

    public static Logger getLogger() {
        return logger;
    }

    // ********* GPU PART ********* //
    public final boolean gpuEnabled;
    public final String perlinNoiseProgram;
    public final cl_context context;
    public final cl_command_queue commandQueue;
    public final cl_device_id device;


    public boolean isGpuEnabled() {
        return gpuEnabled;
    }
    // ******* END GPU PART ******* //

    public AncientCaves() throws IOException {
        super();

        INSTANCE = this;

        this.gpuEnabled = true;

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("perlinNoise.cl");

        InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);

        StringBuilder program = new StringBuilder();
        for (String line; (line = reader.readLine()) != null;) {
            program.append(line).append("\n");
        }

        this.perlinNoiseProgram = program.toString();

        CL.setExceptionsEnabled(true);

        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;
        final long deviceType = CL.CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        // Obtain the number of platforms
        int[] numPlatformsArray = new int[1];
        CL.clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id[] platforms = new cl_platform_id[numPlatforms];
        CL.clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL.CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int[] numDevicesArray = new int[1];
        CL.clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID
        cl_device_id[] devices = new cl_device_id[numDevices];
        CL.clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        device = devices[deviceIndex];

        // Create a context for the selected device
        context = CL.clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);

        // Create a command-queue for the selected device
        commandQueue =
                CL.clCreateCommandQueue(context, device, 0, null);

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

    public static File getMcDir()
    {
        if (Minecraft.getMinecraft().getIntegratedServer() != null && Minecraft.getMinecraft().getIntegratedServer().isDedicatedServer())
        {
            return new File(".");
        }
        return Minecraft.getMinecraft().mcDataDir;
    }
}

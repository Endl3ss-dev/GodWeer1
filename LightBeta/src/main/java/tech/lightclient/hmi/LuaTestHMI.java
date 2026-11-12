package tech.lightclient.hmi;

import tech.lightclient.hmi.resource_controller.LuaAnimationResourceLoader;
import tech.lightclient.hmi.resource_controller.LuaScriptCache;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuaTestHMI {
    public static final String MOD_ID = "holdmyitems";
    public static int swingSpeed = 9;
    public static final Logger LOGGER = LoggerFactory.getLogger("holdmyitems");
    public static MinecraftClient client = MinecraftClient.getInstance();
    public static int age = 0;
    public static int prevAge = 0;
    private static float prevTime = 0.0f;
    public static float deltaTime = 0.0f;
    public static LuaScriptCache handScriptCache;
    public static LuaScriptCache handRelativeScriptCache;
    public static LuaScriptCache itemScriptCache;
    public static HashMap<String, Boolean> renderAsBlock = new HashMap<>();
    public static HashMap<String, Boolean> translateItem = new HashMap<>();
    public static ArrayList<LuaScriptCache> handAddonsCache = new ArrayList<>();
    public static ArrayList<LuaScriptCache> handRelativeAddonsCache = new ArrayList<>();
    public static ArrayList<LuaScriptCache> itemAddonsCache = new ArrayList<>();

    private static boolean initialized = false;
    private static LuaAnimationResourceLoader resourceLoader;

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        resourceLoader = new LuaAnimationResourceLoader();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener((IdentifiableResourceReloadListener) resourceLoader);
        try {
            handScriptCache = new LuaScriptCache("return");
            handRelativeScriptCache = new LuaScriptCache("return");
            itemScriptCache = new LuaScriptCache("return");
            resourceLoader.reload(MinecraftClient.getInstance().getResourceManager());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("[Light Client] HoldMyItems (HMI) engine initialized");
    }

    /**
     * Обновление deltaTime, вызывается каждый кадр из конвейера рендера
     * (замена WorldRenderEvents.START из оригинального мода).
     */
    public static void updateDeltaTime() {
        float currentTime = (float) GLFW.glfwGetTime();
        deltaTime = currentTime - prevTime;
        prevTime = currentTime;
        deltaTime = MinecraftClient.getInstance().isPaused() ? 0.0f : (float) Math.min(0.05, (double) deltaTime);
    }
}

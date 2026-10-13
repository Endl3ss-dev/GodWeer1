package swerve.modules.impl.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;

import swerve.Swerve;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.utils.render.HandOutline;
import swerve.utils.render.HandViewport;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

import org.lwjgl.glfw.GLFW;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class ViewModel extends Module {

    private static final float ARM_X = 0.56F;
    private static final float ARM_Y = -0.52F;
    private static final float ARM_Z = -0.72F;
    private static final float EQUIP_DROP = -0.6F;

    private static final float SCALE_MIN = 0.2F;
    private static final float SCALE_MAX = 5.0F;
    private static final float SCALE_STEP = 0.22F;
    private static final float SCALE_FOLLOW_RATE = 18.0F;
    private static final float POSITION_FOLLOW_RATE = 18.0F;
    private static final float OUTLINE_IN_RATE = 16.0F;
    private static final float OUTLINE_OUT_RATE = 9.0F;
    private static final float MAX_FRAME_DELTA = 0.05F;
    private static final float MAX_OFFSET = 4.0F;

    private static final long DOUBLE_CLICK_NANOS = 300_000_000L;
    private static final float DOUBLE_CLICK_DISTANCE_SQ = 14.0F * 14.0F;

    private static final int CONFIG_SCHEMA = 2;
    private static final long WRITE_DEBOUNCE_NANOS = 400_000_000L;
    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("swerve/viewmodel.json");

    private static boolean eventsRegistered;
    private static ViewModel instance;

    private float mainOffsetX;
    private float mainOffsetY;
    private float offOffsetX;
    private float offOffsetY;

    private float mainTargetX;
    private float mainTargetY;
    private float offTargetX;
    private float offTargetY;

    private float mainScale = 1.0F;
    private float offScale = 1.0F;
    private float mainScaleTarget = 1.0F;
    private float offScaleTarget = 1.0F;

    private InteractionHand activeHand;
    private InteractionHand clickHand;
    private boolean holding;
    private long firstClickNanos;
    private float firstClickX;
    private float firstClickY;

    private float grabCursorX;
    private float grabCursorY;
    private float grabOffsetX;
    private float grabOffsetY;
    private float grabScale;
    private float grabPivotX;
    private float grabPivotY;
    private float grabInv00;
    private float grabInv01;
    private float grabInv10;
    private float grabInv11;
    private boolean grabProjectionValid;

    private HumanoidArm lastMainArm;
    private float outlineAlpha;
    private long lastFrameNanos;
    private int stateRevision;

    private final Object ioLock = new Object();
    private Thread ioThread;
    private boolean savePending;
    private boolean saveImmediate;
    private boolean ioStopping;
    private long saveVersion;
    private float saveMainX;
    private float saveMainY;
    private float saveOffX;
    private float saveOffY;
    private float saveMainScale;
    private float saveOffScale;

    private volatile boolean loadReady;
    private boolean loadConsumed;
    private boolean loadedConfigValid;
    private float loadedMainX;
    private float loadedMainY;
    private float loadedOffX;
    private float loadedOffY;
    private float loadedMainScale;
    private float loadedOffScale;

    public ViewModel() {
        super("ViewModel", "Двойной ЛКМ по руке в чате — перемещение, колесо — размер", ModuleCategory.RENDER);
        instance = this;
        startIoWorker();
        registerEvents();
    }

    public static ViewModel get() {
        return instance;
    }

    private static void registerEvents() {
        if (eventsRegistered) {
            return;
        }
        eventsRegistered = true;

        HudRenderCallback.EVENT.register((graphics, deltaTracker) -> {
            ViewModel module = instance;
            if (module != null) {
                module.onHudRender();
            }
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            ViewModel module = instance;
            if (module != null) {
                module.stopIoWorker();
            }
        });
    }

    @Override
    public void onEnable() {
        super.onEnable();
        consumeLoadedConfig();
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            handleMainArm(minecraft.player.getMainArm());
        }
        lastFrameNanos = System.nanoTime();
    }

    @Override
    public void onDisable() {
        clearEditorState();
        HandOutline.setWanted(false);
        HandViewport.reset();
        requestSave(true);
        super.onDisable();
    }

    public static void beginFrame() {
        ViewModel module = instance;
        if (module == null) {
            HandViewport.beginFrame(false);
            HandOutline.setWanted(false);
            return;
        }

        module.consumeLoadedConfig();
        Minecraft minecraft = Minecraft.getInstance();
        boolean editor = module.editorActive(minecraft);
        HandViewport.beginFrame(editor);
        HandOutline.setWanted(editor);
    }

    public static void applyTransform(PoseStack poseStack, AbstractClientPlayer player, InteractionHand hand,
                                      float equippedProgress) {
        ViewModel module = instance;
        if (module == null || poseStack == null || player == null || hand == null) {
            return;
        }

        module.consumeLoadedConfig();
        if (!module.isEnable()) {
            return;
        }

        module.handleMainArm(player.getMainArm());

        boolean main = hand == InteractionHand.MAIN_HAND;
        HumanoidArm arm = main ? player.getMainArm() : player.getMainArm().getOpposite();
        float side = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        float anchorX = ARM_X * side;
        float anchorY = ARM_Y + Mth.clamp(equippedProgress, 0.0F, 1.0F) * EQUIP_DROP;

        float offsetX = main ? module.mainOffsetX : module.offOffsetX;
        float offsetY = main ? module.mainOffsetY : module.offOffsetY;
        float scale = main ? module.mainScale : module.offScale;

        Minecraft minecraft = Minecraft.getInstance();
        if (module.editorActive(minecraft)) {
            HandViewport.capture(hand, poseStack, anchorX, anchorY, ARM_Z, offsetX, offsetY, scale);
        }

        if (offsetX != 0.0F || offsetY != 0.0F) {
            poseStack.translate(offsetX, offsetY, 0.0F);
        }
        if (Math.abs(scale - 1.0F) > 0.0001F) {
            poseStack.translate(anchorX, anchorY, ARM_Z);
            poseStack.scale(scale, scale, scale);
            poseStack.translate(-anchorX, -anchorY, -ARM_Z);
        }
    }

    public static boolean onMouseButton(int button, int action) {
        ViewModel module = instance;
        if (module == null || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return false;
        }

        module.consumeLoadedConfig();
        Minecraft minecraft = Minecraft.getInstance();
        if (!module.editorActive(minecraft)) {
            module.clearEditorState();
            return false;
        }

        if (action == GLFW.GLFW_RELEASE) {
            if (module.holding) {
                module.updateDrag(minecraft);
                module.holding = false;
                module.requestSave(false);
            }
            return false;
        }
        if (action != GLFW.GLFW_PRESS) {
            return false;
        }

        float mouseX = HandViewport.mouseGuiX(minecraft);
        float mouseY = HandViewport.mouseGuiY(minecraft);
        InteractionHand hovered = module.hoveredHand(mouseX, mouseY);
        if (hovered == null) {
            module.clickHand = null;
            module.firstClickNanos = 0L;
            return false;
        }

        long now = System.nanoTime();
        float clickDx = mouseX - module.firstClickX;
        float clickDy = mouseY - module.firstClickY;
        boolean doubleClick = module.clickHand == hovered
                && now - module.firstClickNanos <= DOUBLE_CLICK_NANOS
                && clickDx * clickDx + clickDy * clickDy <= DOUBLE_CLICK_DISTANCE_SQ;

        if (!doubleClick) {
            module.clickHand = hovered;
            module.firstClickNanos = now;
            module.firstClickX = mouseX;
            module.firstClickY = mouseY;
            return false;
        }

        module.clickHand = null;
        module.firstClickNanos = 0L;
        module.beginGrab(hovered, mouseX, mouseY);
        return true;
    }

    public static boolean onScroll(double vertical) {
        ViewModel module = instance;
        if (module == null || vertical == 0.0D) {
            return false;
        }

        module.consumeLoadedConfig();
        Minecraft minecraft = Minecraft.getInstance();
        if (!module.editorActive(minecraft) || module.activeHand == null) {
            return false;
        }

        float step = vertical > 0.0D ? SCALE_STEP : -SCALE_STEP;
        if (module.activeHand == InteractionHand.MAIN_HAND) {
            module.mainScaleTarget = Mth.clamp(module.mainScaleTarget + step, SCALE_MIN, SCALE_MAX);
        } else {
            module.offScaleTarget = Mth.clamp(module.offScaleTarget + step, SCALE_MIN, SCALE_MAX);
        }
        module.stateRevision++;
        module.requestSave(false);
        return true;
    }

    public void resetLayout() {
        resetLayout(true);
    }

    private void onHudRender() {
        consumeLoadedConfig();

        Minecraft minecraft = Minecraft.getInstance();
        boolean enabled = isEnable() && minecraft.player != null && minecraft.level != null;
        boolean editor = enabled && editorActive(minecraft);

        if (!editor) {
            if (holding) {
                requestSave(false);
            }
            clearEditorState();
        }

        HandOutline.setWanted(editor);
        if (!enabled) {
            HandViewport.reset();
            return;
        }

        handleMainArm(minecraft.player.getMainArm());

        float delta = frameDelta();
        if (editor && holding && activeHand != null) {
            updateDrag(minecraft);
        }

        float positionFactor = expFactor(POSITION_FOLLOW_RATE, delta);
        if (holding && activeHand == InteractionHand.MAIN_HAND) {
            mainOffsetX = mainTargetX;
            mainOffsetY = mainTargetY;
        } else {
            mainOffsetX = follow(mainOffsetX, mainTargetX, positionFactor);
            mainOffsetY = follow(mainOffsetY, mainTargetY, positionFactor);
        }
        if (holding && activeHand == InteractionHand.OFF_HAND) {
            offOffsetX = offTargetX;
            offOffsetY = offTargetY;
        } else {
            offOffsetX = follow(offOffsetX, offTargetX, positionFactor);
            offOffsetY = follow(offOffsetY, offTargetY, positionFactor);
        }

        float scaleFactor = expFactor(SCALE_FOLLOW_RATE, delta);
        mainScale = follow(mainScale, mainScaleTarget, scaleFactor);
        offScale = follow(offScale, offScaleTarget, scaleFactor);

        float outlineTarget = editor && holding ? 1.0F : 0.0F;
        float outlineRate = outlineTarget > outlineAlpha ? OUTLINE_IN_RATE : OUTLINE_OUT_RATE;
        outlineAlpha = follow(outlineAlpha, outlineTarget, expFactor(outlineRate, delta));
        HandOutline.setOutline(outlineAlpha, 2.0F);
        HandOutline.composite();

        if (editor && HandOutline.wantedFor() > 1_000_000_000L) {
            if (!HandOutline.isMaskReady()) {
                HandOutline.reportStale();
            }
            if (!HandViewport.hasAny()) {
                HandViewport.reportStale();
            }
        }
    }

    private InteractionHand hoveredHand(float mouseX, float mouseY) {
        if (HandOutline.isMaskReady()) {
            if (!HandOutline.isHandCoveredAt(mouseX, mouseY)) {
                return null;
            }
            return pickHand(mouseX, mouseY);
        }

        InteractionHand nearest = pickHand(mouseX, mouseY);
        return nearest != null && HandViewport.isNear(nearest, mouseX, mouseY) ? nearest : null;
    }

    private InteractionHand pickHand(float mouseX, float mouseY) {
        boolean mainReady = HandViewport.isReady(InteractionHand.MAIN_HAND);
        boolean offReady = HandViewport.isReady(InteractionHand.OFF_HAND);
        if (mainReady && offReady) {
            float mainDistance = distanceSquared(mouseX, mouseY,
                    HandViewport.anchorX(InteractionHand.MAIN_HAND),
                    HandViewport.anchorY(InteractionHand.MAIN_HAND));
            float offDistance = distanceSquared(mouseX, mouseY,
                    HandViewport.anchorX(InteractionHand.OFF_HAND),
                    HandViewport.anchorY(InteractionHand.OFF_HAND));
            return mainDistance <= offDistance ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        }
        if (mainReady) {
            return InteractionHand.MAIN_HAND;
        }
        if (offReady) {
            return InteractionHand.OFF_HAND;
        }
        return null;
    }

    private void beginGrab(InteractionHand hand, float mouseX, float mouseY) {
        activeHand = hand;
        holding = true;
        grabCursorX = mouseX;
        grabCursorY = mouseY;

        boolean main = hand == InteractionHand.MAIN_HAND;
        grabOffsetX = main ? mainTargetX : offTargetX;
        grabOffsetY = main ? mainTargetY : offTargetY;
        grabScale = Math.max(0.0001F, main ? mainScale : offScale);

        if (HandViewport.isReady(hand)) {
            grabPivotX = HandViewport.anchorX(hand);
            grabPivotY = HandViewport.anchorY(hand);
            grabInv00 = HandViewport.inverse00(hand);
            grabInv01 = HandViewport.inverse01(hand);
            grabInv10 = HandViewport.inverse10(hand);
            grabInv11 = HandViewport.inverse11(hand);
            grabProjectionValid = HandViewport.hasInverse(hand);
        } else {
            grabPivotX = mouseX;
            grabPivotY = mouseY;
            grabProjectionValid = false;
        }
        stateRevision++;
    }

    private void updateDrag(Minecraft minecraft) {
        InteractionHand hand = activeHand;
        if (!holding || hand == null) {
            return;
        }

        float mouseX = HandViewport.mouseGuiX(minecraft);
        float mouseY = HandViewport.mouseGuiY(minecraft);
        float scale = hand == InteractionHand.MAIN_HAND ? mainScale : offScale;
        float scaleRatio = scale / grabScale;

        float screenX = mouseX - grabPivotX - scaleRatio * (grabCursorX - grabPivotX);
        float screenY = mouseY - grabPivotY - scaleRatio * (grabCursorY - grabPivotY);

        float targetX;
        float targetY;
        if (grabProjectionValid) {
            targetX = grabOffsetX + grabInv00 * screenX + grabInv01 * screenY;
            targetY = grabOffsetY + grabInv10 * screenX + grabInv11 * screenY;
        } else {
            float guiHeight = Math.max(1.0F, minecraft.getWindow().getGuiScaledHeight());
            float unitsPerPixel = 3.2F / guiHeight;
            targetX = grabOffsetX + (mouseX - grabCursorX) * unitsPerPixel;
            targetY = grabOffsetY - (mouseY - grabCursorY) * unitsPerPixel;
        }

        targetX = Mth.clamp(targetX, -MAX_OFFSET, MAX_OFFSET);
        targetY = Mth.clamp(targetY, -MAX_OFFSET, MAX_OFFSET);
        if (hand == InteractionHand.MAIN_HAND) {
            mainTargetX = targetX;
            mainTargetY = targetY;
        } else {
            offTargetX = targetX;
            offTargetY = targetY;
        }
    }

    private boolean editorActive(Minecraft minecraft) {
        return isEnable()
                && minecraft.player != null
                && minecraft.level != null
                && minecraft.screen instanceof ChatScreen
                && minecraft.options.getCameraType().isFirstPerson();
    }

    private void handleMainArm(HumanoidArm arm) {
        if (arm == null) {
            return;
        }
        if (lastMainArm == null) {
            lastMainArm = arm;
            return;
        }
        if (lastMainArm != arm) {
            lastMainArm = arm;
            resetLayout(true);
        }
    }

    private void resetLayout(boolean persist) {
        clearEditorState();
        mainOffsetX = 0.0F;
        mainOffsetY = 0.0F;
        offOffsetX = 0.0F;
        offOffsetY = 0.0F;
        mainTargetX = 0.0F;
        mainTargetY = 0.0F;
        offTargetX = 0.0F;
        offTargetY = 0.0F;
        mainScale = 1.0F;
        offScale = 1.0F;
        mainScaleTarget = 1.0F;
        offScaleTarget = 1.0F;
        stateRevision++;
        if (persist) {
            requestSave(false);
        }
    }

    private void clearEditorState() {
        holding = false;
        activeHand = null;
        clickHand = null;
        firstClickNanos = 0L;
        grabProjectionValid = false;
        outlineAlpha = 0.0F;
        HandOutline.setOutline(0.0F, 2.0F);
    }

    private float frameDelta() {
        long now = System.nanoTime();
        if (lastFrameNanos == 0L) {
            lastFrameNanos = now;
            return 0.016F;
        }
        float delta = (float) (now - lastFrameNanos) / 1_000_000_000.0F;
        lastFrameNanos = now;
        return Mth.clamp(delta, 0.0F, MAX_FRAME_DELTA);
    }

    private static float expFactor(float rate, float delta) {
        return 1.0F - (float) Math.exp(-rate * delta);
    }

    private static float follow(float current, float target, float factor) {
        float difference = target - current;
        return Math.abs(difference) < 0.0001F ? target : current + difference * factor;
    }

    private static float distanceSquared(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return dx * dx + dy * dy;
    }

    private void consumeLoadedConfig() {
        if (!loadReady || loadConsumed) {
            return;
        }
        loadConsumed = true;

        if (stateRevision != 0) {
            requestSave(false);
            return;
        }
        if (!loadedConfigValid) {
            resetLayout(false);
            requestSave(false);
            return;
        }

        mainTargetX = loadedMainX;
        mainTargetY = loadedMainY;
        offTargetX = loadedOffX;
        offTargetY = loadedOffY;
        mainOffsetX = mainTargetX;
        mainOffsetY = mainTargetY;
        offOffsetX = offTargetX;
        offOffsetY = offTargetY;
        mainScaleTarget = loadedMainScale;
        offScaleTarget = loadedOffScale;
        mainScale = mainScaleTarget;
        offScale = offScaleTarget;
    }

    private void startIoWorker() {
        ioThread = new Thread(this::ioLoop, "Swerve-ViewModel-Config");
        ioThread.setDaemon(true);
        ioThread.start();
    }

    private void ioLoop() {
        readConfig();

        while (true) {
            float mainX;
            float mainY;
            float offX;
            float offY;
            float mainScaleValue;
            float offScaleValue;

            synchronized (ioLock) {
                while (!savePending && !ioStopping) {
                    try {
                        ioLock.wait();
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                if (!savePending && ioStopping) {
                    return;
                }

                long version = saveVersion;
                long deadline = System.nanoTime() + WRITE_DEBOUNCE_NANOS;
                while (!saveImmediate && version == saveVersion) {
                    long remaining = deadline - System.nanoTime();
                    if (remaining <= 0L) {
                        break;
                    }
                    try {
                        long millis = remaining / 1_000_000L;
                        int nanos = (int) (remaining % 1_000_000L);
                        ioLock.wait(millis, nanos);
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                if (version != saveVersion) {
                    continue;
                }

                mainX = saveMainX;
                mainY = saveMainY;
                offX = saveOffX;
                offY = saveOffY;
                mainScaleValue = saveMainScale;
                offScaleValue = saveOffScale;
                savePending = false;
                saveImmediate = false;
            }

            writeConfig(mainX, mainY, offX, offY, mainScaleValue, offScaleValue);
        }
    }

    private void requestSave(boolean immediate) {
        synchronized (ioLock) {
            saveMainX = mainTargetX;
            saveMainY = mainTargetY;
            saveOffX = offTargetX;
            saveOffY = offTargetY;
            saveMainScale = mainScaleTarget;
            saveOffScale = offScaleTarget;
            savePending = true;
            saveImmediate |= immediate;
            saveVersion++;
            ioLock.notifyAll();
        }
    }

    private void stopIoWorker() {
        requestSave(true);
        synchronized (ioLock) {
            ioStopping = true;
            ioLock.notifyAll();
        }

        Thread worker = ioThread;
        if (worker == null || worker == Thread.currentThread()) {
            return;
        }
        try {
            worker.join(1_500L);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    private void readConfig() {
        boolean valid = false;
        try {
            if (Files.exists(CONFIG_FILE)) {
                String json = Files.readString(CONFIG_FILE, StandardCharsets.UTF_8);
                JsonElement parsed = JsonParser.parseString(json);
                if (parsed.isJsonObject()) {
                    JsonObject object = parsed.getAsJsonObject();
                    if (readInt(object, "schema", -1) == CONFIG_SCHEMA) {
                        loadedMainX = readFiniteFloat(object, "mainX", 0.0F, -MAX_OFFSET, MAX_OFFSET);
                        loadedMainY = readFiniteFloat(object, "mainY", 0.0F, -MAX_OFFSET, MAX_OFFSET);
                        loadedOffX = readFiniteFloat(object, "offX", 0.0F, -MAX_OFFSET, MAX_OFFSET);
                        loadedOffY = readFiniteFloat(object, "offY", 0.0F, -MAX_OFFSET, MAX_OFFSET);
                        loadedMainScale = readFiniteFloat(object, "mainScale", 1.0F, SCALE_MIN, SCALE_MAX);
                        loadedOffScale = readFiniteFloat(object, "offScale", 1.0F, SCALE_MIN, SCALE_MAX);
                        valid = true;
                    }
                }
            }
        } catch (Throwable throwable) {
            Swerve.LOGGER.error("Failed to load ViewModel config", throwable);
        }
        loadedConfigValid = valid;
        loadReady = true;
    }

    private static void writeConfig(float mainX, float mainY, float offX, float offY,
                                    float mainScaleValue, float offScaleValue) {
        try {
            JsonObject object = new JsonObject();
            object.addProperty("schema", CONFIG_SCHEMA);
            object.addProperty("mainX", mainX);
            object.addProperty("mainY", mainY);
            object.addProperty("offX", offX);
            object.addProperty("offY", offY);
            object.addProperty("mainScale", mainScaleValue);
            object.addProperty("offScale", offScaleValue);

            Path parent = CONFIG_FILE.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Path temporary = CONFIG_FILE.resolveSibling(CONFIG_FILE.getFileName() + ".tmp");
            Files.writeString(temporary, object.toString(), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, CONFIG_FILE, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (Throwable atomicMoveUnsupported) {
                Files.move(temporary, CONFIG_FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Throwable throwable) {
            Swerve.LOGGER.error("Failed to save ViewModel config", throwable);
        }
    }

    private static int readInt(JsonObject object, String member, int fallback) {
        try {
            return object.has(member) ? object.get(member).getAsInt() : fallback;
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static float readFiniteFloat(JsonObject object, String member, float fallback, float min, float max) {
        try {
            float value = object.has(member) ? object.get(member).getAsFloat() : fallback;
            return Float.isFinite(value) ? Mth.clamp(value, min, max) : fallback;
        } catch (Throwable ignored) {
            return fallback;
        }
    }
}

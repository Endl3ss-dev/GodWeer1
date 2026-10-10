package swerve.modules.impl.render;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;

import swerve.Swerve;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.utils.animation.AnimationUtils;
import swerve.utils.animation.Easings;
import swerve.utils.render.HandOutline;
import swerve.utils.render.HandProjection;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

import org.joml.Matrix4f;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ViewModel extends Module {

    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("swerve/viewmodel.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final float SCALE_STEP = 0.22F;
    private static final float SCALE_MIN = 0.2F;
    private static final float SCALE_MAX = 5.0F;
    private static final float SCALE_ANIM_SPEED = 10.0F;
    private static final float FOLLOW_SPEED = 10.0F;
    private static final float MAX_FRAME_DELTA = 0.1F;
    private static final float LIFT_SCALE = 1.12F;
    private static final float LIFT_ANIM_SPEED = 8.0F;
    private static final float OUTLINE_IN_SPEED = 2.0F / 0.22F;
    private static final float OUTLINE_OUT_SPEED = 2.0F / 0.3F;
    private static final float OUTLINE_MIN_ALPHA = 0.001F;

    private static ViewModel instance;

    private final HandTransform main = new HandTransform();
    private final HandTransform off = new HandTransform();
    private final Matrix4f grabInverse = new Matrix4f();
    private final float[] hit = new float[3];
    private final AnimationUtils outlineAnim = new AnimationUtils(0.0F, OUTLINE_IN_SPEED, Easings.CUBIC_OUT);

    private boolean loaded;
    private boolean editing;
    private HumanoidArm lastMainArm;
    private long lastFrameNanos;

    private InteractionHand dragHand;
    private InteractionHand outlineHand;
    private float grabPlaneZ;
    private float grabHitX;
    private float grabHitY;
    private float grabOffsetX;
    private float grabOffsetY;
    private float grabScale = 1.0F;
    private float grabPivotX;
    private float grabPivotY;

    public ViewModel() {
        super("ViewModel", "Чат: 2×ЛКМ по руке — режим расположения. В нём: зажать ЛКМ — двигать руку, колесо — размер, ПКМ — анимация", ModuleCategory.RENDER);
        instance = this;
    }

    public static ViewModel get() {
        return instance;
    }

    public static boolean isActive() {
        ViewModel module = instance;
        return module != null && module.isEnable();
    }

    public static void beginHandFrame() {
        HandProjection.beginFrame();
        HandOutline.beginFrame();
        ViewModel module = instance;
        if (module != null && module.isEnable()) {
            module.onFrame();
        }
    }

    public static void endHandFrame() {
        ViewModel module = instance;
        if (module != null && module.isEnable() && module.editing) {
            HandProjection.probe();
        }
        HandOutline.composite();
    }

    public static void applyOffset(PoseStack poseStack, InteractionHand hand) {
        ViewModel module = instance;
        if (module == null || !module.isEnable() || hand == null) {
            return;
        }
        if (module.editing) {
            HandProjection.captureBase(hand, poseStack);
        }
        HandTransform transform = module.transform(hand);
        if (transform.offsetX != 0.0F || transform.offsetY != 0.0F) {
            poseStack.translate(transform.offsetX, transform.offsetY, 0.0F);
        }
    }

    public static void applyScale(PoseStack poseStack, InteractionHand hand) {
        ViewModel module = instance;
        if (module == null || !module.isEnable() || hand == null) {
            return;
        }
        if (module.editing) {
            HandProjection.capturePivot(hand, poseStack);
        }
        float scale = module.transform(hand).effectiveScale();
        if (scale != 1.0F) {
            poseStack.scale(scale, scale, scale);
        }
    }

    public static boolean suppressEatAnimation() {
        return isActive();
    }

    public static float outlineAlpha(InteractionHand hand) {
        ViewModel module = instance;
        if (module == null || !module.isEnable() || hand == null || module.outlineHand != hand) {
            return 0.0F;
        }
        return Mth.clamp(module.outlineAnim.getValue(), 0.0F, 1.0F);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        load();
        lastFrameNanos = 0L;
        outlineHand = null;
        outlineAnim.setValue(0.0F);
    }

    @Override
    public void onDisable() {
        if (dragHand != null) {
            endDrag();
        }
        editing = false;
        super.onDisable();
    }

    private void onFrame() {
        load();
        handleMainArmSwap();

        long now = System.nanoTime();
        float delta = lastFrameNanos == 0L ? 0.0F : Math.min(MAX_FRAME_DELTA, (now - lastFrameNanos) / 1.0E9F);
        lastFrameNanos = now;

        editing = Minecraft.getInstance().screen instanceof ChatScreen;
        if (!editing && dragHand != null) {
            endDrag();
        }
        if (dragHand != null) {
            updateDrag();
        }

        // Экспоненциальное догоняние цели: рука отстаёт от курсора и плавно доезжает после отпускания.
        float follow = 1.0F - (float) Math.exp(-FOLLOW_SPEED * delta);
        main.follow(follow);
        off.follow(follow);
        main.scale.update(main.scaleTarget);
        off.scale.update(off.scaleTarget);
        main.lift.update(dragHand == InteractionHand.MAIN_HAND ? LIFT_SCALE : 1.0F);
        off.lift.update(dragHand == InteractionHand.OFF_HAND ? LIFT_SCALE : 1.0F);
        updateOutline();
    }

    private void updateOutline() {
        boolean holding = dragHand != null;
        if (holding) {
            outlineHand = dragHand;
        }
        float target = holding ? 1.0F : 0.0F;
        if (outlineAnim.getTarget() != target) {
            outlineAnim.setSpeed(holding ? OUTLINE_IN_SPEED : OUTLINE_OUT_SPEED);
        }
        outlineAnim.update(target);
        if (!holding && outlineAnim.getValue() <= OUTLINE_MIN_ALPHA) {
            outlineHand = null;
        }
    }

    // Захваченная точка должна остаться под курсором и при смене масштаба во время перетаскивания:
    // offset = hit - T - r * (P - offset0 - T), где T — пивот без offset, r — отношение масштабов.
    private void updateDrag() {
        if (!HandProjection.rayPlane(grabInverse, HandProjection.cursorNdcX(), HandProjection.cursorNdcY(), grabPlaneZ, hit)) {
            return;
        }
        HandTransform transform = transform(dragHand);
        float ratio = grabScale > 1.0E-4F ? transform.effectiveScale() / grabScale : 1.0F;
        float pivotX = grabPivotX - grabOffsetX;
        float pivotY = grabPivotY - grabOffsetY;
        transform.targetX = hit[0] - pivotX - ratio * (grabHitX - grabOffsetX - pivotX);
        transform.targetY = hit[1] - pivotY - ratio * (grabHitY - grabOffsetY - pivotY);
    }

    public boolean beginDrag(InteractionHand hand) {
        if (!isEnable() || hand == null) {
            return false;
        }
        if (!HandProjection.copyBaseInverse(hand, grabInverse) || !HandProjection.unprojectProbe(grabInverse, hit)) {
            return false;
        }
        grabPlaneZ = hit[2];
        if (!HandProjection.rayPlane(grabInverse, HandProjection.probeNdcX(), HandProjection.probeNdcY(), grabPlaneZ, hit)) {
            return false;
        }
        grabHitX = hit[0];
        grabHitY = hit[1];

        HandTransform transform = transform(hand);
        transform.targetX = transform.offsetX;
        transform.targetY = transform.offsetY;
        grabOffsetX = transform.offsetX;
        grabOffsetY = transform.offsetY;
        grabScale = transform.effectiveScale();

        if (HandProjection.pivotLocal(hand, hit)) {
            grabPivotX = hit[0];
            grabPivotY = hit[1];
        } else {
            grabPivotX = grabHitX;
            grabPivotY = grabHitY;
        }
        dragHand = hand;
        return true;
    }

    public void endDrag() {
        if (dragHand == null) {
            return;
        }
        dragHand = null;
        save();
    }

    public boolean isDragging() {
        return dragHand != null;
    }

    public InteractionHand getDragHand() {
        return dragHand;
    }

    public void scroll(InteractionHand hand, double amount) {
        if (hand == null || amount == 0.0) {
            return;
        }
        load();
        HandTransform transform = transform(hand);
        transform.scaleTarget = Mth.clamp(transform.scaleTarget + (amount > 0.0 ? SCALE_STEP : -SCALE_STEP), SCALE_MIN, SCALE_MAX);
        save();
    }

    public void resetHand(InteractionHand hand) {
        load();
        HandTransform transform = transform(hand);
        transform.targetX = 0.0F;
        transform.targetY = 0.0F;
        transform.scaleTarget = 1.0F;
        if (dragHand == hand) {
            dragHand = null;
        }
        save();
    }

    public void resetAll() {
        resetHand(InteractionHand.MAIN_HAND);
        resetHand(InteractionHand.OFF_HAND);
    }

    private HandTransform transform(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? main : off;
    }

    private void handleMainArmSwap() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        HumanoidArm arm = player.getMainArm();
        if (lastMainArm == null) {
            lastMainArm = arm;
            return;
        }
        if (arm == lastMainArm) {
            return;
        }
        lastMainArm = arm;
        main.targetX = -main.targetX;
        main.offsetX = main.targetX;
        off.targetX = -off.targetX;
        off.offsetX = off.targetX;
        save();
    }

    private void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        if (!Files.isRegularFile(FILE)) {
            return;
        }
        try {
            JsonElement root = JsonParser.parseString(Files.readString(FILE, StandardCharsets.UTF_8));
            if (!root.isJsonObject()) {
                return;
            }
            JsonObject json = root.getAsJsonObject();
            main.set(readFloat(json, "mainX", 0.0F), readFloat(json, "mainY", 0.0F),
                    Mth.clamp(readFloat(json, "mainScale", 1.0F), SCALE_MIN, SCALE_MAX));
            off.set(readFloat(json, "offX", 0.0F), readFloat(json, "offY", 0.0F),
                    Mth.clamp(readFloat(json, "offScale", 1.0F), SCALE_MIN, SCALE_MAX));
        } catch (Exception exception) {
            Swerve.LOGGER.error("Failed to load viewmodel config", exception);
        }
    }

    private void save() {
        JsonObject json = new JsonObject();
        json.addProperty("mainX", main.targetX);
        json.addProperty("mainY", main.targetY);
        json.addProperty("offX", off.targetX);
        json.addProperty("offY", off.targetY);
        json.addProperty("mainScale", main.scaleTarget);
        json.addProperty("offScale", off.scaleTarget);
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(json), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            Swerve.LOGGER.error("Failed to save viewmodel config", exception);
        }
    }

    private static float readFloat(JsonObject json, String key, float fallback) {
        try {
            return json.has(key) ? json.get(key).getAsFloat() : fallback;
        } catch (RuntimeException exception) {
            return fallback;
        }
    }

    private static final class HandTransform {

        private final AnimationUtils scale = new AnimationUtils(1.0F, SCALE_ANIM_SPEED, Easings.CUBIC_OUT);
        private final AnimationUtils lift = new AnimationUtils(1.0F, LIFT_ANIM_SPEED, Easings.CUBIC_OUT);

        private float offsetX;
        private float offsetY;
        private float targetX;
        private float targetY;
        private float scaleTarget = 1.0F;

        private void set(float x, float y, float scaleValue) {
            this.offsetX = x;
            this.targetX = x;
            this.offsetY = y;
            this.targetY = y;
            this.scaleTarget = scaleValue;
            this.scale.setValue(scaleValue);
        }

        private float effectiveScale() {
            return this.scale.getValue() * this.lift.getValue();
        }

        private void follow(float factor) {
            this.offsetX += (this.targetX - this.offsetX) * factor;
            this.offsetY += (this.targetY - this.offsetY) * factor;
        }
    }
}

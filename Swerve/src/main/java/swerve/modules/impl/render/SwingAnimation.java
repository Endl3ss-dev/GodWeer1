package swerve.modules.impl.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.phys.EntityHitResult;

public final class SwingAnimation extends Module {

    public static final String[] MODES = {
            "Взмах", "Сдвиг", "Слом", "Выпад", "Вниз", "Бонк", "Круг"
    };

    private static final int SWING = 0;
    private static final int SHIFT = 1;
    private static final int BREAK = 2;
    private static final int LUNGE = 3;
    private static final int DOWN = 4;
    private static final int BONK = 5;
    private static final int CIRCLE = 6;

    private static final int VANILLA_SWING_TICKS = 6;
    private static final long MIN_PREVIEW_INTERVAL_MS = 1000L;

    private static SwingAnimation instance;

    private final ModeSetting type = new ModeSetting("Тип", MODES[SWING], MODES);
    private final FloatSetting strength = new FloatSetting("Сила взмаха", 1.0F, 0.5F, 3.0F, 0.05F);
    private final FloatSetting speed = new FloatSetting("Скорость", 1.0F, 0.25F, 2.0F, 0.05F);
    private final BooleanSetting onlyOnTarget = new BooleanSetting("Только при наводке", false);
    private final BooleanSetting preview = new BooleanSetting("Превью в чате", true);

    private long nextPreviewAt;
    private long previewUntil;

    public SwingAnimation() {
        super("SwingAnimation", "Кастомные анимации взмаха от первого лица", ModuleCategory.RENDER);
        addSettings(type, strength, speed, onlyOnTarget, preview);
        instance = this;
    }

    public static SwingAnimation get() {
        return instance;
    }

    public ModeSetting getType() {
        return type;
    }

    public FloatSetting getStrength() {
        return strength;
    }

    public FloatSetting getSpeed() {
        return speed;
    }

    @Override
    public void onDisable() {
        nextPreviewAt = 0L;
        previewUntil = 0L;
        super.onDisable();
    }

    public void select(int index) {
        if (index < 0 || index >= MODES.length) {
            return;
        }
        type.set(MODES[index]);
        if (!isEnable()) {
            setEnabled(true);
        }
        nextPreviewAt = 0L;
    }

    // Вне взмаха режимы с позой покоя держат руку в стартовой точке анимации (progress = 0),
    // поэтому удар начинается без рывка из ванильной позы.
    public static boolean apply(PoseStack poseStack, InteractionHand hand, float progress, float equippedProgress) {
        if (hand != InteractionHand.MAIN_HAND) {
            return false;
        }
        SwingAnimation module = instance;
        if (module == null || !module.isEnable()) {
            return false;
        }
        if (progress <= 0.0F || progress >= 1.0F) {
            if (!module.hasRestPose()) {
                return false;
            }
            progress = 0.0F;
        }
        return module.applyAnimation(poseStack, progress, equippedProgress);
    }

    private boolean hasRestPose() {
        int mode = type.getIndex();
        return mode == SHIFT || mode == BREAK || mode == CIRCLE;
    }

    public static int modifySwingDuration(int vanillaTicks) {
        SwingAnimation module = instance;
        if (module == null || !module.isEnable()) {
            return vanillaTicks;
        }
        return Math.max(1, Math.round(vanillaTicks / module.speed.get()));
    }

    // Локальный взмах для превью: только клиентские поля, пакет на сервер не уходит.
    public static void previewTick() {
        SwingAnimation module = instance;
        if (module == null || !module.isEnable() || !module.preview.get()) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now < module.nextPreviewAt) {
            return;
        }
        long swingMs = modifySwingDuration(VANILLA_SWING_TICKS) * 50L;
        module.nextPreviewAt = now + Math.max(MIN_PREVIEW_INTERVAL_MS, swingMs + 250L);
        module.previewUntil = now + swingMs + 100L;
        player.swingTime = -1;
        player.swinging = true;
        player.swingingArm = InteractionHand.MAIN_HAND;
    }

    private boolean applyAnimation(PoseStack poseStack, float progress, float equippedProgress) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return false;
        }

        Item item = player.getMainHandItem().getItem();
        if (item instanceof TridentItem || item instanceof CrossbowItem) {
            return false;
        }
        if (player.isUsingItem() && player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
            return false;
        }
        if (onlyOnTarget.get() && System.currentTimeMillis() >= previewUntil
                && !(mc.hitResult instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity)) {
            return false;
        }

        int side = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        float squareSin = Mth.sin(progress * progress * Mth.PI);
        float rootSin = Mth.sin(Mth.sqrt(progress) * Mth.PI);
        float sin = Mth.sin(progress * Mth.PI);
        float power = strength.get();

        poseStack.translate(side * 0.56F, -0.52F + equippedProgress * -0.6F, -0.72F);

        switch (type.getIndex()) {
            case SHIFT -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(side * 90.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(side * -60.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F - 20.0F * sin * power));
            }
            case BREAK -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(side * 90.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(side * -30.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F - 20.0F * sin * power));
            }
            case LUNGE -> {
                poseStack.translate(0.0F, 0.0F, -sin * 0.2F);
                poseStack.mulPose(Axis.YP.rotationDegrees(side * 45.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(side * squareSin * -20.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(side * rootSin * -20.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(rootSin * -Mth.clamp(power * 55.0F, 55.0F, 80.0F)));
                poseStack.mulPose(Axis.YP.rotationDegrees(side * 10.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(-80.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(side * 60.0F));
            }
            case DOWN -> {
                poseStack.translate(-sin * 0.5F, sin * 0.25F, 0.0F);
                poseStack.mulPose(Axis.YP.rotationDegrees(side * 65.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(-110.0F - 80.0F * sin));
            }
            case BONK -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(15.0F * side));
                poseStack.mulPose(Axis.XN.rotationDegrees(95.0F * easeOutElastic(progress * progress)));
            }
            case CIRCLE -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(15.0F * side));
                float eased = easeOutBack(Mth.lerp(1.0F - progress, progress, progress * 0.5F));
                poseStack.mulPose(Axis.XN.rotationDegrees(360.0F * eased + 45.0F));
            }
            default -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(side * (45.0F + squareSin * -20.0F)));
                poseStack.mulPose(Axis.ZP.rotationDegrees(side * rootSin * -20.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(rootSin * -power * 22.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(side * -45.0F));
            }
        }
        return true;
    }

    private static float easeOutBack(float value) {
        float c1 = 1.70158F;
        float c3 = c1 + 1.0F;
        float t = value - 1.0F;
        return 1.0F + c3 * t * t * t + c1 * t * t;
    }

    private static float easeOutElastic(float value) {
        if (value == 0.0F) {
            return 0.0F;
        }
        if (value == 1.0F) {
            return 1.0F;
        }
        return (float) (Math.pow(2.0, -10.0F * value) * Math.sin((value * 10.0F - 0.75) * 2.0943951023931953) + 1.0);
    }
}

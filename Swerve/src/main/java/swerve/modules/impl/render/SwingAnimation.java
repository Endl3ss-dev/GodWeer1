package swerve.modules.impl.render;

import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.SwingMode;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.HumanoidArm;

/**
 * Единая точка входа для всех анимаций взмаха руки.
 * Список режимов берётся из SwingMode.values(), поэтому повториться в настройках не может.
 */
public final class SwingAnimation extends Module {

    private static final String DISPLAY = "Animation";

    /** Миксин читает это поле каждый кадр — без поиска по менеджеру. */
    private static volatile SwingAnimation active;

    private final ModeSetting animation = new ModeSetting(
            DISPLAY,
            SwingMode.VANILLA.displayName(),
            SwingMode.displayNames());

    private final FloatSetting strength = new FloatSetting("Strength", 1.0F, 0.1F, 3.0F, 0.01F)
            .visible(() -> this.custom() && !this.animation.is(SwingMode.PANDER.displayName()));

    private final FloatSetting corner = new FloatSetting("Corner", 12.0F, 1.0F, 360.0F, 1.0F)
            .visible(() -> this.animation.is(SwingMode.DROP_DOWN.displayName()));

    private final FloatSetting slant = new FloatSetting("Slant", 12.0F, 1.0F, 360.0F, 1.0F)
            .visible(() -> this.animation.is(SwingMode.DROP_DOWN.displayName()));

    private final BooleanSetting mainHandOnly = new BooleanSetting("Main hand only", true);

    private volatile SwingMode mode = SwingMode.VANILLA;

    public SwingAnimation() {
        super("Swing Animation", "Single entry point for every hand swing animation", ModuleCategory.RENDER);
        this.addSettings(this.animation, this.strength, this.corner, this.slant, this.mainHandOnly);
        this.syncMode();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.syncMode();
        active = this;
    }

    @Override
    public void onDisable() {
        active = null;
        super.onDisable();
    }

    @Override
    public void onTick() {
        this.syncMode();
    }

    private void syncMode() {
        this.mode = SwingMode.byDisplayName(this.animation.getCurrent());
    }

    private boolean custom() {
        return this.mode != SwingMode.VANILLA;
    }

    /**
     * Вызывается миксином из ItemInHandRenderer#swingArm.
     * @return true, если анимация применена и ванильный взмах не нужен.
     */
    public static boolean transform(PoseStack poseStack, float attack, float equip, int invert, HumanoidArm arm) {
        SwingAnimation module = active;
        if (module == null) {
            return false;
        }
        SwingMode mode = module.mode;
        if (mode.isVanilla()) {
            return false;
        }
        if (module.mainHandOnly.isState() && !module.isMainHand(arm)) {
            return false;
        }
        mode.apply(poseStack, attack, equip, invert, module.strength.get(), module.corner.get(), module.slant.get());
        return true;
    }

    private boolean isMainHand(HumanoidArm arm) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft == null ? null : minecraft.player;
        return player == null || player.getMainArm() == arm;
    }
}

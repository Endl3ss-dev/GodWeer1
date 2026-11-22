package tech.lightclient.modules.impl.render;

import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.player.SwingUtils;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import static tech.lightclient.client.utils.player.SwingUtils.*;

@FunctionAdd(name = "SwingAnimations", alias = "Swing Animations", category = Category.Render, description = "Красивые и удобные анимации удара руки")
public class SwingAnimations extends Function {
    public ModeSetting mode = new ModeSetting("Анимация", "Дуга", "Дуга", "Флик", "Удар", "Вихрь", "Взмах", "Стандарт");
    public SliderSetting swingPower = new SliderSetting("Сила", 9f, 4f, 30f, 0.1f);
    public SliderSetting swingSpeed = new SliderSetting("Скорость", 5.0f, 1f, 15.0f, 0.1f);
    public SliderSetting rotationX = new SliderSetting("Поворот X", 0f, -180f, 180f, 0.1f);
    public SliderSetting rotationZ = new SliderSetting("Поворот Z", 0f, -180f, 180f, 0.1f);
    public BooleanSetting onlyAura = new BooleanSetting("Только с аурой", false);
    public BooleanSetting smooth = new BooleanSetting("Плавная анимация", true);
    public BooleanSetting invert = new BooleanSetting("Инверсия", false);

    public SwingAnimations() {
        addSettings(mode, swingPower, swingSpeed, rotationX, rotationZ, onlyAura, smooth, invert);
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (mc.world == null)
            return;
        if (SwingUtils.getSwingAnimations().onlyAura.get() &&
                !SwingUtils.hasCombatTargetForSwing())
            return;
    }

    public boolean shouldRenderForTarget() {
        if (!onlyAura.get()) return true;
        return SwingUtils.hasCombatTargetForSwing();
    }

    // ==== Кривые ====

    private static float easeInOut(float t) {
        return t * t * (3f - 2f * t);
    }

    private static float easeOutQuad(float t) {
        return 1f - (1f - t) * (1f - t);
    }

    /** Общая амплитуда пресета из слайдера "Сила". */
    private float amp() {
        return MathHelper.clamp(swingPower.get() / 9f, 0.45f, 2.2f);
    }

    /**
     * Базовая дуга разряда: нулевая в покое и в конце замаха, поэтому рука
     * всегда возвращается в боевую стойку. warp ускоряет атакующую фазу.
     */
    private static float arc(float p, float attackSpeed) {
        float warped = (float) Math.pow(MathHelper.clamp(p, 0f, 1f), attackSpeed);
        return MathHelper.sin(warped * (float) Math.PI);
    }

    public void renderSwordAnimation(MatrixStack matrices, float f, float swingProgress, float equipProgress, Arm arm, boolean mainHand, Runnable viewModelTransform) {
        if (!mainHand) {
            applyEquipOffset(matrices, arm, equipProgress);
            applySwingOffset(matrices, arm, swingProgress);
            viewModelTransform.run();
            return;
        }

        int side = arm == Arm.RIGHT ? 1 : -1;
        int inv = invert.get() ? -1 : 1;

        float p = MathHelper.clamp(swingProgress, 0f, 1f);
        if (smooth.get()) {
            p = easeInOut(p);
        }

        applyEquipOffset(matrices, arm, 0);
        float A = amp();

        switch (mode.get()) {
            // Широкий горизонтальный слэш: резкая атака, мягкий возврат в стойку
            case "Дуга" -> {
                float q = arc(p, 0.7f) * inv;

                matrices.translate(side * 0.14f * q, -0.05f * q, -0.10f * q);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-46f * q * A));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * -72f * q * A));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * 20f * q * A));
                viewModelTransform.run();
            }

            // Лёгкое движение кистью: минимально перекрывает экран, максимально удобен в бою
            case "Флик" -> {
                float q = arc(p, 1.0f) * inv;

                matrices.translate(0, -0.02f * q, -0.06f * q);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22f * q * A));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * 12f * q));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * -28f * q * A));
                viewModelTransform.run();
            }

            // Прямой тычок вперёд: быстрый выброс, возврат и короткая пауза в стойке
            case "Удар" -> {
                float warped = (float) Math.pow(Math.min(p * 1.35f, 1f), 0.85f);
                float q = MathHelper.sin(warped * (float) Math.PI) * inv;

                matrices.translate(0, -0.03f * q, -0.24f * q * A);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-14f * q * A));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * -10f * q));
                viewModelTransform.run();
            }

            // Переворот предмета вокруг оси: эффектный флип с возвратом в хват
            case "Вихрь" -> {
                float q = arc(p, 0.9f) * inv;
                float roll = easeOutQuad(MathHelper.clamp(p, 0f, 1f));

                matrices.translate(side * 0.06f * q, -0.04f * q, -0.05f * q);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-26f * q * A));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * 36f * q * A));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * 165f * q * A * (0.6f + 0.4f * roll)));
                viewModelTransform.run();
            }

            // Рубящий взмах сверху вниз
            case "Взмах" -> {
                float q = arc(p, 0.8f) * inv;

                matrices.translate(0, -0.09f * q, -0.07f * q);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-62f * q * A));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * -14f * q * A));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * 34f * q * A));
                viewModelTransform.run();
            }

            // Прибрённая ванилла: та же кривая, но без рывков и с лёгким выносом вперёд
            case "Стандарт" -> {
                float fSin = MathHelper.sin(p * p * (float) Math.PI) * inv;
                float gSin = MathHelper.sin(MathHelper.sqrt(p) * (float) Math.PI) * inv;

                matrices.translate(0, 0, -0.04f * gSin);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * -14f * fSin * A));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * -11f * gSin * A));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(gSin * -42f * A));
                viewModelTransform.run();
            }
        }

        // Пользовательская докрутка поверх любого пресета
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationX.get()));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationZ.get()));
    }
}

package swerve.modules.api;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.util.Mth;

/**
 * Все анимации взмаха руки, собранные из клиентов репозитория в один набор.
 *
 * Дубликаты в настройках исключены на уровне языка: константы enum не могут
 * повторяться, а список для ModeSetting строится из values().
 *
 * apply() вызывается каждый кадр для каждой руки, поэтому никаких аллокаций
 * внутри — только примитивы и уже существующий PoseStack.
 *
 * attack  — прогресс взмаха 0..1 (в Mojang-маппингах параметр swingArm)
 * equip   — обратная высота руки (inverseArmHeight)
 * invert  — +1 для правой руки, -1 для левой
 */
public enum SwingMode {

    /** Ванильный взмах: миксин не перехватывает вызов. */
    VANILLA("Vanilla") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
        }
    },

    /** AlphaDLC "Smooth" == Pouch "Smooth". */
    SMOOTH("Smooth") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float f = Mth.sin(attack * attack * (float) Math.PI);
            poseStack.translate(0.56F * invert, -0.52F, -0.72F);
            poseStack.mulPose(Axis.YP.rotationDegrees(invert * (45.0F + f * -20.0F * strength)));
            float g = Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
            poseStack.mulPose(Axis.ZP.rotationDegrees(invert * g * -20.0F * strength));
            poseStack.mulPose(Axis.XP.rotationDegrees(g * -80.0F * strength));
            poseStack.mulPose(Axis.YP.rotationDegrees(invert * -45.0F));
        }
    },

    /** Pouch "Static". */
    STATIC("Static") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float sin2 = Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
            poseStack.translate(invert * 0.56F, -0.42F, -0.72F);
            poseStack.mulPose(Axis.XP.rotationDegrees(sin2 * -60.0F * strength));
            poseStack.translate(0.0F, -0.1F, 0.0F);
        }
    },

    /** AlphaDLC "Down" == Pouch "Down". */
    DOWN("Down") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float sin2 = Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
            poseStack.translate(invert * 0.56F, -0.32F, -0.72F);
            poseStack.mulPose(Axis.YP.rotationDegrees(76.0F * invert));
            poseStack.mulPose(Axis.YP.rotationDegrees(sin2 * -5.0F * strength));
            poseStack.mulPose(Axis.XN.rotationDegrees(sin2 * -100.0F * strength));
            poseStack.mulPose(Axis.XP.rotationDegrees(sin2 * -155.0F * strength));
            poseStack.mulPose(Axis.XP.rotationDegrees(-100.0F));
        }
    },

    /** Pouch "Akrien": Down + доборот и смещение. */
    AKRIEN("Akrien") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float sin1 = Mth.sin(attack * attack * (float) Math.PI);
            float sin2 = Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
            poseStack.translate(invert * 0.65F, -0.32F, -0.72F);
            poseStack.mulPose(Axis.YP.rotationDegrees(76.0F * invert));
            poseStack.mulPose(Axis.YP.rotationDegrees(sin2 * -5.0F * strength));
            poseStack.mulPose(Axis.XN.rotationDegrees(sin2 * -100.0F * strength));
            poseStack.mulPose(Axis.XP.rotationDegrees(sin2 * -155.0F * strength));
            poseStack.mulPose(Axis.XP.rotationDegrees(-100.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(sin2 * 25.0F * strength));
            poseStack.mulPose(Axis.XN.rotationDegrees(sin2 * -25.0F * strength));
            poseStack.mulPose(Axis.XN.rotationDegrees(sin1 * 15.0F * strength));
            poseStack.translate(sin2 * 0.18F * strength, sin2 * 0.59F * strength, 0.0F);
        }
    },

    /** Pouch "Poke". */
    POKE("Poke") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float anim = Mth.sin(attack * (float) (Math.PI / 2.0) * 2.0F);
            float tilt = strength / 3.0F;
            poseStack.translate(invert * 0.56F, -0.52F, -0.72F);
            poseStack.translate(0.0F, 0.0F, tilt * -anim);
            poseStack.mulPose(Axis.YP.rotationDegrees(75.0F * invert));
            poseStack.mulPose(Axis.ZP.rotationDegrees((-75.0F * (strength / 4.0F) * anim - 60.0F) * invert));
            poseStack.mulPose(Axis.XP.rotationDegrees(-75.0F));
        }
    },

    /** Pouch "SelfBack" == AlphaDLC "Self". */
    SELF_BACK("SelfBack") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float anim = Mth.sin(attack * (float) (Math.PI / 2.0) * 2.0F);
            poseStack.translate(0.65F * invert, -0.30F, -0.80F);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F * invert));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-70.0F * invert));
            poseStack.mulPose(Axis.XP.rotationDegrees(-100.0F - 60.0F * strength * anim));
        }
    },

    /** Pouch "Feast". */
    FEAST("Feast") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float sin2 = Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
            poseStack.translate(invert * 0.56F, -0.32F, -0.72F);
            poseStack.mulPose(Axis.YP.rotationDegrees(30.0F * invert));
            poseStack.mulPose(Axis.YP.rotationDegrees(sin2 * 75.0F * invert * strength));
            poseStack.mulPose(Axis.XP.rotationDegrees(sin2 * -65.0F * strength));
            poseStack.mulPose(Axis.YP.rotationDegrees(30.0F * invert));
            poseStack.mulPose(Axis.XP.rotationDegrees(-80.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(35.0F * invert));
        }
    },

    /** Pouch "ToBack". */
    TO_BACK("ToBack") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float g = Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
            poseStack.translate(0.65F * invert, -0.45F, -0.90F);
            poseStack.mulPose(Axis.XP.rotationDegrees(50.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees((-30.0F * (1.0F - g * strength) - 30.0F) * invert));
            poseStack.mulPose(Axis.ZP.rotationDegrees(110.0F * invert));
        }
    },

    /** Pouch "Break". */
    BREAK("Break") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float sin2 = Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
            poseStack.translate(0.66F * invert, -0.30F, -0.38F);
            poseStack.mulPose(Axis.YP.rotationDegrees(270.0F * invert));
            poseStack.mulPose(Axis.XP.rotationDegrees(sin2 * 10.0F * strength));
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.translate(-0.1F * invert, 0.2F, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-10.0F * invert));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(-105.0F * invert));
        }
    },

    /** Pouch "Slant" == SkyCore "Slant". */
    SLANT("Slant") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float anim = Mth.sin(attack * (float) (Math.PI / 2.0) * 2.0F);
            float rotate = 35.0F * strength;
            poseStack.translate(invert * 0.56F, -0.52F, -0.72F);
            poseStack.translate(0.0F, 0.0F, -0.30F * anim * strength);
            poseStack.mulPose(Axis.XP.rotationDegrees(anim * -rotate));
            poseStack.mulPose(Axis.ZP.rotationDegrees(anim * rotate));
        }
    },

    /** Pouch "DropDown" — использует corner и slant. */
    DROP_DOWN("DropDown") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float anim = Mth.sin(attack * (float) (Math.PI / 2.0) * 2.0F);
            poseStack.translate(invert * 0.56F, -0.52F, -0.72F);
            poseStack.mulPose(Axis.YP.rotationDegrees(80.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(corner));
            poseStack.mulPose(Axis.XP.rotationDegrees(-slant * anim * strength));
        }
    },

    /** Pouch "Block" == AlphaDLC "BlockHit" — зависит от equip. */
    BLOCK_HIT("BlockHit") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            if (attack > 0.0F) {
                float g = Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
                poseStack.translate(0.56F * invert, equip * -0.20F - 0.50F, -0.70F);
                poseStack.mulPose(Axis.YP.rotationDegrees(45.0F * invert));
                poseStack.mulPose(Axis.XP.rotationDegrees(g * -85.0F * strength));
                poseStack.translate(-0.10F * invert, 0.28F, 0.20F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-85.0F));
                return;
            }
            float n = -0.4F * Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
            float m = 0.2F * Mth.sin(Mth.sqrt(attack) * (float) (Math.PI * 2.0));
            float f1 = -0.2F * Mth.sin(attack * (float) Math.PI);
            poseStack.translate(n * invert * strength, m * strength, f1 * strength);
            poseStack.translate(invert * 0.56F, -0.52F + equip * -0.60F, -0.72F);
            SMOOTH.apply(poseStack, attack, equip, invert, strength, corner, slant);
        }
    },

    /** Pouch "Pander" == AlphaDLC "Pander" — зависит от equip. */
    PANDER("Pander") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float anim = Mth.sin(attack * (float) Math.PI);
            float f = 1.0F - equip;
            poseStack.translate(invert * 0.56F, -0.52F, -0.72F);
            poseStack.translate((0.30F - anim * 0.15F) * invert, 0.20F - f * 0.12F, -0.15F - anim * 0.13F);
            poseStack.mulPose(Axis.YP.rotationDegrees((76.0F - 10.0F * anim) * invert));
            poseStack.mulPose(Axis.ZP.rotationDegrees((-16.0F - 8.0F * anim) * invert));
            poseStack.mulPose(Axis.XP.rotationDegrees(-83.0F - 26.0F * anim));
        }
    };

    private final String displayName;

    SwingMode(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return this.displayName;
    }

    public boolean isVanilla() {
        return this == VANILLA;
    }

    public abstract void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant);

    public static SwingMode byDisplayName(String name) {
        for (SwingMode mode : VALUES) {
            if (mode.displayName.equals(name)) {
                return mode;
            }
        }
        return VANILLA;
    }

    /** Кэшируем: values() аллоцирует массив на каждый вызов. */
    private static final SwingMode[] VALUES = values();

    /** Имена для ModeSetting, собираются один раз. */
    private static final String[] DISPLAY_NAMES = buildDisplayNames();

    /** Копия: ModeSetting оборачивает массив через Arrays.asList. */
    public static String[] displayNames() {
        return DISPLAY_NAMES.clone();
    }

    private static String[] buildDisplayNames() {
        String[] names = new String[VALUES.length];
        for (int i = 0; i < VALUES.length; i++) {
            names[i] = VALUES[i].displayName;
        }
        return names;
    }
}

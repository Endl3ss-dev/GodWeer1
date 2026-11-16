package tech.lightclient.modules.impl.render;

import tech.lightclient.LightClient;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.render3d.ShaderEspRenderer;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ColorSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import tech.lightclient.modules.impl.combat.AuraModule;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * ShaderEsp - копия шейдер-эффекта из Hands, но вокруг игроков.
 * Статичный контур с мягким размытием в стороны (Kawase bloom),
 * заливка выключена по умолчанию, анимации нет.
 */
@FunctionAdd(name = "ShaderEsp", alias = "Shader ESP", category = Category.Render,
        description = "Статичное шейдерное свечение по контуру игроков")
public class ShaderEsp extends Function {
    public static final String COLOR_THEME = "Тема";
    public static final String COLOR_CUSTOM = "Свои";

    private static ShaderEsp instance;

    private final ModeSetting colorMode = new ModeSetting("Цвет", COLOR_THEME, COLOR_THEME, COLOR_CUSTOM);
    private final ColorSetting glowColor1 = new ColorSetting("Цвет 1", 0xFF8A98FF);
    private final ColorSetting glowColor2 = new ColorSetting("Цвет 2", 0xFFFF6BAC);
    private final SliderSetting glowSaturation = new SliderSetting("Насыщенность", 1.40f, 0.5f, 3.0f, 0.05f);

    private final SliderSetting glowRadius = new SliderSetting("Радиус свечения", 4.0f, 1.0f, 6.0f, 1.0f);
    private final SliderSetting glowExposure = new SliderSetting("Сила свечения", 2.0f, 0.5f, 5.0f, 0.05f);

    private final ModeSetting fillMode = new ModeSetting("Заливка", "Выкл", "Выкл", "Обычная", "Caustic", "Water");
    private final SliderSetting fillAlpha = new SliderSetting("Прозрачность заливки", 0.35f, 0.0f, 1.0f, 0.01f)
            .setVisible(() -> !fillMode.is("Выкл"));
    private final ColorSetting fillColor = new ColorSetting("Цвет заливки", 0xFF8A98FF)
            .setVisible(() -> !fillMode.is("Выкл"));

    // Настройки эффекта Caustic/Water (перенесены из SkyShader)
    private boolean isEffectFill() {
        return fillMode.is("Caustic") || fillMode.is("Water");
    }

    private final SliderSetting effectSpeed = new SliderSetting("Скорость эффекта", 1.0f, 0.1f, 5.0f, 0.1f)
            .setVisible(this::isEffectFill);
    private final SliderSetting effectScale = new SliderSetting("Размер эффекта", 5.0f, 1.0f, 20.0f, 0.5f)
            .setVisible(this::isEffectFill);
    private final SliderSetting effectIntensity = new SliderSetting("Интенсивность эффекта", 0.01f, 0.001f, 0.05f, 0.001f)
            .setVisible(this::isEffectFill);

    private final BooleanSetting outline = new BooleanSetting("Контур", true);
    private final SliderSetting outlineThickness = new SliderSetting("Толщина контура", 1.0f, 0.5f, 3.0f, 0.5f)
            .setVisible(outline::get);

    private final BooleanSetting onlyAuraTarget = new BooleanSetting("Только цель ауры", false);
    private final SliderSetting distance = new SliderSetting("Дистанция", 64.0f, 16.0f, 160.0f, 1.0f);

    public ShaderEsp() {
        instance = this;
        glowColor1.setVisible(() -> colorMode.is(COLOR_CUSTOM));
        glowColor2.setVisible(() -> colorMode.is(COLOR_CUSTOM));
        addSettings(
                colorMode, glowColor1, glowColor2, glowSaturation,
                glowRadius, glowExposure,
                fillMode, fillAlpha, fillColor,
                effectSpeed, effectScale, effectIntensity,
                outline, outlineThickness,
                onlyAuraTarget, distance
        );
    }

    public static ShaderEsp getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onRenderWorld(EventRender.World event) {
        if (!isState()) return;
        if (mc.player == null || mc.world == null) return;

        List<AbstractClientPlayerEntity> players = collectTargets();
        if (players.isEmpty()) return;

        Camera camera = mc.gameRenderer.getCamera();
        float tickDelta = event.getTicks();
        MatrixStack worldStack = event.getMatrixStack();

        ShaderEspRenderer.render(this, players, camera, tickDelta, worldStack, camera.getCameraPos());
    }

    private List<AbstractClientPlayerEntity> collectTargets() {
        List<AbstractClientPlayerEntity> result = new ArrayList<>();
        double maxDistSq = distance.get() * distance.get();
        LivingEntity auraTarget = AuraModule.target;

        for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
            if (player == null || !player.isAlive() || player.isSpectator()) continue;
            if (player == mc.player && mc.options.getPerspective().isFirstPerson()) continue;
            if (onlyAuraTarget.get()) {
                if (player != auraTarget) continue;
            }
            if (mc.player.squaredDistanceTo(player) > maxDistSq) continue;
            result.add(player);
        }
        return result;
    }

    // ==== Параметры для рендерера ====

    public boolean useFill() {
        return !fillMode.is("Выкл") && getFillAlpha() > 0.005f;
    }

    public String getFillMode() {
        return fillMode.get();
    }

    public float getEffectSpeed() {
        return effectSpeed.get();
    }

    public float getEffectScale() {
        return effectScale.get();
    }

    public float getEffectIntensity() {
        return effectIntensity.get();
    }

    public float getFillAlpha() {
        return fillAlpha.get();
    }

    public int getFillColor() {
        return fillColor.getColor().getRGB();
    }

    public boolean useOutline() {
        return outline.get();
    }

    public float getOutlineThickness() {
        return outlineThickness.get();
    }

    public int getGlowRadiusPasses() {
        return Math.max(1, Math.min(6, Math.round(glowRadius.get())));
    }

    public float getGlowExposure() {
        return glowExposure.get();
    }

    public float getGlowSaturation() {
        return glowSaturation.get();
    }

    public int getGlowColor1() {
        return colorMode.is(COLOR_THEME) ? ClientColors.GRADIENT_START.getRGB() : glowColor1.getColor().getRGB();
    }

    public int getGlowColor2() {
        return colorMode.is(COLOR_THEME) ? ClientColors.GRADIENT_END.getRGB() : glowColor2.getColor().getRGB();
    }

    public int getOutlineColor() {
        return colorMode.is(COLOR_THEME) ? ClientColors.GRADIENT_START.getRGB() : glowColor1.getColor().getRGB();
    }
}

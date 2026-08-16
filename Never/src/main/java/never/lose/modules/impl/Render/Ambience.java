package never.lose.modules.impl.Render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import never.lose.managers.ModuleManager;
import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;
import never.lose.modules.api.setting.ColorSetting;
import never.lose.modules.api.setting.ModeSetting;
import never.lose.modules.api.setting.SliderSetting;
import never.lose.utils.render.sky.PlasmaSkyRenderer;
import org.joml.Matrix4f;

import java.time.LocalTime;

@ModuleInfo(name = "Ambience", desc = "Позволяет изменить атмосферу игры", category = ModuleCategory.RENDER)
public class Ambience extends Module {

    public static final String SKY_VANILLA = "Обычная";
    public static final String SKY_PLASMA = "Плазма";

    public static final String TIME_DAWN = "Рассвет";
    public static final String TIME_MORNING = "Утро";
    public static final String TIME_DAY = "День";
    public static final String TIME_EVENING = "Вечер";
    public static final String TIME_SUNSET = "Заход солнца";
    public static final String TIME_NIGHT = "Ночь";
    public static final String TIME_REAL = "Время из реальной жизни";
    public static final String TIME_KEEP = "Не менять";

    public static final String FOG_KEEP = "Ничего не делать";
    public static final String FOG_CLEAR = "Очистить";
    public static final String FOG_OVERRIDE = "Переопределить";

    public final ModeSetting timeMode = new ModeSetting("Время", TIME_KEEP,
            TIME_DAWN, TIME_MORNING, TIME_DAY, TIME_EVENING, TIME_SUNSET, TIME_NIGHT, TIME_REAL, TIME_KEEP);
    public final ModeSetting fogMode = new ModeSetting("Туман", FOG_KEEP,
            FOG_KEEP, FOG_CLEAR, FOG_OVERRIDE);
    public final ColorSetting fogColor = new ColorSetting("Цвет тумана", -1)
            .setVisible(() -> fogMode.is(FOG_OVERRIDE));
    public final SliderSetting fogEnd = new SliderSetting("Конец тумана", 1.0, 0.1, 1.5, 0.1)
            .setVisible(() -> fogMode.is(FOG_OVERRIDE));
    public final SliderSetting fogStart = new SliderSetting("Начало тумана", 0.5, 0.1, 1.5, 0.1)
            .setVisible(() -> fogMode.is(FOG_OVERRIDE));
    public final ModeSetting skyMode = new ModeSetting("Небо", SKY_PLASMA, SKY_VANILLA, SKY_PLASMA);
    public final SliderSetting skySpeed = new SliderSetting("Скорость шейдера", 1.2, 0.1, 5.0, 0.1)
            .setVisible(this::isPlasmaSky);
    public final SliderSetting skyScale = new SliderSetting("Масштаб шейдера", 1.0, 0.2, 4.0, 0.1)
            .setVisible(this::isPlasmaSky);
    public final SliderSetting skyOutline = new SliderSetting("Контур шейдера", 0.1, 0.01, 1.0, 0.01)
            .setVisible(this::isPlasmaSky);
    public final SliderSetting skyGlow = new SliderSetting("Глоу шейдера", 10.0, 0.0, 30.0, 0.5)
            .setVisible(this::isPlasmaSky);
    public final SliderSetting skyFill = new SliderSetting("Заливка шейдера", 0.6, 0.0, 2.0, 0.05)
            .setVisible(this::isPlasmaSky);
    public final SliderSetting skyAlpha = new SliderSetting("Альфа шейдера", 1.0, 0.0, 2.0, 0.05)
            .setVisible(this::isPlasmaSky);

    public static Ambience getInstance() {
        return ModuleManager.get().get(Ambience.class);
    }

    public boolean isPlasmaSky() {
        return skyMode.is(SKY_PLASMA);
    }

    public boolean shouldOverrideTime() {
        return isEnabled() && !timeMode.is(TIME_KEEP);
    }

    public static boolean renderCustomSky(Matrix4f positionMatrix, Matrix4f projectionMatrix, float tickDelta, Camera camera, Runnable fogCallback) {
        Ambience ambience = getInstance();
        if (ambience == null || !ambience.isEnabled() || !ambience.isPlasmaSky()) {
            return false;
        }
        if (camera != null && camera.getSubmersionType() != CameraSubmersionType.NONE) {
            return false;
        }
        if (fogCallback != null) {
            fogCallback.run();
        }
        return PlasmaSkyRenderer.draw(ambience, positionMatrix, projectionMatrix, tickDelta);
    }

    public static void applyFogDistance(Camera camera, float viewDistance) {
        Ambience ambience = getInstance();
        if (ambience == null || !ambience.isEnabled() || camera == null) {
            return;
        }
        if (camera.getSubmersionType() != CameraSubmersionType.NONE) {
            return;
        }
        if (ambience.fogMode.is(FOG_CLEAR)) {
            BackgroundRenderer.clearFog();
            RenderSystem.setShaderFogEnd(Float.MAX_VALUE);
            return;
        }
        if (!ambience.fogMode.is(FOG_OVERRIDE)) {
            return;
        }
        float start = viewDistance * ambience.fogStart.getFloatValue();
        float end = viewDistance * ambience.fogEnd.getFloatValue();
        if (start > end) {
            float swap = start;
            start = end;
            end = swap;
        }
        RenderSystem.setShaderFogStart(start);
        RenderSystem.setShaderFogEnd(end);
    }

    public static boolean applyFogColor(Camera camera, float[] rgb) {
        Ambience ambience = getInstance();
        if (ambience == null || !ambience.isEnabled() || !ambience.fogMode.is(FOG_OVERRIDE)) {
            return false;
        }
        if (camera != null && camera.getSubmersionType() != CameraSubmersionType.NONE) {
            return false;
        }
        int color = ambience.fogColor.getValue();
        rgb[0] = ((color >> 16) & 0xFF) / 255.0F;
        rgb[1] = ((color >> 8) & 0xFF) / 255.0F;
        rgb[2] = (color & 0xFF) / 255.0F;
        RenderSystem.setShaderFogColor(rgb[0], rgb[1], rgb[2], 1.0F);
        return true;
    }

    @Override
    public void onTick() {
        applyTimeOfDay();
    }

    public static void applyTimeOfDay() {
        Ambience ambience = getInstance();
        if (ambience == null || !ambience.shouldOverrideTime()) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld world = mc.world;
        if (world == null) {
            return;
        }
        long time = ambience.resolveTime(world.getTimeOfDay());
        if (time >= 0L) {
            world.setTimeOfDay(time);
        }
    }

    private long resolveTime(long current) {
        if (timeMode.is(TIME_KEEP)) {
            return -1L;
        }
        if (timeMode.is(TIME_REAL)) {
            return getRealWorldTime();
        }
        if (timeMode.is(TIME_DAWN)) {
            return 23000L;
        }
        if (timeMode.is(TIME_MORNING)) {
            return 1000L;
        }
        if (timeMode.is(TIME_DAY)) {
            return 6000L;
        }
        if (timeMode.is(TIME_EVENING)) {
            return 12000L;
        }
        if (timeMode.is(TIME_SUNSET)) {
            return 13000L;
        }
        if (timeMode.is(TIME_NIGHT)) {
            return 18000L;
        }
        return current;
    }

    private static long getRealWorldTime() {
        LocalTime now = LocalTime.now();
        int totalSeconds = now.getHour() * 3600 + now.getMinute() * 60 + now.getSecond();
        int offsetSeconds = (totalSeconds - 6 * 3600) % (24 * 3600);
        if (offsetSeconds < 0) {
            offsetSeconds += 24 * 3600;
        }
        return (long) ((offsetSeconds / 86400.0) * 24000.0);
    }
}

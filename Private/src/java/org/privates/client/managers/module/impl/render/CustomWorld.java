package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.events.render.WorldColorEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.SkyShaderRenderer;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "CustomWorld", category = Category.RENDER, description = "Изменяет время суток в игре и также добавляет туман")
public class CustomWorld extends Module {
    public static CustomWorld getInstance() {
        return Instance.get(CustomWorld.class);
    }

    private final ColorSetting color = new ColorSetting(this, "Цвет");
    private final SliderSetting time = new SliderSetting(this, "Время суток", 16000, 0, 24000, 500);
    
    // Fog settings
    private final BooleanSetting enableFog = new BooleanSetting(this, "Включить туман", false);
    private final SliderSetting fogDistance = new SliderSetting(this, "Дистанция тумана", 1F, 0F, 1F, 0.01F)
            .setVisible(() -> enableFog.getValue());
    private final ColorSetting fogColor = new ColorSetting(this, "Цвет тумана", 0xFFFFFFFF)
            .setVisible(() -> enableFog.getValue());
    
    // Sky shader settings
    private final BooleanSetting enableSkyShader = new BooleanSetting(this, "Включить шейдер неба", false);
    private final ModeSetting skyShaderMode = new ModeSetting(this, "Режим шейдера неба", 
            "Nebula", "Cosmic", "Glow Waves", "Aurora")
            .setVisible(() -> enableSkyShader.getValue());
    private final SliderSetting skyShaderIntensity = new SliderSetting(this, "Интенсивность шейдера", 0.7f, 0.0f, 1.0f, 0.05f)
            .setVisible(() -> enableSkyShader.getValue());
    private final SliderSetting skyShaderSpeed = new SliderSetting(this, "Скорость анимации", 1.0f, 0.1f, 5.0f, 0.1f)
            .setVisible(() -> enableSkyShader.getValue());
    
    private float skyShaderTime = 0.0f;

    @Override
    public void toggle() {
        super.toggle();
        mc.world.setRainStrength(0);
        mc.world.getWorldInfo().setRaining(false);
    }

    @EventHandler
    public void onEvent(WorldColorEvent event) {
        float[] rgb = ColorUtil.getRGBf(color.getValue());
        event.setRed(rgb[0]);
        event.setGreen(rgb[1]);
        event.setBlue(rgb[2]);
        
        // Apply fog color if enabled
        if (enableFog.getValue()) {
            float[] fogRgb = ColorUtil.getRGBf(fogColor.getValue());
            event.setRed(event.getRed() * (1 - fogDistance.getValue()) + fogRgb[0] * fogDistance.getValue());
            event.setGreen(event.getGreen() * (1 - fogDistance.getValue()) + fogRgb[1] * fogDistance.getValue());
            event.setBlue(event.getBlue() * (1 - fogDistance.getValue()) + fogRgb[2] * fogDistance.getValue());
        }
    }

    @EventHandler
    public void onEvent(Render3DLastEvent event) {
        mc.world.setDayTime(time.getValue().intValue());
    }
    
    // Sky shader rendering is now handled in WorldRenderer.renderSky()
    // This method is called from there via CustomWorld.getInstance()
    public void renderSkyShader() {
        if (!isEnabled() || !enableSkyShader.getValue()) return;
        
        skyShaderTime += 0.016f * skyShaderSpeed.getValue(); // ~60 FPS
        float intensity = skyShaderIntensity.getValue();
        
        switch (skyShaderMode.getValue()) {
            case "Nebula" -> SkyShaderRenderer.drawNebula(skyShaderTime, intensity);
            case "Cosmic" -> SkyShaderRenderer.drawCosmic(skyShaderTime, intensity);
            case "Glow Waves" -> SkyShaderRenderer.drawWaves(skyShaderTime, intensity);
            case "Aurora" -> SkyShaderRenderer.drawAurora(skyShaderTime, intensity);
        }
    }

    @EventHandler
    public void onEvent(MotionEvent event) {
        if (mc.player.ticksExisted % 20 == 0) {
            mc.world.setRainStrength(0);
            mc.world.getWorldInfo().setRaining(false);
        }
    }

    @EventHandler
    public void onEvent(PacketEvent event) {
        if (event.isReceive()) {
            if (event.getPacket() instanceof SUpdateTimePacket) {
                event.setCancelled(true);
            } else if (event.getPacket() instanceof SChangeGameStatePacket wrapper) {
                if (wrapper.getState() == SChangeGameStatePacket.field_241765_b_ || wrapper.getState() == SChangeGameStatePacket.field_241766_c_) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.privates.client.api.annotations.Category2;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.shader.ShaderManager;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "HandItemShader", description = "Применяет шейдеры к рукам и предметам", category = Category.RENDER)
public class HandItemShader extends Module {

    public static HandItemShader getInstance() {
        return Instance.get(HandItemShader.class);
    }

    private final ModeSetting shaderMode = new ModeSetting(this, "Шейдер", "Cosmic Nebula", "Cobweb", "Holo Crystal", "Energy Grid");
    private final SliderSetting intensity = new SliderSetting(this, "Интенсивность", 1.0F, 0.1F, 2.0F, 0.1F);
    private final SliderSetting nebulaDensity = new SliderSetting(this, "Nebula Density", 1.0F, 0.3F, 2.5F, 0.1F)
            .setVisible(() -> shaderMode.is("Cosmic Nebula"));
    private final SliderSetting cobwebSpeed = new SliderSetting(this, "Cobweb Speed", 0.4F, 0.05F, 2.0F, 0.05F)
            .setVisible(() -> shaderMode.is("Cobweb"));
    private final SliderSetting cobwebIntensity = new SliderSetting(this, "Cobweb Intensity", 1.0F, 0.2F, 3.0F, 0.1F)
            .setVisible(() -> shaderMode.is("Cobweb"));

    public void applyShaderToHand() {
        ShaderManager shader = getShaderForMode();
        if (shader == null) return;

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.disableDepthTest();

        mc.getFramebuffer().bindFramebufferTexture();

        shader.load();
        shader.setUniformf("u_texel_size",
                1.0F / mc.getFramebuffer().framebufferWidth,
                1.0F / mc.getFramebuffer().framebufferHeight);
        shader.setUniformf("u_time", (System.currentTimeMillis() % 1000000) / 1000.0F);

        switch (shaderMode.getValue()) {
            case "Cosmic Nebula" -> shader.setUniformf("u_nebula_density", nebulaDensity.getValue() * intensity.getValue());
            case "Cobweb" -> {
                shader.setUniformf("u_speed", cobwebSpeed.getValue());
                shader.setUniformf("u_intensity", cobwebIntensity.getValue() * intensity.getValue());
            }
        }

        shader.setUniformi("u_diffuse_sampler", 0);
        shader.setUniformi("u_other_sampler", 20);

        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        mc.getFramebuffer().bindFramebufferTexture();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        mc.getFramebuffer().bindFramebufferTexture();

        ShaderManager.draw();

        shader.unload();

        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        GlStateManager.bindTexture(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GlStateManager.bindTexture(0);

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private ShaderManager getShaderForMode() {
        return switch (shaderMode.getValue()) {
            case "Cosmic Nebula" -> ShaderManager.cosmicNebula;
            case "Cobweb" -> ShaderManager.cobweb;
            case "Holo Crystal" -> ShaderManager.blockHoloCrystal;
            case "Energy Grid" -> ShaderManager.blockEnergyGrid;
            default -> null;
        };
    }
}

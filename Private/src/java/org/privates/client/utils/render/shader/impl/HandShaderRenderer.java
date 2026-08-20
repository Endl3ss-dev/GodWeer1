package org.privates.client.utils.render.shader.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.privates.client.managers.module.impl.render.HandItemShader;
import org.privates.client.utils.render.framebuffer.CustomFramebuffer;
import org.privates.client.utils.render.shader.ShaderManager;

public class HandShaderRenderer {
    private final Minecraft mc = Minecraft.getInstance();
    private final HandItemShader module;
    private final CustomFramebuffer hands = new CustomFramebuffer(false).setLinear();
    private final CustomFramebuffer mask = new CustomFramebuffer(false).setLinear();
    private Runnable task3D;

    public HandShaderRenderer(HandItemShader module) {
        this.module = module;
    }

    public void addTask3D(Runnable task) {
        this.task3D = task;
    }

    public void draw() {
        if (task3D == null) return;

        hands.setup();
        task3D.run();
        hands.stop();

        mask.setup();
        hands.draw();
        mask.stop();

        mc.getFramebuffer().bindFramebuffer(false);

        task3D = null;
    }

    public void drawDisplay() {
        ShaderManager shader = getShaderForMode();
        if (shader == null) return;

        shader.load();
        shader.setUniformf("u_texel_size",
                1.0F / mc.getFramebuffer().framebufferWidth,
                1.0F / mc.getFramebuffer().framebufferHeight);
        shader.setUniformf("u_time", (System.currentTimeMillis() % 1000000) / 1000.0F);

        switch (module.shaderMode().getValue()) {
            case "Cosmic Nebula" -> shader.setUniformf("u_nebula_density", module.nebulaDensity().getValue() * module.intensity().getValue());
            case "Cobweb" -> {
                shader.setUniformf("u_speed", module.cobwebSpeed().getValue());
                shader.setUniformf("u_intensity", module.cobwebIntensity().getValue() * module.intensity().getValue());
            }
        }

        shader.setUniformi("u_diffuse_sampler", 0);
        shader.setUniformi("u_other_sampler", 20);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.disableDepthTest();

        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        mask.draw();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        hands.draw();

        ShaderManager.draw();

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        shader.unload();

        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        GlStateManager.bindTexture(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GlStateManager.bindTexture(0);
    }

    private ShaderManager getShaderForMode() {
        return switch (module.shaderMode().getValue()) {
            case "Cosmic Nebula" -> ShaderManager.cosmicNebula;
            case "Cobweb" -> ShaderManager.cobweb;
            case "Holo Crystal" -> ShaderManager.blockHoloCrystal;
            case "Energy Grid" -> ShaderManager.blockEnergyGrid;
            default -> null;
        };
    }
}

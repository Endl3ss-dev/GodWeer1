package tech.lightclient.client.utils.window;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import tech.lightclient.LightClient;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.gl.GlState;
import tech.lightclient.mixins.accessors.GameRendererAccessor;
import tech.lightclient.modules.impl.render.Portals;

import java.nio.ByteBuffer;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

public final class WorldWindowRenderer {

    private static final int UBO_SIZE = 160;

    private static RenderPipeline pipeline;
    private static GpuBuffer ubo;
    private static GpuBuffer dummyVertexBuffer;
    private static GpuSampler sampler;
    private static boolean glInitFailed;

    private static GpuTexture worldTex;
    private static int worldFbo;
    private static int worldW = -1;
    private static int worldH = -1;

    private static final class Target {
        GpuTexture tex;
        GpuTextureView view;
        int fbo;
        int w;
        int h;
    }

    private static final Map<WorldWindow, Target> TARGETS = new IdentityHashMap<>();

    private WorldWindowRenderer() {
    }

    public static void renderChrome(int fbW, int fbH) {
        if (glInitFailed || !Portals.isWindowsActive() || fbW <= 0 || fbH <= 0) {
            return;
        }
        List<WorldWindow> windows = Portals.windows();
        if (windows.isEmpty()) {
            return;
        }
        var instance = LightClient.getInstance();
        if (instance == null || instance.renderer == null) {
            return;
        }
        Renderer2D renderer = instance.renderer;
        if (!renderer.isFrameActive()) {
            LightClient.LOGGER.warn("[Windows] Skipping chrome render because the 2D frame is not active");
            return;
        }

        try {
            ensureWorldCapture(fbW, fbH);
            GlState.Snapshot snapshot = GlState.push();
            try {
                GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, worldFbo);
                GL30.glBlitFramebuffer(0, 0, fbW, fbH, 0, 0, fbW, fbH, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
            } finally {
                GlState.pop(snapshot);
            }

            // Overlay commands queued before this method belong to the main
            // framebuffer. Flush them before switching to a window target.
            renderer.flush();

            int worldGl = ((GlTexture) worldTex).getGlId();
            for (WorldWindow window : windows) {
                int pw = pixW(window);
                int ph = pixH(window);
                ensureBrowser(window, pw, ph);
                Target target = ensureTarget(window, pw, ph);
                if (target == null) {
                    continue;
                }
                GlState.Snapshot targetSnapshot = GlState.push();
                try {
                    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, target.fbo);
                    GL11.glViewport(0, 0, pw, ph);
                    GL11.glClearColor(0f, 0f, 0f, 0f);
                    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
                    // The main overlay renderer is already between begin/end.
                    // Reset its GL state for this target, draw into the target,
                    // and flush before GlState restores the previous framebuffer.
                    renderer.resetPipelineState(pw, ph);
                    renderer.setLazyBlurSource(worldGl, fbW, fbH, 10f);
                    WorldWindowChrome.draw(renderer, window, pw, ph);
                    renderer.flush();
                } finally {
                    GlState.pop(targetSnapshot);
                    // The outer overlay frame remains active. Restore its
                    // framebuffer viewport and 2D pipeline after rendering
                    // the resized offscreen window target.
                    renderer.resetPipelineState(fbW, fbH);
                    renderer.setLazyBlurSource(worldGl, fbW, fbH, 8f);
                }
            }
        } catch (Throwable e) {
            LightClient.LOGGER.error("[Windows] Chrome render failed", e);
            glInitFailed = true;
        }
    }

    public static void renderQuads(float tickDelta) {
        if (glInitFailed || !Portals.isWindowsActive()) {
            return;
        }
        List<WorldWindow> windows = Portals.windows();
        if (windows.isEmpty() || mc.world == null || mc.gameRenderer == null) {
            return;
        }
        if (!ensurePipeline()) {
            return;
        }

        Camera camera = mc.gameRenderer.getCamera();
        float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, tickDelta, true);
        Vec3d camPos = camera.getCameraPos();
        Matrix4f projView = new Matrix4f(mc.gameRenderer.getBasicProjectionMatrix(fov))
                .mul(new Matrix4f().rotation(new Quaternionf(camera.getRotation()).conjugate()));

        for (WorldWindow window : windows) {
            Target target = TARGETS.get(window);
            if (target == null || target.view == null) {
                continue;
            }

            Vec3d c = window.center.subtract(camPos);
            Vec3d halfR = window.right.multiply(window.worldW * 0.5);
            Vec3d halfU = window.up.multiply(window.worldH * 0.5);
            Vec3d tl = c.subtract(halfR).add(halfU);
            Vec3d tr = c.add(halfR).add(halfU);
            Vec3d br = c.add(halfR).subtract(halfU);
            Vec3d bl = c.subtract(halfR).subtract(halfU);

            try {
                CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    ByteBuffer buf = stack.malloc(UBO_SIZE);
                    projView.get(0, buf);
                    putVec4(buf, 64, (float) tl.x, (float) tl.y, (float) tl.z, 0f);
                    putVec4(buf, 80, (float) tr.x, (float) tr.y, (float) tr.z, 0f);
                    putVec4(buf, 96, (float) br.x, (float) br.y, (float) br.z, 0f);
                    putVec4(buf, 112, (float) bl.x, (float) bl.y, (float) bl.z, 0f);
                    putVec4(buf, 128, 1f, 0f, 0f, 1f);
                    putVec4(buf, 144, 1f, 0f, 0f, 0f);
                    encoder.writeToBuffer(ubo.slice(), buf);
                }
                try (RenderPass pass = encoder.createRenderPass(
                        () -> "Light:world_window",
                        mc.getFramebuffer().getColorAttachmentView(),
                        OptionalInt.empty())) {
                    pass.setPipeline(pipeline);
                    pass.setUniform("GhostData", ubo.slice());
                    pass.bindTexture("GhostSampler", target.view, sampler);
                    pass.setVertexBuffer(0, dummyVertexBuffer);
                    pass.draw(0, 6);
                }
            } catch (Throwable e) {
                LightClient.LOGGER.error("[Windows] Quad draw failed", e);
                glInitFailed = true;
                return;
            }
        }
    }

    private static void ensureBrowser(WorldWindow window, int pw, int ph) {
        int tabPix = (int) (ph * WorldWindow.TAB_FRAC);
        int toolPix = (int) (ph * WorldWindow.TOOLBAR_FRAC);
        int barPix = (int) (ph * WorldWindow.BAR_FRAC);
        int cw = pw;
        int ch = Math.max(1, ph - tabPix - toolPix - barPix);
        try {
            for (WorldWindow.Tab tab : window.tabs) {
                if (tab.url == null || tab.url.isBlank()) {
                    continue;
                }
                if (tab.browser == null) {
                    if (!Browsers.ready()) {
                        continue;
                    }
                    tab.browser = Browsers.api().createBrowser(tab.url, false);
                    tab.w = cw;
                    tab.h = ch;
                    tab.browser.resize(cw, ch);
                } else if (tab.w != cw || tab.h != ch) {
                    tab.w = cw;
                    tab.h = ch;
                    tab.browser.resize(cw, ch);
                }
            }
        } catch (Throwable t) {
            LightClient.LOGGER.error("[Windows] Browser create/resize failed", t);
        }
        window.syncActive();
    }

    public static void dispose(WorldWindow window) {
        Target target = TARGETS.remove(window);
        if (target != null) {
            closeTarget(target);
        }
        for (WorldWindow.Tab tab : window.tabs) {
            if (tab.browser != null) {
                try {
                    tab.browser.close();
                } catch (Throwable ignored) {
                }
                tab.browser = null;
            }
        }
        window.browser = null;
    }

    private static int pixW(WorldWindow window) {
        return clamp((int) Math.round(window.worldW * 300), 1024, 1920);
    }

    private static int pixH(WorldWindow window) {
        return clamp((int) Math.round(pixW(window) * (window.worldH / window.worldW)), 600, 1200);
    }

    private static int clamp(int value, int min, int max) {
        return value < min ? min : Math.min(value, max);
    }

    private static Target ensureTarget(WorldWindow window, int pw, int ph) {
        Target target = TARGETS.get(window);
        if (target != null && target.w == pw && target.h == ph) {
            return target;
        }
        if (target != null) {
            closeTarget(target);
        }
        target = new Target();
        target.tex = RenderSystem.getDevice().createTexture(() -> "Light:world_window",
                GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT | GpuTexture.USAGE_COPY_DST,
                TextureFormat.RGBA8, pw, ph, 1, 1);
        target.view = RenderSystem.getDevice().createTextureView(target.tex);
        int glId = ((GlTexture) target.tex).getGlId();
        GlState.Snapshot snapshot = GlState.push();
        try {
            target.fbo = GL30.glGenFramebuffers();
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, target.fbo);
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, glId, 0);
            if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
                closeTarget(target);
                return null;
            }
        } finally {
            GlState.pop(snapshot);
        }
        target.w = pw;
        target.h = ph;
        TARGETS.put(window, target);
        return target;
    }

    private static void closeTarget(Target target) {
        if (target.fbo != 0) {
            GL30.glDeleteFramebuffers(target.fbo);
        }
        if (target.view != null) {
            target.view.close();
        }
        if (target.tex != null) {
            target.tex.close();
        }
    }

    private static void ensureWorldCapture(int fbW, int fbH) {
        if (worldTex != null && worldW == fbW && worldH == fbH) {
            return;
        }
        if (worldFbo != 0) {
            GL30.glDeleteFramebuffers(worldFbo);
            worldFbo = 0;
        }
        if (worldTex != null) {
            worldTex.close();
            worldTex = null;
        }
        worldTex = RenderSystem.getDevice().createTexture(() -> "Light:world_window_capture",
                GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT | GpuTexture.USAGE_COPY_DST,
                TextureFormat.RGBA8, fbW, fbH, 1, 1);
        int glId = ((GlTexture) worldTex).getGlId();
        GlState.Snapshot snapshot = GlState.push();
        try {
            worldFbo = GL30.glGenFramebuffers();
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, worldFbo);
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, glId, 0);
        } finally {
            GlState.pop(snapshot);
        }
        worldW = fbW;
        worldH = fbH;
    }

    private static boolean ensurePipeline() {
        if (glInitFailed) {
            return false;
        }
        if (pipeline != null) {
            return true;
        }
        try {
            pipeline = RenderPipeline.builder()
                    .withLocation(Identifier.of("light", "pipeline/effects/world_window"))
                    .withVertexShader(Identifier.of("light", "effects/clickgui_ghost/quad"))
                    .withFragmentShader(Identifier.of("light", "effects/clickgui_ghost/quad"))
                    .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                    .withUniform("GhostData", UniformType.UNIFORM_BUFFER)
                    .withSampler("GhostSampler")
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withCull(false)
                    .withBlend(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA)
                    .build();
            ubo = RenderSystem.getDevice().createBuffer(() -> "Light:world_window_ubo",
                    GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, UBO_SIZE);
            ByteBuffer dummy = MemoryUtil.memAlloc(4);
            dummy.putInt(0);
            dummy.flip();
            dummyVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "Light:world_window_vb",
                    GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST, dummy);
            MemoryUtil.memFree(dummy);
            sampler = RenderSystem.getDevice().createSampler(
                    AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE,
                    FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
            return true;
        } catch (Throwable e) {
            LightClient.LOGGER.error("[Windows] Pipeline init failed", e);
            glInitFailed = true;
            return false;
        }
    }

    private static void putVec4(ByteBuffer buf, int off, float x, float y, float z, float w) {
        buf.putFloat(off, x);
        buf.putFloat(off + 4, y);
        buf.putFloat(off + 8, z);
        buf.putFloat(off + 12, w);
    }
}

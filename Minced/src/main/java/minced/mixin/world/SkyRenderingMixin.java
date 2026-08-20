/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.Fog
 *  net.minecraft.client.render.SkyRendering
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.util.math.MatrixStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.world;
import com.mojang.blaze3d.systems.RenderSystem;
import minced.core.ColorUtil;
import minced.core.Shaders;
import minced.core.ThemeColors;
import minced.enums.MAIN;
import minced.module.visual.Ambience;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.SkyRendering;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={SkyRendering.class})
public class SkyRenderingMixin {
    private static String field12655 = "field12655";
    private static String field12656 = "field12656";

    @Inject(method={"renderSky"}, at={@At(value="HEAD")}, cancellable=true)
    private void minced$ambientSky(float param1, float param2, float param3, CallbackInfo param4) {
        ShaderProgramKey shaderKey = Ambience.getCustomSkyShader();
        if (shaderKey == null) {
            return;
        }

        ShaderProgram shader = RenderSystem.setShader(shaderKey);
        if (shader == null) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        float width = Math.max(1, client.getWindow().getFramebufferWidth());
        float height = Math.max(1, client.getWindow().getFramebufferHeight());
        int themeColor = ThemeColors.method3199(MAIN.MODULE_VISUAL);
        float red = ColorUtil.method1854(themeColor) / 255.0f;
        float green = ColorUtil.method1846(themeColor) / 255.0f;
        float blue = ColorUtil.method1847(themeColor) / 255.0f;
        float tickDelta = client.getRenderTickCounter().getTickDelta(false);
        float time = client.world == null
                ? System.currentTimeMillis() / 1000.0f
                : ((float)client.world.getTime() + tickDelta) * Ambience.getCustomSkySpeed() / 20.0f;

        if (shader.getUniform("u_Resolution") != null) {
            shader.getUniform("u_Resolution").set(width, height);
        }
        if (shader.getUniform("u_Color") != null) {
            shader.getUniform("u_Color").set(red, green, blue, 1.0f);
        }
        if (shader.getUniform("u_Scale") != null) {
            shader.getUniform("u_Scale").set(Ambience.getCustomSkyScale());
        }
        if (shader.getUniform("u_Time") != null) {
            shader.getUniform("u_Time").set(time);
        }
        if (shader.getUniform("uTime") != null) {
            shader.getUniform("uTime").set(time);
        }
        if (shader.getUniform("uThemeColor") != null) {
            shader.getUniform("uThemeColor").set(red, green, blue);
        }
        if (shader.getUniform("u_Fov") != null) {
            float fov = (float)Math.toRadians(client.options.getFov().getValue().floatValue());
            shader.getUniform("u_Fov").set(fov);
        }
        if (shader.getUniform("u_WorldLocked") != null) {
            shader.getUniform("u_WorldLocked").set(Ambience.isCustomSkyWorldLocked() ? 1.0f : 0.0f);
        }
        if (shader.getUniform("u_WorldFromView") != null) {
            Camera camera = client.gameRenderer.getCamera();
            Matrix4f worldFromView = new Matrix4f().rotation(camera.getRotation());
            shader.getUniform("u_WorldFromView").set(worldFromView);
        }
        if (shader.getUniform("u_Night") != null && client.world != null) {
            float skyAngle = client.world.getSkyAngle(tickDelta);
            shader.getUniform("u_Night").set(skyAngle > 0.5f ? 1.0f : 0.0f);
        }

        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        buffer.vertex(-1.0f, -1.0f, 0.0f);
        buffer.vertex(1.0f, -1.0f, 0.0f);
        buffer.vertex(1.0f, 1.0f, 0.0f);
        buffer.vertex(-1.0f, 1.0f, 0.0f);
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        param4.cancel();
    }

    @Inject(method={"renderCelestialBodies"}, at={@At(value="HEAD")}, cancellable=true)
    private void minced$skipCelestialWhenCustomSky(MatrixStack param1, VertexConsumerProvider.Immediate param2, float param3, int param4, float param5, float param6, Fog param7, CallbackInfo param8) {
        if (Ambience.getCustomSkyShader() != null) {
            param8.cancel();
        }
    }

    static {
        VMBridge.identifyClass(SkyRenderingMixin.class, "tOK1wVzK");
    }
}




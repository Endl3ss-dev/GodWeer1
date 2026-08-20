/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.PostEffectProcessor
 *  net.minecraft.client.gl.PostEffectProcessor$FramebufferSet
 *  net.minecraft.client.render.BuiltChunkStorage
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.DefaultFramebufferSet
 *  net.minecraft.client.render.Fog
 *  net.minecraft.client.render.FrameGraphBuilder
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.client.render.RenderTickCounter
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.WorldRenderer
 *  net.minecraft.client.util.Handle
 *  net.minecraft.client.util.ObjectAllocator
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.profiler.Profiler
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package minced.mixin.world;
import java.util.List;
import minced.core.ServiceRegistry;
import minced.core.BotView;
import minced.core.EventFreecamWorldRender;
import minced.core.EventRenderLevel;
import minced.core.EventNoRender;
import minced.core.ClientUtil;
import minced.core.Event;
import minced.core.EventBus;
import minced.enums.fire;
import minced.module.visual.Ambience;
import minced.module.visual.BlockOverlay;
import minced.module.visual.ShaderESP;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={WorldRenderer.class})
public abstract class WorldRendererMixin {
    @Shadow
    private ClientWorld field_4085;
    @Shadow
    private BuiltChunkStorage field_4112;
    @Shadow
    private Framebuffer field_53080;
    @Shadow
    private DefaultFramebufferSet field_53081;
    @Unique
    private boolean entityShaderDrawing = false;

    @Shadow
    public abstract void method_22977(Entity var1, double var2, double var4, double var6, float var8, MatrixStack var9, VertexConsumerProvider var10);

    @Shadow
    protected abstract void method_62202(FrameGraphBuilder var1, Frustum var2, Camera var3, Matrix4f var4, Matrix4f var5, Fog var6, boolean var7, boolean var8, RenderTickCounter var9, Profiler var10);

    @Inject(method={"render"}, at={@At(value="HEAD")})
    private void mincedbots$syncWorldBeforeRender(ObjectAllocator param1, RenderTickCounter param2, boolean param3, Camera param4, GameRenderer param5, Matrix4f param6, Matrix4f param7, CallbackInfo param8) {
        this.mincedbots$syncRendererWorld(MinecraftClient.getInstance());
    }

    @Inject(method={"tick"}, at={@At(value="HEAD")}, cancellable=true)
    private void mincedbots$syncWorldBeforeTick(CallbackInfo param1) {
        this.mincedbots$syncRendererWorld(MinecraftClient.getInstance());
        if (this.field_4085 == null) {
            param1.cancel();
        }
    }

    @Inject(method={"renderEntities"}, at={@At(value="HEAD")}, cancellable=true)
    private void mincedbots$guardRenderEntities(MatrixStack param1, VertexConsumerProvider.Immediate param2, Camera param3, RenderTickCounter param4, List<?> param5, CallbackInfo param6) {
        if (this.mincedbots$getPreferredRenderWorld(MinecraftClient.getInstance()) == null) {
            param6.cancel();
        }
    }

    @Inject(method={"isRenderingReady"}, at={@At(value="HEAD")}, cancellable=true)
    private void mincedbots$guardRenderingReady(BlockPos param1, CallbackInfoReturnable<Boolean> param2) {
        this.mincedbots$syncRendererWorld(MinecraftClient.getInstance());
        if (this.field_4085 == null || this.field_4112 == null) {
            param2.setReturnValue((Boolean)false);
        }
    }

    @Unique
    private ClientWorld mincedbots$getPreferredRenderWorld(MinecraftClient param1) {
        ClientWorld v3;
        BotView v2 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v2 != null && v2.method2190() && (v3 = v2.method2200()) != null) {
            return v3;
        }
        return param1.world;
    }

    @Unique
    private void mincedbots$syncRendererWorld(MinecraftClient param1) {
        ClientWorld v2 = this.mincedbots$getPreferredRenderWorld(param1);
        if (this.field_4085 != v2) {
            ((WorldRenderer)(Object)this).setWorld(v2);
        }
    }

    @Unique
    private void minced$wrapFramebuffer(Framebuffer param1, Runnable param2) {
        Framebuffer v3 = this.field_53080;
        Handle v4 = this.field_53081.entityOutlineFramebuffer;
        this.field_53080 = param1;
        this.field_53081.entityOutlineFramebuffer = () -> param1;
        this.entityShaderDrawing = true;
        param2.run();
        this.entityShaderDrawing = false;
        this.field_53080 = v3;
        this.field_53081.entityOutlineFramebuffer = v4;
    }

    @Unique
    private boolean minced$drawEntityWithShader(Entity param1, double param2, double param4, double param6, float param8, MatrixStack param9) {
        if (!this.entityShaderDrawing && ShaderESP.field1900 != null && ShaderESP.field1900.method567(param1)) {
            ShaderESP.field1857 = true;
            this.minced$wrapFramebuffer(ShaderESP.field1900.method577().field8057, () -> {
                ShaderESP.field1900.method581().setColor(255, 255, 255, 255);
                this.method_22977(param1, param2, param4, param6, param8, param9, (VertexConsumerProvider)ShaderESP.field1900.method581());
            });
            return true;
        }
        return false;
    }

    @Inject(method={"renderEntity"}, at={@At(value="HEAD")}, cancellable=true)
    private void minced$onRenderEntity(Entity param1, double param2, double param4, double param6, float param8, MatrixStack param9, VertexConsumerProvider param10, CallbackInfo param11) {
        if (ShaderESP.field1900 != null && this.minced$drawEntityWithShader(param1, param2, param4, param6, param8, param9)) {
            param11.cancel();
        }
    }

    @Redirect(method={"render"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gl/PostEffectProcessor;render(Lnet/minecraft/client/render/FrameGraphBuilder;IILnet/minecraft/client/gl/PostEffectProcessor$FramebufferSet;)V", ordinal=0))
    private void minced$onPostEffectRender(PostEffectProcessor param1, FrameGraphBuilder param2, int param3, int param4, PostEffectProcessor.FramebufferSet param5) {
        if (ShaderESP.field1900 == null || ShaderESP.field1900.method574(false)) {
            param1.render(param2, param3, param4, param5);
        }
    }

    @Inject(method={"drawEntityOutlinesFramebuffer"}, at={@At(value="HEAD")}, cancellable=true)
    private void minced$onDrawEntityOutlines(CallbackInfo param1) {
        if (ShaderESP.field1900 != null && ShaderESP.field1900.isEnabled()) {
            param1.cancel();
        }
    }

    @Inject(method={"method_62214"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/render/OutlineVertexConsumerProvider;draw()V")})
    private void minced$onOutlineDraw(CallbackInfo param1) {
        if (ShaderESP.field1900 != null) {
            ShaderESP.field1900.method574(true);
        }
    }

    @ModifyVariable(method={"setupTerrain"}, at=@At(value="HEAD"), index=4, argsOnly=true)
    private boolean modifySpectator(boolean param1) {
        EventFreecamWorldRender v2 = new EventFreecamWorldRender(param1);
        EventBus.method3015(((Object)v2));
        return v2.method2329();
    }

    @Inject(method={"renderWeather"}, at={@At(value="HEAD")}, cancellable=true)
    private void onRenderWeather(CallbackInfo param1) {
        EventNoRender v2 = new EventNoRender(fire.rain);
        EventBus.method3015(((Object)v2));
        if (v2.method2297()) {
            param1.cancel();
        }
    }

    @Inject(method={"method_62214"}, at={@At(value="INVOKE", target="Lnet/minecraft/util/profiler/Profiler;pop()V")})
    private void onEventLevel(CallbackInfo param1) {
        MatrixStack v2 = new MatrixStack();
        float v3 = ClientUtil.I.getRenderTickCounter().getTickDelta(false);
        EventRenderLevel v4 = new EventRenderLevel(v2, v3);
        EventBus.method3015(((Object)v4));
    }

    @Redirect(method={"render"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/render/WorldRenderer;renderMain(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/Frustum;Lnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/render/Fog;ZZLnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/util/profiler/Profiler;)V"))
    private void minced$onRenderMain(WorldRenderer param1, FrameGraphBuilder param2, Frustum param3, Camera param4, Matrix4f param5, Matrix4f param6, Fog param7, boolean param8, boolean param9, RenderTickCounter param10, Profiler param11) {
        boolean v12 = param8 && (BlockOverlay.field3706 == null || !BlockOverlay.field3706.isEnabled());
        this.method_62202(param2, param3, param4, param5, param6, param7, v12, param9, param10, param11);
    }

    @Redirect(method="method_62215", at=@At(value="INVOKE", target="Lnet/minecraft/client/render/DimensionEffects;getSkyType()Lnet/minecraft/client/render/DimensionEffects$SkyType;"), require=0)
    private DimensionEffects.SkyType minced$forceSkyRenderingForCustomSky(DimensionEffects instance) {
        if (Ambience.getCustomSkyShader() != null) {
            return DimensionEffects.SkyType.NORMAL;
        }
        return instance.getSkyType();
    }
}




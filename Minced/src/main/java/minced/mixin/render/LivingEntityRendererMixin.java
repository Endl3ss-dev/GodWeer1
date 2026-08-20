/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.EntityPose
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Direction
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.render;
import minced.core.EventLayerHead;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.EntityRenderAccess;
import minced.core.FeatureRendererHelper;
import minced.core.Minced;
import minced.module.visual.SeeInvisibles;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={LivingEntityRenderer.class})
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
extends EntityRenderer<T, S>
implements FeatureRendererHelper {
    @Shadow
    protected M field_4737;
    @Unique
    private LivingEntity capturedEntity;
    private static int field12532 = 1;
    private static float field12533 = 1.0f;
    private static float field12534 = 1.0f;
    private static float field12535 = 1.0f;
    private static float field12536 = 1.0f;
    private static float field12537 = 1.0f;
    private static int field12538 = 1;

    @Shadow
    public abstract int method_62484(S var1);

    @Shadow
    public abstract float method_23185(S var1);

    @Shadow
    @Nullable
    public abstract RenderLayer method_24302(S var1, boolean var2, boolean var3, boolean var4);

    @Shadow
    public abstract boolean method_4056(S var1);

    @Shadow
    public abstract void method_4058(S var1, MatrixStack var2, float var3, float var4);

    @Shadow
    public abstract void method_4042(S var1, MatrixStack var2);

    protected LivingEntityRendererMixin(EntityRendererFactory.Context param1) {
        super(param1);
    }

    @Inject(method={"updateRenderState"}, at={@At(value="HEAD")})
    private void onUpdateRenderState(T param1, S param2, float param3, CallbackInfo param4) {
        this.capturedEntity = param1;
    }

    @Inject(method={"render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/client/render/entity/state/EntityRenderState;)V", shift=At.Shift.BEFORE)})
    private void minced$emotionBeforeSetAngles(S param1, MatrixStack param2, VertexConsumerProvider param3, int param4, CallbackInfo param5) {
        EntityRenderAccess.method3441(this.capturedEntity);
    }

    @Inject(method={"render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/client/render/entity/state/EntityRenderState;)V", shift=At.Shift.AFTER)})
    private void minced$emotionAfterSetAngles(S param1, MatrixStack param2, VertexConsumerProvider param3, int param4, CallbackInfo param5) {
        EntityRenderAccess.method3444();
    }

    @Inject(method={"render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/util/math/MatrixStack;pop()V", ordinal=0, shift=At.Shift.BEFORE)})
    private void onRenderAfterFeatures(S param1, MatrixStack param2, VertexConsumerProvider param3, int param4, CallbackInfo param5) {
        if (this.capturedEntity != null && this.field_4737 != null) {
            EventLayerHead v6 = new EventLayerHead(this.capturedEntity, param2, (EntityModel)this.field_4737);
            EventBus.method3015(((Object)v6));
        }
    }

    @ModifyArg(method={"render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/util/math/ColorHelper;mix(II)I"), index=0)
    private int modifyInvisibleColor(int param1) {
        SeeInvisibles v2 = (SeeInvisibles)Minced.getInstance().getModuleManager().method187(SeeInvisibles.class);
        if (this.capturedEntity != null && this.capturedEntity.isInvisible() && v2 != null && v2.isEnabled()) {
            float v3 = ((Float)v2.cfr_renamed_4().getValue()).floatValue();
            return ColorHelper.fromFloats((float)v3, (float)1.0f, (float)1.0f, (float)1.0f);
        }
        return param1;
    }

    @Override
    public void minced$render(LivingEntityRenderState param1, MatrixStack param2, VertexConsumerProvider param3, int param4) {
        boolean bl;
        boolean v62;
        Direction v132;
        S v0 = (S)param1;
        param2.push();
        if (param1.isInPose(EntityPose.SLEEPING) && (v132 = param1.sleepingDirection) != null) {
            float v62_141 = param1.standingEyeHeight - field12534;
            param2.translate((float)(-v132.getOffsetX()) * v62_141, 0.0f, (float)(-v132.getOffsetZ()) * v62_141);
        }
        float v132_144 = param1.baseScale;
        param2.scale(v132_144, v132_144, v132_144);
        this.method_4058(v0, param2, param1.bodyYaw, v132_144);
        param2.scale(field12535, field12536, 1.0f);
        this.method_4042(v0, param2);
        param2.translate(0.0f, field12537, 0.0f);
        EntityRenderAccess.method3441(this.capturedEntity);
        try {
            this.field_4737.setAngles(v0);
            v62 = this.method_4056(v0);
            bl = !v62 && !param1.invisibleToPlayer;
        } finally {
            EntityRenderAccess.method3444();
        }
        boolean v7 = bl;
        RenderLayer v8 = this.method_24302(v0, v62, v7, param1.hasOutline);
        if (v8 != null) {
            VertexConsumer v9 = param3.getBuffer(v8);
            int v10 = LivingEntityRenderer.getOverlay((LivingEntityRenderState)param1, (float)this.method_23185(v0));
            int v11 = v7 ? field12538 : -1;
            int v12 = ColorHelper.mix((int)v11, (int)this.method_62484(v0));
            this.field_4737.render(param2, v9, param4, v10, v12);
        }
        param2.pop();
    }

    static {
        VMBridge.identifyClass(LivingEntityRendererMixin.class, "bo3oGhTY");
    }
}




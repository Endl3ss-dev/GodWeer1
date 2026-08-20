/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.EntityRenderDispatcher
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.Box
 *  net.minecraft.world.WorldView
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.entity;
import minced.core.EventNoRender;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.Minced;
import minced.enums.fire;
import minced.module.combat.HitBoxes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderDispatcher.class})
public class EntityRenderDispatcherMixin {
    @Unique
    private static float getVisualExtra() {
        HitBoxes v0 = (HitBoxes)Minced.getInstance().getModuleManager().method187(HitBoxes.class);
        if (!v0.isEnabled() || !((Boolean)HitBoxes.field4743.getValue()).booleanValue()) {
            return 0.0f;
        }
        return ((Float)HitBoxes.field4741.getValue()).floatValue();
    }

    @Redirect(method={"renderHitbox"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;getBoundingBox()Lnet/minecraft/util/math/Box;"))
    private static Box expandHitboxForDebugRender(Entity param0) {
        Box v1 = param0.getBoundingBox();
        float extra = getVisualExtra();
        return extra > 0.0f ? v1.expand(extra, 0.0, extra) : v1;
    }

    @Inject(method={"render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V"}, at={@At(value="HEAD")}, cancellable=true)
    private <E extends Entity, S extends EntityRenderState> void renderEntityPlayer(E param1, double param2, double param4, double param6, float param8, MatrixStack param9, VertexConsumerProvider param10, int param11, EntityRenderer<? super E, S> param12, CallbackInfo param13) {
        if (param1 instanceof PlayerEntity && param1 != MinecraftClient.getInstance().player) {
            EventNoRender v14 = new EventNoRender(fire.players);
            EventBus.method3015(((Object)v14));
            if (v14.method2297()) {
                param13.cancel();
            }
        }
    }

    @Inject(method={"renderShadow"}, at={@At(value="HEAD")}, cancellable=true)
    private static void renderShadow(MatrixStack param0, VertexConsumerProvider param1, EntityRenderState param2, float param3, float param4, WorldView param5, float param6, CallbackInfo param7) {
        EventNoRender v8 = new EventNoRender(fire.shadows);
        EventBus.method3015(((Object)v8));
        if (v8.method2297()) {
            param7.cancel();
        }
    }
}




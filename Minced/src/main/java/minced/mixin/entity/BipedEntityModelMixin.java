/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.entity;
import minced.core.BipedModelAnimator;
import minced.core.EntityRenderAccess;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BipedEntityModel.class})
public abstract class BipedEntityModelMixin {
    @Inject(method={"setAngles(Lnet/minecraft/client/render/entity/state/BipedEntityRenderState;)V"}, at={@At(value="TAIL")})
    private void minced$applyEmotions(BipedEntityRenderState param1, CallbackInfo param2) {
        LivingEntity v3 = EntityRenderAccess.method3443();
        if (v3 instanceof PlayerEntity) {
            PlayerEntity v4 = (PlayerEntity)v3;
            BipedModelAnimator.method2437((BipedEntityModel)(Object)this, param1, v4);
            return;
        }
    }
}




/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.PersistentProjectileEntity
 *  net.minecraft.util.hit.EntityHitResult
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 *  sg.ec.Чы$Ч
 */
package minced.mixin.entity;
import minced.core.EventVelocity;
import minced.core.Event;
import minced.core.EventBus;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import sg.ec.chy;

@Mixin(value={PersistentProjectileEntity.class})
public class PersistentProjectileEntityMixin {
    @Inject(method={"onEntityHit"}, at={@At(value="INVOKE_ASSIGN", target="Lnet/minecraft/util/math/Vec3d;length()D")}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void onEntityHit(EntityHitResult param1, CallbackInfo param2, Entity param3) {
        PersistentProjectileEntity v4 = (PersistentProjectileEntity)(Object)this;
        if (v4.getWorld().isClient && param3 instanceof ClientPlayerEntity) {
            EventBus.method3015(((Object)new EventVelocity(chy.ch.ARROW)));
        }
    }
}




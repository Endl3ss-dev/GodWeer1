/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.projectile.thrown.EnderPearlEntity
 *  net.minecraft.util.hit.HitResult
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  sg.ec.Чы$Ч
 */
package minced.mixin.entity;
import minced.core.EventVelocity;
import minced.core.Event;
import minced.core.EventBus;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sg.ec.chy;

@Mixin(value={EnderPearlEntity.class})
public class EnderPearlEntityMixin {
    @Inject(method={"onCollision"}, at={@At(value="HEAD")})
    private void onEnderPearlCollision(HitResult param1, CallbackInfo param2) {
        EventBus.method3015(((Object)new EventVelocity(chy.ch.ENDER_PEARL)));
    }
}




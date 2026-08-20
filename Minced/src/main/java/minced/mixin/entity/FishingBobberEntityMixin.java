/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.FishingBobberEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.entity;
import minced.core.EventNoPush;
import minced.core.Event;
import minced.core.EventBus;
import minced.enums.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={FishingBobberEntity.class})
public class FishingBobberEntityMixin {
    @Inject(method={"pullHookedEntity"}, at={@At(value="HEAD")}, cancellable=true)
    private void onPullHookedEntity(Entity param1, CallbackInfo param2) {
        EventNoPush v3 = new EventNoPush(Block.FishingRod);
        EventBus.method3015(((Object)v3));
        if (v3.method2297()) {
            param2.cancel();
        }
    }
}




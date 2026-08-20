/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package minced.mixin.entity;
import minced.core.EventNoPush;
import minced.core.Event;
import minced.core.EventBus;
import minced.enums.Block;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={PlayerEntity.class})
public class PlayerEntityMixin {
    @Inject(method={"isPushedByFluids"}, at={@At(value="HEAD")}, cancellable=true)
    private void onIsPushedByFluid(CallbackInfoReturnable<Boolean> param1) {
        EventNoPush v2 = new EventNoPush(Block.Water);
        EventBus.method3015(((Object)v2));
        if (v2.method2297()) {
            param1.setReturnValue((Boolean)false);
        }
    }
}




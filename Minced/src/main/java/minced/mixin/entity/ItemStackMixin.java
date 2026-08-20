/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package minced.mixin.entity;
import minced.core.EventFinishUsing;
import minced.core.Event;
import minced.core.EventBus;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ItemStack.class})
public class ItemStackMixin {
    @Inject(method={"finishUsing"}, at={@At(value="HEAD")})
    private void interactBlock(World param1, LivingEntity param2, CallbackInfoReturnable<ItemStack> param3) {
        EventBus.method3015(((Object)new EventFinishUsing((ItemStack)(Object)this, param1, param2)));
    }
}




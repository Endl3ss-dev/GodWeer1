/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyReturnValue
 *  net.minecraft.entity.player.ItemCooldownManager
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Identifier
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package minced.mixin.entity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import minced.core.EventItemCooldown;
import minced.core.EventCooldownTracker;
import minced.core.Event;
import minced.core.EventBus;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value={ItemCooldownManager.class})
public class ItemCooldownManagerMixin {
    @ModifyVariable(method={"set(Lnet/minecraft/util/Identifier;I)V"}, at=@At(value="HEAD"), argsOnly=true, ordinal=0)
    private int onSetCooldown(int param1, Identifier param2) {
        EventItemCooldown v3 = new EventItemCooldown(param2, param1);
        EventBus.method3015(((Object)v3));
        return v3.method202();
    }

    @ModifyReturnValue(method={"getCooldownProgress"}, at={@At(value="RETURN")})
    private float onGetCooldownProgress(float param1, ItemStack param2, float param3) {
        EventCooldownTracker v4 = new EventCooldownTracker(param2, param1);
        EventBus.method3015(((Object)v4));
        return v4.method2402();
    }
}




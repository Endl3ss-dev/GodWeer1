/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.ClientPlayerInteractionManager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package minced.mixin.accessor;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={ClientPlayerInteractionManager.class})
public interface IClientPlayerInteractionManagerMixin {
    @Invoker(value="syncSelectedSlot")
    public void invokeSyncSelectedSlot();

    @Accessor(value="blockBreakingCooldown")
    public void setblockBreakingCooldown(int var1);

    @Accessor(value="currentBreakingProgress")
    public void setcurrentBreakingProgress(float var1);

    @Accessor(value="currentBreakingProgress")
    public float getcurrentBreakingProgress();
}




/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.ClientPlayerInteractionManager
 *  net.minecraft.util.math.BlockPos
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package minced.mixin.accessor;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={ClientPlayerInteractionManager.class})
public interface ClientPlayerInteractionManagerAccessor {
    @Invoker(value="syncSelectedSlot")
    public void invokeSyncSelectedSlot();

    @Accessor(value="blockBreakingCooldown")
    public int getBlockBreakingCooldown();

    @Accessor(value="blockBreakingCooldown")
    public void setBlockBreakingCooldown(int var1);

    @Accessor(value="currentBreakingProgress")
    public float getCurrentBreakingProgress();

    @Accessor(value="currentBreakingProgress")
    public void setCurrentBreakingProgress(float var1);

    @Accessor(value="currentBreakingPos")
    public BlockPos getCurrentBreakingPos();
}




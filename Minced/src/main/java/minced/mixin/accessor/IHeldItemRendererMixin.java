/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.item.HeldItemRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Arm
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package minced.mixin.accessor;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={HeldItemRenderer.class})
public interface IHeldItemRendererMixin {
    @Invoker(value="applyEquipOffset")
    public void invokeApplyEquipOffset(MatrixStack var1, Arm var2, float var3);

    @Invoker(value="applySwingOffset")
    public void invokeApplySwingOffset(MatrixStack var1, Arm var2, float var3);
}




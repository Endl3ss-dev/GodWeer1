/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.FloatUnaryOperator
 *  net.minecraft.client.render.RenderTickCounter$Dynamic
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package minced.mixin.client;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import minced.core.RenderTickAccess;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={RenderTickCounter.Dynamic.class})
public abstract class RenderTickCounterMixin
implements RenderTickAccess {
    @Unique
    private float speedMultiplier = 1.0f;
    private static float field12562 = 1.0f;

    @Redirect(method={"beginRenderTick(J)I"}, at=@At(value="INVOKE", target="Lit/unimi/dsi/fastutil/floats/FloatUnaryOperator;apply(F)F"))
    private float modifyTargetMillisPerTick(FloatUnaryOperator param1, float param2) {
        return param1.apply(param2) / this.speedMultiplier;
    }

    @Override
    public void setSpeedMultiplier(float param1) {
        this.speedMultiplier = Math.max(field12562, param1);
    }

    @Override
    public float getSpeedMultiplier() {
        return this.speedMultiplier;
    }

    static {
        VMBridge.identifyClass(RenderTickCounterMixin.class, "vjPCr2jB");
    }
}




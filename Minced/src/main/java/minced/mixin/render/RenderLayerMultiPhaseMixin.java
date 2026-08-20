/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.RenderLayer$MultiPhase
 *  net.minecraft.client.render.RenderLayer$MultiPhaseParameters
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package minced.mixin.render;
import minced.core.RenderLayerAccess;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={RenderLayer.MultiPhase.class})
public class RenderLayerMultiPhaseMixin
implements RenderLayerAccess {
    @Shadow
    @Final
    private RenderLayer.MultiPhaseParameters field_21403;

    @Override
    public RenderLayer.MultiPhaseParameters minced$getParameters() {
        return this.field_21403;
    }
}


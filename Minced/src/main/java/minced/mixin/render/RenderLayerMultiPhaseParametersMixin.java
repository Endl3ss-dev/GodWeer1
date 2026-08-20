/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.RenderLayer$MultiPhaseParameters
 *  net.minecraft.client.render.RenderLayer$OutlineMode
 *  net.minecraft.client.render.RenderPhase$Target
 *  net.minecraft.client.render.RenderPhase$TextureBase
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package minced.mixin.render;
import minced.core.RenderLayerParamsAccess;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={RenderLayer.MultiPhaseParameters.class})
public abstract class RenderLayerMultiPhaseParametersMixin
implements RenderLayerParamsAccess {
    @Shadow
    @Final
    private RenderPhase.Target field_21417;
    @Shadow
    @Final
    private RenderLayer.OutlineMode field_21852;
    @Shadow
    @Final
    private RenderPhase.TextureBase field_21406;

    @Override
    public RenderPhase.Target minced$getTarget() {
        return this.field_21417;
    }

    @Override
    public RenderLayer.OutlineMode minced$getOutlineMode() {
        return this.field_21852;
    }

    @Override
    public RenderPhase.TextureBase minced$getTexture() {
        return this.field_21406;
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gl.PostEffectPass
 *  net.minecraft.client.gl.PostEffectProcessor
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package minced.mixin.accessor;
import java.util.List;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={PostEffectProcessor.class})
public interface IPostEffectProcessorMixin {
    @Accessor(value="passes")
    public List<PostEffectPass> minced$getPasses();
}




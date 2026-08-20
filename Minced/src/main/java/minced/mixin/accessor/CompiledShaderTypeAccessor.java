/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gl.CompiledShader$Type
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package minced.mixin.accessor;
import net.minecraft.client.gl.CompiledShader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={CompiledShader.Type.class})
public interface CompiledShaderTypeAccessor {
    @Accessor(value="fileExtension")
    public String getFileExtension();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_765
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.Client;
import ru.decide.module.impl.render.Gamma;
import ru.decide.module.impl.render.NoRender;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_765.class})
public class LightmapTextureManagerMixin {
    @Redirect(method={"method_3313"}, at=@At(value="INVOKE", target="Ljava/lang/Double;floatValue()F", ordinal=1))
    private float night$getGammaValue(Double instance) {
        Gamma module;
        if (Client.get() != null && (module = Client.get().moduleManager().get(Gamma.class)) != null && module.isEnabled()) {
            return 200.0f;
        }
        return instance.floatValue();
    }

    @Inject(method={"method_42596"}, at={@At(value="HEAD")}, cancellable=true)
    private void removeDarknessEffect(CallbackInfoReturnable<Float> cir) {
        NoRender noRender = NoRender.getInstance();
        if (noRender != null && noRender.isEnabled() && noRender.ignoreZalupa.getValue().booleanValue()) {
            cir.setReturnValue((Object)Float.valueOf(0.0f));
        }
    }
}


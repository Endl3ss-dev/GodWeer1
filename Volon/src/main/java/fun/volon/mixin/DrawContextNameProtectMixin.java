package fun.volon.mixin;

import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.client.modules.impl.misc.NameProtect;

@Mixin(DrawContext.class)
public class DrawContextNameProtectMixin {

    @ModifyVariable(
            method = "drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private String Volon$patchStringShadow(String text) {
        return patch(text);
    }

    @ModifyVariable(
            method = "drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private String Volon$patchString(String text) {
        return patch(text);
    }

    private String patch(String text) {
        if (ModuleClass.INSTANCE == null) {
            return text;
        }

        NameProtect nameProtect = ModuleClass.INSTANCE.nameProtect;
        if (nameProtect == null || !nameProtect.isEnable()) {
            return text;
        }

        return nameProtect.patchIncomingText(text);
    }
}

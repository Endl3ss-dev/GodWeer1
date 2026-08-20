/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.text.Style
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  sg.ec.sb2
 */
package minced.mixin.screen;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import minced.core.EventNoRender;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.Minced;
import minced.enums.fire;
import minced.manager.Command;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sg.ec.sb2;

@Mixin(value={Screen.class})
public abstract class ScreenMixin {
    @Inject(method={"handleTextClick"}, at={@At(value="HEAD")}, cancellable=true)
    private void onRunCommand(Style param1, CallbackInfoReturnable<Boolean> param2) {
        Object v4 = param1.getClickEvent();
        if (v4 instanceof sb2) {
            sb2 v3 = (sb2)v4;
            v4 = v3.method_10844();
            String v5 = Minced.getInstance().getCommandManager().method74();
            Command v6 = Minced.getInstance().getCommandManager();
            try {
                if (((String)v4).startsWith(v5)) {
                    v6.method72().execute(((String)v4).substring(v5.length()), (Object)v6.method70());
                } else {
                    v6.method72().execute((String)v4, (Object)v6.method70());
                }
                param2.setReturnValue((Boolean)true);
            } catch (CommandSyntaxException commandSyntaxException) {}
        }
    }

    @Inject(method={"renderInGameBackground"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderInGameBackground(DrawContext param1, CallbackInfo param2) {
        EventNoRender v3 = new EventNoRender(fire.container_background);
        EventBus.method3015(((Object)v3));
        if (v3.method2297()) {
            param2.cancel();
        }
    }
}




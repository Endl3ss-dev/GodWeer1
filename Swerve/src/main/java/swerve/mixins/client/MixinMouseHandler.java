package swerve.mixins.client;

import swerve.utils.input.KeyBoardUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {

    // Мышиные бинды модулей. [-] Перехват клика/колеса для ViewModel удалён: он шёл через
    // заглушки, всегда возвращавшие false, а реальный редактор рук живёт в ClickGuiScreen
    // (он и так получает эти события от Screen). Отмена здесь обрезала бы островок и панели.
    @Inject(method = "onPress", at = @At("HEAD"))
    private void swerve$onMousePress(long window, int button, int action, int modifiers, CallbackInfo ci) {
        if (window != Minecraft.getInstance().getWindow().getWindow()) {
            return;
        }
        KeyBoardUtils.callMouse(button, action);
    }
}

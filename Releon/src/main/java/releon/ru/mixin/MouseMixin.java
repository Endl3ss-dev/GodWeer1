package releon.ru.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil.Type;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import releon.ru.api.drag.core.ElementManager;
import releon.ru.api.events.impl.HotBarScrollEvent;
import releon.ru.api.events.impl.KeyEvent;
import releon.ru.api.events.impl.MouseAppliedRotationEvent;
import releon.ru.api.events.impl.MouseRotationEvent;
import releon.ru.manager.Manager;
import releon.ru.screens.clickgui.ClickGui;
import releon.ru.screens.clickgui.impl.ClickGuiController;
import releon.ru.utils.modules.warden.rotation.FreeLookController;
import releon.ru.utils.render.ui.Render2DCoordinateSpace;

@Mixin({Mouse.class})
public class MouseMixin {
   @Shadow
   @Final
   private MinecraftClient client;

   public MouseMixin() {
   }

   @Inject(
      method = {"onMouseButton"},
      at = {@At("HEAD")}
   )
   private void releon$onMouseButton(long window, MouseInput buttonInfo, int action, CallbackInfo ci) {
      int button = buttonInfo.button();
      if (button != -1 && window == this.client.getWindow().getHandle()) {
         Manager.postEvent(new KeyEvent(this.client.currentScreen, Type.MOUSE, button, action));
         if (action == 0 && this.client.currentScreen instanceof ChatScreen) {
            Click event = new Click(this.client.mouse.getScaledX(this.client.getWindow()), this.client.mouse.getScaledY(this.client.getWindow()), buttonInfo);
            ElementManager.getInstance().handleMouseReleased(releon$toDragEvent(event));
         }
      }
   }

   @Inject(
      method = {"onMouseScroll"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void releon$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
      if (window == this.client.getWindow().getHandle() && this.client.currentScreen instanceof ClickGui) {
         ClickGuiController.mouseScrolled(
            this.client.mouse.getScaledX(this.client.getWindow()), this.client.mouse.getScaledY(this.client.getWindow()), horizontal, vertical
         );
         ci.cancel();
      } else if (window != this.client.getWindow().getHandle() || this.client.currentScreen == null) {
         HotBarScrollEvent event = Manager.postEvent(new HotBarScrollEvent(horizontal, vertical));
         if (event.isCancelled()) {
            ci.cancel();
         }
      }
   }

   @WrapOperation(
      method = {"updateMouse"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
      )},
      require = 0
   )
   private void releon$modifyMouseRotationInput(ClientPlayerEntity player, double cursorDeltaX, double cursorDeltaY, Operation<Void> original) {
      float beforeYaw = player.getYaw();
      float beforePitch = player.getPitch();
      MouseRotationEvent event = Manager.postEvent(new MouseRotationEvent((float)cursorDeltaX, (float)cursorDeltaY));
      if (!event.isCancelled()) {
         double deltaX = (double)event.getCursorDeltaX();
         double deltaY = (double)event.getCursorDeltaY();
         if (FreeLookController.isActive()) {
            FreeLookController.applyMouseDelta(deltaX, deltaY);
         } else {
            original.call(new Object[]{player, deltaX, deltaY});
            Manager.postEvent(
               new MouseAppliedRotationEvent(event.getCursorDeltaX(), event.getCursorDeltaY(), beforeYaw, beforePitch, player.getYaw(), player.getPitch())
            );
         }
      }
   }

   @Unique
   private static Click releon$toDragEvent(Click event) {
      float scale = Render2DCoordinateSpace.guiIndependentScale();
      if (Math.abs(scale - 1.0F) <= 1.0E-4F) {
         return event;
      } else {
         MouseInput info = new MouseInput(event.button(), event.modifiers());
         return new Click(event.x() / (double)scale, event.y() / (double)scale, info);
      }
   }
}

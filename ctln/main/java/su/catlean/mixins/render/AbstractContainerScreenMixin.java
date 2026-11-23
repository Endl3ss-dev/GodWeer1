// deobf by pusia
package su.catlean.mixins.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.render.RenderToolTipEvent;
import su.catlean.api.event.events.render.SlotRenderEvent;

@Mixin({AbstractContainerScreen.class})
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {

   @Shadow
   @Nullable
   protected Slot hoveredSlot;
   @Shadow
   protected int leftPos;
   @Shadow
   protected int topPos;

   protected AbstractContainerScreenMixin(Component component) {
      super(component);
   }

   @Inject(
      method = {"extractRenderState"},
      at = {@At("TAIL")}
   )
   private void renderHook(GuiGraphicsExtractor guiGraphicsExtractor, int value, int value2, float amount, CallbackInfo callbackInfo) {
      if (this.hoveredSlot != null && !this.hoveredSlot.getItem().isEmpty() && Minecraft.getInstance().player.inventoryMenu.getCarried().isEmpty()) {
         RenderToolTipEvent.INSTANCE.call(this.hoveredSlot.getItem(), guiGraphicsExtractor, value, value2);
      }
   }

   @Inject(
      method = {"extractSlot(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/inventory/Slot;II)V"},
      at = {@At("HEAD")}
   )
   protected void drawSlotHook(GuiGraphicsExtractor guiGraphicsExtractor, Slot slot, int value, int value2, CallbackInfo callbackInfo) {
      SlotRenderEvent.INSTANCE.setSlot(slot);
      SlotRenderEvent.INSTANCE.setContext(guiGraphicsExtractor);
      SlotRenderEvent.INSTANCE.call();
   }
}
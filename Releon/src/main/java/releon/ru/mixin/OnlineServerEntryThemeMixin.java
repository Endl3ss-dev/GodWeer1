package releon.ru.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import releon.ru.mixin.accessor.AbstractSelectionListAccessor;
import releon.ru.screens.mainmenu.DraggableListEntryAccess;
import releon.ru.screens.mainmenu.VanillaMenuTheme;

@Mixin(
   targets = {"net/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget$ServerEntry"}
)
public abstract class OnlineServerEntryThemeMixin implements DraggableListEntryAccess {
   @Unique
   private boolean releon$mouseDown;
   @Unique
   private boolean releon$dragging;
   @Unique
   private double releon$pressY;
   @Unique
   private double releon$grabX;
   @Unique
   private double releon$grabY;
   @Unique
   private double releon$cursorX;
   @Unique
   private double releon$cursorY;
   @Shadow
   @Final
   private MultiplayerScreen screen;
   @Shadow
   @Final
   private MultiplayerServerListWidget serverListWidget;

   public OnlineServerEntryThemeMixin() {
   }

   @Redirect(
      method = {"render"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"
      )
   )
   private void releon$alignPlayerCountWithPing(DrawContext graphics, TextRenderer font, Text text, int x, int y, int color) {
      int vanillaRightEdge = x + font.getWidth(text);
      VanillaMenuTheme.drawRightAlignedText(graphics, text.getString(), (float)vanillaRightEdge, (float)y, color);
   }

   @Redirect(
      method = {"render"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"
      )
   )
   private void releon$hideVanillaIconHover(DrawContext graphics, int left, int top, int right, int bottom, int color) {
   }

   @Inject(
      method = {"mouseClicked"},
      at = {@At("HEAD")}
   )
   private void releon$startDragging(Click event, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
      if (VanillaMenuTheme.active() && event.button() == 0) {
         this.releon$mouseDown = true;
         this.releon$dragging = false;
         this.releon$pressY = event.y();
         Widget entry = (Widget)(Object)this;
         this.releon$grabX = event.x() - (double)entry.getX();
         this.releon$grabY = event.y() - (double)entry.getY();
         this.releon$cursorX = event.x();
         this.releon$cursorY = event.y();
      }
   }

   public boolean mouseDragged(Click event, double dragX, double dragY) {
      if (this.releon$mouseDown && event.button() == 0) {
         if (!this.releon$dragging && Math.abs(event.y() - this.releon$pressY) < 3.0) {
            return false;
         } else {
            this.releon$dragging = true;
            this.releon$cursorX = event.x();
            this.releon$cursorY = event.y();
            int index = this.serverListWidget.children().indexOf(this);
            if (index < 0) {
               return true;
            } else {
               int target = index;
               Widget entry = (Widget)(Object)this;
               double draggedCenter = this.releon$cursorY - this.releon$grabY + (double)entry.getHeight() * 0.5;
               if (draggedCenter < (double)(Object)this.serverListWidget.getRowTop(index) && index > 0) {
                  target = index - 1;
               } else if (draggedCenter > (double)(Object)this.serverListWidget.getRowBottom(index) && index < this.screen.getServerList().size() - 1) {
                  target = index + 1;
               }

               if (target != index) {
                  double scroll = this.serverListWidget.getScrollY();
                  this.screen.getServerList().swapEntries(index, target);
                  ((AbstractSelectionListAccessor)(Object)this.serverListWidget).releon$swapEntries(index, target);
                  this.serverListWidget.setScrollY(scroll);
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public boolean mouseReleased(Click event) {
      boolean handled = this.releon$dragging;
      this.releon$mouseDown = false;
      this.releon$dragging = false;
      return handled;
   }

   @Override
   public boolean releon$isDragging() {
      return this.releon$dragging;
   }

   @Override
   public float releon$getDragOffsetX() {
      return !this.releon$dragging ? 0.0F : (float)(this.releon$cursorX - this.releon$grabX);
   }

   @Override
   public float releon$getDragOffsetY() {
      return !this.releon$dragging ? 0.0F : (float)(this.releon$cursorY - this.releon$grabY);
   }
}


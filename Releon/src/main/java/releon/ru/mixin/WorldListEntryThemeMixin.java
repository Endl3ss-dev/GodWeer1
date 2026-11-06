package releon.ru.mixin;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.world.level.storage.LevelSummary;
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
import releon.ru.screens.mainmenu.WorldListEntryOrderAccess;
import releon.ru.screens.mainmenu.WorldOrderStore;

@Mixin(
   targets = {"net/minecraft/client/gui/screen/world/WorldListWidget$WorldEntry"}
)
public abstract class WorldListEntryThemeMixin implements WorldListEntryOrderAccess, DraggableListEntryAccess {
   @Shadow
   @Final
   private WorldListWidget parent;
   @Shadow
   @Final
   private LevelSummary level;
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

   public WorldListEntryThemeMixin() {
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
            int index = this.parent.children().indexOf(this);
            if (index < 0) {
               return true;
            } else {
               int target = index;
               Widget entry = (Widget)(Object)this;
               double draggedCenter = this.releon$cursorY - this.releon$grabY + (double)entry.getHeight() * 0.5;
               if (draggedCenter < (double)(Object)this.parent.getRowTop(index)
                  && index > 0
                  && this.parent.children().get(index - 1) instanceof WorldListEntryOrderAccess) {
                  target = index - 1;
               } else if (draggedCenter > (double)(Object)this.parent.getRowBottom(index)
                  && index + 1 < this.parent.children().size()
                  && this.parent.children().get(index + 1) instanceof WorldListEntryOrderAccess) {
                  target = index + 1;
               }

               if (target != index) {
                  double scroll = this.parent.getScrollY();
                  ((AbstractSelectionListAccessor)(Object)this.parent).releon$swapEntries(index, target);
                  this.parent.setScrollY(scroll);
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
      if (handled) {
         WorldOrderStore.save(this.parent);
      }

      return handled;
   }

   @Override
   public String releon$getLevelId() {
      return this.level.getName();
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


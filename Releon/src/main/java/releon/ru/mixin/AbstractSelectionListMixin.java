package releon.ru.mixin;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import releon.ru.screens.mainmenu.DraggableListEntryAccess;
import releon.ru.screens.mainmenu.VanillaMenuTheme;

@Mixin({EntryListWidget.class})
public abstract class AbstractSelectionListMixin {
   @Unique
   private final Map<Object, Float> releon$entryVisibility = new IdentityHashMap<>();
   @Unique
   private final Map<Object, AbstractSelectionListMixin.CardSnapshot> releon$lastCards = new IdentityHashMap<>();
   @Unique
   private final Map<Object, Float> releon$dragOffsetX = new IdentityHashMap<>();
   @Unique
   private final Map<Object, Float> releon$dragOffsetY = new IdentityHashMap<>();
   @Unique
   private final Map<Object, Float> releon$entryVisualY = new IdentityHashMap<>();
   @Unique
   private final List<AbstractSelectionListMixin.FadingCard> releon$fadingCards = new ArrayList<>();
   @Unique
   private long releon$lastFrameNanos;
   @Unique
   private float releon$deltaSeconds;
   @Unique
   private boolean releon$posePushed;

   public AbstractSelectionListMixin() {
   }

   @Inject(
      method = {"drawMenuListBackground"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void releon$renderThemedListBackground(DrawContext graphics, CallbackInfo ci) {
      if (VanillaMenuTheme.active()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"drawHeaderAndFooterSeparators"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void releon$hideVanillaListSeparators(DrawContext graphics, CallbackInfo ci) {
      if (VanillaMenuTheme.active()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"renderList"},
      at = {@At("HEAD")}
   )
   private void releon$prepareListAnimations(DrawContext graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
      if (!VanillaMenuTheme.active()) {
         this.releon$entryVisibility.clear();
         this.releon$lastCards.clear();
         this.releon$fadingCards.clear();
         this.releon$dragOffsetX.clear();
         this.releon$dragOffsetY.clear();
         this.releon$entryVisualY.clear();
      } else {
         long now = System.nanoTime();
         this.releon$deltaSeconds = this.releon$lastFrameNanos == 0L ? 0.0F : Math.min(0.05F, (float)(now - this.releon$lastFrameNanos) / 1.0E9F);
         this.releon$lastFrameNanos = now;
         EntryListWidget<?> list = (EntryListWidget<?>)(Object)this;
         List<?> currentEntries = list.children();
         Iterator<Entry<Object, AbstractSelectionListMixin.CardSnapshot>> snapshots = this.releon$lastCards.entrySet().iterator();

         while (snapshots.hasNext()) {
            Entry<Object, AbstractSelectionListMixin.CardSnapshot> snapshot = snapshots.next();
            boolean stillPresent = currentEntries.stream().anyMatch(current -> current == snapshot.getKey());
            if (!stillPresent) {
               this.releon$fadingCards.add(new AbstractSelectionListMixin.FadingCard(snapshot.getValue(), 1.0F));
               this.releon$entryVisibility.remove(snapshot.getKey());
               this.releon$dragOffsetX.remove(snapshot.getKey());
               this.releon$dragOffsetY.remove(snapshot.getKey());
               this.releon$entryVisualY.remove(snapshot.getKey());
               snapshots.remove();
            }
         }

         Iterator<AbstractSelectionListMixin.FadingCard> fading = this.releon$fadingCards.iterator();

         while (fading.hasNext()) {
            AbstractSelectionListMixin.FadingCard card = fading.next();
            card.visibility = Math.max(0.0F, card.visibility - this.releon$deltaSeconds * 4.8F);
            if (card.visibility <= 0.01F) {
               fading.remove();
            } else {
               AbstractSelectionListMixin.CardSnapshot snapshot = card.snapshot;
               VanillaMenuTheme.renderListCard(graphics, snapshot.x, snapshot.y, snapshot.width, snapshot.height, false, snapshot.selected, card.visibility);
            }
         }
      }
   }

   @Inject(
      method = {"renderEntry"},
      at = {@At("HEAD")}
   )
   private void releon$renderThemedListCard(DrawContext graphics, int mouseX, int mouseY, float partialTick, @Coerce Object entry, CallbackInfo ci) {
      if (VanillaMenuTheme.active()) {
         EntryListWidget<?> list = (EntryListWidget<?>)(Object)this;
         if (!(list instanceof OptionListWidget)) {
            int index = list.children().indexOf(entry);
            if (index >= 0) {
               int x = list.getRowLeft();
               int y = list.getRowTop(index);
               int width = list.getRowWidth();
               int height = Math.max(1, list.getRowBottom(index) - y);
               boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
               boolean selected = list.getSelectedOrNull() == entry;
               float dragX = 0.0F;
               float dragY = 0.0F;
               if (entry instanceof DraggableListEntryAccess draggable) {
                  float visualY = this.releon$entryVisualY.getOrDefault(entry, (float)y);
                  float slotResponse = Math.min(1.0F, this.releon$deltaSeconds * 13.0F);
                  visualY = lerp(visualY, (float)y, slotResponse);
                  this.releon$entryVisualY.put(entry, visualY);
                  float slotOffsetY = visualY - (float)y;
                  float targetX = draggable.releon$isDragging() ? draggable.releon$getDragOffsetX() - (float)x : 0.0F;
                  float targetY = draggable.releon$isDragging() ? draggable.releon$getDragOffsetY() - (float)y : slotOffsetY;
                  float response = Math.min(1.0F, this.releon$deltaSeconds * (draggable.releon$isDragging() ? 30.0F : 12.0F));
                  dragX = lerp(this.releon$dragOffsetX.getOrDefault(entry, 0.0F), targetX, response);
                  dragY = lerp(this.releon$dragOffsetY.getOrDefault(entry, 0.0F), targetY, response);
                  this.releon$dragOffsetX.put(entry, dragX);
                  this.releon$dragOffsetY.put(entry, dragY);
                  hovered |= draggable.releon$isDragging();
               }

               float visibility = Math.min(1.0F, this.releon$entryVisibility.getOrDefault(entry, 0.0F) + this.releon$deltaSeconds * 4.6F);
               this.releon$entryVisibility.put(entry, visibility);
               this.releon$lastCards.put(entry, new AbstractSelectionListMixin.CardSnapshot((float)x, (float)y, (float)width, (float)height, selected));
               float smooth = visibility * visibility * (3.0F - 2.0F * visibility);
               float scale = 0.88F + smooth * 0.12F;
               float centerX = (float)x + (float)width * 0.5F;
               float centerY = (float)y + (float)height * 0.5F;
               graphics.getMatrices().pushMatrix();
               graphics.getMatrices().translate(dragX, dragY);
               graphics.getMatrices().translate(centerX, centerY);
               graphics.getMatrices().scale(scale, scale);
               graphics.getMatrices().translate(-centerX, -centerY);
               this.releon$posePushed = true;
               VanillaMenuTheme.renderListCard(graphics, (float)x, (float)y, (float)width, (float)height, hovered, selected, visibility);
            }
         }
      }
   }

   @Inject(
      method = {"renderEntry"},
      at = {@At("TAIL")}
   )
   private void releon$finishThemedListCard(DrawContext graphics, int mouseX, int mouseY, float partialTick, @Coerce Object entry, CallbackInfo ci) {
      if (this.releon$posePushed) {
         graphics.getMatrices().popMatrix();
         this.releon$posePushed = false;
      }
   }

   @Inject(
      method = {"drawSelectionHighlight"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void releon$hideVanillaSelection(DrawContext graphics, @Coerce Object entry, int y, CallbackInfo ci) {
      if (VanillaMenuTheme.active()) {
         ci.cancel();
      }
   }

   @Unique
   private static float lerp(float current, float target, float amount) {
      return current + (target - current) * amount;
   }

   @Unique
   private static record CardSnapshot(float x, float y, float width, float height, boolean selected) {
   }

   @Unique
   private static final class FadingCard {
      private final AbstractSelectionListMixin.CardSnapshot snapshot;
      private float visibility;

      private FadingCard(AbstractSelectionListMixin.CardSnapshot snapshot, float visibility) {
         this.snapshot = snapshot;
         this.visibility = visibility;
      }
   }
}


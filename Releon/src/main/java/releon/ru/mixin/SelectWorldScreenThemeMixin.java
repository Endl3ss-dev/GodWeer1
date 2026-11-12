package releon.ru.mixin;

import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SelectWorldScreen.class})
public abstract class SelectWorldScreenThemeMixin {
   private static final int RELEON_SEARCH_WIDTH = 140;
   private static final int RELEON_SEARCH_HEIGHT = 14;
   @Shadow
   protected TextFieldWidget searchBox;

   public SelectWorldScreenThemeMixin() {
   }

   @Inject(
      method = {"init"},
      at = {@At("TAIL")}
   )
   private void releon$positionSearchAtBottom(CallbackInfo ci) {
      this.repositionSearch();
   }

   @Inject(
      method = {"refreshWidgetPositions"},
      at = {@At("TAIL")}
   )
   private void releon$keepSearchAtBottom(CallbackInfo ci) {
      this.repositionSearch();
   }

   private void repositionSearch() {
      if (this.searchBox != null) {
         SelectWorldScreen screen = (SelectWorldScreen)(Object)this;
         this.searchBox.setX((screen.width - 140) / 2);
         this.searchBox.setY(screen.height - 78);
         this.searchBox.setDimensions(140, 14);
      }
   }
}


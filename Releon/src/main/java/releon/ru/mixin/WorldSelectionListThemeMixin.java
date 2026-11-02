package releon.ru.mixin;

import java.util.List;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import releon.ru.screens.mainmenu.WorldOrderStore;

@Mixin({WorldListWidget.class})
public abstract class WorldSelectionListThemeMixin {
   public WorldSelectionListThemeMixin() {
   }

   @Inject(
      method = {"showSummaries"},
      at = {@At("TAIL")}
   )
   private void releon$restoreWorldOrder(String filter, List<?> levels, CallbackInfo ci) {
      WorldOrderStore.apply((WorldListWidget)(Object)this);
   }
}


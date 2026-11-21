package releon.ru.mixin;

import net.minecraft.client.gui.render.state.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import releon.ru.access.GuiRenderStateLayerAccessor;

@Mixin({GuiRenderState.class})
public abstract class GuiRenderStateMixin implements GuiRenderStateLayerAccessor {
   @Unique
   private int releon$layerSerial;

   public GuiRenderStateMixin() {
   }

   @Override
   public int releon$getLayerSerial() {
      return this.releon$layerSerial;
   }

   @Inject(
      method = {"createNewRootLayer"},
      at = {@At("RETURN")}
   )
   private void releon$trackNextStratum(CallbackInfo ci) {
      this.releon$layerSerial++;
   }

   @Inject(
      method = {"goUpLayer"},
      at = {@At("RETURN")}
   )
   private void releon$trackUpLayer(CallbackInfo ci) {
      this.releon$layerSerial++;
   }

   @Inject(
      method = {"clear"},
      at = {@At("HEAD")}
   )
   private void releon$resetLayerSerial(CallbackInfo ci) {
      this.releon$layerSerial = 0;
   }
}

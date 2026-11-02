package sg.mx;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.DimensionEffects.SkyType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.destra.core.DestraClient;
import ru.destra.module.WorldCustomizerModule;
import ru.destra.render.SkyShaderRenderer;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererSkyShaderMixin {
   @Shadow
   @Final
   private MinecraftClient client;

   @Inject(method = "method_62215", at = @At("HEAD"), cancellable = true)
   private void destra$cancelSky(Fog var1, SkyType var2, float var3, DimensionEffects var4, CallbackInfo var5) {
      WorldCustomizerModule var6 = destra$getCustomizer();
      if (var6 != null && var6.й() && this.client.player != null && this.client.world != null && !this.client.gameRenderer.isRenderingPanorama()) {
         var5.cancel();
      }
   }

   @Inject(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw()V", shift = Shift.AFTER))
   private void destra$renderSkyShader(Fog var1, SkyType var2, float var3, DimensionEffects var4, CallbackInfo var5) {
      WorldCustomizerModule var6 = destra$getCustomizer();
      if (var6 != null
         && var6.isSkyShaderVisible()
         && this.client.player != null
         && this.client.world != null
         && !this.client.gameRenderer.isRenderingPanorama()) {
         SkyShaderRenderer.renderSky(var6);
      }
   }

   @Inject(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/SkyRendering;renderEndSky()V", shift = Shift.AFTER))
   private void destra$renderEndSkyShader(Fog var1, SkyType var2, float var3, DimensionEffects var4, CallbackInfo var5) {
      WorldCustomizerModule var6 = destra$getCustomizer();
      if (var6 != null
         && var6.isSkyShaderVisible()
         && this.client.player != null
         && this.client.world != null
         && !this.client.gameRenderer.isRenderingPanorama()) {
         SkyShaderRenderer.renderSky(var6);
      }
   }

   @Unique
   private static WorldCustomizerModule destra$getCustomizer() {
      return DestraClient.getInstance() != null && DestraClient.getInstance().getModuleManager() != null
         ? DestraClient.getInstance().getModuleManager().worldCustomizer
         : null;
   }
}

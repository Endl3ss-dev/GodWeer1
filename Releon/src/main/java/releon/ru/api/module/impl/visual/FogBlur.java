package releon.ru.api.module.impl.visual;

import net.minecraft.client.gl.Framebuffer;
import releon.ru.api.module.Module;
import releon.ru.api.module.ModuleCategory;
import releon.ru.api.settings.impl.ModeSetting;
import releon.ru.api.settings.impl.NumberSetting;
import releon.ru.utils.render.post.fogblur.FogBlurRenderer;

public class FogBlur extends Module {
   private static FogBlur instance;
   private final NumberSetting strength = this.register(new NumberSetting("Strength", "Strength of the distance blur.", 15.0, 5.0, 20.0, 1.0));
   private final NumberSetting distance = new NumberSetting("Distance", "Distance in blocks where the fog blur becomes visible.", 250.0, 100.0, 250.0, 1.0);
   private final ModeSetting quality = this.register(new ModeSetting("Quality", "Render scale and pass limit.", "Balanced", "Balanced", "Quality"));

   public FogBlur() {
      super("Fog Blur", "Blurs distant world pixels using depth, like Schizoid fog blur.", ModuleCategory.VISUAL);
      instance = this;
   }

   public static FogBlur getInstance() {
      return instance;
   }

   @Override
   protected void onDisable() {
      FogBlurRenderer.clear();
   }

   public void onAfterTranslucent(Framebuffer renderTarget) {
      if (mc.player != null && mc.world != null && mc.gameRenderer != null) {
         if (!FogBlurRenderer.isDisabledAfterError()) {
            FogBlurRenderer.apply(renderTarget, this.strength.getFloat(), this.distance.getFloat(), 100.0F, this.renderScale(), this.maxPasses());
         }
      }
   }

   private float renderScale() {
      return this.quality.is("Quality") ? 0.68F : 0.52F;
   }

   private int maxPasses() {
      return this.quality.is("Quality") ? 3 : 2;
   }
}

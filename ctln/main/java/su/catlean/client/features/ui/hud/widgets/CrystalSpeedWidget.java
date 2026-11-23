// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.DamageComparatorResult;
import su.catlean.api.data.DamageData;
import su.catlean.client.features.modules.attack.AutoCrystal;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.render.GradientQuad;

public final class CrystalSpeedWidget extends Widget {
   @NotNull
   public static final CrystalSpeedWidget INSTANCE = new CrystalSpeedWidget();

   private CrystalSpeedWidget() {
      super("crystal-speed-widget");
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      float amount;
      int value;
      long time;
      long time2;
      label21: {
         value = GradientQuad.INSTANCE.getValue();
         time = AutoCrystal.INSTANCE.getTime();
         time2 = AutoCrystal.INSTANCE.getTime2();
         DamageData atomicReferenceItem = (DamageData)AutoCrystal.INSTANCE.getAtomicReference().get();
         if (atomicReferenceItem != null) {
            DamageComparatorResult damageData = atomicReferenceItem.getDamageData();
            if (damageData != null) {
               amount = damageData.getAmount();
               break label21;
            }
         }

         amount = 0.0F;
      }

      float amount2;
      label16: {
         DamageData atomicReferenceItem2 = (DamageData)AutoCrystal.INSTANCE.getAtomicReference().get();
         if (atomicReferenceItem2 != null) {
            DamageComparatorResult damageData2 = atomicReferenceItem2.getDamageData();
            if (damageData2 != null) {
               amount2 = damageData2.getAmount2();
               break label16;
            }
         }

         amount2 = 0.0F;
      }

      float value2 = (int)(amount / amount2 * 10) / 10.0F;
      long value3 = time2;
      long value4 = time;
      this.render(context, value + " c/s | " + value4 + " ping | " + value3 + " cpu | " + value2 + " ef.");
   }
}
// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import java.math.BigDecimal;
import kotlin.math.MathKt;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;

public final class FloatComponent extends SliderComponent {
   public FloatComponent(Setting setting) {
      super(setting);
   }

   @Override
   public void applyClamp(int value) {
      if (this.isFlag()) {
         float value2 = Math.clamp((value - this.getAmount3()) / (this.getAmount5() - 7.0F), 0.0F, 1.0F);
         float value3 = ((Number)this.getClosedFloatingPointRange().getStart()).floatValue()
            + (
                  ((Number)this.getClosedFloatingPointRange().getEndInclusive()).floatValue()
                     - ((Number)this.getClosedFloatingPointRange().getStart()).floatValue()
               )
               * value2;
         float value4 = ((Number)this.getClosedFloatingPointRange().getEndInclusive()).floatValue() > 1.0F ? 10.0F : 100.0F;
         this.getSetting().setObject2(MathKt.roundToInt(value3 * value4) / value4);
      }
   }

   @Override
   public float getAmount() {
      return Math.clamp(
         (((Number)this.getSetting().getObject2()).floatValue() - ((Number)this.getClosedFloatingPointRange().getStart()).floatValue())
            / (
               ((Number)this.getClosedFloatingPointRange().getEndInclusive()).floatValue()
                  - ((Number)this.getClosedFloatingPointRange().getStart()).floatValue()
            ),
         0.0F,
         1.0F
      );
   }

   @NotNull
   public Float computeFloatValue(String value) {
      return Float.parseFloat(value);
   }

   @Override
   public void update() {
      this.setFlag(false);
   }

   @NotNull
   public Float computeFloatValue(float amount, int value) {
      return new BigDecimal(String.valueOf(value * 0.1))
         .add(new BigDecimal(String.valueOf(((Number)this.getSetting().getObject2()).floatValue())))
         .floatValue();
   }

   @Override
   public Number computeNumber(String text) {
      return this.computeFloatValue(text);
   }

   @Override
   public Number computeNumber(Number number, int value) {
      return this.computeFloatValue(number.floatValue(), value);
   }
}
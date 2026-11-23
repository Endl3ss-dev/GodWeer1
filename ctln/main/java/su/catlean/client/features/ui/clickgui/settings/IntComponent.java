// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;

public final class IntComponent extends SliderComponent {
   public IntComponent(Setting setting) {
      super(setting);
   }

   @Override
   public void applyClamp(int value) {
      if (this.isFlag()) {
         float value2 = Math.clamp((value - this.getAmount3()) / (this.getAmount5() - 7.0F), 0.0F, 1.0F);
         this.getSetting().setObject2(MathKt.roundToInt(this.getIntRange().getFirst() + (this.getIntRange().getLast() - this.getIntRange().getFirst()) * value2));
      }
   }

   @Override
   public float getAmount() {
      return Math.clamp(
         ((float)((Number)this.getSetting().getObject2()).intValue() - this.getIntRange().getFirst())
            / (this.getIntRange().getLast() - this.getIntRange().getFirst()),
         0.0F,
         1.0F
      );
   }

   @NotNull
   public Integer computeInteger(String value) {
      return Integer.parseInt(value);
   }

   @NotNull
   public Integer computeInteger(int value, int value2) {
      return RangesKt.coerceIn(value + value2, this.getIntRange().getFirst(), this.getIntRange().getLast());
   }

   @Override
   public void update() {
      this.setFlag(false);
   }

   @Override
   public Number computeNumber(String text) {
      return this.computeInteger(text);
   }

   @Override
   public Number computeNumber(Number number, int value) {
      return this.computeInteger(number.intValue(), value);
   }
}
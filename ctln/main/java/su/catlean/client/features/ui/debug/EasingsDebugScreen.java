// deobf by pusia
package su.catlean.client.features.ui.debug;

import java.awt.Color;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.math.MathKt;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.services.NotificationManager;

public final class EasingsDebugScreen extends Screen {
   private final float amount = 210.0F;
   private final float amount2 = 8.0F;
   @NotNull
   private NotificationManager.Notification.Type type = NotificationManager.Notification.Type.LINEAR;
   private long time = 900L;
   private boolean flag = true;
   private boolean flag2 = true;
   private int value = 120;
   @NotNull
   private final Color color = new Color(-15724528);
   @NotNull
   private final Color color2 = new Color(-15329770);
   @NotNull
   private final Color color3 = new Color(-1);
   @NotNull
   private final Color color4 = new Color(-5197648);
   @NotNull
   private final Color color5 = new Color(-14013910);
   @NotNull
   private final Color color6 = new Color(-14737633);
   @NotNull
   private final Color color7 = new Color(-13882324);
   private long time2;
   @NotNull
   private final List items = CollectionsKt.toList((Iterable)NotificationManager.Notification.Type.getEnumEntries());

   public EasingsDebugScreen() {
      super(Component.nullToEmpty("test"));
   }

   @NotNull
   public final NotificationManager.Notification.Type getType() {
      return this.type;
   }

   public final long getTime() {
      return this.time;
   }

   public final void setTime(long time) {
      this.time = time;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   public final boolean isFlag2() {
      return this.flag2;
   }

   public final void setFlag2(boolean flag) {
      this.flag2 = flag;
   }

   public final int getValue() {
      return this.value;
   }

   public final void setValue(int value) {
      this.value = value;
   }

   @NotNull
   public final Color getColor() {
      return this.color;
   }

   @NotNull
   public final Color getColor2() {
      return this.color2;
   }

   @NotNull
   public final Color getColor3() {
      return this.color3;
   }

   @NotNull
   public final Color getColor4() {
      return this.color4;
   }

   @NotNull
   public final Color getColor5() {
      return this.color5;
   }

   @NotNull
   public final Color getColor6() {
      return this.color6;
   }

   @NotNull
   public final Color getColor7() {
      return this.color7;
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean flag) {
      int value2 = (int)click.x();
      int value3 = (int)click.y();
      int amount22 = (int)this.amount2 + 18 + 56 + 18;
      byte byteValue = 14;
      if ((0 <= value2 ? value2 < (int)this.amount : false) && value3 >= amount22) {
         int value4 = (value3 - amount22) / byteValue;
         if (0 <= value4 ? value4 < this.items.size() : false) {
            this.type = (NotificationManager.Notification.Type)this.items.get(value4);
         }
      }

      return super.mouseClicked(click, flag);
   }

   private final double computeAmount(double amount) {
      return amount;
   }

   private final String computeText(double amount) {
      return String.valueOf(MathKt.roundToInt(amount * 1000.0) / 1000.0);
   }
}
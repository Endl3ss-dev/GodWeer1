// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.modules.esp.PlayerListNames;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.features.ui.hud.WidgetEditorScreen;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ChatStaffScanner;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class StaffWidget extends Widget {
   @NotNull
   public static final StaffWidget INSTANCE = new StaffWidget();
   @NotNull
   private static final List<StaffWidget.Staff> items = new ArrayList();
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_BACK, 250L);

   private StaffWidget() {
      super("staff-widget");
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      for (PlayerListNames items2Item : (Iterable<PlayerListNames>)ChatStaffScanner.INSTANCE.getItems2()) {
         var items2 = items;
         boolean flag2;
         if (items2 instanceof Collection && ((Collection)items2).isEmpty()) {
            flag2 = true;
         } else {
            Iterator<StaffWidget.Staff> iterator = items2.iterator();

            while (true) {
               if (!iterator.hasNext()) {
                  flag2 = true;
                  break;
               }

               StaffWidget.Staff object = iterator.next();
               StaffWidget.Staff staff = (StaffWidget.Staff)object;
               if (Intrinsics.areEqual(staff.getName(), items2Item.getText3())) {
                  flag2 = false;
                  break;
               }
            }
         }

         boolean flag4 = flag2;
         if (flag4) {
            items.add(new StaffWidget.Staff(items2Item.getText3(), null, false, 6, null));
         }
      }

      Iterable<StaffWidget.Staff> items3 = items;
      boolean flag5 = false;

      for (Object object2 : items3) {
         StaffWidget.Staff staff2 = (StaffWidget.Staff)object2;
         var items22 = ChatStaffScanner.INSTANCE.getItems2();
         boolean flag8;
         if (items22 instanceof Collection && ((Collection)items22).isEmpty()) {
            flag8 = true;
         } else {
            Iterator iterator2 = items22.iterator();

            while (true) {
               if (!iterator2.hasNext()) {
                  flag8 = true;
                  break;
               }

               Object object3 = iterator2.next();
               PlayerListNames playerListNames = (PlayerListNames)object3;
               if (Intrinsics.areEqual(playerListNames.getText3(), staff2.getName())) {
                  flag8 = false;
                  break;
               }
            }
         }

         if (flag8) {
            staff2.setFlag(true);
         }
      }

      items.removeIf(arg -> {
         ((StaffWidget.Staff)arg).getAnimation().applyDoubleValue(((StaffWidget.Staff)arg).isFlag() ? 0.0F : 1.0F);
         return ((StaffWidget.Staff)arg).isFlag() && ((StaffWidget.Staff)arg).getAnimation().getAmount() <= 0.0F;
      });
      String text = "Staff";
      flag5 = this.getAmount3() > this.getValue2() / 2.0F;
      animation.applyDoubleValue(
         items.isEmpty()
               && !(MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen)
               && !(MinecraftHolder.getMinecraft().gui.screen() instanceof WidgetEditorScreen)
            ? 0.0F
            : 1.0F
      );
      this.render(context, text, animation.getAmount(), flag5);
      float value = 17.0F;
      float amount = Fonts.getFontRenderer().computeAmount(text) + 24.0F;

      for (StaffWidget.Staff item : (Iterable<su.catlean.client.features.ui.hud.widgets.StaffWidget.Staff>)items) {
         float amount2 = Fonts.getFontRenderer().computeAmount(StringsKt.trim(item.getName()).toString());
         if (amount2 + 7.0F > amount) {
            amount = amount2 + 7.0F;
         }
      }

      for (StaffWidget.Staff item2 : (Iterable<su.catlean.client.features.ui.hud.widgets.StaffWidget.Staff>)items) {
         float amount3 = Fonts.getFontRenderer().computeAmount(StringsKt.trim(item2.getName()).toString());
         float amount32 = flag5
            ? this.getAmount3() - (1.0F - item2.getAnimation().getAmount()) * -10 - (amount3 + 8.0F - amount)
            : this.getAmount3() - (1.0F - item2.getAnimation().getAmount()) * 10;
         Widget.defaultImpl(this, context.pose(), amount32, this.getAmount4() + value, amount3 + 7.0F, 13.0F, item2.getAnimation().getAmount(), false, 64, null);
         this.render(
            context,
            StringsKt.trim(item2.getName()).toString(),
            amount32 + 3.0F,
            this.getAmount4() + value + 1.0F,
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor7(), item2.getAnimation().getAmount())
         );
         value += 14.0F * item2.getAnimation().getAmount();
      }

      this.updateAmount(amount, 13.0F + value);
   }

   public static final class Staff {
      @NotNull
      private final String name;
      @NotNull
      private final Animation animation;
      private boolean flag;

      public Staff(String name, Animation animation, boolean flag) {
         this.name = name;
         this.animation = animation;
         this.flag = flag;
      }

      public Staff(String text, Animation animation, boolean flag, int value, DefaultConstructorMarker defaultConstructorMarker) {
         if ((value & 2) != 0) {
            animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 250L);
         }

         if ((value & 4) != 0) {
            flag = false;
         }

         this(text, animation, flag);
      }

      @NotNull
      public final String getName() {
         return this.name;
      }

      @NotNull
      public final Animation getAnimation() {
         return this.animation;
      }

      public final boolean isFlag() {
         return this.flag;
      }

      public final void setFlag(boolean flag) {
         this.flag = flag;
      }

      @NotNull
      public final String getName2() {
         return this.name;
      }

      @NotNull
      public final Animation getAnimation2() {
         return this.animation;
      }

      public final boolean isFlag2() {
         return this.flag;
      }

      @NotNull
      @Override
      public String toString() {
         boolean flag2 = this.flag;
         Animation animation2 = this.animation;
         String name2 = this.name;
         return "Staff(name=" + name2 + ", animation=" + animation2 + ", removing=" + flag2 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.name.hashCode();
         hashCode = hashCode * 31 + this.animation.hashCode();
         return hashCode * 31 + Boolean.hashCode(this.flag);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof StaffWidget.Staff staff)) {
            return false;
         } else if (!Intrinsics.areEqual(this.name, staff.name)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.animation, staff.animation) ? false : this.flag == staff.flag;
         }
      }
   }
}
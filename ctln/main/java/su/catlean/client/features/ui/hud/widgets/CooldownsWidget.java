// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.features.ui.hud.WidgetEditorScreen;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.LocalizationManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.mixins.accessors.CooldownInstanceAccessor;
import su.catlean.mixins.accessors.ItemCooldownsAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class CooldownsWidget extends Widget {
   @NotNull
   public static final CooldownsWidget INSTANCE = new CooldownsWidget();
   @NotNull
   private static final List<CooldownsWidget.CooldownHolder> items = new ArrayList();
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_BACK, 250L);

   private CooldownsWidget() {
      super("cooldowns-widget");
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      try {
         for (Map.Entry<Identifier, ? extends Object> entrySetItem : ((ItemCooldownsAccessor)MinecraftHolder.getLocalPlayer().getCooldowns()).getCooldowns().entrySet()) {
            Item value = (Item)BuiltInRegistries.ITEM.getValue((Identifier)entrySetItem.getKey());
            var items2 = items;
            boolean flag2;
            if (items2 instanceof Collection && ((Collection)items2).isEmpty()) {
               flag2 = true;
            } else {
               Iterator<CooldownsWidget.CooldownHolder> iterator = items2.iterator();

               while (true) {
                  if (!iterator.hasNext()) {
                     flag2 = true;
                     break;
                  }

                  CooldownsWidget.CooldownHolder object = iterator.next();
                  CooldownsWidget.CooldownHolder cooldownHolder = (CooldownsWidget.CooldownHolder)object;
                  if (Intrinsics.areEqual(cooldownHolder.getInstance(), value)) {
                     flag2 = false;
                     break;
                  }
               }
            }

            if (flag2) {
               items.add(new CooldownsWidget.CooldownHolder(value, ((CooldownInstanceAccessor)entrySetItem.getValue()).getEndTime(), null, false, 12, null));
            }
         }
      } catch (Throwable exception) {
         exception.printStackTrace();
      }

      Iterable<CooldownsWidget.CooldownHolder> items3 = items;
      boolean flag4 = false;

      for (Object object2 : items3) {
         CooldownsWidget.CooldownHolder cooldownHolder2 = (CooldownsWidget.CooldownHolder)object2;
         if (!MinecraftHolder.getLocalPlayer().getCooldowns().isOnCooldown(cooldownHolder2.getInstance().getDefaultInstance())) {
            cooldownHolder2.setFlag(true);
         }
      }

      items.removeIf(arg -> {
         ((CooldownsWidget.CooldownHolder)arg).getAnimation().applyDoubleValue(((CooldownsWidget.CooldownHolder)arg).isFlag() ? 0.0F : 1.0F);
         return ((CooldownsWidget.CooldownHolder)arg).isFlag() && ((CooldownsWidget.CooldownHolder)arg).getAnimation().getAmount() <= 0.0F;
      });
      String text = "Cooldowns";
      flag4 = this.getAmount3() > this.getValue2() / 2.0F;
      animation.applyDoubleValue(
         items.isEmpty()
               && !(MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen)
               && !(MinecraftHolder.getMinecraft().gui.screen() instanceof WidgetEditorScreen)
            ? 0.0F
            : 1.0F
      );
      this.render(context, text, animation.getAmount(), flag4);
      float value2 = 17.0F;
      float amount = Fonts.getFontRenderer().computeAmount(text) + 24.0F;

      for (CooldownsWidget.CooldownHolder item : (Iterable<su.catlean.client.features.ui.hud.widgets.CooldownsWidget.CooldownHolder>)items) {
         String text2 = this.computeText(item.getValue() - ((ItemCooldownsAccessor)MinecraftHolder.getLocalPlayer().getCooldowns()).getTickCount());
         String text3 = LocalizationManager.INSTANCE.computeText(item.getInstance().getDescriptionId(), new Object[0]);
         float amount2 = Fonts.getFontRenderer().computeAmount(text3);
         float amount3 = Fonts.getFontRenderer().computeAmount(text2);
         if (amount2 + amount3 + 27.0F > amount) {
            amount = amount2 + amount3 + 27.0F;
         }
      }

      for (CooldownsWidget.CooldownHolder item2 : (Iterable<su.catlean.client.features.ui.hud.widgets.CooldownsWidget.CooldownHolder>)items) {
         String text4 = this.computeText(item2.getValue() - ((ItemCooldownsAccessor)MinecraftHolder.getLocalPlayer().getCooldowns()).getTickCount());
         String text5 = LocalizationManager.INSTANCE.computeText(item2.getInstance().getDescriptionId(), new Object[0]);
         float amount4 = Fonts.getFontRenderer().computeAmount(text5);
         float amount5 = Fonts.getFontRenderer().computeAmount(text4);
         float amount32 = flag4
            ? this.getAmount3() - (1.0F - item2.getAnimation().getAmount()) * -10 - (amount4 + amount5 + 27.0F - amount)
            : this.getAmount3() - (1.0F - item2.getAnimation().getAmount()) * 10;
         Widget.defaultImpl(this, context.pose(), amount32, this.getAmount4() + value2, 13.0F, 13.0F, item2.getAnimation().getAmount(), false, 64, null);
         Widget.defaultImpl(
            this, context.pose(), amount32 + 14.0F, this.getAmount4() + value2, amount4 + 6.0F, 13.0F, item2.getAnimation().getAmount(), false, 64, null
         );
         Widget.defaultImpl(
            this,
            context.pose(),
            amount32 + (amount4 + 21.0F) * item2.getAnimation().getAmount(),
            this.getAmount4() + value2,
            amount5 + 6.0F,
            13.0F,
            item2.getAnimation().getAmount(),
            false,
            64,
            null
         );
         this.render(
            context,
            text5,
            amount32 + 17.0F,
            this.getAmount4() + value2 + 1.0F,
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor7(), item2.getAnimation().getAmount())
         );
         this.render(
            context,
            text4,
            amount32 + (amount4 + 24.0F) * item2.getAnimation().getAmount(),
            this.getAmount4() + value2 + 1.0F,
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor7(), item2.getAnimation().getAmount())
         );
         context.pose().pushMatrix();
         context.pose().translate(amount32 + 2, this.getAmount4() + value2 + 2);
         context.pose().scale(0.55F);
         if (item2.getAnimation().getAmount() > 0.8F) {
            context.item(item2.getInstance().getDefaultInstance(), 0, 0);
         }

         context.pose().popMatrix();
         value2 += 14.0F * item2.getAnimation().getAmount();
      }

      this.updateAmount(amount, value2 - 2.0F);
   }

   @NotNull
   public final String computeText(int value) {
      int value2 = value / 1200;
      String text = "%02d";
      Object[] objects = new Object[]{value % 1200 / 20};
      return value2 + ":" + String.format(text, Arrays.copyOf(objects, objects.length));
   }

   public static final class CooldownHolder {
      @NotNull
      private final Item instance;
      private final int value;
      @NotNull
      private final Animation animation;
      private boolean flag;

      public CooldownHolder(Item instance, int value, Animation animation, boolean flag) {
         this.instance = instance;
         this.value = value;
         this.animation = animation;
         this.flag = flag;
      }

      public CooldownHolder(Item item, int value, Animation animation, boolean flag, int value2, DefaultConstructorMarker defaultConstructorMarker) {
         if ((value2 & 4) != 0) {
            animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 250L);
         }

         if ((value2 & 8) != 0) {
            flag = false;
         }

         this(item, value, animation, flag);
      }

      @NotNull
      public final Item getInstance() {
         return this.instance;
      }

      public final int getValue() {
         return this.value;
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
      public final Item getInstance2() {
         return this.instance;
      }

      public final int getValue2() {
         return this.value;
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
         int value2 = this.value;
         Item instance2 = this.instance;
         return "CooldownHolder(instance=" + instance2 + ", endTick=" + value2 + ", animation=" + animation2 + ", removing=" + flag2 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.instance.hashCode();
         hashCode = hashCode * 31 + Integer.hashCode(this.value);
         hashCode = hashCode * 31 + this.animation.hashCode();
         return hashCode * 31 + Boolean.hashCode(this.flag);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof CooldownsWidget.CooldownHolder cooldownHolder)) {
            return false;
         } else if (!Intrinsics.areEqual(this.instance, cooldownHolder.instance)) {
            return false;
         } else if (this.value != cooldownHolder.value) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.animation, cooldownHolder.animation) ? false : this.flag == cooldownHolder.flag;
         }
      }
   }
}
// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.modules.esp.NameTags;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.features.ui.hud.WidgetEditorScreen;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.LocalizationManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class PotionsWidget extends Widget {
   @NotNull
   public static final PotionsWidget INSTANCE = new PotionsWidget();
   @NotNull
   private static final List<PotionsWidget.PotionHolder> items = new ArrayList();
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_BACK, 250L);

   private PotionsWidget() {
      super("potions-widget");
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      for (MobEffectInstance activeEffect : MinecraftHolder.getLocalPlayer().getActiveEffects()) {
         var items2 = items;
         boolean flag2;
         if (items2 instanceof Collection && ((Collection)items2).isEmpty()) {
            flag2 = true;
         } else {
            Iterator<PotionsWidget.PotionHolder> iterator = items2.iterator();

            while (true) {
               if (!iterator.hasNext()) {
                  flag2 = true;
                  break;
               }

               PotionsWidget.PotionHolder object = iterator.next();
               PotionsWidget.PotionHolder potionHolder = (PotionsWidget.PotionHolder)object;
               if (Intrinsics.areEqual(potionHolder.getInstance(), activeEffect)) {
                  flag2 = false;
                  break;
               }
            }
         }

         if (flag2) {
            items.add(new PotionsWidget.PotionHolder(activeEffect, null, false, 6, null));
         }
      }

      Iterable<PotionsWidget.PotionHolder> items3 = items;
      boolean flag4 = false;

      for (Object object2 : items3) {
         PotionsWidget.PotionHolder potionHolder2 = (PotionsWidget.PotionHolder)object2;
         var activeEffects = MinecraftHolder.getLocalPlayer().getActiveEffects();
         boolean flag7;
         if (activeEffects instanceof Collection && ((Collection)activeEffects).isEmpty()) {
            flag7 = true;
         } else {
            Iterator<MobEffectInstance> iterator2 = activeEffects.iterator();

            while (true) {
               if (!iterator2.hasNext()) {
                  flag7 = true;
                  break;
               }

               MobEffectInstance object3 = iterator2.next();
               MobEffectInstance mobEffectInstance = (MobEffectInstance)object3;
               if (Intrinsics.areEqual(potionHolder2.getInstance(), mobEffectInstance)) {
                  flag7 = false;
                  break;
               }
            }
         }

         if (flag7) {
            potionHolder2.setFlag(true);
         }
      }

      items.removeIf(arg -> {
         ((PotionsWidget.PotionHolder)arg).getAnimation().applyDoubleValue(((PotionsWidget.PotionHolder)arg).isFlag() ? 0.0F : 1.0F);
         return ((PotionsWidget.PotionHolder)arg).isFlag() && ((PotionsWidget.PotionHolder)arg).getAnimation().getAmount() <= 0.0F;
      });
      String text = "Potions";
      flag4 = this.getAmount3() > this.getValue2() / 2.0F;
      animation.applyDoubleValue(
         items.isEmpty()
               && !(MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen)
               && !(MinecraftHolder.getMinecraft().gui.screen() instanceof WidgetEditorScreen)
            ? 0.0F
            : 1.0F
      );
      this.render(context, text, animation.getAmount(), flag4);
      float value = 17.0F;
      float amount = Fonts.getFontRenderer().computeAmount(text) + 24.0F;

      for (PotionsWidget.PotionHolder item : (Iterable<su.catlean.client.features.ui.hud.widgets.PotionsWidget.PotionHolder>)items) {
         String text2 = LocalizationManager.INSTANCE.computeText(item.getInstance().getDescriptionId(), new Object[0])
            + (item.getInstance().getAmplifier() > 0 ? " " + (item.getInstance().getAmplifier() + 1) : "");
         float amount2 = Fonts.getFontRenderer().computeAmount(text2);
         float amount3 = Fonts.getFontRenderer().computeAmount(NameTags.INSTANCE.fetchDuration(item.getInstance()));
         if (amount2 + amount3 + 27.0F > amount) {
            amount = amount2 + amount3 + 27.0F;
         }
      }

      for (PotionsWidget.PotionHolder item2 : (Iterable<su.catlean.client.features.ui.hud.widgets.PotionsWidget.PotionHolder>)items) {
         String text3 = LocalizationManager.INSTANCE.computeText(item2.getInstance().getDescriptionId(), new Object[0])
            + (item2.getInstance().getAmplifier() > 0 ? " " + (item2.getInstance().getAmplifier() + 1) : "");
         float amount4 = Fonts.getFontRenderer().computeAmount(text3);
         float amount5 = Fonts.getFontRenderer().computeAmount(NameTags.INSTANCE.fetchDuration(item2.getInstance()));
         boolean flag9 = ((MobEffect)item2.getInstance().getEffect().value()).getCategory() == MobEffectCategory.HARMFUL;
         float amount32 = flag4
            ? this.getAmount3() - (1.0F - item2.getAnimation().getAmount()) * -10 - (amount4 + amount5 + 27.0F - amount)
            : this.getAmount3() - (1.0F - item2.getAnimation().getAmount()) * 10;
         float duration = 1.0F;
         if (item2.getInstance().endsWithin(200)) {
            duration = 1.0F - (float)Math.sin(item2.getInstance().getDuration()) * 0.5F;
         }

         Widget.defaultImpl(this, context.pose(), amount32, this.getAmount4() + value, 13.0F, 13.0F, item2.getAnimation().getAmount(), false, 64, null);
         Widget.defaultImpl(
            this, context.pose(), amount32 + 14.0F, this.getAmount4() + value, amount4 + 6.0F, 13.0F, item2.getAnimation().getAmount(), false, 64, null
         );
         Widget.defaultImpl(
            this,
            context.pose(),
            amount32 + (amount4 + 21.0F) * item2.getAnimation().getAmount(),
            this.getAmount4() + value,
            amount5 + 6.0F,
            13.0F,
            item2.getAnimation().getAmount(),
            false,
            64,
            null
         );
         float value2 = amount32 + 17.0F;
         float amount42 = this.getAmount4() + value + 1.0F;
         Color red = flag9 ? Color.RED : ThemeManager.INSTANCE.getColor7();
         Color color = Render2D.INSTANCE.computeColor(red, item2.getAnimation().getAmount() * duration);
         float value3 = amount42;
         float value4 = value2;
         this.render(context, text3, value4, value3, color);
         this.render(
            context,
            NameTags.INSTANCE.fetchDuration(item2.getInstance()),
            amount32 + (amount4 + 24.0F) * item2.getAnimation().getAmount(),
            this.getAmount4() + value + 1.0F,
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor7(), item2.getAnimation().getAmount() * duration)
         );
         context.pose().pushMatrix();
         context.pose().translate(amount32 + 2, this.getAmount4() + value + 2);
         context.pose().scale(0.55F);
         Identifier withPath = ((ResourceKey)item2.getInstance().getEffect().unwrapKey().orElseThrow())
            .identifier()
            .withPath(arg2 -> "textures/mob_effect/" + arg2 + ".png");
         context.blit(
            RenderPipelines.GUI_TEXTURED,
            withPath,
            0,
            0,
            0.0F,
            0.0F,
            18,
            18,
            18,
            18,
            Render2D.INSTANCE.computeColor(Color.WHITE, item2.getAnimation().getAmount() * duration).getRGB()
         );
         context.pose().popMatrix();
         value += 14.0F * item2.getAnimation().getAmount();
      }

      this.updateAmount(amount, value - 2.0F);
   }

   public static final class PotionHolder {
      @NotNull
      private final MobEffectInstance instance;
      @NotNull
      private final Animation animation;
      private boolean flag;

      public PotionHolder(MobEffectInstance instance, Animation animation, boolean flag) {
         this.instance = instance;
         this.animation = animation;
         this.flag = flag;
      }

      public PotionHolder(MobEffectInstance mobEffectInstance, Animation animation, boolean flag, int value, DefaultConstructorMarker defaultConstructorMarker) {
         if ((value & 2) != 0) {
            animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 250L);
         }

         if ((value & 4) != 0) {
            flag = false;
         }

         this(mobEffectInstance, animation, flag);
      }

      @NotNull
      public final MobEffectInstance getInstance() {
         return this.instance;
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
      public final MobEffectInstance getInstance2() {
         return this.instance;
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
         MobEffectInstance instance2 = this.instance;
         return "PotionHolder(instance=" + instance2 + ", animation=" + animation2 + ", removing=" + flag2 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.instance.hashCode();
         hashCode = hashCode * 31 + this.animation.hashCode();
         return hashCode * 31 + Boolean.hashCode(this.flag);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof PotionsWidget.PotionHolder potionHolder)) {
            return false;
         } else if (!Intrinsics.areEqual(this.instance, potionHolder.instance)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.animation, potionHolder.animation) ? false : this.flag == potionHolder.flag;
         }
      }
   }
}
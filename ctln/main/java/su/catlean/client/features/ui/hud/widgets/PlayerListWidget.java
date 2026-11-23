// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.features.ui.hud.WidgetEditorScreen;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ServerInfoService;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.player.PlayerNameUtil;
import su.catlean.utils.render.Render2D;

public final class PlayerListWidget extends Widget {
   @NotNull
   public static final PlayerListWidget INSTANCE = new PlayerListWidget();
   @NotNull
   private static final List<PlayerListWidget.PlayerHolder> items = new ArrayList();
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_BACK, 250L);

   private PlayerListWidget() {
      super("player-list-widget");
   }

   @NotNull
   public final List<PlayerListWidget.PlayerHolder> getItems() {
      return items;
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      Iterable<AbstractClientPlayer> players = MinecraftHolder.getClientLevel().players();
      boolean flag = false;
      var arrayList = new ArrayList();
      boolean flag2 = false;

      for (Object object : players) {
         AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)object;
         if (!Intrinsics.areEqual(abstractClientPlayer, MinecraftHolder.getMinecraft().player)) {
            arrayList.add(object);
         }
      }

      for (AbstractClientPlayer abstractClientPlayer2 : (Iterable<AbstractClientPlayer>)arrayList) {
         var items2 = items;
         boolean flag4;
         if (items2 instanceof Collection && ((Collection)items2).isEmpty()) {
            flag4 = true;
         } else {
            Iterator<PlayerListWidget.PlayerHolder> iterator = items2.iterator();

            while (true) {
               if (!iterator.hasNext()) {
                  flag4 = true;
                  break;
               }

               PlayerListWidget.PlayerHolder object2 = iterator.next();
               PlayerListWidget.PlayerHolder playerHolder = (PlayerListWidget.PlayerHolder)object2;
               if (Intrinsics.areEqual(playerHolder.getEntity(), abstractClientPlayer2)) {
                  flag4 = false;
                  break;
               }
            }
         }

         flag = flag4;
         if (flag) {
            items.add(new PlayerListWidget.PlayerHolder((Player)abstractClientPlayer2, null, false, 6, null));
         }
      }

      Iterable<PlayerListWidget.PlayerHolder> items3 = items;
      var arrayList2 = new ArrayList();

      for (Object object3 : items3) {
         PlayerListWidget.PlayerHolder playerHolder2 = (PlayerListWidget.PlayerHolder)object3;
         if (!CollectionsKt.contains(MinecraftHolder.getClientLevel().players(), playerHolder2.getEntity())) {
            arrayList2.add(object3);
         }
      }

      items3 = arrayList2;

      for (Object object4 : items3) {
         PlayerListWidget.PlayerHolder playerHolder3 = (PlayerListWidget.PlayerHolder)object4;
         flag2 = false;
         playerHolder3.setFlag(true);
      }

      items.removeIf(arg -> {
         ((PlayerListWidget.PlayerHolder)arg).getAnimation().applyDoubleValue(((PlayerListWidget.PlayerHolder)arg).isFlag() ? 0.0F : 1.0F);
         return ((PlayerListWidget.PlayerHolder)arg).isFlag() && ((PlayerListWidget.PlayerHolder)arg).getAnimation().getAmount() <= 0.0F;
      });
      String text = "Players";
      boolean flag10 = this.getAmount3() > this.getValue2() / 2.0F;
      animation.applyDoubleValue(
         items.isEmpty()
               && !(MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen)
               && !(MinecraftHolder.getMinecraft().gui.screen() instanceof WidgetEditorScreen)
            ? 0.0F
            : 1.0F
      );
      this.render(context, text, animation.getAmount(), flag10);
      float value = 17.0F;
      float amount = Fonts.getFontRenderer().computeAmount(text) + 24.0F;

      for (PlayerListWidget.PlayerHolder item : (Iterable<su.catlean.client.features.ui.hud.widgets.PlayerListWidget.PlayerHolder>)items) {
         float amount2 = Fonts.getFontRenderer().computeAmount(item.getText());
         if (amount2 + 13.0F > amount) {
            amount = amount2 + 13.0F;
         }
      }

      for (PlayerListWidget.PlayerHolder item2 : (Iterable<su.catlean.client.features.ui.hud.widgets.PlayerListWidget.PlayerHolder>)items) {
         float amount3 = Fonts.getFontRenderer().computeAmount(item2.getText());
         float amount32 = flag10
            ? this.getAmount3() - (1.0F - item2.getAnimation().getAmount()) * -10 - (amount3 + 0 + 13.0F - amount)
            : this.getAmount3() - (1.0F - item2.getAnimation().getAmount()) * 10;
         Widget.defaultImpl(this, context.pose(), amount32, this.getAmount4() + value, amount3 + 21.0F, 13.0F, item2.getAnimation().getAmount(), false, 64, null);
         this.render(
            context,
            item2.getText(),
            amount32 + 3.0F,
            this.getAmount4() + value + 1.0F,
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor7(), item2.getAnimation().getAmount())
         );
         this.render(context.pose(), amount32 + amount3 + 6.0F, this.getAmount4() + 2.0F + value, item2.getAnimation().getAmount() * 0.27F);
         value += 14.0F * item2.getAnimation().getAmount();
      }

      this.updateAmount(amount, value - 2.0F);
   }

   @Override
   public void render2(GuiGraphicsExtractor context) {
      String text = "Players";
      boolean flag = this.getAmount3() > this.getValue2() / 2.0F;
      float value = 17.0F;
      float amount = Fonts.getFontRenderer().computeAmount(text) + 24.0F;

      for (PlayerListWidget.PlayerHolder item : (Iterable<su.catlean.client.features.ui.hud.widgets.PlayerListWidget.PlayerHolder>)items) {
         float amount2 = Fonts.getFontRenderer().computeAmount(item.getText());
         if (amount2 + 13.0F > amount) {
            amount = amount2 + 13.0F;
         }
      }

      for (PlayerListWidget.PlayerHolder item2 : (Iterable<su.catlean.client.features.ui.hud.widgets.PlayerListWidget.PlayerHolder>)items) {
         float amount3 = Fonts.getFontRenderer().computeAmount(item2.getText());
         float amount32 = flag
            ? this.getAmount3() - (1.0F - item2.getAnimation().getAmount()) * -10 - (amount3 + 0 + 13.0F - amount)
            : this.getAmount3() - (1.0F - item2.getAnimation().getAmount()) * 10;
         Render2D.defaultImpl(
            Render2D.INSTANCE,
            (Matrix3x2f)context.pose(),
            ((AbstractClientPlayer)item2.getEntity()).getSkin().body().texturePath(),
            amount32 + amount3 + 10.0F,
            this.getAmount4() + 2.5F + value,
            8.0F,
            8.0F,
            Render2D.INSTANCE.computeColor(Color.WHITE, item2.getAnimation().getAmount()),
            null,
            null,
            null,
            0.125F,
            0.125F,
            0.25F,
            0.25F,
            false,
            17280,
            null
         );
         Render2D.defaultImpl(
            Render2D.INSTANCE,
            (Matrix3x2f)context.pose(),
            ((AbstractClientPlayer)item2.getEntity()).getSkin().body().texturePath(),
            amount32 + amount3 + 9.0F,
            this.getAmount4() + 1.5F + value,
            10.0F,
            10.0F,
            Render2D.INSTANCE.computeColor(Color.WHITE, item2.getAnimation().getAmount()),
            null,
            null,
            null,
            0.625F,
            0.125F,
            0.75F,
            0.25F,
            false,
            17280,
            null
         );
         value += 14.0F * item2.getAnimation().getAmount();
      }
   }

   public static final class PlayerHolder {
      @NotNull
      private final Player entity;
      @NotNull
      private final Animation animation;
      private boolean flag;

      public PlayerHolder(Player entity, Animation animation, boolean flag) {
         this.entity = entity;
         this.animation = animation;
         this.flag = flag;
      }

      public PlayerHolder(Player player, Animation animation, boolean flag, int value, DefaultConstructorMarker defaultConstructorMarker) {
         if ((value & 2) != 0) {
            animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 250L);
         }

         if ((value & 4) != 0) {
            flag = false;
         }

         this(player, animation, flag);
      }

      @NotNull
      public final Player getEntity() {
         return this.entity;
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
      public final String getText() {
         String text = (PlayerNameUtil.checkGetGameProfile(this.entity) ? ChatFormatting.GREEN : ChatFormatting.WHITE).toString();
         String name = this.entity.getGameProfile().name();
         ChatFormatting reset = ChatFormatting.RESET;
         int roundToInt = MathKt.roundToInt(this.entity.getHealth() + this.entity.getAbsorptionAmount());
         int roundToInt2 = MathKt.roundToInt(MinecraftHolder.getLocalPlayer().distanceTo((Entity)this.entity));
         int connection = ServerInfoService.INSTANCE.fetchConnection(this.entity);
         int value = roundToInt2;
         int value2 = roundToInt;
         ChatFormatting chatFormatting = reset;
         String text2 = name;
         return text + text2 + chatFormatting + " | " + value2 + " hp | " + value + " m | " + connection + " ms";
      }

      @NotNull
      public final Player getEntity2() {
         return this.entity;
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
         Player entity2 = this.entity;
         return "PlayerHolder(entity=" + entity2 + ", animation=" + animation2 + ", removing=" + flag2 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.entity.hashCode();
         hashCode = hashCode * 31 + this.animation.hashCode();
         return hashCode * 31 + Boolean.hashCode(this.flag);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof PlayerListWidget.PlayerHolder playerHolder)) {
            return false;
         } else if (!Intrinsics.areEqual(this.entity, playerHolder.entity)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.animation, playerHolder.animation) ? false : this.flag == playerHolder.flag;
         }
      }
   }
}
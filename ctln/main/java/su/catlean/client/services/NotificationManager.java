// deobf by pusia
package su.catlean.client.services;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.service.Service;
import su.catlean.client.features.ui.hud.widgets.NotificationsWidget;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.chat.MessageType;
import su.catlean.utils.render.Render2D;

public final class NotificationManager implements Service {
   @NotNull
   public static final NotificationManager INSTANCE = new NotificationManager();
   @NotNull
   private static final List<NotificationManager.Notification> items = new ArrayList();
   @NotNull
   private static final SvgTexture svgTexture = new SvgTexture("on", 9, 9);
   @NotNull
   private static final SvgTexture svgTexture2 = new SvgTexture("off", 9, 9);
   @NotNull
   private static final SvgTexture svgTexture3 = new SvgTexture("warning", 9, 9);
   @NotNull
   private static final SvgTexture svgTexture4 = new SvgTexture("info", 9, 9);
   @NotNull
   private static final SvgTexture svgTexture5 = new SvgTexture("success", 9, 9);
   @NotNull
   private static final SvgTexture svgTexture6 = new SvgTexture("error", 9, 9);
   private static boolean flag;

   private NotificationManager() {
   }

   @NotNull
   public final SvgTexture getSvgTexture() {
      return svgTexture;
   }

   @NotNull
   public final SvgTexture getSvgTexture2() {
      return svgTexture2;
   }

   @NotNull
   public final SvgTexture getSvgTexture3() {
      return svgTexture3;
   }

   @NotNull
   public final SvgTexture getSvgTexture4() {
      return svgTexture4;
   }

   @NotNull
   public final SvgTexture getSvgTexture5() {
      return svgTexture5;
   }

   @NotNull
   public final SvgTexture getSvgTexture6() {
      return svgTexture6;
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      NotificationManager.flag = flag;
   }

   public final void applyExecute(String title, String content, int value, MessageType type, ItemStack itemStack) {
      if (NotificationsWidget.INSTANCE.getEnabled() && !flag) {
         if (MinecraftHolder.getMinecraft().player != null) {
            MinecraftHolder.getMinecraft().execute(() -> {
               items.add(new NotificationManager.Notification(title, content, type, value * 1600, itemStack));
               switch (NotificationManager.WhenMappings.values[type.ordinal()]) {
                  case 1:
                     SoundManager.INSTANCE.getSound10().applyExecuteIfPossible();
                     break;
                  case 2:
                     SoundManager.INSTANCE.getSound9().applyExecuteIfPossible();
               }
            });
         }
      }
   }

   public static void defaultImpl(
      NotificationManager notificationManager, String text, String text2, int value, MessageType messageType, ItemStack itemStack, int value2, Object object
   ) {
      if ((value2 & 16) != 0) {
         itemStack = null;
      }

      notificationManager.applyExecute(text, text2, value, messageType, itemStack);
   }
   @Flow
   public final void onRender2D(Render2DEvent e) {
      float guiScaledHeight = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight() / 2.0F + 25;
      if (items.size() > 8) {
         items.removeFirst();
      }

      for (NotificationManager.Notification array : (Iterable<su.catlean.client.services.NotificationManager.Notification>)Lists.newArrayList(items)) {
         this.render(array, e.getContext(), guiScaledHeight + items.size() * 16);
         guiScaledHeight -= 16.0F;
      }

      List<NotificationManager.Notification> items2 = items;
      synchronized (items2) {
         boolean removeIf = items.removeIf(arg -> ((NotificationManager.Notification)arg).isFlag());
      }
   }

   private final void render(NotificationManager.Notification notification, GuiGraphicsExtractor guiGraphicsExtractor, float amount) {
      notification.getAnimation().applyDoubleValue(notification.getStopWatch().testValue(notification.getValue() - 350) ? 0.0F : 1.0F);
      if (!(notification.getAnimation().getAmount() < 0.01)) {
         notification.setAmount(this.computeAmount(notification.getAmount(), amount));
         float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F - notification.getAmount2() / 2.0F;
         Matrix3x2fStack pose = guiGraphicsExtractor.pose();
         pose.pushMatrix();
         pose.translate(guiScaledWidth + notification.getAmount2() / 2.0F, notification.getAmount() + 6.5F);
         pose.scale(notification.getAnimation().getAmount(), notification.getAnimation().getAmount());
         pose.translate(-(guiScaledWidth + notification.getAmount2() / 2.0F), -(notification.getAmount() + 6.5F));
         TexturedQuadPass.defaultImpl(
            pose,
            guiScaledWidth,
            notification.getAmount(),
            notification.getAmount2(),
            13.0F,
            3.0F,
            1.5F,
            0.0F,
            notification.getAnimation().getAmount(),
            ThemeManager.INSTANCE.getColor5(),
            ThemeManager.INSTANCE.getColor6(),
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
         Render2D instance = Render2D.INSTANCE;
         float value = guiScaledWidth + 14;
         float amount2 = notification.getAmount() + 2;
         Color[] colors = new Color[]{Render2D.INSTANCE.computeColor(new Color(1157627903, true), notification.getAnimation().getAmount() * 0.1F)};
         instance.render(pose, value, amount2, 0.5F, 9.0F, colors);
         FontRenderer fontRenderer = Fonts.getFontRenderer();
         String title = notification.getItemStack() == null ? notification.getTitle() + notification.getMessage() : notification.getMessage();
         amount2 = guiScaledWidth + 16.0F;
         float amount3 = notification.getAmount() + 1.0F;
         Color color = Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor7(), notification.getAnimation().getAmount());
         float value2 = amount3;
         float value3 = amount2;
         String text = title;
         fontRenderer.render(guiGraphicsExtractor, text, value3, value2, color);
         if (notification.getItemStack() == null) {
            SvgTexture.defaultImpl(
               notification.getSvgTexture(), (Matrix3x2f)pose, guiScaledWidth + 2.0, notification.getAmount() + 0.5, 0.0F, 0.0F, null, null, 0.0F, 248, null
            );
         } else {
            guiGraphicsExtractor.pose().translate(guiScaledWidth + 2.0F, notification.getAmount() + 1.0F);
            guiGraphicsExtractor.pose().scale(0.65F, 0.65F);
            guiGraphicsExtractor.item(notification.getItemStack(), 0, 0);
            guiGraphicsExtractor.pose().scale(1.0F, 1.0F);
            guiGraphicsExtractor.pose().translate(-(guiScaledWidth + 2.0F), -(notification.getAmount() + 1.0F));
         }

         pose.popMatrix();
      }
   }

   private final float computeAmount(float amount, float amount2) {
      return amount + (amount2 - amount) / 8.0F;
   }

   public static final class Notification {
      @NotNull
      private final String title;
      @NotNull
      private final String message;
      @NotNull
      private final MessageType type;
      private final int value;
      @Nullable
      private final ItemStack itemStack;
      private float amount;
      @NotNull
      private final Animation animation;
      private final float amount2;
      @NotNull
      private final StopWatch stopWatch;
      @NotNull
      private final SvgTexture svgTexture;

      public Notification(String title, String message, MessageType type, int value, ItemStack itemStack) {
         this.title = title;
         this.message = message;
         this.type = type;
         this.value = value;
         this.itemStack = itemStack;
         this.amount = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight() / 2.0F + 41.0F;
         this.animation = new Animation(NotificationManager.Notification.Type.OUT_BACK, 300L);
         this.amount2 = Fonts.getFontRenderer().computeAmount(this.itemStack == null ? this.title + " " + this.message : this.message) + 20.0F;
         this.stopWatch = new StopWatch();

         this.svgTexture = switch (NotificationManager.Notification.WhenMappings.values[this.type.ordinal()]) {
            case 1 -> NotificationManager.INSTANCE.getSvgTexture4();
            case 2 -> NotificationManager.INSTANCE.getSvgTexture();
            case 3 -> NotificationManager.INSTANCE.getSvgTexture2();
            case 4 -> NotificationManager.INSTANCE.getSvgTexture6();
            case 5 -> NotificationManager.INSTANCE.getSvgTexture3();
            case 6 -> NotificationManager.INSTANCE.getSvgTexture5();
            default -> throw new NoWhenBranchMatchedException();
         };
      }

      @NotNull
      public final String getTitle() {
         return this.title;
      }

      @NotNull
      public final String getMessage() {
         return this.message;
      }

      @NotNull
      public final MessageType getType() {
         return this.type;
      }

      public final int getValue() {
         return this.value;
      }

      @Nullable
      public final ItemStack getItemStack() {
         return this.itemStack;
      }

      public final float getAmount() {
         return this.amount;
      }

      public final void setAmount(float amount) {
         this.amount = amount;
      }

      @NotNull
      public final Animation getAnimation() {
         return this.animation;
      }

      public final float getAmount2() {
         return this.amount2;
      }

      @NotNull
      public final StopWatch getStopWatch() {
         return this.stopWatch;
      }

      @NotNull
      public final SvgTexture getSvgTexture() {
         return this.svgTexture;
      }

      public final boolean isFlag() {
         return this.stopWatch.testValue(this.value);
      }

      @NotNull
      public final String getTitle2() {
         return this.title;
      }

      @NotNull
      public final String getMessage2() {
         return this.message;
      }

      @NotNull
      public final MessageType getType2() {
         return this.type;
      }

      public final int getValue2() {
         return this.value;
      }

      @Nullable
      public final ItemStack getItemStack2() {
         return this.itemStack;
      }

      @NotNull
      @Override
      public String toString() {
         ItemStack itemStack2 = this.itemStack;
         int value2 = this.value;
         MessageType type2 = this.type;
         String message2 = this.message;
         String title2 = this.title;
         return "Notification(title=" + title2 + ", message=" + message2 + ", type=" + type2 + ", time=" + value2 + ", item=" + itemStack2 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.title.hashCode();
         hashCode = hashCode * 31 + this.message.hashCode();
         hashCode = hashCode * 31 + this.type.hashCode();
         hashCode = hashCode * 31 + Integer.hashCode(this.value);
         return hashCode * 31 + (this.itemStack == null ? 0 : this.itemStack.hashCode());
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof NotificationManager.Notification notification)) {
            return false;
         } else if (!Intrinsics.areEqual(this.title, notification.title)) {
            return false;
         } else if (!Intrinsics.areEqual(this.message, notification.message)) {
            return false;
         } else if (this.type != notification.type) {
            return false;
         } else {
            return this.value != notification.value ? false : Intrinsics.areEqual(this.itemStack, notification.itemStack);
         }
      }

      public static enum Type {
         LINEAR(arg -> arg),
         IN_QUAD(arg -> Math.pow(arg, 2)),
         OUT_QUAD(arg -> arg * (2 - arg)),
         IN_OUT_QUAD(arg -> arg < 0.5 ? 2 * Math.pow(arg, 2) : -1 + (4 - 2 * arg) * arg),
         IN_CUBIC(arg -> Math.pow(arg, 3)),
         OUT_CUBIC(arg -> Math.pow(arg - 1, 3) + 1),
         IN_QUART(arg -> Math.pow(arg, 4)),
         OUT_QUART(arg -> 1 - Math.pow(arg - 1, 4)),
         IN_OUT_QUART(arg -> arg < 0.5 ? 8.0F * arg * arg * arg * arg : 1.0F - Math.pow(-2.0F * arg + 2.0F, 4) / 2.0F),
         IN_QUINT(arg -> Math.pow(arg, 5)),
         OUT_QUINT(arg -> 1 + Math.pow(arg - 1, 5)),
         IN_OUT_QUINT(arg -> arg < 0.5 ? 16.0F * arg * arg * arg * arg * arg : 1.0F - Math.pow(-2.0F * arg + 2.0F, 5) / 2.0F),
         IN_SINE(arg -> 1 - Math.cos(arg * Math.PI / 2)),
         OUT_SINE(arg -> Math.sin(arg * Math.PI / 2)),
         IN_OUT_SINE(arg -> 1 - Math.cos(Math.PI * arg / 2)),
         IN_EXPO(arg -> arg == 0.0 ? 0.0 : Math.pow(2.0, 10 * arg - 10)),
         OUT_EXPO(arg -> arg == 1.0 ? 1.0 : 1 - Math.pow(2.0, -10 * arg)),
         IN_OUT_EXPO(arg -> arg == 0.0 || arg == 1.0 ? arg : (arg < 0.5 ? Math.pow(2.0, 20 * arg - 10) / 2 : (2 - Math.pow(2.0, -20 * arg + 10)) / 2)),
         IN_CIRC(arg -> 1 - Math.sqrt(1 - Math.pow(arg, 2))),
         OUT_CIRC(arg -> Math.sqrt(1 - Math.pow(arg - 1, 2))),
         IN_OUT_CIRC(arg -> {
            double value;
            if (arg < 0.5) {
               double value2 = 2.0F * arg;
               value = (1.0F - Math.sqrt(1.0F - value2 * value2)) / 2.0F;
            } else {
               double value3 = -2.0F * arg + 2.0F;
               value = (Math.sqrt(1.0F - value3 * value3) + 1.0F) / 2.0F;
            }

            return value;
         }),
         SIGMOID(arg -> {
            double value = 10.0;
            return 1 / (1 + Math.exp(-value * (arg - 0.5)));
         }),
         SIGMOID_2(arg -> {
            double value = 8.0;
            return 1 / (1 + Math.exp(-value * (arg - 0.5)));
         }),
         SIGMOID_3(arg -> {
            double value = 12.0;
            return 1 / (1 + Math.exp(-value * (arg - 0.5)));
         }),
         SIGMOID_4(arg -> {
            double value = 6.0;
            return 1 / (1 + Math.exp(-value * (arg - 0.5)));
         }),
         SIGMOID_5(arg -> {
            double value = 14.0;
            return 1 / (1 + Math.exp(-value * (arg - 0.5)));
         }),
         SIGMOID_6(arg -> {
            double value = 4.0;
            return 1 / (1 + Math.exp(-value * (arg - 0.5)));
         }),
         SIGMOID_7(arg -> {
            double value = 16.0;
            return 1 / (1 + Math.exp(-value * (arg - 0.5)));
         }),
         OUT_ELASTIC(
            arg -> arg == 0.0 ? 0.0 : (arg == 1.0 ? 1.0 : Math.pow(2.0, -10 * arg) * Math.sin((arg * 10 - 0.75) * (Math.PI * 2.0 / 3.0)) * 0.5 + 1)
         ),
         IN_BACK(arg -> 2.70158 * Math.pow(arg, 3) - 1.70158 * Math.pow(arg, 2)),
         OUT_BACK(arg -> 1 + 2.70158 * Math.pow(arg - 1, 3) + 1.70158 * Math.pow(arg - 1, 2)),
         DECELERATE(arg -> 1 - Math.pow(arg - 1, 2)),
         IN_OUT_CUBIC(arg -> arg < 0.5 ? 4.0F * arg * arg * arg : 1.0F - Math.pow(-2.0F * arg + 2.0F, 3) / 2.0F);

         @NotNull
         private final Function1<Double, Double> callback;
         private static final NotificationManager.Notification.Type[] types = $values();
         private static final EnumEntries<NotificationManager.Notification.Type> enumEntries = EnumEntriesKt.enumEntries(types);

         private Type(Function1<Double, Double> callback) {
            this.callback = callback;
         }

         @NotNull
         public final Function1<Double, Double> getCallback() {
            return this.callback;
         }

         @NotNull
         public static EnumEntries<NotificationManager.Notification.Type> getEnumEntries() {
            return enumEntries;
         }

         private static final NotificationManager.Notification.Type[] $values() {
            return new NotificationManager.Notification.Type[]{
               LINEAR,
               IN_QUAD,
               OUT_QUAD,
               IN_OUT_QUAD,
               IN_CUBIC,
               OUT_CUBIC,
               IN_QUART,
               OUT_QUART,
               IN_OUT_QUART,
               IN_QUINT,
               OUT_QUINT,
               IN_OUT_QUINT,
               IN_SINE,
               OUT_SINE,
               IN_OUT_SINE,
               IN_EXPO,
               OUT_EXPO,
               IN_OUT_EXPO,
               IN_CIRC,
               OUT_CIRC,
               IN_OUT_CIRC,
               SIGMOID,
               SIGMOID_2,
               SIGMOID_3,
               SIGMOID_4,
               SIGMOID_5,
               SIGMOID_6,
               SIGMOID_7,
               OUT_ELASTIC,
               IN_BACK,
               OUT_BACK,
               DECELERATE,
               IN_OUT_CUBIC
            };
         }
      }

      public static final class WhenMappings {
         public static final int[] values;

         static {
            int[] values2 = new int[MessageType.values().length];

            try {
               values2[MessageType.INFO.ordinal()] = 1;
            } catch (NoSuchFieldError exception) {
            }

            try {
               values2[MessageType.ENABLED.ordinal()] = 2;
            } catch (NoSuchFieldError exception2) {
            }

            try {
               values2[MessageType.DISABLED.ordinal()] = 3;
            } catch (NoSuchFieldError exception3) {
            }

            try {
               values2[MessageType.ERROR.ordinal()] = 4;
            } catch (NoSuchFieldError exception4) {
            }

            try {
               values2[MessageType.WARNING.ordinal()] = 5;
            } catch (NoSuchFieldError exception5) {
            }

            try {
               values2[MessageType.SUCCESS.ordinal()] = 6;
            } catch (NoSuchFieldError exception6) {
            }

            values = values2;
         }
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[MessageType.values().length];

         try {
            values2[MessageType.DISABLED.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[MessageType.ENABLED.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         values = values2;
      }
   }
}
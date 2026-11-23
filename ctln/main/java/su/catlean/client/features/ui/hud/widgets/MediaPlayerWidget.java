// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import su.catlean.utils.KotlinCompat;
import dev.redstones.mediaplayerinfo.IMediaSession;
import dev.redstones.mediaplayerinfo.MediaInfo;
import dev.redstones.mediaplayerinfo.MediaPlayerInfo;
import java.awt.Color;
import java.awt.image.BufferedImage;
import kotlin.Unit;
import kotlin.concurrent.ThreadsKt;
import kotlin.ranges.RangesKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import su.catlean.api.data.Rectangle;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.features.ui.hud.WidgetEditorScreen;
import su.catlean.client.render.BoxPass;
import su.catlean.client.render.RemoteTextureCache;
import su.catlean.client.render.TexturedOutlinePass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Identifiers;
import su.catlean.utils.render.Render2D;

public final class MediaPlayerWidget extends Widget {
   @NotNull
   public static final MediaPlayerWidget INSTANCE = new MediaPlayerWidget();
   @Nullable
   private static MediaInfo mediaInfo;
   @NotNull
   private static Identifier catlean = Identifiers.computeIdentifier("catlean", "temp/cover");
   @NotNull
   private static Vec2 vec2 = new Vec2(1.0F, 1.0F);
   private static boolean flag = true;
   @NotNull
   private static String text = "";
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 400L);
   @NotNull
   private static final Animation animation2 = new Animation(NotificationManager.Notification.Type.OUT_BACK, 250L);
   private static boolean flag2;
   private static int value;
   @NotNull
   private static StopWatch stopWatch = new StopWatch();

   private MediaPlayerWidget() {
      super("media-player-widget");
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      animation2.applyDoubleValue(
         !flag2
               && !(MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen)
               && !(MinecraftHolder.getMinecraft().gui.screen() instanceof WidgetEditorScreen)
            ? 0.0F
            : 1.0F
      );
      this.updateAmount(120.0F, 43.0F);
      context.pose().pushMatrix();
      context.pose().translate(this.getAmount3() + 60.0F, this.getAmount4() + 21.5F);
      context.pose()
         .scale(
            ((Number)RangesKt.coerceIn(animation2.getAmount(), RangesKt.rangeTo(0.0F, 2.0F))).floatValue(),
            ((Number)RangesKt.coerceIn(animation2.getAmount(), RangesKt.rangeTo(0.0F, 2.0F))).floatValue()
         );
      context.pose().translate(-(this.getAmount3() + 60.0F), -(this.getAmount4() + 21.5F));
      if (mediaInfo == null
         && (MinecraftHolder.getMinecraft().gui.screen() instanceof WidgetEditorScreen || MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen)) {
         Widget.defaultImpl(this, context.pose(), this.getAmount3(), this.getAmount4(), 120.0F, 43.0F, 0.0F, false, 32, null);
         Fonts.getFontRenderer6().render2(context, this.getText(), this.getAmount3() + 60.0F, this.getAmount4() + 21.0F, ThemeManager.INSTANCE.getColor7());
      }

      if (mediaInfo == null) {
         context.pose().popMatrix();
      } else {
         float coerceIn = RangesKt.coerceIn((float)mediaInfo.getPosition() / (float)mediaInfo.getDuration(), 0.0F, 1.0F);
         boolean flag3 = mediaInfo.getPosition() > 1L && mediaInfo.getDuration() - mediaInfo.getPosition() > 1L;
         animation.applyDoubleValue(mediaInfo.getPlaying() && flag3 ? 1.0F : 0.0F);
         Widget.defaultImpl(this, context.pose(), this.getAmount3(), this.getAmount4(), 120.0F, 43.0F, 0.0F, false, 32, null);
         float amount3 = this.getAmount3() + 4.0F;
         float amount4 = this.getAmount4() + 4.0F;
         float amount = !flag3 ? animation.getAmount() : RangesKt.coerceIn(animation.getAmount() + 0.5F, 0.0F, 1.0F);
         Color white = Color.WHITE;
         Color white2 = Color.WHITE;
         Matrix3x2fStack pose = context.pose();
         Identifier catlean2 = catlean;
         Matrix3x2fStack matrix3x2fStack = pose;
         Color color = white2;
         Color color2 = white;
         TexturedOutlinePass.render(amount3, amount4, 35.0F, 35.0F, 3.0F, 2.0F, 0.0F, amount, color2, color, matrix3x2fStack, catlean2);
         amount3 = this.getAmount3() + 4.0F;
         amount4 = this.getAmount4() + 4.0F;
         white = Color.WHITE;
         white2 = Color.WHITE;
         pose = context.pose();
         catlean2 = catlean;
         matrix3x2fStack = pose;
         color = white2;
         color2 = white;
         TexturedOutlinePass.render(amount3, amount4, 35.0F, 35.0F, 3.0F, 2.0F, 0.0F, 1.0F, color2, color, matrix3x2fStack, catlean2);
         if (!mediaInfo.getPlaying() && animation.getAmount() < 1.0F) {
            Matrix3x2fStack pose2 = context.pose();
            float amount32 = this.getAmount3() + 13.0F;
            float amount42 = this.getAmount4() + 10.0F;
            float amount2 = 1.0F - animation.getAmount();
            boolean flag4 = false;
            float value2 = amount2;
            float value3 = 22.0F;
            float value4 = 6.0F;
            float value5 = amount42;
            float value6 = amount32;
            Matrix3x2fStack matrix3x2fStack2 = pose2;
            this.render(matrix3x2fStack2, value6, value5, value4, value3, value2, flag4);
            Matrix3x2fStack pose3 = context.pose();
            amount32 = this.getAmount3() + 23.0F;
            amount42 = this.getAmount4() + 10.0F;
            amount2 = 1.0F - animation.getAmount();
            boolean flag5 = false;
            float value7 = amount2;
            float value8 = 22.0F;
            float value9 = 6.0F;
            value5 = amount42;
            value6 = amount32;
            matrix3x2fStack2 = pose3;
            this.render(matrix3x2fStack2, value6, value5, value9, value8, value7, flag5);
         }

         float amount5 = Fonts.getFontRenderer6().computeAmount(mediaInfo.getTitle());
         float value10 = 74.0F;
         float amount6 = Render2D.INSTANCE
               .computeAmount(
                  MinecraftHolder.getLocalPlayer().tickCount - 1.0F, (float)MinecraftHolder.getLocalPlayer().tickCount, Render2D.INSTANCE.getAmount2()
               )
            % 120
            / 120.0F;
         String position = String.valueOf(mediaInfo.getPosition() % 60);
         Render2D instance = Render2D.INSTANCE;
         Rectangle rectangle = new Rectangle(this.getAmount3() + 43.0F, this.getAmount4(), this.getAmount3() + 116.0F, this.getAmount4() + 30.0F);
         instance.render(context, rectangle);
         FontRenderer fontRenderer6 = Fonts.getFontRenderer6();
         String title = mediaInfo.getTitle() + (amount5 > value10 ? mediaInfo.getTitle() : "");
         float amount33 = this.getAmount3() + 42.0F - (amount5 > value10 ? amount6 * amount5 : 0.0F);
         float amount43 = this.getAmount4() + 3.0F;
         Color color7 = ThemeManager.INSTANCE.getColor7();
         float value11 = amount43;
         float value12 = amount33;
         String text2 = title;
         fontRenderer6.render(context, text2, value12, value11, color7);
         Fonts.getFontRenderer4()
            .render(context, mediaInfo.getArtist(), this.getAmount3() + 42.0F, this.getAmount4() + 13.0F, ThemeManager.INSTANCE.getColor7());
         Render2D.INSTANCE.render(context);
         Fonts.getFontRenderer4()
            .render(
               context,
               (int)((float)mediaInfo.getPosition() / 60.0F) + (position.length() == 1 ? ":0" : ":") + position,
               this.getAmount3() + 42.0F,
               this.getAmount4() + 29.0F,
               ThemeManager.INSTANCE.getColor7()
            );
         float amount7 = Fonts.getFontRenderer4().computeAmount(text);
         Fonts.getFontRenderer4().render(context, text, this.getAmount3() + 116.0F - amount7, this.getAmount4() + 29.0F, ThemeManager.INSTANCE.getColor7());
         BoxPass.render(
            this.getAmount3() + 42.0F,
            this.getAmount4() + 23.0F,
            74.0F,
            4.0F,
            2.0F,
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor11(), 0.7F),
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor11(), 0.7F),
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor11(), 0.7F),
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor11(), 0.7F),
            context.pose(),
            2.0F,
            ThemeManager.INSTANCE.getColor12()
         );
         BoxPass.render(
            this.getAmount3() + 42.0F,
            this.getAmount4() + 23.0F,
            74.0F * coerceIn,
            4.0F,
            2.0F,
            ThemeManager.INSTANCE.getColor9(),
            ThemeManager.INSTANCE.getColor9(),
            ThemeManager.INSTANCE.getColor9(),
            ThemeManager.INSTANCE.getColor9(),
            context.pose(),
            1.0F,
            ThemeManager.INSTANCE.getColor12()
         );
         BoxPass.render(
            this.getAmount3() - 2 + 70.0F * coerceIn + 42.0F,
            this.getAmount4() + 23.0F - 1.5F,
            7.0F,
            7.0F,
            2.5F,
            ThemeManager.INSTANCE.getColor9(),
            ThemeManager.INSTANCE.getColor9(),
            ThemeManager.INSTANCE.getColor9(),
            ThemeManager.INSTANCE.getColor9(),
            context.pose(),
            1.0F,
            ThemeManager.INSTANCE.getColor12()
         );
         context.pose().popMatrix();
      }
   }

   @Override
   public void update() {
      if (stopWatch.testValue(1000)) {
         if (flag) {
            flag = false;
            int value2 = value++;
            if (value2 > 20) {
               flag2 = false;
            }

            KotlinCompat.thread(false, false, null, null, 0, () -> {
               for (Object mediaSession : MediaPlayerInfo.Instance.getMediaSessions()) {
                  IMediaSession iMediaSession = (IMediaSession)mediaSession;
                  mediaInfo = iMediaSession.getMedia();
                  text = iMediaSession.getOwner();
                  BufferedImage artwork = iMediaSession.getMedia().getArtwork();
                  if (artwork != null) {
                     BufferedImage bufferedImage = artwork;
                     Render2D.INSTANCE.applyGetTextureManager(catlean, RemoteTextureCache.INSTANCE.fetchHeight(bufferedImage));
                     vec2 = new Vec2(bufferedImage.getWidth(), bufferedImage.getHeight());
                  }

                  flag2 = true;
                  value = 0;
               }

               flag = true;
               stopWatch.update();
               return Unit.INSTANCE;
            }, 31, null);
         }
      }
   }
}
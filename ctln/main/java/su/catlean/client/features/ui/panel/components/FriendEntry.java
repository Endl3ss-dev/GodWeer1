// deobf by pusia
package su.catlean.client.features.ui.panel.components;

import java.awt.Color;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import su.catlean.client.features.ui.panel.panels.FriendsPanel;
import su.catlean.client.render.RemoteTextureCache;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedOutlinePass;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.FriendManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class FriendEntry extends PanelComponent {
   @NotNull
   private String name;
   @Nullable
   private Identifier identifier;
   private boolean flag;
   private boolean flag2;
   @NotNull
   private Animation animation;

   public FriendEntry(String name) {
      this.name = name;
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   @Override
   public void render(GuiGraphicsExtractor context, float amount, float amount2, float amount3, float amount4) {
      float amount5 = amount + 4.0F;
      this.flag2 = (amount3 <= amount + 102.0F ? amount5 <= amount3 : false) && (amount2 <= amount4 ? amount4 <= amount2 + 25.0F : false);
      this.animation.applyDoubleValue(this.flag2 ? 1.0F : 0.0F);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         amount + 4.0F,
         amount2,
         102.0F,
         25.0F,
         2.0F,
         this.flag2 ? 2.0F : 0.0F,
         0.15F,
         1.0F,
         ThemeManager.INSTANCE.getColor5(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      if (this.identifier == null) {
         if (!this.flag) {
            this.flag = true;
            this.identifier = RemoteTextureCache.INSTANCE.computeIdentifier("https://minotar.net/helm/" + this.name + "/32.png");
            this.flag = false;
         }
      } else {
         float amount6 = amount + 6.0F;
         float amount22 = amount2 + 2;
         Color white = Color.WHITE;
         Color white2 = Color.WHITE;
         Matrix3x2fStack pose = context.pose();
         Identifier identifier2 = this.identifier;
         Matrix3x2fStack matrix3x2fStack = pose;
         Color color = white2;
         Color color2 = white;
         TexturedOutlinePass.render(amount6, amount22, 20.0F, 20.0F, 2.0F, 0.0F, 0.0F, 1.0F, color2, color, matrix3x2fStack, identifier2);
      }

      Fonts.getFontRenderer().render(context, this.name, amount + 30.0F, amount2 + 7.0F, Color.WHITE);
      if (this.animation.getAmount() > 0.0F) {
         SvgTexture.defaultImpl(
            FriendsPanel.INSTANCE.getSvgTexture(),
            (Matrix3x2f)context.pose(),
            (double)amount + 85.0F + (10.0F - 10.0F * this.animation.getAmount()),
            (double)amount2 + 2.0F,
            0.0F,
            0.0F,
            Render2D.INSTANCE.computeColor(Color.RED, this.animation.getAmount()),
            null,
            0.0F,
            216,
            null
         );
      }
   }

   @Override
   public boolean checkIntValue(float amount, float amount2, int value) {
      if (this.flag2) {
         FriendManager.INSTANCE.checkRemove(this.name);
         return true;
      } else {
         return false;
      }
   }
}
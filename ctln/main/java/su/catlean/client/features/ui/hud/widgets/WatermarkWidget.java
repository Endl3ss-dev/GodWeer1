// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import java.awt.Color;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import su.catlean.CatLeanKt;
import su.catlean.api.data.IPCUser;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.render.RemoteTextureCache;
import su.catlean.client.render.TexturedOutlinePass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.discord.ipc.DiscordPresence;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;

public final class WatermarkWidget extends Widget {
   @NotNull
   public static final WatermarkWidget INSTANCE = new WatermarkWidget();

   private WatermarkWidget() {
      super("watermark-widget");
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      String text;
      String name;
      String text2;
      label16: {
         text = " " + ChatFormatting.GRAY + "v" + CatLeanKt.getModContainer().getMetadata().getVersion();
         name = MinecraftHolder.getLocalPlayer().getGameProfile().name();
         ServerData serverData = MinecraftHolder.getClientPacketListener().getServerData();
         if (serverData != null) {
            text2 = serverData.ip;
            if (serverData.ip != null) {
               break label16;
            }
         }

         text2 = "singleplayer";
      }

      String text3 = text2;
      float amount = Fonts.getFontRenderer().computeAmount("CatLean " + text);
      float amount2 = Fonts.getFontRenderer().computeAmount(name);
      float amount3 = Fonts.getFontRenderer().computeAmount(text3);
      float amount32 = this.getAmount3();
      Widget.defaultImpl(this, context.pose(), amount32 + 30.0F, this.getAmount4(), amount + 10.0F, 13.0F, 0.0F, false, 96, null);
      Widget.defaultImpl(this, context.pose(), amount32 + 30.0F, this.getAmount4() + 14.0F, amount2 + 6.0F, 13.0F, 0.0F, false, 96, null);
      Widget.defaultImpl(this, context.pose(), amount32 + 31.0F + amount2 + 6.0F, this.getAmount4() + 14.0F, amount3 + 7.0F, 13.0F, 0.0F, false, 96, null);
      this.render(
         context, text, amount32 + 33.0F + Fonts.getFontRenderer6().computeAmount("CatLean"), this.getAmount4() + 1.0F, ThemeManager.INSTANCE.getColor7()
      );
      this.render(context, name, amount32 + 33.0F, this.getAmount4() + 15.0F, ThemeManager.INSTANCE.getColor7());
      this.render(context, text3, amount32 + 40.5F + amount2, this.getAmount4() + 15.0F, ThemeManager.INSTANCE.getColor7());
      if (!Intrinsics.areEqual(DiscordPresence.INSTANCE.getIPCUser2(), DiscordPresence.INSTANCE.getIPCUser())) {
         float value = amount32 + 1.0F;
         float amount4 = this.getAmount4() - 1.0F;
         Color white = Color.WHITE;
         Color color5 = ThemeManager.INSTANCE.getColor5();
         Matrix3x2fStack pose = context.pose();
         Identifier identifier = RemoteTextureCache.INSTANCE.computeIdentifier(IPCUser.defaultImpl(DiscordPresence.INSTANCE.getIPCUser2(), 0, true, 1, null));
         Matrix3x2fStack matrix3x2fStack = pose;
         Color color = color5;
         Color color2 = white;
         TexturedOutlinePass.render(value, amount4, 27.0F, 27.0F, 3.0F, 2.0F, 0.0F, 1.0F, color2, color, matrix3x2fStack, identifier);
      } else {
         this.render(context, amount32);
      }

      this.updateAmount(amount + 40.0F, 28.0F);
   }

   @Override
   public void render2(GuiGraphicsExtractor context) {
      Fonts.getFontRenderer6().render(context, "CatLean", this.getAmount3() + 33.0F, this.getAmount4() + 0.5F, 10);
   }

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor, float amount) {
      guiGraphicsExtractor.pose().pushMatrix();
      Render2D.defaultImpl(
         Render2D.INSTANCE,
         (Matrix3x2f)guiGraphicsExtractor.pose(),
         ((AbstractClientPlayer)MinecraftHolder.getLocalPlayer()).getSkin().body().texturePath(),
         amount + 3.0F,
         this.getAmount4() + 1.0F,
         24.0F,
         24.0F,
         Color.WHITE,
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
         (Matrix3x2f)guiGraphicsExtractor.pose(),
         ((AbstractClientPlayer)MinecraftHolder.getLocalPlayer()).getSkin().body().texturePath(),
         amount + 1.0F,
         this.getAmount4() - 1.0F,
         28.0F,
         28.0F,
         Color.WHITE,
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
      guiGraphicsExtractor.pose().popMatrix();
   }
}
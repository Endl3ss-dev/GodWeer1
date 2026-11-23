// deobf by pusia
package su.catlean.client.features.ui;

import java.awt.Color;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import su.catlean.api.data.IPCUser;
import su.catlean.client.features.modules.misc.DiscordRpc;
import su.catlean.client.features.ui.panel.components.Button;
import su.catlean.client.render.RemoteTextureCache;
import su.catlean.client.render.TexturedOutlinePass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.LocalizationManager;
import su.catlean.client.services.discord.ipc.DiscordPresence;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Identifiers;
import su.catlean.utils.render.Render2D;

public final class DiscordConfirmScreen extends Screen {
   @NotNull
   private final Identifier catleanDiscordConfirm = Identifiers.computeIdentifier("catlean", "textures/exclude/discord_confirm.png");
   @NotNull
   private final Button button = new Button(0.0F, 0.0F, 100.0F, 25.0F, LocalizationManager.INSTANCE.computeText("gui.discord.yes", new Object[0]), () -> {
      DiscordRpc.INSTANCE.updateFlag(true);
      MinecraftHolder.getMinecraft().gui.setScreen((Screen)(new TitleScreen()));
      return Unit.INSTANCE;
   }, 0, null, null, 0.0F, 0.0F, 1984, null);
   @NotNull
   private final Button button2 = new Button(0.0F, 0.0F, 100.0F, 25.0F, LocalizationManager.INSTANCE.computeText("gui.discord.no", new Object[0]), () -> {
      DiscordRpc.INSTANCE.updateFlag(false);
      MinecraftHolder.getMinecraft().gui.setScreen((Screen)(new TitleScreen()));
      return Unit.INSTANCE;
   }, 0, null, null, 0.0F, 0.0F, 1984, null);

   public DiscordConfirmScreen() {
      super(Component.nullToEmpty("catlean-discord-confirm"));
   }

   public void extractRenderState(GuiGraphicsExtractor context, int value, int value2, float amount) {
      float width = this.width / 2.0F;
      float height = this.height / 2.0F;
      Render2D instance = Render2D.INSTANCE;
      Matrix3x2fStack pose = context.pose();
      float width2 = this.width;
      float height2 = this.height;
      Color[] colors = new Color[]{new Color(-1072689136)};
      float value3 = height2;
      float value4 = width2;
      float value5 = 0.0F;
      float value6 = 0.0F;
      Matrix3x2fStack matrix3x2fStack = pose;
      instance.render(matrix3x2fStack, value6, value5, value4, value3, colors);
      float amount2 = Fonts.getFontRenderer7().computeAmount("...") / 2.0F;
      Fonts.getFontRenderer7().render(context, "...", width - amount2, height - 94.0F, Color.GRAY);
      Fonts.getFontRenderer7()
         .render(context, StringsKt.dropLast("...", 3 - (int)(4 * (MinecraftHolder.getTime() % 1200.0 / 1200.0))), width - amount2, height - 94.0F, Color.WHITE);
      Fonts.getFontRenderer7()
         .render2(
            context,
            LocalizationManager.INSTANCE.computeText("gui.discord.confirm", new Object[0]),
            width,
            height - 10.0F,
            Render2D.INSTANCE.computeColor(Color.WHITE, 0.5F)
         );
      if (!Intrinsics.areEqual(DiscordPresence.INSTANCE.getIPCUser2(), DiscordPresence.INSTANCE.getIPCUser())) {
         float value7 = width - 85.0F;
         float value8 = height - 108.0F;
         Color white = Color.WHITE;
         Color white2 = Color.WHITE;
         Matrix3x2fStack pose2 = context.pose();
         Identifier identifier = RemoteTextureCache.INSTANCE.computeIdentifier(IPCUser.defaultImpl(DiscordPresence.INSTANCE.getIPCUser2(), 0, true, 1, null));
         Matrix3x2fStack matrix3x2fStack2 = pose2;
         Color color = white2;
         Color color2 = white;
         TexturedOutlinePass.render(value7, value8, 65.0F, 65.0F, 32.5F, 2.0F, 0.0F, 1.0F, color2, color, matrix3x2fStack2, identifier);
      } else {
         float value9 = width - 85.0F;
         float value10 = height - 108.0F;
         Color white3 = Color.WHITE;
         Color white4 = Color.WHITE;
         Matrix3x2fStack pose3 = context.pose();
         Identifier identifier2 = RemoteTextureCache.INSTANCE.computeIdentifier("https://i.imgur.com/y5rOVPP.png");
         Matrix3x2fStack matrix3x2fStack3 = pose3;
         Color color3 = white4;
         Color color4 = white3;
         TexturedOutlinePass.render(value9, value10, 65.0F, 65.0F, 32.5F, 2.0F, 0.0F, 1.0F, color4, color3, matrix3x2fStack3, identifier2);
      }

      float value11 = width + 20;
      float value12 = height - 108;
      Color white5 = Color.WHITE;
      Color white6 = Color.WHITE;
      Matrix3x2fStack pose4 = context.pose();
      Identifier catleanDiscordConfirm2 = this.catleanDiscordConfirm;
      Matrix3x2fStack matrix3x2fStack4 = pose4;
      Color color5 = white6;
      Color color6 = white5;
      TexturedOutlinePass.render(value11, value12, 65.0F, 65.0F, 32.5F, 2.0F, 0.0F, 1.0F, color6, color5, matrix3x2fStack4, catleanDiscordConfirm2);
      this.button2.render(width - 110.0F, height + 40.0F, value, value2, context);
      this.button.render(width + 10.0F, height + 40.0F, value, value2, context);
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean flag) {
      double value = click.x();
      double value2 = click.y();
      this.button.updateAmount((float)value, (float)value2);
      this.button2.updateAmount((float)value, (float)value2);
      return super.mouseClicked(click, flag);
   }
}
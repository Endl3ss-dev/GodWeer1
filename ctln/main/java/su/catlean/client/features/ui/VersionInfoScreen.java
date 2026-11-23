// deobf by pusia
package su.catlean.client.features.ui;

import su.catlean.utils.KotlinCompat;
import java.awt.Color;
import java.net.URI;
import java.util.List;
import kotlin.Unit;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import su.catlean.client.features.ui.panel.components.Button;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.LocalizationManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;

public final class VersionInfoScreen extends Screen {
   @NotNull
   private final SvgTexture svgTexture = new SvgTexture("warning", 32, 32);
   @NotNull
   private final Button button = new Button(0.0F, 0.0F, 100.0F, 25.0F, LocalizationManager.INSTANCE.computeText("gui.version.yes", new Object[0]), () -> {
      String text = "https://catlean.su/";
      Util.getPlatform().openUri(URI.create(text));
      return Unit.INSTANCE;
   }, 0, null, null, 0.0F, 0.0F, 1984, null);
   @NotNull
   private final Button button2 = new Button(0.0F, 0.0F, 100.0F, 25.0F, LocalizationManager.INSTANCE.computeText("gui.version.no", new Object[0]), () -> {
      MinecraftHolder.getMinecraft().close();
      return Unit.INSTANCE;
   }, 0, null, null, 0.0F, 0.0F, 1984, null);

   public VersionInfoScreen() {
      super(Component.nullToEmpty("catlean-version-info-screen"));
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
      CharSequence text = LocalizationManager.INSTANCE.computeText("gui.version.confirm", new Object[0]);
      String[] texts = new String[]{", "};
      List<String> splitdefault = KotlinCompat.split(text, texts, false, 0, 6, null);
      float value7 = 0.0F;

      for (String text2 : (Iterable<String>)splitdefault) {
         Fonts.getFontRenderer7().render2(context, text2, width, height - 10.0F + value7, Render2D.INSTANCE.computeColor(Color.WHITE, 0.5F));
         value7 += 23.0F;
      }

      int currentTimeMillis = (int)(System.currentTimeMillis() / 3.0 % 360);
      currentTimeMillis = currentTimeMillis >= 180 ? 360 - currentTimeMillis : currentTimeMillis;
      Color alpha = Render2D.INSTANCE.fetchAlpha(Color.RED, Color.YELLOW, currentTimeMillis / 360.0F);
      SvgTexture.defaultImpl(this.svgTexture, (Matrix3x2f)context.pose(), width - 16.0, height - 60.0, 0.0F, 0.0F, alpha, null, 0.0F, 216, null);
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

   public void onClose() {
   }
}
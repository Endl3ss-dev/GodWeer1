// deobf by pusia
package su.catlean.client.render;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.attributes.ViewBox;
import com.github.weisj.jsvg.parser.SVGLoader;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Identifiers;
import su.catlean.utils.render.Render2D;

public final class SvgTexture {
   @NotNull
   private String name;
   private int value;
   private int value2;
   private int value3;
   @Nullable
   private Identifier identifier;

   public SvgTexture(String name, int value, int value2) {
      this.name = name;
      this.value = value;
      this.value2 = value2;
      this.value3 = -1;
   }

   public final int getValue() {
      return this.value;
   }

   public final void setValue(int value) {
      this.value = value;
   }

   public final int getValue2() {
      return this.value2;
   }

   public final void setValue2(int value) {
      this.value2 = value;
   }

   private final void applyCeil(float amount, float amount2) {
      if (this.identifier != null) {
         this.applyGetNamespace();
      }

      this.identifier = Identifiers.computeIdentifier("temp/vector" + this.name);

      try {
         SVGDocument load = new SVGLoader().load(this.getClass().getResourceAsStream("/assets/catlean/vector/" + this.name + ".svg"));
         BufferedImage bufferedImage = new BufferedImage((int)Math.ceil(amount), (int)Math.ceil(amount2), 2);
         Graphics2D graphics = bufferedImage.createGraphics();
         graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
         if (load != null) {
            load.render(null, graphics, new ViewBox(amount, amount2));
         }

         graphics.dispose();
         Render2D.INSTANCE.applyGetTextureManager(this.identifier, bufferedImage);
      } catch (Throwable exception) {
         this.identifier = Identifier.withDefaultNamespace("missingno");
      }
   }

   public final void applyCeil(
      Matrix3x2f stack, double amount, double amount2, float amount3, float amount4, Color primaryColor, Color secondaryColor, float amount5
   ) {
      double amount6 = amount + 0.5F;
      double amount22 = amount2 + 2.0F;
      int guiScale = MinecraftHolder.getMinecraft().getWindow().getGuiScale();
      if (this.value3 != guiScale || this.identifier == null) {
         this.value3 = guiScale;
         this.applyCeil(this.value * this.value3, this.value2 * this.value3);
      }

      if (this.value != (int)((float)Math.ceil(amount3)) || this.value2 != (int)((float)Math.ceil(amount4))) {
         this.value = (int)((float)Math.ceil(amount3));
         this.value2 = (int)((float)Math.ceil(amount4));
         this.applyCeil(this.value, this.value2);
      }

      Render2D.defaultImpl(
         Render2D.INSTANCE,
         stack,
         this.identifier,
         (float)amount6,
         (float)amount22,
         (float)Math.ceil(amount3),
         (float)Math.ceil(amount4),
         Render2D.INSTANCE.computeColor(primaryColor, amount5 * (primaryColor.getAlpha() / 255.0F)),
         Render2D.INSTANCE.computeColor(secondaryColor, amount5 * (primaryColor.getAlpha() / 255.0F)),
         Render2D.INSTANCE.computeColor(primaryColor, amount5 * (primaryColor.getAlpha() / 255.0F)),
         Render2D.INSTANCE.computeColor(secondaryColor, amount5 * (primaryColor.getAlpha() / 255.0F)),
         0.0F,
         0.0F,
         0.0F,
         0.0F,
         false,
         31744,
         null
      );
   }

   public static void defaultImpl(
      SvgTexture svgTexture,
      Matrix3x2f matrix3x2f,
      double amount,
      double amount2,
      float amount3,
      float amount4,
      Color color,
      Color color2,
      float amount5,
      int value,
      Object object
   ) {
      if ((value & 8) != 0) {
         amount3 = svgTexture.value;
      }

      if ((value & 16) != 0) {
         amount4 = svgTexture.value2;
      }

      if ((value & 32) != 0) {
         color = Color.WHITE;
      }

      if ((value & 64) != 0) {
         color2 = color;
      }

      if ((value & 128) != 0) {
         amount5 = 1.0F;
      }

      svgTexture.applyCeil(matrix3x2f, amount, amount2, amount3, amount4, color, color2, amount5);
   }

   private final void applyGetNamespace() {
      if (Intrinsics.areEqual(this.identifier != null ? this.identifier.getNamespace() : null, "catlean") && this.identifier != null) {
         MinecraftHolder.getMinecraft().getTextureManager().release(this.identifier);
      }

      this.identifier = null;
   }
}
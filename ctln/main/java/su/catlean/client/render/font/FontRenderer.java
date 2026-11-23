// deobf by pusia
package su.catlean.client.render.font;

import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.awt.Color;
import java.awt.Font;
import java.io.Closeable;
import kotlin.ResultKt;
import kotlin.Unit;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import su.catlean.utils.MinecraftHolder;

public final class FontRenderer implements Closeable {
   @NotNull
   private Font font;
   @NotNull
   private Font fallBack;
   private final float amount;
   @NotNull
   private final Object2ObjectOpenHashMap object2ObjectOpenHashMap;
   @NotNull
   private final Object2ObjectOpenHashMap object2ObjectOpenHashMap2;
   @NotNull
   private final ObjectArrayList objectArrayList;
   @NotNull
   private final Char2ObjectArrayMap char2ObjectArrayMap;
   private int value;
   private float amount2;

   public FontRenderer(Font font, Font fallBack, float amount) {
      this.font = font;
      this.fallBack = fallBack;
      this.amount = amount;
      this.object2ObjectOpenHashMap = new Object2ObjectOpenHashMap();
      this.object2ObjectOpenHashMap2 = new Object2ObjectOpenHashMap();
      this.objectArrayList = new ObjectArrayList();
      this.char2ObjectArrayMap = new Char2ObjectArrayMap();
      this.applyDeriveFont();
   }

   public final float getAmount2() {
      return this.amount2;
   }

   public final void setAmount2(float amount) {
      this.amount2 = amount;
   }

   public final void applyDeriveFont() {
      this.value = MinecraftHolder.getMinecraft().getWindow().getGuiScale();
      this.font = this.font.deriveFont(this.amount * this.value);
      this.fallBack = this.fallBack.deriveFont(this.amount * this.value);
   }

   public final void render(GuiGraphicsExtractor context, String s, float amount, float amount2, Color color) {
      Matrix3x2fStack pose = context.pose();
      Object object = null;
      byte byteValue = 96;
      byte byteValue2 = 0;
      boolean flag = false;
      Matrix3x2fStack matrix3x2fStack = pose;
      defaultImpl2(this, matrix3x2fStack, s, amount, amount2, color, flag, byteValue2, byteValue, object);
   }

   public final void render(Matrix3x2fStack m, String s, float amount, float amount2, Color color, boolean flag, int value) {
      float value2 = this.value;
      float amount22 = this.amount2;
      Font fallBack2 = this.fallBack;
      Font font2 = this.font;
      ObjectArrayList objectArrayList2 = this.objectArrayList;
      Char2ObjectArrayMap char2ObjectArrayMap2 = this.char2ObjectArrayMap;
      float value3 = value2;
      FontAtlas instance = FontAtlas.INSTANCE;
      this.amount2 += 0.1F;
      Unit instance2 = Unit.INSTANCE;
      instance.render(m, s, amount, amount2, color, flag, value, value3, char2ObjectArrayMap2, objectArrayList2, font2, fallBack2, amount22, this.object2ObjectOpenHashMap2);
   }

   public static void defaultImpl(
      FontRenderer fontRenderer,
      Matrix3x2fStack matrix3x2fStack,
      String text,
      float amount,
      float amount2,
      Color color,
      boolean flag,
      int value,
      int value2,
      Object object
   ) {
      if ((value2 & 32) != 0) {
         flag = false;
      }

      if ((value2 & 64) != 0) {
         value = 0;
      }

      fontRenderer.render(matrix3x2fStack, text, amount, amount2, color, flag, value);
   }

   public final void render2(Matrix3x2fStack m, String s, float amount, float amount2, Color color, boolean flag, int value) {
      FontAtlas.INSTANCE
         .render(
            m,
            s,
            amount,
            amount2,
            color,
            flag,
            value,
            this.value,
            this.char2ObjectArrayMap,
            this.objectArrayList,
            this.font,
            this.fallBack,
            this.object2ObjectOpenHashMap
         );
   }

   public static void defaultImpl2(
      FontRenderer fontRenderer,
      Matrix3x2fStack matrix3x2fStack,
      String text,
      float amount,
      float amount2,
      Color color,
      boolean flag,
      int value,
      int value2,
      Object object
   ) {
      if ((value2 & 32) != 0) {
         flag = false;
      }

      if ((value2 & 64) != 0) {
         value = 0;
      }

      fontRenderer.render2(matrix3x2fStack, text, amount, amount2, color, flag, value);
   }

   public final void render(Matrix3x2fStack m, String s, float amount, float amount2, Color color) {
      defaultImpl2(
         this,
         m,
         s,
         amount - this.computeAmount(s) / 2.0F,
         amount2 - FontAtlas.INSTANCE.computeAmount(s, this.char2ObjectArrayMap, this.objectArrayList, this.font, this.fallBack, this.value) / 2.0F,
         color,
         false,
         0,
         96,
         null
      );
   }

   public final void render2(GuiGraphicsExtractor context, String s, float amount, float amount2, Color color) {
      Matrix3x2fStack pose = context.pose();
      float amount3 = amount - this.computeAmount(s) / 2.0F;
      float amount22 = amount2
         - FontAtlas.INSTANCE.computeAmount(s, this.char2ObjectArrayMap, this.objectArrayList, this.font, this.fallBack, this.value) / 2.0F;
      Object object = null;
      byte byteValue = 96;
      byte byteValue2 = 0;
      boolean flag = false;
      float value2 = amount22;
      float value3 = amount3;
      Matrix3x2fStack matrix3x2fStack = pose;
      defaultImpl2(this, matrix3x2fStack, s, value3, value2, color, flag, byteValue2, byteValue, object);
   }

   public final void update() {
      FontAtlas.INSTANCE.applyGetKey(this.object2ObjectOpenHashMap2);
      this.amount2 = 0.0F;
   }

   public final float computeAmount(String s) {
      return FontAtlas.INSTANCE.computeAmount2(s, this.char2ObjectArrayMap, this.objectArrayList, this.font, this.fallBack, this.value);
   }

   @Override
   public void close() {
      FontRenderer thisValue = this;

      try {
         FontRenderer fontRenderer = thisValue;
         fontRenderer.objectArrayList.removeIf(arg -> {
            ((GlyphRasterizer)arg).applyClear();
            return true;
         });
         fontRenderer.char2ObjectArrayMap.clear();
         Unit instance = Unit.INSTANCE;
      } catch (Throwable exception) {
         Object failure = ResultKt.createFailure(exception);
      }
   }

   public final void render(GuiGraphicsExtractor context, String s, float amount, float amount2, int value) {
      this.render2(context.pose(), s, amount, amount2, Color.WHITE, true, value);
   }

   public final void render2(GuiGraphicsExtractor context, String s, float amount, float amount2, int value) {
      this.render(context, s, amount - this.computeAmount(s) / 2.0F, amount2, value);
   }
}
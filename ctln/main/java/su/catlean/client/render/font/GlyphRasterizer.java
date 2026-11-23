// deobf by pusia
package su.catlean.client.render.font;

import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.client.services.discord.ipc.IpcUser;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;

public final class GlyphRasterizer {
   private final char symbol;
   private final char symbol2;
   @NotNull
   private final Font font;
   @NotNull
   private final Identifier bindToTexture;
   private final int value;
   @NotNull
   private final Char2ObjectArrayMap char2ObjectArrayMap;
   private int value2;
   private int value3;
   private boolean flag;

   public GlyphRasterizer(char symbol, char symbol2, Font font, Identifier bindToTexture, int value) {
      this.symbol = symbol;
      this.symbol2 = symbol2;
      this.font = font;
      this.bindToTexture = bindToTexture;
      this.value = value;
      this.char2ObjectArrayMap = new Char2ObjectArrayMap();
   }

   @NotNull
   public final Font getFont() {
      return this.font;
   }

   @NotNull
   public final Identifier getBindToTexture() {
      return this.bindToTexture;
   }

   public final int getValue2() {
      return this.value2;
   }

   public final void setValue2(int value) {
      this.value2 = value;
   }

   public final int getValue3() {
      return this.value3;
   }

   public final void setValue3(int value) {
      this.value3 = value;
   }

   @Nullable
   public final IpcUser computeIpcUser(char symbol) {
      if (!this.flag) {
         this.applyMax();
      }

      return (IpcUser)this.char2ObjectArrayMap.get(symbol);
   }

   public final void applyClear() {
      MinecraftHolder.getMinecraft().getTextureManager().release(this.bindToTexture);
      this.char2ObjectArrayMap.clear();
      this.value2 = -1;
      this.value3 = -1;
      this.flag = false;
   }

   public final boolean testSymbol(char symbol) {
      char symbol3 = this.symbol;
      return symbol < this.symbol2 ? symbol3 <= symbol : false;
   }

   private final Font computeFont(char symbol) {
      return this.font.canDisplay(symbol) ? this.font : this.font;
   }

   private final void applyMax() {
      if (!this.flag) {
         int symbol22 = this.symbol2 - this.symbol - 1;
         int value4 = (int)(Math.ceil(Math.sqrt(symbol22)) * 1.5);
         this.char2ObjectArrayMap.clear();
         int value5 = 0;
         int value6 = 0;
         int value7 = 0;
         int value8 = 0;
         int value9 = 0;
         int value10 = 0;
         int value11 = 0;
         List<IpcUser> arrayList = new ArrayList();
         AffineTransform affineTransform = new AffineTransform();

         for (FontRenderContext fontRenderContext = new FontRenderContext(affineTransform, true, false); value5 <= symbol22; value6++) {
            char symbol3 = (char)(this.symbol + value5);
            Font font2 = this.computeFont(symbol3);
            Rectangle2D stringBounds = font2.getStringBounds(String.valueOf(symbol3), fontRenderContext);
            int width = (int)Math.ceil(stringBounds.getWidth());
            int height = (int)Math.ceil(stringBounds.getHeight());
            value5++;
            value7 = (int)Math.max((double)value7, (double)(value9 + width));
            value8 = (int)Math.max((double)value8, (double)(value10 + height));
            if (value6 >= value4) {
               value9 = 0;
               value10 += value11 + this.value;
               value6 = 0;
               value11 = 0;
            }

            value11 = (int)Math.max((double)value11, (double)height);
            arrayList.add(new IpcUser(value9, value10, width, height, symbol3, this));
            value9 += width + this.value;
         }

         BufferedImage bufferedImage = new BufferedImage((int)Math.max((double)(value7 + this.value), 1.0), (int)Math.max((double)(value8 + this.value), 1.0), 2);
         this.value2 = bufferedImage.getWidth();
         this.value3 = bufferedImage.getHeight();
         Graphics2D graphics = bufferedImage.createGraphics();
         graphics.setColor(new Color(255, 255, 255, 0));
         graphics.fillRect(0, 0, this.value2, this.value3);
         graphics.setColor(Color.WHITE);
         graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
         graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
         graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

         for (IpcUser ipcUser : (Iterable<IpcUser>)arrayList) {
            graphics.setFont(this.computeFont(ipcUser.getSymbol()));
            graphics.drawString(String.valueOf(ipcUser.getSymbol()), ipcUser.getValue(), ipcUser.getValue2() + graphics.getFontMetrics().getAscent());
            this.char2ObjectArrayMap.put(ipcUser.getSymbol(), ipcUser);
         }

         Render2D.INSTANCE.applyGetTextureManager(this.bindToTexture, bufferedImage);
         this.flag = true;
      }
   }
}
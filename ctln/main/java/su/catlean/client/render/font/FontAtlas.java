// deobf by pusia
package su.catlean.client.render.font;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap.Entry;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import kotlin.Triple;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix3x2fc;
import su.catlean.api.data.Box;
import su.catlean.client.features.modules.misc.NameProtect;
import su.catlean.client.render.Pipelines;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.discord.ipc.IpcUser;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Identifiers;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.client.render.font.GlyphMatrix;

public final class FontAtlas {
   @NotNull
   public static final FontAtlas INSTANCE = new FontAtlas();
   @NotNull
   private static final Char2IntArrayMap char2IntArrayMap = new Char2IntArrayMap();

   private FontAtlas() {
   }

   @NotNull
   public final Char2IntArrayMap getChar2IntArrayMap() {
      return char2IntArrayMap;
   }

   public final int computeValue(int value, int value2) {
      return value2 * (int)Math.floor((double)value / value2);
   }

   @NotNull
   public final String computeText(String text) {
      char[] charArray = text.toCharArray();
      StringBuilder stringBuilder = new StringBuilder(charArray.length);
      int value = 0;

      while (value < charArray.length) {
         char ch = charArray[value];
         if (ch == 167) {
            if (value + 1 >= charArray.length) {
               break;
            }

            char ch2 = charArray[value + 1];
            switch (ch2) {
               case 'X':
               case 'x':
                  if (this.testSymbols(charArray, value)) {
                     value += 14;
                     break;
                  }
               default:
                  value += 2;
            }
         } else {
            stringBuilder.append(ch);
            value++;
         }
      }

      return stringBuilder.toString();
   }

   @NotNull
   public final Identifier getIdentifier() {
      return Identifiers.computeIdentifier("catlean", "temp/" + this.getText());
   }

   @NotNull
   public final String getText() {
      return IntStream.range(0, 32).mapToObj(arg -> String.valueOf((char)new Random().nextInt(97, 123))).collect(Collectors.joining());
   }

   @NotNull
   public final int[] computeValues(int value) {
      int value2 = value >> 16 & 0xFF;
      int value3 = value >> 8 & 0xFF;
      int value4 = value & 0xFF;
      return new int[]{value2, value3, value4};
   }

   public final float computeAmount(float amount) {
      return Float.isNaN(amount) ? 1.0F : MathKt.roundToInt(MathKt.roundToInt(amount * 100.0F) / 100.0F * 10.0F) / 10.0F;
   }

   @NotNull
   public final GlyphRasterizer computeGlyphRasterizer(char symbol, char symbol2, Font font, ObjectArrayList maps, int value) {
      GlyphRasterizer glyphRasterizer = new GlyphRasterizer(symbol, symbol2, font, this.getIdentifier(), value);
      maps.add(glyphRasterizer);
      return glyphRasterizer;
   }

   @Nullable
   public final IpcUser computeIpcUser(char symbol, ObjectArrayList maps, Font font, Font fallBack) {
      byte byteValue = 5;
      short shortValue = 512;
      ObjectListIterator iterator = maps.iterator();

      while (iterator.hasNext()) {
         GlyphRasterizer glyphRasterizer = (GlyphRasterizer)iterator.next();
         if (glyphRasterizer.testSymbol(symbol)) {
            return glyphRasterizer.computeIpcUser(symbol);
         }
      }

      int value = this.computeValue(symbol, shortValue);
      if (font.canDisplay(symbol)) {
         IpcUser ipcUser = this.computeGlyphRasterizer((char)value, (char)(value + shortValue), font, maps, byteValue).computeIpcUser(symbol);
         if (ipcUser != null) {
            return ipcUser;
         }
      }

      return fallBack.canDisplay(symbol) ? this.computeGlyphRasterizer((char)value, (char)(value + shortValue), fallBack, maps, byteValue).computeIpcUser(symbol) : null;
   }

   @Nullable
   public final IpcUser computeIpcUser(char symbol, Char2ObjectArrayMap allGlyphs, ObjectArrayList maps, Font font, Font fallBack) {
      if (allGlyphs.containsKey(symbol)) {
         return (IpcUser)allGlyphs.get(symbol);
      } else {
         IpcUser ipcUser = this.computeIpcUser(symbol, maps, font, fallBack);
         ((Map)allGlyphs).put(symbol, ipcUser);
         return ipcUser;
      }
   }

   public final void render(
      Matrix3x2fStack stack,
      String s,
      float amount,
      float amount2,
      Color color,
      boolean flag,
      int value,
      float amount3,
      Char2ObjectArrayMap allGlyphs,
      ObjectArrayList maps,
      Font font,
      Font fallBack,
      Object2ObjectOpenHashMap glyphPage
   ) {
      String text = NameProtect.INSTANCE.computeText(s);
      float red = color.getRed() / 255.0F;
      float green = color.getGreen() / 255.0F;
      float blue = color.getBlue() / 255.0F;
      float alpha = color.getAlpha() / 255.0F;
      float amount4 = this.computeAmount(amount + 0.5F);
      float amount5 = this.computeAmount(amount2 + 1.5F);
      stack.pushMatrix();
      stack.translate(amount4, amount5);
      stack.scale(1.0F / amount3, 1.0F / amount3);
      Matrix3x2f matrix3x2f = new Matrix3x2f((Matrix3x2fc)stack);
      char[] charArray = text.toCharArray();
      float value2 = 0.0F;
      float value3 = 0.0F;
      boolean flag2 = false;
      int value4 = 0;
      int value5 = 0;

      while (value5 < charArray.length) {
         char ch = charArray[value5];
         if (flag2) {
            flag2 = false;
            switch (ch) {
               case 'X':
               case 'x':
                  Triple triple = this.computeTriple(charArray, value5);
                  if (triple != null) {
                     int value6 = ((Number)triple.component1()).intValue();
                     int value7 = ((Number)triple.component2()).intValue();
                     int value8 = ((Number)triple.component3()).intValue();
                     red = value6 / 255.0F;
                     green = value7 / 255.0F;
                     blue = value8 / 255.0F;
                     value5 += 12;
                  }

                  value5++;
                  break;
               default:
                  char upperCase = Character.toUpperCase(ch);
                  if (char2IntArrayMap.containsKey(upperCase)) {
                     int char2IntArrayMapItem = char2IntArrayMap.get(upperCase);
                     int[] values = this.computeValues(char2IntArrayMapItem);
                     red = values[0] / 255.0F;
                     green = values[1] / 255.0F;
                     blue = values[2] / 255.0F;
                  } else if (upperCase == 'R') {
                     red = color.getRed() / 255.0F;
                     green = color.getGreen() / 255.0F;
                     blue = color.getBlue() / 255.0F;
                     alpha = color.getAlpha() / 255.0F;
                  }

                  value5++;
            }
         } else {
            if (flag) {
               Color color2 = ThemeManager.INSTANCE.computeColor(value5 * value);
               red = color2.getRed() / 255.0F;
               green = color2.getGreen() / 255.0F;
               blue = color2.getBlue() / 255.0F;
               alpha = color2.getAlpha() / 255.0F;
            }

            switch (ch) {
               case '\n':
                  value3 += this.computeAmount(text.substring(value4, value5), allGlyphs, maps, font, fallBack, amount3) * amount3;
                  value2 = 0.0F;
                  value4 = value5 + 1;
                  value5++;
                  break;
               case '§':
                  flag2 = true;
                  value5++;
                  break;
               default:
                  IpcUser ipcUser = this.computeIpcUser(ch, allGlyphs, maps, font, fallBack);
                  if (ipcUser != null) {
                     if (ipcUser.getSymbol() != ' ') {
                        Identifier bindToTexture = ipcUser.getOwner().getBindToTexture();
                        GlyphMatrix glyphMatrix = new GlyphMatrix(
                           value2,
                           value3,
                           red,
                           green,
                           blue,
                           alpha,
                           ipcUser.getValue3(),
                           ipcUser.getValue4(),
                           ipcUser.getOwner().getValue2(),
                           ipcUser.getOwner().getValue3(),
                           ipcUser.getValue(),
                           ipcUser.getValue2(),
                           0.0F,
                           matrix3x2f,
                           null,
                           16384,
                           null
                        );
                        ((ObjectList)glyphPage.computeIfAbsent(bindToTexture, arg -> (ObjectList)(new ObjectArrayList()))).add(glyphMatrix);
                     }

                     value2 += ipcUser.getValue3();
                  }

                  value5++;
            }
         }
      }

      ObjectIterator iterator = ((ObjectSet)glyphPage.keySet()).iterator();

      while (iterator.hasNext()) {
         Identifier identifier = (Identifier)iterator.next();
         GpuTextureView textureView = MinecraftHolder.getMinecraft().getTextureManager().getTexture(identifier).getTextureView();
         ObjectList glyphPageItem = (ObjectList)glyphPage.get(identifier);
         MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, glyphPageItem.size(), false, 4, null);
         ObjectListIterator iterator2 = glyphPageItem.iterator();

         while (iterator2.hasNext()) {
            GlyphMatrix glyphMatrix2 = (GlyphMatrix)iterator2.next();
            float amount6 = glyphMatrix2.getAmount();
            float amount22 = glyphMatrix2.getAmount2();
            float value9 = glyphMatrix2.getValue();
            float value22 = glyphMatrix2.getValue2();
            float value52 = (float)glyphMatrix2.getValue5() / glyphMatrix2.getValue3();
            float value62 = (float)glyphMatrix2.getValue6() / glyphMatrix2.getValue4();
            float value53 = (float)(glyphMatrix2.getValue5() + glyphMatrix2.getValue()) / glyphMatrix2.getValue3();
            float value63 = (float)(glyphMatrix2.getValue6() + glyphMatrix2.getValue2()) / glyphMatrix2.getValue4();
            MeshBuilder.defaultImpl(meshBuilder, amount6 + 0, amount22 + value22, 0.0F, 4, null)
               .computeMeshBuilder(value52, value63)
               .computeMeshBuilder(glyphMatrix2.getAmount3(), glyphMatrix2.getAmount4(), glyphMatrix2.getAmount5(), glyphMatrix2.getAmount6());
            MeshBuilder.defaultImpl(meshBuilder, amount6 + value9, amount22 + value22, 0.0F, 4, null)
               .computeMeshBuilder(value53, value63)
               .computeMeshBuilder(glyphMatrix2.getAmount3(), glyphMatrix2.getAmount4(), glyphMatrix2.getAmount5(), glyphMatrix2.getAmount6());
            MeshBuilder.defaultImpl(meshBuilder, amount6 + value9, amount22 + 0, 0.0F, 4, null)
               .computeMeshBuilder(value53, value62)
               .computeMeshBuilder(glyphMatrix2.getAmount3(), glyphMatrix2.getAmount4(), glyphMatrix2.getAmount5(), glyphMatrix2.getAmount6());
            MeshBuilder.defaultImpl(meshBuilder, amount6 + 0, amount22 + 0, 0.0F, 4, null)
               .computeMeshBuilder(value52, value62)
               .computeMeshBuilder(glyphMatrix2.getAmount3(), glyphMatrix2.getAmount4(), glyphMatrix2.getAmount5(), glyphMatrix2.getAmount6());
         }

         MeshBuilder.defaultImpl(
            meshBuilder,
            Pipelines.INSTANCE.getRenderPipeline14(),
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget(),
            null,
            null,
            matrix3x2f,
            MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)),
            null,
            76,
            null
         );
      }

      glyphPage.clear();
      stack.popMatrix();
   }

   public final void render(
      Matrix3x2fStack stack,
      String s,
      float amount,
      float amount2,
      Color color,
      boolean flag,
      int value,
      float amount3,
      Char2ObjectArrayMap allGlyphs,
      ObjectArrayList maps,
      Font font,
      Font fallBack,
      float amount4,
      Object2ObjectOpenHashMap batchedDraws
   ) {
      String text = NameProtect.INSTANCE.computeText(s);
      float red = color.getRed() / 255.0F;
      float green = color.getGreen() / 255.0F;
      float blue = color.getBlue() / 255.0F;
      float alpha = color.getAlpha() / 255.0F;
      float amount5 = this.computeAmount(amount + 0.5F);
      float amount6 = this.computeAmount(amount2 + 1.5F);
      stack.pushMatrix();
      stack.translate(amount5, amount6);
      stack.scale(1.0F / amount3, 1.0F / amount3);
      Matrix3x2f matrix3x2f = new Matrix3x2f((Matrix3x2fc)stack);
      char[] charArray = text.toCharArray();
      float value2 = 0.0F;
      float value3 = 0.0F;
      boolean flag2 = false;
      int value4 = 0;
      Identifier identifier = null;
      ObjectList<GlyphMatrix> objectList = null;
      int value5 = 0;

      while (value5 < charArray.length) {
         char ch = charArray[value5];
         if (flag2) {
            flag2 = false;
            switch (ch) {
               case 'X':
               case 'x':
                  Triple triple = this.computeTriple(charArray, value5);
                  if (triple != null) {
                     int value6 = ((Number)triple.component1()).intValue();
                     int value7 = ((Number)triple.component2()).intValue();
                     int value8 = ((Number)triple.component3()).intValue();
                     red = value6 / 255.0F;
                     green = value7 / 255.0F;
                     blue = value8 / 255.0F;
                     value5 += 12;
                  }

                  value5++;
                  break;
               default:
                  char upperCase = Character.toUpperCase(ch);
                  if (char2IntArrayMap.containsKey(upperCase)) {
                     int char2IntArrayMapItem = char2IntArrayMap.get(upperCase);
                     int[] values = this.computeValues(char2IntArrayMapItem);
                     red = values[0] / 255.0F;
                     green = values[1] / 255.0F;
                     blue = values[2] / 255.0F;
                  } else if (upperCase == 'R') {
                     red = color.getRed() / 255.0F;
                     green = color.getGreen() / 255.0F;
                     blue = color.getBlue() / 255.0F;
                     alpha = color.getAlpha() / 255.0F;
                  }

                  value5++;
            }
         } else {
            if (flag) {
               Color color2 = ThemeManager.INSTANCE.computeColor(value5 * value);
               red = color2.getRed() / 255.0F;
               green = color2.getGreen() / 255.0F;
               blue = color2.getBlue() / 255.0F;
               alpha = color2.getAlpha() / 255.0F;
            }

            switch (ch) {
               case '\n':
                  value3 += this.computeAmount(text.substring(value4, value5), allGlyphs, maps, font, fallBack, amount3) * amount3;
                  value2 = 0.0F;
                  value4 = value5 + 1;
                  value5++;
                  break;
               case '§':
                  flag2 = true;
                  value5++;
                  break;
               default:
                  IpcUser ipcUser = this.computeIpcUser(ch, allGlyphs, maps, font, fallBack);
                  if (ipcUser != null) {
                     if (ipcUser.getSymbol() != ' ') {
                        Identifier bindToTexture = ipcUser.getOwner().getBindToTexture();
                        GlyphMatrix glyphMatrix = new GlyphMatrix(
                           value2,
                           value3,
                           red,
                           green,
                           blue,
                           alpha,
                           ipcUser.getValue3(),
                           ipcUser.getValue4(),
                           ipcUser.getOwner().getValue2(),
                           ipcUser.getOwner().getValue3(),
                           ipcUser.getValue(),
                           ipcUser.getValue2(),
                           amount4,
                           matrix3x2f,
                           null,
                           16384,
                           null
                        );
                        if (!Intrinsics.areEqual(identifier, bindToTexture)) {
                           identifier = bindToTexture;
                           ObjectList batchedDraw = (ObjectList)batchedDraws.get(bindToTexture);
                           if (batchedDraw == null) {
                              batchedDraw = (ObjectList)(new ObjectArrayList());
                              ((Map)batchedDraws).put(bindToTexture, batchedDraw);
                           }

                           objectList = batchedDraw;
                        }

                        objectList.add(glyphMatrix);
                     }

                     value2 += ipcUser.getValue3();
                  }

                  value5++;
            }
         }
      }

      stack.popMatrix();
   }

   public final float computeAmount(String text, Char2ObjectArrayMap allGlyphs, ObjectArrayList maps, Font font, Font fallBack, float amount) {
      String text2 = NameProtect.INSTANCE.computeText(text);
      char[] charArray = this.computeText(text2).toCharArray();
      if (charArray.length == 0) {
         char[] chs = new char[]{' '};
         charArray = chs;
      }

      float value4 = 0.0F;
      float value = 0.0F;
      char[] chs2 = charArray;
      int value2 = 0;

      for (int i = charArray.length; value2 < i; value2++) {
         char ch = chs2[value2];
         if (ch == '\n') {
            if (value4 == 0.0F) {
               IpcUser ipcUser = this.computeIpcUser(' ', allGlyphs, maps, font, fallBack);
               value4 = ipcUser != null ? ipcUser.getValue4() : 1.0F / amount;
            }

            value += value4;
            value4 = 0.0F;
         } else {
            IpcUser ipcUser2 = this.computeIpcUser(ch, allGlyphs, maps, font, fallBack);
            value4 = Math.max(ipcUser2 == null ? 0.0F : ipcUser2.getValue4() / amount, value4);
         }
      }

      return value4 + value;
   }

   public final void applyGetKey(Object2ObjectOpenHashMap batchedDraws) {
      if (!batchedDraws.isEmpty()) {
         ObjectIterator iterator = batchedDraws.object2ObjectEntrySet().iterator();

         while (iterator.hasNext()) {
            Entry entry = (Entry)iterator.next();
            Identifier key = (Identifier)((java.util.Map.Entry)entry).getKey();
            ObjectList value = (ObjectList)((java.util.Map.Entry)entry).getValue();
            GpuTextureView textureView = MinecraftHolder.getMinecraft().getTextureManager().getTexture(key).getTextureView();
            Iterable iterable = (Iterable)value;
            Map<Box, List> linkedHashMap = new LinkedHashMap();

            for (Object object : iterable) {
               GlyphMatrix glyphMatrix = (GlyphMatrix)object;
               Box box = glyphMatrix.getBox();
               List object2 = linkedHashMap.get(box);
               List object3;
               if (object2 == null) {
                  List arrayList = new ArrayList();
                  linkedHashMap.put(box, arrayList);
                  object3 = arrayList;
               } else {
                  object3 = object2;
               }

               List<Object> list = (List)object3;
               list.add(object);
            }

            for (Map.Entry<Box, List> entrySetItem : linkedHashMap.entrySet()) {
               Box key2 = (Box)entrySetItem.getKey();
               List value2 = (List)entrySetItem.getValue();
               MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, value2.size(), false, 4, null);

               for (GlyphMatrix glyphMatrix2 : (Iterable<GlyphMatrix>)value2) {
                  Matrix3x2f matrix = glyphMatrix2.getMatrix();
                  float amount = glyphMatrix2.getAmount();
                  float amount2 = glyphMatrix2.getAmount2();
                  float value3 = glyphMatrix2.getValue();
                  float value22 = glyphMatrix2.getValue2();
                  float value5 = (float)glyphMatrix2.getValue5() / glyphMatrix2.getValue3();
                  float value6 = (float)glyphMatrix2.getValue6() / glyphMatrix2.getValue4();
                  float value52 = (float)(glyphMatrix2.getValue5() + glyphMatrix2.getValue()) / glyphMatrix2.getValue3();
                  float value62 = (float)(glyphMatrix2.getValue6() + glyphMatrix2.getValue2()) / glyphMatrix2.getValue4();
                  meshBuilder.computeMeshBuilder(matrix, amount, amount2 + value22, glyphMatrix2.getAmount7())
                     .computeMeshBuilder(value5, value62)
                     .computeMeshBuilder(glyphMatrix2.getAmount3(), glyphMatrix2.getAmount4(), glyphMatrix2.getAmount5(), glyphMatrix2.getAmount6());
                  meshBuilder.computeMeshBuilder(matrix, amount + value3, amount2 + value22, glyphMatrix2.getAmount7())
                     .computeMeshBuilder(value52, value62)
                     .computeMeshBuilder(glyphMatrix2.getAmount3(), glyphMatrix2.getAmount4(), glyphMatrix2.getAmount5(), glyphMatrix2.getAmount6());
                  meshBuilder.computeMeshBuilder(matrix, amount + value3, amount2, glyphMatrix2.getAmount7())
                     .computeMeshBuilder(value52, value6)
                     .computeMeshBuilder(glyphMatrix2.getAmount3(), glyphMatrix2.getAmount4(), glyphMatrix2.getAmount5(), glyphMatrix2.getAmount6());
                  meshBuilder.computeMeshBuilder(matrix, amount, amount2, glyphMatrix2.getAmount7())
                     .computeMeshBuilder(value5, value6)
                     .computeMeshBuilder(glyphMatrix2.getAmount3(), glyphMatrix2.getAmount4(), glyphMatrix2.getAmount5(), glyphMatrix2.getAmount6());
               }

               MeshBuilder.defaultImpl(
                  meshBuilder,
                  Pipelines.INSTANCE.getRenderPipeline14(),
                  MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget(),
                  null,
                  null,
                  new Matrix3x2f(),
                  MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)),
                  key2,
                  12,
                  null
               );
            }
         }

         batchedDraws.clear();
      }
   }

   public final float computeAmount2(String text, Char2ObjectArrayMap allGlyphs, ObjectArrayList maps, Font font, Font fallBack, float amount) {
      String text2 = NameProtect.INSTANCE.computeText(text);
      char[] charArray = this.computeText(text2).toCharArray();
      float value = 0.0F;
      float value2 = 0.0F;
      int value3 = 0;

      for (int i = charArray.length; value3 < i; value3++) {
         char ch = charArray[value3];
         if (ch == '\n') {
            value2 = Math.max(value, value2);
            value = 0.0F;
         } else {
            IpcUser ipcUser = this.computeIpcUser(ch, allGlyphs, maps, font, fallBack);
            value += ipcUser == null ? 0.0F : ipcUser.getValue3() / amount;
         }
      }

      return Math.max(value, value2);
   }

   private final int computeValue(char symbol) {
      return (48 <= symbol ? symbol < 58 : false)
         ? symbol - 48
         : ((97 <= symbol ? symbol < 103 : false) ? symbol - 97 + 10 : ((65 <= symbol ? symbol < 71 : false) ? symbol - 65 + 10 : -1));
   }

   private final boolean testSymbols(char[] symbols, int value) {
      if (value + 13 >= symbols.length) {
         return false;
      } else if (symbols[value] != 167) {
         return false;
      } else {
         char symbol = symbols[value + 1];
         if (symbol != 'x' && symbol != 'X') {
            return false;
         } else {
            int value2 = 0;
            value2 = value + 2;
            byte byteValue = 6;

            for (int i = 0; i < byteValue; i++) {
               if (symbols[value2] != 167) {
                  return false;
               }

               if (INSTANCE.computeValue(symbols[value2 + 1]) < 0) {
                  return false;
               }

               value2 += 2;
            }

            return true;
         }
      }
   }

   private final Triple computeTriple(char[] symbols, int value) {
      if (value - 1 >= 0 && symbols[value - 1] == 167) {
         if (value + 12 >= symbols.length) {
            return null;
         } else {
            int value2 = 0;
            int value3 = 0;
            value3 = value + 1;
            int value4 = 6;

            for (int i = 0; i < value4; i++) {
               if (symbols[value3] != 167) {
                  return null;
               }

               int value5 = INSTANCE.computeValue(symbols[value3 + 1]);
               if (value5 < 0) {
                  return null;
               }

               value2 = value2 << 4 | value5;
               value3 += 2;
            }

            value4 = value2 >> 16 & 0xFF;
            int value6 = value2 >> 8 & 0xFF;
            int value7 = value2 & 0xFF;
            return new Triple(value4, value6, value7);
         }
      } else {
         return null;
      }
   }

   static {
      char2IntArrayMap.put('0', 0);
      char2IntArrayMap.put('1', 170);
      char2IntArrayMap.put('2', 43520);
      char2IntArrayMap.put('3', 43690);
      char2IntArrayMap.put('4', 11141120);
      char2IntArrayMap.put('5', 11141290);
      char2IntArrayMap.put('6', 16755200);
      char2IntArrayMap.put('7', 11184810);
      char2IntArrayMap.put('8', 5592405);
      char2IntArrayMap.put('9', 5592575);
      char2IntArrayMap.put('A', 5635925);
      char2IntArrayMap.put('a', 5635925);
      char2IntArrayMap.put('B', 5636095);
      char2IntArrayMap.put('b', 5636095);
      char2IntArrayMap.put('C', 16733525);
      char2IntArrayMap.put('c', 16733525);
      char2IntArrayMap.put('D', 16733695);
      char2IntArrayMap.put('d', 16733695);
      char2IntArrayMap.put('E', 16777045);
      char2IntArrayMap.put('e', 16777045);
      char2IntArrayMap.put('F', 16777215);
      char2IntArrayMap.put('f', 16777215);
   }
}
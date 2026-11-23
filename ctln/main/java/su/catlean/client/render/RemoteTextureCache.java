// deobf by pusia
package su.catlean.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.random.Random;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Identifiers;

public final class RemoteTextureCache {
   @NotNull
   public static final RemoteTextureCache INSTANCE = new RemoteTextureCache();
   @NotNull
   private static final ConcurrentHashMap<String, Identifier> map = new ConcurrentHashMap();
   @NotNull
   private static final ConcurrentHashMap map2 = new ConcurrentHashMap();
   @NotNull
   private static final Identifier catlean = Identifiers.computeIdentifier("catlean", "textures/ui/windows/config.png");
   private static int value;

   private RemoteTextureCache() {
   }

   @NotNull
   public final Identifier computeIdentifier(String url) {
      Identifier url2 = (Identifier)map.get(url);
      if (url2 != null) {
         return url2;
      } else {
         CompletableFuture ifAbsent = (CompletableFuture)map2.computeIfAbsent(
            url,
            arg -> {
               CompletableFuture completableFuture = new CompletableFuture();
               new Thread(
                     () -> {
                        try {
                           Identifier identifier = Identifiers.computeIdentifier("catlean-img-" + (int)(Random.Default.nextFloat() * 100000));
                           InputStream openStream = URI.create(url).toURL().openStream();
                           NativeImage read = NativeImage.read(openStream);
                           value++;
                           int value2 = value;
                           MinecraftHolder.getMinecraft()
                              .execute(
                                 () -> {
                                    MinecraftHolder.getMinecraft()
                                       .getTextureManager()
                                       .register(identifier, (AbstractTexture)(new DynamicTexture(() -> "ctl_dyn_texture_" + value2, read)));
                                    map.put(url, identifier);
                                    completableFuture.complete(identifier);
                                    map2.remove(url);
                                 }
                              );
                        } catch (Exception exception) {
                           completableFuture.complete(catlean);
                           map2.remove(url);
                        }
                     }
                  )
                  .start();
               return completableFuture;
            }
         );
         return catlean;
      }
   }

   @NotNull
   public final BufferedImage fetchHeight(BufferedImage image) {
      int width = image.getWidth();
      int height = image.getHeight();
      int value2 = Math.min(width, height);
      int value3 = (width - value2) / 2;
      int value4 = (height - value2) / 2;
      return image.getSubimage(value3, value4, value2, value2);
   }
}
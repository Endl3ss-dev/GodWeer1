package releon.ru.api.MainMenu;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import releon.ru.utils.render.ui.GuiResourceLoader;
import releon.ru.utils.render.ui.Render2D;

public final class GifRenderer implements AutoCloseable {
   private static final AtomicInteger NEXT_ID = new AtomicInteger();
   private final Identifier resource;
   private final float fps;
   private volatile boolean loading;
   private volatile boolean failed;
   private volatile String failReason = "";
   private Identifier textureId;
   private NativeImageBackedTexture texture;
   private NativeImage pixels;
   private int[][] frames;
   private int width;
   private int height;
   private int uploadedFrame = -1;
   private long startedAtNanos;

   public GifRenderer(Identifier resource, float fps) {
      if (resource == null) {
         throw new IllegalArgumentException("GIF resource cannot be null");
      } else {
         this.resource = resource;
         this.fps = Math.max(1.0F, fps);
      }
   }

   public synchronized void ensureLoaded() {
      if (!this.loading && !this.failed && this.frames == null) {
         this.loading = true;
         CompletableFuture.<GifRenderer.DecodedGif>supplyAsync(() -> decode(this.resource)).whenComplete((decoded, error) -> {
            if (error != null) {
               this.fail(error);
            } else {
               MinecraftClient.getInstance().execute(() -> this.install(decoded));
            }
         });
      }
   }

   public void renderCover(float x, float y, float targetWidth, float targetHeight, int color) {
      this.ensureLoaded();
      if (this.texture != null && this.frames != null && this.frames.length != 0 && !(targetWidth <= 0.0F) && !(targetHeight <= 0.0F)) {
         int frame = (int)((float)(System.nanoTime() - this.startedAtNanos) * this.fps / 1.0E9F) % this.frames.length;
         this.uploadFrame(frame);
         float sourceAspect = (float)this.width / (float)this.height;
         float targetAspect = targetWidth / targetHeight;
         float u0 = 0.0F;
         float v0 = 0.0F;
         float u1 = 1.0F;
         float v1 = 1.0F;
         if (sourceAspect > targetAspect) {
            float visible = targetAspect / sourceAspect;
            u0 = (1.0F - visible) * 0.5F;
            u1 = 1.0F - u0;
         } else if (sourceAspect < targetAspect) {
            float visible = sourceAspect / targetAspect;
            v0 = (1.0F - visible) * 0.5F;
            v1 = 1.0F - v0;
         }

         Render2D.imageUv(this.textureId.toString(), x, y, targetWidth, targetHeight, 0.0F, u0, v0, u1, v1, color);
      }
   }

   public boolean isLoading() {
      return this.loading;
   }

   public boolean isFailed() {
      return this.failed;
   }

   public String failReason() {
      return this.failReason;
   }

   private void install(GifRenderer.DecodedGif decoded) {
      try {
         this.width = decoded.width();
         this.height = decoded.height();
         this.frames = decoded.frames();
         this.pixels = new NativeImage(this.width, this.height, true);
         this.copyFrame(this.frames[0]);
         this.texture = new NativeImageBackedTexture(() -> "RELEON_gif_" + this.resource, this.pixels);
         this.texture.upload();
         this.textureId = Identifier.of("releon", "dynamic/gif_" + NEXT_ID.getAndIncrement());
         MinecraftClient.getInstance().getTextureManager().registerTexture(this.textureId, this.texture);
         this.uploadedFrame = 0;
         this.startedAtNanos = System.nanoTime();
         this.loading = false;
      } catch (Throwable var3) {
         this.closeTexture();
         this.frames = null;
         this.fail(var3);
      }
   }

   private void uploadFrame(int frame) {
      if (frame != this.uploadedFrame && this.texture != null && this.pixels != null) {
         this.copyFrame(this.frames[frame]);
         this.texture.upload();
         this.uploadedFrame = frame;
      }
   }

   private void copyFrame(int[] argb) {
      for (int y = 0; y < this.height; y++) {
         int row = y * this.width;

         for (int x = 0; x < this.width; x++) {
            this.pixels.setColor(x, y, argbToAbgr(argb[row + x]));
         }
      }
   }

   private static GifRenderer.DecodedGif decode(Identifier resource) {
      try {
         GifRenderer.DecodedGif var41;
         try (InputStream input = GuiResourceLoader.open(resource)) {
            if (input == null) {
               throw new IllegalArgumentException("resource not found: " + resource);
            }

            try (ImageInputStream imageInput = ImageIO.createImageInputStream(input)) {
               Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
               if (!readers.hasNext()) {
                  throw new IllegalStateException("GIF decoder is unavailable");
               }

               ImageReader reader = readers.next();

               try {
                  reader.setInput(imageInput, false, false);
                  int count = reader.getNumImages(true);
                  if (count == 0) {
                     throw new IllegalArgumentException("GIF contains no frames");
                  }

                  int canvasWidth = reader.getWidth(0);
                  int canvasHeight = reader.getHeight(0);
                  int[] logicalSize = logicalScreenSize(reader.getStreamMetadata());
                  if (logicalSize[0] > 0 && logicalSize[1] > 0) {
                     canvasWidth = logicalSize[0];
                     canvasHeight = logicalSize[1];
                  }

                  BufferedImage canvas = new BufferedImage(canvasWidth, canvasHeight, 2);
                  BufferedImage restore = null;
                  GifRenderer.FrameInfo previous = null;
                  List<int[]> decodedFrames = new ArrayList<>(count);

                  for (int index = 0; index < count; index++) {
                     if (previous != null) {
                        if ("restoreToBackgroundColor".equals(previous.disposal())) {
                           clear(canvas, previous.x(), previous.y(), previous.width(), previous.height());
                        } else if ("restoreToPrevious".equals(previous.disposal()) && restore != null) {
                           canvas = copyOf(restore);
                        }
                     }

                     IIOMetadata metadata = reader.getImageMetadata(index);
                     GifRenderer.FrameInfo info = frameInfo(metadata);
                     if ("restoreToPrevious".equals(info.disposal())) {
                        restore = copyOf(canvas);
                     } else {
                        restore = null;
                     }

                     BufferedImage frame = reader.read(index);
                     Graphics2D graphics = canvas.createGraphics();

                     try {
                        graphics.setComposite(AlphaComposite.SrcOver);
                        graphics.drawImage(frame, info.x(), info.y(), null);
                     } finally {
                        graphics.dispose();
                     }

                     decodedFrames.add(canvas.getRGB(0, 0, canvasWidth, canvasHeight, null, 0, canvasWidth));
                     previous = info.withSize(frame.getWidth(), frame.getHeight());
                  }

                  var41 = new GifRenderer.DecodedGif(canvasWidth, canvasHeight, decodedFrames.toArray(int[][]::new));
               } finally {
                  reader.dispose();
               }
            }
         }

         return var41;
      } catch (Exception var40) {
         throw new IllegalStateException("cannot decode " + resource + ": " + var40.getMessage(), var40);
      }
   }

   private static int[] logicalScreenSize(IIOMetadata metadata) {
      if (metadata == null) {
         return new int[]{0, 0};
      } else {
         Node root = metadata.getAsTree("javax_imageio_gif_stream_1.0");
         Node descriptor = child(root, "LogicalScreenDescriptor");
         return new int[]{attributeInt(descriptor, "logicalScreenWidth", 0), attributeInt(descriptor, "logicalScreenHeight", 0)};
      }
   }

   private static GifRenderer.FrameInfo frameInfo(IIOMetadata metadata) {
      Node root = metadata.getAsTree("javax_imageio_gif_image_1.0");
      Node descriptor = child(root, "ImageDescriptor");
      Node control = child(root, "GraphicControlExtension");
      return new GifRenderer.FrameInfo(
         attributeInt(descriptor, "imageLeftPosition", 0),
         attributeInt(descriptor, "imageTopPosition", 0),
         attributeInt(descriptor, "imageWidth", 0),
         attributeInt(descriptor, "imageHeight", 0),
         attribute(control, "disposalMethod", "none")
      );
   }

   private static Node child(Node parent, String name) {
      if (parent == null) {
         return null;
      } else {
         for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (name.equals(node.getNodeName())) {
               return node;
            }
         }

         return null;
      }
   }

   private static int attributeInt(Node node, String name, int fallback) {
      try {
         return Integer.parseInt(attribute(node, name, Integer.toString(fallback)));
      } catch (NumberFormatException var4) {
         return fallback;
      }
   }

   private static String attribute(Node node, String name, String fallback) {
      if (node == null) {
         return fallback;
      } else {
         NamedNodeMap attributes = node.getAttributes();
         Node attribute = attributes == null ? null : attributes.getNamedItem(name);
         return attribute == null ? fallback : attribute.getNodeValue();
      }
   }

   private static void clear(BufferedImage image, int x, int y, int width, int height) {
      Graphics2D graphics = image.createGraphics();

      try {
         graphics.setComposite(AlphaComposite.Clear);
         graphics.fillRect(x, y, width, height);
      } finally {
         graphics.dispose();
      }
   }

   private static BufferedImage copyOf(BufferedImage source) {
      BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), 2);
      Graphics2D graphics = copy.createGraphics();

      try {
         graphics.setComposite(AlphaComposite.Src);
         graphics.drawImage(source, 0, 0, null);
      } finally {
         graphics.dispose();
      }

      return copy;
   }

   private static int argbToAbgr(int argb) {
      int alpha = argb >>> 24 & 0xFF;
      int red = argb >>> 16 & 0xFF;
      int green = argb >>> 8 & 0xFF;
      int blue = argb & 0xFF;
      return alpha << 24 | blue << 16 | green << 8 | red;
   }

   private void fail(Throwable error) {
      String message = error.getMessage();
      if (message == null || message.isBlank()) {
         message = error.getClass().getSimpleName();
      }

      this.failReason = message.length() > 120 ? message.substring(0, 120) : message;
      this.failed = true;
      this.loading = false;
   }

   @Override
   public void close() {
      MinecraftClient minecraft = MinecraftClient.getInstance();
      if (this.textureId != null && minecraft.getTextureManager() != null) {
         minecraft.getTextureManager().destroyTexture(this.textureId);
      } else {
         this.closeTexture();
      }

      this.textureId = null;
      this.texture = null;
      this.pixels = null;
      this.frames = null;
   }

   private void closeTexture() {
      if (this.texture != null) {
         this.texture.close();
      } else if (this.pixels != null) {
         this.pixels.close();
      }
   }

   private static record DecodedGif(int width, int height, int[][] frames) {
   }

   private static record FrameInfo(int x, int y, int width, int height, String disposal) {
      GifRenderer.FrameInfo withSize(int fallbackWidth, int fallbackHeight) {
         return new GifRenderer.FrameInfo(
            this.x, this.y, this.width > 0 ? this.width : fallbackWidth, this.height > 0 ? this.height : fallbackHeight, this.disposal
         );
      }
   }
}

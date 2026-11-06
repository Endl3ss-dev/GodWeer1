/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1011
 *  net.minecraft.class_1011$class_1012
 *  net.minecraft.class_1043
 *  net.minecraft.class_1044
 *  net.minecraft.class_10799
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 */
package ru.decide.utils.render;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_10799;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import org.w3c.dom.Node;

@Environment(value=EnvType.CLIENT)
public class GifTexture {
    private static final ExecutorService LOADER = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "gif-loader");
        thread.setDaemon(true);
        return thread;
    });
    private final List<class_2960> frameIds = new ArrayList<class_2960>();
    private final List<Integer> delays = new ArrayList<Integer>();
    private int totalDuration = 0;
    private long startTime = -1L;
    private volatile boolean loaded = false;
    private volatile boolean failed = false;
    private int width = 0;
    private int height = 0;

    public GifTexture(InputStream stream) {
        try {
            byte[] data = stream.readAllBytes();
            LOADER.submit(() -> this.load(new ByteArrayInputStream(data)));
        }
        catch (Exception e) {
            System.err.println("[GifTexture] Failed to queue GIF: " + String.valueOf(e));
        }
    }

    private void load(InputStream stream) {
        try {
            IIOMetadataNode ld;
            ImageInputStream iis = ImageIO.createImageInputStream(stream);
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
            if (!readers.hasNext()) {
                System.err.println("[GifTexture] No GIF ImageReader available");
                return;
            }
            ImageReader reader = readers.next();
            reader.setInput(iis, false);
            int frameCount = reader.getNumImages(true);
            String uid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            IIOMetadataNode globalMeta = null;
            try {
                globalMeta = (IIOMetadataNode)reader.getStreamMetadata().getAsTree(reader.getStreamMetadata().getNativeMetadataFormatName());
            }
            catch (Exception exception) {
                // empty catch block
            }
            BufferedImage firstFrame = reader.read(0);
            int canvasW = firstFrame.getWidth();
            int canvasH = firstFrame.getHeight();
            if (globalMeta != null && (ld = this.findNode(globalMeta, "LogicalScreenDescriptor")) != null) {
                try {
                    canvasW = Integer.parseInt(ld.getAttribute("logicalScreenWidth"));
                }
                catch (Exception exception) {
                    // empty catch block
                }
                try {
                    canvasH = Integer.parseInt(ld.getAttribute("logicalScreenHeight"));
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            this.width = canvasW;
            this.height = canvasH;
            ArrayList<int[]> pixelData = new ArrayList<int[]>();
            BufferedImage canvas = new BufferedImage(canvasW, canvasH, 2);
            BufferedImage prevCanvas = null;
            for (int i = 0; i < frameCount; ++i) {
                BufferedImage rawFrame = i == 0 ? firstFrame : reader.read(i);
                IIOMetadataNode frameMeta = null;
                try {
                    frameMeta = (IIOMetadataNode)reader.getImageMetadata(i).getAsTree(reader.getImageMetadata(i).getNativeMetadataFormatName());
                }
                catch (Exception exception) {
                    // empty catch block
                }
                int fx = 0;
                int fy = 0;
                String disposal = "none";
                if (frameMeta != null) {
                    IIOMetadataNode gce;
                    IIOMetadataNode id = this.findNode(frameMeta, "ImageDescriptor");
                    if (id != null) {
                        try {
                            fx = Integer.parseInt(id.getAttribute("imageLeftPosition"));
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        try {
                            fy = Integer.parseInt(id.getAttribute("imageTopPosition"));
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                    if ((gce = this.findNode(frameMeta, "GraphicControlExtension")) != null) {
                        disposal = gce.getAttribute("disposalMethod");
                    }
                }
                if ("restoreToPrevious".equalsIgnoreCase(disposal)) {
                    prevCanvas = this.copyImage(canvas);
                }
                Graphics2D g = canvas.createGraphics();
                g.drawImage((Image)rawFrame, fx, fy, null);
                g.dispose();
                pixelData.add(canvas.getRGB(0, 0, canvasW, canvasH, null, 0, canvasW));
                this.delays.add(this.readDelay(reader, i));
                this.frameIds.add(class_2960.method_60655((String)"decide", (String)("gif/" + uid + "/" + i)));
                Graphics2D gc = canvas.createGraphics();
                gc.setComposite(AlphaComposite.Clear);
                if ("restoreToBackgroundColor".equalsIgnoreCase(disposal)) {
                    gc.fillRect(fx, fy, rawFrame.getWidth(), rawFrame.getHeight());
                } else if ("restoreToPrevious".equalsIgnoreCase(disposal) && prevCanvas != null) {
                    gc.setComposite(AlphaComposite.Src);
                    gc.drawImage((Image)prevCanvas, 0, 0, null);
                }
                gc.dispose();
            }
            reader.dispose();
            for (int d : this.delays) {
                this.totalDuration += d;
            }
            int uploadW = canvasW;
            int uploadH = canvasH;
            class_310.method_1551().execute(() -> {
                for (int i = 0; i < pixelData.size(); ++i) {
                    int[] px = (int[])pixelData.get(i);
                    class_1011 img = new class_1011(class_1011.class_1012.field_4997, uploadW, uploadH, false);
                    int visiblePixels = 0;
                    for (int y = 0; y < uploadH; ++y) {
                        for (int x = 0; x < uploadW; ++x) {
                            int argb = px[y * uploadW + x];
                            if (argb >>> 24 != 0 && (argb & 0xFFFFFF) != 0) {
                                ++visiblePixels;
                            }
                            img.method_61941(x, y, argb);
                        }
                    }
                    int idx = i;
                    class_1043 tex = new class_1043(() -> "white:gif_" + uid + "_" + idx, img);
                    tex.method_4524();
                    class_310.method_1551().method_1531().method_4616(this.frameIds.get(idx), (class_1044)tex);
                    if (idx != 0) continue;
                    System.err.println("[GifTexture] GIF READY size=" + uploadW + "x" + uploadH + " frames=" + pixelData.size() + " visiblePixels=" + visiblePixels);
                }
                this.loaded = true;
            });
        }
        catch (Exception e) {
            this.failed = true;
            System.err.println("[GifTexture] GIF LOAD FAILED: " + String.valueOf(e));
            e.printStackTrace();
        }
    }

    private int readDelay(ImageReader reader, int index) {
        try {
            IIOMetadataNode root = (IIOMetadataNode)reader.getImageMetadata(index).getAsTree(reader.getImageMetadata(index).getNativeMetadataFormatName());
            IIOMetadataNode gce = this.findNode(root, "GraphicControlExtension");
            if (gce != null) {
                int cs = Integer.parseInt(gce.getAttribute("delayTime"));
                return Math.max(cs * 10, 20);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return 100;
    }

    private IIOMetadataNode findNode(IIOMetadataNode root, String name) {
        for (int i = 0; i < root.getLength(); ++i) {
            IIOMetadataNode child;
            IIOMetadataNode found;
            if (root.item(i).getNodeName().equalsIgnoreCase(name)) {
                return (IIOMetadataNode)root.item(i);
            }
            Node node = root.item(i);
            if (!(node instanceof IIOMetadataNode) || (found = this.findNode(child = (IIOMetadataNode)node, name)) == null) continue;
            return found;
        }
        return null;
    }

    private BufferedImage copyImage(BufferedImage src) {
        BufferedImage copy = new BufferedImage(src.getWidth(), src.getHeight(), 2);
        Graphics2D g = copy.createGraphics();
        g.drawImage((Image)src, 0, 0, null);
        g.dispose();
        return copy;
    }

    public class_2960 currentFrame() {
        if (!this.loaded || this.frameIds.isEmpty()) {
            return null;
        }
        if (this.frameIds.size() == 1) {
            return this.frameIds.get(0);
        }
        if (this.startTime == -1L) {
            this.startTime = System.currentTimeMillis();
        }
        long elapsed = (System.currentTimeMillis() - this.startTime) % (long)this.totalDuration;
        int acc = 0;
        for (int i = 0; i < this.frameIds.size(); ++i) {
            if (elapsed >= (long)(acc += this.delays.get(i).intValue())) continue;
            return this.frameIds.get(i);
        }
        return this.frameIds.get(this.frameIds.size() - 1);
    }

    public void draw(class_332 ctx, int x, int y, int w, int h) {
        this.draw(ctx, x, y, w, h, 1.0f);
    }

    public void draw(class_332 ctx, int x, int y, int w, int h, float alpha) {
        int a = (int)(Math.min(Math.max(alpha, 0.0f), 1.0f) * 255.0f) & 0xFF;
        this.draw(ctx, x, y, w, h, a << 24 | 0xFFFFFF);
    }

    public void draw(class_332 ctx, int x, int y, int w, int h, int color) {
        class_2960 frame = this.currentFrame();
        if (frame == null) {
            return;
        }
        ctx.method_25291(class_10799.field_56883, frame, x, y, 0.0f, 0.0f, w, h, w, h, color);
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public boolean isFailed() {
        return this.failed;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void destroy() {
        class_310 mc = class_310.method_1551();
        ArrayList<class_2960> copy = new ArrayList<class_2960>(this.frameIds);
        mc.execute(() -> copy.forEach(id -> mc.method_1531().method_4615(id)));
        this.frameIds.clear();
        this.delays.clear();
        this.loaded = false;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.textures.TextureFormat
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1011
 *  net.minecraft.class_1011$class_1012
 *  net.minecraft.class_1044
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3298
 *  net.minecraft.class_3300
 */
package ru.decide.utils.render.gif;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_3300;
import org.w3c.dom.Node;
import ru.decide.utils.render.Draw;

@Environment(value=EnvType.CLIENT)
public final class GifRenderer {
    private static final Map<String, GifAnimation> CACHE = new HashMap<String, GifAnimation>();
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);
    private static final ExecutorService LOADER = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "decidevisuals-gif-loader");
        t.setDaemon(true);
        return t;
    });

    private GifRenderer() {
    }

    public static void shutdown() {
        LOADER.shutdownNow();
        for (GifAnimation a : CACHE.values()) {
            a.dispose();
        }
        CACHE.clear();
    }

    private static int parseInt(String s, int def) {
        if (s == null) {
            return def;
        }
        try {
            return Integer.parseInt(s);
        }
        catch (Exception e) {
            return def;
        }
    }

    private static IIOMetadataNode findNode(IIOMetadataNode node, String name) {
        int i;
        for (i = 0; i < node.getLength(); ++i) {
            if (!node.item(i).getNodeName().equalsIgnoreCase(name)) continue;
            return (IIOMetadataNode)node.item(i);
        }
        for (i = 0; i < node.getLength(); ++i) {
            IIOMetadataNode child;
            IIOMetadataNode found;
            Node n = node.item(i);
            if (!(n instanceof IIOMetadataNode) || (found = GifRenderer.findNode(child = (IIOMetadataNode)n, name)) == null) continue;
            return found;
        }
        return null;
    }

    private static BufferedImage copyImage(BufferedImage src) {
        BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), 2);
        Graphics2D g = dst.createGraphics();
        g.drawImage((Image)src, 0, 0, null);
        g.dispose();
        return dst;
    }

    public static void preload(String path) {
        CACHE.computeIfAbsent(path, k -> {
            GifAnimation a = new GifAnimation();
            a.startLoad((String)k);
            return a;
        });
    }

    public static void draw(float x, float y, float w, float h, float radius, String path, float alpha) {
        String frameId;
        GifAnimation anim = CACHE.computeIfAbsent(path, k -> {
            GifAnimation a = new GifAnimation();
            a.startLoad((String)k);
            return a;
        });
        if (anim.state == State.GPU_READY && (frameId = anim.getCurrentFrameId()) != null) {
            int a = Math.max(0, Math.min(255, (int)(alpha * 255.0f)));
            class_2960 id = class_2960.method_12829((String)frameId);
            if (id != null) {
                Draw.texture(id, x, y, w, h, a << 24 | 0xFFFFFF);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class GifAnimation {
        volatile State state = State.IDLE;
        final List<BufferedImage> rawFrames = new ArrayList<BufferedImage>();
        final List<Integer> rawDelays = new ArrayList<Integer>();
        final List<String> frames = new ArrayList<String>();
        final List<Integer> frameDelays = new ArrayList<Integer>();
        final List<GpuTexture> gpuTextures = new ArrayList<GpuTexture>();
        final List<GpuTextureView> gpuViews = new ArrayList<GpuTextureView>();
        int totalDuration = 0;

        void startLoad(String path) {
            this.state = State.LOADING;
            LOADER.submit(() -> {
                try {
                    this.decodeGif(path);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    this.state = State.IDLE;
                }
            });
        }

        private void decodeGif(String path) throws Exception {
            String resourcePath;
            String namespace;
            if (path.contains(":")) {
                String[] split = path.split(":", 2);
                namespace = split[0];
                resourcePath = split[1];
            } else {
                namespace = "client";
                resourcePath = path;
            }
            Optional resource = class_310.method_1551().method_1478().method_14486(class_2960.method_60655((String)namespace, (String)resourcePath));
            if (resource.isEmpty()) {
                System.err.println("[GifRenderer] Missing GIF resource: " + namespace + ":" + resourcePath);
                this.state = State.IDLE;
                return;
            }
            InputStream is = ((class_3298)resource.get()).method_14482();
            ImageInputStream iis = ImageIO.createImageInputStream(is);
            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            reader.setInput(iis, false, false);
            int w = reader.getWidth(0);
            int h = reader.getHeight(0);
            int count = reader.getNumImages(true);
            BufferedImage canvas = new BufferedImage(w, h, 2);
            Graphics2D g = canvas.createGraphics();
            g.setBackground(new Color(0, 0, 0, 0));
            BufferedImage prev = null;
            for (int i = 0; i < count; ++i) {
                int top;
                IIOMetadata meta = reader.getImageMetadata(i);
                IIOMetadataNode root = (IIOMetadataNode)meta.getAsTree(meta.getNativeMetadataFormatName());
                IIOMetadataNode gce = GifRenderer.findNode(root, "GraphicControlExtension");
                IIOMetadataNode desc = GifRenderer.findNode(root, "ImageDescriptor");
                String disposal = gce != null ? gce.getAttribute("disposalMethod") : "none";
                int delay = 100;
                if (gce != null) {
                    try {
                        delay = Integer.parseInt(gce.getAttribute("delayTime")) * 10;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                if (delay <= 0) {
                    delay = 100;
                }
                int left = desc != null ? GifRenderer.parseInt(desc.getAttribute("imageLeftPosition"), 0) : 0;
                int n = top = desc != null ? GifRenderer.parseInt(desc.getAttribute("imageTopPosition"), 0) : 0;
                if ("restoreToPrevious".equals(disposal)) {
                    prev = GifRenderer.copyImage(canvas);
                }
                BufferedImage frame = reader.read(i);
                g.setComposite(AlphaComposite.SrcOver);
                g.drawImage((Image)frame, left, top, null);
                this.rawFrames.add(GifRenderer.copyImage(canvas));
                this.rawDelays.add(delay);
                if ("restoreToBackgroundColor".equals(disposal)) {
                    g.setComposite(AlphaComposite.Clear);
                    g.fillRect(left, top, frame.getWidth(), frame.getHeight());
                    continue;
                }
                if (!"restoreToPrevious".equals(disposal) || prev == null) continue;
                g.setComposite(AlphaComposite.Src);
                g.drawImage((Image)prev, 0, 0, null);
            }
            g.dispose();
            reader.dispose();
            iis.close();
            is.close();
            this.state = State.FRAMES_READY;
            this.scheduleGpuUploads();
        }

        void dispose() {
            class_310 mc = class_310.method_1551();
            if (mc != null) {
                for (String f : this.frames) {
                    try {
                        class_2960 id = class_2960.method_12829((String)f);
                        if (id == null) continue;
                        mc.method_1531().method_4615(id);
                    }
                    catch (Exception exception) {}
                }
            }
            this.gpuViews.forEach(GpuTextureView::close);
            this.gpuTextures.forEach(GpuTexture::close);
            this.frames.clear();
            this.frameDelays.clear();
            this.gpuTextures.clear();
            this.gpuViews.clear();
            this.rawFrames.clear();
            this.rawDelays.clear();
        }

        private void uploadSingleFrame(int index) {
            try {
                if (index >= this.rawFrames.size()) {
                    return;
                }
                BufferedImage img = this.rawFrames.get(index);
                int w = img.getWidth();
                int h = img.getHeight();
                class_1011 ni = new class_1011(class_1011.class_1012.field_4997, w, h, false);
                for (int y = 0; y < h; ++y) {
                    for (int x = 0; x < w; ++x) {
                        ni.method_61941(x, y, img.getRGB(x, y));
                    }
                }
                int frameId = ID_COUNTER.getAndIncrement();
                GpuTexture tex = RenderSystem.getDevice().createTexture(() -> "decidevisuals_gif_" + frameId, 5, TextureFormat.RGBA8, w, h, 1, 1);
                RenderSystem.getDevice().createCommandEncoder().writeToTexture(tex, ni);
                GpuTextureView view = RenderSystem.getDevice().createTextureView(tex);
                ni.close();
                class_2960 id = class_2960.method_60655((String)"decide", (String)("gif/frame_" + frameId));
                class_310.method_1551().method_1531().method_4616(id, (class_1044)new GifFrameTexture(tex, view, w, h));
                this.frames.add(id.toString());
                this.frameDelays.add(this.rawDelays.get(index));
                this.gpuTextures.add(tex);
                this.gpuViews.add(view);
                this.totalDuration += this.rawDelays.get(index).intValue();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void scheduleGpuUploads() {
            class_310 mc = class_310.method_1551();
            if (mc == null) {
                return;
            }
            int frameCount = this.rawFrames.size();
            int i = 0;
            while (i < frameCount) {
                int idx = i++;
                mc.execute(() -> this.uploadSingleFrame(idx));
            }
            mc.execute(() -> {
                this.rawFrames.clear();
                this.state = State.GPU_READY;
            });
        }

        String getCurrentFrameId() {
            if (this.frames.isEmpty()) {
                return null;
            }
            if (this.totalDuration == 0) {
                return this.frames.get(0);
            }
            long now = System.currentTimeMillis() % (long)this.totalDuration;
            long acc = 0L;
            for (int i = 0; i < this.frames.size(); ++i) {
                if (now >= (acc += (long)this.frameDelays.get(i).intValue())) continue;
                return this.frames.get(i);
            }
            return this.frames.get(this.frames.size() - 1);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum State {
        IDLE,
        LOADING,
        FRAMES_READY,
        GPU_READY;

    }

    @Environment(value=EnvType.CLIENT)
    public static final class GifFrameTexture
    extends class_1044 {
        private final GpuTexture gpuTexture;
        private final GpuTextureView gpuTextureView;

        public GifFrameTexture(GpuTexture tex, GpuTextureView view, int w, int h) {
            this.gpuTexture = tex;
            this.gpuTextureView = view;
        }

        public GpuTexture method_68004() {
            return this.gpuTexture;
        }

        public void load(class_3300 rm) {
        }

        public GpuTextureView getTextureView() {
            return this.gpuTextureView;
        }

        public GpuTexture getTexture() {
            return this.gpuTexture;
        }
    }
}


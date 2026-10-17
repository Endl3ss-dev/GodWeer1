package swerve.cosmetics.cape;

import com.mojang.blaze3d.platform.NativeImage;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

/**
 * Плащи: анимированные полотна (по кадрам в одной картинке) и их кадры для мира.
 *
 * <p>Кадр для мира — это текстура 1024 x 512: лицо плаща спереди и сзади,
 * тонкие бока и верх/низ в цвете соседней стороны.
 */
public final class Capes {

    private static final String[] NAMES = {
        "Arcanic", "Arcanic Neon", "Leopard", "Potion", "Diamond Sword", "Glitch", "Sweatgod"
    };

    private static final String[] FILES = {
        "arcanic", "neon", "leopard", "potion", "diamond_sword", "glitch", "sweatgod"
    };

    /** Сколько кадров в рисунке. */
    private static final int[] FRAMES = {16, 12, 1, 1, 11, 14, 5};

    /** Сколько миллисекунд длится кадр. */
    private static final int[] TIMES = {140, 110, 1000, 1000, 150, 50, 200};

    /** Текстуры кадров мира по рисункам. */
    private static final Map<String, ResourceLocation[]> WORLD = new ConcurrentHashMap<>();

    /** Текстура-превью (первый кадр) по рисункам. */
    private static final Map<String, ResourceLocation> PREVIEW = new ConcurrentHashMap<>();

    private Capes() {
    }

    public static String[] designs() {
        return NAMES.clone();
    }

    public static int count() {
        return NAMES.length;
    }

    public static int indexOf(String name) {
        if (name == null) {
            return 0;
        }
        for (int i = 0; i < NAMES.length; i++) {
            if (NAMES[i].equals(name)) {
                return i + 1;
            }
        }
        return 0;
    }

    public static String nameOf(int index) {
        if (index <= 0 || index > NAMES.length) {
            return null;
        }
        return NAMES[index - 1];
    }

    /** Есть ли плащ у игрока (даже если текстура кадра ещё не загружена). */
    public static boolean hasCape(AbstractClientPlayer player) {
        if (player == null || player.isInvisible()) {
            return false;
        }
        if (player == Minecraft.getInstance().player) {
            return CapeState.design() != null;
        }
        return CapeState.remoteDesign(player.getUUID()) != null;
    }

    /** Текстура текущего кадра плаща игрока (null — плаща нет). */
    public static ResourceLocation resolve(AbstractClientPlayer player) {
        if (player == null || player.isInvisible()) {
            return null;
        }
        String design = player == Minecraft.getInstance().player
                ? CapeState.design()
                : CapeState.remoteDesign(player.getUUID());
        if (design == null) {
            return null;
        }
        return frame(design);
    }

    /** Текущий кадр рисунка: пока не загружена ни одна текстура — null. */
    private static ResourceLocation frame(String design) {
        int index = indexOf(design);
        if (index <= 0) {
            return null;
        }
        ResourceLocation[] ids = WORLD.computeIfAbsent(design, Capes::requestWorld);
        int n = ids.length;
        int i = (int) ((System.currentTimeMillis() / TIMES[index - 1]) % n);
        for (int k = 0; k < n; k++) {
            ResourceLocation id = ids[(i - k + n) % n];
            if (CapeTextures.registered(id)) {
                return id;
            }
        }
        return null;
    }

    /** Текстура-превью рисунка для меню (первый кадр, 160 x 256). */
    public static ResourceLocation preview(String design) {
        int index = indexOf(design);
        if (index <= 0) {
            return null;
        }
        ResourceLocation id = PREVIEW.computeIfAbsent(design, Capes::requestPreview);
        return CapeTextures.registered(id) ? id : null;
    }

    private static ResourceLocation[] requestWorld(String design) {
        int index = indexOf(design);
        String file = FILES[index - 1];
        int count = FRAMES[index - 1];
        ResourceLocation[] out = new ResourceLocation[count];
        NativeImage[] strip = {null}; // разбирается один раз, кадры делят его и освобождают после последнего
        int[] left = {count};
        for (int f = 0; f < count; f++) {
            int frame = f;
            out[f] = ResourceLocation.fromNamespaceAndPath("swerve", "cape/" + file + "_" + f);
            CapeTextures.request(out[f], () -> {
                if (strip[0] == null) {
                    strip[0] = readStrip(file);
                }
                NativeImage image = layout(strip[0], frame);
                if (--left[0] == 0) {
                    strip[0].close();
                    strip[0] = null;
                }
                return image;
            });
        }
        return out;
    }

    private static ResourceLocation requestPreview(String design) {
        int index = indexOf(design);
        String file = FILES[index - 1];
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("swerve", "cape_preview/" + file);
        CapeTextures.request(id, () -> {
            NativeImage strip = readStrip(file);
            try {
                return crop(strip, 0);
            } finally {
                strip.close();
            }
        });
        return id;
    }

    private static NativeImage readStrip(String file) {
        try (InputStream in = Capes.class.getResourceAsStream("/assets/swerve/textures/cape/" + file + ".png")) {
            if (in == null) {
                throw new IllegalStateException("cape texture missing: " + file);
            }
            return NativeImage.read(in);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("cape texture unreadable: " + file, e);
        }
    }

    /** Раскладка кадра мира: лицо плаща, бока и верх/низ. */
    private static NativeImage layout(NativeImage strip, int frame) {
        NativeImage image = new NativeImage(1024, 512, false);
        int oy = frame * 256;
        int edge = strip.getPixel(2, oy + 128) | 0xFF000000;
        image.fillRect(0, 0, 1024, 512, edge);
        for (int y = 0; y < 256; y++) {
            for (int x = 0; x < 160; x++) {
                int c = strip.getPixel(x, oy + y) | 0xFF000000;
                image.setPixel(16 + x, 16 + y, c);
                image.setPixel(192 + (159 - x), 16 + y, c);
            }
        }
        // тонкие бока плаща берут цвет соседней стороны
        for (int y = 0; y < 256; y++) {
            for (int x = 0; x < 16; x++) {
                image.setPixel(x, 16 + y, strip.getPixel(0, oy + y) | 0xFF000000);
                image.setPixel(176 + x, 16 + y, strip.getPixel(159, oy + y) | 0xFF000000);
            }
        }
        for (int x = 0; x < 160; x++) {
            for (int y = 0; y < 16; y++) {
                image.setPixel(16 + x, y, strip.getPixel(x, oy) | 0xFF000000);
                image.setPixel(176 + x, y, strip.getPixel(x, oy + 255) | 0xFF000000);
            }
        }
        return image;
    }

    /** Первый кадр полотна как отдельная картинка 160 x 256 (превью в меню). */
    private static NativeImage crop(NativeImage strip, int frame) {
        NativeImage image = new NativeImage(160, 256, false);
        int oy = frame * 256;
        for (int y = 0; y < 256; y++) {
            for (int x = 0; x < 160; x++) {
                image.setPixel(x, y, strip.getPixel(x, oy + y) | 0xFF000000);
            }
        }
        return image;
    }
}

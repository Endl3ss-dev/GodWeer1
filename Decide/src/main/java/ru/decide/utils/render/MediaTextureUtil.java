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
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 */
package ru.decide.utils.render;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import javax.imageio.ImageIO;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class MediaTextureUtil {
    public static final class_2960 ARTWORK_ID = class_2960.method_60655((String)"decide", (String)"media/artwork");
    private static byte[] lastArtwork = new byte[0];
    private static boolean registered;

    private MediaTextureUtil() {
    }

    public static void updateArtwork(byte[] png) {
        if (png == null) {
            png = new byte[]{};
        }
        if (Arrays.equals(png, lastArtwork)) {
            return;
        }
        lastArtwork = (byte[])png.clone();
        if (png.length == 0) {
            registered = false;
            return;
        }
        byte[] finalPng = png;
        class_310.method_1551().execute(() -> MediaTextureUtil.upload(finalPng));
    }

    private static void upload(byte[] png) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(png));
            if (image == null) {
                registered = false;
                return;
            }
            int w = image.getWidth();
            int h = image.getHeight();
            class_1011 nativeImage = new class_1011(class_1011.class_1012.field_4997, w, h, false);
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    nativeImage.method_61941(x, y, image.getRGB(x, y));
                }
            }
            class_1043 backed = new class_1043(() -> ARTWORK_ID.toString(), nativeImage);
            backed.method_4524();
            class_310.method_1551().method_1531().method_4616(ARTWORK_ID, (class_1044)backed);
            registered = true;
        }
        catch (Exception ignored) {
            registered = false;
        }
    }

    public static boolean hasArtwork() {
        return registered;
    }

    public static void reset() {
        lastArtwork = new byte[0];
        registered = false;
    }
}


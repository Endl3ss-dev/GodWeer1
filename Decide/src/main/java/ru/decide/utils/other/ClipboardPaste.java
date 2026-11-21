/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  org.lwjgl.glfw.GLFW
 */
package ru.decide.utils.other;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public final class ClipboardPaste {
    private ClipboardPaste() {
    }

    public static boolean ctrlDown() {
        class_310 mc = class_310.method_1551();
        if (mc == null || mc.method_22683() == null) {
            return false;
        }
        long handle = mc.method_22683().method_4490();
        return GLFW.glfwGetKey((long)handle, (int)341) == 1 || GLFW.glfwGetKey((long)handle, (int)345) == 1;
    }

    public static boolean pasteCombo(int keyCode) {
        return keyCode == 86 && ClipboardPaste.ctrlDown();
    }

    public static String content(int maxLength) {
        class_310 mc = class_310.method_1551();
        if (mc == null || mc.field_1774 == null) {
            return "";
        }
        String clip = mc.field_1774.method_1460();
        if (clip == null || clip.isEmpty()) {
            return "";
        }
        String clean = clip.replace("\r", " ").replace("\n", " ").trim();
        if (clean.isEmpty()) {
            return "";
        }
        if (maxLength > 0 && clean.length() > maxLength) {
            clean = clean.substring(0, maxLength);
        }
        return clean;
    }

    public static String append(String current, int maxLength) {
        String base;
        String paste = ClipboardPaste.content(maxLength);
        if (paste.isEmpty()) {
            return current;
        }
        String string = base = current == null ? "" : current;
        if (maxLength > 0 && base.length() >= maxLength) {
            return base;
        }
        int room = maxLength > 0 ? maxLength - base.length() : paste.length();
        return base + paste.substring(0, Math.min(room, paste.length()));
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.other;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.utils.other.SoundUtil;

@Environment(value=EnvType.CLIENT)
public class GuiSounds {
    private static final String DIR = "gui/";
    private static long lastTick;
    private static long lastType;

    private static boolean pass(long last, long delay) {
        return System.currentTimeMillis() - last >= delay;
    }

    private static void play(String name, float volume) {
        SoundUtil.playSound_wav(DIR + name, volume);
    }

    private static void play(String name, float volume, float pitch) {
        SoundUtil.playSound_wav(DIR + name, volume, pitch);
    }

    public static void open() {
        GuiSounds.play("gui_open", 0.45f);
    }

    public static void close() {
        GuiSounds.play("gui_close", 0.45f);
    }

    public static void editor() {
        GuiSounds.play("gui_anime", 0.4f);
    }

    public static void category(int index, int total) {
        float t = total <= 1 ? 0.0f : (float)index / (float)(total - 1);
        GuiSounds.play("gui_category", 0.45f, 0.97f + 0.08f * t);
    }

    public static void theme(int index, int total) {
        float t = total <= 1 ? 0.0f : (float)index / (float)(total - 1);
        GuiSounds.play("gui_theme", 0.45f, 0.95f + 0.14f * t);
    }

    public static void expand(boolean opened) {
        GuiSounds.play(opened ? "gui_module_open" : "gui_module_close", 0.45f);
    }

    public static void toggle(boolean value) {
        GuiSounds.play(value ? "gui_boolean_enable" : "gui_boolean_disable", 0.4f);
    }

    public static void chip(int index, int total) {
        float t = total <= 1 ? 0.0f : (float)index / (float)(total - 1);
        GuiSounds.play("gui_mode_multi", 0.45f, 0.95f + 0.15f * t);
    }

    public static void chipMulti(boolean value) {
        GuiSounds.play(value ? "gui_boolean2_enable" : "gui_boolean2_disable", 0.4f);
    }

    public static void button() {
        GuiSounds.play("gui_open_button", 0.4f);
    }

    public static void sliderGrab() {
        GuiSounds.play("gui_click", 0.35f);
    }

    public static void sliderTick(float percent) {
        if (!GuiSounds.pass(lastTick, 22L)) {
            return;
        }
        lastTick = System.currentTimeMillis();
        GuiSounds.play("gui_slider", 0.35f, 0.9f + 0.35f * Math.max(0.0f, Math.min(1.0f, percent)));
    }

    public static void sliderRelease() {
        GuiSounds.play("gui_slider", 0.3f, 1.25f);
    }

    public static void bindStart() {
        GuiSounds.play("gui_binding", 0.45f);
    }

    public static void bindSet() {
        GuiSounds.play("gui_bind", 0.4f);
    }

    public static void bindReset() {
        GuiSounds.play("gui_clear", 0.4f);
    }

    public static void editStart() {
        GuiSounds.play("gui_click", 0.4f);
    }

    public static void editCommit() {
        GuiSounds.play("gui_bind", 0.35f);
    }

    public static void editCancel() {
        GuiSounds.play("gui_clear", 0.35f);
    }

    public static void type() {
        if (!GuiSounds.pass(lastType, 25L)) {
            return;
        }
        lastType = System.currentTimeMillis();
        GuiSounds.play("gui_key_click", 0.35f, 1.0f + (float)(Math.random() * (double)0.12f - (double)0.06f));
    }

    public static void erase() {
        if (!GuiSounds.pass(lastType, 25L)) {
            return;
        }
        lastType = System.currentTimeMillis();
        GuiSounds.play("gui_key_click", 0.3f, 0.88f);
    }

    public static void searchClear() {
        GuiSounds.play("gui_clear", 0.4f);
    }

    public static void picker(boolean opened) {
        GuiSounds.play(opened ? "gui_multi_open" : "gui_multi_close", 0.4f);
    }

    public static void colorTick(float percent) {
        if (!GuiSounds.pass(lastTick, 28L)) {
            return;
        }
        lastTick = System.currentTimeMillis();
        GuiSounds.play("gui_slider", 0.3f, 0.9f + 0.35f * Math.max(0.0f, Math.min(1.0f, percent)));
    }

    public static void scroll() {
        if (!GuiSounds.pass(lastTick, 55L)) {
            return;
        }
        lastTick = System.currentTimeMillis();
        GuiSounds.play("gui_scroll", 0.35f);
    }
}


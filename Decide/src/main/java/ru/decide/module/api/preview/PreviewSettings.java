/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package ru.decide.module.api.preview;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_437;
import ru.decide.module.api.Module;
import ru.decide.module.api.settings.impl.ButtonSetting;
import ru.decide.module.api.settings.impl.DelimiterSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.screen.Menu;
import ru.decide.screen.PreviewEditor;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.other.GuiSounds;

@Environment(value=EnvType.CLIENT)
public final class PreviewSettings {
    public final DelimiterSetting title;
    public final SliderSetting distance;
    public final SliderSetting height;
    public final SliderSetting interval;

    private PreviewSettings(Module parent, Float defaultDistance, Float defaultHeight, Float defaultInterval) {
        boolean anySlider = defaultDistance != null || defaultHeight != null || defaultInterval != null;
        this.title = !anySlider ? null : new DelimiterSetting(parent, "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u043f\u0440\u0435\u0434\u043f\u043e\u043a\u0430\u0437\u0430").setVisible(() -> PreviewSettings.isEditing(parent));
        this.distance = defaultDistance == null ? null : new SliderSetting(parent, "\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0441\u043f\u0430\u0432\u043d\u0430", defaultDistance.floatValue(), 1.5f, 14.0f, 0.5f).setVisible(() -> PreviewSettings.isEditing(parent));
        this.height = defaultHeight == null ? null : new SliderSetting(parent, "\u0412\u044b\u0441\u043e\u0442\u0430 \u0441\u043f\u0430\u0432\u043d\u0430", defaultHeight.floatValue(), -2.0f, 4.0f, 0.1f).setVisible(() -> PreviewSettings.isEditing(parent));
        this.interval = defaultInterval == null ? null : new SliderSetting(parent, "\u0418\u043d\u0442\u0435\u0440\u0432\u0430\u043b \u043f\u043e\u043a\u0430\u0437\u0430", defaultInterval.floatValue(), 0.5f, 10.0f, 0.5f).setVisible(() -> PreviewSettings.isEditing(parent));
    }

    public static PreviewSettings of(Module parent, float distance, float height, float interval) {
        return new PreviewSettings(parent, Float.valueOf(distance), Float.valueOf(height), Float.valueOf(interval));
    }

    public static PreviewSettings withoutInterval(Module parent, float distance, float height) {
        return new PreviewSettings(parent, Float.valueOf(distance), Float.valueOf(height), null);
    }

    public static PreviewSettings intervalOnly(Module parent, float interval) {
        return new PreviewSettings(parent, null, null, Float.valueOf(interval));
    }

    public static PreviewSettings none(Module parent) {
        return new PreviewSettings(parent, null, null, null);
    }

    public boolean repeats() {
        return this.interval != null;
    }

    public long intervalMs() {
        return this.interval == null ? 0L : (long)(this.interval.getValue().floatValue() * 1000.0f);
    }

    public float distance(float fallback) {
        return this.distance == null ? fallback : this.distance.getValue().floatValue();
    }

    public float height(float fallback) {
        return this.height == null ? fallback : this.height.getValue().floatValue();
    }

    private static boolean isEditing(Module parent) {
        return PreviewEditor.getInstance().isEditing(parent);
    }

    public static ButtonSetting button(Module parent) {
        return new ButtonSetting(parent, "\u041f\u0440\u0435\u0434\u043f\u043e\u043a\u0430\u0437", () -> {
            class_437 patt0$temp = IMinecraft.mc.field_1755;
            if (patt0$temp instanceof Menu) {
                Menu menu = (Menu)patt0$temp;
                menu.openPreviewEditor(parent);
            } else {
                PreviewSettings.openEditorFromAnyScreen(parent);
            }
        }).setVisible(() -> !PreviewSettings.isEditing(parent));
    }

    private static void openEditorFromAnyScreen(Module parent) {
        class_310 mc = class_310.method_1551();
        GuiSounds.editor();
        Menu.returnToGlassAfterEditor = true;
        mc.method_1507(null);
        mc.method_63588(() -> {
            Menu menu = new Menu();
            mc.method_1507((class_437)menu);
            mc.method_63588(() -> menu.openPreviewEditor(parent));
        });
    }
}


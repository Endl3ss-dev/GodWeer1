/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.api.preview;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.Module;
import ru.decide.module.api.preview.PreviewContext;
import ru.decide.module.api.preview.PreviewSettings;
import ru.decide.screen.PreviewEditor;

@Environment(value=EnvType.CLIENT)
public interface ModulePreview {
    public PreviewSettings previewSettings();

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    default public boolean isPreviewActive() {
        ModulePreview modulePreview = this;
        if (!(modulePreview instanceof Module)) return false;
        Module module = (Module)((Object)modulePreview);
        if (!PreviewEditor.getInstance().isEditing(module)) return false;
        return true;
    }

    public void previewSpawn(PreviewContext var1);

    default public boolean previewNeedsDummy() {
        return false;
    }

    default public boolean previewControlsDummy() {
        return false;
    }

    default public void previewStart(PreviewContext ctx) {
    }

    default public void previewTick(PreviewContext ctx) {
    }

    default public void previewStop() {
    }
}


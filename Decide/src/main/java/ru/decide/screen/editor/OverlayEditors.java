/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.screen.editor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.screen.CrosshairEditor;
import ru.decide.screen.HandsEditor;
import ru.decide.screen.PreviewEditor;
import ru.decide.screen.editor.OverlayEditor;

@Environment(value=EnvType.CLIENT)
public final class OverlayEditors {
    private static final OverlayEditor[] ALL = new OverlayEditor[]{HandsEditor.getInstance(), PreviewEditor.getInstance(), CrosshairEditor.getInstance()};

    private OverlayEditors() {
    }

    public static OverlayEditor[] all() {
        return ALL;
    }

    public static OverlayEditor active() {
        for (OverlayEditor editor : ALL) {
            if (!editor.isActive()) continue;
            return editor;
        }
        return null;
    }

    public static boolean anyActive() {
        return OverlayEditors.active() != null;
    }

    public static void closeAll() {
        for (OverlayEditor editor : OverlayEditors.all()) {
            if (!editor.isActive()) continue;
            editor.closeFromMenuRemoval();
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.optimization.FrameSyncManager;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Frame Sync", category=Category.UTILITIES, desc="\u041e\u0433\u0440\u0430\u043d\u0438\u0447\u0438\u0432\u0430\u0435\u0442 FPS \u0434\u043e \u0447\u0430\u0441\u0442\u043e\u0442\u044b \u043c\u043e\u043d\u0438\u0442\u043e\u0440\u0430 \u0434\u043b\u044f \u043f\u043e\u0432\u044b\u0448\u0435\u043d\u0438\u044f \u043f\u0440\u043e\u0438\u0437\u0432\u043e\u0434\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u0438", autoEnabled=true)
@Environment(value=EnvType.CLIENT)
public class FrameSync
extends Module {
    public static FrameSync get() {
        return Instance.get(FrameSync.class);
    }

    @Override
    public void onEnable() {
        FrameSyncManager.init();
        FrameSyncManager.setEnabled(true);
    }

    @Override
    public void onDisable() {
        FrameSyncManager.setEnabled(false);
    }
}


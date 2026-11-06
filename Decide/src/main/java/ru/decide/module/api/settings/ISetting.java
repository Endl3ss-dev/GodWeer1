/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.api.settings;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.settings.Setting;

@Environment(value=EnvType.CLIENT)
public interface ISetting {
    public Setting<?> setVisible(Supplier<Boolean> var1);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.Category;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Environment(value=EnvType.CLIENT)
public @interface ModuleInfo {
    public String name();

    public String desc() default "\u0423 \u043c\u043e\u0434\u0443\u043b\u044f \u043d\u0435\u0442\u0443 \u043e\u043f\u0438\u0441\u0430\u043d\u0438\u044f";

    public Category category();

    public int key() default -1;

    public boolean autoEnabled() default false;

    public boolean allowDisable() default true;

    public boolean hidden() default false;
}


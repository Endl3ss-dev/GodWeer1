/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.render.font;

import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.utils.render.font.Font;

@Environment(value=EnvType.CLIENT)
public class Fonts {
    private static final Map<String, String> FONT_REGISTRY = new LinkedHashMap<String, String>();
    public static final Font sf_bold = Fonts.register("sf_bold", "sf_bold");
    public static final Font sf_medium = Fonts.register("sf_medium", "sf_medium");
    public static final Font sf_regular = Fonts.register("sf_regular", "sf_regular");
    public static final Font icon = Fonts.register("decide", "decide");
    public static final Font category = Fonts.register("category", "category");
    public static final Font gui = Fonts.register("icongui", "icongui");
    public static final Font decide_2 = Fonts.register("decide_2", "decide_2");
    public static final Font manasco = Fonts.register("manasco", "manasco");
    public static final Font sf_pro_medium = Fonts.register("sf_pro_medium", "sf_pro_medium");

    private static Font register(String name, String path) {
        FONT_REGISTRY.put(name, path);
        return new Font(name);
    }

    public static Map<String, String> getRegistry() {
        return FONT_REGISTRY;
    }

    private Fonts() {
    }
}


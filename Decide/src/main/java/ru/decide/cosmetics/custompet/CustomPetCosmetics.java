/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 */
package ru.decide.cosmetics.custompet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import ru.decide.cosmetics.CosmeticManager;
import ru.decide.cosmetics.custompet.CustomPetRuntime;
import ru.decide.module.impl.render.custompet.CustomPetVariant;

@Environment(value=EnvType.CLIENT)
public final class CustomPetCosmetics {
    private static final int INDEX_BASE = 400000;
    private static final Map<Integer, Spec> BY_INDEX = new HashMap<Integer, Spec>();
    private static boolean initialized;

    private CustomPetCosmetics() {
    }

    public static void initialize(CosmeticManager cm) {
        if (initialized) {
            return;
        }
        initialized = true;
        for (Spec spec : CustomPetCosmetics.buildSpecs()) {
            BY_INDEX.put(spec.index(), spec);
            cm.addEntry(new CosmeticManager.CosmeticEntry(spec.index(), spec.name(), "companion", false, spec.texture(), null));
        }
        CustomPetRuntime.getInstance().initialize();
    }

    public static Spec specForIndex(int index) {
        return BY_INDEX.get(index);
    }

    private static List<Spec> buildSpecs() {
        ArrayList<Spec> specs = new ArrayList<Spec>();
        int index = 400000;
        class_2960 frog = class_2960.method_60655((String)"decide", (String)"textures/entity/frog/custom_pet.png");
        specs.add(new Spec(index++, "\u041f\u0438\u0442\u043e\u043c\u0435\u0446: \u041d\u0438\u0442\u0432\u0438\u0442", frog, CustomPetVariant.NITWIT, false, 0, false, false));
        specs.add(new Spec(index++, "\u041f\u0438\u0442\u043e\u043c\u0435\u0446: \u0424\u0435\u0440\u043c\u0435\u0440", frog, CustomPetVariant.GARDENER, false, 0, false, false));
        specs.add(new Spec(index++, "\u041f\u0438\u0442\u043e\u043c\u0435\u0446: \u0420\u044b\u0431\u0430\u043a", frog, CustomPetVariant.FISHERMAN, false, 0, false, false));
        specs.add(new Spec(index++, "\u041f\u0438\u0442\u043e\u043c\u0435\u0446: \u041f\u0443\u0442\u0435\u0448\u0435\u0441\u0442\u0432\u0435\u043d\u043d\u0438\u043a", frog, CustomPetVariant.MERCHANT, false, 0, false, false));
        specs.add(new Spec(index++, "\u041f\u0438\u0442\u043e\u043c\u0435\u0446: \u0412\u0435\u0434\u044c\u043c\u0430", frog, CustomPetVariant.SORCERER, false, 0, false, false));
        for (int type = 0; type < 4; ++type) {
            specs.add(new Spec(index++, "\u0420\u043e\u0431\u043e\u0442 " + (type + 1), class_2960.method_60655((String)"decide", (String)("textures/entity/robot/type_" + (type + 1) + ".png")), CustomPetVariant.ROBOT, false, type, false, false));
        }
        specs.add(new Spec(index++, "\u0421\u043e\u0432\u0430", class_2960.method_60655((String)"decide", (String)"textures/entity/owl/owl_jump_rope.png"), CustomPetVariant.NITWIT, true, 0, false, false));
        specs.add(new Spec(index++, "\u0421\u043e\u0431\u0430\u043a\u0430: \u041f\u0451\u0441", class_2960.method_60655((String)"decide", (String)"textures/richdog/djekrussel.png"), CustomPetVariant.NITWIT, false, 0, true, false));
        specs.add(new Spec(index++, "\u0421\u043e\u0431\u0430\u043a\u0430: \u0422\u0430\u043a\u0441\u0430", class_2960.method_60655((String)"decide", (String)"textures/richdog/taksa.png"), CustomPetVariant.NITWIT, false, 0, true, true));
        return specs;
    }

    @Environment(value=EnvType.CLIENT)
    public record Spec(int index, String name, class_2960 texture, CustomPetVariant variant, boolean owl, int robotType, boolean dog, boolean dachshund) {
    }
}


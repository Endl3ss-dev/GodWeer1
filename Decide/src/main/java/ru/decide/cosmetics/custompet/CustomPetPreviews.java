/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.custompet;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.cosmetics.custompet.CustomPetCosmetics;
import ru.decide.cosmetics.custompet.CustomPetModelParser;
import ru.decide.cosmetics.geo.GeoBone;
import ru.decide.cosmetics.geo.GeoModel;
import ru.decide.module.impl.render.custompet.CustomPetVariant;

@Environment(value=EnvType.CLIENT)
public final class CustomPetPreviews {
    private static final String FROG = "/assets/decide/geckolib/models/frog/custom_pet.geo.json";
    private static final String ROBOT = "/assets/decide/geckolib/models/robot/robot.geo.json";
    private static final String OWL = "/assets/decide/geckolib/models/owl/owl_jump_rope.geo.json";
    private static final String DOG = "/assets/decide/geckolib/models/dog/pet.geo.json";
    private static final String[] FROG_ALWAYS_HIDDEN = new String[]{"guitar", "flute", "bongo", "bass", "umbrella", "umbrella2", "umbrella3", "fisherman_umbrella", "fisherman_umbrella2", "fisherman_umbrella3", "pride"};
    private static final String[] OWL_HIDDEN = new String[]{"trick_star", "stumble_star_left", "stumble_star_right", "celebrate_confetti_1", "celebrate_confetti_2", "celebrate_confetti_3", "signature", "fly_rope_left", "fly_rope_right"};
    private static final Map<Integer, GeoModel> CACHE = new HashMap<Integer, GeoModel>();

    private CustomPetPreviews() {
    }

    public static GeoModel model(CustomPetCosmetics.Spec spec) {
        if (spec == null) {
            return null;
        }
        return CACHE.computeIfAbsent(spec.index(), key -> CustomPetPreviews.build(spec));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static GeoModel build(CustomPetCosmetics.Spec spec) {
        String path = spec.dog() ? DOG : (spec.owl() ? OWL : (spec.variant() == CustomPetVariant.ROBOT ? ROBOT : FROG));
        try (InputStream in = CustomPetPreviews.class.getResourceAsStream(path);){
            if (in == null) {
                GeoModel geoModel = null;
                return geoModel;
            }
            String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            GeoModel model = CustomPetModelParser.parse(json);
            if (model == null) {
                GeoModel geoModel = null;
                return geoModel;
            }
            if (!spec.dog()) {
                if (spec.owl()) {
                    CustomPetPreviews.applyOwl(model);
                } else if (spec.variant() != CustomPetVariant.ROBOT) {
                    CustomPetPreviews.applyFrog(model, spec.variant());
                }
                CustomPetPreviews.pruneHidden(model.topLevelBones);
            }
            model.computeBounds();
            GeoModel geoModel = model;
            return geoModel;
        }
        catch (Exception e) {
            return null;
        }
    }

    private static void applyFrog(GeoModel model, CustomPetVariant variant) {
        boolean merchant = variant.usesMerchantBody();
        CustomPetPreviews.setVisible(model, "body_merchant", merchant);
        CustomPetPreviews.setVisible(model, "body_default", !merchant);
        CustomPetPreviews.setVisible(model, "leaf", merchant);
        CustomPetPreviews.setVisible(model, "gardener_hat", variant.usesGardenerGear());
        CustomPetPreviews.setVisible(model, "watering_can", variant.usesGardenerGear());
        CustomPetPreviews.setVisible(model, "sourcerer_hat", variant.usesSorcererHat());
        CustomPetPreviews.setVisible(model, "accessories", variant.usesFishermanGear());
        CustomPetPreviews.setVisible(model, "fishing_rod", variant.usesFishermanGear());
        CustomPetPreviews.setVisible(model, "fishing_rod_2", variant.usesFishermanGear());
        CustomPetPreviews.setVisible(model, "fishing_rod_3", variant.usesFishermanGear());
        for (String name : FROG_ALWAYS_HIDDEN) {
            CustomPetPreviews.hide(model, name);
        }
    }

    private static void applyOwl(GeoModel model) {
        for (String name : OWL_HIDDEN) {
            CustomPetPreviews.hide(model, name);
        }
    }

    private static void setVisible(GeoModel model, String name, boolean visible) {
        if (!visible) {
            CustomPetPreviews.hide(model, name);
        }
    }

    private static void hide(GeoModel model, String name) {
        model.getBone(name).ifPresent(bone -> {
            bone.isHidden = true;
        });
    }

    private static void pruneHidden(List<GeoBone> bones) {
        Iterator<GeoBone> it = bones.iterator();
        while (it.hasNext()) {
            GeoBone bone = it.next();
            if (bone.isHidden) {
                it.remove();
                continue;
            }
            CustomPetPreviews.pruneHidden(bone.childBones);
        }
    }
}


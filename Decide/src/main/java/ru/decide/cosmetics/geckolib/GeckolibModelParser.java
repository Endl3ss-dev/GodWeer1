/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.geckolib;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.cosmetics.geo.GeoModel;
import ru.decide.cosmetics.geo.GeoModelParser;
import ru.decide.cosmetics.model.CosmeticModel;

@Environment(value=EnvType.CLIENT)
public class GeckolibModelParser {
    public GeoModel parseModel(CosmeticModel cosmetic) {
        try {
            String json = cosmetic.getRawModelJson();
            if (json == null) {
                System.err.println("[DecideVisual Cosmetics] No raw model JSON for: " + cosmetic.getName());
                return null;
            }
            GeoModel model = GeoModelParser.parse(json);
            if (model == null) {
                System.err.println("[DecideVisual Cosmetics] Failed to parse GeoModel for: " + cosmetic.getName());
                return null;
            }
            return model;
        }
        catch (Exception e) {
            System.err.println("[DecideVisual Cosmetics] Error parsing model for: " + cosmetic.getName() + " -> " + e.getMessage());
            return null;
        }
    }
}


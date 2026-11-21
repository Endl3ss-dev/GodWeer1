/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 *  software.bernie.geckolib.model.GeoModel
 *  software.bernie.geckolib.renderer.base.GeoRenderState
 */
package ru.decide.module.impl.render.custompet.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import ru.decide.module.impl.render.custompet.entity.CustomPetEntity;
import ru.decide.module.impl.render.custompet.model.CustomPetModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(value=EnvType.CLIENT)
public class RobotModel
extends GeoModel<CustomPetEntity> {
    public static final int TYPE_COUNT = 4;
    private static final class_2960 MODEL = class_2960.method_60655((String)"decide", (String)"robot/robot");
    private static final class_2960 ANIMATIONS = class_2960.method_60655((String)"decide", (String)"robot/robot");
    private static final class_2960[] TEXTURES = new class_2960[4];

    public class_2960 getTextureResource(GeoRenderState geoRenderState) {
        Integer type = (Integer)geoRenderState.getGeckolibData(CustomPetModel.ROBOT_TYPE);
        int n = type != null ? Math.floorMod(type, 4) : 0;
        return TEXTURES[Math.floorMod(n, 4)];
    }

    public class_2960 getModelResource(GeoRenderState geoRenderState) {
        return MODEL;
    }

    public class_2960 getAnimationResource(CustomPetEntity customPetEntity) {
        return ANIMATIONS;
    }

    static {
        for (int i = 0; i < 4; ++i) {
            RobotModel.TEXTURES[i] = class_2960.method_60655((String)"decide", (String)("textures/entity/robot/type_" + (i + 1) + ".png"));
        }
    }
}


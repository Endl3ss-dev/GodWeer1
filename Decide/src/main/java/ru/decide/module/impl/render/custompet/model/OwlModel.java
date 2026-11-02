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
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(value=EnvType.CLIENT)
public class OwlModel
extends GeoModel<CustomPetEntity> {
    private static final class_2960 MODEL = class_2960.method_60655((String)"decide", (String)"owl/owl_jump_rope");
    private static final class_2960 TEXTURE = class_2960.method_60655((String)"decide", (String)"textures/entity/owl/owl_jump_rope.png");
    private static final class_2960 ANIMATIONS = class_2960.method_60655((String)"decide", (String)"owl/owl_jump_rope");

    public class_2960 getTextureResource(GeoRenderState geoRenderState) {
        return TEXTURE;
    }

    public class_2960 getModelResource(GeoRenderState geoRenderState) {
        return MODEL;
    }

    public class_2960 getAnimationResource(CustomPetEntity customPetEntity) {
        return ANIMATIONS;
    }
}


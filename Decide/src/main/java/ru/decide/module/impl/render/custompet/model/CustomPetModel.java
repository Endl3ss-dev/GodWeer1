/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 *  software.bernie.geckolib.constant.dataticket.DataTicket
 *  software.bernie.geckolib.model.GeoModel
 *  software.bernie.geckolib.renderer.base.GeoRenderState
 */
package ru.decide.module.impl.render.custompet.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import ru.decide.module.impl.render.custompet.CustomPetVariant;
import ru.decide.module.impl.render.custompet.entity.CustomPetEntity;
import ru.decide.module.impl.render.custompet.model.FrogModel;
import ru.decide.module.impl.render.custompet.model.OwlModel;
import ru.decide.module.impl.render.custompet.model.RobotModel;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(value=EnvType.CLIENT)
public class CustomPetModel
extends GeoModel<CustomPetEntity> {
    public static final DataTicket<Boolean> UMBRELLA = DataTicket.create((String)"decidevisuals_custom_pet.umbrella", Boolean.class);
    public static final DataTicket<Boolean> AIRBORNE = DataTicket.create((String)"decidevisuals_custom_pet.airborne", Boolean.class);
    public static final DataTicket<String> VARIANT = DataTicket.create((String)"decidevisuals_custom_pet.variant", String.class);
    public static final DataTicket<Integer> ROBOT_TYPE = DataTicket.create((String)"decidevisuals_custom_pet.robot_type", Integer.class);
    public static final DataTicket<Boolean> OWL = DataTicket.create((String)"decidevisuals_custom_pet.owl", Boolean.class);
    private final FrogModel frog = new FrogModel();
    private final RobotModel robot = new RobotModel();
    private final OwlModel owl = new OwlModel();

    private GeoModel<CustomPetEntity> pick(GeoRenderState geoRenderState) {
        if (Boolean.TRUE.equals(geoRenderState.getGeckolibData(OWL))) {
            return this.owl;
        }
        return CustomPetVariant.ROBOT.name().equals(geoRenderState.getGeckolibData(VARIANT)) ? this.robot : this.frog;
    }

    public void addAdditionalStateData(CustomPetEntity customPetEntity, Object relatedObject, GeoRenderState geoRenderState) {
        geoRenderState.addGeckolibData(UMBRELLA, (Object)customPetEntity.shouldUseUmbrella());
        geoRenderState.addGeckolibData(AIRBORNE, (Object)customPetEntity.isAirborneMode());
        geoRenderState.addGeckolibData(VARIANT, (Object)customPetEntity.getPetVariant().name());
        geoRenderState.addGeckolibData(ROBOT_TYPE, (Object)customPetEntity.getRobotType());
        geoRenderState.addGeckolibData(OWL, (Object)customPetEntity.isOwl());
    }

    public class_2960 getTextureResource(GeoRenderState geoRenderState) {
        return this.pick(geoRenderState).getTextureResource(geoRenderState);
    }

    public class_2960 getModelResource(GeoRenderState geoRenderState) {
        return this.pick(geoRenderState).getModelResource(geoRenderState);
    }

    public class_2960 getAnimationResource(CustomPetEntity customPetEntity) {
        if (customPetEntity.isOwl()) {
            return this.owl.getAnimationResource(customPetEntity);
        }
        return customPetEntity.getPetVariant().isRobot() ? this.robot.getAnimationResource(customPetEntity) : this.frog.getAnimationResource(customPetEntity);
    }
}


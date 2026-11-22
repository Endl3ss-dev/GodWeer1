/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10017
 *  net.minecraft.class_11659
 *  net.minecraft.class_12075
 *  net.minecraft.class_12249
 *  net.minecraft.class_1921
 *  net.minecraft.class_2246
 *  net.minecraft.class_2960
 *  net.minecraft.class_4587
 *  net.minecraft.class_4608
 *  net.minecraft.class_5617$class_5618
 *  software.bernie.geckolib.animatable.GeoAnimatable
 *  software.bernie.geckolib.constant.DataTickets
 *  software.bernie.geckolib.model.GeoModel
 *  software.bernie.geckolib.renderer.GeoEntityRenderer
 *  software.bernie.geckolib.renderer.base.BoneSnapshots
 *  software.bernie.geckolib.renderer.base.GeoRenderState
 *  software.bernie.geckolib.renderer.base.RenderPassInfo
 */
package ru.decide.module.impl.render.custompet.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10017;
import net.minecraft.class_11659;
import net.minecraft.class_12075;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2246;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_5617;
import ru.decide.module.impl.render.custompet.CustomPetVariant;
import ru.decide.module.impl.render.custompet.entity.CustomPetEntity;
import ru.decide.module.impl.render.custompet.model.CustomPetModel;
import ru.decide.module.impl.render.custompet.render.CustomPetRenderState;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

@Environment(value=EnvType.CLIENT)
public class CustomPetRenderer
extends GeoEntityRenderer<CustomPetEntity, CustomPetRenderState> {
    private static final float LILY_PAD_SCALE = 1.35f;
    private static final float LILY_PAD_Y_OFFSET = -0.008f;
    private static final float OWL_SCALE = 0.55f;
    private static final String[] OWL_EFFECT_BONES = new String[]{"trick_star", "stumble_star_left", "stumble_star_right", "celebrate_confetti_1", "celebrate_confetti_2", "celebrate_confetti_3", "signature"};
    private static final String[] OWL_GROUND_ROPE_BONES = new String[]{"rope_spin", "left_handle", "right_handle"};
    private static final String[] OWL_FLIGHT_ROPE_BONES = new String[]{"fly_rope_left", "fly_rope_right"};
    private static final String[] CONDITIONAL_BONES = new String[]{"body_default", "body_merchant", "leaf", "gardener_hat", "watering_can", "sourcerer_hat", "accessories", "fishing_rod", "fishing_rod_2", "fishing_rod_3", "guitar", "flute", "bongo", "bass", "umbrella", "umbrella2", "umbrella3", "fisherman_umbrella", "fisherman_umbrella2", "fisherman_umbrella3", "pride"};

    public CustomPetRenderer(class_5617.class_5618 context) {
        super(context, (GeoModel)new CustomPetModel());
        this.field_4673 = 0.35f;
    }

    public void render(CustomPetRenderState state, class_4587 matrices, class_11659 queue, class_12075 cameraState) {
        String string;
        CustomPetVariant customPetVariant;
        super.method_3936((class_10017)state, matrices, queue, cameraState);
        if (Boolean.TRUE.equals(state.getOrDefaultGeckolibData(CustomPetModel.AIRBORNE, false)) && !Boolean.TRUE.equals(state.getOrDefaultGeckolibData(CustomPetModel.OWL, false)) && !(customPetVariant = CustomPetVariant.fromSerializedName(string = (String)state.getOrDefaultGeckolibData(CustomPetModel.VARIANT, CustomPetVariant.NITWIT.name()))).isRobot()) {
            matrices.method_22903();
            matrices.method_46416(-0.675f, -0.008f, -0.675f);
            matrices.method_22905(1.35f, 1.0f, 1.35f);
            queue.method_73481(matrices, class_2246.field_10588.method_9564(), state.field_61820, class_4608.field_21444, state.field_61821);
            matrices.method_22909();
        }
    }

    public class_1921 getRenderType(CustomPetRenderState state, class_2960 texture) {
        return class_12249.method_75990((class_2960)texture);
    }

    public float getMotionAnimThreshold(CustomPetEntity customPetEntity) {
        return 5.0E-4f;
    }

    public void scaleModelForRender(RenderPassInfo<CustomPetRenderState> renderPassInfo, float widthScale, float heightScale) {
        if (Boolean.TRUE.equals(renderPassInfo.getOrDefaultGeckolibData(CustomPetModel.OWL, (Object)false))) {
            widthScale *= 0.55f;
            heightScale *= 0.55f;
        }
        super.scaleModelForRender(renderPassInfo, widthScale, heightScale);
    }

    private static void adjustOwlBones(BoneSnapshots boneSnapshots, boolean bl) {
        for (String string : OWL_EFFECT_BONES) {
            boneSnapshots.ifPresent(string, boneSnapshot -> boneSnapshot.skipRender(true).skipChildrenRender(true));
        }
        for (String string : OWL_GROUND_ROPE_BONES) {
            boneSnapshots.ifPresent(string, boneSnapshot -> boneSnapshot.skipRender(bl).skipChildrenRender(bl));
        }
        for (String string : OWL_FLIGHT_ROPE_BONES) {
            boneSnapshots.ifPresent(string, boneSnapshot -> boneSnapshot.skipRender(!bl).skipChildrenRender(!bl));
        }
    }

    private static boolean isBoneVisible(String string, CustomPetVariant customPetVariant, boolean bl, boolean bl2) {
        return switch (string) {
            case "body_merchant" -> customPetVariant.usesMerchantBody();
            case "body_default" -> {
                if (!customPetVariant.usesMerchantBody()) {
                    yield true;
                }
                yield false;
            }
            case "leaf" -> {
                if (customPetVariant.usesMerchantLeaf() && !bl) {
                    yield true;
                }
                yield false;
            }
            case "gardener_hat", "watering_can" -> customPetVariant.usesGardenerGear();
            case "sourcerer_hat" -> customPetVariant.usesSorcererHat();
            case "accessories", "fishing_rod", "fishing_rod_2", "fishing_rod_3" -> customPetVariant.usesFishermanGear();
            case "umbrella2" -> {
                if (bl2 && !customPetVariant.usesFishermanGear()) {
                    yield true;
                }
                yield false;
            }
            case "fisherman_umbrella2" -> {
                if (bl2 && customPetVariant.usesFishermanGear()) {
                    yield true;
                }
                yield false;
            }
            case "umbrella3" -> {
                if (!bl2 && bl && !customPetVariant.usesFishermanGear()) {
                    yield true;
                }
                yield false;
            }
            case "fisherman_umbrella3" -> {
                if (!bl2 && bl && customPetVariant.usesFishermanGear()) {
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    public void adjustModelBonesForRender(RenderPassInfo<CustomPetRenderState> renderPassInfo, BoneSnapshots boneSnapshots) {
        boolean bl = Boolean.TRUE.equals(renderPassInfo.getOrDefaultGeckolibData(CustomPetModel.AIRBORNE, (Object)false));
        if (Boolean.TRUE.equals(renderPassInfo.getOrDefaultGeckolibData(CustomPetModel.OWL, (Object)false))) {
            CustomPetRenderer.adjustOwlBones(boneSnapshots, bl);
        } else {
            String string = (String)renderPassInfo.getOrDefaultGeckolibData(CustomPetModel.VARIANT, (Object)CustomPetVariant.NITWIT.name());
            CustomPetVariant customPetVariant = CustomPetVariant.fromSerializedName(string);
            boolean bl2 = Boolean.TRUE.equals(renderPassInfo.getOrDefaultGeckolibData(CustomPetModel.UMBRELLA, (Object)false));
            for (String string2 : CONDITIONAL_BONES) {
                boolean bl3 = !CustomPetRenderer.isBoneVisible(string2, customPetVariant, bl2, bl);
                boneSnapshots.ifPresent(string2, boneSnapshot -> boneSnapshot.skipRender(bl3).skipChildrenRender(bl3));
            }
        }
    }

    public CustomPetRenderState createRenderState(CustomPetEntity customPetEntity, Void relatedObject) {
        return new CustomPetRenderState();
    }

    public CustomPetRenderState fillRenderState(CustomPetEntity animatable, Void relatedObject, CustomPetRenderState renderState, float partialTick) {
        renderState.field_53328 = (float)animatable.getAnimationAge() + partialTick;
        CustomPetRenderState result = (CustomPetRenderState)super.fillRenderState((GeoAnimatable)animatable, (Object)relatedObject, (GeoRenderState)renderState, partialTick);
        renderState.addGeckolibData(DataTickets.ENTITY_YAW, Float.valueOf(renderState.field_53446));
        renderState.addGeckolibData(DataTickets.ENTITY_BODY_YAW, Float.valueOf(renderState.field_53446));
        return result;
    }
}


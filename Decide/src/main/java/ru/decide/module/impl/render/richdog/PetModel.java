/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_5603
 *  net.minecraft.class_5606
 *  net.minecraft.class_5607
 *  net.minecraft.class_5609
 *  net.minecraft.class_5610
 *  net.minecraft.class_630
 *  net.minecraft.class_7833
 *  org.joml.Quaternionfc
 */
package ru.decide.module.impl.render.richdog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.class_630;
import net.minecraft.class_7833;
import org.joml.Quaternionfc;
import ru.decide.module.impl.render.richdog.PetBrain;

@Environment(value=EnvType.CLIENT)
public final class PetModel {
    private final class_630 head;
    private final class_630 neck;
    private final class_630 body;
    private final class_630 frontLeftLeg;
    private final class_630 frontRightLeg;
    private final class_630 leftBackLeg;
    private final class_630 rightBackLeg;
    private final class_630 tail;

    public PetModel() {
        class_630 root = PetModel.bakeRoot();
        this.head = root.method_32086("head");
        this.neck = root.method_32086("neck");
        this.body = root.method_32086("body");
        this.frontLeftLeg = root.method_32086("front_left_leg");
        this.frontRightLeg = root.method_32086("front_right_leg");
        this.leftBackLeg = root.method_32086("left_back_leg");
        this.rightBackLeg = root.method_32086("right_back_leg");
        this.tail = root.method_32086("tail");
    }

    private static class_630 bakeRoot() {
        class_5609 mesh = new class_5609();
        class_5610 parts = mesh.method_32111();
        class_5610 head = parts.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-3.0f, -3.0f, -4.0f, 6.0f, 6.0f, 4.0f).method_32101(21, 0).method_32097(-1.5f, 0.0f, -7.0f, 3.0f, 3.0f, 3.0f), class_5603.method_32090((float)0.0f, (float)10.5f, (float)-6.8f));
        head.method_32117("left_ear", class_5606.method_32108().method_32101(32, 4).method_32097(0.0f, -5.0f, -1.5f, 1.0f, 3.0f, 3.0f).method_32101(34, 1).method_32097(0.0f, -5.5f, -0.75f, 1.0f, 1.0f, 1.5f), class_5603.method_32090((float)3.0f, (float)3.0f, (float)-2.0f));
        head.method_32117("right_ear", class_5606.method_32108().method_32101(32, 4).method_32097(-1.0f, -5.0f, -1.5f, 1.0f, 3.0f, 3.0f).method_32101(34, 1).method_32097(-1.0f, -5.5f, -0.75f, 1.0f, 1.0f, 1.5f), class_5603.method_32090((float)-3.0f, (float)3.0f, (float)-2.0f));
        parts.method_32117("neck", class_5606.method_32108().method_32101(15, 7).method_32097(-2.95f, -1.0f, -4.0f, 5.9f, 5.0f, 6.0f), class_5603.method_32091((float)0.0f, (float)10.5f, (float)-5.0f, (float)-0.43633232f, (float)0.0f, (float)0.0f));
        class_5610 body = parts.method_32117("body", class_5606.method_32108(), class_5603.method_32090((float)0.0f, (float)13.5f, (float)-5.0f));
        body.method_32117("chest", class_5606.method_32108().method_32101(32, 13).method_32097(-4.0f, -3.5f, -3.0f, 8.0f, 7.0f, 6.0f), class_5603.method_32090((float)0.0f, (float)0.0f, (float)3.0f));
        body.method_32117("back", class_5606.method_32108().method_32101(3, 19).method_32097(-3.0f, -3.0f, -0.5f, 6.0f, 6.0f, 11.0f), class_5603.method_32090((float)0.0f, (float)-0.5f, (float)5.5f));
        parts.method_32117("front_left_leg", class_5606.method_32108().method_32101(42, 0).method_32097(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f), class_5603.method_32090((float)1.5f, (float)16.0f, (float)-3.0f));
        parts.method_32117("front_right_leg", class_5606.method_32108().method_32096().method_32101(42, 0).method_32097(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f), class_5603.method_32090((float)-1.5f, (float)16.0f, (float)-3.0f));
        parts.method_32117("left_back_leg", class_5606.method_32108().method_32101(52, 0).method_32097(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f), class_5603.method_32090((float)1.5f, (float)16.0f, (float)9.0f));
        parts.method_32117("right_back_leg", class_5606.method_32108().method_32096().method_32101(52, 0).method_32097(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f), class_5603.method_32090((float)-1.5f, (float)16.0f, (float)9.0f));
        parts.method_32117("tail", class_5606.method_32108().method_32101(2, 12).method_32097(-1.0f, 2.0f, -1.0f, 2.0f, 8.0f, 2.0f), class_5603.method_32091((float)0.0f, (float)9.0f, (float)10.0f, (float)0.3926991f, (float)0.0f, (float)0.0f));
        return class_5607.method_32110((class_5609)mesh, (int)60, (int)36).method_32109();
    }

    public void setupAnim(float ageInTicks, PetBrain brain) {
        this.head.field_3675 = brain.getYaw() * ((float)Math.PI / 180);
        this.head.field_3654 = brain.getPitch() * ((float)Math.PI / 180);
        float swing = brain.limbSwing * 0.6662f;
        float amount = brain.limbSwingAmount;
        this.frontLeftLeg.field_3654 = class_3532.method_15362((double)swing) * 1.4f * amount;
        this.frontRightLeg.field_3654 = class_3532.method_15362((double)(swing + (float)Math.PI)) * 1.4f * amount;
        this.leftBackLeg.field_3654 = class_3532.method_15362((double)(swing + (float)Math.PI)) * 1.4f * amount;
        this.rightBackLeg.field_3654 = class_3532.method_15362((double)swing) * 1.4f * amount;
        if (brain.isLay()) {
            this.frontLeftLeg.field_3654 = (float)Math.toRadians(-90.0);
            this.frontRightLeg.field_3654 = (float)Math.toRadians(-90.0);
            this.leftBackLeg.field_3654 = (float)Math.toRadians(90.0);
            this.rightBackLeg.field_3654 = (float)Math.toRadians(90.0);
            this.frontLeftLeg.field_3675 = (float)Math.toRadians(-22.0);
            this.frontRightLeg.field_3675 = (float)Math.toRadians(22.0);
            this.leftBackLeg.field_3675 = (float)Math.toRadians(22.0);
            this.rightBackLeg.field_3675 = (float)Math.toRadians(-22.0);
        } else {
            this.rightBackLeg.field_3675 = 0.0f;
            this.leftBackLeg.field_3675 = 0.0f;
            this.frontRightLeg.field_3675 = 0.0f;
            this.frontLeftLeg.field_3675 = 0.0f;
        }
        this.tail.field_3654 = (float)Math.toRadians(brain.isLay() ? 45.0 : 22.0);
        this.tail.field_3674 = (float)(Math.toRadians(-22.5) + (double)0.3926991f + (double)(class_3532.method_15362((double)(ageInTicks * 0.15f)) * 0.3f));
    }

    public void render(class_4587 pose, class_4588 consumer, int packedLight, int packedOverlay, PetBrain brain) {
        pose.method_22903();
        pose.method_46416(0.0f, 1.2f - (brain.isLay() ? 0.3f : 0.0f), 0.0f);
        pose.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(180.0f));
        pose.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(brain.getBody()));
        this.head.method_22698(pose, consumer, packedLight, packedOverlay);
        this.neck.method_22698(pose, consumer, packedLight, packedOverlay);
        this.body.method_22698(pose, consumer, packedLight, packedOverlay);
        this.frontLeftLeg.method_22698(pose, consumer, packedLight, packedOverlay);
        this.frontRightLeg.method_22698(pose, consumer, packedLight, packedOverlay);
        this.leftBackLeg.method_22698(pose, consumer, packedLight, packedOverlay);
        this.rightBackLeg.method_22698(pose, consumer, packedLight, packedOverlay);
        this.tail.method_22698(pose, consumer, packedLight, packedOverlay);
        pose.method_22909();
    }
}


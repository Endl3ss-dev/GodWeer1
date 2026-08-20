package org.privates.client.render.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.vertex.IVertexBuilder;

import java.util.function.Function;

public class WolfModel extends Model {
    public ModelRenderer head;
    public ModelRenderer body;
    public ModelRenderer frontLeftLeg;
    public ModelRenderer frontRightLeg;
    public ModelRenderer backLeftLeg;
    public ModelRenderer backRightLeg;
    public ModelRenderer tail;
    public ModelRenderer leftEar;
    public ModelRenderer rightEar;

    public WolfModel(Function<ResourceLocation, RenderType> renderTypeIn) {
        super(renderTypeIn);
        this.textureWidth = 64;
        this.textureHeight = 32;
        // Голова
        this.head = new ModelRenderer(this);
        this.head.setRotationPoint(0.0F, 13.0F, -7.0F);
        this.head.setTextureOffset(0, 0).addBox(-3.0F, -3.0F, -4.0F, 6.0F, 6.0F, 6.0F, 0.0F);
        // Уши
        this.leftEar = new ModelRenderer(this);
        this.leftEar.setRotationPoint(2.0F, -2.0F, -2.0F);
        this.leftEar.setTextureOffset(16, 14).addBox(0.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
        this.head.addChild(this.leftEar);
        this.rightEar = new ModelRenderer(this);
        this.rightEar.setRotationPoint(-2.0F, -2.0F, -2.0F);
        this.rightEar.setTextureOffset(16, 14).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
        this.head.addChild(this.rightEar);
        // Тело
        this.body = new ModelRenderer(this);
        this.body.setRotationPoint(0.0F, 14.0F, 2.0F);
        this.body.setTextureOffset(18, 14).addBox(-3.0F, -2.0F, -7.0F, 6.0F, 9.0F, 6.0F, 0.0F);
        // Передние лапы
        this.frontLeftLeg = new ModelRenderer(this);
        this.frontLeftLeg.setRotationPoint(2.0F, 18.0F, -4.0F);
        this.frontLeftLeg.setTextureOffset(0, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F);
        this.frontRightLeg = new ModelRenderer(this);
        this.frontRightLeg.setRotationPoint(-2.0F, 18.0F, -4.0F);
        this.frontRightLeg.setTextureOffset(0, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F);
        // Задние лапы
        this.backLeftLeg = new ModelRenderer(this);
        this.backLeftLeg.setRotationPoint(2.0F, 18.0F, 5.0F);
        this.backLeftLeg.setTextureOffset(0, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F);
        this.backRightLeg = new ModelRenderer(this);
        this.backRightLeg.setRotationPoint(-2.0F, 18.0F, 5.0F);
        this.backRightLeg.setTextureOffset(0, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F);
        // Хвост
        this.tail = new ModelRenderer(this);
        this.tail.setRotationPoint(0.0F, 15.0F, 8.0F);
        this.tail.setTextureOffset(22, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
    }

    public void setRotationAngles(float ageInTicks, AngryDogBrain brain) {
        // Голова
        head.rotateAngleY = brain.getYaw() * ((float) Math.PI / 180F);
        head.rotateAngleX = brain.getPitch() * ((float) Math.PI / 180F);
        // Лапы
        float swing = brain.limbSwing;
        float swingAmount = brain.limbSwingAmount;
        if (brain.isLay()) {
            frontLeftLeg.rotateAngleX = (float) Math.toRadians(-90);
            frontRightLeg.rotateAngleX = (float) Math.toRadians(-90);
            backLeftLeg.rotateAngleX = (float) Math.toRadians(90);
            backRightLeg.rotateAngleX = (float) Math.toRadians(90);
        } else {
            frontLeftLeg.rotateAngleX = (float) Math.cos(swing * 0.6662F) * 1.4F * swingAmount;
            frontRightLeg.rotateAngleX = (float) Math.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * swingAmount;
            backLeftLeg.rotateAngleX = (float) Math.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * swingAmount;
            backRightLeg.rotateAngleX = (float) Math.cos(swing * 0.6662F) * 1.4F * swingAmount;
        }
        // Хвост
        if (brain.isAngry()) {
            tail.rotateAngleY = 0.0F;
            tail.rotateAngleX = (float) Math.toRadians(10);
        } else if (brain.isLay()) {
            tail.rotateAngleY = 0.0F;
            tail.rotateAngleX = (float) Math.toRadians(45);
        } else {
            tail.rotateAngleY = (float) Math.toRadians(brain.getTailWag());
            tail.rotateAngleX = (float) Math.toRadians(22);
        }
    }

    public void render(MatrixStack ms, IVertexBuilder buffer, int packedLight, int packedOverlay, AngryDogBrain brain) {
        ms.push();
        ms.translate(0.0, 0.0, 0.0);
        ms.scale(0.8f, 0.8f, 0.8f); // чуть меньше обычного волка
        head.render(ms, buffer, packedLight, packedOverlay);
        body.render(ms, buffer, packedLight, packedOverlay);
        frontLeftLeg.render(ms, buffer, packedLight, packedOverlay);
        frontRightLeg.render(ms, buffer, packedLight, packedOverlay);
        backLeftLeg.render(ms, buffer, packedLight, packedOverlay);
        backRightLeg.render(ms, buffer, packedLight, packedOverlay);
        tail.render(ms, buffer, packedLight, packedOverlay);
        ms.pop();
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        // Не используется, основной рендер с brain
    }
} 
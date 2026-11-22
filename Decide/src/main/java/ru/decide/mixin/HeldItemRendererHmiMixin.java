/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags
 *  net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
 *  net.minecraft.class_11659
 *  net.minecraft.class_11954
 *  net.minecraft.class_12075
 *  net.minecraft.class_1268
 *  net.minecraft.class_1306
 *  net.minecraft.class_1309
 *  net.minecraft.class_1747
 *  net.minecraft.class_1764
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1803
 *  net.minecraft.class_1828
 *  net.minecraft.class_1839
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2312
 *  net.minecraft.class_2338
 *  net.minecraft.class_2343
 *  net.minecraft.class_2374
 *  net.minecraft.class_243
 *  net.minecraft.class_2459
 *  net.minecraft.class_2586
 *  net.minecraft.class_2680
 *  net.minecraft.class_2769
 *  net.minecraft.class_310
 *  net.minecraft.class_3481
 *  net.minecraft.class_3486
 *  net.minecraft.class_3489
 *  net.minecraft.class_3532
 *  net.minecraft.class_3922
 *  net.minecraft.class_4050
 *  net.minecraft.class_4184
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597
 *  net.minecraft.class_4608
 *  net.minecraft.class_5134
 *  net.minecraft.class_742
 *  net.minecraft.class_759
 *  net.minecraft.class_776
 *  net.minecraft.class_7833
 *  net.minecraft.class_811
 *  net.minecraft.class_824
 *  net.minecraft.class_9285
 *  net.minecraft.class_9285$class_9287
 *  net.minecraft.class_9334
 *  org.joml.Quaternionfc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 */
package ru.decide.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.class_11659;
import net.minecraft.class_11954;
import net.minecraft.class_12075;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1309;
import net.minecraft.class_1747;
import net.minecraft.class_1764;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1803;
import net.minecraft.class_1828;
import net.minecraft.class_1839;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2312;
import net.minecraft.class_2338;
import net.minecraft.class_2343;
import net.minecraft.class_2374;
import net.minecraft.class_243;
import net.minecraft.class_2459;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_2769;
import net.minecraft.class_310;
import net.minecraft.class_3481;
import net.minecraft.class_3486;
import net.minecraft.class_3489;
import net.minecraft.class_3532;
import net.minecraft.class_3922;
import net.minecraft.class_4050;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_5134;
import net.minecraft.class_742;
import net.minecraft.class_759;
import net.minecraft.class_776;
import net.minecraft.class_7833;
import net.minecraft.class_811;
import net.minecraft.class_824;
import net.minecraft.class_9285;
import net.minecraft.class_9334;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import ru.decide.module.impl.render.HoldMyItems;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_759.class})
public abstract class HeldItemRendererHmiMixin {
    @Unique
    private boolean hmi$repPower = false;
    @Unique
    private float hmi$prevAge = 0.0f;
    @Unique
    private double hmi$previousRotation = 0.0;
    @Unique
    private float hmi$swingAngleY = 0.0f;
    @Unique
    private float hmi$swingAngleX = 0.0f;
    @Unique
    private float hmi$swingVelocityY = 0.0f;
    @Unique
    private float hmi$swingVelocityX = 0.0f;
    @Unique
    private float hmi$swingVelocityZ = 0.0f;
    @Unique
    private float hmi$vertAngleY = 0.0f;
    @Unique
    private float hmi$vertVelocityY = 0.0f;
    @Unique
    private float hmi$vertVelocityYSlime = 0.0f;
    @Unique
    private float hmi$vertAngleYSlime = 0.0f;
    @Unique
    private float hmi$riptideCounter = 0.0f;
    @Unique
    private float hmi$netherCounter = 0.0f;
    @Unique
    private float hmi$fallCounter = 0.0f;
    @Unique
    private float hmi$inWaterCounter = 0.0f;
    @Unique
    private float hmi$inspect = 0.0f;
    @Unique
    private float hmi$tilt = 0.0f;
    @Unique
    private float hmi$freezeCounter = 0.0f;
    @Unique
    private float hmi$clCount = 0.0f;
    @Unique
    private float hmi$crawlCount = 0.0f;
    @Unique
    private float hmi$directionalCrawlCount = 0.0f;
    @Unique
    private float hmi$climbCount = 0.0f;
    @Unique
    private float hmi$mouseHolding = 1.0f;
    @Unique
    private boolean hmi$isSwinging = false;
    @Unique
    private float hmi$swingProgress = 0.0f;
    @Unique
    private boolean hmi$isForward = false;
    @Unique
    private boolean hmi$isAttacking = false;
    @Unique
    private boolean hmi$left = false;
    @Shadow
    private class_1799 field_4047;
    @Shadow
    @Final
    private class_310 field_4050;

    @Shadow
    protected abstract void method_3216(class_4587 var1, class_11659 var2, int var3, class_1306 var4);

    @Shadow
    protected abstract void method_3219(class_4587 var1, class_11659 var2, int var3, float var4, float var5, class_1306 var6);

    @Shadow
    protected abstract void method_3222(class_4587 var1, class_11659 var2, int var3, float var4, class_1306 var5, float var6, class_1799 var7);

    @Shadow
    protected abstract void method_3231(class_4587 var1, class_11659 var2, int var3, float var4, float var5, float var6);

    @Shadow
    protected abstract void method_3224(class_4587 var1, class_1306 var2, float var3);

    @Shadow
    protected abstract void method_3217(class_4587 var1, class_1306 var2, float var3);

    @Shadow
    public abstract void method_3233(class_1309 var1, class_1799 var2, class_811 var3, class_4587 var4, class_11659 var5, int var6);

    @Unique
    private float hmiEaseInOutBack(float x) {
        float c1 = 1.70158f;
        float c2 = c1 * 1.525f;
        return (float)((double)x < 0.5 ? Math.pow(2.0 * (double)x, 2.0) * (double)((c2 + 1.0f) * 2.0f * x - c2) / 2.0 : (Math.pow(2.0 * (double)x - 2.0, 2.0) * (double)((c2 + 1.0f) * (x * 2.0f - 2.0f) + c2) + 2.0) / 2.0);
    }

    @Unique
    private float hmiGetAttackDamage(class_1799 stack) {
        class_9285 modifiers = (class_9285)stack.method_57353().method_58694(class_9334.field_49636);
        if (modifiers == null) {
            return 0.0f;
        }
        float totalDamage = 0.0f;
        for (class_9285.class_9287 entry : modifiers.comp_2393()) {
            if (entry.comp_2395().comp_349() != class_5134.field_23721.comp_349()) continue;
            totalDamage += (float)entry.comp_2396().comp_2449();
        }
        return totalDamage;
    }

    @Unique
    private boolean hmi$isCrawling(class_742 player) {
        return player.method_18376() == class_4050.field_18079 && !player.method_20232();
    }

    @Unique
    private boolean hmiIsSharpAnimation(HoldMyItems config) {
        return config != null && config.isSharpAnimation();
    }

    @Unique
    private void hmiAltSwing(class_4587 matrices, class_1306 arm, float swingProgress, class_1799 item) {
        int i = arm == class_1306.field_6183 ? 1 : -1;
        float f = class_3532.method_15374((double)(swingProgress * 3.14f));
        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)i * (45.0f + f * 0.0f)));
        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)i * -45.0f));
    }

    public void hmiRenderCustomFirstPersonItem(class_742 player, float tickDelta, float pitch, class_1268 hand, class_1306 arm, float sideFactor, float swingProgress, class_1799 item, float equipProgress, class_4587 matrices, class_11659 queue, int light) {
        HoldMyItems hmi = HoldMyItems.getInstance();
        if (hmi != null && hmi.isEnabled()) {
            boolean isUsingSpyglass;
            boolean bl = isUsingSpyglass = player.method_6115() && player.method_6030().method_31574(class_1802.field_27070);
            if (!isUsingSpyglass) {
                boolean sharpSword;
                HoldMyItems config = HoldMyItems.getInstance();
                float yaw = player.method_36454();
                double radians = Math.toRadians(yaw);
                double forwardX = -Math.sin(radians);
                double forwardZ = Math.cos(radians);
                class_243 horizontalVelocity = player.method_18798();
                double dotProduct = horizontalVelocity.field_1352 * forwardX + horizontalVelocity.field_1350 * forwardZ;
                double crossProduct = horizontalVelocity.field_1352 * forwardZ - horizontalVelocity.field_1350 * forwardX;
                float al = player.method_36455() != 0.0f ? 90.0f / player.method_36455() / 10.0f : 1.0f;
                if (al > 1.0f) {
                    al = 1.0f;
                }
                if (al < 0.0f) {
                    al = 1.0f;
                }
                boolean bl2 = hand == class_1268.field_5808;
                matrices.method_22903();
                matrices.method_22903();
                if (bl2) {
                    matrices.method_46416(config.mainHandX.getValue().floatValue(), config.mainHandY.getValue().floatValue(), config.mainHandZ.getValue().floatValue());
                } else {
                    matrices.method_46416(config.offHandX.getValue().floatValue(), config.offHandY.getValue().floatValue(), config.offHandZ.getValue().floatValue());
                }
                double tt = 1.0 / (double)Math.max(class_310.method_1551().method_47599(), 1) * 30.0;
                float smoothness = class_3532.method_15363((float)config.smoothness.getValue().floatValue(), (float)0.35f, (float)2.5f);
                float hmiProgress = (float)Math.pow(class_3532.method_15363((float)swingProgress, (float)0.0f, (float)1.0f), smoothness);
                float swing_rot = (double)hmiProgress < 0.6 ? class_3532.method_15374((double)(class_3532.method_15363((float)hmiProgress, (float)0.0f, (float)0.12506f) * 12.56f)) : class_3532.method_15374((double)(class_3532.method_15363((float)hmiProgress, (float)0.62532f, (float)0.75038f) * 12.56f));
                float swing = class_3532.method_15374((double)(hmiProgress * 3.14f));
                swing = this.hmiEaseInOutBack(swing);
                boolean bl3 = sharpSword = item.method_31573(class_3489.field_42611) && this.hmiIsSharpAnimation(config);
                if ((item.method_31574(class_1802.field_8287) || item.method_31574(class_1802.field_49098) || item.method_31574(class_1802.field_8803) || item.method_31574(class_1802.field_8449) || item.method_31574(class_1802.field_8543) || item.method_7909() instanceof class_1828 || item.method_7909() instanceof class_1803) && player.method_6079().method_7960() && item.method_7976() != class_1839.field_63380 && !item.method_31574(class_1802.field_8814) && !player.method_20232() && !this.hmi$isCrawling(player) && !player.method_6101()) {
                    if (player.method_6068() == class_1306.field_6182) {
                        bl2 = !bl2;
                    }
                    matrices.method_22903();
                    matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(-25.0f * sideFactor));
                    matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-10.0f));
                    matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(25.0f * sideFactor * swing));
                    matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(30.0f * swing));
                    matrices.method_22904(-0.15 * (double)sideFactor, 0.1, 0.1);
                    matrices.method_22904(0.0, -0.55 * (double)swing, 0.4 * (double)swing * (double)3.14f);
                    this.method_3219(matrices, queue, light, equipProgress, 0.0f, arm.method_5928());
                    matrices.method_22909();
                }
                if (this.field_4050.field_1690.field_1886.method_1434() && !this.hmi$isAttacking && (double)swingProgress == 0.0) {
                    boolean bl4 = this.hmi$left = !this.hmi$left;
                }
                if (!item.method_7960()) {
                    if (player.method_6068() == class_1306.field_6182) {
                        boolean bl5 = bl2 = !bl2;
                    }
                    if ((this.hmi$left || item.method_31573(class_3489.field_42612) || item.method_7976() == class_1839.field_63380 || item.method_7976() == class_1839.field_8949) && !item.method_31573(class_3489.field_42615)) {
                        if (sharpSword) {
                            matrices.method_22904(0.1 * (double)sideFactor * (double)swing_rot, 0.1 * (double)swing_rot, -0.5 * (double)swing);
                            matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(-30.0f * swing_rot));
                            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(-20.0f * swing_rot * sideFactor));
                            matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(40.0f * swing));
                        } else if (!item.method_31573(class_3489.field_42611) && !item.method_31573(class_3489.field_42612)) {
                            if (item.method_7976() == class_1839.field_63380) {
                                matrices.method_22904(0.0, 0.0, 0.45 * (double)swing_rot);
                                matrices.method_22904(-0.25 * (double)sideFactor * (double)swing, -0.35 * (double)swing_rot, -0.6 * (double)swing);
                                matrices.method_22904(0.0, 0.1 * (double)swing, 0.0);
                                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(15.0f * swing_rot * sideFactor));
                                matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(30.0f * swing_rot * sideFactor));
                            } else if (item.method_31573(ConventionalItemTags.TOOLS) && item.method_7976() != class_1839.field_8949 && !item.method_31573(class_3489.field_42615)) {
                                matrices.method_22904(0.1 * (double)sideFactor * (double)swing_rot, 0.1 * (double)swing_rot, -0.5 * (double)swing);
                                matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(-30.0f * swing_rot));
                                matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(-20.0f * swing_rot * sideFactor));
                                matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(40.0f * swing));
                            } else if (item.method_7976() != class_1839.field_8949) {
                                matrices.method_22904(0.1 * (double)sideFactor * (double)swing_rot, 0.1 * (double)swing_rot, -0.1 * (double)swing);
                                matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(-30.0f * swing_rot));
                                matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(-10.0f * swing_rot * sideFactor));
                                matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(40.0f * swing));
                                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(10.0f * swing * sideFactor));
                            } else {
                                matrices.method_22904(0.1 * (double)sideFactor * (double)swing_rot, 0.1 * (double)swing_rot, -0.2 * (double)swing);
                                matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(-10.0f * swing_rot));
                                matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(-10.0f * swing_rot * sideFactor));
                                matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(20.0f * swing));
                            }
                        } else {
                            matrices.method_22904(0.8 * (double)sideFactor * (double)swing_rot, 0.3 * (double)swing_rot, -0.5 * (double)swing);
                            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(15.0f * swing_rot * sideFactor));
                            matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(-20.0f * swing_rot));
                            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(-70.0f * swing_rot * sideFactor));
                            if (item.method_31573(class_3489.field_42611)) {
                                matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(40.0f * swing));
                            } else {
                                matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(30.0f * swing));
                            }
                        }
                    } else if (!item.method_31573(class_3489.field_42615)) {
                        if (sharpSword) {
                            matrices.method_22904(0.1 * (double)sideFactor * (double)swing_rot, 0.1 * (double)swing_rot, -0.5 * (double)swing);
                            matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(-30.0f * swing_rot));
                            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(-20.0f * swing_rot * sideFactor));
                            matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(40.0f * swing));
                        } else if (item.method_31573(class_3489.field_42611)) {
                            matrices.method_22904(0.1 * (double)sideFactor * (double)swing_rot, 0.1 * (double)swing_rot, -0.5 * (double)swing);
                            matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(-30.0f * swing_rot));
                            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(-20.0f * swing_rot * sideFactor));
                            matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(40.0f * swing));
                        } else if (item.method_31573(ConventionalItemTags.TOOLS) && !item.method_31573(class_3489.field_42615)) {
                            matrices.method_22904(0.1 * (double)sideFactor * (double)swing_rot, 0.1 * (double)swing_rot, -0.5 * (double)swing);
                            matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(-30.0f * swing_rot));
                            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(-20.0f * swing_rot * sideFactor));
                            matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(40.0f * swing));
                        } else {
                            matrices.method_22904(0.1 * (double)sideFactor * (double)swing_rot, 0.1 * (double)swing_rot, -0.1 * (double)swing);
                            matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(-30.0f * swing_rot));
                            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(-10.0f * swing_rot * sideFactor));
                            matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(40.0f * swing));
                            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(10.0f * swing * sideFactor));
                        }
                    } else if (item.method_31573(class_3489.field_42615)) {
                        matrices.method_22904(0.0, 0.15 * (double)swing_rot, -0.25 * (double)swing_rot);
                        matrices.method_22904(0.0, 0.0, -0.2 * (double)swing);
                        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(15.0f * swing_rot));
                        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-35.0f * swing_rot));
                        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(30.0f * swing));
                    }
                } else if (!(class_2248.method_9503((class_1792)item.method_7909()) == class_2246.field_10124 || item.method_31573(ConventionalItemTags.TOOLS) && !item.method_31573(class_3489.field_41890) && !item.method_31573(class_3489.field_40109) && item.method_7976() != class_1839.field_8950 && item.method_7923() || item.method_7976() == class_1839.field_8953 || item.method_7976() == class_1839.field_27079 || this.hmiGetAttackDamage(item) != 0.0f || item.method_7976() == class_1839.field_8949 || item.method_31574(class_1802.field_23254) || item.method_31574(class_1802.field_8184) || item.method_31574(class_1802.field_8378) || item.method_31574(class_1802.field_8868))) {
                    if ((swingProgress = (float)((double)swingProgress * 1.2)) > 1.0f) {
                        swingProgress = 0.0f;
                    }
                } else if (!item.method_31573(class_3489.field_42615) && (swingProgress = (float)((double)swingProgress * 1.5)) > 1.0f) {
                    swingProgress = 0.0f;
                }
                if (player.method_18798().method_1033() >= 0.08) {
                    this.hmi$crawlCount = (float)((double)this.hmi$crawlCount + 0.1 * player.method_18798().method_1033() * 2.0 * tt);
                    this.hmi$directionalCrawlCount = (float)((double)this.hmi$directionalCrawlCount + 0.1 * dotProduct * 4.0 * tt);
                    this.hmi$directionalCrawlCount = (float)((double)this.hmi$directionalCrawlCount + (dotProduct > 0.0 ? 0.1 * Math.abs(crossProduct) * 4.0 * tt : 0.1 * Math.abs(crossProduct) * -1.0 * 4.0 * tt));
                }
                if (player.method_18798().field_1351 > 0.0) {
                    this.hmi$climbCount = (float)((double)this.hmi$climbCount + 0.1 * tt);
                }
                if (player.method_18798().field_1351 < 0.0) {
                    this.hmi$climbCount = (float)((double)this.hmi$climbCount - 0.1 * tt);
                }
                if ((this.hmi$isCrawling(player) && config.climbAndCrawl.getValue().booleanValue() || player.method_6101() && !player.method_24828() && Math.abs(player.method_18798().field_1351) > 0.0 && config.climbAndCrawl.getValue().booleanValue()) && !player.method_6115() && swingProgress == 0.0f) {
                    this.hmi$clCount = (float)((double)this.hmi$clCount + 0.1 * tt);
                    if (this.hmi$clCount > 1.0f) {
                        this.hmi$clCount = 1.0f;
                    }
                    if (!item.method_31574(class_1802.field_16539) && !item.method_31574(class_1802.field_22016)) {
                        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-20.0f * this.hmi$clCount));
                    }
                } else {
                    this.hmi$clCount = (float)((double)this.hmi$clCount * Math.pow(0.88f, tt));
                }
                if (swingProgress == 0.0f) {
                    matrices.method_46416(bl2 ? player.method_36455() / 650.0f * this.hmi$clCount * -1.0f : player.method_36455() / 650.0f * this.hmi$clCount, 0.0f, 0.0f);
                    matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(player.method_36455() * this.hmi$clCount));
                }
                if (!item.method_31574(class_1802.field_16539) && !item.method_31574(class_1802.field_22016)) {
                    matrices.method_46416(0.0f, 0.0f, player.method_36455() / 120.0f * this.hmi$clCount);
                } else if (swingProgress == 0.0f) {
                    matrices.method_46416(0.0f, 0.0f, player.method_36455() / 80.0f * this.hmi$clCount);
                }
                if (player.method_6101() && config.climbAndCrawl.getValue().booleanValue() && !player.method_24828() && !item.method_31574(class_1802.field_16539) && !item.method_31574(class_1802.field_22016) && !player.method_6115()) {
                    matrices.method_22904(0.0, 0.1, -0.2);
                }
                if (player.method_5799() && !player.method_20232() && !player.method_5777(class_3486.field_15517)) {
                    this.hmi$inWaterCounter = (float)((double)this.hmi$inWaterCounter + 0.1 * tt);
                    if (this.hmi$inWaterCounter >= 1.0f) {
                        this.hmi$inWaterCounter = 1.0f;
                    }
                } else {
                    this.hmi$inWaterCounter = (float)((double)this.hmi$inWaterCounter * Math.pow(0.88f, tt));
                }
                this.hmi$freezeCounter = (float)((double)this.hmi$freezeCounter * Math.pow(0.88f, tt));
                matrices.method_22904(0.0, 0.02 * (double)this.hmi$inWaterCounter, 0.0);
                matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(8.0f * sideFactor * this.hmi$inWaterCounter));
                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(0.3f * class_3532.method_15374((double)(this.hmi$freezeCounter * 5.0f))));
                if (player.method_18798().field_1351 < -0.85 && item.method_31574(class_1802.field_49814) && player.method_6047() == item) {
                    this.hmi$fallCounter = (float)((double)this.hmi$fallCounter + 0.1 * tt);
                    if (this.hmi$fallCounter >= 1.0f) {
                        this.hmi$fallCounter = 1.0f;
                    }
                } else {
                    this.hmi$fallCounter = (float)((double)this.hmi$fallCounter * Math.pow(0.88f, tt));
                }
                if (bl2) {
                    matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(45.0f * this.hmi$fallCounter));
                    matrices.method_22904(0.0, -0.2 * (double)this.hmi$fallCounter, 0.0);
                }
                this.hmi$vertAngleY = (float)((double)this.hmi$vertAngleY + player.method_18798().field_1351 * (double)0.015f * tt);
                this.hmi$vertAngleY = (float)((double)this.hmi$vertAngleY - (double)(0.1f * this.hmi$vertAngleY) * tt);
                this.hmi$vertAngleY = (float)((double)this.hmi$vertAngleY * Math.pow(0.88f, tt));
                this.hmi$vertVelocityYSlime = (float)((double)this.hmi$vertVelocityYSlime + player.method_18798().field_1351 * (double)0.015f * tt);
                this.hmi$vertVelocityYSlime = (float)((double)this.hmi$vertVelocityYSlime - (double)(0.1f * this.hmi$vertAngleYSlime) * tt);
                this.hmi$vertVelocityYSlime = (float)((double)this.hmi$vertVelocityYSlime * Math.pow(0.88f, tt));
                this.hmi$vertAngleYSlime = (float)((double)this.hmi$vertAngleYSlime + (double)this.hmi$vertVelocityYSlime * tt);
                matrices.method_46416(0.0f, this.hmi$vertAngleY * -1.0f, 0.0f);
                matrices.method_22904(0.0, Math.sin((double)player.field_6012 * 0.1) * 0.007 * (double)sideFactor, 0.0);
                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(0.15f * class_3532.method_15374((double)((float)player.field_6012 * 0.15f)) * sideFactor));
                if (!item.method_7960() || this.hmi$isCrawling(player) || player.method_6101() && !player.method_24828() || player.method_20232()) {
                    if (player.method_6068() == class_1306.field_6182) {
                        boolean bl6 = bl2 = !bl2;
                    }
                    if (item.method_7976() == class_1839.field_8949) {
                        matrices.method_46416(0.0f, 0.0f, 0.0f);
                    } else {
                        matrices.method_22904(0.0, -0.1, 0.1);
                    }
                }
                if (item.method_31574(class_1802.field_16539) || item.method_31574(class_1802.field_22016) || item.method_31573(class_3489.field_40108)) {
                    matrices.method_22904(0.0, 0.1, 0.0);
                    if (player.method_20232()) {
                        matrices.method_22904(0.0, -0.1, 0.1);
                    }
                }
                if (player.method_20232() && swingProgress == 0.0f && config.swimmingAnimation.getValue().booleanValue()) {
                    double a;
                    double distance = this.hmi$crawlCount;
                    double swingAmplitude = 1.5;
                    double frequency = 2.0;
                    double s = distance * frequency;
                    double handRotation = Math.sin(s) * swingAmplitude;
                    double smoothRotation = handRotation * 0.8 + this.hmi$previousRotation * 0.2;
                    matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(bl2 ? smoothRotation : -smoothRotation)));
                    matrices.method_22904(0.0, 0.0, smoothRotation * (double)0.2f);
                    double k = this.hmi$crawlCount * 2.0f;
                    double b = a = Math.cos(k);
                    if (a <= 0.0) {
                        b = a * 0.5;
                    }
                    matrices.method_22907((Quaternionfc)class_7833.field_40715.rotationDegrees((float)(bl2 ? b * 30.0 : b * 30.0 * -1.0)));
                    matrices.method_22904(0.0, 0.0, a * (double)0.2f);
                    if (item.method_7960() && !bl2 && !player.method_5767()) {
                        matrices.method_22904((double)(1.0f * sideFactor), 0.0 - (double)equipProgress * 0.3, 0.3);
                        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(45.0f * sideFactor));
                        matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(-40.0f * sideFactor));
                        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(30.0f));
                        this.hmiAltSwing(matrices, arm, swingProgress, item);
                        float c = class_3532.method_15374((double)(equipProgress * 3.14f));
                        matrices.method_22905(0.9f, 0.9f, 0.9f);
                        this.method_3219(matrices, queue, light, 0.0f, 0.0f, arm);
                    }
                    this.hmi$previousRotation = smoothRotation;
                }
                if ((player.method_6101() && !player.method_24828() || this.hmi$isCrawling(player) && swingProgress == 0.0f) && !player.method_6115()) {
                    double s = this.hmi$climbCount;
                    float v = (float)player.method_18798().field_1351;
                    float a = class_3532.method_15362((double)((float)s * 2.0f));
                    if (player.method_6101()) {
                        if (!item.method_31574(class_1802.field_16539) && !item.method_31574(class_1802.field_22016)) {
                            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(20.0f * a * sideFactor));
                        } else {
                            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(1.0f * a * sideFactor));
                        }
                    }
                    if (this.hmi$isCrawling(player) && !player.method_6115() && swingProgress == 0.0f) {
                        float crawlProgress = class_3532.method_15374((double)(this.hmi$directionalCrawlCount * 4.0f * this.hmi$mouseHolding));
                        float upAndDown = class_3532.method_15362((double)(this.hmi$directionalCrawlCount * 4.0f * this.hmi$mouseHolding));
                        if (item.method_31574(class_1802.field_16539) || item.method_31574(class_1802.field_22016)) {
                            crawlProgress *= 0.14f;
                            upAndDown *= 0.14f;
                        }
                        matrices.method_22904(0.2 * (double)crawlProgress, 0.3 * (double)crawlProgress * (double)sideFactor, -0.2 * (double)crawlProgress * (double)sideFactor * (double)al);
                        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(25.0f * crawlProgress));
                        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(class_3532.method_15363((float)(20.0f * upAndDown * sideFactor), (float)0.0f, (float)20.0f)));
                    }
                    if (item.method_7960() && !bl2 && !player.method_5767() && (!player.method_24828() && player.method_6101() || this.hmi$isCrawling(player))) {
                        matrices.method_22904((double)(1.0f * sideFactor), 0.0 - (double)equipProgress * 0.3, 0.3);
                        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(45.0f * sideFactor));
                        matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(-40.0f * sideFactor));
                        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(30.0f));
                        this.hmiAltSwing(matrices, arm, swingProgress, item);
                        matrices.method_22905(0.9f, 0.9f, 0.9f);
                        this.method_3219(matrices, queue, light, 0.0f, 0.0f, arm);
                    }
                }
                if (item.method_7960()) {
                    if (bl2 && !player.method_5767()) {
                        if (!(!player.method_24828() && player.method_6101() || player.method_20232() || this.hmi$isCrawling(player))) {
                            if (player.method_6068() == class_1306.field_6182) {
                                bl2 = !bl2;
                            }
                            matrices.method_22904(0.0, 0.2 * (double)swing_rot, 0.15 * (double)swing_rot);
                            matrices.method_22904(0.1 * (double)sideFactor * (double)swing, 0.15 * (double)swing, -0.45 * (double)swing);
                            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(35.0f * swing * sideFactor));
                            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-30.0f * swing));
                            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(-10.0f * swing_rot * sideFactor));
                            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(10.0f * swing_rot));
                            this.method_3219(matrices, queue, light, 0.0f, 0.0f, arm);
                        } else {
                            matrices.method_22904((double)(1.0f * sideFactor), 0.0 - (double)equipProgress * 0.3, 0.3);
                            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(45.0f * sideFactor));
                            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(-40.0f * sideFactor));
                            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(30.0f));
                            this.hmiAltSwing(matrices, arm, swingProgress, item);
                            float c = class_3532.method_15374((double)(equipProgress * 3.14f));
                            matrices.method_22905(0.9f, 0.9f, 0.9f);
                            this.method_3219(matrices, queue, light, 0.0f, 0.0f, arm);
                        }
                    } else if (!bl2 && !player.method_5767()) {
                        matrices.method_22904((double)(1.0f * sideFactor), 0.0 - (double)equipProgress * 0.3, 0.3);
                        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(45.0f * sideFactor));
                        matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(-40.0f * sideFactor));
                        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(30.0f));
                        this.hmiAltSwing(matrices, arm, swingProgress, item);
                        float c = class_3532.method_15374((double)(equipProgress * 3.14f));
                        matrices.method_22905(0.9f, 0.9f, 0.9f);
                        this.method_3219(matrices, queue, light, 0.0f, 0.0f, arm);
                    }
                } else if (item.method_57826(class_9334.field_49646)) {
                    if (bl2 && this.field_4047.method_7960()) {
                        matrices.method_22904(0.0, 0.1, 0.0);
                        this.method_3231(matrices, queue, light, pitch, equipProgress, swingProgress);
                    } else {
                        matrices.method_22904(bl2 ? -0.1 : 0.1, 0.1, 0.0);
                        this.method_3222(matrices, queue, light, equipProgress, arm, swingProgress, item);
                    }
                } else if (item.method_7976() == class_1839.field_8947) {
                    int i;
                    matrices.method_22903();
                    boolean bl22 = class_1764.method_7781((class_1799)item);
                    boolean bl32 = arm == class_1306.field_6183;
                    int n = i = bl32 ? 1 : -1;
                    if (player.method_6115() && player.method_6014() > 0 && player.method_6058() == hand) {
                        this.method_3224(matrices, arm, equipProgress);
                        matrices.method_46416((float)i * -0.4785682f, -0.24387f, 0.05731531f);
                        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-11.935f));
                        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)i * 65.3f));
                        matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)i * 9.785f));
                        f = (float)item.method_7935((class_1309)player) - ((float)player.method_6014() - tickDelta + 1.0f);
                        float g = f / (float)class_1764.method_7775((class_1799)item, (class_1309)player);
                        if (g > 1.0f) {
                            g = 1.0f;
                        }
                        if (g > 0.1f) {
                            h = class_3532.method_15374((double)((f - 0.1f) * 1.3f));
                            float j = g - 0.1f;
                            float k = h * j;
                            matrices.method_46416(k * 0.0f, k * 0.004f, k * 0.0f);
                        }
                        matrices.method_46416(g * 0.0f, g * 0.0f, g * 0.04f);
                        matrices.method_22905(1.0f, 1.0f, 1.0f);
                        matrices.method_22907((Quaternionfc)class_7833.field_40715.rotationDegrees((float)i * 45.0f));
                    } else {
                        this.method_3217(matrices, arm, swingProgress);
                        if (bl22 && swingProgress < 0.001f && bl2) {
                            matrices.method_46416((float)i * -0.341864f, 0.0f, 0.0f);
                            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)i * 10.0f));
                        }
                    }
                    matrices.method_46416(0.0f, 0.0f, -1.0f);
                    matrices.method_22904(-0.45 * (double)i, 0.45, 1.7);
                    matrices.method_22904((double)(1.0f * sideFactor), 0.0 - (double)equipProgress * 0.3, 0.3);
                    matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(45.0f * sideFactor));
                    matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(-40.0f * sideFactor));
                    matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(30.0f));
                    this.hmiAltSwing(matrices, arm, swingProgress, item);
                    float c = class_3532.method_15374((double)(equipProgress * 3.14f));
                    matrices.method_22905(0.9f, 0.9f, 0.9f);
                    this.method_3219(matrices, queue, light, 0.0f, 0.0f, arm);
                    matrices.method_22904(-0.25 * (double)i, 1.25, 0.05);
                    matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(-90 * i)));
                    matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(77.0f));
                    matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(85 * i)));
                    matrices.method_22905(1.2f, 1.2f, 1.2f);
                    matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-10.0f));
                    matrices.method_22904(0.0, -0.15, 0.15);
                    this.method_3233((class_1309)player, item, bl32 ? class_811.field_4322 : class_811.field_4321, matrices, queue, light);
                    matrices.method_22909();
                    if (player.method_6115() && player.method_6014() > 0 && player.method_6058() == hand) {
                        float f = (float)item.method_7935((class_1309)player) - ((float)player.method_6014() - tickDelta + 1.0f);
                        float g = f / (float)class_1764.method_7775((class_1799)item, (class_1309)player);
                        if (g > 1.0f) {
                            g = 1.0f;
                        }
                        if (g > 0.1f) {
                            float h = class_3532.method_15374((double)((f - 0.1f) * 1.3f));
                            float j = g - 0.1f;
                            float k = h * j;
                            matrices.method_46416(k * 0.0f, k * 0.004f, k * 0.0f);
                        }
                        matrices.method_22907((Quaternionfc)class_7833.field_40715.rotationDegrees((double)g <= 0.2 ? 75.0f * g * 5.0f * (float)i : (float)(75 * i)));
                        matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(10.0f * g * 1.5f));
                        matrices.method_22904(-0.37 * (double)i, 0.0, 0.6);
                        matrices.method_22904(0.15 * (double)g * (double)i, 0.0, 0.0);
                        this.method_3219(matrices, queue, light, equipProgress, swingProgress, arm.method_5928());
                    }
                } else {
                    int l;
                    boolean bl23 = arm == class_1306.field_6183;
                    int n = l = bl23 ? 1 : -1;
                    if (player.method_6115() && player.method_6014() > 0 && player.method_6058() == hand) {
                        switch (item.method_7976()) {
                            case field_8952: {
                                this.method_3224(matrices, arm, equipProgress);
                                break;
                            }
                            case field_8950: 
                            case field_8946: {
                                float u = (float)item.method_7935((class_1309)player) - ((float)player.method_6014() - tickDelta + 1.0f);
                                float y = u / 5.0f;
                                if (y > 1.0f) {
                                    y = 1.0f;
                                }
                                float q = class_3532.method_15374((double)(u / 2.0f * 3.14f));
                                matrices.method_22904((double)(1 * l), 0.1, 0.3);
                                matrices.method_22904(0.2 * (double)l * (double)y, -0.7 * (double)y, -0.2 * (double)y);
                                matrices.method_22904(0.0, -0.2 * (double)(q /= 10.0f), -0.2 * (double)q);
                                matrices.method_22904(0.0, 0.1 * (double)this.hmiEaseInOutBack(class_3532.method_15374((double)(y * 3.14f))), 0.0);
                                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(45 * l)));
                                matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(-40 * l)));
                                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(30.0f));
                                this.hmiAltSwing(matrices, arm, swingProgress, item);
                                float c = class_3532.method_15374((double)(equipProgress * 3.14f));
                                matrices.method_22905(0.9f, 0.9f, 0.9f);
                                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(45.0f * y * (float)l));
                                this.method_3219(matrices, queue, light, 0.0f, swingProgress, arm);
                                break;
                            }
                            case field_8949: {
                                float k = (float)item.method_7935((class_1309)player) - ((float)player.method_6014() - tickDelta + 1.0f);
                                float s = k / 4.0f;
                                float s2 = k / 6.0f;
                                if (s > 1.0f) {
                                    s = 1.0f;
                                }
                                if (s2 > 1.0f) {
                                    s2 = 1.0f;
                                }
                                matrices.method_22904(0.0, -0.2, 0.0);
                                matrices.method_22904((double)(1 * l), 0.0, 0.3);
                                matrices.method_22904(0.7 * (double)s * (double)l, 0.0, -1.3 * (double)s);
                                matrices.method_22904(-0.2 * (double)l * (double)s2, 0.0, 0.0);
                                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees((float)(10.0 * Math.sin((double)s2 * 3.14))));
                                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(70.0f * s * (float)l));
                                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(45 * l)));
                                matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(-40 * l)));
                                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(30.0f));
                                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(5 * l) * s));
                                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-10.0f * s));
                                matrices.method_22904(0.0, 0.0, -0.2 * (double)s);
                                this.hmiAltSwing(matrices, arm, swingProgress, item);
                                matrices.method_22905(0.9f, 0.9f, 0.9f);
                                this.method_3219(matrices, queue, light, 0.0f, swingProgress, arm);
                                matrices.method_22904(0.35 * (double)l, -0.13, -0.12);
                                matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(10.0f * (float)l));
                                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(10.0f * (float)l));
                                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(0.0f));
                                matrices.method_22904(-0.2 * (double)l, -0.04, 0.15);
                                matrices.method_22905(1.0f, 1.0f, 1.0f);
                                break;
                            }
                            case field_8953: {
                                float g1;
                                matrices.method_22903();
                                if (player.method_6068() == class_1306.field_6182) {
                                    bl2 = !bl2;
                                }
                                float m1 = (float)item.method_7935((class_1309)player) - ((float)player.method_6014() - tickDelta + 1.0f);
                                float f1 = m1 / 20.0f;
                                float f = (f1 * f1 + f1 * 2.0f) / 3.0f;
                                if (f1 > 1.0f) {
                                    f1 = 1.0f;
                                }
                                if (f1 > 0.1f) {
                                    g1 = class_3532.method_15374((double)((m1 - 0.1f) * 1.3f));
                                    float j1 = g1 * f1;
                                    matrices.method_46416(j1 * 0.0f, j1 * 0.004f, j1 * 0.0f);
                                }
                                matrices.method_22904(bl2 ? -0.1 : 0.1, 0.0, (double)f1 * 0.15);
                                this.method_3219(matrices, queue, light, equipProgress, swingProgress, arm);
                                matrices.method_22909();
                                matrices.method_22904(bl2 ? -0.5 : 0.5, -0.45, 0.1);
                                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotation(0.3f));
                                if (bl2) {
                                    matrices.method_22907((Quaternionfc)class_7833.field_40717.rotation(-0.3f));
                                    matrices.method_22907((Quaternionfc)class_7833.field_40715.rotation(1.0f));
                                } else {
                                    matrices.method_22907((Quaternionfc)class_7833.field_40718.rotation(-0.3f));
                                    matrices.method_22907((Quaternionfc)class_7833.field_40716.rotation(1.0f));
                                }
                                this.method_3219(matrices, queue, light, equipProgress, swingProgress, arm.method_5928());
                                if (bl2) {
                                    matrices.method_22907((Quaternionfc)class_7833.field_40715.rotation(2.5f));
                                } else {
                                    matrices.method_22907((Quaternionfc)class_7833.field_40716.rotation(2.5f));
                                }
                                matrices.method_22904(bl2 ? -0.65 : 0.65, -0.35, 0.27);
                                if (f1 > 1.0f) {
                                    f1 = 1.0f;
                                }
                                matrices.method_22909();
                                if (config.mb3DCompat.getValue().booleanValue()) {
                                    matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(10 * l)));
                                }
                                matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(75.0f));
                                matrices.method_22907((Quaternionfc)class_7833.field_40717.rotationDegrees((float)(-15 * l)));
                                matrices.method_22904(0.8 * (double)l, (double)(0.0f - equipProgress * 0.3f), -0.1);
                                if (f > 0.1f) {
                                    g1 = class_3532.method_15374((double)((m1 - 0.1f) * 1.3f));
                                    float h1 = f1 - 0.1f;
                                    float j1 = g1 * h1;
                                    matrices.method_46416(j1 * 0.0f, j1 * 0.004f, j1 * 0.0f);
                                }
                                matrices.method_22903();
                                break;
                            }
                            case field_63380: {
                                float m;
                                if (player.method_6079().method_7960() && !this.hmi$isCrawling(player) && !player.method_20232() && !player.method_6101()) {
                                    matrices.method_22903();
                                    matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(-25 * l)));
                                    matrices.method_22904(-0.15 * (double)l, 0.1, 0.1);
                                    this.method_3219(matrices, queue, light, equipProgress, swingProgress, arm.method_5928());
                                    matrices.method_22909();
                                }
                                if ((f = (m = (float)item.method_7935((class_1309)player) - ((float)player.method_6014() - tickDelta + 1.0f)) / 10.0f) > 1.0f) {
                                    f = 1.0f;
                                }
                                if (f > 0.1f) {
                                    float g = class_3532.method_15374((double)((m - 0.1f) * 1.3f));
                                    h = f - 0.1f;
                                    float j = g * h;
                                    matrices.method_46416(j * 0.0f, j * 0.004f, j * 0.0f);
                                }
                                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(45.0f));
                                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(25 * l)));
                                matrices.method_22904(0.2 * (double)l, 0.0, 0.8);
                                this.method_3219(matrices, queue, light, equipProgress, swingProgress, arm);
                                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(135.0f));
                                matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(-65 * l)));
                                matrices.method_22904((double)(0.65f * (float)l), -1.0, -0.6);
                                break;
                            }
                            case field_42717: {
                                float f5 = player.method_6014() % 10;
                                float g5 = f5 - tickDelta + 1.0f;
                                float h5 = 1.0f - g5 / 10.0f;
                                float n2 = -15.0f + 75.0f * class_3532.method_15362((double)(h5 * 2.0f * (float)Math.PI));
                                float z = (float)item.method_7935((class_1309)player) - ((float)player.method_6014() - tickDelta + 1.0f);
                                float x = z / 4.0f;
                                if (x > 1.0f) {
                                    x = 1.0f;
                                }
                                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(25 * l) * x));
                                matrices.method_22904((double)(0.3f * (float)l * x), 0.3 * (double)x, 0.1 * (double)x);
                                if (x == 1.0f) {
                                    matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(n2 / 20.0f));
                                }
                                this.method_3219(matrices, queue, light, equipProgress, swingProgress, arm);
                                break;
                            }
                            case field_55494: {
                                matrices.method_22904((double)(1 * l), 0.0 - (double)equipProgress * 0.3, 0.3);
                                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(45 * l)));
                                matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(-40 * l)));
                                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(30.0f));
                                this.hmiAltSwing(matrices, arm, swingProgress, item);
                                matrices.method_22905(0.9f, 0.9f, 0.9f);
                                this.method_3219(matrices, queue, light, 0.0f, 0.0f, arm);
                                break;
                            }
                        }
                    } else if (player.method_6123() && item.method_7976() == class_1839.field_63380) {
                        this.hmi$riptideCounter = (float)((double)this.hmi$riptideCounter + 0.15 * tt);
                        float m = (float)item.method_7935((class_1309)player) - ((float)player.method_6014() - tickDelta + 1.0f);
                        f = m / 10.0f;
                        if (f > 1.0f) {
                            f = 1.0f;
                        }
                        if (f > 0.1f) {
                            float g = class_3532.method_15374((double)((m - 0.1f) * 1.3f));
                            h = f - 0.1f;
                            float j = g * h;
                            matrices.method_46416(j * 0.0f, j * 0.004f, j * 0.0f);
                        }
                        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(45.0f - this.hmi$riptideCounter * 2.0f));
                        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(25 * l)));
                        matrices.method_22904(0.2 * (double)l, 0.0, 0.75);
                        matrices.method_22904(0.0, 0.0, 0.01 * (double)class_3532.method_15374((double)(this.hmi$riptideCounter * 6.28f)));
                        this.method_3219(matrices, queue, light, equipProgress, swingProgress, arm);
                        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(135.0f));
                        matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(-65 * l)));
                        matrices.method_22904((double)(0.65f * (float)l), -1.0, -0.6);
                    } else {
                        this.hmi$riptideCounter = 0.0f;
                        if (!(item.method_31574(class_1802.field_16539) || item.method_31574(class_1802.field_22016) || item.method_31573(class_3489.field_40108))) {
                            if (item.method_7976() == class_1839.field_8949) {
                                matrices.method_22904(0.0, -0.2, 0.0);
                            }
                        } else {
                            matrices.method_22904(0.1 * (double)l, 0.0, -0.1);
                            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(10.0f));
                        }
                        matrices.method_22904((double)(1 * l), 0.0 - (double)equipProgress * 0.3, 0.3);
                        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(45 * l)));
                        matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(-40 * l)));
                        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(30.0f));
                        this.hmiAltSwing(matrices, arm, swingProgress, item);
                        matrices.method_22905(0.9f, 0.9f, 0.9f);
                        this.method_3219(matrices, queue, light, 0.0f, 0.0f, arm);
                    }
                    matrices.method_22904(-0.3 * (double)l, 0.65, -0.1);
                    matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(-65 * l)));
                    matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(10.0f));
                    if (item.method_31573(class_3489.field_15542)) {
                        matrices.method_22904(0.2 * (double)l, -0.1, 0.0);
                    }
                    if (class_2248.method_9503((class_1792)item.method_7909()) != class_2246.field_10124 && item.method_7976() != class_1839.field_8950 && !item.method_31573(ConventionalItemTags.BUCKETS)) {
                        if (item.method_7964().getString().toLowerCase().contains("torch")) {
                            matrices.method_22905(1.5f, 1.5f, 1.5f);
                            matrices.method_22907((Quaternionfc)class_7833.field_40715.rotationDegrees((float)(25 * l)));
                            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(5.0f));
                            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(75 * l)));
                            matrices.method_22904(0.2 * (double)l, 0.2, 0.05);
                        } else if ((item.method_31574(class_1802.field_8276) || item.method_31574(class_1802.field_8725) || item.method_31574(class_1802.field_8865) || item.method_31574(class_1802.field_8366) || class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(ConventionalBlockTags.GLASS_PANES) || class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(class_3481.field_15463) || class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(class_3481.field_22414) || item.method_31573(class_3489.field_15553)) && !class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(class_3481.field_15503) && !class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(class_3481.field_43170) && !class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(class_3481.field_15501)) {
                            matrices.method_22904(0.0, 0.0, -0.1);
                            matrices.method_22907((Quaternionfc)class_7833.field_40715.rotationDegrees((float)(5 * l)));
                            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(15.0f));
                            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(75 * l)));
                        } else if (!(item.method_31574(class_1802.field_16539) || item.method_31574(class_1802.field_22016) || item.method_31573(class_3489.field_40108))) {
                            matrices.method_22907((Quaternionfc)class_7833.field_40715.rotationDegrees((float)(25 * l)));
                            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(5.0f));
                            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(75 * l)));
                            matrices.method_22904(0.2 * (double)l, 0.2, 0.05);
                            if (class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(class_3481.field_15501)) {
                                matrices.method_22904(-0.2 * (double)l, 0.0, 0.0);
                                matrices.method_22905(1.1f, 1.1f, 1.1f);
                            }
                        } else {
                            float yawDelta = player.method_36454() - player.field_6241;
                            float pitchDelta = player.method_36455() - player.method_36455();
                            this.hmi$swingVelocityY += yawDelta * 0.015f * (float)tt;
                            this.hmi$swingVelocityY += swingProgress * 2.0f * (float)tt;
                            this.hmi$swingVelocityX += pitchDelta * 0.015f * (float)tt;
                            this.hmi$swingVelocityY -= 0.1f * this.hmi$swingAngleY * (float)tt;
                            this.hmi$swingVelocityX -= 0.1f * this.hmi$swingAngleX * (float)tt;
                            this.hmi$swingVelocityY = (float)((double)this.hmi$swingVelocityY * Math.pow(0.88f, tt));
                            this.hmi$swingVelocityX = (float)((double)this.hmi$swingVelocityX * Math.pow(0.88f, tt));
                            this.hmi$swingAngleY += this.hmi$swingVelocityY * (float)tt;
                            this.hmi$swingAngleX += this.hmi$swingVelocityX * (float)tt;
                            double currentSpeed = player.method_18798().method_1033();
                            this.hmi$swingVelocityZ = (float)((double)this.hmi$swingVelocityZ + (bl2 ? (currentSpeed * -1.0 * 15.0 - (double)this.hmi$swingVelocityZ) * (double)0.1f * tt : (currentSpeed * 15.0 - (double)this.hmi$swingVelocityZ) * (double)0.1f * tt));
                            if ((currentSpeed > 0.09 && player.method_24828() || player.method_20232() || player.method_6101() && !player.method_24828()) && ((Boolean)this.field_4050.field_1690.method_42448().method_41753()).booleanValue()) {
                                Random random = new Random();
                                boolean randomBoolean = random.nextBoolean();
                                this.hmi$swingVelocityY += (float)(randomBoolean ? -5.5 * currentSpeed * tt : 5.5 * currentSpeed * tt);
                            }
                            matrices.method_22904(0.0, 0.0, -0.1);
                            matrices.method_22907((Quaternionfc)class_7833.field_40715.rotationDegrees((float)(35 * l) + this.hmi$swingAngleY));
                            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(15.0f + this.hmi$swingAngleX));
                            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(75 * l) + this.hmi$swingVelocityZ));
                            if (item.method_31573(class_3489.field_40108)) {
                                matrices.method_22904(0.0, -0.1, 0.0);
                                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(-45 * l)));
                            }
                            matrices.method_22904(0.3 * (double)l, -0.35, 0.0);
                            matrices.method_22904(0.0, 0.0, 0.1);
                            matrices.method_22905(1.5f, 1.5f, 1.5f);
                        }
                    } else {
                        if (!(item.method_31573(ConventionalItemTags.TOOLS) && !item.method_31573(class_3489.field_41890) && !item.method_31573(class_3489.field_40109) && item.method_7976() != class_1839.field_8950 && item.method_7923() || item.method_7976() == class_1839.field_8953 || item.method_7976() == class_1839.field_27079 || this.hmiGetAttackDamage(item) != 0.0f || item.method_7976() == class_1839.field_8949 || item.method_31574(class_1802.field_23254) || item.method_31574(class_1802.field_8184) || item.method_31574(class_1802.field_8378) || item.method_31574(class_1802.field_8868) || item.method_31573(class_3489.field_42613) || config.mb3DCompat.getValue().booleanValue())) {
                            if (item.method_7976() == class_1839.field_42717) {
                                matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(25.0f));
                                matrices.method_22904(bl2 ? 0.0 : 0.35, bl2 ? 0.0 : 0.25, bl2 ? 0.0 : 0.37);
                                if (!bl2) {
                                    matrices.method_22905(0.75f, 0.75f, 0.75f);
                                }
                                matrices.method_22907((Quaternionfc)class_7833.field_40717.rotationDegrees((float)(-75 * l)));
                                matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(35.0f));
                                matrices.method_22904(bl2 ? -0.05 : 0.85, bl2 ? 0.0 : 0.05, bl2 ? 0.08 : -0.2);
                            } else {
                                matrices.method_22907((Quaternionfc)class_7833.field_40715.rotationDegrees((float)(5 * l)));
                                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(15.0f));
                                matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(75 * l)));
                                matrices.method_22904(0.0, -0.05, -0.1);
                                matrices.method_22905(0.7f, 0.7f, 0.7f);
                            }
                            if (item.method_31574(class_1802.field_8153) || item.method_31574(class_1802.field_8777) || item.method_31574(class_1802.field_8323)) {
                                this.hmi$vertVelocityYSlime = (float)((double)this.hmi$vertVelocityYSlime + (double)swingProgress * 0.03 * (double)((float)tt));
                                if ((player.method_18798().method_1033() > 0.09 && player.method_24828() || player.method_20232() || this.hmi$isCrawling(player) || player.method_6101() && !player.method_24828()) && ((Boolean)this.field_4050.field_1690.method_42448().method_41753()).booleanValue()) {
                                    Random random = new Random();
                                    boolean randomBoolean = random.nextBoolean();
                                    this.hmi$vertVelocityYSlime += (float)(-0.05 * player.method_18798().method_1033() * (double)((float)tt));
                                }
                                matrices.method_22905(1.0f, 1.0f + this.hmi$vertAngleYSlime * -2.0f, 1.0f);
                            }
                        } else if (item.method_7976() == class_1839.field_8949 && item.method_7976() != class_1839.field_63380) {
                            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(160 * l)));
                            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(-60 * l)));
                            matrices.method_22907((Quaternionfc)class_7833.field_40713.rotationDegrees(-70.0f));
                            matrices.method_22905(0.75f, 0.75f, 0.75f);
                            matrices.method_22904(0.15 * (double)l, bl2 ? 0.35 : 0.45, bl2 ? -0.15 : -0.1);
                            matrices.method_22904(0.17 * (double)l, 0.0, 0.3);
                            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(-90 * l)));
                        } else if (item.method_7976() == class_1839.field_63380) {
                            matrices.method_22907((Quaternionfc)class_7833.field_40715.rotationDegrees((float)(75 * l)));
                            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(90.0f));
                            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(45 * l)));
                            matrices.method_46416(-0.3f * (float)l, 0.0f, 0.0f);
                        } else {
                            matrices.method_22907((Quaternionfc)class_7833.field_40715.rotationDegrees((float)(75 * l)));
                            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(70.0f));
                            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(45 * l)));
                        }
                        if (item.method_7976() != class_1839.field_8949) {
                            matrices.method_22905(1.2f, 1.2f, 1.2f);
                        }
                        if (item.method_7976() == class_1839.field_8953 && !player.method_6115()) {
                            matrices.method_22904(-0.1 * (double)l, -0.2, 0.0);
                        }
                        if (item.method_31574(class_1802.field_49814)) {
                            if (config.mb3DCompat.getValue().booleanValue()) {
                                matrices.method_22904(-0.08, 0.17, 0.0);
                                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(40.0f));
                            }
                            matrices.method_22904(0.1 * (double)l, 0.0, 0.0);
                            matrices.method_22905(0.9f, 0.9f, 0.9f);
                        }
                    }
                    if (!(!(item.method_7909() instanceof class_1747) || (item.method_31573(ConventionalItemTags.BUCKETS) || item.method_7976() == class_1839.field_8950 || item.method_31573(class_3489.field_15556) || item.method_31574(class_1802.field_8276) || item.method_31574(class_1802.field_8725) || item.method_31574(class_1802.field_8865) || item.method_31574(class_1802.field_8366) || class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(ConventionalBlockTags.GLASS_PANES) || class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(class_3481.field_15463) || class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(class_3481.field_22414) || item.method_31573(class_3489.field_15553)) && !class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(class_3481.field_15503) || class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(class_3481.field_43170))) {
                        class_1747 blockItem = (class_1747)item.method_7909();
                        class_776 blockRenderDispatcher = class_310.method_1551().method_1541();
                        matrices.method_22903();
                        if (!bl23) {
                            matrices.method_46416(-0.4f, 0.0f, 0.0f);
                        }
                        matrices.method_22905(0.4f, 0.4f, 0.4f);
                        matrices.method_22904(-0.9 * (double)l, -0.45, -0.5);
                        if (class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(class_3481.field_15493)) {
                            matrices.method_22904(0.2 * (double)l, -0.15, -0.2);
                        }
                        if (class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(class_3481.field_24076)) {
                            matrices.method_22904(0.0, 0.1, 0.0);
                        }
                        if (item.method_31574(class_1802.field_8828) || item.method_31574(class_1802.field_21086) || class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(class_3481.field_20339) || class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(class_3481.field_15503) || class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(class_3481.field_15462) || class_2248.method_9503((class_1792)item.method_7909()).method_9564().method_26164(class_3481.field_44469)) {
                            this.hmi$vertVelocityYSlime = (float)((double)this.hmi$vertVelocityYSlime + (double)swingProgress * 0.03 * (double)((float)tt));
                            if ((player.method_18798().method_1033() > 0.09 && player.method_24828() || player.method_20232() || this.hmi$isCrawling(player) || player.method_6101() && !player.method_24828()) && ((Boolean)this.field_4050.field_1690.method_42448().method_41753()).booleanValue()) {
                                Random random = new Random();
                                boolean randomBoolean = random.nextBoolean();
                                this.hmi$vertVelocityYSlime += (float)(-0.05 * player.method_18798().method_1033() * (double)((float)tt));
                            }
                            matrices.method_22905(1.0f, 1.0f + this.hmi$vertAngleYSlime * -2.0f, 1.0f);
                        }
                        class_2680 blockState = blockItem.method_7711().method_9564();
                        if ((float)player.field_6012 - this.hmi$prevAge >= 100.0f) {
                            this.hmi$repPower = !this.hmi$repPower;
                            this.hmi$prevAge = player.field_6012;
                        }
                        if (blockItem.method_7711() == class_2246.field_10450 && this.hmi$repPower) {
                            blockState = (class_2680)blockState.method_11657((class_2769)class_2312.field_10911, (Comparable)Boolean.valueOf(true));
                        }
                        if (blockItem.method_7711() == class_2246.field_10377 && this.hmi$repPower) {
                            blockState = (class_2680)blockState.method_11657((class_2769)class_2312.field_10911, (Comparable)Boolean.valueOf(true));
                        }
                        if (blockItem.method_7711() == class_2246.field_10523 && player.method_5777(class_3486.field_15517)) {
                            blockState = (class_2680)blockState.method_11657((class_2769)class_2459.field_11446, (Comparable)Boolean.valueOf(false));
                        }
                        if ((blockItem.method_7711() == class_2246.field_17350 || blockItem.method_7711() == class_2246.field_23860) && player.method_5777(class_3486.field_15517)) {
                            blockState = (class_2680)blockState.method_11657((class_2769)class_3922.field_17352, (Comparable)Boolean.valueOf(false));
                        }
                        if (item.method_31573(class_3489.field_16444)) {
                            if (bl2) {
                                matrices.method_22904(0.9, 0.0, 0.8);
                            }
                            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(90 * l)));
                        }
                        blockRenderDispatcher.method_3353(blockState, matrices, (class_4597)class_310.method_1551().method_22940().method_23000(), light, class_4608.field_21444);
                        class_2248 renderBlock = class_2248.method_9503((class_1792)item.method_7909());
                        if (renderBlock instanceof class_2343) {
                            class_2343 blockEntityProvider = (class_2343)renderBlock;
                            try {
                                class_2338 playerPos = player.method_24515();
                                class_2586 tempBE = blockEntityProvider.method_10123(playerPos, blockState);
                                if (tempBE != null) {
                                    tempBE.method_31662((class_1937)class_310.method_1551().field_1687);
                                    tempBE.method_31664(blockState);
                                    class_824 beDispatcher = class_310.method_1551().method_31975();
                                    beDispatcher.method_3549(class_310.method_1551().field_1773.method_19418());
                                    class_11954 beState = beDispatcher.method_74348(tempBE, class_310.method_1551().method_61966().method_60637(false), null);
                                    if (beState != null) {
                                        class_12075 cameraState = new class_12075();
                                        class_4184 camera = class_310.method_1551().field_1773.method_19418();
                                        cameraState.field_63079 = true;
                                        cameraState.field_63078 = camera.method_71156();
                                        cameraState.field_63077 = class_2338.method_49638((class_2374)camera.method_71156());
                                        cameraState.field_63080 = camera.method_71156();
                                        cameraState.field_63081 = camera.method_23767();
                                        beDispatcher.method_3555(beState, matrices, queue, cameraState);
                                    }
                                }
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                        }
                        matrices.method_22909();
                    } else {
                        if (item.method_31573(ConventionalItemTags.TOOLS) && !item.method_31573(class_3489.field_41890) && !item.method_31573(class_3489.field_40109) && item.method_7976() != class_1839.field_8950 && item.method_7923() || item.method_7976() == class_1839.field_8953 || item.method_7976() == class_1839.field_27079 || this.hmiGetAttackDamage(item) != 0.0f || item.method_7976() == class_1839.field_8949 || item.method_31574(class_1802.field_23254) || item.method_31574(class_1802.field_8184) || item.method_31574(class_1802.field_8378) || item.method_31574(class_1802.field_8868)) {
                            if (item.method_31573(class_3489.field_42611) && !sharpSword) {
                                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-60.0f * swing));
                                matrices.method_22904(0.0, 0.1 * (double)swing, -0.1 * (double)swing);
                            }
                            if (item.method_31573(class_3489.field_42615)) {
                                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-80.0f * swing_rot));
                                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(30.0f * swing));
                            } else if (item.method_7976() == class_1839.field_63380) {
                                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-40.0f * swing_rot));
                                matrices.method_22904(0.0, 0.1 * (double)swing_rot, -0.1 * (double)swing_rot);
                            } else if (item.method_7976() != class_1839.field_8949) {
                                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-25.0f * swing));
                                matrices.method_22904(0.0, 0.05 * (double)swing, -0.05 * (double)swing);
                            }
                        }
                        if (!(item.method_31574(class_1802.field_8137) || item.method_31574(class_1802.field_8301) && config.mb3DCompat.getValue().booleanValue())) {
                            this.hmi$netherCounter = 0.0f;
                        } else {
                            this.hmi$netherCounter = (float)((double)this.hmi$netherCounter + 0.9 * tt);
                            matrices.method_22904(0.0, 0.25 + 0.02 * (double)class_3532.method_15374((double)(this.hmi$netherCounter * 0.1f)), 0.0);
                            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(3.0f * class_3532.method_15374((double)(this.hmi$netherCounter * 0.2f))));
                            matrices.method_22905(1.0f + 0.01f * class_3532.method_15374((double)this.hmi$netherCounter), 1.0f + 0.01f * class_3532.method_15374((double)this.hmi$netherCounter), 1.0f + 0.01f * class_3532.method_15374((double)this.hmi$netherCounter));
                        }
                        if (config.mb3DCompat.getValue().booleanValue()) {
                            if (item.method_31573(class_3489.field_42611)) {
                                matrices.method_22904(0.0, 0.2, 0.0);
                            }
                            if (item.method_31574(class_1802.field_8153) || item.method_31574(class_1802.field_8777) || item.method_31574(class_1802.field_8323)) {
                                this.hmi$vertVelocityYSlime = (float)((double)this.hmi$vertVelocityYSlime + (double)swingProgress * 0.03 * (double)((float)tt));
                                if ((player.method_18798().method_1033() > 0.09 && player.method_24828() || player.method_20232() || this.hmi$isCrawling(player) || player.method_6101() && !player.method_24828()) && ((Boolean)this.field_4050.field_1690.method_42448().method_41753()).booleanValue()) {
                                    Random random = new Random();
                                    boolean randomBoolean = random.nextBoolean();
                                    this.hmi$vertVelocityYSlime += (float)(-0.05 * player.method_18798().method_1033() * (double)((float)tt));
                                }
                                matrices.method_22905(1.0f, 1.0f + this.hmi$vertAngleYSlime * -2.0f, 1.0f);
                            }
                        }
                        if (item.method_31573(class_3489.field_42615)) {
                            matrices.method_22904(0.07 * (double)l, 0.0, 0.05);
                            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(90 * l)));
                            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-15.0f));
                        }
                        if (item.method_31574(class_1802.field_8810)) {
                            // empty if block
                        }
                        this.method_3233((class_1309)player, item, bl23 ? class_811.field_4322 : class_811.field_4321, matrices, queue, light);
                    }
                }
                matrices.method_22909();
                matrices.method_22909();
                this.hmi$isAttacking = this.field_4050.field_1690.field_1886.method_1434();
            }
        }
    }

    @WrapOperation(method={"method_22976"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_759;method_3228(Lnet/minecraft/class_742;FFLnet/minecraft/class_1268;FLnet/minecraft/class_1799;FLnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V")}, require=0)
    private void hmi$handRenderHook(class_759 instance, class_742 player, float tickDelta, float pitch, class_1268 hand, float swingProgress, class_1799 item, float equipProgress, class_4587 matrices, class_11659 queue, int light, Operation<Void> original) {
        HoldMyItems hmi = HoldMyItems.getInstance();
        if (hmi != null && hmi.isEnabled()) {
            float sideFactor;
            class_1306 arm = hand == class_1268.field_5808 ? player.method_6068() : player.method_6068().method_5928();
            float f = sideFactor = hand == class_1268.field_5808 ? 1.0f : -1.0f;
            if (hmi.swapHands.getValue().booleanValue()) {
                arm = arm.method_5928();
                sideFactor *= -1.0f;
            }
            float hmiSwingProgress = hand == class_1268.field_5808 ? swingProgress : 0.0f;
            this.hmiRenderCustomFirstPersonItem(player, tickDelta, pitch, hand, arm, sideFactor, hmiSwingProgress, item, 0.0f, matrices, queue, light);
            return;
        }
        original.call(new Object[]{instance, player, Float.valueOf(tickDelta), Float.valueOf(pitch), hand, Float.valueOf(swingProgress), item, Float.valueOf(equipProgress), matrices, queue, light});
    }
}


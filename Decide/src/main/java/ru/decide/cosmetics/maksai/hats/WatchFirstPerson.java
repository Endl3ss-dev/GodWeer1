/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1007
 *  net.minecraft.class_11659
 *  net.minecraft.class_12249
 *  net.minecraft.class_1306
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_4608
 *  net.minecraft.class_583
 *  net.minecraft.class_591
 *  net.minecraft.class_746
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.cosmetics.maksai.hats;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1007;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_1306;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_583;
import net.minecraft.class_591;
import net.minecraft.class_746;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.cosmetics.maksai.hats.HatManager;
import ru.decide.cosmetics.maksai.hats.WatchHands;

@Environment(value=EnvType.CLIENT)
public final class WatchFirstPerson {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:Watch");
    private static volatile boolean logged;

    private WatchFirstPerson() {
    }

    private static HatManager.Equipped equippedWrist() {
        for (HatManager.Equipped equipped : HatManager.allEquipped()) {
            String attachment = equipped.entry().attachment();
            if (!"RIGHT_ARM".equals(attachment) && !"LEFT_ARM".equals(attachment)) continue;
            return equipped;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void render(class_1007 renderer, class_4587 matrices, class_11659 queue, int light, boolean leftArm) {
        boolean mainIsLeft;
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (player == null || client.field_1690.method_31044() == null || !client.field_1690.method_31044().method_31034()) {
            return;
        }
        boolean bl = mainIsLeft = player.method_6068() == class_1306.field_6182;
        if (leftArm != mainIsLeft) {
            return;
        }
        HatManager.Equipped watch = WatchFirstPerson.equippedWrist();
        if (watch == null || watch.entry().texture() == null) {
            return;
        }
        float[] mesh = watch.vertices();
        if (mesh == null || mesh.length < 32) {
            return;
        }
        class_583 class_5832 = renderer.method_4038();
        if (!(class_5832 instanceof class_591)) {
            return;
        }
        class_591 playerModel = (class_591)class_5832;
        class_591 model = playerModel;
        matrices.method_22903();
        try {
            if (!logged) {
                logged = true;
                LOGGER.info("[watch] \u043f\u0435\u0440\u0432\u043e\u0435 \u043b\u0438\u0446\u043e: \u0440\u0438\u0441\u0443\u0435\u043c '{}' ({} \u0432\u0435\u0440\u0448\u0438\u043d)", (Object)watch.entry().name(), (Object)(mesh.length / 8));
            }
            if (leftArm) {
                model.field_27433.method_22703(matrices);
                matrices.method_22905(-1.0f, 1.0f, 1.0f);
            } else {
                model.field_3401.method_22703(matrices);
            }
            float[] frame = WatchHands.frame(watch);
            int[] bones = watch.boneIndices();
            queue.method_73483(matrices, class_12249.method_75994((class_2960)watch.entry().texture()), (pose, vertices) -> {
                int i = 0;
                while (i + 7 < mesh.length) {
                    int k;
                    float x = mesh[i];
                    float y = mesh[i + 1];
                    float z = mesh[i + 2];
                    float nx = mesh[i + 5];
                    float ny = mesh[i + 6];
                    float nz = mesh[i + 7];
                    if (frame != null && bones != null && bones.length > i / 8 && (k = bones[i / 8] * 12) >= 0 && k + 11 < frame.length) {
                        float px = frame[k] * x + frame[k + 1] * y + frame[k + 2] * z + frame[k + 3];
                        float py = frame[k + 4] * x + frame[k + 5] * y + frame[k + 6] * z + frame[k + 7];
                        float pz = frame[k + 8] * x + frame[k + 9] * y + frame[k + 10] * z + frame[k + 11];
                        float qx = frame[k] * nx + frame[k + 1] * ny + frame[k + 2] * nz;
                        float qy = frame[k + 4] * nx + frame[k + 5] * ny + frame[k + 6] * nz;
                        float qz = frame[k + 8] * nx + frame[k + 9] * ny + frame[k + 10] * nz;
                        float length = (float)Math.sqrt(qx * qx + qy * qy + qz * qz);
                        x = px;
                        y = py;
                        z = pz;
                        if (length > 1.0E-6f) {
                            nx = qx / length;
                            ny = qy / length;
                            nz = qz / length;
                        }
                    }
                    vertices.method_56824(pose, x, y, z).method_39415(-1).method_22913(mesh[i + 3], mesh[i + 4]).method_22922(class_4608.field_21444).method_60803(light).method_60831(pose, nx, ny, nz);
                    i += 8;
                }
            });
        }
        finally {
            matrices.method_22909();
        }
    }
}


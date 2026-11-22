/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10055
 *  net.minecraft.class_11659
 *  net.minecraft.class_12249
 *  net.minecraft.class_1304
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3883
 *  net.minecraft.class_3887
 *  net.minecraft.class_4587
 *  net.minecraft.class_4587$class_4665
 *  net.minecraft.class_4588
 *  net.minecraft.class_4608
 *  net.minecraft.class_591
 *  net.minecraft.class_746
 */
package ru.decide.cosmetics.maksai.hats;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_1304;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3883;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4608;
import net.minecraft.class_591;
import net.minecraft.class_746;
import ru.decide.cosmetics.maksai.hats.HatManager;
import ru.decide.cosmetics.maksai.hats.LivePetState;
import ru.decide.cosmetics.maksai.hats.TextureUvAnimation;
import ru.decide.cosmetics.maksai.hats.WatchHands;

@Environment(value=EnvType.CLIENT)
public final class HatLayer
extends class_3887<class_10055, class_591> {
    private static final float BANDANNA_SCALE = 0.7f;
    private static final float BANDANNA_GROW_PX = 1.5f;
    private static final float BANDANNA_DROP_PX = 1.0f;

    public HatLayer(class_3883<class_10055, class_591> parent) {
        super(parent);
    }

    private static float renderScale(String category) {
        return "bandanna".equals(category) ? 0.7f : 1.0f;
    }

    static void applyAttachment(class_4587 poses, class_591 model, String attachment) {
        switch (attachment == null ? "" : attachment) {
            case "HEAD": {
                model.field_3398.method_22703(poses);
                break;
            }
            case "RIGHT_ARM": {
                model.field_3401.method_22703(poses);
                break;
            }
            case "LEFT_ARM": {
                model.field_27433.method_22703(poses);
                poses.method_22905(-1.0f, 1.0f, 1.0f);
                break;
            }
            default: {
                model.field_3391.method_22703(poses);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void render(class_4587 poses, class_11659 queue, int light, class_10055 state, float limbAngle, float limbDistance) {
        class_746 player = class_310.method_1551().field_1724;
        if (player != null && state.field_53528 == player.method_5628() && !state.field_53333 && !state.field_53542) {
            class_591 model = (class_591)this.method_17165();
            LivePetState.captureLocalPose(model.field_3391.field_3654, model.field_3391.field_3675, model.field_27433.field_3654, model.field_3401.field_3654, model.field_27433.field_3675, model.field_3401.field_3675);
            for (HatManager.Equipped cosmetic : HatManager.allEquipped()) {
                Object frame;
                if (cosmetic.entry().category().equals("companion")) continue;
                if (cosmetic.bindings() != null) {
                    cosmetic.bindings().submit(poses, queue, light, model, cosmetic);
                    continue;
                }
                if (HatManager.hideHeadwearWithHelmet() && cosmetic.entry().attachment().equals("HEAD") && !player.method_6118(class_1304.field_6169).method_7960()) continue;
                float[] mesh = cosmetic.vertices();
                int[] bones = cosmetic.boneIndices();
                float[][] frames = cosmetic.frames();
                if (cosmetic.petMotion() != null && !cosmetic.petMotion().failed()) {
                    frame = cosmetic.petMotion().frame();
                } else if (cosmetic.animation() != null) {
                    frame = cosmetic.animation().pose((double)(System.nanoTime() - cosmetic.started()) * 1.0E-9);
                } else if (frames.length > 0) {
                    double clock = (double)(System.nanoTime() - cosmetic.started()) * 1.0E-9 * (double)cosmetic.fps();
                    double wrapped = cosmetic.loop() ? clock % (double)frames.length : Math.min(clock, (double)(frames.length - 1));
                    int index = (int)wrapped;
                    float weight = (float)(wrapped - (double)index);
                    float[] a = frames[index];
                    float[] b = frames[cosmetic.loop() ? (index + 1) % frames.length : Math.min(index + 1, frames.length - 1)];
                    frame = new float[a.length];
                    for (int i = 0; i < ((float[])frame).length; ++i) {
                        frame[i] = a[i] + (b[i] - a[i]) * weight;
                    }
                } else {
                    frame = WatchHands.has(cosmetic) ? WatchHands.frame(cosmetic) : null;
                }
                TextureUvAnimation.Uv uv = cosmetic.uvAnimation() == null ? null : cosmetic.uvAnimation().at((double)(System.nanoTime() - cosmetic.started()) * 1.0E-9);
                poses.method_22903();
                try {
                    HatLayer.applyAttachment(poses, (class_591)this.method_17165(), cosmetic.entry().attachment());
                    float renderScale = HatLayer.renderScale(cosmetic.entry().category());
                    if (renderScale != 1.0f && mesh.length >= 8) {
                        float minX = Float.MAX_VALUE;
                        float minY = Float.MAX_VALUE;
                        float minZ = Float.MAX_VALUE;
                        float maxX = -3.4028235E38f;
                        float maxY = -3.4028235E38f;
                        float maxZ = -3.4028235E38f;
                        for (int ix = 0; ix < mesh.length; ix += 8) {
                            float mx = mesh[ix];
                            float my = mesh[ix + 1];
                            float mz = mesh[ix + 2];
                            if (mx < minX) {
                                minX = mx;
                            }
                            if (mx > maxX) {
                                maxX = mx;
                            }
                            if (my < minY) {
                                minY = my;
                            }
                            if (my > maxY) {
                                maxY = my;
                            }
                            if (mz < minZ) {
                                minZ = mz;
                            }
                            if (!(mz > maxZ)) continue;
                            maxZ = mz;
                        }
                        float cx = (minX + maxX) * 0.5f;
                        float cy = (minY + maxY) * 0.5f;
                        float cz = (minZ + maxZ) * 0.5f;
                        poses.method_46416(0.0f, 0.0625f, 0.0f);
                        float grow = 0.09375f;
                        float sx = renderScale + (maxX - minX > 1.0E-6f ? grow / (maxX - minX) : 0.0f);
                        float sy = renderScale + (maxY - minY > 1.0E-6f ? grow / (maxY - minY) : 0.0f);
                        float sz = renderScale + (maxZ - minZ > 1.0E-6f ? grow / (maxZ - minZ) : 0.0f);
                        poses.method_46416(cx, cy, cz);
                        poses.method_22905(sx, sy, sz);
                        poses.method_46416(-cx, -cy, -cz);
                    }
                    queue.method_73483(poses, class_12249.method_75994((class_2960)cosmetic.entry().texture()), (arg_0, arg_1) -> HatLayer.lambda$render$0(mesh, (float[])frame, bones, uv, light, arg_0, arg_1));
                }
                finally {
                    poses.method_22909();
                }
            }
        }
    }

    private static /* synthetic */ void lambda$render$0(float[] mesh, float[] frame, int[] bones, TextureUvAnimation.Uv uv, int light, class_4587.class_4665 pose, class_4588 vertices) {
        for (int ix = 0; ix < mesh.length; ix += 8) {
            float x = mesh[ix];
            float y = mesh[ix + 1];
            float z = mesh[ix + 2];
            float nx = mesh[ix + 5];
            float ny = mesh[ix + 6];
            float nz = mesh[ix + 7];
            if (frame != null) {
                int k = bones[ix / 8] * 12;
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
            float u = mesh[ix + 3];
            float v = mesh[ix + 4];
            if (uv != null) {
                u = uv.u(u);
                v = uv.v(v);
            }
            vertices.method_56824(pose, x, y, z).method_39415(-1).method_22913(u, v).method_22922(class_4608.field_21444).method_60803(light).method_60831(pose, nx, ny, nz);
        }
    }
}


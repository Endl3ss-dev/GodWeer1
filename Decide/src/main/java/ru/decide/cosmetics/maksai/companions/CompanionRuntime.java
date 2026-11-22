/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
 *  net.minecraft.class_12249
 *  net.minecraft.class_1944
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_4587$class_4665
 *  net.minecraft.class_4588
 *  net.minecraft.class_4608
 *  net.minecraft.class_746
 *  net.minecraft.class_7833
 *  org.joml.Quaternionfc
 */
package ru.decide.cosmetics.maksai.companions;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.class_12249;
import net.minecraft.class_1944;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4608;
import net.minecraft.class_746;
import net.minecraft.class_7833;
import org.joml.Quaternionfc;
import ru.decide.cosmetics.maksai.MaksaiCosmetics;
import ru.decide.cosmetics.maksai.companions.FollowMotion;
import ru.decide.cosmetics.maksai.hats.HatManager;
import ru.decide.cosmetics.maksai.hats.LivePetState;
import ru.decide.cosmetics.maksai.hats.TextureUvAnimation;

@Environment(value=EnvType.CLIENT)
public final class CompanionRuntime {
    private static FollowMotion actor = new FollowMotion();
    private static Object world;
    private static int id;
    private static int light;
    private static HatManager.Equipped item;
    private static double width;
    private static double height;
    private static boolean failed;
    private static long lastTick;

    public static void initialize() {
        WorldRenderEvents.AFTER_ENTITIES.register(CompanionRuntime::render);
    }

    public static void tick() {
        final class_310 mc = class_310.method_1551();
        HatManager.Equipped selected = HatManager.equipped("companion");
        if (mc.field_1724 == null || mc.field_1687 == null || !mc.field_1724.method_5805() || selected == null) {
            actor.reset();
            item = null;
            id = -1;
            world = null;
        } else if (!mc.method_1493()) {
            if (world != mc.field_1687 || id != selected.entry().id()) {
                actor.reset();
                world = mc.field_1687;
                id = selected.entry().id();
                failed = false;
                width = 0.8;
                height = 0.9;
                try (InputStream s = CompanionRuntime.class.getResourceAsStream("/assets/decide/cosmetics/hats/" + id + "/companion.json");){
                    if (s != null) {
                        JsonObject j = JsonParser.parseReader((Reader)new InputStreamReader(s, StandardCharsets.UTF_8)).getAsJsonObject();
                        if (j.has("hitbox_width")) {
                            width = Math.max(0.2, Math.min(3.0, j.get("hitbox_width").getAsDouble()));
                        }
                        if (j.has("hitbox_height")) {
                            height = Math.max(0.2, Math.min(4.0, j.get("hitbox_height").getAsDouble()));
                        }
                    }
                }
                catch (Exception var9) {
                    MaksaiCosmetics.LOGGER.warn("Companion metadata", (Throwable)var9);
                }
            }
            if (!failed) {
                try {
                    class_746 p = mc.field_1724;
                    FollowMotion.Ground ground = new FollowMotion.Ground(){

                        @Override
                        public boolean clear(double x, double y, double z) {
                            return mc.field_1687.method_18026(new class_238(x - width / 2.0, y + 0.003, z - width / 2.0, x + width / 2.0, y + height, z + width / 2.0));
                        }

                        @Override
                        public double floor(double x, double y, double z) {
                            for (double yy = y + 1.5; yy >= y - 3.0; yy -= 0.125) {
                                if (!this.clear(x, yy, z) || mc.field_1687.method_18026(new class_238(x - width / 2.0, yy - 0.13, z - width / 2.0, x + width / 2.0, yy - 0.003, z + width / 2.0))) continue;
                                double lo = yy - 0.13;
                                double hi = yy;
                                for (int k = 0; k < 10; ++k) {
                                    double mid = (lo + hi) * 0.5;
                                    if (this.clear(x, mid, z)) {
                                        hi = mid;
                                        continue;
                                    }
                                    lo = mid;
                                }
                                return hi + 0.003;
                            }
                            return Double.NaN;
                        }
                    };
                    actor.tick(p.method_23317(), p.method_23318(), p.method_23321(), p.field_6283, ground);
                    if (!actor.ready()) {
                        item = null;
                        return;
                    }
                    HashMap<String, Double> inputs = new HashMap<String, Double>(LivePetState.snapshot());
                    inputs.put("q.ground_speed", CompanionRuntime.actor.speed);
                    inputs.put("q.is_moving", CompanionRuntime.actor.speed > 0.05 ? 1.0 : 0.0);
                    inputs.put("q.is_on_ground", 1.0);
                    inputs.put("q.is_elytra_flying", 0.0);
                    inputs.put("q.is_flying", 0.0);
                    inputs.put("q.body_x_rotation", 0.0);
                    inputs.put("q.body_y_rotation", 0.0);
                    inputs.put("q.head_x_rotation", 0.0);
                    inputs.put("q.head_y_rotation", 0.0);
                    inputs.put("q.owner_distance", Math.hypot(CompanionRuntime.actor.x - p.method_23317(), CompanionRuntime.actor.z - p.method_23321()));
                    inputs.put("q.local_z_velocity", CompanionRuntime.actor.speed);
                    inputs.put("c.lvs_z", CompanionRuntime.actor.speed);
                    if (selected.petMotion() != null) {
                        selected.petMotion().tick(inputs, 0.05);
                    }
                    class_2338 pos = class_2338.method_49637((double)CompanionRuntime.actor.x, (double)CompanionRuntime.actor.y, (double)CompanionRuntime.actor.z);
                    light = mc.field_1687.method_8314(class_1944.field_9282, pos) << 4 | mc.field_1687.method_8314(class_1944.field_9284, pos) << 20;
                    item = selected;
                    lastTick = System.nanoTime();
                }
                catch (RuntimeException var6) {
                    failed = true;
                    item = null;
                    MaksaiCosmetics.LOGGER.warn("Companion safely stopped", (Throwable)var6);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void render(WorldRenderContext context) {
        if (item != null && !failed && actor.ready()) {
            class_243 camera = context.worldState().field_63082.field_63078;
            double f = Math.max(0.0, Math.min(1.0, (double)(System.nanoTime() - lastTick) * 1.0E-9 / 0.05));
            double x = CompanionRuntime.actor.previousX + (CompanionRuntime.actor.x - CompanionRuntime.actor.previousX) * f;
            double y = CompanionRuntime.actor.previousY + (CompanionRuntime.actor.y - CompanionRuntime.actor.previousY) * f;
            double z = CompanionRuntime.actor.previousZ + (CompanionRuntime.actor.z - CompanionRuntime.actor.previousZ) * f;
            if (!((x - camera.field_1352) * (x - camera.field_1352) + (z - camera.field_1350) * (z - camera.field_1350) > 4096.0)) {
                class_4587 stack = context.matrices();
                stack.method_22903();
                try {
                    stack.method_22904(x - camera.field_1352, y - camera.field_1351, z - camera.field_1350);
                    stack.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(180.0 - CompanionRuntime.actor.yaw)));
                    stack.method_22905(-1.0f, -1.0f, 1.0f);
                    stack.method_22904(0.0, -1.5, 0.0);
                    class_4587.class_4665 pose = stack.method_23760();
                    class_4588 vertex = context.consumers().method_73477(class_12249.method_75994((class_2960)item.entry().texture()));
                    float[] mesh = item.vertices();
                    float[] frame = item.petMotion() != null && !item.petMotion().failed() ? item.petMotion().frame() : null;
                    int[] bones = item.boneIndices();
                    TextureUvAnimation.Uv uv = item.uvAnimation() == null ? null : item.uvAnimation().at((double)(System.nanoTime() - item.started()) * 1.0E-9);
                    int n = 0;
                    for (int i = 0; i < mesh.length; i += 8) {
                        float len;
                        float px = mesh[i];
                        float py = mesh[i + 1];
                        float pz = mesh[i + 2];
                        float nx = mesh[i + 5];
                        float ny = mesh[i + 6];
                        float nz = mesh[i + 7];
                        if (frame != null) {
                            int k = bones[n] * 12;
                            float X = frame[k] * px + frame[k + 1] * py + frame[k + 2] * pz + frame[k + 3];
                            float Y = frame[k + 4] * px + frame[k + 5] * py + frame[k + 6] * pz + frame[k + 7];
                            float Z = frame[k + 8] * px + frame[k + 9] * py + frame[k + 10] * pz + frame[k + 11];
                            float N = frame[k] * nx + frame[k + 1] * ny + frame[k + 2] * nz;
                            float M = frame[k + 4] * nx + frame[k + 5] * ny + frame[k + 6] * nz;
                            float L = frame[k + 8] * nx + frame[k + 9] * ny + frame[k + 10] * nz;
                            px = X;
                            py = Y;
                            pz = Z;
                            nx = N;
                            ny = M;
                            nz = L;
                        }
                        if ((double)(len = (float)Math.sqrt(nx * nx + ny * ny + nz * nz)) > 1.0E-8) {
                            nx /= len;
                            ny /= len;
                            nz /= len;
                        }
                        float u = mesh[i + 3];
                        float v = mesh[i + 4];
                        if (uv != null) {
                            u = uv.u(u);
                            v = uv.v(v);
                        }
                        vertex.method_56824(pose, px, py, pz).method_39415(-1).method_22913(u, v).method_22922(class_4608.field_21444).method_60803(light).method_60831(pose, nx, ny, nz);
                        ++n;
                    }
                }
                catch (RuntimeException var35) {
                    failed = true;
                    MaksaiCosmetics.LOGGER.warn("Companion render safely stopped", (Throwable)var35);
                }
                finally {
                    stack.method_22909();
                }
            }
        }
    }

    static {
        id = -1;
        width = 0.8;
        height = 0.9;
    }
}


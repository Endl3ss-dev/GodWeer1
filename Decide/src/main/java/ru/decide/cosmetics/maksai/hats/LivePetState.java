/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1304
 *  net.minecraft.class_1937
 *  net.minecraft.class_310
 *  net.minecraft.class_3486
 *  net.minecraft.class_746
 */
package ru.decide.cosmetics.maksai.hats;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1304;
import net.minecraft.class_1937;
import net.minecraft.class_310;
import net.minecraft.class_3486;
import net.minecraft.class_746;
import ru.decide.cosmetics.maksai.hats.Molang;

@Environment(value=EnvType.CLIENT)
public final class LivePetState {
    static Map<String, Double> values = new HashMap<String, Double>();
    static int id = -1;
    static double x;
    static double y;
    static double z;
    static double yaw;
    static double pitch;
    static double afk;
    static double distance;
    static double sx;
    static double sy;
    static double sz;
    static float localBodyX;
    static float localBodyY;
    static float leftArmX;
    static float rightArmX;
    static float leftArmY;
    static float rightArmY;

    public static void captureLocalPose(float bx, float by, float lx, float rx, float ly, float ry) {
        localBodyX = bx;
        localBodyY = by;
        leftArmX = lx;
        rightArmX = rx;
        leftArmY = ly;
        rightArmY = ry;
    }

    static void put(String k, double v) {
        values.put(k, v);
    }

    public static Map<String, Double> snapshot() {
        return Map.copyOf(values);
    }

    public static boolean tick() {
        int depth;
        double dz;
        double dy;
        double dx;
        class_310 mc = class_310.method_1551();
        class_746 p = mc.field_1724;
        if (p == null || mc.field_1687 == null) {
            id = -1;
            values.clear();
            return false;
        }
        if (mc.method_1493()) {
            return false;
        }
        if (id != p.method_5628()) {
            id = p.method_5628();
            x = p.method_23317();
            y = p.method_23318();
            z = p.method_23321();
            yaw = p.method_36454();
            pitch = p.method_36455();
            sz = 0.0;
            sy = 0.0;
            sx = 0.0;
            distance = 0.0;
            afk = 0.0;
            LivePetState.captureLocalPose(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        }
        if ((dx = p.method_23317() - x) * dx + (dy = p.method_23318() - y) * dy + (dz = p.method_23321() - z) * dz > 16.0) {
            dz = 0.0;
            dy = 0.0;
            dx = 0.0;
            afk = 0.0;
        }
        double angle = Math.toRadians(p.field_6283);
        double vx = (dx * Math.cos(angle) + dz * Math.sin(angle)) * 20.0;
        double vz = (-dx * Math.sin(angle) + dz * Math.cos(angle)) * 20.0;
        double vy = dy * 20.0;
        double speed = Math.hypot(dx, dz) * 20.0;
        boolean moving = speed > 0.05;
        afk = !(moving || Math.abs(dy) > 0.002 || Math.abs(Molang.wrap((double)p.method_36454() - yaw)) > 0.15 || Math.abs((double)p.method_36455() - pitch) > 0.15 || p.method_6115()) ? afk + 0.05 : 0.0;
        distance += Math.hypot(dx, dz);
        sx += (vx - sx) * 0.25;
        sy += (vy - sy) * 0.25;
        sz += (vz - sz) * 0.25;
        LivePetState.put("q.local_x_velocity", vx);
        LivePetState.put("q.local_y_velocity", vy);
        LivePetState.put("q.local_z_velocity", vz);
        LivePetState.put("q.ground_speed", speed);
        LivePetState.put("q.distance_moved", distance);
        LivePetState.put("q.head_x_rotation", p.method_36455());
        LivePetState.put("q.head_y_rotation", Molang.wrap(p.field_6241 - p.field_6283));
        LivePetState.put("q.body_y_rotation", Math.toDegrees(localBodyY));
        LivePetState.put("q.body_x_rotation", Math.toDegrees(localBodyX));
        LivePetState.put("q.left_arm_x_rotation", Math.toDegrees(leftArmX));
        LivePetState.put("q.right_arm_x_rotation", Math.toDegrees(rightArmX));
        LivePetState.put("q.left_arm_y_rotation", Math.toDegrees(leftArmY));
        LivePetState.put("q.right_arm_y_rotation", Math.toDegrees(rightArmY));
        LivePetState.put("q.is_alive", p.method_5805() ? 1.0 : 0.0);
        LivePetState.put("q.is_crouching", p.method_18276() ? 1.0 : 0.0);
        LivePetState.put("q.is_swimming", p.method_5681() ? 1.0 : 0.0);
        LivePetState.put("q.is_in_water", p.method_5799() ? 1.0 : 0.0);
        LivePetState.put("q.is_under_water", p.method_5777(class_3486.field_15517) ? 1.0 : 0.0);
        LivePetState.put("q.is_elytra_flying", p.method_6128() ? 1.0 : 0.0);
        LivePetState.put("q.is_flying", p.method_31549().field_7479 ? 1.0 : 0.0);
        LivePetState.put("q.is_on_ground", p.method_24828() ? 1.0 : 0.0);
        LivePetState.put("q.is_moving", moving ? 1.0 : 0.0);
        LivePetState.put("q.is_running", p.method_5624() ? 1.0 : 0.0);
        LivePetState.put("q.is_sprinting", p.method_5624() ? 1.0 : 0.0);
        LivePetState.put("q.is_raining", mc.field_1687.method_8419() ? 1.0 : 0.0);
        LivePetState.put("q.body_slot_equipped", p.method_6118(class_1304.field_6174).method_7960() ? 0.0 : 1.0);
        LivePetState.put("q.is_slim_skin", p.method_52814().comp_1629().toString().toLowerCase(Locale.ROOT).contains("slim") ? 1.0 : 0.0);
        LivePetState.put("q.is_in_gui", 0.0);
        LivePetState.put("q.is_in_preview_model", 0.0);
        LivePetState.put("q.is_on_fire", p.method_5809() ? 1.0 : 0.0);
        LivePetState.put("c.local_x_velocity_smoothed", sx);
        LivePetState.put("c.local_y_velocity_smoothed", sy);
        LivePetState.put("c.local_z_velocity_smoothed", sz);
        LivePetState.put("c.x_velocity_smoothed", sx);
        LivePetState.put("c.y_velocity_smoothed", sy);
        LivePetState.put("c.z_velocity_smoothed", sz);
        LivePetState.put("c.lvs_x", sx);
        LivePetState.put("c.lvs_y", sy);
        LivePetState.put("c.lvs_z", sz);
        LivePetState.put("c.ground_speed_smoothed", Math.hypot(sx, sz));
        LivePetState.put("c.distance_moved", distance);
        LivePetState.put("c.afk_timer", afk);
        LivePetState.put("c.is_moving_smoothed", Math.min(1.0, Math.hypot(sx, sz)));
        LivePetState.put("c.is_walking_smoothed", p.method_24828() ? Math.min(1.0, Math.hypot(sx, sz)) : 0.0);
        LivePetState.put("c.is_crouching_smoothed", p.method_18276() ? 1.0 : 0.0);
        LivePetState.put("c.is_moving_backwards", vz < -0.1 ? 1.0 : 0.0);
        LivePetState.put("c.x_move_dir_smoothed", Math.signum(sx));
        LivePetState.put("c.z_move_dir_smoothed", Math.signum(sz));
        LivePetState.put("c.in_special_env", !p.method_5799() && !p.method_5681() && !p.method_6128() ? 0.0 : 1.0);
        LivePetState.put("c.tick_delta", 1.0);
        String biome = mc.field_1687.method_23753(p.method_24515()).method_40230().map(Object::toString).orElse("").toLowerCase(Locale.ROOT);
        LivePetState.put("q.biome_swamp", biome.contains("swamp") ? 1.0 : 0.0);
        LivePetState.put("q.biome_desert", biome.contains("desert") ? 1.0 : 0.0);
        LivePetState.put("q.biome_ocean", biome.contains("ocean") ? 1.0 : 0.0);
        LivePetState.put("q.dimension_nether", mc.field_1687.method_27983().equals(class_1937.field_25180) ? 1.0 : 0.0);
        LivePetState.put("q.dimension_end", mc.field_1687.method_27983().equals(class_1937.field_25181) ? 1.0 : 0.0);
        if (p.method_5799()) {
            for (depth = 0; depth < 16 && mc.field_1687.method_8316(p.method_24515().method_10086(depth)).method_15767(class_3486.field_15517); ++depth) {
            }
        }
        LivePetState.put("q.distance_from_surface", depth);
        LivePetState.put("q.surface_blocked", p.method_5799() && !mc.field_1687.method_8320(p.method_24515().method_10086(depth)).method_26215() ? 1.0 : 0.0);
        x = p.method_23317();
        y = p.method_23318();
        z = p.method_23321();
        yaw = p.method_36454();
        pitch = p.method_36455();
        return true;
    }
}


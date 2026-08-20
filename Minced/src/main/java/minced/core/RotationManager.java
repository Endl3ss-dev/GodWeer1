/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec2f
 */
package minced.core;

import minced.core.RotationBase;
import minced.core.EventRotation;
import minced.core.EventLook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public class RotationManager
extends RotationBase
{
    static boolean field796 = false;
    static float field797;
    static float field798;
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field799;
    static float field800;
    static double field801;
    static float field802;
    static float field803;
    static double field804;

    private void method244(EventLook param1) {
        if (field796) {
            this.method256(param1.method2559(), param1.method2557());
            param1.method2298(true);
        }
    }

    public static float method245() {
        return field797;
    }

    public static void method246(float param0, float param1) {
        field798 = param0;
        field797 = param1;
        field796 = true;
    }

    public static void method247(float param0, float param1) {
        RotationManager.method249(param0, param1);
        if (RotationManager.I.player == null) {
            return;
        }
        RotationManager.I.player.headYaw = param0;
        RotationManager.I.player.prevHeadYaw = param0;
    }

    public static void method248() {
        if (RotationManager.I.player == null || !field796) {
            return;
        }
        RotationManager.I.player.headYaw = field798;
        RotationManager.I.player.prevHeadYaw = field798;
    }

    public static void method249(float param0, float param1) {
        if (RotationManager.I.player == null) {
            return;
        }
        float v2 = MathHelper.clamp((float)param1, (float)field799, (float)field800);
        RotationManager.I.player.setYaw(param0);
        RotationManager.I.player.bodyYaw = param0;
        RotationManager.I.player.prevYaw = param0;
        RotationManager.I.player.prevBodyYaw = param0;
        RotationManager.I.player.setPitch(v2);
        RotationManager.I.player.prevPitch = v2;
    }

    public static boolean method250() {
        return field796;
    }

    public static void method251() {
        if (RotationManager.I.player != null) {
            field798 = RotationManager.I.player.getYaw();
            field797 = RotationManager.I.player.getPitch();
        }
        field796 = false;
    }

    public static void method252(float param0) {
        field798 = param0;
    }

    @EventHandler(2)
    private void method253(EventRotation param1) {
        if (field796) {
            param1.method2530(new Vec2f(field798, field797));
        } else {
            field798 = param1.method2528().x;
            field797 = param1.method2528().y;
        }
    }

    public static void method254(boolean param0) {
        field796 = param0;
    }

    public static float method255() {
        return field798;
    }

    private void method256(double param1, double param3) {
        field797 = MathHelper.clamp((float)((float)((double)field797 + param3 * field801)), (float)field802, (float)field803);
        field798 = (float)((double)field798 + param1 * field804);
    }

    public static void method257(float param0) {
        field797 = param0;
    }

    static {
        field798 = 60281.65f;
        field797 = 90.0f;
        field799 = -90.0f;
        field800 = 90.0f;
        field801 = 0.15;
        field802 = -90.0f;
        field803 = 90.0f;
        field804 = 0.15;
    }
}




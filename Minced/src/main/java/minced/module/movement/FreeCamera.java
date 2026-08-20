/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.option.Perspective
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.util.math.Vec3d
 *  org.lwjgl.glfw.GLFW
 */
package minced.module.movement;
import minced.core.EventHandler;

import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventInput;
import minced.core.EventFreecamWorldRender;
import minced.core.EventUpdate;
import minced.core.EventSwapWorld;
import minced.core.RotationManager;
import minced.core.EventMotion;
import minced.core.EventRotation;
import minced.core.EventThirdPersonDistance;
import minced.core.EventThirdPersonRender;
import minced.core.EventPostTick;
import minced.core.EventTogglePerspective;
import minced.core.CameraRotation;
import minced.core.EventMouseOver;
import minced.core.AimRotation;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class FreeCamera
extends Module {
    Vec3d field6599;
    static MinecraftClient I = MinecraftClient.getInstance();
    float cfr_renamed_0;
    float field6600;
    Vec3d field6601;
    Perspective field6602;
    Vec2f field6603;
    SliderSetting field6604 = new SliderSetting(field6609, 1.0f, field6610, 2.0f, field6611);
    static float field6605;
    BooleanSetting field6606 = new BooleanSetting(field6612, true);
    static String field6607;
    static String field6608;
    static String field6609;
    static float field6610;
    static float field6611;
    static String field6612;

    @EventHandler(2)
    public void method1559(EventInput param1) {
        if (this.field6599 == null) {
            return;
        }
        long v2 = I.getWindow().getHandle();
        float v4 = 0.0f;
        if (GLFW.glfwGetKey((long)v2, (int)265) == 1) {
            v4 = 1.0f;
        }
        if (GLFW.glfwGetKey((long)v2, (int)264) == 1) {
            v4 -= 1.0f;
        }
        float v5 = 0.0f;
        if (GLFW.glfwGetKey((long)v2, (int)263) == 1) {
            v5 = 1.0f;
        }
        if (GLFW.glfwGetKey((long)v2, (int)262) == 1) {
            v5 -= 1.0f;
        }
        param1.method1973(v4);
        param1.method1972(v5);
        param1.method1974(false);
        param1.method1970(false);
    }

    @EventHandler(2)
    public void method1560(EventSwapWorld param1) {
        this.toggle();
    }

    @EventHandler(2)
    public void method1561(EventPostTick param1) {
        RotationManager.method247(this.cfr_renamed_0, this.field6600);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.cfr_renamed_0 = FreeCamera.I.player.getYaw();
        this.field6600 = FreeCamera.I.player.getPitch();
        AimRotation.method357().method370();
        CameraRotation.cfr_renamed_288().method53();
        RotationManager.method246(this.cfr_renamed_0, this.field6600);
        this.field6601 = this.field6599 = FreeCamera.I.player.getCameraPosVec(I.getRenderTickCounter().getTickDelta(true));
        this.field6602 = FreeCamera.I.options.getPerspective();
        this.field6603 = new Vec2f(this.cfr_renamed_0, this.field6600);
    }

    @EventHandler(2)
    public void method1562(EventTogglePerspective param1) {
        param1.method2298(true);
    }

    @EventHandler(2)
    public void method1563(EventUpdate param1) {
        this.field6601 = this.field6599;
        FreeCamera.I.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        if (this.field6603 == null) {
            return;
        }
        double v2 = ((Float)this.field6604.getValue()).floatValue();
        float v4 = this.field6603.x;
        float v5 = v4 * field6605;
        Vec3d v6 = new Vec3d(-Math.sin(v5), 0.0, Math.cos(v5));
        Vec3d v7 = new Vec3d(Math.cos(v5), 0.0, Math.sin(v5));
        Vec3d v8 = new Vec3d(0.0, 1.0, 0.0);
        if (FreeCamera.I.options.forwardKey.isPressed()) {
            this.field6599 = this.field6599.add(v6.multiply(v2));
        }
        if (FreeCamera.I.options.backKey.isPressed()) {
            this.field6599 = this.field6599.subtract(v6.multiply(v2));
        }
        if (FreeCamera.I.options.leftKey.isPressed()) {
            this.field6599 = this.field6599.add(v7.multiply(v2));
        }
        if (FreeCamera.I.options.rightKey.isPressed()) {
            this.field6599 = this.field6599.subtract(v7.multiply(v2));
        }
        if (FreeCamera.I.options.jumpKey.isPressed()) {
            this.field6599 = this.field6599.add(v8.multiply(v2));
        }
        if (FreeCamera.I.options.sneakKey.isPressed()) {
            this.field6599 = this.field6599.subtract(v8.multiply(v2));
        }
    }

    @EventHandler(2)
    public void method1564(EventThirdPersonRender param1) {
        param1.method2538(true);
    }

    @Override
    public void onDisable() {
        RotationManager.method251();
        super.onDisable();
        this.field6599 = null;
        this.field6601 = null;
        this.field6603 = null;
        FreeCamera.I.options.setPerspective(this.field6602);
    }

    @EventHandler(2)
    public void method1565(EventThirdPersonDistance param1) {
        param1.method2535(0.0f);
    }

    @EventHandler(2)
    public void method1566(EventRotation param1) {
        this.field6603 = param1.method2528();
        Vec3d v2 = this.field6601 == null ? this.field6599 : this.field6601.add(this.field6599.subtract(this.field6601).multiply((double)I.getRenderTickCounter().getTickDelta(true)));
        param1.method2527(v2);
        param1.method2298(true);
    }

    @EventHandler(2)
    public void method1567(EventFreecamWorldRender param1) {
        param1.method2331(true);
    }

    @EventHandler(2)
    public void method1568(EventMouseOver param1) {
        if (!((Boolean)this.field6606.getValue()).booleanValue()) {
            return;
        }
        if (this.field6599 != null) {
            param1.method3564(this.field6599);
        }
        if (this.field6603 != null) {
            param1.method3565(this.field6603);
        }
    }

    public Vec3d I(float param1) {
        if (this.field6601 == null) {
            return this.field6599;
        }
        double v2 = this.field6601.x + (this.field6599.x - this.field6601.x) * (double)param1;
        double v4 = this.field6601.y + (this.field6599.y - this.field6601.y) * (double)param1;
        double v6 = this.field6601.z + (this.field6599.z - this.field6601.z) * (double)param1;
        return new Vec3d(v2, v4, v6);
    }

    @EventHandler(2)
    public void method1569(EventMotion param1) {
        param1.method2506(this.cfr_renamed_0);
        param1.method2512(this.field6600);
    }

    public FreeCamera() {
        super(field6607, field6608, Combat.Movement);
        this.field6602 = Perspective.FIRST_PERSON;
        this.addSettings(this.field6604, this.field6606);
    }

    static {
        field6607 = "Free Camera";
        field6608 = "Позволяет перемещаться в свободной камере";
        field6609 = "Скорость";
        field6610 = 0.1f;
        field6611 = 0.1f;
        field6612 = "Подменять взаимодействие";
        field6605 = (float)Math.PI / 180;
    }
}




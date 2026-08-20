/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.option.Perspective
 */
package minced.module.visual;
import minced.core.EventHandler;

import minced.core.BindSetting;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.RotationManager;
import minced.core.EventMotion;
import minced.core.EventThirdPersonDistance;
import minced.core.EventPostTick;
import minced.core.EventTogglePerspective;
import minced.core.CameraRotation;
import minced.core.EventKey;
import minced.core.AimRotation;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.movement.FreeCamera;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;

public class ThirdPerson
extends Module
{
    boolean field1045;
    float field1046;
    float field1047;
    boolean field1048;
    static MinecraftClient I = MinecraftClient.getInstance();
    Perspective field1049;
    ModeSetting field1050 = new ModeSetting(field1056, field1057, field1058, field1059);
    static String field1051;
    SliderSetting field1052 = new SliderSetting(field1060, field1061, 1.0f, field1062, field1063);
    BindSetting field1053 = new BindSetting(field1064);
    static String field1054 = "Third Person";
    static String field1055 = "Позволяет осмотреться от третьего лица без отображения другим игрокам";
    static String field1056 = "Режим обзора";
    static String field1057 = "Сзади";
    static String field1058 = "Спереди";
    static String field1059 = "Сзади";
    static String field1060 = "Дистанция";
    static float field1061 = 4.0f;
    static float field1062 = 10.0f;
    static float field1063 = 0.1f;
    static String field1064 = "Бинд";

    @EventHandler(2)
    public void method319(EventPostTick param1) {
        if (!this.field1045) {
            return;
        }
        RotationManager.method247(this.field1046, this.field1047);
    }

    @EventHandler(2)
    private void method320(EventUpdate param1) {
        if (this.field1048 && ThirdPerson.I.currentScreen == null) {
            if (!this.field1045) {
                this.field1049 = ThirdPerson.I.options.getPerspective();
                this.field1046 = ThirdPerson.I.player.getYaw();
                this.field1047 = ThirdPerson.I.player.getPitch();
                AimRotation.method357().method370();
                CameraRotation.cfr_renamed_288().method53();
                RotationManager.method246(this.field1046, this.field1047);
            }
            if (this.field1050.isMode(field1051)) {
                ThirdPerson.I.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            } else {
                ThirdPerson.I.options.setPerspective(Perspective.THIRD_PERSON_FRONT);
            }
            this.field1045 = true;
        } else if (this.field1045) {
            RotationManager.method251();
            ThirdPerson.I.options.setPerspective(this.field1049);
            this.field1045 = false;
        }
    }

    @EventHandler(2)
    public void method321(EventThirdPersonDistance param1) {
        if (this.field1045) {
            param1.method2535(((Float)this.field1052.getValue()).floatValue());
        }
    }

    @EventHandler(2)
    public void method322(EventMotion param1) {
        if (this.field1045) {
            param1.method2506(this.field1046);
            param1.method2512(this.field1047);
        }
    }

    @EventHandler(2)
    public void method323(EventKey param1) {
        if (Minced.getInstance().getModuleManager().method187(FreeCamera.class).isEnabled()) {
            return;
        }
        if (param1.method3595() == ((Integer)this.field1053.getValue()).intValue()) {
            this.field1048 = param1.method3599();
        }
    }

    public ThirdPerson() {
        super(field1054, field1055, Combat.Visuals);
        this.field1049 = Perspective.FIRST_PERSON;
        this.addSettings(this.field1050, this.field1052, this.field1053);
    }

    @EventHandler(2)
    public void method324(EventTogglePerspective param1) {
        if (this.field1048) {
            param1.method2298(true);
        }
    }

    static {
        field1051 = "Сзади";
    }
}




/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.entity.decoration.ArmorStandEntity
 */
package minced.module.visual;
import minced.core.EventHandler;

import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.EventUpdate;
import minced.core.EventNoRender;
import minced.core.EventThirdPersonDistance;
import minced.core.RemovalState;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class Removals
extends Module {
    static String field4512;
    static String field4513;
    static String field4514;
    static String field4515;
    static String field4516;
    static String field4517;
    static String field4518;
    static String field4519;
    static String field4520;
    static String field4521;
    static String field4522;
    static String field4523;
    static String field4524;
    static String field4525;
    static String field4526;
    static String field4527;
    static String field4528;
    static String field4529;
    static String field4530;
    static String field4531;
    static String field4532;
    static String field4533;
    public static MultiBooleanSetting field4534;
    static String field4535;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field4536;
    static String field4537;
    static String field4538;
    static String field4539;
    static String field4540;
    static String field4541;
    static String field4542;
    static String field4543;
    static String field4544;
    static String field4545;
    static String field4546;
    static String field4547;
    static String field4548;
    static String field4549;
    static String field4550;
    static String field4551;
    static String field4552;
    static String field4553;
    static String field4554;
    static String field4555;
    static String field4556;
    static String field4557;
    static String field4558;
    static float field4559;

    public static void method1139() {
        field4534 = new MultiBooleanSetting(field4512, new BooleanSetting(field4513, true), new BooleanSetting(field4514, true), new BooleanSetting(field4515, true), new BooleanSetting(field4516, true), new BooleanSetting(field4517, true), new BooleanSetting(field4518, true), new BooleanSetting(field4519, true), new BooleanSetting(field4520, true), new BooleanSetting(field4521, true), new BooleanSetting(field4522, true), new BooleanSetting(field4523, true), new BooleanSetting(field4524, true), new BooleanSetting(field4525, true), new BooleanSetting(field4526, true), new BooleanSetting(field4527, false), new BooleanSetting(field4528, true), new BooleanSetting(field4529, true), new BooleanSetting(field4530, true), new BooleanSetting(field4531, false), new BooleanSetting(field4532, false), new BooleanSetting(field4533, false));
    }

    @EventHandler(2)
    private void method1140(EventUpdate param1) {
        if (field4534.method1898(field4535).booleanValue() && Removals.I.world != null) {
            for (Entity v3 : Removals.I.world.getEntities()) {
                if (!(v3 instanceof ArmorStandEntity)) continue;
                v3.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    @EventHandler(2)
    private void method1141(EventNoRender param1) {
        boolean v2;
        switch (RemovalState.field12251[param1.field9041.ordinal()]) {
            default: {
                throw new MatchException(null, null);
            }
            case 1: {
                boolean bl = v2 = field4534.method1898(field4536).booleanValue();
                break;
            }
            case 2: {
                boolean bl = v2 = field4534.method1898(field4537).booleanValue();
                break;
            }
            case 3: {
                boolean bl = v2 = field4534.method1898(field4538).booleanValue();
                break;
            }
            case 4: {
                boolean bl = v2 = field4534.method1898(field4539).booleanValue();
                break;
            }
            case 5: {
                boolean bl = v2 = field4534.method1898(field4540).booleanValue();
                break;
            }
            case 6: {
                boolean bl = v2 = field4534.method1898(field4541).booleanValue();
                break;
            }
            case 7: {
                boolean bl = v2 = field4534.method1898(field4542).booleanValue();
                break;
            }
            case 8: {
                boolean bl = v2 = field4534.method1898(field4543).booleanValue();
                break;
            }
            case 9: {
                boolean bl = v2 = field4534.method1898(field4544).booleanValue();
                break;
            }
            case 10: {
                boolean bl = v2 = field4534.method1898(field4545).booleanValue();
                break;
            }
            case 11: {
                boolean bl = v2 = field4534.method1898(field4546).booleanValue();
                break;
            }
            case 12: {
                boolean bl = v2 = field4534.method1898(field4547).booleanValue();
                break;
            }
            case 13: {
                boolean bl = v2 = field4534.method1898(field4548).booleanValue();
                break;
            }
            case 14: {
                boolean bl = v2 = field4534.method1898(field4549).booleanValue();
                break;
            }
            case 15: {
                boolean bl = v2 = field4534.method1898(field4550).booleanValue();
                break;
            }
            case 16: {
                boolean bl = v2 = field4534.method1898(field4551).booleanValue();
                break;
            }
            case 17: {
                boolean bl = v2 = field4534.method1898(field4552).booleanValue();
                break;
            }
            case 18: {
                boolean bl = v2 = field4534.method1898(field4553).booleanValue();
                break;
            }
            case 19: {
                boolean bl = v2 = field4534.method1898(field4554).booleanValue();
                break;
            }
            case 20: {
                boolean bl = v2 = field4534.method1898(field4555).booleanValue();
            }
        }
        if (v2) {
            param1.method2298(true);
        }
    }

    public Removals() {
        super(field4556, field4557, Combat.Visuals);
        this.addSettings(field4534);
    }

    @EventHandler(2)
    public void method1142(EventThirdPersonDistance param1) {
        if (field4534.method1898(field4558).booleanValue()) {
            param1.method2535(field4559);
        }
    }

    static {
        field4556 = "Removals";
        field4557 = "Увеличивает FPS, убирая лишние визуальные эффекты";
        field4536 = "Огонь";
        field4537 = "Босс-бар";
        field4538 = "Скорборд";
        field4539 = "Тайтлы";
        field4540 = "Снесение тотема";
        field4541 = "Тряска камеры";
        field4542 = "Камера клип";
        field4543 = "Удочка на экране";
        field4544 = "Частицы разрушения";
        field4545 = "Дождь";
        field4546 = "Тени";
        field4547 = "Свечение игроков";
        field4548 = "Дым";
        field4549 = "Виньетка";
        field4550 = "Стрелы в игроке";
        field4551 = "Игроки";
        field4552 = "Плохие эффекты";
        field4553 = "Размытие под водой";
        field4554 = "Фон контейнера";
        field4555 = "Видимость лавы";
        field4535 = "Стойки для брони";
        field4558 = "Камера клип";
        field4559 = 4.0f;
        field4512 = "Применять на";
        field4513 = "Тряска камеры";
        field4514 = "Скорборд";
        field4515 = "Удочка на экране";
        field4516 = "Босс-бар";
        field4517 = "Частицы разрушения";
        field4518 = "Дождь";
        field4519 = "Камера клип";
        field4520 = "Тени";
        field4521 = "Дым";
        field4522 = "Снесение тотема";
        field4523 = "Виньетка";
        field4524 = "Стрелы в игроке";
        field4525 = "Плохие эффекты";
        field4526 = "Свечение игроков";
        field4527 = "Игроки";
        field4528 = "Размытие под водой";
        field4529 = "Огонь";
        field4530 = "Тайтлы";
        field4531 = "Видимость лавы";
        field4532 = "Фон контейнера";
        field4533 = "Стойки для брони";
        Removals.method1139();
    }
}




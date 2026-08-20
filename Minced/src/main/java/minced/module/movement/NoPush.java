/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.module.movement;
import minced.core.EventHandler;

import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.EventNoPush;
import minced.core.PushState;
import minced.core.Module;
import minced.enums.Combat;

public class NoPush
extends Module {
    static String field4371 = "No Push";
    static String field4372 = "Позволяет не отталкиваться при определенных условиях, обеспечивая стабильное передвижение";
    static String field4373 = "Тип";
    static String field4374 = "Игроки";
    static String field4375 = "Блоки";
    static String field4376 = "Вода";
    static String field4377 = "Удочки";
    MultiBooleanSetting field4378 = new MultiBooleanSetting(field4373, new BooleanSetting(field4374, true), new BooleanSetting(field4375, true), new BooleanSetting(field4376, true), new BooleanSetting(field4377, true));
    static String field4379 = "Блоки";
    static String field4380 = "Вода";
    static String field4381 = "Игроки";
    static String field4382;

    public NoPush() {
        super(field4371, field4372, Combat.Movement);
        this.addSettings(this.field4378);
    }

    @EventHandler(2)
    public void method1108(EventNoPush param1) {
        boolean v2;
        switch (PushState.field11659[param1.field9025.ordinal()]) {
            default: {
                throw new MatchException(null, null);
            }
            case 1: {
                boolean bl = v2 = this.field4378.method1898(field4379).booleanValue();
                break;
            }
            case 2: {
                boolean bl = v2 = this.field4378.method1898(field4380).booleanValue();
                break;
            }
            case 3: {
                boolean bl = v2 = this.field4378.method1898(field4381).booleanValue();
                break;
            }
            case 4: {
                boolean bl = v2 = this.field4378.method1898(field4382).booleanValue();
            }
        }
        if (v2) {
            param1.method2298(true);
        }
    }

    static {
        field4382 = "Удочки";
    }
}


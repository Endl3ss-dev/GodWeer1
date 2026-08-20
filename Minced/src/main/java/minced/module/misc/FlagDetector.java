/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.math.Vec3d
 *  sLM.4ZXYEo
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.util.function.Function;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.EventPacket;
import minced.core.ChatUtil;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.movement.BedrockClip;
import minced.module.movement.Blink;
import minced.module.movement.Flight;
import minced.module.movement.GuiMove;
import minced.module.movement.HighJump;
import minced.module.combat.NoVelocity;
import minced.module.movement.Speed;
import minced.module.movement.Timer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import sLM._4ZXYEo;

public class FlagDetector
extends Module
{
    static double cfr_underscore;
    static String cfr_renamed_384;

    MultiBooleanSetting field3513;
    static String field3514;
    static String field3515;
    static String field3516;
    static String field3517;
    static String field3518;
    static String field3519;
    static String field3520;
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field3521;
    static String field3522;
    static double field3523;
    static double field3524;
    static String field3525;
    MultiBooleanSetting field3526;
    static String field3527;
    BooleanSetting field3528 = new BooleanSetting(field3534, true);
    static String field3529;
    static String field3530;
    static String field3531;
    static String field3532;
    static String field3533;
    static String field3534;
    static String field3535;
    static String field3536;
    static String field3537;
    static String field3538;
    static String field3539;
    static String field3540;
    static String field3541;
    static String field3542;
    static String field3543;
    static String field3544;

    private void method969() {
        boolean v1 = Module.isStateChangeSuppressed();
        Module.setStateChangeSuppressed(true);
        if (Boolean.TRUE.equals(this.field3513.method1898(field3514))) {
            this.method973(Minced.getInstance().getModuleManager().method187(Flight.class));
        }
        if (Boolean.TRUE.equals(this.field3513.method1898(field3515))) {
            this.method973(Minced.getInstance().getModuleManager().method187(Speed.class));
        }
        if (Boolean.TRUE.equals(this.field3513.method1898(field3516))) {
            this.method973(Minced.getInstance().getModuleManager().method187(BedrockClip.class));
        }
        if (Boolean.TRUE.equals(this.field3513.method1898(field3517))) {
            this.method973(Minced.getInstance().getModuleManager().method187(Timer.class));
        }
        if (Boolean.TRUE.equals(this.field3513.method1898(field3518))) {
            this.method973(Minced.getInstance().getModuleManager().method187(GuiMove.class));
        }
        if (Boolean.TRUE.equals(this.field3513.method1898(field3519))) {
            this.method973(Minced.getInstance().getModuleManager().method187(Blink.class));
        }
        if (Boolean.TRUE.equals(this.field3513.method1898(field3520))) {
            this.method973(Minced.getInstance().getModuleManager().method187(HighJump.class));
        }
        Module.setStateChangeSuppressed(v1);
    }

    @EventHandler(2)
    public void method970(EventPacket param1) {
        if (!param1.method2466() || FlagDetector.I.player == null) {
            return;
        }
        Packet v32 = param1.method2467();
        if (v32 instanceof PlayerPositionLookS2CPacket) {
            PlayerPositionLookS2CPacket v2 = (PlayerPositionLookS2CPacket)v32;
            Vec3d v32s = v2.change().position();
            double v4 = FlagDetector.I.player.getPos().distanceTo((Vec3d)v32s);
            if (v4 >= field3521) {
                this.method972(v2.getClass().getSimpleName(), (String)((Object)_4ZXYEo.Ц6("makeConcatWithConstants", new Object[]{}, (String)String.format(field3522, v4))));
                this.method969();
            }
            return;
        }
        v32 = param1.method2467();
        if (v32 instanceof EntityVelocityUpdateS2CPacket) {
            double v7;
            double v5;
            EntityVelocityUpdateS2CPacket v2 = (EntityVelocityUpdateS2CPacket)v32;
            if (v2.getEntityId() != FlagDetector.I.player.getId()) {
                return;
            }
            double v32_133 = v2.getVelocityX();
            double v9 = Math.sqrt(v32_133 * v32_133 + (v5 = v2.getVelocityY()) * v5 + (v7 = v2.getVelocityZ()) * v7);
            if (v9 < field3523 && FlagDetector.I.player.getVelocity().lengthSquared() > field3524) {
                this.method972(v2.getClass().getSimpleName(), field3525);
                this.method971();
            }
        }
    }

    private void method971() {
        if (!Boolean.TRUE.equals(this.field3526.method1898(field3527))) {
            return;
        }
        Module v1 = Minced.getInstance().getModuleManager().method187(NoVelocity.class);
        if (v1 == null) {
            return;
        }
        boolean v2 = Module.isStateChangeSuppressed();
        Module.setStateChangeSuppressed(true);
        this.method973(v1);
        Module.setStateChangeSuppressed(v2);
    }

    private void method972(String param1, String param2) {
        if (!((Boolean)this.field3528.getValue()).booleanValue()) {
            return;
        }
        MutableText v3 = Text.literal((String)field3529).formatted(Formatting.RED).append((Text)Text.literal((String)field3530).formatted(Formatting.GRAY)).append((Text)Text.literal((String)param1).formatted(Formatting.WHITE)).append((Text)Text.literal((String)field3531).formatted(Formatting.GRAY)).append((Text)Text.literal((String)param2).formatted(Formatting.WHITE));
        ChatUtil.method3308(v3, new Object[0]);
    }

    private void method973(Module param1) {
        if (param1 != null && param1.isEnabled()) {
            param1.cfr_renamed_4(false);
        }
    }

    public FlagDetector() {
        super(field3532, field3533, Combat.Miscellaneous);
        this.field3513 = new MultiBooleanSetting(field3535, new BooleanSetting(field3536, true), new BooleanSetting(field3537, true), new BooleanSetting(field3538, true), new BooleanSetting(field3539, true), new BooleanSetting(field3540, true), new BooleanSetting(field3541, true), new BooleanSetting(field3542, true));
        this.field3526 = new MultiBooleanSetting(field3543, new BooleanSetting(field3544, true));
        this.addSettings(this.field3528, this.field3513, this.field3526);
    }

    static {
        cfr_underscore = 0.0;
        field3532 = "FlagDetector";
        field3533 = "Реагирует на откат позиции и обнуление скорости с сервера, выключает выбранные модули";
        field3534 = "Лог в чат";
        field3535 = "Откат выключает";
        field3536 = "Полёт";
        field3537 = "Спид";
        field3538 = "Клип";
        field3539 = "Таймер";
        field3540 = "Gui Move";
        field3541 = "Блинк";
        field3542 = "Высокий прыжок";
        field3543 = "Велосити сейф";
        field3544 = "No Velocity";
        field3521 = 0.5;
        field3522 = "%.1f блоков";
        cfr_renamed_384 = "Откат позиции: \u0001";
        field3523 = 0.01;
        field3524 = 0.0025000000000000005;
        field3525 = "Обнуление скорости";
        field3514 = "Полёт";
        field3515 = "Спид";
        field3516 = "Клип";
        field3517 = "Таймер";
        field3518 = "Gui Move";
        field3519 = "Блинк";
        field3520 = "Высокий прыжок";
        field3527 = "No Velocity";
        field3529 = "[Flag] ";
        field3530 = "Пакет: ";
        field3531 = " — ";
    }
}




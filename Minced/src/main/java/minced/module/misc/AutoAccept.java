/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import minced.core.BooleanSetting;
import minced.core.EventPacket;
import minced.core.ServerDetector;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

public class AutoAccept
extends Module {
    static String field4409;
    static String field4410;
    static String field4411;
    static String field4412;
    static String field4413;
    static String[] field4414;
    static String field4415 = "Auto Accept";
    static String field4416 = "Автоматически принимает запросы на телепортацию от других игроков";
    static String field4417 = "Принимать запросы только от друзей";
    BooleanSetting field4418 = new BooleanSetting(field4417, true);
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field4419;

    public static void method1110() {
        field4414 = new String[]{field4409, field4410, field4411, field4412, field4413};
    }

    public AutoAccept() {
        super(field4415, field4416, Combat.Miscellaneous);
        this.addSettings(this.field4418);
    }

    @EventHandler(2)
    public void method1111(EventPacket param1) {
        block3: {
            block4: {
                block2: {
                    Object v3;
                    if (AutoAccept.I.player == null || AutoAccept.I.world == null || !((v3 = param1.method2467()) instanceof GameMessageS2CPacket)) break block2;
                    GameMessageS2CPacket v2 = (GameMessageS2CPacket)v3;
                    v3 = v2.content().getString();
                    String v4 = ((String)v3).toLowerCase();
                    Stream<String> stream = Arrays.stream(field4414);
                    Objects.requireNonNull(v4);
                    if (!stream.anyMatch(v4::contains)) break block3;
                    if (((Boolean)this.field4418.getValue()).booleanValue()) {
                        String v5 = ServerDetector.method2687((String)v3);
                        if (!Minced.getInstance().getFriendManager().cfr_renamed_4(v5)) {
                            return;
                        }
                    }
                    break block4;
                }
                return;
            }
            AutoAccept.I.player.networkHandler.sendCommand(field4419);
        }
    }

    static {
        field4419 = "tpaccept";
        field4409 = "телепортироваться";
        field4410 = "has requested teleport";
        field4411 = "просит к вам телепортироваться";
        field4412 = "запрашивает телепорт к вам";
        field4413 = "запрос на телепортацию от";
        AutoAccept.method1110();
    }
}




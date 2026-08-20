/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.EnchantmentHelper$Consumer
 *  net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket
 */
package minced.core;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;

final class PacketPositionUtil {
    public static final int typeSwitch(Object param0, int param1, BiPredicate param2, List param3) {
        Objects.checkIndex(param1, 4);
        if (param0 == null) {
            return -1;
        }
        switch (param1) {
            case 0: {
                if (param0 instanceof EntityS2CPacket) {
                    return 0;
                }
            }
            case 1: {
                if (param0 instanceof EntityPositionS2CPacket) {
                    return 1;
                }
            }
            case 2: {
                if (!(param0 instanceof EntityTrackerUpdateS2CPacket)) break;
                return 2;
            }
        }
        return 3;
    }
}




/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
 *  net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.util.Hand
 */
package minced.module.player;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventUpdate;
import minced.core.EventPacket;
import minced.core.ClientState;
import minced.core.Module;
import minced.enums.Combat;
import minced.mixin.accessor.IClientPlayerInteractionManagerMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public class AutoFish
extends Module {
    BooleanSetting field3285;
    static String field3286 = "AutoFish";
    static String field3287 = "Симулятор рыбалки для риливорлда";
    ClientState field3288 = new ClientState();
    ClientState field3289 = new ClientState();
    static String field3290 = "Менять удочку";
    BooleanSetting field3291 = new BooleanSetting(field3290, true);
    static String field3292 = "Авто-починка";
    BooleanSetting field3293 = new BooleanSetting(field3292, true);
    static String field3294 = "Авто-продажа рыбы";
    static String field3295 = "Задержка (мин)";
    static float field3296 = 10.0f;
    static float field3297 = 60.0f;
    SliderSetting field3298;
    static MinecraftClient I = MinecraftClient.getInstance();
    static long field3299;
    static long field3300;
    static String field3301;
    static String field3302;
    boolean field3303;
    static long field3304;
    boolean field3305;
    static long field3306;

    public AutoFish() {
        super(field3286, field3287, Combat.Player);
        this.field3285 = new BooleanSetting(field3294, true);
        this.field3298 = new SliderSetting(field3295, field3296, 1.0f, field3297, 1.0f, () -> (Boolean)this.field3285.getValue());
        this.addSettings(this.field3291, this.field3293, this.field3285, this.field3298);
    }

    @EventHandler(2)
    public void method923(EventUpdate param1) {
        block7: {
            block6: {
                int v3;
                ItemStack v2;
                if (AutoFish.I.player == null || AutoFish.I.world == null) {
                    return;
                }
                if (((Boolean)this.field3285.getValue()).booleanValue() && this.field3289.method3113((long)((Float)this.field3298.getValue()).intValue() * field3299 * field3300)) {
                    AutoFish.I.player.networkHandler.sendChatCommand(field3301);
                    this.field3289.method3114();
                }
                if ((v2 = AutoFish.I.player.getMainHandStack()).getItem() != Items.FISHING_ROD) break block6;
                if (!((Boolean)this.field3293.getValue()).booleanValue() || !v2.isDamageable() || (v3 = v2.getMaxDamage() - v2.getDamage()) > 10) break block7;
                AutoFish.I.player.networkHandler.sendChatCommand(field3302);
                break block7;
            }
            if (((Boolean)this.field3291.getValue()).booleanValue()) {
                for (int v3 = 0; v3 < 9; ++v3) {
                    ItemStack v4 = AutoFish.I.player.getInventory().getStack(v3);
                    if (v4.getItem() != Items.FISHING_ROD) continue;
                    AutoFish.I.player.getInventory().selectedSlot = v3;
                    ((IClientPlayerInteractionManagerMixin)AutoFish.I.interactionManager).invokeSyncSelectedSlot();
                    break;
                }
            }
        }
        if (this.field3303 && this.field3288.method3113(field3304)) {
            AutoFish.I.player.networkHandler.sendPacket((Packet)new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, AutoFish.I.player.getYaw(), AutoFish.I.player.getPitch()));
            this.field3303 = false;
            this.field3305 = true;
            this.field3288.method3114();
        }
        if (this.field3305 && this.field3288.method3113(field3306)) {
            AutoFish.I.player.networkHandler.sendPacket((Packet)new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, AutoFish.I.player.getYaw(), AutoFish.I.player.getPitch()));
            this.field3305 = false;
            this.field3288.method3114();
        }
    }

    @EventHandler(2)
    public void method924(EventPacket param1) {
        PlaySoundS2CPacket v2;
        if (!this.isEnabled() || !param1.method2466() || AutoFish.I.player == null || AutoFish.I.world == null) {
            return;
        }
        Packet v3 = param1.method2467();
        if (v3 instanceof PlaySoundS2CPacket && (v2 = (PlaySoundS2CPacket)v3).getSound().value() == SoundEvents.ENTITY_FISHING_BOBBER_SPLASH) {
            this.field3303 = true;
            this.field3288.method3114();
        }
    }

    @Override
    public void onEnable() {
        this.field3288.method3114();
        this.field3289.method3114();
        this.field3303 = false;
        this.field3305 = false;
        super.onEnable();
    }

    static {
        field3299 = 60L;
        field3300 = 1000L;
        field3301 = "sellfish";
        field3302 = "fix all";
        field3304 = 600L;
        field3306 = 300L;
    }
}




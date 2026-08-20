/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket$Handler
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  sg.ec.ЧФ$Ч
 */
package minced.module.movement;
import minced.core.EventHandler;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import minced.core.ColorUtil;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventRender3D;
import minced.core.EventUpdate;
import minced.core.Render3D;
import minced.core.EventSwapWorld;
import minced.core.EventPacket;
import minced.core.PacketQueue;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.LOAD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import sg.ec.chf;

public class Blink
extends Module {
    BooleanSetting field6549;
    static MinecraftClient I = MinecraftClient.getInstance();
    Vec3d field6550 = null;
    static double field6551;
    static double field6552;
    static double field6553;
    static double field6554;
    static double field6555;
    CopyOnWriteArrayList field6556;
    int field6557 = 0;
    BooleanSetting field6558;
    SliderSetting cfr_renamed_0;
    BooleanSetting field6559 = new BooleanSetting(field6562, true);
    static String field6560;
    static String field6561;
    static String field6562;
    static String field6563;
    static String field6564;
    static String field6565;
    static float field6566;
    static float field6567;

    @EventHandler(2)
    public void method1539(EventRender3D param1) {
        if (!((Boolean)this.field6549.getValue()).booleanValue() || Blink.I.player == null || this.field6550 == null) {
            return;
        }
        MatrixStack v2 = param1.method2348();
        Vec3d v3 = Blink.I.gameRenderer.getCamera().getPos();
        v2.push();
        v2.translate(-v3.x, -v3.y, -v3.z);
        Box v4 = new Box(this.field6550.x - field6551, this.field6550.y, this.field6550.z - field6552, this.field6550.x + field6553, this.field6550.y + field6554, this.field6550.z + field6555);
        Render3D.method2407(v2, v4, ColorUtil.method1852(100, 150, 255, 100), 2.0f);
        v2.pop();
    }

    private void method1540() {
        if (Blink.I.player == null || Blink.I.player.networkHandler == null) {
            return;
        }
        for (Packet v2 : (Iterable<Packet>)(this.field6556)) {
            PacketQueue.method3505(v2);
        }
        this.field6556.clear();
        this.field6550 = Blink.I.player.getPos();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (Blink.I.player == null) {
            this.toggle();
            return;
        }
        this.field6550 = Blink.I.player.getPos();
        this.field6556.clear();
        this.field6557 = 0;
    }

    @Override
    public void onDisable() {
        if (Blink.I.player != null && Blink.I.player.networkHandler != null) {
            for (Packet v2 : (Iterable<Packet>)(this.field6556)) {
                PacketQueue.method3505(v2);
            }
        }
        this.field6556.clear();
        this.field6550 = null;
        this.field6557 = 0;
        super.onDisable();
    }

    private static boolean method1541(Packet param0) {
        if (param0 instanceof PlayerInteractEntityC2SPacket) {
            PlayerInteractEntityC2SPacket v1 = (PlayerInteractEntityC2SPacket)param0;
            AtomicBoolean v2 = new AtomicBoolean(false);
            v1.handle((PlayerInteractEntityC2SPacket.Handler)new chf.ch(v2));
            return v2.get();
        }
        return false;
    }

    @EventHandler(2)
    public void method1542(EventUpdate param1) {
        if (Blink.I.player == null || Blink.I.world == null) {
            return;
        }
        if (!Blink.I.player.isAlive()) {
            this.method1540();
            this.toggle();
            return;
        }
        if (Boolean.TRUE.equals(this.field6558.getValue()) && ++this.field6557 >= ((Float)this.cfr_renamed_0.getValue()).intValue()) {
            this.method1540();
            this.field6557 = 0;
        }
    }

    @EventHandler(2)
    public void method1544(EventPacket param1) {
        if (Blink.I.player == null || Blink.I.world == null || I.isInSingleplayer()) {
            return;
        }
        if (!Blink.I.player.isAlive()) {
            this.method1540();
            this.toggle();
            return;
        }
        if (param1.method2465() && param1.method2467() instanceof PlayerMoveC2SPacket) {
            this.field6556.add(param1.method2467());
            param1.method2298(true);
            return;
        }
        if (((Boolean)this.field6559.getValue()).booleanValue() && param1.method2465() && Blink.method1541(param1.method2467()) && !this.field6556.isEmpty()) {
            this.method1540();
        }
    }

    public Blink() {
        super(field6560, field6561, Combat.Movement);
        this.field6549 = new BooleanSetting(field6563, true);
        this.field6558 = new BooleanSetting(field6564, true);
        this.cfr_renamed_0 = new SliderSetting(field6565, field6566, 1.0f, field6567, 1.0f, () -> Boolean.TRUE.equals(this.field6558.getValue()));
        this.field6556 = new CopyOnWriteArrayList();
        this.addSettings(this.field6559, this.field6549, this.field6558, this.cfr_renamed_0);
    }

    @EventHandler(2)
    public void method1545(EventSwapWorld param1) {
        if (param1.field8581 == LOAD.CHANGED) {
            this.field6556.clear();
            this.field6557 = 0;
            this.field6550 = Blink.I.player != null ? Blink.I.player.getPos() : null;
        }
    }

    static {
        field6560 = "Blink";
        field6561 = "Задерживает пакеты движения и отправляет их позже";
        field6562 = "Отправлять пакеты на удар";
        field6563 = "Отображать позицию";
        field6564 = "Автоматически отправлять";
        field6565 = "Кол-во тиков между отправками";
        field6566 = 10.0f;
        field6567 = 100.0f;
        field6551 = 0.3;
        field6552 = 0.3;
        field6553 = 0.3;
        field6554 = 1.8;
        field6555 = 0.3;
    }
}




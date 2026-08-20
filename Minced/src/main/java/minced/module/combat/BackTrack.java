/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.TrackedPosition
 *  net.minecraft.network.listener.PacketListener
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket
 *  net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket
 *  net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
 *  net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 */
package minced.module.combat;
import minced.core.EventHandler;

import java.lang.runtime.SwitchBootstraps;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import minced.core.ColorUtil;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.PacketRecord;
import minced.core.EventRender3D;
import minced.core.EventUpdate;
import minced.core.Render3D;
import minced.core.EventSwapWorld;
import minced.core.EventPacket;
import minced.core.EventAttack;
import minced.core.ThemeColors;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.MAIN;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BackTrack
extends Module {
    Entity field4589 = null;
    TrackedPosition field4590 = null;
    ConcurrentLinkedQueue field4591;
    ConcurrentLinkedQueue field4592;
    static MinecraftClient I = MinecraftClient.getInstance();
    long field4593 = 0L;
    SliderSetting field4594 = new SliderSetting(field4604, field4605, 1.0f, field4606, 1.0f);
    long field4595 = 0L;
    SliderSetting field4596;
    SliderSetting field4597;
    static double field4598;
    BooleanSetting field4599;
    static float field4600;
    int field4601 = 0;
    static String field4602;
    static String field4603;
    static String field4604;
    static float field4605;
    static float field4606;
    static String field4607;
    static float field4608;
    static float field4609;
    static float field4610;
    static float field4611;
    SliderSetting field4612 = new SliderSetting(field4607, field4608, field4609, field4610, field4611);
    static String field4613;
    static float field4614;
    static float field4615;
    static float field4616;
    static String field4617;
    static float field4618;
    static float field4619;
    static float field4620;
    static String field4621;

    @Override
    public void onDisable() {
        super.onDisable();
        this.method1160(true);
    }

    private void method1149(Entity param1) {
        if (!this.method1151(param1)) {
            return;
        }
        if (param1 != this.field4589) {
            this.method1160(false);
            this.field4590 = new TrackedPosition();
            this.field4590.setPos(param1.getPos());
        }
        this.field4589 = param1;
    }

    private boolean method1150() {
        return this.field4591.isEmpty() && this.field4592.isEmpty();
    }

    @EventHandler(2)
    public void cfr_renamed_4(EventAttack param1) {
        if (BackTrack.I.player == null || BackTrack.I.world == null) {
            return;
        }
        this.field4593 = System.currentTimeMillis();
        Entity v2 = param1.method2471();
        this.method1149(v2);
    }

    private boolean method1151(Entity param1) {
        boolean v4;
        if (param1 == null) {
            return false;
        }
        double v2 = param1.distanceTo((Entity)BackTrack.I.player);
        boolean bl = v4 = v2 <= (double)((Float)this.field4594.getValue()).floatValue();
        if (v4) {
            this.field4595 = System.currentTimeMillis();
        }
        boolean v5 = (float)(System.currentTimeMillis() - this.field4595) < ((Float)this.field4596.getValue()).floatValue();
        boolean v6 = (float)(System.currentTimeMillis() - this.field4593) < ((Float)this.field4597.getValue()).floatValue();
        return (v4 || v5) && param1.isAlive() && BackTrack.I.player.age > 10 && v6;
    }

    private Box method1152() {
        if (this.field4589 == null || this.field4590 == null) {
            return new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        }
        Vec3d v1 = this.field4590.getPos();
        double v2 = (double)this.field4589.getWidth() / field4598;
        double v4 = this.field4589.getHeight();
        return new Box(v1.x - v2, v1.y, v1.z - v2, v1.x + v2, v1.y + v4, v1.z + v2);
    }

    private boolean hasValidTarget() {
        return this.field4589 != null && this.field4589.isAlive() && this.method1151(this.field4589);
    }

    @EventHandler(2)
    public void method1153(EventRender3D param1) {
        if (!((Boolean)this.field4599.getValue()).booleanValue()) {
            return;
        }
        if (this.field4589 == null || this.field4590 == null) {
            return;
        }
        if (BackTrack.I.player == null || BackTrack.I.world == null) {
            return;
        }
        MatrixStack v2 = param1.method2348();
        Vec3d v3 = BackTrack.I.gameRenderer.getCamera().getPos();
        v2.push();
        v2.translate(-v3.x, -v3.y, -v3.z);
        Box v4 = this.method1152();
        Render3D.method2407(v2, v4, ColorUtil.method1853(ThemeColors.method3199(MAIN.MODULE_VISUAL), ThemeColors.method3214(MAIN.MODULE_VISUAL) - field4600), 2.0f);
        v2.pop();
    }

    @EventHandler(2)
    public void method1154(EventSwapWorld param1) {
        this.field4591.clear();
        this.field4592.clear();
        this.field4589 = null;
        this.field4590 = null;
    }

    private double method1155(Vec3d param1, Box param2) {
        double v3 = Math.max(param2.minX - param1.x, Math.max(0.0, param1.x - param2.maxX));
        double v5 = Math.max(param2.minY - param1.y, Math.max(0.0, param1.y - param2.maxY));
        double v7 = Math.max(param2.minZ - param1.z, Math.max(0.0, param1.z - param2.maxZ));
        return v3 * v3 + v5 * v5 + v7 * v7;
    }

    public BackTrack() {
        super(field4602, field4603, Combat.Combat);
        this.field4596 = new SliderSetting(field4613, field4614, 0.0f, field4615, field4616);
        this.field4597 = new SliderSetting(field4617, field4618, 0.0f, field4619, field4620);
        this.field4599 = new BooleanSetting(field4621, true);
        this.field4591 = new ConcurrentLinkedQueue();
        this.field4592 = new ConcurrentLinkedQueue();
        this.addSettings(this.field4594, this.field4612, this.field4596, this.field4597, this.field4599);
    }

    @EventHandler(2)
    public void method1157(EventPacket param1) {
        PlaySoundS2CPacket v32;
        if (BackTrack.I.player == null || BackTrack.I.world == null) {
            return;
        }
        if (!param1.method2466() || param1.method2297()) {
            return;
        }
        if (this.method1150() && !this.hasValidTarget()) {
            return;
        }
        Packet v2 = param1.method2467();
        if (v2 instanceof ChatMessageC2SPacket || v2 instanceof GameMessageS2CPacket || v2 instanceof CommandExecutionC2SPacket) {
            return;
        }
        if (v2 instanceof PlaySoundS2CPacket && (v32 = (PlaySoundS2CPacket)v2).getSound().value() == SoundEvents.ENTITY_PLAYER_HURT) {
            return;
        }
        if (v2 instanceof HealthUpdateS2CPacket && ((HealthUpdateS2CPacket)v2).getHealth() <= 0.0f) {
            this.method1160(true);
            return;
        }
        if (this.field4589 != null) {
            boolean v5;
            boolean v32_226 = v2 instanceof EntityS2CPacket && ((EntityS2CPacket)v2).getEntity((World)BackTrack.I.world) == this.field4589;
            boolean v4 = v2 instanceof EntityPositionS2CPacket && ((EntityPositionS2CPacket)v2).entityId() == this.field4589.getId();
            boolean bl = v5 = v2 instanceof EntityTrackerUpdateS2CPacket && ((EntityTrackerUpdateS2CPacket)v2).id() == this.field4589.getId();
            if (v32_226 || v4 || v5) {
                Vec3d v6;
                Objects.requireNonNull(v2);
                Packet v72 = v2;
                if (v72 instanceof EntityS2CPacket v9) {
                    v6 = this.field4590 != null ? this.field4590.withDelta((long)v9.getDeltaX(), (long)v9.getDeltaY(), (long)v9.getDeltaZ()) : null;
                } else if (v72 instanceof EntityPositionS2CPacket v10) {
                    Vec3d v11 = v10.change().position();
                    v6 = new Vec3d(v11.x, v11.y, v11.z);
                } else if (v72 instanceof EntityTrackerUpdateS2CPacket) {
                    v6 = this.field4589.getPos();
                } else {
                    v6 = null;
                }
                if (this.field4590 != null && v6 != null) {
                    this.field4590.setPos(v6);
                }
                if (v6 != null) {
                    double v72_263 = this.method1155(BackTrack.I.player.getPos(), this.method1152());
                    double v9 = this.field4589.squaredDistanceTo((Entity)BackTrack.I.player);
                    if (v9 < v72_263) {
                        this.method1158(true);
                        return;
                    }
                }
            }
        }
        param1.method2298(true);
        this.field4591.add(new PacketRecord(v2, System.currentTimeMillis()));
    }

    private void method1158(boolean param1) {
        this.field4591.removeIf(param2 -> {
            if (param1 || System.currentTimeMillis() - ((PacketRecord)param2).field8075 >= (long)this.field4601) {
                this.field4592.add(((PacketRecord)param2).field8074);
                return true;
            }
            return false;
        });
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.method1160(false);
    }

    @EventHandler(2)
    public void method1159(EventUpdate param1) {
        Packet v2;
        if (BackTrack.I.player == null || BackTrack.I.world == null) {
            this.method1160(true);
            return;
        }
        if (this.hasValidTarget()) {
            this.method1158(false);
        } else {
            this.method1160(false);
        }
        while ((v2 = (Packet)this.field4592.poll()) != null) {
            Packet v3 = v2;
            v3.apply((PacketListener)BackTrack.I.player.networkHandler);
        }
        if (this.method1150()) {
            this.field4601 = ((Float)this.field4612.getValue()).intValue();
        }
    }

    private void method1160(boolean param1) {
        if (param1) {
            this.method1158(true);
        } else {
            this.field4591.clear();
        }
        this.field4589 = null;
        this.field4590 = null;
    }

    static {
        field4602 = "BackTrack";
        field4603 = "Задерживает пакеты для увеличения дальности атаки";
        field4604 = "Дистанция";
        field4605 = 3.0f;
        field4606 = 10.0f;
        field4607 = "Задержка (мс)";
        field4608 = 150.0f;
        field4609 = 10.0f;
        field4610 = 1000.0f;
        field4611 = 10.0f;
        field4613 = "Время удержания (мс)";
        field4614 = 500.0f;
        field4615 = 2000.0f;
        field4616 = 50.0f;
        field4617 = "Работа после атаки (мс)";
        field4618 = 1000.0f;
        field4619 = 5000.0f;
        field4620 = 50.0f;
        field4621 = "Рендер бокса";
        field4600 = 0.2f;
        field4598 = 2.0;
    }
}



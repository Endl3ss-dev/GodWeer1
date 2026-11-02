/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_2394
 *  net.minecraft.class_2398
 *  net.minecraft.class_243
 *  net.minecraft.class_3417
 *  net.minecraft.class_3419
 *  net.minecraft.class_3532
 *  net.minecraft.class_5134
 *  net.minecraft.class_745
 */
package ru.decide.module.impl.player;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_3532;
import net.minecraft.class_5134;
import net.minecraft.class_745;
import ru.decide.Client;
import ru.decide.manager.event_impl.AttackEvent;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.impl.render.TotemGhost;

@ModuleInfo(name="Fake Player", desc="\u0421\u043f\u0430\u0432\u043d\u0438\u0442 \u043a\u043b\u0438\u0435\u043d\u0442\u0441\u043a\u043e\u0433\u043e \u0444\u0435\u0439\u043a-\u0438\u0433\u0440\u043e\u043a\u0430 \u0434\u043b\u044f \u0431\u0438\u0442\u044c\u044f/\u0442\u0435\u0441\u0442\u0430", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public class FakePlayer
extends Module {
    private static final String[] NAMES = new String[]{"Steve", "Alex", "Herobrine", "Nagibator", "Vitalik", "Sanya", "Dimon", "Leha", "KolyaPRO", "Artem", "Nikita", "Timoha", "Zhenya", "MaksFX", "Vladik"};
    private static final float WALK_RADIUS = 2.0f;
    public BooleanSetting walk = new BooleanSetting((Module)this, "\u0425\u043e\u0434\u044c\u0431\u0430", true);
    public BooleanSetting rotate = new BooleanSetting((Module)this, "\u041f\u043e\u0432\u043e\u0440\u043e\u0442 \u043a \u0438\u0433\u0440\u043e\u043a\u0443", true);
    public BooleanSetting totem = new BooleanSetting((Module)this, "\u0422\u043e\u0442\u0435\u043c", false);
    private class_745 fakePlayer;
    private class_243 spawnPosition = class_243.field_1353;
    private class_243 walkAxis = class_243.field_1353;
    private float spawnYaw;
    private float walkPhase;
    private int hitsSinceTotem;

    @Override
    protected void onDisable() {
        this.despawn();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        this.despawn();
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (FakePlayer.mc.field_1724 == null || FakePlayer.mc.field_1687 == null) {
            this.fakePlayer = null;
            return;
        }
        if (this.fakePlayer == null || this.fakePlayer.method_31481()) {
            this.despawn();
            this.spawn();
        }
        this.syncTotem();
        this.move();
    }

    private void spawn() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String name = NAMES[random.nextInt(NAMES.length)] + random.nextInt(10, 100);
        class_745 player = new class_745(FakePlayer.mc.field_1687, new GameProfile(UUID.randomUUID(), name));
        player.method_5838(-random.nextInt(1000000, 2000000));
        this.spawnPosition = new class_243(FakePlayer.mc.field_1724.method_23317(), FakePlayer.mc.field_1724.method_23318(), FakePlayer.mc.field_1724.method_23321());
        this.spawnYaw = FakePlayer.mc.field_1724.method_36454();
        float rad = this.spawnYaw * ((float)Math.PI / 180);
        this.walkAxis = new class_243((double)(-class_3532.method_15362((double)rad)), 0.0, (double)(-class_3532.method_15374((double)rad)));
        this.walkPhase = 0.0f;
        player.method_5808(this.spawnPosition.field_1352, this.spawnPosition.field_1351, this.spawnPosition.field_1350, this.spawnYaw, 0.0f);
        player.method_5847(this.spawnYaw);
        player.method_5636(this.spawnYaw);
        player.method_6033(player.method_6063());
        FakePlayer.mc.field_1687.method_53875((class_1297)player);
        this.fakePlayer = player;
        this.hitsSinceTotem = 0;
        this.syncTotem();
    }

    private void move() {
        if (this.fakePlayer == null) {
            return;
        }
        class_243 vec = this.spawnPosition;
        float yaw = this.spawnYaw;
        float pitch = 0.0f;
        if (this.walk.getValue().booleanValue()) {
            this.walkPhase += 0.1f;
            vec = this.spawnPosition.method_1019(this.walkAxis.method_1021((double)(class_3532.method_15374((double)this.walkPhase) * 2.0f)));
            double dir = (double)class_3532.method_15362((double)this.walkPhase) >= 0.0 ? 1.0 : -1.0;
            class_243 axis = this.walkAxis.method_1021(dir);
            yaw = (float)Math.toDegrees(Math.atan2(-axis.field_1352, axis.field_1350));
        }
        if (this.rotate.getValue().booleanValue() && FakePlayer.mc.field_1724 != null) {
            double dx = FakePlayer.mc.field_1724.method_23317() - this.fakePlayer.method_23317();
            double dy = FakePlayer.mc.field_1724.method_23320() - this.fakePlayer.method_23320();
            double dz = FakePlayer.mc.field_1724.method_23321() - this.fakePlayer.method_23321();
            yaw = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
            pitch = (float)(-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz))));
        }
        this.fakePlayer.method_66233().method_66267(vec, yaw, pitch);
        this.fakePlayer.method_5847(yaw);
    }

    private void syncTotem() {
        if (this.fakePlayer == null) {
            return;
        }
        this.fakePlayer.method_5673(class_1304.field_6171, this.totem.getValue() != false ? new class_1799((class_1935)class_1802.field_8288) : class_1799.field_8037);
        if (!this.totem.getValue().booleanValue()) {
            this.hitsSinceTotem = 0;
            this.fakePlayer.method_6033(this.fakePlayer.method_6063());
        }
    }

    private void despawn() {
        if (this.fakePlayer != null) {
            this.fakePlayer.method_31472();
            this.fakePlayer = null;
        }
    }

    public boolean isFakePlayer(class_1297 entity) {
        return this.fakePlayer != null && entity == this.fakePlayer;
    }

    @EventHandler
    public void onAttack(AttackEvent event) {
        if (this.fakePlayer == null || event.getTarget() != this.fakePlayer) {
            return;
        }
        event.cancel();
        if (FakePlayer.mc.field_1724 == null) {
            return;
        }
        FakePlayer.mc.field_1724.method_6104(class_1268.field_5808);
        this.spawnHitParticles();
        if (this.totem.getValue().booleanValue()) {
            if (++this.hitsSinceTotem >= 3) {
                this.triggerTotemPop();
            } else {
                this.fakePlayer.method_6033(Math.max(1.0f, this.fakePlayer.method_6063() * (float)(3 - this.hitsSinceTotem) / 3.0f));
            }
        }
    }

    private void triggerTotemPop() {
        this.hitsSinceTotem = 0;
        this.fakePlayer.method_6033(1.0f);
        FakePlayer.mc.field_1687.method_8466((class_2394)class_2398.field_11220, true, true, this.fakePlayer.method_23317(), this.fakePlayer.method_23318() + 1.0, this.fakePlayer.method_23321(), 0.0, 0.15, 0.0);
        FakePlayer.mc.field_1687.method_8486(this.fakePlayer.method_23317(), this.fakePlayer.method_23318(), this.fakePlayer.method_23321(), class_3417.field_14931, class_3419.field_15248, 1.0f, 1.0f, false);
        TotemGhost ghost = Client.get().moduleManager().get(TotemGhost.class);
        if (ghost instanceof TotemGhost) {
            TotemGhost totemGhost = ghost;
            totemGhost.spawnGhost((class_1297)this.fakePlayer);
        }
        this.fakePlayer.method_6033(this.fakePlayer.method_6063());
    }

    private void spawnHitParticles() {
        if (FakePlayer.mc.field_1724 == null) {
            return;
        }
        double damage = FakePlayer.mc.field_1724.method_45325(class_5134.field_23721);
        int count = Math.max(1, (int)(damage * 0.5));
        for (int i = 0; i < count; ++i) {
            FakePlayer.mc.field_1687.method_8466((class_2394)class_2398.field_11209, true, true, this.fakePlayer.method_23317(), this.fakePlayer.method_23318() + 0.5, this.fakePlayer.method_23321(), FakePlayer.mc.field_1687.field_9229.method_43059() * 0.1, 0.0, FakePlayer.mc.field_1687.field_9229.method_43059() * 0.1);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1937
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2663
 */
package ru.decide.module.impl.render;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import ru.decide.manager.event_impl.AttackEvent;
import ru.decide.manager.event_impl.EventPacket;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.shader.ScanWorldRenderer;

@ModuleInfo(name="Scan World", category=Category.VISUALS, desc="\u0421\u043a\u0430\u043d\u0438\u0440\u0443\u044e\u0449\u0430\u044f \u0432\u043e\u043b\u043d\u0430 \u043f\u043e \u043c\u0438\u0440\u0443")
@Environment(value=EnvType.CLIENT)
public class ScanWorld
extends Module {
    public ModeSetting typeColor = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0422\u0435\u043c\u0430", "\u0421\u0432\u043e\u0439");
    public ColorSetting tintColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442", -11477761).setVisible(() -> this.typeColor.is("\u0421\u0432\u043e\u0439"));
    public BooleanSetting periodic = new BooleanSetting((Module)this, "\u041f\u0435\u0440\u0438\u043e\u0434\u0438\u0447\u0435\u0441\u043a\u0438", true);
    public SliderSetting interval = new SliderSetting(this, "\u0418\u043d\u0442\u0435\u0440\u0432\u0430\u043b", 3.0f, 1.0f, 15.0f, 0.5f).setVisible(() -> this.periodic.getValue());
    public BooleanSetting onTotem = new BooleanSetting((Module)this, "\u041f\u0440\u0438 \u0442\u043e\u0442\u0435\u043c\u0435", true);
    public BooleanSetting onKill = new BooleanSetting((Module)this, "\u041f\u0440\u0438 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0435", true);
    public SliderSetting duration = new SliderSetting(this, "\u0414\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c", 2.5f, 0.5f, 8.0f, 0.1f);
    public SliderSetting width = new SliderSetting(this, "\u0428\u0438\u0440\u0438\u043d\u0430", 10.0f, 1.0f, 32.0f, 1.0f);
    public SliderSetting maxRadius = new SliderSetting(this, "\u0420\u0430\u0434\u0438\u0443\u0441", 80.0f, 10.0f, 256.0f, 2.0f);
    private static final byte DEATH_STATUS = 3;
    private static final byte TOTEM_STATUS = 35;
    private static final long KILL_WINDOW_MS = 6500L;
    private long lastTriggerTime;
    private int lastTargetId = -1;
    private long lastAttackTime;

    public int getColor() {
        if (this.typeColor.is("\u0422\u0435\u043c\u0430")) {
            return ColorUtil.getClientColor1(1);
        }
        return this.tintColor.getValue();
    }

    @Override
    protected void onEnable() {
        this.lastTargetId = -1;
        if (ScanWorld.mc.field_1724 != null) {
            this.lastTriggerTime = System.currentTimeMillis();
            ScanWorldRenderer.getInstance().startScan(ScanWorld.mc.field_1724.method_73189());
        }
    }

    @Override
    protected void onDisable() {
        ScanWorldRenderer.getInstance().stopScan();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        this.lastTargetId = -1;
        ScanWorldRenderer.getInstance().stopScan();
    }

    @EventHandler
    public void onTick(EventTick e) {
        long intervalMs;
        if (ScanWorld.mc.field_1724 == null || ScanWorld.mc.field_1687 == null) {
            return;
        }
        if (!this.periodic.getValue().booleanValue()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastTriggerTime >= (intervalMs = (long)(this.interval.getValue().floatValue() * 1000.0f))) {
            class_243 center = ScanWorld.mc.field_1724.method_73189();
            ScanWorldRenderer.getInstance().startScan(center);
            this.lastTriggerTime = now;
        }
    }

    @EventHandler
    public void onAttack(AttackEvent e) {
        class_1309 living;
        class_1297 class_12972 = e.getTarget();
        if (!(class_12972 instanceof class_1309) || (living = (class_1309)class_12972) == ScanWorld.mc.field_1724) {
            return;
        }
        this.lastTargetId = living.method_5628();
        this.lastAttackTime = System.currentTimeMillis();
    }

    @EventHandler
    public void onPacket(EventPacket e) {
        class_2596<?> class_25962 = e.getPacket();
        if (!(class_25962 instanceof class_2663)) {
            return;
        }
        class_2663 packet = (class_2663)class_25962;
        if (ScanWorld.mc.field_1687 == null) {
            return;
        }
        if (packet.method_11470() == 35 && this.onTotem.getValue().booleanValue()) {
            entity = packet.method_11469((class_1937)ScanWorld.mc.field_1687);
            if (entity == null) {
                return;
            }
            ScanWorldRenderer.getInstance().startScan(entity.method_73189());
        } else if (packet.method_11470() == 3 && this.onKill.getValue().booleanValue()) {
            entity = packet.method_11469((class_1937)ScanWorld.mc.field_1687);
            if (!(entity instanceof class_1309)) {
                return;
            }
            class_1309 living = (class_1309)entity;
            if (living.method_5628() != this.lastTargetId) {
                return;
            }
            if (System.currentTimeMillis() - this.lastAttackTime > 6500L) {
                return;
            }
            this.lastTargetId = -1;
            ScanWorldRenderer.getInstance().startScan(living.method_73189());
        }
    }

    @EventHandler
    public void onRender3D(EventRender3D e) {
        if (ScanWorld.mc.field_1724 == null || ScanWorld.mc.field_1687 == null) {
            return;
        }
        int col = this.getColor();
        int r = col >> 16 & 0xFF;
        int g = col >> 8 & 0xFF;
        int b = col & 0xFF;
        ScanWorldRenderer renderer = ScanWorldRenderer.getInstance();
        renderer.setDuration(this.duration.getValue().floatValue());
        renderer.setWidth(this.width.getValue().floatValue());
        renderer.setMaxRadius(this.maxRadius.getValue().floatValue());
        renderer.setOuterColor(new Color(r, g, b, 90));
        renderer.setMidColor(new Color(r, g, b, 90));
        renderer.setInnerColor(new Color(r, g, b, 180));
        renderer.setScanlineColor(new Color(r, g, b, 90));
        renderer.render(e.getMatrixStack().method_23760().method_23761(), ScanWorld.mc.field_1773.method_22973(((Integer)ScanWorld.mc.field_1690.method_41808().method_41753()).floatValue()));
    }
}


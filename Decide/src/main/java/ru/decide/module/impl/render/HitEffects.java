/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  net.minecraft.class_3419
 *  net.minecraft.class_9799
 */
package ru.decide.module.impl.render;

import java.util.Map;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_9799;
import ru.decide.manager.event_impl.AttackEvent;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.impl.render.hiteffects.HitEffectSystem;

@ModuleInfo(name="Hit Effects", desc="\u042d\u0444\u0444\u0435\u043a\u0442\u044b \u0443\u0434\u0430\u0440\u0430: \u0440\u0430\u0437\u0440\u0435\u0437, \u0438\u0441\u043a\u0440\u044b, \u043f\u0438\u043a\u0441\u0435\u043b\u044c\u043d\u044b\u0439 \u043a\u0440\u0438\u0442, \u0441\u0435\u0440\u0434\u0435\u0447\u043a\u0438 \u0438 \u043a\u043e\u043c\u0438\u043a\u0441", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class HitEffects
extends Module {
    private static final double REACH = 8.0;
    private static final Map<String, class_3414> SOUNDS = Map.of("\u0414\u0437\u044b\u043d\u044c", class_3417.field_15224, "\u041a\u043e\u043b\u043e\u043a\u043e\u043b\u044c\u0447\u0438\u043a", (class_3414)class_3417.field_14793.comp_349(), "\u041f\u043b\u0438\u043d\u043a", (class_3414)class_3417.field_14622.comp_349(), "\u041f\u0435\u0440\u0435\u0437\u0432\u043e\u043d", (class_3414)class_3417.field_14725.comp_349(), "\u041e\u043f\u044b\u0442", class_3417.field_14627, "\u041a\u0440\u0438\u0442", class_3417.field_15016);
    public ModeSetting type = new ModeSetting((Module)this, "\u0412\u0438\u0434", "\u0420\u0430\u0437\u0440\u0435\u0437", "\u0418\u0441\u043a\u0440\u044b", "\u041f\u0438\u043a\u0441\u0435\u043b\u044c\u043d\u044b\u0439 \u043a\u0440\u0438\u0442", "\u041f\u0438\u043a\u0441\u0435\u043b\u044c\u043d\u044b\u0435 \u0441\u0435\u0440\u0434\u0435\u0447\u043a\u0438", "\u041a\u043e\u043c\u0438\u043a\u0441");
    public SliderSetting size = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440", 1.0f, 0.5f, 2.5f, 0.05f);
    public SliderSetting amount = new SliderSetting(this, "\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e", 1.0f, 0.3f, 3.0f, 0.1f).setVisible(() -> !this.type.is("\u0420\u0430\u0437\u0440\u0435\u0437") && !this.type.is("\u041a\u043e\u043c\u0438\u043a\u0441"));
    public ModeSetting sound = new ModeSetting((Module)this, "\u0417\u0432\u0443\u043a", "\u0411\u0435\u0437 \u0437\u0432\u0443\u043a\u0430", "\u0414\u0437\u044b\u043d\u044c", "\u041a\u043e\u043b\u043e\u043a\u043e\u043b\u044c\u0447\u0438\u043a", "\u041f\u043b\u0438\u043d\u043a", "\u041f\u0435\u0440\u0435\u0437\u0432\u043e\u043d", "\u041e\u043f\u044b\u0442", "\u041a\u0440\u0438\u0442");
    public SliderSetting volume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c", 0.6f, 0.1f, 1.0f, 0.05f).setVisible(() -> !this.sound.is("\u0411\u0435\u0437 \u0437\u0432\u0443\u043a\u0430"));
    private final HitEffectSystem system = new HitEffectSystem();
    private final class_9799 allocator = new class_9799(262144);
    private final Random random = new Random();

    @Override
    protected void onDisable() {
        this.system.clear();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        this.allocator.method_60809();
        this.system.clear();
    }

    @EventHandler
    public void onAttack(AttackEvent event) {
        class_243 offset;
        class_243 hit;
        class_1297 target = event.getTarget();
        if (target == null || HitEffects.mc.field_1724 == null || HitEffects.mc.field_1687 == null) {
            return;
        }
        class_243 eye = HitEffects.mc.field_1724.method_33571();
        class_243 gaze = HitEffects.mc.field_1724.method_5828(1.0f);
        class_238 box = target.method_5829();
        class_243 effectHit = hit = box.method_992(eye, eye.method_1019(gaze.method_1021(8.0))).orElse(box.method_1005());
        if (this.type.is("\u0420\u0430\u0437\u0440\u0435\u0437") && (offset = eye.method_1020(hit)).method_1027() > 1.0E-6) {
            effectHit = hit.method_1019(offset.method_1029().method_1021(0.15 * (double)this.size.getValue().floatValue()));
        }
        this.system.configure(this.size.getValue().floatValue());
        this.system.attack(this.type.getValue(), effectHit, gaze, box, HitEffects.mc.field_1724.method_36454(), this.size.getValue().floatValue(), this.amount.getValue().floatValue());
        class_3414 soundEvent = SOUNDS.get(this.sound.getValue());
        if (soundEvent != null) {
            HitEffects.mc.field_1687.method_43128(null, hit.field_1352, hit.field_1351, hit.field_1350, soundEvent, class_3419.field_15248, this.volume.getValue().floatValue(), 0.9f + this.random.nextFloat() * 0.3f);
        }
    }

    @EventHandler
    public void onTick(EventTick event) {
        if (HitEffects.mc.field_1687 == null || mc.method_1493()) {
            return;
        }
        this.system.tick();
    }

    @EventHandler
    public void onRender(EventRender3D event) {
        this.system.configure(this.size.getValue().floatValue());
        this.system.render(event, this.allocator);
    }
}


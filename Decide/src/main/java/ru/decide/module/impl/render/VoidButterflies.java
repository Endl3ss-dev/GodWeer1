/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 */
package ru.decide.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.impl.render.voidbutterflies.VoidButterflyRenderer;
import ru.decide.utils.colors.ColorUtil;

@ModuleInfo(name="Void Butterflies", desc="\u0411\u0430\u0431\u043e\u0447\u043a\u0438 \u043f\u0443\u0441\u0442\u043e\u0442\u044b \u043d\u0430 \u0432\u0441\u0435\u0445 \u0433\u0440\u0430\u043d\u044f\u0445 \u0431\u043b\u043e\u043a\u043e\u0432", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class VoidButterflies
extends Module {
    public SliderSetting radius = new SliderSetting(this, "\u0420\u0430\u0434\u0438\u0443\u0441", 12.0f, 4.0f, 24.0f, 1.0f);
    public SliderSetting density = new SliderSetting(this, "\u041f\u043b\u043e\u0442\u043d\u043e\u0441\u0442\u044c", 1.0f, 0.25f, 2.0f, 0.25f);
    public ModeSetting colorMode = new ModeSetting((Module)this, "\u0426\u0432\u0435\u0442", "\u0422\u0435\u043c\u0430", "\u0421\u0432\u043e\u0439");
    public ColorSetting customColor = new ColorSetting((Module)this, "\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442", -6657025).setVisible(() -> this.colorMode.is("\u0421\u0432\u043e\u0439"));
    private final VoidButterflyRenderer renderer = new VoidButterflyRenderer();

    @Override
    protected void onDisable() {
        super.onDisable();
        this.renderer.reset();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        this.renderer.reset();
    }

    @EventHandler
    public void onTick(EventTick e) {
        this.renderer.update(this.radius.getValue().floatValue(), this.density.getValue().floatValue());
    }

    @EventHandler
    public void onRender3D(EventRender3D e) {
        if (VoidButterflies.mc.field_1755 != null) {
            return;
        }
        Matrix4f projection = e.getProjectionMatrix();
        if (projection == null) {
            return;
        }
        int base = this.colorMode.is("\u0421\u0432\u043e\u0439") ? this.customColor.getValue() : ColorUtil.getClientColor1(1);
        int second = ColorUtil.multBright(base, 0.72f);
        this.renderer.render(e.getMatrixStack().method_23760().method_23761(), projection, e.getTickDelta(), this.radius.getValue().floatValue(), base, second);
    }
}


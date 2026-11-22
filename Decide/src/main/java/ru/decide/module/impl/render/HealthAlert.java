/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3x2fStack
 */
package ru.decide.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix3x2fStack;
import ru.decide.manager.event_impl.EventDisplay;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.RenderUtil;

@ModuleInfo(name="Health Alert", category=Category.VISUALS, desc="\u0412\u0438\u0437\u0443\u0430\u043b\u044c\u043d\u044b\u0439 \u044d\u0444\u0444\u0435\u043a\u0442 \u043a\u043e\u0433\u0434\u0430 \u043c\u0430\u043b\u043e \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f")
@Environment(value=EnvType.CLIENT)
public class HealthAlert
extends Module {
    public SliderSetting heath = new SliderSetting(this, "\u041a\u043e\u043b \u0432 \u0445\u043f", 5.0f, 1.0f, 15.0f, 1.0f);
    public Animation animation_1 = new Animation();
    public Animation animation_2 = new Animation();
    public Animation animation_3 = new Animation();

    @EventHandler
    public void onDisplay(EventDisplay e) {
        Matrix3x2fStack matrixStack = e.getDrawContext().method_51448();
        matrixStack.pushMatrix();
        if (HealthAlert.mc.field_1724 != null && HealthAlert.mc.field_1687 != null) {
            float playerallHP = HealthAlert.mc.field_1724.method_6032() + HealthAlert.mc.field_1724.method_6067();
            boolean effect = false;
            if (playerallHP < this.heath.getValue().floatValue()) {
                effect = true;
            }
            this.animation_1.update();
            this.animation_1.run(effect ? 1.0 : 0.0, (double)0.15f, Easings.SINE_OUT);
            if (this.animation_1.get() > 0.0f) {
                float alpha = (float)((double)0.6f + (double)0.15f * Math.sin((double)System.currentTimeMillis() / 150.0));
                float alpha2 = (float)(0.0 + (double)0.2f * Math.sin((double)System.currentTimeMillis() / 300.0));
                RenderUtil.Render2D.gradientRect(0.0f, 0.0f, mc.method_22683().method_4486(), mc.method_22683().method_4502(), new int[]{ColorUtil.getColor(0, alpha * this.animation_1.get()), 0, 0, 0}, 0.0f);
                RenderUtil.Render2D.gradientRect(0.0f, 0.0f, mc.method_22683().method_4486(), mc.method_22683().method_4502(), new int[]{0, ColorUtil.getColor(0, alpha * this.animation_1.get()), 0, 0}, 0.0f);
                RenderUtil.Render2D.gradientRect(0.0f, 0.0f, mc.method_22683().method_4486(), mc.method_22683().method_4502(), new int[]{0, 0, ColorUtil.getColor(0, alpha * this.animation_1.get()), 0}, 0.0f);
                RenderUtil.Render2D.gradientRect(0.0f, 0.0f, mc.method_22683().method_4486(), mc.method_22683().method_4502(), new int[]{0, 0, 0, ColorUtil.getColor(0, alpha * this.animation_1.get())}, 0.0f);
                RenderUtil.Render2D.gradientRect(0.0f, 0.0f, mc.method_22683().method_4486(), mc.method_22683().method_4502(), new int[]{ColorUtil.getColor(255, 0, 0, alpha2 * this.animation_1.get()), 0, 0, 0}, 0.0f);
                RenderUtil.Render2D.gradientRect(0.0f, 0.0f, mc.method_22683().method_4486(), mc.method_22683().method_4502(), new int[]{0, ColorUtil.getColor(255, 0, 0, alpha2 * this.animation_1.get()), 0, 0}, 0.0f);
                RenderUtil.Render2D.gradientRect(0.0f, 0.0f, mc.method_22683().method_4486(), mc.method_22683().method_4502(), new int[]{0, 0, ColorUtil.getColor(255, 0, 0, alpha2 * this.animation_1.get()), 0}, 0.0f);
                RenderUtil.Render2D.gradientRect(0.0f, 0.0f, mc.method_22683().method_4486(), mc.method_22683().method_4502(), new int[]{0, 0, 0, ColorUtil.getColor(255, 0, 0, alpha2 * this.animation_1.get())}, 0.0f);
            }
        }
        matrixStack.popMatrix();
    }
}


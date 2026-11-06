/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_9799
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package ru.decide.module.impl.render.targetesp;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_4597;
import net.minecraft.class_9799;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.module.impl.render.targeteffects.CircleStyle;
import ru.decide.module.impl.render.targeteffects.DizzyStyle;
import ru.decide.module.impl.render.targeteffects.HeartsStyle;
import ru.decide.module.impl.render.targeteffects.MarkerStyle;
import ru.decide.module.impl.render.targeteffects.SoulsStyle;
import ru.decide.module.impl.render.targeteffects.TargetEffectFrame;
import ru.decide.module.impl.render.targeteffects.TargetEffectPalette;
import ru.decide.module.impl.render.targeteffects.TargetEffectStyle;
import ru.decide.utils.colors.ColorUtil;

@Environment(value=EnvType.CLIENT)
public final class TargetEspHoverRenderer {
    private final SoulsStyle souls = new SoulsStyle();
    private final CircleStyle circle = new CircleStyle();
    private final DizzyStyle dizzy = new DizzyStyle();
    private final MarkerStyle marker = new MarkerStyle();
    private final HeartsStyle hearts = new HeartsStyle();
    private final TargetEffectStyle[] allStyles = new TargetEffectStyle[]{this.souls, this.circle, this.dizzy, this.marker, this.hearts};
    private float clock;
    private float lastClock;
    private double groundY;

    public static boolean supports(String mode) {
        return mode.equals("\u0414\u0443\u0448\u0438") || mode.equals("\u041a\u0440\u0443\u0433") || mode.equals("\u041f\u0438\u043a\u0441\u0435\u043b\u044c\u043d\u044b\u0435 \u0437\u0432\u0451\u0437\u0434\u043e\u0447\u043a\u0438") || mode.equals("\u041f\u0438\u043a\u0441\u0435\u043b\u044c\u043d\u0430\u044f \u043c\u0435\u0442\u043a\u0430") || mode.equals("\u041f\u0438\u043a\u0441\u0435\u043b\u044c\u043d\u044b\u0435 \u0441\u0435\u0440\u0434\u0435\u0447\u043a\u0438");
    }

    public void tick(String mode, class_1309 target, class_243 eye, class_243 gaze, float speed, boolean particles, float size, String colorMode, int color1, int color2) {
        this.lastClock = this.clock;
        this.clock += speed / 20.0f;
        for (TargetEffectStyle style : this.allStyles) {
            style.tick();
            style.dodge(eye, gaze);
        }
        if (target == null || !TargetEspHoverRenderer.supports(mode)) {
            return;
        }
        TargetEffectStyle style = this.style(mode);
        style.setPalette(this.palette(style, colorMode, color1, color2));
        this.groundY = this.groundBelow(target);
        if (particles) {
            style.sprinkle(target, this.radius(target, size));
        }
    }

    public void render(EventRender3D event, class_1309 target, float appearance, boolean ground, boolean trails, float size, String mode, String colorMode, int color1, int color2, class_9799 allocator) {
        float alpha = class_3532.method_15363((float)appearance, (float)0.0f, (float)1.0f);
        boolean drawTarget = target != null && alpha > 0.001f;
        boolean hasParticles = false;
        for (TargetEffectStyle style : this.allStyles) {
            if (!style.hasParticles()) continue;
            hasParticles = true;
            break;
        }
        if (!drawTarget && !hasParticles) {
            return;
        }
        float partialTick = event.getTickDelta();
        float eased = alpha * alpha * (3.0f - 2.0f * alpha);
        float clockValue = class_3532.method_16439((float)partialTick, (float)this.lastClock, (float)this.clock);
        class_243 lerped = target == null ? class_243.field_1353 : target.method_30950(partialTick);
        class_243 camera = this.mcCamera();
        float groundOffset = target == null ? 0.0f : (float)(this.groundY - lerped.field_1351);
        float groundAlpha = eased * class_3532.method_15363((float)(1.0f + groundOffset / 1.5f), (float)0.0f, (float)1.0f);
        TargetEffectStyle style = this.style(mode);
        TargetEffectPalette palette = style.defaultPalette();
        if (colorMode.equals("\u0422\u0435\u043c\u0430")) {
            int base = ColorUtil.getClientColor1(1);
            palette = TargetEffectPalette.gradient(base, ColorUtil.overCol(base, -1, 0.35f));
        } else if (colorMode.equals("\u0421\u0432\u043e\u0439")) {
            palette = TargetEffectPalette.gradient(color1, color2);
        }
        style.setPalette(palette);
        Quaternionf rotation = new Quaternionf((Quaternionfc)class_310.method_1551().field_1773.method_19418().method_23767());
        class_4597.class_4598 provider = class_4597.method_22991((class_9799)allocator);
        TargetEffectFrame frame = new TargetEffectFrame(event.getMatrixStack(), provider, camera, rotation, target, lerped.field_1352 - camera.field_1352, lerped.field_1351 - camera.field_1351, lerped.field_1350 - camera.field_1350, groundOffset, target == null ? 0.0f : this.radius(target, size), clockValue, drawTarget ? eased : 0.0f, groundAlpha, partialTick, palette);
        if (drawTarget) {
            style.render(frame, ground, trails);
        }
        for (TargetEffectStyle candidate : this.allStyles) {
            if (drawTarget && candidate == style) continue;
            candidate.renderParticles(frame);
        }
        provider.method_22993();
    }

    public void clear() {
        this.clock = 0.0f;
        this.lastClock = 0.0f;
        for (TargetEffectStyle style : this.allStyles) {
            style.clear();
        }
    }

    private TargetEffectStyle style(String mode) {
        if (mode.equals("\u041a\u0440\u0443\u0433")) {
            return this.circle;
        }
        if (mode.equals("\u041f\u0438\u043a\u0441\u0435\u043b\u044c\u043d\u044b\u0435 \u0437\u0432\u0451\u0437\u0434\u043e\u0447\u043a\u0438")) {
            return this.dizzy;
        }
        if (mode.equals("\u041f\u0438\u043a\u0441\u0435\u043b\u044c\u043d\u0430\u044f \u043c\u0435\u0442\u043a\u0430")) {
            return this.marker;
        }
        if (mode.equals("\u041f\u0438\u043a\u0441\u0435\u043b\u044c\u043d\u044b\u0435 \u0441\u0435\u0440\u0434\u0435\u0447\u043a\u0438")) {
            return this.hearts;
        }
        return this.souls;
    }

    private TargetEffectPalette palette(TargetEffectStyle style, String colorMode, int color1, int color2) {
        if (colorMode.equals("\u0422\u0435\u043c\u0430")) {
            int base = ColorUtil.getClientColor1(1);
            return TargetEffectPalette.gradient(base, ColorUtil.overCol(base, -1, 0.35f));
        }
        if (colorMode.equals("\u0421\u0432\u043e\u0439")) {
            return TargetEffectPalette.gradient(color1, color2);
        }
        return style.defaultPalette();
    }

    private float radius(class_1309 entity, float size) {
        return (entity.method_17681() * 0.5f + 0.45f) * size;
    }

    private double groundBelow(class_1309 entity) {
        class_243 center = entity.method_5829().method_1005();
        class_3965 result = class_310.method_1551().field_1687.method_17742(new class_3959(center.method_1031(0.0, 0.5, 0.0), center.method_1031(0.0, -1.5, 0.0), class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)entity));
        return result.method_17784().field_1351;
    }

    private class_243 mcCamera() {
        return class_310.method_1551().field_1773.method_19418().method_71156();
    }
}


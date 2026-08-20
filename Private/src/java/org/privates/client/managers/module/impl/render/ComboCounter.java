package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.math.Interpolator;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.Project;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.font.Font;
import org.privates.client.utils.render.font.Fonts;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ComboCounter", category = Category.RENDER, description = "Растущий визуальный combo счетчик над таргетом")
public class ComboCounter extends Module {

    final SliderSetting resetMs = new SliderSetting(this, "Сброс (мс)", 1400f, 500f, 3000f, 50f);
    final SliderSetting scaleBoost = new SliderSetting(this, "Сила анимации", 0.35f, 0.1f, 0.8f, 0.05f);
    final SliderSetting yOffset = new SliderSetting(this, "Высота", 0.35f, 0.05f, 1.2f, 0.05f);
    final BooleanSetting useAuraTarget = new BooleanSetting(this, "Только Aura target", true);

    int combo;
    long lastHitTime;
    float popAnim;
    int lastTargetId = -1;

    @EventHandler
    public void onAttack(AttackEvent event) {
        if (mc.player == null || mc.world == null) return;
        Entity raw = event.getTarget();
        if (!(raw instanceof LivingEntity target) || target == mc.player) return;

        int id = target.getEntityId();
        long now = System.currentTimeMillis();
        if (id != lastTargetId || now - lastHitTime > resetMs.getValue()) {
            combo = 0;
        }

        combo++;
        popAnim = 1.0f;
        lastHitTime = now;
        lastTargetId = id;
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (mc.player == null || mc.world == null) return;

        long now = System.currentTimeMillis();
        if (combo <= 0 || now - lastHitTime > resetMs.getValue()) {
            combo = 0;
            return;
        }

        LivingEntity target = getRenderTarget();
        if (target == null || !target.isAlive()) return;

        float partial = event.getPartialTicks();
        double x = Interpolator.lerp(target.lastTickPosX, target.getPosX(), partial);
        double y = Interpolator.lerp(target.lastTickPosY, target.getPosY(), partial) + target.getHeight() + yOffset.getValue();
        double z = Interpolator.lerp(target.lastTickPosZ, target.getPosZ(), partial);

        org.joml.Vector2f screen = Project.project2D(new Vector3d(x, y, z));
        if (screen.x() == Float.MAX_VALUE || screen.y() == Float.MAX_VALUE) return;

        popAnim = Math.max(0f, popAnim - 0.045f);
        float scale = 1.0f + popAnim * scaleBoost.getValue();

        String text = combo + "x COMBO";
        Font font = Fonts.SF_BOLD;
        MatrixStack ms = event.getMatrix();
        float textSize = 8.0f;
        float textW = font.getWidth(text, textSize);
        float boxW = textW + 10f;
        float boxH = 13f;
        float drawX = screen.x() - boxW / 2f;
        float drawY = screen.y() - boxH / 2f;

        int accent = getComboColor(combo);
        int bg = ColorUtil.getColor(8, 8, 12, 120);
        int bg2 = ColorUtil.getColor(15, 15, 22, 145);

        ms.push();
        ms.translate(screen.x(), screen.y(), 0);
        ms.scale(scale, scale, 1f);
        ms.translate(-screen.x(), -screen.y(), 0);

        RectUtil.drawRect(ms, drawX, drawY, boxW, boxH, bg, bg, bg2, bg2, true, false);
        font.drawCenterShadow(ms, text, screen.x(), drawY + 2.5f, accent, textSize);

        ms.pop();
    }

    private LivingEntity getRenderTarget() {
        AuraNew aura = AuraNew.getInstance();
        if (useAuraTarget.getValue()) {
            return aura != null ? aura.target() : null;
        }
        if (aura != null && aura.target() != null) return aura.target();
        if (mc.pointedEntity instanceof LivingEntity living && living != mc.player) return living;
        return null;
    }

    private int getComboColor(int value) {
        int theme = Theme.getInstance().clientColor();
        int hot = ColorUtil.getColor(255, 70, 70, 255);
        float t = MathHelper.clamp(value / 12.0f, 0.0f, 1.0f);
        int r = (int) MathHelper.lerp(t, ColorUtil.red(theme), ColorUtil.red(hot));
        int g = (int) MathHelper.lerp(t, ColorUtil.green(theme), ColorUtil.green(hot));
        int b = (int) MathHelper.lerp(t, ColorUtil.blue(theme), ColorUtil.blue(hot));
        return ColorUtil.getColor(r, g, b, 255);
    }
}


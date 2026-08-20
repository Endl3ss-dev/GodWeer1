package org.privates.client.screen.hud.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL13;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.combat.KillAura;
import org.privates.client.managers.module.impl.combat.TriggerBot;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.NumberTransition;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.shader.ShaderManager;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TargetHudRenderer4 extends AbstractHud implements IRenderer, IConfigurableHud {

    final DragSetting drag;
    final Animation openAnimation = new Animation();
    final Animation healthAnimation = new Animation();
    final Animation absorptionAnimation = new Animation();
    final Animation armorAnimation = new Animation();
    final StopWatch time = new StopWatch();

    public boolean inWorld;
    public LivingEntity target;
    private LivingEntity previousTarget;
    private boolean hasArmor = false;
    private boolean previousHasArmor = false;
    private boolean shouldRenderArmor = false;

    private final Animation[] armorItemAnimations = {new Animation(), new Animation(), new Animation(), new Animation()};
    private final long[] armorItemStartTimes = new long[4];
    private final long ARMOR_ITEM_DELAY = 150;
    private long armorAnimationStartTime = 0;

    // Точные размеры и отступы как у alekdlc
    final float radius = 3f;
    final float minWidth = 100f;
    final float baseHeight = 25f;
    final float margin = 4f;
    final float avatarSize = 18f;
    final float nameOffsetX = 5f;
    final float hpHeight = 3.6f;
    final float healthBarOffset = 6f;
    final float armorItemSize = 16f;
    final float armorItemSpacing = 3f;
    final float armorOffsetY = 6f;

    private static final ResourceLocation WHITE_PIXEL = new ResourceLocation("minecraft", "textures/misc/white.png");

    private float tempHp = 0f;

    public TargetHudRenderer4(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return List.of();
    }

    @Override
    public void render(Render2DEvent event) {
        boolean inChat = mc.currentScreen instanceof ChatScreen;

        if (inChat) {
            target = mc.player;
            time.reset();
        } else {
            if (mc.pointedEntity instanceof PlayerEntity p && TriggerBot.getInstance().isEnabled()) {
                target = p;
                time.reset();
            }
            if (KillAura.getInstance().isEnabled() && KillAura.getInstance().target() != null) {
                target = KillAura.getInstance().target();
                time.reset();
            }
        }

        if (target != previousTarget) {
            resetArmorAnimations();
            previousTarget = target;
        }

        if (target == null) {
            clearTargetState();
            return;
        }

        inWorld = target == mc.player && inChat || (mc.world != null && StreamSupport.stream(mc.world.getAllEntities().spliterator(), true).anyMatch(e -> e.equals(target)));

        openAnimation.update();
        healthAnimation.update();
        absorptionAnimation.update();
        armorAnimation.update();
        for (Animation a : armorItemAnimations) a.update();

        boolean out = !inWorld || time.finished(400);
        shouldRenderArmor = !out;

        openAnimation.run(out ? 0 : 1, 0.5, Easings.EXPO_OUT, false);

        if (!shouldRenderArmor) {
            armorAnimation.set(0);
            for (Animation a : armorItemAnimations) a.set(0);
        }

        if (openAnimation.getValue() <= 0) {
            clearTargetState();
            return;
        }

        boolean currentHasArmor = checkHasArmor();
        if (currentHasArmor != previousHasArmor) {
            if (currentHasArmor) startArmorAnimation();
            else stopArmorAnimation();
            previousHasArmor = currentHasArmor;
        }
        hasArmor = currentHasArmor;
        armorAnimation.run(hasArmor ? 1 : 0, 0.4, Easings.BACK_OUT, true);
        if (hasArmor && armorAnimation.getValue() > 0.1) updateArmorItemAnimations();

        drawDefault(event);
    }

    private void resetArmorAnimations() {
        armorAnimation.setValue(0);
        for (int i = 0; i < 4; i++) {
            armorItemAnimations[i].setValue(0);
            armorItemStartTimes[i] = 0;
        }
        armorAnimationStartTime = 0;
        previousHasArmor = false;
    }

    private void startArmorAnimation() {
        armorAnimationStartTime = System.currentTimeMillis();
        for (int i = 0; i < 4; i++) {
            armorItemAnimations[i].setValue(0);
            armorItemStartTimes[i] = armorAnimationStartTime + i * ARMOR_ITEM_DELAY;
        }
    }

    private void stopArmorAnimation() {
        for (Animation a : armorItemAnimations) a.run(0, 0.3, Easings.CUBIC_IN, true);
    }

    private void updateArmorItemAnimations() {
        long now = System.currentTimeMillis();
        for (int i = 0; i < 4; i++) {
            if (armorItemStartTimes[i] > 0 && now >= armorItemStartTimes[i] && armorItemAnimations[i].getValue() == 0) {
                armorItemAnimations[i].run(1, 0.5, Easings.BACK_OUT, true);
            }
        }
    }

    private boolean checkHasArmor() {
        if (!(target instanceof PlayerEntity p)) return false;
        for (int i = 0; i < 4; i++) if (!p.inventory.armorInventory.get(i).isEmpty()) return true;
        return false;
    }

    private float getArmorDurability(ItemStack stack) {
        if (stack.isEmpty() || !stack.isDamageable()) return 100f;
        return ((float) (stack.getMaxDamage() - stack.getDamage()) / stack.getMaxDamage()) * 100f;
    }

    private void drawHeadWithShader(MatrixStack m, ResourceLocation tex, float x, float y, float size, float radiusPx, float alpha, int u, int v, int w, int h, int color) {
        ShaderManager shader = ShaderManager.roundedHeadShader2;
        if (shader == null) {
            AbstractGui.blit(m, (int) x, (int) y, (int) size, (int) size, u, v, w, h, 64, 64);
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        mc.getTextureManager().bindTexture(tex);
        shader.load();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        RenderSystem.bindTexture(mc.getTextureManager().getTexture(tex).getGlTextureId());

        shader.setUniformi("texture", 0);
        shader.setUniformf("size", size, size);
        shader.setUniformf("radius", radiusPx);

        int a = (int) (alpha * 255f) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        shader.setUniformf("color", r / 255f, g / 255f, b / 255f, a / 255f);

        shader.setUniformf("cutZone", u, u + w, v, v + h);
        shader.setUniformf("texSize", 64f, 64f);

        RectUtil.setupRenderRect(true, false);
        Matrix4f mat = m.getLast().getMatrix();
        RectUtil.BUFFER.begin(GlStateManager.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        RectUtil.BUFFER.pos(mat, x, y + size, 0).tex(0f, 1f).endVertex();
        RectUtil.BUFFER.pos(mat, x + size, y + size, 0).tex(1f, 1f).endVertex();
        RectUtil.BUFFER.pos(mat, x + size, y, 0).tex(1f, 0f).endVertex();
        RectUtil.BUFFER.pos(mat, x, y, 0).tex(0f, 0f).endVertex();
        RectUtil.TESSELLATOR.draw();
        RectUtil.endRenderRect(false);

        shader.unload();
    }

    private void drawDefault(Render2DEvent event) {
        if (target == null) return;
        MatrixStack m = event.getMatrix();

        float x = drag.position.x;
        float y = drag.position.y;

        String name = target.getName().getString();
        float nameW = font.getWidth(name, fontSize);

        float health = target instanceof AbstractClientPlayerEntity p ? (float) Mathf.round(p.getHealthFixed(), 1) : target.getHealth();
        float absorption = target instanceof PlayerEntity p ? p.getAbsorptionAmount() : 0f;
        health = !inWorld ? 0 : health;
        absorption = !inWorld ? 0 : absorption;

        tempHp = NumberTransition.resultspeed(tempHp, health);
        String hpText = tempHp > 999 ? "?" : String.format("%.1f", tempHp);
        float hpTextW = font.getWidth(hpText, fontSize);

        float calcW = Math.max(minWidth, margin * 2 + avatarSize + nameOffsetX + nameW + hpTextW + healthBarOffset);
        drag.size.set(calcW, baseHeight);

        float w = drag.size.x;
        float h = drag.size.y;
        float hpBarW = w - margin * 2 - nameOffsetX - avatarSize;
        float hpY = y + h - margin - hpHeight;
        float contentY = y + margin + 1.5f;

        float scale = (float) openAnimation.getValue();

        m.push();
        m.translate(x + w / 2f, y + h / 2f, 0);
        m.scale(scale, scale, 1f);
        m.translate(-(x + w / 2f), -(y + h / 2f), 0);

        float alpha = scale;

        // Фон + блюр + тень
        RenderUtil.CustomStyled.draw(m, x, y, w, h, Round.of(4f), true, 12f);

        int bg = ColorUtil.replAlpha(ColorUtil.multDark(Theme.getInstance().backgroundColor(), 0.75f), Math.min(alpha, 0.25f));
        RectUtil.drawRoundedRectShadowed(m, x, y, w, h, 4, 1, bg, bg, bg, bg, false, false, true, true);

        int outline = ColorUtil.replAlpha(Theme.getInstance().clientColor(), alpha);
        RenderUtil.Rounded.roundedOutline(m, x, y, w, h, 1f, outline, Round.of(4f));

        // Голова с шейдером
        if (target instanceof AbstractClientPlayerEntity player) {
            ResourceLocation skin = player.getLocationSkin();
            float hurt = (player.hurtTime - (player.hurtTime != 0 ? mc.timer.renderPartialTicks : 0)) / 20f;

            drawHeadWithShader(m, skin, x + margin, contentY - 1.5f, avatarSize, 3f, alpha, 8, 8, 8, 8, 0xFFFFFFFF);
            drawHeadWithShader(m, skin, x + margin, contentY - 1.5f, avatarSize, 3f, alpha, 40, 8, 8, 8, 0xFFFFFFFF);

            if (hurt > 0) {
                float ha = Mathf.clamp01(hurt * 1.5f) * 0.75f * alpha;
                if (ha > 0.01f) {
                    int tint = ColorUtil.getColor(255, 120, 120);
                    drawHeadWithShader(m, skin, x + margin, contentY - 1.5f, avatarSize, 3f, ha, 8, 8, 8, 8, tint);
                    drawHeadWithShader(m, skin, x + margin, contentY - 1.5f, avatarSize, 3f, ha, 40, 8, 8, 8, tint);
                }
            }
        } else {
            int bgColor = ColorUtil.replAlpha(ColorUtil.getColor(28, 28, 28), alpha);
            drawHeadWithShader(m, WHITE_PIXEL, x + margin, contentY - 1.5f, avatarSize, 3f, 1f, 0, 0, 1, 1, bgColor);

            float hurt = (target.hurtTime - (target.hurtTime != 0 ? mc.timer.renderPartialTicks : 0)) / 10f;
            font.drawCenter(m, "?", x + margin + avatarSize / 2f, contentY - 1.5f + avatarSize / 2f - 7f,
                    ColorUtil.multAlpha(ColorUtil.overCol(theme().textColor(), ColorUtil.RED, hurt), alpha), 15);
        }

        font.draw(m, name, x + margin + avatarSize + nameOffsetX, contentY, ColorUtil.multAlpha(theme().textColor(), alpha), fontSize);
        font.draw(m, hpText, x + w - margin - hpTextW, contentY, ColorUtil.multAlpha(theme().textColor(), alpha), fontSize);

        float maxHp = target.getMaxHealth();
        float hpPercent = maxHp > 0 ? health / maxHp : 0;
        healthAnimation.run(Mathf.clamp01(hpPercent), 0.5, Easings.EXPO_OUT, true);

        int hpBg = ColorUtil.replAlpha(ColorUtil.getColor(14, 18, 24), 0.5f);
        int hpCol = ColorUtil.multAlpha(Theme.getInstance().clientColor(), alpha);

        RenderUtil.Rounded.smooth(m, x + margin + avatarSize + nameOffsetX, hpY, hpBarW, hpHeight, hpBg, Round.of(hpHeight / 2f));
        RenderUtil.Rounded.smooth(m, x + margin + avatarSize + nameOffsetX, hpY, hpBarW * (float) healthAnimation.getValue(), hpHeight,
                hpCol, hpCol, hpCol, hpCol, Round.of(hpHeight / 2f));

        // Render absorption (golden hearts) bar on top if not Funtime
        if (target instanceof PlayerEntity p && !org.privates.client.utils.player.PlayerUtil.isFuntime() && absorption > 0) {
            float absPercent = Math.min(1f, absorption / maxHp);
            absorptionAnimation.run(Mathf.clamp01(absPercent), 0.5, Easings.EXPO_OUT, true);
            int absCol = ColorUtil.multAlpha(ColorUtil.getColor(255, 215, 0), alpha * 0.8f);
            float absW = hpBarW * (float) absorptionAnimation.getValue();
            if (absW > 0) {
                RenderUtil.Rounded.smooth(m, x + margin + avatarSize + nameOffsetX, hpY, absW, hpHeight,
                        absCol, absCol, absCol, absCol, Round.of(hpHeight / 2f));
            }
        } else absorptionAnimation.run(0, 0.3, Easings.CUBIC_IN, true);

        m.pop();

        if (shouldRenderArmor && target instanceof PlayerEntity p && armorAnimation.getValue() > 0) {
            drawArmorItems(m, p);
        }
    }

    private void drawArmorItems(MatrixStack m, PlayerEntity p) {
        float alpha = (float) (armorAnimation.getValue() * openAnimation.getValue());
        if (alpha <= 0) return;

        List<ItemStack> items = new ArrayList<>();
        for (int i = 3; i >= 0; i--) items.add(p.inventory.armorInventory.get(i));

        float totalW = armorItemSize * 4 + armorItemSpacing * 3;
        float startX = drag.position.x + (drag.size.x - totalW) / 2f;
        float baseY = drag.position.y + drag.size.y + armorOffsetY;

        for (int i = 0; i < 4; i++) {
            ItemStack stack = items.get(i);
            float ix = startX + i * (armorItemSize + armorItemSpacing);
            float prog = (float) armorItemAnimations[i].getValue();
            float iy = baseY + armorItemSize * (1f - prog);
            float a = alpha * prog;
            if (a > 0) drawArmorSlot(m, mc.getItemRenderer(), stack, ix, iy, a);
        }
    }

    private void drawArmorSlot(MatrixStack m, ItemRenderer ir, ItemStack stack, float x, float y, float alpha) {
        int bg = ColorUtil.multAlpha(ColorUtil.getColor(35, 35, 40), alpha * 0.9f);
        int outline = ColorUtil.multAlpha(ColorUtil.getColor(60, 60, 70), alpha);

        RenderUtil.CustomStyled.draw(m, x, y, armorItemSize, armorItemSize, Round.of(3f), true, 10f);
        RenderUtil.Rounded.smooth(m, x, y, armorItemSize, armorItemSize, bg, Round.of(3f));
        RenderUtil.Rounded.roundedOutline(m, x, y, armorItemSize, armorItemSize, 1f, outline, Round.of(3f));

        if (!stack.isEmpty()) {
            RenderSystem.enableBlend();
            RenderSystem.color4f(1f, 1f, 1f, alpha);

            float scale = (armorItemSize - 4f) / 16f;
            float ox = x + (armorItemSize - 16f * scale) / 2f;
            float oy = y + (armorItemSize - 16f * scale) / 2f;

            RenderSystem.pushMatrix();
            RenderSystem.translatef(ox, oy, 0);
            RenderSystem.scalef(scale, scale, scale);
            ir.renderItemAndEffectIntoGUI(stack, 0, 0);
            RenderSystem.popMatrix();
            RenderSystem.disableBlend();

            String dur = (int) getArmorDurability(stack) + "%";
            font.drawCenter(m, dur, x + armorItemSize / 2f, y + armorItemSize + 2f,
                    ColorUtil.multAlpha(theme().textColor(), alpha), fontSize - 3);
        } else {
            font.drawCenter(m, "—", x + armorItemSize / 2f, y + armorItemSize / 2f - 4f,
                    ColorUtil.multAlpha(theme().textColor(), alpha * 0.4f), fontSize);
        }
    }

    private void clearTargetState() {
        resetArmorAnimations();
        hasArmor = false;
        target = null;
        previousTarget = null;
        inWorld = false;
        shouldRenderArmor = false;
    }

    public void reset() {
        openAnimation.set(0);
        healthAnimation.set(0);
        absorptionAnimation.set(0);
        armorAnimation.set(0);
        tempHp = 0f;
        clearTargetState();
    }
}
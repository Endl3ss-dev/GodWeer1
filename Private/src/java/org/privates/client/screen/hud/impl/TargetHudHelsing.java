package org.privates.client.screen.hud.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IAttack;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TargetHudHelsing extends AbstractHud implements IRenderer, IAttack, IConfigurableHud {
    final DragSetting drag;
    final Animation openAnimation = new Animation();
    final Animation faceAlphaAnimation = new Animation();
    final Animation healthAnimation = new Animation();
    final StopWatch time = new StopWatch();

    public boolean inWorld;
    public LivingEntity target;
    public float hudScale = 1.0f;

    private float health;
    private float displayedHp = 0f;
    private float displayedAbsorption = 0f;
    private float absorptionWidthSmooth = 0f;
    private float lastHp = -1f;
    private final List<HpParticle> hpParticles = new ArrayList<>();

    public TargetHudHelsing(DragSetting drag) {
        this.drag = drag;
        TargetHudRenderer.registerTargetHud(this);
    }

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return List.of(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER,
                        () -> this.hudScale,
                        v -> this.hudScale = v,
                        0.6f, 1.7f, 0.05f)
        );
    }

    @Override
    public void render(Render2DEvent event) {
        AuraNew auraNew = AuraNew.getInstance();
        boolean inChat = mc.currentScreen instanceof ChatScreen;

        if (inChat) {
            target = mc.player;
            time.reset();
            faceAlphaAnimation.run(1.0, 0.2, Easings.CUBIC_OUT, true);
        } else {
            if (mc.pointedEntity instanceof LivingEntity entity) {
                target = entity;
                time.reset();
                faceAlphaAnimation.run(1.0, 0.2, Easings.CUBIC_OUT, true);
            }
            if (auraNew.isEnabled() && auraNew.target() != null) {
                target = auraNew.target();
                time.reset();
                faceAlphaAnimation.run(1.0, 0.2, Easings.CUBIC_OUT, true);
            }
        }

        if (target == null) {
            inWorld = false;
            faceAlphaAnimation.run(0.0, 0.2, Easings.CUBIC_IN, true);
        } else if (target == mc.player && inChat) {
            inWorld = true;
        } else if (mc.world != null) {
            inWorld = StreamSupport.stream(mc.world.getAllEntities().spliterator(), true).anyMatch(e -> e.equals(target));
        }

        openAnimation.update();
        faceAlphaAnimation.update();
        healthAnimation.update();

        boolean out = target == null || (!inWorld && !inChat) || time.finished(1000);
        openAnimation.run(out ? 0.0 : 1.0, 0.35, out ? Easings.CUBIC_IN : Easings.CUBIC_OUT, true);

        if (openAnimation.getValue() <= 0.0) {
            target = null;
            lastHp = -1f;
            displayedHp = 0f;
            displayedAbsorption = 0f;
            absorptionWidthSmooth = 0f;
            hpParticles.clear();
            return;
        }

        drawTargetHud(event);
    }

    private void drawTargetHud(Render2DEvent event) {
        if (target == null) return;

        MatrixStack matrix = event.getMatrix();
        float scale = hudScale;

        float x = drag.position.x;
        float y = drag.position.y;
        float width = 110f * scale;
        float height = 80f * scale;
        drag.size.set(width, height);

        if (x == 0 && y == 0) {
            x = mc.getMainWindow().getScaledWidth() / 2f - width / 2f;
            y = 40f;
            drag.position.set(x, y);
        }

        float alpha = (float) openAnimation.getValue();
        float localScale = 1.2f;
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;

        matrix.push();
        matrix.translate(centerX, centerY, 0);
        matrix.scale(localScale, localScale, 1f);
        matrix.translate(-centerX, -centerY, 0);
        {
            float headerHeight = 12.2f * scale;
            float headerPaddingTop = 2.3f * scale;
            float gap = 0.8f * scale;
            String headerName = target.getName().getString();
            float headerNameSize = 7.2f * scale;
            float dynamicHeaderWidth = font.getWidth(headerName, headerNameSize) + 14f * scale;
            float headerWidth = MathHelper.clamp(dynamicHeaderWidth, 54.0f * scale, width - 4f * scale);
            float lowerSectionY = y + headerHeight + headerPaddingTop + gap;
            float headerX = x + (width - headerWidth) / 2.0f;

            RenderUtil.CustomStyled.draw(matrix, headerX, y + headerPaddingTop, headerWidth, headerHeight, Round.of(3f * scale), true, 18f * alpha);
            RenderUtil.Rounded.roundedRect(
                    matrix, headerX, y + headerPaddingTop, headerWidth, headerHeight,
                    ColorUtil.replAlpha(ColorUtil.getColor(18, 19, 20), 0.45f * alpha),
                    Round.of(3f * scale)
            );

            RenderUtil.CustomStyled.draw(matrix, x + scale, lowerSectionY, width - 2f * scale, 42f * scale, Round.of(3f * scale), true, 22f * alpha);
            RenderUtil.Rounded.roundedRect(
                    matrix, x + scale, lowerSectionY, width - 2f * scale, 42f * scale,
                    ColorUtil.replAlpha(ColorUtil.getColor(18, 19, 20), 0.45f * alpha),
                    Round.of(3f * scale)
            );

            drawInfo(matrix, x, y, width, headerPaddingTop, alpha, scale);
            drawFace(matrix, x + 2, y + 5, width, scale);
            drawLeftItems(matrix, x, y, scale, alpha);
            drawRightItems(matrix, x, y, width, scale, alpha);
            drawBottomSection(matrix, x, y, width, alpha, scale);
        }
        matrix.pop();
    }

    private void drawInfo(MatrixStack matrix, float x, float y, float width, float headerPaddingTop, float alpha, float scale) {
        String name = target.getName().getString();
        float size = 7.2f * scale;
        float nameX = x + width / 2f - font.getWidth(name, size) / 2f;
        font.draw(matrix, name, nameX, y + headerPaddingTop + 1.7f * scale, ColorUtil.replAlpha(ColorUtil.WHITE, alpha), size);
    }

    private void drawFace(MatrixStack matrix, float x, float y, float width, float scale) {
        float headSize = 27f * scale;
        float headX = x + (width - headSize) / 2f;
        float headY = y + 19f * scale;
        float faceAlpha = (float) faceAlphaAnimation.getValue();
        float hurt = Mathf.clamp(0f, 1f, (target.hurtTime - (target.hurtTime > 0 ? mc.timer.renderPartialTicks : 0f)) / 10f);
        float shakeAmp = hurt * 1.8f * scale;
        float shakeX = (float) Math.sin((target.ticksExisted + mc.timer.renderPartialTicks) * 2.6f) * shakeAmp;
        headX += shakeX;

        if (target instanceof AbstractClientPlayerEntity player) {
            RenderSystem.pushMatrix();
            RenderSystem.color4f(1f, 1f, 1f, faceAlpha);
            RenderSystem.multMatrix(matrix.getLast().getMatrix());
            float pivotX = headX + headSize / 2f;
            float pivotY = headY + headSize / 2f;
            RenderSystem.translatef(pivotX, pivotY, 0f);
            RenderSystem.rotatef(180f, 0f, 0f, 1f);
            RenderSystem.translatef(-pivotX, -pivotY, 0f);
            RenderUtil.Texture.roundedHead(player.getLocationSkin(), headX, headY, headSize, headSize, 3f * scale, faceAlpha, hurt);
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            RenderSystem.popMatrix();
            return;
        }
        drawEntityOnScreen(matrix, headX + headSize / 2f, headY + headSize * 0.9f, 10f * scale, target, faceAlpha);
    }

    private void drawBottomSection(MatrixStack matrix, float x, float y, float width, float alpha, float scale) {
        float hp = getTargetHealth();
        float absorption = Math.max(0f, target.getAbsorptionAmount());
        float maxHp = Math.max(1f, getTargetMaxHealth());

        float barWidth = width - 10f * scale;
        float barHeight = 5f * scale;
        float barX = x + 5.7f * scale;
        float barY = y + 60f * scale;

        float targetHealth = (hp / maxHp) * barWidth;
        health = Mathf.fast(health, targetHealth, 8f);
        healthAnimation.run(Mathf.clamp(0f, 1f, hp / maxHp), 0.25, Easings.CUBIC_OUT, true);
        displayedHp = smoothValue(displayedHp <= 0f ? hp : displayedHp, hp, 0.42f);
        displayedAbsorption = smoothValue(displayedAbsorption, absorption, 0.35f);

        if (lastHp >= 0f && hp < lastHp) {
            float drop = lastHp - hp;
            int count = Math.max(8, Math.min(42, (int) (drop * 10f)));
            float emitX = barX + MathHelper.clamp(health, 0f, barWidth);
            float emitY = barY + barHeight * 0.5f;
            spawnHpParticles(emitX, emitY, count, scale, barY + barHeight * 0.5f);
        }
        lastHp = hp;

        RenderUtil.Rounded.roundedRect(
                matrix, barX, barY, barWidth, barHeight,
                ColorUtil.replAlpha(ColorUtil.getColor(10, 10, 12), 0.65f * alpha),
                Round.of(0.7f * scale)
        );
        RenderUtil.Rounded.roundedRect(
                matrix, barX, barY, MathHelper.clamp(health, 0, barWidth), barHeight,
                ColorUtil.replAlpha(theme().clientColor(), alpha),
                Round.of(0.7f * scale)
        );

        if (absorption > 0f || absorptionWidthSmooth > 0.15f * scale) {
            float targetAbsWidth = MathHelper.clamp((absorption / maxHp) * barWidth, 0f, barWidth);
            absorptionWidthSmooth = smoothValue(absorptionWidthSmooth, targetAbsWidth, 0.3f);
            float absY = barY - 2.2f * scale;
            float absH = 1.8f * scale;
            RenderUtil.Rounded.roundedRect(
                    matrix, barX, absY, absorptionWidthSmooth, absH,
                    ColorUtil.replAlpha(ColorUtil.getColor(255, 205, 80), alpha),
                    Round.of(0.6f * scale)
            );
        }

        float edgeX = barX + MathHelper.clamp(health, 0f, barWidth);
        renderHpParticles(matrix, alpha, edgeX);

        float infoY = barY + barHeight + 2.5f * scale;
        float infoSize = 6.3f * scale;
        String hpBase = String.format("HP %.1f", displayedHp);
        int white = ColorUtil.replAlpha(ColorUtil.WHITE, alpha);
        drawTextWithOutline(matrix, hpBase, barX, infoY, white, infoSize);
        if (displayedAbsorption > 0.05f) {
            String plusAbs = String.format(" + HP %.1f", displayedAbsorption);
            float plusX = barX + font.getWidth(hpBase, infoSize);
            drawTextWithOutline(matrix, plusAbs, plusX, infoY, ColorUtil.replAlpha(ColorUtil.getColor(255, 205, 80), alpha), infoSize);
        }

        boolean winning = mc.player != null && mc.player.getHealth() >= target.getHealth();
        String status = winning ? "WIN" : "LOSE";
        int statusColor = winning ? ColorUtil.replAlpha(ColorUtil.getColor(85, 255, 85), alpha) : ColorUtil.replAlpha(ColorUtil.getColor(255, 85, 85), alpha);
        float statusX = (x + width) - font.getWidth(status, infoSize) - 5f * scale;
        drawTextWithOutline(matrix, status, statusX, infoY, statusColor, infoSize);
    }

    private void drawTextWithOutline(MatrixStack matrix, String text, float x, float y, int color, float size) {
        int black = ColorUtil.replAlpha(ColorUtil.BLACK, 0.75f);
        font.draw(matrix, text, x - 0.5f, y, black, size);
        font.draw(matrix, text, x + 0.5f, y, black, size);
        font.draw(matrix, text, x, y - 0.5f, black, size);
        font.draw(matrix, text, x, y + 0.5f, black, size);
        font.draw(matrix, text, x, y, color, size);
    }

    private void drawLeftItems(MatrixStack matrix, float x, float y, float scale, float alpha) {
        List<ItemStack> armor = new ArrayList<>();
        target.getArmorInventoryList().forEach(armor::add);
        float bgSize = 13f * scale;
        float spacing = 14f * scale;
        float startX = x + 5f * scale;
        float startY = y + 23f * scale;

        for (int i = 0; i < 4; i++) {
            float sx = startX + (i % 2) * spacing;
            float sy = startY + (i / 2) * spacing;
            drawItemSlot(matrix, sx, sy, bgSize, i < armor.size() ? armor.get(armor.size() - 1 - i) : ItemStack.EMPTY, alpha, 0f);
        }
    }

    private void drawRightItems(MatrixStack matrix, float x, float y, float width, float scale, float alpha) {
        List<ItemStack> items = new ArrayList<>();
        if (!target.getHeldItemMainhand().isEmpty()) items.add(target.getHeldItemMainhand());
        if (!target.getHeldItemOffhand().isEmpty()) items.add(target.getHeldItemOffhand());

        float bgSize = 13f * scale;
        float spacing = 14f * scale;
        float startX = x + width - (spacing * 2f) - 4f * scale;
        float startY = y + 23f * scale;

        for (int i = 0; i < 4; i++) {
            float sx = startX + (i % 2) * spacing;
            float sy = startY + (i / 2) * spacing;
            drawItemSlot(matrix, sx, sy, bgSize, i < items.size() ? items.get(i) : ItemStack.EMPTY, alpha, 0f);
        }
    }

    private void drawItemSlot(MatrixStack matrix, float x, float y, float size, ItemStack stack, float alpha, float iconOffsetX) {
        RenderUtil.Rounded.roundedRect(
                matrix, x, y, size, size,
                ColorUtil.replAlpha(ColorUtil.getColor(0, 0, 0), 0.4f * alpha),
                Round.of(1.5f * hudScale)
        );
        if (stack.isEmpty()) {
            float cx = x + size / 2f;
            float cy = y + size / 2f;
            float half = size * 0.22f;
            float lw = Math.max(0.8f, hudScale);
            int cross = ColorUtil.replAlpha(ColorUtil.getColor(140, 140, 140), alpha);
            RenderUtil.Rounded.drawLine(matrix, cx - half, cy - half, cx + half, cy + half, lw, cross);
            RenderUtil.Rounded.drawLine(matrix, cx + half, cy - half, cx - half, cy + half, lw, cross);
            return;
        }

        float itemScale = Math.max(0.1f, (size - 2f * hudScale) / 16f);
        float centerX = x + size / 2f + iconOffsetX;
        float centerY = y + size / 2f;
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrix.getLast().getMatrix());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1f, 1f, 1f, alpha);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(centerX, centerY, 1f);
        GlStateManager.scalef(itemScale, itemScale, 1f);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, -8, -8);
        GlStateManager.popMatrix();
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private float getTargetHealth() {
        float hp = target.getHealthFixed();
        if (target instanceof PlayerEntity
                && mc.getCurrentServerData() != null
                && mc.world != null
                && mc.getCurrentServerData().serverIP != null
                && mc.getCurrentServerData().serverIP.toLowerCase().contains("funtime")) {
            try {
                Score score = mc.world.getScoreboard().getOrCreateScore(
                        target.getScoreboardName(),
                        mc.world.getScoreboard().getObjectiveInDisplaySlot(2)
                );
                hp = score.getScorePoints();
            } catch (Exception ignored) {
            }
        }
        return Math.max(0f, hp);
    }

    private float getTargetMaxHealth() {
        if (target instanceof PlayerEntity
                && mc.getCurrentServerData() != null
                && mc.getCurrentServerData().serverIP != null
                && mc.getCurrentServerData().serverIP.toLowerCase().contains("funtime")) {
            return 20f;
        }
        return target.getMaxHealth();
    }

    private void drawEntityOnScreen(MatrixStack matrixStack, float x, float y, float scale, LivingEntity entity, float alpha) {
        matrixStack.push();
        matrixStack.translate(x, y, 50.0D);
        matrixStack.scale(scale, scale, -scale);
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F));
        matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));

        float prevBodyYaw = entity.renderYawOffset;
        float prevYaw = entity.rotationYaw;
        float prevPitch = entity.rotationPitch;
        float prevHeadYaw = entity.rotationYawHead;

        entity.renderYawOffset = 360.0F;
        entity.rotationYaw = 360.0F;
        entity.rotationPitch = 0.0F;
        entity.rotationYawHead = 360.0F;

        RenderHelper.setupGui3DDiffuseLighting();
        RenderSystem.color4f(1f, 1f, 1f, alpha);
        EntityRendererManager manager = mc.getRenderManager();
        IRenderTypeBuffer.Impl buffer = mc.getRenderTypeBuffers().getBufferSource();
        manager.renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, buffer, 15728880);
        buffer.finish();
        RenderHelper.disableStandardItemLighting();
        RenderSystem.color4f(1f, 1f, 1f, 1f);

        entity.renderYawOffset = prevBodyYaw;
        entity.rotationYaw = prevYaw;
        entity.rotationPitch = prevPitch;
        entity.rotationYawHead = prevHeadYaw;

        matrixStack.pop();
    }

    private float smoothValue(float current, float target, float speed) {
        float delta = target - current;
        if (Math.abs(delta) < 0.001f) return target;
        return current + delta * MathHelper.clamp(speed, 0.05f, 1.0f);
    }

    private void spawnHpParticles(float x, float y, int count, float scale, float barCenterY) {
        int total = count * 3;
        for (int i = 0; i < total; i++) {
            float angle = (float) ((Math.random() * Math.PI * 2.0) - Math.PI);
            float speed = (0.12f + (float) Math.random() * 0.42f) * scale;
            float vx = (float) (-0.20f * scale + Math.cos(angle) * speed);
            float vy = (float) (Math.sin(angle) * speed * 0.65f);
            float life = 24f + (float) (Math.random() * 20f);
            float size = (1.0f + (float) (Math.random() * 1.8f)) * scale;
            hpParticles.add(new HpParticle(x, y, vx, vy, life, size, barCenterY, (float) (Math.random() * 2.0 * Math.PI)));
        }
    }

    private void renderHpParticles(MatrixStack matrix, float alpha, float edgeX) {
        if (hpParticles.isEmpty()) return;
        Iterator<HpParticle> it = hpParticles.iterator();
        while (it.hasNext()) {
            HpParticle p = it.next();
            p.life -= 1f;
            if (p.life <= 0f) {
                it.remove();
                continue;
            }

            p.x += p.vx;
            p.y += p.vy;
            p.noisePhase += 0.22f;
            p.vx += (float) Math.cos(p.noisePhase) * 0.0065f;
            p.vy += (float) Math.sin(p.noisePhase * 1.37f) * 0.005f;
            p.vx *= 0.972f;
            p.vy *= 0.955f;
            p.vy += (p.baseY - p.y) * 0.012f;
            p.x += (edgeX - p.x) * 0.0025f;

            float t = p.life / p.maxLife;
            int theme = theme().clientColor();
            int tr = (theme >> 16) & 255;
            int tg = (theme >> 8) & 255;
            int tb = theme & 255;
            int glow = ColorUtil.getColor(tr, tg, tb, (int) (105f * t * alpha));
            int core = ColorUtil.getColor(tr, tg, tb, (int) (215f * t * alpha));

            float g = p.size * 2.2f;
            RenderUtil.Rounded.roundedRect(matrix, p.x - g / 2f, p.y - g / 2f, g, g, glow, Round.of(g / 2f));
            RenderUtil.Rounded.roundedRect(matrix, p.x - p.size / 2f, p.y - p.size / 2f, p.size, p.size, core, Round.of(p.size / 2f));
        }
    }

    @Override
    public void attack(AttackEvent event) {
    }

    @Override
    public void renderInWorld(Render3DLastEvent event) {
        AuraNew auraNew = AuraNew.getInstance();
        if (mc.currentScreen instanceof ChatScreen) return;

        if (mc.pointedEntity instanceof LivingEntity entity) {
            target = entity;
            time.reset();
        }
        if (auraNew.isEnabled() && auraNew.target() != null) {
            target = auraNew.target();
            time.reset();
        }

        if (target == null || target == mc.player || mc.getRenderManager() == null) return;

        if (mc.world != null) {
            inWorld = StreamSupport.stream(mc.world.getAllEntities().spliterator(), true).anyMatch(e -> e.equals(target));
        }
        if (!inWorld || time.finished(1000)) return;

        openAnimation.update();
        faceAlphaAnimation.update();
        healthAnimation.update();
        openAnimation.run(1.0, 0.25, Easings.CUBIC_OUT, true);
        if (openAnimation.getValue() <= 0.0) return;

        MatrixStack matrix = event.getMatrix();
        Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();
        Vector3d targetPos = target.getPositionVec().add(0, target.getHeight() / 2.0, 0).subtract(cameraPos);
        double distance = mc.player.getDistance(target);
        float worldScale = (float) Math.max(0.005, 0.0075 * (distance / 5.0));

        matrix.push();
        matrix.translate(targetPos.x, targetPos.y, targetPos.z);
        matrix.rotate(mc.getRenderManager().getCameraOrientation());
        matrix.translate(0, 0, -0.5);
        matrix.scale(-worldScale, -worldScale, worldScale);

        float oldX = drag.position.x;
        float oldY = drag.position.y;
        float w = 110f * hudScale;
        float h = 80f * hudScale;
        drag.position.set(-w / 2f, -h / 2f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        drawTargetHud(new Render2DEvent(matrix, mc.getRenderManager().info, mc.getMainWindow(), event.getPartialTicks()));
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        drag.position.set(oldX, oldY);
        matrix.pop();
    }

    private static final class HpParticle {
        private float x;
        private float y;
        private float vx;
        private float vy;
        private float life;
        private final float maxLife;
        private final float size;
        private final float baseY;
        private float noisePhase;

        private HpParticle(float x, float y, float vx, float vy, float life, float size, float baseY, float noisePhase) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.life = life;
            this.maxLife = life;
            this.size = size;
            this.baseY = baseY;
            this.noisePhase = noisePhase;
        }
    }
}

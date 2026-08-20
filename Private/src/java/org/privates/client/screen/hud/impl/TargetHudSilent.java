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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TargetHudSilent extends AbstractHud implements IRenderer, IAttack, IConfigurableHud {
    final DragSetting drag;
    final Animation openAnimation = new Animation();
    final Animation useItemAnimation = new Animation();
    final Animation healthAnimation = new Animation();
    final StopWatch time = new StopWatch();
    final StopWatch previewSwitchTimer = new StopWatch();

    public boolean inWorld;
    public LivingEntity target;
    public float hudScale = 1.0f;
    private boolean previewMode = false;

    private ItemStack lastUsingItem = ItemStack.EMPTY;
    private int previewItemIndex = 0;
    private boolean previewUseVisible = true;
    private final List<ItemStack> previewCycleItems = Arrays.asList(
            new ItemStack(Items.NETHERITE_SWORD),
            new ItemStack(Items.NETHERITE_PICKAXE),
            new ItemStack(Items.GOLDEN_APPLE),
            new ItemStack(Items.TOTEM_OF_UNDYING),
            new ItemStack(Items.END_CRYSTAL),
            new ItemStack(Items.EXPERIENCE_BOTTLE)
    );
    private final List<ItemStack> previewArmorItems = Arrays.asList(
            new ItemStack(Items.NETHERITE_HELMET),
            new ItemStack(Items.NETHERITE_CHESTPLATE),
            new ItemStack(Items.NETHERITE_LEGGINGS),
            new ItemStack(Items.NETHERITE_BOOTS)
    );
    private float healthWidth;
    private float absorptionWidthSmooth;
    private float displayedHp;
    private float displayedAbsorption;
    private float lastHp = -1f;
    private float lastTotalHp = 0f;
    private final List<HealingText> healingTexts = new ArrayList<>();
    private final List<HpParticle> hpParticles = new ArrayList<>();

    public TargetHudSilent(DragSetting drag) {
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
        previewMode = inChat;

        if (auraNew.isEnabled() && auraNew.target() != null) {
            target = auraNew.target();
            time.reset();
        } else if (inChat) {
            target = mc.player;
            time.reset();
        } else if (mc.pointedEntity instanceof LivingEntity entity) {
            target = entity;
            time.reset();
        }

        if (target == null) {
            inWorld = false;
        } else if (target == mc.player && inChat) {
            inWorld = true;
        } else if (mc.world != null) {
            inWorld = StreamSupport.stream(mc.world.getAllEntities().spliterator(), true).anyMatch(e -> e.equals(target));
        }

        openAnimation.update();
        useItemAnimation.update();
        healthAnimation.update();
        updatePreviewState();

        boolean out = target == null || (!inWorld && !inChat) || time.finished(500);
        openAnimation.run(out ? 0.0 : 1.0, 0.23, out ? Easings.CUBIC_IN : Easings.CUBIC_OUT, true);

        if (target == null && openAnimation.getValue() <= 0.0) {
            target = null;
            healingTexts.clear();
            hpParticles.clear();
            displayedHp = 0f;
            displayedAbsorption = 0f;
            absorptionWidthSmooth = 0f;
            lastHp = -1f;
            lastTotalHp = 0f;
            return;
        }

        if (target != null) {
            float currentTotalHp = getTargetHealth() + target.getAbsorptionAmount();
            if (currentTotalHp > lastTotalHp && lastTotalHp > 0f) {
                float healed = currentTotalHp - lastTotalHp;
                if (healed > 0.01f) {
                    healingTexts.add(new HealingText("+" + String.format("%.1f", healed)));
                }
            }
            lastTotalHp = currentTotalHp;
        }

        drawTargetHud(event);
    }

    private void drawTargetHud(Render2DEvent event) {
        if (target == null) return;
        float alpha = (float) openAnimation.getValue();
        if (alpha <= 0.0f) return;

        MatrixStack matrix = event.getMatrix();
        float scale = hudScale;
        float x = drag.position.x;
        float y = drag.position.y;
        float width = 100f * scale;
        float height = 36f * scale;

        drag.size.set(width, height);
        if (x == 0 && y == 0) {
            x = mc.getMainWindow().getScaledWidth() / 2f - width / 2f;
            y = 40f;
            drag.position.set(x, y);
        }

        float faceX = x + 5f * scale;
        float faceY = y + 5.5f * scale;
        float faceSize = 25f * scale;
        float dividerX = x + 32f * scale;
        float dividerY = y + 2f * scale;
        float dividerHeight = height - 3f * scale;
        float contentX = x + 34f * scale;
        float hpBarY = y + 31f * scale;
        float hpWidthMax = 58f * scale;
        float hpTextY = y + 22.5f * scale;
        float nameY = y + 6.7f * scale;
        float handSlotSize = 10f * scale;
        float handsX = x + width - handSlotSize * 2f - 3.2f * scale;
        float handsY = y + 5.0f * scale;

        RenderUtil.CustomStyled.draw(matrix, x, y, width, height, Round.of(7f * scale), true, 22f * alpha);
        RenderUtil.Rounded.roundedRect(
                matrix, x, y, width, height,
                ColorUtil.replAlpha(ColorUtil.getColor(7, 8, 18), 0.62f * alpha),
                Round.of(7f * scale)
        );

        RenderUtil.Rounded.roundedRect(
                matrix, dividerX, dividerY, 0.7f * scale, dividerHeight,
                ColorUtil.replAlpha(ColorUtil.getColor(46, 47, 56), alpha),
                Round.of(0)
        );
        RenderUtil.Rounded.roundedRect(
                matrix, contentX, y + 17f * scale, hpWidthMax, 0.7f * scale,
                ColorUtil.replAlpha(ColorUtil.getColor(46, 47, 56), alpha),
                Round.of(0)
        );

        drawFace(matrix, faceX + 1, faceY + 2, faceSize, alpha);

        String name = target.getName().getString();
        float nameSize = 8.2f * scale;
        float nameMaxWidth = Math.max(20f * scale, handsX - (contentX - 3f * scale) - 2f * scale);
        if (font.getWidth(name, nameSize) > nameMaxWidth) {
            name = trimToWidth(name, nameMaxWidth, nameSize);
        }
        float nameX = contentX + 0.1f * scale;
        font.draw(matrix, name, nameX, nameY, ColorUtil.replAlpha(ColorUtil.WHITE, alpha), nameSize);

        drawHandsInMainPanel(matrix, handsX, handsY, handSlotSize, alpha, scale);

        float hp = previewMode ? (18.8f + (float) Math.sin((mc.player != null ? mc.player.ticksExisted : 0) * 0.08f) * 1.2f) : getTargetHealth();
        float absorption = previewMode ? (previewUseVisible ? 4.0f : 2.0f) : target.getAbsorptionAmount();
        float maxBase = Math.max(1f, getTargetMaxHealth());
        float hpTargetWidth = Math.round(hp / maxBase * hpWidthMax);
        healthWidth = MathHelper.clamp(Mathf.fast(healthWidth, hpTargetWidth, 9f), 2f * scale, hpWidthMax);
        displayedHp = smoothValue(displayedHp <= 0f ? hp : displayedHp, hp, 0.42f);
        displayedAbsorption = smoothValue(displayedAbsorption, absorption, 0.35f);

        if (lastHp >= 0f && hp < lastHp) {
            float drop = lastHp - hp;
            int count = Math.max(8, Math.min(36, (int) (drop * 10f)));
            float emitX = contentX + MathHelper.clamp(healthWidth, 0f, hpWidthMax);
            float emitY = hpBarY + scale;
            spawnHpParticles(emitX, emitY, count, scale);
        }
        lastHp = hp;

        String hpBaseText = String.format("HP %.1f ", displayedHp);
        int hpColor = hp >= 10f ? ColorUtil.replAlpha(ColorUtil.getColor(85, 255, 85), alpha) : ColorUtil.replAlpha(ColorUtil.getColor(255, 85, 85), alpha);
        float hpSize = 8.2f * scale;
        String absText = displayedAbsorption > 0.05f ? String.format("+ HP %.1f ", displayedAbsorption) : null;
        String reservedAbsText = " + HP 99.9";
        float gap = 0.85f * scale;
        float baseW = font.getWidth(hpBaseText, hpSize);
        float absW = absText != null ? font.getWidth(absText, hpSize) : 0f;
        float reservedAbsW = font.getWidth(reservedAbsText, hpSize);
        float totalW = baseW + gap + reservedAbsW;
        if (totalW > nameMaxWidth) {
            float fit = MathHelper.clamp(nameMaxWidth / totalW, 0.78f, 1.0f);
            hpSize *= fit;
            gap = Math.max(0.2f * scale, gap * fit * 0.6f);
            baseW = font.getWidth(hpBaseText, hpSize);
            absW = absText != null ? font.getWidth(absText, hpSize) : 0f;
            reservedAbsW = font.getWidth(reservedAbsText, hpSize);
            totalW = baseW + gap + reservedAbsW;
        }
        float hpStartX = contentX - 0.1f * scale + Math.max(0f, (nameMaxWidth - totalW) / 2f);
        font.draw(matrix, hpBaseText, hpStartX, hpTextY - 2.5f, hpColor, hpSize);
        if (absText != null) {
            float absX = hpStartX + baseW + gap;
            font.draw(matrix, absText, absX, hpTextY - 2, ColorUtil.replAlpha(ColorUtil.getColor(255, 212, 66), alpha), hpSize);
        }

        if (absorption > 0f || absorptionWidthSmooth > 0.5f) {
            float targetAbs = MathHelper.clamp((absorption / maxBase) * hpWidthMax, 0f, hpWidthMax);
            absorptionWidthSmooth = smoothValue(absorptionWidthSmooth, targetAbs, 0.3f);
            RenderUtil.Rounded.roundedRect(
                    matrix, contentX, hpBarY - 2.2f * scale, absorptionWidthSmooth, 1.8f * scale,
                    ColorUtil.replAlpha(ColorUtil.getColor(255, 212, 66), alpha),
                    Round.of(0.6f * scale)
            );
        }

        RenderUtil.Rounded.roundedRect(
                matrix, contentX, hpBarY, hpWidthMax, 2f * scale,
                ColorUtil.replAlpha(ColorUtil.getColor(6, 7, 18), alpha),
                Round.of(0.75f * scale)
        );
        RenderUtil.Rounded.roundedRect(
                matrix, contentX, hpBarY, healthWidth, 2f * scale,
                ColorUtil.replAlpha(theme().clientColor(), alpha),
                Round.of(0.75f * scale)
        );

        float edgeX = contentX + MathHelper.clamp(healthWidth, 0f, hpWidthMax);
        renderHpParticles(matrix, alpha, edgeX);

        drawArmorRow(matrix, x, y, width, height, alpha, scale);
        drawUsingItem(matrix, x, y, width, height, alpha, scale);
        drawHealingTexts(matrix, hpStartX + totalW + 2f * scale, hpTextY, alpha, scale);
    }

    private void drawUsingItem(MatrixStack matrix, float x, float y, float width, float height, float alpha, float scale) {
        boolean using = previewMode ? previewUseVisible : target.isHandActive();
        useItemAnimation.run(using ? 1.0 : 0.0, 0.2, using ? Easings.CUBIC_OUT : Easings.CUBIC_IN, true);
        float anim = (float) useItemAnimation.getValue();
        if (anim <= 0.01f) return;

        if (previewMode) {
            lastUsingItem = previewCycleItems.get(previewItemIndex % previewCycleItems.size());
        } else if (!target.getActiveItemStack().isEmpty()) {
            lastUsingItem = target.getActiveItemStack();
        }
        if (lastUsingItem.isEmpty() || lastUsingItem.getItem() == Items.AIR) return;

        float panel = 24f * scale;
        float panelX = x - 29f * scale * anim;
        float panelY = y + 6f * scale;

        RenderUtil.CustomStyled.draw(matrix, panelX, panelY, panel, panel, Round.of(12f * scale), true, 16f * alpha * anim);
        RenderUtil.Rounded.roundedRect(
                matrix, panelX, panelY, panel, panel,
                ColorUtil.replAlpha(ColorUtil.getColor(7, 8, 18), 0.62f * alpha * anim),
                Round.of(12f * scale)
        );

        float itemScale = Math.max(0.1f, (panel - 6f * scale) / 16f);
        float centerX = panelX + panel / 2f;
        float centerY = panelY + panel / 2f;
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrix.getLast().getMatrix());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1f, 1f, 1f, alpha * anim);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(centerX, centerY, 2f);
        GlStateManager.scalef(itemScale, itemScale, itemScale);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(lastUsingItem, -8, -8);
        GlStateManager.popMatrix();
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private void drawArmorRow(MatrixStack matrix, float x, float y, float width, float height, float alpha, float scale) {
        List<ItemStack> equipped = new ArrayList<>();
        if (previewMode) {
            equipped.addAll(previewArmorItems);
        } else {
            target.getArmorInventoryList().forEach(equipped::add);
        }
        while (equipped.size() < 4) equipped.add(ItemStack.EMPTY);

        float rowW = 4f * 11f * scale;
        float rowH = 11f * scale;
        float rowX = x + width / 2f - rowW / 2f;
        float rowY = y + height + scale;

        RenderUtil.CustomStyled.draw(matrix, rowX, rowY, rowW, rowH, Round.of(3f * scale), true, 14f * alpha);
        RenderUtil.Rounded.roundedRect(
                matrix, rowX, rowY, rowW, rowH,
                ColorUtil.replAlpha(ColorUtil.getColor(7, 8, 18), 0.6f * alpha),
                Round.of(3f * scale)
        );

        float ix = rowX;
        for (int i = 0; i < 4; i++) {
            ItemStack stack = equipped.size() > i ? equipped.get(3 - i) : ItemStack.EMPTY;
            drawItemMini(matrix, stack, ix, rowY, 0.5f * scale, alpha);
            ix += 11f * scale;
        }
    }

    private void drawItemMini(MatrixStack matrix, ItemStack stack, float x, float y, float drawScale, float alpha) {
        float slotSize = 11f * hudScale;
        if (stack.isEmpty()) {
            float cx = x + slotSize / 2f;
            float cy = y + slotSize / 2f;
            float half = slotSize * 0.22f;
            int cross = ColorUtil.replAlpha(ColorUtil.getColor(140, 140, 140), alpha);
            float lw = Math.max(0.8f, 0.95f * hudScale);
            RenderUtil.Rounded.drawLine(matrix, cx - half, cy - half, cx + half, cy + half, lw, cross);
            RenderUtil.Rounded.drawLine(matrix, cx + half, cy - half, cx - half, cy + half, lw, cross);
            return;
        }
        float itemScale = Math.max(0.1f, (slotSize - 2f * hudScale) / 16f);
        float centerX = x + slotSize / 2f;
        float centerY = y + slotSize / 2f;
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrix.getLast().getMatrix());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1f, 1f, 1f, alpha);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(centerX, centerY, 2f);
        GlStateManager.scalef(itemScale, itemScale, itemScale);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, -8, -8);
        GlStateManager.popMatrix();
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private void drawHealingTexts(MatrixStack matrix, float x, float y, float alpha, float scale) {
        Iterator<HealingText> it = healingTexts.iterator();
        while (it.hasNext()) {
            HealingText text = it.next();
            float progress = text.output();
            if (text.finished()) {
                it.remove();
                continue;
            }
            float yOffset = 6f * scale * progress;
            int a = (int) (255 * (1f - progress) * alpha);
            int color = ColorUtil.getColor(85, 255, 85, Math.max(0, a));
            font.draw(matrix, text.text, x, y - yOffset, color, 7.2f * scale);
            x += font.getWidth(text.text, 7.2f * scale) + 2f * scale;
        }
    }

    private void drawFace(MatrixStack matrix, float x, float y, float size, float alpha) {
        float hurt = Mathf.clamp(0f, 1f, (target.hurtTime - (target.hurtTime > 0 ? mc.timer.renderPartialTicks : 0f)) / 10f);


        if (target instanceof AbstractClientPlayerEntity player) {
            RenderSystem.pushMatrix();
            RenderSystem.color4f(1f, 1f, 1f, alpha);
            RenderSystem.multMatrix(matrix.getLast().getMatrix());
            float pivotX = x + size / 2f;
            float pivotY = y + size / 2f;
            RenderSystem.translatef(pivotX, pivotY, 0f);
            RenderSystem.rotatef(180f, 0f, 0f, 1f);
            RenderSystem.translatef(-pivotX, -pivotY, 0f);
            RenderUtil.Texture.roundedHead(player.getLocationSkin(), x, y, size, size, 7f * hudScale, alpha, hurt);
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            RenderSystem.popMatrix();
            return;
        }

        RenderSystem.pushMatrix();
        float tint = 1f - hurt * 0.7f;
        RenderSystem.color4f(1f, tint, tint, alpha);
        drawEntityOnScreen(matrix, x + size / 2f, y + size * 0.9f, 10f * hudScale, target);
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.popMatrix();
    }

    private float getTargetHealth() {
        float hp = target.getHealthFixed();
        if (mc.getCurrentServerData() != null
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
        if (mc.getCurrentServerData() != null
                && mc.getCurrentServerData().serverIP != null
                && mc.getCurrentServerData().serverIP.toLowerCase().contains("funtime")) {
            return 20f;
        }
        return target.getMaxHealth();
    }

    private String trimToWidth(String text, float maxWidth, float size) {
        String result = text;
        while (!result.isEmpty() && font.getWidth(result + "...", size) > maxWidth) {
            result = result.substring(0, result.length() - 1);
        }
        return result.isEmpty() ? text : result + "...";
    }

    private float smoothValue(float current, float target, float speed) {
        float delta = target - current;
        if (Math.abs(delta) < 0.001f) return target;
        return current + delta * MathHelper.clamp(speed, 0.05f, 1.0f);
    }

    private void updatePreviewState() {
        if (!previewMode) return;
        if (previewSwitchTimer.every(1000)) {
            previewItemIndex = (previewItemIndex + 1) % previewCycleItems.size();
            previewUseVisible = !previewUseVisible;
        }
    }

    private void drawHandsInMainPanel(MatrixStack matrix, float x, float y, float size, float alpha, float scale) {
        ItemStack main = previewMode ? previewCycleItems.get(previewItemIndex % previewCycleItems.size()) : target.getHeldItemMainhand();
        ItemStack off = previewMode ? previewCycleItems.get((previewItemIndex + 1) % previewCycleItems.size()) : target.getHeldItemOffhand();
        drawHandSlot(matrix, x, y, size, main, alpha);
        drawHandSlot(matrix, x + size + 1.2f * scale, y, size, off, alpha);
    }

    private void drawHandSlot(MatrixStack matrix, float x, float y, float size, ItemStack stack, float alpha) {
        RenderUtil.Rounded.roundedRect(
                matrix, x, y, size, size,
                ColorUtil.replAlpha(ColorUtil.getColor(0, 0, 0), 0.45f * alpha),
                Round.of(1.6f * hudScale)
        );
        if (stack == null || stack.isEmpty()) {
            float cx = x + size / 2f;
            float cy = y + size / 2f;
            float half = size * 0.22f;
            int cross = ColorUtil.replAlpha(ColorUtil.getColor(140, 140, 140), alpha);
            float lw = Math.max(0.8f, 0.95f * hudScale);
            RenderUtil.Rounded.drawLine(matrix, cx - half, cy - half, cx + half, cy + half, lw, cross);
            RenderUtil.Rounded.drawLine(matrix, cx + half, cy - half, cx - half, cy + half, lw, cross);
            return;
        }
        float itemScale = Math.max(0.1f, (size - 2f * hudScale) / 16f);
        float centerX = x + size / 2f;
        float centerY = y + size / 2f;
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrix.getLast().getMatrix());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1f, 1f, 1f, alpha);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(centerX, centerY, 2f);
        GlStateManager.scalef(itemScale, itemScale, itemScale);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, -8, -8);
        GlStateManager.popMatrix();
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private void spawnHpParticles(float x, float y, int count, float scale) {
        for (int i = 0; i < count; i++) {
            float angle = (float) ((Math.random() * Math.PI * 2.0) - Math.PI);
            float speed = (0.12f + (float) Math.random() * 0.42f) * scale;
            float vx = (float) (-0.20f * scale + Math.cos(angle) * speed);
            float vy = (float) (Math.sin(angle) * speed * 0.65f);
            float life = 24f + (float) (Math.random() * 20f);
            float size = (1.0f + (float) (Math.random() * 1.8f)) * scale;
            hpParticles.add(new HpParticle(x, y, vx, vy, life, size, y, (float) (Math.random() * 2.0 * Math.PI)));
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
            int glow = ColorUtil.getColor(tr, tg, tb, (int) (100f * t * alpha));
            int core = ColorUtil.getColor(tr, tg, tb, (int) (210f * t * alpha));

            float g = p.size * 2.2f;
            RenderUtil.Rounded.roundedRect(matrix, p.x - g / 2f, p.y - g / 2f, g, g, glow, Round.of(g / 2f));
            RenderUtil.Rounded.roundedRect(matrix, p.x - p.size / 2f, p.y - p.size / 2f, p.size, p.size, core, Round.of(p.size / 2f));
        }
    }

    private void drawEntityOnScreen(MatrixStack matrixStack, float x, float y, float scale, LivingEntity entity) {
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
        EntityRendererManager manager = mc.getRenderManager();
        IRenderTypeBuffer.Impl buffer = mc.getRenderTypeBuffers().getBufferSource();
        manager.renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, buffer, 15728880);
        buffer.finish();
        RenderHelper.disableStandardItemLighting();

        entity.renderYawOffset = prevBodyYaw;
        entity.rotationYaw = prevYaw;
        entity.rotationPitch = prevPitch;
        entity.rotationYawHead = prevHeadYaw;

        matrixStack.pop();
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
        if (!inWorld || time.finished(500)) return;

        openAnimation.update();
        useItemAnimation.update();
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
        float w = 100f * hudScale;
        float h = 36f * hudScale;
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

    private static final class HealingText {
        private final Animation animation = new Animation();
        private final String text;

        private HealingText(String text) {
            this.text = text;
            animation.run(1.0, 0.6, Easings.CUBIC_OUT, true);
        }

        private float output() {
            animation.update();
            return (float) animation.getValue();
        }

        private boolean finished() {
            return animation.getValue() >= 0.98;
        }
    }
}

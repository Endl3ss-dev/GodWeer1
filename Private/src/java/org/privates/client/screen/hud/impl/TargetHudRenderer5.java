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
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
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
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.lib.util.time.StopWatch;

import java.util.List;
import java.util.stream.StreamSupport;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TargetHudRenderer5 extends AbstractHud implements IRenderer, IAttack, IConfigurableHud {
    final DragSetting drag;
    final Animation openAnimation = new Animation();
    final Animation healthAnimation = new Animation();
    final Animation absorptionAnimation = new Animation();
    final StopWatch time = new StopWatch();

    public boolean inWorld;
    public LivingEntity target;
    private float displayHealth = 0f;
    private float nameScrollX = 0f;
    private long lastScrollTime = 0L;
    private boolean scrollingForward = true;
    private long scrollPauseUntil = 0L;

    public float hudScale = 1.0f;

    public TargetHudRenderer5(DragSetting drag) {
        this.drag = drag;
        TargetHudRenderer.registerTargetHud(this);
    }

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return List.of(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER,
                        () -> this.hudScale,
                        v -> this.hudScale = v,
                        0.6f, 1.6f, 0.05f)
        );
    }

    @Override
    public void render(Render2DEvent event) {
        AuraNew auraNew = AuraNew.getInstance();
        boolean inChat = mc.currentScreen instanceof ChatScreen;

        if (inChat) {
            target = mc.player;
            time.reset();
        } else {
            if (mc.pointedEntity instanceof LivingEntity entity) {
                target = entity;
                time.reset();
            }
            if (auraNew.isEnabled() && auraNew.target() != null) {
                target = auraNew.target();
                time.reset();
            }
        }

        if (target == null) {
            inWorld = false;
        } else {
            if (target != mc.player || !inChat) {
                if (mc.world != null) {
                    inWorld = StreamSupport.stream(mc.world.getAllEntities().spliterator(), true)
                            .anyMatch(entity -> entity.equals(target));
                }
            } else {
                inWorld = true;
            }
        }

        openAnimation.update();
        healthAnimation.update();
        absorptionAnimation.update();

        boolean out = (target == null || (!inWorld && !inChat) || time.finished(1000));
        openAnimation.run(out ? 0.0 : 1.0, 0.3, out ? Easings.CUBIC_IN : Easings.CUBIC_OUT, true);

        if (openAnimation.getValue() <= 0.0) {
            target = null;
            displayHealth = 0f;
            nameScrollX = 0f;
            absorptionAnimation.set(0);
            return;
        }

        drawTargetHud(event);
    }

    private void drawTargetHud(Render2DEvent event) {
        if (target == null) return;

        MatrixStack matrix = event.getMatrix();
        float x = drag.position.x;
        float y = drag.position.y;

        float width = 114f * 1.06f * hudScale;
        float height = 42f * hudScale;
        float headSize = (34.5f * 0.97f) * hudScale;
        float avatarBoxSize = 33f * hudScale;
        float spacing = 4f * hudScale;
        float radius = 6f * hudScale;

        drag.size.set(width, height);

        if (x == 0 && y == 0) {
            x = mc.getMainWindow().getScaledWidth() / 2f - width / 2f;
            y = 38f;
            drag.position.set(x, y);
        }

        float alpha = (float) openAnimation.getValue();

        if (target instanceof PlayerEntity playerTarget) {
            drawTopEquipmentRow(matrix, x, y, width, alpha, playerTarget);
        }

        RenderUtil.CustomStyled.draw(matrix, x, y, width, height, Round.of(radius), true, 25f * alpha);

        float avatarBoxX = x + spacing;
        float avatarBoxY = y + (height - avatarBoxSize) / 2f;

        float headX = avatarBoxX + (avatarBoxSize - headSize) / 2f;
        float headY = avatarBoxY + (avatarBoxSize - headSize) / 2f;
        drawTargetHead(matrix, headX + 3, headY + 2.5f, headSize, alpha);

        float health = getTargetPureHealth();
        float absorption = getTargetAbsorption();
        float maxHealth = Math.max(1f, target.getMaxHealth());
        float healthPct = Mathf.clamp(0f, 1f, health / maxHealth);
        float absorptionPct = Mathf.clamp(0f, 1f, absorption / maxHealth);
        displayHealth = Mathf.fast(displayHealth, Math.min(maxHealth, health + absorption), 10f);
        healthAnimation.run(healthPct, 0.28, Easings.EXPO_OUT, true);
        absorptionAnimation.run(absorptionPct, 0.33, Easings.EXPO_OUT, true);

        String name = target.getName().getString();
        float nameSize = 8.9f * hudScale;
        float hpSize = 8.1f * hudScale;
        float textX = avatarBoxX + avatarBoxSize + 6f * hudScale;
        float textY = y + 7f * hudScale;
        float maxNameWidth = width - (textX - x) - 7f * hudScale;

        drawScrollingName(matrix, name, textX, textY, maxNameWidth, ColorUtil.replAlpha(ColorUtil.WHITE, alpha), nameSize);
        String hpBaseText = "HP: " + (int) Math.max(0, health);
        float hpTextY = textY + 11f * hudScale;
        int white = ColorUtil.replAlpha(ColorUtil.WHITE, alpha);
        font.draw(matrix, hpBaseText, textX, hpTextY, white, hpSize);

        if (absorption > 0.01f) {
            String plusText = " + ";
            String absText = String.valueOf((int) Math.max(0, absorption));
            float plusX = textX + font.getWidth(hpBaseText, hpSize);
            font.draw(matrix, plusText, plusX, hpTextY, white, hpSize);
            font.draw(matrix, absText, plusX + font.getWidth(plusText, hpSize), hpTextY,
                    ColorUtil.replAlpha(ColorUtil.getColor(255, 205, 74), alpha), hpSize);
        }

        float barX = textX;
        float barY = y + height - 10f * hudScale;
        float barW = width - (barX - x) - 7f * hudScale;
        float barH = 7f * hudScale;
        float barRound = 3.0f * hudScale;
        Round barRoundObj = Round.of(barRound);

        RenderUtil.Rounded.roundedRect(
                matrix, barX, barY - 2, barW, barH,
                ColorUtil.replAlpha(ColorUtil.getColor(34, 34, 34), 0.95f * alpha),
                barRoundObj
        );

        float hpW = barW * (float) healthAnimation.getValue();
        float goldW = Math.min(hpW, barW * (float) absorptionAnimation.getValue());

        int c1 = ColorUtil.replAlpha(theme().clientColor(), alpha);
        int c2 = ColorUtil.replAlpha(ColorUtil.multDark(theme().clientColor(), 0.86f), alpha);
        int c3 = ColorUtil.replAlpha(ColorUtil.multDark(theme().clientColor(), 0.72f), alpha);
        int c4 = ColorUtil.replAlpha(theme().clientColor(), alpha);

        if (hpW > 0.0f) {
            RenderUtil.Rounded.roundedRect(
                    matrix, barX, barY - 2, hpW, barH,
                    c1, c2, c3, c4, barRoundObj
            );
        }

        if (goldW > 0.0f) {
            float gx = barX + Math.max(0f, hpW - goldW);
            float gw = Math.min(goldW, barW - (gx - barX));
            int g1 = ColorUtil.replAlpha(ColorUtil.getColor(188, 132, 28), alpha * 0.96f);
            int g2 = ColorUtil.replAlpha(ColorUtil.getColor(255, 208, 94), alpha);
            int g3 = ColorUtil.replAlpha(ColorUtil.getColor(255, 208, 94), alpha);
            int g4 = ColorUtil.replAlpha(ColorUtil.getColor(188, 132, 28), alpha * 0.96f);
            RenderUtil.Rounded.roundedRect(
                    matrix, gx, barY - 2, gw, barH,
                    g1, g2, g3, g4, barRoundObj
            );
        }
    }

    private void drawTopEquipmentRow(MatrixStack matrix, float x, float y, float width, float alpha, PlayerEntity player) {
        float slotSize = 11.5f * hudScale;
        float gap = 3.2f * hudScale;
        float rowY = y - (slotSize + 4.0f * hudScale);

        ItemStack[] items = new ItemStack[]{
                player.getItemStackFromSlot(EquipmentSlotType.HEAD),
                player.getItemStackFromSlot(EquipmentSlotType.CHEST),
                player.getItemStackFromSlot(EquipmentSlotType.LEGS),
                player.getItemStackFromSlot(EquipmentSlotType.FEET),
                player.getHeldItemMainhand(),
                player.getHeldItemOffhand()
        };

        float totalW = items.length * slotSize + (items.length - 1) * gap;
        float startX = x + (width - totalW) / 2f;

        for (int i = 0; i < items.length; i++) {
            drawEquipmentSlot(matrix, startX + i * (slotSize + gap), rowY, slotSize, items[i], alpha);
        }
    }

    private void drawEquipmentSlot(MatrixStack matrix, float x, float y, float size, ItemStack stack, float alpha) {
        RenderUtil.CustomStyled.draw(matrix, x, y, size, size, Round.of(3f * hudScale), true, 16f * alpha);
        RenderUtil.Rounded.roundedRect(
                matrix, x, y, size, size,
                ColorUtil.replAlpha(ColorUtil.getColor(24, 24, 28), 0.62f * alpha),
                Round.of(3f * hudScale)
        );

        if (stack == null || stack.isEmpty()) {
            float pad = size * 0.22f;
            float lw = Math.max(1f, 1.35f * hudScale);
            int cross = ColorUtil.replAlpha(ColorUtil.getColor(228, 67, 67), alpha);
            RenderUtil.Rounded.drawLine(matrix, x + pad, y + pad, x + size - pad, y + size - pad, lw, cross);
            RenderUtil.Rounded.drawLine(matrix, x + size - pad, y + pad, x + pad, y + size - pad, lw, cross);
            return;
        }

        float drawScale = (size - 3f * hudScale) / 16f;
        float dx = x + (size - 16f * drawScale) / 2f;
        float dy = y + (size - 16f * drawScale) / 2f;

        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1f, 1f, 1f, alpha);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(dx, dy, 2f);
        GlStateManager.scalef(drawScale, drawScale, drawScale);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        GlStateManager.popMatrix();
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private void drawScrollingName(MatrixStack matrix, String text, float x, float y, float availableWidth, int color, float size) {
        float textWidth = font.getWidth(text, size);
        long currentTime = System.currentTimeMillis();

        if (textWidth > availableWidth) {
            if (currentTime > scrollPauseUntil) {
                long updateRate = 16L;
                if (currentTime - lastScrollTime >= updateRate) {
                    float speed = 0.45f * hudScale;
                    float minX = -(textWidth - availableWidth);

                    if (scrollingForward) {
                        nameScrollX -= speed;
                        if (nameScrollX <= minX) {
                            nameScrollX = minX;
                            scrollingForward = false;
                            scrollPauseUntil = currentTime + 900L;
                        }
                    } else {
                        nameScrollX += speed;
                        if (nameScrollX >= 0f) {
                            nameScrollX = 0f;
                            scrollingForward = true;
                            scrollPauseUntil = currentTime + 900L;
                        }
                    }
                    lastScrollTime = currentTime;
                }
            }
        } else {
            nameScrollX = 0f;
            scrollingForward = true;
        }

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, y - 2f * hudScale, availableWidth, size + 4f * hudScale);
        font.draw(matrix, text, x + nameScrollX, y, color, size);
        ScissorExp.unset();
        ScissorExp.pop();
    }

    private float getTargetPureHealth() {
        float health = target.getHealthFixed();
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
                health = score.getScorePoints();
            } catch (Exception ignored) {
            }
        }
        return Math.max(0, health);
    }

    private float getTargetAbsorption() {
        if (target instanceof PlayerEntity) {
            return Math.max(0, target.getAbsorptionAmount());
        }
        return 0f;
    }

    private void drawTargetHead(MatrixStack matrix, float x, float y, float size, float alpha) {
        float hurt = Mathf.clamp(0f, 1f, (target.hurtTime - (target.hurtTime > 0 ? mc.timer.renderPartialTicks : 0f)) / 10f);
        if (target instanceof AbstractClientPlayerEntity player) {
            RenderSystem.pushMatrix();
            RenderSystem.color4f(1f, 1f, 1f, alpha);
            RenderSystem.multMatrix(matrix.getLast().getMatrix());
            float pivotX = x + size / 2.0F;
            float pivotY = y + size / 2.0F;
            RenderSystem.translatef(pivotX, pivotY, 0);
            RenderSystem.rotatef(180, 0, 0, 1);
            RenderSystem.translatef(-pivotX, -pivotY, 0);
            RenderUtil.Texture.roundedHead(player.getLocationSkin(), x, y, size, size, 5f * hudScale, alpha, hurt);
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            RenderSystem.popMatrix();
        } else {
            RenderSystem.pushMatrix();
            float c = 1f - hurt * 0.72f;
            RenderSystem.color4f(1f, c, c, alpha);
            drawEntityOnScreen(matrix, x + size / 2f, y + size * 0.9f, 10f * hudScale, target);
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            RenderSystem.popMatrix();
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
        if (!inWorld) return;

        openAnimation.update();
        healthAnimation.update();
        openAnimation.run(1.0, 0.3, Easings.CUBIC_OUT, true);
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

        float originalX = drag.position.x;
        float originalY = drag.position.y;
        float width = 114f * hudScale;
        float height = 42f * hudScale;
        drag.position.set(-width / 2f, -height / 2f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        drawTargetHud(new Render2DEvent(matrix, mc.getRenderManager().info, mc.getMainWindow(), event.getPartialTicks()));

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        drag.position.set(originalX, originalY);
        matrix.pop();
    }
}

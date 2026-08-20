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
public class TargetHudExpensive extends AbstractHud implements IRenderer, IAttack, IConfigurableHud {
    final DragSetting drag;
    final Animation openAnimation = new Animation();
    final Animation healthAnimation = new Animation();
    final StopWatch time = new StopWatch();

    public boolean inWorld;
    public LivingEntity target;
    public float hudScale = 1.0f;

    private float renderedHealth = 0f;
    private float nameScrollX = 0f;
    private long lastScrollTime = 0L;
    private long scrollPauseUntil = 0L;
    private boolean scrollForward = true;

    public TargetHudExpensive(DragSetting drag) {
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
        } else if (target == mc.player && inChat) {
            inWorld = true;
        } else if (mc.world != null) {
            inWorld = StreamSupport.stream(mc.world.getAllEntities().spliterator(), true).anyMatch(e -> e.equals(target));
        }

        openAnimation.update();
        healthAnimation.update();

        boolean out = target == null || (!inWorld && !inChat) || time.finished(1000);
        openAnimation.run(out ? 0.0 : 1.0, 0.28, out ? Easings.CUBIC_IN : Easings.CUBIC_OUT, true);

        if (openAnimation.getValue() <= 0.0) {
            target = null;
            renderedHealth = 0f;
            nameScrollX = 0f;
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
        float width = 150f * scale;
        float height = 58f * scale;
        float rounding = 6f * scale;
        float spacing = 6f * scale;
        float headSize = 32f * scale;

        drag.size.set(width, height);
        if (x == 0 && y == 0) {
            x = mc.getMainWindow().getScaledWidth() / 2f - width / 2f;
            y = 42f;
            drag.position.set(x, y);
        }

        float alpha = (float) openAnimation.getValue();

        RenderUtil.CustomStyled.draw(matrix, x, y, width, height, Round.of(rounding), true, 24f * alpha);
        RenderUtil.Rounded.roundedRect(
                matrix, x, y, width, height,
                ColorUtil.replAlpha(ColorUtil.getColor(8, 8, 12), 0.75f * alpha),
                Round.of(rounding)
        );

        drawTargetHead(matrix, x + spacing, y + spacing, headSize, alpha);

        float textX = x + spacing + headSize + spacing;
        float nameY = y + spacing + 2f * scale;
        float nameSize = 8.2f * scale;
        float maxNameWidth = width - (textX - x) - spacing;
        drawScrollingName(matrix, target.getName().getString(), textX, nameY, maxNameWidth, nameSize, alpha);

        if (target instanceof PlayerEntity player) {
            float itemY = nameY + 10.5f * scale;
            float itemSize = 15f * scale;
            float itemGap = 17f * scale;
            drawItemStack(matrix, player.getHeldItemMainhand(), textX, itemY, itemSize, alpha);
            drawItemStack(matrix, player.getItemStackFromSlot(EquipmentSlotType.HEAD), textX + itemGap, itemY, itemSize, alpha);
            drawItemStack(matrix, player.getItemStackFromSlot(EquipmentSlotType.CHEST), textX + itemGap * 2f, itemY, itemSize, alpha);
            drawItemStack(matrix, player.getItemStackFromSlot(EquipmentSlotType.LEGS), textX + itemGap * 3f, itemY, itemSize, alpha);
            drawItemStack(matrix, player.getItemStackFromSlot(EquipmentSlotType.FEET), textX + itemGap * 4f, itemY, itemSize, alpha);
            drawItemStack(matrix, player.getHeldItemOffhand(), textX + itemGap * 5f, itemY, itemSize, alpha);
        }

        float lineX = x + spacing;
        float lineY = y + height - 16f * scale;
        float lineW = width - spacing * 2f;
        RenderUtil.Rounded.roundedRect(
                matrix, lineX, lineY, lineW, 1f * scale,
                ColorUtil.replAlpha(ColorUtil.WHITE, 0.12f * alpha),
                Round.of(0.5f * scale)
        );

        float hp = getTargetHealth();
        float maxHp = Math.max(1f, getTargetMaxHealth());
        float hpPct = Mathf.clamp(0f, 1f, hp / maxHp);
        healthAnimation.run(hpPct, 0.27, Easings.EXPO_OUT, true);
        renderedHealth = Mathf.fast(renderedHealth, hp, 10f);

        String hpText = String.format("%.0fhp", renderedHealth);
        float hpTextSize = 8f * scale;
        float hpTextWidth = font.getWidth(hpText, hpTextSize);

        float barX = x + spacing;
        float barH = 5f * scale;
        float barY = y + height - spacing - barH;
        float barW = width - spacing * 3f - hpTextWidth;

        RenderUtil.Rounded.roundedRect(
                matrix, barX, barY, barW, barH,
                ColorUtil.replAlpha(ColorUtil.getColor(32, 32, 32), 0.8f * alpha),
                Round.of(2.5f * scale)
        );

        int hpColor = ColorUtil.replAlpha(theme().clientColor(), alpha);
        RenderUtil.Rounded.roundedRect(
                matrix, barX, barY, barW * (float) healthAnimation.getValue(), barH,
                hpColor, Round.of(2.5f * scale)
        );

        font.draw(matrix, hpText, barX + barW + spacing, barY - 1.3f * scale, hpColor, hpTextSize);
    }

    private void drawScrollingName(MatrixStack matrix, String text, float x, float y, float availableWidth, float size, float alpha) {
        float width = font.getWidth(text, size);
        long now = System.currentTimeMillis();

        if (width > availableWidth) {
            if (now > scrollPauseUntil && now - lastScrollTime >= 16L) {
                float speed = 0.45f * hudScale;
                float minX = -(width - availableWidth);
                if (scrollForward) {
                    nameScrollX -= speed;
                    if (nameScrollX <= minX) {
                        nameScrollX = minX;
                        scrollForward = false;
                        scrollPauseUntil = now + 850L;
                    }
                } else {
                    nameScrollX += speed;
                    if (nameScrollX >= 0f) {
                        nameScrollX = 0f;
                        scrollForward = true;
                        scrollPauseUntil = now + 850L;
                    }
                }
                lastScrollTime = now;
            }
        } else {
            nameScrollX = 0f;
            scrollForward = true;
        }

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, y - 1.5f * hudScale, availableWidth, size + 4f * hudScale);
        font.draw(matrix, text, x + nameScrollX, y, ColorUtil.replAlpha(ColorUtil.WHITE, alpha), size);
        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void drawItemStack(MatrixStack matrix, ItemStack stack, float x, float y, float size, float alpha) {
        RenderUtil.Rounded.roundedRect(
                matrix, x, y, size, size,
                ColorUtil.replAlpha(ColorUtil.getColor(20, 20, 24), 0.65f * alpha),
                Round.of(3f * hudScale)
        );

        if (stack == null || stack.isEmpty()) {
            float crossSize = size * 0.5f;
            float thickness = Math.max(1f, 1.2f * hudScale);
            float cx = x + size / 2f;
            float cy = y + size / 2f;
            int crossColor = ColorUtil.replAlpha(ColorUtil.getColor(180, 70, 70), alpha);

            RenderUtil.Rounded.drawLine(matrix, cx - crossSize / 2f, cy - crossSize / 2f, cx + crossSize / 2f, cy + crossSize / 2f, thickness, crossColor);
            RenderUtil.Rounded.drawLine(matrix, cx + crossSize / 2f, cy - crossSize / 2f, cx - crossSize / 2f, cy + crossSize / 2f, thickness, crossColor);
            return;
        }

        float drawScale = (size - 2f * hudScale) / 16f;
        float dx = x + (size - 16f * drawScale) / 2f;
        float dy = y + (size - 16f * drawScale) / 2f;

        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1f, 1f, 1f, alpha);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(dx, dy, 1f);
        GlStateManager.scalef(drawScale, drawScale, drawScale);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        GlStateManager.popMatrix();
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private void drawTargetHead(MatrixStack matrix, float x, float y, float size, float alpha) {
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
            RenderUtil.Texture.roundedHead(player.getLocationSkin(), x, y, size, size, 4f * hudScale, alpha, hurt);
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            RenderSystem.popMatrix();
        } else {
            RenderSystem.pushMatrix();
            float tint = 1f - hurt * 0.72f;
            RenderSystem.color4f(1f, tint, tint, alpha);
            drawEntityOnScreen(matrix, x + size / 2f, y + size * 0.9f, 10f * hudScale, target);
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            RenderSystem.popMatrix();
        }
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
        if (!inWorld || time.finished(1000)) return;

        openAnimation.update();
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
        float w = 150f * hudScale;
        float h = 58f * hudScale;
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
}

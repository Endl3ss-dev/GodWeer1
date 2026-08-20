package org.privates.client.screen.hud.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IAttack;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easing;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.lib.util.time.StopWatch;

import java.util.Arrays;
import java.util.stream.StreamSupport;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TargetHudMinimal3 extends AbstractHud implements IRenderer, IAttack, IConfigurableHud {
    final DragSetting drag;
    final Animation openAnimation = new Animation();
    final Animation healthAnimation = new Animation();
    final Animation healthCircleAnimation = new Animation();
    final StopWatch time = new StopWatch();

    final Animation swordAnimation = new Animation();
    final Animation offhandAnimation = new Animation();
    final Animation helmetAnimation = new Animation();
    final Animation chestplateAnimation = new Animation();
    final Animation leggingsAnimation = new Animation();
    final Animation bootsAnimation = new Animation();

    final Animation glowAnimation = new Animation();

    public boolean inWorld;
    public LivingEntity target;

    private float displayHealth = 0f;

    public float hudScale = 1.0f;

    private static final float ITEM_ANIMATION_DELAY = 0.03f;

    private static final int BG_COLOR = 0xFF0F0D16;
    private static int ACCENT_COLOR() { return Theme.getInstance().clientColor(); }
    private static final int SEPARATOR_COLOR = 0x0AFFFFFF;
    private static final int ITEM_SEPARATOR_COLOR = 0x1FFFFFFF;
    private static int TEXT_COLOR() { return Theme.getInstance().textColor(); }
    private static final int HEALTH_BG_COLOR = 0x14FFFFFF;

    public TargetHudMinimal3(DragSetting drag) {
        this.drag = drag;
        TargetHudRenderer.registerTargetHud(this);
    }

    @Getter
    @Setter
    private boolean enableOutline = false;
    @Getter
    @Setter
    private float outlineWidth = 1.0f;

    @Override
    public java.util.List<DragComponent.HudSetting> getHudSettings() {
        return Arrays.asList(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER,
                        () -> this.hudScale,
                        v -> this.hudScale = v,
                        0.5f, 2.0f, 0.1f),
                new DragComponent.HudSetting("Enable Outline", DragComponent.SettingType.BOOLEAN,
                        () -> this.enableOutline ? 1f : 0f,
                        v -> this.enableOutline = v > 0.5f),
                new DragComponent.HudSetting("Outline Width", DragComponent.SettingType.SLIDER,
                        () -> this.outlineWidth,
                        v -> this.outlineWidth = v,
                        0.5f, 3.0f, 0.1f)
        );
    }

    @Override
    public void render(Render2DEvent event) {
        AuraNew auraNew = AuraNew.getInstance();
        boolean inChatScreen = mc.currentScreen instanceof ChatScreen;

        if (inChatScreen) {
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
            if (target != mc.player || !inChatScreen) {
                if (mc.world != null) {
                    inWorld = StreamSupport.stream(mc.world.getAllEntities().spliterator(), true).anyMatch(entity -> entity.equals(target));
                }
            } else {
                inWorld = true;
            }
        }

        openAnimation.update();
        healthAnimation.update();
        healthCircleAnimation.update();
        glowAnimation.update();
        swordAnimation.update();
        offhandAnimation.update();
        helmetAnimation.update();
        chestplateAnimation.update();
        leggingsAnimation.update();
        bootsAnimation.update();

        boolean out = (target == null || (!inWorld && !inChatScreen) || time.finished(1000));

        openAnimation.run(out ? 0.0 : 1.0, 0.3, out ? Easings.CUBIC_IN : Easings.CUBIC_OUT, true);
        glowAnimation.run(out ? 0.0 : 1.0, 0.5, out ? Easings.CUBIC_IN : Easings.SINE_OUT, true);

        updateItemAnimations(out);

        if (openAnimation.getValue() <= 0.0) {
            target = null;
            displayHealth = 0f;
            return;
        }

        drawTargetHud(event);
    }

    private void updateItemAnimations(boolean out) {
        double targetValue = out ? 0.0 : 1.0;
        Easing easing = out ? Easings.CUBIC_IN : Easings.BACK_OUT;
        float duration = 0.25f;

        swordAnimation.run(targetValue, duration, easing, true);
        offhandAnimation.run(targetValue, duration + ITEM_ANIMATION_DELAY, easing, true);
        helmetAnimation.run(targetValue, duration + ITEM_ANIMATION_DELAY * 2, easing, true);
        chestplateAnimation.run(targetValue, duration + ITEM_ANIMATION_DELAY * 3, easing, true);
        leggingsAnimation.run(targetValue, duration + ITEM_ANIMATION_DELAY * 4, easing, true);
        bootsAnimation.run(targetValue, duration + ITEM_ANIMATION_DELAY * 5, easing, true);
    }

    private void drawTargetHud(Render2DEvent event) {
        if (this.target == null) return;

        MatrixStack matrix = event.getMatrix();
        boolean isPlayer = this.target instanceof AbstractClientPlayerEntity;

        float x = drag.position.x;
        float y = drag.position.y;

        float scaleValue = hudScale;

        float totalWidth = 136.25f * scaleValue;
        float totalHeight = 33.75f * scaleValue;
        float radius = 7.5f * scaleValue;

        drag.size.set(totalWidth, totalHeight);

        if (x == 0 && y == 0) {
            float screenWidth = mc.getMainWindow().getScaledWidth();
            float screenHeight = mc.getMainWindow().getScaledHeight();
            float newX = screenWidth / 2f - totalWidth / 2f;
            float newY = screenHeight / 2f - totalHeight / 2f;
            drag.position.set(newX, newY);
            x = newX;
            y = newY;
        }

        matrix.push();

        float openScale = (float) this.openAnimation.getValue();
        float fadeAlpha = openScale; // Fade animation - только прозрачность
        
        // Убираем zoom анимацию
        // matrix.translate и matrix.scale УДАЛЕНЫ

        float glowAlpha = (float) glowAnimation.getValue() * 0.48f * fadeAlpha;
        if (glowAlpha > 0.01f) {
       
        }

        // Use CustomStyled for theme support
        RenderUtil.CustomStyled.draw(matrix, x, y, totalWidth, totalHeight, Round.of(radius), true, 50f * fadeAlpha);

        float coverX = x;
        float coverY = y;
        float coverSize = 33.75f * scaleValue;
        float headPadding = 7.5f * scaleValue;
        float headSize = 18.75f * scaleValue;
        float headX = coverX + headPadding + 3;
        float headY = coverY + headPadding + 2;

        if (isPlayer) {
            RenderSystem.pushMatrix();
            RenderSystem.color4f(1f, 1f, 1f, fadeAlpha);
            drawPlayerHead(matrix, (AbstractClientPlayerEntity) target, headX, headY, headSize, openScale);
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            RenderSystem.popMatrix();
        } else {
            RenderSystem.pushMatrix();
            RenderSystem.color4f(1f, 1f, 1f, fadeAlpha);
            drawEntityOnScreen(matrix, headX + headSize / 2f, headY + headSize * 0.9f, 7f * scaleValue, this.target);
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            RenderSystem.popMatrix();
        }

        float separatorX = coverX + coverSize;
        float separatorWidth = 0.625f * scaleValue;
        RenderUtil.Rounded.roundedRect(matrix, separatorX, y, separatorWidth, totalHeight,
                ColorUtil.replAlpha(SEPARATOR_COLOR, fadeAlpha), Round.of(0));

        float infoX = separatorX + separatorWidth + 7.5f * scaleValue;
        float infoY = y + 7.5f * scaleValue;

        String nameText = target.getName().getString();
        if (nameText.length() > 12) {
            nameText = nameText.substring(0, 12) + "...";
        }
        float nameFontSize = 7.5f * scaleValue;
        font.draw(matrix, nameText, infoX, infoY, ColorUtil.replAlpha(TEXT_COLOR(), fadeAlpha), nameFontSize);

        float itemsY = infoY + nameFontSize + 2.5f * scaleValue + 2.5f * scaleValue;
        drawItems(matrix, infoX, itemsY, scaleValue, fadeAlpha);

        float health = getTargetHealth();
        float maxHealth = target.getMaxHealth();
        float healthPercent = maxHealth > 0 ? Math.min(1.0f, health / maxHealth) : 0;

        displayHealth = Mathf.fast(displayHealth, health, 10f);
        this.healthAnimation.run(healthPercent, 0.3, Easings.EXPO_OUT, true);
        this.healthCircleAnimation.run(healthPercent, 0.4, Easings.EXPO_OUT, true);

        float healthCircleSize = 18.75f * scaleValue;
        float healthCircleX = x + totalWidth - healthCircleSize - 7.5f * scaleValue;
        float healthCircleY = y + (totalHeight - healthCircleSize) / 2f;

        drawHealthCircle(matrix, healthCircleX, healthCircleY, healthCircleSize, scaleValue);

        matrix.pop();

        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private void drawHealthCircle(MatrixStack matrix, float x, float y, float size, float scaleValue) {
        float centerX = x + size / 2f;
        float centerY = y + size / 2f;
        float radius = size / 2f;
        float strokeWidth = 1.25f * scaleValue;

        RenderUtil.drawStrokedCircle(matrix, centerX, centerY, radius, strokeWidth, HEALTH_BG_COLOR);

        float healthPercent = (float) healthCircleAnimation.getValue();
        if (healthPercent > 0.01f) {
            float startAngle = -90f;
            float sweepAngle = healthPercent * 360f;
            RenderUtil.drawArc(matrix, centerX + 0.2f, centerY + 0.5f, radius, startAngle, sweepAngle, strokeWidth, ACCENT_COLOR());
        }

        // Render absorption (golden hearts) arc on top if not Funtime
        if (target instanceof net.minecraft.entity.player.PlayerEntity player && !org.privates.client.utils.player.PlayerUtil.isFuntime()) {
            float absorption = player.getAbsorptionAmount();
            if (absorption > 0) {
                float maxHealth = player.getMaxHealth();
                float absorptionPercent = Math.min(1.0f, absorption / maxHealth);
                if (absorptionPercent > 0.01f) {
                    float startAngle = -90f;
                    float sweepAngle = absorptionPercent * 360f;
                    // Golden/yellow color for absorption
                    int absorptionColor = ColorUtil.getColor(255, 215, 0);
                    RenderUtil.drawArc(matrix, centerX + 0.2f, centerY + 0.5f, radius, startAngle, sweepAngle, strokeWidth, absorptionColor);
                }
            }
        }

        String healthText = String.valueOf((int) displayHealth);
        float healthFontSize = 7.5f * scaleValue;
        float textWidth = font.getWidth(healthText, healthFontSize);
        float textHeight = font.getHeight(healthText, healthFontSize);
        font.draw(matrix, healthText, centerX - textWidth / 2f, centerY - textHeight / 2f, TEXT_COLOR(), healthFontSize);
    }

    private void drawItems(MatrixStack matrix, float startX, float startY, float scaleValue, float fadeAlpha) {
        float itemSize = 7.5f * scaleValue;
        float separatorWidth = 0.625f * scaleValue;
        float separatorHeight = 5f * scaleValue;
        float separatorPadding = 1.25f * scaleValue;
        float gap = 1.25f * scaleValue;

        ItemStack mainHand = target.getHeldItemMainhand();
        ItemStack offHand = target.getHeldItemOffhand();
        ItemStack helmet = target.getItemStackFromSlot(EquipmentSlotType.HEAD);
        ItemStack chestplate = target.getItemStackFromSlot(EquipmentSlotType.CHEST);
        ItemStack leggings = target.getItemStackFromSlot(EquipmentSlotType.LEGS);
        ItemStack boots = target.getItemStackFromSlot(EquipmentSlotType.FEET);

        float currentX = startX;

        drawAnimatedItem(matrix, mainHand, currentX, startY, itemSize, swordAnimation, fadeAlpha);
        currentX += itemSize + gap;

        drawItemSeparator(matrix, currentX, startY + separatorPadding, separatorWidth, separatorHeight, fadeAlpha);
        currentX += separatorWidth + gap;

        if (!offHand.isEmpty()) {
            drawAnimatedItem(matrix, offHand, currentX, startY, itemSize, offhandAnimation, fadeAlpha);
        } else {
            drawEmptyItemSlot(matrix, currentX, startY, itemSize, offhandAnimation, fadeAlpha);
        }
        currentX += itemSize + gap;

        drawItemSeparator(matrix, currentX, startY + separatorPadding, separatorWidth, separatorHeight, fadeAlpha);
        currentX += separatorWidth + gap;

        drawAnimatedItem(matrix, helmet, currentX, startY, itemSize, helmetAnimation, fadeAlpha);
        currentX += itemSize + gap;

        drawItemSeparator(matrix, currentX, startY + separatorPadding, separatorWidth, separatorHeight, fadeAlpha);
        currentX += separatorWidth + gap;

        drawAnimatedItem(matrix, chestplate, currentX, startY, itemSize, chestplateAnimation, fadeAlpha);
        currentX += itemSize + gap;

        drawItemSeparator(matrix, currentX, startY + separatorPadding, separatorWidth, separatorHeight, fadeAlpha);
        currentX += separatorWidth + gap;

        drawAnimatedItem(matrix, leggings, currentX, startY, itemSize, leggingsAnimation, fadeAlpha);
        currentX += itemSize + gap;

        drawItemSeparator(matrix, currentX, startY + separatorPadding, separatorWidth, separatorHeight, fadeAlpha);
        currentX += separatorWidth + gap;

        drawAnimatedItem(matrix, boots, currentX, startY, itemSize, bootsAnimation, fadeAlpha);
    }

    private void drawItemSeparator(MatrixStack matrix, float x, float y, float width, float height, float fadeAlpha) {
        RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, ColorUtil.replAlpha(ITEM_SEPARATOR_COLOR, fadeAlpha), Round.of(320f));
    }

    private void drawEmptyItemSlot(MatrixStack matrix, float x, float y, float size, Animation animation, float fadeAlpha) {
        float animValue = (float) animation.getValue();
        if (animValue <= 0.01f) return;

        float scale = 0.5f + (0.5f * animValue);
        float centerX = x + size / 2f;
        float centerY = y + size / 2f;
        float crossSize = size * 0.5f * scale;

        int crossColor = ColorUtil.multAlpha(0x7AFFFFFF, animValue * fadeAlpha);

        float thickness = 0.625f;
        RenderUtil.Rounded.roundedRect(matrix, centerX - crossSize / 2f, centerY - thickness / 2f,
                crossSize, thickness, crossColor, Round.of(0.3125f));
        RenderUtil.Rounded.roundedRect(matrix, centerX - thickness / 2f, centerY - crossSize / 2f,
                thickness, crossSize, crossColor, Round.of(0.3125f));
    }

    private void drawAnimatedItem(MatrixStack matrix, ItemStack item, float x, float y, float size, Animation animation, float fadeAlpha) {
        if (item.isEmpty()) return;

        float animValue = (float) animation.getValue();
        if (animValue <= 0.01f) return;

        float alpha = animValue * fadeAlpha;
        float scale = 0.5f + (0.5f * animValue);
        float offsetY = (1f - animValue) * 3.125f;

        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1f, 1f, 1f, alpha);

        float centerX = x + size / 2f;
        float centerY = y + size / 2f + offsetY;

        GlStateManager.pushMatrix();
        GlStateManager.translatef(centerX, centerY, 100);
        GlStateManager.scalef(scale * (size / 16f), scale * (size / 16f), 1f);
        GlStateManager.translatef(-8, -8, 0);

        mc.getItemRenderer().renderItemAndEffectIntoGUI(item, 0, 0);

        GlStateManager.popMatrix();

        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private float getTargetHealth() {
        if (!inWorld && !(mc.currentScreen instanceof ChatScreen)) {
            return 0;
        }
        return target.getHealthFixed();
    }

    private void drawPlayerHead(MatrixStack matrix, final AbstractClientPlayerEntity player, final float x, final float y, final float size, float openScale) {
        float hurtPC = Mathf.clamp(0f, 1f, (player.hurtTime - (player.hurtTime > 0 ? mc.timer.renderPartialTicks : 0f)) / 10f);
        int headColor = ColorUtil.overCol(ColorUtil.WHITE, ColorUtil.RED, hurtPC);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0F);
        RenderSystem.enableTexture();

        mc.getTextureManager().bindTexture(player.getLocationSkin());

        float[] rgba = ColorUtil.getRGBAf(headColor);
        RenderSystem.color4f(rgba[0], rgba[1], rgba[2], rgba[3]);

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrix.getLast().getMatrix());

        float pivotX = x + size / 2.0F;
        float pivotY = y + size / 2.0F;

        RenderSystem.translatef(pivotX, pivotY, 0);
        RenderSystem.rotatef(180, 0, 0, 1);
        RenderSystem.translatef(-pivotX, -pivotY, 0);

        RenderUtil.Texture.roundedHead(player.getLocationSkin(), x, y, size, size, size / 9F * openScale, rgba[3], hurtPC);

        RenderSystem.popMatrix();

        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.disableBlend();
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
        EntityRendererManager entityrenderermanager = mc.getRenderManager();
        IRenderTypeBuffer.Impl irendertypebuffer$impl = mc.getRenderTypeBuffers().getBufferSource();
        entityrenderermanager.renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, irendertypebuffer$impl, 15728880);
        irendertypebuffer$impl.finish();
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
        AuraNew auraNew = org.privates.client.managers.module.impl.combat.AuraNew.getInstance();
        boolean inChatScreen = mc.currentScreen instanceof ChatScreen;
        if (inChatScreen) return;
        if (mc.pointedEntity instanceof LivingEntity entity) { target = entity; time.reset(); }
        if (auraNew.isEnabled() && auraNew.target() != null) { target = auraNew.target(); time.reset(); }
        if (target == null || target == mc.player) return;
        if (mc.world != null) { inWorld = StreamSupport.stream(mc.world.getAllEntities().spliterator(), true).anyMatch(e -> e.equals(target)); }
        if (!inWorld || time.finished(1000) || mc.getRenderManager() == null) return;
        
        MatrixStack matrix = event.getMatrix();
        Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();
        Vector3d targetPos = target.getPositionVec().add(0, target.getHeight() / 2.0, 0).subtract(cameraPos);
        double distance = mc.player.getDistance(target);
        float scale = (float) Math.max(0.005, 0.0075 * (distance / 5.0));
        
        matrix.push();
        matrix.translate(targetPos.x, targetPos.y, targetPos.z);
        matrix.rotate(mc.getRenderManager().getCameraOrientation());
        matrix.translate(0, 0, -0.5); // Move closer to camera (render on top)
        matrix.scale(-scale, -scale, scale);
        
        float originalX = drag.position.x, originalY = drag.position.y;
        float totalWidth = 120f * hudScale, totalHeight = 40f * hudScale;
        drag.position.set(-totalWidth / 2f, -totalHeight / 2f);
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        render(new Render2DEvent(matrix, mc.getRenderManager().info, mc.getMainWindow(), event.getPartialTicks()));
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        
        drag.position.set(originalX, originalY);
        matrix.pop();
    }
}

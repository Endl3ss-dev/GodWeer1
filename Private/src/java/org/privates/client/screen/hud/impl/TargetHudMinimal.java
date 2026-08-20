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
import net.minecraft.item.UseAction;
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
public class TargetHudMinimal extends AbstractHud implements IRenderer, IAttack, IConfigurableHud {
    final DragSetting drag;
    final Animation openAnimation = new Animation();
    final Animation healthAnimation = new Animation();
    final StopWatch time = new StopWatch();

    final Animation swordAnimation = new Animation();
    final Animation offhandAnimation = new Animation();
    final Animation helmetAnimation = new Animation();
    final Animation chestplateAnimation = new Animation();
    final Animation leggingsAnimation = new Animation();
    final Animation bootsAnimation = new Animation();

    public boolean inWorld;
    public LivingEntity target;

    private float displayHealth = 0f;

    public float hudScale = 0.9f;

    private static final float ITEM_ANIMATION_DELAY = 0.03f;

    public TargetHudMinimal(DragSetting drag) {
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
        swordAnimation.update();
        offhandAnimation.update();
        helmetAnimation.update();
        chestplateAnimation.update();
        leggingsAnimation.update();
        bootsAnimation.update();

        boolean out = (target == null || (!inWorld && !inChatScreen) || time.finished(1000));

        openAnimation.run(out ? 0.0 : 1.0, 0.3, out ? Easings.CUBIC_IN : Easings.CUBIC_OUT, true);

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

        float totalWidth = 107.5f * scaleValue;
        float totalHeight = 35f * scaleValue;
        float radius = 4.375f * scaleValue;

        drag.size.set(totalWidth, totalHeight);

        if (x == 0 && y == 0) {
            float screenWidth = mc.getMainWindow().getScaledWidth();
            float newX = screenWidth / 2f - totalWidth / 2f;
            float newY = 25f;
            drag.position.set(newX, newY);
            x = newX;
            y = newY;
        }

        matrix.push();

        float openScale = (float) this.openAnimation.getValue();
        float fadeAlpha = openScale; // Fade animation - только прозрачность, без zoom
        
        // Убираем zoom анимацию - рендерим в нормальном размере
        // matrix.translate и matrix.scale УДАЛЕНЫ

        // Use CustomStyled for theme support
        RenderUtil.CustomStyled.draw(matrix, x, y, totalWidth, totalHeight, Round.of(radius), true, 50f * fadeAlpha);

        int borderColor = ColorUtil.replAlpha(ColorUtil.rgba(30, 30, 35, 255), fadeAlpha);
        RenderUtil.Rounded.roundedOutline(matrix, x, y, totalWidth, totalHeight, 0.625f * scaleValue, borderColor, Round.of(radius));

        int shadowColor = ColorUtil.replAlpha(ColorUtil.rgba(27, 27, 32, 128), fadeAlpha);
        RenderUtil.Shadow.drawShadow(matrix, x, y, totalWidth, totalHeight, 3.125f * scaleValue, shadowColor, Round.of(radius));

        float avatarSize = 32.5f * scaleValue;
        float avatarX = x + 2.5f * scaleValue;
        float avatarY = y + 2.5f * scaleValue;

        int avatarBgColor = ColorUtil.replAlpha(ColorUtil.rgba(33, 33, 38, 255), fadeAlpha);
        int avatarBorderColor = ColorUtil.replAlpha(ColorUtil.rgba(39, 39, 44, 255), fadeAlpha);
        RenderUtil.Rounded.roundedRect(matrix, avatarX - 1, avatarY - 1.5f, avatarSize, avatarSize, avatarBgColor, Round.of(3.125f * scaleValue));
        RenderUtil.Rounded.roundedOutline(matrix, avatarX - 1, avatarY - 1.5f, avatarSize, avatarSize, 0.625f * scaleValue, avatarBorderColor, Round.of(3.125f * scaleValue));
        RenderUtil.Shadow.drawShadow(matrix, avatarX - 1, avatarY, avatarSize - 1.5f, avatarSize, 6.25f * scaleValue, ColorUtil.replAlpha(ColorUtil.rgba(0, 0, 0, 64), fadeAlpha), Round.of(3.125f * scaleValue));

        if (isPlayer) {
            RenderSystem.pushMatrix();
            RenderSystem.color4f(1f, 1f, 1f, fadeAlpha);
            drawPlayerHead(matrix, (AbstractClientPlayerEntity) target, avatarX + 1, avatarY + 1, avatarSize, openScale);
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            RenderSystem.popMatrix();
        } else {
            RenderSystem.pushMatrix();
            RenderSystem.color4f(1f, 1f, 1f, fadeAlpha);
            drawEntityOnScreen(matrix, avatarX + avatarSize / 2f, avatarY + avatarSize * 0.9f, 11.25f * scaleValue, this.target);
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            RenderSystem.popMatrix();
        }

        float panelWidth = 70f * scaleValue;
        float panelHeight = 30f * scaleValue;
        float panelX = x + 35f * scaleValue;
        float panelY = y + 2.5f * scaleValue;

        int panelBgColor = ColorUtil.replAlpha(ColorUtil.rgba(33, 33, 38, 255), fadeAlpha);
        int panelBorderColor = ColorUtil.replAlpha(ColorUtil.rgba(39, 39, 44, 255), fadeAlpha);
        RenderUtil.Rounded.roundedRect(matrix, panelX, panelY, panelWidth, panelHeight, panelBgColor, Round.of(3.125f * scaleValue));
        RenderUtil.Rounded.roundedOutline(matrix, panelX, panelY, panelWidth, panelHeight, 0.625f * scaleValue, panelBorderColor, Round.of(3.125f * scaleValue));
        RenderUtil.Shadow.drawShadow(matrix, panelX, panelY, panelWidth, panelHeight, 6.25f * scaleValue, ColorUtil.replAlpha(ColorUtil.rgba(0, 0, 0, 64), fadeAlpha), Round.of(3.125f * scaleValue));

        String nameText = target.getName().getString();
        if (nameText.length() > 10) {
            nameText = nameText.substring(0, 10) + "...";
        }
        float nameFontSize = 7.5f * scaleValue;
        float nameX = panelX + 3.75f * scaleValue;
        float nameY = panelY + 0.625f * scaleValue;
        font.draw(matrix, nameText, nameX, nameY, ColorUtil.replAlpha(ColorUtil.WHITE, fadeAlpha), nameFontSize);

        float health = getTargetHealth();
        float maxHealth = target.getMaxHealth();
        float healthPercent = maxHealth > 0 ? Math.min(1.0f, health / maxHealth) : 0;

        displayHealth = Mathf.fast(displayHealth, health, 10f);
        this.healthAnimation.run(healthPercent, 0.3, Easings.EXPO_OUT, true);

        String healthText = String.format("%.0f", displayHealth);
        float healthFontSize = 7.5f * scaleValue;
        float healthTextX = panelX + 50f * scaleValue;
        float healthTextY = panelY + 1.25f * scaleValue;
        int healthTextColor = ColorUtil.replAlpha(Theme.getInstance().clientColor(), fadeAlpha);
        font.draw(matrix, healthText, healthTextX, healthTextY, healthTextColor, healthFontSize);

        drawItems(matrix, panelX, panelY, scaleValue, fadeAlpha);

        float barWidth = 63.75f * scaleValue;
        float barHeight = 1.875f * scaleValue;
        float barX = panelX + 3.125f * scaleValue;
        float barY = panelY + 25f * scaleValue;

        float filledWidth = barWidth * (float) healthAnimation.getValue();

        int barColor = ColorUtil.replAlpha(Theme.getInstance().clientColor(), fadeAlpha);
        RenderUtil.Rounded.roundedRect(matrix, barX, barY, filledWidth, barHeight, barColor, Round.of(1.25f * scaleValue));

        // Render absorption (golden hearts) bar on top if not Funtime
        if (target instanceof net.minecraft.entity.player.PlayerEntity player && !org.privates.client.utils.player.PlayerUtil.isFuntime()) {
            float absorption = player.getAbsorptionAmount();
            if (absorption > 0) {
                float absorptionPercent = Math.min(1.0f, absorption / maxHealth);
                float absorptionWidth = barWidth * absorptionPercent;
                
                // Golden/yellow color for absorption
                int absorptionColor = ColorUtil.replAlpha(ColorUtil.getColor(255, 215, 0), fadeAlpha * 0.7f);
                RenderUtil.Rounded.roundedRect(matrix, barX, barY, absorptionWidth, barHeight, absorptionColor, Round.of(1.25f * scaleValue));
            }
        }

        if ((target.getHeldItemOffhand().getUseAction() == UseAction.EAT || target.getHeldItemMainhand().getUseAction() == UseAction.EAT) && target.isHandActive()) {
            drawItemUseIndicator(matrix, x, avatarY, avatarSize, scaleValue);
        }

        matrix.pop();

        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private void drawItems(MatrixStack matrix, float panelX, float panelY, float scaleValue, float fadeAlpha) {
        float itemSize = 5.625f * scaleValue;
        float itemY = panelY + 17.5f * scaleValue;

        ItemStack mainHand = target.getHeldItemMainhand();
        ItemStack offHand = target.getHeldItemOffhand();
        ItemStack helmet = target.getItemStackFromSlot(EquipmentSlotType.HEAD);
        ItemStack chestplate = target.getItemStackFromSlot(EquipmentSlotType.CHEST);
        ItemStack leggings = target.getItemStackFromSlot(EquipmentSlotType.LEGS);
        ItemStack boots = target.getItemStackFromSlot(EquipmentSlotType.FEET);

        float swordX = panelX + 4.375f * scaleValue;
        drawAnimatedItem(matrix, mainHand, swordX, itemY, itemSize, swordAnimation, fadeAlpha);

        float offhandX = panelX + 10f * scaleValue;
        drawAnimatedItem(matrix, offHand, offhandX, itemY, itemSize, offhandAnimation, fadeAlpha);

        float helmetX = panelX + 42.5f * scaleValue;
        drawAnimatedItem(matrix, helmet, helmetX, itemY, itemSize, helmetAnimation, fadeAlpha);

        float chestX = panelX + 48.75f * scaleValue;
        drawAnimatedItem(matrix, chestplate, chestX, itemY, itemSize, chestplateAnimation, fadeAlpha);

        float legsX = panelX + 55f * scaleValue;
        drawAnimatedItem(matrix, leggings, legsX, itemY, itemSize, leggingsAnimation, fadeAlpha);

        float bootsX = panelX + 61.25f * scaleValue;
        drawAnimatedItem(matrix, boots, bootsX, itemY, itemSize, bootsAnimation, fadeAlpha);
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

    private void drawItemUseIndicator(MatrixStack matrix, float x, float avatarY, float avatarSize, float scaleValue) {
        float circleRadius = 9.375f * scaleValue;
        float circleX = x - 23.125f * scaleValue + circleRadius;
        float circleY = avatarY + 10f * scaleValue + circleRadius;

        int circleBgColor = ColorUtil.rgba(28, 28, 33, 255);
        RenderUtil.Rounded.roundedRect(matrix, circleX - circleRadius, circleY - circleRadius, circleRadius * 2, circleRadius * 2, circleBgColor, Round.of(circleRadius));

        ItemStack itemInUse = target.getHeldItemOffhand().getUseAction() == UseAction.EAT ? target.getHeldItemOffhand() : target.getHeldItemMainhand();

        if (!itemInUse.isEmpty()) {
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.color4f(1f, 1f, 1f, 1f);

            float itemDisplaySize = 14.375f * scaleValue;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(circleX - itemDisplaySize / 2f, circleY - itemDisplaySize / 2f, 100);
            GlStateManager.scalef(itemDisplaySize / 16f, itemDisplaySize / 16f, 1f);
            mc.getItemRenderer().renderItemAndEffectIntoGUI(itemInUse, 0, 0);
            GlStateManager.popMatrix();

            RenderSystem.color4f(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
        }

        int useDuration = target.getItemInUseMaxCount();
        int maxUseDuration = itemInUse.getUseDuration();

        float progress = 0;
        if (maxUseDuration > 0 && useDuration > 0) {
            progress = Math.min(1f, (float) useDuration / maxUseDuration) * 360f;
        }

        int progressColor = Theme.getInstance().clientColor();
        RenderUtil.drawArc(matrix, circleX, circleY, circleRadius, -90, progress, scaleValue, progressColor);
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
        
        if (inChatScreen) {
            return; // Don't render in world when in chat
        }
        
        // Determine target
        if (mc.pointedEntity instanceof LivingEntity entity) {
            target = entity;
            time.reset();
        }
        if (auraNew.isEnabled() && auraNew.target() != null) {
            target = auraNew.target();
            time.reset();
        }
        
        if (target == null || target == mc.player) {
            return;
        }
        
        // Check if target is in world
        if (mc.world != null) {
            inWorld = StreamSupport.stream(mc.world.getAllEntities().spliterator(), true).anyMatch(e -> e.equals(target));
        }
        
        if (!inWorld) {
            return;
        }
        
        // Update animations
        openAnimation.update();
        healthAnimation.update();
        swordAnimation.update();
        offhandAnimation.update();
        helmetAnimation.update();
        chestplateAnimation.update();
        leggingsAnimation.update();
        bootsAnimation.update();
        
        boolean out = (target == null || !inWorld || time.finished(1000));
        openAnimation.run(out ? 0.0 : 1.0, 0.3, out ? Easings.CUBIC_IN : Easings.CUBIC_OUT, true);
        
        if (openAnimation.getValue() <= 0.0) {
            target = null;
            displayHealth = 0f;
            return;
        }
        
        // Render in 3D world
        if (mc.getRenderManager() == null) return;
        
        MatrixStack matrix = event.getMatrix();
        float partialTicks = event.getPartialTicks();
        Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();
        
        // Calculate target position (center of entity)
        Vector3d targetPos = target.getPositionVec()
                .add(0, target.getHeight() / 2.0, 0)
                .subtract(cameraPos);
        
        // Calculate distance for scaling (increased 5x)
        double distance = mc.player.getDistance(target);
        float scale = (float) Math.max(0.005, 0.0075 * (distance / 5.0));
        
        matrix.push();
        
        // Translate to target position
        matrix.translate(targetPos.x, targetPos.y, targetPos.z);
        
        // Billboard effect - face camera
        matrix.rotate(mc.getRenderManager().getCameraOrientation());
        
        // Move closer to camera to render on top of everything
        matrix.translate(0, 0, -0.5);
        
        matrix.scale(-scale, -scale, scale);
        
        // Create a fake Render2DEvent for rendering
        Render2DEvent fakeEvent = new Render2DEvent(matrix, mc.getRenderManager().info, mc.getMainWindow(), partialTicks);
        
        // Temporarily override drag position to center
        float originalX = drag.position.x;
        float originalY = drag.position.y;
        
        float scaleValue = hudScale;
        float totalWidth = 120f * scaleValue;
        float totalHeight = 40f * scaleValue;
        
        drag.position.set(-totalWidth / 2f, -totalHeight / 2f);
        
        // Enable transparency
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        
        // Render the HUD
        render(fakeEvent);
        
        // Restore
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        
        drag.position.set(originalX, originalY);
        
        matrix.pop();
    }
}

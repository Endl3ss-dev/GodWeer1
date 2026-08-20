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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;
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
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TargetHudFigma extends AbstractHud implements IRenderer, IAttack, IConfigurableHud {
    final DragSetting drag;
    final Animation openAnimation = new Animation();
    final Animation healthAnimation = new Animation();
    final Animation absorptionAnimation = new Animation();
    final StopWatch time = new StopWatch();
    final StopWatch previewUpdateTimer = new StopWatch();

    public boolean inWorld;
    public LivingEntity target;

    final boolean blurEnabled = true;
    final Random random = new Random();

    private float displayHealth = 0f;
    private final List<ItemStack> previewArmor = new ArrayList<>();
    private final List<ItemStack> previewMainHand = new ArrayList<>();
    private int currentPreviewArmorIndex = 0;
    private int currentPreviewHandIndex = 0;

    public float hudScale = 0.9f;

    public TargetHudFigma(DragSetting drag) {
        this.drag = drag;
        TargetHudRenderer.registerTargetHud(this);
        initializePreviewItems();
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

    private void initializePreviewItems() {
        List<Item> allItems = Registry.ITEM.stream()
                .filter(item -> item != Items.AIR)
                .collect(Collectors.toList());

        for (int i = 0; i < 4; i++) {
            if (!allItems.isEmpty()) {
                Item randomItem = allItems.get(random.nextInt(allItems.size()));
                previewArmor.add(new ItemStack(randomItem));
            } else {
                previewArmor.add(ItemStack.EMPTY);
            }
        }

        for (int i = 0; i < 4; i++) {
            if (!allItems.isEmpty()) {
                Item randomItem = allItems.get(random.nextInt(allItems.size()));
                previewMainHand.add(new ItemStack(randomItem));
            } else {
                previewMainHand.add(ItemStack.EMPTY);
            }
        }
    }

    private void updatePreviewItems() {
        if (previewUpdateTimer.finished(1000)) {
            List<Item> allItems = Registry.ITEM.stream()
                    .filter(item -> item != Items.AIR)
                    .collect(Collectors.toList());

            if (!allItems.isEmpty()) {
                Item randomArmorItem = allItems.get(random.nextInt(allItems.size()));
                previewArmor.set(currentPreviewArmorIndex, new ItemStack(randomArmorItem));
                currentPreviewArmorIndex = (currentPreviewArmorIndex + 1) % 4;

                Item randomHandItem = allItems.get(random.nextInt(allItems.size()));
                previewMainHand.set(currentPreviewHandIndex, new ItemStack(randomHandItem));
                currentPreviewHandIndex = (currentPreviewHandIndex + 1) % 4;
            }
            previewUpdateTimer.reset();
        }
    }

    @Override
    public void render(Render2DEvent event) {
        AuraNew auraNew = AuraNew.getInstance();
        boolean inChatScreen = mc.currentScreen instanceof ChatScreen;

        if (inChatScreen) {
            target = mc.player;
            time.reset();
            updatePreviewItems();
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
        absorptionAnimation.update();

        boolean out = (target == null || (!inWorld && !inChatScreen) || time.finished(1000));

        openAnimation.run(out ? 0.0 : 1.0, 0.3, out ? Easings.CUBIC_IN : Easings.CUBIC_OUT, true);

        if (openAnimation.getValue() <= 0.0) {
            target = null;
            displayHealth = 0f;
            return;
        }

        drawTargetHud(event);
    }

    private void drawTargetHud(Render2DEvent event) {
        if (this.target == null) return;

        MatrixStack matrix = event.getMatrix();
        boolean isPlayer = this.target instanceof AbstractClientPlayerEntity;
        boolean inChatScreen = mc.currentScreen instanceof ChatScreen;

        float x = drag.position.x;
        float y = drag.position.y;

        float scaleValue = hudScale;

        float totalWidth = 101.25f * scaleValue;
        float totalHeight = 70.375f * scaleValue;
        float topHeight = 15.1875f * scaleValue;
        float bottomHeight = 15.1875f * scaleValue;
        float middleHeight = 41.0625f * scaleValue;
        float radius = 5.625f * scaleValue;

        drag.size.set(totalWidth, totalHeight);

        if (x == 0 && y == 0) {
            float screenWidth = mc.getMainWindow().getScaledWidth();
            float newX = screenWidth / 2f - totalWidth / 2f;
            float newY = 40f;
            drag.position.set(newX, newY);
            x = newX;
            y = newY;
        }

        matrix.push();

        float openScale = (float) this.openAnimation.getValue();
        float fadeAlpha = openScale;

        float topY = y;
        float middleY = y + topHeight;
        float bottomY = y + topHeight + middleHeight;

        RenderUtil.CustomStyled.drawGui(matrix, x, topY, totalWidth, totalHeight, radius, blurEnabled, 25f * fadeAlpha);

        int middleGradientTop = ColorUtil.rgba(8, 7, 13, (int) (0.42f * 255 * fadeAlpha));
        int middleGradientBottom = ColorUtil.rgba(17, 16, 30, (int) (0.42f * 255 * fadeAlpha));
        RenderUtil.Rounded.drawGradientRound(matrix, x, topY, totalWidth, totalHeight, radius, middleGradientTop, middleGradientBottom, middleGradientTop, middleGradientBottom);

        int separatorColor = ColorUtil.rgba(255, 255, 255, (int)(25 * fadeAlpha));
        float separatorHeight = 0.5f * scaleValue;
        float separatorY1 = middleY - separatorHeight / 2f;
        float separatorY2 = bottomY - separatorHeight / 2f;

        RenderUtil.Rounded.roundedRect(matrix, x, separatorY1, totalWidth, separatorHeight, separatorColor, Round.of(0));
        RenderUtil.Rounded.roundedRect(matrix, x, separatorY2, totalWidth, separatorHeight, separatorColor, Round.of(0));

        if (enableOutline) {
            int outlineColor = ColorUtil.rgba(208, 89, 255, (int)(64 * fadeAlpha));
            RenderUtil.Rounded.roundedOutline(matrix, x - 0.5f * scaleValue, y - 0.5f * scaleValue, totalWidth + scaleValue, totalHeight + scaleValue, 0.5f * scaleValue, outlineColor, Round.of(radius));
        }

        String nameText = target.getName().getString();
        float nameFontSize = 6.1875f * scaleValue;
        float nameX = x + totalWidth / 2f - font.getWidth(nameText, nameFontSize) / 2f;
        float nameY = topY + topHeight / 2f - nameFontSize / 2f;
        font.draw(matrix, nameText, nameX, nameY, ColorUtil.rgba(255, 255, 255, (int)(255 * fadeAlpha)), nameFontSize);

        float headSize = 26.75f * scaleValue;
        float headX = x + totalWidth / 2f - headSize / 2f;
        float headY = middleY + 8.4375f * scaleValue;

        if (isPlayer) {
            drawPlayerHead(matrix, (AbstractClientPlayerEntity) target, headX + 2, headY, headSize, openScale, fadeAlpha);
        } else {
            drawEntityOnScreen(matrix, headX + headSize / 2f, headY + headSize * 0.9f, 10.125f * scaleValue, this.target, fadeAlpha);
        }

        if (isPlayer || inChatScreen) {
            AbstractClientPlayerEntity player = isPlayer ? (AbstractClientPlayerEntity) target : null;
            renderArmor(matrix, x, middleY, player, scaleValue, inChatScreen, fadeAlpha);
            renderMainHandItems(matrix, x, middleY, totalWidth, player, scaleValue, inChatScreen, fadeAlpha);
        }

        float health = getTargetHealth();
        float maxHealth = target.getMaxHealth();
        float healthPercent = maxHealth > 0 ? Math.min(1.0f, health / maxHealth) : 0;

        displayHealth = Mathf.fast(displayHealth, health, 10f);
        this.healthAnimation.run(healthPercent, 0.3, Easings.EXPO_OUT, true);

        float absorptionHealth = target.getAbsorptionAmount();
        float absorptionPercent = maxHealth > 0 ? Math.min(1.0f, absorptionHealth / maxHealth) : 0;
        this.absorptionAnimation.run(absorptionPercent, 0.3, Easings.EXPO_OUT, true);

        renderHealthBar(matrix, x, bottomY, totalWidth, bottomHeight, scaleValue, fadeAlpha);

        String healthText = String.format("%.0f HP", displayHealth);
        float healthFontSize = 6.1875f * scaleValue;
        float healthTextX = x + totalWidth / 2f - font.getWidth(healthText, healthFontSize) / 2f;
        float healthTextY = bottomY - 1 + bottomHeight / 2f - healthFontSize / 2f;
        font.draw(matrix, healthText, healthTextX, healthTextY, ColorUtil.rgba(255, 255, 255, (int)(255 * fadeAlpha)), healthFontSize);

        matrix.pop();

        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private void renderArmor(MatrixStack matrix, float x, float y, AbstractClientPlayerEntity player, float scaleValue, boolean preview, float fadeAlpha) {
        float itemSize = 11.25f * scaleValue;
        float startX = x + 4.5f * scaleValue;
        float startY = y + 8f * scaleValue;
        float gapX = 13.5f * scaleValue;
        float gapY = 12.9375f * scaleValue;

        List<ItemStack> armorToRender = new ArrayList<>();
        if (preview) {
            armorToRender.addAll(previewArmor);
        } else if (player != null) {
            armorToRender.add(player.getItemStackFromSlot(EquipmentSlotType.HEAD));
            armorToRender.add(player.getItemStackFromSlot(EquipmentSlotType.CHEST));
            armorToRender.add(player.getItemStackFromSlot(EquipmentSlotType.LEGS));
            armorToRender.add(player.getItemStackFromSlot(EquipmentSlotType.FEET));
        }

        for (int i = 0; i < Math.min(armorToRender.size(), 4); i++) {
            int row = i / 2;
            int col = i % 2;
            float itemX = startX + (col * gapX);
            float itemY = startY + (row * gapY);
            renderItemSlot(matrix, itemX, itemY, itemSize, armorToRender.get(i), scaleValue, fadeAlpha);
        }
    }

    private void renderMainHandItems(MatrixStack matrix, float x, float y, float width, AbstractClientPlayerEntity player, float scaleValue, boolean preview, float fadeAlpha) {
        float itemSize = 11.25f * scaleValue;
        float startX = x + width - 15.75f * scaleValue;
        float startY = y + 8f * scaleValue;
        float gapX = 12.9375f * scaleValue;
        float gapY = 12.9375f * scaleValue;

        List<ItemStack> handsToRender = new ArrayList<>();
        if (preview) {
            handsToRender.addAll(previewMainHand);
        } else if (player != null) {
            ItemStack mainHand = player.getHeldItemMainhand();
            ItemStack offHand = player.getHeldItemOffhand();
            if (!mainHand.isEmpty()) handsToRender.add(mainHand);
            if (!offHand.isEmpty()) handsToRender.add(offHand);
        }

        int index = 0;
        for (ItemStack hand : handsToRender) {
            if (index >= 4) break;
            int row = index / 2;
            int col = index % 2;
            float itemX = startX - (col * gapX);
            float itemY = startY + (row * gapY);
            renderItemSlot(matrix, itemX, itemY, itemSize, hand, scaleValue, fadeAlpha);
            index++;
        }
    }

    private void renderItemSlot(MatrixStack matrix, float x, float y, float size, ItemStack stack, float scaleValue, float fadeAlpha) {
        int bgColor = ColorUtil.rgba(28, 28, 28, (int)(128 * fadeAlpha));
        RenderUtil.Rounded.roundedRect(matrix, x, y, size, size, bgColor, Round.of(2.25f * scaleValue));

        if (stack.isEmpty()) {
            float lineWidth = 0.5625f * scaleValue;
            int lineColor = ColorUtil.rgba(255, 255, 255, (int)(50 * fadeAlpha));
            float padding = size * 0.3f;
            RenderUtil.Rounded.drawLine(matrix, x + padding, y + size / 2f, x + size - padding, y + size / 2f, lineWidth, lineColor);
        } else {
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.color4f(1f, 1f, 1f, fadeAlpha);

            GlStateManager.pushMatrix();
            GlStateManager.translatef(x + (size - 9f * scaleValue) / 2f, y + (size - 9f * scaleValue) / 2f, 1);
            GlStateManager.scalef(0.5625f * scaleValue, 0.5625f * scaleValue, 0.5625f * scaleValue);
            mc.getItemRenderer().renderItemIntoGUI(stack, 0, 0);
            GlStateManager.popMatrix();

            RenderSystem.color4f(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
        }
    }

    private void renderHealthBar(MatrixStack matrix, float x, float y, float width, float height, float scaleValue, float fadeAlpha) {
        float barMargin = 5.0625f * scaleValue;
        float barX = x + barMargin;
        float barWidth = width - barMargin * 2f;
        float barHeight = 1.6875f * scaleValue;
        float barY = y - 3.9375f * scaleValue;

        float healthPercent = (float) healthAnimation.getValue();
        float filledWidth = barWidth * healthPercent;

        int backgroundColor = ColorUtil.rgba(255, 255, 255, (int)(25 * fadeAlpha));
        RenderUtil.Rounded.roundedRect(matrix, barX, barY, barWidth, barHeight, backgroundColor, Round.of(0.5625f * scaleValue));

        int healthColor = Theme.getInstance().clientColor();
        int fadedHealthColor = ColorUtil.rgba(
            (healthColor >> 16) & 0xFF,
            (healthColor >> 8) & 0xFF,
            healthColor & 0xFF,
            (int)(((healthColor >> 24) & 0xFF) * fadeAlpha)
        );
        RenderUtil.Rounded.roundedRect(matrix, barX, barY, filledWidth, barHeight, fadedHealthColor, Round.of(0.5625f * scaleValue));

        float absorptionPercent = (float) absorptionAnimation.getValue();
        if (absorptionPercent > 0) {
            float absorptionWidth = barWidth * absorptionPercent;
            float absorptionBarY = barY - 2.25f * scaleValue;

            int absorptionColor = ColorUtil.rgba(255, 215, 0, (int)(255 * fadeAlpha));
            RenderUtil.Rounded.roundedRect(matrix, barX, absorptionBarY, absorptionWidth, barHeight, absorptionColor, Round.of(0.5625f * scaleValue));
        }
    }

    private float getTargetHealth() {
        if (!inWorld && !(mc.currentScreen instanceof ChatScreen)) {
            return 0;
        }
        return target.getHealthFixed();
    }

    private void drawPlayerHead(MatrixStack matrix, final AbstractClientPlayerEntity player, final float x, final float y, final float size, float openScale, float fadeAlpha) {
        float hurtPC = Mathf.clamp(0f, 1f, (player.hurtTime - (player.hurtTime > 0 ? mc.timer.renderPartialTicks : 0f)) / 10f);
        int headColor = ColorUtil.overCol(ColorUtil.WHITE, ColorUtil.RED, hurtPC);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0F);
        RenderSystem.enableTexture();

        mc.getTextureManager().bindTexture(player.getLocationSkin());

        float[] rgba = ColorUtil.getRGBAf(headColor);
        RenderSystem.color4f(rgba[0], rgba[1], rgba[2], rgba[3] * fadeAlpha);

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrix.getLast().getMatrix());

        float pivotX = x + size / 2.0F;
        float pivotY = y + size / 2.0F;

        RenderSystem.translatef(pivotX, pivotY, 0);
        RenderSystem.rotatef(180, 0, 0, 1);
        RenderSystem.translatef(-pivotX, -pivotY, 0);

        RenderUtil.Texture.roundedHead(player.getLocationSkin(), x, y, size, size, size / 9.9F * openScale, rgba[3] * fadeAlpha, hurtPC);

        RenderSystem.popMatrix();

        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.disableBlend();
    }

    private void drawEntityOnScreen(MatrixStack matrixStack, float x, float y, float scale, LivingEntity entity, float fadeAlpha) {
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
        
        RenderSystem.color4f(1f, 1f, 1f, fadeAlpha);
        
        EntityRendererManager entityrenderermanager = mc.getRenderManager();
        IRenderTypeBuffer.Impl irendertypebuffer$impl = mc.getRenderTypeBuffers().getBufferSource();
        entityrenderermanager.renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, irendertypebuffer$impl, 15728880);
        irendertypebuffer$impl.finish();
        RenderHelper.disableStandardItemLighting();

        entity.renderYawOffset = prevBodyYaw;
        entity.rotationYaw = prevYaw;
        entity.rotationPitch = prevPitch;
        entity.rotationYawHead = prevHeadYaw;

        RenderSystem.color4f(1f, 1f, 1f, 1f);
        
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

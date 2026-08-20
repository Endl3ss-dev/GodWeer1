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
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;
import org.privates.lib.util.time.StopWatch;

import java.util.List;
import java.util.stream.StreamSupport;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TargetHudRock extends AbstractHud implements IRenderer, IAttack, IConfigurableHud {
    final DragSetting drag;
    final Animation openAnimation = new Animation();
    final Animation healthAnimation = new Animation();
    final Animation damageAnimation = new Animation();
    final Animation hurtAnimation = new Animation();
    final StopWatch time = new StopWatch();

    final Animation[] armorAnimations = {new Animation(), new Animation(), new Animation(), new Animation()};
    final Animation[] itemAnimations = {new Animation(), new Animation()};

    public boolean inWorld;
    public LivingEntity target;

    private float displayHealth = 0f;
    private float previousHealthPercent = 1f;
    public float hudScale = 1.0f;

    private float nameScrollX;
    private long lastScrollTime;
    private boolean scrollingForward = true;
    private long scrollPauseUntil;

    public TargetHudRock(DragSetting drag) {
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
        return List.of(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER,
                        () -> this.hudScale,
                        v -> this.hudScale = v,
                        0.5f, 2.0f, 0.1f)
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
        damageAnimation.update();
        hurtAnimation.update();
        for (Animation anim : armorAnimations) anim.update();
        for (Animation anim : itemAnimations) anim.update();

        boolean out = (target == null || (!inWorld && !inChatScreen) || time.finished(1000));

        openAnimation.run(out ? 0.0 : 1.0, 0.2, out ? Easings.BACK_IN : Easings.BACK_OUT, true);

        if (out) {
            for (Animation anim : armorAnimations) anim.run(0.0, 0.5, Easings.BACK_IN, true);
            for (Animation anim : itemAnimations) anim.run(0.0, 0.5, Easings.BACK_IN, true);
            hurtAnimation.run(0.0, 0.3, Easings.EXPO_OUT, true);
        } else {
            for (int i = 0; i < armorAnimations.length; i++) {
                armorAnimations[i].run(1.0, 0.5 + i * 0.02, Easings.BACK_OUT, true);
            }
            for (int i = 0; i < itemAnimations.length; i++) {
                itemAnimations[i].run(1.0, 0.5 + i * 0.02, Easings.BACK_OUT, true);
            }

            if (target != null && target.hurtTime > 0) {
                hurtAnimation.run(1.0, 0.15, Easings.EXPO_OUT, true);
            } else {
                hurtAnimation.run(0.0, 0.3, Easings.EXPO_OUT, true);
            }
        }

        if (openAnimation.getValue() <= 0.0) {
            target = null;
            displayHealth = 0f;
            previousHealthPercent = 1f;
            nameScrollX = 0;
            scrollingForward = true;
            return;
        }

        drawTargetHud(event);
    }

    private void drawTargetHud(Render2DEvent event) {
        if (this.target == null) return;

        MatrixStack matrix = event.getMatrix();
        boolean isPlayer = this.target instanceof AbstractClientPlayerEntity;

        float x = drag.position.x;
        float y = drag.position.y;

        float scaleValue = hudScale;

        float totalWidth = (160f / 1.5f) * scaleValue;
        float totalHeight = (86f / 1.5f) * scaleValue;
        float mainHeight = (47f / 1.5f) * scaleValue;
        float radius = (12f / 1.5f) * scaleValue;

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
        float fadeAlpha = openScale; // Fade animation - только прозрачность, без zoom

        // Убираем zoom анимацию
        // matrix.translate и matrix.scale УДАЛЕНЫ

        RenderUtil.CustomStyled.draw(matrix, x, y, totalWidth, mainHeight, Round.of(radius), true, 2f * fadeAlpha);

        float avatarSize = (33f / 1.5f) * scaleValue;
        float avatarX = x + (7f / 1.5f) * scaleValue;
        float avatarY = y + (7f / 1.5f) * scaleValue;
        float avatarRadius = (5f / 1.5f) * scaleValue;

        if (isPlayer) {
            RenderSystem.pushMatrix();
            RenderSystem.color4f(1f, 1f, 1f, fadeAlpha);
            drawPlayerHead(matrix, (AbstractClientPlayerEntity) target, avatarX + 2, avatarY + 2, avatarSize, openScale, avatarRadius);
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            RenderSystem.popMatrix();
        } else {
            RenderSystem.pushMatrix();
            RenderSystem.color4f(1f, 1f, 1f, fadeAlpha);
            drawEntityHead(matrix, target, avatarX + 2, avatarY + 2, avatarSize, scaleValue);
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            RenderSystem.popMatrix();
        }

        String nameText = target.getName().getString();
        float nameFontSize = (13f / 1.5f) * scaleValue;
        float nameX = x + 2 + (47f / 1.5f) * scaleValue;
        float nameY = y + (11f / 1.5f) * scaleValue;
        int nameColor = ColorUtil.replAlpha(ColorUtil.rgba(255, 255, 255, 255), fadeAlpha);

        float healthTextX = x - 2 + (130f / 1.5f) * scaleValue;
        float maxNameWidth = healthTextX - nameX - (5f / 1.5f) * scaleValue;

        drawScrollingName(matrix, nameText, nameX, nameY, maxNameWidth, nameColor, nameFontSize, scaleValue);

        float health = getTargetHealth();
        float healthPercent = Math.min(1.0f, health / 20f);

        displayHealth = Mathf.fast(displayHealth, health, 10f);
        this.healthAnimation.run(healthPercent, 0.3, Easings.EXPO_OUT, true);

        if (healthPercent < previousHealthPercent) {
            damageAnimation.setValue(previousHealthPercent);
            previousHealthPercent = healthPercent;
        }
        damageAnimation.run(healthPercent, 0.5, Easings.EXPO_OUT, true);

        String healthText = String.format("%.1f", displayHealth);
        float healthFontSize = (11f / 1.5f) * scaleValue;
        float healthTextY = y + (14f / 1.5f) * scaleValue;
        int healthTextColor = ColorUtil.replAlpha(Theme.getInstance().clientColor(), fadeAlpha);
        Fonts.SF_MEDIUM.draw(matrix, healthText, healthTextX, healthTextY, healthTextColor, healthFontSize);

        renderRoundedHealthBar(matrix, x, y, scaleValue, fadeAlpha);

        if (isPlayer) {
            boolean hasArmor = checkHasArmor((AbstractClientPlayerEntity) target);
            boolean hasItems = checkHasItems((AbstractClientPlayerEntity) target);

            if (hasArmor) {
                float armorY = y + mainHeight + (6f / 1.5f) * scaleValue;
                renderArmor(matrix, x, armorY, scaleValue, (AbstractClientPlayerEntity) target, fadeAlpha);
            }

            if (hasItems) {
                float itemsY = hasArmor ? y + mainHeight + (25f / 1.5f) * scaleValue : y + mainHeight + (6f / 1.5f) * scaleValue;
                renderItems(matrix, x, itemsY, scaleValue, (AbstractClientPlayerEntity) target, fadeAlpha);
            }
        }

        matrix.pop();

        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private void drawScrollingName(MatrixStack matrix, String text, float x, float y, float availableWidth, int color, float size, float scale) {
        float textWidth = Fonts.SF_MEDIUM.getWidth(text, size);

        if (textWidth > availableWidth) {
            long currentTime = System.currentTimeMillis();

            if (currentTime > scrollPauseUntil) {
                float speed = 0.5f * scale;
                long updateRate = 20;

                if (currentTime - lastScrollTime > updateRate) {
                    if (scrollingForward) {
                        nameScrollX -= speed;
                        if (nameScrollX <= -(textWidth - availableWidth)) {
                            nameScrollX = -(textWidth - availableWidth);
                            scrollingForward = false;
                            scrollPauseUntil = currentTime + 1500;
                        }
                    } else {
                        nameScrollX += speed;
                        if (nameScrollX >= 0) {
                            nameScrollX = 0;
                            scrollingForward = true;
                            scrollPauseUntil = currentTime + 1500;
                        }
                    }
                    lastScrollTime = currentTime;
                }
            }

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x, y - 2 * scale, availableWidth, size + 4 * scale);
            Fonts.SF_MEDIUM.draw(matrix, text, x + nameScrollX, y, color, size);
            ScissorExp.unset();
            ScissorExp.pop();
        } else {
            nameScrollX = 0;
            Fonts.SF_MEDIUM.draw(matrix, text, x, y, color, size);
        }
    }

    private void drawEntityHead(MatrixStack matrix, LivingEntity entity, float x, float y, float size, float scale) {
        matrix.push();
        matrix.translate(x + size / 2f, y + size * 0.85f, 50.0D);
        float entityScale = size / 2.2f;
        matrix.scale(entityScale, entityScale, -entityScale);
        matrix.rotate(Vector3f.ZP.rotationDegrees(180.0F));
        matrix.rotate(Vector3f.YP.rotationDegrees(180.0F));

        float prevBodyYaw = entity.renderYawOffset;
        float prevYaw = entity.rotationYaw;
        float prevPitch = entity.rotationPitch;
        float prevHeadYaw = entity.rotationYawHead;

        entity.renderYawOffset = 0.0F;
        entity.rotationYaw = 0.0F;
        entity.rotationPitch = 0.0F;
        entity.rotationYawHead = 0.0F;

        RenderHelper.setupGui3DDiffuseLighting();
        EntityRendererManager entityrenderermanager = mc.getRenderManager();
        IRenderTypeBuffer.Impl irendertypebuffer$impl = mc.getRenderTypeBuffers().getBufferSource();
        entityrenderermanager.renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrix, irendertypebuffer$impl, 15728880);
        irendertypebuffer$impl.finish();
        RenderHelper.disableStandardItemLighting();

        entity.renderYawOffset = prevBodyYaw;
        entity.rotationYaw = prevYaw;
        entity.rotationPitch = prevPitch;
        entity.rotationYawHead = prevHeadYaw;

        matrix.pop();
    }

    private void renderRoundedHealthBar(MatrixStack matrix, float x, float y, float scaleValue, float fadeAlpha) {
        float barBgX = x + (51f / 1.5f) * scaleValue;
        float barBgY = y + (30f / 1.5f) * scaleValue;
        float barBgWidth = (102f / 1.5f) * scaleValue;
        float barBgHeight = (6f / 1.5f) * scaleValue;
        float barRadius = 2.3f;

        int blackBgColor = ColorUtil.replAlpha(ColorUtil.rgba(0, 0, 0, 100), fadeAlpha);
        RenderUtil.Rounded.roundedRect(matrix, barBgX, barBgY, barBgWidth, barBgHeight, blackBgColor, Round.of(barRadius));

        float damageBarWidth = barBgWidth * (float) damageAnimation.getValue();
        if (damageBarWidth > 0) {
            int damageColor = ColorUtil.replAlpha(ColorUtil.rgba(255, 0, 0, 150), fadeAlpha);
            RenderUtil.Rounded.roundedRect(matrix, barBgX, barBgY, damageBarWidth, barBgHeight, damageColor, Round.of(barRadius));
        }

        float barFillWidth = barBgWidth * (float) healthAnimation.getValue();
        if (barFillWidth > 0) {
            int barFillColor = ColorUtil.replAlpha(Theme.getInstance().clientColor(), fadeAlpha);
            RenderUtil.Rounded.roundedRect(matrix, barBgX, barBgY, barFillWidth, barBgHeight, barFillColor, Round.of(barRadius));
        }

        // Render absorption (golden hearts) bar on top if not Funtime
        if (target instanceof net.minecraft.entity.player.PlayerEntity player && !org.privates.client.utils.player.PlayerUtil.isFuntime()) {
            float absorption = player.getAbsorptionAmount();
            if (absorption > 0) {
                float maxHealth = player.getMaxHealth();
                float absorptionPercent = Math.min(1.0f, absorption / maxHealth);
                float absorptionWidth = barBgWidth * absorptionPercent;

                // Golden/yellow color for absorption
                int absorptionColor = ColorUtil.replAlpha(ColorUtil.getColor(255, 215, 0), fadeAlpha * 0.7f);
                RenderUtil.Rounded.roundedRect(matrix, barBgX, barBgY, absorptionWidth, barBgHeight, absorptionColor, Round.of(barRadius));
            }
        }
    }

    private boolean checkHasArmor(AbstractClientPlayerEntity player) {
        for (int i = 0; i < 4; i++) {
            if (!player.inventory.armorInventory.get(i).isEmpty()) return true;
        }
        return false;
    }

    private boolean checkHasItems(AbstractClientPlayerEntity player) {
        return !player.getHeldItemMainhand().isEmpty() || !player.getHeldItemOffhand().isEmpty();
    }

    private void renderArmor(MatrixStack matrix, float x, float y, float scaleValue, AbstractClientPlayerEntity player, float fadeAlpha) {
        float itemBoxSize = (24f / 1.5f) * scaleValue;
        float itemBoxHeight = (14f / 1.5f) * scaleValue;
        float itemBoxRadius = (3f / 1.5f) * scaleValue;
        float itemSize = (16f / 1.5f) * scaleValue;
        float startX = x + (26f / 1.5f) * scaleValue;

        ItemStack[] armor = {
                player.getItemStackFromSlot(EquipmentSlotType.HEAD),
                player.getItemStackFromSlot(EquipmentSlotType.CHEST),
                player.getItemStackFromSlot(EquipmentSlotType.LEGS),
                player.getItemStackFromSlot(EquipmentSlotType.FEET)
        };

        for (int i = 0; i < armor.length; i++) {
            float animValue = (float) armorAnimations[i].getValue();
            float combinedAlpha = animValue * fadeAlpha;

            float itemOffsetX = (1f - animValue) * 10f * scaleValue;
            float itemOffsetY = (1f - animValue) * 10f * scaleValue;

            float itemX = startX + i * ((28f / 1.5f) * scaleValue) + itemOffsetX;
            float itemY = y + itemOffsetY;

            RenderUtil.CustomStyled.draw(matrix, itemX, itemY, itemBoxSize, itemBoxHeight, Round.of(itemBoxRadius), true, 2f * fadeAlpha);

            if (!armor[i].isEmpty()) {
                float itemRenderX = itemX + (itemBoxSize - itemSize) / 2f;
                float itemRenderY = itemY - (itemSize - itemBoxHeight) / 2f;
                renderItem(matrix, armor[i], itemRenderX, itemRenderY, itemSize, combinedAlpha);

                float durability = getArmorDurability(armor[i]);
                String durText = (int) durability + "%";
                float durFontSize = (8f / 1.5f) * scaleValue;
                float durX = itemX + (itemBoxSize - Fonts.SF_BOLD.getWidth(durText, durFontSize)) / 2f;
                float durY = itemY + (itemBoxHeight - durFontSize) / 2f;
                int durColor = ColorUtil.replAlpha(ColorUtil.rgba(255, 255, 255, 255), combinedAlpha);

                matrix.push();
                matrix.translate(0, 0, 200);
                Fonts.SF_BOLD.draw(matrix, durText, durX, durY, durColor, durFontSize);
                matrix.pop();
            } else {
                drawEmptySlotX(matrix, itemX, itemY, itemBoxSize, itemBoxHeight, scaleValue, combinedAlpha);
            }
        }
    }

    private void renderItems(MatrixStack matrix, float x, float y, float scaleValue, AbstractClientPlayerEntity player, float fadeAlpha) {
        float itemBoxSize = (14f / 1.5f) * scaleValue;
        float itemBoxRadius = (3f / 1.5f) * scaleValue;
        float itemSize = (12f / 1.5f) * scaleValue;
        float startX = x + (64f / 1.5f) * scaleValue;

        ItemStack[] items = {
                player.getHeldItemMainhand(),
                player.getHeldItemOffhand()
        };

        for (int i = 0; i < items.length; i++) {
            float animValue = (float) itemAnimations[i].getValue();
            float combinedAlpha = animValue * fadeAlpha;

            float itemOffsetX = (1f - animValue) * 10f * scaleValue;
            float itemOffsetY = (1f - animValue) * 10f * scaleValue;

            float itemX = startX + i * ((18f / 1.5f) * scaleValue) + itemOffsetX;
            float itemY = y + itemOffsetY;

            RenderUtil.CustomStyled.draw(matrix, itemX, itemY, itemBoxSize, itemBoxSize, Round.of(itemBoxRadius), true, 2f * fadeAlpha);

            if (!items[i].isEmpty()) {
                float itemRenderX = itemX + (itemBoxSize - itemSize) / 2f;
                float itemRenderY = itemY + (itemBoxSize - itemSize) / 2f;
                renderItem(matrix, items[i], itemRenderX, itemRenderY, itemSize, combinedAlpha);
            } else {
                drawEmptySlotX(matrix, itemX, itemY, itemBoxSize, itemBoxSize, scaleValue, combinedAlpha);
            }
        }
    }

    private void drawEmptySlotX(MatrixStack matrix, float x, float y, float width, float height, float scaleValue, float alpha) {
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;
        float lineLength = Math.min(width, height) * 0.4f;
        float lineWidth = (1f / 1.5f) * scaleValue;

        int redColor = ColorUtil.replAlpha(ColorUtil.rgba(255, 85, 85, 255), alpha);

        float x1 = centerX - lineLength / 2f;
        float y1 = centerY - lineLength / 2f;
        float x2 = centerX + lineLength / 2f;
        float y2 = centerY + lineLength / 2f;

        RenderUtil.Rounded.drawLine(matrix, x1, y1, x2, y2, lineWidth, redColor);
        RenderUtil.Rounded.drawLine(matrix, x2, y1, x1, y2, lineWidth, redColor);
    }

    private void renderItem(MatrixStack matrix, ItemStack itemStack, float x, float y, float size, float alpha) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1f, 1f, 1f, alpha);

        GlStateManager.pushMatrix();
        GlStateManager.translatef(x, y, 100);
        GlStateManager.scalef(size / 16f, size / 16f, 0f);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(itemStack, 0, 0);
        GlStateManager.popMatrix();

        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private float getArmorDurability(ItemStack stack) {
        if (stack.isEmpty() || !stack.isDamageable()) return 100f;
        return ((float) (stack.getMaxDamage() - stack.getDamage()) / stack.getMaxDamage()) * 100f;
    }

    private float getTargetHealth() {
        if (!inWorld && !(mc.currentScreen instanceof ChatScreen)) {
            return 0;
        }
        return target.getHealthFixed();
    }

    private void drawPlayerHead(MatrixStack matrix, final AbstractClientPlayerEntity player, final float x, final float y, final float size, float openScale, float radius) {
        float hurtPC = Math.max((float) hurtAnimation.getValue(),
                Mathf.clamp(0f, 1f, (player.hurtTime - (player.hurtTime > 0 ? mc.timer.renderPartialTicks : 0f)) / 10f));
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

        RenderUtil.Texture.roundedHead(player.getLocationSkin(), x, y, size, size, radius * openScale, rgba[3], hurtPC);

        RenderSystem.popMatrix();

        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.disableBlend();
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

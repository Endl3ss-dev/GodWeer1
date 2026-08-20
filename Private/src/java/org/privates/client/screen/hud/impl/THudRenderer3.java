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
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IAttack;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.particle.Particle;
import org.privates.common.impl.fastrandom.FastRandom;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.StreamSupport;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class THudRenderer3 extends AbstractHud implements IRenderer, IAttack {
    final DragSetting drag;
    final Animation openAnimation = new Animation();
    final Animation healthAnimation = new Animation();
    final Animation healthDecreaseAnimation = new Animation();
    final StopWatch time = new StopWatch();

    public boolean inWorld;
    public LivingEntity target;

    final boolean blurEnabled = true;
    final List<Particle> particles = new ArrayList<>();
    final Random random = new FastRandom();

    private float lastHealth = 0f;
    private float displayHealth = 0f;
    private float animatedDisplayHealth = 0f;

    public THudRenderer3(DragSetting drag) {
        this.drag = drag;
    }

    private float getResolutionScale() {
        int width = mc.getMainWindow().getWidth();
        int height = mc.getMainWindow().getHeight();

        if (width >= 3840 && height >= 2160) {
            return 1f;
        } else if (width >= 2560 && height >= 1440) {
            return 1.0f;
        } else if (width >= 1920 || height >= 1080) {
            return 1f;
        } else {
            return 1f;
        }
    }

    @Override
    public void render(Render2DEvent event) {
        AuraNew auraNew = org.privates.client.managers.module.impl.combat.AuraNew.getInstance();
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
        healthDecreaseAnimation.update();

        boolean out = (target == null || (!inWorld && !inChatScreen) || time.finished(1000));

        openAnimation.run(out ? 0.0 : 1.0, 0.3, out ? Easings.CUBIC_IN : Easings.CUBIC_OUT, true);
        if (openAnimation.getValue() <= 0.0) {
            target = null;
            lastHealth = 0f;
            displayHealth = 0f;
            animatedDisplayHealth = 0f;
            return;
        }

        particles.removeIf(Particle::isFinished);
        particles.forEach(particle -> particle.update(Mathf.deltaTime()));

        drawDefault(event);
    }

    private void drawDefault(Render2DEvent event) {
        float scale = getResolutionScale();
        if (this.target == null) {
            return;
        }

        MatrixStack matrix = event.getMatrix();
        boolean isPlayer = this.target instanceof AbstractClientPlayerEntity;

        float x = drag.position.x;
        float y = drag.position.y;

        float avatarSize = (32.92f + 2.992f) * scale;
        float margin = (3.74F + 0.374f) * scale;

        float infoContainerWidth = 130f * scale;
        float infoContainerHeight = avatarSize + margin * 2;

        float totalWidth = infoContainerWidth;
        float totalHeight = infoContainerHeight;

        drag.size.set(totalWidth, totalHeight);

        if (x == 0 && y == 0) {
            float screenWidth = mc.getMainWindow().getScaledWidth();
            float newX = screenWidth / 2f - totalWidth / 2f;
            float newY = 40f * scale;
            drag.position.set(newX, newY);
            x = newX;
            y = newY;
        }

        matrix.push();
        float openScale = (float) (this.openAnimation.getValue() * 0.85f);
        matrix.translate((x + totalWidth / 2F), (y + totalHeight / 2F), 0);
        matrix.scale(openScale, openScale, 1);
        matrix.translate(-(x + totalWidth / 2F), -(y + totalHeight / 2F), 0);

        renderInfoContainer(matrix, x, y, infoContainerWidth, infoContainerHeight, isPlayer, scale);

        matrix.pop();
    }

    @Getter
    @Setter
    private boolean enableOutline = false;
    @Getter
    @Setter
    private float outlineWidth = 1.0f;

    private void renderInfoContainer(MatrixStack matrix, float x, float y, float width, float height, boolean isPlayer, float scale) {
        float radius = 6f * scale;
        RenderUtil.CustomStyled.draw(matrix, x, y, width, height, Round.of(radius), blurEnabled, 10f);

        if (enableOutline) {
            int themeColor = Theme.getInstance().clientColor();
            int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
            float halfWidth = outlineWidth * scale / 2f;
            RenderUtil.Rounded.roundedOutline(matrix,
                    x - halfWidth,
                    y - halfWidth,
                    width + outlineWidth * scale,
                    height + outlineWidth * scale,
                    outlineWidth * scale,
                    outlineColor,
                    Round.of(radius));
        }

        float fontSize = (7f + 0.6f) * scale;
        float margin = (3.74F + 0.374f) * scale;

        float avatarSize = (37f + 2.992f) * scale;
        float headX = x + margin;
        float headY = y + margin;

        if (openAnimation.getValue() >= 1.0) {
            if (isPlayer) {
                drawHead(matrix, (AbstractClientPlayerEntity) target, headX, headY, avatarSize, scale);
            } else {
                drawEntityOnScreen(matrix, headX + avatarSize / 2f, headY + avatarSize * 0.9f, 18f * scale, this.target);
            }
        }

        float nameX = headX + avatarSize + margin;
        float nameY = y + margin;
        float nameFontSize = fontSize * 1.12f;
        font.draw(matrix, target.getName().getString(), nameX, nameY, theme().clientColor(), nameFontSize);

        float health = getTargetHealth();
        float maxHealth = target.getMaxHealth();
        float absorptionHealth = target.getAbsorptionAmount();

        float totalHealth = health + absorptionHealth;
        float healthPercent = maxHealth > 0 ? Math.min(1.0f, health / maxHealth) : 0;

        if (lastHealth == 0f) {
            lastHealth = health;
            displayHealth = health;
            animatedDisplayHealth = health;
        }

        if (health < lastHealth) {
            healthDecreaseAnimation.run(1.0, 0.5, Easings.BACK_IN, true);
        } else if (health > lastHealth) {
            displayHealth = health;
        }

        if (healthDecreaseAnimation.getValue() > 0) {
            float lerpFactor = (float) (1.0 - healthDecreaseAnimation.getValue());
            displayHealth = (float) Mathf.lerp(lastHealth, health, lerpFactor);
        } else {
            displayHealth = health;
        }

        animatedDisplayHealth = Mathf.fast(animatedDisplayHealth, displayHealth, 8f);

        lastHealth = health;
        this.healthAnimation.run(healthPercent, 0.3, Easings.EXPO_OUT, true);

        String healthText = String.format("HP: %.1f", animatedDisplayHealth);
        float healthTextX = nameX;
        float healthTextY = nameY + font.getHeight(target.getName().getString(), nameFontSize) + 5.5f * scale;
        float healthFontSize = fontSize * 1.12f;
        font.draw(matrix, healthText, healthTextX, healthTextY, theme().clientColor(), healthFontSize);

        if (isPlayer && openAnimation.getValue() >= 1.0) {
            float itemX = nameX;
            float itemY = healthTextY + font.getHeight(healthText, healthFontSize) + 5f * scale + 8.5f;
            int itemIndex = 0;

            List<ItemStack> equipmentToRender = new ArrayList<>();
            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) target;
            equipmentToRender.add(player.getItemStackFromSlot(EquipmentSlotType.HEAD));
            equipmentToRender.add(player.getItemStackFromSlot(EquipmentSlotType.CHEST));
            equipmentToRender.add(player.getItemStackFromSlot(EquipmentSlotType.LEGS));
            equipmentToRender.add(player.getItemStackFromSlot(EquipmentSlotType.FEET));
            equipmentToRender.add(player.getHeldItemMainhand());
            equipmentToRender.add(player.getHeldItemOffhand());

            for (ItemStack itemStack : equipmentToRender) {
                if (!itemStack.isEmpty()) {
                    renderItem(matrix, itemStack, itemX + (itemIndex * 10f * scale), itemY, scale);
                    itemIndex++;
                }
            }
        }

        float hpHeight = (6.545F + 0.6545f) * scale;
        float hpBarX = nameX;
        float hpBarY = y + height - margin - hpHeight;
        float hpBarWidth = width - nameX - margin + x;

        RenderUtil.Rounded.roundedRect(matrix, hpBarX, hpBarY, hpBarWidth, hpHeight, ColorUtil.replAlpha(theme().clientColor(), 0.5f), Round.of(hpHeight / 2F * scale));

        float displayHealthPercent = maxHealth > 0 ? Math.min(1.0f, displayHealth / maxHealth) : 0;
        float decreaseWidth = hpBarWidth * displayHealthPercent;

        if (displayHealth < health) {
            int decreaseColor = ColorUtil.replAlpha(ColorUtil.getColor(255, 100, 100), 100);
            RenderUtil.Rounded.roundedRect(matrix, hpBarX, hpBarY, decreaseWidth, hpHeight, decreaseColor, Round.of(hpHeight / 2F * scale));
        }

        float filledWidth = hpBarWidth * (float) healthAnimation.getValue();
        int healthColor;

        if (absorptionHealth > 0) {
            float absorptionPercent = absorptionHealth / totalHealth;
            healthColor = ColorUtil.interpolateColor(theme().textAccentColor(), ColorUtil.getColor(255, 215, 0), absorptionPercent);
        } else {
            healthColor = theme().textAccentColor();
        }

        RenderUtil.Shadow.drawShadow(matrix, hpBarX, hpBarY, filledWidth, hpHeight, 6 * scale, 1f, healthColor, Round.of(hpHeight / 2F * scale));
        RenderUtil.Rounded.roundedRect(matrix, hpBarX, hpBarY, filledWidth, hpHeight, healthColor, Round.of(hpHeight / 2F * scale));

        // Render absorption (golden hearts) bar on top if not Funtime
        if (target instanceof net.minecraft.entity.player.PlayerEntity player && !org.privates.client.utils.player.PlayerUtil.isFuntime()) {
            float absorption = player.getAbsorptionAmount();
            if (absorption > 0) {
                float absorptionPercent = Math.min(1.0f, absorption / maxHealth);
                float absorptionWidth = hpBarWidth * absorptionPercent;
                
                // Golden/yellow color for absorption
                int absorptionColor = ColorUtil.getColor(255, 215, 0);
                RenderUtil.Shadow.drawShadow(matrix, hpBarX, hpBarY, absorptionWidth, hpHeight, 6 * scale, 0.7f, absorptionColor, Round.of(hpHeight / 2F * scale));
                RenderUtil.Rounded.roundedRect(matrix, hpBarX, hpBarY, absorptionWidth, hpHeight, absorptionColor, Round.of(hpHeight / 2F * scale));
            }
        }
    }

    private float getTargetHealth() {
        if (!inWorld && !(mc.currentScreen instanceof ChatScreen)) {
            return 0;
        }
        return target.getHealthFixed();
    }

    private void drawHead(MatrixStack matrix, final AbstractClientPlayerEntity player, final float x, final float y, final float size, float scale) {
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

        RenderUtil.Texture.roundedHead(player.getLocationSkin(), x, y, size, size, size / 7F * scale, rgba[3], hurtPC);

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

    private void renderItem(MatrixStack matrix, ItemStack itemStack, float x, float y, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(x, y, 1);
        GlStateManager.scalef(0.595F * scale, 0.595F * scale, 0.595F * scale);
        mc.getItemRenderer().renderItemIntoGUI(itemStack, 0, 0);
        GlStateManager.popMatrix();
    }

    @Override
    public void attack(AttackEvent event) {
        float scale = getResolutionScale();
        if (this.target != null && event.getTarget() == target) {
            float headContainerSize = (29.92f + 2.992f + (3.74F + 0.374f) * 2) * scale;
            for (int i = 0; i < 30; i++) {
                spawnParticles(drag.position.x + headContainerSize / 2F, drag.position.y + headContainerSize / 2F, scale, Mathf.randomValue(15F, 30F) * scale, 0F, 0F, 0F, 1000);
            }
        }
    }

    private void spawnParticles(float x, float y, float size, float motion, float mass, float gravity, float gravityMultiplier, float delay) {
        float motionX = (random.nextFloat() - 0.5F) * motion;
        float motionY = (random.nextFloat() - 0.5F) * motion;
        particles.add(new Particle(motionX + x, motionY + y, size, motionX, motionY, mass, gravity, gravityMultiplier, delay));
    }
}

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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Namespaced;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.Excellent;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.misc.ScoreboardHealth;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IAttack;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.lib.util.time.StopWatch;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.StreamSupport;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TargetHudRenderer extends AbstractHud implements IRenderer, IAttack, IConfigurableHud {
    final DragSetting drag;
    final Animation openAnimation = new Animation();
    final Animation healthAnimation = new Animation();
    final StopWatch time = new StopWatch();

    final Animation headAnimation = new Animation();
    final Animation leftItemsAnimation = new Animation();
    final Animation rightItemsAnimation = new Animation();
    final Animation barAnimation = new Animation();
    final Animation textAnimation = new Animation();
    final Animation killTextAnimation = new Animation();

    public boolean inWorld;
    public LivingEntity target;

    final boolean blurEnabled = true;

    private float displayHealth = 0f;
    private float previousHealth = 0f;
    private final LinkedList<ItemStack> rememberedItems = new LinkedList<>();
    private final List<HealthParticle> healthParticles = new ArrayList<>();

    public float hudScale = 0.9f;
    public boolean showName = true;
    public boolean showArmor = true;
    public boolean showItems = true;
    public boolean enableParticles = true;
    public boolean enableElementAnimation = true;

    private boolean isKilling = false;
    private long killStartTime = 0;
    private int killTextProgress = 0;
    private final String killText = "EZZZZZZZZ";

    public float typeSpeed = 50f;
    public float shakeIntensity = 2.0f;

    private final Animation headShakeAnimation = new Animation();
    private final Animation headRedAnimation = new Animation();

    // Статический список всех TargetHud для синхронизации scale
    private static final List<Object> allTargetHuds = new ArrayList<>();

    public static void registerTargetHud(Object targetHud) {
        if (!allTargetHuds.contains(targetHud)) {
            allTargetHuds.add(targetHud);
        }
    }

    public static void syncScaleToAll(float newScale) {
        for (Object hud : allTargetHuds) {
            try {
                java.lang.reflect.Field scaleField = hud.getClass().getDeclaredField("hudScale");
                scaleField.setAccessible(true);
                scaleField.setFloat(hud, newScale);
            } catch (Exception ignored) {
                // Если у HUD нет поля hudScale, пропускаем
            }
        }
    }

    public TargetHudRenderer(DragSetting drag) {
        this.drag = drag;
        registerTargetHud(this);
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
                        v -> {
                            this.hudScale = v;
                            syncScaleToAll(v); // Синхронизация со всеми TargetHud
                        },
                        0.5f, 2.0f, 0.1f),

                new DragComponent.HudSetting("Name", DragComponent.SettingType.BOOLEAN,
                        () -> this.showName ? 1f : 0f,
                        v -> this.showName = v > 0.5f),

                new DragComponent.HudSetting("Armor", DragComponent.SettingType.BOOLEAN,
                        () -> this.showArmor ? 1f : 0f,
                        v -> this.showArmor = v > 0.5f),

                new DragComponent.HudSetting("Items", DragComponent.SettingType.BOOLEAN,
                        () -> this.showItems ? 1f : 0f,
                        v -> this.showItems = v > 0.5f),

                new DragComponent.HudSetting("HP Particles", DragComponent.SettingType.BOOLEAN,
                        () -> this.enableParticles ? 1f : 0f,
                        v -> this.enableParticles = v > 0.5f),

                new DragComponent.HudSetting("Element Animation", DragComponent.SettingType.BOOLEAN,
                        () -> this.enableElementAnimation ? 1f : 0f,
                        v -> this.enableElementAnimation = v > 0.5f),

                new DragComponent.HudSetting("Enable Outline", DragComponent.SettingType.BOOLEAN,
                        () -> this.enableOutline ? 1f : 0f,
                        v -> this.enableOutline = v > 0.5f),

                new DragComponent.HudSetting("Outline Width", DragComponent.SettingType.SLIDER,
                        () -> this.outlineWidth,
                        v -> this.outlineWidth = v,
                        0.5f, 3.0f, 0.1f),

                new DragComponent.HudSetting("Type Speed", DragComponent.SettingType.SLIDER,
                        () -> this.typeSpeed,
                        v -> this.typeSpeed = v,
                        20f, 100f, 5f),

                new DragComponent.HudSetting("Shake Intensity", DragComponent.SettingType.SLIDER,
                        () -> this.shakeIntensity,
                        v -> this.shakeIntensity = v,
                        0.5f, 5.0f, 0.5f)
        );
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
        killTextAnimation.update();
        headShakeAnimation.update();
        headRedAnimation.update();

        if (enableElementAnimation) {
            headAnimation.update();
            leftItemsAnimation.update();
            rightItemsAnimation.update();
            barAnimation.update();
            textAnimation.update();
        }

        boolean out = (target == null || (!inWorld && !inChatScreen) || time.finished(1000));

        if (out && !isKilling) {
            isKilling = true;
            killStartTime = System.currentTimeMillis();
            killTextProgress = 0;
        }

        if (isKilling) {
            long elapsed = System.currentTimeMillis() - killStartTime;
            int charDelay = (int) (1000f / typeSpeed);
            killTextProgress = Math.min((int) (elapsed / charDelay), killText.length());

            if (killTextProgress >= killText.length()) {
                if (elapsed > (long) killText.length() * charDelay + 500) {
                    openAnimation.run(0.0, 0.3, Easings.CUBIC_IN, true);
                }
            }

            killTextAnimation.run(killTextProgress >= killText.length() ? 0.0 : 1.0, 0.2, Easings.LINEAR, true);

            if (enableElementAnimation) {
                float fadeProgress = Math.min(1.0f, elapsed / 200f);
                headAnimation.run(1.0 - fadeProgress, 0.2, Easings.LINEAR, true);
                leftItemsAnimation.run(1.0 - fadeProgress, 0.2, Easings.LINEAR, true);
                rightItemsAnimation.run(1.0 - fadeProgress, 0.2, Easings.LINEAR, true);
                barAnimation.run(1.0 - fadeProgress, 0.2, Easings.LINEAR, true);
                textAnimation.run(1.0 - fadeProgress, 0.2, Easings.LINEAR, true);
            }
        } else {
            openAnimation.run(out ? 0.0 : 1.0, 0.3, out ? Easings.CUBIC_IN : Easings.CUBIC_OUT, true);

            if (enableElementAnimation) {
                if (out) {
                    textAnimation.run(0.0, 0.15, Easings.CUBIC_IN, true);
                    barAnimation.run(0.0, 0.2, Easings.CUBIC_IN, true);
                    rightItemsAnimation.run(0.0, 0.25, Easings.CUBIC_IN, true);
                    leftItemsAnimation.run(0.0, 0.3, Easings.CUBIC_IN, true);
                    headAnimation.run(0.0, 0.35, Easings.CUBIC_IN, true);
                } else {
                    barAnimation.run(1.0, 0.15, Easings.CUBIC_OUT, true);
                    headAnimation.run(1.0, 0.2, Easings.CUBIC_OUT, true);
                    leftItemsAnimation.run(1.0, 0.25, Easings.CUBIC_OUT, true);
                    rightItemsAnimation.run(1.0, 0.3, Easings.CUBIC_OUT, true);
                    textAnimation.run(1.0, 0.35, Easings.CUBIC_OUT, true);
                }
            }
        }

        if (openAnimation.getValue() <= 0.0) {
            target = null;
            displayHealth = 0f;
            previousHealth = 0f;
            rememberedItems.clear();
            healthParticles.clear();
            isKilling = false;
            killTextProgress = 0;
            return;
        }

        if (target != null) {
            float health = getTargetHealth();
            boolean lowHealth = health < 4f;
            headShakeAnimation.run(lowHealth ? 1.0 : 0.0, 0.2, Easings.CUBIC_OUT, true);
            headRedAnimation.run(lowHealth ? 1.0 : 0.0, 0.3, Easings.CUBIC_OUT, true);
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

        float totalWidth = 110.5425f * scaleValue;
        float totalHeight = 52f * scaleValue;
        float infoHeight = 38.10465f * scaleValue;
        float bottomHeight = 8f * scaleValue;
        float radius = 6.14125f * scaleValue;

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

        float shakeX = 0f;
        float shakeY = 0f;
        if (isKilling && killTextProgress < killText.length()) {
            float shakeAmount = shakeIntensity * scaleValue * (float) killTextAnimation.getValue();
            shakeX = Mathf.randomValue(-shakeAmount, shakeAmount);
            shakeY = Mathf.randomValue(-shakeAmount, shakeAmount);
        }

        float openScale = (float) this.openAnimation.getValue();
        float fadeAlpha = openScale; // Fade animation - только прозрачность

        // Применяем только shake, без zoom
        if (shakeX != 0f || shakeY != 0f) {
            matrix.translate(shakeX, shakeY, 0);
        }

        float infoY = y;
        float bottomY = y + infoHeight + 1.5f * scaleValue;

        RenderUtil.CustomStyled.draw(matrix, x, y, totalWidth, totalHeight, Round.of(radius), blurEnabled, 50f * fadeAlpha);

        if (enableOutline) {
            int themeColor = Theme.getInstance().clientColor();
            int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
            outlineColor = ColorUtil.replAlpha(outlineColor, fadeAlpha);
            float halfWidth = outlineWidth * scaleValue / 2f;
            RenderUtil.Rounded.roundedOutline(matrix,
                    x - halfWidth,
                    y - halfWidth,
                    totalWidth + outlineWidth * scaleValue,
                    totalHeight + outlineWidth * scaleValue,
                    outlineWidth * scaleValue,
                    outlineColor,
                    Round.of(radius));
        }

        if (isKilling && killTextProgress > 0) {
            String displayText = killText.substring(0, killTextProgress);
            float fontSize = 12f * scaleValue;
            float textWidth = font.getWidth(displayText, fontSize);
            float textX = x + (totalWidth - textWidth) / 2f;
            float textY = y + (totalHeight - fontSize) / 2f;

            font.draw(matrix, displayText, textX, textY, ColorUtil.replAlpha(Theme.getInstance().clientColor(), fadeAlpha), fontSize);
        }

        float headSize = 25.53f * scaleValue;
        float headX = x + (totalWidth - headSize) / 2f;
        float headY = infoY + 1.5f + (infoHeight - headSize) / 2f;

        float headAlpha = (enableElementAnimation ? (float) headAnimation.getValue() : 1.0f) * fadeAlpha;
        float leftItemsAlpha = (enableElementAnimation ? (float) leftItemsAnimation.getValue() : 1.0f) * fadeAlpha;
        float rightItemsAlpha = (enableElementAnimation ? (float) rightItemsAnimation.getValue() : 1.0f) * fadeAlpha;
        float barAlpha = (enableElementAnimation ? (float) barAnimation.getValue() : 1.0f) * fadeAlpha;
        float textAlpha = (enableElementAnimation ? (float) textAnimation.getValue() : 1.0f) * fadeAlpha;

        if (openAnimation.getValue() >= 1.0) {
            if (isPlayer && headAlpha > 0) {
                RenderSystem.pushMatrix();
                RenderSystem.color4f(1f, 1f, 1f, headAlpha);

                float shakeOffsetX = 0f;
                float shakeOffsetY = 0f;
                if (headShakeAnimation.getValue() > 0) {
                    float shakeValue = (float) headShakeAnimation.getValue() * 2f * scaleValue;
                    shakeOffsetX = Mathf.randomValue(-shakeValue, shakeValue);
                    shakeOffsetY = Mathf.randomValue(-shakeValue, shakeValue);
                }

                drawPlayerHead(matrix, (AbstractClientPlayerEntity) target, headX + shakeOffsetX, headY + shakeOffsetY, headSize + 2.55f * scaleValue, openScale);
                RenderSystem.color4f(1f, 1f, 1f, 1f);
                RenderSystem.popMatrix();
            } else if (!isPlayer && headAlpha > 0) {
                RenderSystem.pushMatrix();
                RenderSystem.color4f(1f, 1f, 1f, headAlpha);
                drawEntityOnScreen(matrix, headX + headSize / 2f, headY + headSize * 0.9f, 9.3925f * scaleValue, this.target);
                RenderSystem.color4f(1f, 1f, 1f, 1f);
                RenderSystem.popMatrix();
            }

            if (isPlayer) {
                AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) target;
                if (showArmor && leftItemsAlpha > 0) {
                    renderArmor(matrix, x, infoY, player, scaleValue, leftItemsAlpha);
                }
                if (showItems && rightItemsAlpha > 0) {
                    renderMainHandItems(matrix, x, infoY, totalWidth, player, scaleValue, rightItemsAlpha);
                    updateRememberedItems(player);
                    renderRememberedItems(matrix, x, infoY - 10.8375f * scaleValue, totalWidth, scaleValue, rightItemsAlpha);
                }
            }
        }

        if (barAlpha > 0) {
            renderHealthBar(matrix, x, bottomY - 1, totalWidth, bottomHeight, scaleValue, barAlpha, textAlpha);
        }

        float health = getTargetHealth();
        float maxHealth = target.getMaxHealth();
        float healthPercent = maxHealth > 0 ? Math.min(1.0f, health / maxHealth) : 0;

        if (Math.abs(health - previousHealth) > 0.1f && enableParticles) {
            spawnHealthParticles(x, bottomY, totalWidth, bottomHeight, scaleValue);
            previousHealth = health;
        }

        displayHealth = Mathf.fast(displayHealth, health, 10f);

        this.healthAnimation.run(healthPercent, 0.3, Easings.EXPO_OUT, true);

        matrix.pop();

        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private static class HealthParticle {
        float x, y;
        float startX;
        float targetX;
        float offsetY;
        float size;
        float alpha = 1.0f;
        float rotation = 0f;
        long spawnTime;
        static final long LIFETIME = 2500;

        HealthParticle(float startX, float y, float scaleValue) {
            this.startX = startX;
            this.x = startX;
            this.targetX = startX;
            this.y = y;
            this.offsetY = Mathf.randomValue(-2f, 2f);
            this.size = Mathf.randomValue(5f, 13f);
            this.spawnTime = System.currentTimeMillis();
            this.rotation = Mathf.randomValue(0, 50);
        }

        void update(float barStartX, float currentFilledWidth, float barCenterY, float barHeight) {
            targetX = barStartX + currentFilledWidth;

            x = Mathf.fast(x, targetX, 12f);
            y = barCenterY + barHeight / 2f + offsetY;

            rotation += 2f;

            long elapsed = System.currentTimeMillis() - spawnTime;
            alpha = Math.max(0, 1.0f - (float) elapsed / LIFETIME);
        }

        boolean isDead() {
            return System.currentTimeMillis() - spawnTime > LIFETIME || Math.abs(x - targetX) < 0.5f;
        }

        float getAlpha() {
            return alpha;
        }
    }

    private void spawnHealthParticles(float x, float y, float width, float height, float scaleValue) {
        float armorStartX = x + 6.14125f * scaleValue;
        float itemsEndX = x + width - 6.14125f * scaleValue;
        float barX = armorStartX;
        float barWidth = itemsEndX - armorStartX;
        float barY = y + height / 2f;

        float oldHealthPercent = previousHealth / target.getMaxHealth();
        float oldHealthX = barX + (barWidth * oldHealthPercent);

        for (int i = 0; i < 10; i++) {
            healthParticles.add(new HealthParticle(oldHealthX, barY, scaleValue));
        }
    }

    private void renderArmor(MatrixStack matrix, float x, float y, AbstractClientPlayerEntity player, float scaleValue, float alpha) {
        float itemSize = 10.861f * scaleValue;
        float startX = x + 6.14125f * scaleValue;
        float startY = y + 7.3117f * scaleValue;
        float gapX = 17.83f * scaleValue;
        float gapY = 15.16f * scaleValue;

        ItemStack[] armor = {
                player.getItemStackFromSlot(EquipmentSlotType.HEAD),
                player.getItemStackFromSlot(EquipmentSlotType.CHEST),
                player.getItemStackFromSlot(EquipmentSlotType.LEGS),
                player.getItemStackFromSlot(EquipmentSlotType.FEET)
        };

        for (int i = 0; i < armor.length; i++) {
            int row = i / 2;
            int col = i % 2;
            float itemX = startX + (col * gapX);
            float itemY = startY + (row * gapY);
            renderItemSlot(matrix, itemX, itemY, itemSize, armor[i], scaleValue, false, alpha);
        }
    }

    private void renderMainHandItems(MatrixStack matrix, float x, float y, float width, AbstractClientPlayerEntity player, float scaleValue, float alpha) {
        float itemSize = 10.861f * scaleValue;
        float startX = x + width - 16.59f * scaleValue;
        float startY = y + 7.3117f * scaleValue;
        float gapX = 17.83f * scaleValue;

        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        ItemStack[] hands = {mainHand, offHand};

        for (int i = 0; i < hands.length; i++) {
            if (!hands[i].isEmpty()) {
                float itemX = startX - (i * gapX);
                renderItemSlot(matrix, itemX, startY, itemSize, hands[i], scaleValue, true, alpha);
            }
        }
    }

    private void updateRememberedItems(AbstractClientPlayerEntity player) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty()) {
                if (stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE || stack.getItem() == Items.GOLDEN_APPLE) {
                    if (rememberedItems.stream().noneMatch(s -> ItemStack.areItemsEqual(s, stack))) {
                        rememberedItems.add(stack.copy());
                        if (rememberedItems.size() > 2) {
                            rememberedItems.removeFirst();
                        }
                    }
                }
            }
        }
    }

    private void renderRememberedItems(MatrixStack matrix, float x, float y, float width, float scaleValue, float alpha) {
        float itemSize = 9.861f * scaleValue;
        float startX = x + width - 16.59f * scaleValue;
        float startY = y + 5.5f * scaleValue + 30.318f * scaleValue;
        float gapX = 17.83f * scaleValue;

        int index = 0;
        for (ItemStack stack : rememberedItems) {
            if (index >= 2) break;
            if (!stack.isEmpty()) {
                float itemX = startX - (index * gapX);
                renderItemSlot(matrix, itemX, startY, itemSize, stack, scaleValue, true, alpha);
            }
            index++;
        }
    }

    private void renderItemSlot(MatrixStack matrix, float x, float y, float size, ItemStack stack, float scaleValue, boolean showCount, float alpha) {
        int bgAlpha = (int) (255 * alpha);
        RenderUtil.CustomStyled.draw(matrix, x, y, size, size, Round.of(2.972f * scaleValue), blurEnabled, 50f);
        RenderUtil.Rounded.roundedRect(matrix, x, y, size, size,
                ColorUtil.replAlpha(new Color(0x1C1C1C).getRGB(), 0.5f * alpha), Round.of(2.972f * scaleValue));

        if (stack.isEmpty()) {
            float lineWidth = 1.5f * scaleValue;
            int redColor = ColorUtil.replAlpha(ColorUtil.getColor(255, 0, 0), alpha);
            float padding = size * 0.2f;
            RenderUtil.Rounded.drawLine(matrix, x + padding, y + padding, x + size - padding, y + size - padding, lineWidth, redColor);
            RenderUtil.Rounded.drawLine(matrix, x + size - padding, y + padding, x + padding, y + size - padding, lineWidth, redColor);
        } else {
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.color4f(1f, 1f, 1f, alpha);

            GlStateManager.pushMatrix();
            GlStateManager.translatef(x + (size - 13.988f * scaleValue) / 2f, y + (size - 13.988f * scaleValue) / 2f, 1);
            GlStateManager.scalef(0.8745f * scaleValue, 0.8745f * scaleValue, 0.8745f * scaleValue);
            mc.getItemRenderer().renderItemIntoGUI(stack, 0, 0);
            GlStateManager.popMatrix();

            if (showCount && stack.getCount() > 1) {
                String countText = String.valueOf(stack.getCount());
                float fontSize = 6.0f * scaleValue;
                float textX = x + size - font.getWidth(countText, fontSize) - 2f * scaleValue;
                float textY = y + size - fontSize - 1.5f * scaleValue;

                RenderSystem.pushMatrix();
                RenderSystem.translated(0, 0, 300);
                font.draw(matrix, countText, textX, textY, ColorUtil.replAlpha(-1, alpha), fontSize);
                RenderSystem.popMatrix();
            }

            RenderSystem.color4f(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
        }
    }

    private void renderHealthBar(MatrixStack matrix, float x, float y, float width, float height, float scaleValue, float barAlpha, float textAlpha) {
        float barHeight = height;
        float barWidth = width - 12f * scaleValue;

        float armorStartX = x + 6.14125f * scaleValue;
        float itemsEndX = x + width - 6.14125f * scaleValue;

        float barX = armorStartX;
        barWidth = itemsEndX - armorStartX;

        float barY = y + (height - barHeight) / 2f;

        float healthPercent = (float) healthAnimation.getValue();
        float filledWidth = barWidth * healthPercent;

        RenderUtil.Rounded.roundedRect(matrix, barX, barY, barWidth, barHeight,
                ColorUtil.replAlpha(ColorUtil.multDark(theme().clientColor(), 0.4f), 0.25f * barAlpha), Round.of(barHeight / 2f));

        int healthColor = ColorUtil.replAlpha(theme().clientColor(), barAlpha);
        RenderUtil.Shadow.drawShadow(matrix, barX, barY, filledWidth, barHeight, 5.1f * scaleValue, barHeight / 2f, healthColor, Round.of(barHeight / 2f));
        RenderUtil.Rounded.roundedRect(matrix, barX, barY, filledWidth, barHeight, healthColor, Round.of(barHeight / 2f));

        // Render absorption (golden hearts) bar on top if not Funtime
        if (target instanceof PlayerEntity player && !org.privates.client.utils.player.PlayerUtil.isFuntime()) {
            float absorption = player.getAbsorptionAmount();
            if (absorption > 0) {
                float absorptionPercent = Math.min(1.0f, absorption / player.getMaxHealth());
                float absorptionWidth = barWidth * absorptionPercent;

                // Golden/yellow color for absorption
                int absorptionColor = ColorUtil.replAlpha(ColorUtil.getColor(255, 215, 0), barAlpha * 0.7f);
                RenderUtil.Shadow.drawShadow(matrix, barX, barY, absorptionWidth, barHeight, 3f * scaleValue, barHeight / 2f, absorptionColor, Round.of(barHeight / 2f));
                RenderUtil.Rounded.roundedRect(matrix, barX, barY, absorptionWidth, barHeight, absorptionColor, Round.of(barHeight / 2f));
            }
        }

        if (enableParticles) {
            healthParticles.removeIf(particle -> {
                particle.update(barX, filledWidth, barY, barHeight);
                return particle.isDead();
            });

            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO
            );

            for (HealthParticle particle : healthParticles) {
                float particleAlpha = particle.getAlpha() * barAlpha;
                int particleColor = ColorUtil.replAlpha(ColorUtil.getColor(255, 255, 255), particleAlpha);

                matrix.push();
                matrix.translate(particle.x, particle.y, 0);
                matrix.rotate(Vector3f.ZP.rotationDegrees(particle.rotation));

                float size = particle.size * scaleValue;

                RenderUtil.bindTexture(new Namespaced("particle/bloom.png"));
                RectUtil.drawRect(matrix, -size * 1.5f, -size * 1.5f, size * 3f, size * 3f,
                        ColorUtil.replAlpha(theme().clientColor(), particleAlpha * 0.3f), true, true);

                RenderUtil.bindTexture(new Namespaced("particle/snow.png"));
                RectUtil.drawRect(matrix, -size / 2f, -size / 2f, size, size, particleColor, true, true);

                matrix.pop();
            }

            RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO
            );
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
        }

        String healthText = String.format("%.0f HP", displayHealth);
        float fontSize = 6.0f * scaleValue;
        float textX = barX + (barWidth - font.getWidth(healthText, fontSize)) / 2f;
        float textY = barY + (barHeight - fontSize) / 2f;
        font.draw(matrix, healthText, textX, textY, ColorUtil.replAlpha(-1, textAlpha), fontSize);
    }

    private float getTargetHealth() {
        if (!inWorld && !(mc.currentScreen instanceof ChatScreen)) {
            return 0;
        }

        if (Excellent.inst().moduleManager().get(ScoreboardHealth.class).isEnabled()) {
            return target.getRealHealth();
        }

        return target.getHealthFixed();
    }

    private void drawPlayerHead(MatrixStack matrix, final AbstractClientPlayerEntity player, final float x, final float y, final float size, float openScale) {
        float hurtPC = Mathf.clamp(0f, 1f, (player.hurtTime - (player.hurtTime > 0 ? mc.timer.renderPartialTicks : 0f)) / 10f);
        float lowHealthRed = (float) headRedAnimation.getValue();
        int headColor = ColorUtil.overCol(ColorUtil.WHITE, ColorUtil.RED, Math.max(hurtPC, lowHealthRed));

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

        RenderUtil.Texture.roundedHead(player.getLocationSkin(), x, y, size, size, size / 7F * openScale, rgba[3], hurtPC);

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
            inWorld = StreamSupport.stream(mc.world.getAllEntities().spliterator(), true).anyMatch(entity -> entity.equals(target));
        }

        if (!inWorld) {
            return;
        }

        // Update animations
        openAnimation.update();
        healthAnimation.update();
        killTextAnimation.update();
        headShakeAnimation.update();
        headRedAnimation.update();

        if (enableElementAnimation) {
            headAnimation.update();
            leftItemsAnimation.update();
            rightItemsAnimation.update();
            barAnimation.update();
            textAnimation.update();
        }

        boolean out = (target == null || !inWorld || time.finished(1000));

        if (out && !isKilling) {
            isKilling = true;
            killStartTime = System.currentTimeMillis();
            killTextProgress = 0;
        }

        if (isKilling) {
            long elapsed = System.currentTimeMillis() - killStartTime;
            int charDelay = (int) (1000f / typeSpeed);
            killTextProgress = Math.min((int) (elapsed / charDelay), killText.length());

            if (killTextProgress >= killText.length()) {
                if (elapsed > (long) killText.length() * charDelay + 500) {
                    openAnimation.run(0.0, 0.3, Easings.CUBIC_IN, true);
                }
            }

            killTextAnimation.run(killTextProgress >= killText.length() ? 0.0 : 1.0, 0.2, Easings.LINEAR, true);

            if (enableElementAnimation) {
                float fadeProgress = Math.min(1.0f, elapsed / 200f);
                headAnimation.run(1.0 - fadeProgress, 0.2, Easings.LINEAR, true);
                leftItemsAnimation.run(1.0 - fadeProgress, 0.2, Easings.LINEAR, true);
                rightItemsAnimation.run(1.0 - fadeProgress, 0.2, Easings.LINEAR, true);
                barAnimation.run(1.0 - fadeProgress, 0.2, Easings.LINEAR, true);
                textAnimation.run(1.0 - fadeProgress, 0.2, Easings.LINEAR, true);
            }
        } else {
            openAnimation.run(out ? 0.0 : 1.0, 0.3, out ? Easings.CUBIC_IN : Easings.CUBIC_OUT, true);

            if (enableElementAnimation) {
                if (out) {
                    textAnimation.run(0.0, 0.15, Easings.CUBIC_IN, true);
                    barAnimation.run(0.0, 0.2, Easings.CUBIC_IN, true);
                    rightItemsAnimation.run(0.0, 0.25, Easings.CUBIC_IN, true);
                    leftItemsAnimation.run(0.0, 0.3, Easings.CUBIC_IN, true);
                    headAnimation.run(0.0, 0.35, Easings.CUBIC_IN, true);
                } else {
                    barAnimation.run(1.0, 0.15, Easings.CUBIC_OUT, true);
                    headAnimation.run(1.0, 0.2, Easings.CUBIC_OUT, true);
                    leftItemsAnimation.run(1.0, 0.25, Easings.CUBIC_OUT, true);
                    rightItemsAnimation.run(1.0, 0.3, Easings.CUBIC_OUT, true);
                    textAnimation.run(1.0, 0.35, Easings.CUBIC_OUT, true);
                }
            }
        }

        if (openAnimation.getValue() <= 0.0) {
            target = null;
            displayHealth = 0f;
            previousHealth = 0f;
            rememberedItems.clear();
            healthParticles.clear();
            isKilling = false;
            killTextProgress = 0;
            return;
        }

        if (target != null) {
            float health = getTargetHealth();
            boolean lowHealth = health < 4f;
            headShakeAnimation.run(lowHealth ? 1.0 : 0.0, 0.2, Easings.CUBIC_OUT, true);
            headRedAnimation.run(lowHealth ? 1.0 : 0.0, 0.3, Easings.CUBIC_OUT, true);
        }

        // Render in 3D world
        drawTargetHudInWorld(event);
    }

    private void drawTargetHudInWorld(Render3DLastEvent event) {
        if (this.target == null || mc.getRenderManager() == null) return;

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
        float totalWidth = 110.5425f * scaleValue;
        float totalHeight = 52f * scaleValue;

        drag.position.set(-totalWidth / 2f, -totalHeight / 2f);

        // Enable transparency
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        // Render the HUD
        drawTargetHud(fakeEvent);

        // Restore
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        drag.position.set(originalX, originalY);

        matrix.pop();
    }
}

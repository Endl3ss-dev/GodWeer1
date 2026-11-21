package tech.lightclient.ui.hud.star.impl;

import java.util.List;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.impl.combat.AimBot;
import tech.lightclient.modules.impl.combat.AuraModule;
import tech.lightclient.modules.impl.combat.ThrowableAim;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.star.HudElement;
import tech.lightclient.ui.hud.star.ItemDraw;
import tech.lightclient.ui.hud.star.PopupMenu;
import tech.lightclient.ui.hud.star.SAnim;
import tech.lightclient.ui.hud.star.SEasing;
import tech.lightclient.ui.hud.star.STimer;
import tech.lightclient.ui.hud.star.StarUtil;

public class TargetHudElement extends HudElement {
    private static final float CARD_WIDTH = 103.0f;
    private static final float CARD_HEIGHT = 31.0f;

    private final SAnim content = new SAnim(300L, 0.0f, SEasing.BAKEK_SIZE);
    private final SAnim health = new SAnim(300L, 0.0f, SEasing.BAKEK);
    private final SAnim golden = new SAnim(300L, 0.0f, SEasing.BAKEK);
    private final SAnim number = new SAnim(300L, 0.0f, SEasing.FIGMA_EASE_IN_OUT);
    private final SAnim itemsX = new SAnim(300L, 0.0f, SEasing.BAKEK);
    private final SAnim copy = new SAnim(300L, 0.0f, SEasing.BAKEK);
    private final SAnim success = new SAnim(500L, 0.0f, SEasing.BAKEK_SIZE);
    private final STimer copyTimer = new STimer();
    private boolean copied;

    private LivingEntity target;
    private final SAnim[] items = new SAnim[4];

    public TargetHudElement(Function module) {
        super(module, "Star Таргет", "icons/hud/target.png", 10.0f, 200.0f);
        for (int i = 0; i < items.length; i++) {
            items[i] = new SAnim(300L, 0.0f, SEasing.BAKEK);
        }
    }

    @Override
    protected void update(EventRender.Screen.Hud event) {
        LivingEntity resolved = getTarget();
        if (resolved != null) {
            target = resolved;
        }
        width = CARD_WIDTH;
        height = CARD_HEIGHT;

        if (resolved != null) {
            float hp = healthOf(resolved);
            health.update(hp / Math.max(1.0f, resolved.getMaxHealth()));
            golden.update(resolved.getAbsorptionAmount() / 20.0f);
            number.update(hp);
            content.update(true);
            setSelfVisible(true);
        } else {
            content.update(false);
            setSelfVisible(content.getValue() > 0.02f);
            if (content.getValue() <= 0.02f) {
                target = null;
            }
        }
    }

    @Override
    protected void draw(EventRender.Screen.Hud event, float alpha) {
        if (target == null || content.getValue() <= 0.02f) {
            return;
        }
        Renderer2D r = event.getRenderer();
        float appear = alpha * content.getValue();

        String numberText = StarUtil.formatNumber(number.getValue());
        float numberWidth = FontRegistry.DELTA_HUD.getWidth(numberText, 6.0f);

        r.pushAlpha(appear);
        try {
            drawGear(event, r, appear);

            drawClientRect(r, scope(), x, y, width, height, 7.0f, alpha, dragging.isDragging());

            r.pushRoundedClipRect((int) x, (int) y, (int) Math.ceil(width), (int) Math.ceil(height), 7.0f, 7.0f, 7.0f, 7.0f);
            try {
                drawHead(r, appear);

                float nameShift = 8.0f * copy.getValue();
                float nameMaxWidth = width - 40.0f - nameShift - numberWidth - 7.0f;
                String name = StarUtil.trim(FontRegistry.DELTA_HUD, target.getName().getString(), nameMaxWidth, 7.0f);
                float rowTop = y + 6.0f + 6.0f * (1.0f - content.getValue());
                r.text(FontRegistry.DELTA_HUD, x + 30.0f + nameShift,
                        StarUtil.textBaseline(FontRegistry.DELTA_HUD, 'H', rowTop, 7.0f, 7.0f), 7.0f, name,
                        StarUtil.text(alpha));

                r.text(FontRegistry.DELTA_HUD, x + width - 7.0f - numberWidth,
                        StarUtil.textBaseline(FontRegistry.DELTA_HUD, '0', rowTop, 6.0f, 6.0f), 6.0f, numberText,
                        StarUtil.accent(alpha));

                float barY = y + height - 9.0f;
                r.rect(x + 30.0f, barY, 65.0f, 3.0f, 0.7f, StarUtil.text(Math.round(26.0f * alpha / 255.0f * 255.0f) / 255.0f * 0.35f));
                r.rect(x + 30.0f, barY, 65.0f, 3.0f, 0.7f, StarUtil.text(0.12f * alpha));
                float healthWidth = 65.0f * MathHelper.clamp(health.getValue(), 0.0f, 1.0f);
                if (healthWidth > 0.2f) {
                    r.shadow(x + 30.0f, barY, healthWidth, 3.0f, 0.7f, 1.5f, 0.3f, StarUtil.accent(alpha * 0.35f));
                    r.rect(x + 30.0f, barY, healthWidth, 3.0f, 0.7f, StarUtil.accent(alpha));
                }
                float goldenWidth = 65.0f * MathHelper.clamp(golden.getValue(), 0.0f, 1.0f);
                if (goldenWidth > 0.2f) {
                    r.shadow(x + 95.0f - goldenWidth, barY, goldenWidth, 3.0f, 0.7f, 1.5f, 0.3f,
                            ClientColors.applyAlpha((int) StarUtil.GOLD, alpha * 0.35f));
                    r.rect(x + 95.0f - goldenWidth, barY, goldenWidth, 3.0f, 0.7f,
                            ClientColors.applyAlpha((int) StarUtil.GOLD, alpha));
                }
            } finally {
                r.popClipRect();
            }
        } finally {
            r.popAlpha();
        }
    }

    private void drawGear(EventRender.Screen.Hud event, Renderer2D r, float appear) {
        String mode = armorMode();
        if ("none".equals(mode)) {
            return;
        }

        ItemStack[] handItems = {target.getMainHandStack(), target.getOffHandStack()};
        List<ItemStack> armor = new java.util.ArrayList<>();
        for (EquipmentSlot slot : new EquipmentSlot[] {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        }) {
            ItemStack stack = target.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                armor.add(stack);
            }
        }

        float animOff = 0.0f;
        for (ItemStack stack : armor) {
            animOff += ("icon".equals(mode) ? 11.0f : Math.max(9.0f, FontRegistry.DELTA_HUD.getWidth(percent(stack), 6.0f))) + 2.0f;
        }
        if ("icon".equals(mode)) {
            for (ItemStack handItem : handItems) {
                if (!handItem.isEmpty()) {
                    animOff += 13.0f;
                }
            }
        }

        itemsX.update(animOff - 2.0f);
        float xOffsetBase = -itemsX.getValue() / 2.0f;
        int i = 0;
        float xOffset = xOffsetBase;

        for (ItemStack stack : armor) {
            items[i].update(true);
            float itemAnim = content.getValue() * items[i].getValue();
            String percent = percent(stack);
            boolean isIcon = "icon".equals(mode);
            float textWidth = FontRegistry.DELTA_HUD.getWidth(percent, 6.0f);
            float iconSize = isIcon ? 11.0f : 9.0f;
            float panelWidth = isIcon ? 11.0f : Math.max(iconSize, textWidth);
            float panelHeight = isIcon ? 11.0f : 9.0f;
            float panelX = x + width / 2.0f + xOffset;
            float panelY = y + height - 4.0f + 6.0f * itemAnim;

            if (isIcon) {
                r.pushRoundedClipRect((int) panelX, (int) panelY, (int) Math.ceil(panelWidth), (int) Math.ceil(panelHeight),
                        1.5f, 1.5f, 1.5f, 1.5f);
                try {
                    ItemDraw.drawItem(r, stack, panelX, panelY, iconSize, appear);
                } finally {
                    r.popClipRect();
                }
            } else {
                // Percent text hugging the bottom edge of the panel, armor icon just below it.
                float textTop = y + height + 1.0f + 6.0f * itemAnim;
                r.text(FontRegistry.DELTA_HUD, panelX + (panelWidth - textWidth) / 2.0f, textTop, 6.0f, percent, StarUtil.text(appear));
                float iconX = panelX + (panelWidth - iconSize) / 2.0f;
                float iconY = textTop + 1.5f;
                r.pushRoundedClipRect((int) iconX, (int) iconY, (int) Math.ceil(iconSize), (int) Math.ceil(iconSize),
                        1.5f, 1.5f, 1.5f, 1.5f);
                try {
                    ItemDraw.drawItem(r, stack, iconX, iconY, iconSize, appear);
                } finally {
                    r.popClipRect();
                }
            }

            xOffset += (panelWidth + 2.0f) * itemAnim;
            i++;
        }
        for (int k = i; k < items.length; k++) {
            items[k].update(false);
        }

        float handOffset = "number".equals(mode) ? -handItems.length * 13.0f / 2.0f : xOffset;
        for (ItemStack handItem : handItems) {
            if (handItem.isEmpty()) {
                continue;
            }
            float handX = x + width / 2.0f + handOffset;
            float handY = y + height - 4.0f + 6.0f * content.getValue() + ("number".equals(mode) ? 18.0f : 0.0f);
            r.pushRoundedClipRect((int) handX, (int) handY, 11, 11, 1.5f, 1.5f, 1.5f, 1.5f);
            try {
                ItemDraw.drawItem(r, handItem, handX, handY, 11.0f, appear);
            } finally {
                r.popClipRect();
            }
            handOffset += 13.0f;
            if ("icon".equals(mode)) {
                xOffset += 13.0f * content.getValue();
            }
        }
    }

    private void drawHead(Renderer2D r, float appear) {
        float faceX = x + 6.0f * content.getValue();
        float faceY = y + 6.0f;
        float faceSize = 19.0f;
        int tint = ClientColors.applyAlpha(0xFFFFFFFF, appear);
        boolean drawn = false;
        if (target instanceof AbstractClientPlayerEntity player && player.getSkin() != null) {
            try {
                    Identifier skin = player.getSkin().body().texturePath();
                if (skin != null) {
                    r.drawTextureRegionRoundedNearest(skin, faceX, faceY, faceSize, faceSize,
                            8f / 64f, 8f / 64f, 16f / 64f, 16f / 64f, tint, 3.0f, false);
                    r.drawTextureRegionRoundedNearest(skin, faceX, faceY, faceSize, faceSize,
                            40f / 64f, 8f / 64f, 48f / 64f, 16f / 64f, tint, 3.0f, false);
                    drawn = true;
                }
            } catch (Throwable ignored) {
                drawn = false;
            }
        }
        if (!drawn) {
            String mode = InterfaceGlassMode();
            Identifier fallback = StarUtil.id("icons/hud/who"
                    + (Interface.isLiquidGlass() ? "glass" : (isLightTheme() ? "" : "dark")) + ".png");
            r.drawTextureRounded(fallback, faceX, faceY, faceSize, faceSize, tint, 3.0f);
        }
    }

    private static boolean isLightTheme() {
        return false;
    }

    private static String InterfaceGlassMode() {
        return "glass";
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, int button) {
        boolean hoverName = mouseX >= x + 30.0f && mouseX <= x + 90.0f
                && mouseY >= y + 3.0f && mouseY <= y + 15.0f;
        if (hoverName && button == 0 && target != null) {
            copyToClipboard(target.getName().getString());
            copyTimer.reset();
            copied = true;
            return;
        }
        super.onMouseClicked(mouseX, mouseY, button);
    }

    private void copyToClipboard(String text) {
        try {
            mc.keyboard.setClipboard(text);
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void fillSettingsPopup(PopupMenu popup) {
        popup.button("Броня: " + armorLabel(), iconPath(), () -> {
            String next = switch (armorMode()) {
                case "none" -> "number";
                case "number" -> "icon";
                default -> "none";
            };
            DraggingManagerSetMode(scope(), "armor", next);
        });
        popup.checkbox("Цель в прицеле", () -> boolSetting(scope(), "look", true),
                value -> setBoolSetting(scope(), "look", value));
    }

    private static void DraggingManagerSetMode(String scope, String key, String value) {
        tech.lightclient.client.utils.render.drag.DraggingManager.setHudMode(scope, key, value);
    }

    private String armorMode() {
        String mode = tech.lightclient.client.utils.render.drag.DraggingManager
                .getHudMode(scope(), "armor", "number");
        return mode == null ? "number" : mode;
    }

    private String armorLabel() {
        return switch (armorMode()) {
            case "none" -> "скрыта";
            case "icon" -> "иконки";
            default -> "числа";
        };
    }

    @Override
    public String settingName() {
        return "Активный таргет";
    }

    private LivingEntity getTarget() {
        LivingEntity combatTarget = firstAlive(AuraModule.target, AimBot.getTarget(), ThrowableAim.target);
        if (combatTarget != null) {
            return combatTarget;
        }
        if (boolSetting(scope(), "look", true) && mc.targetedEntity instanceof LivingEntity living && living.isAlive()) {
            return living;
        }
        return mc.currentScreen instanceof ChatScreen ? mc.player : null;
    }

    private LivingEntity firstAlive(LivingEntity... candidates) {
        for (LivingEntity candidate : candidates) {
            if (candidate != null && candidate.isAlive() && candidate != mc.player) {
                return candidate;
            }
        }
        return null;
    }

    private static float healthOf(LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            return player.getHealth();
        }
        return entity.getHealth();
    }

    private static String percent(ItemStack stack) {
        if (!stack.isEmpty() && stack.isDamageable() && stack.getMaxDamage() > 0) {
            int max = stack.getMaxDamage();
            int damage = stack.getDamage();
            if (damage >= max) {
                return "0%";
            }
            return String.format("%.0f%%", 100.0 - (double) damage / max * 100.0);
        }
        return "100%";
    }

    private static int blend(int a, int b, float amount) {
        float t = Math.max(0.0f, Math.min(1.0f, amount));
        int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
        int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
        int rr = Math.round(ar + (br - ar) * t);
        int rg = Math.round(ag + (bg - ag) * t);
        int rb = Math.round(ab + (bb - ab) * t);
        return 0xFF000000 | (rr << 16) | (rg << 8) | rb;
    }
}

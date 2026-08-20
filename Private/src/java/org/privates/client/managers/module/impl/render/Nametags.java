package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.events.orbit.EventPriority;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.events.render.RenderNameEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.misc.Globals;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.client.utils.render.color.ColorFormatting;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.*;
import org.privates.client.utils.render.font.Font;
import org.privates.client.utils.render.font.Fonts;
import org.privates.common.impl.globals.ClientAPI;

import java.util.*;
import java.util.stream.Collectors;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Nametags NEW", category = Category.RENDER, description = "WallHack & Esp")
public class Nametags extends Module {

    public static Nametags getInstance() {
        return Instance.get(Nametags.class);
    }

    private final MultiBooleanSetting checks = new MultiBooleanSetting(this, "Элементы",
            BooleanSetting.of("Игроки", true), BooleanSetting.of("Предметы", false));
    private final SliderSetting fontSize = new SliderSetting(this, "Размер шрифта", 5F, 4F, 10F, 0.1F);
    private final BooleanSetting optimized = new BooleanSetting(this, "Оптимизировать", true);
    private final BooleanSetting renderDonat = new BooleanSetting(this, "Отображать донат", true);
    private final BooleanSetting showArmor = new BooleanSetting(this, "Отображать броню", true);
    private final BooleanSetting showDurability = new BooleanSetting(this, "Прочность брони", true).setVisible(showArmor::getValue);
    private final BooleanSetting smoothDurability = new BooleanSetting(this, "Плавная прочность", true).setVisible(() -> showArmor.getValue() && showDurability.getValue());
    private final BooleanSetting gradientDurability = new BooleanSetting(this, "Градиент прочности", true).setVisible(() -> showArmor.getValue() && showDurability.getValue());
    private final BooleanSetting holyWorld = new BooleanSetting(this, "HolyWorld", false);
    private final BooleanSetting renderItemIcon = new BooleanSetting(this, "Иконка предмета", true);
    private final BooleanSetting showHead = new BooleanSetting(this, "Голова игрока", true);
    private final BooleanSetting showEffects = new BooleanSetting(this, "Эффекты справа", true);
    private final BooleanSetting showSphereBottom = new BooleanSetting(this, "Предмет снизу", true);
    private final BooleanSetting showVerticalHpBar = new BooleanSetting(this, "Вертикальный HP бар", true);
    private final SliderSetting playerLimit = new SliderSetting(this, "Лимит игроков", 15F, 5F, 30F, 1F);
    private final BooleanSetting prioritizeArmor = new BooleanSetting(this, "Приоритет по броне", true);
    private final SliderSetting itemLimit = new SliderSetting(this, "Лимит предметов", 100F, 50F, 200F, 1F);
    private final BooleanSetting prioritizeItems = new BooleanSetting(this, "Приоритет по предметам", true);
    private final BindSetting previewGuiBind = new BindSetting(this, "Открыть Preview GUI", GLFW.GLFW_KEY_P);

    private final Font font = Fonts.SF_MEDIUM;
    private final int timeTick = 600;
    private final Map<ItemStack, Float> durabilityCache = new HashMap<>();
    private final Map<PlayerEntity, Float> scaleCache = new HashMap<>();

    // Кэши для анимации размеров элементов
    private final Map<PlayerEntity, Float> armorScaleCache = new HashMap<>();
    private final Map<PlayerEntity, Float> hpBarScaleCache = new HashMap<>();
    private final Map<PlayerEntity, Float> bottomBarScaleCache = new HashMap<>();

    private static final float SCALE_FACTOR = 2.75f;
    private static final float CORNER_RADIUS = 4.2f;
    private static final float TEXT_PAD_H = 6f;
    private static final float TEXT_PAD_V = 3f;

    // Минимальный scale = 1.0 (базовый размер без уменьшений)
    // Максимальный scale = 3.0 (в 3 раза больше базового)
    private static final float SCALE_MIN = 0.9f;
    private static final float SCALE_MAX = 1.1f;
    // Lerp в ~3 раза медленнее: было 0.165f → стало 0.055f
    private static final float SCALE_LERP = 0.055f;

    // Параметры анимации для элементов
    private static final float ELEMENT_SCALE_MIN = 0.85f;
    private static final float ELEMENT_SCALE_MAX = 1.15f;
    private static final float ELEMENT_SCALE_LERP = 0.08f;

    private boolean wasPreviewKeyPressed = false;

    private int bgColor() {
        return ColorUtil.multAlpha(ColorUtil.multDark(Theme.getInstance().clientColor(), 0.15f), 0.75f);
    }

    private int borderColor() {
        return ColorUtil.multAlpha(Theme.getInstance().clientColor(), 0.45f);
    }

    private int accentColor() {
        return Theme.getInstance().clientColor();
    }

    @EventHandler
    public void onRenderName(RenderNameEvent event) {
        if (event.getEntity() instanceof AbstractClientPlayerEntity) event.cancel();
    }

    @EventHandler
    public void onPacket(PacketEvent e) {
        if (PlayerUtil.nullCheck()) return;
        IPacket<?> packet = e.getPacket();
        if (packet instanceof SEntityStatusPacket entityStatus) {
            Entity entity = entityStatus.getEntity(mc.world);
            if (entity instanceof PlayerEntity player && (entityStatus.getOpCode() == 2 || entityStatus.getOpCode() == 33)) {
                if (!(player.prevFallDistance > 3 && player.fallDistance == 0)) {
                    for (Entity ent : mc.world.getAllEntities()) {
                        if (ent instanceof MobEntity mob && mob.getLastAttackedEntity() == player && mob.getLastAttackedEntityTime() < 1000)
                            return;
                    }
                    player.addPotionEffect(new EffectInstance(Effects.UNLUCK, timeTick, -1));
                }
            }
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;
        boolean pressed = GLFW.glfwGetKey(mc.getMainWindow().getHandle(), previewGuiBind.getValue()) == GLFW.GLFW_PRESS;
        if (pressed && !wasPreviewKeyPressed && mc.currentScreen == null) {
            Entity closest = findClosestValidEntity();
            if (closest != null) mc.displayScreen(new NametagsPreviewGui(closest));
        }
        wasPreviewKeyPressed = pressed;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRender2D(Render2DEvent event) {
        float fontHeight = fontSize.getValue();
        List<Entity> targets = new ArrayList<>();

        if (checks.getValue("Игроки")) {
            List<PlayerEntity> players = mc.world.getEntitiesWithinAABB(
                    PlayerEntity.class, mc.renderViewEntity.getBoundingBox().grow(128), this::isValidPlayer);
            if (prioritizeArmor.getValue() && players.size() > playerLimit.getValue()) {
                players.sort(Comparator.comparing((PlayerEntity p) -> -getArmorPriority(p)));
                players = players.subList(0, playerLimit.getValue().intValue());
            }
            targets.addAll(players);
        }

        if (checks.getValue("Предметы")) {
            List<ItemEntity> items = mc.world.loadedItemEntityList().stream()
                    .filter(e -> e instanceof ItemEntity && isValidItem((ItemEntity) e))
                    .map(e -> (ItemEntity) e)
                    .sorted(Comparator.comparingDouble(i -> mc.renderViewEntity.getDistanceSq(i)))
                    .collect(Collectors.toList());
            if (prioritizeItems.getValue() && items.size() > itemLimit.getValue()) {
                items.sort(Comparator.comparing((ItemEntity i) -> -i.getItem().getRarity().ordinal()));
                items = items.subList(0, itemLimit.getValue().intValue());
            }

            // Group nearby items
            List<List<ItemEntity>> itemGroups = groupNearbyItems(items, 0.33);
            Set<ItemEntity> processedItems = new HashSet<>();

            for (List<ItemEntity> group : itemGroups) {
                if (group.size() > 1) {
                    // Render grouped items
                    renderGroupedItemNametag(event.getMatrix(), group, fontHeight);
                    processedItems.addAll(group);
                }
            }

            // Add ungrouped items to targets for normal rendering
            for (ItemEntity item : items) {
                if (!processedItems.contains(item)) {
                    targets.add(item);
                }
            }
        }

        targets.forEach(entity -> renderNametag(entity, event.getMatrix(), fontHeight));
    }

    private void renderNametag(Entity entity, MatrixStack matrix, float fontHeight) {
        Vector3d interpolated = RenderUtil3D.interpolate(entity, mc.getRenderPartialTicks());
        AxisAlignedBB aabb = getEntityBox(entity, interpolated);

        Vector2f min = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
        Vector2f max = new Vector2f(Float.MIN_VALUE, Float.MIN_VALUE);

        for (Vector3d corner : aabb.getCorners()) {
            Vector2f vec = Project.project2D(corner);
            if (vec.x != Float.MAX_VALUE && vec.y != Float.MAX_VALUE) {
                min.x = Math.min(min.x, vec.x);
                min.y = Math.min(min.y, vec.y);
                max.x = Math.max(max.x, vec.x);
                max.y = Math.max(max.y, vec.y);
            }
        }

        float projW = max.x - min.x;
        float projH = max.y - min.y;
        float centerX = min.x + projW / 2f;

        if (checks.getValue("Игроки") && entity instanceof PlayerEntity player) {
            renderPlayerNametag(matrix, player, centerX, min.y, projW, projH, fontHeight);
        } else if (checks.getValue("Предметы") && entity instanceof ItemEntity item) {
            renderItemNametag(matrix, item, centerX, min.y, fontHeight);
        }
    }

    private void renderPlayerNametag(MatrixStack matrix, PlayerEntity player,
                                     float centerX, float entityTopY,
                                     float projW, float projH, float fontHeight) {
        float topH = 14.4f;
        float hpBarW = 1.8f;
        float sideW = 0.4f;
        float botH = 14.4f;
        float gap = 0.5f;

        float panelW = Math.max(projW, 40f);
        float panelH = projH;

        float minTopW = computeMinTopWidth(player, fontHeight, sideW);
        float topTotalW = Math.max(hpBarW + gap + sideW + gap + panelW, minTopW);
        float midTotalW = hpBarW + gap + sideW + gap + panelW;

        boolean hasBottom = showSphereBottom.getValue();

        float anchorY = entityTopY + projH * 0.5f;

        float topExtraShift = 0f;

        if (showArmor.getValue()) {
            List<ItemStack> armorList = new ArrayList<>();
            for (ItemStack s : player.getArmorInventoryList()) armorList.add(s);
            if (!player.getHeldItemMainhand().isEmpty()) armorList.add(player.getHeldItemMainhand());
            if (!player.getHeldItemOffhand().isEmpty()) armorList.add(player.getHeldItemOffhand());
            Collections.reverse(armorList);

            int count = Math.min(armorList.size(), 6);
            if (count > 0) {
                float slotH = sideW + 12;
                float groupH = count * slotH + Math.max(0, count - 1) * gap;
                float totalTagH = topH + gap + panelH + (hasBottom ? gap + botH : 0f);
                float startY = anchorY - totalTagH / 2f;
                float midY = startY + topH + gap;
                float armorStartY = midY + (panelH - groupH) / 2f;

                if (armorStartY < midY) {
                    topExtraShift = midY - armorStartY;
                }
            }
        }

        float totalTagH = topH + gap + panelH + (hasBottom ? gap + botH : 0f);
        float startY = anchorY - totalTagH / 2f - topExtraShift;

        float topY = startY;
        float midY = topY + topH + gap;
        float botY = midY + panelH + gap;

        float topStartX = centerX - topTotalW / 2f;
        float midStartX = centerX - midTotalW / 2f;
        float hpX = midStartX;
        float sideX = hpX + hpBarW + gap;
        float panelX = sideX + sideW + gap;

        renderTopBar(matrix, player, topStartX, topY, topTotalW, topH, sideW, fontHeight);

        if (showVerticalHpBar.getValue())
            renderVerticalHpBar(matrix, player, hpX, midY, hpBarW, panelH, gap);

        if (showArmor.getValue())
            renderSideArmorBar(matrix, player, sideX - 20, midY, sideW + 12, panelH, gap);

        if (showEffects.getValue())
            renderEffectsPanel(matrix, player, panelX + panelW + gap, midY + 15, gap, fontHeight);

        if (hasBottom)
            renderBottomBar(matrix, player, panelX, botY, panelW, botH, gap, fontHeight);
    }

    private float computeMinTopWidth(PlayerEntity player, float fontHeight, float S) {
        float headBlockW = 36f * S;
        float rightBlockW = 50f * S;
        ITextComponent displayName = renderDonat.getValue() ? player.getDisplayName() : player.getName();
        String nameStr = displayName.getString();
        String client = Globals.getInstance().isEnabled() ? ClientAPI.getClient(player.getName().getString()) : null;
        float nameW = font.getWidth(nameStr, fontHeight) + (client != null ? font.getWidth(client + " ", fontHeight) : 0f);
        String hpStr = formatHealthString(player.getHealthFixed()) + " HP";
        float hpW = font.getWidth(hpStr, fontHeight);
        return headBlockW + (nameW + TEXT_PAD_H * 2f) + Math.max(rightBlockW, hpW + TEXT_PAD_H * 2f);
    }

    private void renderTopBar(MatrixStack matrix, PlayerEntity player,
                              float x, float y, float totalW, float h, float S, float fontHeight) {
        float headBlockW = 36f * S;
        ITextComponent displayName = renderDonat.getValue() ? player.getDisplayName() : player.getName();
        String nameStr = displayName.getString();
        String client = Globals.getInstance().isEnabled() ? ClientAPI.getClient(player.getName().getString()) : null;
        float nameContentW = font.getWidth(nameStr, fontHeight) + (client != null ? font.getWidth(client + " ", fontHeight) : 0f);
        String hpStr = formatHealthString(player.getHealthFixed()) + " HP";
        float hpW = font.getWidth(hpStr, fontHeight);
        float rightBlockW = Math.max(50f * S, hpW + TEXT_PAD_H * 2f);
        float nameBlockW = Math.max(totalW - headBlockW - rightBlockW, nameContentW + TEXT_PAD_H * 2f);

        drawBorderedRect(matrix, x, y, headBlockW, h, CORNER_RADIUS * S, true, false, true, false);
        drawBorderedRect(matrix, x + headBlockW, y, nameBlockW, h, 0, false, false, false, false);
        drawBorderedRect(matrix, x + headBlockW + nameBlockW, y, rightBlockW, h, CORNER_RADIUS * S, false, true, false, true);

        if (showHead.getValue() && player instanceof AbstractClientPlayerEntity clientPlayer) {
            float headSize = Math.min(26f * S, h - 4f * S);
            drawPlayerHead(matrix, clientPlayer, x + (headBlockW - headSize) / 2f + 2, y + 1 + (h - headSize) / 2f + 1.5f, headSize);
        }

        float fs = fontHeight;
        float textX = x + headBlockW + TEXT_PAD_H;
        float textY = y + h / 2f - fs / 2f;
        if (client != null) {
            font.draw(matrix, client + " ", textX, textY, accentColor(), fs);
            font.draw(matrix, nameStr, textX + font.getWidth(client + " ", fs), textY, -1, fs);
        } else {
            font.draw(matrix, nameStr, textX, textY, -1, fs);
        }

        font.draw(matrix, hpStr,
                x + headBlockW + nameBlockW + rightBlockW / 2f - hpW / 2f,
                y + h / 2f - fs / 2f, accentColor(), fs);
    }

    private void drawPlayerHead(MatrixStack matrix, AbstractClientPlayerEntity player, float x, float y, float size) {
        float hurtPC = (float) Math.sin(player.hurtTime * (18F * Math.PI / 180F));
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

        RenderUtil.Texture.roundedHead(player.getLocationSkin(), x, y, size, size, size / 7F, rgba[3], 0);

        RenderSystem.popMatrix();
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.disableBlend();
    }

    private void renderVerticalHpBar(MatrixStack matrix, PlayerEntity player, float x, float y, float w, float h, float S) {
        float health = player.getHealthFixed();
        float maxHp = player.getMaxHealth();
        float percent = maxHp > 0 ? Math.min(1f, health / maxHp) : 0f;

        // Получаем анимированный scale
        float scale = getElementScale(player, hpBarScaleCache);

        // Применяем scale к размерам
        float scaledW = w * scale;
        float scaledH = h * scale;

        // Центрируем по вертикали
        float offsetY = (h - scaledH) / 2f;

        float finalX = x;
        float finalY = y + offsetY;

        RenderUtil.Rounded.roundedRect(matrix, finalX, finalY, scaledW, scaledH, bgColor(), Round.of(CORNER_RADIUS * S));
        RenderUtil.Rounded.roundedOutline(matrix, finalX, finalY, scaledW, scaledH, 0.5f, borderColor(), Round.of(CORNER_RADIUS * S));

        float pad = S, barW = scaledW - pad * 2f, barH = scaledH - pad * 2f;
        float filled = barH * percent;
        float barX = finalX + pad, barY = finalY + pad + (barH - filled);
        Round barRound = Round.of(2f * S);

        if (gradientDurability.getValue()) {
            RenderUtil.Rounded.roundedRect(matrix, barX, barY, barW, filled,
                    ColorUtil.getColor(100, 247, 133, 255), ColorUtil.getColor(100, 247, 133, 255),
                    ColorUtil.getColor(160, 65, 61, 255), ColorUtil.getColor(160, 65, 61, 255), barRound);
        } else {
            RenderUtil.Rounded.roundedRect(matrix, barX, barY, barW, filled, accentColor(), barRound);
        }
    }

    private void renderSideArmorBar(MatrixStack matrix, PlayerEntity player, float x, float y, float w, float totalH, float S) {
        List<ItemStack> armorList = new ArrayList<>();
        for (ItemStack s : player.getArmorInventoryList()) armorList.add(s);
        Collections.reverse(armorList);

        List<ItemStack> all = new ArrayList<>(armorList);
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offhand = player.getHeldItemOffhand();
        if (!mainHand.isEmpty()) all.add(mainHand);
        if (!offhand.isEmpty()) all.add(offhand);

        int count = Math.min(all.size(), 6);
        if (count == 0) return;

        // Получаем анимированный scale
        float scale = getElementScale(player, armorScaleCache);

        float slotH = w * scale;
        float slotW = w * scale;
        float gapScaled = S * scale;
        float groupH = count * slotH + Math.max(0, count - 1) * gapScaled;

        // Ограничиваем количество слотов чтобы не вылезти за totalH
        while (count > 1 && groupH > totalH) {
            count--;
            groupH = count * slotH + Math.max(0, count - 1) * gapScaled;
        }

        float startY = y + (totalH - groupH) / 2f;

        for (int i = 0; i < count; i++) {
            float slotY = startY + i * (slotH + gapScaled);
            drawSideSlot(matrix, x, slotY, slotW, slotH, S * scale, i == 0, i == count - 1, all.get(i));
        }
    }

    private void drawSideSlot(MatrixStack matrix, float x, float y, float w, float h, float S,
                              boolean roundTop, boolean roundBot, ItemStack stack) {
        // Round constructor: LB, LT, RB, RT (Left Bottom, Left Top, Right Bottom, Right Top)
        Round round = new Round(
                roundBot ? CORNER_RADIUS * S : 0,  // Left Bottom
                roundTop ? CORNER_RADIUS * S : 0,  // Left Top
                roundBot ? CORNER_RADIUS * S : 0,  // Right Bottom
                roundTop ? CORNER_RADIUS * S : 0   // Right Top
        );
        RenderUtil.Rounded.roundedRect(matrix, x, y, w, h, bgColor(), round);
        RenderUtil.Rounded.roundedOutline(matrix, x, y, w, h, 0.5f, borderColor(), round);

        if (stack.isEmpty()) {
            drawRedCross(matrix, x, y, w, h, S);
        } else {
            float iconSize = w * 0.72f;
            draw2dItemIcon(matrix, stack, x + (w - iconSize) / 2f, y + (h - iconSize) / 2f, iconSize);
            if (showDurability.getValue() && stack.isDamageable()) {
                float target = 1f - (float) stack.getDamage() / (float) stack.getMaxDamage();
                float cached = durabilityCache.getOrDefault(stack, target);
                float dur = smoothDurability.getValue() ? cached + (target - cached) * 0.1f : target;
                durabilityCache.put(stack, dur);
                float barH = 1.5f * S, barX = x + S, barY = y + h - barH - 0.5f * S, barW = w - 2f * S;
                RectUtil.drawRect(matrix, barX, barY, barW, barH, ColorUtil.getColor(20, 20, 20, 200));
                if (gradientDurability.getValue()) drawGradientDurabilityBar(matrix, barX, barY, barW, barH, dur);
                else RectUtil.drawRect(matrix, barX, barY, barW * dur, barH, getDurabilityColor(dur));
            }
        }
    }

    private void drawRedCross(MatrixStack matrix, float x, float y, float w, float h, float S) {
        int red = ColorUtil.getColor(220, 60, 60, 200);
        float pad = w * 0.25f, lw = Math.max(1f, 1.5f * S);
        float cx = x + w / 2f, cy = y + h / 2f, half = w / 2f - pad;
        RenderUtil.Rounded.drawLine(matrix, cx - half, cy - half, cx + half, cy + half, lw, red);
        RenderUtil.Rounded.drawLine(matrix, cx + half, cy - half, cx - half, cy + half, lw, red);
    }

    private void renderEffectsPanel(MatrixStack matrix, PlayerEntity player, float x, float y, float S, float fontHeight) {
        List<EffectInstance> effects = player.getActivePotionEffects().stream()
                .filter(e -> e.getPotion() != Effects.UNLUCK).collect(Collectors.toList());
        if (effects.isEmpty()) return;

        int shown = Math.min(effects.size(), 4);
        float fs = fontHeight * 0.7f;
        float iconSize = 16f * S;
        float maxNameW = 0f;
        float maxDurW = 0f;
        for (int i = 0; i < shown; i++) {
            EffectInstance eff = effects.get(i);
            String name = eff.getPotion().getDisplayName().getString();
            String dur = formatDuration(eff.getDuration());
            maxNameW = Math.max(maxNameW, font.getWidth(name, fs));
            maxDurW = Math.max(maxDurW, font.getWidth(dur, fs * 0.9f));
        }

        float slotW = Math.max(45f * S, iconSize + TEXT_PAD_H * 2f + Math.max(maxNameW, maxDurW) + 3f * S);
        float slotH = Math.max(28f * S, iconSize + TEXT_PAD_V * 2f);
        float gap = 0.5f * S;

        for (int i = 0; i < shown; i++) {
            EffectInstance eff = effects.get(i);
            float slotY = y + i * (slotH + gap);

            // Round corners
            Round round = new Round(
                    i == shown - 1 ? CORNER_RADIUS * S : 0,  // LB
                    i == 0 ? CORNER_RADIUS * S : 0,          // LT
                    i == shown - 1 ? CORNER_RADIUS * S : 0,  // RB
                    i == 0 ? CORNER_RADIUS * S : 0           // RT
            );

            // Draw background
            RenderUtil.Rounded.roundedRect(matrix, x, slotY, slotW - 10, slotH, bgColor(), round);
            RenderUtil.Rounded.roundedOutline(matrix, x, slotY, slotW - 10, slotH, 0.5f, borderColor(), round);

            // Draw effect icon
            float iconX = x + TEXT_PAD_H;
            float iconY = slotY + (slotH - iconSize) / 2f;
            drawEffectIcon(matrix, eff.getPotion(), iconX, iconY, iconSize);

            // Draw effect name and duration with proper z-offset
            matrix.push();
            matrix.translate(0, 0, 200);

            // Draw effect name
            String name = eff.getPotion().getDisplayName().getString();
            float nameX = iconX + iconSize + 3f * S;
            float nameY = slotY + slotH / 2f - fs - 1f;

            // Get effect color
            int effectColor = eff.getPotion().getLiquidColor();
            if (effectColor == 0) effectColor = 0xFFFFFFFF;

            font.draw(matrix, name, nameX, nameY, effectColor, fs);

            // Draw duration
            String dur = formatDuration(eff.getDuration());
            float durY = nameY + fs + 2f;
            int durColor = ColorUtil.rgba(180, 180, 180, 255);
            font.draw(matrix, dur, nameX, durY - 5, durColor, fs * 1.9f);

            matrix.pop();
        }
    }

    private void drawEffectIcon(MatrixStack matrix, Effect effect, float x, float y, float size) {
        net.minecraft.client.renderer.texture.PotionSpriteUploader uploader = mc.getPotionSpriteUploader();
        net.minecraft.client.renderer.texture.TextureAtlasSprite sprite = uploader.getSprite(effect);

        mc.getTextureManager().bindTexture(sprite.getAtlasTexture().getTextureLocation());

        matrix.push();
        matrix.translate(0, 0, 100);

        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        net.minecraft.client.gui.AbstractGui.blit(matrix, (int) x, (int) y, 0, (int) size, (int) size, sprite);

        RenderSystem.disableBlend();

        matrix.pop();
    }

    private void renderBottomBar(MatrixStack matrix, PlayerEntity player,
                                 float x, float y, float w, float h, float S, float fontHeight) {
        ItemStack offhand = player.getHeldItemOffhand();

        // Fixed scale - 20% smaller
        float scale = 0.8f;

        float scaledW = w * scale;
        float scaledH = h * scale;
        float offsetX = (w - scaledW) / 2f;
        float finalX = x + offsetX;
        float finalY = y;
        float iconBlockW = Math.max(36f * S * scale, scaledH);

        if (offhand.isEmpty()) {
            float nameBlockW = scaledW - iconBlockW;
            drawBorderedRect(matrix, finalX, finalY, iconBlockW, scaledH, CORNER_RADIUS * S * scale, true, false, true, false);
            drawBorderedRect(matrix, finalX + iconBlockW, finalY, nameBlockW, scaledH, CORNER_RADIUS * S * scale, false, true, false, true);
            drawRedCross(matrix, finalX, finalY, iconBlockW, scaledH, S * scale);
            return;
        }

        // Check if item has custom name
        boolean hasCustomName = offhand.hasDisplayName();

        if (hasCustomName) {
            ITextComponent displayName = offhand.getDisplayName();
            float itemNameW = font.getWidth(displayName, fontHeight * scale);
            float nameBlockW = Math.max(scaledW - iconBlockW, itemNameW + TEXT_PAD_H * 2f * scale);

            drawBorderedRect(matrix, finalX, finalY, iconBlockW, scaledH, CORNER_RADIUS * S * scale, true, false, true, false);
            drawBorderedRect(matrix, finalX + iconBlockW, finalY, nameBlockW, scaledH, CORNER_RADIUS * S * scale, false, true, false, true);

            float iconSize = Math.min(26f * S * scale, Math.min(iconBlockW, scaledH) - 4f * S * scale);
            draw2dItemIcon(matrix, offhand, finalX + (iconBlockW - iconSize) / 2f, finalY + (scaledH - iconSize) / 2f, iconSize);
            font.drawTextComponent(matrix, displayName, finalX + iconBlockW + TEXT_PAD_H * scale, finalY + scaledH / 2f - fontHeight * scale / 2f, -1, false, fontHeight * scale);
        } else {
            // No custom name - only draw icon block
            drawBorderedRect(matrix, finalX, finalY, iconBlockW, scaledH, CORNER_RADIUS * S * scale, true, true, true, true);

            float iconSize = Math.min(26f * S * scale, Math.min(iconBlockW, scaledH) - 4f * S * scale);
            draw2dItemIcon(matrix, offhand, finalX + (iconBlockW - iconSize) / 2f, finalY + (scaledH - iconSize) / 2f, iconSize);
        }
    }

    private void renderItemNametag(MatrixStack matrix, ItemEntity itemEntity, float centerX, float topY, float fontHeight) {
        ItemStack stack = itemEntity.getItem();
        ITextComponent tag;
        if (holyWorld.getValue()) {
            tag = new StringTextComponent(stack.getTranslationKey().replace("item.minecraft.", "").replace("_", " ").toUpperCase());
        } else {
            tag = new StringTextComponent("").append(stack.getDisplayName());
            if (stack.getCount() > 1) {
                String gray = ColorFormatting.getColor(TextFormatting.GRAY.getColor());
                tag = tag.deepCopy().appendString(gray + " " + stack.getCount() + "x");
            }
        }

        float nameW = getWidth(tag);
        float rectW = Math.max(nameW + TEXT_PAD_H * 2f, 20f);
        float rectH = fontHeight + TEXT_PAD_V * 2f;
        float tagY = topY - rectH - 2f;

        if (renderItemIcon.getValue()) {
            float iconSize = fontHeight + 7f;
            draw2dItemIcon(matrix, stack, centerX - iconSize / 2f, tagY - iconSize - 2f, iconSize);
        }

        Round r = Round.of(CORNER_RADIUS);
        RenderUtil.Rounded.roundedRect(matrix, centerX - rectW / 2f, tagY, rectW, rectH, bgColor(), r);
        RenderUtil.Rounded.roundedOutline(matrix, centerX - rectW / 2f, tagY, rectW, rectH, 0.5f, borderColor(), r);
        font.drawTextComponent(matrix, tag, centerX - nameW / 2f, tagY + TEXT_PAD_V, -1, false, fontHeight);
    }

    private List<List<ItemEntity>> groupNearbyItems(List<ItemEntity> items, double threshold) {
        List<List<ItemEntity>> groups = new ArrayList<>();
        Set<ItemEntity> processed = new HashSet<>();

        for (ItemEntity item : items) {
            if (processed.contains(item)) continue;

            List<ItemEntity> group = new ArrayList<>();
            group.add(item);
            processed.add(item);

            // Find all items within threshold distance
            for (ItemEntity other : items) {
                if (processed.contains(other)) continue;

                double distance = item.getPositionVec().distanceTo(other.getPositionVec());
                if (distance <= threshold) {
                    group.add(other);
                    processed.add(other);
                }
            }

            groups.add(group);
        }

        return groups;
    }

    private void renderGroupedItemNametag(MatrixStack matrix, List<ItemEntity> group, float fontHeight) {
        if (group.isEmpty()) return;

        // Calculate center position (average of all items in group)
        Vector3d avgPos = Vector3d.ZERO;
        for (ItemEntity item : group) {
            avgPos = avgPos.add(item.getPositionVec());
        }
        avgPos = avgPos.scale(1.0 / group.size());

        // Project to screen
        Vector3d vec = avgPos.add(0, group.get(0).getHeight() + 0.5, 0);
        AxisAlignedBB box = getEntityBox(group.get(0), vec);
        Vector3d[] corners = {
                new Vector3d(box.minX, box.minY, box.minZ),
                new Vector3d(box.minX, box.maxY, box.minZ),
                new Vector3d(box.maxX, box.minY, box.minZ),
                new Vector3d(box.maxX, box.maxY, box.minZ),
                new Vector3d(box.minX, box.minY, box.maxZ),
                new Vector3d(box.minX, box.maxY, box.maxZ),
                new Vector3d(box.maxX, box.minY, box.maxZ),
                new Vector3d(box.maxX, box.maxY, box.maxZ)
        };

        Vector2f topCenter = null;
        for (Vector3d corner : corners) {
            Vector2f screen = Project.project2D(corner);
            if (screen == null) return;
            if (topCenter == null || screen.y < topCenter.y) {
                topCenter = screen;
            }
        }
        if (topCenter == null) return;

        float centerX = topCenter.x;
        float topY = topCenter.y;

        // Build list of item texts
        List<ITextComponent> itemTexts = new ArrayList<>();
        float maxWidth = 0;

        for (ItemEntity itemEntity : group) {
            ItemStack stack = itemEntity.getItem();
            ITextComponent tag;
            if (holyWorld.getValue()) {
                tag = new StringTextComponent(stack.getTranslationKey().replace("item.minecraft.", "").replace("_", " ").toUpperCase());
            } else {
                tag = new StringTextComponent("").append(stack.getDisplayName());
                if (stack.getCount() > 1) {
                    String gray = ColorFormatting.getColor(TextFormatting.GRAY.getColor());
                    tag = tag.deepCopy().appendString(gray + " " + stack.getCount() + "x");
                }
            }
            itemTexts.add(tag);
            maxWidth = Math.max(maxWidth, getWidth(tag));
        }

        // Calculate rectangle dimensions
        float lineHeight = fontHeight + 2f;
        float rectW = Math.max(maxWidth + TEXT_PAD_H * 2f, 50f);
        float rectH = (lineHeight * itemTexts.size()) + TEXT_PAD_V * 2f;
        float tagY = topY - rectH - 2f;

        // Draw grouped rectangle
        Round r = Round.of(CORNER_RADIUS);
        RenderUtil.Rounded.roundedRect(matrix, centerX - rectW / 2f, tagY, rectW + 5, rectH, bgColor(), r);
        RenderUtil.Rounded.roundedOutline(matrix, centerX - rectW / 2f, tagY, rectW + 5, rectH, 0.5f, borderColor(), r);

        // Draw each item as a line
        float currentY = tagY + TEXT_PAD_V;
        for (int i = 0; i < itemTexts.size(); i++) {
            ITextComponent text = itemTexts.get(i);
            float textW = getWidth(text);

            // Draw icon if enabled
            if (renderItemIcon.getValue()) {
                float iconSize = fontHeight;
                ItemStack stack = group.get(i).getItem();
                draw2dItemIcon(matrix, stack, centerX - rectW / 2f + TEXT_PAD_H, currentY, iconSize);
                font.drawTextComponent(matrix, text, centerX - rectW / 2f + TEXT_PAD_H + iconSize + 2f, currentY, -1, false, fontHeight);
            } else {
                font.drawTextComponent(matrix, text, centerX - textW / 2f, currentY, -1, false, fontHeight);
            }

            currentY += lineHeight;
        }
    }

    private void drawBorderedRect(MatrixStack matrix, float x, float y, float w, float h, float radius,
                                  boolean roundTL, boolean roundTR, boolean roundBL, boolean roundBR) {
        // Round constructor: LB, LT, RB, RT (Left Bottom, Left Top, Right Bottom, Right Top)
        Round round = new Round(
                roundBL ? radius : 0,  // Left Bottom
                roundTL ? radius : 0,  // Left Top
                roundBR ? radius : 0,  // Right Bottom
                roundTR ? radius : 0   // Right Top
        );
        RenderUtil.Rounded.roundedRect(matrix, x, y, w, h, bgColor(), round);
        RenderUtil.Rounded.roundedOutline(matrix, x, y, w, h, 0.5f, borderColor(), round);
    }


    private void draw2dItemIcon(MatrixStack matrix, ItemStack stack, float x, float y, float size) {
        matrix.push();
        RenderSystem.translated(x, y, 0);
        RenderSystem.scaled(size / 16f, size / 16f, 1);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        RenderSystem.scaled(16f / size, 16f / size, 1);
        RenderSystem.translated(-x, -y, 0);
        matrix.pop();
    }

    private void drawGradientDurabilityBar(MatrixStack matrix, float x, float y, float width, float height, float percent) {
        int c1 = getDurabilityColor(Math.min(percent + 0.2f, 1f));
        int c2 = getDurabilityColor(Math.max(percent - 0.2f, 0f));
        float fw = width * percent;
        int segs = Math.max(1, (int) (fw / 2));
        float sw = fw / segs;
        for (int i = 0; i < segs; i++)
            RectUtil.drawRect(matrix, x + i * sw, y, sw, height, interpolateColor(c1, c2, (float) i / segs));
    }

    private String formatDuration(int ticks) {
        return StringUtils.ticksToElapsedTime(ticks).replace("0:0", "").replace("0:", "");
    }

    private int interpolateColor(int c1, int c2, float t) {
        int r = (int) (((c1 >> 16) & 0xFF) + (((c2 >> 16) & 0xFF) - ((c1 >> 16) & 0xFF)) * t);
        int g = (int) (((c1 >> 8) & 0xFF) + (((c2 >> 8) & 0xFF) - ((c1 >> 8) & 0xFF)) * t);
        int b = (int) ((c1 & 0xFF) + ((c2 & 0xFF) - (c1 & 0xFF)) * t);
        int a = (int) (((c1 >> 24) & 0xFF) + (((c2 >> 24) & 0xFF) - ((c1 >> 24) & 0xFF)) * t);
        return ColorUtil.getColor(r, g, b, a);
    }

    private int getDurabilityColor(float p) {
        if (p > 0.75f) return ColorUtil.getColor(100, 247, 133, 255);
        if (p > 0.5f) return ColorUtil.getColor(130, 154, 96, 255);
        if (p > 0.25f) return ColorUtil.getColor(147, 90, 71, 255);
        return ColorUtil.getColor(160, 65, 61, 255);
    }

    private String formatHealthString(float health) {
        if (health > 9999) return "9999";
        String s = String.valueOf((int) health);
        return s.length() > 4 ? s.substring(0, 4) : s;
    }

    private int getArmorPriority(PlayerEntity player) {
        int p = 0;
        for (ItemStack s : player.getArmorInventoryList()) if (!s.isEmpty()) p++;
        return p;
    }

    private AxisAlignedBB getEntityBox(Entity entity, Vector3d vec) {
        double sx = entity.getBoundingBox().maxX - entity.getBoundingBox().minX;
        double sy = entity.getBoundingBox().maxY - entity.getBoundingBox().minY;
        double sz = entity.getBoundingBox().maxZ - entity.getBoundingBox().minZ;
        return new AxisAlignedBB(
                vec.x - sx / 2, vec.y, vec.z - sz / 2,
                vec.x + sx / 2, vec.y + sy + (0.2f - (entity.isSneaking() ? 0.1f : 0f)), vec.z + sz / 2);
    }

    private boolean isValidPlayer(PlayerEntity player) {
        if (!player.isAlive()) return false;
        if (mc.renderViewEntity != null && player == mc.renderViewEntity
                && mc.gameSettings.getPointOfView().firstPerson()) return false;
        return isInView(player);
    }

    private boolean isValidItem(ItemEntity entity) {
        return entity.isAlive() && isInView(entity);
    }

    public boolean isInView(Entity entity) {
        if (mc.getRenderViewEntity() == null || mc.worldRenderer.getClippinghelper() == null) return false;
        return mc.worldRenderer.getClippinghelper().isBoundingBoxInFrustum(entity.getBoundingBox()) || entity.ignoreFrustumCheck;
    }

    private Entity findClosestValidEntity() {
        Entity closest = null;
        double closestDist = Double.MAX_VALUE;
        if (checks.getValue("Игроки")) {
            for (PlayerEntity p : mc.world.getPlayers()) {
                if (isValidPlayer(p)) {
                    double d = mc.player.getDistanceSq(p);
                    if (d < closestDist) {
                        closestDist = d;
                        closest = p;
                    }
                }
            }
        }
        if (checks.getValue("Предметы")) {
            for (Entity e : mc.world.getAllEntities()) {
                if (e instanceof ItemEntity && isValidItem((ItemEntity) e)) {
                    double d = mc.player.getDistanceSq(e);
                    if (d < closestDist) {
                        closestDist = d;
                        closest = e;
                    }
                }
            }
        }
        return closest != null ? closest : mc.player;
    }

    private float getWidth(ITextComponent text) {
        return font.getWidth(text, fontSize.getValue());
    }

    /**
     * Вычисляет анимированный scale для элемента на основе расстояния до игрока
     * Чем ближе игрок, тем больше элементы (как у основного тега)
     */
    private float getElementScale(PlayerEntity player, Map<PlayerEntity, Float> cache) {
        if (mc.player == null) return 1.0f;

        double dist = mc.player.getDistanceSq(player);
        float targetScale;

        // Чем ближе игрок, тем больше scale (как у основного тега)
        if (dist < 25) {
            targetScale = ELEMENT_SCALE_MAX; // Близко = большой (1.5x)
        } else if (dist > 100) {
            targetScale = ELEMENT_SCALE_MIN; // Далеко = маленький (0.7x)
        } else {
            // Линейная интерполяция между MAX и MIN
            float t = (float) ((dist - 25) / (100 - 25));
            targetScale = ELEMENT_SCALE_MAX - (ELEMENT_SCALE_MAX - ELEMENT_SCALE_MIN) * t;
        }

        // Плавная анимация
        float currentScale = cache.getOrDefault(player, targetScale);
        float newScale = currentScale + (targetScale - currentScale) * ELEMENT_SCALE_LERP;
        cache.put(player, newScale);

        return newScale;
    }
}
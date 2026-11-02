package tech.lightclient.modules.impl.combat;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.EventClickSlot;
import tech.lightclient.client.events.impl.client.EventKey;
import tech.lightclient.client.events.impl.client.EventMouse;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.player.PlayerInventoryUtil;
import tech.lightclient.client.utils.player.swap.InvBypassMode;
import tech.lightclient.client.utils.player.swap.InventoryTasks;
import tech.lightclient.client.utils.player.swap.SwapConfig;
import tech.lightclient.client.utils.player.swap.SwapInventory;
import tech.lightclient.client.utils.render.CursorHelper;
import tech.lightclient.client.utils.render.color.ColorUtils;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BindSetting;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import tech.lightclient.ui.hud.NotificationsOverlay;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;

@FunctionAdd(name = "AutoSwap", alias = "Auto Swap", category = Category.Combat, description = "Свапает один предмет на другой по клавише")
public class AutoSwap extends Function {
    ModeSetting mod = new ModeSetting("Мод", "Дуо", "Дуо", "Трио");

    BindSetting bind = new BindSetting("Кнопка свапа", -1).setVisible(() -> mod.is("Дуо"));
    ModeSetting firstItem = new ModeSetting("Первый", "Тотем", "Шар", "Гепл", "Щит", "Тотем").setVisible(() -> mod.is("Дуо"));
    ModeSetting secondItem = new ModeSetting("Второй", "Тотем", "Шар", "Гепл", "Щит", "Тотем").setVisible(() -> mod.is("Дуо"));

    BooleanSetting separateSwaps = new BooleanSetting("Отдельные свапы", false).setVisible(() -> mod.is("Дуо"));
    BindSetting cerberusSphereBind = new BindSetting("Сфера цербера", -1).setVisible(() -> mod.is("Дуо") && separateSwaps.get());
    BindSetting vortexTalismanBind = new BindSetting("Талисман вихря", -1).setVisible(() -> mod.is("Дуо") && separateSwaps.get());

    BindSetting wheelBind = new BindSetting("Бинд колеса", -1).setVisible(() -> mod.is("Трио"));
    SliderSetting wheelSlots = new SliderSetting("Ячейки", 3f, 3f, 8f, 1f).setVisible(() -> mod.is("Трио"));

    ModeSetting swapMode = new ModeSetting("Режим свапа", "FunTime", "FunTime", "HolyWorld");

    private static final int MAX_WHEEL_SLOTS = 8;
    private static final float BASE_OUTER_RADIUS = 88f;
    private static final float BASE_INNER_RADIUS = 63f;
    private static final float SEGMENT_GAP_DEG = 18f;
    private static final float TRAJECTORY_DEADZONE = 20f;
    private static final float HOVER_HYSTERESIS_DEG = 8f;

    /** Хранит выбранный стак, а не только id предмета: все кастомные сферы - головы игрока. */
    private final ItemStack[] wheelItems = new ItemStack[MAX_WHEEL_SLOTS];

    private boolean wheelOpen;
    private int hoverIndex = -1;
    private int pendingPickSlot = -1;

    private boolean cursorUnlocked;

    private float wheelCenterX;
    private float wheelCenterY;
    private float wheelInnerRadius;
    private float wheelOuterRadius;
    private int wheelViewportWidth;
    private int wheelViewportHeight;
    private ItemStack duoFirstSphere = ItemStack.EMPTY;
    private ItemStack duoSecondSphere = ItemStack.EMPTY;
    private int lastDuoSphereIndex = -1;

    public AutoSwap() {
        addSettings(mod, firstItem, secondItem, bind, separateSwaps, cerberusSphereBind, vortexTalismanBind,
                wheelBind, wheelSlots, swapMode);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        resetDuoSphereState();
        closeWheel();
    }

    @EventHandler
    public void onKey(EventKey e) {
        if (nullCheck()) return;

        if (mod.is("Трио")) {
            if (pendingPickSlot != -1 && mc.currentScreen == null) {
                pendingPickSlot = -1;
            }

            if (e.getAction() == 1 && e.getConvertedKey() == GLFW.GLFW_KEY_ESCAPE) {
                if (wheelOpen) {
                    closeWheel();
                }
                return;
            }

            int wheelKey = wheelBind.get();
            if (wheelKey > 0 && e.getConvertedKey() == wheelKey) {
                handleWheelBindAction(e.getAction());
            }
            return;
        }

        if (e.getAction() != 1) return;

        if (separateSwaps.get()) {
            if (cerberusSphereBind.get() != -1 && e.isKeyDown(cerberusSphereBind.get())) {
                swapCerberusSphere();
                return;
            }
            if (vortexTalismanBind.get() != -1 && e.isKeyDown(vortexTalismanBind.get())) {
                swapVortexTalisman();
                return;
            }
        }

        if (!e.isKeyDown(bind.get())) return;

        Slot first = findConfiguredDuoSlot(0);
        Slot second = findConfiguredDuoSlot(1);

        int targetIndex = resolveDuoTargetIndex(first, second);
        Slot validSlot = targetIndex == 0 ? first : targetIndex == 1 ? second : null;

        if (validSlot != null) {
            rememberDuoSphereState(targetIndex, validSlot);
            NotificationsOverlay.push(Text.literal("Свапнул: ").append(validSlot.getStack().getName()), 1200, NotificationsOverlay.Kind.PICKUP, validSlot.getStack());
            swapToOffhand(validSlot);
        }
    }
    @EventHandler
    public void onMouse(EventMouse e) {
        if (nullCheck()) return;
        if (!mod.is("Трио") || !wheelOpen) return;
        if (mc.currentScreen != null) return;
        if (e.getAction() != 1) return;

        switch (e.getButton()) {
            case GLFW.GLFW_MOUSE_BUTTON_LEFT -> {
                handleWheelLeftClick();
                e.cancel();
            }
            case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> {
                handleWheelRightClick();
                e.cancel();
            }
            case GLFW.GLFW_MOUSE_BUTTON_MIDDLE -> {
                handleWheelMiddleClick();
                e.cancel();
            }
            default -> {
            }
        }
    }

    @EventHandler
    public void onRenderWheel(EventRender.Screen.Hud e) {
        if (nullCheck()) return;
        if (!mod.is("Трио") || !wheelOpen) return;
        if (mc.currentScreen != null) {
            closeWheel();
            return;
        }

        updateWheelCursorState(true);

        Renderer2D renderer = e.getRenderer();
        if (renderer == null) return;

        float guiScale = (float) mc.getWindow().getScaleFactor();
        if (guiScale <= 0f) guiScale = 1f;

        int count = getWheelSlotCount();
        float scaledCenterX = mc.getWindow().getScaledWidth() / 2f;
        float scaledCenterY = mc.getWindow().getScaledHeight() / 2f;
        float cx = scaledCenterX * guiScale;
        float cy = scaledCenterY * guiScale;
        float outerR = BASE_OUTER_RADIUS * guiScale;
        float innerR = BASE_INNER_RADIUS * guiScale;

        wheelCenterX = cx;
        wheelCenterY = cy;
        wheelInnerRadius = innerR;
        wheelOuterRadius = outerR;
        wheelViewportWidth = e.getViewportWidth();
        wheelViewportHeight = e.getViewportHeight();

        float mouseX = toWheelMouseX(e.getViewportWidth());
        float mouseY = toWheelMouseY(e.getViewportHeight());
        hoverIndex = getHoverIndex(mouseX, mouseY, cx, cy, innerR, outerR, count, hoverIndex);

        float segmentSpan = 360f / count;
        float gap = Math.min(SEGMENT_GAP_DEG, segmentSpan * 0.25f);
        float ringMid = (outerR + innerR) * 0.5f;

        for (int i = 0; i < count; i++) {
            boolean isHover = i == hoverIndex;
            int color = getWheelSegmentColor(i, count, isHover);

            float startDeg = -90f + segmentSpan * i + gap * 0.5f;
            float endDeg = -90f + segmentSpan * (i + 1) - gap * 0.5f;
            drawSector(renderer, cx, cy, innerR, outerR, startDeg, endDeg, color);
        }

        DrawContext context = LightClient.getCurrentDrawContext();
        float iconRadius = ringMid;
        float iconSize = 14f * guiScale;

        for (int i = 0; i < count; i++) {
            float midDeg = -90f + segmentSpan * (i + 0.5f);
            double midRad = Math.toRadians(midDeg);
            float ix = cx + (float) Math.cos(midRad) * iconRadius;
            float iy = cy + (float) Math.sin(midRad) * iconRadius;

            ItemStack stack = getStackForIndex(i);
            if (stack.isEmpty() || stack.getItem() == Items.AIR) {
                String plus = "+";
                int color = i == hoverIndex ? 0xFFFFFFFF : 0xA0FFFFFF;
                float plusWidth = FontRegistry.SF_MEDIUM.getWidth(plus, 10f);
                renderer.text(FontRegistry.SF_MEDIUM, ix - plusWidth * 0.5f, iy + FontRegistry.centeredBaselineOffset(FontRegistry.SF_MEDIUM, '+', 10f), 10f, plus, color);
                continue;
            }

            drawItem(renderer, context, stack, ix - iconSize * 0.5f, iy - iconSize * 0.5f, iconSize);
        }
    }
    @EventHandler
    public void onClickSlot(EventClickSlot e) {
        if (nullCheck()) return;
        if (!mod.is("Трио")) return;
        if (pendingPickSlot == -1) return;
        if (!(mc.currentScreen instanceof HandledScreen<?>)) return;
        if (e.actionType == SlotActionType.QUICK_CRAFT) return;

        var handler = mc.player.currentScreenHandler;
        if (handler == null) return;
        if (e.slotId < 0 || e.slotId >= handler.slots.size()) return;

        Slot slot = handler.slots.get(e.slotId);
        ItemStack picked = slot.getStack();
        if ((picked == null || picked.isEmpty()) && handler.getCursorStack() != null && !handler.getCursorStack().isEmpty()) {
            picked = handler.getCursorStack();
        }
        if (picked == null || picked.isEmpty() || picked.getItem() == Items.AIR) return;

        int wheelSlot = pendingPickSlot;
        pendingPickSlot = -1;
        ItemStack toStore = picked.copy();
        toStore.setCount(1);
        setWheelStack(wheelSlot, toStore);
        e.cancel();
        mc.setScreen(null);
        updateWheelCursorState(false);
    }

    private void handleWheelBindAction(int action) {
        if (action == 1) {
            if (mc.currentScreen != null || wheelOpen) return;
            wheelOpen = true;
            hoverIndex = -1;
            pendingPickSlot = -1;
            updateWheelCursorState(true);
            return;
        }

        if (action == 0) {
            performWheelSelection();
        }
    }

    private void handleWheelLeftClick() {
        if (hoverIndex < 0) return;

        if (isWheelSlotEmpty(hoverIndex)) {
            startWheelBinding(hoverIndex);
            return;
        }

        swapWheelSlot(hoverIndex);
        closeWheel();
    }

    private void handleWheelRightClick() {
        if (hoverIndex < 0) {
            closeWheel();
            return;
        }

        if (isWheelSlotEmpty(hoverIndex)) {
            startWheelBinding(hoverIndex);
            return;
        }

        clearWheelSlot(hoverIndex);
        closeWheel();
    }

    private void handleWheelMiddleClick() {
        if (hoverIndex < 0 || isWheelSlotEmpty(hoverIndex)) return;
        clearWheelSlot(hoverIndex);
    }

    private void performWheelSelection() {
        if (!wheelOpen) return;

        int selected = hoverIndex;
        closeWheel();

        if (selected < 0 || isWheelSlotEmpty(selected)) return;
        swapWheelSlot(selected);
    }

    private void startWheelBinding(int slotIndex) {
        if (mc.player == null) return;
        pendingPickSlot = slotIndex;
        wheelOpen = false;
        hoverIndex = -1;
        updateWheelCursorState(false);
        mc.setScreen(new InventoryScreen(Objects.requireNonNull(mc.player)));
    }

    private void clearWheelSlot(int index) {
        setWheelStack(index, ItemStack.EMPTY);
    }

    private void swapWheelSlot(int index) {
        ItemStack stack = getStackForIndex(index);
        if (stack.isEmpty() || stack.getItem() == Items.AIR) return;

        Slot slot = findWheelSlot(stack);
        if (slot == null) return;

        NotificationsOverlay.push(Text.literal("Свапнул: ").append(slot.getStack().getName()), 1200, NotificationsOverlay.Kind.PICKUP, slot.getStack());
        swapToOffhand(slot);
    }

    private void closeWheel() {
        wheelOpen = false;
        hoverIndex = -1;
        pendingPickSlot = -1;
        updateWheelCursorState(false);
    }
    private void updateWheelCursorState(boolean shouldBeUnlocked) {
        if (mc == null || mc.mouse == null) return;

        if (shouldBeUnlocked) {
            if (!cursorUnlocked) {
                CursorHelper.ensureVisible();
                CursorHelper.setArrow();
                cursorUnlocked = true;
            } else {
                CursorHelper.setArrow();
            }
            return;
        }

        if (cursorUnlocked) {
            if (mc.currentScreen == null) {
                mc.mouse.lockCursor();
            }
            cursorUnlocked = false;
        }
    }

    private float toWheelMouseX(int viewportWidth) {
        int windowWidth = mc.getWindow().getWidth();
        if (windowWidth <= 0) return 0f;
        return (float) (mc.mouse.getX() * viewportWidth / windowWidth);
    }

    private float toWheelMouseY(int viewportHeight) {
        int windowHeight = mc.getWindow().getHeight();
        if (windowHeight <= 0) return 0f;
        return (float) (mc.mouse.getY() * viewportHeight / windowHeight);
    }

    private int getWheelSlotCount() {
        return MathHelper.clamp(Math.round(wheelSlots.get()), 3, MAX_WHEEL_SLOTS);
    }

    private void setWheelStack(int index, ItemStack stack) {
        if (index < 0 || index >= MAX_WHEEL_SLOTS) return;
        if (stack == null || stack.isEmpty() || stack.getItem() == Items.AIR) {
            wheelItems[index] = ItemStack.EMPTY;
            return;
        }

        ItemStack saved = stack.copy();
        saved.setCount(1);
        wheelItems[index] = saved;
    }

    private ItemStack getStackForIndex(int index) {
        if (index < 0 || index >= MAX_WHEEL_SLOTS) return ItemStack.EMPTY;
        ItemStack stack = wheelItems[index];
        return stack == null ? ItemStack.EMPTY : stack;
    }

    private Slot findWheelSlot(ItemStack selected) {
        if (mc.player == null || selected == null || selected.isEmpty()) return null;

        return PlayerInventoryUtil.slots()
                .filter(slot -> slot.id != 45 && !slot.getStack().isEmpty())
                .filter(slot -> matchesWheelStack(slot.getStack(), selected))
                .max(Comparator.comparing(slot -> slot.getStack().hasEnchantments()))
                .orElse(null);
    }

    private boolean matchesWheelStack(ItemStack actual, ItemStack selected) {
        if (actual.isEmpty() || selected.isEmpty() || actual.getItem() != selected.getItem()) {
            return false;
        }

        return !selected.isOf(Items.PLAYER_HEAD) || ItemStack.areItemsAndComponentsEqual(actual, selected);
    }

    private boolean isWheelSlotEmpty(int index) {
        ItemStack stack = getStackForIndex(index);
        return stack.isEmpty() || stack.getItem() == Items.AIR;
    }

    private int getHoverIndex(float mouseX, float mouseY, float cx, float cy, float innerR, float outerR, int count, int previousIndex) {
        if (outerR <= 0f) return -1;

        float dx = mouseX - cx;
        float dy = mouseY - cy;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        float deadzone = Math.max(TRAJECTORY_DEADZONE, innerR * 0.32f);
        float maxReach = Math.max(outerR + 240f, Math.max(wheelViewportWidth, wheelViewportHeight) * 0.55f);
        if (dist < deadzone || dist > maxReach) return -1;

        double ang = Math.atan2(dy, dx) + Math.PI / 2d;
        if (ang < 0) ang += Math.PI * 2d;
        ang %= Math.PI * 2d;

        float angleDeg = (float) Math.toDegrees(ang);
        float segmentSpan = 360f / count;
        int idx = (int) Math.floor((ang / (Math.PI * 2d)) * count);
        if (idx < 0 || idx >= count) return -1;

        if (previousIndex >= 0 && previousIndex < count) {
            float previousCenter = segmentCenterDeg(previousIndex, segmentSpan);
            float diff = angleDistanceDeg(angleDeg, previousCenter);
            if (diff <= segmentSpan * 0.5f + HOVER_HYSTERESIS_DEG) {
                return previousIndex;
            }
        }

        return idx;
    }

    private float segmentCenterDeg(int index, float segmentSpan) {
        return normalizeDeg(segmentSpan * (index + 0.5f));
    }

    private float angleDistanceDeg(float a, float b) {
        float diff = Math.abs(normalizeDeg(a) - normalizeDeg(b));
        return diff > 180f ? 360f - diff : diff;
    }

    private float normalizeDeg(float angle) {
        float normalized = angle % 360f;
        return normalized < 0f ? normalized + 360f : normalized;
    }
    private void drawSector(Renderer2D renderer, float cx, float cy, float innerR, float outerR,
                            float startDeg, float endDeg, int color) {
        float spanDeg = endDeg - startDeg;
        if (spanDeg <= 0f) return;

        float height = outerR - innerR;
        if (height <= 0f) return;

        float arcLen = (float) (Math.toRadians(spanDeg) * outerR);
        int steps = Math.max(2, Math.round(arcLen / 2f));
        float stepDeg = spanDeg / steps;
        float sliceWidth = (float) (Math.toRadians(stepDeg) * outerR) + 1.5f;

        renderer.pushTranslation(cx, cy);
        try {
            for (int i = 0; i < steps; i++) {
                float midDeg = startDeg + stepDeg * (i + 0.5f);
                renderer.pushRotation(midDeg - 90f);
                try {
                    renderer.rect(-sliceWidth * 0.5f, innerR, sliceWidth, height, color);
                } finally {
                    renderer.popRotation();
                }
            }
        } finally {
            renderer.popTransform();
        }
    }

    private int getWheelSegmentColor(int index, int count, boolean hovered) {
        int alpha = hovered ? 210 : 140;

        if (count == 3) {
            return switch (index) {
                case 0 -> ColorUtils.rgba(239, 216, 56, alpha);
                case 1 -> ColorUtils.rgba(235, 236, 239, hovered ? 168 : 108);
                default -> ColorUtils.rgba(225, 130, 140, alpha);
            };
        }

        int[][] palette = {
                {239, 216, 56},
                {235, 236, 239},
                {225, 130, 140},
                {101, 212, 239},
                {111, 224, 175},
                {246, 172, 79},
                {210, 151, 238},
                {244, 122, 122}
        };
        int[] rgb = palette[index % palette.length];
        return ColorUtils.rgba(rgb[0], rgb[1], rgb[2], alpha);
    }

    private void drawItem(Renderer2D renderer, DrawContext context, ItemStack stack, float x, float y, float size) {
        if (renderer == null || context == null || stack == null || stack.isEmpty()) return;

        var transform = renderer.getTransformStack().current();
        float absX = x;
        float absY = y;
        float absSize = size;

        if (transform != null && transform.length >= 6) {
            float scaleX = (float) Math.sqrt(transform[0] * transform[0] + transform[3] * transform[3]);
            float scaleY = (float) Math.sqrt(transform[1] * transform[1] + transform[4] * transform[4]);
            absX = transform[0] * x + transform[1] * y + transform[2];
            absY = transform[3] * x + transform[4] * y + transform[5];
            absSize = size * Math.min(scaleX, scaleY);
        }
        if (!Float.isFinite(absX) || !Float.isFinite(absY) || !Float.isFinite(absSize) || absSize <= 0f) return;

        double windowScale = mc.getWindow().getScaleFactor();
        if (windowScale <= 0.0) return;

        float guiX = (float) (absX / windowScale);
        float guiY = (float) (absY / windowScale);
        float guiScale = (float) (absSize / (16f * windowScale));
        if (!Float.isFinite(guiX) || !Float.isFinite(guiY) || !Float.isFinite(guiScale) || guiScale <= 0f) return;

        renderer.flush();
        var matrices = context.getMatrices();
        matrices.pushMatrix();
        try {
            matrices.translate(guiX, guiY);
            matrices.scale(guiScale, guiScale);
            context.drawItem(stack, 0, 0);
        } catch (Throwable ignored) {
        } finally {
            matrices.popMatrix();
            renderer.resetPipelineState();
        }
    }

    private void swapToOffhand(Slot slot) {
        if (slot == null || mc.player == null) return;
        if (swapMode.is("HolyWorld")) {
            SwapConfig.invBypassMode = InvBypassMode.HOLY_WORLD;
            SwapConfig.inventoryStopMoving = false;
            if (!InventoryTasks.isRealCompleted(AutoSwap.class)) return;
            InventoryTasks.addTask(AutoSwap.class, () -> SwapInventory.swapHand(slot, Hand.OFF_HAND, true));
        } else {
            PlayerInventoryUtil.swapHandPhased(slot, Hand.OFF_HAND, true);
        }
    }

    private void swapCerberusSphere() {
        Slot slot = findCerberusSphere();
        if (slot == null) return;
        NotificationsOverlay.push(Text.literal("Свапнул: ").append(slot.getStack().getName()), 1200, NotificationsOverlay.Kind.PICKUP, slot.getStack());
        swapToOffhand(slot);
    }

    private void swapVortexTalisman() {
        Slot slot = findVortexTalisman();
        if (slot == null) return;
        NotificationsOverlay.push(Text.literal("Свапнул: ").append(slot.getStack().getName()), 1200, NotificationsOverlay.Kind.PICKUP, slot.getStack());
        swapToOffhand(slot);
    }

    private Slot findCerberusSphere() {
        if (mc.player == null) return null;
        for (Slot slot : mc.player.currentScreenHandler.slots) {
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;
            String name = stripFormatting(stack.getName().getString()).toLowerCase();
            if (name.contains("сфера цербера") || name.contains("цербера") || name.contains("cerberus")) return slot;
        }
        return null;
    }

    private Slot findVortexTalisman() {
        if (mc.player == null) return null;
        for (Slot slot : mc.player.currentScreenHandler.slots) {
            if (isVortexTalisman(slot.getStack())) return slot;
        }
        return null;
    }

    private boolean isVortexTalisman(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        AttributeModifiersComponent modifiers = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (modifiers == null) return false;

        boolean hasHealth = false, hasSpeed = false, hasAttackSpeed = false;
        for (AttributeModifiersComponent.Entry entry : modifiers.modifiers()) {
            RegistryEntry<EntityAttribute> attr = entry.attribute();
            double value = entry.modifier().value();
            if (attr.matches(EntityAttributes.MAX_HEALTH) && Math.abs(value - 2.0) < 0.01) hasHealth = true;
            if (attr.matches(EntityAttributes.MOVEMENT_SPEED) && Math.abs(value - 0.15) < 0.01) hasSpeed = true;
            if (attr.matches(EntityAttributes.ATTACK_SPEED) && Math.abs(value - 0.15) < 0.01) hasAttackSpeed = true;
        }
        return hasHealth && hasSpeed && hasAttackSpeed;
    }

    private String stripFormatting(String s) {
        if (s == null || s.isEmpty()) return "";
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\u00A7') {
                i++;
                continue;
            }
            out.append(c);
        }
        return out.toString();
    }
    private void resetDuoSphereState() {
        duoFirstSphere = ItemStack.EMPTY;
        duoSecondSphere = ItemStack.EMPTY;
        lastDuoSphereIndex = -1;
    }

    private Slot findConfiguredDuoSlot(int index) {
        ModeSetting setting = index == 0 ? firstItem : secondItem;
        Item item = getItemByType(setting.get());
        if (item != Items.PLAYER_HEAD) {
            return PlayerInventoryUtil.getSlot(item,
                    Comparator.comparing(s -> s.getStack().hasEnchantments()), s -> s.id != 45);
        }
        return findConfiguredSphereSlot(index);
    }

    private Slot findConfiguredSphereSlot(int index) {
        ItemStack remembered = getRememberedDuoSphere(index);
        ItemStack otherRemembered = getRememberedDuoSphere(1 - index);

        if (!remembered.isEmpty()) {
            Slot exact = findSphereSlot(stack -> ItemStack.areItemsAndComponentsEqual(stack, remembered));
            if (exact != null) {
                return exact;
            }
        }

        Slot alternative = findSphereSlot(stack -> otherRemembered.isEmpty()
                || !ItemStack.areItemsAndComponentsEqual(stack, otherRemembered));
        if (alternative != null) {
            return alternative;
        }

        return findSphereSlot(stack -> true);
    }

    private Slot findSphereSlot(Predicate<ItemStack> predicate) {
        if (mc.player == null) {
            return null;
        }

        return mc.player.currentScreenHandler.slots.stream()
                .filter(slot -> slot.id != 45)
                .filter(slot -> isPrimarySphereCandidate(slot.getStack()))
                .filter(slot -> predicate.test(slot.getStack()))
                .max(Comparator.comparing(slot -> slot.getStack().hasEnchantments()))
                .orElse(null);
    }

    private int resolveDuoTargetIndex(Slot first, Slot second) {
        ItemStack offhand = mc.player.getOffHandStack();
        boolean offhandIsFirst = matchesConfiguredDuoEntry(offhand, 0);
        boolean offhandIsSecond = matchesConfiguredDuoEntry(offhand, 1);

        if (offhandIsFirst && second != null) {
            return 1;
        }
        if (offhandIsSecond && first != null) {
            return 0;
        }

        if (lastDuoSphereIndex == 0 && second != null) {
            return 1;
        }
        if (lastDuoSphereIndex == 1 && first != null) {
            return 0;
        }

        if (first != null) {
            return 0;
        }
        if (second != null) {
            return 1;
        }
        return -1;
    }

    private boolean matchesConfiguredDuoEntry(ItemStack stack, int index) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        ModeSetting setting = index == 0 ? firstItem : secondItem;
        Item item = getItemByType(setting.get());
        if (item != Items.PLAYER_HEAD) {
            return stack.isOf(item);
        }

        ItemStack remembered = getRememberedDuoSphere(index);
        if (!remembered.isEmpty()) {
            return ItemStack.areItemsAndComponentsEqual(stack, remembered);
        }

        return isPrimarySphereCandidate(stack);
    }

    private void rememberDuoSphereState(int targetIndex, Slot targetSlot) {
        if (targetIndex < 0 || mc.player == null || targetSlot == null) {
            return;
        }

        int otherIndex = 1 - targetIndex;
        if (isSphereSetting(otherIndex) && isPrimarySphereCandidate(mc.player.getOffHandStack())) {
            setRememberedDuoSphere(otherIndex, mc.player.getOffHandStack());
        }

        if (isSphereSetting(targetIndex) && isPrimarySphereCandidate(targetSlot.getStack())) {
            setRememberedDuoSphere(targetIndex, targetSlot.getStack());
            lastDuoSphereIndex = targetIndex;
        }
    }

    private boolean isSphereSetting(int index) {
        ModeSetting setting = index == 0 ? firstItem : secondItem;
        return getItemByType(setting.get()) == Items.PLAYER_HEAD;
    }

    private ItemStack getRememberedDuoSphere(int index) {
        return index == 0 ? duoFirstSphere : duoSecondSphere;
    }

    private void setRememberedDuoSphere(int index, ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setCount(1);
        if (index == 0) {
            duoFirstSphere = copy;
        } else {
            duoSecondSphere = copy;
        }
    }

    private boolean isPrimarySphereCandidate(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.isOf(Items.PLAYER_HEAD)) {
            return false;
        }

        return !separateSwaps.get() || !isCerberusSphere(stack);
    }

    private static final String CERBERUS_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA5NWE3ZmQ5MGRhYTFiYmU3MDY5MDg5NzQwZTA1ZDBiZmM2NjI5NmVlM2M0MGVlNzFhNGUwYTY2MTZiMmJiYyJ9fX0=";

    private boolean isCerberusSphere(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.isOf(Items.PLAYER_HEAD)) return false;
        NbtComponent custom = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (custom != null) {
            NbtCompound nbt = custom.copyNbt();
            boolean nbtMatch = nbt.getCompound("SkullOwner")
                    .flatMap(c -> c.getCompound("Properties"))
                    .flatMap(c -> c.getList("textures"))
                    .filter(list -> !list.isEmpty())
                    .flatMap(list -> list.getCompound(0))
                    .flatMap(c -> c.getString("Value"))
                    .map(v -> v.equals(CERBERUS_TEXTURE))
                    .orElse(false);
            if (nbtMatch) return true;
        }
        String name = stripFormatting(stack.getName().getString()).toLowerCase();
        return name.contains("цербер") || name.contains("cerberus");
    }

    private Item getItemByType(String itemType) {
        return switch (itemType) {
            case "Тотем" -> Items.TOTEM_OF_UNDYING;
            case "Шар" -> Items.PLAYER_HEAD;
            case "Гепл" -> Items.GOLDEN_APPLE;
            case "Щит" -> Items.SHIELD;
            default -> Items.AIR;
        };
    }
}

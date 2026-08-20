package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.math.vector.Matrix4f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.glfw.GLFW;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.annotations.Customize;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.input.KeyboardPressEvent;
import org.privates.client.managers.events.input.MousePressEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.screen.notification.Notification;
import org.privates.client.screen.notification.NotificationType;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.InvUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.font.Fonts;

import java.util.Arrays;

@Category3
@Customize
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoSwap", category = Category.COMBAT, description = "Авто свапает предмет при нажатии клавиши")
public class AutoSwap extends Module {
    private final BindSetting keyToSwap = new BindSetting(this, "Кнопка");
    private final ItemSetting item1 = new ItemSetting(this, "Предмет 1", Items.TOTEM_OF_UNDYING);
    private final ItemSetting item2 = new ItemSetting(this, "Предмет 2", Items.PLAYER_HEAD);
    private final ModeSetting itemMode = new ModeSetting(this, "Количество предметов", "2 предмета", "3 предмета").set("2 предмета");
    private final ModeSetting mode = new ModeSetting(this, "Режим", "Обычный", "HolyWorld", "Легитный").set("Обычный");
    private final BindSetting tripleGuiKey = new BindSetting(this, "Открыть GUI").setVisible(() -> itemMode.getValue().equals("3 предмета"));
    private final StringSetting priorityName = new StringSetting(this, "Приоритет по имени", "");
    private final BooleanSetting onlyHotbar = new BooleanSetting(this, "Только хотбар", false).setVisible(() -> mode.getValue().equals("Легитный"));
    private final BooleanSetting openInventory = new BooleanSetting(this, "Открывать инвентарь", false).setVisible(() -> mode.getValue().equals("Легитный"));
    private final BooleanSetting stopForwardMovement = new BooleanSetting(this, "Останавливать движение вперед", false);
    private final BooleanSetting pressShift = new BooleanSetting(this, "Нажимать Shift", false).setVisible(() -> mode.getValue().equals("Легитный"));
    private final BooleanSetting resetSprint = new BooleanSetting(this, "Сбрасывать спринт", false);

    private final SliderSetting delayBeforeSwap = new SliderSetting(this, "Задержка до свапа (мс)", 150, 0, 300, 5).setVisible(() -> !mode.getValue().equals("Обычный"));
    private final SliderSetting delayAfterSwap = new SliderSetting(this, "Задержка после свапа (мс)", 150, 0, 300, 5).setVisible(() -> !mode.getValue().equals("Обычный"));

    boolean isBlockingMovement = false;
    private long blockEndTime = 0;
    private long swapTime = 0;
    private boolean swapQueued = false;
    private boolean inventoryOpened = false;
    private boolean shiftPressed = false;
    private boolean wasForwardPressed = false;
    private boolean wasSprintingBeforeBlock = false;
    private Slot lastSwappedSlot = null;

    private final ItemStack[] tripleItems = new ItemStack[3];
    private boolean wasTripleGuiKeyPressed = false;
    private int selectedSlotForItem = -1;
    private ItemStack queuedTripleItem = null;

    public static AutoSwap getInstance() {
        return Instance.get(AutoSwap.class);
    }

    public boolean isTripleGuiOpen() {
        return mc.currentScreen instanceof TripleSwapGui || mc.currentScreen instanceof InventorySelectionScreen;
    }

    @EventHandler
    public void onEvent(KeyboardPressEvent e) {
        if (e.isKey(keyToSwap.getValue())) {
            if (itemMode.getValue().equals("2 предмета")) {
                handleEventWithTimer();
            }
        }
        if (itemMode.getValue().equals("3 предмета") && e.isKey(tripleGuiKey.getValue())) {
            if (!wasTripleGuiKeyPressed && mc.currentScreen == null) {
                mc.displayScreen(new TripleSwapGui());
            }
            wasTripleGuiKeyPressed = true;
        } else {
            wasTripleGuiKeyPressed = false;
        }
    }

    @EventHandler
    public void onEvent(MousePressEvent e) {
        int bindValue = keyToSwap.getValue();
        if (bindValue >= 500 && e.isKey(bindValue - 500)) {
            if (itemMode.getValue().equals("2 предмета")) {
                handleEventWithTimer();
            }
        }
        int tripleBindValue = tripleGuiKey.getValue();
        if (itemMode.getValue().equals("3 предмета") && tripleBindValue >= 500 && e.isKey(tripleBindValue - 500)) {
            if (!wasTripleGuiKeyPressed && mc.currentScreen == null) {
                mc.displayScreen(new TripleSwapGui());
            }
            wasTripleGuiKeyPressed = true;
        } else if (tripleBindValue < 500) {
            wasTripleGuiKeyPressed = false;
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (Minecraft.getInstance().currentScreen instanceof ChatScreen) {
            return;
        }

        refreshTripleGuiBindState();

        if (mc.currentScreen instanceof InventorySelectionScreen && selectedSlotForItem >= 0) {
            return;
        }

        if (isBlockingMovement) {
            long now = System.currentTimeMillis();

            if (swapQueued && now >= swapTime) {
                if (queuedTripleItem != null) {
                    swapToItemStackDirect(queuedTripleItem);
                    queuedTripleItem = null;
                } else {
                    handleEvent();
                }
                swapQueued = false;
            }

            if (now >= blockEndTime) {
                isBlockingMovement = false;
                restoreMovement();
            }
        }
    }

    private void refreshTripleGuiBindState() {
        if (!itemMode.getValue().equals("3 РїСЂРµРґРјРµС‚Р°")) {
            wasTripleGuiKeyPressed = false;
            return;
        }

        int bind = tripleGuiKey.getValue();
        if (bind <= 0) {
            wasTripleGuiKeyPressed = false;
            return;
        }

        long handle = mc.getMainWindow().getHandle();
        boolean pressedNow;
        if (bind >= 500) {
            pressedNow = GLFW.glfwGetMouseButton(handle, bind - 500) == GLFW.GLFW_PRESS;
        } else {
            pressedNow = InputMappings.isKeyDown(handle, bind);
        }

        if (!pressedNow) {
            wasTripleGuiKeyPressed = false;
        }
    }

    private void handleEventWithTimer() {
        String currentMode = mode.getValue();

        if (currentMode.equals("Обычный")) {
            if (stopForwardMovement.getValue() || resetSprint.getValue()) {
                handleTimedModeWithMovementBlock();
            } else {
                handleEvent();
            }
            return;
        }

        if (currentMode.equals("HolyWorld")) {
            handleTimedMode();
            return;
        }

        if (currentMode.equals("Легитный")) {
            handleLegitMode();
        }
    }

    private void handleTimedModeWithMovementBlock() {
        if (resetSprint.getValue()) {
            wasSprintingBeforeBlock = mc.player.isSprinting();
            mc.gameSettings.keyBindBack.setPressed(true);
            mc.player.setSprinting(false);
        }

        if (stopForwardMovement.getValue()) {
            wasForwardPressed = mc.gameSettings.keyBindForward.isKeyDown();
            mc.gameSettings.keyBindForward.setPressed(false);
        }

        isBlockingMovement = true;
        long now = System.currentTimeMillis();
        swapTime = now + 50;
        blockEndTime = now + 100;
        swapQueued = true;
    }

    private void handleTimedMode() {
        if (resetSprint.getValue()) {
            wasSprintingBeforeBlock = mc.player.isSprinting();
            mc.gameSettings.keyBindBack.setPressed(true);
            mc.player.setSprinting(false);
        }

        if (stopForwardMovement.getValue()) {
            wasForwardPressed = mc.gameSettings.keyBindForward.isKeyDown();
            mc.gameSettings.keyBindForward.setPressed(false);
        }

        isBlockingMovement = true;
        long now = System.currentTimeMillis();
        swapTime = now + delayBeforeSwap.getValue().intValue();
        blockEndTime = now + delayBeforeSwap.getValue().intValue() + delayAfterSwap.getValue().intValue();
        swapQueued = true;
    }

    private void handleLegitMode() {
        if (resetSprint.getValue()) {
            wasSprintingBeforeBlock = mc.player.isSprinting();
            mc.gameSettings.keyBindBack.setPressed(true);
            mc.player.setSprinting(false);
        }

        if (stopForwardMovement.getValue()) {
            wasForwardPressed = mc.gameSettings.keyBindForward.isKeyDown();
            mc.gameSettings.keyBindForward.setPressed(false);
        }

        if (pressShift.getValue()) {
            mc.gameSettings.keyBindSneak.setPressed(true);
            shiftPressed = true;
        }

        if (openInventory.getValue() && mc.currentScreen == null) {
            mc.displayScreen(new InventoryScreen(mc.player));
            inventoryOpened = true;
        }

        isBlockingMovement = true;
        long now = System.currentTimeMillis();
        int randomDelay = (int) (Math.random() * 100);
        swapTime = now + delayBeforeSwap.getValue().intValue() + randomDelay;
        blockEndTime = now + delayBeforeSwap.getValue().intValue() + randomDelay + delayAfterSwap.getValue().intValue();
        swapQueued = true;
    }

    private void handleTripleLegitMode(ItemStack itemStack) {
        if (resetSprint.getValue()) {
            wasSprintingBeforeBlock = mc.player.isSprinting();
            mc.gameSettings.keyBindBack.setPressed(true);
            mc.player.setSprinting(false);
        }

        if (stopForwardMovement.getValue()) {
            wasForwardPressed = mc.gameSettings.keyBindForward.isKeyDown();
            mc.gameSettings.keyBindForward.setPressed(false);
        }

        if (pressShift.getValue()) {
            mc.gameSettings.keyBindSneak.setPressed(true);
            shiftPressed = true;
        }

        if (openInventory.getValue() && !(mc.currentScreen instanceof TripleSwapGui)) {
            mc.displayScreen(new InventoryScreen(mc.player));
            inventoryOpened = true;
        }

        isBlockingMovement = true;
        queuedTripleItem = itemStack;
        long now = System.currentTimeMillis();
        int randomDelay = (int) (Math.random() * 100);
        swapTime = now + delayBeforeSwap.getValue().intValue() + randomDelay;
        blockEndTime = now + delayBeforeSwap.getValue().intValue() + randomDelay + delayAfterSwap.getValue().intValue();
        swapQueued = true;
    }

    private void restoreMovement() {
        if (resetSprint.getValue()) {
            mc.gameSettings.keyBindBack.setPressed(false);
            if (wasSprintingBeforeBlock) {
                mc.player.setSprinting(true);
                wasSprintingBeforeBlock = false;
            }
        }

        if (stopForwardMovement.getValue() && wasForwardPressed) {
            mc.gameSettings.keyBindForward.setPressed(true);
            wasForwardPressed = false;
        }

        if (shiftPressed) {
            mc.gameSettings.keyBindSneak.setPressed(false);
            shiftPressed = false;
        }

        if (inventoryOpened && mc.currentScreen instanceof InventoryScreen) {
            mc.displayScreen(null);
            inventoryOpened = false;
        }
    }

    private void handleEvent() {
        ItemStack offhandStack = mc.player.getHeldItemOffhand();
        Item offhandItem = offhandStack.getItem();
        Item targetItem1 = item1.getValue();
        Item targetItem2 = item2.getValue();

        boolean item1EqualsItem2 = targetItem1 == targetItem2 && targetItem1 != Items.AIR;

        Slot first = null;
        if (targetItem1 != Items.AIR) {
            first = getSlotWithPriority(targetItem1, null);
        }

        Slot second = null;
        if (targetItem2 != Items.AIR) {
            second = getSlotWithPriority(targetItem2, null);
        }

        if (first == null && second == null) {
            return;
        }

        if (mode.getValue().equals("Легитный") && onlyHotbar.getValue()) {
            if (targetItem1 != Items.AIR) first = getHotbarSlot(targetItem1, null);
            if (targetItem2 != Items.AIR) second = getHotbarSlot(targetItem2, null);

            if (first == null && second == null) {
                return;
            }
        }

        Slot validSlot = null;

        if (item1EqualsItem2) {
            if (offhandItem == targetItem1) {
                if (lastSwappedSlot == null) {
                    validSlot = first;
                } else {
                    if (first != null && !isSameSlot(first, lastSwappedSlot)) {
                        validSlot = first;
                    } else if (second != null && !isSameSlot(second, lastSwappedSlot)) {
                        validSlot = second;
                    } else {
                        validSlot = first != null ? first : second;
                    }
                }
            } else {
                validSlot = first != null ? first : second;
            }
        } else {
            validSlot = (first != null && offhandItem != first.getStack().getItem()) ? first :
                    (second != null && offhandItem != second.getStack().getItem()) ? second : null;
        }

        if (validSlot == null) {
            return;
        }

        if (!item1EqualsItem2 && offhandItem == validSlot.getStack().getItem()) {
            return;
        }

        ItemStack targetStack = validSlot.getStack().copy();

        InvUtil.swapHand(validSlot, Hand.OFF_HAND, false);

        lastSwappedSlot = validSlot;

        String itemName = targetStack.getDisplayName().getString();

        Excellent.inst().notifManager().add(
                new Notification(NotificationType.SWAP, itemName, 1500, targetStack)
        );
    }

    public void swapToItem(ItemStack itemStack) {
        String currentMode = mode.getValue();

        if (currentMode.equals("Обычный")) {
            if (stopForwardMovement.getValue() || resetSprint.getValue()) {
                if (mc.currentScreen instanceof TripleSwapGui) {
                    mc.displayScreen(null);
                }
                handleTimedModeWithMovementBlock();
                queuedTripleItem = itemStack;
                swapQueued = true;
            } else {
                if (mc.currentScreen instanceof TripleSwapGui) {
                    mc.displayScreen(null);
                }
                swapToItemStackDirect(itemStack);
            }
            return;
        }

        if (currentMode.equals("HolyWorld")) {
            if (mc.currentScreen instanceof TripleSwapGui) {
                mc.displayScreen(null);
            }

            if (resetSprint.getValue()) {
                wasSprintingBeforeBlock = mc.player.isSprinting();
                mc.gameSettings.keyBindBack.setPressed(true);
                mc.player.setSprinting(false);
            }

            if (stopForwardMovement.getValue()) {
                wasForwardPressed = mc.gameSettings.keyBindForward.isKeyDown();
                mc.gameSettings.keyBindForward.setPressed(false);
            }

            isBlockingMovement = true;
            queuedTripleItem = itemStack;
            long now = System.currentTimeMillis();
            swapTime = now + delayBeforeSwap.getValue().intValue();
            blockEndTime = now + delayBeforeSwap.getValue().intValue() + delayAfterSwap.getValue().intValue();
            swapQueued = true;
            return;
        }

        if (currentMode.equals("Легитный")) {
            if (mc.currentScreen instanceof TripleSwapGui) {
                mc.displayScreen(null);
            }
            handleTripleLegitMode(itemStack);
        }
    }

    private void swapToItemStackDirect(ItemStack targetItemStack) {
        Slot slot = findMatchingSlot(targetItemStack);
        if (slot != null) {
            ItemStack swappedStack = slot.getStack().copy();

            InvUtil.swapHand(slot, Hand.OFF_HAND, false);

            String itemName = swappedStack.getDisplayName().getString();

            Excellent.inst().notifManager().add(
                    new Notification(NotificationType.SWAP, itemName, 1500, swappedStack)
            );
        }
    }

    private Slot findMatchingSlot(ItemStack targetStack) {
        if (targetStack == null || targetStack.isEmpty()) {
            return null;
        }

        for (int i = 0; i < mc.player.container.inventorySlots.size(); i++) {
            Slot slot = mc.player.container.inventorySlots.get(i);
            ItemStack slotStack = slot.getStack();

            if (!slotStack.isEmpty() && ItemStack.areItemStacksEqual(slotStack, targetStack)) {
                return slot;
            }
        }
        return null;
    }

    private Slot getSlotWithPriority(Item item, Slot excludeSlot) {
        if (priorityName.getValue().isEmpty()) {
            return getSlotExcluding(item, excludeSlot);
        }

        for (int i = 0; i < mc.player.container.inventorySlots.size(); i++) {
            Slot slot = mc.player.container.inventorySlots.get(i);
            if (excludeSlot != null && isSameSlot(slot, excludeSlot)) {
                continue;
            }
            if (slot.getStack().getItem() == item) {
                String displayName = TextFormatting.removeFormatting(slot.getStack().getDisplayName().getString());
                if (displayName.toLowerCase().contains(priorityName.getValue().toLowerCase())) {
                    return slot;
                }
            }
        }

        return getSlotExcluding(item, excludeSlot);
    }

    private Slot getSlotExcluding(Item item, Slot excludeSlot) {
        for (int i = 0; i < mc.player.container.inventorySlots.size(); i++) {
            Slot slot = mc.player.container.inventorySlots.get(i);
            if (excludeSlot != null && isSameSlot(slot, excludeSlot)) {
                continue;
            }
            if (slot.getStack().getItem() == item) {
                return slot;
            }
        }
        return null;
    }

    private Slot getHotbarSlot(Item item, Slot excludeSlot) {
        for (int i = 0; i < 9; i++) {
            Slot slot = mc.player.container.inventorySlots.get(i + 36);
            if (excludeSlot != null && isSameSlot(slot, excludeSlot)) {
                continue;
            }
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return slot;
            }
        }
        return null;
    }

    private boolean isSameSlot(Slot slot1, Slot slot2) {
        if (slot1 == null || slot2 == null) return false;
        return slot1.slotNumber == slot2.slotNumber;
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        lastSwappedSlot = null;
        isBlockingMovement = false;
        wasSprintingBeforeBlock = false;
        queuedTripleItem = null;
        if (wasForwardPressed) {
            mc.gameSettings.keyBindForward.setPressed(true);
            wasForwardPressed = false;
        }
    }

    public class TripleSwapGui extends Screen {
        private final float iconSize = 16f;
        private final float outerRadius = 126f;
        private final float innerRadius = 46f;
        private final float[] hoverAnims = new float[3];
        private int hoveredSlot = -1;
        private int lastAutoSwapSlot = -1;

        private final KeyBinding[] moveKeys = {
                mc.gameSettings.keyBindForward,
                mc.gameSettings.keyBindBack,
                mc.gameSettings.keyBindLeft,
                mc.gameSettings.keyBindRight,
                mc.gameSettings.keyBindJump,
                mc.gameSettings.keyBindSneak,
                mc.gameSettings.keyBindSprint
        };

        public TripleSwapGui() {
            super(new StringTextComponent("Triple Swap"));
        }

        @Override
        public void tick() {
            super.tick();
            updateKeyBindingState();
        }

        private void updateKeyBindingState() {
            Arrays.stream(moveKeys).forEach(keyBinding -> {
                boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                keyBinding.setPressed(isKeyPressed);
            });
        }

        private int getHoveredSector(double mouseX, double mouseY, float centerX, float centerY) {
            float dx = (float) mouseX - centerX;
            float dy = (float) mouseY - centerY;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist < innerRadius || dist > outerRadius) {
                return -1;
            }

            float angle = (float) Math.atan2(dy, dx);
            float shifted = angle + (float) Math.PI / 2f;
            if (shifted < 0f) {
                shifted += (float) (Math.PI * 2f);
            }
            int sector = (int) (shifted / ((float) (Math.PI * 2f) / 3f));
            return Math.max(0, Math.min(2, sector));
        }

        private float[] getSectorIconPos(int sector, float centerX, float centerY) {
            float segment = (float) (Math.PI * 2f) / 3f;
            float start = -(float) Math.PI / 2f + segment * sector;
            float mid = start + segment / 2f;
            float iconRadius = (innerRadius + outerRadius) / 2f;
            float ix = centerX + (float) Math.cos(mid) * iconRadius - iconSize / 2f;
            float iy = centerY + (float) Math.sin(mid) * iconRadius - iconSize / 2f;
            return new float[]{ix, iy};
        }

        private void drawSector(MatrixStack stack, float centerX, float centerY, float innerR, float outerR, float startAngle, float endAngle, int color) {
            Matrix4f matrix = stack.getLast().getMatrix();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            int a = (color >> 24) & 255;
            int r = (color >> 16) & 255;
            int g = (color >> 8) & 255;
            int b = color & 255;

            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            int steps = 42;
            buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            for (int i = 0; i < steps; i++) {
                float t1 = startAngle + (endAngle - startAngle) * (i / (float) steps);
                float t2 = startAngle + (endAngle - startAngle) * ((i + 1) / (float) steps);

                float x1o = centerX + (float) Math.cos(t1) * outerR;
                float y1o = centerY + (float) Math.sin(t1) * outerR;
                float x2o = centerX + (float) Math.cos(t2) * outerR;
                float y2o = centerY + (float) Math.sin(t2) * outerR;

                float x1i = centerX + (float) Math.cos(t1) * innerR;
                float y1i = centerY + (float) Math.sin(t1) * innerR;
                float x2i = centerX + (float) Math.cos(t2) * innerR;
                float y2i = centerY + (float) Math.sin(t2) * innerR;

                buffer.pos(matrix, x1o, y1o, 0f).color(r, g, b, a).endVertex();
                buffer.pos(matrix, x2o, y2o, 0f).color(r, g, b, a).endVertex();
                buffer.pos(matrix, x2i, y2i, 0f).color(r, g, b, a).endVertex();
                buffer.pos(matrix, x1i, y1i, 0f).color(r, g, b, a).endVertex();
            }
            tessellator.draw();

            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
        }

        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            renderBackground(stack);

            float centerX = width / 2f;
            float centerY = height / 2f;
            hoveredSlot = getHoveredSector(mouseX, mouseY, centerX, centerY);
            for (int i = 0; i < 3; i++) {
                hoverAnims[i] = Mathf.fast(hoverAnims[i], hoveredSlot == i ? 1f : 0f, 12f);
            }

            int circleBg = ColorUtil.getColor(8, 8, 8, 145);
            RenderUtil.Rounded.roundedRect(stack, centerX - outerRadius, centerY - outerRadius, outerRadius * 2f, outerRadius * 2f, circleBg,
                    org.privates.client.utils.render.draw.Round.of(outerRadius));

            float segment = (float) (Math.PI * 2f) / 3f;
            for (int i = 0; i < 3; i++) {
                float start = -(float) Math.PI / 2f + i * segment;
                float end = start + segment;

                int baseColor = ColorUtil.getColor(25, 16, 18, 110);
                int hoverColor = ColorUtil.getColor(210, 70, 80, (int) (155f * hoverAnims[i]));
                drawSector(stack, centerX, centerY, innerRadius, outerRadius, start, end, baseColor);
                if (hoverAnims[i] > 0.01f) {
                    drawSector(stack, centerX, centerY, innerRadius, outerRadius, start, end, hoverColor);
                }
            }

            RenderUtil.Rounded.roundedRect(stack, centerX - innerRadius, centerY - innerRadius, innerRadius * 2f, innerRadius * 2f,
                    ColorUtil.getColor(10, 10, 10, 180), org.privates.client.utils.render.draw.Round.of(innerRadius));
            RenderUtil.Rounded.drawRoundOutline(stack, centerX - outerRadius, centerY - outerRadius, outerRadius * 2f, outerRadius * 2f,
                    outerRadius, 1.4f, ColorUtil.getColor(255, 120, 135, 90));

            for (int i = 0; i < 3; i++) {
                float[] iconPos = getSectorIconPos(i, centerX, centerY);
                float itemX = iconPos[0];
                float itemY = iconPos[1];

                if (tripleItems[i] != null && !tripleItems[i].isEmpty()) {
                    RenderSystem.pushMatrix();
                    RenderHelper.enableStandardItemLighting();
                    RenderSystem.translatef(itemX, itemY, 0);
                    mc.getItemRenderer().renderItemAndEffectIntoGUI(tripleItems[i], 0, 0);
                    RenderHelper.disableStandardItemLighting();
                    RenderSystem.popMatrix();
                } else {
                    float plusSize = 17f;
                    Fonts.SF_BOLD.draw(stack, "+",
                            itemX + iconSize / 2f - Fonts.SF_BOLD.getWidth("+", plusSize) / 2f,
                            itemY + iconSize / 2f - plusSize / 2.1f,
                            ColorUtil.multAlpha(-1, 0.62f), plusSize);
                }
            }

            if (hoveredSlot != -1 && tripleItems[hoveredSlot] != null && !tripleItems[hoveredSlot].isEmpty()) {
                ItemStack hoveredStack = tripleItems[hoveredSlot];
                String itemName = hoveredStack.getDisplayName().getString();

                float textSize = 9f;
                float textWidth = Fonts.SF_MEDIUM.getWidth(itemName, textSize);
                float textX = centerX - textWidth / 2f;
                float textY = centerY - outerRadius - 26f;

                int bgColor = ColorUtil.getColor(0, 0, 0, 170);
                RenderUtil.Rounded.roundedRect(stack, textX - 7, textY - 4, textWidth + 14, textSize + 8, bgColor,
                        org.privates.client.utils.render.draw.Round.of(5f));

                Fonts.SF_MEDIUM.draw(stack, itemName, textX, textY, -1, textSize);
            }

            if (hoveredSlot != -1 && hoverAnims[hoveredSlot] > 0.92f
                    && tripleItems[hoveredSlot] != null && !tripleItems[hoveredSlot].isEmpty()) {
                if (lastAutoSwapSlot != hoveredSlot) {
                    lastAutoSwapSlot = hoveredSlot;
                    swapToItem(tripleItems[hoveredSlot]);
                    return;
                }
            } else if (hoveredSlot == -1) {
                lastAutoSwapSlot = -1;
            }

            super.render(stack, mouseX, mouseY, partialTicks);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            float centerX = width / 2f;
            float centerY = height / 2f;
            int hovered = getHoveredSector(mouseX, mouseY, centerX, centerY);
            if (hovered != -1) {
                if (button == 0) {
                    if (tripleItems[hovered] == null || tripleItems[hovered].isEmpty()) {
                        selectedSlotForItem = hovered;
                        mc.displayScreen(new InventorySelectionScreen(hovered));
                    }
                } else if (button == 1) {
                    tripleItems[hovered] = ItemStack.EMPTY;
                }
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }

        @Override
        public void onClose() {
            super.onClose();
        }
    }

    public class InventorySelectionScreen extends InventoryScreen {
        private final int targetSlot;

        public InventorySelectionScreen(int targetSlot) {
            super(mc.player);
            this.targetSlot = targetSlot;
        }

        @Override
        protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
            if (slotIn != null && !slotIn.getStack().isEmpty() && mouseButton == 0) {
                tripleItems[targetSlot] = slotIn.getStack().copy();
                selectedSlotForItem = -1;
                mc.displayScreen(new TripleSwapGui());
                return;
            }
            super.handleMouseClick(slotIn, slotId, mouseButton, type);
        }

        @Override
        public void onClose() {
            selectedSlotForItem = -1;
            super.onClose();
        }
    }
}

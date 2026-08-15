package antileak.base.client.modules.impl.combat;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.gl.ShaderProgramKeys;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventBinding;
import antileak.base.api.events.implement.EventMoveInput;
import antileak.base.api.events.implement.EventUpdate;
import antileak.base.api.events.implement.EventRender;
import antileak.base.api.utils.notification.NotificationManager;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.impl.movement.Sprint;
import antileak.base.client.modules.impl.movement.GuiMove;
import antileak.base.client.modules.settings.implement.BindSetting;
import antileak.base.client.modules.settings.implement.BooleanSetting;
import antileak.base.client.modules.settings.implement.ModeSetting;
import antileak.base.client.modules.settings.implement.TextSetting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AutoSwap extends Module {

    public static AutoSwap INSTANCE = new AutoSwap();

    private final ModeSetting swapMode = new ModeSetting("Режим свапа", "Простой", "Простой", "Колесо");
    private final ModeSetting firstItem = new ModeSetting("Первый предмет", "Тотем", "Тотем", "Шар").visible(() -> swapMode.is("Простой"));
    private final ModeSetting secondItem = new ModeSetting("Второй предмет", "Шар", "Тотем", "Шар").visible(() -> swapMode.is("Простой"));
    private final BindSetting swapKey = new BindSetting("Кнопка свапа", -98);
    private final BooleanSetting bypassgrim = new BooleanSetting("Обходить Grim", true);
    private final BooleanSetting onlyEnchanted = new BooleanSetting("Только зачарованные", false);
    
    // Настройки колеса (3 слота) - скрытые от пользователя
    private final TextSetting slot1 = new TextSetting("Слот 1", "minecraft:air").visible(() -> false);
    private final TextSetting slot2 = new TextSetting("Слот 2", "minecraft:air").visible(() -> false);
    private final TextSetting slot3 = new TextSetting("Слот 3", "minecraft:air").visible(() -> false);
    
    // Храним реальные ItemStack с NBT-тегами для правильного отображения
    private ItemStack[] wheelStacks = new ItemStack[3];

    private int bypassTicks;
    private boolean sprintPaused;
    private int swapCooldown;
    private int targetSlot = -1;
    private boolean needSwap = false;
    
    // Для синхронизации с GuiMove
    private int syncSwapTicks = 0;
    private String pendingSwapItem = "";
    
    // Переменные для колеса
    private boolean wheelOpen = false;
    private int selectedWheelIndex = -1;
    private int pendingPickSlot = -1;
    private boolean cursorUnlocked = false;
    
    private static final float OUTER_R = 75f;
    private static final float INNER_R = 50f;
    private static final int WHEEL_SLOTS = 3; // Количество слотов в колесе

    public AutoSwap() {
        super("AutoSwap", "Быстрая смена предметов в офф-хенде", ModuleCategory.COMBAT);
        addSettings(swapMode, firstItem, secondItem, swapKey, bypassgrim, onlyEnchanted,
                slot1, slot2, slot3);
        
        // Инициализируем массив ItemStack
        wheelStacks = new ItemStack[3];
        for (int i = 0; i < wheelStacks.length; i++) {
            wheelStacks[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public void onEnable() {
        this.needSwap = false;
        this.targetSlot = -1;
        this.bypassTicks = 0;
        this.swapCooldown = 0;
        this.wheelOpen = false;
        this.selectedWheelIndex = -1;
        this.pendingPickSlot = -1;
        this.cursorUnlocked = false;
        
        // Инициализируем массив ItemStack
        if (wheelStacks == null) {
            wheelStacks = new ItemStack[3];
        }
        for (int i = 0; i < wheelStacks.length; i++) {
            if (wheelStacks[i] == null) {
                wheelStacks[i] = ItemStack.EMPTY;
            }
        }
        
        super.onEnable();
    }

    @EventLink
    public void onBinding(final EventBinding event) {
        if (mc.player == null || mc.world == null) return;
        
        // Игнорируем события, если открыт инвентарь для выбора предмета
        if (mc.currentScreen instanceof InventoryScreen && pendingPickSlot != -1) {
            return;
        }
        
        if (mc.currentScreen != null && !(mc.currentScreen instanceof InventoryScreen)) return;

        if (event.getKey() == swapKey.getKey()) {
            // Режим колеса
            if (swapMode.is("Колесо")) {
                if (event.getBindType() == EventBinding.BindType.KEYBOARD) {
                    // Открываем колесо при нажатии
                    if (!wheelOpen) {
                        wheelOpen = true;
                        updateWheelCursorState(true);
                    }
                } 
                return;
            }
            
            // Простой режим
            if (event.getBindType() == EventBinding.BindType.KEYBOARD && swapCooldown == 0) {
                this.needSwap = true;
            }
        }
        
        // Обработка кликов мыши в режиме колеса
        if (wheelOpen && event.getBindType() == EventBinding.BindType.MOUSE) {
            if (mc.currentScreen != null) return;
            
            int hover = getHoverIndex();
            if (hover == -1) return;
            
            if (event.getKey() == (GLFW.GLFW_MOUSE_BUTTON_RIGHT + 1000)) {
                // ПКМ - открыть инвентарь для выбора предмета
                pendingPickSlot = hover;
                cursorUnlocked = false;
                mc.setScreen(new InventoryScreen(Objects.requireNonNull(mc.player)));
            } else if (event.getKey() == (GLFW.GLFW_MOUSE_BUTTON_LEFT + 1000)) {
                // ЛКМ - свапнуть предмет (если слот не пустой)
                ItemStack targetStack = getStackForIndex(hover);
                if (!targetStack.isEmpty() && targetStack.getItem() != Items.AIR) {
                    executeSwapLogicByStack(targetStack);
                    // Колесо остается открытым
                }
            }
        }
    }

    @EventLink
    public void onInput(final EventMoveInput e) {
        if (bypassgrim.isState() && bypassTicks > 0) {
            if (mc.player == null) return;
            mc.player.setSprinting(false);
            e.setForward(0);
            e.setStrafe(0);
            e.setJump(false);
            e.setSneak(false);
        }
    }

    @EventLink
    public void onUpdate(final EventUpdate e) {
        if (mc.player == null || mc.world == null) return;

        // Проверяем, отпущена ли кнопка колеса
        if (wheelOpen && swapMode.is("Колесо")) {
            long window = mc.getWindow().getHandle();
            int key = swapKey.getKey();
            
            boolean isPressed;
            if (key >= 1000) {
                // Кнопка мыши
                isPressed = GLFW.glfwGetMouseButton(window, key - 1000) == GLFW.GLFW_PRESS;
            } else {
                // Клавиша клавиатуры
                isPressed = GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
            }
            
            // Закрываем колесо при отпускании кнопки
            if (!isPressed && mc.currentScreen == null) {
                closeWheel();
            }
        }

        if (swapCooldown > 0) {
            swapCooldown--;
        }
        
        // Обработка отложенного свапа с синхронизацией
        if (syncSwapTicks > 0) {
            syncSwapTicks--;
            if (syncSwapTicks == 1) {
                // Выполняем свап
                doSwap(targetSlot);
                mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
                NotificationManager.pushCustom("Свапнул на: " + pendingSwapItem, getCategory().getIcons());
                targetSlot = -1;
                pendingSwapItem = "";
            }
            return;
        }

        if (bypassgrim.isState() && bypassTicks > 0) {
            mc.player.setSprinting(false);
            bypassTicks--;

            if (bypassTicks == 1) {
                performSwap();
            }

            if (bypassTicks == 0) {
                restoreSprint();
            }
            return;
        }

        if (needSwap && targetSlot == -1) {
            needSwap = false;

            Item offhand = mc.player.getOffHandStack().getItem();
            Item first = getItem(firstItem.getCurrent());
            Item second = getItem(secondItem.getCurrent());

            int firstSlot = findItemSlot(first);
            int secondSlot = findItemSlot(second);

            if (firstSlot == -1 && secondSlot == -1) return;

            int slot;
            if (offhand == first && secondSlot != -1) {
                slot = secondSlot;
            } else if (firstSlot != -1) {
                slot = firstSlot;
            } else {
                slot = secondSlot;
            }

            if (slot == -1) return;

            targetSlot = slot;

            if (bypassgrim.isState()) {
                disableSprint();
                bypassTicks = 2;
                swapCooldown = 2;
            } else {
                performSwap();
                swapCooldown = 2;
            }
        }
    }
    
    private void performSwap() {
        if (targetSlot == -1) return;

        String itemName = mc.player.playerScreenHandler.getSlot(targetSlot).getStack().getName().getString();
        
        // Проверяем синхронизацию с GuiMove
        if (GuiMove.INSTANCE != null && GuiMove.INSTANCE.isSyncSwapEnabled() && isPlayerMoving()) {
            // Останавливаем движение на время свапа
            GuiMove.INSTANCE.stopMovementTemporarily(GuiMove.INSTANCE.getSyncSwapStopSeconds());
            
            // Запоминаем данные для отложенного свапа
            pendingSwapItem = itemName;
            syncSwapTicks = 3; // 3 тика задержка (~150мс)
        } else {
            // Обычный свап без задержки
            doSwap(targetSlot);
            mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
            NotificationManager.pushCustom("Свапнул на: " + itemName, getCategory().getIcons());
            targetSlot = -1;
        }
    }

    private void doSwap(int slot) {
        if (slot >= 36 && slot <= 44) {
            // Слот в хотбаре - используем SWAP с номером хотбар-слота
            int hotbarSlot = slot - 36;
            mc.interactionManager.clickSlot(0, 45, hotbarSlot, SlotActionType.SWAP, mc.player);
        } else {
            // Слот в инвентаре - используем тройной клик (PICKUP + PICKUP)
            mc.interactionManager.clickSlot(0, slot, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(0, 45, 0, SlotActionType.PICKUP, mc.player);
        }
    }

    private int findItemSlot(Item item) {
        for (int i = 9; i < 45; i++) {
            ItemStack stack = mc.player.playerScreenHandler.getSlot(i).getStack();
            if (stack.getItem() == item) {
                // Если включена настройка "только зачарованные" и это тотем - проверяем зачарования
                if (onlyEnchanted.isState() && item == Items.TOTEM_OF_UNDYING) {
                    // Требуется зачарованный тотем
                    if (stack.hasEnchantments() || stack.hasGlint()) {
                        return i; // Нашли зачарованный тотем
                    }
                    // Продолжаем искать зачарованный
                } else {
                    // Для всех остальных предметов или когда настройка выключена
                    return i;
                }
            }
        }
        return -1;
    }

    private Item getItem(String name) {
        return switch (name) {
            case "Тотем" -> Items.TOTEM_OF_UNDYING;
            case "Шар" -> Items.PLAYER_HEAD;
            default -> Items.AIR;
        };
    }

    private void disableSprint() {
        if (sprintPaused) {
            return;
        }

        Sprint.pushPause(1000);
        sprintPaused = true;
    }

    private void restoreSprint() {
        if (!sprintPaused) {
            return;
        }

        sprintPaused = false;
        Sprint.popPause();
    }
    
    // ========== МЕТОДЫ ДЛЯ КОЛЕСА ==========
    
    @EventLink
    public void onRender(final EventRender.Default event) {
        if (!wheelOpen || mc.player == null) return;
        if (mc.currentScreen != null && !(mc.currentScreen instanceof InventoryScreen)) return;
        
        renderRadialWheel(event);
    }
    
    private void closeWheel() {
        wheelOpen = false;
        pendingPickSlot = -1;
        selectedWheelIndex = -1;
        updateWheelCursorState(false);
    }
    
    private void updateWheelCursorState(boolean shouldBeUnlocked) {
        if (shouldBeUnlocked) {
            if (!cursorUnlocked) {
                mc.mouse.unlockCursor();
                cursorUnlocked = true;
            }
        } else {
            if (cursorUnlocked) {
                if (mc.currentScreen == null) {
                    mc.mouse.lockCursor();
                }
                cursorUnlocked = false;
            }
        }
    }
    
    private List<TextSetting> getWheelSettings() {
        List<TextSetting> list = new ArrayList<>();
        list.add(slot1); 
        list.add(slot2); 
        list.add(slot3);
        return list;
    }
    
    private void setSlotString(int index, String value) {
        List<TextSetting> settings = getWheelSettings();
        if (index < 0 || index >= settings.size()) return;
        settings.get(index).setText(value);
    }
    
    private ItemStack getStackForIndex(int index) {
        if (index < 0 || index >= wheelStacks.length) return ItemStack.EMPTY;
        
        ItemStack stored = wheelStacks[index];
        if (stored != null && !stored.isEmpty()) {
            return stored;
        }
        
        // Fallback: пробуем загрузить из текстовой настройки
        List<TextSetting> settings = getWheelSettings();
        if (index >= settings.size()) return ItemStack.EMPTY;
        
        String raw = settings.get(index).get();
        if (raw == null || raw.isBlank() || raw.equals("minecraft:air")) return ItemStack.EMPTY;
        
        Identifier id = Identifier.tryParse(raw);
        if (id == null) return ItemStack.EMPTY;
        
        Item item = Registries.ITEM.get(id);
        if (item == null || item == Items.AIR) return ItemStack.EMPTY;
        
        return item.getDefaultStack();
    }
    
    private int getHoverIndex() {
        float cx = mc.getWindow().getScaledWidth() / 2f;
        float cy = mc.getWindow().getScaledHeight() / 2f;
        
        double mx = mc.mouse.getX() * mc.getWindow().getScaledWidth() / (double) mc.getWindow().getWidth();
        double my = mc.mouse.getY() * mc.getWindow().getScaledHeight() / (double) mc.getWindow().getHeight();
        
        return getHoverIndex((float) mx, (float) my, cx, cy, INNER_R, OUTER_R, WHEEL_SLOTS);
    }
    
    private int getHoverIndex(float mouseX, float mouseY, float cx, float cy, float innerR, float outerR, int count) {
        float dx = mouseX - cx;
        float dy = mouseY - cy;
        float dist = MathHelper.sqrt(dx * dx + dy * dy);
        
        if (dist < innerR || dist > outerR) return -1;
        
        // Вычисляем угол от центра к мыши
        double ang = Math.atan2(dy, dx);
        // Смещаем угол так, чтобы первый слот был сверху (начало в -PI/2)
        ang = ang + Math.PI / 2.0;
        // Нормализуем угол в диапазон [0, 2*PI)
        if (ang < 0) ang += Math.PI * 2.0;
        
        // Вычисляем индекс слота
        double sliceSize = (Math.PI * 2.0) / count;
        int idx = (int) (ang / sliceSize);
        
        // Проверяем границы
        if (idx < 0 || idx >= count) return -1;
        
        return idx;
    }
    
    private void renderRadialWheel(EventRender.Default event) {
        DrawContext context = event.getContext();
        MatrixStack ms = context.getMatrices();
        
        float cx = mc.getWindow().getScaledWidth() / 2f;
        float cy = mc.getWindow().getScaledHeight() / 2f;
        
        double mx = mc.mouse.getX() * mc.getWindow().getScaledWidth() / (double) mc.getWindow().getWidth();
        double my = mc.mouse.getY() * mc.getWindow().getScaledHeight() / (double) mc.getWindow().getHeight();
        
        selectedWheelIndex = getHoverIndex((float) mx, (float) my, cx, cy, INNER_R, OUTER_R, WHEEL_SLOTS);
        
        for (int i = 0; i < WHEEL_SLOTS; i++) {
            boolean hovered = (i == selectedWheelIndex);
            
            double slice = (Math.PI * 2.0) / WHEEL_SLOTS;
            double startAngle = i * slice - Math.PI / 2.0; // Начинаем сверху
            double endAngle = startAngle + slice;
            
            Color segColor = hovered
                    ? new Color(255, 255, 255, 50)
                    : new Color(30, 35, 30, 100);
            
            Color borderC = hovered
                    ? new Color(100, 150, 255, 200)
                    : new Color(255, 255, 255, 40);
            
            drawRadialSegment(cx, cy, INNER_R, OUTER_R, startAngle, endAngle, segColor, borderC);
            
            double midAngle = startAngle + slice / 2.0;
            float iconDist = INNER_R + (OUTER_R - INNER_R) / 2f;
            float itemX = cx + (float) Math.cos(midAngle) * iconDist;
            float itemY = cy + (float) Math.sin(midAngle) * iconDist;
            
            ItemStack stack = getStackForIndex(i);
            if (!stack.isEmpty() && stack.getItem() != Items.AIR) {
                ms.push();
                ms.translate(itemX - 8f, itemY - 8f, 200); // Z-координата для правильного отображения
                RenderSystem.enableDepthTest();
                context.drawItem(stack, 0, 0);
                ms.pop();
            }
        }
        
        // Рисуем крестик в центре
        float cs = 3f;
        Color crossColor = new Color(255, 255, 255, 180);
        fillRect(ms, cx - cs, cy - 0.5f, cs * 2f, 1f, crossColor);
        fillRect(ms, cx - 0.5f, cy - cs, 1f, cs * 2f, crossColor);
    }
    
    private void fillRect(MatrixStack matrices, float x, float y, float width, float height, Color color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        
        buffer.vertex(matrices.peek().getPositionMatrix(), x, y + height, 0)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        buffer.vertex(matrices.peek().getPositionMatrix(), x + width, y + height, 0)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        buffer.vertex(matrices.peek().getPositionMatrix(), x + width, y, 0)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        buffer.vertex(matrices.peek().getPositionMatrix(), x, y, 0)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }
    
    private void drawRadialSegment(float cx, float cy, float innerR, float outerR, double startAngle, double endAngle, Color bg, Color border) {
        int steps = 24;
        double delta = (endAngle - startAngle) / steps;
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        
        for (int i = 0; i <= steps; i++) {
            double a = startAngle + i * delta;
            float cos = (float) Math.cos(a);
            float sin = (float) Math.sin(a);
            
            builder.vertex(cx + cos * outerR, cy + sin * outerR, 0)
                    .color(bg.getRed(), bg.getGreen(), bg.getBlue(), bg.getAlpha());
            builder.vertex(cx + cos * innerR, cy + sin * innerR, 0)
                    .color(bg.getRed(), bg.getGreen(), bg.getBlue(), bg.getAlpha());
        }
        
        BufferRenderer.drawWithGlobalProgram(builder.end());
        
        // Рисуем границы
        BufferBuilder lineBuilder = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        
        for (int i = 0; i <= steps; i++) {
            double a = startAngle + i * delta;
            float cos = (float) Math.cos(a);
            float sin = (float) Math.sin(a);
            lineBuilder.vertex(cx + cos * outerR, cy + sin * outerR, 0)
                    .color(border.getRed(), border.getGreen(), border.getBlue(), border.getAlpha());
        }
        
        for (int i = steps; i >= 0; i--) {
            double a = startAngle + i * delta;
            float cos = (float) Math.cos(a);
            float sin = (float) Math.sin(a);
            lineBuilder.vertex(cx + cos * innerR, cy + sin * innerR, 0)
                    .color(border.getRed(), border.getGreen(), border.getBlue(), border.getAlpha());
        }
        
        BufferRenderer.drawWithGlobalProgram(lineBuilder.end());
    }
    
    /**
     * Поиск слота с точно таким же предметом (включая NBT-теги, зачарования и т.д.)
     * Используется в режиме колеса для свапа на конкретный предмет
     */
    private int findExactItemSlot(ItemStack targetStack) {
        if (mc.player == null || targetStack == null || targetStack.isEmpty()) return -1;
        
        for (int i = 9; i < 45; i++) {
            ItemStack stack = mc.player.playerScreenHandler.getSlot(i).getStack();
            if (ItemStack.areEqual(stack, targetStack)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Свап на точный предмет из колеса (с учетом NBT)
     */
    private void executeSwapLogicByStack(ItemStack targetStack) {
        int slot = findExactItemSlot(targetStack);
        if (slot != -1) {
            int containerSlot = slot < 9 ? slot + 36 : slot;
            targetSlot = containerSlot;
            
            if (bypassgrim.isState()) {
                disableSprint();
                bypassTicks = 2;
                swapCooldown = 2;
            } else {
                String itemName = mc.player.playerScreenHandler.getSlot(containerSlot).getStack().getName().getString();
                
                // Проверяем синхронизацию с GuiMove
                if (GuiMove.INSTANCE != null && GuiMove.INSTANCE.isSyncSwapEnabled() && isPlayerMoving()) {
                    // Останавливаем движение на время свапа
                    GuiMove.INSTANCE.stopMovementTemporarily(GuiMove.INSTANCE.getSyncSwapStopSeconds());
                    
                    // Запоминаем данные для отложенного свапа
                    pendingSwapItem = itemName;
                    syncSwapTicks = 3; // 3 тика задержка (~150мс)
                } else {
                    // Обычный свап без задержки
                    doSwap(containerSlot);
                    mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
                    NotificationManager.pushCustom("Свапнул на: " + itemName, getCategory().getIcons());
                    targetSlot = -1;
                }
                
                swapCooldown = 2;
            }
        }
    }
    
    private boolean isPlayerMoving() {
        if (mc.player == null) return false;
        return mc.player.input.playerInput.forward() 
                || mc.player.input.playerInput.backward()
                || mc.player.input.playerInput.left()
                || mc.player.input.playerInput.right()
                || mc.player.input.playerInput.jump();
    }
    
    /**
     * Старый метод для простого режима - ищет по типу Item
     */
    private void executeSwapLogicByItem(Item target) {
        int slot = findItemSlot(target);
        if (slot != -1) {
            int containerSlot = slot < 9 ? slot + 36 : slot;
            targetSlot = containerSlot;
            
            if (bypassgrim.isState()) {
                disableSprint();
                bypassTicks = 2;
                swapCooldown = 2;
            } else {
                // Обычный свап без задержки
                doSwap(containerSlot);
                mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
                
                String itemName = mc.player.playerScreenHandler.getSlot(containerSlot).getStack().getName().getString();
                NotificationManager.pushCustom("Свапнул на: " + itemName, getCategory().getIcons());
                targetSlot = -1;
                swapCooldown = 2;
            }
        }
    }
    
    // ========== ПУБЛИЧНЫЕ МЕТОДЫ ДЛЯ МИКСИНА ==========
    
    public boolean isPendingPickSlot() {
        return pendingPickSlot != -1 && wheelOpen;
    }
    
    public boolean handleInventoryClick(net.minecraft.screen.slot.Slot slot, int slotId) {
        if (mc.player == null || pendingPickSlot == -1) return false;
        
        ItemStack picked = slot.getStack();
        if (picked == null || picked.isEmpty() || picked.getItem() == Items.AIR) return false;
        
        Identifier id = Registries.ITEM.getId(picked.getItem());
        if (id == null) return false;
        
        // Сохраняем ID в текстовую настройку (для persistence)
        setSlotString(pendingPickSlot, id.toString());
        
        // Сохраняем полный ItemStack с NBT-тегами для правильного отображения
        if (pendingPickSlot >= 0 && pendingPickSlot < wheelStacks.length) {
            wheelStacks[pendingPickSlot] = picked.copy();
        }
        
        pendingPickSlot = -1;
        
        mc.setScreen(null);
        cursorUnlocked = false;
        updateWheelCursorState(true);
        
        return true;
    }

    @Override
    public void onDisable() {
        bypassTicks = 0;
        swapCooldown = 0;
        needSwap = false;
        targetSlot = -1;
        syncSwapTicks = 0;
        pendingSwapItem = "";
        restoreSprint();
        
        // Закрываем колесо при выключении модуля
        if (wheelOpen) {
            closeWheel();
        }
        
        super.onDisable();
    }
}

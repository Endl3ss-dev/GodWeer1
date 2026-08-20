package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Category1

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoEventLoot", category = Category.MISC, description = "Автоматический фарм ивентов")
public class AutoEventLoot extends Module {

    public static AutoEventLoot getInstance() {
        return Instance.get(AutoEventLoot.class);
    }

    // Настройки
    private final SliderSetting travelHeightSetting = new SliderSetting(this, "Высота полета (RT)", 400f, 200f, 1000f, 10f);
    private final SliderSetting eventHeightSetting = new SliderSetting(this, "Высота на ивенте", 150f, 100f, 300f, 5f);
    private final SliderSetting maxWaitTimeSetting = new SliderSetting(this, "Макс. ожидание (мин)", 4f, 1f, 30f, 1f);
    private final BooleanSetting elytraHelper = new BooleanSetting(this, "ElytraHelper", true);
    private final BooleanSetting testMode = new BooleanSetting(this, "Режим тестирования", false);
    private final ModeSetting testPhase = new ModeSetting(this, "Тестовая фаза",
            "Поиск ивента",
            "Полет к ивенту",
            "Установка дома",
            "Ожидание открытия",
            "RTP телепорт",
            "Возврат домой",
            "Открытие сундука",
            "Лут сундука"
    ).setVisible(() -> testMode.getValue());

    private final List<String> ANARCHY_COMMANDS = Arrays.asList(
            "/an102", "/an103", "/an104", "/an105", "/an106", "/an107",
            "/an203", "/an204", "/an205", "/an206", "/an207", "/an208", "/an209", "/an210",
            "/an211", "/an212", "/an213", "/an214", "/an215", "/an216", "/an217", "/an218",
            "/an219", "/an220", "/an302", "/an303", "/an304", "/an305", "/an306", "/an307",
            "/an308", "/an309", "/an310", "/an311", "/an502", "/an503", "/an504", "/an505",
            "/an506", "/an507", "/an602", "/an603"
    );

    private final List<String> IGNORED_EVENTS = Arrays.asList(
            "Сундук смерти", "Адская резня", "Вулкан", "Загадочный маяк"
    );

    private enum State {
        SEARCHING,
        WAITING_EVENT,
        FOUND_EVENT_WAIT,
        PRE_TRAVEL_RP,
        PREPARING_FLIGHT,
        TAKING_OFF,
        TRAVELING,
        SETTING_HOME,
        WAITING_OPEN,
        EXECUTING_RTP,
        RTP_CHEST_CLICK,
        RTP_WAIT,
        HOME_AND_FLY,
        OPENING_CHEST,
        LOOTING,
        ESCAPING
    }

    private State state = State.SEARCHING;
    private final StopWatch timer = new StopWatch();
    private final StopWatch fireworkTimer = new StopWatch();
    private final StopWatch swapTimer = new StopWatch();
    private final StopWatch afkTimer = new StopWatch();

    private int anarchyIndex = 0;
    private BlockPos eventPos = null;
    private long openTimeMillis = 0;
    private boolean isHomeSet = false;
    private boolean chestStealerToggled = false;
    private boolean pearlThrown = false;
    private boolean armorSwappedAfterPearl = false;

    private boolean currentServerIsBad = false;
    private boolean sentDelayCommand = false;
    private boolean spawnCommandSent = false;
    private boolean forceSkip = false;

    private final Pattern COORD_PATTERN = Pattern.compile("(?:\\[|:)\\s*(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)\\s*]?");
    private final Pattern TIME_MIN = Pattern.compile("(\\d+)\\s*мин");
    private final Pattern TIME_SEC = Pattern.compile("(\\d+)\\s*сек");

    private final int KEY_K = GLFW.GLFW_KEY_K;

    @Override
    public void onEnable() {
        super.onEnable();
        ChatUtil.addText(TextFormatting.GREEN + "Начинаю поиск ивентов...");
        reset();

        // Если режим тестирования, устанавливаем нужную фазу
        if (testMode.getValue()) {
            setTestPhase();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        // КРИТИЧЕСКИ ВАЖНО: Освобождаем все зажатые клавиши
        releaseAllKeys();

        if (chestStealerToggled) {
            toggleChestStealer();
            chestStealerToggled = false;
        }

        log("Модуль выключен, все клавиши освобождены.");
    }

    private void reset() {
        state = State.SEARCHING;
        anarchyIndex = 0;
        eventPos = null;
        openTimeMillis = 0;
        isHomeSet = false;
        chestStealerToggled = false;
        pearlThrown = false;
        armorSwappedAfterPearl = false;
        currentServerIsBad = false;
        sentDelayCommand = false;
        spawnCommandSent = false;
        forceSkip = false;
        timer.reset();
        afkTimer.reset();
        fireworkTimer.reset();
        swapTimer.reset();
    }

    private void setTestPhase() {
        String phase = testPhase.getValue();

        switch (phase) {
            case "Поиск ивента" -> {
                state = State.SEARCHING;
                log("Тест: Фаза поиска ивента");
            }
            case "Полет к ивенту" -> {
                eventPos = new BlockPos(1500, 80, -1500);
                state = State.PREPARING_FLIGHT;
                log("Тест: Фаза полета к ивенту (1500, 80, -1500)");
            }
            case "Установка дома" -> {
                eventPos = new BlockPos(1500, 80, -1500);
                state = State.SETTING_HOME;
                log("Тест: Фаза установки дома");
            }
            case "Ожидание открытия" -> {
                state = State.WAITING_OPEN;
                openTimeMillis = System.currentTimeMillis() + 60000;
                log("Тест: Фаза ожидания открытия");
            }
            case "RTP телепорт" -> {
                state = State.EXECUTING_RTP;
                log("Тест: Фаза RTP телепорта");
            }
            case "Возврат домой" -> {
                state = State.HOME_AND_FLY;
                log("Тест: Фаза возврата домой");
            }
            case "Открытие сундука" -> {
                state = State.OPENING_CHEST;
                log("Тест: Фаза открытия сундука");
            }
            case "Лут сундука" -> {
                state = State.LOOTING;
                log("Тест: Фаза лута сундука");
            }
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.getHeldItemMainhand().isEmpty() || mc.player.getHeldItemMainhand().getItem() != Items.FIREWORK_ROCKET) {
            ensureFireworks();
        }

        switch (state) {
            case SEARCHING -> phaseSearching();
            case WAITING_EVENT -> phaseWaitingEvent();
            case FOUND_EVENT_WAIT -> phaseFoundEventWait();
            case PRE_TRAVEL_RP -> phasePreTravelRP();
            case PREPARING_FLIGHT -> phasePreparingFlight();
            case TAKING_OFF -> phaseTakingOff();
            case TRAVELING -> phaseTraveling();
            case SETTING_HOME -> phaseSettingHome();
            case WAITING_OPEN -> phaseWaitingOpen();
            case EXECUTING_RTP -> phaseExecutingRTP();
            case RTP_CHEST_CLICK -> phaseRTPChestClick();
            case RTP_WAIT -> phaseRTPWait();
            case HOME_AND_FLY -> phaseHomeAndFly();
            case OPENING_CHEST -> phaseOpeningChest();
            case LOOTING -> phaseLooting();
            case ESCAPING -> phaseEscaping();
        }
    }

    // ========== ФАЗА 1: ПОИСК ИВЕНТА ==========
    private void phaseSearching() {
        if (timer.finished(2500) && !sentDelayCommand && !forceSkip) {
            mc.player.sendChatMessage("/event delay");
            sentDelayCommand = true;
        }

        if (timer.finished(4500) || forceSkip) {
            if (anarchyIndex >= ANARCHY_COMMANDS.size()) anarchyIndex = 0;
            String cmd = ANARCHY_COMMANDS.get(anarchyIndex);
            mc.player.sendChatMessage(cmd);
            log("Переключаюсь на сервер: " + cmd);

            currentServerIsBad = false;
            sentDelayCommand = false;
            eventPos = null;
            forceSkip = false;
            anarchyIndex++;
            timer.reset();
        }
    }

    // ========== ФАЗА 2: ОЖИДАНИЕ ИВЕНТА ==========
    private void phaseWaitingEvent() {
        if (timer.finished(10000)) {
            mc.player.sendChatMessage("/event delay");
            timer.reset();
        }
    }

    // ========== ФАЗА 3: НАЙДЕН ИВЕНТ ==========
    private void phaseFoundEventWait() {
        if (timer.finished(2000)) {
            log("Появился ивент, летаю!");
            mc.player.sendChatMessage("/rp");
            state = State.PRE_TRAVEL_RP;
            timer.reset();
        }
    }

    // ========== ФАЗА 4: ПОДГОТОВКА К ПОЛЕТУ (ПОСЛЕ /RP) ==========
    private void phasePreTravelRP() {
        if (timer.finished(7500)) {
            state = State.PREPARING_FLIGHT;
            timer.reset();
        }
    }

    // ========== ФАЗА 5: ПОДГОТОВКА ПОЛЕТА ==========
    private void phasePreparingFlight() {
        // Проверяем элитру
        if (mc.player.inventory.armorItemInSlot(2).getItem() != Items.ELYTRA) {
            if (swapTimer.finished(500)) {
                equipItem(Items.ELYTRA, 6);
                swapTimer.reset();
                return;
            }
        }

        // Проверяем фейерверки
        ensureFireworks();
        if (mc.player.getHeldItemMainhand().getItem() != Items.FIREWORK_ROCKET) {
            log("Нет фейерверков! Отключаюсь.");
            toggle();
            return;
        }

        state = State.TAKING_OFF;
        timer.reset();
    }

    // ========== ФАЗА 6: ВЗЛЕТ ==========
    private void phaseTakingOff() {
        mc.player.rotationPitch = -20;

        if (elytraHelper.getValue()) {
            if (mc.player.isElytraFlying()) {
                if (fireworkTimer.finished(500)) {
                    useFirework();
                    fireworkTimer.reset();
                    state = State.TRAVELING;
                    log("Взлетел! Лечу к координатам.");
                }
            } else {
                if (mc.player.isOnGround()) mc.player.jump();
            }
        } else {
            if (mc.player.isOnGround()) {
                mc.player.jump();
                timer.reset();
            } else if (mc.player.fallDistance > 0 && !mc.player.isElytraFlying()) {
                if (timer.finished(200)) {
                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                    timer.reset();
                }
            } else if (mc.player.isElytraFlying()) {
                if (fireworkTimer.finished(500)) {
                    useFirework();
                    fireworkTimer.reset();
                    state = State.TRAVELING;
                    log("Взлетел! Лечу к координатам.");
                }
            }
        }
    }

    // ========== ФАЗА 7: ПОЛЕТ К ИВЕНТУ ==========
    private void phaseTraveling() {
        if (eventPos == null) {
            log("Нет координат ивента!");
            state = State.WAITING_EVENT;
            return;
        }

        // Если упали, взлетаем снова
        if (!mc.player.isElytraFlying() && mc.player.isOnGround()) {
            state = State.TAKING_OFF;
            return;
        }

        // Используем фейерверки
        if (mc.player.isElytraFlying() && fireworkTimer.finished(7000)) {
            useFirework();
            fireworkTimer.reset();
        }

        double dist = Math.sqrt(mc.player.getDistanceSq(eventPos.getX(), mc.player.getPosY(), eventPos.getZ()));
        float travelY = travelHeightSetting.getValue();
        float eventY = eventHeightSetting.getValue();

        lookAtPos(eventPos.getX(), eventPos.getZ());

        if (dist > 100) {
            // Далеко от цели - набираем высоту
            if (mc.player.getPosY() < travelY) {
                mc.player.rotationPitch = -15;
            } else {
                mc.player.rotationPitch = 0;
            }
        } else {
            // Близко к цели - снижаемся
            if (mc.player.getPosY() > eventY + 5) {
                mc.player.rotationPitch = 20;
            } else {
                log("Достиг ивента! Устанавливаю дом.");
                mc.player.sendChatMessage("/sethome Zalupa3001");
                isHomeSet = true;
                state = State.SETTING_HOME;
                spawnCommandSent = false;
                timer.reset();
            }
        }
    }

    // ========== ФАЗА 8: УСТАНОВКА ДОМА ==========
    private void phaseSettingHome() {
        mc.player.rotationPitch = -90;

        // Поддерживаем полет
        if (!mc.player.isElytraFlying() && !mc.player.isOnGround()) {
            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
        }

        if (fireworkTimer.finished(1000)) {
            useFirework();
            fireworkTimer.reset();
        }

        if (!spawnCommandSent) {
            if (timer.finished(500)) {
                mc.player.sendChatMessage("/spawn");
                spawnCommandSent = true;
                log("Телепортируюсь на спавн.");
                timer.reset();
            }
        } else {
            if (timer.finished(7500)) {
                state = State.WAITING_OPEN;
                mc.player.sendChatMessage("/event delay");
                afkTimer.reset();
                timer.reset();
                log("Жду открытия ивента...");
            }
        }
    }

    // ========== ФАЗА 9: ОЖИДАНИЕ ОТКРЫТИЯ ==========
    private void phaseWaitingOpen() {
        handleAntiAFK();

        if (timer.finished(5000)) {
            mc.player.sendChatMessage("/event delay");
            timer.reset();
        }

        if (openTimeMillis > 0) {
            long timeLeft = openTimeMillis - System.currentTimeMillis();

            if (timeLeft <= 20000) {
                log("Ивент скоро откроется! Телепортируюсь через RTP.");

                // ВАЖНО: Освобождаем клавиши перед RTP
                releaseAllKeys();

                state = State.EXECUTING_RTP;
                timer.reset();
            }
        }
    }

    // ========== ФАЗА 10: ВЫПОЛНЕНИЕ RTP ==========
    private void phaseExecutingRTP() {
        // ВАЖНО: Убедимся что все клавиши отпущены
        releaseAllKeys();

        mc.player.sendChatMessage("/rtp");
        log("Прописал /rtp, жду меню...");
        state = State.RTP_CHEST_CLICK;
        timer.reset();
    }

    // ========== ФАЗА 11: КЛИК ПО СЛОТУ В RTP МЕНЮ ==========
    private void phaseRTPChestClick() {
        // ВАЖНО: Убедимся что все клавиши отпущены
        releaseAllKeys();

        // Ждем 400мс после команды /rtp и кликаем на слот 13 в ChestScreen
        if (timer.finished(400)) {
            if (mc.currentScreen instanceof ChestScreen) {
                mc.playerController.windowClick(
                        mc.player.openContainer.windowId,
                        13,
                        0,
                        ClickType.PICKUP,
                        mc.player
                );
                log("Нажал на слот 13 в RTP меню, телепортируюсь...");
                state = State.RTP_WAIT;
                timer.reset();
            } else {
                // Если экран не открылся, продолжаем ждать
                if (timer.finished(2000)) {
                    log("ChestScreen не открылся за 2 секунды, пропускаю клик");
                    state = State.RTP_WAIT;
                    timer.reset();
                }
            }
        }
    }

    // ========== ФАЗА 12: ОЖИДАНИЕ ПОСЛЕ RTP ==========
    private void phaseRTPWait() {
        // ВАЖНО: Убедимся что все клавиши отпущены
        releaseAllKeys();

        if (timer.finished(8000)) {
            mc.player.sendChatMessage("/home Zalupa3001");
            log("Возвращаюсь домой к ивенту!");
            state = State.HOME_AND_FLY;
            pearlThrown = false;
            armorSwappedAfterPearl = false;
            timer.reset();
        }

        // Надеваем элитру если её нет
        if (mc.player.inventory.armorItemInSlot(2).getItem() != Items.ELYTRA) {
            if (swapTimer.finished(500)) {
                equipItem(Items.ELYTRA, 6);
                swapTimer.reset();
            }
        }
    }

    // ========== ФАЗА 13: ВОЗВРАТ ДОМОЙ И ПОЛЕТ ==========
    private void phaseHomeAndFly() {
        // ВАЖНО: Убедимся что все клавиши отпущены в начале фазы
        if (timer.elapsedTime() < 100) {
            releaseAllKeys();
        }

        mc.player.rotationPitch = -90;

        // Поддерживаем полет
        if (mc.player.isElytraFlying()) {
            if (fireworkTimer.finished(300)) {
                useFirework();
                fireworkTimer.reset();
            }
        } else {
            if (mc.player.isOnGround()) {
                mc.player.jump();
            } else {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                useFirework();
            }
        }

        // Бросаем перл вверх
        if (timer.finished(4500) && !pearlThrown) {
            throwPearlUp();
            pearlThrown = true;
            log("Бросил эндер перл вверх!");
        }

        // Меняем элитру на нагрудник после броска перла
        if (timer.finished(6200) && pearlThrown && !armorSwappedAfterPearl) {
            if (mc.player.inventory.armorItemInSlot(2).getItem() == Items.ELYTRA) {
                int chestSlot = findArmor();
                if (chestSlot != -1) {
                    equipItem(mc.player.inventory.getStackInSlot(chestSlot).getItem(), 6);
                    armorSwappedAfterPearl = true;
                    log("Сменил элитру на нагрудник.");
                }
            }
        }

        if (timer.finished(7500)) {
            state = State.OPENING_CHEST;
            swapTimer.reset();
            timer.reset();
            log("Открываю эндер сундук...");
        }
    }

    // ========== ФАЗА 14: ОТКРЫТИЕ СУНДУКА ==========
    private void phaseOpeningChest() {
        // ВАЖНО: Убедимся что все клавиши отпущены в начале фазы
        if (timer.elapsedTime() < 100) {
            releaseAllKeys();
        }

        // Меняем элитру на нагрудник если всё еще в элитре
        if (mc.player.inventory.armorItemInSlot(2).getItem() == Items.ELYTRA) {
            if (swapTimer.finished(200)) {
                int chestSlot = findArmor();
                if (chestSlot != -1) {
                    equipItem(mc.player.inventory.getStackInSlot(chestSlot).getItem(), 6);
                }
                swapTimer.reset();
            }
        }

        // Включаем ChestStealer
        if (!chestStealerToggled) {
            toggleChestStealer();
            chestStealerToggled = true;
            log("Включил ChestStealer (клавиша K)");
        }

        // Ищем и открываем эндер сундук
        BlockPos ec = findNearestBlock(Blocks.ENDER_CHEST, 5);
        if (ec != null) {
            lookAtPos(ec.getX() + 0.5, ec.getZ() + 0.5);
            mc.player.rotationPitch = 50;

            if (swapTimer.finished(200)) {
                BlockRayTraceResult ray = new BlockRayTraceResult(
                        new Vector3d(ec.getX() + 0.5, ec.getY() + 0.5, ec.getZ() + 0.5),
                        Direction.UP, ec, false
                );
                mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, ray);
                mc.player.swingArm(Hand.MAIN_HAND);
                swapTimer.reset();
            }
        }

        if (mc.currentScreen != null) {
            state = State.LOOTING;
            timer.reset();
            log("Сундук открыт! Собираю лут...");
        }
    }

    // ========== ФАЗА 15: СБОР ЛУТА ==========
    private void phaseLooting() {
        // ВАЖНО: Убедимся что все клавиши отпущены
        releaseAllKeys();

        if (timer.finished(10000)) {
            if (!isPvpBossbarActive()) {
                state = State.ESCAPING;
                log("Лут собран! Сваливаю с сервера.");
            }
        }
    }

    // ========== ФАЗА 16: ПОБЕГ ==========
    private void phaseEscaping() {
        // ВАЖНО: Освобождаем все клавиши перед выходом
        releaseAllKeys();

        mc.player.sendChatMessage("/delhome Zalupa3001");
        mc.player.sendChatMessage("/hub");
        log("Ивент окончен! Возвращаюсь в хаб.");

        if (chestStealerToggled) {
            toggleChestStealer();
            chestStealerToggled = false;
        }

        toggle();
    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ==========

    private void handleAntiAFK() {
        if (afkTimer.finished(30000)) {
            if (!afkTimer.finished(30500)) {
                mc.gameSettings.keyBindForward.setPressed(true);
                mc.gameSettings.keyBindBack.setPressed(false);
            } else if (!afkTimer.finished(31000)) {
                mc.gameSettings.keyBindForward.setPressed(false);
                mc.gameSettings.keyBindBack.setPressed(true);
            } else {
                mc.gameSettings.keyBindForward.setPressed(false);
                mc.gameSettings.keyBindBack.setPressed(false);
                afkTimer.reset();
            }
        } else {
            mc.gameSettings.keyBindForward.setPressed(false);
            mc.gameSettings.keyBindBack.setPressed(false);
        }
    }

    /**
     * КРИТИЧЕСКИ ВАЖНАЯ ФУНКЦИЯ: Освобождает все клавиши
     * Это предотвращает баг, когда игрок не может ничего делать
     */
    private void releaseAllKeys() {
        mc.gameSettings.keyBindForward.setPressed(false);
        mc.gameSettings.keyBindBack.setPressed(false);
        mc.gameSettings.keyBindLeft.setPressed(false);
        mc.gameSettings.keyBindRight.setPressed(false);
        mc.gameSettings.keyBindJump.setPressed(false);
        mc.gameSettings.keyBindSneak.setPressed(false);
        mc.gameSettings.keyBindSprint.setPressed(false);
        mc.gameSettings.keyBindUseItem.setPressed(false);
        mc.gameSettings.keyBindAttack.setPressed(false);
    }

    /**
     * ИСПРАВЛЕННАЯ ФУНКЦИЯ для нажатия клавиши K (ChestStealer)
     * Не ломает ввод, использует правильный метод
     */
    private void toggleChestStealer() {
        try {
            InputMappings.Input input = InputMappings.getInputByCode(KEY_K, 0);
            KeyBinding.setKeyBindState(input, true);
            KeyBinding.onTick(input);
            KeyBinding.setKeyBindState(input, false);
        } catch (Exception e) {
            log("Ошибка при нажатии K: " + e.getMessage());
        }
    }

    private void ensureFireworks() {
        int rocketSlot = findItem(Items.FIREWORK_ROCKET);
        if (rocketSlot != -1) {
            if (rocketSlot < 9) {
                mc.player.inventory.currentItem = rocketSlot;
            } else {
                mc.playerController.windowClick(0, rocketSlot, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
            }
        }
    }

    private void equipItem(Item item, int armorSlot) {
        int slot = findItem(item);
        if (slot != -1) {
            if (slot < 9) slot += 36;
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, armorSlot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
        }
    }

    private int findArmor() {
        for (int i = 0; i < 36; i++) {
            net.minecraft.item.ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == Items.NETHERITE_CHESTPLATE || stack.getItem() == Items.DIAMOND_CHESTPLATE) {
                return i < 9 ? i + 36 : i;
            }
        }
        return -1;
    }

    private void useFirework() {
        ensureFireworks();
        if (mc.player.getHeldItemMainhand().getItem() == Items.FIREWORK_ROCKET) {
            mc.playerController.processRightClick(mc.player, mc.world, Hand.MAIN_HAND);
        }
    }

    @EventHandler
    public void onPacket(PacketEvent e) {
        if (e.isSend()) return;
        IPacket<?> packet = e.getPacket();

        if (packet instanceof SChatPacket) {
            String text = ((SChatPacket) packet).getChatComponent().getString();
            String rawText = TextFormatting.removeFormatting(text);
            String cleanText = rawText.toLowerCase().replace("ё", "е");

            if (currentServerIsBad && state == State.SEARCHING) return;

            // Проверка на плохие ивенты
            for (String badEvent : IGNORED_EVENTS) {
                if (rawText.contains(badEvent)) {
                    log("Плохой ивент (" + badEvent + "), скипаю сервер.");
                    triggerSkip();
                    return;
                }
            }

            // Проверка на уже открытые сундуки
            if (cleanText.contains("сундуки открыты")) {
                log("Сундуки уже открыты, скипаю сервер.");
                triggerSkip();
                return;
            }

            // Парсинг времени до ивента
            if (cleanText.contains("до следующего ивента") ||
                    cleanText.contains("начнется через") ||
                    cleanText.contains("до активации") ||
                    cleanText.contains("до открытия")) {

                long millisToAdd = parseTime(cleanText);

                if (millisToAdd > 0) {
                    if (cleanText.contains("сундуки закрыты") || cleanText.contains("до активации")) {
                        openTimeMillis = System.currentTimeMillis() + millisToAdd;
                        log("Ивент откроется через " + (millisToAdd / 1000) + " секунд");
                    } else if (state == State.SEARCHING) {
                        long minutesLeft = millisToAdd / 1000 / 60;
                        if (minutesLeft >= maxWaitTimeSetting.getValue()) {
                            log("Ивент слишком далеко (" + minutesLeft + " мин), скипаю.");
                            triggerSkip();
                            return;
                        } else {
                            state = State.WAITING_EVENT;
                            log("Ивент найден! Жду " + minutesLeft + " минут.");
                        }
                    }
                }
            }

            // Парсинг координат ивента
            Matcher coords = COORD_PATTERN.matcher(rawText);
            if (coords.find()) {
                if (currentServerIsBad) return;
                int x = Integer.parseInt(coords.group(1));
                int y = Integer.parseInt(coords.group(2));
                int z = Integer.parseInt(coords.group(3));
                eventPos = new BlockPos(x, y, z);
                log("Координаты ивента: " + x + " " + y + " " + z);
                if (state == State.SEARCHING || state == State.WAITING_EVENT) {
                    state = State.FOUND_EVENT_WAIT;
                    timer.reset();
                }
            }
        }
    }

    private void triggerSkip() {
        currentServerIsBad = true;
        state = State.SEARCHING;
        forceSkip = true;
    }

    private long parseTime(String text) {
        long totalMillis = 0;
        Matcher mMin = TIME_MIN.matcher(text);
        if (mMin.find()) totalMillis += Integer.parseInt(mMin.group(1)) * 60000L;
        Matcher mSec = TIME_SEC.matcher(text);
        if (mSec.find()) totalMillis += Integer.parseInt(mSec.group(1)) * 1000L;
        return totalMillis;
    }

    private boolean isPvpBossbarActive() {
        if (mc.ingameGUI.getBossOverlay() == null) return false;
        try {
            Field field = null;
            try {
                field = net.minecraft.client.gui.overlay.BossOverlayGui.class.getDeclaredField("mapBossInfos");
            } catch (NoSuchFieldException ex) {
                field = net.minecraft.client.gui.overlay.BossOverlayGui.class.getDeclaredField("field_184060_g");
            }

            if (field != null) {
                field.setAccessible(true);
                Map<UUID, ?> map = (Map<UUID, ?>) field.get(mc.ingameGUI.getBossOverlay());
                return map.values().stream()
                        .anyMatch(info -> {
                            try {
                                Object name = info.getClass().getMethod("getName").invoke(info);
                                String nameStr = name.toString();
                                return TextFormatting.removeFormatting(nameStr).contains("PvP");
                            } catch (Exception ignored) {
                                return false;
                            }
                        });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private BlockPos findNearestBlock(Block block, int range) {
        BlockPos p = mc.player.getPosition();
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos target = p.add(x, y, z);
                    if (mc.world.getBlockState(target).getBlock() == block) return target;
                }
            }
        }
        return null;
    }

    private int findItem(Item item) {
        for (int i = 0; i < 36; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) return i;
        }
        return -1;
    }

    private void lookAtPos(double x, double z) {
        double dx = x - mc.player.getPosX();
        double dz = z - mc.player.getPosZ();
        float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        mc.player.rotationYaw = yaw;
    }

    private void throwPearlUp() {
        int slot = findItem(Items.ENDER_PEARL);
        if (slot != -1) {
            float oldPitch = mc.player.rotationPitch;
            mc.player.rotationPitch = -90;
            int prevSlot = mc.player.inventory.currentItem;
            if (slot < 9) {
                mc.player.inventory.currentItem = slot;
            } else {
                mc.playerController.windowClick(0, slot, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
            }
            mc.playerController.processRightClick(mc.player, mc.world, Hand.MAIN_HAND);
            if (slot >= 9) {
                mc.playerController.windowClick(0, slot, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
            } else {
                mc.player.inventory.currentItem = prevSlot;
            }
            mc.player.rotationPitch = oldPitch;
        }
    }

    private void log(String msg) {
        if (mc.player != null) {
            ChatUtil.addText(TextFormatting.GRAY + "[" + TextFormatting.RED + "AutoEventLoot" + TextFormatting.GRAY + "] " + TextFormatting.WHITE + msg);
        }
    }
}
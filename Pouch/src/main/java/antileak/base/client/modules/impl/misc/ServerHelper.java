package antileak.base.client.modules.impl.misc;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;

import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventBinding;
import antileak.base.api.events.implement.EventMoveInput;
import antileak.base.api.events.implement.EventUpdate;
import antileak.base.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import antileak.base.api.utils.input.KeyBoardUtils;
import antileak.base.api.utils.notification.NotificationManager;
import antileak.base.api.utils.player.InventoryUtils;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.BindSetting;
import antileak.base.client.modules.settings.implement.ModeSetting;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ServerHelper extends Module {

    public static ServerHelper INSTANCE = new ServerHelper();

    private final ModeSetting mode = new ModeSetting("Режим", "SpookyTime", "SpookyTime");

    private final BindSetting featherKey = new BindSetting("Перышко", -1)
            .visible(() -> mode.is("Lony"));
    private final BindSetting magmaKey = new BindSetting("Ливалка", -1)
            .visible(() -> mode.is("Lony"));
    private final BindSetting cryingObsidianKey = new BindSetting("Трапка", -1)
            .visible(() -> mode.is("Lony"));
    private final BindSetting clayKey = new BindSetting("Ливалка с платформой", -1)
            .visible(() -> mode.is("Lony"));

    private final BindSetting disorientationKey = new BindSetting("Дезориентация", -1)
            .visible(() -> mode.is("SpookyTime"));
    private final BindSetting trapKey = new BindSetting("Трапка", -1)
            .visible(() -> mode.is("SpookyTime"));
    private final BindSetting plastKey = new BindSetting("Пласт", -1)
            .visible(() -> mode.is("SpookyTime"));
    private final BindSetting pilKey = new BindSetting("Явная пыль", -1)
            .visible(() -> mode.is("SpookyTime"));
    private final BindSetting snegKey = new BindSetting("Снег заморозки", -1)
            .visible(() -> mode.is("SpookyTime"));
    private final BindSetting auraKey = new BindSetting("Божья аура", -1)
            .visible(() -> mode.is("SpookyTime"));
    private final BindSetting wrathKey = new BindSetting("Зелье Гнева", -1)
            .visible(() -> mode.is("SpookyTime"));
    private final BindSetting assassinKey = new BindSetting("Зелье Ассасина", -1)
            .visible(() -> mode.is("SpookyTime"));
    private final BindSetting sleepKey = new BindSetting("Снотворное", -1)
            .visible(() -> mode.is("SpookyTime"));
    private final BindSetting radiationKey = new BindSetting("Зелье Радиации", -1)
            .visible(() -> mode.is("SpookyTime"));
    private final BindSetting paladinKey = new BindSetting("Зелье Палладина", -1)
            .visible(() -> mode.is("SpookyTime"));
    private final BindSetting holyWaterKey = new BindSetting("Святая Вода", -1)
            .visible(() -> mode.is("SpookyTime"));
    private final BindSetting fireTornadoKey = new BindSetting("Огненный смерч", -1)
            .visible(() -> mode.is("SpookyTime"));

    private final BindSetting crossbowKey = new BindSetting("Арбалет", -1);

    private static final int COLOR_WRATH = 0xFF5555;
    private static final int COLOR_ASSASSIN = 0xFF55FF;
    private static final int COLOR_SLEEP = 0x5555FF;
    private static final int COLOR_RADIATION = 0x55FF55;
    private static final int COLOR_PALADIN = 0x55FFFF;
    private static final int COLOR_HOLY_WATER = 0xFFFFFF;

    private static ItemStack createPotionStack(int color) {
        ItemStack stack = new ItemStack(Items.SPLASH_POTION);
        stack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.<RegistryEntry<Potion>>empty(), Optional.of(color), List.<StatusEffectInstance>of(), Optional.<String>empty()));
        return stack;
    }

    private Item pendingItem;
    private Action pendingAction;
    private int slowingTicks;

    private boolean crossbowPending;
    private Action activeAction;
    private Hand activeHand;
    private int activeInventorySlot = -1;
    private int activeHotbarSlot = -1;
    private int activePreviousSlot = -1;
    private int activeStage;
    private float activeOriginalYaw;
    private float activeOriginalPitch;

    public ServerHelper() {
        super("ServerHelper", "Помощник для серверов", ModuleCategory.MISC);
        addSettings(
                mode,
                featherKey,
                magmaKey,
                cryingObsidianKey,
                clayKey,
                wrathKey,
                assassinKey,
                sleepKey,
                radiationKey,
                paladinKey,
                holyWaterKey,
                fireTornadoKey,
                disorientationKey,
                trapKey,
                plastKey,
                pilKey,
                snegKey,
                auraKey,
                crossbowKey
        );
    }

    public boolean isSpookyMode() {
        return mode.is("SpookyTime");
    }

    public boolean isLonyMode() {
        return mode.is("Lony");
    }

    public List<HelperBind> getLonyHelperBinds() {
        return List.of(
                new HelperBind("Перышко", null, new ItemStack(Items.FEATHER), featherKey),
                new HelperBind("Ливалка", null, new ItemStack(Items.MAGMA_CREAM), magmaKey),
                new HelperBind("Трапка", null, new ItemStack(Items.CRYING_OBSIDIAN), cryingObsidianKey),
                new HelperBind("Ливалка с платформой", null, new ItemStack(Items.CLAY), clayKey)
        );
    }

    public List<HelperBind> getSpookyHelperBinds() {
        return List.of(
                new HelperBind("Дезориентация", null, new ItemStack(Items.ENDER_EYE), disorientationKey),
                new HelperBind("Трапка", null, new ItemStack(Items.NETHERITE_SCRAP), trapKey),
                new HelperBind("Пласт", null, new ItemStack(Items.DRIED_KELP), plastKey),
                new HelperBind("Явная пыль", null, new ItemStack(Items.SUGAR), pilKey),
                new HelperBind("Снег заморозки", null, new ItemStack(Items.SNOWBALL), snegKey),
                new HelperBind("Божья аура", null, new ItemStack(Items.PHANTOM_MEMBRANE), auraKey),
                new HelperBind("Зелье Гнева", "гнева", createPotionStack(COLOR_WRATH), wrathKey),
                new HelperBind("Зелье Ассасина", "ассасина", createPotionStack(COLOR_ASSASSIN), assassinKey),
                new HelperBind("Снотворное", "снотворное", createPotionStack(COLOR_SLEEP), sleepKey),
                new HelperBind("Зелье Радиации", "радиации", createPotionStack(COLOR_RADIATION), radiationKey),
                new HelperBind("Зелье Палладина", "палладина", createPotionStack(COLOR_PALADIN), paladinKey),
                new HelperBind("Святая Вода", "святая", createPotionStack(COLOR_HOLY_WATER), holyWaterKey),
                new HelperBind("Огненный смерч", null, new ItemStack(Items.FIRE_CHARGE), fireTornadoKey)
        );
    }

    public record HelperBind(String name, String query, ItemStack itemStack, BindSetting bind) {
        public Item item() {
            return itemStack.getItem();
        }
        public ItemStack stack() {
            return itemStack;
        }
        public boolean matches(ItemStack stack) {
            if (stack.isEmpty() || stack.getItem() != item()) return false;
            if (query != null) {
                return stack.getName().getString().toLowerCase(Locale.ROOT).contains(query);
            }
            return true;
        }
    }

    public List<HelperBind> getActiveHelperBinds() {
        if (mode.is("Lony")) {
            return getLonyHelperBinds();
        }
        return getSpookyHelperBinds();
    }

    public String resolveHelperBindName(Item item) {
        for (HelperBind bind : getActiveHelperBinds()) {
            if (bind.item() == item) {
                return bind.name();
            }
        }
        return null;
    }

    public int getItemCountForBind(HelperBind bind) {
        if (mc.player == null) return 0;
        int count = 0;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (bind.matches(stack)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public ItemStack findStackForBind(HelperBind bind) {
        if (mc.player == null) return null;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (bind.matches(stack)) {
                return stack;
            }
        }
        return null;
    }

    @Override
    public void onEnable() {
        pendingItem = null;
        pendingAction = null;
        slowingTicks = 0;
        crossbowPending = false;
        resetLegitUse();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        pendingItem = null;
        pendingAction = null;
        slowingTicks = 0;
        crossbowPending = false;
        resetLegitUse();
        super.onDisable();
    }

    @EventLink
    public void onBinding(EventBinding event) {
        if (mc.currentScreen != null && !ModuleClass.INSTANCE.guiMove.isEnable()) return;

        if (event.getKey() == crossbowKey.getKey()) {
            crossbowPending = true;
        }

        if (mode.is("Lony")) {
            if (event.getKey() == featherKey.getKey()) {
                pendingItem = Items.FEATHER;
            } else if (event.getKey() == magmaKey.getKey()) {
                pendingItem = Items.MAGMA_CREAM;
            } else if (event.getKey() == cryingObsidianKey.getKey()) {
                pendingItem = Items.CRYING_OBSIDIAN;
            } else if (event.getKey() == clayKey.getKey()) {
                pendingItem = Items.CLAY_BALL;
            }
            return;
        }

        if (event.getKey() == disorientationKey.getKey()) {
            pendingAction = Action.DISORIENTATION;
        } else if (event.getKey() == trapKey.getKey()) {
            pendingAction = Action.TRAP;
        } else if (event.getKey() == plastKey.getKey()) {
            pendingAction = Action.PLAST;
        } else if (event.getKey() == pilKey.getKey()) {
            pendingAction = Action.DUST;
        } else if (event.getKey() == snegKey.getKey()) {
            pendingAction = Action.FREEZE_SNOW;
        } else if (event.getKey() == auraKey.getKey()) {
            pendingAction = Action.AURA;
        } else if (event.getKey() == wrathKey.getKey()) {
            pendingAction = Action.WRATH;
        } else if (event.getKey() == assassinKey.getKey()) {
            pendingAction = Action.ASSASSIN;
        } else if (event.getKey() == sleepKey.getKey()) {
            pendingAction = Action.SLEEP;
        } else if (event.getKey() == radiationKey.getKey()) {
            pendingAction = Action.RADIATION;
        } else if (event.getKey() == paladinKey.getKey()) {
            pendingAction = Action.PALADIN;
        } else if (event.getKey() == holyWaterKey.getKey()) {
            pendingAction = Action.HOLY_WATER;
        } else if (event.getKey() == fireTornadoKey.getKey()) {
            pendingAction = Action.FIRE_TORNADO;
        }
    }

    @EventLink
    public void onInput(EventMoveInput e) {
        if (slowingTicks > 0) {
            slowingTicks--;
            if (mc.player != null) {
                mc.player.setSprinting(false);
            }
            e.setForward(0);
            e.setStrafe(0);
            e.setJump(false);
            e.setSneak(false);
        }
    }

    @EventLink
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null) {
            pendingItem = null;
            pendingAction = null;
            resetLegitUse();
            return;
        }

        if (activeAction != null) {
            processLegitUse();
            return;
        }

        if (crossbowPending) {
            crossbowPending = false;
            fireCrossbow();
        }

        if (mode.is("Lony")) {
            if (pendingItem == null) return;

            InventoryUtils.swapAndUseHvH(pendingItem);
            pendingItem = null;
            return;
        }

        if (pendingAction == null || mc.interactionManager == null) {
            return;
        }

        useAction(pendingAction);
        pendingAction = null;
    }

    private void useAction(Action action) {
        if (mc.player.getItemCooldownManager().isCoolingDown(new ItemStack(action.item))) {
            NotificationManager.pushCustom("У предмета " + action.cooldownName + " есть кд", getCategory().getIcons());
            return;
        }

        if (matchesAction(mc.player.getMainHandStack(), action)) {
            startLegitUse(action, Hand.MAIN_HAND);
            return;
        }

        if (matchesAction(mc.player.getOffHandStack(), action)) {
            startLegitUse(action, Hand.OFF_HAND);
            return;
        }

        int hotbarSlot = findMatchingSlot(action, 0, 8);
        if (hotbarSlot != -1) {
            startLegitUseFromHotbar(action, hotbarSlot);
            return;
        }

        int inventorySlot = findMatchingSlot(action, 9, 35);
        if (inventorySlot != -1) {
            startLegitUseFromInventory(action, inventorySlot);
            return;
        }

        NotificationManager.pushCustom(action.failText, getCategory().getIcons());
    }

    private void startLegitUse(Action action, Hand hand) {
        activeAction = action;
        activeHand = hand;
        activeInventorySlot = -1;
        activeHotbarSlot = mc.player.getInventory().selectedSlot;
        activePreviousSlot = activeHotbarSlot;
        activeStage = 0;
        activeOriginalYaw = mc.player.getYaw();
        activeOriginalPitch = mc.player.getPitch();
        beginLegitUsePause();
    }

    private void startLegitUseFromHotbar(Action action, int hotbarSlot) {
        activeAction = action;
        activeHand = Hand.MAIN_HAND;
        activeInventorySlot = -1;
        activeHotbarSlot = hotbarSlot;
        activePreviousSlot = mc.player.getInventory().selectedSlot;
        activeStage = 0;
        activeOriginalYaw = mc.player.getYaw();
        activeOriginalPitch = mc.player.getPitch();
        beginLegitUsePause();
    }

    private void startLegitUseFromInventory(Action action, int inventorySlot) {
        activeAction = action;
        activeHand = Hand.MAIN_HAND;
        activeInventorySlot = inventorySlot;
        activeHotbarSlot = findTemporaryHotbarSlot();
        activePreviousSlot = mc.player.getInventory().selectedSlot;
        activeStage = 0;
        activeOriginalYaw = mc.player.getYaw();
        activeOriginalPitch = mc.player.getPitch();
        beginLegitUsePause();
    }

    private void processLegitUse() {
        if (mc.player == null || mc.interactionManager == null || activeAction == null) {
            resetLegitUse();
            return;
        }

        if (activeStage == 0) {
            beginLegitUsePause();

            if (activeInventorySlot != -1) {
                mc.interactionManager.clickSlot(0, activeInventorySlot, activeHotbarSlot, SlotActionType.SWAP, mc.player);
                mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
            }

            if (activeHand == Hand.MAIN_HAND) {
                selectHotbarSlot(activeHotbarSlot);
            }

            activeStage = 1;
            return;
        }

        if (activeStage == 1) {
            beginLegitUsePause();

            mc.interactionManager.interactItem(mc.player, activeHand);
            mc.player.swingHand(activeHand);
            NotificationManager.pushCustom(activeAction.successText, getCategory().getIcons());
            activeStage = 2;
            return;
        }

        finishLegitUse();
    }

    private void finishLegitUse() {
        restoreLegitUseState();
        slowingTicks = 0;
        restoreMovementInput();
        clearLegitUse();
    }

    private void resetLegitUse() {
        restoreLegitUseState();
        clearLegitUse();
    }

    private void restoreLegitUseState() {
        if (activeAction == null || mc.player == null) {
            return;
        }

        if (activeHand == Hand.MAIN_HAND && activePreviousSlot != -1 && activeStage > 0) {
            selectHotbarSlot(activePreviousSlot);
        }

        if (activeInventorySlot != -1 && activeHotbarSlot != -1 && activeStage > 0 && mc.interactionManager != null) {
            mc.interactionManager.clickSlot(0, activeInventorySlot, activeHotbarSlot, SlotActionType.SWAP, mc.player);
            mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
        }
    }

    private void clearLegitUse() {
        activeAction = null;
        activeHand = null;
        activeInventorySlot = -1;
        activeHotbarSlot = -1;
        activePreviousSlot = -1;
        activeStage = 0;
        activeOriginalYaw = 0.0F;
        activeOriginalPitch = 0.0F;
    }

    private void beginLegitUsePause() {
        slowingTicks = Math.max(slowingTicks, 2);

        if (mc.player == null || mc.player.networkHandler == null) {
            return;
        }

        mc.player.networkHandler.sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));

        if (mc.player.isSprinting()) {
            mc.player.setSprinting(false);
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }
    }

    private void restoreMovementInput() {
        if (mc.player != null && mc.player.networkHandler != null && mc.player.input != null) {
            mc.player.networkHandler.sendPacket(new PlayerInputC2SPacket(mc.player.input.playerInput));
        }
    }

    private void selectHotbarSlot(int slot) {
        if (slot < 0 || slot > 8 || mc.player == null || mc.player.networkHandler == null) {
            return;
        }

        mc.player.getInventory().selectedSlot = slot;
        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
    }

    private int findTemporaryHotbarSlot() {
        int fallback = 8;

        for (int slot = 0; slot < 9; slot++) {
            if (slot == mc.player.getInventory().selectedSlot) {
                continue;
            }

            ItemStack stack = mc.player.getInventory().getStack(slot);
            if (stack.isEmpty()) {
                return slot;
            }

            if (stack.getUseAction() == UseAction.NONE) {
                fallback = slot;
            }
        }

        return fallback;
    }

    private int findMatchingSlot(Action action, int start, int end) {
        for (int slot = start; slot <= end; slot++) {
            if (matchesAction(mc.player.getInventory().getStack(slot), action)) {
                return slot;
            }
        }

        return -1;
    }

    private boolean matchesAction(ItemStack stack, Action action) {
        if (stack == null || stack.isEmpty() || stack.getItem() != action.item) {
            return false;
        }

        return stack.getName().getString().toLowerCase(Locale.ROOT).contains(action.query);
    }

    private void fireCrossbow() {
        if (crossbowKey.getKey() == -1) return;

        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = mc.player.getInventory().getStack(slot);
            if (stack.getItem() != Items.CROSSBOW) continue;

            ChargedProjectilesComponent charged = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
            if (charged == null || charged.isEmpty()) continue;

            int oldSlot = mc.player.getInventory().selectedSlot;

            if (slot < 9) {
                mc.player.getInventory().selectedSlot = slot;
                for (int i = 0; i < 5; i++) {
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                }
                mc.player.getInventory().selectedSlot = oldSlot;
            } else {
                int tempSlot = findTemporaryHotbarSlot();
                mc.interactionManager.clickSlot(0, slot, tempSlot, SlotActionType.SWAP, mc.player);
                mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
                mc.player.getInventory().selectedSlot = tempSlot;
                for (int i = 0; i < 5; i++) {
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                }
                mc.interactionManager.clickSlot(0, slot, tempSlot, SlotActionType.SWAP, mc.player);
                mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
                mc.player.getInventory().selectedSlot = oldSlot;
            }

            NotificationManager.pushCustom("Выстрелил из арбалета!", getCategory().getIcons());
            return;
        }

        NotificationManager.pushCustom("Заряженный арбалет не найден!", getCategory().getIcons());
    }

    private enum Action {
        DISORIENTATION("дезориентация", "дезориентации", Items.ENDER_EYE, "Использовал дезориентацию!", "Дезориентация не найдена!"),
        TRAP("трапка", "трапки", Items.NETHERITE_SCRAP, "Использовал трапку!", "Трапка не найдена!"),
        PLAST("пласт", "пласта", Items.DRIED_KELP, "Использовал пласт!", "Пласт не найден!"),
        DUST("явная пыль", "пыли", Items.SUGAR, "Использовал пыль!", "Пыль не найдена!"),
        FREEZE_SNOW("заморозка", "снега", Items.SNOWBALL, "Использовал снег!", "Снег не найден!"),
        AURA("божья", "ауры", Items.PHANTOM_MEMBRANE, "Использовал ауру!", "Аура не найдена!"),
        WRATH("гнева", "зелья гнева", Items.SPLASH_POTION, "Использовал зелье гнева!", "Зелье гнева не найдено!"),
        ASSASSIN("ассасина", "зелья ассасина", Items.SPLASH_POTION, "Использовал зелье ассасина!", "Зелье ассасина не найдено!"),
        SLEEP("снотворное", "снотворного", Items.SPLASH_POTION, "Использовал снотворное!", "Снотворное не найдено!"),
        RADIATION("радиации", "зелья радиации", Items.SPLASH_POTION, "Использовал зелье радиации!", "Зелье радиации не найдено!"),
        PALADIN("палладина", "зелья палладина", Items.SPLASH_POTION, "Использовал зелье палладина!", "Зелье палладина не найдено!"),
        HOLY_WATER("святая", "святой воды", Items.SPLASH_POTION, "Использовал святую воду!", "Святая вода не найдена!"),
        FIRE_TORNADO("огненный", "огненного смерча", Items.FIRE_CHARGE, "Использовал огненный смерч!", "Огненный смерч не найден!");

        private final String query;
        private final String cooldownName;
        private final Item item;
        private final String successText;
        private final String failText;

        Action(String query, String cooldownName, Item item, String successText, String failText) {
            this.query = query;
            this.cooldownName = cooldownName;
            this.item = item;
            this.successText = successText;
            this.failText = failText;
        }
    }
}

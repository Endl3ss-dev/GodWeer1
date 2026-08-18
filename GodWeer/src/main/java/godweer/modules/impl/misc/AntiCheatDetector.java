package godweer.modules.impl.misc;

import com.mojang.brigadier.tree.CommandNode;

import godweer.GodWeer;
import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.ModeSetting;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public final class AntiCheatDetector extends Module {

    private static final long TICKS_PER_SECOND = 20L;
    private static final long STATUS_INTERVAL_TICKS = 1_200L;
    private static AntiCheatDetector activeDetector;

    private final ModeSetting duration = new ModeSetting("Длительность", "20 минут",
            "10 минут", "15 минут", "20 минут");
    private final ModeSetting intensity = new ModeSetting("Автотесты", "Осторожные",
            "Осторожные", "Интенсивные");
    private final BooleanSetting automaticActions = new BooleanSetting("Действия автоматически", true);
    private final BooleanSetting copyToClipboard = new BooleanSetting("Копировать отчёт", true);
    private final BooleanSetting stageNotifications = new BooleanSetting("Этапы в чат", true);

    private AntiCheatAnalyzer analyzer;
    private ScanStage stage = ScanStage.FINGERPRINT;
    private long startedAtMillis;
    private long scanTicks;
    private long targetTicks;
    private long nextStatusTick;
    private int stageActionSequence;
    private int lastScannedCommandCount = -1;
    private boolean scanning;
    private boolean reportFinalized;
    private boolean pendingCorrection;
    private double correctionStartX;
    private double correctionStartY;
    private double correctionStartZ;
    private boolean syntheticJump;
    private long releaseJumpAt;
    private boolean syntheticSneak;
    private long releaseSneakAt;
    private boolean syntheticSprint;
    private boolean sprintBeforeProbe;
    private long releaseSprintAt;
    private boolean syntheticHotbar;
    private int hotbarBeforeProbe;
    private int hotbarProbeSlot;
    private long releaseHotbarAt;

    public AntiCheatDetector() {
        super("AntiCheatDetector", "20-минутный анализ реакций и метаданных античита", ModuleCategory.MISC);
        addSettings(duration, intensity, automaticActions, copyToClipboard, stageNotifications);
    }

    @Override
    public void onEnable() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || minecraft.getConnection() == null) {
            notifyChat("§cСначала подключитесь к серверу.");
            setEnabled(false);
            return;
        }
        if (minecraft.hasSingleplayerServer()) {
            notifyChat("§cПроверка доступна только на удалённом сервере.");
            setEnabled(false);
            return;
        }

        analyzer = new AntiCheatAnalyzer();
        stage = ScanStage.FINGERPRINT;
        startedAtMillis = System.currentTimeMillis();
        scanTicks = 0L;
        targetTicks = resolveDurationMinutes() * 60L * TICKS_PER_SECOND;
        nextStatusTick = STATUS_INTERVAL_TICKS;
        stageActionSequence = 0;
        lastScannedCommandCount = -1;
        scanning = true;
        reportFinalized = false;
        pendingCorrection = false;
        resetSyntheticState();
        activeDetector = this;

        ServerData server = minecraft.getCurrentServer();
        String address = server == null ? "unknown" : server.ip;
        String name = server == null ? "unknown" : server.name;
        String brand = minecraft.player.connection.serverBrand();
        analyzer.begin(address, name, brand, SharedConstants.getCurrentVersion().getName());
        inspectServerMetadata(minecraft, true);
        notifyChat("§aСканирование запущено на " + resolveDurationMinutes()
                + " минут. Не открывайте GUI во время активных этапов.");
        notifyChat("§7Используются только валидные vanilla-действия; точная версия возможна только при явной сигнатуре.");
    }

    @Override
    public void onDisable() {
        Minecraft minecraft = Minecraft.getInstance();
        restoreSyntheticActions(minecraft);
        if (activeDetector == this) {
            activeDetector = null;
        }
        if (scanning && !reportFinalized) {
            finalizeReport(minecraft, "manual stop");
        }
        scanning = false;
    }

    @Override
    public void onTick() {
        if (!scanning) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || minecraft.getConnection() == null) {
            finalizeReport(minecraft, "connection lost");
            scanning = false;
            setEnabled(false);
            return;
        }

        scanTicks++;
        releaseExpiredActions(minecraft, player);
        resolvePendingCorrection(player);

        ScanStage nextStage = resolveStage(scanTicks, targetTicks);
        if (nextStage != stage) {
            restoreSyntheticActions(minecraft);
            stage = nextStage;
            stageActionSequence = 0;
            if (stageNotifications.get()) {
                notifyChat("§7Этап: §f" + stage.getDisplayName());
            }
        }

        analyzer.recordTick(stage);
        if ((scanTicks % 600L) == 1L) {
            inspectServerMetadata(minecraft, false);
        }
        if (automaticActions.get()) {
            runStageActions(minecraft, player);
        }

        if (scanTicks >= nextStatusTick && scanTicks < targetTicks) {
            nextStatusTick += STATUS_INTERVAL_TICKS;
            long remaining = Math.max(0L, (targetTicks - scanTicks) / TICKS_PER_SECOND);
            notifyOverlay("AntiCheatDetector: " + stage.getDisplayName() + " • осталось "
                    + remaining / 60L + "м " + remaining % 60L + "с");
        }

        if (scanTicks >= targetTicks) {
            finalizeReport(minecraft, "completed");
            scanning = false;
            setEnabled(false);
        }
    }

    public static void onPositionCorrectionPacket() {
        AntiCheatDetector detector = activeDetector;
        if (detector == null || !detector.scanning) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        detector.pendingCorrection = true;
        detector.correctionStartX = player.getX();
        detector.correctionStartY = player.getY();
        detector.correctionStartZ = player.getZ();
    }

    public static void onCustomPayloadPacket(String channel) {
        AntiCheatDetector detector = activeDetector;
        if (detector != null && detector.scanning && detector.analyzer != null) {
            detector.analyzer.recordCustomPayload(channel);
        }
    }

    public static void onSystemMessagePacket(String message) {
        AntiCheatDetector detector = activeDetector;
        if (detector != null && detector.scanning && detector.analyzer != null) {
            detector.analyzer.recordSystemMessage(message);
        }
    }

    public static void onDisconnectPacket(String reason) {
        AntiCheatDetector detector = activeDetector;
        if (detector == null || !detector.scanning || detector.analyzer == null) {
            return;
        }
        detector.analyzer.recordDisconnect(reason);
        Minecraft minecraft = Minecraft.getInstance();
        detector.finalizeReport(minecraft, "server disconnect");
        detector.scanning = false;
        detector.setEnabled(false);
    }

    private void inspectServerMetadata(Minecraft minecraft, boolean forceCommands) {
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        analyzer.updateBrand(player.connection.serverBrand());
        int commandCount = player.connection.getCommands().getRoot().getChildren().size();
        if (forceCommands || commandCount != lastScannedCommandCount) {
            lastScannedCommandCount = commandCount;
            for (CommandNode<?> node : player.connection.getCommands().getRoot().getChildren()) {
                analyzer.recordCommand(node.getName());
            }
        }

        for (String objectiveName : minecraft.level.getScoreboard().getObjectiveNames()) {
            analyzer.recordSystemMessage("scoreboard-objective:" + objectiveName);
        }
    }

    private void runStageActions(Minecraft minecraft, LocalPlayer player) {
        if (minecraft.screen != null || !canProbe(player)) {
            return;
        }

        int baseCadence = intensity.is("Интенсивные") ? 35 : 90;
        long stageTick = scanTicks;
        switch (stage) {
            case FINGERPRINT, BASELINE -> {
            }
            case MOVEMENT -> {
                if (stageTick % baseCadence == 0L) {
                    runMovementAction(minecraft, player);
                }
            }
            case POSTURE -> {
                if (stageTick % baseCadence == 0L) {
                    startSneakProbe(minecraft, player);
                }
            }
            case INTERACTION -> {
                if (stageTick % baseCadence == 0L) {
                    runSwingProbe(player);
                }
            }
            case INVENTORY -> {
                if (stageTick % (baseCadence + 30L) == 0L) {
                    startHotbarProbe(player);
                }
            }
            case MIXED -> {
                int mixedCadence = Math.max(20, baseCadence - 15);
                if (stageTick % mixedCadence == 0L) {
                    runMixedAction(minecraft, player);
                }
            }
        }
    }

    private void runMovementAction(Minecraft minecraft, LocalPlayer player) {
        Vec3 velocity = player.getDeltaMovement();
        double horizontalSpeedSquared = velocity.x * velocity.x + velocity.z * velocity.z;
        if ((stageActionSequence++ & 1) == 0) {
            if (horizontalSpeedSquared < 0.0025D && player.onGround()) {
                startJumpProbe(minecraft);
            } else {
                startSprintProbe(player);
            }
        } else {
            startSprintProbe(player);
        }
    }

    private void runMixedAction(Minecraft minecraft, LocalPlayer player) {
        int action = stageActionSequence++ % 5;
        switch (action) {
            case 0 -> runMovementAction(minecraft, player);
            case 1 -> startSneakProbe(minecraft, player);
            case 2 -> runSwingProbe(player);
            case 3 -> startHotbarProbe(player);
            default -> runSwingProbe(player);
        }
    }

    private void startJumpProbe(Minecraft minecraft) {
        if (syntheticJump || minecraft.options.keyJump.isDown()) {
            return;
        }
        syntheticJump = true;
        releaseJumpAt = scanTicks + 1L;
        minecraft.options.keyJump.setDown(true);
        analyzer.recordAction(stage, ProbeAction.JUMP);
    }

    private void startSprintProbe(LocalPlayer player) {
        if (syntheticSprint) {
            return;
        }
        Vec3 velocity = player.getDeltaMovement();
        if (velocity.x * velocity.x + velocity.z * velocity.z < 0.001D) {
            return;
        }
        syntheticSprint = true;
        sprintBeforeProbe = player.isSprinting();
        releaseSprintAt = scanTicks + (intensity.is("Интенсивные") ? 8L : 16L);
        player.setSprinting(!sprintBeforeProbe);
        analyzer.recordAction(stage, ProbeAction.SPRINT);
    }

    private void startSneakProbe(Minecraft minecraft, LocalPlayer player) {
        if (syntheticSneak || minecraft.options.keyShift.isDown() || player.isUsingItem()) {
            return;
        }
        syntheticSneak = true;
        releaseSneakAt = scanTicks + (intensity.is("Интенсивные") ? 6L : 12L);
        minecraft.options.keyShift.setDown(true);
        analyzer.recordAction(stage, ProbeAction.SNEAK);
    }

    private void runSwingProbe(LocalPlayer player) {
        if (player.isUsingItem()) {
            return;
        }
        player.swing((stageActionSequence++ & 1) == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        analyzer.recordAction(stage, ProbeAction.SWING);
    }

    private void startHotbarProbe(LocalPlayer player) {
        if (syntheticHotbar || player.isUsingItem() || player.containerMenu != player.inventoryMenu) {
            return;
        }
        hotbarBeforeProbe = player.getInventory().selected;
        hotbarProbeSlot = (hotbarBeforeProbe + 1 + (stageActionSequence++ % 8)) % 9;
        if (hotbarProbeSlot == hotbarBeforeProbe) {
            hotbarProbeSlot = (hotbarBeforeProbe + 1) % 9;
        }
        syntheticHotbar = true;
        releaseHotbarAt = scanTicks + (intensity.is("Интенсивные") ? 5L : 10L);
        selectHotbar(player, hotbarProbeSlot);
        analyzer.recordAction(stage, ProbeAction.HOTBAR);
    }

    private void releaseExpiredActions(Minecraft minecraft, LocalPlayer player) {
        if (syntheticJump && scanTicks >= releaseJumpAt) {
            minecraft.options.keyJump.setDown(false);
            syntheticJump = false;
        }
        if (syntheticSneak && scanTicks >= releaseSneakAt) {
            minecraft.options.keyShift.setDown(false);
            syntheticSneak = false;
        }
        if (syntheticSprint && scanTicks >= releaseSprintAt) {
            player.setSprinting(sprintBeforeProbe);
            syntheticSprint = false;
        }
        if (syntheticHotbar && scanTicks >= releaseHotbarAt) {
            if (player.getInventory().selected == hotbarProbeSlot) {
                selectHotbar(player, hotbarBeforeProbe);
            }
            syntheticHotbar = false;
        }
    }

    private void restoreSyntheticActions(Minecraft minecraft) {
        if (minecraft == null) {
            resetSyntheticState();
            return;
        }
        LocalPlayer player = minecraft.player;
        if (syntheticJump) {
            minecraft.options.keyJump.setDown(false);
        }
        if (syntheticSneak) {
            minecraft.options.keyShift.setDown(false);
        }
        if (player != null && syntheticSprint) {
            player.setSprinting(sprintBeforeProbe);
        }
        if (player != null && syntheticHotbar && player.getInventory().selected == hotbarProbeSlot) {
            selectHotbar(player, hotbarBeforeProbe);
        }
        resetSyntheticState();
    }

    private void resetSyntheticState() {
        syntheticJump = false;
        syntheticSneak = false;
        syntheticSprint = false;
        syntheticHotbar = false;
        releaseJumpAt = 0L;
        releaseSneakAt = 0L;
        releaseSprintAt = 0L;
        releaseHotbarAt = 0L;
    }

    private void selectHotbar(LocalPlayer player, int slot) {
        if (slot < 0 || slot > 8 || player.getInventory().selected == slot) {
            return;
        }
        player.getInventory().selected = slot;
        player.connection.send(new ServerboundSetCarriedItemPacket(slot));
    }

    private boolean canProbe(LocalPlayer player) {
        if (!player.isAlive() || player.isPassenger() || player.isSleeping() || player.isUsingItem()) {
            return false;
        }
        if (player.isInWater() || player.isInLava() || player.isFallFlying() || player.getAbilities().flying) {
            return false;
        }
        return player.onGround();
    }

    private void resolvePendingCorrection(LocalPlayer player) {
        if (!pendingCorrection) {
            return;
        }
        pendingCorrection = false;
        double deltaX = player.getX() - correctionStartX;
        double deltaY = player.getY() - correctionStartY;
        double deltaZ = player.getZ() - correctionStartZ;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        analyzer.recordCorrection(stage, distance);
    }

    private void finalizeReport(Minecraft minecraft, String reason) {
        if (reportFinalized || analyzer == null) {
            return;
        }
        reportFinalized = true;
        analyzer.finish();
        long actualDuration = Math.max(0L, (System.currentTimeMillis() - startedAtMillis) / 1_000L);
        String report = analyzer.buildReport(targetTicks / TICKS_PER_SECOND, actualDuration, reason);
        if (copyToClipboard.get() && minecraft != null) {
            minecraft.keyboardHandler.setClipboard(report);
        }
        GodWeer.LOGGER.info("AntiCheatDetector report:\n{}", report);
        notifyChat(copyToClipboard.get()
                ? "§aПроверка завершена. Отчёт скопирован в буфер обмена."
                : "§aПроверка завершена. Отчёт записан в лог.");
    }

    private ScanStage resolveStage(long elapsedTicks, long totalTicks) {
        double progress = totalTicks <= 0L ? 1.0D : (double) elapsedTicks / totalTicks;
        if (progress < 0.10D) {
            return ScanStage.FINGERPRINT;
        }
        if (progress < 0.25D) {
            return ScanStage.BASELINE;
        }
        if (progress < 0.47D) {
            return ScanStage.MOVEMENT;
        }
        if (progress < 0.63D) {
            return ScanStage.POSTURE;
        }
        if (progress < 0.78D) {
            return ScanStage.INTERACTION;
        }
        if (progress < 0.90D) {
            return ScanStage.INVENTORY;
        }
        return ScanStage.MIXED;
    }

    private int resolveDurationMinutes() {
        return switch (duration.getCurrent()) {
            case "10 минут" -> 10;
            case "15 минут" -> 15;
            default -> 20;
        };
    }

    private void notifyChat(String text) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.gui.getChat().addMessage(Component.literal("§8[§fAC Detector§8] §r" + text));
        }
    }

    private void notifyOverlay(String text) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.gui.setOverlayMessage(Component.literal("§7" + text), false);
    }

    public enum ScanStage {
        FINGERPRINT("Сигнатуры сервера"),
        BASELINE("Базовая линия"),
        MOVEMENT("Движение и прыжки"),
        POSTURE("Спринт и приседание"),
        INTERACTION("Взаимодействия"),
        INVENTORY("Хотбар"),
        MIXED("Смешанный профиль");

        private final String displayName;

        ScanStage(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ProbeAction {
        JUMP,
        SPRINT,
        SNEAK,
        SWING,
        HOTBAR
    }
}

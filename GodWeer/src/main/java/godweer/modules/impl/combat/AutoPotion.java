package godweer.modules.impl.combat;

import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.FloatSetting;
import godweer.modules.api.setting.ListSetting;
import godweer.modules.api.setting.ModeSetting;
import godweer.utils.item.ConsumableItemUtil;
import godweer.utils.item.ConsumableUseController;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;

public final class AutoPotion extends Module {

    private static final int NO_SLOT = -1;
    private static final int ROTATION_STEP = 25;
    private static final int MAX_BURST_USES = 3;
    private static final long EFFECT_REFRESH_GUARD_MS = 1500L;
    private static final long INVENTORY_SCAN_INTERVAL_MS = 250L;
    private static final PotionType[] POTION_TYPES = PotionType.values();

    private final ListSetting potions = new ListSetting(
            "Зелья",
            new BooleanSetting("Сила", true),
            new BooleanSetting("Скорость", true),
            new BooleanSetting("Огнестойкость", true),
            new BooleanSetting("Исцеление", true)
    );
    private final ModeSetting throwTarget = new ModeSetting("Кидать", "Под себя", "На себя", "Под себя");
    private final BooleanSetting throughHand = new BooleanSetting("Через руку", true);
    private final BooleanSetting eachSeparately = new BooleanSetting("Каждую отдельно", false);
    private final FloatSetting useDelay = new FloatSetting("Задержка между использованием (мс)", 250.0F, 0.0F, 2000.0F, 25.0F)
            .visible(() -> eachSeparately.get());
    private final ModeSetting rotationMode = new ModeSetting("Повороты камеры", "Плавные", "Плавные", "Резкие", "Пакетные");
    private final BooleanSetting clientLock = new BooleanSetting("Client lock", false)
            .visible(() -> !rotationMode.is("Пакетные"));
    private final BooleanSetting disableAfterUse = new BooleanSetting("Выключать после использования", false);

    private final ConsumableUseController useController;
    private final long[] lastThrowTimes = new long[POTION_TYPES.length];

    private boolean sequenceActive;
    private boolean sequenceUsedAny;
    private boolean rotationReady;
    private int pendingMask;
    private long nextUseTime;
    private long nextScanTime;
    private float startYaw;
    private float startPitch;
    private float rotationYaw;
    private float rotationPitch;
    private float targetYaw;
    private float targetPitch;

    public AutoPotion() {
        super("AutoPotion", "Автоматически кидает выбранные бафы", ModuleCategory.COMBAT);
        useController = new ConsumableUseController(this);
        addSettings(potions, throwTarget, throughHand, eachSeparately, useDelay,
                rotationMode, clientLock, disableAfterUse);
    }

    @Override
    public void onEnable() {
        resetSequence();
        nextScanTime = 0L;
        for (int index = 0; index < lastThrowTimes.length; index++) {
            lastThrowTimes[index] = 0L;
        }
    }

    @Override
    public void onDisable() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player != null && sequenceActive && !rotationMode.is("Пакетные")) {
            if (clientLock.get()) {
                player.setYRot(startYaw);
                player.setXRot(startPitch);
            } else if (rotationMode.is("Плавные")) {
                sendRotation(player, startYaw, startPitch);
            }
        }
        useController.cancel(minecraft, player);
        resetSequence();
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || minecraft.gameMode == null) {
            useController.reset();
            resetSequence();
            return;
        }

        if (useController.tick(minecraft, player)) {
            return;
        }
        if (minecraft.screen != null || player.isUsingItem() || !player.isAlive()
                || player.isCreative() || player.isSpectator()) {
            return;
        }

        long now = System.currentTimeMillis();
        if (!sequenceActive) {
            if (now < nextScanTime) {
                return;
            }
            beginSequence(player, now);
            if (!sequenceActive) {
                nextScanTime = now + INVENTORY_SCAN_INTERVAL_MS;
                return;
            }
        }

        if (now < nextUseTime || !advanceRotation(player)) {
            return;
        }

        int maximumUses = eachSeparately.get() ? 1 : MAX_BURST_USES;
        int attempts = 0;
        for (int index = 0; index < POTION_TYPES.length && attempts < maximumUses; index++) {
            int bit = 1 << index;
            if ((pendingMask & bit) == 0) {
                continue;
            }

            PotionType type = POTION_TYPES[index];
            if (isPotionNoLongerNeeded(player, type, index, now)) {
                pendingMask &= ~bit;
                continue;
            }

            int slot = ConsumableItemUtil.findPotionSlot(player, Items.SPLASH_POTION, type.effect);
            if (slot == NO_SLOT) {
                pendingMask &= ~bit;
                continue;
            }

            attempts++;
            if (throwPotion(minecraft, player, slot)) {
                sequenceUsedAny = true;
                lastThrowTimes[index] = now;
            }
            pendingMask &= ~bit;

            if (eachSeparately.get()) {
                nextUseTime = now + Math.round(useDelay.get());
                break;
            }
        }

        if (pendingMask == 0) {
            finishSequence();
        }
    }

    private void beginSequence(LocalPlayer player, long now) {
        int availableMask = 0;
        for (int index = 0; index < POTION_TYPES.length; index++) {
            PotionType type = POTION_TYPES[index];
            if (!potions.is(type.settingName) || isPotionNoLongerNeeded(player, type, index, now)) {
                continue;
            }
            if (ConsumableItemUtil.findPotionSlot(player, Items.SPLASH_POTION, type.effect) != NO_SLOT) {
                availableMask |= 1 << index;
            }
        }

        if (availableMask == 0) {
            return;
        }

        pendingMask = availableMask;
        sequenceActive = true;
        sequenceUsedAny = false;
        rotationReady = false;
        nextUseTime = now;
        startYaw = player.getYRot();
        startPitch = player.getXRot();
        rotationYaw = startYaw;
        rotationPitch = startPitch;
        targetYaw = startYaw;
        targetPitch = throwTarget.is("На себя") ? -90.0F : 90.0F;
    }

    private boolean isPotionNoLongerNeeded(LocalPlayer player, PotionType type, int index, long now) {
        if (now - lastThrowTimes[index] < EFFECT_REFRESH_GUARD_MS) {
            return true;
        }
        if (type == PotionType.HEALING) {
            return player.getHealth() >= player.getMaxHealth();
        }
        return player.hasEffect(type.effect);
    }

    private boolean advanceRotation(LocalPlayer player) {
        if (rotationReady || rotationMode.is("Пакетные")) {
            rotationReady = true;
            return true;
        }

        if (rotationMode.is("Резкие")) {
            rotationYaw = targetYaw;
            rotationPitch = targetPitch;
            if (clientLock.get()) {
                player.setYRot(rotationYaw);
                player.setXRot(rotationPitch);
            }
            rotationReady = true;
            return true;
        }

        rotationYaw = approachAngle(rotationYaw, targetYaw, ROTATION_STEP);
        rotationPitch = approach(rotationPitch, targetPitch, ROTATION_STEP);
        if (clientLock.get()) {
            player.setYRot(rotationYaw);
            player.setXRot(rotationPitch);
        } else {
            sendRotation(player, rotationYaw, rotationPitch);
        }

        if (Math.abs(Mth.wrapDegrees(targetYaw - rotationYaw)) <= 0.5F
                && Math.abs(targetPitch - rotationPitch) <= 0.5F) {
            rotationReady = true;
        }
        return rotationReady;
    }

    private boolean throwPotion(Minecraft minecraft, LocalPlayer player, int slot) {
        float viewYaw = player.getYRot();
        float viewPitch = player.getXRot();
        boolean packetRotation = rotationMode.is("Пакетные") || !clientLock.get();

        if (packetRotation) {
            sendRotation(player, targetYaw, targetPitch);
        }
        player.setYRot(targetYaw);
        player.setXRot(targetPitch);

        boolean used = useController.useInstant(minecraft, player, slot, throughHand.get());
        if (packetRotation) {
            player.setYRot(viewYaw);
            player.setXRot(viewPitch);
            sendRotation(player, viewYaw, viewPitch);
        } else {
            player.setYRot(targetYaw);
            player.setXRot(targetPitch);
        }
        return used;
    }

    private void sendRotation(LocalPlayer player, float yaw, float pitch) {
        player.connection.send(new ServerboundMovePlayerPacket.Rot(
                yaw,
                pitch,
                player.onGround(),
                player.horizontalCollision
        ));
    }

    private void finishSequence() {
        boolean shouldDisable = disableAfterUse.get() && sequenceUsedAny;
        sequenceActive = false;
        sequenceUsedAny = false;
        rotationReady = false;
        pendingMask = 0;
        nextScanTime = System.currentTimeMillis() + INVENTORY_SCAN_INTERVAL_MS;
        if (shouldDisable) {
            setEnabled(false);
        }
    }

    private void resetSequence() {
        sequenceActive = false;
        sequenceUsedAny = false;
        rotationReady = false;
        pendingMask = 0;
        nextUseTime = 0L;
        startYaw = 0.0F;
        startPitch = 0.0F;
        rotationYaw = 0.0F;
        rotationPitch = 0.0F;
        targetYaw = 0.0F;
        targetPitch = 0.0F;
    }

    private static float approachAngle(float current, float target, float step) {
        float difference = Mth.wrapDegrees(target - current);
        if (Math.abs(difference) <= step) {
            return target;
        }
        return current + Math.copySign(step, difference);
    }

    private static float approach(float current, float target, float step) {
        float difference = target - current;
        if (Math.abs(difference) <= step) {
            return target;
        }
        return current + Math.copySign(step, difference);
    }

    private enum PotionType {
        STRENGTH("Сила", MobEffects.DAMAGE_BOOST),
        SPEED("Скорость", MobEffects.MOVEMENT_SPEED),
        FIRE_RESISTANCE("Огнестойкость", MobEffects.FIRE_RESISTANCE),
        HEALING("Исцеление", MobEffects.HEAL);

        private final String settingName;
        private final Holder<MobEffect> effect;

        PotionType(String settingName, Holder<MobEffect> effect) {
            this.settingName = settingName;
            this.effect = effect;
        }
    }
}
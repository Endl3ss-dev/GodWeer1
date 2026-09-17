package godweer.modules.impl.movement;

import godweer.GodWeer;
import godweer.managers.ModuleManager;
import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.FloatSetting;
import godweer.modules.api.setting.ModeSetting;
import godweer.utils.movement.MotionAudit;
import godweer.utils.movement.MotionHooks;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Speed extends Module implements MotionHooks.Listener {

    private static final double EPSILON = 1.0E-4;
    private static final int WINDOW = 64;
    private static final int WINDOW_MASK = WINDOW - 1;
    private static final double FALLBACK_BASELINE = 0.2805;
    private static final int CALIBRATION_SAMPLES = 24;
    private static final int CALIBRATION_TICKET_LIMIT = 90;
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    // Переиспользуемый буфер: метод вызывается каждый тик, new double[2] здесь — лишняя аллокация.
    private static final double[] WISH = new double[2];

    private final ModeSetting mode = new ModeSetting("Режим", "Адаптив",
            "Адаптив", "Грим Страф", "Матрица Рывок", "Ваниль");
    private final ModeSetting align = new ModeSetting("Вектор", "По вводу", "По вводу", "По текущей");
    private final BooleanSetting compensation = new BooleanSetting("Компенсировать трение", true);
    private final BooleanSetting sprint = new BooleanSetting("Авто-спринт", true);
    private final BooleanSetting onlyForward = new BooleanSetting("Только вперёд", false);
    private final BooleanSetting graceMode = new BooleanSetting("Грация после урона", true);
    private final BooleanSetting respectCollision = new BooleanSetting("Гасить об блок", true);
    private final BooleanSetting chatLog = new BooleanSetting("Флаги в чат", true);
    private final BooleanSetting fileLog = new BooleanSetting("Писать в файл", false);
    private final BooleanSetting copyClipboard = new BooleanSetting("Копировать в буфер", true);
    private final BooleanSetting autoStop = new BooleanSetting("Стоп по флагам", false);

    private final FloatSetting multiplier = new FloatSetting("Множитель", 1.28F, 1.0F, 3.0F, 0.01F)
            .visible(() -> !mode.is("Ваниль"));
    private final FloatSetting hardLimit = new FloatSetting("Жёсткий предел", 0.78F, 0.2F, 1.6F, 0.01F)
            .visible(() -> !mode.is("Ваниль"));
    private final FloatSetting safety = new FloatSetting("Запас", 0.04F, 0.0F, 0.25F, 0.005F);
    private final FloatSetting step = new FloatSetting("Шаг потолка", 0.01F, 0.001F, 0.06F, 0.001F)
            .visible(() -> mode.is("Адаптив"));
    private final FloatSetting degrade = new FloatSetting("Штраф за флаг", 0.3F, 0.05F, 0.95F, 0.01F);
    private final FloatSetting pauseTicks = new FloatSetting("Пауза после флага", 14.0F, 0.0F, 60.0F, 1.0F);
    private final FloatSetting graceTicks = new FloatSetting("Тиков грация", 6.0F, 0.0F, 20.0F, 1.0F)
            .visible(() -> graceMode.get());
    private final FloatSetting graceMultiplier = new FloatSetting("Множитель грации", 1.9F, 1.0F, 3.0F, 0.05F)
            .visible(() -> graceMode.get());
    private final FloatSetting burstLimit = new FloatSetting("Пик рывка", 1.05F, 0.3F, 2.0F, 0.01F)
            .visible(() -> mode.is("Матрица Рывок"));
    private final FloatSetting window = new FloatSetting("Окно усреднения", 20.0F, 8.0F, 60.0F, 1.0F)
            .visible(() -> mode.is("Матрица Рывок"));
    private final FloatSetting friction = new FloatSetting("Компенсация трения", 0.91F, 0.6F, 1.0F, 0.01F)
            .visible(() -> compensation.get());
    private final FloatSetting sampleRate = new FloatSetting("Тиков на запись", 4.0F, 1.0F, 40.0F, 1.0F);
    private final FloatSetting autoStopFlags = new FloatSetting("Флагов до стопа", 8.0F, 1.0F, 64.0F, 1.0F)
            .visible(() -> autoStop.get());

    private final double[] history = new double[WINDOW];

    private long tick;
    private int historyHead;

    private double lastX;
    private double lastZ;
    private double predictX;
    private double predictZ;
    private double moveDirX;
    private double moveDirZ;
    private double current;
    private double baseline = FALLBACK_BASELINE;
    private double ceiling = FALLBACK_BASELINE * 1.05;
    private double limit = FALLBACK_BASELINE;
    private double peak;
    private double distance;
    private double baselineSum;
    private int baselineCount;
    private int calibrationTicks;
    private float lastHealth = -1.0F;

    private boolean sampled;
    private boolean flushed;
    private boolean calibrated;
    private boolean predicted;
    private boolean running;
    private int pause;
    private int grace;
    private int flags;
    private int corrections;

    public Speed() {
        super("Speed", "Адаптивный обход Grim/Matrix с аудитом флагов", ModuleCategory.MOVEMENT);
        addSettings(mode, align, multiplier, hardLimit, safety, step, degrade, pauseTicks,
                graceTicks, graceMultiplier, burstLimit, window, friction, sampleRate, autoStopFlags,
                compensation, sprint, onlyForward, graceMode, respectCollision,
                chatLog, fileLog, copyClipboard, autoStop);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        resetState();
        MotionAudit.reset();
        MotionHooks.subscribe(this);
        running = true;

        Minecraft minecraft = Minecraft.getInstance();
        MotionAudit.record(MotionAudit.STATE, tick, baseline, 0.0, 0.0);
        if (minecraft.player != null && minecraft.gui != null) {
            minecraft.gui.getChat().addMessage(Component.literal("§7Speed §8» §fрежим §b" + mode.getCurrent()
                    + " §8| §7baseline " + MotionAudit.fixed(baseline, 3)
                    + (MotionHooks.hooksLoaded() ? " §8| §aхук-тайминг" : " §8| §eтик-фолбэк")));
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        running = false;
        MotionHooks.unsubscribe(this);
        flush();
    }

    @Override
    public void onTick() {
        if (MotionHooks.hooksLoaded()) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            step(player);
        }
    }

    @Override
    public void preMotion(LocalPlayer player) {
        if (!running || !MotionHooks.hooksLoaded()) {
            return;
        }
        step(player);
    }

    @Override
    public void positionCorrection() {
        if (!running) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !predicted) {
            return;
        }

        double dx = player.getX() - predictX;
        double dz = player.getZ() - predictZ;
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist < 0.18) {
            return;
        }

        corrections++;
        // Обратный скаляр — сервер реально отбросил назад, это и есть setback античита.
        if (dx * moveDirX + dz * moveDirZ < 0.0) {
            penalty(MotionAudit.FLAG_ROLLBACK, dist);
        } else {
            MotionAudit.record(MotionAudit.CORRECTION, tick, current, limit, dist);
        }
        capture(player);
    }

    private void step(LocalPlayer player) {
        tick++;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null || !player.isAlive() || player.isPassenger()
                || player.isFallFlying() || player.isSwimming() || player.isInWater() || player.isInLava()) {
            capture(player);
            MotionAudit.record(MotionAudit.SKIP, tick, current, limit, 0.0);
            return;
        }

        measure(player);
        if (!isEnable()) {
            return;
        }

        if (sprint.get() && !player.isSprinting() && player.input != null && player.input.hasForwardImpulse()) {
            player.setSprinting(true);
        }

        if (mode.is("Ваниль")) {
            limit = baseline;
            return;
        }

        adapt();

        double target = limit;
        if (pause > 0) {
            pause--;
            target = Math.min(baseline, current);
            MotionAudit.record(MotionAudit.PAUSE, tick, current, target, pause);
        }
        if (grace > 0) {
            grace--;
        }

        if (player.input == null || !player.input.hasForwardImpulse() || (onlyForward.get() && player.zza <= 0.0F)) {
            predicted = false;
            return;
        }

        apply(player, target);
        if (tick % (long) Math.max(1.0F, sampleRate.get()) == 0L) {
            MotionAudit.record(MotionAudit.SAMPLE, tick, current, limit, ceiling);
        }
    }

    /**
     * Замер фактического смещения за такт + калибровка «ванильного» спринта.
     * Обратная связь по факту вместо модели трения: серверная валидация опирается на пройденную
     * дистанцию, поэтому целевая скорость всегда считается от реального результата предыдущего такта.
     */
    private void measure(LocalPlayer player) {
        double x = player.getX();
        double z = player.getZ();

        if (sampled) {
            double dx = x - lastX;
            double dz = z - lastZ;
            double dist = Math.sqrt(dx * dx + dz * dz);
            current = dist;
            distance += dist;

            if (dist > peak) {
                peak = dist;
            }
            if (dist > EPSILON) {
                double inverse = 1.0 / dist;
                moveDirX = dx * inverse;
                moveDirZ = dz * inverse;
            }
            push(dist);

            float health = player.getHealth();
            if (graceMode.get() && lastHealth > 0.0F && health < lastHealth - 0.05F) {
                grace = Math.round(graceTicks.get());
                MotionAudit.record(MotionAudit.GRACE, tick, current, limit, lastHealth - health);
            }
            lastHealth = health;

            if (calibrated) {
                double hard = hardLimit.get();
                if (dist > hard + safety.get()) {
                    penalty(MotionAudit.FLAG_LIMIT, dist - hard);
                }
            } else if (player.onGround() && player.isSprinting() && dist > 0.05 && dist < 0.6) {
                baselineSum += dist;
                if (++baselineCount >= CALIBRATION_SAMPLES) {
                    baseline = Math.max(0.12, baselineSum / baselineCount);
                    ceiling = baseline * 1.05;
                    calibrated = true;
                    MotionAudit.record(MotionAudit.STATE, tick, baseline, 0.0, baselineCount);
                }
            }

            if (!calibrated && ++calibrationTicks >= CALIBRATION_TICKET_LIMIT) {
                baseline = FALLBACK_BASELINE;
                ceiling = FALLBACK_BASELINE * 1.05;
                calibrated = true;
                MotionAudit.record(MotionAudit.STATE, tick, baseline, 0.0, -1.0);
            }
        }

        lastX = x;
        lastZ = z;
        sampled = true;
        predicted = false;
    }

    // Адаптивный потолок: плавно растим к запрошенному, резко режем после флага.
    private void adapt() {
        if (!calibrated) {
            limit = baseline;
            return;
        }

        double hard = Math.max(0.1, hardLimit.get() - safety.get());
        double wanted = Math.min(hard, Math.max(0.1, baseline * multiplier.get()));

        if (pause > 0) {
            limit = Math.max(0.05, Math.min(baseline, wanted));
            return;
        }

        if (grace > 0 && graceMode.get()) {
            wanted = Math.min(hard, wanted * graceMultiplier.get());
        }
        if (mode.is("Матрица Рывок")) {
            wanted = burst(wanted);
        }

        if (wanted > ceiling) {
            double next = Math.min(wanted, ceiling + step.get());
            if (next >= wanted - EPSILON) {
                MotionAudit.record(MotionAudit.CEILING_UP, tick, current, wanted, next);
            }
            ceiling = next;
        } else if (wanted < ceiling - 0.02) {
            ceiling = Math.max(baseline * 1.02, wanted);
            MotionAudit.record(MotionAudit.CEILING_DOWN, tick, current, wanted, ceiling);
        }

        limit = Math.max(0.05, ceiling - safety.get());
    }

    /**
     * Бюджет рывка под усредняющую проверку Matrix: окно из N тиков должно в среднем
     * оставаться в пределах допустимой скорости, поэтому разгон разрешён только «в долг».
     */
    private double burst(double wanted) {
        int size = (int) Math.max(8.0F, Math.min((float) WINDOW, window.get()));
        double sum = 0.0;
        for (int i = 0; i < size; i++) {
            sum += history[(historyHead - 1 - i) & WINDOW_MASK];
        }

        double budget = wanted * size - sum;
        if (budget <= 0.0) {
            return Math.min(wanted, baseline * 1.02);
        }
        double headroom = Math.min(burstLimit.get(), wanted + budget * 0.5);
        return Math.max(0.05, Math.min(headroom, Math.max(0.1, hardLimit.get() - safety.get())));
    }

    private void apply(LocalPlayer player, double target) {
        Vec3 movement = player.getDeltaMovement();
        double horizontal = Math.sqrt(movement.x * movement.x + movement.z * movement.z);

        double dirX;
        double dirZ;
        if (align.is("По текущей") && horizontal > EPSILON) {
            double inverse = 1.0 / horizontal;
            dirX = movement.x * inverse;
            dirZ = movement.z * inverse;
        } else {
            wishDirection(player);
            dirX = WISH[0];
            dirZ = WISH[1];
        }

        double value = target;
        if (respectCollision.get() && player.horizontalCollision) {
            value = Math.min(value, baseline * 1.04);
        }

        double frictionCompensation = compensation.get()
                ? Math.max(0.6, Math.min(1.0, friction.get()))
                : 1.0;
        player.setDeltaMovement(dirX * value / frictionCompensation, movement.y, dirZ * value / frictionCompensation);

        Vec3 applied = player.getDeltaMovement();
        predictX = player.getX() + applied.x;
        predictZ = player.getZ() + applied.z;
        predicted = true;
    }

    private void wishDirection(LocalPlayer player) {
        float yaw = player.getYRot() * Mth.DEG_TO_RAD;
        float sin = Mth.sin(yaw);
        float cos = Mth.cos(yaw);
        double forward = player.zza;
        double strafe = player.xxa;

        double x = -sin * forward + cos * strafe;
        double z = cos * forward + sin * strafe;
        double length = Math.sqrt(x * x + z * z);
        if (length < EPSILON) {
            x = -sin;
            z = cos;
            length = 1.0;
        }

        WISH[0] = x / length;
        WISH[1] = z / length;
    }

    private void push(double value) {
        history[historyHead] = value;
        historyHead = (historyHead + 1) & WINDOW_MASK;
    }

    private void capture(LocalPlayer player) {
        lastX = player.getX();
        lastZ = player.getZ();
        sampled = true;
        predicted = false;
    }

    private void penalty(int kind, double extra) {
        flags++;
        pause = Math.round(pauseTicks.get());

        double next = Math.max(baseline * 1.02, ceiling - ceiling * degrade.get());
        if (next < ceiling - EPSILON) {
            ceiling = next;
        }
        MotionAudit.record(kind, tick, current, ceiling, extra);

        if (chatLog.get()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.gui != null) {
                minecraft.gui.getChat().addMessage(Component.literal("§cSpeed §8» §c" + MotionAudit.label(kind)
                        + " §8| §ftick " + tick + " cur " + MotionAudit.fixed(current, 3)
                        + " §8| §7нов. потолок " + MotionAudit.fixed(ceiling, 3)
                        + " §8| §eпауза " + pause + "т"));
            }
        }

        if (autoStop.get() && flags >= autoStopFlags.get()) {
            setEnabled(false);
        }
    }

    private void resetState() {
        tick = 0L;
        historyHead = 0;
        current = 0.0;
        baseline = FALLBACK_BASELINE;
        ceiling = FALLBACK_BASELINE * 1.05;
        limit = FALLBACK_BASELINE;
        peak = 0.0;
        distance = 0.0;
        baselineSum = 0.0;
        baselineCount = 0;
        calibrationTicks = 0;
        lastHealth = -1.0F;
        sampled = false;
        flushed = false;
        calibrated = false;
        predicted = false;
        pause = 0;
        grace = 0;
        flags = 0;
        corrections = 0;
        for (int i = 0; i < WINDOW; i++) {
            history[i] = 0.0;
        }
    }

    private void flush() {
        if (tick == 0L || flushed) {
            return;
        }
        flushed = true;

        Minecraft minecraft = Minecraft.getInstance();
        StringBuilder builder = new StringBuilder(8192);
        builder.append("=== GodWeer ").append(GodWeer.MOD_VERSION).append(" | Speed audit ===\n");
        builder.append("server=").append(minecraft.isLocalServer() ? "singleplayer" : "multiplayer")
                .append(" ticks=").append(tick)
                .append(" hooks=").append(MotionHooks.hooksLoaded() ? "mixin" : "tick-fallback").append('\n');
        builder.append("mode=").append(mode.getCurrent()).append(" align=").append(align.getCurrent()).append('\n');
        builder.append("mult=").append(MotionAudit.fixed(multiplier.get(), 2))
                .append(" hardLimit=").append(MotionAudit.fixed(hardLimit.get(), 2))
                .append(" safety=").append(MotionAudit.fixed(safety.get(), 3))
                .append(" step=").append(MotionAudit.fixed(step.get(), 3))
                .append(" degrade=").append(MotionAudit.fixed(degrade.get(), 2)).append('\n');
        builder.append("pause=").append((int) pauseTicks.get())
                .append(" graceTicks=").append((int) graceTicks.get())
                .append(" graceMult=").append(MotionAudit.fixed(graceMultiplier.get(), 2))
                .append(" burst=").append(MotionAudit.fixed(burstLimit.get(), 2))
                .append(" window=").append((int) window.get())
                .append(" friction=").append(MotionAudit.fixed(compensation.get() ? friction.get() : 1.0F, 2)).append('\n');
        builder.append("baseline=").append(MotionAudit.fixed(baseline, 3))
                .append(" calibrated=").append(calibrated)
                .append(" ceiling=").append(MotionAudit.fixed(ceiling, 3))
                .append(" peak=").append(MotionAudit.fixed(peak, 3))
                .append(" avg=").append(MotionAudit.fixed(tick > 0L ? distance / tick : 0.0, 3))
                .append(" corrections=").append(corrections).append('\n');
        builder.append("--- ticks ---\n");
        MotionAudit.renderBody(builder, tick - 1L);
        builder.append("--- totals ---\n");
        MotionAudit.renderTotals(builder);
        builder.append("flags=").append(MotionAudit.flags())
                .append(" (rollback=").append(MotionAudit.count(MotionAudit.FLAG_ROLLBACK))
                .append(" limit=").append(MotionAudit.count(MotionAudit.FLAG_LIMIT))
                .append(") lines=").append(MotionAudit.written())
                .append(" dropped=").append(MotionAudit.dropped()).append('\n');

        String text = builder.toString();
        GodWeer.LOGGER.info("Speed audit:\n{}", text);

        if (fileLog.get()) {
            writeToFile(text);
        }
        if (copyClipboard.get()) {
            copyToClipboard(minecraft, text);
        }
        if (minecraft.gui != null) {
            String tail = MotionAudit.flags() == 0 ? "§aфлагов нет" : "§cфлаги: " + MotionAudit.flags();
            minecraft.gui.getChat().addMessage(Component.literal("§7Speed §8» §fлог: " + MotionAudit.written()
                    + " строк, " + tail + (copyClipboard.get() ? " §8| §aскопировано в буфер" : "")));
        }
    }

    private void copyToClipboard(Minecraft minecraft, String text) {
        if (minecraft.keyboardHandler == null) {
            return;
        }
        try {
            minecraft.keyboardHandler.setClipboard(text);
        } catch (Exception exception) {
            GodWeer.LOGGER.warn("Speed audit clipboard failed", exception);
        }
    }

    private void writeToFile(String text) {
        try {
            Path dir = FabricLoader.getInstance().getGameDir().resolve("godweer").resolve("logs");
            Files.createDirectories(dir);
            Path file = dir.resolve("speed-" + LocalDateTime.now().format(STAMP) + ".log");
            Files.writeString(file, text, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            GodWeer.LOGGER.warn("Speed audit file write failed", exception);
        }
    }

    @Override
    public String getSuffix() {
        if (!running) {
            return "";
        }

        StringBuilder builder = new StringBuilder(32);
        builder.append(" §7").append(mode.getCurrent())
                .append(" §f").append(MotionAudit.fixed(current, 2))
                .append("§8/").append(MotionAudit.fixed(limit, 2));
        if (pause > 0) {
            builder.append(" §ePAUSE ").append(pause);
        }
        if (flags > 0) {
            builder.append(" §cF").append(flags);
        }
        return builder.toString();
    }

    public int getFlags() {
        return flags;
    }

    public double getBaseline() {
        return baseline;
    }

    public double getCeiling() {
        return ceiling;
    }

    public double getMeasuredSpeed() {
        return current;
    }

    public static Speed get() {
        return ModuleManager.get(Speed.class);
    }
}

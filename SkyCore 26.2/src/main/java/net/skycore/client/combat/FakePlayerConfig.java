package net.skycore.client.combat;

/**
 * Снимок настроек фейк-игрока.
 *
 * <p>Это «мост» между GUI-модулем {@code net.skycore.client.module.combat.FakePlayerModule}
 * и самой болванкой {@code net.skycore.client.command.FakeplayerCommand.FakePlayer}.
 * Такое разделение уже было в проекте: команда не должна зависеть от GUI, а модуль —
 * от физики болванки. Модуль раз в тик собирает актуальный конфиг, болванка читает
 * {@link FakePlayerSettings#current()}.
 *
 * <p>Поля можно собирать цепочкой — так на месте вызова видно, какое значение куда
 * идёт (длинный конструктор из трёх десятков аргументов легко перепутать):
 * <pre>{@code
 * new FakePlayerConfig()
 *         .followPlayer(true)
 *         .followSpeed(1.5)
 *         .useTotem(true);
 * }</pre>
 *
 * <p>Все числовые значения зажимаются в допустимый диапазон прямо в сеттере, поэтому
 * конфиг нельзя «испортить» ни слайдером, ни правкой JSON-конфига руками.
 */
public final class FakePlayerConfig {

    /** Тип брони, который выдаётся болванке, когда копирование инвентаря выключено. */
    public static final int ARMOR_NONE = 0;
    public static final int ARMOR_IRON = 1;
    public static final int ARMOR_DIAMOND = 2;
    public static final int ARMOR_NETHERITE = 3;

    /** Предмет в правой руке болванки. */
    public static final int HELD_NONE = 0;
    public static final int HELD_SWORD = 1;
    public static final int HELD_AXE = 2;
    public static final int HELD_CRYSTAL = 3;
    public static final int HELD_BOW = 4;

    /** Размеры массивов режимов — по ним зажимаются индексы. */
    private static final int ARMOR_TYPES = 4;
    private static final int HELD_ITEMS = 5;
    private static final int MODES = 4;
    private static final int POTION_LEVELS = 5;

    // ── инвентарь ───────────────────────────────────────────────────────────
    private boolean copyInventory;
    private int armorType = ARMOR_NETHERITE;
    private int protection;
    private int heldItem = HELD_SWORD;

    // ── тотем / щит ─────────────────────────────────────────────────────────
    private boolean useTotem = true;
    private int totemMode = 1;
    private int totemCount = 1;
    private boolean infiniteTotems;
    private boolean useShield;
    private int shieldMode = 1;

    // ── поведение ───────────────────────────────────────────────────────────
    private boolean followPlayer;
    private double followSpeed = 1.0;
    private boolean strafe;
    private double strafeSpeed = 1.0;
    private boolean jump;

    // ── выживаемость ────────────────────────────────────────────────────────
    private double maxHealth = 20.0;
    private boolean regeneration;
    private int regenInterval = 20;
    private int fireResistance;
    private int resistance;
    private int speedBoost;
    private boolean immuneToFire;
    private boolean immuneToExplosions;

    // ── бой ─────────────────────────────────────────────────────────────────
    private boolean attackTarget;
    private double attackDamage = 6.0;
    private double eatAtHealth;

    // ── визуал / сеть ───────────────────────────────────────────────────────
    private boolean invisible;
    private boolean glowing;
    private int simulatedPing;

    // ── инвентарь ───────────────────────────────────────────────────────────

    public boolean copyInventory() {
        return this.copyInventory;
    }

    public FakePlayerConfig copyInventory(boolean value) {
        this.copyInventory = value;
        return this;
    }

    public int armorType() {
        return this.armorType;
    }

    public FakePlayerConfig armorType(int value) {
        this.armorType = clamp(value, 0, ARMOR_TYPES - 1);
        return this;
    }

    public int protection() {
        return this.protection;
    }

    public FakePlayerConfig protection(int value) {
        this.protection = clamp(value, 0, 4);
        return this;
    }

    public int heldItem() {
        return this.heldItem;
    }

    public FakePlayerConfig heldItem(int value) {
        this.heldItem = clamp(value, 0, HELD_ITEMS - 1);
        return this;
    }

    // ── тотем / щит ─────────────────────────────────────────────────────────

    public boolean useTotem() {
        return this.useTotem;
    }

    public FakePlayerConfig useTotem(boolean value) {
        this.useTotem = value;
        return this;
    }

    public int totemMode() {
        return this.totemMode;
    }

    public FakePlayerConfig totemMode(int value) {
        this.totemMode = clamp(value, 0, MODES - 1);
        return this;
    }

    public int totemCount() {
        return this.totemCount;
    }

    public FakePlayerConfig totemCount(int value) {
        this.totemCount = clamp(value, 0, 300);
        return this;
    }

    public boolean infiniteTotems() {
        return this.infiniteTotems;
    }

    public FakePlayerConfig infiniteTotems(boolean value) {
        this.infiniteTotems = value;
        return this;
    }

    public boolean useShield() {
        return this.useShield;
    }

    public FakePlayerConfig useShield(boolean value) {
        this.useShield = value;
        return this;
    }

    public int shieldMode() {
        return this.shieldMode;
    }

    public FakePlayerConfig shieldMode(int value) {
        this.shieldMode = clamp(value, 0, MODES - 1);
        return this;
    }

    // ── поведение ───────────────────────────────────────────────────────────

    public boolean followPlayer() {
        return this.followPlayer;
    }

    public FakePlayerConfig followPlayer(boolean value) {
        this.followPlayer = value;
        return this;
    }

    public double followSpeed() {
        return this.followSpeed;
    }

    public FakePlayerConfig followSpeed(double value) {
        this.followSpeed = clampDouble(value, 0.1, 3.0, 1.0);
        return this;
    }

    public boolean strafe() {
        return this.strafe;
    }

    public FakePlayerConfig strafe(boolean value) {
        this.strafe = value;
        return this;
    }

    public double strafeSpeed() {
        return this.strafeSpeed;
    }

    public FakePlayerConfig strafeSpeed(double value) {
        this.strafeSpeed = clampDouble(value, 0.2, 4.0, 1.0);
        return this;
    }

    public boolean jump() {
        return this.jump;
    }

    public FakePlayerConfig jump(boolean value) {
        this.jump = value;
        return this;
    }

    // ── выживаемость ────────────────────────────────────────────────────────

    public double maxHealth() {
        return this.maxHealth;
    }

    public FakePlayerConfig maxHealth(double value) {
        this.maxHealth = clampDouble(value, 1.0, 200.0, 20.0);
        return this;
    }

    public boolean regeneration() {
        return this.regeneration;
    }

    public FakePlayerConfig regeneration(boolean value) {
        this.regeneration = value;
        return this;
    }

    public int regenInterval() {
        return this.regenInterval;
    }

    public FakePlayerConfig regenInterval(int value) {
        this.regenInterval = clamp(value, 5, 100);
        return this;
    }

    public int fireResistance() {
        return this.fireResistance;
    }

    public FakePlayerConfig fireResistance(int value) {
        this.fireResistance = clamp(value, 0, POTION_LEVELS - 1);
        return this;
    }

    public int resistance() {
        return this.resistance;
    }

    public FakePlayerConfig resistance(int value) {
        this.resistance = clamp(value, 0, POTION_LEVELS - 1);
        return this;
    }

    public int speedBoost() {
        return this.speedBoost;
    }

    public FakePlayerConfig speedBoost(int value) {
        this.speedBoost = clamp(value, 0, POTION_LEVELS - 1);
        return this;
    }

    public boolean immuneToFire() {
        return this.immuneToFire;
    }

    public FakePlayerConfig immuneToFire(boolean value) {
        this.immuneToFire = value;
        return this;
    }

    public boolean immuneToExplosions() {
        return this.immuneToExplosions;
    }

    public FakePlayerConfig immuneToExplosions(boolean value) {
        this.immuneToExplosions = value;
        return this;
    }

    // ── бой ─────────────────────────────────────────────────────────────────

    public boolean attackTarget() {
        return this.attackTarget;
    }

    public FakePlayerConfig attackTarget(boolean value) {
        this.attackTarget = value;
        return this;
    }

    public double attackDamage() {
        return this.attackDamage;
    }

    public FakePlayerConfig attackDamage(double value) {
        this.attackDamage = clampDouble(value, 1.0, 40.0, 6.0);
        return this;
    }

    public double eatAtHealth() {
        return this.eatAtHealth;
    }

    public FakePlayerConfig eatAtHealth(double value) {
        this.eatAtHealth = clampDouble(value, 0.0, 20.0, 0.0);
        return this;
    }

    // ── визуал / сеть ───────────────────────────────────────────────────────

    public boolean invisible() {
        return this.invisible;
    }

    public FakePlayerConfig invisible(boolean value) {
        this.invisible = value;
        return this;
    }

    public boolean glowing() {
        return this.glowing;
    }

    public FakePlayerConfig glowing(boolean value) {
        this.glowing = value;
        return this;
    }

    public int simulatedPing() {
        return this.simulatedPing;
    }

    public FakePlayerConfig simulatedPing(int value) {
        this.simulatedPing = clamp(value, 0, 1000);
        return this;
    }

    // ── производные значения ────────────────────────────────────────────────

    /** Сколько тиков назад берётся позиция игрока при имитации пинга (50 мс = 1 тик). */
    public int pingTicks() {
        return Math.min(40, this.simulatedPing / 50);
    }

    /** Множитель скорости движения от эффекта «Скорость» (+20 % на уровень). */
    public double speedMultiplier() {
        return 1.0 + 0.2 * this.speedBoost;
    }

    /** Доля урона, которую съедает «Сопротивление» (20 % на уровень, максимум 80 %). */
    public float damageMultiplier() {
        return Math.max(0.2F, 1.0F - 0.2F * this.resistance);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double clampDouble(double value, double min, double max, double fallback) {
        if (!Double.isFinite(value)) {
            return fallback;
        }
        return Math.max(min, Math.min(max, value));
    }
}

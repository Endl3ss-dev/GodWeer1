package net.skycore.client.module.combat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.skycore.client.combat.FakePlayerConfig;
import net.skycore.client.combat.FakePlayerSettings;
import net.skycore.client.command.ChatMessages;
import net.skycore.client.command.FakeplayerCommand;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.StringSetting;

/**
 * FakePlayer — фейковый игрок (болванка для тестов урона, тотема и щита).
 *
 * <p>Сам фейк-игрок живёт в {@link FakeplayerCommand}: там физика, урон, тотем,
 * щит и взрыв кристалла. Модуль только управляет жизненным циклом болванок и
 * передаёт им настройки через {@link FakePlayerSettings} (снимок
 * {@link FakePlayerConfig}, который собирается каждый тик — менять настройки можно,
 * не пересоздавая болванку).
 *
 * <p>Настройки сгруппированы так же, как в запросе:
 * <ol>
 *   <li><b>Кто и сколько</b> — имя, количество болванок, дистанция спавна,
 *       авто-респавн.</li>
 *   <li><b>Инвентарь</b> — копирование ваших вещей либо выбор типа брони,
 *       уровня Protection и предмета в руке.</li>
 *   <li><b>Тотем</b> — вкл/выкл, режим {@code Legit / Normal / Hard / Extra},
 *       количество 0…300, бесконечные тотемы.</li>
 *   <li><b>Щит</b> — вкл/выкл и такой же режим.</li>
 *   <li><b>Поведение</b> — следовать за вами, стрейф вокруг вас, прыгать.</li>
 *   <li><b>Выживаемость</b> — своё HP, регенерация, эффекты, иммунитет к огню
 *       и взрывам.</li>
 *   <li><b>Бой</b> — атаковать цель, сила атаки, «поесть» при низком HP.</li>
 *   <li><b>Визуал и сеть</b> — невидимость, свечение, имитация пинга.</li>
 * </ol>
 *
 * <p><b>Границы возможного (честно):</b> болванка существует только на клиенте,
 * поэтому её урон по вам виден лишь визуально (замах, звук, частицы) — здоровье
 * реального игрока считает сервер. По другим фейк-игрокам урон настоящий, потому
 * что они целиком клиентские.
 */
public final class FakePlayerModule extends ClientModule {

    private static final String[] MODES = {"Legit", "Normal", "Hard", "Extra"};
    private static final String[] POTION_LEVELS = {"Off", "I", "II", "III", "IV"};
    private static final String[] ARMOR_MODES = {"Нет", "Железо", "Алмаз", "Незерит"};
    private static final String[] HELD_MODES = {"Нет", "Меч", "Топор", "Кристалл", "Лук"};
    private static final String FALLBACK_NAME = "FakePlayer";

    public static final FakePlayerModule INSTANCE = new FakePlayerModule();

    // ── кто и сколько ───────────────────────────────────────────────────────
    private final StringSetting botName = this.add(new StringSetting("Имя бота", FALLBACK_NAME, 16));
    private final NumberSetting botCount = this.add(
            new NumberSetting("Кол-во ботов", 1.0, 1.0, 10.0, 1.0));
    private final NumberSetting spawnDistance = this.add(
            new NumberSetting("Дистанция спавна", 3.0, 1.0, 20.0, 0.5));
    private final BooleanSetting autoRespawn = this.add(
            new BooleanSetting("Авто-респавн", true));

    // ── инвентарь ───────────────────────────────────────────────────────────
    private final BooleanSetting copyInventory = this.add(
            new BooleanSetting("Копировать инвентарь", false));
    private final ModeSetting armorType = this.add(
            new ModeSetting("Тип брони", "Незерит", ARMOR_MODES));
    private final NumberSetting protection = this.add(
            new NumberSetting("Protection", 0.0, 0.0, 4.0, 1.0));
    private final ModeSetting heldItem = this.add(
            new ModeSetting("Предмет в руке", "Меч", HELD_MODES));

    // ── тотем ───────────────────────────────────────────────────────────────
    private final BooleanSetting useTotem = this.add(
            new BooleanSetting("Использовать тотем", true));
    private final ModeSetting totemMode = this.add(
            new ModeSetting("Режим тотема", "Normal", MODES));
    private final NumberSetting totemCount = this.add(
            new NumberSetting("Кол-во тотемов", 1.0, 0.0, 300.0, 1.0));
    private final BooleanSetting infiniteTotems = this.add(
            new BooleanSetting("Бесконечные тотемы", false));

    // ── щит ─────────────────────────────────────────────────────────────────
    private final BooleanSetting useShield = this.add(
            new BooleanSetting("Использовать щит", false));
    private final ModeSetting shieldMode = this.add(
            new ModeSetting("Режим щита", "Normal", MODES));

    // ── поведение ───────────────────────────────────────────────────────────
    private final BooleanSetting followPlayer = this.add(
            new BooleanSetting("Следовать за игроком", false));
    private final NumberSetting followSpeed = this.add(
            new NumberSetting("Скорость следования", 1.0, 0.1, 3.0, 0.1));
    private final BooleanSetting strafe = this.add(
            new BooleanSetting("Стрейф", false));
    private final NumberSetting strafeSpeed = this.add(
            new NumberSetting("Скорость стрейфа", 1.0, 0.2, 4.0, 0.2));
    private final BooleanSetting jump = this.add(
            new BooleanSetting("Прыгать", false));

    // ── выживаемость ────────────────────────────────────────────────────────
    private final NumberSetting maxHealth = this.add(
            new NumberSetting("Здоровье (HP)", 20.0, 1.0, 200.0, 1.0));
    private final BooleanSetting regeneration = this.add(
            new BooleanSetting("Регенерация", false));
    private final NumberSetting regenInterval = this.add(
            new NumberSetting("Тик регенерации", 20.0, 5.0, 100.0, 5.0));
    private final ModeSetting fireResistance = this.add(
            new ModeSetting("Огнестойкость", "Off", POTION_LEVELS));
    private final ModeSetting resistance = this.add(
            new ModeSetting("Сопротивление", "Off", POTION_LEVELS));
    private final ModeSetting speedBoost = this.add(
            new ModeSetting("Скорость", "Off", POTION_LEVELS));
    private final BooleanSetting immuneToFire = this.add(
            new BooleanSetting("Иммунитет к огню", false));
    private final BooleanSetting immuneToExplosions = this.add(
            new BooleanSetting("Иммунитет к взрывам", false));

    // ── бой ─────────────────────────────────────────────────────────────────
    private final BooleanSetting attackTarget = this.add(
            new BooleanSetting("Атаковать цель", false));
    private final NumberSetting attackDamage = this.add(
            new NumberSetting("Сила атаки", 6.0, 1.0, 40.0, 1.0));
    private final NumberSetting eatAtHealth = this.add(
            new NumberSetting("Есть при HP <", 0.0, 0.0, 20.0, 1.0));

    // ── визуал и сеть ───────────────────────────────────────────────────────
    private final BooleanSetting invisible = this.add(
            new BooleanSetting("Невидимость", false));
    private final BooleanSetting glowing = this.add(
            new BooleanSetting("Свечение", false));
    private final NumberSetting simulatedPing = this.add(
            new NumberSetting("Пинг (мс)", 0.0, 0.0, 1000.0, 50.0));

    private final List<Player> bots = new ArrayList<>();
    /** Сколько болванок модуль намерен держать (растёт/падает вместе с «Кол-во ботов»). */
    private int slots;
    /** Имя, с которым созданы текущие болванки: при смене имени пересоздаём. */
    private String spawnedName = "";
    /** Первый спавн после включения модуля ещё не удался (мир не был загружен). */
    private boolean initialSpawn = true;

    private FakePlayerModule() {
        super("Fake Player", "Фейковые игроки: тотем, щит, броня, поведение", ModuleCategory.COMBAT);

        this.armorType.visibleWhen(() -> !this.copyInventory.get());
        this.protection.visibleWhen(() -> !this.copyInventory.get() && !this.armorType.is("Нет"));
        this.heldItem.visibleWhen(() -> !this.copyInventory.get());

        this.totemMode.visibleWhen(this.useTotem::get);
        this.totemCount.visibleWhen(() -> this.useTotem.get() && !this.infiniteTotems.get());
        this.infiniteTotems.visibleWhen(this.useTotem::get);
        this.shieldMode.visibleWhen(this.useShield::get);

        this.followSpeed.visibleWhen(this.followPlayer::get);
        this.strafeSpeed.visibleWhen(this.strafe::get);
        this.regenInterval.visibleWhen(this.regeneration::get);
        this.attackDamage.visibleWhen(this.attackTarget::get);
    }

    // ── жизненный цикл ──────────────────────────────────────────────────────

    @Override
    protected void onEnable() {
        this.sync();
        this.spawnedName = this.name();
        this.slots = this.wantedCount();
        this.initialSpawn = true;
        this.bots.clear();
        for (int i = 0; i < this.slots; i++) {
            this.spawnOne(i);
        }
        if (this.bots.isEmpty()) {
            ChatMessages.displayError("FakePlayer: не удалось создать болванку (нужен загруженный мир)");
        }
    }

    @Override
    protected void onDisable() {
        this.despawnAll();
        this.slots = 0;
        FakePlayerSettings.reset();
    }

    @Override
    protected void onTick() {
        this.sync();
        this.maintain();
    }

    /** Оставляет в списке только живых болванок этого мира и добирает недостающих. */
    private void maintain() {
        String name = this.name();
        if (!name.equals(this.spawnedName)) {
            this.despawnAll();
            this.spawnedName = name;
            this.slots = this.wantedCount();
        }

        var level = Minecraft.getInstance().level;
        this.bots.removeIf(bot -> bot == null || bot.isRemoved() || bot.level() != level);

        int wanted = this.wantedCount();
        boolean grew = wanted > this.slots;
        if (grew) {
            this.slots = wanted;
        } else if (wanted < this.slots) {
            this.slots = wanted;
            while (this.bots.size() > this.slots) {
                FakeplayerCommand.INSTANCE.despawn(this.bots.remove(this.bots.size() - 1));
            }
        }

        // «Авто-респавн» касается только убитых болванок: первый спавн и добавление
        // новых слотов выполняются всегда, иначе модуль «молчал» бы до перезапуска.
        if (this.bots.size() < this.slots && (this.initialSpawn || this.autoRespawn.get() || grew)) {
            for (int index = 0; index < this.slots && this.bots.size() < this.slots; index++) {
                if (this.slotFree(index)) {
                    this.spawnOne(index);
                }
            }
            if (!this.bots.isEmpty()) {
                this.initialSpawn = false;
            }
        }
    }

    private boolean slotFree(int index) {
        String name = this.slotName(index);
        for (Player bot : this.bots) {
            if (bot.getScoreboardName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }

    // ── настройки → конфиг болванки ─────────────────────────────────────────

    /** Передаёт текущие настройки в {@link FakePlayerSettings}. */
    private void sync() {
        FakePlayerSettings.apply(new FakePlayerConfig()
                .copyInventory(this.copyInventory.get())
                .armorType(indexOf(this.armorType.get(), ARMOR_MODES))
                .protection((int) Math.round(this.protection.get()))
                .heldItem(indexOf(this.heldItem.get(), HELD_MODES))
                .useTotem(this.useTotem.get())
                .totemMode(indexOf(this.totemMode.get(), MODES))
                .totemCount((int) Math.round(this.totemCount.get()))
                .infiniteTotems(this.infiniteTotems.get())
                .useShield(this.useShield.get())
                .shieldMode(indexOf(this.shieldMode.get(), MODES))
                .followPlayer(this.followPlayer.get())
                .followSpeed(this.followSpeed.get())
                .strafe(this.strafe.get())
                .strafeSpeed(this.strafeSpeed.get())
                .jump(this.jump.get())
                .maxHealth(this.maxHealth.get())
                .regeneration(this.regeneration.get())
                .regenInterval((int) Math.round(this.regenInterval.get()))
                .fireResistance(indexOf(this.fireResistance.get(), POTION_LEVELS))
                .resistance(indexOf(this.resistance.get(), POTION_LEVELS))
                .speedBoost(indexOf(this.speedBoost.get(), POTION_LEVELS))
                .immuneToFire(this.immuneToFire.get())
                .immuneToExplosions(this.immuneToExplosions.get())
                .attackTarget(this.attackTarget.get())
                .attackDamage(this.attackDamage.get())
                .eatAtHealth(this.eatAtHealth.get())
                .invisible(this.invisible.get())
                .glowing(this.glowing.get())
                .simulatedPing((int) Math.round(this.simulatedPing.get())));
    }

    // ── спавн / деспавн ─────────────────────────────────────────────────────

    private void spawnOne(int index) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return;
        }
        Player bot = FakeplayerCommand.INSTANCE.spawn(this.slotName(index), this.spawnDistance.get(),
                -35.0F + index * 35.0F, true);
        if (bot != null) {
            this.bots.add(bot);
        }
    }

    private void despawnAll() {
        for (Player bot : this.bots) {
            if (bot != null && !bot.isRemoved()) {
                FakeplayerCommand.INSTANCE.despawn(bot);
            }
        }
        this.bots.clear();
    }

    private String name() {
        String name = this.botName.get();
        if (name == null || name.isBlank()) {
            return FALLBACK_NAME;
        }
        return name.trim();
    }

    private String slotName(int index) {
        return index == 0 ? this.name() : this.name() + (index + 1);
    }

    private int wantedCount() {
        return Math.max(1, (int) Math.round(this.botCount.get()));
    }

    /** Первая болванка (для отладки и других модулей). */
    public Player bot() {
        return this.bots.isEmpty() ? null : this.bots.get(0);
    }

    /** Все болванки, созданные модулем. */
    public List<Player> bots() {
        return List.copyOf(this.bots);
    }

    private static int indexOf(String value, String[] values) {
        if (value != null) {
            for (int i = 0; i < values.length; i++) {
                if (values[i].equalsIgnoreCase(value)) {
                    return i;
                }
            }
        }
        return 0;
    }
}

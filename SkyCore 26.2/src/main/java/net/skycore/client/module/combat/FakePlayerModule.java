package net.skycore.client.module.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.skycore.client.combat.FakePlayerSettings;
import net.skycore.client.command.FakeplayerCommand;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.command.ChatMessages;

/**
 * FakePlayer — фейковый игрок (болванка для тестов урона и тотемов).
 *
 * <p>Сам фейк-игрок живёт в {@link FakeplayerCommand}: там физика, урон, тотем и
 * кристаллический взрыв. Модуль даёт к нему настройки и управляет его жизненным
 * циклом: включили — болванка появилась рядом, выключили — убрали.
 *
 * <p>Настройки запроса:
 * <ul>
 *   <li><b>Копировать инвентарь</b> — броня, левая и правая рука копируются у вас
 *       (иначе болванка получает незерит с починкой и тотем);</li>
 *   <li><b>Использовать тотем</b>, режим {@code Legit / Normal / Hard / Extra},
 *       <b>количество</b> 0…300 и <b>бесконечные тотемы</b> — сколько раз и на каком
 *       здоровье болванка тратит тотем;</li>
 *   <li><b>Использовать щит</b> и такой же режим — когда болванка поднимает щит.</li>
 * </ul>
 *
 * <p>Смысл режимов один и тот же для тотема и щита: чем «выше» режим, тем раньше
 * болванка защищается, то есть тем труднее её убить. Пороги и условия собраны в
 * {@link FakePlayerSettings}.
 */
public final class FakePlayerModule extends ClientModule {

    public static final FakePlayerModule INSTANCE = new FakePlayerModule();

    private static final String BOT_NAME = "FakePlayer";
    private static final String[] MODES = {"Legit", "Normal", "Hard", "Extra"};

    private final BooleanSetting copyInventory = this.add(
            new BooleanSetting("Копировать инвентарь", false));
    private final BooleanSetting useTotem = this.add(new BooleanSetting("Использовать тотем", true));
    private final ModeSetting totemMode = this.add(
            new ModeSetting("Режим тотема", "Normal", MODES));
    private final NumberSetting totemCount = this.add(
            new NumberSetting("Кол-во тотемов", 1.0, 0.0, 300.0, 1.0));
    private final BooleanSetting infiniteTotems = this.add(
            new BooleanSetting("Бесконечные тотемы", false));
    private final BooleanSetting useShield = this.add(new BooleanSetting("Использовать щит", false));
    private final ModeSetting shieldMode = this.add(
            new ModeSetting("Режим щита", "Normal", MODES));

    private Player bot;

    private FakePlayerModule() {
        super("Fake Player", "Фейковый игрок с тотемом и щитом для тестов", ModuleCategory.COMBAT);
        this.totemMode.visibleWhen(() -> this.useTotem.get());
        this.totemCount.visibleWhen(() -> this.useTotem.get() && !this.infiniteTotems.get());
        this.infiniteTotems.visibleWhen(() -> this.useTotem.get());
        this.shieldMode.visibleWhen(() -> this.useShield.get());
    }

    @Override
    protected void onEnable() {
        sync();
        if (!spawn()) {
            ChatMessages.displayError("FakePlayer: не удалось создать болванку (нужен мир)");
        }
    }

    @Override
    protected void onDisable() {
        despawn();
        FakePlayerSettings.reset();
    }

    @Override
    protected void onTick() {
        // настройки читаются каждый тик: менять режим можно, не пересоздавая болванку
        sync();
        if (this.bot != null && (this.bot.isRemoved() || this.bot.level() != Minecraft.getInstance().level)) {
            this.bot = null;
        }
        if (this.bot == null) {
            spawn();
        }
    }

    /** Передаёт текущие настройки в {@link FakePlayerSettings}. */
    private void sync() {
        FakePlayerSettings.configure(this.copyInventory.get(), this.useTotem.get(),
                indexOf(this.totemMode.get(), MODES, 1), (int) Math.round(this.totemCount.get()),
                this.infiniteTotems.get(), this.useShield.get(),
                indexOf(this.shieldMode.get(), MODES, 1));
    }

    private boolean spawn() {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return false;
        }
        Player spawned = FakeplayerCommand.INSTANCE.spawn(BOT_NAME);
        if (spawned == null) {
            return false;
        }
        this.bot = spawned;
        return true;
    }

    private void despawn() {
        Player current = this.bot;
        this.bot = null;
        if (current != null && !current.isRemoved()) {
            FakeplayerCommand.INSTANCE.despawn(current);
        }
    }

    /** Текущая болванка (для отладки и других модулей). */
    public Player bot() {
        return this.bot;
    }

    private static int indexOf(String value, String[] values, int fallback) {
        if (value != null) {
            for (int i = 0; i < values.length; i++) {
                if (values[i].equalsIgnoreCase(value)) {
                    return i;
                }
            }
        }
        return fallback;
    }
}

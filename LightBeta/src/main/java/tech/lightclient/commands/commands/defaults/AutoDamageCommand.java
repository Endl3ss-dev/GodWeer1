package tech.lightclient.commands.commands.defaults;

import tech.lightclient.LightClient;
import tech.lightclient.client.utils.client.ILogger;
import tech.lightclient.commands.Command;
import tech.lightclient.commands.argument.IArgConsumer;
import tech.lightclient.commands.exception.CommandException;
import tech.lightclient.commands.helpers.TabCompleteHelper;
import tech.lightclient.modules.impl.combat.AuraModule;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class AutoDamageCommand extends Command implements ILogger {
    protected AutoDamageCommand() {
        super("autodamage", "ad");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        AuraModule aura = AuraModule.getInstance();
        if (aura == null) {
            logDirect(Formatting.RED + "Aura модуль не найден!", Formatting.GRAY);
            return;
        }

        if (!args.hasAny()) {
            // Show current settings
            String autoDamageName = aura.getAutoDamageItemName();
            String autoHealName = aura.getAutoHealItemName();
            logDirect("Текущие настройки Авто Урона:", Formatting.GRAY);
            logDirect("Урон (d): " + Formatting.RED + autoDamageName + Formatting.GRAY + " - предмет урона для автосвапа", Formatting.GRAY);
            logDirect("Хил (h): " + Formatting.GREEN + autoHealName + Formatting.GRAY + " - предмет здоровья для автосвапа", Formatting.GRAY);
            logDirect("Использование:", Formatting.GRAY);
            logDirect(Formatting.YELLOW + ".autodamage d <название>" + Formatting.GRAY + " - предмет для урона (автосвап на урон)", Formatting.GRAY);
            logDirect(Formatting.YELLOW + ".autodamage h <название>" + Formatting.GRAY + " - предмет для здоровья (автосвап на хил)", Formatting.GRAY);
            logDirect("Без названия запоминается предмет в вашей руке", Formatting.GRAY);
            logDirect("Логика:", Formatting.GRAY);
            logDirect("- У противника меч/арбалет/булава → свап на хил", Formatting.GRAY);
            logDirect("- У противника что-то другое → свап на урон", Formatting.GRAY);
            logDirect("- Если вы сами свапнули на урон → автоматика ждёт вашего свапа на хил", Formatting.GRAY);
            return;
        }

        // Get mode (d or h)
        String mode = args.getString().toLowerCase();

        String itemName;
        if (args.hasAny()) {
            itemName = args.getString();
        } else {
            // Имя не указано - запоминаем предмет из руки
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.player.getMainHandStack().isEmpty()) {
                logDirect(Formatting.RED + "Укажите название предмета или возьмите его в основную руку!", Formatting.GRAY);
                return;
            }
            itemName = mc.player.getMainHandStack().getName().getString().replaceAll("§.", "");
            logDirect("Запомнен предмет из руки: " + Formatting.YELLOW + itemName, Formatting.GRAY);
        }

        if (mode.equals("d") || mode.equals("damage") || mode.equals("урон")) {
            // Set damage item
            aura.setAutoDamageItemName(itemName);
            logDirect("Установлен предмет урона для автосвапа: " + Formatting.RED + itemName + Formatting.GRAY, Formatting.GRAY);
            logDirect("Предмет будет искаться в инвентаре для автосвапа в левую руку", Formatting.GRAY);
        } else if (mode.equals("h") || mode.equals("heal") || mode.equals("хил") || mode.equals("здоровье")) {
            // Set heal item  
            aura.setAutoHealItemName(itemName);
            logDirect("Установлен предмет здоровья для автосвапа: " + Formatting.GREEN + itemName + Formatting.GRAY, Formatting.GRAY);
            logDirect("Предмет будет искаться в инвентаре для автосвапа в левую руку", Formatting.GRAY);
        } else {
            logDirect(Formatting.RED + "Неизвестный режим: " + mode + Formatting.GRAY, Formatting.GRAY);
            logDirect("Используйте " + Formatting.YELLOW + "d" + Formatting.GRAY + " для урона или " + Formatting.YELLOW + "h" + Formatting.GRAY + " для здоровья", Formatting.GRAY);
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        TabCompleteHelper helper = new TabCompleteHelper().sortAlphabetically();
        
        if (!args.hasAny()) {
            // First argument: mode
            return helper.prepend("d", "h", "damage", "heal", "урон", "хил").stream();
        }
        
        String mode = args.getString().toLowerCase();
        if (!args.hasAny()) {
            // Second argument: item name suggestions based on mode
            if (mode.equals("d") || mode.equals("damage") || mode.equals("урон")) {
                return helper.prepend("Ярост", "Урон", "Сила", "Атака", "Клинок").stream();
            } else if (mode.equals("h") || mode.equals("heal") || mode.equals("хил")) {
                return helper.prepend("Бестия", "Здоровье", "Хил", "Лечение", "Щит").stream();
            }
        }
        
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Устанавливает названия предметов для автоматического свапа между уроном и хилом в Aura";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Устанавливает названия предметов для автоматического свапа между уроном и хилом в левой руке при использовании Aura",
                "",
                "Использование:",
                "> autodamage - показать текущие настройки",
                "> autodamage d <название> - установить предмет для урона",
                "> autodamage h <название> - установить предмет для здоровья",
                "> autodamage d / autodamage h - запомнить предмет из вашей руки",
                "",
                "Логика автоматического свапа:",
                "- У противника меч/арбалет/булава → автоматически свапает на хил",
                "- У противника что-то другое → автоматически свапает на урон",
                "- Если вы сами вручную свапнули на урон (инвентарь/AutoSwap) → система не вмешивается,",
                "  пока вы сами не свапнете обратно на предмет здоровья",
                "",
                "Примеры:",
                "> autodamage d Ярост - искать предметы с 'Ярост' в названии для автосвапа на урон",
                "> autodamage h Бестия - установить 'Бестия' как предмет для автосвапа на хил",
                "",
                "Примечание:",
                "- Предметы ищутся в инвентаре по части названия",
                "- Оружие противника: любой меч, арбалет или булава",
                "- Автосвап работает только при включенной Aura и активном противнике",
                "- Приоритет: ручной свап на урон блокирует автоматику до ручного возврата хила"
        );
    }
}
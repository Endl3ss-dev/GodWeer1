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
public class MaxDamageCommand extends Command implements ILogger {
    protected MaxDamageCommand() {
        super("maxdamage", "md");
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
            String damageName = aura.getMaxDamageItemName();
            String healName = aura.getMaxHealItemName();
            logDirect("Текущие настройки предметов:", Formatting.GRAY);
            logDirect("Урон (d): " + Formatting.RED + damageName + Formatting.GRAY + " - ищется в инвентаре", Formatting.GRAY);
            logDirect("Здоровье (h): " + Formatting.GREEN + healName + Formatting.GRAY + " - должен быть в левой руке", Formatting.GRAY);
            logDirect("Использование:", Formatting.GRAY);
            logDirect(Formatting.YELLOW + ".maxdamage d <название>" + Formatting.GRAY + " - предмет для урона", Formatting.GRAY);
            logDirect(Formatting.YELLOW + ".maxdamage h <название>" + Formatting.GRAY + " - предмет для здоровья", Formatting.GRAY);
            logDirect("Без названия запоминается предмет в вашей руке", Formatting.GRAY);
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
            aura.setMaxDamageItemName(itemName);
            logDirect("Установлен предмет урона: " + Formatting.RED + itemName + Formatting.GRAY, Formatting.GRAY);
            logDirect("Предмет будет искаться в инвентаре и свапаться в левую руку при атаке", Formatting.GRAY);
        } else if (mode.equals("h") || mode.equals("heal") || mode.equals("хил") || mode.equals("здоровье")) {
            // Set heal item
            aura.setMaxHealItemName(itemName);
            logDirect("Установлен предмет здоровья: " + Formatting.GREEN + itemName + Formatting.GRAY, Formatting.GRAY);
            logDirect("После удара предмет урона свапается обратно на этот предмет", Formatting.GRAY);
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
        return "Устанавливает названия предметов для урона и здоровья в Aura";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Устанавливает названия предметов для быстрого свапа в левой руке при использовании максимального урона в Aura",
                "",
                "Использование:",
                "> maxdamage - показать текущие настройки",
                "> maxdamage d <название> - установить предмет для урона (ищется в инвентаре)",
                "> maxdamage h <название> - установить предмет для здоровья",
                "> maxdamage d / maxdamage h - запомнить предмет, который вы держите в руке",
                "",
                "Примеры:",
                "> maxdamage d Ярост - искать предметы с 'Ярост' в названии для урона",
                "> maxdamage h Бестия - установить 'Бестия' как предмет для здоровья",
                "",
                "Примечание:",
                "- Оба предмета ищутся в инвентаре по части названия",
                "- Перед ударом предмет урона свапается в левую руку, сразу после удара возвращается предмет здоровья",
                "- Свапы уходят подряд в один тик, поэтому бафы обоих предметов складываются",
                "- Для работы необходима включенная опция 'Макс Урон' в Aura"
        );
    }
}
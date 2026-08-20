package org.privates.client.utils.autosort;

import lombok.Getter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;

import java.io.*;
import java.util.*;


public class AutoSorterManager {
    @Getter
    private static final List<SortItem> items = new ArrayList<>();

    @Getter
    private static final Map<String, SortItem> itemsByName = new HashMap<>();

    public static boolean isValidItem(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }

        for (SortItem sortItem : items) {
            if (sortItem.item.equals(itemStack.getItem())) {
                return true;
            }
        }
        return false;
    }


    public static final SortItem netheriteHelmet = new SortItem("Незеритовый шлем", Items.NETHERITE_HELMET, "Незеритовый шлем");
    public static final SortItem netheriteChestplate = new SortItem("Незеритовый нагрудник", Items.NETHERITE_CHESTPLATE, "Незеритовый нагрудник");
    public static final SortItem netheriteLeggings = new SortItem("Незеритовые штаны", Items.NETHERITE_LEGGINGS, "Незеритовые штаны");
    public static final SortItem netheriteBoots = new SortItem("Незеритовые ботинки", Items.NETHERITE_BOOTS, "Незеритовые ботинки");

    // Оружие и инструменты
    public static final SortItem netheriteSword = new SortItem("Незеритовый меч", Items.NETHERITE_SWORD, "Незеритовый меч");
    public static final SortItem netheritePickaxe = new SortItem("Незеритовая кирка", Items.NETHERITE_PICKAXE, "Незеритовая кирка");
    public static final SortItem netheriteAxe = new SortItem("Незеритовый топор", Items.NETHERITE_AXE, "Незеритовый топор");
    public static final SortItem netheriteShovel = new SortItem("Незеритовая лопата", Items.NETHERITE_SHOVEL, "Незеритовая лопата");

    // Элитры
    public static final SortItem elytra = new SortItem("Элитры", Items.ELYTRA, "Элитра");

    // Еда и полезные предметы
    public static final SortItem goldenApple = new SortItem("Золотое яблоко", Items.GOLDEN_APPLE, "Золотое яблоко");
    public static final SortItem enchantedGoldenApple = new SortItem("Зачарованное золотое яблоко", Items.ENCHANTED_GOLDEN_APPLE, "Зачарованное золотое яблоко");
    public static final SortItem goldenCarrot = new SortItem("Золотая морковь", Items.GOLDEN_CARROT, "Золотая морковь");
    public static final SortItem enderPearl = new SortItem("Эндер-жемчуг", Items.ENDER_PEARL, "Эндер-жемчуг");
    public static final SortItem fireworkRocket = new SortItem("Фейерверк", Items.FIREWORK_ROCKET, "Фейерверк");

    // Зелья
    public static final SortItem healingPotion = new SortItem("Зелье исцеления", Items.POTION, "Зелье исцеления");
    public static final SortItem strengthPotion = new SortItem("Зелье силы", Items.POTION, "Зелье силы");
    public static final SortItem speedPotion = new SortItem("Зелье скорости", Items.POTION, "Зелье скорости");
    public static final SortItem turtleMasterPotion = new SortItem("Зелье черепашьей мощи", Items.POTION, "Зелье черепашьей мощи");
    public static final SortItem winnerPotion = new SortItem("Зелье победителя", Items.POTION, "Зелье победителя");

    // Специальные предметы
    public static final SortItem explosiveTrap = new SortItem("Взрывная трапка", Items.PRISMARINE_SHARD, "Взрывная трапка");
    public static final SortItem stun = new SortItem("Стан", Items.NETHER_STAR, "Стан");
    public static final SortItem artifact = new SortItem("Артефакт", Items.COOKIE, "Артефакт");
    public static final SortItem totemOfUndying = new SortItem("Тотем бессмертия", Items.TOTEM_OF_UNDYING, "Тотем бессмертия");
    public static final SortItem backpack = new SortItem("Рюкзак", Items.SHULKER_BOX, "Рюкзак");
    public static final SortItem expBottle = new SortItem("Пузырек опыта", Items.EXPERIENCE_BOTTLE, "Пузырек опыта");

    // Сферы и талисманы
    public static final SortItem armortalitySphere = new SortItem("Сфера Armortality", Items.PLAYER_HEAD, "Сфера Armortality");
    public static final SortItem immortalitySphere = new SortItem("Сфера Immortality", Items.PLAYER_HEAD, "Сфера Immortality");
    public static final SortItem damage3Sphere = new SortItem("Сфера Урон 3", Items.PLAYER_HEAD, "Сфера Урон 3");
    public static final SortItem speed3Sphere = new SortItem("Сфера Скорость 3", Items.PLAYER_HEAD, "Сфера Скорость 3");
    public static final SortItem stingerSphere = new SortItem("Сфера Stinger", Items.PLAYER_HEAD, "Сфера Stinger");
    public static final SortItem eternitySphere = new SortItem("Сфера Eternity", Items.PLAYER_HEAD, "Сфера Eternity");
    public static final SortItem damage3Armor2Sphere = new SortItem("Сфера Урон 3 Броня 2", Items.PLAYER_HEAD, "Сфера Урон 3 Броня 2");
    public static final SortItem damage2Armor3Sphere = new SortItem("Сфера Урон 2 Броня 3", Items.PLAYER_HEAD, "Сфера Урон 2 Броня 3");

    // Талисманы
    public static final SortItem eternityTalisman = new SortItem("Талисман Eternity", Items.TOTEM_OF_UNDYING, "Талисман Eternity");
    public static final SortItem stingerTalisman = new SortItem("Талисман Stinger", Items.TOTEM_OF_UNDYING, "Талисман Stinger");
    public static final SortItem infinityTalisman = new SortItem("Талисман Infinity", Items.TOTEM_OF_UNDYING, "Талисман Infinity");
    public static final SortItem satireTalisman = new SortItem("Талисман Сатиры", Items.TOTEM_OF_UNDYING, "Талисман Сатиры");
    public static final SortItem commonTotem = new SortItem("Обычный тотем", Items.TOTEM_OF_UNDYING, "Тотем бессмертия");

    // Инициализация менеджера
    public static void init() {
        // Инициализация характеристик предметов

        speedPotion.lore = of("Скорость III");
        strengthPotion.lore = of("Сила III");

        // Незеритовые доспехи

        netheriteHelmet.enchantments = of("protection:5", "unbreaking:2", "mending:1");
        netheriteChestplate.enchantments = of("protection:5", "unbreaking:2", "mending:1");
        netheriteLeggings.enchantments = of("protection:5", "unbreaking:2", "mending:1");
        netheriteBoots.enchantments = of("protection:5", "depth_strider:3", "unbreaking:2", "mending:1");

        // Оружие и инструменты
        netheriteSword.enchantments = of("sharpness:7");
        netheritePickaxe.enchantments = of("efficiency:5");
        netheriteAxe.enchantments = of("efficiency:5", "fortune:3", "unbreaking:3");

        // Элитры
        elytra.enchantments = of("unbreaking:5", "mending:1");

        // Специальные предметы
        explosiveTrap.lore = of("при взрыве наносит урон в радиусе 3 блоков");
        stun.lore = of("игроки в нем не могут использовать");

        // Зелья
        winnerPotion.lore = of("Сила III", "Скорость III", "Невидимость", "Спешка II", "Регенерация II", "Сопротивление II", "Исцеление II");

        // Сферы и талисманы
        armortalitySphere.effect = of("hms-health:2", "hms-armor:2", "hms-damage:2");
        immortalitySphere.effect = of("hms-damage:3", "hms-speed:2");
        damage3Sphere.effect = of("hms-damage:3");
        speed3Sphere.effect = of("hms-speed:3");
        stingerSphere.effect = of("hms-speed:1", "hms-armor:2", "hms-damage:2");
        eternitySphere.effect = of("hms-speed:2", "hms-armor:2", "hms-damage:2");
        damage3Armor2Sphere.effect = of("hms-damage:3", "hms-armor:2");
        damage2Armor3Sphere.effect = of("hms-damage:2", "hms-armor:3");

        // Талисманы
        eternityTalisman.lore = of("Талисман ᴇᴛᴇʀɴɪᴛʏ");
        stingerTalisman.lore = of("Талисман sᴛɪɴɢᴇʀ");
        infinityTalisman.lore = of("Талисман ɪɴғɪɴɪᴛʏ");
        satireTalisman.lore = of("Урон IV");

        // Добавление всех предметов в списки
        SortItem[] allItems = {
                netheriteHelmet, netheriteChestplate, netheriteLeggings, netheriteBoots,
                netheriteSword, netheritePickaxe, netheriteAxe, netheriteShovel,
                elytra, goldenApple, enchantedGoldenApple, goldenCarrot, enderPearl, fireworkRocket,
                healingPotion, strengthPotion, speedPotion, turtleMasterPotion, winnerPotion,
                explosiveTrap, stun, artifact, totemOfUndying, backpack, expBottle,
                armortalitySphere, immortalitySphere, damage3Sphere, speed3Sphere, stingerSphere, eternitySphere,
                damage3Armor2Sphere, damage2Armor3Sphere, eternityTalisman, stingerTalisman,
                infinityTalisman, satireTalisman, commonTotem
        };

        items.addAll(Arrays.asList(allItems));

        // Также заполняем карту для быстрого поиска по имени
        for (SortItem item : allItems) {
            itemsByName.put(item.name.toLowerCase(), item);
            if (!item.searchName.isEmpty()) {
                itemsByName.put(item.searchName.toLowerCase(), item);
            }
        }
    }

    // Получение предмета по имени
    public static SortItem getItemByName(String name) {
        return itemsByName.get(name.toLowerCase());
    }

    // Проверка наличия предмета в списке
    public static boolean containsItem(String name) {
        return itemsByName.containsKey(name.toLowerCase());
    }

    // Методы для работы с конфигурацией
    public static void save() {
        File file = new File("config/excellent/autosort.txt");

        if (file.exists()) {
            file.delete();
        }

        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            file.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

            for (SortItem item : items) {
                if (item.enabled) {
                    bufferedWriter.write(item.name + ":" + item.maxPrice + ":" + item.maxCount + ":" + item.slotIndex + "\n");
                }
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        File file = new File("config/excellent/autosort.txt");

        if (!file.exists()) {
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                try {
                    String[] parts = line.split(":");
                    if (parts.length >= 4) {
                        String name = parts[0];
                        int price = Integer.parseInt(parts[1]);
                        int count = Integer.parseInt(parts[2]);
                        int slotIndex = Integer.parseInt(parts[3]);

                        SortItem item = getItemByName(name);
                        if (item != null) {
                            item.enabled = true;
                            item.maxPrice = price;
                            item.maxCount = count;
                            item.slotIndex = slotIndex;
                        }
                    }
                } catch (Exception e) {
                    // Пропускаем некорректные строки
                    e.printStackTrace();
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Вспомогательные методы
    public static List<String> of(String... elements) {
        return Arrays.asList(elements);
    }

    public static String get(Effect effect, double level) {
        return effect.getName().replaceAll("effect.minecraft.", "") + ":" + level;
    }

    public static String get(Enchantment enchantment, double level) {
        return enchantment.getName().replaceAll("enchantment.minecraft.", "") + ":" + (int) level;
    }

    public static String get(Attribute attribute, double value) {
        return attribute.getAttributeName() + value;
    }

    // Класс предмета для автосортировки
    public static class SortItem {
        public final String name;
        public final Item item;
        public final String searchName;
        public boolean enabled;
        public int maxPrice;
        public int maxCount;
        public int slotIndex;
        public List<String> enchantments; // Список зачарований предмета
        public List<String> lore; // Описание предмета
        public List<String> effect; // Эффекты предмета
        public List<String> attributes; // Атрибуты предмета

        public SortItem(String name, Item item, String searchName) {
            this.name = name;
            this.item = item;
            this.searchName = searchName;
            this.enabled = false;
            this.maxPrice = 0;
            this.maxCount = 1;
            this.slotIndex = -1;
            this.enchantments = new ArrayList<>();
            this.lore = new ArrayList<>();
            this.effect = new ArrayList<>();
            this.attributes = new ArrayList<>();
        }

        // Проверка совпадения предмета
        public boolean matches(ItemStack stack) {
            if (stack.getItem() != this.item) {
                return false;
            }

            // Если название предмета указано в описании, проверяем его
            if (!lore.isEmpty()) {
                if (!stack.hasDisplayName() && !stack.hasTag()) {
                    return false;
                }

                // Проверка лора
                boolean hasLore = false;
                if (stack.hasTag() && stack.getTag().contains("display", 10)) {
                    net.minecraft.nbt.CompoundNBT displayTag = stack.getTag().getCompound("display");
                    if (displayTag.contains("Lore", 9)) {
                        net.minecraft.nbt.ListNBT loreList = displayTag.getList("Lore", 8);

                        // Проверяем каждую строку лора
                        for (String loreLine : this.lore) {
                            hasLore = false;
                            for (int i = 0; i < loreList.size(); i++) {
                                String itemLore = loreList.getString(i);
                                if (itemLore.contains(loreLine)) {
                                    hasLore = true;
                                    break;
                                }
                            }
                            if (!hasLore) {
                                return false;
                            }
                        }
                    }
                }
            }

            return true;
        }
    }
}
package org.privates.client.utils.autobuy;

import lombok.Getter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.registry.Registry;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class AutoBuyManager {
    @Getter
    private static final List<AutoBuyItem> items = new ArrayList<>();
    @Getter
    public static final List<CustomAutoBuyItem> customAutoBuyItemList = new ArrayList<>();

    @Getter
    public static ItemStack selectedItem = ItemStack.EMPTY;

    public static boolean isValidItem(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }


        for (CustomAutoBuyItem customItem : customAutoBuyItemList) {
            if (customItem.item.equals(itemStack.getItem())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidSelectedItem() {
        return isValidItem(selectedItem);
    }

    public static final CustomAutoBuyItem eternityPickaxe = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, "кирка этернити");
    public static final CustomAutoBuyItem MHPickaxe3 = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, "");
    public static final CustomAutoBuyItem stingerPickaxe = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, "кирка stinger");
    public static final CustomAutoBuyItem goldenPickaxe = new CustomAutoBuyItem(Items.GOLDEN_PICKAXE, 0, "золотая кирка джейка");

    public static final CustomAutoBuyItem cerberusSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, "сфера цербера");
    public static final CustomAutoBuyItem fleshSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, "сфера флеша");
    public static final CustomAutoBuyItem damageSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, "сфера на урон 3");
    public static final CustomAutoBuyItem speedSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, "сфера на скорость 3");
    public static final CustomAutoBuyItem candy = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, "");
    public static final CustomAutoBuyItem et = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "талисман eternity");
    public static final CustomAutoBuyItem inf = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "талисман infinity");
    public static final CustomAutoBuyItem sting = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "талисман stinger");
    public static final CustomAutoBuyItem satir = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "талисман сатиры");
    public static final CustomAutoBuyItem mif = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "мифический талисман");
    public static final CustomAutoBuyItem eternitySphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, "");
    public static final CustomAutoBuyItem stingerSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, "");
    public static final CustomAutoBuyItem armor = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "");
    public static final CustomAutoBuyItem immSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, "");

    public static final CustomAutoBuyItem ur = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "");
    public static final CustomAutoBuyItem skr = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "");
    public static final CustomAutoBuyItem run = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "");
    public static final CustomAutoBuyItem a = new CustomAutoBuyItem(Items.TNT, 0, "Динамит А");
    public static final CustomAutoBuyItem st = new CustomAutoBuyItem(Items.TNT, 0, "");
    public static final CustomAutoBuyItem vol = new CustomAutoBuyItem(Items.TNT, 0, "Разрывная волна");
    public static final CustomAutoBuyItem swor = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, "");

    public static final CustomAutoBuyItem mythicalSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, "Мифическая сфера");
    public static final CustomAutoBuyItem mythicalSphere2 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, "");
    public static final CustomAutoBuyItem sp = new CustomAutoBuyItem(Items.SPAWNER, 0, "");


    public static final CustomAutoBuyItem goldenSpawner = new CustomAutoBuyItem(Items.SPAWNER, 0, "золотой спавнер");


    /// * 272 */                     if (item instanceof CustomAutoBuyItem && (


    public static final CustomAutoBuyItem infinityHelmet = new CustomAutoBuyItem(Items.NETHERITE_HELMET, 0, "Шлем infinity");
    public static final CustomAutoBuyItem GHELM = new CustomAutoBuyItem(Items.GOLDEN_HELMET, 0, "");
    public static final CustomAutoBuyItem infinityChestplate = new CustomAutoBuyItem(Items.NETHERITE_CHESTPLATE, 0, "Нагрудник infinity");
    public static final CustomAutoBuyItem infinityLeggings = new CustomAutoBuyItem(Items.NETHERITE_LEGGINGS, 0, "Штаны Infinity");
    public static final CustomAutoBuyItem infinityBoots = new CustomAutoBuyItem(Items.NETHERITE_BOOTS, 0, "ботинки infinity");

    public static final CustomAutoBuyItem eternityHelmet = new CustomAutoBuyItem(Items.NETHERITE_HELMET, 0, "шлем eternity");
    public static final CustomAutoBuyItem eternityChestplate = new CustomAutoBuyItem(Items.NETHERITE_CHESTPLATE, 0, "нагрдник eternity");
    public static final CustomAutoBuyItem eternityLeggings = new CustomAutoBuyItem(Items.NETHERITE_LEGGINGS, 0, "штаны eternity");
    public static final CustomAutoBuyItem eternityBoots = new CustomAutoBuyItem(Items.NETHERITE_BOOTS, 0, "ботинки eternity");
    public static final CustomAutoBuyItem eternitySword = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, "меч eternity");

    public static final CustomAutoBuyItem stingerHelmet = new CustomAutoBuyItem(Items.NETHERITE_HELMET, 0, "");
    public static final CustomAutoBuyItem stingerChestplate = new CustomAutoBuyItem(Items.NETHERITE_CHESTPLATE, 0, "");
    public static final CustomAutoBuyItem stingerLeggings = new CustomAutoBuyItem(Items.NETHERITE_LEGGINGS, 0, "");
    public static final CustomAutoBuyItem stingerBoots = new CustomAutoBuyItem(Items.NETHERITE_BOOTS, 0, "");
    public static final CustomAutoBuyItem stingerSword = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, "");


    public static final CustomAutoBuyItem helm = new CustomAutoBuyItem(Items.NETHERITE_HELMET, 0, "");
    public static final CustomAutoBuyItem hel = new CustomAutoBuyItem(Items.NETHERITE_HELMET, 0, "");
    public static final CustomAutoBuyItem ches = new CustomAutoBuyItem(Items.NETHERITE_CHESTPLATE, 0, "");
    public static final CustomAutoBuyItem lege = new CustomAutoBuyItem(Items.NETHERITE_LEGGINGS, 0, "");
    public static final CustomAutoBuyItem boo = new CustomAutoBuyItem(Items.NETHERITE_BOOTS, 0, "");
    public static final CustomAutoBuyItem chest = new CustomAutoBuyItem(Items.NETHERITE_CHESTPLATE, 0, "");
    public static final CustomAutoBuyItem legen = new CustomAutoBuyItem(Items.NETHERITE_LEGGINGS, 0, "");
    public static final CustomAutoBuyItem boot = new CustomAutoBuyItem(Items.NETHERITE_BOOTS, 0, "");
    public static final CustomAutoBuyItem ghelm = new CustomAutoBuyItem(Items.GOLDEN_HELMET, 0, "шлем солнца");
    public static final CustomAutoBuyItem explosiveTrap = new CustomAutoBuyItem(Items.PRISMARINE_SHARD, 0, "взрывная трапка");
    public static final CustomAutoBuyItem stan = new CustomAutoBuyItem(Items.NETHER_STAR, 0, "стан");
    public static final CustomAutoBuyItem explosiveSubstance = new CustomAutoBuyItem(Items.CLAY, 0, "взрывчатое вещество");
    public static final CustomAutoBuyItem universalKey = new CustomAutoBuyItem(Items.TRIPWIRE_HOOK, 0, "универсальный ключ");
    public static final CustomAutoBuyItem stealer = new CustomAutoBuyItem(Items.TNT, 0, "стилер");
    public static final CustomAutoBuyItem expBottle100lvl = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0, "опыт 100");
    public static final CustomAutoBuyItem expBottle30lvl = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0, "опыт 30");
    public static final CustomAutoBuyItem expBottle15lvl = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0, "опыт 15");
    public static final CustomAutoBuyItem expBottle50lvl = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0, "опыт 50");
    public static final CustomAutoBuyItem ex = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0, "");
    public static final CustomAutoBuyItem farmerSword = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, "Меч выгодный фарм");
    public static final CustomAutoBuyItem farmerSword2 = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, "kk");
    public static final CustomAutoBuyItem farmerSword3 = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, "kk");
    public static final CustomAutoBuyItem farmerSword4 = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, "kk");
    public static final CustomAutoBuyItem farmerSword5 = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, "kk");
    public static final CustomAutoBuyItem farmerSword6 = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, "kk");
    public static final CustomAutoBuyItem farmerSword7 = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, "kk");
    public static final CustomAutoBuyItem farmerSword8 = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, "kk");
    public static final CustomAutoBuyItem farmerSword9 = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, "kk");
    public static final CustomAutoBuyItem farmerSword10 = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, "kk");
    public static final CustomAutoBuyItem farmerSword11 = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, "kk");
    public static final CustomAutoBuyItem ur2 = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "");
    public static final CustomAutoBuyItem skr2 = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "");
    public static final CustomAutoBuyItem armor2 = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "");
    public static final CustomAutoBuyItem skur = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "");
    public static final CustomAutoBuyItem urar = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "");
    public static final CustomAutoBuyItem skar = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "");


    public static final CustomAutoBuyItem vt = new CustomAutoBuyItem(Items.YELLOW_DYE, 0, "второй шанс");
    public static final CustomAutoBuyItem isz = new CustomAutoBuyItem(Items.POTION, 0, "");
    public static final CustomAutoBuyItem ely = new CustomAutoBuyItem(Items.ELYTRA, 0, "Броневая элитра");
    public static final CustomAutoBuyItem b2 = new CustomAutoBuyItem(Items.TNT, 0, "Динамит Б2");
    public static final CustomAutoBuyItem b = new CustomAutoBuyItem(Items.TNT, 0, "Динамит Б");
    public static final CustomAutoBuyItem motiga = new CustomAutoBuyItem(Items.NETHERITE_HOE, 0, "");
    public static final CustomAutoBuyItem drow = new CustomAutoBuyItem(Items.NETHERITE_AXE, 0, "");
    public static final CustomAutoBuyItem topor = new CustomAutoBuyItem(Items.NETHERITE_AXE, 0, "");
    public static final CustomAutoBuyItem osk = new CustomAutoBuyItem(Items.PUFFERFISH_SPAWN_EGG, 0, "");
    public static final CustomAutoBuyItem arSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, "");


    public static final CustomAutoBuyItem sk1 = new CustomAutoBuyItem(Items.PINK_SHULKER_BOX, 0, "Рюкзак 1 уровень");
    public static final CustomAutoBuyItem vos = new CustomAutoBuyItem(Items.RED_DYE, 0, "Восстановление");
    public static final CustomAutoBuyItem bes = new CustomAutoBuyItem(Items.ORANGE_DYE, 0, "Бессметрие");
    public static final CustomAutoBuyItem th = new CustomAutoBuyItem(Items.PURPLE_DYE, 0, "Шипы");
    public static final CustomAutoBuyItem sk2 = new CustomAutoBuyItem(Items.LIGHT_BLUE_SHULKER_BOX, 0, "Рюкзак 2 уровень");
    public static final CustomAutoBuyItem sk3 = new CustomAutoBuyItem(Items.RED_SHULKER_BOX, 0, "Рюкзак 3 уровень");
    public static final CustomAutoBuyItem sk4 = new CustomAutoBuyItem(Items.MAGENTA_SHULKER_BOX, 0, "Рюкзак 4 уровень");
    public static final CustomAutoBuyItem mysteriousSummonEgg = new CustomAutoBuyItem(Items.BLAZE_SPAWN_EGG, 0, "загадочное яйцо призыва");
    public static final CustomAutoBuyItem privat = new CustomAutoBuyItem(Items.ANCIENT_DEBRIS, 0, "Уникальный приват");
    public static final CustomAutoBuyItem Trap = new CustomAutoBuyItem(Items.POPPED_CHORUS_FRUIT, 0, "Трапка");
    public static final CustomAutoBuyItem gra = new CustomAutoBuyItem(Items.FEATHER, 0, "гравитация");
    public static final CustomAutoBuyItem winnerPotion = new CustomAutoBuyItem(Items.POTION, 0, "");
    public static final CustomAutoBuyItem combatFragment = new CustomAutoBuyItem(Items.PRISMARINE_CRYSTALS, 0, "боевой фрагмент");
    public static final CustomAutoBuyItem c4 = new CustomAutoBuyItem(Items.TNT, 0, "с4 взрывчатка");
    public static final CustomAutoBuyItem gunp = new CustomAutoBuyItem(Items.GUNPOWDER, 0, "Порох");
    public static final CustomAutoBuyItem art = new CustomAutoBuyItem(Items.CONDUIT, 0, "");
    public static final CustomAutoBuyItem def = new CustomAutoBuyItem(Items.TRIPWIRE_HOOK, 0, "Обычная отмычка");
    public static final CustomAutoBuyItem red = new CustomAutoBuyItem(Items.TRIPWIRE_HOOK, 0, "Редкая отмычнка");
    public static final CustomAutoBuyItem sek = new CustomAutoBuyItem(Items.TRIPWIRE_HOOK, 0, "Секретная отмычка");
    public static final CustomAutoBuyItem leg = new CustomAutoBuyItem(Items.TRIPWIRE_HOOK, 0, "Уникальная отмычка");
    public static final CustomAutoBuyItem goldcompas = new CustomAutoBuyItem(Items.COMPASS, 0, "золотой компасс");
    public static final CustomAutoBuyItem osobcompas = new CustomAutoBuyItem(Items.COMPASS, 0, "");
    public static final CustomAutoBuyItem BookProtection = new CustomAutoBuyItem(Items.ENCHANTED_BOOK, 0, "");
    public static final CustomAutoBuyItem Bookbur = new CustomAutoBuyItem(Items.ENCHANTED_BOOK, 0, "");
    public static final CustomAutoBuyItem BookNeprobiv = new CustomAutoBuyItem(Items.ENCHANTED_BOOK, 0, "");
    public static final CustomAutoBuyItem MHPickaxe = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, "");
    public static final CustomAutoBuyItem MHPickaxe2 = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, "kk");
    public static final CustomAutoBuyItem all = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "");
    public static final CustomAutoBuyItem kock = new CustomAutoBuyItem(Items.SPAWNER, 0, "");
    public static final CustomAutoBuyItem kok = new CustomAutoBuyItem(Items.SPAWNER, 0, "");
    public static final CustomAutoBuyItem ved = new CustomAutoBuyItem(Items.SPAWNER, 0, "");
    public static final CustomAutoBuyItem kick = new CustomAutoBuyItem(Items.SPAWNER, 0, "");
    public static final CustomAutoBuyItem El = new CustomAutoBuyItem(Items.ELYTRA, 0, "");
    public static final CustomAutoBuyItem Spr = new CustomAutoBuyItem(Items.POTION, 0, "");
    public static final CustomAutoBuyItem vam = new CustomAutoBuyItem(Items.WITHER_SKELETON_SKULL, 0, "");
    public static final CustomAutoBuyItem pech = new CustomAutoBuyItem(Items.FURNACE, 0, "");
    public static final CustomAutoBuyItem mega = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, "");
    public static final CustomAutoBuyItem maga = new CustomAutoBuyItem(Items.NETHERITE_SHOVEL, 0, "");
    public static final CustomAutoBuyItem plav = new CustomAutoBuyItem(Items.NETHERITE_SHOVEL, 0, "");
    public static final CustomAutoBuyItem zom = new CustomAutoBuyItem(Items.SPAWNER, 0, "");
    public static final CustomAutoBuyItem sli = new CustomAutoBuyItem(Items.SPAWNER, 0, "");
    public static final CustomAutoBuyItem skelete = new CustomAutoBuyItem(Items.SPAWNER, 0, "");
    public static final CustomAutoBuyItem skeletr = new CustomAutoBuyItem(Items.SPAWNER, 0, "");
    public static final CustomAutoBuyItem blazer = new CustomAutoBuyItem(Items.SPAWNER, 0, "");
    public static final CustomAutoBuyItem blazee = new CustomAutoBuyItem(Items.SPAWNER, 0, "");
    public static final CustomAutoBuyItem magma = new CustomAutoBuyItem(Items.SPAWNER, 0, "");
    public static final CustomAutoBuyItem lop = new CustomAutoBuyItem(Items.IRON_SHOVEL, 0, "");
    public static final CustomAutoBuyItem fan = new CustomAutoBuyItem(Items.PHANTOM_MEMBRANE, 0, "");
    public static final CustomAutoBuyItem ender = new CustomAutoBuyItem(Items.ENDER_PEARL, 0, "");
    public static final CustomAutoBuyItem f4 = new CustomAutoBuyItem(Items.NETHERITE_AXE, 0, "");
    public static final CustomAutoBuyItem f3 = new CustomAutoBuyItem(Items.NETHERITE_AXE, 0, "");
    public static final CustomAutoBuyItem f2 = new CustomAutoBuyItem(Items.NETHERITE_AXE, 0, "");
    public static final CustomAutoBuyItem bam = new CustomAutoBuyItem(Items.FIRE_CHARGE, 0, "");
    public static final CustomAutoBuyItem ef7 = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, "");
    public static final CustomAutoBuyItem ef8 = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, "");
    public static final CustomAutoBuyItem ef9 = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, "");
    public static final CustomAutoBuyItem ef10 = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, "");
    public static final CustomAutoBuyItem bet = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, "");
    public static final CustomAutoBuyItem art2 = new CustomAutoBuyItem(Items.CONDUIT, 0, "");
    public static final CustomAutoBuyItem hp = new CustomAutoBuyItem(Items.NETHERITE_HELMET, 0, "");
    public static final CustomAutoBuyItem sfspeed2 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, "");
    public static final CustomAutoBuyItem sfdamage2 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, "");
    public static final CustomAutoBuyItem sfarmor2 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, "");
    public static final CustomAutoBuyItem wither = new CustomAutoBuyItem(Items.SPAWNER, 0, "");
    public static final CustomAutoBuyItem cp = new CustomAutoBuyItem(Items.NETHERITE_CHESTPLATE, 0, "");
    public static final CustomAutoBuyItem lp = new CustomAutoBuyItem(Items.NETHERITE_LEGGINGS, 0, "");
    public static final CustomAutoBuyItem bp = new CustomAutoBuyItem(Items.NETHERITE_BOOTS, 0, "");
    public static final CustomAutoBuyItem skorka = new CustomAutoBuyItem(Items.POTION, 0, "");
    public static final CustomAutoBuyItem silka = new CustomAutoBuyItem(Items.POTION, 0, "");


    public static void addItem(AutoBuyItem autoBuyItem) {
        items.add(autoBuyItem);
    }

    public static void removeItem(AutoBuyItem autoBuyItem) {
        items.remove(autoBuyItem);
    }


    public static void init() {
        goldenPickaxe.name = "Золотая кирка джейка";
        goldenPickaxe.enchantments = of("spawner-getter-enchant:1");

        //infinity

        infinityHelmet.name = "Шлем infinity";
        infinityHelmet.enchantments = of("impenetrable-enchant-custom:2", "aqua_affinity:1", "blast_protection:5", "fire_protection:5", "mending:2", "projectile_protection:5", "protection:5", "respiration:3", "unbreaking:5");

        infinityChestplate.name = "Нагрудник infinity";
        infinityChestplate.enchantments = of("impenetrable-enchant-custom:2", "blast_protection:5", "fire_protection:5", "mending:2", "projectile_protection:5", "protection:5", "unbreaking:5");

        infinityLeggings.name = "Штаны infinity";
        infinityLeggings.enchantments = of("impenetrable-enchant-custom:2", "blast_protection:5", "fire_protection:5", "mending:2", "projectile_protection:5", "protection:5", "unbreaking:5");

        infinityBoots.name = "Ботинки infinity";
        infinityBoots.enchantments = of("impenetrable-enchant-custom:2", "blast_protection:5", "fire_protection:5", "mending:2", "projectile_protection:5", "protection:5", "unbreaking:5");


        //eternity


        eternityPickaxe.name = "Кирка eternity";
        eternityPickaxe.enchantments = of("drill-enchant-custom:2", "exp-enchant-custom:3", "foundry-enchant-custom:1", "internal-enchant-custom:1", "magnet-enchant-custom:1", "efficiency:10", "fortune:5", "mending:1", "unbreaking:5");

        eternityHelmet.name = "Шлем eternity";
        eternityHelmet.enchantments = of("impenetrable-enchant-custom:1", "aqua_affinity:1", "blast_protection:5", "fire_protection:5", "mending:1", "projectile_protection:5", "protection:5", "respiration:3", "unbreaking:5");
        eternityHelmet.strictCheck = true;

        eternityChestplate.name = "Нагрудник eternity";
        eternityChestplate.enchantments = of("impenetrable-enchant-custom:1", "blast_protection:5", "fire_protection:5", "mending:1", "projectile_protection:5", "protection:5", "unbreaking:5");
        eternityChestplate.strictCheck = true;

        eternityLeggings.name = "Штаны eternity";
        eternityLeggings.enchantments = of("impenetrable-enchant-custom:1", "blast_protection:5", "fire_protection:5", "mending:1", "projectile_protection:5", "protection:5", "unbreaking:5");
        eternityLeggings.strictCheck = true;

        eternityBoots.name = "Ботинки eternity";
        eternityBoots.enchantments = of("blast_protection:5", "depth_strider:3", "feather_falling:4", "fire_protection:5", "mending:1", "projectile_protection:5", "protection:5", "soul_speed:3", "unbreaking:5");
        eternityBoots.strictCheck = true;

        eternitySword.name = "Меч eternity";
        eternitySword.enchantments = of("critical-enchant-custom:2", "destroyer-enchant-custom:2", "rich-enchant-custom:1", "bane_of_arthropods:7", "fire_aspect:2", "looting:5", "mending:1", "sharpness:7", "smite:7", "sweeping:3", "unbreaking:5");


        //stinger

        stingerPickaxe.name = "Кирка stinger";
        stingerPickaxe.enchantments = of("drill-enchant-custom:1", "exp-enchant-custom:3", "foundry-enchant-custom:1", "internal-enchant-custom:1", "efficiency:8", "fortune:4", "mending:1", "unbreaking:4");

        stingerHelmet.name = "Шлем stinger";
        stingerHelmet.enchantments = of("aqua_affinity:1", "blast_protection:4", "fire_protection:4", "mending:1", "projectile_protection:4", "protection:4", "respiration:3", "thorns:3", "unbreaking:4");
        stingerHelmet.strictCheck = true;

        stingerChestplate.name = "Нагрудник stinger";
        stingerChestplate.enchantments = of("impenetrable-enchant-custom:1", "blast_protection:4", "fire_protection:4", "mending:1", "projectile_protection:4", "protection:4", "thorns:3", "unbreaking:4");
        stingerChestplate.strictCheck = true;

        stingerLeggings.name = "Штаны stinger";
        stingerLeggings.enchantments = of("impenetrable-enchant-custom:1", "blast_protection:4", "fire_protection:4", "mending:1", "projectile_protection:4", "protection:4", "thorns:3", "unbreaking:4");
        stingerLeggings.strictCheck = true;

        stingerBoots.name = "Ботинки stinger";
        stingerBoots.enchantments = of("blast_protection:4", "depth_strider:3", "feather_falling:4", "fire_protection:4", "mending:1", "projectile_protection:4", "protection:4", "soul_speed:3", "thorns:3", "unbreaking:4");
        stingerBoots.strictCheck = true;

        stingerSword.name = "Меч stinger";
        stingerSword.enchantments = of("critical-enchant-custom:2", "rich-enchant-custom:1", "bane_of_arthropods:7", "fire_aspect:2", "looting:5", "mending:1", "sharpness:6", "smite:7", "sweeping:3", "unbreaking:4");


        //sphere / talisman

        cerberusSphere.name = "Сфера Цербера";
        cerberusSphere.effect = of("hms-damage:5");


        immSphere.name = "Сфера Иморталити";
        immSphere.effect = of("hms-damage:3", "hms-speed:2");


        fleshSphere.name = "Сфера Флеша";
        fleshSphere.effect = of("hms-speed:3", "hms-armor:1");


        damageSphere.name = "Сфера на Урон 3";
        damageSphere.effect = of("hms-damage:3");


        speedSphere.name = "Сфера на Скорость 3";
        speedSphere.effect = of("hms-speed:3");


        eternitySphere.name = "Сфера eternity";
        eternitySphere.effect = of("hms-speed:2", "hms-armor:2", "hms-damage:2");

        arSphere.name = "Сфера ARMORTALITY";
        arSphere.effect = of("hms-health:2", "hms-armor:2", "hms-damage:2");

        stingerSphere.name = "Сфера stinger";
        stingerSphere.effect = of("hms-speed:1", "hms-armor:2", "hms-damage:2");

        mythicalSphere.name = "Мифическая Сфера";
        mythicalSphere.effect = of("hms-damage:3", "hms-armor:2");

        sfarmor2.name = "Сфера на броня 2";
        sfarmor2.effect = of("hms-armor:2");

        sfdamage2.name = "Сфера на урон 2";
        sfdamage2.effect = of("hms-damage:2");

        sfspeed2.name = "Сфера на скорость 2";
        sfspeed2.effect = of("hms-speed:2");


        mythicalSphere2.name = "Мифическая сфера на броня 3";
        mythicalSphere2.effect = of("hms-damage:2", "hms-armor:3");


        et.name = "Талисман Eternity";
        et.line = of("Талисман ᴇᴛᴇʀɴɪᴛʏ");

        mif.name = "Мифический Талисман";
        mif.line = of("Мифический талисман");

        inf.name = "Талисман Инфинити";
        inf.line = of("Талисман ɪɴғɪɴɪᴛʏ");

        sting.name = "Талисман Stinger";
        sting.line = of("Талисман sᴛɪɴɢᴇʀ");

        satir.name = "Талисман сатиры";
        satir.line = of("Урон IV");


        gunp.name = "Порох";
        gunp.line = of("gunpowder");


        // donate items
        explosiveTrap.name = "Взрывная трапка";
        explosiveTrap.line = of("при взрыве наносит урон в радиусе 3 блоков");

        Trap.name = "Анти-фантом";
        Trap.line = of("атаковать вас");

        Trap.name = "Иллюминатор";
        Trap.line = of("по которому совершен удар");

        Trap.name = "Вампиризм";
        Trap.line = of("получить регенирацию II");

        Trap.name = "Снеговик";
        Trap.line = of("эффект замедления на 5 секунд");

        Trap.name = "Нерушимые Элитры";
        Trap.line = of("Unbreakable");

        privat.name = "Уникальный приват";
        privat.line = of("имеет свойства незеритового региона");


        sp.name = "Загадочный спавнер";
        sp.line = of("Визер-скелет", "Крипер", "Скелет", "Зомби", "Блейз");


        stan.name = "Стан";
        stan.line = of("игроки в нем не могут использовать");

        goldenSpawner.name = "Золотой спавнер";
        goldenSpawner.line = of("5 блоков от спавнера игроку");

        explosiveSubstance.name = "Взрывчатое вещество";
        explosiveSubstance.line = of("используется только для крафта");

        universalKey.name = "Универсальный ключ";
        universalKey.line = of("Используйте данный ключ для открытия");

        stealer.name = "Стиллер";
        stealer.line = of("после взрыва выпадает");

        armor.name = "Талисман на броня 3";
        armor.line = of("Броня III");

        skr.name = "Талисман на скорость 3";
        skr.line = of("Скорость III");

        ur.name = "Талисман на урон 3";
        ur.line = of("Урон III");

        run.name = "Талисман с рунами";
        run.line = of("Бессметрие");

        ur2.name = "Талисман урон II";
        ur2.line = of("Урон II");

        skr2.name = "Талисман скорость II";
        skr2.line = of("Скорость II");

        armor2.name = "Талисман броня II";
        armor2.line = of("Броня II");

        skur.name = "Урон II Скорость II";
        skur.line = of("Урон II", "Скорость II");

        skar.name = "Скорость II Броня II";
        skar.line = of("Скорость II", "Броня II");

        urar.name = "Урон II Броня II";
        urar.line = of("Урон II", "Броня II");


        motiga.name = "Посев IV";
        motiga.enchantments = of("sowing-enchant-custom:4");


        drow.name = "Дровосек IV";
        drow.enchantments = of("treecapitator-enchant-custom:");

        topor.name = "Эфф 9";
        topor.enchantments = of("efficiency:9");

        osk.name = "осколочное яйцо";
        osk.line = of("на ценные яйца призыва мобов");

        BookProtection.name = "защита 5";
        BookProtection.line = of("Protection V");

        BookNeprobiv.name = "непробиваемый";
        BookNeprobiv.line = of("Непробиваемый I");

        Bookbur.name = "Бур 2";
        Bookbur.line = of("Бур II");


        a.name = "Динамит А";
        a.line = of("имеет в 3 раза больший радус взрыва");

        b.name = "Динамит Б";
        b.line = of("имеет в 10 раз больший радус взрыва");

        b2.name = "Динамит Б2";
        b2.line = of("взрывает практически все блоки");

        st.name = "Надёжный стиллер";
        st.line = of("спавнер с мобом шансом в 75%");

        vol.name = "Разрывная волна";
        vol.line = of("взрывает блоки в воде");

        expBottle100lvl.name = "Опыт 100";
        expBottle100lvl.line = of("В пузырьке 30971 опыта");

        expBottle50lvl.name = "Опыт 50";
        expBottle50lvl.line = of("В пузырьке 5345 опыта");

        expBottle30lvl.name = "Опыт 30";
        expBottle30lvl.line = of("В пузырьке 1395 опыта");

        expBottle15lvl.name = "Опыт 15";
        expBottle15lvl.line = of("В пузырьке 315 опыта");

        farmerSword.name = "Выгодный фарм";
        farmerSword.enchantments = of("mob-farmer-enchant:");

        farmerSword2.name = "Фармер 3 ";
        farmerSword2.enchantments = of("mob-farmer-enchant:3");

        farmerSword3.name = "Фармер 4 ";
        farmerSword3.enchantments = of("mob-farmer-enchant:4");

        farmerSword4.name = "Фармер 5 ";
        farmerSword4.enchantments = of("mob-farmer-enchant:5");

        farmerSword5.name = "Фармер 6";
        farmerSword5.enchantments = of("mob-farmer-enchant:6");

        farmerSword6.name = "Фармер 7";
        farmerSword6.enchantments = of("mob-farmer-enchant:7");

        farmerSword7.name = "Фармер 8";
        farmerSword7.enchantments = of("mob-farmer-enchant:8");

        farmerSword8.name = "Фармер 9";
        farmerSword8.enchantments = of("mob-farmer-enchant:9");

        farmerSword9.name = "Фармер 10";
        farmerSword9.enchantments = of("mob-farmer-enchant:10");

        farmerSword10.name = "Фармер 11";
        farmerSword10.enchantments = of("mob-farmer-enchant:11");

        farmerSword11.name = "Фармер 12";
        farmerSword11.enchantments = of("mob-farmer-enchant:12");

        helm.name = "Топка шлем ";
        helm.enchantments = of("protection:5", "aqua_affinity:1,unbreaking:4,mending:1");

        chest.name = "Топка нагрудник ";
        chest.enchantments = of("mending:1", "protection:5", "unbreaking:5");

        legen.name = "Топка поножи ";
        legen.enchantments = of("mending:1", "protection:5", "unbreaking:4");

        boot.name = "Топка ботинки ";
        boot.enchantments = of("depth_strider:3", "feather_falling:4", "mending:1", "protection:5", "unbreaking:4");


        hel.name = "з5 шлем ";
        hel.enchantments = of("protection:5");

        ches.name = "з5 нагрудник ";
        ches.enchantments = of("protection:5");

        lege.name = "з5 поножи ";
        lege.enchantments = of("protection:5");

        boo.name = "з5 ботинки ";
        boo.enchantments = of("protection:5");

        swor.name = "о7  ";
        swor.enchantments = of("sharpness:7");

        ely.name = "Броневая Элитра";
        ely.line = of("имеет свойства алмазного нагрудника");

        mysteriousSummonEgg.name = "Загадочное яйцо призыва";
        mysteriousSummonEgg.line = of("Мститель", "Крипер", "Зомби", "Блейз", "Ведьма");


        winnerPotion.name = "Зелье победителя";
        winnerPotion.line = of("Сила III", "Скорость III", "Невидимость", "Спешка II", "Регенерация II", "Сопротивление II", "Исцеление II");


        isz.name = "Исцеление 3";
        isz.line = of("healing III");

        ex.name = "Пузырь Опыта";
        ex.line = of("при нажатии ПКМ полностью ремонтирует");


        combatFragment.name = "Боевой фрагмент";
        combatFragment.enchantments = of("luck_of_the_sea:1");

        c4.name = "C4";
        c4.line = of("разрушает блок незеритового привата");

        def.name = "Обычная отмычка";
        def.line = of("Редкость: Обычная");
        red.name = "Редкая отмычка ";
        red.line = of("Редкость: Редкая");
        sek.name = "Секретная отмычка";
        sek.line = of("Редкость: Секретная");
        leg.name = "Уникальная отмычка";
        leg.line = of("Редкость: Уникальная");

        goldcompas.name = "Золотой компас";
        goldcompas.line = of("выводит в чат кординаты");

        osobcompas.name = "Особый компас";
        osobcompas.line = of("ведёт к ближайшему или рандомному сокровищу");

        ghelm.name = "Шлем Солнца";
        ghelm.line = of("имеет свойства незеритового шлема");


        MHPickaxe.name = "Мечта Шахтёра";
        MHPickaxe.enchantments = of("drill-enchant-custom:2", "exp-enchant-custom:3", "foundry-enchant-custom:1", "internal-enchant-custom:1", "magnet-enchant-custom:1", "efficiency:10", "fortune:5", "mending:1", "unbreaking:10");

        MHPickaxe3.name = "Прочность 10";
        MHPickaxe3.enchantments = of("unbreaking:10");


        MHPickaxe2.name = "Мечта Шахтёра (неразрушимая)";
        MHPickaxe2.line = of("Unbreakable");

        candy.name = "Мешок конфет";
        candy.line = of("вы можете обменять предмет");

        vos.name = "Восстановление";
        vos.line = of("У вас полностью восстановиться здоровье");

        bes.name = "Бессмертие";
        bes.line = of("Вы получите неуязвимость к урону");

        th.name = "Шипы";
        th.line = of("после активации тотема с этим эффектом");

        vt.name = "Второй шанс";
        vt.line = of("после активации тотема с этим эффектом");


        all.name = "Тотем (Все Чары)";
        all.line = of("Бессмертие", "Шипы", "Последний шанс", "Потеря зрения", "Восстановление", "Второй шанс", "Пора бежать", "Прыжки веры");

        kock.name = "Загадочный Спавнер 1";
        kock.line = of("Мститель", "Крипер", "Зомби", "Блейз", "Ведьма");

        kok.name = "Загадочный спавнер 3";
        kok.line = of("Визер-скелет", "Крипер", "Скелет", "Зомби", "Блейз");

        ved.name = "Загадочное яйцо призыва 2";
        ved.line = of("Визер-скелет", "Скелет", "Зомби", "Блейз", "Ведьма");

        kick.name = "Загадочный спавнер 2";
        kick.line = of("Визер-скелет", "Скелет", "Зомби", "Блейз", "Ведьма");

        El.name = "Элитры (Нерушимая)";
        El.line = of("Неразрушаемый");

        Spr.name = "Справедливость";
        Spr.line = of("нельзя");

        gra.name = "Гравитация";
        gra.line = of("не получает урон от падения");


        vam.name = "Вампиризм";
        vam.line = of("получить регенерацию");

        pech.name = "Быстрая печка";
        pech.line = of("печь");

        mega.name = "Мега-Бур (Кирка)";
        mega.line = of("Мега-бур");

        maga.name = "Мега-Бур (Лопата)";
        maga.line = of("Мега-бур");

        plav.name = "Автоплавка (Лопата)";
        plav.line = of("Автоплавка");


        zom.name = "Зомби";
        zom.line = of("Зомби");

        sli.name = "Слизень";
        sli.line = of("Слизень");

        skelete.name = "SKELETON ENG";
        skelete.line = of("SKELETON");

        skeletr.name = "Скелет РФ";
        skeletr.line = of("Скелет");

        blazer.name = "Блейз РФ";
        blazer.line = of("Блейз");

        blazee.name = "BLAZE ENG";
        blazee.line = of("BLAZE");

        wither.name = "WITHER_SKELETON";
        wither.line = of("WITHER_SKELETON");

        magma.name = "MAGMA_CUBE ENG";
        magma.line = of("MAGMA_CUBE");


        lop.name = "Снежная лопата";
        lop.line = of("Снежная лопата");
        lop.line = of("Снежный Карьер");
        lop.line = of("на мероприятии");
        fan.name = "АнтиФантом";
        fan.line = of("атаковать вас");
        ender.name = "Эндермен";
        ender.line = of("Эндермен");
        f4.name = "Фермер 4";
        f4.line = of("Фермер IV");
        f3.name = "Фермер 3";
        f3.line = of("Фермер III");
        f2.name = "Фермер 2";
        f2.line = of("Фермер II");


        bam.name = "Взрывная штучка";
        bam.line = of("Взрывная штучка");


        ef7.name = "Кирка эф7";
        ef7.enchantments = of("efficiency:7");

        ef8.name = "Кирка эф8";
        ef8.enchantments = of("efficiency:8");

        ef9.name = "Кирка эф9";
        ef9.enchantments = of("efficiency:9");

        ef10.name = "Кирка эф10";
        ef10.enchantments = of("efficiency:10");

        bet.name = "Тотем Бессметрие";
        bet.line = of("Бессмертие");


        art2.name = "Артефакт Фулл";
        art2.line = of("Справедливость", "Охотник", "Иллюминатор", "Вампиризм", "Эндермен", "Снеговик", "Анти-Фантом", "Гравитация");

        art.name = "Артефакт без эффектов";
        art.line = of("Может активировать сразу несколько");


        hp.name = "Шлем на непробиваемый";
        hp.line = of("Непробиваемый I");

        cp.name = "Нагрудник на непробиваемый";
        cp.line = of("Непробиваемый I");

        lp.name = "Поножи на непробиваемый";
        lp.line = of("Непробиваемый I");

        bp.name = "Ботинки на непробиваемый";
        bp.line = of("Непробиваемый I");

        silka.name = "Сила 3";
        silka.line = of("+9 Урон");

        skorka.name = "Скорость 3";
        skorka.line = of("+60% Скорости");


        sk1.name = "Рюкзак 1 Уровень";
        sk1.line = of("нельзя поставить на землю");

        sk2.name = "Рюкзак 2 Уровень";
        sk2.line = of("нельзя поставить на землю");

        sk3.name = "Рюкзак 3 Уровень";
        sk3.line = of("нельзя поставить на землю");

        sk4.name = "Рюкзак 4 Уровень";
        sk4.line = of("нельзя поставить на землю");


        CustomAutoBuyItem[] customAutoBuyItems = {
                universalKey, farmerSword, farmerSword2, farmerSword3, farmerSword4, farmerSword5, farmerSword6, farmerSword7, farmerSword8, farmerSword9, farmerSword10, farmerSword11,
                helm, chest, legen, boot,
                goldenSpawner,
                MHPickaxe2, MHPickaxe, MHPickaxe3
                , osobcompas, goldcompas, goldenPickaxe,
                sk1, sk2, sk3, sk4, infinityHelmet, infinityChestplate, infinityLeggings, infinityBoots,
                eternitySphere, eternityHelmet, eternityChestplate, eternityLeggings, eternityBoots, eternitySword, eternityPickaxe,
                stingerSphere, stingerHelmet, stingerChestplate, stingerLeggings, def, red, sek, leg, stingerBoots, stingerSword, stingerPickaxe, Bookbur
                , mythicalSphere, mythicalSphere2, fleshSphere, immSphere, cerberusSphere, speedSphere, damageSphere, inf, mif, et, sting, armor, skr, ur, ur2, skr2, armor2, candy, skur, urar, skar, vt, vt, gunp, ex, skorka, silka, bp, lp, cp, hp, art2, ef10, ef9, ef8, ef7, bam, f2, f3, f4, ender, fan, lop, magma, wither, blazee, blazer, sli, zom, plav, maga, mega, pech, vam, all, kock, kok, kick, El, Spr, ved,
                explosiveTrap, stan, explosiveSubstance, gra, privat, th, ghelm, isz, ely, b2, b, motiga, drow, topor, osk, arSphere, expBottle15lvl, st, a, hel, lege, boo, ches, vol, satir, run, swor, expBottle30lvl, stealer, BookProtection, BookNeprobiv
                , vos, expBottle50lvl, art, expBottle100lvl,
                bes, sp, Trap, mysteriousSummonEgg, winnerPotion, combatFragment, c4


        };
        initFuntime();
        customAutoBuyItemList.addAll(Arrays.asList(customAutoBuyItems));
    }


    private static CustomAutoBuyItem getCustomAutoBuyItemByName(String name) {
        return customAutoBuyItemList.stream().filter(customAutoBuyItem -> customAutoBuyItem.name.equalsIgnoreCase(name)).toList().get(0);
    }

    public static void load() {
        File file = new File("\\ab.private\\autobuy.txt");

        if (!file.exists()) {
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                int id = Integer.parseInt(line.split(":")[0]);
                if (id == 0) {
                    Item item = null;
                    for (Item i : Registry.ITEM) {
                        if (i.getTranslationKey().equalsIgnoreCase(line.split(":")[1])) {
                            item = i;
                            break;
                        }
                    }
                    items.add(new DefaultAutoBuyItem(item, Integer.parseInt(line.split(":")[2])));
                }
                if (id == 1) {
                    CustomAutoBuyItem customAutoBuyItem = getCustomAutoBuyItemByName(line.split(":")[1]);
                    CustomAutoBuyItem add = new CustomAutoBuyItem(customAutoBuyItem.item, Integer.parseInt(line.split(":")[2]), "");
                    add.line = customAutoBuyItem.line;
                    add.enchantments = customAutoBuyItem.enchantments;
                    add.effect = customAutoBuyItem.effect;
                    add.attributes = customAutoBuyItem.attributes;
                    add.name = customAutoBuyItem.name;
                    add.strictCheck = customAutoBuyItem.strictCheck;
                    items.add(add);
                }
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save() {
        File file = new File("\\ab.private\\autobuy.txt");

        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (AutoBuyItem item : items) {
                if (item instanceof CustomAutoBuyItem) {
                    bufferedWriter.write("1:" + ((CustomAutoBuyItem) item).name + ":" + item.price + "\n");
                }
                if (item instanceof DefaultAutoBuyItem) {
                    bufferedWriter.write("0:" + item.item.getTranslationKey() + ":" + item.price + "\n");
                }
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<CustomAutoBuyItem> ftItems = new CopyOnWriteArrayList<>();

    public static void initFuntime() {

        CustomAutoBuyItem medikaPotion = CustomAutoBuyItem.withPotionEffect("Зелье медика", "", of(
                get(Effects.HEALTH_BOOST, 2.0, 45),
                get(Effects.REGENERATION, 2.0, 45)
        ));
        CustomAutoBuyItem killerPotion = CustomAutoBuyItem.withPotionEffect("Зелье Киллера", "", of(
                get(Effects.STRENGTH, 4.0, 180),
                get(Effects.RESISTANCE, 1.0, 180)
        ));

        CustomAutoBuyItem agentaPotion = CustomAutoBuyItem.withPotionEffect("Зелье агента", "", of(
                get(Effects.INVISIBILITY, 1, 15 * 60),
                get(Effects.HASTE, 0, 2 * 60),
                get(Effects.FIRE_RESISTANCE, 0, 15 * 60),
                get(Effects.SPEED, 1, 15 * 60),
                get(Effects.STRENGTH, 2, 5 * 60)

        ));
        CustomAutoBuyItem serkaPotion = CustomAutoBuyItem.withPotionEffect("Зелье серка", "", of(
                get(Effects.POISON, 1, 50),
                get(Effects.SLOWNESS, 3, 90),
                get(Effects.WEAKNESS, 2, 90),
                get(Effects.WITHER, 4, 30)
        ));

        CustomAutoBuyItem otrigaPotion = CustomAutoBuyItem.withPotionEffect("Зелье отрыги", "", of(
                get(Effects.BLINDNESS, 0, 10),
                get(Effects.GLOWING, 0, 3 * 60),
                get(Effects.HUNGER, 10, 90),
                get(Effects.SLOWNESS, 2, 3 * 60),
                get(Effects.WITHER, 4, 30)
        ));


        CustomAutoBuyItem pobedilkaPotion = CustomAutoBuyItem.withPotionEffect("Зелье победилки", "", of(
                get(Effects.HEALTH_BOOST, 1, 3 * 60),
                get(Effects.INVISIBILITY, 0, 15 * 60),
                get(Effects.REGENERATION, 1, 60),
                get(Effects.RESISTANCE, 0, 60)
        ));

        CustomAutoBuyItem boots_krush = CustomAutoBuyItem.withEnchantment("Ботинки крушителя", "", Items.NETHERITE_BOOTS.getDefaultInstance(), of(
                get(Enchantments.BLAST_PROTECTION, 5),
                get(Enchantments.DEPTH_STRIDER, 3),
                get(Enchantments.FEATHER_FALLING, 4),
                get(Enchantments.FIRE_PROTECTION, 5),
                get(Enchantments.MENDING, 1),
                get(Enchantments.PROJECTILE_PROTECTION, 5),
                get(Enchantments.PROTECTION, 5),
                get(Enchantments.SOUL_SPEED, 3),
                get(Enchantments.UNBREAKING, 5)
        ));
        CustomAutoBuyItem leggings_krush = CustomAutoBuyItem.withEnchantment("Поножи крушителя", "", Items.NETHERITE_LEGGINGS.getDefaultInstance(), of(
                get(Enchantments.BLAST_PROTECTION, 5),
                get(Enchantments.FIRE_PROTECTION, 5),
                get(Enchantments.MENDING, 1),
                get(Enchantments.PROJECTILE_PROTECTION, 5),
                get(Enchantments.PROTECTION, 5),
                get(Enchantments.UNBREAKING, 5)
        ));
        CustomAutoBuyItem chestplate_krush = CustomAutoBuyItem.withEnchantment("Нагрудник крушителя", "", Items.NETHERITE_CHESTPLATE.getDefaultInstance(), of(
                get(Enchantments.BLAST_PROTECTION, 5),
                get(Enchantments.FIRE_PROTECTION, 5),
                get(Enchantments.MENDING, 1),
                get(Enchantments.PROJECTILE_PROTECTION, 5),
                get(Enchantments.PROTECTION, 5),
                get(Enchantments.UNBREAKING, 5)
        ));
        CustomAutoBuyItem crossbow_krush = CustomAutoBuyItem.withEnchantment("Арбалет крушителя", "", Items.CROSSBOW.getDefaultInstance(), of(
                get(Enchantments.MENDING, 1),
                get(Enchantments.MULTISHOT, 1),
                get(Enchantments.PIERCING, 5),
                get(Enchantments.QUICK_CHARGE, 3),
                get(Enchantments.UNBREAKING, 3)
        ));
        CustomAutoBuyItem helmet_krush = CustomAutoBuyItem.withEnchantment("Шлем крушителя", "", Items.NETHERITE_HELMET.getDefaultInstance(), of(
                get(Enchantments.AQUA_AFFINITY, 1),
                get(Enchantments.BLAST_PROTECTION, 5),
                get(Enchantments.FIRE_PROTECTION, 5),
                get(Enchantments.MENDING, 1),
                get(Enchantments.PROJECTILE_PROTECTION, 5),
                get(Enchantments.PROTECTION, 5),
                get(Enchantments.RESPIRATION, 3),
                get(Enchantments.UNBREAKING, 5)
        ));

        CustomAutoBuyItem andromedaIII = CustomAutoBuyItem.withAttributes("Сфера андромеды III", "", Items.PLAYER_HEAD.getDefaultInstance(), of(
                get(Attributes.ATTACK_DAMAGE, 3.0),
                get(Attributes.ARMOR, 2.0),
                get(Attributes.MAX_HEALTH, -4.0),
                get(Attributes.MOVEMENT_SPEED, 0.15)
        ));
        CustomAutoBuyItem pandoraIII = CustomAutoBuyItem.withAttributes("Сфера пандоры III", "", Items.PLAYER_HEAD.getDefaultInstance(), of(
                get(Attributes.MOVEMENT_SPEED, 0.1),
                get(Attributes.ARMOR, -0.1),
                get(Attributes.ATTACK_DAMAGE, 0.25)
        ));
        CustomAutoBuyItem apollonaIII = CustomAutoBuyItem.withAttributes("Сфера Аполллона III", "", Items.PLAYER_HEAD.getDefaultInstance(), of(
                get(Attributes.ATTACK_DAMAGE, 4.0),
                get(Attributes.MOVEMENT_SPEED, -0.10)
        ));
        CustomAutoBuyItem osirisIII = CustomAutoBuyItem.withAttributes("Сфера Осириса III", "", Items.PLAYER_HEAD.getDefaultInstance(), of(
                get(Attributes.KNOCKBACK_RESISTANCE, -0.15),
                get(Attributes.ATTACK_KNOCKBACK, 0.15),
                get(Attributes.ARMOR, 3.0)
        ));
        CustomAutoBuyItem astreyaIII = CustomAutoBuyItem.withAttributes("Сфера Астрея III", "", Items.PLAYER_HEAD.getDefaultInstance(), of(
                get(Attributes.MAX_HEALTH, 4.0),
                get(Attributes.ATTACK_DAMAGE, 3.0),
                get(Attributes.ATTACK_SPEED, -0.15)
        ));
        CustomAutoBuyItem titanIII = CustomAutoBuyItem.withAttributes("Сфера Титана III", "", Items.PLAYER_HEAD.getDefaultInstance(), of(
                get(Attributes.MOVEMENT_SPEED, -0.15),
                get(Attributes.ARMOR, 2.0),
                get(Attributes.ARMOR_TOUGHNESS, 2.0)
        ));
        CustomAutoBuyItem ximeraIII = CustomAutoBuyItem.withAttributes("Сфера Химера III", "", Items.PLAYER_HEAD.getDefaultInstance(), of(
                get(Attributes.MAX_HEALTH, -2.0),
                get(Attributes.ATTACK_DAMAGE, 3.0),
                get(Attributes.ATTACK_SPEED, 0.15)
        ));

        CustomAutoBuyItem krushTal = CustomAutoBuyItem.withAttributes("Крушитель", "", Items.TOTEM_OF_UNDYING.getDefaultInstance(), of(
                get(Attributes.MAX_HEALTH, 4.0),
                get(Attributes.ATTACK_DAMAGE, 3.0),
                get(Attributes.ARMOR_TOUGHNESS, 2.0),
                get(Attributes.ARMOR, 2.0)
        ));

        CustomAutoBuyItem karatelTal = CustomAutoBuyItem.withAttributes("Каратель", "", Items.TOTEM_OF_UNDYING.getDefaultInstance(), of(
                get(Attributes.MOVEMENT_SPEED, 0.10),
                get(Attributes.MAX_HEALTH, -4.0),
                get(Attributes.ATTACK_DAMAGE, 7.0)
        ));

        CustomAutoBuyItem dedalaIII = CustomAutoBuyItem.withAttributes("Тотем Дедала III", "", Items.TOTEM_OF_UNDYING.getDefaultInstance(), of(
                get(Attributes.ATTACK_DAMAGE, 5.0),
                get(Attributes.MAX_HEALTH, -4.0)
        ));
        CustomAutoBuyItem exidnaIII = CustomAutoBuyItem.withAttributes("Тотем Ехидны III", "", Items.TOTEM_OF_UNDYING.getDefaultInstance(), of(
                get(Attributes.ATTACK_DAMAGE, 6.0),
                get(Attributes.ARMOR, -2.0),
                get(Attributes.ARMOR_TOUGHNESS, -2.0),
                get(Attributes.MAX_HEALTH, -4.0)
        ));
        CustomAutoBuyItem granyIII = CustomAutoBuyItem.withAttributes("Грань III", "", Items.TOTEM_OF_UNDYING.getDefaultInstance(), of(
                get(Attributes.MOVEMENT_SPEED, 0.15),
                get(Attributes.ATTACK_DAMAGE, 3.0),
                get(Attributes.MAX_HEALTH, -4.0)
        ));
        CustomAutoBuyItem harmonyIII = CustomAutoBuyItem.withAttributes("Гармония III", "", Items.TOTEM_OF_UNDYING.getDefaultInstance(), of(
                get(Attributes.ATTACK_DAMAGE, 2.0),
                get(Attributes.MAX_HEALTH, 2.0),
                get(Attributes.ARMOR, 2.0)
        ));
        CustomAutoBuyItem tritonaIII = CustomAutoBuyItem.withAttributes("Тритон III", "", Items.TOTEM_OF_UNDYING.getDefaultInstance(), of(
                get(Attributes.ARMOR, 3.0),
                get(Attributes.MAX_HEALTH, 2.0),
                get(Attributes.ARMOR_TOUGHNESS, -3.0)
        ));


        ftItems.addAll(List.of(
                // medikaPotion,
                // agentaPotion,
                // serkaPotion,
                // otrigaPotion,
                // pobedilkaPotion,
                // boots_krush,
                // leggings_krush,
                // chestplate_krush,
                // helmet_krush,
                // crossbow_krush,
                // andromedaIII,
                // pandoraIII,
                // apollonaIII,
                // osirisIII,
                // astreyaIII,
                // titanIII,
                // ximeraIII,
                // krushTal,
                // karatelTal,
                // dedalaIII,
                // exidnaIII,
                // granyIII,
                // harmonyIII,
                // tritonaIII
        ));

        customAutoBuyItemList.addAll(ftItems);
    }

    public static List<String> of(String... attributes) {
        return List.of(attributes);
    }

    public static String get(Effect instance, double lvl, double ms) {
        return instance.getName().replaceAll("effect.minecraft.", "") + ":" + lvl;
    }

    public static String get(Enchantment e, double lvl) {
        return e.getName().replaceAll("enchantment.minecraft.", "") + ":" + (int) lvl;
    }

    public static String get(Attribute attribute, double lvl) {
        return attribute.getAttributeName() + lvl;
    }
}



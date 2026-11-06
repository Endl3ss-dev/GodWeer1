package tech.lightclient.modules.impl.render;

import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.client.utils.globals.GlobalsManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

@FunctionAdd(name = "ItemReplacer", alias = "Item Replacer", category = Category.Render, description = "Заменяет модель мечей")
public class ItemReplacer extends Function {
    private static final String[] MODELS = {
            "Abominable Blade",
            "Abominable Great Saber",
            "Abominable Scythe",
            "Acidic Cleaver",
            "Amethyst Shuriken",
            "Ancient Royal Great Sword",
            "Aquatic Sacred Blade",
            "Arcanethyst",
            "Ashura's Blade",
            "Awakened Lichblade",
            "Blood Edge",
            "Bloody Death",
            "Bramblethorn",
            "Brimstone Claymore",
            "Carian Knight's Sword",
            "Chrono Blade",
            "Corrupted Mythic Blade",
            "Creation Splitter",
            "Crescent Rose",
            "Cyber Katana",
            "Cyber Mantis Blade",
            "Cyber Sword",
            "Cybernetic Chainsaw Blade",
            "Cybernetic Katana",
            "Cybernetic Knife",
            "Dainsleif",
            "Dark Blade",
            "Dark Cleaver",
            "Death Knight's Dagger",
            "Death Knight's Sword",
            "Demigod's Unholy Blade",
            "Demigod's Unholy Halberd",
            "Demon Lord's Great Axe",
            "Demon Lord's Sword",
            "Demonic Blade",
            "Demonic Cleaver",
            "Divine Axe Rhitta",
            "Divine Justice",
            "Divine Punisher",
            "Divine Reaper",
            "Dragon Slaying blade",
            "Edge Of The Astral Plane",
            "Emberblade",
            "Enigma",
            "Epic Sword",
            "Estoc",
            "Fallen God's Spear",
            "Fallen God's Sword",
            "Floral Longsword",
            "Floral Sabre",
            "Forest Guardian's Glaive",
            "Frost Axe",
            "Frost Blade",
            "Frost Scythe",
            "Hearthflame",
            "Hero Sword",
            "Holy Moonlight Sword",
            "Hornet's Needle",
            "Icewhisper",
            "Jade Halberd",
            "Katana",
            "Legendary Sword",
            "Longsword",
            "Magi Scythe",
            "Masamune",
            "Mjolnir",
            "Molten Blade",
            "Molten Sword",
            "Muramasa",
            "Mystical Spellblade",
            "Mythic Blade",
            "Ocean's Rage",
            "Partisan",
            "Pharaoh's Treasure",
            "Pheonix Grace",
            "Plague Longsword",
            "Power Fuse Hammer",
            "Power Fuse Sword",
            "Requiem of the Ninth Abyss",
            "Ribbon Cleaver",
            "Righteous Relic",
            "Rivers Of Blood",
            "Royal Chakram",
            "Royal Rapier",
            "Sabre",
            "Scissor Blade",
            "Sculk Cleaver",
            "Sculk Scythe",
            "Sculk Sword",
            "Sentinel's Will",
            "Silverine Blade",
            "Soul Claws",
            "Soul Collector",
            "Soul Devourer",
            "Soul Edge",
            "Soul Harvester",
            "Soul Stealer",
            "Soulrender",
            "Star's Edge",
            "Steel Sword",
            "Stop Sign",
            "Storm Bringer",
            "Storm's Edge",
            "Sunbreak",
            "Tengen's Blade",
            "Terra Blade",
            "Thousand Demon Daggers",
            "Thunder Bringer",
            "Thunderbrand",
            "True Excalibur",
            "Vampiric Needle",
            "Wakizashi",
            "Watcher Claymore",
            "Watching Warglaive",
            "Waxweaver",
            "Whisperwind",
            "Wickpiercer",
            "Wraith Scythe",
            "Yoru"
    };

    private static ItemReplacer instance;

    private final ModeSetting model = new ModeSetting("Модель", "Katana", MODELS);

    public ItemReplacer() {
        instance = this;
        addSettings(model);
    }

    public static ItemReplacer getInstance() {
        return instance;
    }

    public static ItemStack getRenderStack(ItemStack stack) {
        return instance == null ? stack : instance.applyRenderStack(stack);
    }

    public static ItemStack getRenderStack(ItemStack stack, LivingEntity entity) {
        return instance == null ? stack : instance.applyRenderStack(stack, entity);
    }

    public static String getSelectedModelName() {
        return instance == null ? "" : instance.model.get();
    }

    private ItemStack applyRenderStack(ItemStack stack) {
        if (!isState() || stack == null || stack.isEmpty() || !isSwordLike(stack)) {
            return stack;
        }

        return applyModel(stack, model.get());
    }

    private ItemStack applyRenderStack(ItemStack stack, LivingEntity entity) {
        if (stack == null || stack.isEmpty() || !isSwordLike(stack)) {
            return stack;
        }

        String remoteModel = "";
        if (entity instanceof PlayerEntity player) {
            remoteModel = GlobalsManager.getInstance().getPartyCustomTexture(player.getName().getString());
        }
        if (remoteModel != null && !remoteModel.isBlank() && matchesModelName(remoteModel)) {
            return applyModel(stack, remoteModel);
        }

        if (!isState()) {
            return stack;
        }

        return applyModel(stack, model.get());
    }

    private static ItemStack applyModel(ItemStack stack, String modelName) {
        ItemStack copy = stack.copy();
        copy.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(
                List.of(),
                List.of(),
                List.of(modelName),
                List.of()
        ));
        Identifier defaultModel = getDefaultItemModel(stack);
        if (defaultModel != null) {
            copy.set(DataComponentTypes.ITEM_MODEL, defaultModel);
        }
        return copy;
    }

    private static Identifier getDefaultItemModel(ItemStack stack) {
        // Все мечи рендерим через iron_sword item-definition, где описаны все
        // варианты кастомных моделей. Это защищает от серверных ресурспаков,
        // которые перезаписывают netherite_sword (и другие) item-definition.
        if (isSwordLike(stack)) return Identifier.of("minecraft", "iron_sword");
        return null;
    }

    private static boolean isSword(Item item) {
        return item == Items.WOODEN_SWORD
                || item == Items.COPPER_SWORD
                || item == Items.STONE_SWORD
                || item == Items.IRON_SWORD
                || item == Items.GOLDEN_SWORD
                || item == Items.DIAMOND_SWORD
                || item == Items.NETHERITE_SWORD;
    }

    private static boolean isSwordLike(ItemStack stack) {
        if (isSword(stack.getItem())) return true;
        return hasCustomSwordTexture(stack);
    }

    private static boolean hasCustomSwordTexture(ItemStack stack) {
        CustomModelDataComponent cmd = stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (cmd != null) {
            List<String> strings = cmd.strings();
            if (strings != null) {
                for (String current : strings) {
                    if (matchesModelName(current)) return true;
                }
            }
        }
        // Переименованные мечи могут хранить название модели только в кастомном имени,
        // без соответствующего custom_model_data — проверяем его тоже.
        Text customName = stack.get(DataComponentTypes.CUSTOM_NAME);
        if (customName != null && matchesModelName(customName.getString())) return true;
        return false;
    }

    private static boolean matchesModelName(String current) {
        if (current == null || current.isEmpty()) return false;
        for (String name : MODELS) {
            if (name.equalsIgnoreCase(current)) return true;
        }
        return false;
    }
}

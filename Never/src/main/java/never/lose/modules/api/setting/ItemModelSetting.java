package never.lose.modules.api.setting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public class ItemModelSetting extends ModeSetting {

    private final Map<String, ItemStack> previewStacks = new LinkedHashMap<>();

    public ItemModelSetting(String name, String defaultValue, String... models) {
        super(name, defaultValue, models);
        for (String model : models) {
            previewStacks.put(model, createPreview(model));
        }
    }

    private ItemStack createPreview(String model) {
        Identifier id = Identifier.tryParse(model.contains(":") ? model : "minecraft:" + model);
        if (id != null && Registries.ITEM.containsId(id)) {
            Item item = Registries.ITEM.get(id);
            if (item != Items.AIR) {
                return new ItemStack(item);
            }
        }
        return new ItemStack(Items.STICK);
    }

    public ItemStack getPreviewStack(String model) {
        return previewStacks.get(model);
    }
}

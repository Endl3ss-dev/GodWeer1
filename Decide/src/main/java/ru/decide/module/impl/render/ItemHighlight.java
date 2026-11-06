/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_332
 */
package ru.decide.module.impl.render;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_332;
import ru.decide.Client;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.Setting;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.DelimiterSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.inventory.SlotCategory;
import ru.decide.utils.inventory.SlotItem;

@ModuleInfo(name="Item Highlight", desc="\u041f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0435\u0442 \u0444\u043e\u043d \u044f\u0447\u0435\u0435\u043a \u0434\u043b\u044f \u043d\u0443\u0436\u043d\u044b\u0445 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class ItemHighlight
extends Module {
    private static ItemHighlight instance;
    private final Map<SlotItem, ColorSetting> itemColors = new EnumMap<SlotItem, ColorSetting>(SlotItem.class);
    private final Map<class_1792, ItemEntry> itemEntries = new LinkedHashMap<class_1792, ItemEntry>();
    public final SliderSetting opacity = new SliderSetting(this, "\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", 80.0f, 0.0f, 100.0f, 1.0f);

    public ItemHighlight() {
        instance = this;
        for (SlotItem slotItem : SlotItem.values()) {
            ColorSetting colorSetting = new ColorSetting((Module)this, slotItem.getDisplayName(), 0xFF000000 | slotItem.getDefaultColor());
            this.itemColors.put(slotItem, colorSetting);
        }
        new DelimiterSetting(this, "\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b");
        this.addItem(class_1802.field_8634, "\u042d\u043d\u0434\u0435\u0440 \u043f\u0435\u0440\u043b", -16738816);
        this.addItem(class_1802.field_8543, "\u0421\u043d\u0435\u0436\u043e\u043a", -10172161);
        this.addItem(class_1802.field_22021, "\u0422\u0440\u0430\u043f\u043a\u0430", -3623786);
        this.addItem(class_1802.field_16539, "\u0421\u0432\u0435\u0442\u0438\u043b\u044c\u043d\u0438\u043a", -27136);
        this.addItem(class_1802.field_8814, "\u0412\u0437\u0440\u044b\u0432\u043d\u0430\u044f \u0442", -65536);
        this.addItem(class_1802.field_8288, "\u0422\u043e\u0442\u0435\u043c", -14336);
        this.addItem(class_1802.field_8399, "\u0410\u0440\u0431\u0430\u043b\u0435\u0442", -6921166);
        this.addItem(class_1802.field_22022, "\u041d\u0435\u0437\u0435\u0440\u0438\u0442\u043e\u0432\u044b\u0439 \u043c\u0435\u0447", -13487566);
        this.addItem(class_1802.field_8233, "\u0425\u043e\u0440\u0443\u0441", -3644216);
        this.addItem(class_1802.field_8479, "\u0421\u0430\u0445\u0430\u0440", -1);
        this.addItem(class_1802.field_8614, "\u041c\u0435\u043c\u0431\u0440\u0430\u043d\u0430", -3623786);
        this.addItem(class_1802.field_8449, "\u041e\u043a\u043e \u044d\u043d\u0434\u0435\u0440\u0430", -10223416);
        this.addItem(class_1802.field_8551, "\u041b\u0430\u043c\u0438\u043d\u0430\u0440\u0438\u044f", -10185166);
        this.addItem(class_1802.field_8287, "\u041f\u0443\u0437\u044b\u0440\u0435\u043a \u043e\u043f\u044b\u0442\u0430", -16725916);
        this.addItem(class_1802.field_8463, "\u0417\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e", -14336);
        this.addItem(class_1802.field_8367, "\u0427\u0430\u0440. \u044f\u0431\u043b\u043e\u043a\u043e", -27136);
    }

    public static ItemHighlight getInstance() {
        ItemHighlight itemHighlight = Client.get().moduleManager().get(ItemHighlight.class);
        return itemHighlight != null ? itemHighlight : instance;
    }

    public int backgroundFor(class_1799 stack, boolean ignored) {
        if (this.isEnabled() && stack != null && !stack.method_7960()) {
            int n = this.potionBackground(stack);
            if (n != 0) {
                return n;
            }
            ItemEntry entry = this.itemEntries.get(stack.method_7909());
            if (entry != null && entry.enabled.getValue().booleanValue()) {
                int rgb = entry.color.getValue() & 0xFFFFFF;
                float alpha = (float)(entry.color.getValue() >> 24 & 0xFF) / 255.0f;
                return this.withOpacity(rgb, alpha);
            }
            return 0;
        }
        return 0;
    }

    public void drawSlotBackground(class_332 ctx, int x, int y, int argb) {
        if (ctx != null && argb != 0) {
            ctx.method_25294(x, y, x + 16, y + 16, argb);
        }
    }

    private void addItem(class_1792 item, String displayName, int color) {
        BooleanSetting enabled = new BooleanSetting((Module)this, displayName, true);
        Setting colorSetting = new ColorSetting((Module)this, displayName + " \u0446\u0432\u0435\u0442", color).setVisible(enabled::getValue);
        this.itemEntries.put(item, new ItemEntry(enabled, (ColorSetting)colorSetting));
    }

    private int potionBackground(class_1799 stack) {
        float alpha;
        int rgb;
        SlotCategory category = SlotCategory.classify(stack);
        if (category == null) {
            return 0;
        }
        SlotItem slotItem = SlotItem.match(stack, category.getItemGroup());
        if (slotItem == null) {
            return 0;
        }
        ColorSetting colorSetting = this.itemColors.get((Object)slotItem);
        if (colorSetting != null) {
            rgb = colorSetting.getValue() & 0xFFFFFF;
            alpha = (float)(colorSetting.getValue() >> 24 & 0xFF) / 255.0f;
        } else {
            rgb = category.getDefaultColor() & 0xFFFFFF;
            alpha = 1.0f;
        }
        return this.withOpacity(rgb, alpha);
    }

    private int withOpacity(int rgb, float alpha) {
        float percent = Math.max(0.0f, Math.min(1.0f, this.opacity.getValue().floatValue() / 100.0f));
        int a = Math.round(255.0f * alpha * percent);
        return a <= 0 ? 0 : a << 24 | rgb;
    }

    @Environment(value=EnvType.CLIENT)
    public record ItemEntry(BooleanSetting enabled, ColorSetting color) {
    }
}


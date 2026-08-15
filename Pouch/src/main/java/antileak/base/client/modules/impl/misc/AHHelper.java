package antileak.base.client.modules.impl.misc;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.FloatSetting;

import java.util.HashMap;
import java.util.Map;

public final class AHHelper extends Module {

    public static final AHHelper INSTANCE = new AHHelper();

    private final FloatSetting profitablePercent = new FloatSetting("Выгодная сделка %", 20.0f, 5.0f, 50.0f, 1.0f);

    private final int slotColor = 0x8C40FF40;

    private final Map<Integer, Integer> slotPrices = new HashMap<>();
    private int averagePrice = 0;
    private long lastScanTime = 0;

    private final java.util.List<Integer> topProfitableSlots = new java.util.ArrayList<>();
    private boolean dataReady = false;

    private AHHelper() {
        super("AH Helper", "помощник в поиске дешевых предметов", ModuleCategory.MISC);
        addSettings(profitablePercent);
    }

    public void scanPrices(Iterable<Slot> slots) {
        long now = System.currentTimeMillis();

        slotPrices.clear();
        topProfitableSlots.clear();
        dataReady = false;

        int totalPrice = 0;
        int count = 0;

        for (Slot slot : slots) {
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;

            int price = extractPricePerItem(stack);
            if (price > 0) {
                slotPrices.put(slot.id, price);
                totalPrice += price;
                count++;
            }
        }

        if (count > 0) {
            averagePrice = totalPrice / count;
            calculateTopProfitable();
            dataReady = true;
        } else {
            averagePrice = 0;
        }

        lastScanTime = now;
    }

    private void calculateTopProfitable() {
        float threshold = averagePrice * (1.0f - profitablePercent.get() / 100.0f);

        java.util.List<Map.Entry<Integer, Integer>> items = new java.util.ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : slotPrices.entrySet()) {
            if (entry.getValue() <= threshold) {
                items.add(entry);
            }
        }

        items.sort(java.util.Comparator.comparingInt(Map.Entry::getValue));

        for (int i = 0; i < Math.min(3, items.size()); i++) {
            topProfitableSlots.add(items.get(i).getKey());
        }
    }

    public boolean shouldRenderSlot(Slot slot) {
        return isEnable() && averagePrice > 0 && dataReady && topProfitableSlots.contains(slot.id);
    }

    public void renderSlot(DrawContext context, Slot slot) {
        context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, slotColor);
    }

    private int extractPricePerItem(ItemStack stack) {
        LoreComponent loreComp = stack.get(DataComponentTypes.LORE);
        if (loreComp == null) return -1;

        for (Text text : loreComp.lines()) {
            String line = Formatting.strip(text.getString());
            if (line == null) continue;

            String lower = line.toLowerCase();
            if (lower.contains("цена") || lower.contains("ценa") || 
                lower.contains("price") || lower.contains("стоимость") ||
                lower.contains("купить за") || lower.contains("buy for")) {

                String priceText = line.replaceAll("[^0-9]", "");
                if (!priceText.isEmpty()) {
                    try {
                        int totalPrice = Integer.parseInt(priceText);
                        if (totalPrice > 0) {
                            int count = Math.max(1, stack.getCount());
                            return totalPrice / count;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        for (Text text : loreComp.lines()) {
            String line = Formatting.strip(text.getString());
            if (line == null) continue;

            String lower = line.toLowerCase();
            if (lower.contains("$") || lower.contains("монет") || lower.contains("coins") ||
                lower.contains("₽") || lower.contains("руб")) {

                String priceText = line.replaceAll("[^0-9]", "");
                if (!priceText.isEmpty()) {
                    try {
                        int totalPrice = Integer.parseInt(priceText);
                        if (totalPrice > 0) {
                            int count = Math.max(1, stack.getCount());
                            return totalPrice / count;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        return -1;
    }
}

package org.privates.client.utils.autobuy;

import com.google.common.collect.Multimap;
import lombok.Getter;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.ITextComponent;
import org.privates.client.utils.chat.ChatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.privates.client.api.interfaces.IMinecraft.mc;

@Getter

public class CustomAutoBuyItem extends AutoBuyItem {
    public String name;
    public List<String> enchantments = new ArrayList<>();
    public List<String> line = new ArrayList<>();
    public List<String> attributes = new ArrayList<>();
    public List<String> effect = new ArrayList<>();
    public boolean strictCheck = false;
    public String ahName;

    public CustomAutoBuyItem(Item item, int price, String ahName) {
        this.item = item;
        this.price = price;
        this.ahName = ahName;
    }

    public static CustomAutoBuyItem withAttributes(String pName, String ahName, ItemStack pStack, List<String> pList) {
        CustomAutoBuyItem item = new CustomAutoBuyItem(pStack.getItem(), 1000, ahName);
        item.name = pName;
        item.attributes = pList;

        return item;
    }

    public String ahName() {
        return ahName;
    }

    public static CustomAutoBuyItem withEnchantment(String pName, String ahName, ItemStack pStack, List<String> pList) {
        CustomAutoBuyItem item = new CustomAutoBuyItem(pStack.getItem(), 1000, ahName);
        item.name = pName;
        item.enchantments = pList;

        return item;
    }

    public static CustomAutoBuyItem withPotionEffect(String pName, String ahName, List<String> pList) {
        CustomAutoBuyItem item = new CustomAutoBuyItem(Items.SPLASH_POTION, 1000, ahName);
        item.name = pName;

        item.effect = pList;

        return item;
    }

    public boolean tryBuy(ItemStack stack, int price, boolean ft) {
        if (!stack.getItem().equals(item)) {
            ChatUtil.addText("§c[DEBUG] Item mismatch: expected " + item.getTranslationKey() + ", found " + stack.getItem().getTranslationKey());
            return false;
        }

        int unitPrice = price / (stack.getCount() == 0 ? 1 : stack.getCount());
        if (unitPrice > this.price) {
            ChatUtil.addText("§c[DEBUG] Price too high: unit " + unitPrice + " > max " + this.price);
            return false;
        }

        if (!effect.isEmpty()) {
            List<String> eff = new ArrayList<>();
            String lvl = "";
            int c = 0;

            if (ft) {
                eff = PotionUtils.getEffectsFromStack(stack).stream()
                        .map(instance ->
                                instance.getPotion().getName().replaceAll("effect.minecraft.", "") + ":" + instance.getAmplifier() + ".0"
                        ).toList();
            } else {
                for (String string : stack.getTag().get("sphereEffect").getString().split("'")[1].split("'")[0].replace("]", "").replace("[", "").split(",")) {
                    if (c == 2)
                        c = 0;
                    String b = string.replace("{\"lvl\":", "").replace("\"nbtName\":", "").replace("\"", "").replace("}", "").replace("\n", "");
                    if (c == 1) {
                        eff.add(b + ":" + lvl);
                        lvl = "";
                        c = 2;
                    }
                    if (c == 0) {
                        lvl = b;
                        c++;
                    }
                }
            }

            if (eff.size() != effect.size()) {
                ChatUtil.addText("§c[DEBUG] Effect count mismatch: required " + effect.size() + ", found " + eff.size());
                return false;
            }
            for (String s : effect) {
                boolean flag = true;
                for (String string : eff) {
                    if (s.equalsIgnoreCase(string)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    ChatUtil.addText("§c[DEBUG] Effect missing: required '" + s + "', found: " + eff);
                    return false;
                }
            }
        }

        if (!attributes.isEmpty()) {
            Multimap<Attribute, AttributeModifier> multimap = stack.getAttributeModifiers(EquipmentSlotType.OFFHAND);

            if (!multimap.isEmpty()) {
                for (String attribute : attributes) {
                    boolean flag = true;
                    for (Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) {

                        if (attribute.equals(entry.getKey().getAttributeName() + entry.getValue().getAmount())) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        ChatUtil.addText("§c[DEBUG] Attribute missing: required '" + attribute + "', found: " + multimap.entries().stream().map(e -> e.getKey().getAttributeName() + e.getValue().getAmount()).toList());
                        return false;
                    }
                }
            } else {
                ChatUtil.addText("§c[DEBUG] No attributes found, but required: " + attributes);
                return false;
            }
        }

        if (!enchantments.isEmpty()) {
            if (!ft) {
                if (strictCheck) {
                    List<String> eList = getEnchantments(stack);
                    if (eList.size() != enchantments.size()) {
                        ChatUtil.addText("§c[DEBUG] Strict enchant count mismatch: required " + enchantments.size() + ", found " + eList.size());
                        return false;
                    }
                    for (String check : eList) {
                        boolean flag = true;
                        for (String in : enchantments) {
                            if ((in.contains("farmer") && in.contains(check)) || check.equalsIgnoreCase(in)) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            ChatUtil.addText("§c[DEBUG] Strict enchant missing: found '" + check + "', required: " + enchantments);
                            return false;
                        }
                    }
                } else {
                    for (String check : enchantments) {
                        boolean flag = true;
                        for (String in : getEnchantments(stack)) {
                            if ((check.contains("farmer") && in.contains(check)) || check.equalsIgnoreCase(in)) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            ChatUtil.addText("§c[DEBUG] Enchant missing: required '" + check + "', found: " + getEnchantments(stack));
                            return false;
                        }
                    }
                }
            } else {
                List<String> enchantmentsList = new ArrayList<>();

                for (var pair : EnchantmentHelper.getEnchantments(stack).entrySet()) {
                    enchantmentsList.add(pair.getKey().getName().replaceAll("enchantment.minecraft.", "") + ":" + pair.getValue());
                }

                for (String string : this.enchantments) {
                    boolean found = true;
                    for (String in : enchantmentsList) {
                        if (in.equalsIgnoreCase(string)) {
                            found = false;
                            break;
                        }
                    }

                    if (found) {
                        ChatUtil.addText("§c[DEBUG] FT enchant missing: required '" + string + "', found: " + enchantmentsList);
                        return false;
                    }
                }
            }
        }

        if (!line.isEmpty()) {
            for (String check : line) {
                boolean flag = true;

                if (checkString(check, stack.getDisplayName().getString())) {
                    flag = false;
                } else {
                    for (ITextComponent in : stack.getTooltip(mc.player, ITooltipFlag.TooltipFlags.NORMAL)) {
                        if (in.equals(stack.getDisplayName())) continue;
                        if (checkString(check, in.getString())) {
                            flag = false;
                            break;
                        }
                    }
                }

                if (flag) {
                    ChatUtil.addText("§c[DEBUG] Line missing: required '" + check + "', displayName: '" + stack.getDisplayName().getString() + "', tooltip: " + stack.getTooltip(mc.player, ITooltipFlag.TooltipFlags.NORMAL).stream().map(ITextComponent::getString).toList());
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkString(String stringFromPlayer, String stringToCheck) {
        return Pattern.compile("\\b" + stringFromPlayer + "\\b", Pattern.CASE_INSENSITIVE).matcher(stringToCheck).find();
    }

    private List<String> getEnchantments(ItemStack stack) {
        List<String> list = new ArrayList<>();
        if (stack.hasTag() && stack.getTag().contains("Enchantments", 10)) {
            ListNBT enchants = stack.getTag().getList("Enchantments", 10);
            for (int i = 0; i < enchants.size(); i++) {
                CompoundNBT enchant = enchants.getCompound(i);
                String id = enchant.getString("id");
                int lvl = enchant.getInt("lvl");
                list.add(id + ":" + lvl);
            }
        }
        return list;
    }
}
package org.privates.client.utils.autobuy;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.privates.client.api.interfaces.IMinecraft.mc;


public class AutoBuyUtils {

    public static boolean isSearchScreen(String title) {
        return title.toLowerCase().contains("аукцион") || title.toLowerCase().contains("поиск:");
    }

    public static List<ITextComponent> getTooltip(ItemStack stack) {
        return stack.getTooltip(mc.player, net.minecraft.client.util.ITooltipFlag.TooltipFlags.NORMAL);
    }

    @Nullable
    public static Integer getPrice(ItemStack stack) {
        try {
            if (!stack.hasTag() || !stack.getTag().contains("display", 10)) {
                return null;
            }

            CompoundNBT display = stack.getTag().getCompound("display");
            if (!display.contains("Lore", 9)) {
                return null;
            }

            ListNBT lore = display.getList("Lore", 8);
            for (int i = 0; i < lore.size(); i++) {
                String string = ITextComponent.Serializer.getComponentFromJsonLenient(lore.getString(i)).getString();

                if (Stream.of("$", "₽", "Цeнa", "Цена:", "Стоимость").anyMatch(string::contains)) {
                    List<Character> list = new ArrayList<>();
                    for (char c : string.toCharArray()) {
                        if (c == '.') break;
                        if (c >= '0' && c <= '9') list.add(c);
                    }
                    char[] chars = new char[list.size()];
                    for (int index = 0; index < list.size(); index++) chars[index] = list.get(index);
                    try {
                        return Integer.parseInt(new String(chars));
                    } catch (NumberFormatException ex) {
                    }
                }
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    @Nullable
    public static String getSellerName(ItemStack stack) {
        try {
            if (!stack.hasTag() || !stack.getTag().contains("display", 10)) {
                return null;
            }

            CompoundNBT display = stack.getTag().getCompound("display");
            if (!display.contains("Lore", 9)) {
                return null;
            }

            ListNBT lore = display.getList("Lore", 8);
            for (int i = 0; i < lore.size(); i++) {
                String string = ITextComponent.Serializer.getComponentFromJsonLenient(lore.getString(i)).getString();

                if (Stream.of("Продавец:").anyMatch(string::contains)) {
                    List<Character> list = new ArrayList<>();
                    for (char c : string.toCharArray()) {
                        if (c == '.' || c == '(') break;
                        if (isLatinLetter(c) || Character.isDigit(c)) list.add(c);
                    }
                    char[] chars = new char[list.size()];
                    for (int index = 0; index < list.size(); index++) chars[index] = list.get(index);
                    try {
                        return new String(chars);
                    } catch (NumberFormatException ex) {
                    }
                }
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean isLatinLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_';
    }

    public static int getPriceHolyWorld(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("display", 10)) {
            CompoundNBT display = tag.getCompound("display");
            if (display.contains("Lore", 9)) {
                ListNBT lore = display.getList("Lore", 8);
                for (int j = 0; j < lore.size(); ++j) {
                    String loreLine = lore.getString(j);
                    try {
                        JsonObject object = new JsonParser().parse(loreLine).getAsJsonObject();
                        if (object.has("extra") && object.getAsJsonArray("extra").size() > 2) {
                            JsonArray array = object.getAsJsonArray("extra");
                            JsonObject title = array.get(1).getAsJsonObject();
                            if (title.has("text") && title.get("text").getAsString().trim().toLowerCase().contains("цена")) {
                                String line = array.get(2).getAsJsonObject().get("text").getAsString().trim().substring(1).replaceAll("[ ,]", "");
                                return Integer.parseInt(line);
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return -1;
    }

    public static int[] calcSum(List<ITextComponent> s) {
        if (s == null || s.isEmpty()) return null;
        int i1 = -1;
        int i2 = -1;
        for (ITextComponent t : s) {
            String string = t.getString();
            StringBuilder stringBuilder = new StringBuilder();
            for (char c : string.toCharArray()) {
                try {
                    stringBuilder.append(Integer.parseInt(String.valueOf(c)));
                } catch (Exception ignore) {
                }
            }
            if (!stringBuilder.toString().isEmpty()) {
                if (i1 < 0) {
                    i1 = Integer.parseInt(stringBuilder.toString());
                } else {
                    i2 = Integer.parseInt(stringBuilder.toString());
                }
            }
        }
        return new int[]{i1, i2};
    }
}
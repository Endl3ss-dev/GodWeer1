/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.LoreComponent
 *  net.minecraft.component.type.NbtComponent
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.registry.Registries
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  sLM.4ZXYEo
 *  sg.ec.ТО$2
 */
package minced.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import minced.core.PathUtil;
import minced.core.InventoryStorage;
import minced.core.ConfigFile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sLM._4ZXYEo;
import sg.ec.to2;

public class Inventory
extends ConfigFile
{
    static String cfr_renamed_350;
    static String cfr_renamed_351;
    static String cfr_renamed_352;
    static String cfr_renamed_353;
    static String cfr_renamed_354;
    static String cfr_renamed_355;
    static String cfr_renamed_356;
    static String cfr_renamed_357;

    Object data;
    static String field6817 = "inventories\\inventory.file";
    static File field6818;
    static String field6819;
    static String field6820;
    static String field6821;
    static String field6822;
    static String field6823;
    static String field6824;
    static String field6825;
    static String field6826;
    static String field6827;
    static String field6828;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field6829;
    static String field6830;
    static String field6831;
    static String field6832;
    static String field6833;
    static String field6834;
    static long field6835;
    static String field6836;
    static String field6837;
    static String field6838;
    static String field6839;
    static String field6840;
    Gson GSON;
    static String field6841;

    @Override
    protected void resetData() {
        this.data = new InventoryStorage();
    }

    public Inventory() {
        super(field6817);
    }

    private static /* synthetic */ void method1689(JsonArray param0, Text param1) {
        param0.add(param1.getString());
    }

    @Override
    protected JsonObject writeJson() {
        return new JsonObject();
    }

    @Override
    public void load() {
        field6818.mkdirs();
        super.load();
    }

    public List method1690() {
        File[] v1 = field6818.listFiles((param0, param1) -> param1.endsWith(field6834));
        if (v1 == null) {
            return Collections.emptyList();
        }
        ArrayList<String> v2 = new ArrayList<String>();
        for (File v6 : v1) {
            v2.add(v6.getName().replace(field6819, ""));
        }
        return v2;
    }

    private JsonObject method1691(ItemStack param1) {
        NbtCompound v5;
        JsonObject v2 = new JsonObject();
        Identifier v3 = Registries.ITEM.getId(param1.getItem());
        v2.addProperty(field6820, v3.toString());
        v2.addProperty(field6821, (Number)param1.getCount());
        JsonObject v4 = new JsonObject();
        if (param1.contains(DataComponentTypes.CUSTOM_DATA) && !(v5 = ((NbtComponent)param1.get(DataComponentTypes.CUSTOM_DATA)).copyNbt()).isEmpty()) {
            v4.addProperty(field6822, v5.toString());
        }
        if (param1.contains(DataComponentTypes.CUSTOM_NAME)) {
            v4.addProperty(field6823, ((Text)param1.get(DataComponentTypes.CUSTOM_NAME)).getString());
        }
        if (param1.contains(DataComponentTypes.LORE)) {
            JsonArray v5s = new JsonArray();
            ((LoreComponent)param1.get(DataComponentTypes.LORE)).lines().forEach(arg_0 -> Inventory.method1689((JsonArray)v5s, arg_0));
            v4.add(field6824, (JsonElement)v5s);
        }
        if (param1.contains(DataComponentTypes.DAMAGE)) {
            v4.addProperty(field6825, (Number)param1.get(DataComponentTypes.DAMAGE));
        }
        if (!v4.isEmpty()) {
            v2.add(field6826, (JsonElement)v4);
        }
        return v2;
    }

    public static void method1692() {
        field6818 = new File((String)((Object)_4ZXYEo.ф西("makeConcatWithConstants", new Object[]{}, (String)PathUtil.field7558)));
    }

    private boolean cfr_renamed_4(ItemStack param1, ItemStack param2) {
        Object v4;
        Object v3;
        if (param1.isEmpty() || param2.isEmpty()) {
            return false;
        }
        if (!param1.getItem().equals(param2.getItem())) {
            return false;
        }
        if (param1.contains(DataComponentTypes.CUSTOM_DATA) && param2.contains(DataComponentTypes.CUSTOM_DATA) ? !(v3 = ((NbtComponent)param1.get(DataComponentTypes.CUSTOM_DATA)).copyNbt()).equals(v4 = ((NbtComponent)param2.get(DataComponentTypes.CUSTOM_DATA)).copyNbt()) : param1.contains(DataComponentTypes.CUSTOM_DATA) != param2.contains(DataComponentTypes.CUSTOM_DATA)) {
            return false;
        }
        if (param1.contains(DataComponentTypes.CUSTOM_NAME) && param2.contains(DataComponentTypes.CUSTOM_NAME)) {
            v3 = ((Text)param1.get(DataComponentTypes.CUSTOM_NAME)).getString();
            v4 = ((Text)param2.get(DataComponentTypes.CUSTOM_NAME)).getString();
            return ((String)v3).equals(v4);
        }
        return param1.contains(DataComponentTypes.CUSTOM_NAME) == param2.contains(DataComponentTypes.CUSTOM_NAME);
    }

    private ItemStack method1693(JsonObject param1) {
        String v2 = param1.get(field6827).getAsString();
        Identifier v3 = Identifier.tryParse((String)v2);
        if (v3 == null) {
            return ItemStack.EMPTY;
        }
        return new ItemStack((ItemConvertible)Registries.ITEM.get(v3), param1.get(field6828).getAsInt());
    }

    public void method1694(String param1) {
        if (Inventory.I.player == null) {
            return;
        }
        if (param1 == null || param1.trim().isEmpty()) {
            System.err.println(field6829);
            return;
        }
        File v2 = new File(field6818, (String)((Object)_4ZXYEo.ф诶("makeConcatWithConstants", new Object[]{}, (String)param1.trim())));
        if (!v2.exists()) {
            System.err.println((String)((Object)_4ZXYEo.фЫ("makeConcatWithConstants", new Object[]{}, (String)param1)));
            return;
        }
        String v3;
        try {
            v3 = Files.readString(v2.toPath());
        }
        catch (java.io.IOException v13) {
            System.err.println(v13);
            return;
        }
        JsonObject v4 = new JsonParser().parse(v3).getAsJsonObject();
        if (!v4.has(field6830)) {
            return;
        }
        JsonArray v5 = v4.getAsJsonArray(field6831);
        HashMap<Integer, ItemStack> v6 = new HashMap<Integer, ItemStack>();
        for (JsonElement v8 : v5) {
            JsonObject v9 = v8.getAsJsonObject();
            int v10 = v9.get(field6832).getAsInt();
            JsonObject v11 = v9.getAsJsonObject(field6833);
            ItemStack v12 = this.method1693(v11);
            if (v12 == null || v12.isEmpty()) continue;
            v6.put(v10, v12);
        }
        this.method1696(v6);
    }

    private ItemStack I(int param1) {
        if (Inventory.I.player == null) {
            return ItemStack.EMPTY;
        }
        if (param1 < 36) {
            return Inventory.I.player.getInventory().getStack(param1);
        }
        if (param1 < 40) {
            int v2 = param1 - 36;
            return (ItemStack)Inventory.I.player.getInventory().armor.get(v2);
        }
        if (param1 == 40) {
            return Inventory.I.player.getOffHandStack();
        }
        return ItemStack.EMPTY;
    }

    private void method1696(Map param1) {
        if (Inventory.I.player == null || Inventory.I.interactionManager == null) {
            return;
        }
        ArrayList<to2._2> v2 = new ArrayList<to2._2>();
        HashSet<Integer> v3 = new HashSet<Integer>();
        for (Map.Entry entry : (Iterable<java.util.Map.Entry>)(param1.entrySet())) {
            int v6 = (Integer)entry.getKey();
            if (v6 >= 36) continue;
            ItemStack v7 = (ItemStack)entry.getValue();
            ItemStack v8 = Inventory.I.player.getInventory().getStack(v6);
            if (this.cfr_renamed_4(v8, v7)) {
                v3.add(v6);
                continue;
            }
            int v9 = -1;
            for (int v10 = 0; v10 < 36; ++v10) {
                ItemStack v11;
                if (v3.contains(v10) || !this.cfr_renamed_4(v11 = Inventory.I.player.getInventory().getStack(v10), v7)) continue;
                v9 = v10;
                break;
            }
            if (v9 == -1) continue;
            v2.add(new to2._2(v9, v6));
            v3.add(v9);
        }
        for (to2._2 var5_7 : v2) {
            this.O(var5_7.н0, var5_7.cfr_renamed);
            try {
                Thread.sleep(field6835);
            }
            catch (InterruptedException v12) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public void method1697(String param1) {
        if (Inventory.I.player == null) {
            return;
        }
        if (param1 == null || param1.trim().isEmpty()) {
            System.err.println(field6836);
            return;
        }
        field6818.mkdirs();
        File v2 = new File(field6818, (String)((Object)_4ZXYEo.фй("makeConcatWithConstants", new Object[]{}, (String)param1.trim())));
        JsonObject v3 = new JsonObject();
        JsonArray v4 = new JsonArray();
        for (int v5 = 0; v5 < 41; ++v5) {
            ItemStack v6 = this.I(v5);
            if (v6.isEmpty()) continue;
            JsonObject v7 = new JsonObject();
            v7.addProperty(field6837, (Number)v5);
            v7.add(field6838, (JsonElement)this.method1691(v6));
            v4.add((JsonElement)v7);
        }
        v3.add(field6839, (JsonElement)v4);
        v3.addProperty(field6840, (Number)System.currentTimeMillis());
        String v5 = GSON.toJson((JsonElement)v3);
        try {
            Files.writeString(v2.toPath(), (CharSequence)v5, new OpenOption[0]);
        }
        catch (java.io.IOException v13) {
            System.err.println(v13);
        }
    }

    public boolean method1698(String param1) {
        if (param1 == null || param1.trim().isEmpty()) {
            System.err.println(field6841);
            return false;
        }
        return new File(field6818, (String)((Object)_4ZXYEo.фЯ("makeConcatWithConstants", new Object[]{}, (String)param1.trim()))).delete();
    }

    private void O(int param1, int param2) {
        if (Inventory.I.player == null || Inventory.I.interactionManager == null) {
            return;
        }
        if (param1 == param2) {
            return;
        }
        int v3 = param1 < 9 ? param1 + 36 : param1;
        int v4 = param2 < 9 ? param2 + 36 : param2;
        Inventory.I.interactionManager.clickSlot(Inventory.I.player.currentScreenHandler.syncId, v3, 0, SlotActionType.PICKUP, (PlayerEntity)Inventory.I.player);
        Inventory.I.interactionManager.clickSlot(Inventory.I.player.currentScreenHandler.syncId, v4, 0, SlotActionType.PICKUP, (PlayerEntity)Inventory.I.player);
        Inventory.I.interactionManager.clickSlot(Inventory.I.player.currentScreenHandler.syncId, v3, 0, SlotActionType.PICKUP, (PlayerEntity)Inventory.I.player);
    }

    @Override
    protected void readJson(JsonObject param1) {
    }

    static {
        field6819 = ".inv";
        field6836 = "Cannot save inventory: name is null or empty";
        cfr_renamed_350 = "\u0001.inv";
        field6837 = "slot";
        field6838 = "item";
        field6839 = "slots";
        field6840 = "creationDate";
        cfr_renamed_351 = "Failed to save inventory: \u0001 - \u0001";
        field6829 = "Cannot load inventory: name is null or empty";
        cfr_renamed_352 = "\u0001.inv";
        cfr_renamed_353 = "Inventory file not found: \u0001";
        field6830 = "slots";
        field6831 = "slots";
        field6832 = "slot";
        field6833 = "item";
        cfr_renamed_354 = "Failed to load inventory: \u0001 - \u0001";
        field6841 = "Cannot delete inventory: name is null or empty";
        cfr_renamed_355 = "\u0001.inv";
        field6820 = "id";
        field6821 = "count";
        field6822 = "custom_nbt";
        field6823 = "custom_name";
        field6824 = "lore";
        field6825 = "damage";
        field6826 = "components";
        field6827 = "id";
        field6828 = "count";
        cfr_renamed_356 = "Failed to deserialize item: \u0001";
        field6835 = 50L;
        field6834 = ".inv";
        cfr_renamed_357 = "\u0001inventories\\";
        Inventory.method1692();
    }
}




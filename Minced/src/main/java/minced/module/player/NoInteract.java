/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.item.Items
 *  net.minecraft.util.Hand
 */
package minced.module.player;
import minced.core.EventHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.EventClickBlockRight;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class NoInteract
extends Module {
    BooleanSetting cfr_underscore;
    static String field3333;
    static String field3334;
    static String field3335;
    static String field3336;
    static String field3337;
    static String field3338;
    static String field3339;
    static String field3340;
    static String field3341;
    static String field3342;
    static String field3343;
    static String field3344;
    MultiBooleanSetting field3345;
    BooleanSetting field3346 = new BooleanSetting(field3349, false);
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field3347 = "No Interact";
    static String field3348 = "Отменяет взаимодействие по выбранным сущностям и блокам";
    static String field3349 = "Не ставить шар,сферу";
    static String field3350 = "Все блоки";
    static String field3351 = "Обьекты";
    static String field3352 = "Стойки";
    static String field3353 = "Сундуки";
    static String field3354 = "Двери";
    static String field3355 = "Кнопки";
    static String field3356 = "Воронки";
    static String field3357 = "Раздатчики";
    static String field3358 = "Нотные блоки";
    static String field3359 = "Верстаки";
    static String field3360 = "Люки";
    static String field3361 = "Печки";
    static String field3362 = "Калитки";
    static String field3363 = "Наковальни";
    static String field3364 = "Рычаги";

    public Set method942() {
        HashSet v1 = new HashSet();
        this.method943(v1, field3333, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR, Blocks.BIRCH_DOOR, Blocks.IRON_DOOR, Blocks.JUNGLE_DOOR, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR);
        this.method943(v1, field3334, Blocks.OAK_BUTTON, Blocks.STONE_BUTTON, Blocks.CRIMSON_BUTTON, Blocks.WARPED_BUTTON);
        this.method943(v1, field3335, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST);
        this.method943(v1, field3336, Blocks.HOPPER);
        this.method943(v1, field3337, Blocks.DISPENSER, Blocks.DROPPER);
        this.method943(v1, field3338, Blocks.NOTE_BLOCK);
        this.method943(v1, field3339, Blocks.CRAFTING_TABLE, Blocks.CARTOGRAPHY_TABLE, Blocks.SMITHING_TABLE, Blocks.LOOM);
        this.method943(v1, field3340, Blocks.OAK_TRAPDOOR, Blocks.IRON_TRAPDOOR);
        this.method943(v1, field3341, Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER);
        this.method943(v1, field3342, Blocks.ACACIA_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE);
        this.method943(v1, field3343, Blocks.ANVIL);
        this.method943(v1, field3344, Blocks.LEVER);
        return v1;
    }

    private void method943(Set param1, String param2, Block ... param3) {
        if (this.field3345.method1898(param2).booleanValue()) {
            Collections.addAll(param1, param3);
        }
    }

    @EventHandler(2)
    private void method944(EventClickBlockRight param1) {
        if (((Boolean)this.field3346.getValue()).booleanValue() && param1.method2304() == Hand.OFF_HAND && NoInteract.I.player.getOffHandStack().getItem() == Items.PLAYER_HEAD) {
            param1.method2298(true);
        }
        if (((Boolean)this.cfr_underscore.getValue()).booleanValue()) {
            param1.method2298(true);
        } else if (this.method942().contains(param1.method2306().getBlockState(param1.method2303().getBlockPos()).getBlock())) {
            param1.method2298(true);
        }
    }

    public NoInteract() {
        super(field3347, field3348, Combat.Player);
        this.cfr_underscore = new BooleanSetting(field3350, false);
        this.field3345 = new MultiBooleanSetting(field3351, () -> (Boolean)this.cfr_underscore.getValue() == false, new BooleanSetting(field3352, true), new BooleanSetting(field3353, true), new BooleanSetting(field3354, true), new BooleanSetting(field3355, true), new BooleanSetting(field3356, true), new BooleanSetting(field3357, true), new BooleanSetting(field3358, true), new BooleanSetting(field3359, true), new BooleanSetting(field3360, true), new BooleanSetting(field3361, true), new BooleanSetting(field3362, true), new BooleanSetting(field3363, true), new BooleanSetting(field3364, true));
        this.addSettings(this.field3346, this.cfr_underscore, this.field3345);
    }

    static {
        field3333 = "Двери";
        field3334 = "Кнопки";
        field3335 = "Сундуки";
        field3336 = "Воронки";
        field3337 = "Раздатчики";
        field3338 = "Нотные блоки";
        field3339 = "Верстаки";
        field3340 = "Люки";
        field3341 = "Печки";
        field3342 = "Калитки";
        field3343 = "Наковальни";
        field3344 = "Рычаги";
    }
}




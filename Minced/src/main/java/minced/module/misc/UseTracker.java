/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.PotionContentsComponent
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item$TooltipContext
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.consume.UseAction
 *  net.minecraft.item.tooltip.TooltipType
 *  net.minecraft.text.HoverEvent
 *  net.minecraft.text.HoverEvent$Action
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  sLM.4ZXYEo
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.util.List;
import minced.core.BooleanSetting;
import minced.core.EventEntityUseItem;
import minced.core.EventTotemPop;
import minced.core.NotificationManager;
import minced.core.ChatUtil;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sLM._4ZXYEo;

public class UseTracker
extends Module {
    static String Оч;

    static String field1401 = "Use Tracker";
    static String field1402 = "Оповещает об использовании еды/зелий другими игроками";
    static String field1403 = "Отслеживать снесенные тотемы";
    BooleanSetting field1404 = new BooleanSetting(field1403, true);
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field1405;
    static String field1406;
    static String field1407;
    static String field1408;
    static String field1409;
    static String field1410;
    static String field1411;
    static String field1412;
    static String field1413;
    static String field1414;
    static String field1415;

    public UseTracker() {
        super(field1401, field1402, Combat.Miscellaneous);
        this.addSettings(this.field1404);
    }

    @EventHandler(2)
    public void method490(EventEntityUseItem param12) {
        PlayerEntity v2;
        ItemStack v12;
        LivingEntity v3 = param12.method2351();
        if (v3 instanceof PlayerEntity) {
            v2 = (PlayerEntity)v3;
            v12 = param12.method2354();
            if (v12.isEmpty()) {
                return;
            }
        } else {
            return;
        }
        if (!param12.method2353()) {
            return;
        }
        UseAction v4 = v12.getItem().getUseAction(v12);
        if (v4 != UseAction.EAT && v4 != UseAction.DRINK) {
            return;
        }
        MutableText v5 = v12.getName().copy();
        List v6 = v12.getTooltip(Item.TooltipContext.DEFAULT, v2, (TooltipType)(UseTracker.I.options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC));
        MutableText v7 = Text.empty();
        for (int v8 = 0; v8 < v6.size(); ++v8) {
            if (v8 > 0) {
                v7.append((Text)Text.literal((String)field1405));
            }
            v7.append((Text)((Text)v6.get(v8)).copy());
        }
        HoverEvent v8 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (Text)v7);
        MutableText v9 = Text.empty().append((Text)v2.getName().copy().formatted(Formatting.WHITE)).append((Text)Text.literal((String)field1406).formatted(Formatting.GRAY)).append((Text)v5).styled(param1 -> param1.withHoverEvent(v8));
        Identifier v10 = v12.getItem() == Items.GOLDEN_APPLE || v12.getItem() == Items.ENCHANTED_GOLDEN_APPLE ? Identifier.of((String)field1407, (String)field1408) : Identifier.of((String)field1409, (String)((Object)_4ZXYEo.ц("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(v12.getItem()))));
        ChatUtil.method3308(v9, new Object[0]);
        PotionContentsComponent v11 = (PotionContentsComponent)v12.getOrDefault(DataComponentTypes.POTION_CONTENTS, (Object)PotionContentsComponent.DEFAULT);
        NotificationManager.method2453(v10, (Text)v9, v11.getColor());
    }

    @EventHandler(2)
    public void method491(EventTotemPop param12) {
        PlayerEntity v2;
        Entity v3;
        block6: {
            block5: {
                v3 = param12.method2450();
                if (!(v3 instanceof PlayerEntity)) break block5;
                v2 = (PlayerEntity)v3;
                if (((Boolean)this.field1404.getValue()).booleanValue()) break block6;
            }
            return;
        }
        ItemStack v3s = v2.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING ? v2.getOffHandStack() : (v2.getMainHandStack().getItem() == Items.TOTEM_OF_UNDYING ? v2.getMainHandStack() : new ItemStack((ItemConvertible)Items.TOTEM_OF_UNDYING));
        boolean v4 = v3s.hasEnchantments();
        MutableText v5 = v3s.getName().copy();
        List v6 = v3s.getTooltip(Item.TooltipContext.DEFAULT, v2, (TooltipType)(UseTracker.I.options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC));
        MutableText v7 = Text.empty();
        for (int v8 = 0; v8 < v6.size(); ++v8) {
            if (v8 > 0) {
                v7.append((Text)Text.literal((String)field1410));
            }
            v7.append((Text)((Text)v6.get(v8)).copy());
        }
        HoverEvent v8 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (Text)v7);
        MutableText v9 = Text.empty().append((Text)v2.getName().copy().formatted(Formatting.WHITE)).append((Text)Text.literal((String)field1411).formatted(Formatting.GRAY)).append((Text)v5).append((Text)Text.literal((String)field1412).formatted(Formatting.GRAY)).append((Text)Text.literal((String)field1413).formatted(v4 ? Formatting.GREEN : Formatting.RED)).styled(param1 -> param1.withHoverEvent(v8));
        ChatUtil.method3308(v9, new Object[0]);
        NotificationManager.method2454(Identifier.of((String)field1414, (String)field1415), (Text)v9);
    }

    static {
        field1410 = "\n";
        field1411 = " потерял ";
        field1412 = ", зачарован: ";
        field1413 = "●";
        field1414 = "minecraft";
        field1415 = "textures/item/totem_of_undying.png";
        field1405 = "\n";
        field1406 = " использовал ";
        field1407 = "minecraft";
        field1408 = "textures/item/golden_apple.png";
        field1409 = "minecraft";
        Оч = "textures/item/\u0001.png";
    }
}




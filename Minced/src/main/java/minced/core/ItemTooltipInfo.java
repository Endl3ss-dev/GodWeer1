/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item$TooltipContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.tooltip.TooltipType
 *  net.minecraft.text.Text
 */
package minced.core;

import java.util.List;
import minced.core.ServerDetector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

public class ItemTooltipInfo {
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field7576;
    static String field7577;
    static String field7578;
    static String field7579;
    static String field7580;
    static String field7581;
    static String field7582;
    static String field7583;
    static String field7584;
    static String field7585;
    static String field7586;
    static String field7587;
    static String field7588;
    static String field7589;
    static String field7590;
    static String field7591;
    static String field7592;
    static String field7593;
    static String field7594;
    static String field7595;
    static String field7596;
    static String field7597;

    public int method1859(ItemStack param1) {
        List<Text> v2 = (List<Text>) param1.getTooltip(Item.TooltipContext.DEFAULT, (PlayerEntity)ItemTooltipInfo.I.player, (TooltipType)(ItemTooltipInfo.I.options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC));
        String v3 = ServerDetector.method2684(field7576) ? field7577 : (ServerDetector.method2684(field7578) ? field7579 : field7580);
        for (Text v5 : v2) {
            String v6 = v5.getString();
            if (!v6.contains(v3)) continue;
            if (ServerDetector.method2684(field7581)) {
                return Integer.parseInt(v6.replace(field7582, "").replace(field7583, "").replace(field7584, ""));
            }
            if (ServerDetector.method2684(field7585)) {
                return Integer.parseInt(v6.replace(field7586, "").replace(field7587, "").replace(field7588, "").replace(field7589, "").replace(field7590, "").replace(field7591, "").replace(field7592, ""));
            }
            return Integer.parseInt(v6.replace(field7593, "").replace(field7594, "").replace(field7595, "").replace(field7596, "").replace(field7597, "").trim());
        }
        return -1;
    }

    static {
        field7576 = "spooky";
        field7577 = "Цена";
        field7578 = "holyworld";
        field7579 = "Цена";
        field7580 = "Ценa";
        field7581 = "funtime";
        field7582 = "$ Ценa $";
        field7583 = " ";
        field7584 = ",";
        field7585 = "holyworld";
        field7586 = "▍";
        field7587 = "¤";
        field7588 = "Цена";
        field7589 = " ";
        field7590 = ",";
        field7591 = ":";
        field7592 = "шага";
        field7593 = "Цена";
        field7594 = "$";
        field7595 = ": ";
        field7596 = " ";
        field7597 = ",";
    }
}




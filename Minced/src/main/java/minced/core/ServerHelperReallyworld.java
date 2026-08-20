/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.LoreComponent
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
 *  net.minecraft.text.Text
 *  sLM.4ZXYEo
 *  sg.ec.2>
 *  sg.ec.СН$Ч
 */
package minced.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BooleanSupplier;
import minced.core.ColorUtil;
import minced.core.BindSetting;
import minced.core.BooleanSetting;
import minced.core.ServerHelperBase;
import minced.core.EventPacket;
import minced.core.ChatUtil;
import minced.core.InventoryUtil;
import minced.enums.KeyCode;
import minced.module.misc.ServerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.text.Text;
import sLM._4ZXYEo;
import sg.ec.CfrRenamed298;
import sg.ec.sn;

public class ServerHelperReallyworld
extends ServerHelperBase {
    static String И4;

    List cfr_renamed_4;
    static String field7623;
    static String field7624;
    MinecraftClient var_net_minecraft_client_MinecraftClient_I;
    ServerHelper field7625;
    static long field7626;
    static String field7627;
    static String field7628;
    static String field7629;
    static String field7630;
    static String field7631;
    static String field7632;
    static String field7633;
    static String field7634;
    static String field7635;
    static String field7636;
    static String field7637;
    static String field7638;
    static float field7639;
    static String field7640;
    BooleanSupplier var_java_util_function_BooleanSupplier_I;
    BooleanSetting field7641;
    static String field7642;
    BooleanSetting field7643;
    Set<String> field7644 = new HashSet<String>(Arrays.asList(field7646, field7647, field7648, field7649, field7650, field7651, field7652, field7653, field7654, field7655, field7656, field7657, field7658, field7659, field7660, field7661, field7662, field7663, field7664, field7665, field7666, field7667, field7668, field7669, field7670, field7671, field7672, field7673, field7674, field7675, field7676, field7677, field7678, field7679, field7680, field7681, field7682, field7683, field7684, field7685, field7686, field7687, field7688, field7689, field7690, field7691, field7692, field7693, field7694, field7695, field7696, field7697, field7698, field7699, field7700, field7701, field7702, field7703, field7704, field7705, field7706, field7707, field7708));
    static String field7645;
    static String field7646 = "field7646";
    static String field7647 = "field7647";
    static String field7648 = "field7648";
    static String field7649 = "field7649";
    static String field7650 = "field7650";
    static String field7651 = "field7651";
    static String field7652 = "field7652";
    static String field7653 = "field7653";
    static String field7654 = "field7654";
    static String field7655 = "field7655";
    static String field7656 = "field7656";
    static String field7657 = "field7657";
    static String field7658 = "field7658";
    static String field7659 = "field7659";
    static String field7660 = "field7660";
    static String field7661 = "field7661";
    static String field7662 = "field7662";
    static String field7663 = "field7663";
    static String field7664 = "field7664";
    static String field7665 = "field7665";
    static String field7666 = "field7666";
    static String field7667 = "field7667";
    static String field7668 = "field7668";
    static String field7669 = "field7669";
    static String field7670 = "field7670";
    static String field7671 = "field7671";
    static String field7672 = "field7672";
    static String field7673 = "field7673";
    static String field7674 = "field7674";
    static String field7675 = "field7675";
    static String field7676 = "field7676";
    static String field7677 = "field7677";
    static String field7678 = "field7678";
    static String field7679 = "field7679";
    static String field7680 = "field7680";
    static String field7681 = "field7681";
    static String field7682 = "field7682";
    static String field7683 = "field7683";
    static String field7684 = "field7684";
    static String field7685 = "field7685";
    static String field7686 = "field7686";
    static String field7687 = "field7687";
    static String field7688 = "field7688";
    static String field7689 = "field7689";
    static String field7690 = "field7690";
    static String field7691 = "field7691";
    static String field7692 = "field7692";
    static String field7693 = "field7693";
    static String field7694 = "field7694";
    static String field7695 = "field7695";
    static String field7696 = "field7696";
    static String field7697 = "field7697";
    static String field7698 = "field7698";
    static String field7699 = "field7699";
    static String field7700 = "field7700";
    static String field7701 = "field7701";
    static String field7702 = "field7702";
    static String field7703 = "field7703";
    static String field7704 = "field7704";
    static String field7705 = "field7705";
    static String field7706 = "field7706";
    static String field7707 = "field7707";
    static String field7708 = "field7708";
    static String field7709;
    static String field7710;
    static String field7711;
    static String field7712;
    static String field7713;
    static String field7714;
    static String field7715;
    static String field7716;
    static String field7717;
    static String field7718;
    static String field7719;
    static String field7720;
    static String field7721;
    static String field7722;
    static String field7723;
    static String field7724;
    static String field7725;
    static String field7726;
    static String field7727;
    static String field7728;
    static String field7729;
    static String field7730;
    static String field7731;
    static String field7732;
    static String field7733;
    static String field7734;
    static String field7735;

    @Override
    public boolean method1888(int param1) {
        BindSetting v3;
        if (this.cfr_renamed_4 == null || this.cfr_renamed_4.isEmpty()) {
            return false;
        }
        BindSetting v2 = (BindSetting)this.cfr_renamed_4.get(0);
        if ((Integer)v2.getValue() != -1 && (Integer)v2.getValue() == param1) {
            ServerHelperBase.method1962();
            return true;
        }
        if (this.cfr_renamed_4.size() > 1 && (Integer)(v3 = (BindSetting)this.cfr_renamed_4.get(1)).getValue() != -1 && (Integer)v3.getValue() == param1) {
            int v4 = this.method1896(field7623);
            if (v4 == -1) {
                ChatUtil.method3308(field7624, new Object[0]);
                return true;
            }
            ItemStack v5 = this.var_net_minecraft_client_MinecraftClient_I.player.getInventory().getStack(v4);
            if (!v5.isEmpty()) {
                this.field7625.method666(v4);
            }
            return true;
        }
        if (this.cfr_renamed_4.size() > 2 && (Integer)(v3 = (BindSetting)this.cfr_renamed_4.get(2)).getValue() != -1 && (Integer)v3.getValue() == param1) {
            if (this.var_net_minecraft_client_MinecraftClient_I.options != null) {
                this.var_net_minecraft_client_MinecraftClient_I.options.sneakKey.setPressed(true);
                final MinecraftClient mc = this.var_net_minecraft_client_MinecraftClient_I;
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (mc.options != null) {
                            mc.options.sneakKey.setPressed(false);
                        }
                    }
                }, field7626);
            }
            return true;
        }
        if (this.cfr_renamed_4.size() > 3 && (Integer)(v3 = (BindSetting)this.cfr_renamed_4.get(3)).getValue() != -1 && (Integer)v3.getValue() == param1) {
            int v4 = this.method1896(field7627);
            if (v4 == -1) {
                ChatUtil.method3308(field7628, new Object[0]);
                return true;
            }
            ItemStack v5 = this.var_net_minecraft_client_MinecraftClient_I.player.getInventory().getStack(v4);
            if (!v5.isEmpty()) {
                this.field7625.method666(v4);
            }
            return true;
        }
        if (this.cfr_renamed_4.size() > 4 && (Integer)(v3 = (BindSetting)this.cfr_renamed_4.get(4)).getValue() != -1 && (Integer)v3.getValue() == param1) {
            int v4 = this.method1896(field7629);
            if (v4 == -1) {
                ChatUtil.method3308(field7630, new Object[0]);
                return true;
            }
            ItemStack v5 = this.var_net_minecraft_client_MinecraftClient_I.player.getInventory().getStack(v4);
            if (!v5.isEmpty()) {
                this.field7625.method666(v4);
            }
            return true;
        }
        if (this.cfr_renamed_4.size() > 5 && (Integer)(v3 = (BindSetting)this.cfr_renamed_4.get(5)).getValue() != -1 && (Integer)v3.getValue() == param1) {
            int v4 = this.method1896(field7631);
            if (v4 == -1) {
                ChatUtil.method3308(field7632, new Object[0]);
                return true;
            }
            ItemStack v5 = this.var_net_minecraft_client_MinecraftClient_I.player.getInventory().getStack(v4);
            if (!v5.isEmpty()) {
                this.field7625.method666(v4);
            }
            return true;
        }
        if (this.cfr_renamed_4.size() > 6 && (Integer)(v3 = (BindSetting)this.cfr_renamed_4.get(6)).getValue() != -1 && (Integer)v3.getValue() == param1) {
            int v4 = this.method1896(field7633);
            if (v4 == -1) {
                ChatUtil.method3308(field7634, new Object[0]);
                return true;
            }
            ItemStack v5 = this.var_net_minecraft_client_MinecraftClient_I.player.getInventory().getStack(v4);
            if (!v5.isEmpty()) {
                this.field7625.method666(v4);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean method1889() {
        return false;
    }

    @Override
    public void method1890() {
    }

    private String method1891(int param1) {
        if (this.cfr_renamed_4 == null || param1 < 0 || param1 >= this.cfr_renamed_4.size()) {
            return "";
        }
        int v2 = (Integer)((BindSetting)this.cfr_renamed_4.get(param1)).getValue();
        return v2 == -1 ? "" : KeyCode.getDisplayName(v2);
    }

    private String method1892(int param1, boolean param2) {
        if (param1 == -1) {
            return field7635;
        }
        if (param2 && param1 >= 9) {
            return field7636;
        }
        ItemStack v3 = this.var_net_minecraft_client_MinecraftClient_I.player.getInventory().getStack(param1);
        if (v3.isEmpty()) {
            return field7637;
        }
        if (this.var_net_minecraft_client_MinecraftClient_I.player.getItemCooldownManager().isCoolingDown(v3)) {
            float v4 = this.var_net_minecraft_client_MinecraftClient_I.player.getItemCooldownManager().getCooldownProgress(v3, var_net_minecraft_client_MinecraftClient_I.getRenderTickCounter().getLastDuration());
            return String.format(field7638, Float.valueOf(v4 * field7639));
        }
        return field7640;
    }

    @EventHandler(2)
    private void method1893(EventPacket param1) {
        OpenScreenS2CPacket v2;
        if (!this.var_java_util_function_BooleanSupplier_I.getAsBoolean()) {
            return;
        }
        Object v3 = param1.method2467();
        if (param1.method2466() && v3 instanceof OpenScreenS2CPacket) {
            v2 = (OpenScreenS2CPacket)v3;
            if (((Boolean)this.field7641.getValue()).booleanValue() && v2.getName().getString().contains(field7642) && this.var_net_minecraft_client_MinecraftClient_I.player != null && this.var_net_minecraft_client_MinecraftClient_I.player.age < 100) {
                this.var_net_minecraft_client_MinecraftClient_I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(this.var_net_minecraft_client_MinecraftClient_I.player.currentScreenHandler.syncId));
                param1.method2298(true);
            }
        }
        if (param1.method2465() && (v3 = param1.method2467()) instanceof ChatMessageC2SPacket) {
            ChatMessageC2SPacket v2_chat = (ChatMessageC2SPacket)v3;
            if (((Boolean)this.field7643.getValue()).booleanValue()) {
                v3 = v2_chat.chatMessage().toLowerCase(Locale.ROOT);
                for (String v5 : this.field7644) {
                    if (v5 == null || v5.startsWith("field") || !((String)v3).contains(v5.toLowerCase(Locale.ROOT))) continue;
                    param1.method2298(true);
                    ChatUtil.method3308(field7645, new Object[0]);
                    return;
                }
            }
        }
    }

    public ServerHelperReallyworld(List param1, BooleanSetting param2, BooleanSetting param3, BooleanSupplier param4, ServerHelper param5) {
        this.cfr_renamed_4 = param1;
        this.field7641 = param2;
        this.field7643 = param3;
        this.var_java_util_function_BooleanSupplier_I = param4;
        this.field7625 = param5;
    }

    @Override
    public List method1894(boolean param1) {
        ArrayList<CfrRenamed298> v2 = new ArrayList<CfrRenamed298>();
        if (var_net_minecraft_client_MinecraftClient_I == null || this.var_net_minecraft_client_MinecraftClient_I.player == null) {
            return v2;
        }
        int v3 = InventoryUtil.method3336(Items.FIREWORK_STAR);
        String v4 = v3 == -1 ? field7709 : field7710;
        v2.add(new CfrRenamed298(field7711, field7712, v4, Items.NETHER_STAR, this.method1891(0), -1));
        int v5 = this.method1896(field7713);
        v2.add(new CfrRenamed298(field7714, field7715, this.method1892(v5, param1), Items.SPLASH_POTION, this.method1891(1), ColorUtil.method1858(85, 255, 85)));
        v2.add(new CfrRenamed298(field7716, field7717, field7718, Items.PLAYER_HEAD, this.method1891(2), -1));
        int v6 = this.method1896(field7719);
        v2.add(new CfrRenamed298(field7720, field7721, this.method1892(v6, param1), Items.SPLASH_POTION, this.method1891(3), ColorUtil.method1858(255, 85, 85)));
        int v7 = this.method1896(field7722);
        v2.add(new CfrRenamed298(field7723, field7724, this.method1892(v7, param1), Items.SPLASH_POTION, this.method1891(4), ColorUtil.method1858(170, 85, 255)));
        int v8 = this.method1896(field7725);
        v2.add(new CfrRenamed298(field7726, field7727, this.method1892(v8, param1), Items.SPLASH_POTION, this.method1891(5), ColorUtil.method1858(85, 170, 255)));
        int v9 = this.method1896(field7728);
        v2.add(new CfrRenamed298(field7729, field7730, this.method1892(v9, param1), Items.SPLASH_POTION, this.method1891(6), ColorUtil.method1858(255, 170, 85)));
        return v2;
    }

    @Override
    public String method1895() {
        return field7731;
    }

    private int method1896(String param1) {
        if (var_net_minecraft_client_MinecraftClient_I == null || this.var_net_minecraft_client_MinecraftClient_I.player == null || param1 == null || param1.isEmpty()) {
            return -1;
        }
        String v2 = param1.toLowerCase();
        boolean v3 = v2.contains(field7732);
        String v4 = v3 ? field7733 : "";
        for (int v5 = 0; v5 < 41; ++v5) {
            ItemStack v6 = this.var_net_minecraft_client_MinecraftClient_I.player.getInventory().getStack(v5);
            if (v6.isEmpty()) continue;
            String v7 = v6.getName().getString();
            String v8 = v7.replaceAll(field7734, "").toLowerCase();
            if (v8.contains(v2) || v3 && v8.contains(v4)) {
                return v5;
            }
            LoreComponent v9 = (LoreComponent)v6.get(DataComponentTypes.LORE);
            if (v9 == null) continue;
            for (Text v11 : v9.lines()) {
                String v12 = v11.getString();
                String v13 = v12.replaceAll(field7735, "").toLowerCase();
                if (!v13.contains(v2) && (!v3 || !v13.contains(v4))) continue;
                return v5;
            }
        }
        return -1;
    }

    static {
        field7642 = "ꈁꀀꈂꌁꈂꀁ§0ꈃꄀ";
        И4 = ".*\u0001.*";
        field7645 = "В вашем сообщении было найдено запретное слово, отправка сообщения отменена!";
        field7731 = "Reallyworld";
        field7709 = "—";
        field7710 = "✓";
        field7711 = "Анти полет";
        field7712 = "AP";
        field7713 = "гринч";
        field7714 = "Зелье Гринча";
        field7715 = "G";
        field7716 = "AutoShift";
        field7717 = "H";
        field7718 = "✓";
        field7719 = "новогодний ужас";
        field7720 = "Новогодний ужас";
        field7721 = "НУ";
        field7722 = "эссенция кромешника";
        field7723 = "Эссенция кромешника";
        field7724 = "ЭК";
        field7725 = "снежок";
        field7726 = "Снежок";
        field7727 = "СН";
        field7728 = "ловушка";
        field7729 = "Ловушка";
        field7730 = "L";
        field7635 = "—";
        field7636 = "—";
        field7637 = "—";
        field7638 = "%.0fс";
        field7639 = 20.0f;
        field7640 = "✓";
        field7623 = "гринч";
        field7624 = "§cЗелье Гринча не найдено в инвентаре";
        field7626 = 150L;
        field7627 = "новогодний ужас";
        field7628 = "§cНовогодний ужас не найден в инвентаре";
        field7629 = "эссенция кромешника";
        field7630 = "§cЭссенция кромешника не найдена в инвентаре";
        field7631 = "снежок";
        field7632 = "§cЗелье 'Снежок' не найдено в инвентаре";
        field7633 = "ловушка";
        field7634 = "§cПредмет 'Ловушка' не найден в инвентаре";
        field7732 = "ловуш";
        field7733 = "ловушк";
        field7734 = "§[0-9a-fk-or]";
        field7735 = "§[0-9a-fk-or]";
    }
}




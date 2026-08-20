/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.Formatting
 *  sLM.4ZXYEo
 */
package minced.module.misc;
import minced.core.EventHandler;

import minced.core.BindSetting;
import minced.core.ChatUtil;
import minced.core.EventKey;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import sLM._4ZXYEo;

public class ClickFriend
extends Module {
    static String cfr_renamed_376;
    static String cfr_renamed_377;

    static String field2649 = "Click Friend";
    static String field2650 = "Упрощает добавление друзей, позволяя добавить их в список друзей одним кликом";
    static String field2651 = "Кнопка взаимодействия";
    BindSetting field2652 = new BindSetting(field2651);
    static MinecraftClient I = MinecraftClient.getInstance();

    public ClickFriend() {
        super(field2649, field2650, Combat.Miscellaneous);
        this.addSettings(this.field2652);
    }

    @EventHandler(2)
    public void method709(EventKey param1) {
        Entity v3;
        if (param1.method3595() == ((Integer)this.field2652.getValue()).intValue() && param1.method3599() && (v3 = ClickFriend.I.targetedEntity) instanceof PlayerEntity) {
            PlayerEntity v2 = (PlayerEntity)v3;
            if (Minced.getInstance().getFriendManager().cfr_renamed_4(v2.getGameProfile().getName())) {
                Minced.getInstance().getFriendManager().method1703(v2.getGameProfile().getName());
                ChatUtil.method3308(_4ZXYEo.Цл("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(Formatting.RESET), (String)v2.getName().getString()), new Object[0]);
            } else {
                Minced.getInstance().getFriendManager().method1707(v2.getGameProfile().getName());
                ChatUtil.method3308(_4ZXYEo.ЦС("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(Formatting.RESET), (String)v2.getName().getString()), new Object[0]);
            }
        }
    }

    static {
        cfr_renamed_376 = "\u0001\u0001 Удален из списка друзей!";
        cfr_renamed_377 = "\u0001\u0001 Добавлен в список друзей!";
    }
}




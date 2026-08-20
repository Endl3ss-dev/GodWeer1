/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.PotionContentsComponent
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item$TooltipContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.text.HoverEvent
 *  net.minecraft.text.HoverEvent$Action
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.text.TextColor
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.world.World
 *  sLM.4ZXYEo
 *  sg.ec.p11>
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Objects;
import minced.core.ColorUtil;
import minced.core.BooleanSetting;
import minced.core.EventEntityRemove;
import minced.core.NotificationManager;
import minced.core.ChatUtil;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import sLM._4ZXYEo;
import sg.ec.CfrRenamed1568;

public class PotionTracker
extends Module {
    static String нШх;
    static String нШЮ;
    static String нШЖ;

    static MinecraftClient I = MinecraftClient.getInstance();
    static float field6575;
    static String field6576;
    BooleanSetting field6577 = new BooleanSetting(field6588, false);
    static float field6578;
    static int field6579;
    static String field6580;
    static String field6581;
    static String field6582;
    static String field6583;
    static String field6584;
    static String field6585;
    static String field6586 = "Potion Tracker";
    static String field6587 = "Оповещает о наложенных эффектах";
    static String field6588 = "Игнорировать себя";
    static String field6589;
    static String field6590;
    static String field6591;

    private MutableText method1552(ItemStack param1, PotionContentsComponent param2) {
        ArrayList<Text> v3 = new ArrayList<Text>();
        v3.add(param1.getName().copy());
        float v4 = PotionTracker.I.world != null ? Item.TooltipContext.create((World)PotionTracker.I.world).getUpdateTickRate() : field6575;
        Objects.requireNonNull(v3);
        param2.buildTooltip(v3::add, 1.0f, v4);
        MutableText v5 = Text.empty();
        for (int v6 = 0; v6 < v3.size(); ++v6) {
            if (v6 > 0) {
                v5.append((Text)Text.literal((String)field6576));
            }
            v5.append((Text)v3.get(v6));
        }
        return v5;
    }

    @EventHandler(2)
    public void method1553(EventEntityRemove param1) {
        if (((Boolean)this.field6577.getValue()).booleanValue() && param1.method2415() instanceof PlayerEntity && param1.method2415().equals((Object)PotionTracker.I.player)) {
            return;
        }
        int v2 = this.method1554(param1);
        int v3 = ColorUtil.method1850(Color.RED.getRGB(), Color.GREEN.getRGB(), (float)v2 / field6578) & field6579;
        MutableText v4 = this.method1555(param1);
        PotionContentsComponent v5 = (PotionContentsComponent)param1.method2414().getOrDefault(DataComponentTypes.POTION_CONTENTS, (Object)PotionContentsComponent.DEFAULT);
        MutableText v6 = Text.empty().append((Text)param1.method2415().getName().copy().formatted(Formatting.GRAY)).append((Text)Text.literal((String)field6580).formatted(Formatting.GRAY)).append((Text)param1.method2414().getName().copy()).append((Text)Text.literal((String)field6581).formatted(Formatting.GRAY)).append((Text)Text.literal((String)((Object)_4ZXYEo.cfr_renamed_396("makeConcatWithConstants", new Object[]{}, (int)v2))).setStyle(Style.EMPTY.withColor(TextColor.fromRgb((int)v3)))).append((Text)Text.literal((String)field6582)).append((Text)v4).styled(param3 -> param3.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, this.method1552(param1.method2414(), v5))));
        ChatUtil.method3308(v6, new Object[0]);
        MutableText v7 = Text.empty().append((Text)param1.method2415().getName().copy().formatted(Formatting.WHITE)).append((Text)Text.literal((String)field6583).formatted(Formatting.WHITE)).append((Text)Text.literal((String)((Object)_4ZXYEo.cfr_renamed_397("makeConcatWithConstants", new Object[]{}, (int)v2))).setStyle(Style.EMPTY.withColor(TextColor.fromRgb((int)v3))));
        NotificationManager.method2453(Identifier.of((String)field6584, (String)field6585), (Text)v7, v5.getColor());
    }

    public PotionTracker() {
        super(field6586, field6587, Combat.Miscellaneous);
        this.addSettings(this.field6577);
    }

    private int method1554(EventEntityRemove param1) {
        int v2 = 0;
        int v3 = 0;
        for (StatusEffectInstance v5 : (Iterable<StatusEffectInstance>)(param1.method2413())) {
            v2 += (int)((double)v5.getDuration() * param1.method2416());
            v3 += v5.getDuration();
        }
        return v3 > 0 ? v2 * 100 / v3 : 100;
    }

    private MutableText method1555(EventEntityRemove param1) {
        MutableText v2 = Text.empty();
        for (StatusEffectInstance v4 : (Iterable<StatusEffectInstance>)(param1.method2413())) {
            int v5 = (int)((double)v4.getDuration() * param1.method2416()) / 20;
            String v6 = String.format(field6589, v5 / 60, v5 % 60);
            String v7 = CfrRenamed1568.Б((int)(v4.getAmplifier() + 1));
            if (!v2.getString().isEmpty()) {
                v2.append((Text)Text.literal((String)field6590));
            }
            v2.append((Text)Text.literal((String)field6591).formatted(Formatting.GRAY)).append((Text)Text.translatable((String)v4.getTranslationKey()).formatted(Formatting.RED)).append((Text)Text.literal((String)((Object)_4ZXYEo.cfr_renamed_398("makeConcatWithConstants", new Object[]{}, (String)v7, (String)v6))).formatted(Formatting.GRAY));
        }
        return v2;
    }

    static {
        field6578 = 100.0f;
        field6579 = 0xFFFFFF;
        field6580 = " получил эффекты из ";
        field6581 = "\n● Успешность ";
        нШх = "\u0001%";
        field6582 = "\n";
        field6583 = " получил эффекты ";
        нШЮ = "\u0001%";
        field6584 = "minecraft";
        field6585 = "textures/item/splash_potion.png";
        field6589 = "%d:%02d";
        field6590 = "\n";
        field6591 = "● ";
        нШЖ = " \u0001 \u0001";
        field6575 = 20.0f;
        field6576 = "\n";
    }
}




/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.entity.player.ItemCooldownManager
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.Registries
 *  net.minecraft.util.Identifier
 *  sLM.4ZXYEo
 *  sg.ec.К$2
 *  sg.ec.К$Ч
 */
package minced.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import minced.core.HudTheme;
import minced.core.ScissorUtil;
import minced.core.Render2DBatch;
import minced.core.HudComponent;
import minced.core.Animation;
import minced.core.EventRender2D;
import minced.core.Easings;
import minced.core.HudWindow;
import minced.core.Fonts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import sLM._4ZXYEo;
import sg.ec.k2;

public class DelaysHud
implements HudComponent {
    static String ъм;

    static MinecraftClient I = MinecraftClient.getInstance();
    static String field9636;
    static String field9637;
    static String field9638;
    static String field9639;
    static String field9640;
    static String field9641;
    static String field9642;
    static String field9643;
    static String field9644;
    static String field9645;
    static float field9646;
    static String field9647;
    static String field9648;
    Map field9649 = new HashMap();
    static long field9650;
    Animation field9651 = new Animation(Easings.field11952, 0.0f, field9650);
    static long field9652;
    Animation field9653 = new Animation(Easings.field11952, 0.0f, field9652);
    static long field9654;
    Animation field9655 = new Animation(Easings.field11952, 0.0f, field9654);
    HudWindow field9656;
    static float field9657;
    static float field9658;
    static String field9659;
    static float field9660;
    static float field9661;
    static String field9662;
    static String field9663;
    static float field9664;
    static float field9665;
    static String field9666;
    static float field9667;
    static float field9668;
    static float field9669;
    static float field9670;
    static float field9671;
    static float field9672;
    static float field9673;
    static String field9674;
    static String field9675;
    static String field9676;
    static String field9677;
    static float field9678;
    static float field9679;
    static float field9680;
    static float field9681;
    static float field9682;
    static float field9683;
    static float field9684;
    static float field9685;
    static float field9686;

    private Field method2829(Class param1, String ... param2) {
        for (String v6 : param2) {
            try {
                Field v7 = param1.getDeclaredField(v6);
                v7.setAccessible(true);
                return v7;
            }
            catch (NoSuchFieldException v8) {
                // Candidate name not present under this mapping; try the next one.
            }
        }
        return null;
    }

    private List method2830() {
        ArrayList<k2._2> v1 = new ArrayList<k2._2>();
        if (DelaysHud.I.player == null) {
            return v1;
        }
        ItemCooldownManager v2 = DelaysHud.I.player.getItemCooldownManager();
        Field v3 = this.method2829(v2.getClass(), field9636, field9637);
        if (v3 == null) {
            return v1;
        }
        Map v4;
        try {
            v4 = (Map)v3.get(v2);
        }
        catch (IllegalAccessException v18) {
            return v1;
        }
        int v5 = this.method2832(v2, field9638, field9639);
        if (v5 == -1) {
            return v1;
        }
        for (Map.Entry v7 : (Iterable<java.util.Map.Entry>)(v4.entrySet())) {
            Identifier v8 = (Identifier)v7.getKey();
            Item v9 = (Item)Registries.ITEM.get(v8);
            if (v9 == null) continue;
            ItemStack v10 = new ItemStack((ItemConvertible)v9);
            Object v11 = v7.getValue();
            int v12 = this.method2832(v11, field9640, field9641, field9642);
            int v13 = this.method2832(v11, field9643, field9644, field9645);
            if (v12 == -1 || v13 == -1) continue;
            int v14 = v13 - v12;
            int v15 = v13 - v5;
            if (v14 <= 0 || v15 <= 0) continue;
            String v16 = this.method2834((float)v15 / field9646);
            String v17 = v10.getName().getString().replace(field9647, "").replace(field9648, "");
            if (v16 == null || v16.isEmpty()) continue;
            v1.add(new k2._2(v9, v17, v16, v15));
        }
        v1.sort((param0, param1) -> {
            float v2_148 = Fonts.I[13].method2719(param0.name()) + Fonts.I[13].method2719(param0.timeStr());
            float v3_149 = Fonts.I[13].method2719(param1.name()) + Fonts.I[13].method2719(param1.timeStr());
            return Float.compare(v3_149, v2_148);
        });
        return v1;
    }

    private int method2832(Object param1, String ... param2) {
        for (String v6 : param2) {
            try {
                Field v7 = param1.getClass().getDeclaredField(v6);
                v7.setAccessible(true);
                return v7.getInt(param1);
            }
            catch (NoSuchFieldException | IllegalAccessException v8) {
                // Candidate name not present under this mapping; try the next one.
            }
        }
        return -1;
    }

    public DelaysHud(HudWindow param1) {
        this.field9656 = param1;
    }

    private String method2834(float param1) {
        int v2 = (int)(param1 / field9657);
        int v3 = (int)(param1 % field9658);
        return String.format(field9659, v2, v3);
    }

    @Override
    @EventHandler(2)
    public void method2818(EventRender2D param1) {
        DrawContext v2 = param1.method2997();
        List v3 = this.method2830();
        boolean v4 = v3.isEmpty();
        boolean v5 = DelaysHud.I.currentScreen instanceof ChatScreen;
        float v6 = this.field9656.method3457();
        float v7 = this.field9656.method3496();
        float v8 = field9660;
        float v9 = field9661;
        this.field9655.animate(!v4 || v5 ? 1.0f : 0.0f);
        float v10 = this.field9655.getAnimatedValue();
        if (v10 <= 0.0f) {
            this.field9656.method3459(0.0f);
            this.field9656.method3483(0.0f);
            return;
        }
        HudTheme.method1871(v6, v7, this.field9656.method3499(), v8 + this.field9653.getAnimatedValue(), v10, v9, v9, v9, v9);
        ScissorUtil.method2621(v6, v7, this.field9656.method3499(), v8 + this.field9653.getAnimatedValue());
        float v11 = Fonts.field12504[16].method2719(field9662);
        Fonts.I[14].method2711(v2, field9663, v6 + field9664, v7 + field9665, HudTheme.method1873(v10));
        Fonts.field12504[16].method2711(v2, field9666, v6 + this.field9656.method3499() - v11 - field9667, v7 + field9668, HudTheme.method1875(v10));
        float v12 = field9669;
        int v13 = 0;
        int v14 = 0;
        for (k2._2 v16 : (Iterable<k2._2>)(v3)) {
            Item v17 = v16.item();
            k2.ch v18 = (k2.ch)(this.field9649.computeIfAbsent(v17, param0 -> new k2.ch()));
            v18.p11.animate(1.0f);
            float v19 = v18.p11.getAnimatedValue();
            float v20 = (float)v14 * field9670;
            if (v18.O.getAnimatedValue() == 0.0f && v19 < field9671) {
                v18.O.setAnimatedValue(v20);
                v18.O.setValue(v20);
                v18.O.setTarget(v20);
                v18.O.setFinished(true);
            } else {
                v18.O.animate(v20);
            }
            String v21 = v16.name();
            String v22 = v16.timeStr();
            float v23 = Fonts.I[13].method2719(v21);
            float v24 = Fonts.I[13].method2719(v22);
            float v25 = v23 + v24 + field9672;
            v12 = Math.max(v12, v25);
            float v26 = v7 + v18.O.getAnimatedValue() + field9673;
            Identifier v27 = Registries.ITEM.getId((Item)v17);
            String v28 = v27.getPath();
            String v29 = field9674.equals(v28) ? field9675 : (field9676.equals(v28) ? field9677 : v28);
            Identifier v30 = Identifier.of((String)v27.getNamespace(), (String)((Object)_4ZXYEo.щ("makeConcatWithConstants", new Object[]{}, (String)v29)));
            v2.getMatrices().push();
            v2.getMatrices().translate(v6 + field9678, v26 + 1.0f, 0.0f);
            float iconScale = field9679 / 16.0f;
            v2.getMatrices().scale(iconScale, iconScale, 1.0f);
            v2.drawItem(v17.getDefaultStack(), 0, 0);
            v2.getMatrices().pop();
            Fonts.I[13].method2711(v2, v21, v6 + field9681, v26 + field9682, HudTheme.method1873(v19));
            Fonts.I[13].method2711(v2, v22, v6 + this.field9656.method3499() - v24 - field9683, v26 + field9684, HudTheme.method1875(v19));
            ++v13;
            ++v14;
        }
        this.field9649.keySet().removeIf(param12 -> !v3.stream().anyMatch(param1_233 -> ((k2._2)param1_233).item() == param12));
        this.field9651.animate(v12);
        this.field9653.animate(field9685 + (float)v13 * field9686);
        this.field9656.method3459(this.field9651.getAnimatedValue());
        this.field9656.method3483(v8 + (v4 ? 0.0f : this.field9653.getAnimatedValue()));
        ScissorUtil.method2620();
    }

    static {
        field9660 = 16.0f;
        field9661 = 5.0f;
        field9662 = "v";
        field9663 = "Cooldowns";
        field9664 = 5.0f;
        field9665 = 7.0f;
        field9666 = "v";
        field9667 = 4.5f;
        field9668 = 7.5f;
        field9669 = 75.0f;
        field9670 = 11.0f;
        field9671 = 0.1f;
        field9672 = 30.0f;
        field9673 = 18.0f;
        field9674 = "enchanted_golden_apple";
        field9675 = "golden_apple";
        field9676 = "crossbow";
        field9677 = "crossbow_arrow";
        ъм = "textures/item/\u0001.png";
        field9678 = 5.0f;
        field9679 = 8.0f;
        field9680 = 8.0f;
        field9681 = 16.0f;
        field9682 = 3.5f;
        field9683 = 4.5f;
        field9684 = 3.5f;
        field9685 = 4.0f;
        field9686 = 11.0f;
        field9636 = "cooldowns";
        field9637 = "entries";
        field9638 = "tickCount";
        field9639 = "tick";
        field9640 = "startTime";
        field9641 = "startTick";
        field9642 = "start";
        field9643 = "endTime";
        field9644 = "endTick";
        field9645 = "end";
        field9646 = 20.0f;
        field9647 = "[";
        field9648 = "]";
        field9657 = 60.0f;
        field9658 = 60.0f;
        field9659 = "%02d:%02d";
        field9650 = 100L;
        field9652 = 100L;
        field9654 = 200L;
    }
}





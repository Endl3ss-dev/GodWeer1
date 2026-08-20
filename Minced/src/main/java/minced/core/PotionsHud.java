/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  sLM.4ZXYEo
 */
package minced.core;

import java.util.HashMap;
import java.util.Map;
import minced.core.HudTheme;
import minced.core.BooleanSetting;
import minced.core.PotionEntry;
import minced.core.ScissorUtil;
import minced.core.Render2DBatch;
import minced.core.HudComponent;
import minced.core.Animation;
import minced.core.EventRender2D;
import minced.core.Easings;
import minced.core.HudWindow;
import minced.core.Fonts;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import sLM._4ZXYEo;

public class PotionsHud
implements HudComponent {
    static String cfr_renamed_245;

    static MinecraftClient I = MinecraftClient.getInstance();
    HudWindow field9714;
    static float field9715;
    static float field9716;
    Animation field9717;
    Animation field9718;
    static String field9719;
    static String field9720;
    static float field9721;
    static float field9722;
    static String field9723;
    static float field9724;
    static float field9725;
    static float field9726;
    Map field9727 = new HashMap();
    static float field9728;
    static float field9729;
    static float field9730;
    static float field9731;
    static float field9732;
    static float field9733;
    static float field9734;
    static float field9735;
    static float field9736;
    static float field9737;
    static float field9738;
    static float field9739;
    static float field9740;
    static float field9741;
    static float field9742;
    static float field9743;
    static float field9744;
    static float field9745;
    static float field9746;
    static float field9747;
    static float field9748;
    static float field9749;
    static float field9750;
    static float field9751;
    static float field9752;
    static float field9753;
    static float field9754;
    static float field9755;
    Animation field9756 = new Animation(Easings.field11952, 0.0f, field9765);
    static float field9757;
    static float field9758;
    static float field9759;
    static String field9760;
    static String field9761;
    static String field9762;
    static String field9763;
    static BooleanSetting field9764;
    static long field9765;
    static long field9766;
    static long field9767;

    @Override
    @EventHandler(2)
    public void method2818(EventRender2D param1) {
        if (PotionsHud.I.player == null) {
            this.field9714.method3459(0.0f);
            this.field9714.method3483(0.0f);
            return;
        }
        DrawContext v2 = param1.method2997();
        Map v3 = PotionsHud.I.player.getActiveStatusEffects();
        boolean v4 = v3.isEmpty();
        boolean v5 = PotionsHud.I.currentScreen instanceof ChatScreen;
        float v6 = this.field9714.method3457();
        float v7 = this.field9714.method3496();
        float v8 = this.field9714.method3499();
        float v9 = field9715;
        float v10 = field9716;
        this.field9717.animate(!v4 || v5 ? 1.0f : 0.0f);
        float v11 = this.field9717.getAnimatedValue();
        if (v11 <= 0.0f) {
            this.field9714.method3459(0.0f);
            this.field9714.method3483(0.0f);
            return;
        }
        HudTheme.method1871(v6, v7, v8, v9 + this.field9718.getAnimatedValue(), v11, v10, v10, v10, v10);
        ScissorUtil.method2621(v6, v7, v8, v9 + this.field9718.getAnimatedValue());
        float v12 = Fonts.field12504[16].method2719(field9719);
        Fonts.I[14].method2711(v2, field9720, v6 + field9721, v7 + field9722, HudTheme.method1873(v11));
        Fonts.field12504[16].method2711(v2, field9723, v6 + v8 - v12 - field9724, v7 + field9725, HudTheme.method1875(v11));
        int v13 = 0;
        float v14 = field9726;
        int v15 = 0;
        int v16 = 0;
        for (Map.Entry v18 : (Iterable<java.util.Map.Entry>)(v3.entrySet())) {
            boolean v22;
            RegistryEntry<StatusEffect> v19Entry = (RegistryEntry<StatusEffect>)v18.getKey();
            StatusEffect v19 = v19Entry.value();
            PotionEntry v20 = (PotionEntry)(this.field9727.computeIfAbsent(v19, param0 -> new PotionEntry()));
            StatusEffectInstance v21 = (StatusEffectInstance)v18.getValue();
            boolean bl = v22 = v21.getDuration() > 2;
            if (v22) {
                ++v13;
            }
            float v23 = v7 + v20.field7978.getAnimatedValue() - 2.0f;
            Text v24 = v19.getName();
            boolean v25 = v21.getAmplifier() != 0;
            String v26 = v25 ? Integer.toString(v21.getAmplifier() + 1) : "";
            String v27 = this.method2847(v21);
            float v28 = Fonts.I[13].method2719(v27);
            float v29 = Fonts.I[13].method2716(v24);
            float v30 = Fonts.I[13].method2719(v26);
            float v31 = v20.field7976.getAnimatedValue();
            float v32 = field9728 - (v15 == 0 ? 0.0f : (float)v15 * field9729) + (float)v16 * field9730;
            float v33 = field9731;
            if (v20.field7978.getAnimatedValue() == 0.0f && v20.field7976.getAnimatedValue() < field9732) {
                v20.field7978.setAnimatedValue(v32);
                v20.field7978.setValue(v32);
                v20.field7978.setTarget(v32);
                v20.field7978.setFinished(true);
            } else {
                v20.field7978.animate(v32);
            }
            v20.field7976.animate(v22 ? 1.0f : 0.0f);
            if (v20.field7976.getAnimatedValue() < field9733) {
                v20.field7978.setAnimatedValue(0.0f);
                v20.field7978.setValue(0.0f);
                v20.field7978.setTarget(0.0f);
                v20.field7978.setFinished(true);
            }
            if (v21.isDurationBelow(200) && !v21.isDurationBelow(5)) {
                int v34 = v21.getDuration();
                int v35 = 10 - v34 / 20;
                float v36 = MathHelper.clamp((float)((float)v34 / field9734 / field9735 * field9736), (float)0.0f, (float)field9737);
                float v37 = MathHelper.clamp((float)((float)v34 / field9738), (float)0.0f, (float)1.0f);
                float v38 = MathHelper.cos((float)((float)v34 * field9739 / field9740));
                float v39 = MathHelper.clamp((float)((float)v35 / field9741 * field9742), (float)0.0f, (float)field9743);
                float v40 = v36 + v38 * v39 * v37;
                v31 = Math.min(v40, v31);
            }
            if (((Boolean)field9764.getValue()).booleanValue()) {
                Sprite v34 = I.getStatusEffectSpriteManager().getSprite(v19Entry);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, v31);
                v2.drawSpriteStretched(RenderLayer::getGuiTextured, v34, Math.round(v6 + field9744), Math.round(v23 + field9745), Math.round(field9746), Math.round(field9747));
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
            Fonts.I[13].method2711(v2, v26, v6 + v33 + v29 + field9748, v23 + field9749, HudTheme.method1873(field9750 * v31));
            Fonts.I[13].method2711(v2, v24.getString(), v6 + v33 + field9751, v23 + field9752, HudTheme.method1873(v31));
            Fonts.I[13].method2711(v2, v27, v6 + v8 - v28 - field9753, v23 + field9754, HudTheme.method1875(v31));
            if (!v22) {
                ++v15;
            }
            v14 = Math.max(v14, v28 + v29 + v30 + v33 + field9755);
            ++v16;
        }
        boolean v17 = v5 && v4;
        this.field9756.animate(v17 ? field9757 : v14);
        this.field9718.animate(field9758 + (float)v13 * field9759);
        this.field9714.method3459(this.field9756.getAnimatedValue());
        this.field9714.method3483(v9 + (v4 ? 0.0f : this.field9718.getAnimatedValue()));
        ScissorUtil.method2620();
    }

    private Identifier method2846(StatusEffect param1) {
        Identifier v2 = Registries.STATUS_EFFECT.getId((StatusEffect)param1);
        if (v2 != null) {
            return Identifier.of((String)v2.getNamespace(), (String)((Object)_4ZXYEo.дф("makeConcatWithConstants", new Object[]{}, (String)v2.getPath())));
        }
        return Identifier.of((String)field9760, (String)field9761);
    }

    private String method2847(StatusEffectInstance param1) {
        int v2 = param1.getDuration();
        int v3 = v2 / 20;
        int v4 = v3 / 60;
        int v5 = v3 % 60;
        return String.format(field9762, v4, v5);
    }

    public static void method2848() {
        field9764 = new BooleanSetting(field9763, true);
    }

    public PotionsHud(HudWindow param1) {
        this.field9718 = new Animation(Easings.field11952, 0.0f, field9766);
        this.field9717 = new Animation(Easings.field11952, 0.0f, field9767);
        this.field9714 = param1;
    }

    static {
        field9715 = 16.0f;
        field9716 = 5.0f;
        field9719 = "u";
        field9720 = "Potions";
        field9721 = 5.0f;
        field9722 = 7.0f;
        field9723 = "u";
        field9724 = 4.5f;
        field9725 = 7.5f;
        field9726 = 75.0f;
        field9728 = 18.0f;
        field9729 = 10.0f;
        field9730 = 11.0f;
        field9731 = 10.0f;
        field9732 = 0.1f;
        field9733 = 0.01f;
        field9734 = 10.0f;
        field9735 = 5.0f;
        field9736 = 0.5f;
        field9737 = 0.5f;
        field9738 = 50.0f;
        field9739 = (float)Math.PI;
        field9740 = 5.0f;
        field9741 = 10.0f;
        field9742 = 0.25f;
        field9743 = 0.25f;
        field9744 = 5.0f;
        field9745 = 3.0f;
        field9746 = 8.0f;
        field9747 = 8.0f;
        field9748 = 7.5f;
        field9749 = 6.0f;
        field9750 = 0.7f;
        field9751 = 5.5f;
        field9752 = 6.0f;
        field9753 = 4.5f;
        field9754 = 6.0f;
        field9755 = 14.0f;
        field9757 = 75.0f;
        field9758 = 4.0f;
        field9759 = 11.0f;
        cfr_renamed_245 = "textures/mob_effect/\u0001.png";
        field9760 = "minecraft";
        field9761 = "textures/mob_effect/strength.png";
        field9762 = "%02d:%02d";
        field9765 = 100L;
        field9766 = 100L;
        field9767 = 200L;
        field9763 = "Icons";
        PotionsHud.method2848();
    }
}





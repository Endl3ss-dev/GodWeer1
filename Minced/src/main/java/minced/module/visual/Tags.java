/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.util.DefaultSkinHelper
 *  net.minecraft.component.type.ItemEnchantmentsComponent
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Vector2f
 *  org.joml.Vector4f
 *  sLM.4ZXYEo
 *  sg.ec.23$П
 *  sg.ec.p11>
 *  sg.ec.chp
 */
package minced.module.visual;
import minced.core.EventHandler;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import minced.core.ColorUtil;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.TagEntry;
import minced.core.ProjectionUtil;
import minced.core.Render2DBatch;
import minced.core.ServerDetector;
import minced.core.FontRenderer;
import minced.core.ServerItemTags;
import minced.core.EventRender2D;
import minced.core.EntityFilter;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.misc.ScoreboardHealth;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;
import org.joml.Vector4f;
import sLM._4ZXYEo;
import sg.ec.CfrRenamed1568;
import sg.ec._23;
import sg.ec.chp;
import net.minecraft.util.Rarity;

public class Tags
extends Module {
    static float н5;
    static float нн;
    static String 诶ю;
    static String 诶э;
    static String Цс;
    static String Цп;

    MultiBooleanSetting field3559 = new MultiBooleanSetting(field3617, new BooleanSetting(field3618, true), new BooleanSetting(field3619, false), new BooleanSetting(field3620, false), new BooleanSetting(field3621, false), new BooleanSetting(field3622, false), new BooleanSetting(field3623, false), new BooleanSetting(field3624, false), new BooleanSetting(field3625, true));
    static float field3560;
    static float field3561;
    static float field3562;
    static float field3563;
    static MinecraftClient I = MinecraftClient.getInstance();
    BooleanSetting field3564;
    static String field3565;
    static String field3566;
    static float field3567;
    static float field3568;
    static float field3569;
    static float field3570;
    static float field3571;
    static float field3572;
    static String field3573;
    static String field3574;
    static String field3575;
    static String field3576;
    static String field3577;
    static float field3578;
    static float field3579;
    BooleanSetting field3580;
    static float field3581;
    static float field3582;
    static float field3583;
    static float field3584;
    static float field3585;
    static float field3586;
    BooleanSetting field3587;
    static float field3588;
    BooleanSetting field3589;
    BooleanSetting field3590 = new BooleanSetting(field3626, false);
    static float field3591;
    static float field3592;
    static float field3593;
    static float field3594;
    static float field3595;
    static float field3596;
    static float field3597;
    static float field3598;
    static float field3599;
    static float field3600;
    static float field3601;
    static float field3602;
    static float field3603;
    static float field3604;
    static float field3605;
    static float field3606;
    BooleanSetting field3607;
    static float field3608;
    static float field3609;
    static float field3610;
    static float field3611;
    static float field3612;
    static int field3613;
    static float field3614;
    static String field3615;
    static String field3616;
    static String field3617;
    static String field3618;
    static String field3619;
    static String field3620;
    static String field3621;
    static String field3622;
    static String field3623;
    static String field3624;
    static String field3625;
    static String field3626;
    static String field3627;
    static String field3628;
    static String field3629;
    static String field3630;
    static String field3631;

    public boolean method977(Entity param1) {
        if (!this.isEnabled()) {
            return false;
        }
        if (param1 instanceof ItemEntity) {
            return EntityFilter.method3433(param1, this.field3559);
        }
        if (param1 instanceof LivingEntity) {
            LivingEntity v2 = (LivingEntity)param1;
            return this.method988(v2);
        }
        return false;
    }

    private Vector4f method978(Box param1) {
        Vec3d[] v2 = ProjectionUtil.method2000(param1);
        Vector2f v3 = new Vector2f(field3560, field3561);
        Vector2f v4 = new Vector2f(field3562, field3563);
        for (Vec3d v8 : v2) {
            Vector2f v9 = ProjectionUtil.method2005(v8);
            if (v9.x < v3.x) {
                v3.x = v9.x;
            }
            if (v9.y < v3.y) {
                v3.y = v9.y;
            }
            if (v9.x > v4.x) {
                v4.x = v9.x;
            }
            if (!(v9.y > v4.y)) continue;
            v4.y = v9.y;
        }
        return new Vector4f(v3.x, v3.y, v4.x, v4.y);
    }

    @EventHandler(2)
    public void method979(EventRender2D param1) {
        float v2 = I.getRenderTickCounter().getTickDelta(true);
        FontRenderer v3 = Fonts.I[14];
        int v4 = ColorUtil.method1852(0, 0, 0, 120);
        for (Entity v6 : Tags.I.world.getEntities()) {
            boolean v82;
            Vector4f v10;
            Box v9;
            ItemEntity v7;
            if (!ProjectionUtil.method2003(v6)) continue;
            if (EntityFilter.method3433(v6, this.field3559) && v6 instanceof ItemEntity) {
                v7 = (ItemEntity)v6;
                Vec3d v82_218 = v7.getLerpedPos(param1.method2996());
                v9 = ProjectionUtil.method2004((Entity)v7, v82_218);
                v10 = this.method978(v9);
                this.method980(param1, v7, v10, v3, v4);
                continue;
            }
            if (!(v6 instanceof LivingEntity)) continue;
            LivingEntity v12 = (LivingEntity)v6;
            if (!v12.isAlive() || !(v82 = this.method988(v12))) continue;
            Vec3d v13 = v12.getLerpedPos(v2);
            Box v14 = ProjectionUtil.method2004((Entity)v12, v13);
            Vector4f v11 = this.method978(v14);
            this.method981(param1, v12, v11, v3, v4);
        }
    }

    private void method980(EventRender2D param1, ItemEntity param2, Vector4f param3, FontRenderer param4, int param5) {
        Object v13;
        MutableText v12;
        ItemStack v6 = param2.getStack();
        _23.p11 v7 = ServerItemTags.method2873(v6);
        String v8 = v7 != null ? v7.Ц(v6) : ((Boolean)this.field3564.getValue() != false ? v6.getName() : v6.getItem().getName(v6)).getString().replace(field3565, "").replace(field3566, "");
        int v9 = v6.getCount();
        Object v10 = v9 > 1 ? _4ZXYEo.иш("makeConcatWithConstants", new Object[]{}, (int)v9) : "";
        int v11 = -1;
        if (v7 != null) {
            v11 = v7.Ц(v6);
        }
        if (v7 != null) {
            v12 = Text.literal((String)v8);
        } else {
            v13 = v6.getRarity();
            v12 = Text.empty().append((Text)Text.literal((String)v8)).formatted(((Rarity)v13).getFormatting());
        }
        if (v9 > 1) {
            v12.append((Text)Text.literal((String)v10));
        }
        v13 = _4ZXYEo.ик("makeConcatWithConstants", new Object[]{}, (String)v8, (String)v10);
        float v14 = param4.method2719((String)v13);
        float v15 = param4.method2718();
        float v16 = (param3.x + param3.z) / 2.0f;
        float v17 = param3.y - v15 - field3567;
        Render2DBatch.method2664(param1.method2997(), v16 - v14 / 2.0f - field3568, v17 - 2.0f, v14 + field3569, field3570, param5);
        param4.method2717(param1.method2997(), (Text)v12, v16 - v14 / 2.0f + field3571, v17 + field3572, v11);
    }

    private void method981(EventRender2D param1, LivingEntity param2, Vector4f param3, FontRenderer param4, int param5) {
        float v242;
        Text v6 = param2.getDisplayName();
        boolean v7 = param2 instanceof PlayerEntity && Minced.getInstance().getFriendManager().cfr_renamed_4(((PlayerEntity)param2).getGameProfile().getName());
        boolean v8 = Minced.getInstance().getModuleManager().method187(ScoreboardHealth.class).isEnabled();
        float v9 = v8 ? (float)ServerDetector.method2681(param2) : param2.getHealth();
        int v10 = Math.max(0, Math.round(v9));
        MutableText v11 = Text.literal((String)"").append(v6).append((Text)Text.literal((String)field3573).formatted(Formatting.GRAY)).append((Text)Text.literal((String)Integer.toString(v10)).formatted(Formatting.RED)).append((Text)Text.literal((String)field3574).formatted(Formatting.GRAY));
        if (v7) {
            v11.append((Text)Text.literal((String)field3575).formatted(Formatting.GRAY)).append((Text)Text.literal((String)field3576).formatted(Formatting.GREEN)).append((Text)Text.literal((String)field3577).formatted(Formatting.GRAY));
        }
        float v12 = param4.method2716((Text)v11);
        float v13 = param4.method2718();
        float v14 = (param3.x + param3.z) / 2.0f - field3578;
        float v15 = param3.y - v13 - field3579;
        boolean v16 = (Boolean)this.field3580.getValue() != false && param2 instanceof PlayerEntity;
        float v17 = v16 ? field3581 : 0.0f;
        float v18 = v17 + v12;
        float v19 = v14 - v18 / 2.0f - 1.0f;
        float v20 = v18 + field3582;
        float v21 = v19 + v17 + 2.0f;
        int v22 = v7 ? ColorUtil.method1852(0, 120, 0, 120) : param5;
        Render2DBatch.method2664(param1.method2997(), v19, v15 - 2.0f, v20 + 2.0f, field3583, v22);
        if (v16) {
            Identifier identifier;
            if (param2 instanceof AbstractClientPlayerEntity) {
                AbstractClientPlayerEntity v242_288 = (AbstractClientPlayerEntity)param2;
                identifier = v242_288.getSkinTextures().texture();
            } else {
                identifier = DefaultSkinHelper.getTexture();
            }
            Identifier v23 = identifier;
            v242 = v15 - 2.0f + 1.0f;
            RenderUtil.method389(v23, param2, v19 + 1.0f, v242, field3584, field3585, 0.0f, 1.0f);
        }
        param4.method2717(param1.method2997(), (Text)v11, v21 + field3586, v15 + 1.0f, -1);
        float v23 = v19 + v20 / 2.0f;
        if (((Boolean)this.field3587.getValue()).booleanValue()) {
            this.method984(param1, param2, v23, v15);
        }
        v242 = param3.w + field3588;
        if (((Boolean)this.field3589.getValue()).booleanValue()) {
            v242 = this.method983(param1, param2, v23, v242, v22);
        }
        if (((Boolean)this.field3590.getValue()).booleanValue()) {
            this.method987(param1, param2, v23, v242, Fonts.I[11]);
        }
    }

    private float method983(EventRender2D param1, LivingEntity param2, float param3, float param4, int param5) {
        float v11;
        ItemStack v9;
        float v6 = field3591;
        float v7 = param4;
        ItemStack v8 = param2.getMainHandStack();
        if (!v8.isEmpty()) {
            Text v12 = (Boolean)this.field3564.getValue() != false ? v8.getName() : v8.getItem().getName(v8);
            float v10 = Fonts.I[12].method2716(v12);
            v11 = param3 - v10 / 2.0f + field3592;
            Render2DBatch.method2664(param1.method2997(), v11 - field3593, v7 - field3594, v10 + field3595, field3596, param5);
            Fonts.I[12].method2717(param1.method2997(), v12, v11, v7, -1);
            v7 += v6;
        }
        if (!(v9 = param2.getOffHandStack()).isEmpty()) {
            Text v10 = (Boolean)this.field3564.getValue() != false ? v9.getName() : v9.getItem().getName(v9);
            v11 = Fonts.I[12].method2716(v10);
            float v12 = param3 - v11 / 2.0f + field3597;
            Render2DBatch.method2664(param1.method2997(), v12 - field3598, v7 - field3599, v11 + field3600, field3601, param5);
            Fonts.I[12].method2717(param1.method2997(), v10, v12, v7 + 1.0f, -1);
            v7 += v6;
        }
        return v7;
    }

    private void method984(EventRender2D param1, LivingEntity param2, float param3, float param4) {
        ArrayList v5 = new ArrayList();
        Iterable iterable = param2.getArmorItems();
        Objects.requireNonNull(v5);
        iterable.forEach(v5::add);
        ItemStack v6 = param2.getMainHandStack();
        ItemStack v7 = param2.getOffHandStack();
        float v8 = param4 - field3602;
        float v9 = 0.0f;
        for (int v10 = 3; v10 >= 0; --v10) {
            if (((ItemStack)v5.get(v10)).isEmpty()) continue;
            v9 += field3603;
        }
        if (!v6.isEmpty()) {
            v9 += field3604;
        }
        if (!v7.isEmpty()) {
            v9 += field3605;
        }
        float v10 = -v9 / 2.0f + field3606;
        for (int v11 = 0; v11 < 6; ++v11) {
            List v13;
            ItemStack v12;
            v12 = v11 == 0 ? v7 : (v11 == 5 ? v6 : (ItemStack)v5.get(4 - v11));
            if (v12.isEmpty()) continue;
            if (((Boolean)this.field3607.getValue()).booleanValue() && !(v13 = this.method986(v12)).isEmpty()) {
                float v14 = v8 - field3608;
                for (String v16 : (Iterable<String>)(v13)) {
                    String v17 = Formatting.strip((String)v16);
                    float v18 = Fonts.I[10].method2719(v16);
                    Fonts.I[10].method2711(param1.method2997(), v17, param3 + v10 - v18 / 2.0f + field3609, v14, -1);
                    v14 -= field3610;
                }
            }
            RenderUtil.method398(param1.method2997(), v12, param3 + v10, v8, field3611, -1, 1.0f);
            v10 += field3612;
        }
    }

    private List method986(ItemStack param1) {
        ArrayList<String> v2 = new ArrayList<String>();
        ItemEnchantmentsComponent v3 = param1.getEnchantments();
        for (Object2IntMap.Entry v5 : v3.getEnchantmentEntries()) {
            Enchantment v6 = (Enchantment)((RegistryEntry)v5.getKey()).value();
            int v7 = v5.getIntValue();
            String v8 = Enchantment.getName((RegistryEntry)((RegistryEntry)v5.getKey()), (int)v7).getString();
            String v9 = v8.length() >= 2 ? v8.substring(0, 2) : v8;
            Formatting v10 = Formatting.WHITE;
            if (v6.getMaxLevel() == 1 && v7 == 1) {
                v2.add(_4ZXYEo.иР("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(v10), (String)v9));
                continue;
            }
            v2.add(_4ZXYEo.иЮ("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(v10), (String)v9, (int)v7));
        }
        return v2;
    }

    private void method987(EventRender2D param1, LivingEntity param2, float param3, float param4, FontRenderer param5) {
        Collection v6 = param2.getStatusEffects();
        if (v6.isEmpty()) {
            return;
        }
        ArrayList<TagEntry> v7 = new ArrayList<TagEntry>(v6.size());
        for (StatusEffectInstance v9 : (Iterable<StatusEffectInstance>)(v6)) {
            int v10 = v9.getDuration();
            if (v10 <= 20) continue;
            String v11 = Registries.STATUS_EFFECT.getId((StatusEffect)((StatusEffect)v9.getEffectType().value())).getPath();
            Formatting v12 = chp.ц((String)v11);
            Integer v13 = v12.getColorValue();
            int v14 = v13 != null ? field3613 | v13 : -1;
            String v15 = Text.translatable((String)((StatusEffect)v9.getEffectType().value()).getTranslationKey()).getString();
            int v16 = v9.getAmplifier() + 1;
            StringBuilder v17 = new StringBuilder(v15);
            if (v16 > 1) {
                v17.append(' ').append((String)CfrRenamed1568.Б((int)v16));
            }
            v17.append(' ').append((String)chp.ц((int)v10));
            String v18 = v17.toString();
            float v19 = param5.method2719(v18);
            v7.add(new TagEntry(v18, v19, v14));
        }
        if (v7.size() > 1) {
            v7.sort(Comparator.comparingDouble(param0 -> param0.Шк));
        }
        float v8 = param5.method2718() + 2.0f;
        for (int v9 = 0; v9 < v7.size(); ++v9) {
            TagEntry v10 = (TagEntry)v7.get(v9);
            float v11 = param3 - v10.Шк / 2.0f + field3614;
            float v12 = param4 + (float)v9 * v8;
            param5.method2711(param1.method2997(), v10.н7, v11, v12, v10.Ш必);
        }
    }

    private boolean method988(LivingEntity param1) {
        return EntityFilter.method3432((Entity)param1, this.field3559) || EntityFilter.method3435((Entity)param1, this.field3559, true) || EntityFilter.method3436(param1, this.field3559) || EntityFilter.method3439(param1, this.field3559) || EntityFilter.method3437(param1, this.field3559);
    }

    public Tags() {
        super(field3615, field3616, Combat.Visuals);
        this.field3587 = new BooleanSetting(field3627, false);
        this.field3607 = new BooleanSetting(field3628, false);
        this.field3589 = new BooleanSetting(field3629, false);
        this.field3564 = new BooleanSetting(field3630, false, () -> (Boolean)this.field3589.getValue());
        this.field3580 = new BooleanSetting(field3631, true);
        this.addSettings(this.field3559, this.field3590, this.field3587, this.field3607, this.field3589, this.field3564, this.field3580);
    }

    static {
        н5 = 0.0f;
        нн = 0.0f;
        field3615 = "Tags";
        field3616 = "Отображает информацию о сущностях над ними (имя, здоровье, эффекты, броня)";
        field3617 = "Отображать";
        field3618 = "Игроков";
        field3619 = "Монстров";
        field3620 = "Друзей";
        field3621 = "Животных";
        field3622 = "Себя";
        field3623 = "Жителей";
        field3624 = "Голых";
        field3625 = "Предметы";
        field3626 = "Отображать зелья";
        field3627 = "Отображать броню";
        field3628 = "Отображать зачарования";
        field3629 = "Отображать предметы в руках";
        field3630 = "Отображать визуальное имя предмета";
        field3631 = "Голова игрока";
        field3560 = Float.MAX_VALUE;
        field3561 = Float.MAX_VALUE;
        field3562 = Float.MIN_VALUE;
        field3563 = Float.MIN_VALUE;
        field3565 = "[";
        field3566 = "]";
        诶ю = " x\u0001";
        诶э = "\u0001\u0001";
        field3567 = 3.0f;
        field3568 = 0.5f;
        field3569 = 3.5f;
        field3570 = 10.0f;
        field3571 = 1.5f;
        field3572 = 1.5f;
        field3573 = " [";
        field3574 = "]";
        field3575 = " [";
        field3576 = "F";
        field3577 = "]";
        field3578 = 3.0f;
        field3579 = 3.0f;
        field3581 = 8.0f;
        field3582 = 3.5f;
        field3583 = 10.0f;
        field3584 = 8.0f;
        field3585 = 8.0f;
        field3586 = 1.5f;
        field3588 = 6.0f;
        field3602 = 12.0f;
        field3603 = 9.5f;
        field3604 = 9.5f;
        field3605 = 9.5f;
        field3606 = 3.0f;
        field3608 = 4.0f;
        field3609 = 5.0f;
        field3610 = 6.0f;
        field3611 = 0.5f;
        field3612 = 10.5f;
        field3591 = 10.0f;
        field3592 = 3.0f;
        field3593 = 1.5f;
        field3594 = 4.0f;
        field3595 = 3.0f;
        field3596 = 10.0f;
        field3597 = 3.0f;
        field3598 = 1.5f;
        field3599 = 3.0f;
        field3600 = 3.0f;
        field3601 = 10.0f;
        Цс = "\u0001\u0001";
        Цп = "\u0001\u0001\u0001";
        field3613 = -16777216;
        field3614 = 3.0f;
    }
}




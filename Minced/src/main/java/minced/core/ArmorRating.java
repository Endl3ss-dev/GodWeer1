/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.AttributeModifiersComponent
 *  net.minecraft.component.type.AttributeModifiersComponent$Entry
 *  net.minecraft.component.type.ItemEnchantmentsComponent
 *  net.minecraft.enchantment.Enchantments
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.attribute.EntityAttribute
 *  net.minecraft.entity.attribute.EntityAttributeModifier
 *  net.minecraft.item.ArmorItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.entry.RegistryEntry
 */
package minced.core;

import java.util.function.ToDoubleFunction;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public class ArmorRating
implements ToDoubleFunction {
    public int Р;
    public ItemStack sh;
    public static final String cfr_renamed_149 = "armor";
    public static final String cfr_renamed_150 = "toughness";
    public static final String cfr_renamed_151 = "knockback_resistance";
    public static final float cfr_renamed_155 = 0.0f;

    @Override
    public double applyAsDouble(Object param1) {
        this.sh = (ItemStack)param1;
        return (double)this.cfr_renamed_4();
    }

    static float cfr_renamed_152;
    static float cfr_renamed_153;
    static float cfr_renamed_154;
    static float cfr_renamed_143;
    static float cfr_renamed_144;
    static float cfr_renamed_145;
    static float cfr_renamed_146;
    static float cfr_renamed_147;
    static float cfr_renamed_148;
    static float cfr_renamed_156;

    private float method2049() {
        ItemEnchantmentsComponent v2;
        float v1 = 0.0f;
        if (this.sh.contains(DataComponentTypes.ENCHANTMENTS) && (v2 = (ItemEnchantmentsComponent)this.sh.get(DataComponentTypes.ENCHANTMENTS)) != null) {
            for (RegistryEntry v4 : v2.getEnchantments()) {
                int v5 = v2.getLevel(v4);
                if (v4.matchesKey(Enchantments.PROTECTION)) {
                    v1 += (float)v5 * cfr_renamed_143;
                    continue;
                }
                if (v4.matchesKey(Enchantments.BLAST_PROTECTION)) {
                    v1 += (float)v5 * cfr_renamed_144;
                    continue;
                }
                if (v4.matchesKey(Enchantments.FIRE_PROTECTION)) {
                    v1 += (float)v5 * cfr_renamed_145;
                    continue;
                }
                if (v4.matchesKey(Enchantments.PROJECTILE_PROTECTION)) {
                    v1 += (float)v5 * cfr_renamed_146;
                    continue;
                }
                if (v4.matchesKey(Enchantments.THORNS)) {
                    v1 += (float)v5 * cfr_renamed_147;
                    continue;
                }
                if (!v4.matchesKey(Enchantments.UNBREAKING)) continue;
                v1 += (float)v5 * cfr_renamed_148;
            }
        }
        return v1;
    }

    public ArmorRating(int param1, ItemStack param2, EquipmentSlot param3) {
        this.Р = param1;
        this.sh = param2;
    }

    public float cfr_renamed_4() {
        AttributeModifiersComponent v102;
        if (this.sh.isEmpty() || !(this.sh.getItem() instanceof ArmorItem)) {
            return 0.0f;
        }
        float v1 = 0.0f;
        float v2 = 0.0f;
        if (this.sh.contains(DataComponentTypes.ATTRIBUTE_MODIFIERS) && (v102 = (AttributeModifiersComponent)this.sh.get(DataComponentTypes.ATTRIBUTE_MODIFIERS)) != null) {
            for (AttributeModifiersComponent.Entry v5 : v102.modifiers()) {
                RegistryEntry v6 = v5.attribute();
                String v7 = Registries.ATTRIBUTE.getId((EntityAttribute)v6.value()).toString();
                if (!v7.contains(cfr_renamed_149)) continue;
                EntityAttributeModifier v8 = v5.modifier();
                double v9 = v8.value();
                if (v7.contains(cfr_renamed_150)) {
                    v2 += (float)v9;
                    continue;
                }
                if (v7.contains(cfr_renamed_151)) continue;
                v1 += (float)v9;
            }
        }
        float v102_f = this.method2049();
        return v1 * cfr_renamed_152 + v102_f * cfr_renamed_153 + v2 * cfr_renamed_154;
    }

    public float method2050() {
        if (!this.sh.isDamageable()) {
            return cfr_renamed_155;
        }
        int v1 = this.sh.getMaxDamage();
        int v2 = this.sh.getDamage();
        return (float)(v1 - v2) / (float)v1 * cfr_renamed_156;
    }

    static {
        cfr_renamed_152 = 1.0f;
        cfr_renamed_153 = 0.25f;
        cfr_renamed_154 = 0.5f;
        cfr_renamed_143 = 0.25f;
        cfr_renamed_144 = 0.08f;
        cfr_renamed_145 = 0.08f;
        cfr_renamed_146 = 0.08f;
        cfr_renamed_147 = 0.05f;
        cfr_renamed_148 = 0.03f;
        cfr_renamed_156 = 100.0f;
    }
}




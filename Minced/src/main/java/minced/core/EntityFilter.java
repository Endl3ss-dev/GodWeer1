/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.entity.passive.AnimalEntity
 *  net.minecraft.entity.passive.VillagerEntity
 *  net.minecraft.entity.player.PlayerEntity
 */
package minced.core;

import minced.core.MultiBooleanSetting;
import minced.core.ClientUtil;
import minced.core.Minced;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class EntityFilter {
    static String field12043;
    static String field12044;
    static String field12045;
    static String field12046;
    static String field12047;
    static String field12048;
    static String field12049;
    static String field12050;
    static String field12051;

    public static boolean method3431(LivingEntity param0, MultiBooleanSetting param1) {
        return param0 instanceof MobEntity && param1.method1898(field12043) != false;
    }

    public static boolean method3432(Entity param0, MultiBooleanSetting param1) {
        return param0 == ClientUtil.I.player && param1.method1898(field12044) != false && !ClientUtil.I.options.getPerspective().isFirstPerson();
    }

    public static boolean method3433(Entity param0, MultiBooleanSetting param1) {
        return param0 instanceof ItemEntity && param1.method1898(field12045) != false;
    }

    public static boolean method3434(Entity param0, MultiBooleanSetting param1) {
        return EntityFilter.method3435(param0, param1, true);
    }

    public static boolean method3435(Entity param0, MultiBooleanSetting param1, boolean param2) {
        boolean v5;
        PlayerEntity v3;
        block6: {
            block5: {
                if (!(param0 instanceof PlayerEntity)) break block5;
                v3 = (PlayerEntity)param0;
                if (param0 != ClientUtil.I.player) break block6;
            }
            return false;
        }
        boolean v4 = Minced.getInstance().getFriendManager().cfr_renamed_4(v3.getGameProfile().getName());
        boolean bl = v5 = v3.getArmor() == 0;
        if (v4) {
            return param1.method1898(field12046);
        }
        if (param2 && v5) {
            return param1.method1898(field12047);
        }
        return param1.method1898(field12048);
    }

    public static boolean method3436(LivingEntity param0, MultiBooleanSetting param1) {
        return param0 instanceof VillagerEntity && param1.method1898(field12049) != false;
    }

    public static boolean method3437(LivingEntity param0, MultiBooleanSetting param1) {
        return param0 instanceof MobEntity && !(param0 instanceof AnimalEntity) && !(param0 instanceof VillagerEntity) && param1.method1898(field12050) != false;
    }

    public static boolean method3438(Entity param0, MultiBooleanSetting param1) {
        if (param0 instanceof LivingEntity) {
            LivingEntity v2 = (LivingEntity)param0;
            return EntityFilter.method3432(param0, param1) || EntityFilter.method3435(param0, param1, true) || EntityFilter.method3436(v2, param1) || EntityFilter.method3439(v2, param1) || EntityFilter.method3437(v2, param1);
        }
        return EntityFilter.method3433(param0, param1);
    }

    public static boolean method3439(LivingEntity param0, MultiBooleanSetting param1) {
        return param0 instanceof AnimalEntity && param1.method1898(field12051) != false;
    }

    static {
        field12046 = "Друзей";
        field12047 = "Голых";
        field12048 = "Игроков";
        field12049 = "Жителей";
        field12051 = "Животных";
        field12043 = "Мобов";
        field12050 = "Монстров";
        field12044 = "Себя";
        field12045 = "Предметы";
    }
}




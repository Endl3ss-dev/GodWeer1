/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.NbtComponent
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 */
package minced.module.movement;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.BindSetting;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.TimerUtil;
import minced.core.EventKey;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class Jesus
extends Module {
    static float Ш衣;
    static float ШВ;
    static float ШЗ;

    ModeSetting field5852 = new ModeSetting(field5874, field5875, field5876, field5877, field5878);
    static String field5853;
    BindSetting field5854;
    boolean field5855 = false;
    SliderSetting field5856;
    static float field5857;
    long field5858;
    static long field5859;
    int field5860 = -1;
    long field5861;
    boolean field5862 = false;
    static String field5863;
    static String field5864;
    static String field5865;
    static String field5866;
    static String field5867;
    static String field5868;
    static float field5869;
    static float field5870;
    static float field5871;
    static String field5872;
    static String field5873;
    static String field5874;
    static String field5875;
    static String field5876;
    static String field5877;
    static String field5878;
    static String field5879;
    static float field5880;
    static float field5881;
    static float field5882;
    static float field5883;
    SliderSetting field5884 = new SliderSetting(field5879, field5880, field5881, field5882, field5883, () -> this.field5852.isMode(field5867));
    static String field5885;
    static String field5886;
    static float field5887;
    static float field5888;
    static float field5889;
    SliderSetting field5890;
    static String field5891;
    static float field5892;
    static float field5893;
    static float field5894;
    static float field5895;
    static String field5896;
    static float field5897;
    static float field5898;
    static float field5899;
    SliderSetting field5900;
    static long field5901;
    static long field5902;
    static String field5903;
    static String field5904;
    static String field5905;
    static String field5906;
    static String field5907;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field5908;
    static double field5909;
    static double field5910;
    static float field5911;
    static float field5912;
    static float field5913;
    static long field5914;
    static String field5915;
    static String field5916;
    static String field5917;
    static float field5918;
    static float field5919;
    static String field5920;
    static String field5921;
    static String field5922;
    static float field5923;
    static float field5924;
    static String field5925;
    static String field5926;
    static String field5927;
    static float field5928;
    static float field5929;
    static String field5930;
    static String field5931;
    static String field5932;
    static float field5933;
    static float field5934;
    static float field5935;
    static float field5936;
    static float field5937;
    static float field5938;
    static float field5939;
    static double field5940;
    static double field5941;

    @EventHandler(2)
    public void method1400(EventKey param1) {
        if (!this.field5852.isMode(field5853)) {
            return;
        }
        if (param1.method3599()) {
            return;
        }
        if (param1.method3595() == ((Integer)this.field5854.getValue()).intValue() && !this.field5855) {
            long v2 = System.currentTimeMillis();
            float v4 = ((Float)this.field5856.getValue()).floatValue() * field5857;
            if (this.field5858 == field5859 || (float)(v2 - this.field5858) >= v4) {
                this.field5860 = param1.method3595();
                this.field5861 = v2;
                this.field5858 = v2;
                this.field5855 = true;
                this.field5862 = false;
            }
        }
    }

    private boolean method1401(ItemStack param1) {
        if (param1.getItem() != Items.PLAYER_HEAD || !param1.contains(DataComponentTypes.CUSTOM_DATA)) {
            return false;
        }
        String v2 = ((NbtComponent)param1.get(DataComponentTypes.CUSTOM_DATA)).copyNbt().toString();
        if (!v2.contains(field5863)) {
            return false;
        }
        String v3 = v2.split(field5864)[1];
        return v3.startsWith(field5865);
    }

    private float method1405(StatusEffectInstance param1) {
        if (param1 != null) {
            int v2 = param1.getAmplifier();
            if (v2 >= 3) {
                return field5869;
            }
            if (v2 == 2) {
                return field5870;
            }
        }
        return field5871;
    }

    public Jesus() {
        super(field5872, field5873, Combat.Movement);
        this.field5854 = new BindSetting(field5885, () -> this.field5852.isMode(field5868));
        this.field5890 = new SliderSetting(field5886, 1.0f, field5887, field5888, field5889, () -> this.field5852.isMode(field5907));
        this.field5856 = new SliderSetting(field5891, field5892, field5893, field5894, field5895, () -> this.field5852.isMode(field5866));
        this.field5900 = new SliderSetting(field5896, field5897, field5898, 1.0f, field5899, () -> this.field5852.isMode(field5906));
        this.field5861 = field5901;
        this.field5858 = field5902;
        this.addSettings(this.field5852, this.field5854, this.field5884, this.field5890, this.field5856, this.field5900);
    }

    private boolean method1406(ItemStack param1) {
        if (param1.getItem() != Items.PLAYER_HEAD || !param1.contains(DataComponentTypes.CUSTOM_DATA)) {
            return false;
        }
        String v2 = ((NbtComponent)param1.get(DataComponentTypes.CUSTOM_DATA)).copyNbt().toString();
        if (!v2.contains(field5903)) {
            return false;
        }
        String v3 = v2.split(field5904)[1];
        return v3.startsWith(field5905);
    }

    private boolean method1409() {
        ItemStack v1 = Jesus.I.player.getEquippedStack(EquipmentSlot.HEAD);
        return this.method1406(v1) || this.method1401(v1);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     */
    @EventHandler(2)
    public void method1410(EventUpdate param1) {
        if (Jesus.I.player == null) {
            return;
        }
        if (this.field5852.isMode(Jesus.field5908)) {
            if (!Jesus.I.player.isTouchingWater() && !Jesus.I.player.isInLava() || Jesus.I.player.isTouchingWater()) {
                return;
            }
            double jumpVelocity = Jesus.I.options.jumpKey.isPressed() != false ? Jesus.field5909 : Jesus.field5910;
            Jesus.I.options.jumpKey.setPressed(true);
            Jesus.I.player.setVelocity(Jesus.I.player.getVelocity().x, jumpVelocity, Jesus.I.player.getVelocity().z);
            return;
        }
        if (!Jesus.I.player.isTouchingWater() && !Jesus.I.player.isInLava()) {
            return;
        }
        StatusEffectInstance speed = Jesus.I.player.getStatusEffect(StatusEffects.SPEED);
        StatusEffectInstance slowness = Jesus.I.player.getStatusEffect(StatusEffects.SLOWNESS);
        ItemStack offHandStack = Jesus.I.player.getOffHandStack();
        String offHandName = offHandStack.getName().getString();
        float boostTimeout = ((Float)this.field5890.getValue()).floatValue() * Jesus.field5912;
        if (this.field5855 && (float)(System.currentTimeMillis() - this.field5861) > boostTimeout) {
            this.field5855 = false;
            this.field5860 = -1;
        }
        float delay = ((Float)this.field5856.getValue()).floatValue() * Jesus.field5913;
        if (!this.field5862 && this.field5858 != Jesus.field5914 && (float)(System.currentTimeMillis() - this.field5858) >= delay) {
            this.field5862 = true;
        }
        String mode = (String)this.field5852.getValue();
        float speedValue;
        if (mode.equals(Jesus.field5915)) {
            speedValue = this.method1405(speed);
            if (offHandName.contains(Jesus.field5917) && speed != null && speed.getAmplifier() == 2) {
                speedValue = Jesus.field5918;
            } else if (speed != null && speed.getAmplifier() == 2) {
                speedValue = Jesus.field5919;
                if (offHandName.contains(Jesus.field5920) || offHandName.contains(Jesus.field5921) || offHandName.contains(Jesus.field5922)) {
                    speedValue = Jesus.field5923;
                }
            } else if (speed != null && speed.getAmplifier() == 1) {
                speedValue = Jesus.field5924;
                if (offHandName.contains(Jesus.field5925) || offHandName.contains(Jesus.field5926) || offHandName.contains(Jesus.field5927)) {
                    speedValue = Jesus.field5928;
                }
            } else if (speed == null) {
                speedValue = Jesus.field5929;
                if (offHandName.contains(Jesus.field5930) || offHandName.contains(Jesus.field5931) || offHandName.contains(Jesus.field5932)) {
                    speedValue = Jesus.field5933;
                }
            }
            if (slowness != null) {
                speedValue *= Jesus.field5934;
            }
            if (this.method1409()) {
                speedValue /= Jesus.field5935;
            }
            if (this.field5855 && this.field5860 == (Integer)this.field5854.getValue()) {
                float boost = ((Float)this.field5900.getValue()).floatValue();
                if (this.method1409()) {
                    boost /= Jesus.field5936;
                }
                speedValue += boost;
            }
        } else if (mode.equals(Jesus.field5916)) {
            speedValue = ((Float)this.field5884.getValue()).floatValue();
            if (slowness != null) {
                speedValue *= Jesus.field5937;
            }
            if (this.method1409()) {
                speedValue /= Jesus.field5938;
            }
        } else {
            speedValue = Jesus.field5939;
        }
        boolean moving = Jesus.I.options.forwardKey.isPressed() != false || Jesus.I.options.backKey.isPressed() != false || Jesus.I.options.leftKey.isPressed() != false || Jesus.I.options.rightKey.isPressed() != false;
        if (!moving) {
            Jesus.I.player.setVelocity(0.0, Jesus.I.player.getVelocity().y, 0.0);
        }
        double verticalVelocity = Jesus.I.options.jumpKey.isPressed() != false ? Jesus.field5940 : Jesus.field5941;
        Jesus.I.player.setVelocity(Jesus.I.player.getVelocity().x, verticalVelocity, Jesus.I.player.getVelocity().z);
        TimerUtil.method3546(speedValue);
    }

    static {
        Ш衣 = 0.0f;
        ШВ = 0.0f;
        ШЗ = 0.0f;
        field5872 = "Jesus";
        field5873 = "Позволяет ходить по поверхности воды как Иисус Христос";
        field5874 = "Мод";
        field5875 = "MetaHvH";
        field5876 = "MetaHvH";
        field5877 = "Custom";
        field5878 = "ReallyWorld Jump";
        field5879 = "Скорость";
        field5880 = 0.2f;
        field5881 = 0.2f;
        field5882 = 1.05f;
        field5883 = 0.01f;
        field5885 = "Бинд буста";
        field5886 = "Время буста (сек)";
        field5887 = 0.5f;
        field5888 = 10.0f;
        field5889 = 0.01f;
        field5891 = "Кулдаун буста (сек)";
        field5892 = 12.0f;
        field5893 = 3.0f;
        field5894 = 30.0f;
        field5895 = 0.5f;
        field5896 = "Скорость буста";
        field5897 = 0.4f;
        field5898 = 0.1f;
        field5899 = 0.01f;
        field5901 = -1L;
        field5902 = -1L;
        field5853 = "MetaHvH";
        field5857 = 1000.0f;
        field5859 = -1L;
        field5908 = "ReallyWorld Jump";
        field5909 = 0.019;
        field5910 = 0.003;
        field5911 = 0.65f;
        field5912 = 1000.0f;
        field5913 = 1000.0f;
        field5914 = -1L;
        field5915 = "MetaHvH";
        field5916 = "Custom";
        field5917 = "Ломтик Дыни";
        field5918 = 0.492775f;
        field5919 = 0.489325f;
        field5920 = "Шар Аида 2";
        field5921 = "Талисман Венома";
        field5922 = "Шар Геракла 2";
        field5923 = 0.431f;
        field5924 = 0.4255f;
        field5925 = "Шар Аида 2";
        field5926 = "Талисман Венома";
        field5927 = "Шар Геракла 2";
        field5928 = 0.5f;
        field5929 = 0.28934002f;
        field5930 = "Шар Аида 2";
        field5931 = "Талисман Венома";
        field5932 = "Шар Геракла 2";
        field5933 = 0.365f;
        field5934 = 0.85f;
        field5935 = 1.2f;
        field5936 = 1.35f;
        field5937 = 0.85f;
        field5938 = 1.35f;
        field5939 = 0.65f;
        field5940 = 0.019;
        field5941 = 0.003;
        field5869 = 0.8f;
        field5870 = 0.65f;
        field5871 = 0.65f;
        field5903 = "AttributeModifiers";
        field5904 = "AttributeModifiers";
        field5905 = ":[{Amount:3.0d,Slot:\"head\",AttributeName:\"minecraft:generic.armor\",Operation:0,UUID:[I;427683850,761809167,-1124274585,-962634053]";
        field5863 = "AttributeModifiers";
        field5864 = "AttributeModifiers";
        field5865 = ":[{Amount:3.5d,Slot:\"head\",AttributeName:\"minecraft:generic.armor\",Operation:0,UUID:[I;-368453572,-1112977890,-1779712266,-159547550],Name:\"GENERIC_ARMOR\"},{Amount:2.5d,Slot:\"head\",AttributeName:\"minecraft:generic.armor_toughness\",Operation:0,UUID:[I;-177222828,1392200633,-1721737065,1359173546]";
        field5906 = "MetaHvH";
        field5866 = "MetaHvH";
        field5907 = "MetaHvH";
        field5868 = "MetaHvH";
        field5867 = "Custom";
    }
}




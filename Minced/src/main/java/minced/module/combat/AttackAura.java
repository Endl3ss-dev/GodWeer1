/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.AttributeModifiersComponent
 *  net.minecraft.component.type.AttributeModifiersComponent$Entry
 *  net.minecraft.component.type.ItemEnchantmentsComponent
 *  net.minecraft.enchantment.Enchantments
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.entity.decoration.ArmorStandEntity
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ArmorItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket$Action
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.Hand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package minced.module.combat;

import minced.module.movement.Speed;
import minced.core.EventHandler;
import java.util.ArrayList;
import java.util.Comparator;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
import minced.core.EventClientTick;
import minced.core.ColorUtil;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventFrame;
import minced.core.AimRotation;
import minced.core.Rotation;
import minced.core.EventInput;
import minced.core.RotationTargetUtil;
import minced.core.ModeSetting;
import minced.core.NeuroSampler;
import minced.core.NeuroProfileStorage;
import minced.core.EventUpdate;
import minced.core.RotationManager;
import minced.core.EventPostUpdate;
import minced.core.TpsUtil;
import minced.core.EventRender2D;
import minced.core.AuraTargetBuilder;
import minced.core.AuraModePolar;
import minced.core.AuraItemUtil;
import minced.core.ClientState;
import minced.core.WeaponUtil;
import minced.core.ThemeColors;
import minced.core.InventoryUtil;
import minced.core.TimerUtil;
import minced.core.Fonts;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.MAIN;
import minced.mixin.accessor.IClientPlayerEntityMixin;
import minced.module.combat.AntiBot;
import minced.module.movement.ElytraResolver;
import minced.module.movement.ElytraTarget;
import minced.module.combat.PacketCriticals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AttackAura
extends Module {
    int field2731 = 0;
    double field2732 = 0.0;
    ModeSetting field2733 = new ModeSetting(field2849, field2850, field2851, field2852, field2853, field2854, field2855, field2856, field2857);
    static String field2734;
    ModeSetting field2735;
    static String field2736;
    static String field2737;
    BooleanSetting field2738;
    SliderSetting field2739;
    WeaponUtil field2740;
    static String field2741;
    static String field2742;
    AuraModePolar field2743;
    static double field2744;
    static String field2745;
    static String field2746;
    static String field2747;
    MultiBooleanSetting var_minced_core_ClsClass270_I;
    static String field2748;
    static String field2749;
    static String field2750;
    SliderSetting field2751;
    static String field2752;
    ClientState field2753;
    static String field2754;
    static String field2755;
    static MinecraftClient var_net_minecraft_client_MinecraftClient_I = MinecraftClient.getInstance();
    BooleanSetting field2756;
    BooleanSetting field2757;
    static LivingEntity field2758;
    MultiBooleanSetting field2759;
    static String field2760;
    boolean var_boolean_l;
    static String field2761;
    int field2762;
    BooleanSetting field2763;
    static String field2764;
    BooleanSetting field2765;
    static long field2766;
    static long field2767;
    long field2768 = 0L;
    static float field2769;
    static float field2770;
    float field2771;
    long field2772 = 0L;
    static long field2773;
    static String field2774;
    static float field2775;
    static float field2776;
    float field2777;
    static String field2778;
    static String field2779;
    AuraTargetBuilder field2780;
    static String field2781;
    static String field2782;
    SliderSetting field2783;
    static String field2784;
    SliderSetting field2785;
    double field2786 = 0.0;
    static String field2787;
    static String field2788;
    static String field2789;
    static String field2790;
    SliderSetting field2791;
    SliderSetting field2792;
    static String field2793;
    static String field2794;
    SliderSetting field2795;
    static double field2796;
    static double field2797;
    static double field2798;
    static double field2799;
    static double field2800;
    static double field2801;
    static float field2802;
    static double field2803;
    static double field2804;
    SliderSetting field2805;
    BooleanSetting var_minced_core_ClsClass271_l;
    static String field2806;
    static String field2807;
    BooleanSetting field2808;
    ModeSetting field2809 = new ModeSetting(field2858, field2859, field2860, field2861, field2862, field2863);
    static String field2810;
    static String field2811;
    static String field2812;
    static String field2813;
    boolean field2814;
    SliderSetting field2815;
    static String field2816;
    static String field2817;
    static String field2818;
    static String field2819;
    static String field2820;
    static String field2821;
    SliderSetting field2822;
    BooleanSetting var_minced_core_ClsClass271_O;
    SliderSetting field2823;
    SliderSetting field2824;
    SliderSetting field2825;
    static String field2826;
    static String field2827;
    static String field2828;
    static String field2829;
    SliderSetting field2830;
    static String field2831;
    static String field2832;
    static String field2833;
    SliderSetting field2834;
    ModeSetting field2835;
    SliderSetting field2836;
    static String field2837;
    static String field2838;
    static String field2839;
    static String field2840;
    static String field2841;
    static String field2842;
    static String field2843;
    SliderSetting field2844;
    static String field2845;
    static String field2846;
    static String field2847;
    static String field2848;
    static String field2849;
    static String field2850 = "Builder";
    static String field2851 = "Builder";
    static String field2852 = "Neuro";
    static String field2853 = "Polar";
    static String field2854 = "Legit";
    static String field2855 = "ReallyWorld";
    static String field2856 = "Spooky Test";
    static String field2857 = "Matrix";
    static String field2858;
    static String field2859;
    static String field2860 = "Оптимальный";
    static String field2861;
    static String field2862;
    static String field2863;
    static String field2864;
    static String field2865;
    static String field2866;
    static String field2867;
    static String field2868;
    static String field2869;
    static String field2870;
    static String field2871;
    static String field2872;
    static String field2873;
    static String field2874;
    static String field2875;
    static String field2876;
    ModeSetting field2877;
    static String field2878;
    static String field2879;
    static String field2880;
    static String field2881;
    static String field2882;
    static String field2883;
    static String field2884;
    static String field2885;
    static String field2886;
    static String field2887;
    static float field2888;
    static float field2889;
    static float field2890;
    static String field2891;
    static float field2892;
    static float field2893;
    static float field2894;
    SliderSetting field2895;
    static String field2896;
    static float field2897;
    static float field2898;
    static float field2899;
    static String field2900;
    static String field2901;
    static String field2902;
    BooleanSetting field2903;
    static String field2904;
    static String field2905;
    static String field2906;
    static String field2907;
    static String field2908;
    static String field2909;
    static String field2910;
    static String field2911;
    static String field2912;
    static String field2913;
    static String field2914;
    MultiBooleanSetting field2915;
    static String field2916;
    BooleanSetting field2917;
    static String field2918;
    static float field2919;
    static float field2920;
    static float field2921;
    static float field2922;
    SliderSetting field2923;
    static String field2924;
    BooleanSetting field2925;
    static String field2926;
    BooleanSetting field2927;
    static String field2928;
    BooleanSetting field2929;
    static String field2930;
    static String field2931;
    static String field2932;
    static String field2933;
    static String field2934;
    static String field2935;
    static float field2936;
    static float field2937;
    static float field2938;
    SliderSetting field2939;
    static String field2940;
    static float field2941;
    static float field2942;
    SliderSetting field2943;
    static String field2944;
    static float field2945;
    static float field2946;
    static String field2947;
    static float field2948;
    static float field2949;
    static String field2950;
    static float field2951;
    static float field2952;
    static float field2953;
    static float field2954;
    static String field2955;
    static float field2956;
    static float field2957;
    static float field2958;
    static float field2959;
    SliderSetting field2960;
    static String field2961;
    static float field2962;
    static float field2963;
    static float field2964;
    SliderSetting field2965;
    static String field2966;
    static float field2967;
    static float field2968;
    static float field2969;
    SliderSetting field2970;
    static String field2971;
    static float field2972;
    static float field2973;
    static String field2974;
    static float field2975;
    static float field2976;
    SliderSetting var_minced_core_ClsClass276_O;
    static String field2977;
    static float field2978;
    static float field2979;
    static float field2980;
    static float field2981;
    static String field2982;
    static float O5;
    static float field2983;
    static float field2984;
    static float field2985;
    SliderSetting field2986;
    static String OI;
    static float O2;
    static float field2987;
    static float field2988;
    static float field2989;
    static String field2990;
    static float field2991;
    static float field2992;
    static float field2993;
    static float O7;
    static String field2994;
    static float field2995;
    static float field2996;
    static float field2997;
    SliderSetting field2998;
    static String field2999;
    static float field3000;
    static float field3001;
    SliderSetting field3002;
    static String field3003;
    static float field3004;
    static float field3005;
    SliderSetting field3006;
    static String field3007;
    static float field3008;
    static float field3009;
    static float field3010;
    SliderSetting field3011;
    static String field3012;
    static float field3013;
    static float field3014;
    static String field3015;
    static float field3016;
    static float OO;
    SliderSetting field3017;
    static String field3018;
    static float field3019;
    static float field3020;
    SliderSetting field3021;
    static String field3022;
    static float field3023;
    static float field3024;
    static float field3025;
    static float field3026;
    SliderSetting var_minced_core_ClsClass276_l;
    static String field3027;
    static float field3028;
    static float field3029;
    static float Ol;
    static float field3030;
    SliderSetting field3031;
    static String field3032;
    static float field3033;
    SliderSetting field3034;
    static String field3035;
    static float field3036;
    static String field3037;
    BooleanSetting field3038;
    static String field3039;
    BooleanSetting field3040;
    static String field3041;
    static float field3042;
    static float O4;
    static float field3043;
    static float field3044;
    SliderSetting field3045;
    static String field3046;
    static float O1;
    static float O0;
    SliderSetting field3047;
    static String cfr_renamed_364;
    static float field3048;
    static float field3049;
    SliderSetting field3050;
    static String field3051;
    static float field3052;
    static float O3;
    SliderSetting field3053;
    static String field3054;
    static float field3055;
    static float field3056;
    static float field3057;
    SliderSetting field3058;
    static String field3059;
    static float field3060;
    static float field3061;
    static float field3062;
    SliderSetting field3063;
    static String O6;
    static float O9;
    static float O_;
    static float cfr_renamed_365;
    SliderSetting field3064;
    static String field3065;
    static float O8;
    static float field3066;
    static float field3067;
    SliderSetting field3068;
    static String field3069;
    static float field3070;
    static float field3071;
    SliderSetting field3072;
    static String field3073;
    static float field3074;
    static float field3075;
    static float field3076;
    static float field3077;
    SliderSetting field3078;
    static String field3079;
    static float field3080;
    SliderSetting field3081;
    static String field3082;
    BooleanSetting field3083;
    static String field3084;
    BooleanSetting field3085;
    static String field3086;
    static float field3087;
    static float field3088;
    static float field3089;
    SliderSetting field3090;
    static String field3091;
    static float field3092;
    static float field3093;
    static float field3094;
    SliderSetting cfr_renamed_4;
    static String field3095;
    static float field3096;
    static float field3097;
    static float field3098;
    static float field3099;
    SliderSetting field3100;
    static String field3101;
    static float field3102;
    static float field3103;
    static float field3104;
    static float field3105;
    SliderSetting field3106;
    static String field3107;
    static float field3108;
    static float field3109;
    static float field3110;
    static float field3111;
    static String field3112;
    static float field3113;
    static float field3114;
    static float field3115;
    static float field3116;
    SliderSetting field3117;
    static String field3118;
    static float field3119;
    static float field3120;
    static float field3121;
    SliderSetting field3122;
    static String field3123;
    static float field3124;
    static float field3125;
    SliderSetting field3126;
    static String field3127;
    static float field3128;
    static float field3129;
    static float field3130;
    static float field3131;
    static String field3132;
    static float field3133;
    static float field3134;
    static String field3135;
    static float field3136;
    static float field3137;
    static String field3138;
    static float field3139;
    static float field3140;
    static float field3141;
    static String field3142;
    static float field3143;
    static String field3144;
    static float field3145;
    SliderSetting field3146;
    static String field3147;
    BooleanSetting field3148;
    static float field3149;
    static float field3150;
    static String field3151;
    static String field3152;
    static String field3153;
    static String field3154;
    static String field3155;
    static String field3156;
    static String field3157;
    static String field3158;
    static String field3159;
    static double field3160;
    static double field3161;
    static String field3162;
    static String field3163;
    static String field3164;
    static String field3165;
    static String field3166;
    static String field3167;
    static String field3168;
    static String field3169;
    static String field3170;
    static String field3171;
    static String field3172;
    static String field3173;
    static String field3174;
    static String field3175;
    static String field3176;
    static String field3177;
    static String field3178;
    static String field3179;
    static String field3180;
    static String field3181;
    static double field3182;
    static double field3183;
    static float field3184;
    static String field3185;
    static String field3186;
    static String field3187;
    static String field3188;
    static String field3189;
    static String field3190;
    static String field3191;
    static double field3192;
    static String field3193;
    static float field3194;
    static long field3195;
    static long field3196;
    static String field3197;
    static String field3198;
    static String field3199;
    static String field3200;
    static String field3201;
    static String field3202;
    static String field3203;
    static String field3204;
    static double field3205;
    static String field3206;
    static String field3207;
    static String field3208;
    static String field3209;
    static String field3210;
    static String field3211;
    static String field3212;
    static String field3213;
    static String field3214;
    static String field3215;
    static String field3216;
    static String field3217;
    static String field3218;
    static String field3219;
    static String field3220;
    static String field3221;
    static String field3222;
    static String field3223;
    static String field3224;
    static String field3225;
    static String field3226;
    static String field3227;
    static String field3228;
    static String field3229;
    static String field3230;
    static String field3231;
    static String field3232;
    static String field3233;
    static String field3234;
    static double field3235;
    static double field3236;
    static double field3237;
    static double field3238;
    static double field3239;
    static String field3240;
    static float field3241;
    static float field3242;
    static float field3243;
    static float field3244;
    static String field3245;
    static float field3246;
    static float field3247;
    static float field3248;
    static float field3249;
    static float field3250;
    static float field3251;
    static String field3252;
    static String field3253;
    static String field3254;
    static String field3255;
    static String field3256;
    static String field3257;
    static String field3258;
    static String field3259;
    static String field3260;
    static String field3261;
    static String field3262;
    static String field3263;
    static String field3264;
    static String field3265;
    static String field3266;
    static String field3267;

    public AttackAura() {
        super(field2847, field2848, Combat.Combat);
        this.var_minced_core_ClsClass270_I = new MultiBooleanSetting(field2864, new BooleanSetting(field2865, true), new BooleanSetting(field2866, true), new BooleanSetting(field2867, false), new BooleanSetting(field2868, false));
        this.field2759 = new MultiBooleanSetting(field2869, new BooleanSetting(field2870, true), new BooleanSetting(field2871, false), new BooleanSetting(field2872, false));
        this.field2877 = new ModeSetting(field2873, field2874, field2875, field2876);
        this.field2835 = new ModeSetting(field2878, field2879, field2880, field2881, field2882, field2883, field2884, field2885, field2886);
        this.field2830 = new SliderSetting(field2887, field2888, 2.0f, field2889, field2890);
        this.field2895 = new SliderSetting(field2891, field2892, 0.0f, field2893, field2894);
        this.field2844 = new SliderSetting(field2896, field2897, 0.0f, field2898, field2899);
        this.var_minced_core_ClsClass271_O = new BooleanSetting(field2900, false, () -> !this.field2733.isMode(field2745) && !this.field2733.isMode(field2746));
        this.field2738 = new BooleanSetting(field2901, false, () -> this.field2759.method1898(field2837));
        this.field2903 = new BooleanSetting(field2902, true, () -> this.field2759.method1898(field3212));
        this.field2808 = new BooleanSetting(field2904, false);
        this.field2765 = new BooleanSetting(field2905, false);
        this.field2756 = new BooleanSetting(field2906, false);
        this.field2757 = new BooleanSetting(field2907, true);
        this.field2763 = new BooleanSetting(field2908, true, () -> this.field2733.isMode(field2748));
        this.field2915 = new MultiBooleanSetting(field2909, () -> this.field2733.isMode(field3181), new BooleanSetting(field2910, true), new BooleanSetting(field2911, true), new BooleanSetting(field2912, true), new BooleanSetting(field2913, true), new BooleanSetting(field2914, true));
        this.field2917 = new BooleanSetting(field2916, true, () -> this.field2733.isMode(field3176));
        this.field2923 = new SliderSetting(field2918, field2919, field2920, field2921, field2922, () -> this.field2733.isMode(field3191));
        this.field2925 = new BooleanSetting(field2924, true, () -> this.field2733.isMode(field2737));
        this.field2927 = new BooleanSetting(field2926, true, () -> this.field2733.isMode(field2752));
        this.field2929 = new BooleanSetting(field2928, false, () -> this.field2733.isMode(field3258));
        this.field2735 = new ModeSetting(field2930, field2931, () -> this.field2733.isMode(field3267), field2932, field2933);
        this.var_minced_core_ClsClass271_l = new BooleanSetting(field2934, true, () -> this.field2733.isMode(field3219) && this.field2735.isMode(field3220));
        this.field2939 = new SliderSetting(field2935, field2936, field2937, field2938, 1.0f, () -> this.field2733.isMode(field3217) && this.field2735.isMode(field3218));
        this.field2943 = new SliderSetting(field2940, field2941, 0.0f, field2942, 1.0f, () -> this.field2733.isMode(field3265) && this.field2735.isMode(field3266));
        this.field2823 = new SliderSetting(field2944, field2945, 0.0f, field2946, 1.0f, () -> this.field2733.isMode(field3229) && this.field2735.isMode(field3230));
        this.field2825 = new SliderSetting(field2947, field2948, 0.0f, field2949, 1.0f, () -> this.field2733.isMode(field3197) && this.field2735.isMode(field3198));
        this.field2739 = new SliderSetting(field2950, field2951, field2952, field2953, field2954, () -> this.field2733.isMode(field2781) && this.field2735.isMode(field2782));
        this.field2960 = new SliderSetting(field2955, field2956, field2957, field2958, field2959, () -> this.field2733.isMode(field2820) && this.field2735.isMode(field2821));
        this.field2965 = new SliderSetting(field2961, field2962, field2963, field2964, 1.0f, () -> this.field2733.isMode(field3172) && this.field2735.isMode(field3173));
        this.field2970 = new SliderSetting(field2966, field2967, field2968, field2969, 1.0f, () -> this.field2733.isMode(field3156) && this.field2735.isMode(field3157));
        this.field2824 = new SliderSetting(field2971, field2972, 2.0f, field2973, 1.0f, () -> this.field2733.isMode(field3179) && this.field2735.isMode(field3180));
        this.var_minced_core_ClsClass276_O = new SliderSetting(field2974, field2975, 2.0f, field2976, 1.0f, () -> this.field2733.isMode(field3158) && this.field2735.isMode(field3159));
        this.field2751 = new SliderSetting(field2977, field2978, field2979, field2980, field2981, () -> this.field2733.isMode(field2845) && this.field2735.isMode(field2846));
        this.field2986 = new SliderSetting(field2982, O5, field2983, field2984, field2985, () -> this.field2733.isMode(field2778) && this.field2735.isMode(field2779));
        this.field2785 = new SliderSetting(OI, O2, field2987, field2988, field2989, () -> this.field2733.isMode(field3261) && this.field2735.isMode(field3262));
        this.field2791 = new SliderSetting(field2990, field2991, field2992, field2993, O7, () -> this.field2733.isMode(field3221) && this.field2735.isMode(field3222));
        this.field2998 = new SliderSetting(field2994, field2995, 0.0f, field2996, field2997, () -> this.field2733.isMode(field2812) && this.field2735.isMode(field2813));
        this.field3002 = new SliderSetting(field2999, field3000, 0.0f, field3001, 1.0f, () -> this.field2733.isMode(field2749) && this.field2735.isMode(field2750));
        this.field3006 = new SliderSetting(field3003, field3004, 0.0f, field3005, 1.0f, () -> this.field2733.isMode(field3215) && this.field2735.isMode(field3216));
        this.field3011 = new SliderSetting(field3007, field3008, field3009, field3010, 1.0f, () -> this.field2733.isMode(field3225) && this.field2735.isMode(field3226));
        this.field2805 = new SliderSetting(field3012, field3013, 0.0f, field3014, 1.0f, () -> this.field2733.isMode(field2793) && this.field2735.isMode(field2794));
        this.field3017 = new SliderSetting(field3015, field3016, 0.0f, OO, 1.0f, () -> this.field2733.isMode(field2842) && this.field2735.isMode(field2843));
        this.field3021 = new SliderSetting(field3018, field3019, 0.0f, field3020, 1.0f, () -> this.field2733.isMode(field3252) && this.field2735.isMode(field3253));
        this.var_minced_core_ClsClass276_l = new SliderSetting(field3022, field3023, field3024, field3025, field3026, () -> this.field2733.isMode(field3199) && this.field2735.isMode(field3200));
        this.field3031 = new SliderSetting(field3027, field3028, field3029, Ol, field3030, () -> this.field2733.isMode(field3154) && this.field2735.isMode(field3155));
        this.field3034 = new SliderSetting(field3032, 0.0f, 0.0f, field3033, 1.0f, () -> this.field2733.isMode(field3168) && this.field2735.isMode(field3169));
        this.field2815 = new SliderSetting(field3035, 0.0f, 0.0f, field3036, 1.0f, () -> this.field2733.isMode(field3162) && this.field2735.isMode(field3163));
        this.field3038 = new BooleanSetting(field3037, true, () -> this.field2733.isMode(field2816) && this.field2735.isMode(field2817));
        this.field3040 = new BooleanSetting(field3039, false, () -> this.field2733.isMode(field2806) && this.field2735.isMode(field2807));
        this.field3045 = new SliderSetting(field3041, field3042, O4, field3043, field3044, () -> this.field2733.isMode(field3189) && this.field2735.isMode(field3190));
        this.field3047 = new SliderSetting(field3046, O1, 0.0f, O0, 1.0f, () -> this.field2733.isMode(field3185) && this.field2735.isMode(field3186));
        this.field3050 = new SliderSetting(cfr_renamed_364, field3048, 0.0f, field3049, 1.0f, () -> this.field2733.isMode(field3206) && this.field2735.isMode(field3207));
        this.field3053 = new SliderSetting(field3051, field3052, 0.0f, O3, 1.0f, () -> this.field2733.isMode(field3170) && this.field2735.isMode(field3171));
        this.field3058 = new SliderSetting(field3054, field3055, field3056, field3057, 1.0f, () -> this.field2733.isMode(field2818) && this.field2735.isMode(field2819));
        this.field3063 = new SliderSetting(field3059, field3060, field3061, field3062, 1.0f, () -> this.field2733.isMode(field2789) && this.field2735.isMode(field2790));
        this.field3064 = new SliderSetting(O6, O9, O_, cfr_renamed_365, 1.0f, () -> this.field2733.isMode(field3208) && this.field2735.isMode(field3209));
        this.field3068 = new SliderSetting(field3065, O8, field3066, field3067, 1.0f, () -> this.field2733.isMode(field2734) && this.field2735.isMode(field2736));
        this.field3072 = new SliderSetting(field3069, field3070, 1.0f, field3071, 1.0f, () -> this.field2733.isMode(field3164) && this.field2735.isMode(field3165));
        this.field3078 = new SliderSetting(field3073, field3074, field3075, field3076, field3077, () -> this.field2733.isMode(field3210) && this.field2735.isMode(field3211));
        this.field3081 = new SliderSetting(field3079, 0.0f, 0.0f, field3080, 1.0f, () -> this.field2733.isMode(field2810) && this.field2735.isMode(field2811));
        this.field3083 = new BooleanSetting(field3082, true, () -> this.field2733.isMode(field3152) && this.field2735.isMode(field3153));
        this.field3085 = new BooleanSetting(field3084, true, () -> this.field2733.isMode(field3201) && this.field2735.isMode(field3202));
        this.field3090 = new SliderSetting(field3086, field3087, field3088, field3089, 1.0f, () -> this.field2733.isMode(field3263) && this.field2735.isMode(field3264));
        this.cfr_renamed_4 = new SliderSetting(field3091, field3092, 0.0f, field3093, field3094, () -> this.field2733.isMode(field3213) && this.field2735.isMode(field3214));
        this.field3100 = new SliderSetting(field3095, field3096, field3097, field3098, field3099, () -> this.field2733.isMode(field2832) && this.field2735.isMode(field2833));
        this.field3106 = new SliderSetting(field3101, field3102, field3103, field3104, field3105, () -> this.field2733.isMode(field2787) && this.field2735.isMode(field2788));
        this.field2795 = new SliderSetting(field3107, field3108, field3109, field3110, field3111, () -> this.field2733.isMode(field3187) && this.field2735.isMode(field3188));
        this.field3117 = new SliderSetting(field3112, field3113, field3114, field3115, field3116, () -> this.field2733.isMode(field3259) && this.field2735.isMode(field3260));
        this.field3122 = new SliderSetting(field3118, field3119, 0.0f, field3120, field3121, () -> this.field2733.isMode(field2838) && this.field2735.isMode(field2839));
        this.field3126 = new SliderSetting(field3123, field3124, 0.0f, 2.0f, field3125, () -> this.field2733.isMode(field3166) && this.field2735.isMode(field3167));
        this.field2836 = new SliderSetting(field3127, field3128, field3129, field3130, field3131, () -> this.field2733.isMode(field3174) && this.field2735.isMode(field3175));
        this.field2834 = new SliderSetting(field3132, field3133, 0.0f, 2.0f, field3134, () -> this.field2733.isMode(field3223) && this.field2735.isMode(field3224));
        this.field2783 = new SliderSetting(field3135, field3136, 0.0f, field3137, 1.0f, () -> this.field2733.isMode(field2741) && this.field2735.isMode(field2742));
        this.field2792 = new SliderSetting(field3138, field3139, 0.0f, field3140, field3141, () -> this.field2733.isMode(field3177) && this.field2735.isMode(field3178));
        this.field2822 = new SliderSetting(field3142, 0.0f, 0.0f, field3143, 1.0f, () -> this.field2733.isMode(field2754) && this.field2735.isMode(field2755));
        this.field3146 = new SliderSetting(field3144, 0.0f, 0.0f, field3145, 1.0f, () -> this.field2733.isMode(field3227) && this.field2735.isMode(field3228));
        this.field3148 = new BooleanSetting(field3147, true, () -> this.field2733.isMode(field2840) && this.field2735.isMode(field2841));
        this.field2777 = field3149;
        this.field2780 = new AuraTargetBuilder();
        this.field2740 = new WeaponUtil();
        this.field2743 = new AuraModePolar();
        this.field2753 = new ClientState();
        this.field2771 = field3150;
        if (this.field2733.isMode(field3151)) {
            NeuroProfileStorage.method2288();
        }
        this.addSettings(this.field2733, this.var_minced_core_ClsClass270_I, this.field2809, this.field2877, this.field2759, this.field2835, this.field2915, this.field2917, this.field2923, this.field2925, this.field2927, this.field2929, this.field2735, this.var_minced_core_ClsClass271_l, this.field2939, this.field2943, this.field2823, this.field2825, this.field2739, this.field2960, this.field2965, this.field2970, this.field2824, this.var_minced_core_ClsClass276_O, this.field2751, this.field2986, this.field2785, this.field2791, this.field2998, this.field3002, this.field3006, this.field3011, this.field2805, this.field3017, this.field3021, this.var_minced_core_ClsClass276_l, this.field3031, this.field3034, this.field2815, this.field3038, this.field3040, this.field3045, this.field3047, this.field3050, this.field3053, this.field3058, this.field3063, this.field3064, this.field3068, this.field3072, this.field3078, this.field3081, this.field3083, this.field3085, this.field3090, this.cfr_renamed_4, this.field3100, this.field3106, this.field2795, this.field3117, this.field3122, this.field3126, this.field2836, this.field2834, this.field2783, this.field2792, this.field2822, this.field3146, this.field3148, this.field2830, this.field2895, this.var_minced_core_ClsClass271_O, this.field2903, this.field2808, this.field2765, this.field2756, this.field2757, this.field2763);
    }

    public int method731() {
        return this.field2731;
    }

    public double method732() {
        return this.field2732;
    }

    public BooleanSetting method735() {
        return this.field2738;
    }

    public SliderSetting method736() {
        return this.field2739;
    }

    private boolean method737() {
        return this.field2740.method3149(this);
    }

    public AuraItemUtil method739() {
        if (this.field2743.getVanillaHandler() == null) {
            return null;
        }
        return this.field2743.getVanillaHandler().method3147();
    }

    private double I(LivingEntity param1) {
        double v2 = param1.getHealth() + param1.getAbsorptionAmount();
        if (param1 instanceof PlayerEntity) {
            PlayerEntity v4 = (PlayerEntity)param1;
            double v5 = this.method744((LivingEntity)v4);
            return v2 * (1.0 + v5 / field2744);
        }
        return v2;
    }

    public boolean method741() {
        return this.field2733.isMode(field2747);
    }

    public MultiBooleanSetting method742() {
        return this.var_minced_core_ClsClass270_I;
    }

    private double method744(LivingEntity param1) {
        if (param1 instanceof PlayerEntity) {
            PlayerEntity v2 = (PlayerEntity)param1;
            double v3 = 0.0;
            for (ItemStack v6 : v2.getArmorItems()) {
                if (!(v6.getItem() instanceof ArmorItem)) continue;
                v3 += this.method898(v6);
            }
            return v3;
        }
        return param1.getArmor();
    }

    public SliderSetting method746() {
        return this.field2751;
    }

    public ClientState method748() {
        return this.field2753;
    }

    private void method750() {
        boolean v1;
        if (AttackAura.var_net_minecraft_client_MinecraftClient_I.player == null || AttackAura.var_net_minecraft_client_MinecraftClient_I.world == null || AttackAura.var_net_minecraft_client_MinecraftClient_I.interactionManager == null) {
            return;
        }
        if (((Boolean)this.field2756.getValue()).booleanValue() && AttackAura.var_net_minecraft_client_MinecraftClient_I.player.isUsingItem() && !AttackAura.var_net_minecraft_client_MinecraftClient_I.player.getOffHandStack().getItem().equals(Items.SHIELD)) {
            return;
        }
        if (!((Boolean)this.field2757.getValue()).booleanValue() && !AttackAura.var_net_minecraft_client_MinecraftClient_I.player.canSee((Entity)field2758)) {
            return;
        }
        if (AttackAura.var_net_minecraft_client_MinecraftClient_I.player.distanceTo((Entity)field2758) > this.method909()) {
            return;
        }
        boolean bl = v1 = AttackAura.var_net_minecraft_client_MinecraftClient_I.player.isTouchingWater() || AttackAura.var_net_minecraft_client_MinecraftClient_I.player.isInLava() || AttackAura.var_net_minecraft_client_MinecraftClient_I.player.isSwimming() || AttackAura.var_net_minecraft_client_MinecraftClient_I.player.isGliding();
        if (this.field2759.method1898(field2760).booleanValue() && AttackAura.var_net_minecraft_client_MinecraftClient_I.player.isBlocking()) {
            AttackAura.var_net_minecraft_client_MinecraftClient_I.interactionManager.stopUsingItem((PlayerEntity)AttackAura.var_net_minecraft_client_MinecraftClient_I.player);
        }
        if (!v1 && AttackAura.var_net_minecraft_client_MinecraftClient_I.player.isSprinting()) {
            this.var_boolean_l = true;
            if (this.field2733.isMode(field2761)) {
                this.field2762 = 1;
            }
            if (((IClientPlayerEntityMixin)AttackAura.var_net_minecraft_client_MinecraftClient_I.player).getLastSprinting()) {
                return;
            }
        }
        if (field2758 != null && ((Boolean)this.field2763.getValue()).booleanValue() && !AttackAura.var_net_minecraft_client_MinecraftClient_I.player.canSee((Entity)field2758)) {
            this.method838();
        }
        if (!this.field2733.isMode(field2764)) {
            if (((Boolean)this.field2765.getValue()).booleanValue()) {
                this.field2768 = System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(field2766, field2767);
                this.field2771 = ThreadLocalRandom.current().nextFloat(field2769, field2770);
                this.field2772 = 0L;
            } else {
                this.field2768 = System.currentTimeMillis() + field2773;
            }
        }
        this.field2743.notifyAttack(this, field2758);
        AttackAura.var_net_minecraft_client_MinecraftClient_I.interactionManager.attackEntity((PlayerEntity)AttackAura.var_net_minecraft_client_MinecraftClient_I.player, (Entity)field2758);
        ElytraResolver v2 = (ElytraResolver)Minced.getInstance().getModuleManager().method187(ElytraResolver.class);
        if (v2 != null && v2.isEnabled()) {
            v2.method264(field2758);
        }
        AttackAura.var_net_minecraft_client_MinecraftClient_I.player.swingHand(Hand.MAIN_HAND);
        if (this.field2759.method1898(field2774).booleanValue()) {
            this.method850();
        }
        this.field2753.method3114();
        ++this.field2731;
        this.field2777 = ThreadLocalRandom.current().nextFloat(field2775, field2776);
    }

    public AuraTargetBuilder method752() {
        return this.field2780;
    }

    public SliderSetting method754() {
        return this.field2783;
    }

    @EventHandler(2)
    public void method755(EventPostUpdate param1) {
        PacketCriticals v2 = (PacketCriticals)Minced.getInstance().getModuleManager().method187(PacketCriticals.class);
        if (v2 != null && v2.isEnabled() && v2.method103().isMode(field2784) && AttackAura.var_net_minecraft_client_MinecraftClient_I.player.hasStatusEffect(StatusEffects.SLOW_FALLING) && !AttackAura.var_net_minecraft_client_MinecraftClient_I.player.isTouchingWater() && AttackAura.var_net_minecraft_client_MinecraftClient_I.player.fallDistance > 0.0f && AttackAura.var_net_minecraft_client_MinecraftClient_I.player.fallDistance < 1.0f) {
            if (field2758 != null) {
                if (this.method737() && this.method854() && !this.field2743.isSelectedModeReady(this, field2758)) {
                    this.method750();
                }
            } else {
                this.field2768 = System.currentTimeMillis();
            }
        }
    }

    public BooleanSetting method756() {
        return this.field2763;
    }

    public SliderSetting method757() {
        return this.field2785;
    }

    public void I(double param1) {
        this.field2786 = param1;
    }

    @EventHandler(2)
    public void method759(EventFrame param1) {
        this.method864();
    }

    public SliderSetting method761() {
        return this.field2791;
    }

    public SliderSetting method762() {
        return this.field2792;
    }

    public SliderSetting method764() {
        return this.field2795;
    }

    public double method765(LivingEntity param1) {
        if (AttackAura.var_net_minecraft_client_MinecraftClient_I.player == null) {
            return field2796;
        }
        double v2 = 0.0 + (double)AttackAura.var_net_minecraft_client_MinecraftClient_I.player.distanceTo((Entity)param1) * field2797;
        v2 += this.method807(param1) * field2798;
        v2 += this.I(param1) * field2799;
        v2 += this.method744(param1) * 1.0;
        if (param1 instanceof PlayerEntity) {
            PlayerEntity v4 = (PlayerEntity)param1;
            if (v4.isBlocking()) {
                v2 += field2800;
            }
            if (v4.getArmor() == 0) {
                v2 -= field2801;
            }
            if (v4.getHealth() <= field2802) {
                v2 -= field2803;
            }
        }
        if (AttackAura.var_net_minecraft_client_MinecraftClient_I.player.canSee((Entity)param1)) {
            v2 -= field2804;
        }
        return v2;
    }

    public SliderSetting method766() {
        return this.field2805;
    }

    public void cfr_underscore(int param1) {
        this.field2731 = param1;
    }

    public BooleanSetting method767() {
        return this.var_minced_core_ClsClass271_l;
    }

    public void method768(boolean param1) {
        this.var_boolean_l = param1;
    }

    public BooleanSetting cfr_renamed_267() {
        return this.field2808;
    }

    public ModeSetting method770() {
        return this.field2809;
    }

    public long long_I() {
        return this.field2772;
    }

    private static /* synthetic */ Boolean method773(LivingEntity param0) {
        return !param0.isGliding();
    }

    private void method774() {
        AuraItemUtil v1 = this.method739();
        if (v1 == null || field2758 == null) {
            if (v1 != null) {
                v1.method3084();
            }
            return;
        }
        if (!this.field2814) {
            this.field2743.beforeAttack(this, field2758);
        }
        if (this.method737() && v1.method3092(this) && v1.method3101(this, field2758) && !this.field2743.isSelectedModeReady(this, field2758)) {
            this.method750();
        }
    }

    public SliderSetting method775() {
        return this.field2815;
    }

    public SliderSetting method779() {
        return this.field2822;
    }

    public BooleanSetting method780() {
        return this.var_minced_core_ClsClass271_O;
    }

    public boolean method781(double param1) {
        return false;
    }

    public SliderSetting minced_core_ClsClass276_I() {
        return this.field2823;
    }

    public SliderSetting cfr_renamed_266() {
        return this.field2824;
    }

    public boolean method782() {
        return field2758 != null && this.method737() && this.method854() && !this.field2743.isSelectedModeReady(this, field2758);
    }

    public double method783() {
        return this.field2786;
    }

    public SliderSetting method784() {
        return this.field2825;
    }

    private void method785() {
        PacketCriticals v1;
        if (AttackAura.var_net_minecraft_client_MinecraftClient_I.player == null || AttackAura.var_net_minecraft_client_MinecraftClient_I.world == null || AttackAura.var_net_minecraft_client_MinecraftClient_I.interactionManager == null) {
            return;
        }
        if (this.field2733.isMode(field2826) && !((Boolean)this.var_minced_core_ClsClass271_O.getValue()).booleanValue()) {
            this.var_minced_core_ClsClass271_O.setValue(true);
        }
        if ((v1 = (PacketCriticals)Minced.getInstance().getModuleManager().method187(PacketCriticals.class)) == null || !v1.isEnabled() || !v1.method103().isMode(field2827) || !AttackAura.var_net_minecraft_client_MinecraftClient_I.player.hasStatusEffect(StatusEffects.SLOW_FALLING) || AttackAura.var_net_minecraft_client_MinecraftClient_I.player.isTouchingWater()) {
            if (field2758 != null) {
                if (this.field2733.isMode(field2828)) {
                    this.method774();
                } else if (this.method737() && this.method854() && !this.field2743.isSelectedModeReady(this, field2758)) {
                    boolean v3;
                    ElytraTarget v2 = (ElytraTarget)Minced.getInstance().getModuleManager().method187(ElytraTarget.class);
                    boolean bl = v3 = v2 != null && AttackAura.var_net_minecraft_client_MinecraftClient_I.player.isGliding() && v2.method1186((Entity)field2758);
                    if (!v3 && (this.field2733.isMode(field2829) || ((Boolean)this.var_minced_core_ClsClass271_O.getValue()).booleanValue())) {
                        if (AttackAura.method836(AttackAura.var_net_minecraft_client_MinecraftClient_I.player.getYaw(), AttackAura.var_net_minecraft_client_MinecraftClient_I.player.getPitch(), ((Float)this.field2830.getValue()).floatValue(), (Entity)field2758)) {
                            this.method750();
                        }
                    } else {
                        this.method750();
                    }
                    if (!this.field2814) {
                        this.field2743.beforeAttack(this, field2758);
                    }
                } else if (!this.field2814) {
                    this.field2743.beforeAttack(this, field2758);
                }
            } else {
                AuraItemUtil v2;
                this.field2768 = System.currentTimeMillis();
                this.field2814 = false;
                if (this.field2733.isMode(field2831) && (v2 = this.method739()) != null) {
                    v2.method3084();
                }
                this.field2743.resetSelectedMode(this);
            }
        }
    }

    public SliderSetting method787() {
        return this.field2834;
    }

    public ModeSetting method788() {
        return this.field2835;
    }

    public SliderSetting method789() {
        return this.field2836;
    }

    public SliderSetting method794() {
        return this.field2844;
    }

    public SliderSetting method796() {
        return this.field3081;
    }

    public boolean method798() {
        return this.field2814;
    }

    public ModeSetting method799() {
        return this.field2733;
    }

    public long method800() {
        return this.field2768;
    }

    public boolean method801() {
        return this.var_boolean_l;
    }

    public SliderSetting method805() {
        return this.field3006;
    }

    public SliderSetting method806() {
        return this.field3122;
    }

    private double method807(LivingEntity param1) {
        if (AttackAura.var_net_minecraft_client_MinecraftClient_I.player == null) {
            return 0.0;
        }
        Vec3d v2 = AttackAura.var_net_minecraft_client_MinecraftClient_I.player.getEyePos();
        Vec3d v3 = param1.getPos().add(0.0, (double)param1.getHeight() / field3160, 0.0);
        Vec3d v4 = v3.subtract(v2);
        double v5 = Math.toDegrees(Math.atan2(v4.z, v4.x)) - field3161;
        double v7 = -Math.toDegrees(Math.atan2(v4.y, Math.sqrt(v4.x * v4.x + v4.z * v4.z)));
        double v9 = MathHelper.wrapDegrees((double)(v5 - (double)AttackAura.var_net_minecraft_client_MinecraftClient_I.player.getYaw()));
        double v11 = v7 - (double)AttackAura.var_net_minecraft_client_MinecraftClient_I.player.getPitch();
        return Math.sqrt(v9 * v9 + v11 * v11);
    }

    public SliderSetting method809() {
        return this.field2943;
    }

    public SliderSetting method811() {
        return this.field3031;
    }

    public SliderSetting method812() {
        return this.field2960;
    }

    public SliderSetting method813() {
        return this.var_minced_core_ClsClass276_l;
    }

    public SliderSetting method815() {
        return this.field2986;
    }

    public void method816(double param1) {
        this.field2732 = param1;
    }

    public SliderSetting method817() {
        return this.field3078;
    }

    public BooleanSetting method818() {
        return this.field2929;
    }

    public SliderSetting method819() {
        return this.field2970;
    }

    public BooleanSetting method821() {
        return this.field2757;
    }

    public ModeSetting method822() {
        return this.field2735;
    }

    public SliderSetting method825() {
        return this.field3058;
    }

    public BooleanSetting method826() {
        return this.field2903;
    }

    public BooleanSetting method831() {
        return this.field3038;
    }

    public void method832(boolean param1) {
        this.field2814 = param1;
    }

    public SliderSetting method833() {
        return this.field2965;
    }

    public SliderSetting method834() {
        return this.field3146;
    }

    public static boolean method836(float param0, float param1, double param2, Entity param4) {
        Vec3d v5 = AttackAura.var_net_minecraft_client_MinecraftClient_I.player.getCameraPosVec(var_net_minecraft_client_MinecraftClient_I.getRenderTickCounter().getTickDelta(false));
        Vec3d v6 = AttackAura.var_net_minecraft_client_MinecraftClient_I.player.getRotationVector(param1, param0);
        Vec3d v7 = v5.add(v6.multiply(param2));
        Box v8 = param4.getBoundingBox();
        return v8.contains(v5) || v8.raycast(v5, v7).isPresent();
    }

    public void method837(ModeSetting param1) {
        this.field2835 = param1;
    }

    private void method838() {
        if (field2758 == null) {
            return;
        }
        Vec3d v1 = AttackAura.var_net_minecraft_client_MinecraftClient_I.player.getEyePos();
        Vec3d v2 = field2758.getEyePos();
        Vec3d v3 = v2.subtract(v1);
        double v4 = v3.length();
        if (v4 < field3182) {
            return;
        }
        int v6 = Math.max(1, (int)Math.ceil(v4 * field3183));
        HashSet<BlockPos> v7 = new HashSet<BlockPos>();
        for (int v8 = 0; v8 <= v6; ++v8) {
            double v9 = (double)v8 / (double)v6;
            BlockPos v11 = BlockPos.ofFloored((double)(v1.x + v3.x * v9), (double)(v1.y + v3.y * v9), (double)(v1.z + v3.z * v9));
            if (!v7.add(v11) || AttackAura.var_net_minecraft_client_MinecraftClient_I.world.getBlockState(v11).isAir()) continue;
            Vec3d v12 = v1.subtract(v11.toCenterPos());
            double v13 = Math.abs(v12.x);
            double v15 = Math.abs(v12.y);
            double v17 = Math.abs(v12.z);
            Direction v19 = v15 >= v13 && v15 >= v17 ? (v12.y > 0.0 ? Direction.UP : Direction.DOWN) : (v13 >= v17 ? (v12.x > 0.0 ? Direction.EAST : Direction.WEST) : (v12.z > 0.0 ? Direction.SOUTH : Direction.NORTH));
            AttackAura.var_net_minecraft_client_MinecraftClient_I.player.networkHandler.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, v11, v19, 0));
            AttackAura.var_net_minecraft_client_MinecraftClient_I.player.networkHandler.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, v11, v19, 0));
        }
    }

    public SliderSetting method839() {
        return this.field3100;
    }

    public float method840() {
        return (Boolean)this.field2808.getValue() != false ? TpsUtil.method2600() : field3184;
    }

    public SliderSetting method842() {
        return this.field3053;
    }

    public void method843(int param1) {
        this.field2762 = param1;
    }

    public SliderSetting method844() {
        return this.field2923;
    }

    @EventHandler(2)
    public void method848(EventClientTick param1) {
        if (field2758 != null) {
            double v2 = field2758.getX() - AttackAura.field2758.prevX;
            double v4 = field2758.getY() - AttackAura.field2758.prevY;
            double v6 = field2758.getZ() - AttackAura.field2758.prevZ;
            double v8 = Math.sqrt(v2 * v2 + v4 * v4 + v6 * v6);
            this.field2786 = this.field2732;
            this.field2732 = field3192 * v8;
        }
    }

    public SliderSetting method849() {
        return this.field3047;
    }

    private void method850() {
        if (!this.field2759.method1898(field3193).booleanValue()) {
            return;
        }
        int v1 = InventoryUtil.method3326();
        if (v1 == -1) {
            return;
        }
        if (field2758.getOffHandStack().getItem() != Items.SHIELD && field2758.getMainHandStack().getItem() != Items.SHIELD) {
            return;
        }
        if (v1 >= 9) {
            if (((Boolean)this.field2738.getValue()).booleanValue()) {
                return;
            }
            AttackAura.var_net_minecraft_client_MinecraftClient_I.interactionManager.clickSlot(AttackAura.var_net_minecraft_client_MinecraftClient_I.player.currentScreenHandler.syncId, v1, AttackAura.var_net_minecraft_client_MinecraftClient_I.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)AttackAura.var_net_minecraft_client_MinecraftClient_I.player);
            AttackAura.var_net_minecraft_client_MinecraftClient_I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(AttackAura.var_net_minecraft_client_MinecraftClient_I.player.currentScreenHandler.syncId));
            AttackAura.var_net_minecraft_client_MinecraftClient_I.interactionManager.attackEntity((PlayerEntity)AttackAura.var_net_minecraft_client_MinecraftClient_I.player, (Entity)field2758);
            AttackAura.var_net_minecraft_client_MinecraftClient_I.player.swingHand(Hand.MAIN_HAND);
            AttackAura.var_net_minecraft_client_MinecraftClient_I.interactionManager.clickSlot(AttackAura.var_net_minecraft_client_MinecraftClient_I.player.currentScreenHandler.syncId, v1, AttackAura.var_net_minecraft_client_MinecraftClient_I.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)AttackAura.var_net_minecraft_client_MinecraftClient_I.player);
            AttackAura.var_net_minecraft_client_MinecraftClient_I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(AttackAura.var_net_minecraft_client_MinecraftClient_I.player.currentScreenHandler.syncId));
        } else {
            AttackAura.var_net_minecraft_client_MinecraftClient_I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(v1));
            AttackAura.var_net_minecraft_client_MinecraftClient_I.interactionManager.attackEntity((PlayerEntity)AttackAura.var_net_minecraft_client_MinecraftClient_I.player, (Entity)field2758);
            AttackAura.var_net_minecraft_client_MinecraftClient_I.player.swingHand(Hand.MAIN_HAND);
            AttackAura.var_net_minecraft_client_MinecraftClient_I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(AttackAura.var_net_minecraft_client_MinecraftClient_I.player.getInventory().selectedSlot));
        }
    }

    private static /* synthetic */ double method851(LivingEntity param0) {
        return AttackAura.var_net_minecraft_client_MinecraftClient_I.player.distanceTo((Entity)param0);
    }

    public SliderSetting method852() {
        return this.cfr_renamed_4;
    }

    public SliderSetting method853() {
        return this.field3011;
    }

    private boolean method854() {
        if (AttackAura.var_net_minecraft_client_MinecraftClient_I.player == null) {
            return false;
        }
        long v1 = System.currentTimeMillis();
        if (v1 < this.field2768) {
            return false;
        }
        if (!((Boolean)this.field2765.getValue()).booleanValue()) {
            return true;
        }
        float v3 = AttackAura.var_net_minecraft_client_MinecraftClient_I.player.getAttackCooldownProgress(field3194);
        if (v3 < this.field2771) {
            this.field2772 = 0L;
            return false;
        }
        if (this.field2772 == 0L) {
            this.field2772 = v1 + ThreadLocalRandom.current().nextLong(field3195, field3196);
            return false;
        }
        return v1 >= this.field2772;
    }

    public float method855() {
        return this.field2777;
    }

    public AuraModePolar method856() {
        return this.field2743;
    }

    public SliderSetting method857() {
        return this.field3126;
    }

    public void method860(long param1) {
        this.field2772 = param1;
    }

    public static LivingEntity method861() {
        return field2758;
    }

    public SliderSetting method862() {
        return this.field2998;
    }

    private void method864() {
        if (AttackAura.var_net_minecraft_client_MinecraftClient_I.player == null || AttackAura.var_net_minecraft_client_MinecraftClient_I.world == null || field2758 == null) {
            this.field2814 = false;
            return;
        }
        boolean bl = this.field2814 = this.field2780.method3023(this, field2758) && AttackAura.field2758.hurtTime <= 0;
        if (!this.field2814) {
            this.field2743.updateSelectedRotation(this, field2758);
            // Keep a valid rotation request even when a mode has no usable
            // point (older mode implementations could silently do nothing).
            AimRotation rotation = AimRotation.method357();
            if (rotation != null && !rotation.method368()) {
                Vec3d eye = AttackAura.var_net_minecraft_client_MinecraftClient_I.player.getEyePos();
                Vec3d delta = field2758.getBoundingBox().getCenter().subtract(eye);
                float yaw = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0);
                float pitch = (float)-Math.toDegrees(Math.atan2(delta.y, Math.hypot(delta.x, delta.z)));
                AimRotation.method367(new Rotation(yaw, pitch), 180.0f, 1, 6);
            }
        } else {
            // Even a ready vanilla hit must keep the camera aligned.
            Vec3d eye = AttackAura.var_net_minecraft_client_MinecraftClient_I.player.getEyePos();
            Vec3d delta = field2758.getBoundingBox().getCenter().subtract(eye);
            AimRotation.method367(new Rotation(
                (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0),
                (float)-Math.toDegrees(Math.atan2(delta.y, Math.hypot(delta.x, delta.z)))),
                180.0f, 1, 6);
        }
    }

    @EventHandler(2)
    public void method865(EventInput param1) {
        if (field2758 != null) {
            if (this.field2877.isMode(field3204)) {
                TimerUtil.method3543(param1, RotationManager.method255());
            } else {
                Vec3d v2 = RotationTargetUtil.method1979((Entity)field2758);
                float v3 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v2.z, v2.x)) - field3205));
                TimerUtil.method3543(param1, v3);
            }
            if (this.var_boolean_l) {
                param1.method1973(0.0f);
                param1.method1972(0.0f);
                this.var_boolean_l = false;
            }
        }
    }

    public BooleanSetting method866() {
        return this.field3085;
    }

    public ModeSetting method867() {
        return this.field2877;
    }

    public BooleanSetting method868() {
        return this.field2925;
    }

    public SliderSetting method869() {
        return this.field3117;
    }

    public BooleanSetting method873() {
        return this.field2927;
    }

    public SliderSetting method876() {
        return this.field3072;
    }

    public SliderSetting method877() {
        return this.field3063;
    }

    public SliderSetting method878() {
        return this.field3050;
    }

    public SliderSetting method880() {
        return this.field3021;
    }

    public SliderSetting method881() {
        return this.field3106;
    }

    public BooleanSetting method886() {
        return this.field3148;
    }

    public SliderSetting method890() {
        return this.field2895;
    }

    public void method891(float param1) {
        this.field2777 = param1;
    }

    public WeaponUtil method892() {
        return this.field2740;
    }

    public SliderSetting method893() {
        return this.field3068;
    }

    public MultiBooleanSetting method894() {
        return this.field2759;
    }

    private boolean method895(LivingEntity param1) {
        if (AttackAura.var_net_minecraft_client_MinecraftClient_I.player == null || AttackAura.var_net_minecraft_client_MinecraftClient_I.world == null) {
            return false;
        }
        if (param1 instanceof ClientPlayerEntity) {
            return false;
        }
        float f = AttackAura.var_net_minecraft_client_MinecraftClient_I.player.distanceTo((Entity)param1);
        float f2 = ((Float)this.field2830.getValue()).floatValue();
        float f3 = ((Float)this.field2895.getValue()).floatValue();
        float f4 = AttackAura.var_net_minecraft_client_MinecraftClient_I.player.isGliding() ? ((Float)this.field2844.getValue()).floatValue() : 0.0f;
        if (f >= f2 + (f3 + f4)) {
            return false;
        }
        if (param1 instanceof PlayerEntity) {
            PlayerEntity v2 = (PlayerEntity)param1;
            if (v2.getName().getString().equalsIgnoreCase(AttackAura.var_net_minecraft_client_MinecraftClient_I.player.getName().getString())) {
                return false;
            }
            if (AntiBot.method1033(v2)) {
                return false;
            }
            if (!this.var_minced_core_ClsClass270_I.method1898(field3231).booleanValue() && Minced.getInstance().getFriendManager().cfr_renamed_4(v2.getName().getString())) {
                return false;
            }
        }
        if (!this.var_minced_core_ClsClass270_I.method1898(field3232).booleanValue() && param1 instanceof PlayerEntity) {
            return false;
        }
        if (!this.var_minced_core_ClsClass270_I.method1898(field3233).booleanValue() && param1 instanceof PlayerEntity && param1.getArmor() == 0) {
            return false;
        }
        if (!this.var_minced_core_ClsClass270_I.method1898(field3234).booleanValue() && param1 instanceof MobEntity) {
            return false;
        }
        return !param1.isInvulnerable() && param1.isAlive() && !(param1 instanceof ArmorStandEntity);
    }

    public MultiBooleanSetting method896() {
        return this.field2915;
    }

    public SliderSetting method897() {
        return this.field2939;
    }

    private double method898(ItemStack param1) {
        if (!(param1.getItem() instanceof ArmorItem)) {
            return 0.0;
        }
        double v2 = 0.0;
        AttributeModifiersComponent v4 = (AttributeModifiersComponent)param1.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (v4 != null) {
            for (AttributeModifiersComponent.Entry v6 : v4.modifiers()) {
                if (v6.attribute().equals((Object)EntityAttributes.ARMOR)) {
                    v2 += v6.modifier().value();
                }
                if (!v6.attribute().equals((Object)EntityAttributes.ARMOR_TOUGHNESS)) continue;
                v2 += v6.modifier().value() * field3235;
            }
        }
        ItemEnchantmentsComponent v5 = (ItemEnchantmentsComponent)param1.getOrDefault(DataComponentTypes.ENCHANTMENTS, (Object)ItemEnchantmentsComponent.DEFAULT);
        for (RegistryEntry v7 : v5.getEnchantments()) {
            int v8 = v5.getLevel(v7);
            if (v7.matchesKey(Enchantments.PROTECTION)) {
                v2 += (double)v8 * field3236;
                continue;
            }
            if (v7.matchesKey(Enchantments.FIRE_PROTECTION)) {
                v2 += (double)v8 * field3237;
                continue;
            }
            if (v7.matchesKey(Enchantments.BLAST_PROTECTION)) {
                v2 += (double)v8 * field3238;
                continue;
            }
            if (!v7.matchesKey(Enchantments.PROJECTILE_PROTECTION)) continue;
            v2 += (double)v8 * field3239;
        }
        return v2;
    }

    @EventHandler(2)
    public void method899(EventUpdate param1) {
        if (AttackAura.var_net_minecraft_client_MinecraftClient_I.player == null || AttackAura.var_net_minecraft_client_MinecraftClient_I.world == null) {
            return;
        }
        if (field2758 == null || !this.method895(field2758)) {
            this.method907();
        }
        this.method785();
    }

    public BooleanSetting method900() {
        return this.field3083;
    }

    @EventHandler(2)
    public void method901(EventRender2D param1) {
        if (!(this.isEnabled() && AttackAura.var_net_minecraft_client_MinecraftClient_I.player != null && this.field2733.isMode(field3240) && ((Boolean)this.field2917.getValue()).booleanValue())) {
            return;
        }
        NeuroSampler v2 = NeuroProfileStorage.method2292();
        v2.method2285((int)((Float)this.field2923.getValue()).floatValue());
        if (v2.method2286()) {
            return;
        }
        int v3 = v2.method2282();
        int v4 = v2.method2284();
        float v5 = v4 > 0 ? Math.clamp((float)v3 / (float)v4, 0.0f, 1.0f) : 0.0f;
        float v6 = var_net_minecraft_client_MinecraftClient_I.getWindow().getScaledWidth();
        float v7 = field3241;
        float v9 = field3243;
        float cfr_ignored_0 = (v6 - v7) * field3244;
        String v12 = String.format(field3245, v3, v4, Math.round(v5 * field3246));
        float v13 = Fonts.field12486[14].method2719(v12);
        float v14 = (v6 - v13) * field3247;
        ThemeColors.method3199(MAIN.HEADER);
        ThemeColors.method3199(MAIN.MODULE_VISUAL);
        int v17 = ThemeColors.method3199(MAIN.TEXT);
        Fonts.field12486[14].method2711(param1.method2997(), v12, v14, v9, v17);
        float cfr_ignored_1 = v9 + field3248;
        ColorUtil.method1853(v17, field3249);
    }

    public int method902() {
        return this.field2762;
    }

    public BooleanSetting method903() {
        return this.field2917;
    }

    public SliderSetting method904() {
        return this.field3045;
    }

    public SliderSetting minced_core_ClsClass276__() {
        return this.var_minced_core_ClsClass276_O;
    }

    public SliderSetting method905() {
        return this.field3034;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.field2814 = false;
        this.field2771 = ThreadLocalRandom.current().nextFloat(field3250, field3251);
        this.field2772 = 0L;
        this.field2743.activateSelectedMode(this);
    }

    public SliderSetting minced_core_ClsClass276_l() {
        return this.field3017;
    }

    private void method907() {
        if (var_net_minecraft_client_MinecraftClient_I.player == null || var_net_minecraft_client_MinecraftClient_I.world == null) {
            return;
        }
        ArrayList<LivingEntity> v1 = new ArrayList<LivingEntity>();
        for (Entity v3 : var_net_minecraft_client_MinecraftClient_I.world.getEntities()) {
            LivingEntity v4;
            if (!(v3 instanceof LivingEntity) || !this.method895(v4 = (LivingEntity)v3)) continue;
            v1.add(v4);
        }
        if (v1.isEmpty()) {
            field2758 = null;
            return;
        }
        if (v1.size() == 1) {
            field2758 = (LivingEntity)v1.getFirst();
            return;
        }
        String v3 = (String)this.field2809.getValue();
        Comparator<LivingEntity> v2;
        if (v3.equals(field3254)) {
            v2 = Comparator.comparingDouble(param0 -> AttackAura.var_net_minecraft_client_MinecraftClient_I.player.distanceTo((Entity)param0));
        } else if (v3.equals(field3255)) {
            v2 = Comparator.comparingDouble(this::I);
        } else if (v3.equals(field3256)) {
            v2 = Comparator.comparingDouble(this::method807);
        } else {
            v2 = Comparator.comparingDouble(this::method765);
        }
        ElytraTarget v5 = (ElytraTarget)Minced.getInstance().getModuleManager().method187(ElytraTarget.class);
        if (v5 != null && v5.isEnabled() && var_net_minecraft_client_MinecraftClient_I.player.isGliding() && v5.isPriorityMode(field3257)) {
            v1.sort(Comparator.comparing((LivingEntity param0) -> !param0.isGliding()).thenComparing(v2));
        } else {
            v1.sort(v2);
        }
        field2758 = (LivingEntity)v1.getFirst();
    }

    public BooleanSetting method908() {
        return this.field2756;
    }

    public float method909() {
        return ((Float)this.field2830.getValue()).floatValue();
    }

    public SliderSetting method911() {
        return this.field3090;
    }

    public void method912(long param1) {
        this.field2768 = param1;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        field2758 = null;
        this.field2768 = System.currentTimeMillis();
        this.field2731 = 0;
        this.field2762 = 0;
        this.field2814 = false;
        this.field2772 = 0L;
        this.field2743.deactivateCurrentMode(this);
    }

    public BooleanSetting minced_core_ClsClass271_l() {
        return this.field3040;
    }

    public SliderSetting method914() {
        return this.field3002;
    }

    public BooleanSetting minced_core_ClsClass271__() {
        return this.field2765;
    }

    public SliderSetting method917() {
        return this.field3064;
    }

    public void method918(float param1) {
        this.field2771 = param1;
    }

    public float method920() {
        return this.field2771;
    }

    static {
        field2847 = "AttackAura";
        field2848 = "Автоматически атакует сущностей в радиусе действия, выбирая цели и вращая камеру";
        field2849 = "Обход";
        field2858 = "Приоритет";
        field2859 = "Оптимальный";
        field2861 = "Дистанция";
        field2862 = "Здоровье";
        field2863 = "Угол поворота";
        field2864 = "Цели";
        field2865 = "Игроки";
        field2866 = "Голые";
        field2867 = "Мобы";
        field2868 = "Друзья";
        field2869 = "Опции";
        field2870 = "Только криты";
        field2871 = "Ломать щит";
        field2872 = "Отжимать щит";
        field2873 = "Тип коррекции";
        field2874 = "Свободная";
        field2875 = "Свободная";
        field2876 = "Строгая";
        field2878 = "Отображение цели";
        field2879 = "Призраки";
        field2880 = "Ромб";
        field2881 = "Призраки";
        field2882 = "Кристаллы";
        field2883 = "Кружок";
        field2884 = "Пентаграмма";
        field2885 = "Сияние";
        field2886 = "Не отображать";
        field2887 = "Дистанция аттаки";
        field2888 = 3.0f;
        field2889 = 5.0f;
        field2890 = 0.1f;
        field2891 = "Дистанция ротации";
        field2892 = 1.5f;
        field2893 = 5.0f;
        field2894 = 0.05f;
        field2896 = "Элитра ротация";
        field2897 = 30.0f;
        field2898 = 30.0f;
        field2899 = 0.05f;
        field2900 = "Проверка на луч";
        field2901 = "Только с хотбара";
        field2902 = "Только с пробелом";
        field2904 = "Синхронизация с ТПС";
        field2905 = "Легитное кд ударов";
        field2906 = "Не бить если ешь";
        field2907 = "Бить через стены";
        field2908 = "Бить через стены RW";
        field2909 = "Neuro Train";
        field2910 = "Yaw Speed";
        field2911 = "Pitch Speed";
        field2912 = "Hit Point";
        field2913 = "Rotation Point";
        field2914 = "Behavior";
        field2916 = "Прогресс обучения";
        field2918 = "Neuro: семплов до готовности";
        field2919 = 30000.0f;
        field2920 = 1000.0f;
        field2921 = 100000.0f;
        field2922 = 1000.0f;
        field2924 = "Neuro: отводка при разрыве";
        field2926 = "Legit: отводка при разрыве";
        field2928 = "Neuro Reset";
        field2930 = "Версия билдера";
        field2931 = "Старый";
        field2932 = "Старый";
        field2933 = "Новый";
        field2934 = "Земля: клиент лук";
        field2935 = "Земля: высота точки %";
        field2936 = 72.0f;
        field2937 = 15.0f;
        field2938 = 100.0f;
        field2940 = "Земля: смещение плеча %";
        field2941 = 100.0f;
        field2942 = 150.0f;
        field2944 = "Земля: разброс вбок %";
        field2945 = 38.0f;
        field2946 = 120.0f;
        field2947 = "Земля: разброс вглубь %";
        field2948 = 42.0f;
        field2949 = 120.0f;
        field2950 = "Земля: смена точки мс мин";
        field2951 = 140.0f;
        field2952 = 40.0f;
        field2953 = 900.0f;
        field2954 = 5.0f;
        field2955 = "Земля: смена точки мс макс";
        field2956 = 380.0f;
        field2957 = 60.0f;
        field2958 = 1400.0f;
        field2959 = 5.0f;
        field2961 = "Земля: инерция yaw %";
        field2962 = 78.0f;
        field2963 = 30.0f;
        field2964 = 98.0f;
        field2966 = "Земля: инерция pitch %";
        field2967 = 74.0f;
        field2968 = 30.0f;
        field2969 = 98.0f;
        field2971 = "Земля: сила слежения yaw %";
        field2972 = 38.0f;
        field2973 = 100.0f;
        field2974 = "Земля: сила слежения pitch %";
        field2975 = 30.0f;
        field2976 = 100.0f;
        field2977 = "Земля: макс шаг yaw °/тик";
        field2978 = 6.0f;
        field2979 = 0.3f;
        field2980 = 25.0f;
        field2981 = 0.05f;
        field2982 = "Земля: макс шаг pitch °/тик";
        O5 = 2.9f;
        field2983 = 0.15f;
        field2984 = 15.0f;
        field2985 = 0.05f;
        OI = "Земля: ускорение yaw °/тик²";
        O2 = 0.42f;
        field2987 = 0.05f;
        field2988 = 1.2f;
        field2989 = 0.01f;
        field2990 = "Земля: ускорение pitch °/тик²";
        field2991 = 0.28f;
        field2992 = 0.03f;
        field2993 = 0.9f;
        O7 = 0.01f;
        field2994 = "Земля: мёртвая зона pitch °";
        field2995 = 0.65f;
        field2996 = 4.0f;
        field2997 = 0.05f;
        field2999 = "Земля: джиттер yaw %";
        field3000 = 14.0f;
        field3001 = 100.0f;
        field3003 = "Земля: джиттер pitch %";
        field3004 = 11.0f;
        field3005 = 100.0f;
        field3007 = "Земля: отклик джиттера %";
        field3008 = 48.0f;
        field3009 = 10.0f;
        field3010 = 95.0f;
        field3012 = "Земля: усиление от камеры %";
        field3013 = 32.0f;
        field3014 = 100.0f;
        field3015 = "Земля: хаос / вариации %";
        field3016 = 14.0f;
        OO = 55.0f;
        field3018 = "Земля: шанс микропаузы %";
        field3019 = 6.0f;
        field3020 = 28.0f;
        field3022 = "Земля: сглаживание jerk yaw";
        field3023 = 0.22f;
        field3024 = 0.03f;
        field3025 = 0.95f;
        field3026 = 0.01f;
        field3027 = "Земля: сглаживание jerk pitch";
        field3028 = 0.14f;
        field3029 = 0.02f;
        Ol = 0.75f;
        field3030 = 0.01f;
        field3032 = "Земля: приоритет ротации";
        field3033 = 5.0f;
        field3035 = "Земля: таймаут ротации";
        field3036 = 40.0f;
        field3037 = "Земля: шаг по GCD";
        field3039 = "Элитра: клиент лук";
        field3041 = "Элитра: сила предикта";
        field3042 = 2.15f;
        O4 = 0.2f;
        field3043 = 5.5f;
        field3044 = 0.05f;
        field3046 = "Элитра: шум %";
        O1 = 24.0f;
        O0 = 100.0f;
        cfr_renamed_364 = "Элитра: смесь look/move %";
        field3048 = 52.0f;
        field3049 = 100.0f;
        field3051 = "Элитра: высота точки %";
        field3052 = 50.0f;
        O3 = 100.0f;
        field3054 = "Элитра: скорость yaw (прицел)";
        field3055 = 200.0f;
        field3056 = 5.0f;
        field3057 = 360.0f;
        field3059 = "Элитра: скорость pitch (прицел)";
        field3060 = 200.0f;
        field3061 = 5.0f;
        field3062 = 360.0f;
        O6 = "Элитра: возврат yaw";
        O9 = 200.0f;
        O_ = 5.0f;
        cfr_renamed_365 = 360.0f;
        field3065 = "Элитра: возврат pitch";
        O8 = 200.0f;
        field3066 = 5.0f;
        field3067 = 360.0f;
        field3069 = "Элитра: таймаут ротации";
        field3070 = 14.0f;
        field3071 = 40.0f;
        field3073 = "Элитра: порог скорости цели";
        field3074 = 0.07f;
        field3075 = 0.02f;
        field3076 = 0.35f;
        field3077 = 0.01f;
        field3079 = "Элитра: приоритет ротации";
        field3080 = 5.0f;
        field3082 = "Элитра: + предикт ElytraTarget";
        field3084 = "Земля: клиент лук";
        field3086 = "Земля: высота точки";
        field3087 = 80.0f;
        field3088 = 20.0f;
        field3089 = 100.0f;
        field3091 = "Земля: упреждение скорости";
        field3092 = 1.2f;
        field3093 = 4.0f;
        field3094 = 0.05f;
        field3095 = "Земля: отклонение от точки по яву";
        field3096 = 0.22f;
        field3097 = 0.05f;
        field3098 = 0.6f;
        field3099 = 0.01f;
        field3101 = "Земля: отклонение от точки по питчу";
        field3102 = 0.18f;
        field3103 = 0.05f;
        field3104 = 0.6f;
        field3105 = 0.01f;
        field3107 = "Земля: макс шаг yaw";
        field3108 = 8.0f;
        field3109 = 0.5f;
        field3110 = 25.0f;
        field3111 = 0.1f;
        field3112 = "Земля: макс шаг pitch";
        field3113 = 4.0f;
        field3114 = 0.3f;
        field3115 = 15.0f;
        field3116 = 0.1f;
        field3118 = "Земля: шум yaw";
        field3119 = 0.6f;
        field3120 = 3.0f;
        field3121 = 0.05f;
        field3123 = "Земля: шум pitch";
        field3124 = 0.35f;
        field3125 = 0.05f;
        field3127 = "Земля: дыхание частота";
        field3128 = 1.8f;
        field3129 = 0.3f;
        field3130 = 4.5f;
        field3131 = 0.05f;
        field3132 = "Земля: дыхание амплитуда";
        field3133 = 0.4f;
        field3134 = 0.05f;
        field3135 = "Земля: микропауза";
        field3136 = 30.0f;
        field3137 = 100.0f;
        field3138 = "Земля: мёртвая зона";
        field3139 = 0.4f;
        field3140 = 3.0f;
        field3141 = 0.05f;
        field3142 = "Земля: приоритет ротации";
        field3143 = 5.0f;
        field3144 = "Земля: таймаут ротации";
        field3145 = 40.0f;
        field3147 = "Земля: шаг по GCD";
        field3149 = 0.95f;
        field3150 = 0.94f;
        field3151 = "Neuro";
        field3204 = "Свободная";
        field3205 = 90.0;
        field3192 = 20.0;
        field2784 = "Post";
        field3240 = "Neuro";
        field3241 = 200.0f;
        field3242 = 5.0f;
        field3243 = 14.0f;
        field3244 = 0.5f;
        field3245 = "Neuro: %d / %d  (%d%%)";
        field3246 = 100.0f;
        field3247 = 0.5f;
        field3248 = 14.0f;
        field3249 = 0.22f;
        field3203 = "Polar";
        field2826 = "Legit";
        field2827 = "Post";
        field2828 = "Polar";
        field2829 = "Legit";
        field2831 = "Polar";
        field2747 = "Polar";
        field3184 = 0.5f;
        field3254 = "Дистанция";
        field3255 = "Здоровье";
        field3256 = "Угол поворота";
        field3257 = "Сперва элитры";
        field2796 = Double.MAX_VALUE;
        field2797 = 3.0;
        field2798 = 2.0;
        field2799 = 1.5;
        field2800 = 5.0;
        field2801 = 3.0;
        field2802 = 6.0f;
        field2803 = 5.0;
        field2804 = 2.0;
        field3160 = 2.0;
        field3161 = 90.0;
        field2760 = "Отжимать щит";
        field2761 = "Spooky Test";
        field2764 = "Polar";
        field2766 = 20L;
        field2767 = 80L;
        field2769 = 0.9f;
        field2770 = 0.985f;
        field2773 = 460L;
        field2774 = "Ломать щит";
        field2775 = 0.88f;
        field2776 = 0.94f;
        field3182 = 0.01;
        field3183 = 2.0;
        field3193 = "Ломать щит";
        field3194 = 0.5f;
        field3195 = 8L;
        field3196 = 45L;
        field3231 = "Друзья";
        field3232 = "Игроки";
        field3233 = "Голые";
        field3234 = "Мобы";
        field3250 = 0.9f;
        field3251 = 0.985f;
        field3235 = 0.5;
        field3236 = 0.25;
        field3237 = 0.15;
        field3238 = 0.15;
        field3239 = 0.15;
        field2744 = 25.0;
        field2840 = "Builder";
        field2841 = "Новый";
        field3227 = "Builder";
        field3228 = "Новый";
        field2754 = "Builder";
        field2755 = "Новый";
        field3177 = "Builder";
        field3178 = "Новый";
        field2741 = "Builder";
        field2742 = "Новый";
        field3223 = "Builder";
        field3224 = "Новый";
        field3174 = "Builder";
        field3175 = "Новый";
        field3166 = "Builder";
        field3167 = "Новый";
        field2838 = "Builder";
        field2839 = "Новый";
        field3259 = "Builder";
        field3260 = "Новый";
        field3187 = "Builder";
        field3188 = "Новый";
        field2787 = "Builder";
        field2788 = "Новый";
        field2832 = "Builder";
        field2833 = "Новый";
        field3213 = "Builder";
        field3214 = "Новый";
        field3263 = "Builder";
        field3264 = "Новый";
        field3201 = "Builder";
        field3202 = "Новый";
        field3152 = "Builder";
        field3153 = "Старый";
        field2810 = "Builder";
        field2811 = "Старый";
        field3210 = "Builder";
        field3211 = "Старый";
        field3164 = "Builder";
        field3165 = "Старый";
        field2734 = "Builder";
        field2736 = "Старый";
        field3208 = "Builder";
        field3209 = "Старый";
        field2789 = "Builder";
        field2790 = "Старый";
        field2818 = "Builder";
        field2819 = "Старый";
        field3170 = "Builder";
        field3171 = "Старый";
        field3206 = "Builder";
        field3207 = "Старый";
        field3185 = "Builder";
        field3186 = "Старый";
        field3189 = "Builder";
        field3190 = "Старый";
        field2806 = "Builder";
        field2807 = "Старый";
        field2816 = "Builder";
        field2817 = "Старый";
        field3162 = "Builder";
        field3163 = "Старый";
        field3168 = "Builder";
        field3169 = "Старый";
        field3154 = "Builder";
        field3155 = "Старый";
        field3199 = "Builder";
        field3200 = "Старый";
        field3252 = "Builder";
        field3253 = "Старый";
        field2842 = "Builder";
        field2843 = "Старый";
        field2793 = "Builder";
        field2794 = "Старый";
        field3225 = "Builder";
        field3226 = "Старый";
        field3215 = "Builder";
        field3216 = "Старый";
        field2749 = "Builder";
        field2750 = "Старый";
        field2812 = "Builder";
        field2813 = "Старый";
        field3221 = "Builder";
        field3222 = "Старый";
        field3261 = "Builder";
        field3262 = "Старый";
        field2778 = "Builder";
        field2779 = "Старый";
        field2845 = "Builder";
        field2846 = "Старый";
        field3158 = "Builder";
        field3159 = "Старый";
        field3179 = "Builder";
        field3180 = "Старый";
        field3156 = "Builder";
        field3157 = "Старый";
        field3172 = "Builder";
        field3173 = "Старый";
        field2820 = "Builder";
        field2821 = "Старый";
        field2781 = "Builder";
        field2782 = "Старый";
        field3197 = "Builder";
        field3198 = "Старый";
        field3229 = "Builder";
        field3230 = "Старый";
        field3265 = "Builder";
        field3266 = "Старый";
        field3217 = "Builder";
        field3218 = "Старый";
        field3219 = "Builder";
        field3220 = "Старый";
        field3267 = "Builder";
        field3258 = "Neuro";
        field2752 = "Legit";
        field2737 = "Neuro";
        field3191 = "Neuro";
        field3176 = "Neuro";
        field3181 = "Neuro";
        field2748 = "ReallyWorld";
        field3212 = "Только криты";
        field2837 = "Ломать щит";
        field2745 = "Polar";
        field2746 = "Legit";
    }
}



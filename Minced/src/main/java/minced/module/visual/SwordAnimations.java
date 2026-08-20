/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.Hand
 */
package minced.module.visual;

import minced.module.combat.AttackAura;
import minced.core.EventHandler;
import minced.core.Minced;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;

import java.util.function.BooleanSupplier;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.EventSwingSpeed;
import minced.core.EventEquipOffset;
import minced.core.EventSwingArm;
import minced.core.SwingAnimation;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;

public class SwordAnimations
extends Module {
    BooleanSetting field1916;
    static float field1917;
    static MinecraftClient I = MinecraftClient.getInstance();
    ModeSetting field1918 = new ModeSetting(field2031, field2032, field2033, field2034, field2035, field2036, field2037, field2038, field2039, field2040, field2041, field2042);
    static String field1919;
    static String field1920;
    static String field1921;
    static String field1922;
    static String field1923;
    static String field1924;
    static String field1925;
    static String field1926;
    static String field1927;
    static String field1928;
    static float field1929;
    static float field1930;
    static float field1931;
    static float field1932;
    static float field1933;
    static float field1934;
    static float field1935;
    static float field1936;
    SliderSetting field1937;
    static float field1938;
    static float field1939;
    static double field1940;
    static double field1941;
    static float field1942;
    static float field1943;
    static float field1944;
    static float field1945;
    SliderSetting field1946 = new SliderSetting(field2043, field2044, 0.0f, field2045, 1.0f, () -> this.field1918.isMode(field2020) || this.field1918.isMode(field2021));
    static float field1947;
    static float field1948;
    static float field1949;
    static double field1950;
    static double field1951;
    static float field1952;
    static float field1953;
    static float field1954;
    static float field1955;
    static float field1956;
    static float field1957;
    static float field1958;
    static float field1959;
    static float field1960;
    static float field1961;
    static float field1962;
    static float field1963;
    static float field1964;
    static float field1965;
    static float field1966;
    static float field1967;
    static float field1968;
    static float field1969;
    static float field1970;
    static float field1971;
    static float field1972;
    static float field1973;
    static float field1974;
    static float field1975;
    static float field1976;
    static float field1977;
    static float field1978;
    static float field1979;
    static double field1980;
    static float field1981;
    static float field1982;
    static float field1983;
    static float field1984;
    static float field1985;
    static float field1986;
    static float field1987;
    static float field1988;
    static float field1989;
    static float field1990;
    static float field1991;
    static float field1992;
    static float field1993;
    static float field1994;
    static float field1995;
    static float field1996;
    static double field1997;
    static float field1998;
    static double field1999;
    static float field2000;
    static float field2001;
    static float field2002;
    static float field2003;
    static float field2004;
    static float field2005;
    static float field2006;
    static float field2007;
    static float field2008;
    static float field2009;
    static float field2010;
    static float field2011;
    static float field2012;
    static float field2013;
    static float field2014;
    static float field2015;
    static float field2016;
    static float field2017;
    static float field2018;
    SliderSetting field2019;
    static String field2020;
    static String field2021;
    static String field2022;
    static String field2023;
    static String field2024;
    static String field2025;
    static String field2026;
    static String field2027;
    SliderSetting field2028;
    static String field2029;
    static String field2030;
    static String field2031;
    static String field2032;
    static String field2033;
    static String field2034;
    static String field2035;
    static String field2036;
    static String field2037;
    static String field2038;
    static String field2039;
    static String field2040;
    static String field2041;
    static String field2042;
    static String field2043;
    static float field2044;
    static float field2045;
    static String field2046;
    static float field2047;
    static float field2048;
    static String field2049;
    static float field2050;
    static String field2051;
    static float field2052;
    static float field2053;
    static String field2054;

    @EventHandler(2)
    public void method583(EventSwingArm param1) {
        AttackAura aura = (AttackAura)Minced.getInstance().getModuleManager().method187(AttackAura.class);
        if (((Boolean)this.field1916.getValue()).booleanValue() && (aura == null || !aura.isEnabled() || AttackAura.method861() == null)) {
            return;
        }
        MatrixStack v3 = param1.method2550();
        float v4 = MathHelper.sin((float)(MathHelper.sqrt((float)param1.method2551()) * field1917));
        if (param1.method2546() == Hand.MAIN_HAND) {
            boolean v5 = SwordAnimations.I.player.getMainArm() == Arm.LEFT;
            String v6 = (String)this.field1918.getValue();
            int v7 = -1;
            if (v6.equals(field1919)) v7 = 0;
            else if (v6.equals(field1920)) v7 = 1;
            else if (v6.equals(field1921)) v7 = 2;
            else if (v6.equals(field1922)) v7 = 3;
            else if (v6.equals(field1923)) v7 = 4;
            else if (v6.equals(field1924)) v7 = 5;
            else if (v6.equals(field1925)) v7 = 6;
            else if (v6.equals(field1926)) v7 = 7;
            else if (v6.equals(field1927)) v7 = 8;
            else if (v6.equals(field1928)) v7 = 9;
            switch (v7) {
                case 0: {
                    float v8 = MathHelper.sqrt((float)param1.method2551());
                    float v9 = v5 ? field1929 : 1.0f;
                    v3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(v9 * (field1930 + MathHelper.sin((float)(param1.method2551() * param1.method2551() * field1931)) / field1932 * field1933)));
                    v3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(v9 * MathHelper.sin((float)(v8 * field1934)) * field1935));
                    v3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.sin((float)(v8 * field1936)) * -((Float)this.field1937.getValue()).floatValue() * field1938));
                    v3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-v9 * field1939));
                    break;
                }
                case 1: {
                    v3.translate(0.0, field1940, field1941);
                    v3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(v5 ? field1942 : field1943));
                    v3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(v5 ? field1944 : field1945));
                    v3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-((Float)this.field1946.getValue()).floatValue() - ((Float)this.field1937.getValue()).floatValue() * field1947 * v4));
                    break;
                }
                case 2: {
                    v3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(param1.method2551() * field1948 - ((Float)this.field1937.getValue()).floatValue() * field1949 * v4));
                    break;
                }
                case 3: {
                    v3.translate(0.0, field1950, field1951);
                    v3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(v5 ? field1952 : field1953));
                    v3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(v5 ? field1954 : field1955));
                    v3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-((Float)this.field1946.getValue()).floatValue() - ((Float)this.field1937.getValue()).floatValue() * field1956 * v4));
                    break;
                }
                case 4: {
                    float v8 = v5 ? field1957 : 1.0f;
                    v3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(v8 * field1958));
                    v3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(v4 * field1959));
                    v3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(v8 * v4 * field1960));
                    v3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(v4 * field1961));
                    v3.translate(v8 * field1962, field1963, field1964);
                    v3.translate(v8 * field1965, field1966, 0.0f);
                    v3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(v8 * field1967));
                    v3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(field1968));
                    v3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(v8 * field1969));
                    break;
                }
                case 5: {
                    float v8 = v5 ? field1970 : 1.0f;
                    float v9 = MathHelper.sin((float)(param1.method2551() * param1.method2551() * field1971));
                    v3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(v8 * (field1972 + v9 * field1973)));
                    float v10 = MathHelper.sin((float)(MathHelper.sqrt((float)param1.method2551()) * field1974));
                    v3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(v8 * v10 * field1975));
                    v3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(v10 * -((Float)this.field1937.getValue()).floatValue() * field1976));
                    v3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(v8 * field1977));
                    break;
                }
                case 6: {
                    float v8 = field1978;
                    float v9 = v5 ? field1979 : 1.0f;
                    v3.translate(0.0, 0.0, field1980 * (double)v4);
                    v3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(v4 * -v8));
                    v3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(v9 * v4 * v8));
                    break;
                }
                case 7: {
                    float v8 = v5 ? field1981 : 1.0f;
                    float v9 = field1982;
                    v3.translate(v8 * (field1983 - v4 * field1984), field1985 - v9, field1986 - v4 * field1987);
                    v3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(v8 * (field1988 - field1989 * v4)));
                    v3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(v8 * (field1990 - field1991 * v4)));
                    v3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(field1992 - field1993 * v4));
                    break;
                }
                case 8: {
                    float v8 = MathHelper.sin((float)(param1.method2551() * field1994));
                    float v9 = v5 ? field1995 : 1.0f;
                    v3.translate((double)(v9 * field1996), field1997 - (double)(v8 * ((Float)this.field1937.getValue()).floatValue() / field1998), field1999);
                    v3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(v5 ? field2000 : field2001));
                    v3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(v5 ? field2002 : field2003));
                    v3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(field2004));
                    break;
                }
                case 9: {
                    float v8 = MathHelper.sqrt((float)param1.method2551());
                    float v9 = MathHelper.sin((float)(v8 * field2005));
                    float v10 = MathHelper.sin((float)(param1.method2551() * field2006));
                    float v11 = v5 ? field2007 : 1.0f;
                    v3.translate(v11 * (field2008 - v9 * field2009), field2010 + v9 * field2011, field2012 - v10 * field2013);
                    v3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(v5 ? field2014 : field2015));
                    v3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(v11 * (field2016 + v9 * field2017)));
                    v3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(field2018));
                }
            }
            param1.method2298(true);
        }
    }

    @EventHandler(2)
    public void method584(EventSwingSpeed param1) {
        if (param1.method2501() != Hand.MAIN_HAND) {
            return;
        }
        param1.method2500(((Float)this.field2019.getValue()).intValue());
        param1.method2298(true);
    }

    @EventHandler(2)
    public void method587(EventEquipOffset param1) {
        if (param1.method2544() != SwordAnimations.I.player.getMainArm()) {
            return;
        }
        param1.method2541(-((Float)this.field2028.getValue()).floatValue());
    }

    public SwordAnimations() {
        super(field2029, field2030, Combat.Visuals);
        this.field1937 = new SliderSetting(field2046, field2047, 1.0f, field2048, 1.0f, () -> this.field1918.isMode(field2022) || this.field1918.isMode(field2023) || this.field1918.isMode(field2024) || this.field1918.isMode(field2025) || this.field1918.isMode(field2026) || this.field1918.isMode(field2027));
        this.field2028 = new SliderSetting(field2049, 0.0f, 0.0f, 1.0f, field2050);
        this.field2019 = new SliderSetting(field2051, field2052, 1.0f, field2053, 1.0f);
        this.field1916 = new BooleanSetting(field2054, false);
        this.addSettings(this.field1918, this.field1946, this.field1937, this.field2019, this.field2028, this.field1916);
    }

    static {
        field2029 = "Sword Animations";
        field2030 = "Позволяет изменить анимацию удара";
        field2031 = "Мод";
        field2032 = "Мод 1";
        field2033 = "Мод 1";
        field2034 = "Мод 2";
        field2035 = "Мод 3";
        field2036 = "Мод 4";
        field2037 = "Мод 5";
        field2038 = "Мод 6";
        field2039 = "Мод 7";
        field2040 = "Мод 8";
        field2041 = "Мод 9";
        field2042 = "Мод 10";
        field2043 = "Угол";
        field2044 = 100.0f;
        field2045 = 360.0f;
        field2046 = "Сила взмаха";
        field2047 = 8.0f;
        field2048 = 10.0f;
        field2049 = "Сила взмаха вниз";
        field2050 = 0.1f;
        field2051 = "Плавность взмаха";
        field2052 = 11.0f;
        field2053 = 20.0f;
        field2054 = "Только с активной AttackAura";
        field1917 = (float)Math.PI;
        field1919 = "Мод 1";
        field1920 = "Мод 2";
        field1921 = "Мод 3";
        field1922 = "Мод 4";
        field1923 = "Мод 5";
        field1924 = "Мод 6";
        field1925 = "Мод 7";
        field1926 = "Мод 8";
        field1927 = "Мод 9";
        field1928 = "Мод 10";
        field1929 = -1.0f;
        field1930 = 45.0f;
        field1931 = (float)Math.PI;
        field1932 = 4.0f;
        field1933 = -20.0f;
        field1934 = (float)Math.PI;
        field1935 = -20.0f;
        field1936 = (float)Math.PI;
        field1938 = 10.0f;
        field1939 = 45.0f;
        field1940 = 0.15;
        field1941 = -0.3f;
        field1942 = -90.0f;
        field1943 = 90.0f;
        field1944 = 60.0f;
        field1945 = -60.0f;
        field1947 = 10.0f;
        field1948 = (float)Math.PI;
        field1949 = 10.0f;
        field1950 = 0.15;
        field1951 = -0.3f;
        field1952 = -70.0f;
        field1953 = 70.0f;
        field1954 = 30.0f;
        field1955 = -30.0f;
        field1956 = 10.0f;
        field1957 = -1.0f;
        field1958 = 45.0f;
        field1959 = -20.0f;
        field1960 = -20.0f;
        field1961 = -80.0f;
        field1962 = 0.4f;
        field1963 = 0.2f;
        field1964 = 0.2f;
        field1965 = -0.5f;
        field1966 = 0.08f;
        field1967 = 20.0f;
        field1968 = -80.0f;
        field1969 = 20.0f;
        field1970 = -1.0f;
        field1971 = (float)Math.PI;
        field1972 = 45.0f;
        field1973 = -20.0f;
        field1974 = (float)Math.PI;
        field1975 = -20.0f;
        field1976 = 10.0f;
        field1977 = -45.0f;
        field1978 = 35.0f;
        field1979 = -1.0f;
        field1980 = -0.3;
        field1981 = -1.0f;
        field1982 = 0.14f;
        field1983 = 0.3f;
        field1984 = 0.15f;
        field1985 = 0.2f;
        field1986 = -0.15f;
        field1987 = 0.13f;
        field1988 = 76.0f;
        field1989 = 10.0f;
        field1990 = -16.0f;
        field1991 = 8.0f;
        field1992 = -83.0f;
        field1993 = 26.0f;
        field1994 = (float)Math.PI;
        field1995 = -1.0f;
        field1996 = 0.35f;
        field1997 = 0.15;
        field1998 = 24.0f;
        field1999 = -0.3f;
        field2000 = -90.0f;
        field2001 = 90.0f;
        field2002 = 30.0f;
        field2003 = -30.0f;
        field2004 = -90.0f;
        field2005 = (float)Math.PI;
        field2006 = (float)Math.PI;
        field2007 = -1.0f;
        field2008 = 0.4f;
        field2009 = 0.2f;
        field2010 = -0.2f;
        field2011 = 0.3f;
        field2012 = -0.5f;
        field2013 = 0.2f;
        field2014 = -91.0f;
        field2015 = 91.0f;
        field2016 = -40.0f;
        field2017 = -100.0f;
        field2018 = -60.0f;
        field2022 = "Мод 1";
        field2023 = "Мод 2";
        field2024 = "Мод 3";
        field2025 = "Мод 4";
        field2026 = "Мод 6";
        field2027 = "Мод 10";
        field2020 = "Мод 2";
        field2021 = "Мод 4";
    }
}




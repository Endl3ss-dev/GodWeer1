/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import minced.core.Easing;

public final class Easings
implements Easing {
    @Override
    public double method3420(double p0) { return 0.0; }

    static double cfr_renamed_5;
    static double ц;
    static double И;
    static double 西;
    static double cfr_renamed_6;

    static double field11894;
    static double field11895;
    static double field11896;
    static double field11897;
    static double field11898;
    static double field11899;
    static double field11900;
    static double field11901;
    static Easing field11902;
    static double field11903;
    static double field11904;
    static double field11905;
    static double field11906;
    static double field11907;
    static double field11908;
    static double field11909;
    static double field11910;
    static double field11911;
    static double field11912;
    static double field11913;
    static double field11914;
    static double field11915;
    static double field11916;
    static double field11917;
    static double field11918;
    static double field11919;
    static double field11920;
    static double field11921;
    static double field11922;
    static double field11923;
    static double field11924;
    static double field11925;
    static double field11926;
    static double field11927;
    static double field11928;
    static double field11929;
    static double field11930;
    static double field11931;
    static double field11932;
    static double field11933;
    static double field11934;
    static double field11935;
    static double field11936;
    static double field11937;
    static double field11938;
    static double field11939;
    static double field11940;
    static double field11941;
    static double field11942;
    static double field11943;
    static double field11944;
    static double field11945;
    static double field11946;
    static double field11947;
    static double field11948;
    static double field11949;
    static double field11950;
    static double field11951;
    public static Easing field11952;
    static Easing field11953;
    static Easing I;
    static double field11954;
    static Easing field11955;
    public static Easing field11956;
    public static Easing field11957;
    static double field11958;
    public static Easing field11959;
    static Easing field11960;
    static Easing field11961;
    static double field11962;
    static Easing field11963;
    static Easing field11964;
    static Easing field11965;
    static double field11966;
    static Easing field11967;
    static Easing field11968;
    public static Easing field11969;
    static Easing field11970;
    static Easing field11971;
    static Easing field11972;
    static Easing field11973;
    static Easing field11974;
    static Easing field11975;
    static Easing field11976;
    static Easing field11977;
    static Easing field11978;
    static Easing field11979;
    public static Easing field11980;
    static Easing field11981;
    static Easing field11982;
    static Easing field11983;
    static Easing field11984;
    static double field11985;
    static double field11986;
    static double field11987;
    static double field11988;
    static double field11989;
    static double field11990;
    static double field11991;
    static double field11992;
    static double field11993;
    static double field11994;
    static double field11995;
    static double field11996;
    static double field11997;
    static double field11998;
    static double field11999;
    static double field12000;
    static double field12001;
    static double field12002;
    static double field12003;
    static double field12004;
    static double field12005;
    static double field12006;
    static double field12007;
    static double field12008;
    static double field12009;
    static String field12010;
    static double field12011;
    static double field12012;
    static double field12013;
    static double field12014;
    static double field12015;
    static double field12016;
    static double field12017;
    static double field12018;
    static double field12019;
    static double field12020;
    static double field12021;
    static double field12022;
    static double field12023;
    static double field12024;
    static double field12025;
    static double field12026;
    static double field12027;
    static double field12028;
    static double field12029;
    static double field12030;
    static double field12031;
    static double field12032;

    public static Easing method3398(int param0) {
        return Easings.method3417(param0);
    }

    public static Easing method3401(int param0) {
        return Easings.method3403(param0);
    }

    public static Easing method3403(double param0) {
        return param2 -> Math.pow(param2, param0);
    }

    public static Easing method3410(double param0) {
        return param2 -> param2 < field12028 ? Math.pow(field12029, param0 - 1.0) * Math.pow(param2, param0) : 1.0 - Math.pow(field12030 * param2 + field12031, param0) / field12032;
    }

    public static void method3412() {
        field11952 = param0 -> param0;
        field11953 = Easings.method3401(2);
        I = Easings.method3398(2);
        field11955 = Easings.method3410(field11954);
        field11956 = Easings.method3401(3);
        field11957 = Easings.method3398(3);
        field11959 = Easings.method3410(field11958);
        field11960 = Easings.method3401(4);
        field11961 = Easings.method3398(4);
        field11963 = Easings.method3410(field11962);
        field11964 = Easings.method3401(5);
        field11965 = Easings.method3398(5);
        field11967 = Easings.method3410(field11966);
        field11968 = param0 -> 1.0 - Math.cos(param0 * field11950 / field11951);
        field11969 = param0 -> Math.sin(param0 * field11911 / field11912);
        field11970 = param0 -> -(Math.cos(field12026 * param0) - 1.0) / field12027;
        field11971 = param0 -> 1.0 - Math.sqrt(1.0 - Math.pow(param0, field11900));
        field11972 = param0 -> Math.sqrt(1.0 - Math.pow(param0 - 1.0, field11924));
        field11973 = param0 -> param0 < field11916 ? (1.0 - Math.sqrt(1.0 - Math.pow(field11917 * param0, field11918))) / field11919 : (Math.sqrt(1.0 - Math.pow(field11920 * param0 + field11921, field11922)) + 1.0) / field11923;
        field11974 = param0 -> param0 != 0.0 && param0 != 1.0 ? Math.pow(field11944, field11945 * param0 - field11946) * Math.sin((param0 * field11947 - field11948) * field11949) : param0;
        field11975 = param0 -> param0 != 0.0 && param0 != 1.0 ? Math.pow(field11939, field11940 * param0) * Math.sin((param0 * field11941 - field11942) * field11943) + 1.0 : param0;
        field11976 = param0 -> {
            if (param0 != 0.0 && param0 != 1.0) {
                return param0 < field12011 ? -(Math.pow(field12012, field12013 * param0 - field12014) * Math.sin((field12015 * param0 - field12016) * field12017)) / field12018 : Math.pow(field12019, field12020 * param0 + field12021) * Math.sin((field12022 * param0 - field12023) * field12024) / field12025 + 1.0;
            }
            return param0;
        };
        field11977 = param0 -> param0 != 0.0 ? Math.pow(field11913, field11914 * param0 - field11915) : param0;
        field11978 = param0 -> param0 != 1.0 ? 1.0 - Math.pow(field11894, field11895 * param0) : param0;
        field11979 = param0 -> {
            if (param0 != 0.0 && param0 != 1.0) {
                return param0 < field11985 ? Math.pow(field11986, field11987 * param0 - field11988) / field11989 : (field11990 - Math.pow(field11991, field11992 * param0 + field11993)) / field11994;
            }
            return param0;
        };
        field11980 = param0 -> field11896 * Math.pow(param0, field11897) - field11898 * Math.pow(param0, field11899);
        field11981 = param0 -> 1.0 + field11907 * Math.pow(param0 - 1.0, field11908) + field11909 * Math.pow(param0 - 1.0, field11910);
        field11982 = param0 -> param0 < field11995 ? Math.pow(field11996 * param0, field11997) * (field11998 * param0 - field11999) / field12000 : (Math.pow(field12001 * param0 - field12002, field12003) * (field12004 * (param0 * field12005 - field12006) + field12007) + field12008) / field12009;
        field11902 = param0 -> {
            double v2 = field11925;
            double v4 = field11926;
            if (param0 < v4) {
                return v2 * Math.pow(param0, field11927);
            }
            if (param0 < field11928 / v4) {
                return v2 * Math.pow(param0 - field11929 / v4, field11930) + field11931;
            }
            return param0 < field11932 / v4 ? v2 * Math.pow(param0 - field11933 / v4, field11934) + field11935 : v2 * Math.pow(param0 - field11936 / v4, field11937) + field11938;
        };
        field11983 = param0 -> 1.0 - field11902.method3420(1.0 - param0);
        field11984 = param0 -> param0 < field11901 ? (1.0 - field11902.method3420(1.0 - field11903 * param0)) / field11904 : (1.0 + field11902.method3420(field11905 * param0 - 1.0)) / field11906;
    }

    private Easings() {
        throw new UnsupportedOperationException(field12010);
    }

    public static Easing method3417(double param0) {
        return param2 -> 1.0 - Math.pow(1.0 - param2, param0);
    }

    static {
        cfr_renamed_5 = 0.0;
        ц = 0.0;
        И = 0.0;
        西 = 0.0;
        cfr_renamed_6 = 0.0;
        field12010 = "This is a utility class and cannot be instantiated";
        field12028 = 0.5;
        field12029 = 2.0;
        field12030 = -2.0;
        field12031 = 2.0;
        field12032 = 2.0;
        field11901 = 0.5;
        field11903 = 2.0;
        field11904 = 2.0;
        field11905 = 2.0;
        field11906 = 2.0;
        field11925 = 7.5625;
        field11926 = 2.75;
        field11927 = 2.0;
        field11928 = 2.0;
        field11929 = 1.5;
        field11930 = 2.0;
        field11931 = 0.75;
        field11932 = 2.5;
        field11933 = 2.25;
        field11934 = 2.0;
        field11935 = 0.9375;
        field11936 = 2.625;
        field11937 = 2.0;
        field11938 = 0.984375;
        field11995 = 0.5;
        field11996 = 2.0;
        field11997 = 2.0;
        field11998 = 7.189819;
        field11999 = 2.5949095;
        field12000 = 2.0;
        field12001 = 2.0;
        field12002 = 2.0;
        field12003 = 2.0;
        field12004 = 3.5949095;
        field12005 = 2.0;
        field12006 = 2.0;
        field12007 = 2.5949095;
        field12008 = 2.0;
        field12009 = 2.0;
        field11907 = 2.70158;
        field11908 = 3.0;
        field11909 = 1.70158;
        field11910 = 2.0;
        field11896 = 2.70158;
        field11897 = 3.0;
        field11898 = 1.70158;
        field11899 = 2.0;
        field11985 = 0.5;
        field11986 = 2.0;
        field11987 = 20.0;
        field11988 = 10.0;
        field11989 = 2.0;
        field11990 = 2.0;
        field11991 = 2.0;
        field11992 = -20.0;
        field11993 = 10.0;
        field11994 = 2.0;
        field11894 = 2.0;
        field11895 = -10.0;
        field11913 = 2.0;
        field11914 = 10.0;
        field11915 = 10.0;
        field12011 = 0.5;
        field12012 = 2.0;
        field12013 = 20.0;
        field12014 = 10.0;
        field12015 = 20.0;
        field12016 = 11.125;
        field12017 = 1.3962634015954636;
        field12018 = 2.0;
        field12019 = 2.0;
        field12020 = -20.0;
        field12021 = 10.0;
        field12022 = 20.0;
        field12023 = 11.125;
        field12024 = 1.3962634015954636;
        field12025 = 2.0;
        field11939 = 2.0;
        field11940 = -10.0;
        field11941 = 10.0;
        field11942 = 0.75;
        field11943 = 2.0943951023931953;
        field11944 = -2.0;
        field11945 = 10.0;
        field11946 = 10.0;
        field11947 = 10.0;
        field11948 = 10.75;
        field11949 = 2.0943951023931953;
        field11916 = 0.5;
        field11917 = 2.0;
        field11918 = 2.0;
        field11919 = 2.0;
        field11920 = -2.0;
        field11921 = 2.0;
        field11922 = 2.0;
        field11923 = 2.0;
        field11924 = 2.0;
        field11900 = 2.0;
        field12026 = Math.PI;
        field12027 = 2.0;
        field11911 = Math.PI;
        field11912 = 2.0;
        field11950 = Math.PI;
        field11951 = 2.0;
        field11954 = 2.0;
        field11958 = 3.0;
        field11962 = 4.0;
        field11966 = 5.0;
        Easings.method3412();
    }
}




/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import minced.core.EasingFunction;

public final class EasingConstants
implements EasingFunction {
    @Override
    public double O(double p0) { return 0.0; }

    static double ш;
    static double А;
    static double Я;
    static double ch;
    static double Ж;

    static double field11755;
    static double field11756;
    static double field11757;
    static double field11758;
    static double field11759;
    static double field11760;
    static double field11761;
    static double field11762;
    static double field11763;
    static double field11764;
    static double field11765;
    static double field11766;
    static double field11767;
    static double field11768;
    static double field11769;
    static double field11770;
    static double field11771;
    static double field11772;
    static double field11773;
    static double field11774;
    static EasingFunction field11775;
    static EasingFunction field11776;
    static EasingFunction field11777;
    static double field11778;
    static EasingFunction I;
    static EasingFunction field11779;
    static EasingFunction field11780;
    static double field11781;
    static EasingFunction field11782;
    static EasingFunction field11783;
    static EasingFunction field11784;
    static double field11785;
    static EasingFunction field11786;
    static EasingFunction field11787;
    static EasingFunction field11788;
    static double field11789;
    static EasingFunction field11790;
    static EasingFunction field11791;
    static EasingFunction field11792;
    static EasingFunction field11793;
    static EasingFunction field11794;
    static EasingFunction field11795;
    static EasingFunction field11796;
    static EasingFunction field11797;
    static EasingFunction field11798;
    static EasingFunction field11799;
    static EasingFunction field11800;
    public static EasingFunction field11801;
    static EasingFunction field11802;
    static EasingFunction field11803;
    static EasingFunction field11804;
    static EasingFunction field11805;
    static EasingFunction field11806;
    static EasingFunction field11807;
    static EasingFunction field11808;
    static double field11809;
    static double field11810;
    static double field11811;
    static double field11812;
    static double field11813;
    static double field11814;
    static double field11815;
    static double field11816;
    static double field11817;
    static String field11818;
    static double field11819;
    static double field11820;
    static double field11821;
    static double field11822;
    static double field11823;
    static double field11824;
    static double field11825;
    static double field11826;
    static double field11827;
    static double field11828;
    static double field11829;
    static double field11830;
    static double field11831;
    static double field11832;
    static double field11833;
    static double field11834;
    static double field11835;
    static double field11836;
    static double field11837;
    static double field11838;
    static double field11839;
    static double field11840;
    static double field11841;
    static double field11842;
    static double field11843;
    static double field11844;
    static double field11845;
    static double field11846;
    static double field11847;
    static double field11848;
    static double field11849;
    static double field11850;
    static double field11851;
    static double field11852;
    static double field11853;
    static double field11854;
    static double field11855;
    static double field11856;
    static double field11857;
    static double field11858;
    static double field11859;
    static double field11860;
    static double field11861;
    static double field11862;
    static double field11863;
    static double field11864;
    static double field11865;
    static double field11866;
    static double field11867;
    static double field11868;
    static double field11869;
    static double field11870;
    static double field11871;
    static double field11872;
    static double field11873;
    static double field11874;
    static double field11875;
    static double field11876;
    static double field11877;
    static double field11878;
    static double field11879;
    static double field11880;
    static double field11881;
    static double field11882;
    static double field11883;
    static double field11884;
    static double field11885;
    static double field11886;
    static double field11887;
    static double field11888;
    static double field11889;
    static double field11890;
    static double field11891;
    static double field11892;
    static double field11893;

    public static void method3372() {
        field11775 = param0 -> param0;
        field11776 = EasingConstants.cfr_underscore(2);
        field11777 = EasingConstants.method3377(2);
        I = EasingConstants.method3379(field11778);
        field11779 = EasingConstants.cfr_underscore(3);
        field11780 = EasingConstants.method3377(3);
        field11782 = EasingConstants.method3379(field11781);
        field11783 = EasingConstants.cfr_underscore(4);
        field11784 = EasingConstants.method3377(4);
        field11786 = EasingConstants.method3379(field11785);
        field11787 = EasingConstants.cfr_underscore(5);
        field11788 = EasingConstants.method3377(5);
        field11790 = EasingConstants.method3379(field11789);
        field11791 = param0 -> 1.0 - Math.cos(param0 * field11819 / field11820);
        field11792 = param0 -> Math.sin(param0 * field11816 / field11817);
        field11793 = param0 -> -(Math.cos(field11773 * param0) - 1.0) / field11774;
        field11794 = param0 -> 1.0 - Math.sqrt(1.0 - Math.pow(param0, field11873));
        field11795 = param0 -> Math.sqrt(1.0 - Math.pow(param0 - 1.0, field11857));
        field11796 = param0 -> param0 < field11761 ? (1.0 - Math.sqrt(1.0 - Math.pow(field11762 * param0, field11763))) / field11764 : (Math.sqrt(1.0 - Math.pow(field11765 * param0 + field11766, field11767)) + 1.0) / field11768;
        field11797 = param0 -> param0 != 0.0 && param0 != 1.0 ? Math.pow(field11755, field11756 * param0 - field11757) * Math.sin((param0 * field11758 - field11759) * field11760) : param0;
        field11798 = param0 -> param0 != 0.0 && param0 != 1.0 ? Math.pow(field11842, field11843 * param0) * Math.sin((param0 * field11844 - field11845) * field11846) + 1.0 : param0;
        field11799 = param0 -> {
            if (param0 != 0.0 && param0 != 1.0) {
                return param0 < field11858 ? -(Math.pow(field11859, field11860 * param0 - field11861) * Math.sin((field11862 * param0 - field11863) * field11864)) / field11865 : Math.pow(field11866, field11867 * param0 + field11868) * Math.sin((field11869 * param0 - field11870) * field11871) / field11872 + 1.0;
            }
            return param0;
        };
        field11800 = param0 -> param0 != 0.0 ? Math.pow(field11821, field11822 * param0 - field11823) : param0;
        field11801 = param0 -> param0 != 1.0 ? 1.0 - Math.pow(field11814, field11815 * param0) : param0;
        field11802 = param0 -> {
            if (param0 != 0.0 && param0 != 1.0) {
                return param0 < field11847 ? Math.pow(field11848, field11849 * param0 - field11850) / field11851 : (field11852 - Math.pow(field11853, field11854 * param0 + field11855)) / field11856;
            }
            return param0;
        };
        field11803 = param0 -> field11838 * Math.pow(param0, field11839) - field11840 * Math.pow(param0, field11841);
        field11804 = param0 -> 1.0 + field11769 * Math.pow(param0 - 1.0, field11770) + field11771 * Math.pow(param0 - 1.0, field11772);
        field11805 = param0 -> param0 < field11874 ? Math.pow(field11875 * param0, field11876) * (field11877 * param0 - field11878) / field11879 : (Math.pow(field11880 * param0 - field11881, field11882) * (field11883 * (param0 * field11884 - field11885) + field11886) + field11887) / field11888;
        field11806 = param0 -> {
            double v2 = field11824;
            double v4 = field11825;
            if (param0 < v4) {
                return v2 * Math.pow(param0, field11826);
            }
            if (param0 < field11827 / v4) {
                return v2 * Math.pow(param0 - field11828 / v4, field11829) + field11830;
            }
            return param0 < field11831 / v4 ? v2 * Math.pow(param0 - field11832 / v4, field11833) + field11834 : v2 * Math.pow(param0 - field11835 / v4, field11836) + field11837;
        };
        field11807 = param0 -> 1.0 - field11806.O(1.0 - param0);
        field11808 = param0 -> param0 < field11889 ? (1.0 - field11806.O(1.0 - field11890 * param0)) / field11891 : (1.0 + field11806.O(field11892 * param0 - 1.0)) / field11893;
    }

    public static EasingFunction method3375(double param0) {
        return param2 -> 1.0 - Math.pow(1.0 - param2, param0);
    }

    private EasingConstants() {
        throw new UnsupportedOperationException(field11818);
    }

    public static EasingFunction cfr_underscore(double param0) {
        return param2 -> Math.pow(param2, param0);
    }

    public static EasingFunction method3377(int param0) {
        return EasingConstants.method3375(param0);
    }

    public static EasingFunction method3379(double param0) {
        return param2 -> param2 < field11809 ? Math.pow(field11810, param0 - 1.0) * Math.pow(param2, param0) : 1.0 - Math.pow(field11811 * param2 + field11812, param0) / field11813;
    }

    public static EasingFunction cfr_underscore(int param0) {
        return EasingConstants.cfr_underscore((double)param0);
    }

    static {
        ш = 0.0;
        А = 0.0;
        Я = 0.0;
        ch = 0.0;
        Ж = 0.0;
        field11818 = "This is a utility class and cannot be instantiated";
        field11809 = 0.5;
        field11810 = 2.0;
        field11811 = -2.0;
        field11812 = 2.0;
        field11813 = 2.0;
        field11889 = 0.5;
        field11890 = 2.0;
        field11891 = 2.0;
        field11892 = 2.0;
        field11893 = 2.0;
        field11824 = 7.5625;
        field11825 = 2.75;
        field11826 = 2.0;
        field11827 = 2.0;
        field11828 = 1.5;
        field11829 = 2.0;
        field11830 = 0.75;
        field11831 = 2.5;
        field11832 = 2.25;
        field11833 = 2.0;
        field11834 = 0.9375;
        field11835 = 2.625;
        field11836 = 2.0;
        field11837 = 0.984375;
        field11874 = 0.5;
        field11875 = 2.0;
        field11876 = 2.0;
        field11877 = 7.189819;
        field11878 = 2.5949095;
        field11879 = 2.0;
        field11880 = 2.0;
        field11881 = 2.0;
        field11882 = 2.0;
        field11883 = 3.5949095;
        field11884 = 2.0;
        field11885 = 2.0;
        field11886 = 2.5949095;
        field11887 = 2.0;
        field11888 = 2.0;
        field11769 = 2.70158;
        field11770 = 3.0;
        field11771 = 1.70158;
        field11772 = 2.0;
        field11838 = 2.70158;
        field11839 = 3.0;
        field11840 = 1.70158;
        field11841 = 2.0;
        field11847 = 0.5;
        field11848 = 2.0;
        field11849 = 20.0;
        field11850 = 10.0;
        field11851 = 2.0;
        field11852 = 2.0;
        field11853 = 2.0;
        field11854 = -20.0;
        field11855 = 10.0;
        field11856 = 2.0;
        field11814 = 2.0;
        field11815 = -10.0;
        field11821 = 2.0;
        field11822 = 10.0;
        field11823 = 10.0;
        field11858 = 0.5;
        field11859 = 2.0;
        field11860 = 20.0;
        field11861 = 10.0;
        field11862 = 20.0;
        field11863 = 11.125;
        field11864 = 1.3962634015954636;
        field11865 = 2.0;
        field11866 = 2.0;
        field11867 = -20.0;
        field11868 = 10.0;
        field11869 = 20.0;
        field11870 = 11.125;
        field11871 = 1.3962634015954636;
        field11872 = 2.0;
        field11842 = 2.0;
        field11843 = -10.0;
        field11844 = 10.0;
        field11845 = 0.75;
        field11846 = 2.0943951023931953;
        field11755 = -2.0;
        field11756 = 10.0;
        field11757 = 10.0;
        field11758 = 10.0;
        field11759 = 10.75;
        field11760 = 2.0943951023931953;
        field11761 = 0.5;
        field11762 = 2.0;
        field11763 = 2.0;
        field11764 = 2.0;
        field11765 = -2.0;
        field11766 = 2.0;
        field11767 = 2.0;
        field11768 = 2.0;
        field11857 = 2.0;
        field11873 = 2.0;
        field11773 = Math.PI;
        field11774 = 2.0;
        field11816 = Math.PI;
        field11817 = 2.0;
        field11819 = Math.PI;
        field11820 = 2.0;
        field11778 = 2.0;
        field11781 = 3.0;
        field11785 = 4.0;
        field11789 = 5.0;
        EasingConstants.method3372();
    }
}




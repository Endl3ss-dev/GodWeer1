/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.MathHelper
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.ClientUtil;
import minced.core.Minced;
import minced.module.visual.Emotions;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public final class BipedModelAnimator
implements CancellableEvent {
    static float field8774;
    static float field8775;
    static float field8776;
    static float field8777;
    static float field8778;
    static float field8779;
    static float field8780;
    static float field8781;
    static float field8782;
    static float field8783;
    static float field8784;
    static long field8785;
    static float field8786;
    static float field8787;
    static float field8788;
    static float field8789;
    static float field8790;
    static float field8791;
    static float field8792;
    static float field8793;
    static float field8794;
    static float field8795;
    static float field8796;
    static float field8797;
    static float field8798;
    static float field8799;
    static float field8800;
    static float field8801;
    static float field8802;
    static float field8803;
    static float field8804;
    static float field8805;
    static float field8806;
    static float field8807;
    static float field8808;
    static float field8809;
    static float field8810;
    static float field8811;
    static float field8812;
    static float field8813;
    static float field8814;
    static float field8815;
    static float field8816;
    static float field8817;
    static float field8818;
    static float field8819;
    static float field8820;
    static float field8821;
    static float field8822;
    static float field8823;
    static float field8824;
    static float field8825;
    static float field8826;
    static float field8827;
    static float field8828;
    static float field8829;
    static float field8830;
    static float field8831;
    static float field8832;
    static float field8833;
    static float field8834;
    static float field8835;
    static float field8836;
    static float field8837;
    static float field8838;
    static float field8839;
    static float field8840;
    static float field8841;
    static float field8842;
    static float field8843;
    static float field8844;
    static float field8845;
    static float field8846;
    static float field8847;
    static float field8848;
    static float field8849;
    static float field8850;
    static float field8851;
    static float field8852;
    static String field8853;
    static String field8854;
    static String field8855;
    static String field8856;
    static String field8857;
    static float field8858;
    static float field8859;
    static float field8860;
    static float field8861;
    static float field8862;
    static float field8863;
    static float field8864;
    static float field8865;
    static float field8866;
    static float field8867;
    static float field8868;
    static float field8869;
    static float field8870;
    static long field8871;
    static float field8872;
    static String field8873;
    static String field8874;
    static String field8875;
    static String field8876;
    static String field8877;
    static float field8878;
    static float field8879;
    static float field8880;
    static float field8881;
    static float field8882;
    static float field8883;
    static float field8884;
    static float field8885;
    static float field8886;
    static long field8887;
    static float field8888;
    static float field8889;
    static float field8890;
    static float field8891;
    static float field8892;
    static float field8893;
    static float field8894;
    static float field8895;
    static float field8896;
    static float field8897;
    static float field8898;
    static float field8899;
    static float field8900;
    static float field8901;
    static float field8902;
    static float field8903;
    static float field8904;
    static float field8905;
    static float field8906;
    static float field8907;
    static float field8908;
    static float field8909;
    static float field8910;
    static float field8911;
    static float field8912;
    static float field8913;
    static float field8914;
    static float field8915;
    static float field8916;
    static float field8917;
    static float field8918;
    static float field8919;
    static float field8920;
    static float field8921;
    static float field8922;
    static float field8923;
    static float field8924;
    static float field8925;
    static float field8926;
    static float field8927;
    static float field8928;
    static float field8929;
    static float field8930;
    static float field8931;
    static float field8932;
    static float field8933;
    static float field8934;
    static float field8935;
    static float field8936;
    static float field8937;
    static float field8938;
    static float field8939;
    static float field8940;

    private BipedModelAnimator() {
    }

    private static void method2435(BipedEntityModel param0, float param1, float param2, float param3) {
        float v5;
        float v4;
        param0.rightArm.pitch = MathHelper.cos((float)(param1 * field8774 + field8775)) * 2.0f * param2 / param3;
        param0.leftArm.pitch = MathHelper.cos((float)(param1 * field8776)) * 2.0f * param2 / param3;
        param0.rightArm.roll = v4 = (MathHelper.cos((float)(param1 * field8777)) + 1.0f) * 1.0f * param2 / param3;
        param0.leftArm.roll = -v4;
        param0.rightArm.yaw = 0.0f;
        param0.leftArm.yaw = 0.0f;
        param0.rightLeg.pitch = MathHelper.cos((float)(param1 * field8778)) * field8779 * param2 / param3;
        param0.leftLeg.pitch = MathHelper.cos((float)(param1 * field8780 + field8781)) * field8782 * param2 / param3;
        param0.rightLeg.roll = v5 = MathHelper.cos((float)(param1 * field8783)) * field8784 * param2 / param3;
        param0.leftLeg.roll = -v5;
        param0.body.yaw = 0.0f;
        param0.body.pitch = 0.0f;
        param0.body.roll = 0.0f;
    }

    private static void method2436(BipedEntityModel param0, float param1, float param2, float param3) {
        float v4 = (float)(System.currentTimeMillis() % field8785) / field8786;
        float v5 = Math.max(field8787, param2);
        param0.body.pitch = field8788 + MathHelper.sin((float)(v4 * field8789)) * field8790;
        param0.body.roll = MathHelper.sin((float)(v4 * field8791)) * field8792;
        param0.body.yaw = MathHelper.sin((float)(v4 * field8793)) * field8794;
        param0.head.pitch = field8795 + MathHelper.sin((float)(v4 * field8796)) * field8797;
        param0.head.roll = MathHelper.sin((float)(v4 * field8798)) * field8799;
        param0.head.yaw = MathHelper.sin((float)(v4 * field8800)) * field8801;
        float v6 = field8802;
        float v7 = v4 % v6 / v6;
        if (v7 < field8803) {
            float v8 = v7 / field8804;
            float v9 = MathHelper.sin((float)(v8 * field8805));
            param0.rightArm.pitch = field8806 - v9 * field8807;
            param0.leftArm.pitch = field8808 - v9 * field8809;
            param0.rightArm.yaw = field8810 * v9;
            param0.leftArm.yaw = field8811 * v9;
            param0.rightArm.roll = field8812 * v9;
            param0.leftArm.roll = field8813 * v9;
        } else {
            float v8 = v4 * 2.0f;
            param0.rightArm.pitch = field8814 + MathHelper.sin((float)v8) * field8815;
            param0.leftArm.pitch = field8816 + MathHelper.sin((float)(v8 + field8817)) * field8818;
            param0.rightArm.roll = field8819 + MathHelper.cos((float)(v8 * field8820)) * field8821;
            param0.leftArm.roll = field8822 + MathHelper.cos((float)(v8 * field8823)) * field8824;
            param0.rightArm.yaw = field8825 + MathHelper.sin((float)v8) * field8826;
            param0.leftArm.yaw = field8827 + MathHelper.sin((float)v8) * field8828;
        }
        param0.rightLeg.pitch = field8829 + MathHelper.cos((float)(param1 * field8830)) * field8831 * v5 / param3 + MathHelper.sin((float)(v4 * field8832)) * field8833;
        param0.leftLeg.pitch = field8834 + MathHelper.cos((float)(param1 * field8835 + field8836)) * field8837 * v5 / param3 + MathHelper.sin((float)(v4 * field8838)) * field8839;
        param0.rightLeg.roll = field8840 + MathHelper.sin((float)(v4 * field8841)) * field8842;
        param0.leftLeg.roll = field8843 - MathHelper.sin((float)(v4 * field8844)) * field8845;
    }

    public static void method2437(BipedEntityModel param0, BipedEntityRenderState param1, PlayerEntity param2) {
        Emotions v3 = Emotions.method1578();
        if (v3 == null) {
            return;
        }
        String v4 = v3.method1581();
        if (v4 != null) {
            if (!v3.isEnabled()) {
                return;
            }
            BipedModelAnimator.method2438(param0);
            BipedModelAnimator.method2439(param0, v4, param1);
            param0.hat.copyTransform(param0.head);
            return;
        }
        if (!v3.isEnabled() || v3.method1572() || ClientUtil.I.player == null) {
            return;
        }
        if (!BipedModelAnimator.method2441(v3, param2)) {
            return;
        }
        float v5 = param1.limbFrequency;
        float v6 = param1.limbAmplitudeMultiplier;
        float v7 = Math.max(1.0f, (float)param2.getVelocity().lengthSquared() / field8846);
        v7 = v7 * v7 * v7;
        BipedModelAnimator.method2440(v3.method1574(), param0, v5, v6, v7);
        param0.hat.copyTransform(param0.head);
    }

    private static void method2438(BipedEntityModel param0) {
        ModelPart v1 = param0.head;
        ModelPart v2 = param0.body;
        ModelPart v3 = param0.rightArm;
        ModelPart v4 = param0.leftArm;
        ModelPart v5 = param0.rightLeg;
        ModelPart v6 = param0.leftLeg;
        v1.setAngles(0.0f, 0.0f, 0.0f);
        v2.setAngles(0.0f, 0.0f, 0.0f);
        v3.setAngles(0.0f, 0.0f, 0.0f);
        v4.setAngles(0.0f, 0.0f, 0.0f);
        v5.setAngles(0.0f, 0.0f, 0.0f);
        v6.setAngles(0.0f, 0.0f, 0.0f);
        v1.pivotY = 0.0f;
        v2.pivotY = 0.0f;
        v3.pivotX = field8847;
        v3.pivotY = 2.0f;
        v3.pivotZ = 0.0f;
        v4.pivotX = field8848;
        v4.pivotY = 2.0f;
        v4.pivotZ = 0.0f;
        v5.pivotY = field8849;
        v5.pivotZ = field8850;
        v6.pivotY = field8851;
        v6.pivotZ = field8852;
    }

    private static void method2439(BipedEntityModel param0, String param1, BipedEntityRenderState param2) {
        float v3 = param2.limbFrequency;
        float v4 = param2.limbAmplitudeMultiplier;
        int v6 = -1;
        switch (param1.hashCode()) {
            case 814437702: {
                if (!param1.equals(field8853)) break;
                v6 = 0;
                break;
            }
            case 1010098336: {
                if (!param1.equals(field8854)) break;
                v6 = 1;
                break;
            }
            case 862293355: {
                if (!param1.equals(field8855)) break;
                v6 = 2;
                break;
            }
            case 1732330147: {
                if (!param1.equals(field8856)) break;
                v6 = 3;
                break;
            }
            case -1826410863: {
                if (!param1.equals(field8857)) break;
                v6 = 4;
            }
        }
        switch (v6) {
            case 0: {
                param0.rightArm.pitch = field8858;
                param0.rightArm.roll = field8859;
                break;
            }
            case 1: {
                param0.body.yaw = field8860;
                param0.head.yaw = field8861;
                param0.rightArm.pitch = field8862;
                param0.rightArm.roll = field8863;
                param0.leftArm.pitch = field8864;
                param0.leftArm.roll = field8865;
                param0.rightLeg.pitch = field8866;
                param0.leftLeg.pitch = field8867;
                break;
            }
            case 2: {
                param0.rightArm.pitch = field8868;
                param0.rightArm.roll = field8869;
                param0.head.pitch = field8870;
                break;
            }
            case 3: {
                BipedModelAnimator.method2435(param0, v3, v4, 1.0f);
                break;
            }
            case 4: {
                BipedModelAnimator.method2436(param0, v3, v4, 1.0f);
            }
        }
    }

    private static void method2440(String param0, BipedEntityModel param1, float param2, float param3, float param4) {
        float v5 = (float)(System.currentTimeMillis() % field8871) / field8872;
        int v7 = -1;
        switch (param0.hashCode()) {
            case 814437702: {
                if (!param0.equals(field8873)) break;
                v7 = 0;
                break;
            }
            case 1010098336: {
                if (!param0.equals(field8874)) break;
                v7 = 1;
                break;
            }
            case 862293355: {
                if (!param0.equals(field8875)) break;
                v7 = 2;
                break;
            }
            case 1732330147: {
                if (!param0.equals(field8876)) break;
                v7 = 3;
                break;
            }
            case -1826410863: {
                if (!param0.equals(field8877)) break;
                v7 = 4;
            }
        }
        switch (v7) {
            case 0: {
                float v8 = field8878;
                float v9 = field8879;
                param1.rightArm.pitch = field8880;
                param1.rightArm.yaw = MathHelper.sin((float)(v5 * v8)) * v9;
                param1.rightArm.roll = field8881 + MathHelper.sin((float)(v5 * v8)) * v9;
                param1.leftArm.pitch = 0.0f;
                param1.rightLeg.pitch = MathHelper.cos((float)(param2 * field8882)) * field8883 * param3 / param4;
                param1.leftLeg.pitch = MathHelper.cos((float)(param2 * field8884 + field8885)) * field8886 * param3 / param4;
                param1.head.pitch = 0.0f;
                break;
            }
            case 1: {
                float v10;
                float v8 = (float)(System.currentTimeMillis() % field8887) / field8888;
                param1.body.yaw = MathHelper.sin((float)(v8 * 2.0f)) * field8889;
                param1.body.pitch = MathHelper.sin((float)(v8 * field8890)) * field8891;
                param1.head.yaw = MathHelper.sin((float)(v8 * field8892)) * field8893;
                param1.head.pitch = MathHelper.sin((float)(v8 * field8894)) * field8895;
                param1.rightArm.pitch = field8896 + MathHelper.sin((float)(v8 * field8897)) * field8898;
                param1.rightArm.yaw = MathHelper.sin((float)(v8 * field8899)) * field8900;
                param1.rightArm.roll = MathHelper.sin((float)(v8 * field8901)) * field8902;
                param1.leftArm.pitch = field8903 + MathHelper.sin((float)(v8 * field8904 + field8905)) * field8906;
                param1.leftArm.yaw = MathHelper.sin((float)(v8 * field8907 + field8908)) * field8909;
                param1.leftArm.roll = MathHelper.sin((float)(v8 * field8910 + field8911)) * field8912;
                param1.rightLeg.pitch = field8913 + MathHelper.sin((float)(v8 * 2.0f)) * field8914;
                param1.leftLeg.pitch = field8915 + MathHelper.sin((float)(v8 * 2.0f + field8916)) * field8917;
                float v9 = v8 % field8918;
                if (v9 > field8919 && v9 < field8920) {
                    v10 = v9 - field8921;
                    param1.body.pivotY = v10 * 2.0f;
                    param1.rightLeg.pivotY = field8922 + v10 * 2.0f;
                    param1.leftLeg.pivotY = field8923 + v10 * 2.0f;
                    param1.rightArm.pivotY = 2.0f + v10 * 2.0f;
                    param1.leftArm.pivotY = 2.0f + v10 * 2.0f;
                } else {
                    param1.body.pivotY = 0.0f;
                    param1.rightLeg.pivotY = field8924;
                    param1.leftLeg.pivotY = field8925;
                    param1.rightArm.pivotY = 2.0f;
                    param1.leftArm.pivotY = 2.0f;
                }
                if (!(v9 > field8926) || !(v9 < field8927)) break;
                v10 = MathHelper.sin((float)((v9 - field8928) * field8929 * 2.0f));
                param1.body.pivotY = -v10 * field8930;
                param1.head.pivotY = v10 * field8931;
                break;
            }
            case 2: {
                float v8 = field8932;
                float v9 = field8933;
                param1.rightArm.pitch = field8934 + MathHelper.sin((float)(v5 * v8)) * v9;
                param1.rightArm.yaw = MathHelper.sin((float)(v5 * v8)) * v9;
                param1.rightArm.roll = field8935 + MathHelper.sin((float)(v5 * v8)) * v9;
                param1.leftArm.pitch = 0.0f;
                param1.rightLeg.pitch = MathHelper.cos((float)(param2 * field8936)) * field8937 * param3 / param4;
                param1.leftLeg.pitch = MathHelper.cos((float)(param2 * field8938 + field8939)) * field8940 * param3 / param4;
                param1.head.pitch = 0.0f;
                break;
            }
            case 3: {
                BipedModelAnimator.method2435(param1, param2, param3, param4);
                break;
            }
            case 4: {
                BipedModelAnimator.method2436(param1, param2, param3, param4);
            }
        }
    }

    private static boolean method2441(Emotions param0, PlayerEntity param1) {
        boolean v2;
        boolean bl = v2 = param1 == ClientUtil.I.player;
        if (v2 && ((Boolean)param0.method1573().getValue()).booleanValue()) {
            return true;
        }
        String v3 = param1.getGameProfile().getName();
        if (!v2 && ((Boolean)param0.method1571().getValue()).booleanValue() && Minced.getInstance().getFriendManager().cfr_renamed_4(v3)) {
            return true;
        }
        return !v2 && (Boolean)param0.method1579().getValue() != false;
    }

    static {
        field8846 = 0.2f;
        field8847 = -5.0f;
        field8848 = 5.0f;
        field8849 = 12.0f;
        field8850 = 0.1f;
        field8851 = 12.0f;
        field8852 = 0.1f;
        field8853 = "Приветствие";
        field8854 = "Танец";
        field8855 = "Дрочка";
        field8856 = "Альфа ходьба";
        field8857 = "Альфа-Мужик";
        field8858 = -3.0f;
        field8859 = -0.5f;
        field8860 = 0.2f;
        field8861 = 0.3f;
        field8862 = -1.5f;
        field8863 = 0.5f;
        field8864 = -1.2f;
        field8865 = -0.5f;
        field8866 = 0.3f;
        field8867 = -0.3f;
        field8868 = -0.3f;
        field8869 = -0.5f;
        field8870 = 0.3f;
        field8871 = 10000L;
        field8872 = 1000.0f;
        field8873 = "Приветствие";
        field8874 = "Танец";
        field8875 = "Дрочка";
        field8876 = "Альфа ходьба";
        field8877 = "Альфа-Мужик";
        field8878 = 10.0f;
        field8879 = 0.2f;
        field8880 = -3.0f;
        field8881 = -0.5f;
        field8882 = 0.6662f;
        field8883 = 1.4f;
        field8884 = 0.6662f;
        field8885 = (float)Math.PI;
        field8886 = 1.4f;
        field8887 = 6000L;
        field8888 = 1000.0f;
        field8889 = 0.3f;
        field8890 = 1.5f;
        field8891 = 0.1f;
        field8892 = 1.8f;
        field8893 = 0.4f;
        field8894 = 2.2f;
        field8895 = 0.2f;
        field8896 = -1.5f;
        field8897 = 3.0f;
        field8898 = 0.8f;
        field8899 = 1.5f;
        field8900 = 0.5f;
        field8901 = 2.5f;
        field8902 = 0.7f;
        field8903 = -1.5f;
        field8904 = 3.0f;
        field8905 = (float)Math.PI;
        field8906 = 0.8f;
        field8907 = 1.5f;
        field8908 = (float)Math.PI;
        field8909 = 0.5f;
        field8910 = 2.5f;
        field8911 = (float)Math.PI;
        field8912 = 0.7f;
        field8913 = 0.5f;
        field8914 = 0.6f;
        field8915 = 0.5f;
        field8916 = (float)Math.PI;
        field8917 = 0.6f;
        field8918 = 6.0f;
        field8919 = 3.0f;
        field8920 = 4.0f;
        field8921 = 3.0f;
        field8922 = 12.0f;
        field8923 = 12.0f;
        field8924 = 12.0f;
        field8925 = 12.0f;
        field8926 = 4.5f;
        field8927 = 5.0f;
        field8928 = 4.5f;
        field8929 = (float)Math.PI;
        field8930 = 1.5f;
        field8931 = 1.5f;
        field8932 = 30.0f;
        field8933 = 0.2f;
        field8934 = -1.1f;
        field8935 = -0.5f;
        field8936 = 0.6662f;
        field8937 = 1.4f;
        field8938 = 0.6662f;
        field8939 = (float)Math.PI;
        field8940 = 1.4f;
        field8785 = 10000L;
        field8786 = 1000.0f;
        field8787 = 0.25f;
        field8788 = -0.1f;
        field8789 = 1.15f;
        field8790 = 0.04f;
        field8791 = 2.1f;
        field8792 = 0.14f;
        field8793 = 1.4f;
        field8794 = 0.09f;
        field8795 = -0.18f;
        field8796 = 1.05f;
        field8797 = 0.05f;
        field8798 = 2.25f;
        field8799 = 0.11f;
        field8800 = 0.95f;
        field8801 = 0.13f;
        field8802 = 2.8f;
        field8803 = 0.32f;
        field8804 = 0.32f;
        field8805 = (float)Math.PI;
        field8806 = -0.55f;
        field8807 = 1.05f;
        field8808 = -0.55f;
        field8809 = 1.05f;
        field8810 = -0.55f;
        field8811 = 0.55f;
        field8812 = -0.35f;
        field8813 = 0.35f;
        field8814 = -0.42f;
        field8815 = 0.32f;
        field8816 = -0.32f;
        field8817 = (float)Math.PI;
        field8818 = 0.38f;
        field8819 = 0.22f;
        field8820 = 0.65f;
        field8821 = 0.22f;
        field8822 = -0.28f;
        field8823 = 0.65f;
        field8824 = 0.18f;
        field8825 = 0.28f;
        field8826 = 0.18f;
        field8827 = -0.32f;
        field8828 = 0.14f;
        field8829 = 0.12f;
        field8830 = 0.6662f;
        field8831 = 1.15f;
        field8832 = 3.2f;
        field8833 = 0.07f;
        field8834 = 0.1f;
        field8835 = 0.6662f;
        field8836 = (float)Math.PI;
        field8837 = 1.15f;
        field8838 = 3.2f;
        field8839 = 0.07f;
        field8840 = 0.1f;
        field8841 = 2.05f;
        field8842 = 0.07f;
        field8843 = -0.1f;
        field8844 = 2.05f;
        field8845 = 0.07f;
        field8774 = 0.6662f;
        field8775 = (float)Math.PI;
        field8776 = 0.6662f;
        field8777 = 0.2312f;
        field8778 = 0.6662f;
        field8779 = 1.4f;
        field8780 = 0.6662f;
        field8781 = (float)Math.PI;
        field8782 = 1.4f;
        field8783 = 0.6662f;
        field8784 = 0.4f;
    }
}




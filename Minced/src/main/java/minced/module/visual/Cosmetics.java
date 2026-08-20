/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.ShaderProgram
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Matrix4f
 *  org.joml.Vector4f
 */
package minced.module.visual;
import minced.core.EventHandler;

import com.mojang.blaze3d.systems.RenderSystem;
import minced.core.ColorUtil;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.Shaders;
import minced.core.EventLayerHead;
import minced.core.ThemeColors;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.MAIN;
import minced.module.visual.Ambience;
import minced.module.visual.Emotions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class Cosmetics
extends Module {
    static String field4777 = "field4777";
    static String field4778 = "field4778";
    static String field4779 = "field4779";
    static String field4780 = "field4780";
    static float field4781;
    static float field4782;
    static float field4783;
    static float field4784;
    static String field4785 = "field4785";
    static String field4786 = "field4786";
    static String field4787 = "field4787";
    static String field4788 = "field4788";
    static String field4789 = "field4789";
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field4790;
    static String field4791 = "field4791";
    static float field4792;
    static String field4793 = "field4793";
    static String field4794 = "field4794";
    static float field4795;
    static float field4796;
    static float field4797;
    SliderSetting field4798;
    static float field4799;
    static float field4800;
    static float field4801;
    static float field4802;
    static float field4803;
    static float field4804;
    static float field4805;
    static float field4806;
    static float field4807;
    static float field4808;
    static float field4809;
    static float field4810;
    static float field4811;
    static float field4812;
    static float field4813;
    static float field4814;
    static float field4815;
    static float field4816;
    static float field4817;
    static float field4818;
    static float field4819;
    static float field4820;
    static float field4821;
    static float field4822;
    static float field4823;
    static float field4824;
    ModeSetting l;
    static String field4825;
    static float field4826;
    static float field4827;
    static float field4828;
    static float field4829;
    static float field4830;
    static float field4831;
    static float field4832;
    static float field4833;
    static float field4834;
    static float field4835;
    static float field4836;
    static float field4837;
    static float field4838;
    static float field4839;
    static float field4840;
    static float field4841;
    static float field4842;
    static float field4843;
    static float field4844;
    static float field4845;
    static float field4846;
    static float field4847;
    static float field4848;
    static float field4849;
    static float field4850;
    static float field4851;
    static float field4852;
    static float field4853;
    static float field4854;
    static float field4855;
    static float field4856;
    static float field4857;
    static float field4858;
    static float field4859;
    static float field4860;
    static float field4861;
    static float field4862;
    static float field4863;
    static float field4864;
    static float field4865;
    static float field4866;
    static float field4867;
    static float field4868;
    static float field4869;
    static float field4870;
    static float field4871;
    static float field4872;
    static float field4873;
    static float field4874;
    static float field4875;
    static float field4876;
    static float field4877;
    static float field4878;
    static float field4879;
    static float field4880;
    static float field4881;
    static float field4882;
    static float field4883;
    static float field4884;
    static float field4885;
    static float field4886;
    static float field4887;
    static float field4888;
    static float field4889;
    static float field4890;
    static float field4891;
    static float field4892;
    static float field4893;
    static float field4894;
    static float field4895;
    static float field4896;
    static float field4897;
    static float field4898;
    static float field4899;
    static float field4900;
    static float field4901;
    static float field4902;
    static float field4903;
    static float field4904;
    static float field4905;
    static float field4906;
    static float field4907;
    static float field4908;
    static float field4909;
    static float field4910;
    static float field4911;
    static float field4912;
    static float field4913;
    static float field4914;
    static float field4915;
    static float field4916;
    static float field4917;
    static float[][] field4918;
    static float field4919;
    static float field4920;
    static float field4921;
    ModeSetting field4922 = new ModeSetting(field4956, field4957, field4958, field4959);
    static String field4923;
    static String field4924;
    ModeSetting field4925 = new ModeSetting(field4960, field4961, () -> this.field4922.isMode(field4923), field4962);
    static String field4926;
    static String field4927;
    static String field4928;
    static float field4929;
    static float field4930;
    static float field4931;
    static String field4932;
    BooleanSetting field4933;
    BooleanSetting field4934;
    static String field4935;
    static float field4936;
    static float field4937;
    static float field4938;
    static float field4939;
    static float field4940;
    static float field4941;
    static String field4942;
    static String field4943;
    static float field4944;
    static float field4945;
    static float field4946;
    static float field4947;
    static float field4948;
    static float field4949;
    static float field4950;
    float field4951;
    float field4952;
    static float field4953;
    static String field4954;
    static String field4955;
    static String field4956;
    static String field4957;
    static String field4958;
    static String field4959;
    static String field4960;
    static String field4961;
    static String field4962;
    static String field4963;
    static String field4964;
    static String field4965;
    static String field4966;
    static String field4967;
    static String field4968;
    static String field4969;
    static String field4970;
    ModeSetting field4971;
    static String field4972;
    static float field4973;
    static float field4974;
    static String field4975;
    static String field4976;
    static String field4977;
    static float field4978;
    SliderSetting field4979;
    static float field4980;
    static float field4981;
    static float field4982;
    static float field4983;
    static float field4984;
    static float field4985;
    static float field4986;
    static float field4987;
    static float field4988;
    static float field4989;
    static float field4990;
    static float field4991;
    static float field4992;
    static float field4993;
    static float field4994;
    static float field4995;
    static float field4996;
    static float field4997;
    static float field4998;
    static float field4999;
    static float field5000;
    static float field5001;
    static float field5002;
    static float field5003;
    static float field5004;
    static float field5005;
    static float field5006;
    static float field5007;
    static float field5008;
    static float field5009;
    static float field5010;
    static float field5011;
    static float field5012;
    static float field5013;
    static float field5014;
    static float field5015;
    static int[] field5016;
    static String field5017;
    static String field5018;
    static String field5019;
    static float field5020;
    static float field5021;
    static float field5022;
    static float field5023;
    static float field5024;
    static float field5025;
    static float field5026;
    static float field5027;
    static float field5028;
    static long field5029;
    static float field5030;
    static float field5031;
    static float field5032;
    static float field5033;
    static float field5034;
    static float field5035;
    static float field5036;
    static float field5037;
    static float field5038;
    static float field5039;
    static String field5040;
    static float field5041;
    static float field5042;
    static float field5043;
    static float field5044;
    static float field5045;
    static float field5046;
    static float field5047;
    static float field5048;
    static float field5049;
    static float field5050;
    static float field5051;
    static float field5052;
    static float field5053;
    static float field5054;
    static float field5055;
    static float field5056;
    static float field5057;
    static float field5058;
    static float field5059;
    static float field5060;
    static float field5061;
    static float field5062;

    private ShaderProgram method1199(ShaderProgramKey param1, int param2, float param3) {
        ShaderProgram v4 = RenderSystem.setShader((ShaderProgramKey)param1);
        if (v4 == null) {
            return null;
        }
        if (v4.getUniform(field4777) != null) {
            v4.getUniform(field4778).set(RenderSystem.getModelViewMatrix());
        }
        if (v4.getUniform(field4779) != null) {
            v4.getUniform(field4780).set(RenderSystem.getProjectionMatrix());
        }
        float v5 = (float)ColorUtil.method1854(param2) / field4781;
        float v6 = (float)ColorUtil.method1846(param2) / field4782;
        float v7 = (float)ColorUtil.method1847(param2) / field4783;
        float v8 = (float)ColorUtil.cfr_renamed_0(param2) / field4784 * param3;
        if (v4.getUniform(field4785) != null) {
            v4.getUniform(field4786).set(v5, v6, v7, 1.0f);
        }
        if (v4.getUniform(field4787) != null) {
            v4.getUniform(field4788).set(((Float)Ambience.field1656.getValue()).floatValue());
        }
        if (v4.getUniform(field4789) != null) {
            float v9 = I.getRenderTickCounter().getTickDelta(false);
            float v10 = Cosmetics.I.world != null ? ((float)Cosmetics.I.world.getTime() + v9) * ((Float)Ambience.field1657.getValue()).floatValue() : (float)System.currentTimeMillis() / field4790;
            v4.getUniform(field4791).set(v10 * field4792);
        }
        if (v4.getUniform(field4793) != null) {
            v4.getUniform(field4794).set(MathHelper.clamp((float)v8, (float)0.0f, (float)1.0f));
        }
        return v4;
    }

    private void method1200(MatrixStack param1, BipedEntityModel param2) {
        param2.body.rotate(param1);
        param1.translate(field4795, 0.0f, field4796);
        param1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(2.0f));
        param1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(2.0f));
        param1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(field4797));
        float v3 = ((Float)this.field4798.getValue()).floatValue();
        param1.scale(v3, v3, v3);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.lineWidth((float)field4799);
    }

    private void method1201(Matrix4f param1, float param2, float param3, float param4, float param5, float param6, float param7, float param8, float param9, float param10, float param11) {
        float v12 = param8 * field4800;
        float v13 = param9 * field4801;
        float v14 = param10 * field4802;
        float v15 = param8 * field4803;
        float v16 = param9 * field4804;
        float v17 = param10 * field4805;
        BufferBuilder v18 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        v18.vertex(param1, param2, param3, param4).color(param8, param9, param10, param11);
        v18.vertex(param1, param5, param3, param4).color(param8, param9, param10, param11);
        v18.vertex(param1, param5, param6, param4).color(param8, param9, param10, param11);
        v18.vertex(param1, param2, param6, param4).color(param8, param9, param10, param11);
        v18.vertex(param1, param5, param3, param7).color(v12, v13, v14, param11);
        v18.vertex(param1, param2, param3, param7).color(v12, v13, v14, param11);
        v18.vertex(param1, param2, param6, param7).color(v12, v13, v14, param11);
        v18.vertex(param1, param5, param6, param7).color(v12, v13, v14, param11);
        v18.vertex(param1, param2, param3, param7).color(v15, v16, v17, param11);
        v18.vertex(param1, param2, param3, param4).color(v15, v16, v17, param11);
        v18.vertex(param1, param2, param6, param4).color(v15, v16, v17, param11);
        v18.vertex(param1, param2, param6, param7).color(v15, v16, v17, param11);
        v18.vertex(param1, param5, param3, param4).color(v15, v16, v17, param11);
        v18.vertex(param1, param5, param3, param7).color(v15, v16, v17, param11);
        v18.vertex(param1, param5, param6, param7).color(v15, v16, v17, param11);
        v18.vertex(param1, param5, param6, param4).color(v15, v16, v17, param11);
        v18.vertex(param1, param2, param6, param4).color(param8, param9, param10, param11 * field4806);
        v18.vertex(param1, param5, param6, param4).color(param8, param9, param10, param11 * field4807);
        v18.vertex(param1, param5, param6, param7).color(param8, param9, param10, param11 * field4808);
        v18.vertex(param1, param2, param6, param7).color(param8, param9, param10, param11 * field4809);
        v18.vertex(param1, param2, param3, param7).color(v12, v13, v14, param11 * field4810);
        v18.vertex(param1, param5, param3, param7).color(v12, v13, v14, param11 * field4811);
        v18.vertex(param1, param5, param3, param4).color(v12, v13, v14, param11 * field4812);
        v18.vertex(param1, param2, param3, param4).color(v12, v13, v14, param11 * field4813);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v18.end());
    }

    private void method1202(Matrix4f param1, float param2, float param3, float param4, float param5, float param6, float param7) {
        Vector4f v8 = new Vector4f((param2 + param5) * field4814, (param3 + param6) * field4815, (param4 + param7) * field4816, 1.0f);
        param1.transform(v8);
        float v9 = v8.x;
        float v10 = v8.y;
        float v11 = v8.z;
        BufferBuilder v12 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        this.method1214(v12, param1, param2, param6, param4, v9, v10, v11);
        this.method1214(v12, param1, param2, param6, param7, v9, v10, v11);
        this.method1214(v12, param1, param5, param6, param7, v9, v10, v11);
        this.method1214(v12, param1, param5, param6, param4, v9, v10, v11);
        this.method1214(v12, param1, param2, param3, param7, v9, v10, v11);
        this.method1214(v12, param1, param2, param3, param4, v9, v10, v11);
        this.method1214(v12, param1, param5, param3, param4, v9, v10, v11);
        this.method1214(v12, param1, param5, param3, param7, v9, v10, v11);
        this.method1214(v12, param1, param2, param3, param4, v9, v10, v11);
        this.method1214(v12, param1, param2, param6, param4, v9, v10, v11);
        this.method1214(v12, param1, param5, param6, param4, v9, v10, v11);
        this.method1214(v12, param1, param5, param3, param4, v9, v10, v11);
        this.method1214(v12, param1, param5, param3, param7, v9, v10, v11);
        this.method1214(v12, param1, param5, param6, param7, v9, v10, v11);
        this.method1214(v12, param1, param2, param6, param7, v9, v10, v11);
        this.method1214(v12, param1, param2, param3, param7, v9, v10, v11);
        this.method1214(v12, param1, param2, param3, param7, v9, v10, v11);
        this.method1214(v12, param1, param2, param6, param7, v9, v10, v11);
        this.method1214(v12, param1, param2, param6, param4, v9, v10, v11);
        this.method1214(v12, param1, param2, param3, param4, v9, v10, v11);
        this.method1214(v12, param1, param5, param3, param4, v9, v10, v11);
        this.method1214(v12, param1, param5, param6, param4, v9, v10, v11);
        this.method1214(v12, param1, param5, param6, param7, v9, v10, v11);
        this.method1214(v12, param1, param5, param3, param7, v9, v10, v11);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v12.end());
    }

    private void method1203(Matrix4f param1, float param2, float param3, float param4, float param5, float param6, float param7, float param8, float param9, float param10) {
        Vector4f v11 = new Vector4f((param2 + param5 + param8) / field4817, (param3 + param6 + param9) / field4818, (param4 + param7 + param10) / field4819, 1.0f);
        param1.transform(v11);
        float v12 = v11.x;
        float v13 = v11.y;
        float v14 = v11.z;
        BufferBuilder v15 = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        this.method1214(v15, param1, param2, param3, param4, v12, v13, v14);
        this.method1214(v15, param1, param5, param6, param7, v12, v13, v14);
        this.method1214(v15, param1, param8, param9, param10, v12, v13, v14);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v15.end());
    }

    private void method1204(MatrixStack param1, BipedEntityModel param2) {
        ShaderProgram v14;
        param1.push();
        this.method1200(param1, param2);
        Matrix4f v3 = param1.peek().getPositionMatrix();
        int v4 = this.method1212();
        int v5 = ColorUtil.method1856(v4, 60);
        float[] v6 = ColorUtil.method1849(v5);
        float[] v7 = ColorUtil.method1849(ColorUtil.method1851(v4, field4820));
        float[] v8 = ColorUtil.method1849(ColorUtil.method1851(v4, field4821));
        float[] v9 = ColorUtil.method1849(ColorUtil.method1851(v4, field4822));
        float[] v10 = ColorUtil.method1849(ColorUtil.method1851(v4, 2.0f));
        float v11 = field4823;
        float v12 = field4824;
        boolean v13 = this.l.isMode(field4825);
        if (v13 && (v14 = this.method1199(this.method1218(), v5, field4826)) == null) {
            v13 = false;
            RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
        }
        if (v13) {
            float v25;
            float v24;
            this.method1199(this.method1218(), v5, field4827);
            this.method1202(v3, -v11, field4828, -v12, v11, field4829, v12);
            param1.push();
            param1.translate(0.0f, field4830, 0.0f);
            Matrix4f v14mat = param1.peek().getPositionMatrix();
            this.method1199(this.method1218(), v5, field4831);
            this.method1203(v14mat, -v11, 0.0f, -v12, v11, 0.0f, -v12, 0.0f, field4832, 0.0f);
            this.method1199(this.method1218(), v5, field4833);
            this.method1203(v14mat, v11, 0.0f, v12, -v11, 0.0f, v12, 0.0f, field4834, 0.0f);
            this.method1199(this.method1218(), v5, field4835);
            this.method1203(v14mat, -v11, 0.0f, v12, -v11, 0.0f, -v12, 0.0f, field4836, 0.0f);
            this.method1199(this.method1218(), v5, field4837);
            this.method1203(v14mat, v11, 0.0f, -v12, v11, 0.0f, v12, 0.0f, field4838, 0.0f);
            param1.pop();
            float v15 = field4839;
            float v16 = field4840;
            float v17 = field4841;
            this.method1199(this.method1218(), ColorUtil.method1852((int)(v7[0] * field4842), (int)(v7[1] * field4843), (int)(v7[2] * field4844), (int)(v7[3] * field4845)), field4846);
            this.method1202(v3, -v15, field4847 - v17 * field4848, -v16, v15, field4849 + v17 * field4850, v16);
            float v18 = field4851;
            float v19 = field4852;
            float v20 = field4853;
            float v22 = v20 / 8.0f;
            for (int v23 = 0; v23 < 8; ++v23) {
                v24 = field4854 - (float)(v23 + 1) * v22;
                v25 = field4855 - (float)v23 * v22;
                float[] v26 = (v23 & 1) == 0 ? v8 : v9;
                this.method1199(this.method1218(), ColorUtil.method1852((int)(v26[0] * field4856), (int)(v26[1] * field4857), (int)(v26[2] * field4858), (int)(v26[3] * field4859)), 1.0f);
                this.method1202(v3, -v18, v24, -v19, v18, v25, v19);
            }
            float v23 = field4860;
            v24 = field4861;
            v25 = field4862 - v20 - field4863;
            float v26 = field4864 - v20;
            this.method1199(this.method1218(), ColorUtil.method1852((int)(v10[0] * field4865), (int)(v10[1] * field4866), (int)(v10[2] * field4867), (int)(v10[3] * field4868)), field4869);
            this.method1202(v3, -v23, v25, -v24, v23, v26, v24);
        } else {
            float v26;
            float v25;
            this.method1201(v3, -v11, field4870, -v12, v11, field4871, v12, v6[0], v6[1], v6[2], field4872);
            param1.push();
            param1.translate(0.0f, field4873, 0.0f);
            Matrix4f v14mat = param1.peek().getPositionMatrix();
            BufferBuilder v15 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            v15.vertex(v14mat, -v11, 0.0f, -v12).color(v6[0], v6[1], v6[2], field4874);
            v15.vertex(v14mat, v11, 0.0f, -v12).color(v6[0], v6[1], v6[2], field4875);
            v15.vertex(v14mat, 0.0f, field4876, 0.0f).color(v6[0], v6[1], v6[2], field4877);
            v15.vertex(v14mat, 0.0f, field4878, 0.0f).color(v6[0], v6[1], v6[2], field4879);
            v15.vertex(v14mat, v11, 0.0f, v12).color(v6[0], v6[1], v6[2], field4880);
            v15.vertex(v14mat, -v11, 0.0f, v12).color(v6[0], v6[1], v6[2], field4881);
            v15.vertex(v14mat, 0.0f, field4882, 0.0f).color(v6[0], v6[1], v6[2], field4883);
            v15.vertex(v14mat, 0.0f, field4884, 0.0f).color(v6[0], v6[1], v6[2], field4885);
            v15.vertex(v14mat, -v11, 0.0f, v12).color(v6[0], v6[1], v6[2], field4886);
            v15.vertex(v14mat, -v11, 0.0f, -v12).color(v6[0], v6[1], v6[2], field4887);
            v15.vertex(v14mat, 0.0f, field4888, 0.0f).color(v6[0], v6[1], v6[2], field4889);
            v15.vertex(v14mat, 0.0f, field4890, 0.0f).color(v6[0], v6[1], v6[2], field4891);
            v15.vertex(v14mat, v11, 0.0f, -v12).color(v6[0], v6[1], v6[2], field4892);
            v15.vertex(v14mat, v11, 0.0f, v12).color(v6[0], v6[1], v6[2], field4893);
            v15.vertex(v14mat, 0.0f, field4894, 0.0f).color(v6[0], v6[1], v6[2], field4895);
            v15.vertex(v14mat, 0.0f, field4896, 0.0f).color(v6[0], v6[1], v6[2], field4897);
            BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v15.end());
            param1.pop();
            float v16 = field4898;
            float v17 = field4899;
            float v18 = field4900;
            this.method1201(v3, -v16, field4901 - v18 * field4902, -v17, v16, field4903 + v18 * field4904, v17, v7[0], v7[1], v7[2], field4905);
            float v19 = field4906;
            float v20 = field4907;
            float v21 = field4908;
            float v23 = v21 / 8.0f;
            for (int v24 = 0; v24 < 8; ++v24) {
                v25 = field4909 - (float)(v24 + 1) * v23;
                v26 = field4910 - (float)v24 * v23;
                float[] v27 = (v24 & 1) == 0 ? v8 : v9;
                this.method1201(v3, -v19, v25, -v20, v19, v26, v20, v27[0], v27[1], v27[2], v27[3]);
            }
            float v24 = field4911;
            v25 = field4912;
            v26 = field4913 - v21 - field4914;
            float v27 = field4915 - v21;
            this.method1201(v3, -v24, v26, -v25, v24, v27, v25, v10[0], v10[1], v10[2], field4916);
        }
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
        this.method1217();
        param1.pop();
    }

    private void method1205(Matrix4f param1, int[] param2, int param3, float param4, boolean param5) {
        float v6 = param5 ? field4917 : 1.0f;
        float[] v7 = ColorUtil.method1849(param3);
        BufferBuilder v8 = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
        for (int v12 : param2) {
            if (v12 >= field4918.length) continue;
            float[] v13 = field4918[v12];
            v8.vertex(param1, 0.0f, 0.0f, 0.0f).color(v7[0], v7[1], v7[2], param4 * field4919);
            v8.vertex(param1, v13[0] * v6, v13[1], v13[2]).color(v7[0], v7[1], v7[2], param4 * field4920);
        }
        // end() throws when every index was filtered out — draw only if something was buffered.
        BuiltBuffer v14 = v8.endNullable();
        if (v14 != null) {
            BufferRenderer.drawWithGlobalProgram(v14);
        }
    }

    private void method1206(Matrix4f param1, boolean param2) {
        float v3 = param2 ? field4921 : 1.0f;
        Vector4f v4 = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        param1.transform(v4);
        float v5 = v4.x;
        float v6 = v4.y;
        float v7 = v4.z;
        BufferBuilder v8 = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        this.method1214(v8, param1, 0.0f, 0.0f, 0.0f, v5, v6, v7);
        for (float[] v12 : field4918) {
            this.method1214(v8, param1, v12[0] * v3, v12[1], v12[2], v5, v6, v7);
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v8.end());
    }

    private void method1209(Matrix4f param1, int param2, int param3, float param4, boolean param5) {
        float v6 = param5 ? field4929 : 1.0f;
        float[] v7 = ColorUtil.method1849(param2);
        float[] v8 = ColorUtil.method1849(param3);
        BufferBuilder v9 = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        v9.vertex(param1, 0.0f, 0.0f, 0.0f).color(v7[0], v7[1], v7[2], param4 * v7[3]);
        for (float[] v13 : field4918) {
            float v14 = (float)Math.sqrt(v13[0] * v13[0] + v13[1] * v13[1] + v13[2] * v13[2]);
            float v15 = Math.min(v14 / field4930, 1.0f);
            float v16 = MathHelper.lerp((float)v15, (float)v7[0], (float)v8[0]);
            float v17 = MathHelper.lerp((float)v15, (float)v7[1], (float)v8[1]);
            float v18 = MathHelper.lerp((float)v15, (float)v7[2], (float)v8[2]);
            float v19 = param4 * MathHelper.lerp((float)v15, (float)v7[3], (float)(v8[3] * field4931));
            v9.vertex(param1, v13[0] * v6, v13[1], v13[2]).color(v16, v17, v18, v19);
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v9.end());
    }

    @EventHandler(2)
    public void method1210(EventLayerHead param1) {
        PlayerEntity v3;
        BipedEntityModel v4s;
        Emotions v2 = Emotions.method1578();
        if (v2 != null && v2.method1581() != null) {
            return;
        }
        LivingEntity v4 = param1.method2337();
        if (v4 instanceof PlayerEntity) {
            v3 = (PlayerEntity)v4;
            EntityModel v5m = param1.method2335();
            if (!(v5m instanceof BipedEntityModel)) {
                return;
            }
            v4s = (BipedEntityModel)v5m;
        } else {
            return;
        }
        boolean v5 = v3 == Cosmetics.I.player;
        boolean v6 = Minced.getInstance().getFriendManager().cfr_renamed_4(v3.getGameProfile().getName());
        if (!(v5 || v6 && ((Boolean)this.field4933.getValue()).booleanValue())) {
            return;
        }
        if (v5 && ((Boolean)this.field4934.getValue()).booleanValue() && Cosmetics.I.options.getPerspective().isFirstPerson()) {
            return;
        }
        if (this.field4922.isMode(field4935)) {
            this.method1215(v3);
            this.method1220(v3, param1.method2336(), v4s);
        } else {
            this.method1204(param1.method2336(), v4s);
        }
    }

    private void method1211(Matrix4f param1, int param2, float param3, boolean param4) {
        float v5 = param4 ? field4936 : 1.0f;
        float[] v6 = ColorUtil.method1849(param2);
        RenderSystem.lineWidth((float)field4937);
        BufferBuilder v7 = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION_COLOR);
        v7.vertex(param1, 0.0f, 0.0f, 0.0f).color(v6[0], v6[1], v6[2], param3 * field4938);
        for (float[] v11 : field4918) {
            v7.vertex(param1, v11[0] * v5, v11[1], v11[2]).color(v6[0], v6[1], v6[2], param3 * field4939);
        }
        v7.vertex(param1, 0.0f, 0.0f, 0.0f).color(v6[0], v6[1], v6[2], param3 * field4940);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v7.end());
        RenderSystem.lineWidth((float)field4941);
    }

    private int method1212() {
        return ThemeColors.method3199(MAIN.MODULE_VISUAL);
    }

    private void method1214(BufferBuilder param1, Matrix4f param2, float param3, float param4, float param5, float param6, float param7, float param8) {
        Vector4f v9 = new Vector4f(param3, param4, param5, 1.0f);
        param2.transform(v9);
        float v10 = v9.x;
        float v11 = v9.y;
        float v12 = v9.z;
        float v13 = v10 - param6;
        float v14 = v11 - param7;
        float v15 = v12 - param8;
        float v16 = MathHelper.sqrt((float)(v13 * v13 + v14 * v14 + v15 * v15));
        if (v16 > field4944) {
            v13 /= v16;
            v14 /= v16;
            v15 /= v16;
        } else {
            v13 = 0.0f;
            v14 = 1.0f;
            v15 = 0.0f;
        }
        float v17 = MathHelper.clamp((float)(v13 * field4945 + field4946), (float)0.0f, (float)1.0f);
        float v18 = MathHelper.clamp((float)(v14 * field4947 + field4948), (float)0.0f, (float)1.0f);
        float v19 = MathHelper.clamp((float)(v15 * field4949 + field4950), (float)0.0f, (float)1.0f);
        param1.vertex(param2, param3, param4, param5).color(v17, v18, v19, 1.0f);
    }

    private void method1215(PlayerEntity param1) {
        this.field4952 = this.field4951;
        float v2 = param1.isGliding() ? 1.0f : 0.0f;
        this.field4951 += (v2 - this.field4951) * field4953;
    }

    public Cosmetics() {
        super(field4954, field4955, Combat.Visuals);
        this.l = new ModeSetting(field4963, field4964, () -> this.field4922.isMode(field4932), field4965);
        this.field4971 = new ModeSetting(field4966, field4967, () -> this.field4922.isMode(field4924) && this.field4925.isMode(field4926) || this.field4922.isMode(field4927) && this.l.isMode(field4928), field4968, field4969, field4970);
        this.field4798 = new SliderSetting(field4972, 1.0f, field4973, 2.0f, field4974);
        this.field4933 = new BooleanSetting(field4975, true);
        this.field4934 = new BooleanSetting(field4976, false);
        this.field4979 = new SliderSetting(field4977, 1.0f, 0.0f, 2.0f, field4978, () -> this.field4922.isMode(field4942) && !this.field4925.isMode(field4943));
        this.addSettings(this.field4922, this.field4925, this.l, this.field4971, this.field4798, this.field4979, this.field4933, this.field4934);
    }

    public static void method1216() {
        field4918 = new float[][]{{field4980, field4981, field4982}, {field4983, field4984, field4985}, {field4986, field4987, field4988}, {field4989, field4990, field4991}, {field4992, field4993, field4994}, {field4995, field4996, field4997}, {field4998, field4999, field5000}, {field5001, field5002, field5003}, {field5004, field5005, field5006}, {field5007, field5008, field5009}, {field5010, field5011, field5012}, {field5013, field5014, field5015}};
        field5016 = new int[]{1, 3, 5, 7};
    }

    private void method1217() {
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private ShaderProgramKey method1218() {
        if (this.field4971.isMode(field5017)) {
            return Shaders.field8369;
        }
        if (this.field4971.isMode(field5018)) {
            return Shaders.field8371;
        }
        return Ambience.field1634.isMode(field5019) ? Shaders.field8371 : Shaders.field8369;
    }

    private void method1219(Matrix4f param1, int param2, float param3, boolean param4) {
        float v5 = param4 ? field5020 : 1.0f;
        float[] v6 = ColorUtil.method1849(param2);
        BufferBuilder v7 = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        v7.vertex(param1, 0.0f, 0.0f, 0.0f).color(v6[0], v6[1], v6[2], param3 * field5021);
        for (float[] v11 : field4918) {
            v7.vertex(param1, v11[0] * v5, v11[1], v11[2]).color(v6[0], v6[1], v6[2], param3 * field5022);
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v7.end());
    }

    private void method1220(PlayerEntity param1, MatrixStack param2, BipedEntityModel param3) {
        param2.push();
        param3.body.rotate(param2);
        param2.translate(0.0f, field5023, field5024);
        float v4 = ((Float)this.field4798.getValue()).floatValue() * field5025;
        param2.scale(v4 * field5026, v4 * field5027, v4 * field5028);
        float v5 = (float)(System.currentTimeMillis() % field5029) / field5030;
        float v6 = 1.0f + MathHelper.sin((float)(v5 * field5031)) * field5032;
        param2.scale(v6, v6, v6);
        float v7 = MathHelper.lerp((float)I.getRenderTickCounter().getTickDelta(false), (float)this.field4952, (float)this.field4951);
        float v8 = param1.isGliding() ? MathHelper.sin((float)(v5 * field5033)) * field5034 : 0.0f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.lineWidth((float)field5035);
        int v9 = this.method1212();
        int v10 = ColorUtil.method1853(ColorUtil.method1851(v9, field5036), field5037);
        int v11 = ColorUtil.method1851(v9, field5038);
        float v12 = field5039;
        float v13 = ((Float)this.field4979.getValue()).floatValue();
        boolean v14 = this.field4925.isMode(field5040);
        if (v13 > 0.0f && !v14) {
            RenderSystem.depthMask((boolean)false);
            RenderSystem.blendFuncSeparate((int)770, (int)1, (int)0, (int)1);
            float v15 = 1.0f + MathHelper.sin((float)(v5 * field5041)) * field5042;
            for (float[] v20 : new float[][]{{field5043, field5044}, {field5045, field5046}, {field5047, field5048}}) {
                float v21 = v20[0];
                float v22 = v20[1] * v13 * v15;
                param2.push();
                param2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp((float)v7, (float)field5049, (float)field5050)));
                if (v8 != 0.0f) {
                    param2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(v8 * field5051));
                }
                param2.scale(v21, v21, v21);
                this.method1219(param2.peek().getPositionMatrix(), v11, v22, false);
                param2.pop();
                param2.push();
                param2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp((float)v7, (float)field5052, (float)field5053)));
                if (v8 != 0.0f) {
                    param2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-v8 * field5054));
                }
                param2.scale(v21, v21, v21);
                this.method1219(param2.peek().getPositionMatrix(), v11, v22, true);
                param2.pop();
            }
            RenderSystem.depthMask((boolean)true);
            RenderSystem.defaultBlendFunc();
        }
        param2.push();
        param2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp((float)v7, (float)field5055, (float)field5056)));
        if (v8 != 0.0f) {
            param2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(v8 * field5057));
        }
        if (v14) {
            ShaderProgram v15 = this.method1199(this.method1218(), v9, v12);
            if (v15 != null) {
                this.method1206(param2.peek().getPositionMatrix(), false);
            } else {
                this.method1209(param2.peek().getPositionMatrix(), v9, v10, v12, false);
            }
        } else {
            this.method1209(param2.peek().getPositionMatrix(), v9, v10, v12, false);
        }
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
        this.method1205(param2.peek().getPositionMatrix(), field5016, v11, v12, false);
        this.method1211(param2.peek().getPositionMatrix(), v11, v12 * field5058, false);
        param2.pop();
        param2.push();
        param2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp((float)v7, (float)field5059, (float)field5060)));
        if (v8 != 0.0f) {
            param2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-v8 * field5061));
        }
        if (v14) {
            ShaderProgram v15 = this.method1199(this.method1218(), v9, v12);
            if (v15 != null) {
                this.method1206(param2.peek().getPositionMatrix(), true);
            } else {
                this.method1209(param2.peek().getPositionMatrix(), v9, v10, v12, true);
            }
        } else {
            this.method1209(param2.peek().getPositionMatrix(), v9, v10, v12, true);
        }
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
        this.method1205(param2.peek().getPositionMatrix(), field5016, v11, v12, true);
        this.method1211(param2.peek().getPositionMatrix(), v11, v12 * field5062, true);
        param2.pop();
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        param2.pop();
    }

    static {
        field4954 = "Cosmetics";
        field4955 = "Косметика: демонические крылья и катана";
        field4956 = "Тип";
        field4957 = "Крылья";
        field4958 = "Крылья";
        field4959 = "Катана";
        field4960 = "Отрисовка (крылья)";
        field4961 = "Шейдер";
        field4962 = "Шейдер";
        field4963 = "Отрисовка (катана)";
        field4964 = "Шейдер";
        field4965 = "Шейдер";
        field4966 = "Шейдер";
        field4967 = "Из Ambience";
        field4968 = "Из Ambience";
        field4969 = "Плазма";
        field4970 = "Balatro";
        field4972 = "Размер";
        field4973 = 0.5f;
        field4974 = 0.05f;
        field4975 = "На друзьях";
        field4976 = "Только от 3-го лица";
        field4977 = "Свечение";
        field4978 = 0.1f;
        field4935 = "Крылья";
        field4953 = 0.12f;
        field4795 = -0.3f;
        field4796 = 0.18f;
        field4797 = -38.0f;
        field4799 = 1.5f;
        field5017 = "Плазма";
        field5018 = "Balatro";
        field5019 = "Balatro";
        field4781 = 255.0f;
        field4782 = 255.0f;
        field4783 = 255.0f;
        field4784 = 255.0f;
        field4790 = 50.0f;
        field4792 = 0.08f;
        field4800 = 0.85f;
        field4801 = 0.85f;
        field4802 = 0.85f;
        field4803 = 0.7f;
        field4804 = 0.7f;
        field4805 = 0.7f;
        field4806 = 0.95f;
        field4807 = 0.95f;
        field4808 = 0.95f;
        field4809 = 0.95f;
        field4810 = 0.9f;
        field4811 = 0.9f;
        field4812 = 0.9f;
        field4813 = 0.9f;
        field4814 = 0.5f;
        field4815 = 0.5f;
        field4816 = 0.5f;
        field4817 = 3.0f;
        field4818 = 3.0f;
        field4819 = 3.0f;
        field4944 = 1.0E-5f;
        field4945 = 0.5f;
        field4946 = 0.5f;
        field4947 = 0.5f;
        field4948 = 0.5f;
        field4949 = 0.5f;
        field4950 = 0.5f;
        field4820 = 0.6f;
        field4821 = 1.3f;
        field4822 = 1.7f;
        field4823 = 0.018f;
        field4824 = 0.008f;
        field4825 = "Шейдер";
        field4826 = 0.97f;
        field4827 = 0.97f;
        field4828 = 0.06f;
        field4829 = 0.74f;
        field4830 = 0.74f;
        field4831 = 0.97f;
        field4832 = 0.06f;
        field4833 = 0.92f;
        field4834 = 0.06f;
        field4835 = 0.85f;
        field4836 = 0.06f;
        field4837 = 0.85f;
        field4838 = 0.06f;
        field4839 = 0.04f;
        field4840 = 0.016f;
        field4841 = 0.018f;
        field4842 = 255.0f;
        field4843 = 255.0f;
        field4844 = 255.0f;
        field4845 = 255.0f;
        field4846 = 0.95f;
        field4847 = 0.06f;
        field4848 = 0.5f;
        field4849 = 0.06f;
        field4850 = 0.5f;
        field4851 = 0.016f;
        field4852 = 0.01f;
        field4853 = 0.22f;
        field4854 = 0.06f;
        field4855 = 0.06f;
        field4856 = 255.0f;
        field4857 = 255.0f;
        field4858 = 255.0f;
        field4859 = 255.0f;
        field4860 = 0.02f;
        field4861 = 0.012f;
        field4862 = 0.06f;
        field4863 = 0.015f;
        field4864 = 0.06f;
        field4865 = 255.0f;
        field4866 = 255.0f;
        field4867 = 255.0f;
        field4868 = 255.0f;
        field4869 = 0.95f;
        field4870 = 0.06f;
        field4871 = 0.74f;
        field4872 = 0.97f;
        field4873 = 0.74f;
        field4874 = 0.97f;
        field4875 = 0.97f;
        field4876 = 0.06f;
        field4877 = 0.97f;
        field4878 = 0.06f;
        field4879 = 0.97f;
        field4880 = 0.92f;
        field4881 = 0.92f;
        field4882 = 0.06f;
        field4883 = 0.92f;
        field4884 = 0.06f;
        field4885 = 0.92f;
        field4886 = 0.85f;
        field4887 = 0.85f;
        field4888 = 0.06f;
        field4889 = 0.85f;
        field4890 = 0.06f;
        field4891 = 0.85f;
        field4892 = 0.85f;
        field4893 = 0.85f;
        field4894 = 0.06f;
        field4895 = 0.85f;
        field4896 = 0.06f;
        field4897 = 0.85f;
        field4898 = 0.04f;
        field4899 = 0.016f;
        field4900 = 0.018f;
        field4901 = 0.06f;
        field4902 = 0.5f;
        field4903 = 0.06f;
        field4904 = 0.5f;
        field4905 = 0.95f;
        field4906 = 0.016f;
        field4907 = 0.01f;
        field4908 = 0.22f;
        field4909 = 0.06f;
        field4910 = 0.06f;
        field4911 = 0.02f;
        field4912 = 0.012f;
        field4913 = 0.06f;
        field4914 = 0.015f;
        field4915 = 0.06f;
        field4916 = 0.95f;
        field5023 = 0.25f;
        field5024 = 0.35f;
        field5025 = 0.0625f;
        field5026 = 16.0f;
        field5027 = 16.0f;
        field5028 = 16.0f;
        field5029 = 10000L;
        field5030 = 1000.0f;
        field5031 = 1.8f;
        field5032 = 0.02f;
        field5033 = 4.0f;
        field5034 = 0.06f;
        field5035 = 1.5f;
        field5036 = 0.3f;
        field5037 = 0.9f;
        field5038 = 1.5f;
        field5039 = 0.85f;
        field5040 = "Шейдер";
        field5041 = 3.0f;
        field5042 = 0.15f;
        field5043 = 1.15f;
        field5044 = 0.12f;
        field5045 = 1.3f;
        field5046 = 0.06f;
        field5047 = 1.5f;
        field5048 = 0.025f;
        field5049 = 15.0f;
        field5050 = 35.0f;
        field5051 = 30.0f;
        field5052 = -15.0f;
        field5053 = -35.0f;
        field5054 = 30.0f;
        field5055 = 15.0f;
        field5056 = 35.0f;
        field5057 = 30.0f;
        field5058 = 0.6f;
        field5059 = -15.0f;
        field5060 = -35.0f;
        field5061 = 30.0f;
        field5062 = 0.6f;
        field5020 = -1.0f;
        field5021 = 0.8f;
        field5022 = 0.1f;
        field4929 = -1.0f;
        field4930 = 0.7f;
        field4931 = 0.6f;
        field4921 = -1.0f;
        field4917 = -1.0f;
        field4919 = 0.8f;
        field4920 = 0.15f;
        field4936 = -1.0f;
        field4937 = 2.5f;
        field4938 = 0.5f;
        field4939 = 0.3f;
        field4940 = 0.5f;
        field4941 = 1.5f;
        field4942 = "Крылья";
        field4943 = "Шейдер";
        field4924 = "Крылья";
        field4926 = "Шейдер";
        field4927 = "Катана";
        field4928 = "Шейдер";
        field4932 = "Катана";
        field4923 = "Крылья";
        field4980 = 0.06f;
        field4981 = 0.38f;
        field4982 = -0.01f;
        field4983 = 0.28f;
        field4984 = 0.52f;
        field4985 = -0.04f;
        field4986 = 0.22f;
        field4987 = 0.36f;
        field4988 = -0.06f;
        field4989 = 0.48f;
        field4990 = 0.44f;
        field4991 = -0.09f;
        field4992 = 0.38f;
        field4993 = 0.26f;
        field4994 = -0.11f;
        field4995 = 0.66f;
        field4996 = 0.3f;
        field4997 = -0.13f;
        field4998 = 0.56f;
        field4999 = 0.1f;
        field5000 = -0.14f;
        field5001 = 0.74f;
        field5002 = 0.02f;
        field5003 = -0.15f;
        field5004 = 0.58f;
        field5005 = -0.12f;
        field5006 = -0.12f;
        field5007 = 0.4f;
        field5008 = -0.22f;
        field5009 = -0.08f;
        field5010 = 0.22f;
        field5011 = -0.24f;
        field5012 = -0.04f;
        field5013 = 0.08f;
        field5014 = -0.14f;
        field5015 = -0.01f;
        Cosmetics.method1216();
    }
}




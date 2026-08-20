/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer$TextLayerType
 *  net.minecraft.client.gl.ShaderProgram
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Identifier
 *  org.joml.Matrix4f
 *  org.joml.Vector4f
 *  org.lwjgl.opengl.GL11
 */
package minced.core;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import minced.core.ColorUtil;
import minced.core.Shaders;
import minced.core.Render2DBatch;
import minced.core.BlurRenderer;
import minced.core.KawaseBlur;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

public final class RenderUtil
{
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field1098;
    static float field1099;
    static float field1100;
    static float field1101;
    static float field1102;
    static String field1103 = "size";
    static String field1104 = "radius";
    static String field1105 = "hurt_time";
    static String field1106 = "alpha";
    static String field1107 = "texXSize";
    static float field1108;
    static String field1109 = "texYSize";
    static float field1110;
    static float field1111;
    static float field1112;
    static float field1113;
    static float field1114;
    static float field1115;
    static String field1116 = "size";
    static String field1117 = "radius";
    static float field1118;
    static float field1119;
    static float field1120;
    static float field1121;
    static String field1122 = "color0";
    static float field1123;
    static float field1124;
    static float field1125;
    static String field1126 = "color1";
    static float field1127;
    static float field1128;
    static float field1129;
    static String field1130 = "color2";
    static float field1131;
    static float field1132;
    static float field1133;
    static String field1134 = "color3";
    static float field1135;
    static float field1136;
    static float field1137;
    static float field1138;
    static float field1139;
    static float field1140;
    static float field1141;
    static float field1142;
    static int field1143;
    static float field1144;
    static int field1145;
    static float field1146;
    static float field1147;
    static float field1148;
    static float field1149;
    static int field1150;
    static float field1151;
    static String field1152 = "size";
    static String field1153 = "radius";
    static String field1154 = "color0";
    static float field1155;
    static float field1156;
    static float field1157;
    static String field1158 = "color1";
    static float field1159;
    static float field1160;
    static float field1161;
    static String field1162 = "color2";
    static float field1163;
    static float field1164;
    static float field1165;
    static String field1166 = "color3";
    static float field1167;
    static float field1168;
    static float field1169;
    static String field1170 = "size";
    static String field1171 = "radius";
    static String field1172 = "color";
    static String field1173 = "size";
    static String field1174 = "time";
    static String field1175 = "outlineWidth";
    static String field1176 = "alpha";
    static String field1177 = "uvOffset";
    static String field1178 = "uvScale";
    static float field1179;
    static float field1180;
    static float field1181;
    static float field1182;
    static String field1183 = "u_size";
    static String field1184 = "u_radius";
    static String field1185 = "u_border_size";
    static String field1186 = "u_alpha";
    static String field1187 = "u_color_1";
    static float field1188;
    static float field1189;
    static float field1190;
    static String field1191 = "u_color_2";
    static float field1192;
    static float field1193;
    static float field1194;
    static String field1195 = "u_color_3";
    static float field1196;
    static float field1197;
    static float field1198;
    static String field1199 = "u_color_4";
    static float field1200;
    static float field1201;
    static float field1202;
    static float field1203;
    static float field1204;
    static float field1205;
    static float field1206;
    static String field1207 = "resolution";
    static String field1208 = "size";
    static String field1209 = "radius";
    static String field1210 = "alpha";
    static String field1211 = "color";
    static String field1212;
    static String field1213 = "size";
    static String field1214 = "radius";
    static String field1215 = "color0";
    static float field1216;
    static float field1217;
    static float field1218;
    static String field1219 = "color1";
    static float field1220;
    static float field1221;
    static float field1222;
    static String field1223 = "color2";
    static float field1224;
    static float field1225;
    static float field1226;
    static String field1227 = "color3";
    static float field1228;
    static float field1229;
    static float field1230;
    // param9 у всех вызывающих — это alpha (0..1), param10 — радиус свечения (6/100 px);
    // прежние восстановленные имена юниформов были перепутаны местами, из-за чего свечение
    // рисовалось непрозрачным квадратом без размытия.
    static String field1231 = "alpha";
    static String field1232 = "glowRadius";

    public static void method388(MatrixStack param0, Identifier param1, double param2, double param4, double param6, double param8, Color param10) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture((int)0, (Identifier)param1);
        TextureManager v11 = I.getTextureManager();
        AbstractTexture v12 = v11.getTexture(param1);
        if (v12 != null) {
            RenderSystem.bindTexture((int)v12.getGlId());
            GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
        }
        float v13 = (float)param10.getRed() / field1098;
        float v14 = (float)param10.getGreen() / field1099;
        float v15 = (float)param10.getBlue() / field1100;
        float v16 = (float)param10.getAlpha() / field1101;
        BufferBuilder v17 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        Matrix4f v18 = param0.peek().getPositionMatrix();
        v17.vertex(v18, (float)param2, (float)param4 + (float)param8, 0.0f).texture(0.0f, 1.0f).color(v13, v14, v15, v16);
        v17.vertex(v18, (float)param2 + (float)param6, (float)param4 + (float)param8, 0.0f).texture(1.0f, 1.0f).color(v13, v14, v15, v16);
        v17.vertex(v18, (float)param2 + (float)param6, (float)param4, 0.0f).texture(1.0f, 0.0f).color(v13, v14, v15, v16);
        v17.vertex(v18, (float)param2, (float)param4, 0.0f).texture(0.0f, 0.0f).color(v13, v14, v15, v16);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v17.end());
        RenderSystem.disableBlend();
    }

    public static void method389(Identifier param0, LivingEntity param1, float param2, float param3, float param4, float param5, float param6, float param7) {
        float v8 = param1 != null ? (float)param1.hurtTime : 0.0f;
        v8 = v8 > 0.0f ? Math.min(field1102, v8 / (float)param1.maxHurtTime) : 0.0f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ShaderProgram v9 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8355);
        RenderSystem.setShaderTexture((int)0, (Identifier)param0);
        v9.getUniform(field1103).set(param4, param5);
        v9.getUniform(field1104).set(param6);
        v9.getUniform(field1105).set(v8);
        v9.getUniform(field1106).set(param7);
        v9.getUniform(field1107).set(field1108);
        v9.getUniform(field1109).set(field1110);
        BufferBuilder v10 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        v10.vertex(param2, param3, 0.0f).texture(0.0f, 0.0f).color(255, 255, 255, (int)(param7 * field1111));
        v10.vertex(param2, param3 + param5, 0.0f).texture(0.0f, 1.0f).color(255, 255, 255, (int)(param7 * field1112));
        v10.vertex(param2 + param4, param3 + param5, 0.0f).texture(1.0f, 1.0f).color(255, 255, 255, (int)(param7 * field1113));
        v10.vertex(param2 + param4, param3, 0.0f).texture(1.0f, 0.0f).color(255, 255, 255, (int)(param7 * field1114));
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v10.end());
        RenderSystem.disableBlend();
    }

    public static void method390(float param0, float param1, float param2, float param3, float param4, int param5, float param6) {
        RenderUtil.method403(param0, param1, param2, param3, new Vector4f(param4, param4, param4, param4), param5, param6);
    }

    public static void method391(float param0, float param1, float param2, float param3, float param4, int param5, int param6, int param7, int param8, float param9, float param10) {
        RenderUtil.method404(param0, param1, param2, param3, new Vector4f(param4, param4, param4, param4), param5, param6, param7, param8, param9, param10);
    }

    public static void method392(float param0, float param1, float param2, float param3, float param4, float param5, int param6, float param7) {
        RenderUtil.method402(param0, param1, param2, param3, param4, param5, param6, param6, param6, param6, param7 / field1115);
    }

    public static void method393(float param0, float param1, float param2, float param3) {
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.getModelViewStack().translate(param0, param1, 0.0f);
        RenderSystem.getModelViewStack().scale(param2, param3, 1.0f);
        RenderSystem.getModelViewStack().translate(-param0, -param1, 0.0f);
    }

    public static void method394(DrawContext param0, float param1, float param2, float param3, float param4, int param5) {
        Render2DBatch.method2664(param0, param1, param2, param3, param4, param5);
    }

    public static void method395(float param0, float param1, float param2, float param3, Vector4f param4, int param5, int param6, int param7, int param8) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ShaderProgram v9 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8349);
        v9.getUniform(field1116).set(param2, param3);
        v9.getUniform(field1117).set(param4.x, param4.y, param4.z, param4.w);
        float v10 = (float)(param5 >> 24 & 0xFF) / field1118;
        float v11 = (float)(param6 >> 24 & 0xFF) / field1119;
        float v12 = (float)(param7 >> 24 & 0xFF) / field1120;
        float v13 = (float)(param8 >> 24 & 0xFF) / field1121;
        v9.getUniform(field1122).set((float)(param5 >> 16 & 0xFF) / field1123, (float)(param5 >> 8 & 0xFF) / field1124, (float)(param5 & 0xFF) / field1125, v10);
        v9.getUniform(field1126).set((float)(param6 >> 16 & 0xFF) / field1127, (float)(param6 >> 8 & 0xFF) / field1128, (float)(param6 & 0xFF) / field1129, v11);
        v9.getUniform(field1130).set((float)(param7 >> 16 & 0xFF) / field1131, (float)(param7 >> 8 & 0xFF) / field1132, (float)(param7 & 0xFF) / field1133, v12);
        v9.getUniform(field1134).set((float)(param8 >> 16 & 0xFF) / field1135, (float)(param8 >> 8 & 0xFF) / field1136, (float)(param8 & 0xFF) / field1137, v13);
        BlurRenderer.drawQuads(param0, (double)param1, (double)param2, param3);
        RenderSystem.disableBlend();
    }

    public static void method396() {
        RenderSystem.getModelViewStack().popMatrix();
    }

    public static void method397(float param0, float param1, float param2, float param3, float param4, int param5) {
        RenderUtil.method400(param0, param1, param2, param3, new Vector4f(param4, param4, param4, param4), param5);
    }

    public static void method398(DrawContext param0, ItemStack param1, float param2, float param3, float param4, int param5, float param6) {
        if (param1.isEmpty()) {
            return;
        }
        float v7 = (float)(param5 >> 16 & 0xFF) / field1138;
        float v8 = (float)(param5 >> 8 & 0xFF) / field1139;
        float v9 = (float)(param5 & 0xFF) / field1140;
        MatrixStack v10 = param0.getMatrices();
        v10.push();
        v10.translate(param2, param3, 0.0f);
        v10.scale(param4, param4, param4);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor((float)v7, (float)v8, (float)v9, (float)param6);
        param0.drawItem(param1, 0, 0);
        if (param1.getCount() != 1 || param1.isDamaged()) {
            int v15;
            int v14;
            int v12;
            v10.push();
            v10.translate(0.0f, 0.0f, field1141);
            if (param1.getCount() != 1) {
                String v11 = String.valueOf(param1.getCount());
                v12 = 17 - RenderUtil.I.textRenderer.getWidth(v11);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                v14 = (int)(param6 * field1142);
                v15 = v14 << 24 | field1143;
                v10.push();
                v10.translate(0.0f, 0.0f, field1144);
                VertexConsumerProvider.Immediate v16 = I.getBufferBuilders().getEntityVertexConsumers();
                RenderUtil.I.textRenderer.draw(v11, (float)v12, 9.0f, v15, true, v10.peek().getPositionMatrix(), (VertexConsumerProvider)v16, TextRenderer.TextLayerType.SEE_THROUGH, 0, field1145);
                v16.draw();
                v10.pop();
            }
            if (param1.isDamaged()) {
                int v11 = param1.getMaxDamage();
                v12 = param1.getDamage();
                int v13 = Math.round(field1146 - (float)v12 * field1147 / (float)v11);
                v14 = Math.round((1.0f - (float)v12 / (float)v11) * field1148);
                v15 = 255 - v14 << 16 | v14 << 8;
                int v16 = (int)(param6 * field1149);
                int v17 = v16 << 24 | v15 & field1150;
                int v18 = (int)(param6 * field1151);
                int v19 = v18 << 24;
                param0.fill(2, 13, 15, 15, v19);
                if (v13 > 0) {
                    param0.fill(2, 13, 2 + v13, 14, v17);
                }
            }
            v10.pop();
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        v10.pop();
    }

    public static void method399(float param0, float param1, float param2, float param3, Vector4f param4, int param5, int param6, int param7, int param8, float param9) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ShaderProgram v10 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8349);
        v10.getUniform(field1152).set(param2, param3);
        v10.getUniform(field1153).set(param4.x, param4.y, param4.z, param4.w);
        v10.getUniform(field1154).set((float)(param5 >> 16 & 0xFF) / field1155, (float)(param5 >> 8 & 0xFF) / field1156, (float)(param5 & 0xFF) / field1157, param9);
        v10.getUniform(field1158).set((float)(param6 >> 16 & 0xFF) / field1159, (float)(param6 >> 8 & 0xFF) / field1160, (float)(param6 & 0xFF) / field1161, param9);
        v10.getUniform(field1162).set((float)(param7 >> 16 & 0xFF) / field1163, (float)(param7 >> 8 & 0xFF) / field1164, (float)(param7 & 0xFF) / field1165, param9);
        v10.getUniform(field1166).set((float)(param8 >> 16 & 0xFF) / field1167, (float)(param8 >> 8 & 0xFF) / field1168, (float)(param8 & 0xFF) / field1169, param9);
        BlurRenderer.drawQuads(param0, (double)param1, (double)param2, param3);
        RenderSystem.disableBlend();
    }

    public static void method400(float param0, float param1, float param2, float param3, Vector4f param4, int param5) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ShaderProgram v6 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8344);
        v6.getUniform(field1170).set(param2, param3);
        v6.getUniform(field1171).set(param4.x, param4.y, param4.z, param4.w);
        v6.getUniform(field1172).set(ColorUtil.method1849(param5));
        BlurRenderer.drawQuads(param0, (double)param1, (double)param2, param3);
        RenderSystem.disableBlend();
    }

    public static void method401(Identifier param0, float param1, float param2, float param3, float param4, float param5, float param6, float param7) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ShaderProgram v8 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8357);
        RenderSystem.setShaderTexture((int)0, (Identifier)param0);
        float v10 = param1 - param5;
        float v11 = param2 - param5;
        float v12 = param3 + param5 * 2.0f;
        float v13 = param4 + param5 * 2.0f;
        float v14 = param5 / v12;
        float v15 = param5 / v13;
        float v16 = param3 / v12;
        float v17 = param4 / v13;
        v8.getUniform(field1173).set(v12, v13);
        v8.getUniform(field1174).set(param7);
        v8.getUniform(field1175).set(param5);
        v8.getUniform(field1176).set(param6);
        v8.getUniform(field1177).set(v14, v15);
        v8.getUniform(field1178).set(v16, v17);
        BufferBuilder v18 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        v18.vertex(v10, v11, 0.0f).texture(0.0f, 0.0f).color(255, 255, 255, (int)(param6 * field1179));
        v18.vertex(v10, v11 + v13, 0.0f).texture(0.0f, 1.0f).color(255, 255, 255, (int)(param6 * field1180));
        v18.vertex(v10 + v12, v11 + v13, 0.0f).texture(1.0f, 1.0f).color(255, 255, 255, (int)(param6 * field1181));
        v18.vertex(v10 + v12, v11, 0.0f).texture(1.0f, 0.0f).color(255, 255, 255, (int)(param6 * field1182));
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v18.end());
        RenderSystem.disableBlend();
    }

    public static void method402(float param0, float param1, float param2, float param3, float param4, float param5, int param6, int param7, int param8, int param9, float param10) {
        RenderSystem.enableBlend();
        ShaderProgram v11 = RenderSystem.setShader((ShaderProgramKey)Shaders.I);
        v11.getUniform(field1183).set(param2, param3);
        v11.getUniform(field1184).set(param4);
        v11.getUniform(field1185).set(param5);
        v11.getUniform(field1186).set(param10);
        v11.getUniform(field1187).set((float)(param6 >> 16 & 0xFF) / field1188, (float)(param6 >> 8 & 0xFF) / field1189, (float)(param6 & 0xFF) / field1190);
        v11.getUniform(field1191).set((float)(param7 >> 16 & 0xFF) / field1192, (float)(param7 >> 8 & 0xFF) / field1193, (float)(param7 & 0xFF) / field1194);
        v11.getUniform(field1195).set((float)(param8 >> 16 & 0xFF) / field1196, (float)(param8 >> 8 & 0xFF) / field1197, (float)(param8 & 0xFF) / field1198);
        v11.getUniform(field1199).set((float)(param9 >> 16 & 0xFF) / field1200, (float)(param9 >> 8 & 0xFF) / field1201, (float)(param9 & 0xFF) / field1202);
        BlurRenderer.drawQuads(param0, (double)param1, (double)param2, param3);
        RenderSystem.disableBlend();
    }

    public static void method403(float param0, float param1, float param2, float param3, Vector4f param4, int param5, float param6) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ShaderProgram v7 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8347);
        GlStateManager._bindTexture((int)KawaseBlur.field12561.field12556.getColorAttachment());
        float v8 = (float)(param5 >> 16 & 0xFF) / field1203;
        float v9 = (float)(param5 >> 8 & 0xFF) / field1204;
        float v10 = (float)(param5 & 0xFF) / field1205;
        float v11 = (float)(param5 >> 24 & 0xFF) / field1206;
        v7.getUniform(field1207).set((float)I.getWindow().getWidth(), (float)I.getWindow().getHeight());
        v7.getUniform(field1208).set(param2, param3);
        v7.getUniform(field1209).set(param4.x, param4.y, param4.z, param4.w);
        v7.getUniform(field1210).set(param6);
        v7.getUniform(field1211).set(v8, v9, v10, v11);
        BlurRenderer.drawQuads(param0, (double)param1, (double)param2, param3);
        RenderSystem.disableBlend();
    }

    public static void cfr_renamed_0(float param0, float param1, float param2) {
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.getModelViewStack().translate(param0, param1, 0.0f);
        RenderSystem.getModelViewStack().scale(param2, param2, 1.0f);
        RenderSystem.getModelViewStack().translate(-param0, -param1, 0.0f);
    }

    private RenderUtil() {
        throw new UnsupportedOperationException(field1212);
    }

    public static void method404(float param0, float param1, float param2, float param3, Vector4f param4, int param5, int param6, int param7, int param8, float param9, float param10) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ShaderProgram v11 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8351);
        v11.getUniform(field1213).set(param2, param3);
        v11.getUniform(field1214).set(param4.x, param4.y, param4.z, param4.w);
        v11.getUniform(field1215).set((float)(param5 >> 16 & 0xFF) / field1216, (float)(param5 >> 8 & 0xFF) / field1217, (float)(param5 & 0xFF) / field1218);
        v11.getUniform(field1219).set((float)(param6 >> 16 & 0xFF) / field1220, (float)(param6 >> 8 & 0xFF) / field1221, (float)(param6 & 0xFF) / field1222);
        v11.getUniform(field1223).set((float)(param7 >> 16 & 0xFF) / field1224, (float)(param7 >> 8 & 0xFF) / field1225, (float)(param7 & 0xFF) / field1226);
        v11.getUniform(field1227).set((float)(param8 >> 16 & 0xFF) / field1228, (float)(param8 >> 8 & 0xFF) / field1229, (float)(param8 & 0xFF) / field1230);
        v11.getUniform(field1231).set(param9);
        v11.getUniform(field1232).set(param10);
        BlurRenderer.drawQuads(param0, (double)param1, (double)param2, param3);
        RenderSystem.disableBlend();
    }

    static {
        field1098 = 255.0f;
        field1099 = 255.0f;
        field1100 = 255.0f;
        field1101 = 255.0f;
        field1203 = 255.0f;
        field1204 = 255.0f;
        field1205 = 255.0f;
        field1206 = 255.0f;
        field1115 = 255.0f;
        field1216 = 255.0f;
        field1217 = 255.0f;
        field1218 = 255.0f;
        field1220 = 255.0f;
        field1221 = 255.0f;
        field1222 = 255.0f;
        field1224 = 255.0f;
        field1225 = 255.0f;
        field1226 = 255.0f;
        field1228 = 255.0f;
        field1229 = 255.0f;
        field1230 = 255.0f;
        field1155 = 255.0f;
        field1156 = 255.0f;
        field1157 = 255.0f;
        field1159 = 255.0f;
        field1160 = 255.0f;
        field1161 = 255.0f;
        field1163 = 255.0f;
        field1164 = 255.0f;
        field1165 = 255.0f;
        field1167 = 255.0f;
        field1168 = 255.0f;
        field1169 = 255.0f;
        field1118 = 255.0f;
        field1119 = 255.0f;
        field1120 = 255.0f;
        field1121 = 255.0f;
        field1123 = 255.0f;
        field1124 = 255.0f;
        field1125 = 255.0f;
        field1127 = 255.0f;
        field1128 = 255.0f;
        field1129 = 255.0f;
        field1131 = 255.0f;
        field1132 = 255.0f;
        field1133 = 255.0f;
        field1135 = 255.0f;
        field1136 = 255.0f;
        field1137 = 255.0f;
        field1188 = 255.0f;
        field1189 = 255.0f;
        field1190 = 255.0f;
        field1192 = 255.0f;
        field1193 = 255.0f;
        field1194 = 255.0f;
        field1196 = 255.0f;
        field1197 = 255.0f;
        field1198 = 255.0f;
        field1200 = 255.0f;
        field1201 = 255.0f;
        field1202 = 255.0f;
        field1138 = 255.0f;
        field1139 = 255.0f;
        field1140 = 255.0f;
        field1141 = 200.0f;
        field1142 = 255.0f;
        field1143 = 0xFFFFFF;
        field1144 = 200.0f;
        field1145 = 0xF000F0;
        field1146 = 13.0f;
        field1147 = 13.0f;
        field1148 = 255.0f;
        field1149 = 255.0f;
        field1150 = 0xFFFFFF;
        field1151 = 255.0f;
        field1179 = 255.0f;
        field1180 = 255.0f;
        field1181 = 255.0f;
        field1182 = 255.0f;
        field1102 = 0.25f;
        field1108 = 64.0f;
        field1110 = 64.0f;
        field1111 = 255.0f;
        field1112 = 255.0f;
        field1113 = 255.0f;
        field1114 = 255.0f;
        field1212 = "This is a utility class and cannot be instantiated";
    }
}




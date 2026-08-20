/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Matrix4f
 *  sLM.4ZXYEo
 */
package minced.module.visual;
import minced.core.EventHandler;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import minced.core.ColorUtil;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventRender2D;
import minced.core.ThemeColors;
import minced.core.FadeAnimation;
import minced.core.SmoothValue;
import minced.core.EasingConstants;
import minced.core.Fonts;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.MAIN;
import minced.gui.PlayerArrowTarget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import sLM._4ZXYEo;

public class Arrows
extends Module {
    static String cfr_renamed_362;
    static String cfr_renamed_363;

    List field1255;
    static String field1256 = "Arrows";
    static String field1257 = "Отображает стрелки, указывающие на игроков за пределами экрана";
    static String field1258 = "Дистанция от прицела";
    static float field1259 = 25.0f;
    static float field1260 = 50.0f;
    SliderSetting field1261 = new SliderSetting(field1258, field1259, 1.0f, field1260, 1.0f);
    static String field1262 = "Размер стрелки";
    static float field1263 = 1.5f;
    static float field1264 = 0.1f;
    SliderSetting field1265 = new SliderSetting(field1262, 1.0f, 1.0f, field1263, field1264);
    static String field1266 = "Информация";
    BooleanSetting field1267 = new BooleanSetting(field1266, true);
    static String field1268 = "Отображать";
    static String field1269 = "Никнеймы";
    static String field1270 = "Дистанцию";
    MultiBooleanSetting field1271 = new MultiBooleanSetting(field1268, () -> (Boolean)this.field1267.getValue(), new BooleanSetting(field1269, true), new BooleanSetting(field1270, false));
    SmoothValue field1272 = new SmoothValue();
    SmoothValue field1273 = new SmoothValue();
    SmoothValue field1274 = new SmoothValue();
    SmoothValue I = new SmoothValue();
    static MinecraftClient field1275 = MinecraftClient.getInstance();
    static long field1276;
    static float field1277;
    static double field1278;
    static float field1279;
    static double field1280;
    static double field1281;
    static String field1282;
    static String field1283;
    static float field1284;
    static float field1285;
    static float field1286;
    static float field1287;
    static float field1288;
    static float field1289;
    static float field1290;
    static float field1291;
    static float field1292;
    static float field1293;
    static float field1294;
    static float field1295;
    static double field1296;
    static double field1297;
    static double field1298;
    static float field1299;
    static float field1300;
    static float field1301;
    static String field1302;
    static float field1303;
    static String field1304;
    static float field1305;
    static float field1306;
    static float field1307;
    static float field1308;
    static float field1309;
    static float field1310;
    static float field1311;
    static int field1312;
    static String field1313;
    static String field1314;
    static Identifier field1315;

    private void method437(List param12) {
        for (PlayerArrowTarget v3 : (Iterable<PlayerArrowTarget>)(new ArrayList(this.field1255))) {
            boolean v4 = param12.stream().anyMatch(param1 -> ((PlayerArrowTarget)param1).getPlayer() == v3.getPlayer());
            if (v4) continue;
            v3.getFadeAnimation().method3288(false);
            if (v3.getFadeAnimation().method3293()) continue;
            param12.add(v3);
        }
    }

    public Arrows() {
        super(field1256, field1257, Combat.Visuals);
        this.field1255 = new ArrayList();
        this.addSettings(this.field1267, this.field1271, this.field1261, this.field1265);
    }

    private List method438() {
        ArrayList<PlayerArrowTarget> v1 = new ArrayList<PlayerArrowTarget>();
        for (AbstractClientPlayerEntity v3 : Arrows.field1275.world.getPlayers()) {
            Optional<PlayerArrowTarget> v4 = this.field1255.stream().filter(param1 -> ((PlayerArrowTarget)param1).getPlayer() == v3).findFirst();
            if (v4.isPresent() && !this.method448((PlayerEntity)v3)) continue;
            PlayerArrowTarget v5 = new PlayerArrowTarget((PlayerEntity)v3, v4.map(PlayerArrowTarget::getFadeAnimation).orElse(new FadeAnimation(1.0f, Duration.ofMillis(field1276))));
            v1.add(v5);
        }
        return v1;
    }

    private void method439() {
        this.field1272.method3367();
        this.field1273.method3367();
        this.field1274.method3367();
        this.I.method3367();
        this.field1273.method3361(Arrows.field1275.player.sidewaysSpeed * field1277, field1278, EasingConstants.field11801);
        this.field1274.method3361(Arrows.field1275.player.forwardSpeed * field1279, field1280, EasingConstants.field11801);
        this.I.method3343(Arrows.field1275.gameRenderer.getCamera().getYaw(), field1281, EasingConstants.field11801, true);
    }

    private void method440(DrawContext param1, float param2, float param3, float param4, int param5) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFuncSeparate((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE, (GlStateManager.SrcFactor)GlStateManager.SrcFactor.ZERO, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        RenderSystem.setShaderTexture((int)0, (Identifier)Identifier.of((String)field1282, (String)field1283));
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR_TEX_LIGHTMAP);
        RenderSystem.depthMask((boolean)false);
        RenderSystem.disableCull();
        Matrix4f v6 = param1.getMatrices().peek().getPositionMatrix();
        float v7 = param4 / 2.0f;
        this.method447(v6, param2, param3, param4, param5);
        this.method443(v6, param2 - v7, param3 - v7, param2 + v7, param3 + v7, param5);
        RenderSystem.enableCull();
        RenderSystem.depthMask((boolean)true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private void method443(Matrix4f param1, float param2, float param3, float param4, float param5, int param6) {
        float v7 = (float)(param6 >> 24 & 0xFF) / field1284;
        float v8 = (float)(param6 >> 16 & 0xFF) / field1285;
        float v9 = (float)(param6 >> 8 & 0xFF) / field1286;
        float v10 = (float)(param6 & 0xFF) / field1287;
        Tessellator v11 = Tessellator.getInstance();
        BufferBuilder v12 = v11.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
        v12.vertex(param1, param2, param5, 0.0f).color(v8, v9, v10, v7).texture(field1288, field1289).light(0, 240);
        v12.vertex(param1, param4, param5, 0.0f).color(v8, v9, v10, v7).texture(field1290, field1291).light(0, 240);
        v12.vertex(param1, param4, param3, 0.0f).color(v8, v9, v10, v7).texture(field1292, field1293).light(0, 240);
        v12.vertex(param1, param2, param3, 0.0f).color(v8, v9, v10, v7).texture(field1294, field1295).light(0, 240);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v12.end());
    }

    private float method444(PlayerEntity param1, float param2) {
        Camera v3 = Arrows.field1275.gameRenderer.getCamera();
        if (v3 == null) {
            return 0.0f;
        }
        double v4 = MathHelper.lerp((double)param2, (double)param1.prevX, (double)param1.getX()) - v3.getPos().getX();
        double v6 = MathHelper.lerp((double)param2, (double)param1.prevZ, (double)param1.getZ()) - v3.getPos().getZ();
        double v8 = this.I.method3351() * field1296;
        double v10 = MathHelper.cos((float)((float)v8));
        double v12 = MathHelper.sin((float)((float)v8));
        double v14 = -(v6 * v10 - v4 * v12);
        double v16 = -(v4 * v10 + v6 * v12);
        return (float)(Math.atan2(v14, v16) * field1297 / field1298);
    }

    @EventHandler(2)
    public void method445(EventRender2D param1) {
        if (Arrows.field1275.player == null || Arrows.field1275.world == null) {
            return;
        }
        DrawContext v2 = param1.method2997();
        float v3 = param1.method2996();
        this.method439();
        float v4 = this.method446();
        this.field1272.method3343(v4, 1.0, EasingConstants.field11801, false);
        List v5 = this.method438();
        this.method437(v5);
        this.field1255.clear();
        this.field1255.addAll(v5);
        float v6 = (float)field1275.getWindow().getScaledWidth() / 2.0f;
        float v7 = (float)field1275.getWindow().getScaledHeight() / 2.0f;
        for (PlayerArrowTarget v9 : (Iterable<PlayerArrowTarget>)(this.field1255)) {
            PlayerEntity v10 = v9.player;
            if (!this.method448(v10)) continue;
            float v11 = this.method444(v10, v3);
            float v12 = v9.fadeAnimation.method3294();
            float v13 = (float)(this.field1272.method3351() * (double)v12);
            float v14 = v13 * MathHelper.cos((float)((float)Math.toRadians(v11))) + v6;
            float v15 = v13 * MathHelper.sin((float)((float)Math.toRadians(v11))) + v7;
            v14 += (float)this.field1273.method3351();
            v15 += (float)this.field1274.method3351();
            int v16 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
            boolean v17 = Minced.getInstance().getFriendManager().cfr_renamed_4(v10.getGameProfile().getName());
            int v18 = v17 ? Color.GREEN.getRGB() : ColorUtil.method1853(v16, v12);
            int v19 = ColorUtil.method1853(-1, v12);
            float v20 = field1299 * ((Float)this.field1265.getValue()).floatValue();
            v2.getMatrices().push();
            v2.getMatrices().translate(v14, v15, 0.0f);
            v2.getMatrices().push();
            v2.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(v11 + field1300));
            this.method440(v2, 0.0f, 0.0f, v20, v18);
            v2.getMatrices().pop();
            if (((Boolean)this.field1267.getValue()).booleanValue()) {
                float v21 = v20 / 2.0f - field1301;
                if (this.field1271.method1898(field1302).booleanValue()) {
                    Fonts.field12494[13].method2711(v2, v10.getName().getString(), 0.0f - Fonts.field12494[13].method2719(v10.getName().getString()) / 2.0f, -v21 + field1303, v19);
                }
                if (this.field1271.method1898(field1304).booleanValue()) {
                    Fonts.field12494[13].method2711(v2, (String)((Object)_4ZXYEo.弟3("makeConcatWithConstants", new Object[]{}, (int)((int)Arrows.field1275.player.distanceTo((Entity)v10)))), 0.0f - Fonts.field12494[13].method2719((String)((Object)_4ZXYEo.弟в("makeConcatWithConstants", new Object[]{}, (int)((int)Arrows.field1275.player.distanceTo((Entity)v10))))) / 2.0f, v21 - field1305, v19);
                }
            }
            v2.getMatrices().pop();
        }
    }

    private float method446() {
        float v1 = field1306 + ((Float)this.field1261.getValue()).floatValue();
        if (Arrows.field1275.currentScreen instanceof InventoryScreen) {
            v1 += field1307;
        }
        if (Arrows.field1275.player.isSneaking()) {
            v1 -= field1308;
        }
        return v1 += field1309;
    }

    private void method447(Matrix4f param1, float param2, float param3, float param4, int param5) {
        float v7 = field1310;
        float v8 = field1311;
        for (int v9 = 8; v9 >= 1; --v9) {
            float v10 = 1.0f + (float)(v9 - 1) * v8;
            float v11 = v7 / 8.0f * (float)(8 - v9 + 1) / 8.0f;
            float v12 = param4 * v10;
            float v13 = v12 / 2.0f;
            int v14 = (int)((float)(param5 >> 24 & 0xFF) * v11);
            int v15 = v14 << 24 | param5 & field1312;
            this.method443(param1, param2 - v13, param3 - v13, param2 + v13, param3 + v13, v15);
        }
    }

    private boolean method448(PlayerEntity param1) {
        return param1 != Arrows.field1275.player;
    }

    public static void method449() {
        field1315 = Identifier.of((String)field1313, (String)field1314);
        field1275 = MinecraftClient.getInstance();
    }

    static {
        field1299 = 70.0f;
        field1300 = 90.0f;
        field1301 = 6.0f;
        field1302 = "Никнеймы";
        field1303 = 20.0f;
        field1304 = "Дистанцию";
        cfr_renamed_362 = "\u0001m";
        cfr_renamed_363 = "\u0001m";
        field1305 = 20.0f;
        field1277 = 5.0f;
        field1278 = 0.75;
        field1279 = 5.0f;
        field1280 = 0.75;
        field1281 = 0.75;
        field1306 = 35.0f;
        field1307 = 80.0f;
        field1308 = 20.0f;
        field1309 = 10.0f;
        field1276 = 200L;
        field1296 = Math.PI / 180;
        field1297 = 180.0;
        field1298 = Math.PI;
        field1310 = 0.7f;
        field1311 = 0.1f;
        field1312 = 0xFFFFFF;
        field1282 = "minced";
        field1283 = "textures/triangle.png";
        field1284 = 255.0f;
        field1285 = 255.0f;
        field1286 = 255.0f;
        field1287 = 255.0f;
        field1288 = 0.002f;
        field1289 = 0.998f;
        field1290 = 0.998f;
        field1291 = 0.998f;
        field1292 = 0.998f;
        field1293 = 0.002f;
        field1294 = 0.002f;
        field1295 = 0.002f;
        field1313 = "minced";
        field1314 = "textures/arrow.png";
    }
}




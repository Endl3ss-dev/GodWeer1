/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package minced.module.movement;
import minced.core.EventHandler;

import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.EventFireworkBoost;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SuperFirework
extends Module {
    BooleanSetting field5266 = new BooleanSetting(field5311, true);
    ModeSetting field5267 = new ModeSetting(field5312, field5313, this.field5266::getValue, field5314, field5315, field5316, field5317);
    static String field5268;
    static String field5269;
    static String field5270;
    static String field5271;
    static float field5272;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field5273;
    static String field5274;
    static String field5275;
    static float[] field5276;
    static float[] field5277;
    static float[] field5278;
    static float[] field5279;
    static String field5280;
    static String field5281;
    static String field5282;
    static String field5283;
    static float field5284;
    SliderSetting[] field5285;
    static String field5286;
    static float field5287;
    static float field5288;
    static float field5289;
    static float field5290;
    static float field5291;
    static float field5292;
    static float field5293;
    static float field5294;
    static float field5295;
    static float field5296;
    static float field5297;
    static float field5298;
    static float field5299;
    static float field5300;
    static float field5301;
    static float field5302;
    static float field5303;
    static float field5304;
    static float field5305;
    static float field5306;
    static float field5307;
    static String field5308;
    static String field5309;
    static String field5310;
    static String field5311;
    static String field5312;
    static String field5313;
    static String field5314;
    static String field5315;
    static String field5316;
    static String field5317;
    static String field5318;
    static float field5319;
    static float field5320;
    static float field5321;
    static float field5322;
    SliderSetting field5323 = new SliderSetting(field5318, field5319, field5320, field5321, field5322, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5280));
    static String field5324;
    static float field5325;
    static float field5326;
    static float field5327;
    static float field5328;
    SliderSetting field5329 = new SliderSetting(field5324, field5325, field5326, field5327, field5328, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5474));
    static String field5330;
    static float field5331;
    static float field5332;
    static float field5333;
    static float field5334;
    SliderSetting field5335 = new SliderSetting(field5330, field5331, field5332, field5333, field5334, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5269));
    static String field5336;
    static float field5337;
    static float field5338;
    static float field5339;
    static float field5340;
    SliderSetting field5341 = new SliderSetting(field5336, field5337, field5338, field5339, field5340, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5470));
    static String field5342;
    static float field5343;
    static float field5344;
    static float field5345;
    static float field5346;
    SliderSetting field5347 = new SliderSetting(field5342, field5343, field5344, field5345, field5346, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5471));
    static String field5348;
    static float field5349;
    static float field5350;
    static float field5351;
    static float field5352;
    SliderSetting field5353 = new SliderSetting(field5348, field5349, field5350, field5351, field5352, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5286));
    static String field5354;
    static float field5355;
    static float field5356;
    static float field5357;
    static float field5358;
    SliderSetting field5359 = new SliderSetting(field5354, field5355, field5356, field5357, field5358, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5535));
    static String field5360;
    static float field5361;
    static float field5362;
    static float field5363;
    static float field5364;
    SliderSetting field5365 = new SliderSetting(field5360, field5361, field5362, field5363, field5364, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5473));
    static String field5366;
    static float field5367;
    static float field5368;
    static float field5369;
    static float field5370;
    SliderSetting field5371 = new SliderSetting(field5366, field5367, field5368, field5369, field5370, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5308));
    static String field5372;
    static float field5373;
    static float field5374;
    static float field5375;
    static float field5376;
    SliderSetting field5377 = new SliderSetting(field5372, field5373, field5374, field5375, field5376, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5281));
    static String field5378;
    static float field5379;
    static float field5380;
    static float field5381;
    static float field5382;
    SliderSetting field5383 = new SliderSetting(field5378, field5379, field5380, field5381, field5382, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5270));
    static String field5384;
    static float field5385;
    static float field5386;
    static float field5387;
    static float field5388;
    SliderSetting field5389 = new SliderSetting(field5384, field5385, field5386, field5387, field5388, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5268));
    static String field5390;
    static float field5391;
    static float field5392;
    static float field5393;
    static float field5394;
    SliderSetting field5395 = new SliderSetting(field5390, field5391, field5392, field5393, field5394, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5271));
    static String field5396;
    static float field5397;
    static float field5398;
    static float field5399;
    static float field5400;
    SliderSetting field5401 = new SliderSetting(field5396, field5397, field5398, field5399, field5400, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5282));
    static String field5402;
    static float field5403;
    static float field5404;
    static float field5405;
    static float field5406;
    SliderSetting field5407 = new SliderSetting(field5402, field5403, field5404, field5405, field5406, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5283));
    static String field5408;
    static float field5409;
    static float field5410;
    static float field5411;
    static float field5412;
    SliderSetting field5413 = new SliderSetting(field5408, field5409, field5410, field5411, field5412, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5472));
    static String field5414;
    static float field5415;
    static float field5416;
    static float field5417;
    static float field5418;
    SliderSetting field5419 = new SliderSetting(field5414, field5415, field5416, field5417, field5418, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5463));
    static String field5420;
    static float field5421;
    static float field5422;
    static float field5423;
    static float field5424;
    SliderSetting field5425 = new SliderSetting(field5420, field5421, field5422, field5423, field5424, () -> (Boolean)this.field5266.getValue() != false && this.field5267.isMode(field5464));
    SliderSetting[] field5426;
    static float field5427;
    static float field5428;
    static float field5429;
    static float field5430;
    static float field5431;
    static float field5432;
    static float field5433;
    static float field5434;
    static float field5435;
    static float field5436;
    static float field5437;
    static float field5438;
    static float field5439;
    static float field5440;
    static float field5441;
    static float field5442;
    static float field5443;
    static float field5444;
    static float field5445;
    static float field5446;
    static float field5447;
    static float field5448;
    static float field5449;
    static float field5450;
    static float field5451;
    static float field5452;
    static float field5453;
    static float field5454;
    static float field5455;
    static float field5456;
    static float field5457;
    static float field5458;
    static float field5459;
    static float field5460;
    static float field5461;
    static float field5462;
    static String field5463;
    static String field5464;
    static float field5465;
    static float field5466;
    static float field5467;
    static float field5468;
    static float field5469;
    static String field5470;
    static String field5471;
    static String field5472;
    static String field5473;
    static String field5474;
    static float field5475;
    static float field5476;
    static float field5477;
    static float field5478;
    static float field5479;
    static float field5480;
    static float field5481;
    static float field5482;
    static float field5483;
    static float field5484;
    static float field5485;
    static float field5486;
    static float field5487;
    static float field5488;
    static float field5489;
    static float field5490;
    static float field5491;
    static float field5492;
    static float field5493;
    static float field5494;
    static float field5495;
    static float field5496;
    static float field5497;
    static float field5498;
    static float field5499;
    static float field5500;
    static float field5501;
    static float field5502;
    static float field5503;
    static float field5504;
    static float field5505;
    static float field5506;
    static float field5507;
    static float field5508;
    static float field5509;
    static float field5510;
    static float field5511;
    static float field5512;
    static float field5513;
    static float field5514;
    static float field5515;
    static float field5516;
    static float field5517;
    static float field5518;
    static float field5519;
    static float field5520;
    static float field5521;
    static float field5522;
    static float field5523;
    static float field5524;
    static float field5525;
    static float field5526;
    static float field5527;
    static float field5528;
    static float field5529;
    static float field5530;
    static float field5531;
    static float field5532;
    static float field5533;
    static float field5534;
    static String field5535;

    public SuperFirework() {
        super(field5309, field5310, Combat.Movement);
        this.field5285 = new SliderSetting[]{this.field5323, this.field5329, this.field5335, this.field5341, this.field5347, this.field5353, this.field5359, this.field5365, this.field5371};
        this.field5426 = new SliderSetting[]{this.field5377, this.field5383, this.field5389, this.field5395, this.field5401, this.field5407, this.field5413, this.field5419, this.field5425};
        this.addSettings(this.field5266, this.field5267, this.field5323, this.field5329, this.field5335, this.field5341, this.field5347, this.field5353, this.field5359, this.field5365, this.field5371, this.field5377, this.field5383, this.field5389, this.field5395, this.field5401, this.field5407, this.field5413, this.field5419, this.field5425);
    }

    private static float method1269(float[] param0, float param1) {
        int v2 = (int)(param1 / field5272);
        if (v2 >= param0.length) {
            v2 = param0.length - 1;
        }
        if (v2 < 0) {
            v2 = 0;
        }
        return param0[v2];
    }

    @EventHandler(2)
    public void method1270(EventFireworkBoost param1) {
        float v5;
        float v4;
        if (!((Boolean)this.field5266.getValue()).booleanValue() || SuperFirework.I.player == null) {
            return;
        }
        float v2 = MathHelper.wrapDegrees((float)SuperFirework.I.player.getYaw());
        float v3 = SuperFirework.I.player.getPitch();
        if (this.field5267.isMode(field5273)) {
            v4 = this.method1275(v2);
            v5 = this.method1282(v3);
            if (v5 > v4) {
                v4 = v5;
            }
        } else if (this.field5267.isMode(field5274)) {
            v4 = this.method1289(v3, v2);
            v5 = this.method1277(v3);
        } else if (this.field5267.isMode(field5275)) {
            v4 = SuperFirework.method1269(field5276, this.method1283(v2));
            v5 = SuperFirework.method1269(field5277, this.method1283(v3));
        } else {
            v4 = SuperFirework.method1269(field5278, this.method1283(v2));
            v5 = SuperFirework.method1269(field5279, this.method1283(v3));
            if (v5 > v4) {
                v4 = v5;
            }
        }
        param1.method3590(new Vec3d((double)v4, (double)v5, (double)v4));
    }

    private float method1275(float param1) {
        float v2 = this.method1283(param1);
        int v3 = (int)(v2 / field5284);
        if (v3 >= this.field5285.length) {
            v3 = this.field5285.length - 1;
        }
        if (v3 < 0) {
            v3 = 0;
        }
        return ((Float)this.field5285[v3].getValue()).floatValue();
    }

    private float method1277(float param1) {
        float v2 = Math.abs(param1);
        if (v2 >= field5287 && v2 <= field5288) {
            return field5289;
        }
        if (v2 >= field5290 && v2 <= field5291) {
            return 2.0f;
        }
        if (v2 >= field5292 && v2 <= field5293) {
            return field5294;
        }
        if (v2 >= field5295 && v2 <= field5296) {
            return field5297;
        }
        if (v2 >= field5298 && v2 <= field5299) {
            return field5300;
        }
        if (v2 >= field5301 && v2 <= field5302) {
            return field5303;
        }
        if (v2 >= field5304 && v2 <= field5305) {
            return field5306;
        }
        return field5307;
    }

    public static void method1279() {
        field5276 = new float[]{field5427, field5428, field5429, field5430, field5431, field5432, field5433, field5434, field5435};
        field5277 = new float[]{field5436, field5437, field5438, field5439, field5440, field5441, field5442, field5443, field5444};
        field5278 = new float[]{field5445, field5446, field5447, field5448, field5449, field5450, field5451, field5452, field5453};
        field5279 = new float[]{field5454, field5455, field5456, field5457, field5458, field5459, field5460, field5461, field5462};
    }

    private float method1282(float param1) {
        float v2 = this.method1283(param1);
        int v3 = (int)(v2 / field5465);
        if (v3 >= this.field5426.length) {
            v3 = this.field5426.length - 1;
        }
        if (v3 < 0) {
            v3 = 0;
        }
        return ((Float)this.field5426[v3].getValue()).floatValue();
    }

    private float method1283(float param1) {
        float v2 = Math.abs(param1);
        if (v2 > field5466) {
            v2 = field5467 - v2;
        }
        if (v2 > field5468) {
            v2 = field5469 - v2;
        }
        return v2;
    }

    private float method1289(float param1, float param2) {
        float v3 = Math.abs(param1);
        float v4 = Math.abs(MathHelper.wrapDegrees((float)param2) % field5475);
        float v5 = v3 >= field5476 && v3 <= field5477 ? 2.0f : (v3 >= field5478 && v3 <= field5479 ? field5480 : (v3 >= field5481 && v3 <= field5482 ? field5483 : (v4 >= field5484 && v4 <= field5485 || v3 >= field5486 && v3 <= field5487 ? field5488 : (v4 >= field5489 && v4 <= field5490 || v3 >= field5491 && v3 <= field5492 ? field5493 : (v4 >= field5494 && v4 <= field5495 || v3 >= field5496 && v3 <= field5497 ? field5498 : (v4 >= field5499 && v4 <= field5500 || v3 >= field5501 && v3 <= field5502 ? field5503 : (v4 >= field5504 && v4 <= field5505 || v3 >= field5506 && v3 <= field5507 ? field5508 : (v4 >= field5509 && v4 <= field5510 || v3 >= field5511 && v3 <= field5512 ? field5513 : (v4 >= field5514 && v4 <= field5515 || v3 >= field5516 && v3 <= field5517 ? field5518 : (v4 >= field5519 && v4 <= field5520 || v3 >= field5521 && v3 <= field5522 ? field5523 : (v4 >= field5524 && v4 <= field5525 || v3 >= field5526 && v3 <= field5527 ? field5528 : (v4 <= field5529 || v3 <= field5530 ? field5531 : field5532))))))))))));
        return param1 > field5533 ? v5 - field5534 : v5;
    }

    static {
        field5309 = "SuperFirework";
        field5310 = "Кастомный буст фейерверка на элитре";
        field5311 = "Ускорение";
        field5312 = "Режим ускорения";
        field5313 = "ReallyWorld";
        field5314 = "ReallyWorld";
        field5315 = "Bravo";
        field5316 = "BravoGrief";
        field5317 = "Кастомный";
        field5318 = "XZ 0-5°";
        field5319 = 1.52f;
        field5320 = 1.5f;
        field5321 = 3.0f;
        field5322 = 0.01f;
        field5324 = "XZ 5-10°";
        field5325 = 1.53f;
        field5326 = 1.5f;
        field5327 = 3.0f;
        field5328 = 0.01f;
        field5330 = "XZ 10-15°";
        field5331 = 1.54f;
        field5332 = 1.5f;
        field5333 = 3.0f;
        field5334 = 0.01f;
        field5336 = "XZ 15-20°";
        field5337 = 1.55f;
        field5338 = 1.5f;
        field5339 = 3.0f;
        field5340 = 0.01f;
        field5342 = "XZ 20-25°";
        field5343 = 1.56f;
        field5344 = 1.5f;
        field5345 = 3.0f;
        field5346 = 0.01f;
        field5348 = "XZ 25-30°";
        field5349 = 1.57f;
        field5350 = 1.5f;
        field5351 = 3.0f;
        field5352 = 0.01f;
        field5354 = "XZ 30-35°";
        field5355 = 1.58f;
        field5356 = 1.5f;
        field5357 = 3.0f;
        field5358 = 0.01f;
        field5360 = "XZ 35-40°";
        field5361 = 1.59f;
        field5362 = 1.5f;
        field5363 = 3.0f;
        field5364 = 0.01f;
        field5366 = "XZ 40-45°";
        field5367 = 1.6f;
        field5368 = 1.5f;
        field5369 = 3.0f;
        field5370 = 0.01f;
        field5372 = "Y 0-5°";
        field5373 = 1.51f;
        field5374 = 1.5f;
        field5375 = 3.0f;
        field5376 = 0.01f;
        field5378 = "Y 5-10°";
        field5379 = 1.52f;
        field5380 = 1.5f;
        field5381 = 3.0f;
        field5382 = 0.01f;
        field5384 = "Y 10-15°";
        field5385 = 1.53f;
        field5386 = 1.5f;
        field5387 = 3.0f;
        field5388 = 0.01f;
        field5390 = "Y 15-20°";
        field5391 = 1.54f;
        field5392 = 1.5f;
        field5393 = 3.0f;
        field5394 = 0.01f;
        field5396 = "Y 20-25°";
        field5397 = 1.55f;
        field5398 = 1.5f;
        field5399 = 3.0f;
        field5400 = 0.01f;
        field5402 = "Y 25-30°";
        field5403 = 1.56f;
        field5404 = 1.5f;
        field5405 = 3.0f;
        field5406 = 0.01f;
        field5408 = "Y 30-35°";
        field5409 = 1.57f;
        field5410 = 1.5f;
        field5411 = 3.0f;
        field5412 = 0.01f;
        field5414 = "Y 35-40°";
        field5415 = 1.58f;
        field5416 = 1.5f;
        field5417 = 3.0f;
        field5418 = 0.01f;
        field5420 = "Y 40-45°";
        field5421 = 1.59f;
        field5422 = 1.5f;
        field5423 = 3.0f;
        field5424 = 0.01f;
        field5273 = "Кастомный";
        field5274 = "Bravo";
        field5275 = "BravoGrief";
        field5284 = 5.0f;
        field5465 = 5.0f;
        field5466 = 90.0f;
        field5467 = 180.0f;
        field5468 = 45.0f;
        field5469 = 90.0f;
        field5272 = 5.0f;
        field5475 = 90.0f;
        field5476 = 38.0f;
        field5477 = 52.0f;
        field5478 = 32.0f;
        field5479 = 58.0f;
        field5480 = 1.96f;
        field5481 = 28.0f;
        field5482 = 62.0f;
        field5483 = 1.95f;
        field5484 = 29.0f;
        field5485 = 61.0f;
        field5486 = 29.0f;
        field5487 = 61.0f;
        field5488 = 1.963f;
        field5489 = 28.0f;
        field5490 = 60.0f;
        field5491 = 28.0f;
        field5492 = 60.0f;
        field5493 = 1.954f;
        field5494 = 26.0f;
        field5495 = 64.0f;
        field5496 = 26.0f;
        field5497 = 64.0f;
        field5498 = 1.874f;
        field5499 = 24.0f;
        field5500 = 66.0f;
        field5501 = 24.0f;
        field5502 = 66.0f;
        field5503 = 1.75f;
        field5504 = 15.0f;
        field5505 = 75.0f;
        field5506 = 15.0f;
        field5507 = 75.0f;
        field5508 = 1.75f;
        field5509 = 13.0f;
        field5510 = 77.0f;
        field5511 = 13.0f;
        field5512 = 77.0f;
        field5513 = 1.75f;
        field5514 = 12.0f;
        field5515 = 78.0f;
        field5516 = 12.0f;
        field5517 = 78.0f;
        field5518 = 1.75f;
        field5519 = 8.0f;
        field5520 = 82.0f;
        field5521 = 11.0f;
        field5522 = 79.0f;
        field5523 = 1.75f;
        field5524 = 5.0f;
        field5525 = 85.0f;
        field5526 = 8.0f;
        field5527 = 82.0f;
        field5528 = 1.67f;
        field5529 = 90.0f;
        field5530 = 90.0f;
        field5531 = 1.67f;
        field5532 = 1.66f;
        field5533 = 15.0f;
        field5534 = 0.068f;
        field5287 = 37.0f;
        field5288 = 38.0f;
        field5289 = 2.03f;
        field5290 = 25.0f;
        field5291 = 30.0f;
        field5292 = 35.0f;
        field5293 = 45.0f;
        field5294 = 1.99f;
        field5295 = 40.0f;
        field5296 = 50.0f;
        field5297 = 1.97f;
        field5298 = 50.0f;
        field5299 = 60.0f;
        field5300 = 1.96f;
        field5301 = 51.0f;
        field5302 = 61.0f;
        field5303 = 1.85f;
        field5304 = 52.0f;
        field5305 = 65.0f;
        field5306 = 1.8f;
        field5307 = 1.59f;
        field5464 = "Кастомный";
        field5463 = "Кастомный";
        field5472 = "Кастомный";
        field5283 = "Кастомный";
        field5282 = "Кастомный";
        field5271 = "Кастомный";
        field5268 = "Кастомный";
        field5270 = "Кастомный";
        field5281 = "Кастомный";
        field5308 = "Кастомный";
        field5473 = "Кастомный";
        field5535 = "Кастомный";
        field5286 = "Кастомный";
        field5471 = "Кастомный";
        field5470 = "Кастомный";
        field5269 = "Кастомный";
        field5474 = "Кастомный";
        field5280 = "Кастомный";
        field5427 = 1.67f;
        field5428 = 1.68f;
        field5429 = 1.69f;
        field5430 = 1.72f;
        field5431 = 1.72f;
        field5432 = 1.73f;
        field5433 = 1.74f;
        field5434 = 1.75f;
        field5435 = 1.76f;
        field5436 = 1.72f;
        field5437 = 1.72f;
        field5438 = 1.72f;
        field5439 = 1.72f;
        field5440 = 1.72f;
        field5441 = 1.73f;
        field5442 = 1.74f;
        field5443 = 1.76f;
        field5444 = 1.8f;
        field5445 = 1.6f;
        field5446 = 1.63f;
        field5447 = 1.66f;
        field5448 = 1.71f;
        field5449 = 1.73f;
        field5450 = 1.81f;
        field5451 = 1.81f;
        field5452 = 1.81f;
        field5453 = 1.81f;
        field5454 = 1.6f;
        field5455 = 1.62f;
        field5456 = 1.6f;
        field5457 = 1.62f;
        field5458 = 1.64f;
        field5459 = 1.68f;
        field5460 = 1.75f;
        field5461 = 1.9f;
        field5462 = 2.16f;
        SuperFirework.method1279();
    }
}




/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.impl.client.screen.ScreenExtensions
 *  net.fabricmc.fabric.mixin.screen.ScreenAccessor
 *  net.minecraft.client.gui.Drawable
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.ParentElement
 *  net.minecraft.client.gui.navigation.Navigable
 *  net.minecraft.text.Style
 */
package minced.gui;

import java.util.HashMap;
import java.util.Map;
import minced.core.ColorUtil;
import net.fabricmc.fabric.impl.client.screen.ScreenExtensions;
import net.fabricmc.fabric.mixin.screen.ScreenAccessor;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.navigation.Navigable;
import net.minecraft.text.Style;

public class StyledTextGlyphs
implements  Navigable,
Element,
ParentElement,
Drawable
{
    @Override
    public boolean isDragging() { return false; }

    @Override
    public void setDragging(boolean p0) { }

    @Override
    public net.minecraft.client.gui.Element getFocused() { return null; }

    @Override
    public void setFocused(net.minecraft.client.gui.Element p0) { }

    @Override
    public java.util.List<? extends net.minecraft.client.gui.Element> children() { return java.util.List.of(); }

    @Override
    public void render(net.minecraft.client.gui.DrawContext p0, int p1, int p2, float p3) { }



    static Map field12259;
    static int[] field12260;
    static Map field12261;
    static float field12262;
    static Map field12263;
    static Map field12264;
    static int field12265;
    static int field12266;
    static int field12267;
    static int field12268;
    static int field12269;
    static int field12270;
    static int field12271;
    static int field12272;
    static int field12273;
    static int field12274;
    static int field12275;
    static int field12276;
    static int field12277;
    static int field12278;
    static int field12279;
    static int field12280;
    static int field12281;
    static int field12282;
    static int field12283;
    static int field12284;
    static int field12285;
    static int field12286;
    static int field12287;
    static int field12288;
    static int field12289;
    static int field12290;
    static int field12291;
    static int field12292;
    static String field12293;
    static int field12294;
    static String field12295;
    static int field12296;
    static String field12297;
    static int field12298;
    static String field12299;
    static int field12300;
    static String field12301;
    static int field12302;
    static String field12303;
    static int field12304;
    static String field12305;
    static int field12306;
    static String field12307;
    static int field12308;
    static String field12309;
    static int field12310;
    static String field12311;
    static int field12312;
    static String field12313;
    static int field12314;
    static String field12315;
    static int field12316;
    static String field12317;
    static int field12318;
    static String field12319;
    static int field12320;
    static String field12321;
    static int field12322;
    static String field12323;
    static int field12324;
    static String field12325;
    static int field12326;
    static String field12327;
    static int field12328;
    static String field12329;
    static int field12330;
    static String field12331;
    static int field12332;
    static String field12333;
    static int field12334;
    static String field12335;
    static int field12336;
    static String field12337;
    static int field12338;
    static String field12339;
    static int field12340;
    static String field12341;
    static int field12342;
    static String field12343;
    static int field12344;
    static String field12345;
    static String field12346;
    static String field12347;
    static String field12348;
    static String field12349;
    static String field12350;
    static int field12351;
    static String field12352;
    static String field12353;
    static String field12354;
    static String field12355;
    static String field12356;
    static String field12357;
    static String field12358;
    static String field12359;
    static String field12360;
    static String field12361;
    static String field12362;
    static String field12363;
    static String field12364;
    static String field12365;
    static String field12366;
    static int field12367;
    static String field12368;
    static String field12369;
    static String field12370;
    static String field12371;
    static String field12372;
    static String field12373;
    static String field12374 = "field12374";
    static String field12375;
    static String field12376 = "field12376";
    static String field12377;
    static String field12378 = "field12378";
    static String field12379;
    static String field12380 = "field12380";
    static String field12381;
    static String field12382 = "field12382";
    static String field12383;
    static String field12384 = "field12384";
    static String field12385;
    static String field12386 = "field12386";
    static String field12387;
    static String field12388 = "field12388";
    static String field12389;
    static String field12390 = "field12390";
    static String field12391;
    static String field12392 = "field12392";
    static String field12393;
    static String field12394 = "field12394";
    static String field12395;
    static String field12396 = "field12396";
    static String field12397;
    static String field12398 = "field12398";
    static String field12399;
    static String field12400 = "field12400";
    static String field12401;
    static String field12402 = "field12402";
    static String field12403;
    static String field12404 = "field12404";
    static String field12405;
    static String field12406 = "field12406";
    static String field12407;
    static String field12408 = "field12408";
    static String field12409;
    static String field12410 = "field12410";
    static String field12411;
    static String field12412 = "field12412";
    static String field12413;
    static String field12414 = "field12414";
    static String field12415 = "field12415";
    static String field12416 = "field12416";
    static String field12417 = "field12417";
    static String field12418 = "field12418";
    static String field12419 = "field12419";
    static String field12420 = "field12420";
    static String field12421 = "field12421";
    static String field12422 = "field12422";
    static String field12423 = "field12423";
    static String field12424 = "field12424";
    static String field12425 = "field12425";
    static String field12426 = "field12426";
    static String field12427 = "field12427";
    static String field12428 = "field12428";
    static String field12429 = "field12429";
    static String field12430 = "field12430";
    static String field12431 = "field12431";
    static String field12432 = "field12432";
    static String field12433 = "field12433";
    static int field12434;
    static int field12435;
    static int field12436;
    static int field12437;
    static int field12438;
    static int field12439;
    static int field12440;
    static int field12441;
    static int field12442;
    static int field12443;
    static int field12444;
    static int field12445;
    static int field12446;
    static int field12447;
    static int field12448;
    static int field12449;
    static int field12450;
    static int field12451;
    static int field12452;
    static int field12453;
    static int field12454;
    static int field12455;
    static int field12456;
    static int field12457;
    static int field12458;
    static int field12459;
    static int field12460;

    public static boolean isCustomFontMarker(int param0, Style param1) {
        if (param0 == 97 && param1 != null && param1.getFont() != null) {
            String v2 = param1.getFont().toString();
            return field12259.containsKey(v2);
        }
        return false;
    }

    public static int getGlyphColor(int param0, int param1, int param2, float param3, int param4) {
        boolean v5 = false;
        for (int v9 : field12260) {
            if (v9 != param0) continue;
            v5 = true;
            break;
        }
        if (v5) {
            int v9;
            Object v6 = (Integer)field12261.get(param0);
            int v7 = ColorUtil.method1851((Integer)v6, field12262);
            float v8 = (float)param1 / (float)(param2 - 1);
            v9 = ColorUtil.method1850((Integer)v6, v7, v8);
            return ColorUtil.method1853(v9, param3);
        }
        return ColorUtil.method1853(param4, param3);
    }

    public static String resolveFontAlias(String param0) {
        return (String)field12259.getOrDefault(param0, param0);
    }

    public static String getGlyphText(int param0) {
        return (String)field12263.get(param0);
    }

    public static void initializeMappings() {
        field12263 = new HashMap();
        field12261 = new HashMap();
        field12259 = new HashMap();
        field12264 = new HashMap();
        field12260 = new int[]{field12265, field12266, field12267, field12268, field12269, field12270, field12271, field12272, field12273, field12274, field12275, field12276, field12277, field12278, field12279, field12280, field12281, field12282, field12283, field12284, field12285, field12286, field12287, field12288, field12289, field12290, field12291};
        field12263.put(9889, "");
        field12263.put(9733, "");
        field12263.put(field12292, field12293);
        field12263.put(field12294, field12295);
        field12263.put(field12296, field12297);
        field12263.put(field12298, field12299);
        field12263.put(field12300, field12301);
        field12263.put(field12302, field12303);
        field12263.put(field12304, field12305);
        field12263.put(field12306, field12307);
        field12263.put(field12308, field12309);
        field12263.put(field12310, field12311);
        field12263.put(field12312, field12313);
        field12263.put(field12314, field12315);
        field12263.put(field12316, field12317);
        field12263.put(field12318, field12319);
        field12263.put(field12320, field12321);
        field12263.put(field12322, field12323);
        field12263.put(field12324, field12325);
        field12263.put(field12326, field12327);
        field12263.put(field12328, field12329);
        field12263.put(field12330, field12331);
        field12263.put(field12332, field12333);
        field12263.put(field12334, field12335);
        field12263.put(field12336, field12337);
        field12263.put(field12338, field12339);
        field12263.put(field12340, field12341);
        field12263.put(field12342, field12343);
        field12263.put(field12344, field12345);
        field12263.put(7424, field12346);
        field12263.put(665, field12347);
        field12263.put(7428, field12348);
        field12263.put(7429, field12349);
        field12263.put(7431, field12350);
        field12263.put(field12351, field12352);
        field12263.put(610, field12353);
        field12263.put(668, field12354);
        field12263.put(618, field12355);
        field12263.put(7434, field12356);
        field12263.put(7435, field12357);
        field12263.put(671, field12358);
        field12263.put(7437, field12359);
        field12263.put(628, field12360);
        field12263.put(7439, field12361);
        field12263.put(7448, field12362);
        field12263.put(491, field12363);
        field12263.put(640, field12364);
        field12263.put(7451, field12365);
        field12263.put(7452, field12366);
        field12263.put(field12367, field12368);
        field12263.put(7456, field12369);
        field12263.put(7457, field12370);
        field12263.put(7521, field12371);
        field12263.put(655, field12372);
        field12263.put(7458, field12373);
        field12259.put(field12374, field12375);
        field12259.put(field12376, field12377);
        field12259.put(field12378, field12379);
        field12259.put(field12380, field12381);
        field12259.put(field12382, field12383);
        field12259.put(field12384, field12385);
        field12259.put(field12386, field12387);
        field12259.put(field12388, field12389);
        field12259.put(field12390, field12391);
        field12259.put(field12392, field12393);
        field12259.put(field12394, field12395);
        field12259.put(field12396, field12397);
        field12259.put(field12398, field12399);
        field12259.put(field12400, field12401);
        field12259.put(field12402, field12403);
        field12259.put(field12404, field12405);
        field12259.put(field12406, field12407);
        field12259.put(field12408, field12409);
        field12259.put(field12410, field12411);
        field12259.put(field12412, field12413);
        field12264.put(field12414, ColorUtil.method1858(100, 149, 237));
        field12264.put(field12415, ColorUtil.method1858(255, 215, 0));
        field12264.put(field12416, ColorUtil.method1858(255, 165, 0));
        field12264.put(field12417, ColorUtil.method1858(139, 0, 139));
        field12264.put(field12418, ColorUtil.method1858(255, 0, 0));
        field12264.put(field12419, ColorUtil.method1858(187, 0, 0));
        field12264.put(field12420, ColorUtil.method1858(173, 216, 230));
        field12264.put(field12421, ColorUtil.method1858(0, 255, 0));
        field12264.put(field12422, ColorUtil.method1858(144, 238, 144));
        field12264.put(field12423, ColorUtil.method1858(255, 0, 0));
        field12264.put(field12424, ColorUtil.method1858(184, 134, 11));
        field12264.put(field12425, ColorUtil.method1858(255, 215, 0));
        field12264.put(field12426, ColorUtil.method1858(0, 0, 255));
        field12264.put(field12427, ColorUtil.method1858(65, 105, 225));
        field12264.put(field12428, ColorUtil.method1858(255, 69, 0));
        field12264.put(field12429, ColorUtil.method1858(135, 206, 250));
        field12264.put(field12430, ColorUtil.method1858(255, 215, 0));
        field12264.put(field12431, ColorUtil.method1858(240, 248, 255));
        field12264.put(field12432, ColorUtil.method1858(255, 0, 0));
        field12264.put(field12433, ColorUtil.method1858(255, 255, 255));
        field12261.put(field12434, ColorUtil.method1858(120, 120, 120));
        field12261.put(field12435, ColorUtil.method1858(100, 113, 251));
        field12261.put(field12436, ColorUtil.method1858(214, 200, 42));
        field12261.put(field12437, ColorUtil.method1858(101, 189, 56));
        field12261.put(field12438, ColorUtil.method1858(64, 151, 214));
        field12261.put(field12439, ColorUtil.method1858(202, 130, 60));
        field12261.put(field12440, ColorUtil.method1858(202, 60, 60));
        field12261.put(field12441, ColorUtil.method1858(245, 51, 238));
        field12261.put(field12442, ColorUtil.method1858(214, 200, 42));
        field12261.put(field12443, ColorUtil.method1858(121, 81, 202));
        field12261.put(field12444, ColorUtil.method1858(202, 130, 60));
        field12261.put(field12445, ColorUtil.method1858(202, 60, 60));
        field12261.put(field12446, ColorUtil.method1858(68, 65, 66));
        field12261.put(field12447, ColorUtil.method1858(127, 214, 86));
        field12261.put(field12448, ColorUtil.method1858(92, 120, 7));
        field12261.put(field12449, ColorUtil.method1858(120, 120, 120));
        field12261.put(field12450, ColorUtil.method1858(214, 200, 42));
        field12261.put(field12451, ColorUtil.method1858(100, 113, 251));
        field12261.put(field12452, ColorUtil.method1858(100, 113, 251));
        field12261.put(field12453, ColorUtil.method1858(121, 81, 202));
        field12261.put(field12454, ColorUtil.method1858(100, 113, 251));
        field12261.put(field12455, ColorUtil.method1858(121, 81, 202));
        field12261.put(field12456, ColorUtil.method1858(64, 151, 214));
        field12261.put(field12457, ColorUtil.method1858(202, 60, 60));
        field12261.put(field12458, ColorUtil.method1858(121, 81, 202));
        field12261.put(field12459, ColorUtil.method1858(255, 255, 255));
        field12261.put(field12460, ColorUtil.method1858(245, 215, 66));
    }

    public static int getFontColor(String param0, float param1) {
        int v2 = StyledTextGlyphs.getBaseFontColor(param0);
        if (v2 == -1) {
            return -1;
        }
        return ColorUtil.method1853(v2, param1);
    }

    public static void registerFontAlias(String param0, String param1) {
        field12259.put(param0, param1);
    }

    public static int getBaseFontColor(String param0) {
        return ((Integer)field12264.getOrDefault(param0, -1)).intValue();
    }

    public static String getStyledFontAlias(Style param0) {
        if (param0 != null && param0.getFont() != null) {
            String v1 = param0.getFont().toString();
            return (String)field12259.get(v1);
        }
        return null;
    }

    static {
        field12262 = 0.8f;
        field12265 = 42240;
        field12266 = 42244;
        field12267 = 42248;
        field12268 = 42258;
        field12269 = 42262;
        field12270 = 42272;
        field12271 = 42276;
        field12272 = 42280;
        field12273 = 42336;
        field12274 = 42290;
        field12275 = 42294;
        field12276 = 42308;
        field12277 = 42326;
        field12278 = 42312;
        field12279 = 42304;
        field12280 = 42322;
        field12281 = 42249;
        field12282 = 42259;
        field12283 = 42263;
        field12284 = 42273;
        field12285 = 42277;
        field12286 = 42281;
        field12287 = 42291;
        field12288 = 42295;
        field12289 = 42241;
        field12290 = 42245;
        field12291 = 42313;
        field12292 = 42240;
        field12293 = "PLAYER";
        field12294 = 42244;
        field12295 = "HERO";
        field12296 = 42248;
        field12297 = "TITAN";
        field12298 = 42258;
        field12299 = "AVENGER";
        field12300 = 42262;
        field12301 = "OVERLORD";
        field12302 = 42272;
        field12303 = "MAGISTER";
        field12304 = 42276;
        field12305 = "IMPERATOR";
        field12306 = 42280;
        field12307 = "DRAGON";
        field12308 = 42336;
        field12309 = "D.HELPER";
        field12310 = 42290;
        field12311 = "BULL";
        field12312 = 42294;
        field12313 = "TIGER";
        field12314 = 42308;
        field12315 = "DRACULA";
        field12316 = 42326;
        field12317 = "BUNNY";
        field12318 = 42312;
        field12319 = "COBRA";
        field12320 = 42304;
        field12321 = "HYDRA";
        field12322 = 42322;
        field12323 = "RABBIT";
        field12324 = 42249;
        field12325 = "HELPER";
        field12326 = 42259;
        field12327 = "ML.MODER";
        field12328 = 42263;
        field12329 = "MODER";
        field12330 = 42273;
        field12331 = "MODER+";
        field12332 = 42277;
        field12333 = "ST.MODER";
        field12334 = 42281;
        field12335 = "GL.MODER";
        field12336 = 42291;
        field12337 = "ML.ADMIN";
        field12338 = 42295;
        field12339 = "ADMIN";
        field12340 = 42241;
        field12341 = "MEDIA";
        field12342 = 42245;
        field12343 = "YT";
        field12344 = 42313;
        field12345 = "PEGAS";
        field12346 = "A";
        field12347 = "B";
        field12348 = "C";
        field12349 = "D";
        field12350 = "E";
        field12351 = 42800;
        field12352 = "F";
        field12353 = "G";
        field12354 = "H";
        field12355 = "I";
        field12356 = "J";
        field12357 = "K";
        field12358 = "L";
        field12359 = "M";
        field12360 = "N";
        field12361 = "O";
        field12362 = "P";
        field12363 = "Q";
        field12364 = "R";
        field12365 = "T";
        field12366 = "U";
        field12367 = 42801;
        field12368 = "S";
        field12369 = "V";
        field12370 = "W";
        field12371 = "X";
        field12372 = "Y";
        field12373 = "Z";
        field12375 = "Гидра";
        field12377 = "Цербер";
        field12379 = "Тритон";
        field12381 = "Феникс";
        field12383 = "Пандар";
        field12385 = "Жара";
        field12387 = "Холод";
        field12389 = "Кронос";
        field12391 = "Лето";
        field12393 = "Зима";
        field12395 = "Фобос";
        field12397 = "Арес";
        field12399 = "Аристократ";
        field12401 = "Ютубер";
        field12403 = "Хелпер";
        field12405 = "Ст Хелпер";
        field12407 = "Модер";
        field12409 = "Ст Модер";
        field12411 = "Админ";
        field12413 = "Игрок";
        field12434 = 42240;
        field12435 = 42244;
        field12436 = 42248;
        field12437 = 42258;
        field12438 = 42262;
        field12439 = 42272;
        field12440 = 42276;
        field12441 = 42280;
        field12442 = 42336;
        field12443 = 42290;
        field12444 = 42294;
        field12445 = 42308;
        field12446 = 42326;
        field12447 = 42312;
        field12448 = 42304;
        field12449 = 42322;
        field12450 = 42249;
        field12451 = 42259;
        field12452 = 42263;
        field12453 = 42273;
        field12454 = 42277;
        field12455 = 42281;
        field12456 = 42291;
        field12457 = 42295;
        field12458 = 42241;
        field12459 = 42245;
        field12460 = 42313;
        StyledTextGlyphs.initializeMappings();
    }
}




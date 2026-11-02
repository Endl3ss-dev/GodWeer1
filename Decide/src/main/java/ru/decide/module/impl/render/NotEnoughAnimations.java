/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10055
 *  net.minecraft.class_10255
 *  net.minecraft.class_1268
 *  net.minecraft.class_1297
 *  net.minecraft.class_1306
 *  net.minecraft.class_1451
 *  net.minecraft.class_1493
 *  net.minecraft.class_1496
 *  net.minecraft.class_1657
 *  net.minecraft.class_1675
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1839
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_3966
 *  net.minecraft.class_4050
 *  net.minecraft.class_5498
 *  net.minecraft.class_591
 *  net.minecraft.class_630
 */
package ru.decide.module.impl.render;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_10255;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1306;
import net.minecraft.class_1451;
import net.minecraft.class_1493;
import net.minecraft.class_1496;
import net.minecraft.class_1657;
import net.minecraft.class_1675;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1839;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3966;
import net.minecraft.class_4050;
import net.minecraft.class_5498;
import net.minecraft.class_591;
import net.minecraft.class_630;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.DelimiterSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Not Enough Animations", desc="\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u0438 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432 \u0438 \u043f\u043e\u0437 (\u043f\u043e\u0440\u0442 NEA)", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class NotEnoughAnimations
extends Module {
    public final DelimiterSetting secGeneral = new DelimiterSetting(this, "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0435");
    public final BooleanSetting smoothing = new BooleanSetting((Module)this, "\u0421\u0433\u043b\u0430\u0436\u0438\u0432\u0430\u043d\u0438\u0435 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0439", true);
    public final SliderSetting smoothingSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0441\u0433\u043b\u0430\u0436\u0438\u0432\u0430\u043d\u0438\u044f", 0.1f, 0.02f, 1.0f, 0.01f).setVisible(() -> this.smoothing.getValue());
    public final ModeSetting rotationLock = new ModeSetting((Module)this, "\u0411\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u043a\u0430 \u043f\u043e\u0432\u043e\u0440\u043e\u0442\u0430", "\u041d\u0435\u0442", "\u0424\u0438\u043a\u0441\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u0430\u044f", "\u041f\u043b\u0430\u0432\u043d\u0430\u044f");
    public final BooleanSetting rotationLockFirstPerson = new BooleanSetting((Module)this, "\u0411\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u043a\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0432 1-\u043c \u043b\u0438\u0446\u0435", true).setVisible(() -> !this.rotationLock.is("\u041d\u0435\u0442"));
    public final BooleanSetting rotationLockEveryone = new BooleanSetting((Module)this, "\u0411\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u043a\u0430 \u0434\u043b\u044f \u0432\u0441\u0435\u0445 \u0438\u0433\u0440\u043e\u043a\u043e\u0432", false).setVisible(() -> !this.rotationLock.is("\u041d\u0435\u0442"));
    public final DelimiterSetting secHands = new DelimiterSetting(this, "\u0420\u0443\u043a\u0438 \u0438 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b");
    public final BooleanSetting eatDrink = new BooleanSetting((Module)this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0435\u0434\u044b \u0438 \u043f\u0438\u0442\u044c\u044f", true);
    public final BooleanSetting offhandHiding = new BooleanSetting((Module)this, "\u0421\u043a\u0440\u044b\u0432\u0430\u0442\u044c \u0432\u0442\u043e\u0440\u0443\u044e \u0440\u0443\u043a\u0443", true);
    public final BooleanSetting mapInWorld = new BooleanSetting((Module)this, "\u041a\u0430\u0440\u0442\u0430 \u0432 \u043c\u0438\u0440\u0435", true);
    public final BooleanSetting itemSwap = new BooleanSetting((Module)this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0441\u043c\u0435\u043d\u044b \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430", true);
    public final ModeSetting holdUpItems = new ModeSetting((Module)this, "\u041f\u043e\u0434\u043d\u0438\u043c\u0430\u0442\u044c \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b", "\u0418\u0437 \u0441\u043f\u0438\u0441\u043a\u0430", "\u0412\u0441\u0435", "\u041d\u0438\u0447\u0435\u0433\u043e", "\u041a\u0440\u043e\u043c\u0435 \u0441\u043f\u0438\u0441\u043a\u0430");
    public final ModeSetting holdUpTarget = new ModeSetting((Module)this, "\u0426\u0435\u043b\u044c \u043f\u043e\u0434\u044a\u0451\u043c\u0430", "\u041d\u0435\u0442", "\u041a\u0430\u043c\u0435\u0440\u0430");
    public final SliderSetting holdUpCameraOffset = new SliderSetting(this, "\u0421\u043c\u0435\u0449\u0435\u043d\u0438\u0435 \u043a \u043a\u0430\u043c\u0435\u0440\u0435", 0.1f, 0.0f, 1.0f, 0.05f).setVisible(() -> this.holdUpTarget.is("\u041a\u0430\u043c\u0435\u0440\u0430"));
    public final SliderSetting holdUpItemOffset = new SliderSetting(this, "\u0421\u043c\u0435\u0449\u0435\u043d\u0438\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430", 0.0f, -0.5f, 0.5f, 0.05f);
    public final BooleanSetting holdUpOnlySelf = new BooleanSetting((Module)this, "\u041f\u043e\u0434\u043d\u0438\u043c\u0430\u0442\u044c \u0442\u043e\u043b\u044c\u043a\u043e \u0441\u0435\u0431\u0435", false);
    public final BooleanSetting dontHoldInBed = new BooleanSetting((Module)this, "\u041d\u0435 \u0434\u0435\u0440\u0436\u0430\u0442\u044c \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u0432 \u043a\u0440\u043e\u0432\u0430\u0442\u0438", true);
    public final BooleanSetting freezeArmsInBed = new BooleanSetting((Module)this, "\u0417\u0430\u043c\u043e\u0440\u0430\u0436\u0438\u0432\u0430\u0442\u044c \u0440\u0443\u043a\u0438 \u0432 \u043a\u0440\u043e\u0432\u0430\u0442\u0438", true);
    public final SliderSetting maxBlockingAngle = new SliderSetting(this, "\u041c\u0430\u043a\u0441. \u0443\u0433\u043e\u043b \u0431\u043b\u043e\u043a\u0430", 15.0f, 0.0f, 90.0f, 1.0f);
    public final SliderSetting maxNormalAngle = new SliderSetting(this, "\u041c\u0430\u043a\u0441. \u043e\u0431\u044b\u0447\u043d\u044b\u0439 \u0443\u0433\u043e\u043b", 50.0f, 0.0f, 90.0f, 1.0f);
    public final BooleanSetting showLastUsedSword = new BooleanSetting((Module)this, "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u043f\u043e\u0441\u043b\u0435\u0434\u043d\u0438\u0439 \u043c\u0435\u0447", false);
    public final BooleanSetting customBowRotationLock = new BooleanSetting((Module)this, "\u0421\u0432\u043e\u0439 \u043f\u043e\u0432\u043e\u0440\u043e\u0442 \u0434\u043b\u044f \u043b\u0443\u043a\u0430", false);
    public final BooleanSetting clampCrossbowAnimations = new BooleanSetting((Module)this, "\u041e\u0433\u0440\u0430\u043d\u0438\u0447\u0438\u0432\u0430\u0442\u044c \u0430\u0440\u0431\u0430\u043b\u0435\u0442", false);
    public final DelimiterSetting secMove = new DelimiterSetting(this, "\u0414\u0432\u0438\u0436\u0435\u043d\u0438\u0435");
    public final BooleanSetting ladder = new BooleanSetting((Module)this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u043b\u0435\u0441\u0442\u043d\u0438\u0446", true);
    public final SliderSetting ladderAmplifier = new SliderSetting(this, "\u0410\u043c\u043f\u043b\u0438\u0442\u0443\u0434\u0430 \u043b\u0435\u0441\u0442\u043d\u0438\u0446", 0.35f, 0.1f, 1.0f, 0.05f).setVisible(() -> this.ladder.getValue());
    public final SliderSetting ladderArmHeight = new SliderSetting(this, "\u0412\u044b\u0441\u043e\u0442\u0430 \u0440\u0443\u043a \u043d\u0430 \u043b\u0435\u0441\u0442\u043d\u0438\u0446\u0435", 1.7f, 1.0f, 2.5f, 0.05f).setVisible(() -> this.ladder.getValue());
    public final SliderSetting ladderArmSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0440\u0443\u043a \u043d\u0430 \u043b\u0435\u0441\u0442\u043d\u0438\u0446\u0435", 2.0f, 0.5f, 4.0f, 0.05f).setVisible(() -> this.ladder.getValue());
    public final BooleanSetting rotateToLadder = new BooleanSetting((Module)this, "\u041f\u043e\u0432\u043e\u0440\u0430\u0447\u0438\u0432\u0430\u0442\u044c\u0441\u044f \u043a \u043b\u0435\u0441\u0442\u043d\u0438\u0446\u0435", true).setVisible(() -> this.ladder.getValue());
    public final BooleanSetting crawling = new BooleanSetting((Module)this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u043f\u043e\u043b\u0437\u0430\u043d\u0438\u044f", true);
    public final BooleanSetting falling = new BooleanSetting((Module)this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u043f\u0430\u0434\u0435\u043d\u0438\u044f", false);
    public final BooleanSetting elytra = new BooleanSetting((Module)this, "\u042d\u043b\u0438\u0442\u0440\u0430", true);
    public final BooleanSetting naruto = new BooleanSetting((Module)this, "\u0411\u0435\u0433 \u041d\u0430\u0440\u0443\u0442\u043e", false);
    public final BooleanSetting freezing = new BooleanSetting((Module)this, "\u0417\u0430\u043c\u0435\u0440\u0437\u0430\u043d\u0438\u0435", true);
    public final BooleanSetting burning = new BooleanSetting((Module)this, "\u0413\u043e\u0440\u0435\u043d\u0438\u0435", true);
    public final BooleanSetting hugging = new BooleanSetting((Module)this, "\u041e\u0431\u044a\u044f\u0442\u0438\u044f", false);
    public final BooleanSetting petting = new BooleanSetting((Module)this, "\u0413\u043b\u0430\u0434\u0438\u0442\u044c \u043f\u0438\u0442\u043e\u043c\u0446\u0435\u0432", true);
    public final BooleanSetting boat = new BooleanSetting((Module)this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0432 \u043b\u043e\u0434\u043a\u0435", true);
    public final BooleanSetting horse = new BooleanSetting((Module)this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0432\u0435\u0440\u0445\u043e\u043c", true);
    public final BooleanSetting horseLegs = new BooleanSetting((Module)this, "\u041d\u043e\u0433\u0438 \u0432\u0435\u0440\u0445\u043e\u043c", false).setVisible(() -> this.horse.getValue());
    public final BooleanSetting noLegSmoothing = new BooleanSetting((Module)this, "\u0411\u0435\u0437 \u0441\u0433\u043b\u0430\u0436\u0438\u0432\u0430\u043d\u0438\u044f \u043d\u043e\u0433", false);
    private static final Set<class_1792> HOLDING_ITEMS = Set.of(class_1802.field_8557, class_1802.field_8251, class_1802.field_38747, class_1802.field_8810, class_1802.field_22001, class_1802.field_16539, class_1802.field_22016);
    private static final Set<class_1792> MAP_ITEMS = Set.of(class_1802.field_8204);
    private static final Map<class_1657, SwapData> SWAP_STATE = new WeakHashMap<class_1657, SwapData>();
    private static final Map<class_1657, SmoothData> SMOOTH_STATE = new WeakHashMap<class_1657, SmoothData>();

    public static NotEnoughAnimations getInstance() {
        return Instance.get(NotEnoughAnimations.class);
    }

    public static boolean enabled() {
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        return module != null && module.isEnabled();
    }

    public static class_1657 playerOf(class_10055 state) {
        class_1657 player;
        class_310 mc = class_310.method_1551();
        if (mc == null || mc.field_1687 == null || state == null) {
            return null;
        }
        if (mc.field_1724 != null && state.field_53528 == mc.field_1724.method_5628()) {
            return mc.field_1724;
        }
        class_1297 entity = mc.field_1687.method_8469(state.field_53528);
        return entity instanceof class_1657 ? (player = (class_1657)entity) : null;
    }

    public static boolean applyEatDrink(class_591 model, class_10055 state) {
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        if (module == null || !module.isEnabled() || !module.eatDrink.getValue().booleanValue() || model == null || state == null) {
            return false;
        }
        class_1657 player = NotEnoughAnimations.playerOf(state);
        if (player == null || player.method_6014() <= 0) {
            return false;
        }
        class_1839 action = player.method_6030().method_7976();
        if (action != class_1839.field_8950 && action != class_1839.field_8946) {
            return false;
        }
        class_1306 arm = player.method_6058() == class_1268.field_5808 ? player.method_6068() : player.method_6068().method_5928();
        class_310 mc = class_310.method_1551();
        float tickDelta = mc.method_61966().method_60637(true);
        float g = (float)player.method_6014() - tickDelta + 1.0f;
        float headTilt = class_3532.method_16439((float)(-1.0f * (player.method_36455() - 90.0f) / 180.0f), (float)1.0f, (float)2.0f);
        float wobble = class_3532.method_15374((double)(class_3532.method_15362((double)(g / 4.0f * (float)Math.PI)) * 0.2f));
        float pitch = -headTilt + wobble;
        boolean right = arm == class_1306.field_6183;
        float sign = right ? 1.0f : -1.0f;
        class_630 part = right ? model.field_3401 : model.field_27433;
        part.field_3654 = pitch;
        part.field_3675 = -0.3f * sign;
        part.field_3674 = 0.3f * sign;
        return true;
    }

    private static float tilt(class_1657 player) {
        return -1.0f * (player.method_36455() - 90.0f) / 180.0f;
    }

    private static void poseArm(class_591 model, class_1306 arm, float pitch, float yaw, float roll) {
        boolean right = arm == class_1306.field_6183;
        float sign = right ? 1.0f : -1.0f;
        class_630 part = right ? model.field_3401 : model.field_27433;
        part.field_3654 = pitch;
        part.field_3675 = yaw * sign;
        part.field_3674 = roll * sign;
    }

    public static boolean applyMapHolding(class_591 model, class_10055 state) {
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        if (module == null || !module.isEnabled() || !module.mapInWorld.getValue().booleanValue()) {
            return false;
        }
        class_1657 player = NotEnoughAnimations.playerOf(state);
        if (player == null) {
            return false;
        }
        class_1799 main = player.method_5998(class_1268.field_5808);
        class_1799 off = player.method_5998(class_1268.field_5810);
        boolean mainMap = MAP_ITEMS.contains(main.method_7909());
        boolean offMap = MAP_ITEMS.contains(off.method_7909());
        float t = NotEnoughAnimations.tilt(player);
        class_1306 mainArm = player.method_6068();
        class_1306 offArm = mainArm.method_5928();
        if (mainMap && off.method_7960()) {
            float pitch = -class_3532.method_16439((float)t, (float)0.7f, (float)0.9f);
            float yaw = class_3532.method_16439((float)t, (float)-0.3f, (float)-0.2f);
            NotEnoughAnimations.poseArm(model, mainArm, pitch, yaw, 0.3f);
            NotEnoughAnimations.poseArm(model, offArm, pitch, -yaw, 0.3f);
            return true;
        }
        if (mainMap) {
            NotEnoughAnimations.poseArm(model, mainArm, -class_3532.method_16439((float)t, (float)0.5f, (float)1.5f), 0.0f, 0.3f);
            return true;
        }
        if (offMap && main.method_7960()) {
            NotEnoughAnimations.poseArm(model, offArm, -class_3532.method_16439((float)t, (float)0.5f, (float)1.5f), 0.0f, 0.3f);
            return true;
        }
        return false;
    }

    public static boolean applyLookAtItem(class_591 model, class_10055 state) {
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        if (module == null || !module.isEnabled() || module.holdUpItems.is("\u041d\u0438\u0447\u0435\u0433\u043e")) {
            return false;
        }
        class_1657 player = NotEnoughAnimations.playerOf(state);
        if (player == null) {
            return false;
        }
        if (module.holdUpOnlySelf.getValue().booleanValue()) {
            class_310 mc = class_310.method_1551();
            if (mc.field_1724 != player) {
                return false;
            }
        }
        if (player.field_6252) {
            return false;
        }
        boolean allItems = module.holdUpItems.is("\u0412\u0441\u0435");
        boolean inverted = module.holdUpItems.is("\u041a\u0440\u043e\u043c\u0435 \u0441\u043f\u0438\u0441\u043a\u0430");
        class_1306 mainArm = player.method_6068();
        class_1306 offArm = mainArm.method_5928();
        class_1268 mainHand = mainArm == class_1306.field_6183 ? class_1268.field_5808 : class_1268.field_5810;
        class_1268 offHand = mainArm == class_1306.field_6183 ? class_1268.field_5810 : class_1268.field_5808;
        boolean rightArm = NotEnoughAnimations.wanted(module, player.method_5998(NotEnoughAnimations.rightHandOf(mainArm)), allItems, inverted);
        boolean leftArm = NotEnoughAnimations.wanted(module, player.method_5998(NotEnoughAnimations.leftHandOf(mainArm)), allItems, inverted);
        float t = NotEnoughAnimations.tilt(player);
        boolean camera = module.holdUpTarget.is("\u041a\u0430\u043c\u0435\u0440\u0430");
        float cameraOffset = module.holdUpCameraOffset.getValue().floatValue();
        float itemOffset = module.holdUpItemOffset.getValue().floatValue();
        if (rightArm && leftArm) {
            NotEnoughAnimations.poseLookArm(model, module, class_1306.field_6183, t, camera, cameraOffset, itemOffset);
            NotEnoughAnimations.poseLookArm(model, module, class_1306.field_6182, t, camera, cameraOffset, itemOffset);
            return true;
        }
        if (rightArm) {
            NotEnoughAnimations.poseLookArm(model, module, class_1306.field_6183, t, camera, cameraOffset, itemOffset);
            return true;
        }
        if (leftArm) {
            NotEnoughAnimations.poseLookArm(model, module, class_1306.field_6182, t, camera, cameraOffset, itemOffset);
            return true;
        }
        return false;
    }

    private static void poseLookArm(class_591 model, NotEnoughAnimations module, class_1306 arm, float t, boolean camera, float cameraOffset, float itemOffset) {
        if (camera) {
            float invert = arm == class_1306.field_6183 ? 1.0f : -1.0f;
            float pitch = class_3532.method_15363((float)(-1.5707964f + model.field_3398.field_3654), (float)-2.5f, (float)0.0f);
            float yaw = class_3532.method_15363((float)(cameraOffset + model.field_3398.field_3675 * invert), (float)-0.2f, (float)Math.max(0.2f, cameraOffset));
            NotEnoughAnimations.poseArm(model, arm, pitch, yaw, 0.1f);
        } else {
            NotEnoughAnimations.poseArm(model, arm, -itemOffset - class_3532.method_16439((float)t, (float)1.0f, (float)1.5f), -0.2f, 0.3f);
        }
    }

    private static boolean wanted(NotEnoughAnimations module, class_1799 stack, boolean allItems, boolean inverted) {
        boolean listed = HOLDING_ITEMS.contains(stack.method_7909());
        if (allItems) {
            return !stack.method_7960();
        }
        return inverted ? !listed : listed;
    }

    private static class_1268 rightHandOf(class_1306 mainArm) {
        return mainArm == class_1306.field_6183 ? class_1268.field_5808 : class_1268.field_5810;
    }

    private static class_1268 leftHandOf(class_1306 mainArm) {
        return mainArm == class_1306.field_6183 ? class_1268.field_5810 : class_1268.field_5808;
    }

    public static boolean applyLadder(class_591 model, class_10055 state) {
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        if (module == null || !module.isEnabled() || !module.ladder.getValue().booleanValue()) {
            return false;
        }
        class_1657 player = NotEnoughAnimations.playerOf(state);
        if (player == null || !player.method_6101() || player.method_24828()) {
            return false;
        }
        float speed = module.ladderArmSpeed.getValue().floatValue();
        float amplifier = module.ladderAmplifier.getValue().floatValue();
        float height = module.ladderArmHeight.getValue().floatValue();
        float rotation = -class_3532.method_15374((double)((float)(player.method_23318() * (double)speed))) * amplifier;
        NotEnoughAnimations.poseArm(model, class_1306.field_6183, -height - rotation, -0.2f, 0.3f);
        NotEnoughAnimations.poseArm(model, class_1306.field_6182, -height + rotation, -0.2f, 0.3f);
        model.field_3392.field_3654 = -1.0f - rotation;
        model.field_3397.field_3654 = -1.0f + rotation;
        model.field_3392.field_3675 = -0.2f;
        model.field_3397.field_3675 = 0.2f;
        model.field_3392.field_3674 = 0.3f;
        model.field_3397.field_3674 = -0.3f;
        return true;
    }

    private static float tickDelta() {
        return class_310.method_1551().method_61966().method_60637(true);
    }

    public static boolean applyElytra(class_591 model, class_10055 state) {
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        if (module == null || !module.isEnabled() || !module.elytra.getValue().booleanValue()) {
            return false;
        }
        class_1657 player = NotEnoughAnimations.playerOf(state);
        if (player == null || player.method_18376() != class_4050.field_18077) {
            return false;
        }
        float k = (float)player.method_18798().field_1351 / 0.2f;
        if ((k *= k * k) < 1.0f) {
            k = 1.0f;
        }
        float tick = (float)player.field_6012 + NotEnoughAnimations.tickDelta();
        float moveOut = class_3532.method_15363((float)(0.1507964f / k), (float)0.1f, (float)0.25f);
        model.field_27433.field_3654 = class_3532.method_15374((double)(tick * 0.6662f)) * 0.5f / k;
        model.field_27433.field_3674 = -moveOut;
        model.field_3401.field_3654 = class_3532.method_15374((double)(tick * 0.6662f + (float)Math.PI)) * 0.5f / k;
        model.field_3401.field_3674 = moveOut;
        model.field_3397.field_3654 = class_3532.method_15374((double)(tick * 0.6662f + (float)Math.PI)) * 0.7f / k;
        model.field_3397.field_3674 = -moveOut;
        model.field_3392.field_3654 = class_3532.method_15374((double)(tick * 0.6662f)) * 0.7f / k;
        model.field_3392.field_3674 = moveOut;
        return true;
    }

    public static boolean applyFalling(class_591 model, class_10055 state) {
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        if (module == null || !module.isEnabled() || !module.falling.getValue().booleanValue()) {
            return false;
        }
        class_1657 player = NotEnoughAnimations.playerOf(state);
        if (player == null || player.method_18376() == class_4050.field_18077 || player.method_24828() || player.method_6101() || player.method_7325() || player.field_6017 <= 3.0) {
            return false;
        }
        float speed = (float)(-player.method_18798().field_1351 / 11.0);
        if (speed <= 0.14285715f) {
            return false;
        }
        float armsMove = Math.min(1.0f, speed * 2.0f);
        float moveSqrt = Math.min(1.0f, speed);
        float moveOutArms = 1.9f * armsMove;
        float moveOutLegs = 0.6f * moveSqrt;
        float movement = (float)player.field_6012 + NotEnoughAnimations.tickDelta();
        model.field_27433.field_3654 = class_3532.method_15374((double)(movement * 0.6662f)) * moveSqrt;
        model.field_27433.field_3674 = -moveOutArms;
        model.field_3401.field_3654 = class_3532.method_15374((double)(movement * 0.6662f + (float)Math.PI)) * moveSqrt;
        model.field_3401.field_3674 = moveOutArms;
        model.field_3397.field_3654 = class_3532.method_15374((double)(movement * 0.6662f + (float)Math.PI)) * 1.4f * moveSqrt;
        model.field_3397.field_3674 = -moveOutLegs;
        model.field_3392.field_3654 = class_3532.method_15374((double)(movement * 0.6662f)) * 1.4f * moveSqrt;
        model.field_3392.field_3674 = moveOutLegs;
        return true;
    }

    public static boolean applyItemSwap(class_591 model, class_10055 state) {
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        if (module == null || !module.isEnabled() || !module.itemSwap.getValue().booleanValue()) {
            return false;
        }
        class_1657 player = NotEnoughAnimations.playerOf(state);
        if (player == null) {
            return false;
        }
        SwapData data = SWAP_STATE.computeIfAbsent(player, k -> new SwapData());
        if (data.lastTick != player.field_6012) {
            data.lastTick = player.field_6012;
            class_1792 main = player.method_6047().method_7909();
            class_1792 off = player.method_6079().method_7909();
            if (data.main != null && data.main != data.off && data.main == off && data.off == main) {
                data.timer = 10;
            }
            data.main = main;
            data.off = off;
            if (data.timer > 0) {
                --data.timer;
            }
        }
        if (data.timer <= 0) {
            return false;
        }
        float t = NotEnoughAnimations.tickDelta();
        float position = (float)(-data.timer) / 10.0f;
        position = class_3532.method_16439((float)t, (float)(-((float)(data.timer + 1) / 10.0f)), (float)position);
        NotEnoughAnimations.poseArm(model, class_1306.field_6183, -0.5f, 0.2f, position);
        NotEnoughAnimations.poseArm(model, class_1306.field_6182, -0.5f, 0.2f, position);
        return true;
    }

    public static boolean applyBoat(class_591 model, class_10055 state) {
        class_1297 class_12972;
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        if (module == null || !module.isEnabled() || !module.boat.getValue().booleanValue()) {
            return false;
        }
        class_1657 player = NotEnoughAnimations.playerOf(state);
        if (player == null || !((class_12972 = player.method_5854()) instanceof class_10255)) {
            return false;
        }
        class_10255 boat = (class_10255)class_12972;
        float t = NotEnoughAnimations.tickDelta();
        NotEnoughAnimations.poseArm(model, class_1306.field_6182, -1.1f - class_3532.method_15374((double)boat.method_64485(0, t)) * 0.3f, 0.2f, 0.3f);
        NotEnoughAnimations.poseArm(model, class_1306.field_6183, -1.1f - class_3532.method_15374((double)boat.method_64485(1, t)) * 0.3f, 0.2f, 0.3f);
        return true;
    }

    public static boolean applyHorse(class_591 model, class_10055 state) {
        class_1496 horse;
        class_1297 class_12972;
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        if (module == null || !module.isEnabled() || !module.horse.getValue().booleanValue()) {
            return false;
        }
        class_1657 player = NotEnoughAnimations.playerOf(state);
        if (player == null || !((class_12972 = player.method_5854()) instanceof class_1496) || !(horse = (class_1496)class_12972).method_66672()) {
            return false;
        }
        float speed = horse.field_42108.method_48566();
        float rotation = (float)((double)(-class_3532.method_15374((double)(speed * 0.3f))) * 0.1);
        NotEnoughAnimations.poseArm(model, class_1306.field_6183, -1.1f - rotation, -0.2f, 0.3f);
        NotEnoughAnimations.poseArm(model, class_1306.field_6182, -1.1f - rotation, -0.2f, 0.3f);
        if (module.horseLegs.getValue().booleanValue()) {
            float legPitch;
            boolean angry = horse.method_6736();
            model.field_3397.field_3654 = legPitch = angry ? -0.8f : -0.2f;
            model.field_3392.field_3654 = legPitch;
            model.field_3397.field_3674 = -0.8f;
            model.field_3392.field_3674 = 0.8f;
        }
        return true;
    }

    private static float wrapDegrees(float angle) {
        return (angle + (float)Math.PI) % ((float)Math.PI * 2) - (float)Math.PI;
    }

    private static float lerpAngle(float delta, float start, float end) {
        float wrappedStart = NotEnoughAnimations.wrapDegrees(start);
        float wrappedEnd = NotEnoughAnimations.wrapDegrees(end);
        float difference = wrappedEnd - wrappedStart;
        float shortestPath = (difference + (float)Math.PI) % ((float)Math.PI * 2) - (float)Math.PI;
        return NotEnoughAnimations.wrapDegrees(wrappedStart + shortestPath * delta);
    }

    private static float interpolateRotation(float start, float end, float amount) {
        float wrappedStart = NotEnoughAnimations.wrapDegrees(start);
        float wrappedEnd = NotEnoughAnimations.wrapDegrees(end);
        float diff = wrappedEnd - wrappedStart;
        if (diff > (float)Math.PI) {
            wrappedEnd -= (float)Math.PI * 2;
        } else if (diff < (float)(-Math.PI)) {
            wrappedEnd += (float)Math.PI * 2;
        }
        return NotEnoughAnimations.wrapDegrees(wrappedStart + (wrappedEnd - wrappedStart) * amount);
    }

    private static void cleanInvalid(float[] data, int offset) {
        for (int i = 0; i < 3; ++i) {
            if (!Float.isNaN(data[offset + i])) continue;
            data[offset + i] = 0.0f;
        }
    }

    private static void interpolate(class_630 part, float[] last, int offset, float timePassed, boolean differentFrame, float speed, float delta) {
        if (timePassed > 50.0f) {
            float f = part.field_3654;
            last[offset + 6] = f;
            last[offset + 3] = f;
            last[offset] = f;
            float f2 = part.field_3675;
            last[offset + 7] = f2;
            last[offset + 4] = f2;
            last[offset + 1] = f2;
            float f3 = part.field_3674;
            last[offset + 8] = f3;
            last[offset + 5] = f3;
            last[offset + 2] = f3;
            NotEnoughAnimations.cleanInvalid(last, offset);
        } else if (!differentFrame) {
            last[offset + 6] = NotEnoughAnimations.lerpAngle(delta, last[offset + 3], last[offset]);
            last[offset + 7] = NotEnoughAnimations.lerpAngle(delta, last[offset + 4], last[offset + 1]);
            last[offset + 8] = NotEnoughAnimations.lerpAngle(delta, last[offset + 5], last[offset + 2]);
            part.field_3654 = last[offset + 6];
            part.field_3675 = last[offset + 7];
            part.field_3674 = last[offset + 8];
        } else {
            last[offset + 3] = last[offset] = last[offset + 6];
            float f = last[offset + 7];
            last[offset + 1] = f;
            last[offset + 4] = f;
            float f4 = last[offset + 8];
            last[offset + 2] = f4;
            last[offset + 5] = f4;
            float amount = class_3532.method_15363((float)speed, (float)0.0f, (float)1.0f);
            last[offset] = NotEnoughAnimations.interpolateRotation(part.field_3654, last[offset], amount);
            last[offset + 1] = NotEnoughAnimations.interpolateRotation(part.field_3675, last[offset + 1], amount);
            last[offset + 2] = NotEnoughAnimations.interpolateRotation(part.field_3674, last[offset + 2], amount);
            NotEnoughAnimations.cleanInvalid(last, offset);
            last[offset + 6] = NotEnoughAnimations.lerpAngle(delta, last[offset + 3], last[offset]);
            last[offset + 7] = NotEnoughAnimations.lerpAngle(delta, last[offset + 4], last[offset + 1]);
            last[offset + 8] = NotEnoughAnimations.lerpAngle(delta, last[offset + 5], last[offset + 2]);
            part.field_3654 = last[offset + 6];
            part.field_3675 = last[offset + 7];
            part.field_3674 = last[offset + 8];
        }
    }

    public static void applySmoothingAndRotation(class_591 model, class_10055 state) {
        boolean firstPersonOk;
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        if (module == null || !module.isEnabled()) {
            return;
        }
        class_1657 player = NotEnoughAnimations.playerOf(state);
        if (player == null) {
            return;
        }
        SmoothData data = SMOOTH_STATE.computeIfAbsent(player, k -> new SmoothData());
        int tickId = player.field_6012;
        int passedTicks = data.lastTick == Integer.MIN_VALUE ? 0 : tickId - data.lastTick;
        boolean differentFrame = passedTicks != 0;
        float timePassed = (float)passedTicks * 50.0f;
        float delta = NotEnoughAnimations.tickDelta();
        if (module.smoothing.getValue().booleanValue()) {
            float speed = module.smoothingSpeed.getValue().floatValue();
            NotEnoughAnimations.interpolate(model.field_27433, data.data, 0, timePassed, differentFrame, speed, delta);
            NotEnoughAnimations.interpolate(model.field_3401, data.data, 9, timePassed, differentFrame, speed, delta);
            if (!module.noLegSmoothing.getValue().booleanValue()) {
                NotEnoughAnimations.interpolate(model.field_3397, data.data, 18, timePassed, differentFrame, speed, delta);
                NotEnoughAnimations.interpolate(model.field_3392, data.data, 27, timePassed, differentFrame, speed, delta);
            }
        }
        class_310 mc = class_310.method_1551();
        boolean cameraEntity = mc.field_1724 == player;
        boolean lockEnabled = !module.rotationLock.is("\u041d\u0435\u0442");
        boolean applyHere = cameraEntity || module.rotationLockEveryone.getValue() != false;
        boolean bl = firstPersonOk = !cameraEntity || module.rotationLockFirstPerson.getValue() == false || mc.field_1690.method_31044() == class_5498.field_26664;
        if (lockEnabled && applyHere && firstPersonOk && player.method_5854() == null) {
            if (module.rotationLock.is("\u041f\u043b\u0430\u0432\u043d\u0430\u044f")) {
                model.field_3391.field_3675 = !differentFrame ? model.field_3398.field_3675 : NotEnoughAnimations.interpolateRotation(model.field_3391.field_3675, model.field_3398.field_3675, 0.5f);
            } else if (module.rotationLock.is("\u0424\u0438\u043a\u0441\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u0430\u044f") && differentFrame) {
                player.field_6283 = player.field_6241;
                model.field_3391.field_3675 = model.field_3398.field_3675;
            }
        }
        data.lastTick = tickId;
    }

    private static float quadraticArmUpdate(float f) {
        return -65.0f * f + f * f;
    }

    private static float rotlerpRad(float f, float g, float h) {
        float i = (h - g) % ((float)Math.PI * 2);
        if (i < (float)(-Math.PI)) {
            i += (float)Math.PI * 2;
        }
        if (i >= (float)Math.PI) {
            i -= (float)Math.PI * 2;
        }
        return g + f * i;
    }

    public static boolean applyCrawling(class_591 model, class_10055 state) {
        float p;
        float o;
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        if (module == null || !module.isEnabled() || !module.crawling.getValue().booleanValue()) {
            return false;
        }
        class_1657 player = NotEnoughAnimations.playerOf(state);
        if (player == null || player.method_18376() != class_4050.field_18079) {
            return false;
        }
        float swimAmount = state.field_53403;
        if (swimAmount <= 0.0f) {
            return false;
        }
        float step = state.field_53450 * 2.5f % 26.0f;
        float step2 = (step + 13.0f) % 26.0f;
        float armMoveHeight = 0.3707964f;
        float m = swimAmount;
        float n = swimAmount;
        if (step < 14.0f) {
            model.field_3401.field_3654 = class_3532.method_16439((float)m, (float)model.field_3401.field_3654, (float)0.0f);
            model.field_3401.field_3675 = class_3532.method_16439((float)m, (float)model.field_3401.field_3675, (float)((float)Math.PI));
            model.field_3401.field_3674 = class_3532.method_16439((float)m, (float)model.field_3401.field_3674, (float)((float)Math.PI - 1.8707964f * NotEnoughAnimations.quadraticArmUpdate(step) / NotEnoughAnimations.quadraticArmUpdate(14.0f)));
        } else if (step < 24.0f) {
            o = (step - 14.0f) / 10.0f;
            model.field_3401.field_3654 = class_3532.method_16439((float)m, (float)model.field_3401.field_3654, (float)(-armMoveHeight * o));
            model.field_3401.field_3675 = class_3532.method_16439((float)m, (float)model.field_3401.field_3675, (float)((float)Math.PI));
            model.field_3401.field_3674 = class_3532.method_16439((float)m, (float)model.field_3401.field_3674, (float)(1.2707963f + 1.8707964f * o));
        } else if (step < 26.0f) {
            p = (step - 24.0f) / 2.0f;
            model.field_3401.field_3654 = class_3532.method_16439((float)m, (float)model.field_3401.field_3654, (float)(-armMoveHeight + armMoveHeight * p));
            model.field_3401.field_3675 = class_3532.method_16439((float)m, (float)model.field_3401.field_3675, (float)((float)Math.PI));
            model.field_3401.field_3674 = class_3532.method_16439((float)m, (float)model.field_3401.field_3674, (float)((float)Math.PI));
        }
        if (step2 < 14.0f) {
            model.field_27433.field_3654 = NotEnoughAnimations.rotlerpRad(n, model.field_27433.field_3654, 0.0f);
            model.field_27433.field_3675 = NotEnoughAnimations.rotlerpRad(n, model.field_27433.field_3675, (float)Math.PI);
            model.field_27433.field_3674 = NotEnoughAnimations.rotlerpRad(n, model.field_27433.field_3674, (float)Math.PI + 1.8707964f * NotEnoughAnimations.quadraticArmUpdate(step2) / NotEnoughAnimations.quadraticArmUpdate(14.0f));
        } else if (step2 < 24.0f) {
            o = (step2 - 14.0f) / 10.0f;
            model.field_27433.field_3654 = NotEnoughAnimations.rotlerpRad(n, model.field_27433.field_3654, -armMoveHeight * o);
            model.field_27433.field_3675 = NotEnoughAnimations.rotlerpRad(n, model.field_27433.field_3675, (float)Math.PI);
            model.field_27433.field_3674 = NotEnoughAnimations.rotlerpRad(n, model.field_27433.field_3674, 5.012389f - 1.8707964f * o);
        } else if (step2 < 26.0f) {
            p = (step2 - 24.0f) / 2.0f;
            model.field_27433.field_3654 = NotEnoughAnimations.rotlerpRad(n, model.field_27433.field_3654, -armMoveHeight + armMoveHeight * p);
            model.field_27433.field_3675 = NotEnoughAnimations.rotlerpRad(n, model.field_27433.field_3675, (float)Math.PI);
            model.field_27433.field_3674 = NotEnoughAnimations.rotlerpRad(n, model.field_27433.field_3674, (float)Math.PI);
        }
        float tick = ((float)player.field_6012 + NotEnoughAnimations.tickDelta()) * 2.5f;
        model.field_3397.field_3654 = class_3532.method_16439((float)swimAmount, (float)model.field_3397.field_3654, (float)(0.15f * class_3532.method_15362((double)(tick * 0.33333334f + (float)Math.PI))));
        model.field_3397.field_3674 = -0.1507964f;
        model.field_3392.field_3654 = class_3532.method_16439((float)swimAmount, (float)model.field_3392.field_3654, (float)(0.15f * class_3532.method_15362((double)(tick * 0.33333334f))));
        model.field_3392.field_3674 = 0.1507964f;
        return true;
    }

    public static boolean applyFreezing(class_591 model, class_10055 state) {
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        if (module == null || !module.isEnabled() || !module.freezing.getValue().booleanValue()) {
            return false;
        }
        class_1657 player = NotEnoughAnimations.playerOf(state);
        if (player == null || !player.field_27857 && player.method_32312() <= 0) {
            return false;
        }
        NotEnoughAnimations.poseArm(model, class_1306.field_6182, -0.6f, 0.2f, 0.3f);
        NotEnoughAnimations.poseArm(model, class_1306.field_6183, -0.5f, 0.2f, 0.3f);
        return true;
    }

    public static boolean applyBurning(class_591 model, class_10055 state) {
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        if (module == null || !module.isEnabled() || !module.burning.getValue().booleanValue()) {
            return false;
        }
        class_1657 player = NotEnoughAnimations.playerOf(state);
        if (player == null || !player.method_5809() || player.method_5753()) {
            return false;
        }
        float armHeight = (float)(Math.sin((float)player.field_6012 * 0.6f) * 0.1);
        model.field_3398.field_3675 += class_3532.method_15374((double)player.field_6012) * 0.1f;
        NotEnoughAnimations.poseArm(model, class_1306.field_6182, -2.6f + armHeight, -0.2f, -0.3f);
        NotEnoughAnimations.poseArm(model, class_1306.field_6183, -2.6f + armHeight, -0.2f, -0.3f);
        return true;
    }

    public static boolean applyNaruto(class_591 model, class_10055 state) {
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        if (module == null || !module.isEnabled() || !module.naruto.getValue().booleanValue()) {
            return false;
        }
        class_1657 player = NotEnoughAnimations.playerOf(state);
        if (player == null || !player.method_5624()) {
            return false;
        }
        NotEnoughAnimations.poseArm(model, class_1306.field_6182, 1.0f, -0.2f, 0.3f);
        NotEnoughAnimations.poseArm(model, class_1306.field_6183, 1.0f, -0.2f, 0.3f);
        return true;
    }

    private static class_1297 lookedAt(class_1657 player, double distance) {
        class_238 box;
        class_243 dir;
        class_243 end;
        class_243 start = player.method_33571();
        class_3966 hit = class_1675.method_18075((class_1297)player, (class_243)start, (class_243)(end = start.method_1019((dir = player.method_5828(1.0f)).method_1021(distance))), (class_238)(box = player.method_5829().method_1014(distance)), e -> e != player && !e.method_7325() && e.method_5805(), (double)(distance * distance));
        return hit == null ? null : hit.method_17782();
    }

    public static boolean applyHugging(class_591 model, class_10055 state) {
        class_1657 other;
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        if (module == null || !module.isEnabled() || !module.hugging.getValue().booleanValue()) {
            return false;
        }
        class_1657 player = NotEnoughAnimations.playerOf(state);
        if (player == null || !player.method_5715()) {
            return false;
        }
        class_1297 class_12972 = NotEnoughAnimations.lookedAt(player, 1.0);
        if (!(class_12972 instanceof class_1657) || !(other = (class_1657)class_12972).method_5715()) {
            return false;
        }
        if (Math.abs(other.method_23318() - player.method_23318()) >= 0.3) {
            return false;
        }
        NotEnoughAnimations.poseArm(model, class_1306.field_6182, -1.0f, -0.2f, 0.3f);
        NotEnoughAnimations.poseArm(model, class_1306.field_6183, -1.5f, -0.2f, 0.3f);
        return true;
    }

    public static boolean applyPetting(class_591 model, class_10055 state) {
        NotEnoughAnimations module = NotEnoughAnimations.getInstance();
        if (module == null || !module.isEnabled() || !module.petting.getValue().booleanValue()) {
            return false;
        }
        class_1657 player = NotEnoughAnimations.playerOf(state);
        if (player == null || !player.method_5715()) {
            return false;
        }
        class_1297 target = NotEnoughAnimations.lookedAt(player, 1.0);
        if (!(target instanceof class_1493) && !(target instanceof class_1451)) {
            return false;
        }
        if (Math.abs(target.method_23318() - player.method_23318()) >= 0.6) {
            return false;
        }
        NotEnoughAnimations.poseArm(model, class_1306.field_6183, -0.9f, 0.3f, 0.3f);
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class SwapData {
        class_1792 main;
        class_1792 off;
        int timer;
        int lastTick = -1;

        private SwapData() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class SmoothData {
        final float[] data = new float[36];
        int lastTick = Integer.MIN_VALUE;

        private SmoothData() {
        }
    }
}


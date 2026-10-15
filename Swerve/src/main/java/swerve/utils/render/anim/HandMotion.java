package swerve.utils.render.anim;

import com.mojang.blaze3d.vertex.PoseStack;

import swerve.modules.impl.render.HandAnimations;
import swerve.modules.impl.render.ViewModel;
import swerve.ui.newgui.ClickGuiScreen;
import swerve.utils.input.KeyBoardUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.phys.Vec3;

import org.joml.Quaternionf;

/**
 * Состояние и математика слоёв HandAnimations. Один beginFrame на кадр (HEAD renderHandsWithItems),
 * один apply на руку (после pushPose в renderArmWithItem), один tick на игровой тик.
 * Горячий путь без аллокаций: один общий Quaternionf, все веса и пружины — статические float.
 *
 * Система координат — пространство камеры до ванильных трансформов руки: +x вправо, +y вверх,
 * -z вперёд. Повороты «O» — вокруг камеры (руки уводит по дуге), «A» — вокруг якоря кисти
 * (предмет наклоняется на месте).
 */
public final class HandMotion {

    private static final float DEG = (float) (Math.PI / 180.0D);
    private static final float PI = (float) Math.PI;
    private static final float LIMB = 0.6662F;
    private static final float MAX_DT = 0.05F;
    private static final float TIME_WRAP = 10000.0F;
    private static final long STALE_NANOS = 250_000_000L;

    private static final float ANCHOR_X = 0.56F;
    private static final float ANCHOR_Y = -0.52F;
    private static final float ANCHOR_EQUIP_DROP = -0.6F;
    private static final float ANCHOR_Z = -0.72F;

    private static final float EQUIP_LOWER_S = 0.15F;
    private static final float EQUIP_LOWER_TIMEOUT_S = 0.4F;
    private static final float EQUIP_RAISE_S = 0.35F;
    private static final float BACK_C1 = 1.70158F;
    private static final float BACK_C3 = BACK_C1 + 1.0F;

    private static final float INSPECT_BLEND_S = 0.25F;
    private static final float INSPECT_FADE_RATE = 9.0F;

    private static final float LAG_GAIN = 2.5F;
    private static final float LAG_STIFFNESS = 160.0F;
    private static final float LAG_MAX_DEG = 15.0F;

    private static final int EQUIP_IDLE = 0;
    private static final int EQUIP_LOWER = 1;
    private static final int EQUIP_RAISE = 2;

    private static final Quaternionf ROT = new Quaternionf();

    private static long lastNanos;
    private static float dt;
    private static float time;
    private static boolean frameReady;

    // Снимок настроек на кадр: читается один раз, руки используют готовые значения.
    private static float master;
    private static float offScale;
    private static boolean lagOn;
    private static boolean walkOn;
    private static boolean sprintOn;
    private static boolean idleOn;
    private static boolean landOn;
    private static boolean sneakOn;
    private static boolean fallOn;
    private static boolean equipOn;
    private static boolean useOn;
    private static boolean inspectOn;
    private static boolean shieldOn;
    private static boolean hitOn;
    private static boolean hurtOn;
    private static boolean shiverOn;
    private static boolean bowOn;
    private static boolean swimOn;
    private static boolean climbOn;
    private static boolean rideOn;
    private static boolean screenOn;
    private static float lagStr;
    private static float lagStiff;
    private static float walkStr;
    private static float sprintStr;
    private static float idleStr;
    private static float equipSpeed;
    private static float inspectDur;
    private static int sprintStyle;
    private static int equipStyle;

    // Сглаженные веса состояний игрока 0..1.
    private static float walkW;
    private static float sprintW;
    private static float swimW;
    private static float crawlW;
    private static float climbW;
    private static float rideW;
    private static float screenW;
    private static float fireW;
    private static float frozenW;
    private static float blockW;
    private static float fallW;
    private static float bowT;
    private static float walkPhase;
    private static float walkSpeed;
    private static float climbPhase;

    private static final HandSpring lagYaw = new HandSpring();
    private static final HandSpring lagPitch = new HandSpring();
    private static final HandSpring land = new HandSpring();
    private static final HandSpring sneakS = new HandSpring();
    private static final HandSpring hurtS = new HandSpring();
    private static final HandSpring hitS = new HandSpring();
    private static final HandSpring shieldRaise = new HandSpring();
    private static final HandSpring shieldHit = new HandSpring();
    private static final HandSpring useMain = new HandSpring();
    private static final HandSpring useOff = new HandSpring();

    private static boolean viewInit;
    private static float lastYaw;
    private static float lastPitch;
    private static float hurtDir = 1.0F;
    private static InteractionHand hitHand = InteractionHand.MAIN_HAND;

    // Смена предмета: held — что реально в руке, shown — что сейчас рисует ItemInHandRenderer.
    private static Item heldMain;
    private static Item heldOff;
    private static Item shownMain;
    private static Item shownOff;
    private static int equipMainState;
    private static int equipOffState;
    private static float equipMainT;
    private static float equipOffT;
    private static float equipMainD;
    private static float equipOffD;
    private static float equipMainA;
    private static float equipOffA;
    private static int chargeTicks;

    private static boolean inspecting;
    private static float inspectT;
    private static float inspectFade;
    private static float inspectSpin;
    private static boolean inspectKeyDown;

    private static boolean tickInit;
    private static boolean prevOnGround;
    private static float prevFall;
    private static int prevHurt;
    private static boolean prevSwinging;
    private static int prevSwingTime;

    private HandMotion() {
    }

    public static void reset() {
        lastNanos = 0L;
        dt = 0.0F;
        frameReady = false;
        walkW = sprintW = swimW = crawlW = climbW = rideW = screenW = fireW = frozenW = blockW = fallW = bowT = 0.0F;
        climbPhase = 0.0F;
        lagYaw.reset(0.0F);
        lagPitch.reset(0.0F);
        land.reset(0.0F);
        sneakS.reset(0.0F);
        hurtS.reset(0.0F);
        hitS.reset(0.0F);
        shieldRaise.reset(0.0F);
        shieldHit.reset(0.0F);
        useMain.reset(0.0F);
        useOff.reset(0.0F);
        viewInit = false;
        heldMain = heldOff = shownMain = shownOff = null;
        equipMainState = equipOffState = EQUIP_IDLE;
        equipMainT = equipOffT = equipMainD = equipOffD = equipMainA = equipOffA = 0.0F;
        chargeTicks = 0;
        inspecting = false;
        inspectT = inspectFade = inspectSpin = 0.0F;
        inspectKeyDown = false;
        tickInit = false;
    }

    private static void snapshot(HandAnimations m) {
        master = m.masterStrength();
        offScale = m.offhandScale();
        lagOn = m.lagEnabled();
        lagStr = m.lagStrength();
        lagStiff = m.lagStiffness();
        walkOn = m.walkEnabled();
        walkStr = m.walkStrength();
        sprintOn = m.sprintEnabled();
        sprintStyle = m.sprintStyle();
        sprintStr = m.sprintStrength();
        idleOn = m.idleEnabled();
        idleStr = m.idleStrength();
        landOn = m.landingEnabled();
        sneakOn = m.sneakEnabled();
        fallOn = m.fallingEnabled();
        equipOn = m.equipEnabled();
        equipStyle = m.equipStyle();
        equipSpeed = m.equipSpeed();
        useOn = m.useEnabled();
        inspectOn = m.inspectEnabled();
        inspectDur = m.inspectDuration();
        shieldOn = m.shieldEnabled();
        hitOn = m.hitEnabled();
        hurtOn = m.hurtEnabled();
        shiverOn = m.shiverEnabled();
        bowOn = m.bowEnabled();
        swimOn = m.swimEnabled();
        climbOn = m.climbEnabled();
        rideOn = m.rideEnabled();
        screenOn = m.screenEnabled();
    }

    public static void beginFrame(LocalPlayer player, float partial) {
        frameReady = false;
        HandAnimations module = HandAnimations.get();
        if (module == null || !module.isEnable() || player == null) {
            return;
        }
        snapshot(module);

        long now = System.nanoTime();
        float d = lastNanos == 0L ? 0.0F : (now - lastNanos) * 1.0E-9F;
        lastNanos = now;
        if (d < 0.0F) {
            d = 0.0F;
        } else if (d > MAX_DT) {
            d = MAX_DT;
        }
        dt = d;
        time += d;
        if (time > TIME_WRAP) {
            time -= TIME_WRAP;
        }

        boolean ground = player.onGround();
        boolean passenger = player.isPassenger();
        boolean swimming = player.isSwimming();
        boolean crawling = !swimming && player.isVisuallyCrawling();
        boolean climbing = !passenger && !swimming && player.onClimbable();
        boolean sprinting = !passenger && !swimming && !player.isFallFlying() && player.isSprinting();
        Vec3 motion = player.getDeltaMovement();
        float vy = (float) motion.y;
        walkPhase = player.walkAnimation.position(partial);
        walkSpeed = player.walkAnimation.speed(partial);
        boolean moving = walkSpeed > 0.02F;

        float aFast = 1.0F - (float) Math.exp(-14.0D * d);
        float aMid = 1.0F - (float) Math.exp(-8.0D * d);
        float aSlow = 1.0F - (float) Math.exp(-5.0D * d);

        sprintW += ((sprinting && moving ? 1.0F : 0.0F) - sprintW) * aMid;
        walkW += ((moving && !sprinting && !swimming && !crawling && !passenger ? 1.0F : 0.0F) - walkW) * aMid;
        swimW += ((swimming ? 1.0F : 0.0F) - swimW) * aSlow;
        crawlW += ((crawling ? 1.0F : 0.0F) - crawlW) * aSlow;
        climbW += ((climbing ? 1.0F : 0.0F) - climbW) * aSlow;
        rideW += ((passenger ? 1.0F : 0.0F) - rideW) * aSlow;
        fireW += ((player.isOnFire() ? 1.0F : 0.0F) - fireW) * aFast;
        frozenW += (Mth.clamp(player.getPercentFrozen(), 0.0F, 1.0F) - frozenW) * aFast;
        boolean blocking = player.isUsingItem() && player.getUseItem().getUseAnimation() == ItemUseAnimation.BLOCK;
        blockW += ((blocking ? 1.0F : 0.0F) - blockW) * aFast;

        Screen screen = Minecraft.getInstance().screen;
        boolean relaxed = screen != null && !(screen instanceof ClickGuiScreen) && !(screen instanceof ChatScreen);
        screenW += ((relaxed ? 1.0F : 0.0F) - screenW) * aMid;

        float fallTarget = 0.0F;
        if (!ground && !passenger && !climbing && !player.isInWater() && !player.isFallFlying()) {
            fallTarget = Mth.clamp((-vy - 0.6F) / 1.4F, 0.0F, 1.0F);
        }
        fallW += (fallTarget - fallW) * aSlow;
        if (climbW > 0.01F) {
            climbPhase += vy * d * 60.0F;
        }

        bowT = chargeTicks > 0 && player.isUsingItem()
                ? Mth.clamp((float) player.getTicksUsingItem() / chargeTicks, 0.0F, 1.0F) : 0.0F;

        // Инерция взгляда: угловая скорость камеры уходит импульсом в пружину с покоем в нуле.
        float yaw = player.getViewYRot(partial);
        float pitch = player.getViewXRot(partial);
        if (!viewInit) {
            viewInit = true;
            lastYaw = yaw;
            lastPitch = pitch;
        }
        float dYaw = Mth.wrapDegrees(yaw - lastYaw);
        float dPitch = pitch - lastPitch;
        lastYaw = yaw;
        lastPitch = pitch;
        if (lagOn) {
            float gain = LAG_GAIN * lagStr;
            lagYaw.impulse(dYaw * gain);
            lagPitch.impulse(dPitch * gain);
            float stiffness = LAG_STIFFNESS * lagStiff;
            lagYaw.update(0.0F, stiffness, 0.6F, d);
            lagPitch.update(0.0F, stiffness, 0.6F, d);
        } else {
            lagYaw.reset(0.0F);
            lagPitch.reset(0.0F);
        }

        land.update(0.0F, 220.0F, 0.45F, d);
        sneakS.update(player.isCrouching() ? 1.0F : 0.0F, 180.0F, 0.5F, d);
        hurtS.update(0.0F, 260.0F, 0.4F, d);
        hitS.update(0.0F, 420.0F, 0.6F, d);
        shieldRaise.update(blocking ? 1.0F : 0.0F, 300.0F, 0.45F, d);
        shieldHit.update(0.0F, 380.0F, 0.5F, d);
        useMain.update(0.0F, 330.0F, 0.5F, d);
        useOff.update(0.0F, 330.0F, 0.5F, d);

        if (inspecting) {
            inspectT += d;
            if (inspectT >= inspectDur) {
                inspecting = false;
                inspectSpin = 0.0F;
            } else {
                float u = Mth.clamp((inspectT - INSPECT_BLEND_S) / (inspectDur - 2.0F * INSPECT_BLEND_S), 0.0F, 1.0F);
                inspectSpin = u >= 1.0F ? 0.0F : 360.0F * easeInOutCubic(u);
            }
        }
        float fadeTarget = inspecting && inspectT < inspectDur - INSPECT_BLEND_S ? 1.0F : 0.0F;
        inspectFade += (fadeTarget - inspectFade) * (1.0F - (float) Math.exp(-INSPECT_FADE_RATE * d));
        if (!inspecting && inspectFade < 0.005F) {
            inspectFade = 0.0F;
            inspectSpin = 0.0F;
        }

        frameReady = true;
    }

    public static void apply(PoseStack poseStack, AbstractClientPlayer player, InteractionHand hand,
                             ItemStack shown, float restEquip) {
        if (!frameReady || poseStack == null || player == null || hand == null) {
            return;
        }
        boolean main = hand == InteractionHand.MAIN_HAND;
        HumanoidArm arm = main ? player.getMainArm() : player.getMainArm().getOpposite();
        float s = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        float hs = master * (main ? 1.0F : offScale);
        Item shownItem = shown == null || shown.isEmpty() ? null : shown.getItem();

        float x = 0.0F;
        float y = 0.0F;
        float z = 0.0F;
        float rxO = 0.0F;
        float ryO = 0.0F;
        float rzO = 0.0F;
        float rxA = 0.0F;
        float ryA = 0.0F;
        float rzA = 0.0F;

        float t = time;
        float ph = walkPhase * LIMB;
        float sw = Mth.sin(ph);
        float cw = Mth.cos(ph);
        float asw = s * sw;

        if (idleOn) {
            float a = idleStr * hs;
            y += 0.006F * Mth.sin(t * 1.3F) * a;
            x += 0.003F * Mth.sin(t * 0.9F + 1.7F) * a;
            rxA += 0.7F * Mth.sin(t * 1.3F + 0.5F) * a;
            rzO += 0.25F * s * Mth.sin(t * 0.8F) * a;
        }

        if (walkOn && walkW > 0.001F) {
            float a = walkW * walkStr * hs * Mth.clamp(walkSpeed * 1.5F, 0.0F, 1.0F);
            ryO += 1.6F * asw * a;
            rzO += 1.0F * sw * a;
            y -= 0.008F * Mth.abs(sw) * a;
            x += 0.006F * cw * a;
            rxA += 1.5F * Mth.cos(2.0F * ph) * a;
        }

        if (sprintOn && sprintW > 0.001F) {
            float a = sprintW * sprintStr * hs;
            if (sprintStyle == 1) {
                float bounce = Mth.sin(2.0F * ph);
                x += (0.02F * sw - s * 0.08F) * a;
                y += (-0.05F + 0.03F * bounce) * a;
                z += 0.03F * a;
                rxA += (-14.0F + 4.0F * bounce) * a;
                ryA += 20.0F * s * a;
                rzA += 8.0F * s * a;
            } else if (sprintStyle == 2) {
                ryO += 4.0F * sw * a;
                rzO += 3.0F * sw * a;
                y += 0.02F * Mth.abs(cw) * a;
                rxA += 3.0F * Mth.cos(2.0F * ph) * a;
            } else {
                z -= 0.09F * asw * a;
                y += 0.025F * Mth.sin(2.0F * ph) * a;
                x += 0.015F * s * cw * a;
                rxA += 14.0F * asw * a;
                ryO += 3.0F * s * cw * a;
            }
        }

        if (lagOn) {
            float maxLag = LAG_MAX_DEG * lagStr;
            ryO += Mth.clamp(lagYaw.pos, -maxLag, maxLag) * hs;
            rxO += Mth.clamp(lagPitch.pos, -maxLag, maxLag) * hs;
        }

        if (landOn) {
            float p = land.pos * hs;
            y += p;
            rxO += 25.0F * p;
            rzO += 10.0F * s * p;
        }

        if (sneakOn) {
            float p = sneakS.pos * hs;
            y -= 0.035F * p;
            x -= 0.012F * s * p;
            rxA += 4.0F * p;
        }

        if (fallOn && fallW > 0.001F) {
            float a = fallW * hs;
            y += 0.10F * a;
            x += 0.05F * s * a;
            rxA += 15.0F * a;
            rzA += 8.0F * s * a;
            ryO += 3.0F * s * a;
        }

        if (swimOn) {
            if (swimW > 0.001F) {
                float a = swimW * hs;
                y += (0.04F * asw - 0.06F) * a;
                z -= 0.05F * a;
                x += 0.02F * cw * a;
                rxA += 15.0F * a;
                ryA += -10.0F * s * a;
            }
            if (crawlW > 0.001F) {
                float a = crawlW * hs;
                y -= 0.05F * a;
                x += (0.02F * asw - 0.08F * s) * a;
                rxA += 20.0F * a;
                ryA += -20.0F * s * a;
            }
        }

        if (climbOn && climbW > 0.001F) {
            float a = climbW * hs;
            float stroke = Mth.sin(climbPhase + (s > 0.0F ? 0.0F : PI));
            y += (0.15F + 0.06F * stroke) * a;
            z -= 0.10F * a;
            x -= 0.10F * s * a;
            rxA += 15.0F * a;
        }

        if (rideOn && rideW > 0.001F) {
            float a = rideW * hs;
            y -= 0.08F * a;
            x -= 0.12F * s * a;
            rxA += 12.0F * a;
            ryA += 10.0F * s * a;
        }

        if (screenOn && screenW > 0.001F) {
            float a = screenW * hs;
            y -= 0.10F * a;
            x += 0.04F * s * a;
            rxA += 8.0F * a;
        }

        if (shiverOn) {
            if (fireW > 0.001F) {
                float a = fireW * hs;
                x += 0.012F * Mth.sin(t * 37.0F) * a;
                y += 0.010F * Mth.sin(t * 29.0F + 1.0F) * a;
                rzO += 2.5F * s * Mth.sin(t * 41.0F + 2.0F) * a;
                ryO += 5.0F * s * Mth.sin(t * 6.0F) * a;
                rxA += 6.0F * Mth.sin(t * 7.3F) * a;
            }
            if (frozenW > 0.001F) {
                float a = frozenW * hs;
                x += 0.006F * Mth.sin(t * 53.0F) * a;
                y += 0.005F * Mth.sin(t * 61.0F + 2.0F) * a;
                rzA += 1.5F * Mth.sin(t * 47.0F) * a;
            }
        }

        if (bowOn && bowT > 0.001F) {
            float a = bowT * bowT * hs;
            x += 0.004F * Mth.sin(t * 31.0F) * a;
            y += 0.004F * Mth.sin(t * 27.0F + 1.0F) * a;
            rxA += 0.8F * Mth.sin(t * 35.0F) * a;
            rzA += 1.5F * s * Mth.sin(t * 23.0F) * a;
        }

        if (hurtOn) {
            float p = hurtS.pos * hs;
            y += 0.05F * p;
            x += 0.03F * hurtDir * p;
            rzO += 4.0F * hurtDir * p;
            rxA += 6.0F * p;
        }

        if (hitOn && hitHand == hand) {
            float p = hitS.pos * hs;
            z += 0.06F * p;
            rxA += 8.0F * p;
            ryO += 3.0F * s * p;
        }

        // Щит: ванильный подъём остаётся, добавляется только перелёт пружины и толчок от блока.
        if (shieldOn && shownItem instanceof ShieldItem) {
            float over = (shieldRaise.pos - blockW) * hs;
            y += 0.08F * over;
            rxA += -12.0F * over;
            float h = shieldHit.pos * hs;
            z += 0.10F * h;
            x += 0.03F * s * h;
            rxA += 14.0F * h;
            rzA += 6.0F * s * h;
        }

        if (useOn) {
            float p = (main ? useMain.pos : useOff.pos) * hs;
            y += 0.08F * p;
            z += 0.04F * p;
            rxA += 10.0F * p;
        }

        float d = equipBlend(main, shownItem);
        if (d != 0.0F) {
            if (equipStyle == 1) {
                x += 0.75F * s * d;
                y -= 0.25F * d;
                ryA += 35.0F * s * d;
            } else if (equipStyle == 2) {
                y -= 0.55F * d;
                x += 0.10F * s * d;
                rzA += 60.0F * s * d;
                ryA += 25.0F * s * d;
            } else {
                y -= 0.50F * d;
                rxA += -25.0F * d;
            }
        }

        if (inspectOn && main && inspectFade > 0.0F) {
            float f = inspectFade;
            x -= 0.40F * s * f;
            y += 0.30F * f;
            z += 0.18F * f;
            rxA += -15.0F * f;
            ryA += inspectSpin * s * f;
            rzA += 20.0F * s * Mth.sin(inspectSpin / 360.0F * PI) * f;
        }

        if (x != 0.0F || y != 0.0F || z != 0.0F) {
            poseStack.translate(x, y, z);
        }
        if (rxO != 0.0F || ryO != 0.0F || rzO != 0.0F) {
            ROT.rotationXYZ(rxO * DEG, ryO * DEG, rzO * DEG);
            poseStack.mulPose(ROT);
        }
        if (rxA != 0.0F || ryA != 0.0F || rzA != 0.0F) {
            ROT.rotationXYZ(rxA * DEG, ryA * DEG, rzA * DEG);
            poseStack.rotateAround(ROT, s * ANCHOR_X + ViewModel.offsetX(hand),
                    ANCHOR_Y + restEquip * ANCHOR_EQUIP_DROP + ViewModel.offsetY(hand), ANCHOR_Z);
        }
    }

    // Ванильный подъём/опускание руки гасится, пока слой смены предмета включён: его заменяет свой.
    public static float equipProgress(float progress, float rest) {
        return frameReady && equipOn ? rest : progress;
    }

    // Опускание идёт до момента, когда ванильный рендер подменит предмет (shown меняется),
    // подъём — своей длительностью с перелётом. Таймаут страхует мгновенную подмену ванили.
    private static float equipBlend(boolean main, Item shownItem) {
        int state = main ? equipMainState : equipOffState;
        float t = main ? equipMainT : equipOffT;
        float amp = main ? equipMainA : equipOffA;
        Item lastShown = main ? shownMain : shownOff;
        float d = 0.0F;
        if (!equipOn) {
            state = EQUIP_IDLE;
        } else if (state == EQUIP_LOWER) {
            t += dt;
            float p = t / EQUIP_LOWER_S;
            if (p > 1.0F) {
                p = 1.0F;
            }
            d = Mth.sin(p * PI * 0.5F);
            if (shownItem != lastShown || t >= EQUIP_LOWER_TIMEOUT_S) {
                state = EQUIP_RAISE;
                t = 0.0F;
                amp = d;
            }
        } else if (state == EQUIP_RAISE) {
            t += dt;
            float p = t * equipSpeed / EQUIP_RAISE_S;
            if (p >= 1.0F) {
                p = 1.0F;
                state = EQUIP_IDLE;
            }
            d = amp * (1.0F - easeOutBack(p));
        }
        if (main) {
            equipMainState = state;
            equipMainT = t;
            equipMainD = d;
            equipMainA = amp;
            shownMain = shownItem;
        } else {
            equipOffState = state;
            equipOffT = t;
            equipOffD = d;
            equipOffA = amp;
            shownOff = shownItem;
        }
        return d;
    }

    private static void startEquip(boolean main) {
        float last = main ? equipMainD : equipOffD;
        if (last < 0.0F) {
            last = 0.0F;
        } else if (last > 1.0F) {
            last = 1.0F;
        }
        // Старт с текущей высоты: повторная смена во время подъёма не даёт рывка.
        float t = (float) (Math.asin(last) / (Math.PI * 0.5D)) * EQUIP_LOWER_S;
        if (main) {
            equipMainState = EQUIP_LOWER;
            equipMainT = t;
        } else {
            equipOffState = EQUIP_LOWER;
            equipOffT = t;
        }
    }

    public static void tick(LocalPlayer player) {
        HandAnimations module = HandAnimations.get();
        if (module == null || player == null) {
            return;
        }
        boolean ground = player.onGround();
        float fall = player.fallDistance;
        int hurtTime = player.hurtTime;
        boolean swinging = player.swinging;
        int swingTime = player.swingTime;
        Item mainNow = itemOf(player.getMainHandItem());
        Item offNow = itemOf(player.getOffhandItem());

        if (!tickInit) {
            tickInit = true;
            prevOnGround = ground;
            prevFall = fall;
            prevHurt = hurtTime;
            prevSwinging = swinging;
            prevSwingTime = swingTime;
            heldMain = mainNow;
            heldOff = offNow;
            shownMain = mainNow;
            shownOff = offNow;
            return;
        }
        // Руки не рисуются (3-е лицо, F1, SpatialGui): импульсы не копятся до следующего кадра.
        if (lastNanos != 0L && System.nanoTime() - lastNanos > STALE_NANOS) {
            lastNanos = 0L;
            land.reset(0.0F);
            hurtS.reset(0.0F);
            hitS.reset(0.0F);
            shieldHit.reset(0.0F);
            useMain.reset(0.0F);
            useOff.reset(0.0F);
            lagYaw.reset(0.0F);
            lagPitch.reset(0.0F);
            viewInit = false;
            equipMainState = equipOffState = EQUIP_IDLE;
            equipMainD = equipOffD = 0.0F;
            shownMain = mainNow;
            shownOff = offNow;
            inspecting = false;
            inspectFade = inspectSpin = 0.0F;
        }

        if (module.landingEnabled()) {
            if (ground && !prevOnGround) {
                float f = prevFall > fall ? prevFall : fall;
                if (f > 0.3F) {
                    land.impulse(-0.7F * Mth.clamp(f, 0.3F, 6.0F) * module.landingStrength());
                }
            } else if (!ground && prevOnGround && player.getDeltaMovement().y > 0.2D) {
                land.impulse(module.landingStrength());
            }
        }
        prevOnGround = ground;
        prevFall = fall;

        if (hurtTime > prevHurt && module.hurtEnabled()) {
            hurtDir = -hurtDir;
            hurtS.impulse(-28.0F);
        }
        prevHurt = hurtTime;

        boolean swingStart = swinging && swingTime <= 0 && !(prevSwinging && prevSwingTime <= 0);
        if (swingStart) {
            hitHand = player.swingingArm == null ? InteractionHand.MAIN_HAND : player.swingingArm;
            if (module.hitEnabled()) {
                hitS.impulse(35.0F * module.hitStrength());
            }
            cancelInspect();
        }
        prevSwinging = swinging;
        prevSwingTime = swingTime;

        if (mainNow != heldMain) {
            heldMain = mainNow;
            startEquip(true);
            cancelInspect();
        }
        if (offNow != heldOff) {
            heldOff = offNow;
            startEquip(false);
        }

        chargeTicks = 0;
        boolean using = player.isUsingItem();
        if (using) {
            ItemStack used = player.getUseItem();
            ItemUseAnimation anim = used.getUseAnimation();
            if (anim == ItemUseAnimation.BOW) {
                chargeTicks = 20;
            } else if (anim == ItemUseAnimation.SPEAR) {
                chargeTicks = 10;
            } else if (anim == ItemUseAnimation.CROSSBOW) {
                chargeTicks = CrossbowItem.getChargeDuration(used, player);
            }
        }

        if (module.inspectEnabled()) {
            boolean down = KeyBoardUtils.isBindHeld(module.inspectKey());
            if (down && !inspectKeyDown && Minecraft.getInstance().screen == null) {
                if (inspecting) {
                    cancelInspect();
                } else if (mainNow != null && !using) {
                    inspecting = true;
                    inspectT = 0.0F;
                    inspectSpin = 0.0F;
                }
            }
            inspectKeyDown = down;
            if (inspecting && using) {
                cancelInspect();
            }
        } else {
            cancelInspect();
            inspectKeyDown = false;
        }
    }

    // Прерванный осмотр доворачивается кратчайшим путём, а не раскручивается назад.
    private static void cancelInspect() {
        if (!inspecting) {
            return;
        }
        inspecting = false;
        if (inspectSpin > 180.0F) {
            inspectSpin -= 360.0F;
        }
    }

    public static void onItemUsed(InteractionHand hand) {
        if (!frameReady || !useOn) {
            return;
        }
        if (hand == InteractionHand.OFF_HAND) {
            useOff.impulse(-28.0F);
        } else {
            useMain.impulse(-28.0F);
        }
    }

    public static void onShieldBlock(boolean disabled) {
        if (!frameReady || !shieldOn) {
            return;
        }
        shieldHit.impulse(disabled ? 40.0F : 24.0F);
    }

    // Ванильный bobView для рук с множителем: та же формула, повороты через общий кватернион.
    public static void applyHandBob(PoseStack poseStack, float partial, float scale) {
        if (scale <= 0.0F || poseStack == null) {
            return;
        }
        Entity entity = Minecraft.getInstance().getCameraEntity();
        if (!(entity instanceof AbstractClientPlayer player)) {
            return;
        }
        float stride = player.walkDist - player.walkDistO;
        float g = -(player.walkDist + stride * partial);
        float h = Mth.lerp(partial, player.oBob, player.bob) * scale;
        poseStack.translate(Mth.sin(g * PI) * h * 0.5F, -Mth.abs(Mth.cos(g * PI) * h), 0.0F);
        ROT.rotationZ(Mth.sin(g * PI) * h * 3.0F * DEG);
        poseStack.mulPose(ROT);
        ROT.rotationX(Mth.abs(Mth.cos(g * PI - 0.2F) * h) * 5.0F * DEG);
        poseStack.mulPose(ROT);
    }

    private static Item itemOf(ItemStack stack) {
        return stack == null || stack.isEmpty() ? null : stack.getItem();
    }

    private static float easeOutBack(float p) {
        float q = p - 1.0F;
        return 1.0F + BACK_C3 * q * q * q + BACK_C1 * q * q;
    }

    private static float easeInOutCubic(float p) {
        return p < 0.5F ? 4.0F * p * p * p : 1.0F - (float) Math.pow(-2.0D * p + 2.0D, 3.0D) * 0.5F;
    }
}

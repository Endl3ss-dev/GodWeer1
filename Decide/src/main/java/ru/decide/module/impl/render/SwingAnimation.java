/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1306
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_2886
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_7204
 *  net.minecraft.class_7833
 *  org.joml.Quaternionfc
 */
package ru.decide.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2886;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_7204;
import net.minecraft.class_7833;
import org.joml.Quaternionfc;
import ru.decide.manager.event_impl.HandAnimationEvent;
import ru.decide.manager.event_impl.SwingDurationEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Swing Animation", category=Category.VISUALS, desc="\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u0438 \u0432\u0437\u043c\u0430\u0445\u0430 \u0440\u0443\u043a\u0438")
@Environment(value=EnvType.CLIENT)
public class SwingAnimation
extends Module {
    public ModeSetting swingMode = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438", "\u041f\u0435\u0440\u0432\u044b\u0439", "\u0412\u0442\u043e\u0440\u043e\u0439", "\u0422\u0440\u0435\u0442\u0438\u0439", "\u0427\u0435\u0442\u0432\u0451\u0440\u0442\u044b\u0439", "\u041f\u044f\u0442\u044b\u0439", "\u0428\u0435\u0441\u0442\u043e\u0439");
    public ModeSetting typeSwing = new ModeSetting((Module)this, "\u0422\u0438\u043f", "\u041f\u0435\u0440\u0432\u044b\u0439", "\u0412\u0442\u043e\u0440\u043e\u0439").setVisible(() -> this.swingMode.is("\u0422\u0440\u0435\u0442\u0438\u0439"));
    public SliderSetting swingPowessss = new SliderSetting(this, "\u0423\u0433\u043e\u043b", 90.0f, -180.0f, 180.0f, 1.0f).setVisible(() -> this.swingMode.is("\u0422\u0440\u0435\u0442\u0438\u0439"));
    public ModeSetting easeSwin = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c \u0438\u043d\u0442\u0435\u0440\u043f\u043e\u043b\u044f\u0446\u0438\u0438", "\u041f\u0435\u0440\u0432\u044b\u0439", "\u0412\u0442\u043e\u0440\u043e\u0439", "\u0422\u0440\u0435\u0442\u0438\u0439");
    public BooleanSetting auraOnly = new BooleanSetting((Module)this, "\u0422\u043e\u043b\u044c\u043a\u043e \u0441 \u0430\u0443\u0440\u043e\u0439", false);
    public SliderSetting animSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438", 1.0f, 0.1f, 4.0f, 0.1f);
    public SliderSetting animSize = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438", 6.0f, 0.1f, 10.0f, 0.1f);

    public static SwingAnimation get() {
        return Instance.get(SwingAnimation.class);
    }

    public static void interactItem(class_1268 hand) {
        SwingAnimation.mc.field_1724.method_6104(class_1268.field_5808);
        SwingAnimation.sendSequencedPacket(id -> new class_2886(hand, id, SwingAnimation.mc.field_1724.method_36454(), SwingAnimation.mc.field_1724.method_36455()));
    }

    public static void sendSequencedPacket(class_7204 packetCreator) {
        SwingAnimation.mc.field_1761.method_41931(SwingAnimation.mc.field_1687, packetCreator);
    }

    @EventHandler
    public void onSwingDuration(SwingDurationEvent e) {
        if (!this.auraCheck()) {
            return;
        }
        class_1792 item = SwingAnimation.mc.field_1724.method_5998(class_1268.field_5808).method_7909();
        if (item == class_1802.field_8162) {
            return;
        }
        float speed = this.animSpeed.getValue().floatValue();
        e.setAnimation(speed);
        e.cancel();
    }

    @EventHandler
    public void onEvent(HandAnimationEvent event) {
        int i;
        if (!this.isEnabled()) {
            return;
        }
        if (!this.auraCheck()) {
            return;
        }
        boolean rightHand = event.getArm() == class_1306.field_6183;
        ModeSetting mode = this.swingMode;
        if (event.getArm() != SwingAnimation.mc.field_1724.method_6068()) {
            return;
        }
        class_1799 stack = SwingAnimation.mc.field_1724.method_5998(event.getHand());
        class_4587 matrices = event.getMatrices();
        float swingProgress = event.getSwingProgress();
        int n = i = rightHand ? 1 : -1;
        float t = this.easeSwin.is("\u041f\u0435\u0440\u0432\u044b\u0439") ? (float)Easings.SINE_OUT.ease(swingProgress) : (this.easeSwin.is("\u0422\u0440\u0435\u0442\u0438\u0439") ? swingProgress : (float)Easings.BACK_OUT.ease(swingProgress));
        float back = t;
        float sin1 = class_3532.method_15374((double)(back * back * (float)Math.PI));
        float sin2 = class_3532.method_15374((double)(class_3532.method_15355((float)back) * (float)Math.PI));
        float sinSmooth = sin1 * sin2;
        if (this.easeSwin.is("\u0422\u0440\u0435\u0442\u0438\u0439")) {
            sinSmooth = (float)Math.sin((double)swingProgress * 1.5707963267948966 * 2.0);
        }
        float size = this.animSize.getValue().floatValue();
        class_1306 arm = event.getArm();
        switch (mode.getValue()) {
            case "\u041f\u0435\u0440\u0432\u044b\u0439": {
                matrices.method_46416((float)i * 0.56f, -0.52f, -0.72f);
                float g = sinSmooth;
                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(g * -180.0f * size / 10.0f));
                matrices.method_22907((Quaternionfc)class_7833.field_40717.rotationDegrees(g * (rightHand ? -60.0f * size / 10.0f : 60.0f * size / 10.0f)));
                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(g * (rightHand ? -60.0f * size / 10.0f : 60.0f * size / 10.0f)));
                break;
            }
            case "\u0412\u0442\u043e\u0440\u043e\u0439": {
                matrices.method_46416((float)i * 0.56f, -0.42f, -0.72f);
                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(sinSmooth * -(24.0f * size)));
                matrices.method_22904(0.0, -0.1, 0.0);
                break;
            }
            case "\u0422\u0440\u0435\u0442\u0438\u0439": {
                matrices.method_22904((double)((float)i * 0.7f), (double)-0.35f, -0.9);
                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(i * 60)));
                matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(i * -60)));
                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-this.swingPowessss.getValue().floatValue() - (this.typeSwing.is("\u041f\u0435\u0440\u0432\u044b\u0439") ? size * 24.0f * sinSmooth : -(size * 24.0f) * sinSmooth)));
                break;
            }
            case "\u0427\u0435\u0442\u0432\u0451\u0440\u0442\u044b\u0439": {
                matrices.method_22904((double)i * ((double)0.2f - 0.1 * (double)sinSmooth * (double)(0.25f * size)), 0.0, (double)-0.2f - 0.2 * (double)sinSmooth * (double)(0.25f * size));
                this.applyEquipOffset(matrices, arm, 0.0f);
                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(50.0f - 4.0f * sinSmooth * (0.35f * size)));
                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)i * (-60.0f + 0.6f * size * sinSmooth)));
                matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)i * (110.0f + 40.0f * sinSmooth * (0.2f * size))));
                break;
            }
            case "\u041f\u044f\u0442\u044b\u0439": {
                matrices.method_46416((float)i * 0.56f, -0.42f, -0.72f);
                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(0.0f + t * 360.0f));
                matrices.method_22904(0.0, -0.1, 0.0);
                break;
            }
            case "\u0428\u0435\u0441\u0442\u043e\u0439": {
                matrices.method_22904((double)((float)i * 0.7f), (double)-0.35f, -0.9);
                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(i * 65)));
                matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)(i * -10)));
                matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-90.0f - size * 24.0f * sinSmooth));
            }
        }
        event.cancel();
    }

    public boolean auraCheck() {
        return true;
    }

    public void onEatAnimsHands(class_4587 matrices, float tickDelta, class_1306 arm, class_1799 stack, class_1657 player) {
        float h;
        float f = (float)player.method_6014() - tickDelta + 1.0f;
        float g = f / (float)stack.method_7935((class_1309)player);
        if (g < 0.8f) {
            h = class_3532.method_15379((float)(class_3532.method_15362((double)(f / 5.0f * (float)Math.PI)) * 0.015f));
            matrices.method_46416(0.0f, h, 0.0f);
        }
        h = 1.0f - (float)Math.pow(g, 27.0);
        int i = arm == class_1306.field_6183 ? 1 : -1;
        matrices.method_46416(h * 0.6f * (float)i, h * -0.5f, h * 0.0f);
        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)i * h * 90.0f));
        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(h * 10.0f));
        matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)i * h * 30.0f));
    }

    private void applyEquipOffset(class_4587 matrices, class_1306 arm, float equipProgress) {
        int i = arm == class_1306.field_6183 ? 1 : -1;
        matrices.method_46416((float)i * 0.56f, -0.52f + equipProgress * -0.6f, -0.72f);
    }
}


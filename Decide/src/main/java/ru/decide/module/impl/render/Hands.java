/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1306
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_4587
 */
package ru.decide.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1306;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.ButtonSetting;
import ru.decide.module.api.settings.impl.DelimiterSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.impl.render.GlassHands;
import ru.decide.module.impl.render.SwingAnimation;
import ru.decide.screen.Menu;
import ru.decide.utils.other.GuiSounds;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Hands", desc="\u041f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u0435 \u0438 \u0440\u0430\u0437\u043c\u0435\u0440 \u0440\u0443\u043a \u043e\u0442 \u043f\u0435\u0440\u0432\u043e\u0433\u043e \u043b\u0438\u0446\u0430", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class Hands
extends Module {
    private boolean editorActive;
    private boolean restoreSwingAnimation;
    private boolean restoreGlassHands;
    public ButtonSetting openEditor = new ButtonSetting((Module)this, "\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 \u0440\u0443\u043a", () -> {
        class_437 patt0$temp = Hands.mc.field_1755;
        if (patt0$temp instanceof Menu) {
            Menu menu = (Menu)patt0$temp;
            menu.openHandsEditor();
        } else {
            GuiSounds.editor();
            Menu.returnToGlassAfterEditor = true;
            class_310.method_1551().method_1507(null);
            class_310.method_1551().method_63588(() -> {
                Menu menu = new Menu();
                class_310.method_1551().method_1507((class_437)menu);
                class_310.method_1551().method_63588(menu::openHandsEditor);
            });
        }
    });
    public DelimiterSetting rightTitle = new DelimiterSetting(this, "\u041f\u0440\u0430\u0432\u0430\u044f \u0440\u0443\u043a\u0430");
    public SliderSetting rx = new SliderSetting(this, "\u041f\u0440\u0430\u0432\u0430\u044f X", 0.0f, -2.0f, 2.0f, 0.05f);
    public SliderSetting ry = new SliderSetting(this, "\u041f\u0440\u0430\u0432\u0430\u044f Y", 0.0f, -2.0f, 2.0f, 0.05f);
    public SliderSetting rz = new SliderSetting(this, "\u041f\u0440\u0430\u0432\u0430\u044f Z", 0.0f, -2.0f, 2.0f, 0.05f);
    public SliderSetting rScale = new SliderSetting(this, "\u041f\u0440\u0430\u0432\u0430\u044f \u0440\u0430\u0437\u043c\u0435\u0440", 1.0f, 0.1f, 3.0f, 0.05f);
    public ButtonSetting resetRightButton = new ButtonSetting((Module)this, "\u0421\u0431\u0440\u043e\u0441\u0438\u0442\u044c \u043f\u0440\u0430\u0432\u0443\u044e", this::resetRight);
    public DelimiterSetting leftTitle = new DelimiterSetting(this, "\u041b\u0435\u0432\u0430\u044f \u0440\u0443\u043a\u0430");
    public SliderSetting lx = new SliderSetting(this, "\u041b\u0435\u0432\u0430\u044f X", 0.0f, -2.0f, 2.0f, 0.05f);
    public SliderSetting ly = new SliderSetting(this, "\u041b\u0435\u0432\u0430\u044f Y", 0.0f, -2.0f, 2.0f, 0.05f);
    public SliderSetting lz = new SliderSetting(this, "\u041b\u0435\u0432\u0430\u044f Z", 0.0f, -2.0f, 2.0f, 0.05f);
    public SliderSetting lScale = new SliderSetting(this, "\u041b\u0435\u0432\u0430\u044f \u0440\u0430\u0437\u043c\u0435\u0440", 1.0f, 0.1f, 3.0f, 0.05f);
    public ButtonSetting resetLeftButton = new ButtonSetting((Module)this, "\u0421\u0431\u0440\u043e\u0441\u0438\u0442\u044c \u043b\u0435\u0432\u0443\u044e", this::resetLeft);
    public ButtonSetting resetAllButton = new ButtonSetting((Module)this, "\u0421\u0431\u0440\u043e\u0441\u0438\u0442\u044c \u0432\u0441\u0451", this::resetAll);

    public static Hands get() {
        return Instance.get(Hands.class);
    }

    @Override
    protected void onEnable() {
        SwingAnimation swingAnimation = SwingAnimation.get();
        if (this.restoreSwingAnimation && swingAnimation != null && !swingAnimation.isEnabled()) {
            swingAnimation.setEnabled(true, false);
        }
        GlassHands glassHands = GlassHands.getInstance();
        if (this.restoreGlassHands && glassHands != null && !glassHands.isEnabled()) {
            glassHands.setEnabled(true, false);
        }
    }

    @Override
    protected void onDisable() {
        GlassHands glassHands;
        SwingAnimation swingAnimation = SwingAnimation.get();
        boolean bl = this.restoreSwingAnimation = swingAnimation != null && swingAnimation.isEnabled();
        if (swingAnimation != null && swingAnimation.isEnabled()) {
            swingAnimation.setEnabled(false, false);
        }
        boolean bl2 = this.restoreGlassHands = (glassHands = GlassHands.getInstance()) != null && glassHands.isEnabled();
        if (glassHands != null && glassHands.isEnabled()) {
            glassHands.setEnabled(false, false);
        }
    }

    public void applyHandTranslation(class_4587 matrices, class_1306 arm) {
        if (!this.shouldApplyTransforms()) {
            return;
        }
        float x = (arm == class_1306.field_6183 ? this.rx.getValue() : this.lx.getValue()).floatValue();
        float y = (arm == class_1306.field_6183 ? this.ry.getValue() : this.ly.getValue()).floatValue();
        float z = (arm == class_1306.field_6183 ? this.rz.getValue() : this.lz.getValue()).floatValue();
        matrices.method_46416(x, y, z);
    }

    public void applyHandScale(class_4587 matrices, class_1306 arm) {
        if (!this.shouldApplyTransforms()) {
            return;
        }
        float s = (arm == class_1306.field_6183 ? this.rScale.getValue() : this.lScale.getValue()).floatValue();
        matrices.method_22905(s, s, s);
    }

    public void applyEmptyHandAlignment(class_4587 matrices, class_1306 arm) {
        if (!this.shouldApplyTransforms()) {
            return;
        }
        matrices.method_46416(arm == class_1306.field_6183 ? -0.08f : 0.08f, 0.08f, 0.0f);
    }

    public boolean shouldApplyTransforms() {
        return this.isEnabled() || this.editorActive;
    }

    public boolean isEditorActive() {
        return this.editorActive;
    }

    public void setEditorActive(boolean editorActive) {
        this.editorActive = editorActive;
    }

    public void resetRight() {
        this.rx.set(Float.valueOf(0.0f));
        this.ry.set(Float.valueOf(0.0f));
        this.rz.set(Float.valueOf(0.0f));
        this.rScale.set(Float.valueOf(1.0f));
    }

    public void resetLeft() {
        this.lx.set(Float.valueOf(0.0f));
        this.ly.set(Float.valueOf(0.0f));
        this.lz.set(Float.valueOf(0.0f));
        this.lScale.set(Float.valueOf(1.0f));
    }

    public void resetAll() {
        this.resetRight();
        this.resetLeft();
    }
}


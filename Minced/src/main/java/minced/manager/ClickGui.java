/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.fabric.impl.client.screen.ScreenExtensions
 *  net.fabricmc.fabric.mixin.screen.ScreenAccessor
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Drawable
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.ParentElement
 *  net.minecraft.client.gui.navigation.Navigable
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.text.Text
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec2f
 */
package minced.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import minced.core.GuiConfigsPanel;
import minced.core.ColorUtil;
import minced.core.BindSetting;
import minced.core.SliderSetting;
import minced.core.ScaleState;
import minced.core.Setting;
import minced.core.Render2DBatch;
import minced.core.ScaleUtil;
import minced.core.Animation;
import minced.core.GuiModuleList;
import minced.core.GuiSidebar;
import minced.core.GuiHeader;
import minced.core.GuiThemePanel;
import minced.core.BindPopup;
import minced.core.ColorPickerElement;
import minced.core.BindPopupState;
import minced.core.Easings;
import minced.core.AnimatedFloat;
import minced.core.RenderUtil;
import minced.core.Minced;
import minced.core.Module;
import minced.core.RecordClass234;
import minced.enums.Combat;
import net.fabricmc.fabric.impl.client.screen.ScreenExtensions;
import net.fabricmc.fabric.mixin.screen.ScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.navigation.Navigable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public class ClickGui
extends Screen
implements  Navigable,
Element,
ParentElement,
Drawable
{


    static float cfr_renamed_335;
    static float cfr_renamed_336;

    AnimatedFloat field11194;
    boolean field11195;
    boolean field11196;
    Combat field11197;
    Combat var_minced_enum_Combat_I;
    boolean field11198;
    boolean field11199;
    SliderSetting field11200;
    Object field11201;
    GuiThemePanel field11202;
    int field11203;
    GuiSidebar field11204;
    GuiModuleList field11205;
    GuiConfigsPanel field11206;
    Animation field11207 = new Animation(Easings.field11957, 0.0f, field11234);
    static float field11208;
    static MinecraftClient var_net_minecraft_client_MinecraftClient_I = MinecraftClient.getInstance();
    static float field11209;
    int field11210;
    GuiHeader field11211;
    boolean field11212;
    float field11213;
    static float field11214;
    static float field11215;
    static float field11216;
    static float field11217;
    static float field11218;
    static float field11219;
    static int field11220;
    static int field11221;
    static int field11222;
    static int field11223;
    static float field11224;
    static float field11225;
    static float field11226;
    static float field11227;
    static int field11228;
    static float field11229;
    static float field11230;
    static float field11231;
    static float field11232;
    static float field11233;
    static long field11234;
    static float field11235;
    static double field11236;

    protected void init() {
        super.init();
        this.field11194.setValue(1.0f);
        this.field11195 = false;
        this.field11196 = false;
        this.var_minced_enum_Combat_I = this.field11197;
        this.field11199 = this.field11198;
        this.field11200 = null;
        this.field11201 = null;
        this.updatePosition();
    }

    public GuiThemePanel getThemeViewComponent() {
        return this.field11202;
    }

    public int getPosX() {
        return this.field11203;
    }

    public AnimatedFloat getContentAlphaAnimation() {
        return this.field11194;
    }

    public void close() {
        this.field11195 = true;
        this.field11200 = null;
        this.field11201 = null;
        this.field11204.method2956();
        this.field11205.method2927();
        this.field11206.method119();
        ColorPickerElement.method3236();
        BindPopup.method3175();
        BindPopupState.method3276();
    }

    public boolean isTextInputFocused() {
        return this.field11206.method116() || this.field11204.method2966() || this.field11201 != null;
    }

    public boolean isPendingThemeView() {
        return this.field11199;
    }

    public GuiConfigsPanel getConfigsView() {
        return this.field11206;
    }

    public Animation getOpeningAnimation() {
        return this.field11207;
    }

    public void render(DrawContext param1, int param2, int param3, float param4) {
        this.field11207.setEasing(this.field11195 ? Easings.field11956 : Easings.field11957);
        this.field11207.animate(this.field11195 ? 0.0f : 1.0f);
        float v5 = MathHelper.clamp((float)this.field11207.getAnimatedValue(), (float)0.0f, (float)1.0f);
        if (this.field11195 && v5 <= field11208) {
            super.close();
            return;
        }
        Vec2f v6 = ScaleUtil.method2887(param2, param3);
        ScaleState v7 = ScaleUtil.method2885(2.0f);
        this.updatePosition();
        this.updateContentSwitch();
        float v8 = v5 * this.field11194.getValue();
        Render2DBatch.method2668(param1);
        Render2DBatch.method2672(0.0f, 0.0f, var_net_minecraft_client_MinecraftClient_I.getWindow().getWidth(), var_net_minecraft_client_MinecraftClient_I.getWindow().getHeight(), ColorUtil.method1852(10, 10, 10, (int)(field11209 * v5)));
        Render2DBatch.method2651();
        this.renderInterface(param1, v5, v8, v6.x, v6.y);
        ScaleUtil.method2886(v7);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private void updateMovementKeysWhileOpen() {
        KeyBinding[] v1 = new KeyBinding[]{ClickGui.var_net_minecraft_client_MinecraftClient_I.options.forwardKey, ClickGui.var_net_minecraft_client_MinecraftClient_I.options.backKey, ClickGui.var_net_minecraft_client_MinecraftClient_I.options.leftKey, ClickGui.var_net_minecraft_client_MinecraftClient_I.options.rightKey, ClickGui.var_net_minecraft_client_MinecraftClient_I.options.jumpKey};
        if (this.isTextInputFocused()) {
            KeyBinding[] v2 = v1;
            for (int v4 = 0; v4 < 5; ++v4) {
                KeyBinding v5 = v2[v4];
                v5.setPressed(false);
            }
            return;
        }
        KeyBinding[] v2 = v1;
        for (int v4 = 0; v4 < 5; ++v4) {
            KeyBinding v5 = v2[v4];
            boolean v6 = InputUtil.isKeyPressed((long)var_net_minecraft_client_MinecraftClient_I.getWindow().getHandle(), (int)v5.getDefaultKey().getCode());
            v5.setPressed(v6);
        }
    }

    public Object getActiveBinding() {
        return this.field11201;
    }

    public boolean isThemeView() {
        return this.field11198;
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public boolean mouseClicked(double param1, double param3, int param5) {
        Object v82;
        Object v9;
        boolean v7;
        Vec2f v6 = ScaleUtil.method2887((int)param1, (int)param3);
        param1 = v6.x;
        param3 = v6.y;
        boolean bl = v7 = !this.field11198 && this.field11197 == Combat.Configs && this.field11204.method2967().isEmpty();
        if (v7 && this.field11206.method131() && this.field11206.method113((float)param1, (float)param3, param5, this.field11203, this.field11210)) {
            return true;
        }
        if (param5 != 0 && (v9 = this.field11201) instanceof Module) {
            Module v82_234 = (Module)v9;
            v82_234.setKey(-param5);
            this.field11201 = null;
            return true;
        }
        if (param5 != 0 && (v9 = this.field11201) instanceof BindSetting) {
            BindSetting v82_240 = (BindSetting)v9;
            v82_240.setValue(param5);
            this.field11201 = null;
            return true;
        }
        if (param5 == 0 && this.field11211.method2975((float)param1, (float)param3, this.field11203, this.field11210)) {
            this.requestContentSwitch(this.field11197, true);
            return true;
        }
        if (param5 == 0 && (v82 = this.field11204.method2957((float)param1, (float)param3, this.field11203, this.field11210)) != null) {
            this.requestContentSwitch((Combat)v82, false);
            return true;
        }
        if (this.field11198) {
            if (this.field11202.method309((float)param1, (float)param3, this.field11203, this.field11210)) {
                return true;
            }
        } else if (v7) {
            if (this.field11206.method113((float)param1, (float)param3, param5, this.field11203, this.field11210)) {
                return true;
            }
        } else {
            v82 = this.field11205.cfr_renamed_4((float)param1, (float)param3, param5, this.field11203, this.field11210, this.field11197, this.field11204.method2967(), this.field11200, this.field11201);
            this.applyClickResult((RecordClass234)v82);
            if (((RecordClass234)v82).handled() || ((RecordClass234)v82).clearBindingRequested() || ((RecordClass234)v82).newSlider() != null || ((RecordClass234)v82).newBinding() != null) {
                if (((RecordClass234)v82).newSlider() != null) {
                    this.updateSlider(param1);
                }
                return true;
            }
        }
        if (this.field11204.method2960((float)param1, (float)param3, this.field11203, this.field11210)) {
            return true;
        }
        return super.mouseClicked(param1, param3, param5);
    }

    public GuiSidebar getSidebar() {
        return this.field11204;
    }

    public boolean isSuppressNextCharTyped() {
        return this.field11212;
    }

    public Combat getCurrentCategory() {
        return this.field11197;
    }

    public GuiHeader getHeader() {
        return this.field11211;
    }

    public GuiModuleList getModuleList() {
        return this.field11205;
    }

    private void applyClickResult(RecordClass234 param1) {
        if (param1.newSlider() != null) {
            this.field11200 = param1.newSlider();
            this.field11213 = param1.newSliderX();
        }
        if (param1.clearBindingRequested()) {
            this.field11201 = null;
        } else if (param1.newBinding() != null) {
            this.field11201 = param1.newBinding();
        }
    }

    public Combat getPendingCategory() {
        return this.var_minced_enum_Combat_I;
    }

    private void updatePosition() {
        this.field11203 = (int)((float)var_net_minecraft_client_MinecraftClient_I.getWindow().getScaledWidth() / 2.0f - field11214);
        this.field11210 = (int)((float)var_net_minecraft_client_MinecraftClient_I.getWindow().getScaledHeight() / 2.0f - field11215);
    }

    public boolean shouldPause() {
        return false;
    }

    public float getActiveSliderX() {
        return this.field11213;
    }

    public SliderSetting getActiveSlider() {
        return this.field11200;
    }

    private void updateContentSwitch() {
        if (this.field11196) {
            this.field11194.setTarget(0.0f);
            if (this.field11194.getValue() <= field11216) {
                this.field11197 = this.var_minced_enum_Combat_I;
                this.field11198 = this.field11199;
                if (!this.field11198) {
                    this.field11205.method2928();
                    if (this.field11197 == Combat.Configs) {
                        this.field11206.method133();
                    } else {
                        this.field11206.method119();
                    }
                }
                this.field11196 = false;
                this.field11194.setValue(0.0f);
            }
        } else {
            this.field11194.setTarget(1.0f);
        }
    }

    public boolean isContentSwitching() {
        return this.field11196;
    }

    public void mouseMoved(double param1, double param3) {
        Vec2f v5 = ScaleUtil.method2887((int)param1, (int)param3);
        this.updateSlider(v5.x);
        super.mouseMoved(param1, param3);
    }

    public boolean charTyped(char param1, int param2) {
        if (this.field11201 != null) {
            return true;
        }
        if (this.field11212) {
            this.field11212 = false;
            return true;
        }
        if (!this.field11198 && this.field11197 == Combat.Configs && this.field11206.method110(param1)) {
            return true;
        }
        if (this.field11205.method2933(param1, param2)) {
            return true;
        }
        if (this.field11204.method2959(param1)) {
            this.field11205.method2928();
            return true;
        }
        return super.charTyped(param1, param2);
    }

    private void requestContentSwitch(Combat param1, boolean param2) {
        if (!this.field11196 && param2 && this.field11198) {
            return;
        }
        if (!(this.field11196 || param2 || this.field11198 || param1 != this.field11197)) {
            this.field11205.method2928();
            return;
        }
        this.var_minced_enum_Combat_I = param1;
        this.field11199 = param2;
        this.field11196 = true;
        this.field11204.method2956();
        this.field11200 = null;
        this.field11201 = null;
    }

    public void tick() {
        this.updateMovementKeysWhileOpen();
        super.tick();
    }

    public boolean mouseReleased(double param1, double param3, int param5) {
        this.field11200 = null;
        return super.mouseReleased(param1, param3, param5);
    }

    public boolean mouseDragged(double param1, double param3, int param5, double param6, double param8) {
        Vec2f v10 = ScaleUtil.method2887((int)param1, (int)param3);
        this.updateSlider(v10.x);
        return this.field11200 != null || super.mouseDragged(param1, param3, param5, param6, param8);
    }

    private void renderInterface(DrawContext param1, float param2, float param3, float param4, float param5) {
        Minced.getInstance().getThemeManager().cfr_renamed_334();
        int v6 = ColorUtil.method1853(Minced.getInstance().getThemeManager().method279(), param2);
        RenderUtil.method391(this.field11203 - 8, this.field11210 - 8, field11217, field11218, field11219, ColorUtil.method1853(field11220, param2), ColorUtil.method1853(field11221, param2), ColorUtil.method1853(field11222, param2), ColorUtil.method1853(field11223, param2), param2, field11224);
        RenderUtil.method390(this.field11203, this.field11210, field11225, field11226, field11227, ColorUtil.method1853(field11228, param2 - field11229), param2);
        RenderUtil.method397(this.field11203, this.field11210, field11230, field11231, field11232, ColorUtil.method1853(v6, param2 - field11233));
        Combat v7 = this.field11196 && !this.field11199 ? this.var_minced_enum_Combat_I : this.field11197;
        this.field11211.method2974(param1, this.field11203, this.field11210, param2, param3, this.field11198, this.field11197);
        this.field11204.method2963(param1, this.field11203, this.field11210, param2, v7);
        if (this.field11198) {
            this.field11202.method310(param1, this.field11203, this.field11210, param3, param4, param5);
        } else if (this.field11197 == Combat.Configs && this.field11204.method2967().isEmpty()) {
            this.field11206.method127(param1, this.field11203, this.field11210, param3, param4, param5);
        } else {
            this.field11205.cfr_renamed_4(param1, this.field11203, this.field11210, param3, this.field11197, this.field11204.method2967(), this.field11201);
        }
        if (this.field11197 == Combat.Configs && !this.field11198) {
            this.field11206.method132(param1, this.field11203, this.field11210, param2, param4, param5);
        }
    }

    public boolean keyPressed(int param1, int param2, int param3) {
        if (!this.field11198 && this.field11197 == Combat.Configs && this.field11206.method109(param1, param3)) {
            return true;
        }
        if (this.field11205.method2932(param1, param3)) {
            return true;
        }
        if (this.field11201 != null) {
            Object v42;
            if (param1 == 256 || param1 == 261) {
                Object v42_446;
                Object v5 = this.field11201;
                if (v5 instanceof Module) {
                    v42_446 = (Module)v5;
                    ((Module)v42_446).setKey(-100);
                }
                if ((v5 = this.field11201) instanceof BindSetting) {
                    v42_446 = (BindSetting)v5;
                    ((Setting)v42_446).setValue(-1);
                }
                this.field11201 = null;
                this.field11212 = true;
                return true;
            }
            Object v5 = this.field11201;
            if (v5 instanceof Module) {
                v42 = (Module)v5;
                ((Module)v42).setKey(param1);
            }
            if ((v5 = this.field11201) instanceof BindSetting) {
                v42 = (BindSetting)v5;
                ((Setting)v42).setValue(param1);
            }
            this.field11201 = null;
            this.field11212 = true;
            return true;
        }
        if (this.field11204.method2961(param1)) {
            return true;
        }
        if (param1 == 256) {
            this.close();
            return true;
        }
        return super.keyPressed(param1, param2, param3);
    }

    public int getPosY() {
        return this.field11210;
    }

    public ClickGui() {
        super((Text)Text.empty());
        this.field11194 = new AnimatedFloat(1.0f, field11235, Easings.field11969);
        this.field11211 = new GuiHeader();
        this.field11204 = new GuiSidebar(Combat.Combat);
        this.field11205 = new GuiModuleList();
        this.field11202 = new GuiThemePanel();
        this.field11206 = new GuiConfigsPanel();
        this.var_minced_enum_Combat_I = this.field11197 = Combat.Combat;
    }

    private void updateSlider(double param1) {
        if (this.field11200 == null) {
            return;
        }
        float v3 = this.field11200.field7742;
        float v4 = this.field11200.field7743;
        float v5 = (float)((double)v3 + (double)(v4 - v3) * MathHelper.clamp((double)((param1 - (double)this.field11213) / field11236), (double)0.0, (double)1.0));
        this.field11200.method1906(Float.valueOf(v5));
    }

    public boolean mouseScrolled(double param1, double param3, double param5, double param7) {
        Vec2f v9 = ScaleUtil.method2887((int)param1, (int)param3);
        param1 = v9.x;
        param3 = v9.y;
        if (this.field11198 ? this.field11202.method318((float)param1, (float)param3, param5, param7, this.field11203, this.field11210) : (this.field11197 == Combat.Configs && this.field11204.method2967().isEmpty() ? this.field11206.method120((float)param1, (float)param3, param7, this.field11203, this.field11210) : this.field11205.method2931((float)param1, (float)param3, param5, param7, this.field11203, this.field11210))) {
            return true;
        }
        return super.mouseScrolled(param1, param3, param5, param7);
    }

    public boolean isClosing() {
        return this.field11195;
    }

    static {
        cfr_renamed_335 = 0.0f;
        cfr_renamed_336 = 0.0f;
        field11234 = 180L;
        field11235 = 12.0f;
        field11208 = 0.01f;
        field11209 = 105.0f;
        field11217 = 464.5f;
        field11218 = 288.5f;
        field11219 = 8.0f;
        field11220 = -16119286;
        field11221 = -16119286;
        field11222 = -16119286;
        field11223 = -16119286;
        field11224 = 6.0f;
        field11225 = 448.5f;
        field11226 = 272.5f;
        field11227 = 8.0f;
        field11228 = -16119286;
        field11229 = 0.05f;
        field11230 = 448.5f;
        field11231 = 272.5f;
        field11232 = 8.0f;
        field11233 = 0.96f;
        field11236 = 144.0;
        field11216 = 0.05f;
        field11214 = 224.25f;
        field11215 = 136.25f;
    }
}






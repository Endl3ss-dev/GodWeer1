/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_11909
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_332
 *  net.minecraft.class_429
 *  net.minecraft.class_437
 *  net.minecraft.class_500
 *  net.minecraft.class_526
 */
package ru.decide.screen;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_429;
import net.minecraft.class_437;
import net.minecraft.class_500;
import net.minecraft.class_526;
import ru.decide.alt.AltManager;
import ru.decide.lang.Lang;
import ru.decide.screen.AltManagerScreen;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.MathUtil;
import ru.decide.utils.render.MainMenuShaderRenderer;
import ru.decide.utils.render.Render2D;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.ScreenBlur;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public class MainMenuScreen
extends class_437
implements IMinecraft {
    public Animation alpha = new Animation();
    private final List<MenuButton> buttons = new ArrayList<MenuButton>();
    private MenuButton multiplayerButton;
    private MenuButton singleplayerButton;
    private MenuButton settingsButton;
    private MenuButton accountsButton;
    private MenuButton modsButton;
    private MenuButton exitButton;
    boolean exit = false;
    private float scaleFix = 1.0f;
    double lastMouseX;
    double lastMouseY;
    private final MainMenuShaderRenderer menuShader = new MainMenuShaderRenderer();
    private boolean exitSlideDragging = false;
    private float exitSlideProgress = 0.0f;
    private float exitSlideTarget = 0.0f;
    private boolean exitTriggered = false;
    private float exitFade = 1.0f;
    private float exitFadeTarget = 1.0f;
    private float langBtnX;
    private float langBtnY;
    private float langBtnW;
    private float langBtnH;
    private final Animation langHover = new Animation();

    public MainMenuScreen() {
        super((class_2561)class_2561.method_43470((String)"MainMenuScreen"));
    }

    protected void method_25426() {
        this.exit = false;
        this.alpha.set(0.0);
        this.alpha.run(1.0, 0.5, Easings.BACK_OUT);
        AltManager.get().bootstrap();
        this.buildButtons();
    }

    private void buildButtons() {
        this.buttons.clear();
        this.singleplayerButton = new MenuButton("\u041e\u0434\u0438\u043d\u043e\u0447\u043d\u0430\u044f \u0438\u0433\u0440\u0430", "Singleplayer", "\u0418\u0433\u0440\u0430\u0442\u044c \u0432 \u043a\u0430\u043c\u043f\u0430\u043d\u0438\u044e \u0438 \u043e\u0444\u0444\u043b\u0430\u0439\u043d-\u043c\u0438\u0440\u044b", "Play the campaign and offline worlds", () -> mc.method_1507((class_437)new class_526((class_437)this)));
        this.multiplayerButton = new MenuButton("\u0421\u0435\u0442\u0435\u0432\u0430\u044f \u0438\u0433\u0440\u0430", "Multiplayer", "\u041f\u043e\u0434\u043a\u043b\u044e\u0447\u0438\u0442\u044c\u0441\u044f \u043a \u043c\u0443\u043b\u044c\u0442\u0438\u043f\u043b\u0435\u0435\u0440-\u0441\u0435\u0440\u0432\u0435\u0440\u0430\u043c", "Join and play on multiplayer servers", () -> mc.method_1507((class_437)new class_500((class_437)this)));
        this.buttons.add(this.multiplayerButton);
        this.buttons.add(this.singleplayerButton);
        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            this.modsButton = new MenuButton("\u041c\u043e\u0434\u044b", "Mods", "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u043d\u044b\u043c\u0438 \u043c\u043e\u0434\u0438\u0444\u0438\u043a\u0430\u0446\u0438\u044f\u043c\u0438", "Manage your installed modifications", () -> {
                try {
                    Class<?> modMenuClass = Class.forName("com.terraformersmc.modmenu.gui.ModsScreen");
                    class_437 modsScreen = (class_437)modMenuClass.getConstructor(class_437.class).newInstance(this);
                    mc.method_1507(modsScreen);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });
            this.buttons.add(this.modsButton);
        } else {
            this.modsButton = null;
        }
        this.accountsButton = new MenuButton("\u0410\u043a\u043a\u0430\u0443\u043d\u0442\u044b", "Accounts", "\u041f\u0435\u0440\u0435\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u0438 \u0443\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0430\u043a\u043a\u0430\u0443\u043d\u0442\u0430\u043c\u0438", "Switch and manage your accounts", () -> mc.method_1507((class_437)new AltManagerScreen(this)));
        this.settingsButton = new MenuButton("\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438", "Settings", "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u0438\u0433\u0440\u044b \u0438 \u0432\u0438\u0434\u0435\u043e", "Adjust game and video options", () -> mc.method_1507((class_437)new class_429((class_437)this, MainMenuScreen.mc.field_1690)));
        this.exitButton = new MenuButton("\u0412\u044b\u0445\u043e\u0434", "Exit", "\u0417\u0430\u043a\u0440\u044b\u0442\u044c \u0438\u0433\u0440\u0443 \u0438 \u0432\u044b\u0439\u0442\u0438 \u043d\u0430 \u0440\u0430\u0431\u043e\u0447\u0438\u0439 \u0441\u0442\u043e\u043b", "Close the game and return to desktop", true, () -> mc.method_1592());
        this.buttons.add(this.settingsButton);
        this.buttons.add(this.accountsButton);
        this.buttons.add(this.exitButton);
    }

    public void method_25420(class_332 context, int mouseX, int mouseY, float delta) {
    }

    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        this.closeCheck();
        mouseX = (int)((float)mouseX / this.scaleFix);
        mouseY = (int)((float)mouseY / this.scaleFix);
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
        this.alpha.update();
        float slideSpeed = 0.12f;
        this.exitSlideProgress += (this.exitSlideTarget - this.exitSlideProgress) * slideSpeed;
        if (this.exitSlideTarget == 1.0f && this.exitSlideProgress > 0.99f) {
            this.exitSlideProgress = 1.0f;
        }
        if (this.exitSlideProgress >= 1.0f && !this.exitTriggered) {
            this.exitTriggered = true;
            this.exitFadeTarget = 0.0f;
        }
        this.exitFade += (this.exitFadeTarget - this.exitFade) * 0.08f;
        if (this.exitTriggered && this.exitFade < 0.02f) {
            this.exitFade = 0.0f;
            mc.method_1592();
        }
        this.renderOverlay(context);
    }

    public void renderOverlay(class_332 context) {
        float targetScale = 2.0f;
        float currentScale = mc.method_22683().method_4495();
        this.scaleFix = targetScale / currentScale;
        int screenWidth = (int)((float)mc.method_22683().method_4486() / this.scaleFix);
        int screenHeight = (int)((float)mc.method_22683().method_4502() / this.scaleFix);
        if (context != null) {
            context.method_51448().pushMatrix();
        }
        Render2D.beginOverlay();
        float alphaVal = this.alpha.get() * this.exitFade;
        float parallaxStrength = 0.07f;
        float offsetX = ((float)screenWidth / 2.0f - (float)this.lastMouseX) * parallaxStrength;
        float offsetY = ((float)screenHeight / 2.0f - (float)this.lastMouseY) * parallaxStrength;
        float bgScale = 1.1f;
        float bgW = (float)screenWidth * bgScale;
        float bgH = (float)screenHeight * bgScale;
        float bgX = ((float)screenWidth - bgW) / 2.0f + offsetX;
        float bgY = ((float)screenHeight - bgH) / 2.0f + offsetY;
        RenderUtil.Images.texture(class_2960.method_60655((String)"decide", (String)"textures/frame/menu.png"), bgX, bgY, bgW, bgH, ColorUtil.getColor(255, alphaVal));
        RenderUtil.Render2D.rect(0.0f, 0.0f, screenWidth, screenHeight, ColorUtil.replAlpha(ColorUtil.getColor(0), 0.2f * alphaVal));
        ScreenBlur.capture(2);
        RenderUtil.Images.texture(class_2960.method_60655((String)"decide", (String)"textures/frame/logo.png"), (float)screenWidth / 2.0f - 24.0f, (float)screenHeight * 0.3f + 30.0f - 30.0f * alphaVal - 24.0f, 48.0f, 48.0f, ColorUtil.getColor(255, alphaVal));
        Fonts.sf_regular.drawCentered("DecideVisuals", screenWidth / 2, (float)screenHeight * 0.34f + 30.0f - 30.0f * alphaVal, 12.0f, ColorUtil.getColor(255, alphaVal));
        for (MenuButton b : this.buttons) {
            b.update(this.lastMouseX, this.lastMouseY);
        }
        this.drawMenuButtons(screenWidth, screenHeight, alphaVal);
        Render2D.endOverlay();
        if (context != null) {
            context.method_51448().popMatrix();
        }
    }

    private void drawMenuButtons(int screenWidth, int screenHeight, float alphaVal) {
        this.updateExitSlideProgress();
        float gap = 4.0f;
        float panelWidth = Math.min(220.0f, (float)screenWidth - 36.0f);
        float cardWidth = (panelWidth - gap) / 2.0f;
        float cardHeight = 25.0f;
        float totalCardsWidth = cardWidth * 2.0f + gap;
        float smallGap = 4.0f;
        int smallCount = this.modsButton == null ? 2 : 3;
        float smallWidth = (totalCardsWidth - smallGap * (float)(smallCount - 1)) / (float)smallCount;
        float smallHeight = 25.0f;
        float exitWidth = Math.min(150.0f, totalCardsWidth * 0.65f);
        float exitHeight = 25.0f;
        float totalBlockHeight = cardHeight + gap + smallHeight + gap + exitHeight;
        float startX = (float)screenWidth / 2.0f - totalCardsWidth / 2.0f;
        float startY = (float)screenHeight / 2.0f - totalBlockHeight / 2.0f;
        float appearY = (1.0f - alphaVal) * 34.0f;
        float currentY = startY + appearY;
        this.multiplayerButton.drawHero(startX, currentY, cardWidth, cardHeight, alphaVal, 0);
        this.singleplayerButton.drawHero(startX + cardWidth + gap, currentY, cardWidth, cardHeight, alphaVal, 1);
        float smallX = startX;
        this.settingsButton.drawCompact(smallX, currentY += cardHeight + gap, smallWidth, smallHeight, alphaVal, "l");
        this.accountsButton.drawCompact(smallX += smallWidth + smallGap, currentY, smallWidth, smallHeight, alphaVal, "m");
        if (this.modsButton != null) {
            this.modsButton.drawCompact(smallX += smallWidth + smallGap, currentY, smallWidth, smallHeight, alphaVal, "n");
        }
        float exitX = (float)screenWidth / 2.0f - exitWidth / 2.0f;
        this.exitButton.drawExit(exitX, currentY += smallHeight + gap, exitWidth, exitHeight, alphaVal, this.exitSlideProgress, this.exitSlideDragging);
    }

    private void updateExitSlideProgress() {
        if (!this.exitSlideDragging || this.exitButton == null) {
            return;
        }
        float trackX = this.exitButton.getLastX() + 5.0f;
        float trackW = Math.max(1.0f, this.exitButton.getLastW() - 34.0f);
        this.exitSlideProgress = Math.max(0.0f, Math.min(1.0f, ((float)this.lastMouseX - trackX) / trackW));
    }

    private void drawLanguageButton(int screenWidth, float alphaVal) {
        float w = 25.0f;
        float h = 15.0f;
        float bx = (float)screenWidth - w - 12.0f;
        float by = 12.0f;
        this.langBtnX = bx;
        this.langBtnY = by;
        this.langBtnW = w;
        this.langBtnH = h;
        boolean hov = MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, bx, by, w, h);
        this.langHover.update();
        this.langHover.run(hov ? 1.0 : 0.0, (double)0.18f, Easings.QUAD_OUT);
        float hp = this.langHover.get();
        int accent = ColorUtil.getColor(125, 145, 244);
        RenderUtil.Blur.blur(bx, by, w, h, 1.0f, 5.0f, ColorUtil.getColor(20, alphaVal * (0.3f + 0.1f * hp)));
        int textColor = ColorUtil.replAlpha(ColorUtil.interpolateColor(ColorUtil.getColor(170, 170, 170, 1.0f), accent, hp), alphaVal);
        Fonts.sf_regular.drawCentered(Lang.tag(), bx + w / 2.0f, by + h / 2.0f - 3.8f, 6.0f, textColor);
    }

    public boolean method_25402(class_11909 click, boolean doubled) {
        if (this.exitTriggered) {
            return true;
        }
        float mouseX = (float)(click.comp_4798() / (double)this.scaleFix);
        float mouseY = (float)(click.comp_4799() / (double)this.scaleFix);
        int button = click.method_74245();
        if (button == 0) {
            if (MathUtil.isHovered(mouseX, mouseY, this.langBtnX, this.langBtnY, this.langBtnW, this.langBtnH)) {
                Lang.toggle();
                return true;
            }
            for (MenuButton btn : this.buttons) {
                if (!btn.isHovered()) continue;
                if (btn == this.exitButton) {
                    this.exitSlideDragging = true;
                    this.updateExitSlideProgress();
                    return true;
                }
                btn.onClick();
                return true;
            }
        }
        return super.method_25402(click, doubled);
    }

    public boolean method_25403(class_11909 click, double deltaX, double deltaY) {
        if (this.exitSlideDragging) {
            this.lastMouseX = click.comp_4798() / (double)this.scaleFix;
            this.lastMouseY = click.comp_4799() / (double)this.scaleFix;
            this.updateExitSlideProgress();
            return true;
        }
        return super.method_25403(click, deltaX, deltaY);
    }

    public boolean method_25406(class_11909 click) {
        if (this.exitSlideDragging) {
            this.lastMouseX = click.comp_4798() / (double)this.scaleFix;
            this.lastMouseY = click.comp_4799() / (double)this.scaleFix;
            this.updateExitSlideProgress();
            this.exitSlideDragging = false;
            this.exitSlideTarget = this.exitSlideProgress >= 0.9f ? 1.0f : 0.0f;
            return true;
        }
        return super.method_25406(click);
    }

    public boolean method_25421() {
        return false;
    }

    public boolean method_25422() {
        if (!this.exit) {
            this.exit = false;
        }
        return false;
    }

    private void closeCheck() {
    }

    @Environment(value=EnvType.CLIENT)
    private static class MenuButton {
        private final String textRu;
        private final String textEn;
        private final String descRu;
        private final String descEn;
        private final Runnable action;
        private final boolean danger;
        private final Animation hoverAnim = new Animation();
        private boolean hovered;
        private float lastX;
        private float lastY;
        private float lastW;
        private float lastH;

        public MenuButton(String textRu, String textEn, String descRu, String descEn, Runnable action) {
            this(textRu, textEn, descRu, descEn, false, action);
        }

        public MenuButton(String textRu, String textEn, String descRu, String descEn, boolean danger, Runnable action) {
            this.textRu = textRu;
            this.textEn = textEn;
            this.descRu = descRu;
            this.descEn = descEn;
            this.danger = danger;
            this.action = action;
            this.hoverAnim.set(0.0);
        }

        private String text() {
            return this.textRu;
        }

        private String description() {
            return Lang.pick(this.descRu, this.descEn);
        }

        public float getWidth() {
            Font font = Fonts.sf_regular;
            return Math.max(font.getWidth(this.text(), 8.0f), font.getWidth(this.description(), 6.0f)) + 34.0f;
        }

        public float getHoverValue() {
            return this.hoverAnim.get();
        }

        public void update(double mouseX, double mouseY) {
            this.hovered = MathUtil.isHovered((float)mouseX, (float)mouseY, this.lastX, this.lastY, this.lastW, this.lastH);
            this.hoverAnim.update();
            this.hoverAnim.run(this.hovered ? 1.0 : 0.0, (double)0.18f, Easings.QUAD_OUT);
        }

        private void rememberBounds(float x, float y, float width, float height) {
            this.lastX = x;
            this.lastY = y;
            this.lastW = width;
            this.lastH = height;
        }

        public float getLastX() {
            return this.lastX;
        }

        public float getLastW() {
            return this.lastW;
        }

        public void drawHero(float x, float y, float width, float height, float globalAlpha, int variant) {
            this.rememberBounds(x, y, width, height);
            float hp = this.hoverAnim.get();
            float headerHeight = height;
            int accent = ColorUtil.overCol(ColorUtil.getColor(125, 125, 125), ColorUtil.client(), hp);
            RenderUtil.Blur.blur(x, y, width, headerHeight, 1.0f, 6.0f, ColorUtil.getColor(12, globalAlpha * 0.35f));
            int textColor = ColorUtil.replAlpha(ColorUtil.interpolateColor(ColorUtil.getColor(255, 1.0f), ColorUtil.getColor(255, 1.0f), hp), globalAlpha * (0.3f + hp * 0.7f));
            Fonts.sf_regular.draw(this.text(), x + 12.0f, y + 7.8f, 7.0f, textColor);
            Fonts.icon.drawCentered(variant == 0 ? "j" : "k", x + width - 15.0f, y + 9.25f, 6.0f, ColorUtil.replAlpha(accent, globalAlpha * (0.3f + hp * 0.7f)));
        }

        public void drawCompact(float x, float y, float width, float height, float globalAlpha, String icon) {
            this.rememberBounds(x, y, width, height);
            float hp = this.hoverAnim.get();
            float radius = 6.0f;
            int accent = ColorUtil.overCol(ColorUtil.getColor(255), ColorUtil.client(), hp);
            RenderUtil.Blur.blur(x, y, width, height, 1.0f, radius, ColorUtil.getColor(12, globalAlpha * 0.35f));
            int textColor = ColorUtil.replAlpha(ColorUtil.getColor(255), globalAlpha * (0.3f + hp * 0.7f));
            Fonts.sf_regular.draw(this.text(), x + 12.0f, y + 7.8f, 7.0f, textColor);
            Fonts.icon.drawCentered(icon, x + width - 15.0f, y + 9.25f, 6.0f, ColorUtil.replAlpha(accent, globalAlpha * (0.2f + hp * 0.8f)));
        }

        public void drawExit(float x, float y, float width, float height, float globalAlpha, float slideProgress, boolean dragging) {
            this.rememberBounds(x, y, width, height);
            float hp = this.hoverAnim.get();
            float radius = 6.0f;
            int accent = ColorUtil.getColor(255, dragging ? 145 : 255, dragging ? 145 : 255);
            int fill = ColorUtil.getColor(20, globalAlpha * 0.35f);
            float progress = Math.max(0.0f, Math.min(1.0f, slideProgress));
            float knobSize = height - 8.0f;
            float knobX = x + 5.0f + (width - knobSize - 10.0f) * progress;
            RenderUtil.Blur.blur(x, y, width, height, 1.0f, radius, fill);
            RenderUtil.Render2D.rect(knobX, y + 4.0f, knobSize, knobSize, ColorUtil.getColor(255, 255, 255, globalAlpha * (0.02f + hp * 0.03f + (dragging ? 0.02f : 0.0f))), 4.0f);
            Fonts.icon.drawCentered("i", knobX + knobSize / 2.0f, y + height / 2.0f - 3.0f, 7.0f, ColorUtil.replAlpha(accent, globalAlpha * (0.2f + hp * 0.2f + progress * 0.2f)));
            Fonts.sf_regular.drawCentered(Lang.pick("\u0421\u0434\u0432\u0438\u043d\u044c\u0442\u0435, \u0447\u0442\u043e\u0431\u044b \u0432\u044b\u0439\u0442\u0438", "\u0421\u0434\u0432\u0438\u043d\u044c\u0442\u0435, \u0447\u0442\u043e\u0431\u044b \u0432\u044b\u0439\u0442\u0438"), x + width / 2.0f + 8.0f, y + height / 2.0f - 4.4f, 7.0f, ColorUtil.getColor(255, globalAlpha * (0.1f + hp * 0.2f - progress * 0.16f)));
        }

        public void draw(float x, float y, float width, float height, float globalAlpha) {
            float hp = this.hoverAnim.get();
            float radius = 6.0f;
            int accent = ColorUtil.getColor(125, 145, 244);
            this.rememberBounds(x, y, width, height);
            int fill = this.danger ? ColorUtil.overCol(ColorUtil.getColor(12, globalAlpha * (0.3f - 0.2f * hp)), ColorUtil.getColor(45, 0, 0, globalAlpha * 0.3f), hp) : ColorUtil.getColor(12, globalAlpha * (0.3f - 0.2f * hp));
            RenderUtil.Blur.blur(x, y, width, height, 1.0f, radius, fill);
            Font font = Fonts.sf_regular;
            int titleTarget = this.danger ? ColorUtil.getColor(255, 125, 125) : accent;
            int titleColor = ColorUtil.replAlpha(ColorUtil.interpolateColor(ColorUtil.getColor(160, 160, 160, 1.0f), titleTarget, hp), globalAlpha);
            String text = this.text();
            float titleX = x + width / 2.0f - font.getWidth(text, 8.0f) / 2.0f;
            font.draw(text, titleX, y + 12.0f, 8.0f, titleColor);
        }

        public boolean isHovered() {
            return this.hovered;
        }

        public void onClick() {
            if (this.action != null) {
                this.action.run();
            }
        }
    }
}


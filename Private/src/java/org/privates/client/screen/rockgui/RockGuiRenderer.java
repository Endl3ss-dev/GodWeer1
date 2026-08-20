package org.privates.client.screen.rockgui;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.Excellent;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.Setting;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RockGuiRenderer implements IMinecraft {

    public static Animation opening = new Animation().setSpeed(300).setEasing(Easings.SINE_IN_OUT);
    public static Animation changeCategoryAnim = new Animation().setSpeed(150).setEasing(Easings.SINE_IN_OUT);
    public static Animation openedAnim = new Animation().setSpeed(400).setEasing(Easings.SINE_IN_OUT);
    public static Animation searchAnim = new Animation().setSpeed(300).setEasing(Easings.SINE_IN_OUT);
    public static Animation searchFocusedAnim = new Animation().setSpeed(300).setEasing(Easings.SINE_IN_OUT);
    public static Animation separatorAnim = new Animation().setSpeed(300).setEasing(Easings.SINE_IN_OUT);
    public static Animation moveAnim = new Animation().setSpeed(300).setEasing(Easings.SINE_IN_OUT);
    public static Animation upSideAnim = new Animation().setSpeed(300).setEasing(Easings.SINE_IN_OUT);
    public static Animation loadedAnim = new Animation().setSpeed(600).setEasing(Easings.SINE_IN_OUT);
    public static Animation swapAnim = new Animation().setSpeed(300).setEasing(Easings.SINE_IN_OUT);
    public static Animation shadowAnim = new Animation().setSpeed(300).setEasing(Easings.SINE_IN_OUT);
    public static Animation hoverCloseAnim = new Animation().setSpeed(300).setEasing(Easings.SINE_IN_OUT);
    public static Animation hoverBindAnim = new Animation().setSpeed(300).setEasing(Easings.SINE_IN_OUT);
    public static Animation hoverSearchAnim = new Animation().setSpeed(300).setEasing(Easings.SINE_IN_OUT);

    @Getter
    public static final ArrayList<SettingRect> settings = new ArrayList<>();
    @Getter
    public static final GlyphType[] glyphes = new GlyphType[Category.values().length];

    @Getter
    @Setter
    private static boolean loaded, apply;

    RockGuiWindow window;
    private SettingRect hovered;
    private float yaw, pitch;
    private boolean stencil = true;

    private final Comparator<Object> SORT_METHOD = Comparator.comparing(m -> {
        Module module = (Module) m;
        return module.getModuleInfo().name();
    }).reversed();

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        opening.setSpeed(300);
        opening.update();

        renderWindow(matrixStack, mouseX, mouseY, partialTicks);

        if (hovered != null && hovered.getParent().getDescription() != null && !hovered.getParent().getDescription().isEmpty()) {
            renderTooltip(matrixStack, mouseX, mouseY);
        }
    }

    private void renderWindow(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Theme theme = Theme.getInstance();
        int bgColor = ColorUtil.multAlpha(theme.backgroundColor(), opening.get());
        int separatorColor = ColorUtil.multAlpha(theme.secondaryColor(), opening.get() * separatorAnim.get());
        int textColor = ColorUtil.multAlpha(theme.textColor(), opening.get());

        RenderUtil.CustomStyled.drawGui(matrixStack, window.getX(), window.getY(), window.getWidth(), window.getHeight(), 8, true, 20f);

        swapAnim.setForward(loaded);
        swapAnim.update();
        loadedAnim.setForward(swapAnim.finished());
        loadedAnim.update();
        upSideAnim.setForward(loadedAnim.finished());
        upSideAnim.update();
        separatorAnim.setForward(upSideAnim.finished());
        separatorAnim.update();
        moveAnim.setForward(separatorAnim.finished());
        moveAnim.update();

        if (!apply) {
            loadSettings();
            apply = true;
        }

        if (!loaded || !swapAnim.finished()) {
            renderLoading(matrixStack);
            return;
        }

        renderPanel(matrixStack, mouseX, mouseY, partialTicks);
        renderModules(matrixStack, mouseX, mouseY, partialTicks);
        renderOpened(matrixStack, mouseX, mouseY, partialTicks);

        RenderUtil.Rounded.roundedRect(matrixStack,
                window.getX() + 155,
                window.getY(),
                1,
                window.getHeight(),
                separatorColor,
                Round.of(0)
        );

        renderTab(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void loadSettings() {
        new Thread(() -> {
            settings.clear();

            swapAnim.setForward(false);
            loadedAnim.setForward(false);
            upSideAnim.setForward(false);
            separatorAnim.setForward(false);
            moveAnim.setForward(false);

            for (Category cat : Category.values()) {
                int i = cat.ordinal();
                glyphes[i] = new GlyphType();

                List<Module> modules = new ArrayList<>();
                Excellent.inst().moduleManager().values().forEach(modules::add);
                modules.sort(SORT_METHOD);

                for (Module module : modules) {
                    if (module.getCategory() == cat) {
                        if (!glyphes[i].containsKey(module.getModuleInfo().name().charAt(0))) {
                            glyphes[i].put(module.getModuleInfo().name().charAt(0), new ArrayList<>());
                        }

                        glyphes[i].get(module.getModuleInfo().name().charAt(0)).add(module);

                        for (Setting setting : module.getSettings()) {
                            settings.add(new SettingRect(setting));
                        }
                    }
                }
            }

            try {
                Thread.sleep(1300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            loaded = true;
        }).start();
    }

    private void renderLoading(MatrixStack matrixStack) {
        Theme theme = Theme.getInstance();
        int textColor = ColorUtil.multAlpha(theme.textColor(), opening.get());

        String wait = "Loading...";
        float x = window.getX() + window.getWidth() / 2 - Fonts.SF_BOLD.getWidth(wait, 24) / 2;
        float y = window.getY() + window.getHeight() / 2;

        Fonts.SF_BOLD.draw(matrixStack, wait, x, y, textColor, 24);
    }

    private void renderPanel(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Theme theme = Theme.getInstance();
        int textColor = ColorUtil.multAlpha(theme.textColor(), opening.get());
        int separatorColor = ColorUtil.multAlpha(theme.secondaryColor(), opening.get() * separatorAnim.get());

        float anim1 = 30 * moveAnim.get() - 30;

        float yOff = 0;
        for (Category type : Category.values()) {
            float anim = 30 * moveAnim.get() - 30;

            boolean hover = Mathf.isHover(mouseX, mouseY,
                    window.getX() + 8 + anim,
                    window.getY() + 49 + yOff,
                    21, 21);

            if (hover) {
                window.setCanDrag(false);
            }

            boolean isOpen = type == window.getCurrent();
            float hoverAnim = hover ? 1.0f : 0.0f;
            float openAnim = isOpen ? 1.0f : 0.0f;

            int bgAlpha = isOpen ?
                    (int) (0.1f + openAnim * 0.05f * 255) :
                    (int) (hoverAnim * 0.1f * 255);

            RenderUtil.Rounded.smooth(matrixStack,
                    window.getX() + 8 + anim,
                    window.getY() + 49 + yOff,
                    21, 21,
                    ColorUtil.multAlpha(textColor, bgAlpha / 255f),
                    Round.of(4)
            );

            Fonts.CLICKGUI.draw(matrixStack, type.getIcon(),
                    window.getX() + 12 + anim,
                    window.getY() + 53 + yOff,
                    textColor, 13);

            yOff += 24;
        }

        RenderUtil.Rounded.roundedRect(matrixStack,
                window.getX() + 37,
                window.getY(),
                1,
                window.getHeight(),
                separatorColor,
                Round.of(0)
        );
    }

    private void renderModules(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Category current = window.getCurrent();
        Theme theme = Theme.getInstance();
        int textColor = ColorUtil.multAlpha(theme.textColor(), opening.get());

        Fonts.SF_BOLD.draw(matrixStack, current.getName(),
                window.getX() + 48,
                window.getY() + 9,
                textColor, 24);

        float yOff = window.getScrollValue().get();

        GlyphType glyph = window.get(current);
        if (glyph != null) {
            for (Map.Entry<Character, List<Module>> entry : glyph.entrySet()) {
                Fonts.SF_MEDIUM.draw(matrixStack, entry.getKey().toString(),
                        window.getX() + 49,
                        window.getY() + 31 + yOff,
                        ColorUtil.multAlpha(textColor, 0.5f), 12);

                yOff += 12;

                for (Module module : entry.getValue()) {
                    boolean hover = Mathf.isHover(mouseX, mouseY,
                            window.getX() + 37,
                            window.getY() + 30 + yOff,
                            118, 13);

                    boolean opened = window.getOpened() == module;
                    float hoverVal = hover ? 1.0f : 0.0f;
                    float toggleVal = module.isEnabled() ? 1.0f : 0.0f;

                    int moduleColor = ColorUtil.interpolateColor(
                            ColorUtil.multAlpha(textColor, 0.5f),
                            theme.clientColor(),
                            toggleVal
                    );

                    Fonts.SF_MEDIUM.draw(matrixStack, module.getModuleInfo().name(),
                            window.getX() + 48 + hoverVal * 2 + (opened ? 4 : 0),
                            window.getY() + 30 + yOff,
                            moduleColor, 16);

                    yOff += 13;
                }
            }
        }
    }

    private void renderOpened(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (window.getQueue() != null) {
            openedAnim.setForward(false);
            if (openedAnim.finished(false)) {
                window.setOpened(window.getQueue());
                window.setQueue(null);
            }
        } else {
            openedAnim.setForward(window.getOpened() != null);
        }

        if (window.getOpened() != null) {
            window.setPrevOpened(window.getOpened());
        }

        if (!openedAnim.finished(false) && window.getPrevOpened() != null) {
            renderSettings(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    private void renderSettings(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Theme theme = Theme.getInstance();
        Module module = window.getPrevOpened();

        float anim = window.getWidth() - window.getWidth() * openedAnim.get();
        int textColor = ColorUtil.multAlpha(theme.textColor(), opening.get());

        String title = "Settings " + module.getModuleInfo().name();
        Fonts.SF_BOLD.draw(matrixStack, title,
                window.getX() + 166 + anim,
                window.getY() + 39,
                textColor, 24);

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(
                window.getX() + 156,
                window.getY() + 70,
                window.getWidth() - 156,
                window.getHeight() - 70
        );

        int leftY = 0, rightY = 0, column = 0;

        for (SettingRect setting : window.get(module)) {
            setting.getHide().setForward(!(boolean) setting.getParent().getVisible().get());

            if (!setting.getHide().finished()) {
                setting.set(
                        window.getX() + 166 + 160 * column + anim,
                        window.getY() + 74 + (column == 0 ? leftY : rightY) + window.getSettingsScrollValue().get(),
                        152,
                        setting.getHeight()
                );
                setting.render(matrixStack, mouseX, mouseY, partialTicks, opening.get() * (1 - setting.getHide().get()));
            }

            if (column == 0) {
                leftY += (int) ((setting.getHeight() + 9) * (1 - setting.getHide().get()));
            } else {
                rightY += (int) ((setting.getHeight() + 9) * (1 - setting.getHide().get()));
            }

            column++;
            if (column > 1) column = 0;
        }

        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void renderTab(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Theme theme = Theme.getInstance();
        int separatorColor = ColorUtil.multAlpha(theme.secondaryColor(), opening.get());

        RenderUtil.Rounded.roundedRect(matrixStack,
                window.getX() + 156,
                window.getY() + 29,
                window.getWidth() - 156,
                1,
                separatorColor,
                Round.of(0)
        );
    }

    private void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        String desc = hovered.getParent().getDescription();
        if (desc == null || desc.isEmpty()) return;

        Theme theme = Theme.getInstance();
        float alpha = hovered.getSlowHover().get();

        float width = Fonts.SF_MEDIUM.getWidth(desc, 14) + 10;
        float height = 20;

        RenderUtil.Rounded.smooth(matrixStack,
                hovered.getX() + hovered.getWidth() + 10,
                hovered.getY() + hovered.getHeight() / 2 - height / 2,
                width, height,
                ColorUtil.multAlpha(theme.backgroundColor(), alpha),
                Round.of(5)
        );

        Fonts.SF_MEDIUM.draw(matrixStack, desc,
                hovered.getX() + hovered.getWidth() + 15,
                hovered.getY() + hovered.getHeight() / 2 - 5,
                ColorUtil.multAlpha(theme.textColor(), alpha), 14);
    }
}
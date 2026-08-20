package org.privates.client.screen.newgui;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.Excellent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.impl.client.FlatGui;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.lang.annotation.Annotation;
import java.util.ArrayList;

import static org.privates.client.api.interfaces.IMinecraft.mc;

@Getter
@Setter
public class Panel extends Builder {

    private final ClickGuiScreen gui;
    private final Category category;
    private final int index;
    private final ArrayList<Module> modules = new ArrayList<>();

    private float delta, deltaV;
    private final boolean isThemePanel;

    private float customWidth = 110f;
    private float customHeight = 300f;
    private float animatedY;
    private float animX, animY;
    private float animProgress = 0f;
    private float shakeOffset = 0f;
    private long animStartTime = 0L;
    private boolean opening = true;
    private float overallScale = 1f;
    private float targetX, targetY;
    private float panelScale = 0f;

    private SubCategory currentSubCategory;
    private float subCategoryAnim = 0f;
    private float indicatorAnim = 0f;

    private final float[] moduleAnimations = new float[128];
    private float categoryTransitionProgress = 0f;
    private boolean isTransitioning = false;
    private float transitionDirection = 1f;

    public Panel(ClickGuiScreen gui, Category category, float x, float y, int index, boolean isThemePanel) {
        this.gui = gui;
        this.category = category;
        this.index = index;
        this.isThemePanel = isThemePanel;
        this.targetX = x;
        this.targetY = y;
        this.x = x;
        this.y = y;
        float sScale = gui.getResolutionScale();
        this.width = customWidth * sScale;
        this.height = customHeight * sScale;
        this.animatedY = y;
        this.animX = x;
        this.animY = y;
        if (isThemePanel) {
            loadThemeModule();
        } else {
            this.currentSubCategory = SubCategory.getDefault(category);
            loadModules("");
        }
    }

    public void loadThemeModule() {
        float sScale = gui.getResolutionScale();
        if (!modules.isEmpty())
            modules.clear();
        float offsetY = y + 25 * sScale;
        delta = 0;
        deltaV = 0;

        org.privates.client.managers.module.Module themeModule = Excellent.inst().moduleManager().get(Theme.class);
        if (themeModule != null) {
            Module module = new Module(this, themeModule, x * sScale, offsetY);
            module.setWidth(width - 6 * sScale);
            modules.add(module);
        }
    }

    public void setupInitialAnimation() {
        this.opening = true;
        float screenWidth = mc.getMainWindow().getScaledWidth();
        float screenHeight = mc.getMainWindow().getScaledHeight();
        float sScale = gui.getResolutionScale();

        if (isThemePanel) {
            animX = targetX;
            animY = targetY;
            animStartTime = System.currentTimeMillis();
        } else {
            int totalPanels = Category.values().length;
            float centerIndex = (totalPanels - 1) / 2f;

            float delayFactor = Math.abs(index - centerIndex);
            if (index == (int) centerIndex) {
                animStartTime = System.currentTimeMillis();
            } else if (index == (int) centerIndex - 1 || index == (int) centerIndex + 1) {
                animStartTime = System.currentTimeMillis() + 100L;
            } else {
                animStartTime = System.currentTimeMillis() + 200L;
            }

            animX = targetX;
            animY = targetY;
        }
        this.animatedY = animY;
    }

    public void startClosingAnimation() {
        opening = false;
        animStartTime = System.currentTimeMillis();

        if (isThemePanel) {
            float sScale = gui.getResolutionScale();
            animX = targetX;
            animY = targetY;
        } else {
            int totalPanels = Category.values().length;
            float centerIndex = (totalPanels - 1) / 2f;

            float delayFactor = Math.abs(index - centerIndex);
            if (index == (int) centerIndex) {
                animStartTime = System.currentTimeMillis();
            } else if (index == (int) centerIndex - 1 || index == (int) centerIndex + 1) {
                animStartTime = System.currentTimeMillis() + 100L;
            } else {
                animStartTime = System.currentTimeMillis() + 200L;
            }

            animX = targetX;
            animY = targetY;
        }
    }

    private float getEase(float t) {
        return (float) (1 - Math.pow(1 - t, 3));
    }

    private float calculateShake(float progress) {
        if (progress > 0.9f) {
            float shakePhase = (progress - 0.9f) * 10f;
            return (float) (Math.sin(shakePhase * Math.PI * 4) * 0.5 * (1 - progress)) * 2f;
        }
        return 0f;
    }

    public void updateAnimation(long currentTime, float scale) {
        overallScale = scale;
        long elapsed = currentTime - animStartTime;
        float duration = opening ? 250f : 120f;

        if (opening && elapsed < 0) {
            animProgress = 0;
        } else {
            animProgress = Math.min(1f, Math.max(0f, elapsed / duration));
        }

        if (!opening) animProgress = 1f - animProgress;

        float eased = getEase(animProgress);

        panelScale = eased;

        this.x = targetX;
        this.y = targetY;

        float targetPanelY;
        if (gui.getScale() == 1) {
            targetPanelY = this.targetY;
        } else {
            targetPanelY = mc.getMainWindow().getScaledHeight();
        }
        this.animatedY = this.y;

        updateModulePositions();

        if (isTransitioning) {
            categoryTransitionProgress = Mathf.fast(categoryTransitionProgress, 1f, 10f);
            if (categoryTransitionProgress > 0.99f) {
                isTransitioning = false;
                categoryTransitionProgress = 0f;
            }
        }
    }

    private float descriptionWidthAnim = 0f;

    public void setCustomSize(float width, float height) {
        float sScale = gui.getResolutionScale();
        this.customWidth = width / sScale;
        this.customHeight = height / sScale;
        this.width = width;
        this.height = height;
        this.targetX = x;
        this.targetY = y;

        updateModulePositions();
    }

    private void updateModulePositions() {
        float sScale = gui.getResolutionScale();
        boolean merged = FlatGui.getInstance().panelHeaderStyle().is("Слитно");
        float headerOffset = merged ? 0 : 25 * sScale;
        float subCategoryOffset = isThemePanel ? 0 : 16 * sScale;
        float offsetY = animatedY + headerOffset + subCategoryOffset;

        for (Module module : modules) {
            module.setX(x + 3 * sScale);
            module.setY(offsetY);
            module.setWidth(width - 6 * sScale);
            offsetY += module.getHeight() + 3 * sScale;
        }
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        deltaV = Mathf.fast(deltaV, delta, 16);

        float centerX = x + width / 2f;
        float centerY = animatedY + height / 2f;

        stack.push();
        stack.translate(centerX, centerY, 0);
        stack.scale(panelScale, panelScale, 1);
        stack.translate(-centerX, -centerY, 0);

        Theme theme = Theme.getInstance();
        float sScale = gui.getResolutionScale();
        float adaptiveTitleSize = gui.getAdaptiveTitleSize();
        float radius = 6f * sScale;

        boolean merged = FlatGui.getInstance().panelHeaderStyle().is("Слитно");
        boolean isOldMode = FlatGui.getInstance().panelRenderMode().is("Старый");

        // Calculate dynamic height for old mode
        float renderHeight = height;
        if (isOldMode && !isThemePanel) {
            float totalContentHeight = 0;
            for (Module m : modules) {
                totalContentHeight += m.getHeight() + 3 * sScale;
            }
            if (totalContentHeight > 0) totalContentHeight -= 3 * sScale;

            float headerHeight = merged ? 41f * sScale : 41f * sScale + 4.5f * sScale;
            renderHeight = headerHeight + totalContentHeight + 8 * sScale;
        }

        if (merged) {
            RenderUtil.CustomStyled.drawGui(stack, x, animatedY, width, renderHeight, radius, true, theme != null ? theme.blurStrength().getValue() : 10);

            float headerHeight = isThemePanel ? 25f * sScale : 41f * sScale;
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x, animatedY, width, headerHeight + 3);

            renderHeader(stack, x, animatedY, width, 25f * sScale, sScale, adaptiveTitleSize, theme);

            if (!isThemePanel) {
                renderSubCategories(stack, x, animatedY + 23 * sScale, width, 18 * sScale, sScale, theme, mouseX, mouseY);
            }

            ScissorExp.unset();
            ScissorExp.pop();
        } else {
            float headerHeight = isThemePanel ? 25f * sScale : 41f * sScale;
            float spacing = 4.5f * sScale;

            RenderUtil.CustomStyled.drawGui(stack, x, animatedY, width, headerHeight, radius, true, theme != null ? theme.blurStrength().getValue() : 10);
            renderHeader(stack, x, animatedY, width, 25f * sScale, sScale, adaptiveTitleSize, theme);

            if (!isThemePanel) {
                renderSubCategories(stack, x, animatedY + 23 * sScale, width, 18 * sScale, sScale, theme, mouseX, mouseY);
            }

            float modulePanelY = animatedY + headerHeight + spacing;
            float modulePanelHeight = isOldMode ? renderHeight - headerHeight - spacing : height - headerHeight - spacing;

            RenderUtil.CustomStyled.drawGui(stack, x, modulePanelY, width, modulePanelHeight, radius, true, theme != null ? theme.blurStrength().getValue() : 10);
        }

        float modulePanelY = merged ? (isThemePanel ? animatedY + 25 * sScale : animatedY + 41 * sScale) : (isThemePanel ? animatedY + 25 * sScale + 4.5f * sScale : animatedY + 41 * sScale + 4.5f * sScale);
        float modulePanelHeight = merged ? (isThemePanel ? renderHeight - 25 * sScale : renderHeight - 41 * sScale) : (isThemePanel ? renderHeight - 25 * sScale - 4.5f * sScale : renderHeight - 41 * sScale - 4.5f * sScale);

        // In old mode, don't use scissor for modules - show all
        if (isOldMode && !isThemePanel) {
            float offsetY = merged ? 4.5f * sScale : 0f;
            int moduleIndex = 0;
            for (Module module : modules) {
                if (moduleIndex < moduleAnimations.length) {
                    float targetAnim = 1f;

                    if (isTransitioning) {
                        float progress = categoryTransitionProgress;
                        float slideOffset = transitionDirection * (1f - progress) * 30 * sScale;
                        module.setX(x + 3 * sScale + slideOffset);
                        targetAnim = progress;
                    } else {
                        module.setX(x + 3 * sScale);
                    }

                    moduleAnimations[moduleIndex] = Mathf.fast(moduleAnimations[moduleIndex], targetAnim, 12f);
                    module.setAlpha(moduleAnimations[moduleIndex]);
                }

                module.setY(modulePanelY + offsetY);
                module.setDelta(0); // No scroll in old mode
                module.setWidth(width - 6 * sScale);
                module.render(stack, mouseX, mouseY);

                offsetY += module.getHeight() + 3 * sScale;
                moduleIndex++;
            }
        } else {
            // New mode with scissor and scroll
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x, modulePanelY, width, modulePanelHeight - 5);

            float offsetY = merged ? 4.5f * sScale : 0f;
            int moduleIndex = 0;
            for (Module module : modules) {
                if (moduleIndex < moduleAnimations.length) {
                    float targetAnim = 1f;

                    if (isTransitioning) {
                        float progress = categoryTransitionProgress;
                        float slideOffset = transitionDirection * (1f - progress) * 30 * sScale;
                        module.setX(x + 3 * sScale + slideOffset);
                        targetAnim = progress;
                    } else {
                        module.setX(x + 3 * sScale);
                    }

                    moduleAnimations[moduleIndex] = Mathf.fast(moduleAnimations[moduleIndex], targetAnim, 12f);
                    module.setAlpha(moduleAnimations[moduleIndex]);
                }

                module.setY(modulePanelY + offsetY);
                module.setDelta(deltaV);
                module.setWidth(width - 6 * sScale);

                float moduleTop = modulePanelY + offsetY + deltaV;
                float moduleBottom = moduleTop + module.getHeight();

                if (isThemePanel || (moduleBottom > modulePanelY && moduleTop < modulePanelY + modulePanelHeight)) {
                    module.render(stack, mouseX, mouseY);
                }
                offsetY += module.getHeight() + 3 * sScale;
                moduleIndex++;
            }

            ScissorExp.unset();
            ScissorExp.pop();
        }

        stack.pop();
    }

    private void renderHeader(MatrixStack stack, float x, float y, float width, float height, float sScale, float adaptiveTitleSize, Theme theme) {
        boolean isCentered = FlatGui.getInstance().textAlign().is("Центрично");
        String panelName = isThemePanel ? "Client" : category.getName();

        float textWidth = Fonts.SF_MEDIUM.getWidth(panelName, adaptiveTitleSize);
        float textX, iconX;
        float textY = y + 12.5f * sScale - Fonts.SF_MEDIUM.getHeight(panelName, adaptiveTitleSize) / 1.5f;

        if (isThemePanel) {
            float iconSize = adaptiveTitleSize * 1.15f + 2;
            float actualIconWidth = Fonts.iconf.getWidth("n", iconSize);
            float iconHeight = Fonts.iconf.getHeight("n", iconSize);
            float iconY = y + 12.5f * sScale - iconHeight / 2f;

            if (isCentered) {
                float totalWidth = textWidth + actualIconWidth + 8 * sScale;
                float startX = x + width / 2f - totalWidth / 2f;
                textX = startX;
                iconX = startX + textWidth + 8 * sScale;
            } else {
                textX = x + 10 * sScale;
                iconX = x + width - actualIconWidth - 8 * sScale;
            }

            Fonts.SF_MEDIUM.draw(stack, panelName, textX, textY, theme.textColor(), adaptiveTitleSize);
            Fonts.iconf.draw(stack, "n", iconX - 3, iconY - 1.5f, theme.textColor(), iconSize);
        } else {
            String icon = category.getIcon();
            if (icon == null || icon.isEmpty()) icon = "A";

            float iconSize = adaptiveTitleSize * 1.15f;
            float iconWidth;
            float iconHeight;
            float iconY;

            if (category == Category.COMBAT) {
                iconWidth = Fonts.iconf.getWidth("c", iconSize);
                iconHeight = Fonts.iconf.getHeight("c", iconSize);
                iconY = y + 12.5f * sScale - iconHeight / 2f;
            } else if (category == Category.MOVEMENT) {
                iconWidth = Fonts.iconf.getWidth("e", iconSize);
                iconHeight = Fonts.iconf.getHeight("e", iconSize);
                iconY = y + 12.5f * sScale - iconHeight / 2f;
            } else {
                iconWidth = Fonts.CLICKGUI.getWidth(icon, iconSize);
                iconHeight = Fonts.CLICKGUI.getHeight(icon, iconSize);
                iconY = y + 12.5f * sScale - iconHeight / 2f;
            }

            if (isCentered) {
                float totalWidth = textWidth + iconWidth + 8 * sScale;
                float startX = x + width / 2f - totalWidth / 2f;
                textX = startX;
                iconX = startX + textWidth + 8 * sScale;
            } else {
                textX = x + 10 * sScale;
                iconX = x + width - iconWidth - 10 * sScale;
            }

            Fonts.SF_MEDIUM.draw(stack, panelName, textX, textY, theme.textColor(), adaptiveTitleSize);

            if (category == Category.COMBAT) {
                Fonts.iconf.draw(stack, "c", iconX, iconY, theme.textColor(), iconSize);
            } else if (category == Category.MOVEMENT) {
                Fonts.iconf.draw(stack, "e", iconX, iconY, theme.textColor(), iconSize);
            } else {
                Fonts.CLICKGUI.draw(stack, icon, iconX, iconY, theme.textColor(), iconSize);
            }
        }
    }

    private void renderSubCategories(MatrixStack stack, float x, float y, float width, float height, float sScale, Theme theme, float mouseX, float mouseY) {
        SubCategory[] subCategories = SubCategory.getSubCategories(category);
        if (subCategories.length == 0) return;

        float buttonWidth = (width - 8 * sScale) / 3f;
        float buttonHeight = 12 * sScale;
        float buttonY = y + 3 * sScale;
        float buttonSpacing = 2 * sScale;

        for (int i = 0; i < subCategories.length; i++) {
            SubCategory subCat = subCategories[i];
            float buttonX = x + 4 * sScale + (buttonWidth + buttonSpacing) * i;

            boolean isHovered = isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);
            boolean isActive = currentSubCategory == subCat;

            float buttonAlpha = isActive ? 1.0f : (isHovered ? 0.8f : 0.6f);
            int textColor = ColorUtil.multAlpha(theme.textColor(), buttonAlpha);

            if (isActive) {
                int bgColor = ColorUtil.multAlpha(theme.clientColor(), 0.2f);

                float indicatorWidth = buttonWidth / 4f;
                float indicatorX = buttonX + buttonWidth / 2f - indicatorWidth / 2f;
                float indicatorY = buttonY + buttonHeight - 2.5f * sScale;
                int indicatorColor = ColorUtil.multAlpha(theme.clientColor(), 0.9f);
                RenderUtil.Rounded.roundedRect(stack, indicatorX, indicatorY + 2, indicatorWidth, 1.5f * sScale, indicatorColor, Round.of(2.55f * sScale));
            } else if (isHovered) {
                int bgColor = ColorUtil.multAlpha(theme.clientColor(), 0.1f);
                RenderUtil.Rounded.roundedRect(stack, buttonX, buttonY, buttonWidth, buttonHeight, bgColor, Round.of(6 * sScale));
            }

            float textSize = 6.5f * sScale;
            float textWidth = Fonts.SF_BOLD.getWidth(subCat.getName(), textSize);
            float textX = buttonX + buttonWidth / 2f - textWidth / 2f;
            float textY = buttonY + buttonHeight / 2f - Fonts.SF_BOLD.getHeight(subCat.getName(), textSize) / 2f;

            Fonts.SF_BOLD.draw(stack, subCat.getName(), textX, textY, textColor, textSize);
        }
    }

    public DescriptionInfo getDescriptionInfo(float mouseX, float mouseY) {
        long currentTime = System.currentTimeMillis();
        String currentDescription = "";
        float maxAlpha = 0f;

        for (Module module : modules) {
            float sScale2 = module.getPanel().getGui().getResolutionScale();
            final float baseHeight = 14f * sScale2;
            final float yExpansion = baseHeight * 0.05f;
            final float renderY = module.getY() + module.getDelta() - yExpansion;
            final float renderHeight = baseHeight + yExpansion * 2;

            if (isHovered(mouseX, mouseY, module.getX(), renderY, module.getWidth(), renderHeight)) {
                if (module.getHoverStartTime() == 0L) {
                    module.setHoverStartTime(currentTime);
                }

                long hoverDuration = currentTime - module.getHoverStartTime();
                if (hoverDuration >= 250L) {
                    String description = module.getModule().getModuleInfo().description();
                    if (description == null || description.isEmpty()) {
                        description = "Ручки кодера ещё не дошли до того чтобы написать описание это-й функции";
                    }

                    module.setDescriptionAlphaAnim(Mathf.fast(module.getDescriptionAlphaAnim(), 1f, 6));

                    if (module.getDescriptionAlphaAnim() > maxAlpha) {
                        maxAlpha = module.getDescriptionAlphaAnim();
                        currentDescription = description;
                    }
                }
            } else {
                module.setHoverStartTime(0L);
                module.setDescriptionAlphaAnim(Mathf.fast(module.getDescriptionAlphaAnim(), 0f, 6));
            }
        }

        return new DescriptionInfo(currentDescription, maxAlpha);
    }

    public record DescriptionInfo(String description, float alpha) {
    }

    @Override
    protected void mouse(float mouseX, float mouseY, int button, int action) {
        float sScale = gui.getResolutionScale();
        boolean isOldMode = FlatGui.getInstance().panelRenderMode().is("Старый");

        // Calculate effective height for click detection (same as render)
        float effectiveHeight = height;
        if (isOldMode && !isThemePanel) {
            float totalContentHeight = 0;
            for (Module m : modules) {
                totalContentHeight += m.getHeight() + 3 * sScale;
            }
            if (totalContentHeight > 0) totalContentHeight -= 3 * sScale;

            boolean merged = FlatGui.getInstance().panelHeaderStyle().is("Слитно");
            float headerHeight = merged ? 41f * sScale : 41f * sScale + 4.5f * sScale;
            effectiveHeight = headerHeight + totalContentHeight + 8 * sScale;
        }

        if (!isThemePanel && button == 0 && action == 0) {
            SubCategory[] subCategories = SubCategory.getSubCategories(category);
            float buttonWidth = (width - 8 * sScale) / 3f;
            float buttonHeight = 12 * sScale;
            float buttonY = animatedY + 23 * sScale + 3 * sScale;
            float buttonSpacing = 2 * sScale;

            for (int i = 0; i < subCategories.length; i++) {
                SubCategory subCat = subCategories[i];
                float buttonX = x + 4 * sScale + (buttonWidth + buttonSpacing) * i;

                if (isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight)) {
                    if (currentSubCategory != subCat) {
                        SubCategory oldCategory = currentSubCategory;
                        currentSubCategory = subCat;
                        isTransitioning = true;
                        categoryTransitionProgress = 0f;

                        int oldIndex = java.util.Arrays.asList(subCategories).indexOf(oldCategory);
                        int newIndex = java.util.Arrays.asList(subCategories).indexOf(subCat);
                        transitionDirection = newIndex > oldIndex ? 1f : -1f;

                        for (int j = 0; j < moduleAnimations.length; j++) {
                            moduleAnimations[j] = 0f;
                        }
                        loadModules("");
                        delta = 0;
                        deltaV = 0;
                        org.privates.client.utils.other.SoundUtil.playSound("mode_pick.wav", 0.1f);
                    }
                    return;
                }
            }
        }

        if (isHovered(mouseX, mouseY, x, animatedY, width, effectiveHeight)) {
            for (Module module : modules)
                module.mouse(mouseX, mouseY, button, action);
        }
    }

    @Override
    protected void key(int key) {
        for (Module module : modules)
            module.key(key);
    }

    @Override
    public void charTyped(char typed) {
        for (Module module : modules)
            module.charTyped(typed);
    }

    public void scroll(float mouseX, float mouseY, float delta) {
        boolean isOldMode = FlatGui.getInstance().panelRenderMode().is("Старый");

        float sScale = gui.getResolutionScale();
        if (modules.isEmpty()) return;

        if (isOldMode && !isThemePanel) {
            return;
        }

        float totalContentHeight = 0;
        for (Module m : modules) {
            totalContentHeight += m.getHeight() + 3 * sScale;
        }
        totalContentHeight -= 3 * sScale;

        float scrollableAreaHeight = height - (isThemePanel ? 27 * sScale : 43 * sScale);

        boolean shouldScroll = isHovered(mouseX, mouseY, x, animatedY, width, height);

        if (shouldScroll &&
                !(delta > 0 && this.delta >= 0) &&
                !(delta < 0 && totalContentHeight + this.delta <= scrollableAreaHeight)) {
            this.delta += delta * 9;
        }
    }

    public void loadModules(String key) {
        float sScale = gui.getResolutionScale();
        if (!modules.isEmpty())
            modules.clear();

        boolean merged = FlatGui.getInstance().panelHeaderStyle().is("Слитно");
        float headerOffset = merged ? 0 : 25 * sScale;
        float subCategoryOffset = isThemePanel ? 0 : 16 * sScale;
        float offsetY = y + headerOffset + subCategoryOffset;

        delta = 0;
        deltaV = 0;

        for (org.privates.client.managers.module.Module m : Excellent.inst().moduleManager().get(category)) {
            boolean matchesSearch = key.isEmpty() || m.getName().toLowerCase().contains(key.toLowerCase());
            boolean matchesSubCategory = currentSubCategory == null || m.getClass().isAnnotationPresent((Class<? extends Annotation>) currentSubCategory.getAnnotationClass());

            if (matchesSearch && matchesSubCategory) {
                Module module = new Module(this, m, x + 3 * sScale, offsetY);
                module.setWidth(width - 6 * sScale);
                modules.add(module);
                offsetY += module.getHeight() + 3 * sScale;
            }
        }
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public float getAnimProgress() {
        return animProgress;
    }

    public float getPanelScale() {
        return panelScale;
    }
}
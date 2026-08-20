package org.privates.client.screen.account;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.privates.client.Excellent;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.api.interfaces.IMouse;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.*;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.scroll.ScrollUtil;
import org.privates.client.utils.render.text.TextAlign;
import org.privates.client.utils.render.text.TextBox;
import org.privates.common.user.LoginManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class AccountGuiScreen extends Screen implements IMinecraft, IMouse {
    public AccountGuiScreen() {
        super(StringTextComponent.EMPTY);
    }

    private final Animation hoverAddBtn = new Animation();
    private final Animation hoverGenerateBtn = new Animation();
    private final Animation hoverClearBtn = new Animation();
    private final Animation hoverMenuBtn = new Animation();
    private final Animation hoverTextBox = new Animation();
    private final Animation hoverSearchBox = new Animation();
    private final Animation emptyListAnimation = new Animation();
    private final List<Animation> deleteButtonAnimations = new ArrayList<>();
    private final List<Animation> favoriteButtonAnimations = new ArrayList<>();
    private final List<Animation> accountHoverAnimations = new ArrayList<>();
    private final Animation yOffset = new Animation();
    private final Animation tooltipAlpha = new Animation();

    private TextBox textBox;
    private TextBox searchBox;
    private boolean exit = false;
    private final Animation alpha = new Animation();
    private final Animation scale = new Animation();
    private final ScrollUtil scroll = new ScrollUtil();
    @Setter
    private Account selected = null;
    private boolean wasEmpty = true;
    private final NameGenerator nameGenerator = new NameGenerator();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");
    private String hoveredButtonTooltip = null;
    private String activeTooltip = null;

    private float getResolutionScale() {
        int width = mc.getMainWindow().getWidth();
        int height = mc.getMainWindow().getHeight();

        if (width >= 3840 && height >= 2160) {
            return 1.5f;
        } else if (width >= 2560 && height >= 1440) {
            return 1.0f;
        } else if (width >= 1920 || height >= 1080) {
            return 0.75f;
        } else {
            return 0.65f;
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        float scale = getResolutionScale();
        Excellent.inst().accountManager().forEach(account -> account.resize(minecraft, (int) (width * scale), (int) (height * scale)));
    }

    @Override
    protected void init() {
        super.init();
        alpha.set(0);
        scale.set(0);
        alpha.run(1, 0.2);
        scale.run(1, 0.2, Easings.EXPO_OUT);

        yOffset.set(-30);
        yOffset.run(0, 0.25, Easings.EXPO_OUT);

        boolean isEmpty = Excellent.inst().accountManager().isEmpty();
        emptyListAnimation.set(isEmpty ? 1.0 : 0.0);
        wasEmpty = isEmpty;

        exit = false;

        float scale = getResolutionScale();
        textBox = new TextBox(new Vector2f(), Fonts.SF_MEDIUM, 5.72f * scale, Theme.getInstance().textColor(), TextAlign.LEFT, "Введите имя", 0, false, false);
        textBox.setText(mc.session.getProfile().getName());
        textBox.setCursor(mc.session.getProfile().getName().length());

        searchBox = new TextBox(new Vector2f(), Fonts.SF_MEDIUM, 7.48f * scale, Theme.getInstance().textColor(), TextAlign.LEFT, "Поиск", 0, false, false);

        setSelected(Excellent.inst().accountManager().stream().filter(a -> a.name().equals(mc.session.getProfile().getName())).findFirst().orElse(Excellent.inst().accountManager().stream().findFirst().orElse(null)));

        Excellent.inst().accountManager().forEach(Account::init);
        Excellent.inst().accountManager().forEach(acc -> accountHoverAnimations.add(new Animation()));
    }

    private void checkEmptyListState() {
        emptyListAnimation.update();
        boolean isEmpty = Excellent.inst().accountManager().isEmpty();

        if (isEmpty != wasEmpty) {
            emptyListAnimation.run(isEmpty ? 1.0 : 0.0, 0.1, isEmpty ? Easings.QUAD_OUT : Easings.QUAD_IN);
            wasEmpty = isEmpty;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        float scale = getResolutionScale();
        hoverAddBtn.update();
        hoverGenerateBtn.update();
        hoverClearBtn.update();
        hoverMenuBtn.update();
        hoverTextBox.update();
        hoverSearchBox.update();
        checkEmptyListState();
        alpha.update();
        this.scale.update();
        yOffset.update();
        accountHoverAnimations.forEach(Animation::update);
        tooltipAlpha.update();
        this.closeCheck();

        hoveredButtonTooltip = null;

        RenderUtil.drawBackground(matrixStack);

        Theme theme = Theme.getInstance();

        float containerWidth = 264 * scale;
        float containerHeight = 422.4f * scale;
        float x = this.width / 2F - containerWidth / 2F;
        float y = this.height / 2F - containerHeight / 2F + yOffset.get() * scale;

        BlurRenderer2.registerRenderCall(() -> {
            RenderUtil.Rounded.roundedRect(
                    matrixStack,
                    x,
                    y,
                    containerWidth,
                    containerHeight,
                    -1,
                    Round.of(4.4f * scale)
            );
        });
        BlurRenderer2.draw(5);

        int containerBg = ColorUtil.multAlpha(ColorUtil.interpolate(theme.clientColor(), ColorUtil.rgba(5, 5, 7, 60), 0.85f), alpha.get());
        RenderUtil.Rounded.roundedRect(matrixStack, x, y, containerWidth, containerHeight, containerBg, Round.of(4.4f * scale));

        float headerHeight = 50.6f * scale;

        String altText = "ALT";
        String managerText = "MANAGER";
        float altWidth = Fonts.SF_MEDIUM.getWidth(altText, 8.8f * scale);
        float managerWidth = Fonts.SF_MEDIUM.getWidth(managerText, 8.8f * scale);
        float totalWidth = altWidth + Fonts.SF_MEDIUM.getWidth(" ", 8.8f * scale) + managerWidth;
        float titleX = x + containerWidth / 2f - totalWidth / 2f;

        Fonts.SF_MEDIUM.draw(matrixStack, altText, titleX, y + 14.08f * scale, ColorUtil.multAlpha(ColorUtil.getColor(240, 60, 60), alpha.get()), 8.8f * scale);
        Fonts.SF_MEDIUM.draw(matrixStack, managerText, titleX + altWidth + Fonts.SF_MEDIUM.getWidth(" ", 8.8f * scale), y + 14.08f * scale, ColorUtil.multAlpha(theme.textColor(), alpha.get()), 8.8f * scale);

        String currentAccountText = "Текущий: " + mc.session.getProfile().getName();
        float currentTextWidth = Fonts.SF_MEDIUM.getWidth(currentAccountText, 4.84f * scale);
        if (currentTextWidth > containerWidth - 35.2f * scale) {
            String shortName = mc.session.getProfile().getName();
            if (shortName.length() > 12) {
                shortName = shortName.substring(0, 12) + "...";
            }
            currentAccountText = "Текущий: " + shortName;
        }
        Fonts.SF_MEDIUM.drawCenter(matrixStack, currentAccountText, x + containerWidth / 2f, y + 30.8f * scale, ColorUtil.multAlpha(theme.textColor(), 0.5f * alpha.get()), 4.84f * scale);

        float searchBoxMinWidth = 80 * scale;
        String searchText = searchBox.getText().isEmpty() ? "Поиск" : searchBox.getText();
        float searchTextWidth = Fonts.SF_MEDIUM.getWidth(searchText, 7.48f * scale);
        float searchBoxWidth = Math.max(searchBoxMinWidth, searchTextWidth + 40 * scale);
        float searchBoxHeight = 22.88f * scale;
        float searchBoxX = x + containerWidth / 2f - searchBoxWidth / 2f;
        float searchBoxY = y + headerHeight;

        searchBox.position.set(searchBoxX + 22.88f * scale, searchBoxY + searchBoxHeight / 2f - 3.08f * scale);
        searchBox.setWidth(searchBoxWidth - 28.16f * scale);
        searchBox.setColor(ColorUtil.multAlpha(theme.textColor(), 0.8f * alpha.get()));

        boolean searchBoxHovered = isHover(mouseX, mouseY, searchBoxX, searchBoxY, searchBoxWidth, searchBoxHeight) || searchBox.isSelected();
        hoverSearchBox.run(searchBoxHovered ? 1 : 0, 0.08, Easings.QUAD_OUT);

        int boxBg = ColorUtil.multAlpha(ColorUtil.interpolate(theme.clientColor(), ColorUtil.rgba(5, 5, 7, 60), 0.85f), alpha.get());
        int searchBoxBorder = ColorUtil.interpolate(
                ColorUtil.multAlpha(ColorUtil.rgba(50, 50, 55, 255), alpha.get()),
                ColorUtil.multAlpha(theme.textColor(), 0.2f * alpha.get()),
                hoverSearchBox.get()
        );

        RenderUtil.Rounded.roundedRect(matrixStack, searchBoxX, searchBoxY, searchBoxWidth, searchBoxHeight, boxBg, Round.of(4.4f * scale));
        RenderUtil.Rounded.roundedOutline(matrixStack, searchBoxX, searchBoxY, searchBoxWidth, searchBoxHeight, 0.44f * scale, searchBoxBorder, Round.of(4.4f * scale));
        Fonts.icon.draw(matrixStack, "G", searchBoxX + 7.04f * scale, searchBoxY + searchBoxHeight / 2f - 3.08f * scale, ColorUtil.multAlpha(theme.textColor(), 0.4f * alpha.get()), 7.48f * scale);
        searchBox.draw(matrixStack);

        float listX = x;
        float listY = searchBoxY + searchBoxHeight + 8.8f * scale;
        float listWidth = containerWidth;
        float listHeight = containerHeight - headerHeight - searchBoxHeight - 8.8f * scale - 61.6f * scale;

        scroll.update();

        if (Excellent.inst().accountManager().isEmpty() && emptyListAnimation.getValue() > 0) {
            float emptyListAlpha = (float) emptyListAnimation.getValue() * alpha.get();
            if (emptyListAlpha > 0.01f) {
                Fonts.SF_MEDIUM.drawCenter(matrixStack, "Нет аккаунтов", listX + listWidth / 2f, listY + listHeight / 2f, ColorUtil.multAlpha(theme.textColor(), 0.4f * emptyListAlpha), 6.16f * scale);
            }
        }

        if (!Excellent.inst().accountManager().isEmpty()) {
            List<Account> filteredAccounts = getFilteredAccounts();

            while (deleteButtonAnimations.size() < filteredAccounts.size()) {
                deleteButtonAnimations.add(new Animation());
            }
            while (favoriteButtonAnimations.size() < filteredAccounts.size()) {
                favoriteButtonAnimations.add(new Animation());
            }
            while (accountHoverAnimations.size() < filteredAccounts.size()) {
                accountHoverAnimations.add(new Animation());
            }

            float cardWidth = containerWidth - 35.2f * scale;
            float cardHeight = 35.2f * scale;
            float cardSpacing = 4.4f * scale;
            float margin = 17.6f * scale;

            StencilUtil.enable();
            RectUtil.drawRect(matrixStack, listX, listY, listWidth, listHeight, -1);
            StencilUtil.read(1);

            int totalItems = filteredAccounts.size();
            float totalContentHeight = margin + (totalItems * (cardHeight + cardSpacing)) + margin + 275;
            float maxScroll = Math.max(0, totalContentHeight - listHeight);
            scroll.setMax(maxScroll, listHeight);

            for (int i = 0; i < totalItems; i++) {
                Account account = filteredAccounts.get(i);
                float cardX = listX + margin;
                float cardY = listY + margin + scroll.getWheel() + i * (cardHeight + cardSpacing);

                if (cardY + cardHeight >= listY && cardY <= listY + listHeight) {
                    renderAccountCard(matrixStack, account, cardX, cardY, cardWidth, cardHeight, mouseX, mouseY, theme, i, scale);
                }
            }

            StencilUtil.disable();
        }

        float bottomY = y + containerHeight - 52.8f * scale;

        float inputMinWidth = 100 * scale;
        String inputText = textBox.getText().isEmpty() ? "Введите имя" : textBox.getText();
        float inputTextWidth = Fonts.SF_MEDIUM.getWidth(inputText, 5.72f * scale);
        float inputWidth = Math.max(inputMinWidth, inputTextWidth + 30 * scale);
        float inputHeight = 24.64f * scale;
        float inputX = x + containerWidth / 2f - inputWidth / 2f;

        textBox.position.set(inputX + 8.8f * scale, bottomY + inputHeight / 2f - 3.08f * scale);
        textBox.setWidth(inputWidth - 17.6f * scale);
        textBox.setColor(ColorUtil.multAlpha(theme.textColor(), 0.8f * alpha.get()));

        boolean textBoxHovered = isHover(mouseX, mouseY, inputX, bottomY, inputWidth, inputHeight) || textBox.isSelected();
        hoverTextBox.run(textBoxHovered ? 1 : 0, 0.08, Easings.QUAD_OUT);

        int textBoxBorder = ColorUtil.interpolate(
                ColorUtil.multAlpha(ColorUtil.rgba(48, 48, 52, 255), alpha.get()),
                ColorUtil.multAlpha(theme.textColor(), 0.2f * alpha.get()),
                hoverTextBox.get()
        );

        RenderUtil.Rounded.roundedRect(matrixStack, inputX, bottomY, inputWidth, inputHeight, boxBg, Round.of(4.4f * scale));
        RenderUtil.Rounded.roundedOutline(matrixStack, inputX, bottomY, inputWidth, inputHeight, 0.44f * scale, textBoxBorder, Round.of(4.4f * scale));
        textBox.draw(matrixStack);

        float buttonWidth = 50 * scale;
        float buttonHeight = 22.88f * scale;
        float buttonY = bottomY + inputHeight + 5.28f * scale;
        float totalButtonsWidth = buttonWidth * 4 + 1.76f * scale * 3;
        float buttonsStartX = x + containerWidth / 2f - totalButtonsWidth / 2f;

        drawControlButton(matrixStack, "C", buttonsStartX, buttonY, buttonWidth, buttonHeight, mouseX, mouseY, hoverGenerateBtn, theme, "Сгенерировать", scale);
        drawControlButton(matrixStack, "R", buttonsStartX + buttonWidth + 1.76f * scale, buttonY, buttonWidth, buttonHeight, mouseX, mouseY, hoverAddBtn, theme, "Добавить", scale);
        drawControlButton(matrixStack, "X", buttonsStartX + (buttonWidth + 1.76f * scale) * 2, buttonY, buttonWidth, buttonHeight, mouseX, mouseY, hoverClearBtn, theme, "Очистить", scale);
        drawControlButton(matrixStack, "T", buttonsStartX + (buttonWidth + 1.76f * scale) * 3, buttonY, buttonWidth, buttonHeight, mouseX, mouseY, hoverMenuBtn, theme, "Выйти", scale);

        if (hoveredButtonTooltip != null) {
            activeTooltip = hoveredButtonTooltip;
        }

        if (activeTooltip != null) {
            tooltipAlpha.run(hoveredButtonTooltip != null ? 1 : 0, 0.08, Easings.QUAD_OUT);

            if (tooltipAlpha.get() > 0.01) {
                float tooltipWidth = Fonts.SF_MEDIUM.getWidth(activeTooltip, 5.28f * scale) + 10.56f * scale;
                float tooltipHeight = 15.84f * scale;
                float tooltipX = mouseX + 8.8f * scale;
                float tooltipY = mouseY - 22 * scale;

                if (tooltipX + tooltipWidth > this.width) {
                    tooltipX = mouseX - tooltipWidth - 8.8f * scale;
                }

                float currentAlpha = alpha.get() * tooltipAlpha.get();

                matrixStack.push();
                matrixStack.translate(tooltipX + tooltipWidth / 2, tooltipY + tooltipHeight / 2, 0);
                matrixStack.scale(0.9f + 0.1f * tooltipAlpha.get(), 0.9f + 0.1f * tooltipAlpha.get(), 1f);
                matrixStack.translate(-(tooltipX + tooltipWidth / 2), -(tooltipY + tooltipHeight / 2), 0);

                int tooltipBg = ColorUtil.multAlpha(ColorUtil.rgba(25, 25, 28, 240), currentAlpha);
                RenderUtil.Rounded.roundedRect(matrixStack, tooltipX, tooltipY, tooltipWidth, tooltipHeight, tooltipBg, Round.of(4.4f * scale));
                RenderUtil.Rounded.roundedOutline(matrixStack, tooltipX, tooltipY, tooltipWidth, tooltipHeight, 0.44f * scale, ColorUtil.multAlpha(theme.textColor(), 0.15f * currentAlpha), Round.of(4.4f * scale));
                Fonts.SF_MEDIUM.drawCenter(matrixStack, activeTooltip, tooltipX + tooltipWidth / 2f, tooltipY + tooltipHeight / 2f - 2.64f * scale, ColorUtil.multAlpha(theme.textColor(), 0.9f * currentAlpha), 5.28f * scale);

                matrixStack.pop();
            } else {
                activeTooltip = null;
            }
        }
    }

    private void drawControlButton(MatrixStack matrix, String icon, float x, float y, float width, float height, int mouseX, int mouseY, Animation animation, Theme theme, String tooltip, float scale) {
        boolean hovered = isHover(mouseX, mouseY, x, y, width, height);
        animation.run(hovered ? 1 : 0, 0.08, Easings.QUAD_OUT);

        if (hovered) {
            hoveredButtonTooltip = tooltip;
        }

        float yShift = -animation.get() * 1.76f * scale;

        int bgColor = ColorUtil.interpolate(
                ColorUtil.multAlpha(ColorUtil.interpolate(theme.clientColor(), ColorUtil.rgba(5, 5, 7, 60), 0.85f), alpha.get()),
                ColorUtil.multAlpha(ColorUtil.interpolate(theme.clientColor(), ColorUtil.rgba(15, 15, 18, 255), 0.7f), alpha.get()),
                animation.get()
        );

        int iconColor = ColorUtil.interpolate(
                ColorUtil.multAlpha(theme.textColor(), 0.4f * alpha.get()),
                ColorUtil.multAlpha(theme.textColor(), 0.8f * alpha.get()),
                animation.get()
        );

        RenderUtil.Rounded.roundedRect(matrix, x, y + yShift, width, height, bgColor, Round.of(4.4f * scale));

        if (hovered) {
            RenderUtil.Rounded.roundedOutline(matrix, x, y + yShift, width, height, 0.44f * scale, ColorUtil.multAlpha(theme.textColor(), 0.15f * alpha.get()), Round.of(4.4f * scale));
        }

        float iconSize = 7.04f * scale;
        float iconX = x + width / 2f - Fonts.icon.getWidth(icon, iconSize) / 2f;
        float iconY = y + height / 2f - iconSize / 2f;
        Fonts.icon.draw(matrix, icon, iconX, iconY + yShift, iconColor, iconSize);
    }

    private List<Account> getFilteredAccounts() {
        List<Account> result = new ArrayList<>();
        String query = searchBox.getText().toLowerCase();

        for (Account account : Excellent.inst().accountManager().getFavoriteAccountsSorted()) {
            if (query.isEmpty() || account.name().toLowerCase().contains(query)) {
                result.add(account);
            }
        }

        for (Account account : Excellent.inst().accountManager()) {
            if (account.favorite()) continue;
            if (query.isEmpty() || account.name().toLowerCase().contains(query)) {
                result.add(account);
            }
        }

        return result;
    }

    private void renderAccountCard(MatrixStack matrix, Account account, float x, float y, float width, float height, int mouseX, int mouseY, Theme theme, int index, float scale) {
        boolean isSelected = selected != null && selected.name().equals(account.name());
        Animation hoverAnim = accountHoverAnimations.get(index);
        boolean hovered = isHover(mouseX, mouseY, x, y, width, height);
        hoverAnim.run(hovered ? 1 : 0, 0.08, Easings.QUAD_OUT);

        int bgColor;
        if (isSelected) {
            bgColor = ColorUtil.multAlpha(ColorUtil.interpolate(theme.clientColor(), ColorUtil.rgba(20, 20, 25, 255), 0.7f), alpha.get());
        } else if (hovered) {
            bgColor = ColorUtil.multAlpha(ColorUtil.interpolate(theme.clientColor(), ColorUtil.rgba(15, 15, 18, 255), 0.75f), alpha.get());
        } else {
            bgColor = ColorUtil.multAlpha(ColorUtil.interpolate(theme.clientColor(), ColorUtil.rgba(5, 5, 7, 60), 0.85f), alpha.get());
        }

        RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, bgColor, Round.of(4.4f * scale));
        RenderUtil.Rounded.roundedOutline(matrix, x, y, width, height, 0.44f * scale, ColorUtil.multAlpha(ColorUtil.rgba(60, 60, 65, 255), alpha.get()), Round.of(4.4f * scale));

        if (isSelected) {
            RenderUtil.Rounded.roundedOutline(matrix, x, y, width, height, 0.88f * scale, ColorUtil.multAlpha(theme.textColor(), 0.2f * alpha.get()), Round.of(4.4f * scale));
        }

        float headSize = 26.4f * scale;
        float headX = x + 6.16f * scale;
        float headY = y + 2 + (height - headSize) / 2f;

        ResourceLocation skin = getSkinLocation(account.name());
        drawPlayerHead(matrix, skin, headX, headY, headSize, scale);

        float textX = headX + headSize + 7.04f * scale;
        float nameY = y + height / 2f - 3.52f * scale;

        int nameColor = ColorUtil.multAlpha(theme.textColor(), 0.9f * alpha.get());

        String displayName = account.name();

        Fonts.SF_MEDIUM.draw(matrix, displayName, textX, nameY, nameColor, 6.16f * scale);

        float buttonSize = 15.84f * scale;
        float buttonY = y + (height - buttonSize) / 2f;
        float favoriteX = x + width - buttonSize - 6.6f * scale;
        float deleteX = favoriteX - buttonSize - 5.28f * scale;

        if (index < favoriteButtonAnimations.size()) {
            Animation favAnim = favoriteButtonAnimations.get(index);
            favAnim.update();
            boolean favHovered = isHover(mouseX, mouseY, favoriteX, buttonY, buttonSize, buttonSize);
            favAnim.run(favHovered ? 1 : 0, 0.08, Easings.QUAD_OUT);

            int favBg = ColorUtil.interpolate(
                    ColorUtil.multAlpha(ColorUtil.rgba(35, 35, 38, 0), alpha.get()),
                    ColorUtil.multAlpha(ColorUtil.rgba(255, 200, 60, 100), alpha.get()),
                    favAnim.get()
            );
            int favIcon = account.favorite()
                    ? ColorUtil.multAlpha(ColorUtil.getColor(255, 200, 60), alpha.get())
                    : ColorUtil.interpolate(
                    ColorUtil.multAlpha(theme.textColor(), 0.3f * alpha.get()),
                    ColorUtil.multAlpha(ColorUtil.getColor(255, 200, 60), alpha.get()),
                    favAnim.get()
            );

            if (favAnim.get() > 0.01) {
                RenderUtil.Rounded.roundedRect(matrix, favoriteX, buttonY, buttonSize, buttonSize, favBg, Round.of(4.4f * scale));
            }
            Fonts.icon.draw(matrix, "S", favoriteX + buttonSize / 2f - 3.08f * scale, buttonY + buttonSize / 2f - 3.08f * scale, favIcon, 6.16f * scale);
        }

        if (index < deleteButtonAnimations.size()) {
            Animation deleteAnim = deleteButtonAnimations.get(index);
            deleteAnim.update();
            boolean deleteHovered = isHover(mouseX, mouseY, deleteX, buttonY, buttonSize, buttonSize);
            deleteAnim.run(deleteHovered ? 1 : 0, 0.08, Easings.QUAD_OUT);

            int deleteBg = ColorUtil.interpolate(
                    ColorUtil.multAlpha(ColorUtil.rgba(35, 35, 38, 0), alpha.get()),
                    ColorUtil.multAlpha(ColorUtil.rgba(255, 60, 60, 100), alpha.get()),
                    deleteAnim.get()
            );
            int deleteIcon = ColorUtil.interpolate(
                    ColorUtil.multAlpha(ColorUtil.getColor(255, 80, 80), 0.5f * alpha.get()),
                    ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alpha.get()),
                    deleteAnim.get()
            );

            if (deleteAnim.get() > 0.01) {
                RenderUtil.Rounded.roundedRect(matrix, deleteX, buttonY, buttonSize, buttonSize, deleteBg, Round.of(4.4f * scale));
            }
            Fonts.icon.draw(matrix, "X", deleteX + buttonSize / 2f - 2.64f * scale, buttonY + buttonSize / 2f - 2.64f * scale, deleteIcon, 5.28f * scale);
        }

        if (account.isBanned()) {
            float banSize = 12.32f * scale;
            float banX = headX + headSize - banSize / 2f;
            float banY = headY - banSize / 3.52f * scale;
            int banBg = ColorUtil.multAlpha(ColorUtil.rgba(255, 60, 60, 200), alpha.get());
            RenderUtil.Rounded.roundedRect(matrix, banX, banY, banSize, banSize, banBg, Round.of(3.52f * scale));
            Fonts.icon.draw(matrix, "X", banX + banSize / 2f - 2.2f * scale, banY + banSize / 2f - 2.2f * scale, ColorUtil.multAlpha(ColorUtil.WHITE, alpha.get()), 4.4f * scale);
        }

        account.position().set(x, y);
        account.size().set(width, height);
    }

    private void drawPlayerHead(MatrixStack matrix, ResourceLocation skin, float x, float y, float size, float scale) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0F);
        RenderSystem.enableTexture();

        mc.getTextureManager().bindTexture(skin);

        float[] rgba = ColorUtil.getRGBAf(ColorUtil.WHITE);
        RenderSystem.color4f(rgba[0], rgba[1], rgba[2], alpha.get());

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrix.getLast().getMatrix());

        float pivotX = x + size / 2.0F;
        float pivotY = y + size / 2.0F;

        RenderSystem.translatef(pivotX, pivotY, 0);
        RenderSystem.rotatef(180, 0, 0, 1);
        RenderSystem.translatef(-pivotX, -pivotY, 0);

        RenderUtil.Texture.roundedHead(skin, x, y, size, size, size / 7.04f * scale, alpha.get(), 0);

        RenderSystem.popMatrix();

        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.disableBlend();
    }

    private ResourceLocation getSkinLocation(String name) {
        if (mc.getConnection() != null) {
            var info = mc.getConnection().getPlayerInfo(name);
            if (info != null) {
                return info.getLocationSkin();
            }
        }
        return new ResourceLocation("textures/entity/steve.png");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float scale = getResolutionScale();
        if (exit) return false;

        float containerWidth = 264 * scale;
        float containerHeight = 422.4f * scale;
        float x = this.width / 2F - containerWidth / 2F;
        float y = this.height / 2F - containerHeight / 2F + yOffset.get() * scale;

        textBox.mouse(mouseX, mouseY, button);
        searchBox.mouse(mouseX, mouseY, button);

        float bottomY = y + containerHeight - 52.8f * scale;

        float inputMinWidth = 100 * scale;
        String inputText = textBox.getText().isEmpty() ? "Введите имя" : textBox.getText();
        float inputTextWidth = Fonts.SF_MEDIUM.getWidth(inputText, 5.72f * scale);
        float inputWidth = Math.max(inputMinWidth, inputTextWidth + 30 * scale);
        float inputX = x + containerWidth / 2f - inputWidth / 2f;

        float inputHeight = 24.64f * scale;
        float buttonWidth = 50 * scale;
        float buttonHeight = 22.88f * scale;
        float buttonY = bottomY + inputHeight + 5.28f * scale;
        float totalButtonsWidth = buttonWidth * 4 + 1.76f * scale * 3;
        float buttonsStartX = x + containerWidth / 2f - totalButtonsWidth / 2f;

        if (isLClick(button)) {
            if (isHover(mouseX, mouseY, buttonsStartX, buttonY, buttonWidth, buttonHeight)) {
                setSession(nameGenerator.generate());
                return true;
            }
            if (isHover(mouseX, mouseY, buttonsStartX + buttonWidth + 1.76f * scale, buttonY, buttonWidth, buttonHeight)) {
                addAccountFromTextBox();
                return true;
            }
            if (isHover(mouseX, mouseY, buttonsStartX + (buttonWidth + 1.76f * scale) * 2, buttonY, buttonWidth, buttonHeight)) {
                Excellent.inst().accountManager().clear();
                selected = null;
                return true;
            }
            if (isHover(mouseX, mouseY, buttonsStartX + (buttonWidth + 1.76f * scale) * 3, buttonY, buttonWidth, buttonHeight)) {
                closeScreen();
                return true;
            }
        }

        float headerHeight = 50.6f * scale;
        float searchBoxMinWidth = 80 * scale;
        String searchText = searchBox.getText().isEmpty() ? "Поиск" : searchBox.getText();
        float searchTextWidth = Fonts.SF_MEDIUM.getWidth(searchText, 7.48f * scale);
        float searchBoxWidth = Math.max(searchBoxMinWidth, searchTextWidth + 40 * scale);
        float searchBoxHeight = 22.88f * scale;
        float searchBoxY = y + headerHeight;

        float listX = x;
        float listY = searchBoxY + searchBoxHeight + 8.8f * scale;
        float listWidth = containerWidth;
        float listHeight = containerHeight - headerHeight - searchBoxHeight - 8.8f * scale - 61.6f * scale;

        if (isHover(mouseX, mouseY, listX, listY, listWidth, listHeight)) {
            List<Account> filteredAccounts = getFilteredAccounts();
            float cardWidth = containerWidth - 35.2f * scale;
            float cardHeight = 35.2f * scale;
            float cardSpacing = 4.4f * scale;
            float margin = 17.6f * scale;

            for (int i = 0; i < filteredAccounts.size(); i++) {
                Account account = filteredAccounts.get(i);
                float cardX = listX + margin;
                float cardY = listY + margin + scroll.getWheel() + i * (cardHeight + cardSpacing);

                if (isHover(mouseX, mouseY, cardX, cardY, cardWidth, cardHeight)) {
                    float buttonSize = 15.84f * scale;
                    float buttonYPos = cardY + (cardHeight - buttonSize) / 2f;
                    float favoriteX = cardX + cardWidth - buttonSize - 6.6f * scale;
                    float deleteX = favoriteX - buttonSize - 5.28f * scale;

                    if (isLClick(button)) {
                        if (isHover(mouseX, mouseY, deleteX, buttonYPos, buttonSize, buttonSize)) {
                            Excellent.inst().accountManager().remove(account);
                            if (selected != null && selected.name().equals(account.name())) {
                                selected = null;
                            }
                            return true;
                        }
                        if (isHover(mouseX, mouseY, favoriteX, buttonYPos, buttonSize, buttonSize)) {
                            account.toggleFavorite();
                            return true;
                        }
                        setSession(account.name());
                        return true;
                    }

                    if (button == 2) {
                        account.toggleBanned();
                        return true;
                    }
                }
            }

            Excellent.inst().accountManager().forEach(acc -> acc.mouseClicked(mouseX, mouseY, button));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        float scale = getResolutionScale();
        float containerWidth = 264 * scale;
        float containerHeight = 422.4f * scale;
        float x = this.width / 2F - containerWidth / 2F;
        float y = this.height / 2F - containerHeight / 2F + yOffset.get() * scale;

        float headerHeight = 50.6f * scale;
        float searchBoxMinWidth = 80 * scale;
        String searchText = searchBox.getText().isEmpty() ? "Поиск" : searchBox.getText();
        float searchTextWidth = Fonts.SF_MEDIUM.getWidth(searchText, 7.48f * scale);
        float searchBoxWidth = Math.max(searchBoxMinWidth, searchTextWidth + 40 * scale);
        float searchBoxHeight = 22.88f * scale;
        float searchBoxY = y + headerHeight;

        float listX = x;
        float listY = searchBoxY + searchBoxHeight + 8.8f * scale;
        float listWidth = containerWidth;
        float listHeight = containerHeight - headerHeight - searchBoxHeight - 8.8f * scale - 61.6f * scale;

        if (isHover(mouseX, mouseY, listX, listY, listWidth, listHeight)) {
            scroll.setWheel(scroll.getWheel() + (float) delta * 35f);
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void addAccountFromTextBox() {
        if (!textBox.getText().isEmpty() && textBox.getText().length() >= 3 && !PlayerUtil.isInvalidName(textBox.getText())) {
            setSession(textBox.getText());
        }
    }

    private void setSession(String name) {
        mc.session = new Session(name, "", "", "mojang");
        LoginManager.saveUsername(name);
        textBox.setText(name);
        textBox.setCursor(name.length());

        Account existing = Excellent.inst().accountManager().getAccount(name).orElse(null);
        if (existing == null) {
            Account account = new Account(LocalDateTime.now(), name);
            Excellent.inst().accountManager().addAccount(account);
            selected = account;
        } else {
            selected = existing;
        }

        scroll.setTarget(0);
        scroll.update();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Excellent.inst().accountManager().forEach(account -> account.mouseReleased(mouseX, mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (exit) return false;
        Excellent.inst().accountManager().forEach(account -> account.keyPressed(keyCode, scanCode, modifiers));

        if (textBox.isSelected()) {
            textBox.keyPressed(keyCode);
            if (keyCode == Keyboard.KEY_ENTER.getKey()) {
                textBox.setSelected(false);
                addAccountFromTextBox();
            }
        }

        if (searchBox.isSelected()) {
            searchBox.keyPressed(keyCode);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (exit) return false;
        Excellent.inst().accountManager().forEach(account -> account.charTyped(codePoint, modifiers));

        if (textBox.isSelected() && textBox.getText().length() < 16) {
            textBox.charTyped(codePoint);
        }

        if (searchBox.isSelected() && searchBox.getText().length() < 16) {
            searchBox.charTyped(codePoint);
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (!exit) {
            alpha.run(0.0, 0.15, Easings.SINE_IN);
            scale.run(0.0, 0.15, Easings.SINE_IN);
            yOffset.run(-30, 0.15, Easings.SINE_IN);
            exit = true;
        }
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        Excellent.inst().accountManager().forEach(Account::onClose);
        LoginManager.saveUsername(mc.session.getProfile().getName());
    }

    private void closeCheck() {
        if (exit && this.scale.isFinished() && this.alpha.isFinished()) {
            closeScreen();
        }
    }

    private static class NameGenerator {
        private static final String[] PREFIXES = {"1WantTo", "xDeadFor", "im", "vol", "strat", "king", "private", "flying", "light", "dark", "mega", "ultra", "pro", "the", "real", "not", "just", "super", "hyper", "neo", "astro", "nova", "shadow", "fire", "ice", "storm", "void", "night", "day", "fast", "slow", "silent", "loud", "crazy", "lucky", "unreal", "true", "fake", "legend", "ghost", "phantom", "alpha", "beta", "omega", "prime", "zero", "one", "two", "triple", "quad", "fly", "dead", "for", "king", "strat", "private", "flying", "xX", "Its", "i", "Dr", "Mr", "Official", "NotSo", "Maybe", "Giga", "Chad", "TheOnly"};
        private static final String[] SUFFIXES = {"King", "Strat", "Private", "Flying", "Light", "Dark", "Pro", "God", "Legend", "Ghost", "Phantom", "Alpha", "Beta", "Omega", "Prime", "Zero", "One", "Two", "Triple", "Quad", "Fly", "Dead", "For", "Night", "Day", "Fast", "Slow", "Silent", "Loud", "Crazy", "Lucky", "Unreal", "True", "Fake", "Nova", "Astro", "Shadow", "Fire", "Ice", "Storm", "Void", "Gamer", "Player", "YT", "TV", "PvP", "Master", "Slayer", "_Official", "HD"};
        private static final String[] ADJECTIVES = {"Angry", "Happy", "Sad", "Lazy", "Quick", "Silent", "Loud", "Red", "Blue", "Green", "Dark", "Light", "Golden", "Iron", "Diamond", "Emerald", "Broken", "Hidden", "Forgotten", "Lost", "Found", "Ancient", "Future", "Magic", "Epic", "Toxic", "Salty"};
        private static final String[] GAMING_NOUNS = {"Creeper", "Ender", "Ghast", "Warden", "Noob", "Pro", "Glitch", "Pixel", "Byte", "Lag", "Sniper", "Tank", "Healer", "Mage", "Rogue", "Warrior", "Headshot", "Frag", "Gank", "Rusher", "Camper"};
        private static final String[] ANIMAL_NOUNS = {"Wolf", "Fox", "Panda", "Tiger", "Lion", "Eagle", "Shark", "Dragon", "Phoenix", "Kraken", "Bear", "Snake", "Cobra", "Viper"};
        private static final String[] CELEB_FIRST_NAMES = {"Keanu", "Elon", "Taylor", "Dwayne", "Tom", "Bill", "Steve", "Nikola", "Albert", "Peter", "Tony", "Bruce", "Clark"};
        private static final String[] CELEB_LAST_NAMES = {"Reeves", "Musk", "Swift", "Johnson", "Hanks", "Gates", "Jobs", "Tesla", "Einstein", "Parker", "Stark", "Wayne", "Kent"};
        private static final String[] FICTIONAL_CHARS = {"JohnWick", "WalterWhite", "TonyStark", "DarthVader", "LukeSkywalker", "MasterChief", "Agent47", "GeraltOfRivia", "SolidSnake"};

        private static final char[] VOWELS = {'a', 'e', 'i', 'o', 'u', 'y'};
        private static final char[] CONSONANTS = {'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'z'};
        private final Random random = new Random();

        private String randomFrom(String[] array) {
            return array[random.nextInt(array.length)];
        }

        private String generateReadable(int len) {
            StringBuilder sb = new StringBuilder();
            boolean vowel = random.nextBoolean();
            for (int i = 0; i < len; i++) {
                sb.append(vowel ? VOWELS[random.nextInt(VOWELS.length)] : CONSONANTS[random.nextInt(CONSONANTS.length)]);
                vowel = !vowel;
            }
            return sb.toString();
        }

        public String generate() {
            int type = random.nextInt(12);
            return switch (type) {
                case 0 -> randomFrom(PREFIXES) + randomFrom(SUFFIXES);
                case 1 -> {
                    String name = generateReadable(6 + random.nextInt(4));
                    yield name.substring(0, 1).toUpperCase() + name.substring(1) + (random.nextBoolean() ? "_" : "") + (1000 + random.nextInt(9000));
                }
                case 2 ->
                        randomFrom(ADJECTIVES) + randomFrom(ANIMAL_NOUNS) + (random.nextBoolean() ? "" : String.valueOf(random.nextInt(100)));
                case 3 ->
                        randomFrom(ADJECTIVES) + randomFrom(GAMING_NOUNS) + (random.nextBoolean() ? "" : String.valueOf(1337 + random.nextInt(8662)));
                case 4 ->
                        randomFrom(CELEB_FIRST_NAMES) + (random.nextBoolean() ? "_" : "") + randomFrom(CELEB_LAST_NAMES) + (random.nextBoolean() ? "" : String.valueOf(random.nextInt(1000)));
                case 5 -> "The" + randomFrom(ADJECTIVES) + randomFrom(ANIMAL_NOUNS);
                case 6 -> "xX_" + randomFrom(GAMING_NOUNS) + randomFrom(SUFFIXES) + "_Xx";
                case 7 -> {
                    String base = randomFrom(PREFIXES) + randomFrom(GAMING_NOUNS);
                    yield base.replace('e', '3').replace('a', '4').replace('o', '0').replace('i', '1');
                }
                case 8 -> randomFrom(FICTIONAL_CHARS) + (random.nextBoolean() ? "Fan" : "") + random.nextInt(100);
                case 9 -> {
                    String namePart1 = generateReadable(3 + random.nextInt(3));
                    String namePart2 = generateReadable(3 + random.nextInt(3));
                    yield namePart1.substring(0, 1).toUpperCase() + namePart1.substring(1) + (random.nextBoolean() ? "" : "_") + namePart2.substring(0, 1).toUpperCase() + namePart2.substring(1);
                }
                case 10 -> "Not" + randomFrom(CELEB_FIRST_NAMES) + randomFrom(CELEB_LAST_NAMES);
                default -> randomFrom(GAMING_NOUNS) + random.nextInt(10000);
            };
        }
    }
}
package org.privates.client.managers.module.impl.player;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Namespaced;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer2;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.privates.client.api.interfaces.IMinecraft.mc;
import static org.privates.client.utils.math.Mathf.isHover;

public class ChangelogGui extends Screen {

    private static class ChangeEntry {
        final String type;
        final String category;
        final String text;
        float yPosition;

        ChangeEntry(String type, String category, String text) {
            this.type = type;
            this.category = category;
            this.text = text;
        }
    }

    private final List<ChangeEntry> entries = new ArrayList<>();
    private final Animation openAnimation = new Animation();
    private final Animation closeAnimation = new Animation();
    private final Animation scrollbarAnimation = new Animation();
    private final Map<String, Animation> buttonAnimations = new HashMap<>();
    private boolean closing = false;

    private float scrollOffset = 0;
    private float targetScrollOffset = 0;
    private boolean draggingScroll = false;
    private float dragStartY = 0;
    private float dragStartOffset = 0;
    private float totalContentHeight = 0;

    private int addedCount = 0;
    private int fixedCount = 0;
    private int removedCount = 0;
    private int remakeCount = 0;

    private String highlightedType = "";
    private final Animation highlightAnimation = new Animation();
    private long highlightStartTime = 0;

    public ChangelogGui() {
        super(new StringTextComponent("CHANGELOG"));
        parseChangelog();
        buttonAnimations.put("+", new Animation());
        buttonAnimations.put("~", new Animation());
        buttonAnimations.put("-", new Animation());
        buttonAnimations.put("#", new Animation());
    }

    private float getResolutionScale() {
        return mc.getMainWindow().getWidth() >= 3840 ? 1.5f : mc.getMainWindow().getWidth() >= 2560 ? 1.0f : mc.getMainWindow().getWidth() >= 1920 ? 0.75f : 0.65f;
    }

    private void parseChangelog() {
        String[][] categoryChanges = {
                {"best", "[ + ] FtFarm [узнаете на апдейте можно поднимать до 40кк в час]"},
                {"best", "[ + ] SellFarm [2-й фарм для FtFarm автопродает то что мы нафармили]"},
                {"other", "[ ~ ] ProxyMod пофиксил теперь работает стабильно"},
                {"hvh", "[ + ] CrystalOptimizer"},
                {"hvh", "[ + ] PortalGodMode"},
                {"hvh", "[ + ] AutoPotion теперь работает с ротацией то что кидает зелье на вашу голову а не куда вы смотрите как раньше"},
                {"elytrahvh", "[ ~ ] Доработали ElytraTarget"},
                {"elytrahvh", "[ ~ ] Измениили ElytraBooster"},
                {"holyworld", "[ + ] Ротация HW теперь не должна банить"},
                {"holyworld", "[ + ] Timer (Grim mode) работает на HolyWorld"},
                {"spookytime", "[ ~ ] Разделение между ротациями SpookyTime и SpookyTime дуэли"},
                {"spookytime", "[ ~ ] AutoBuy был чуть чуть изменен"},
                {"reallyworld", "[ + ] ScoreBoardHealth (FIX HP)"},
                {"funtime", "[ ~ ] Изменили ротацию на Funtime теперь она быстрее и более точная"},
                {"pvp", "[ ~ ] AttarkAura Переработали ротацию Smooth теперь она гибкая в настройке не так как кастом но все ровно гибкая плавная настройка джиттеров, мультипоитов и скоростей"},
                {"pvp", "[ ~ ] AttakAura в ротацию Custom добавлены новые настройки для современного обхода античитов типо polar или sloth а точнее \"Ограничить Pitch\",\"Ротация шагами (шаг yaw, шаг pitch)\",\"Рваная ротация\""},
                {"pvp", "[ ~ ] AttakAura были добавлены выборы пвп режимов между 1.8 и 1.9+"},
                {"pvp", "[ + ] Изменили и пофиксили ротации и остальные чеки в AttackAura"},
                {"pvp", "[ ~ ] Пофикшены баги в AttackAura (также чуть чуть доработали ротацию Custom)"},
                {"pvp", "[ ~ ] Изменили GuiMove и обходы GuiMove (на HolyWorld и SpookyTime ставить просто Обход)"},
                {"pvp", "[ + ] в NoSlow добавлены новые виды такие как Matrix и другие"},
                {"pvp", "[ + ] в Speed добавили новые виды такие как Matrix702,ACC.V5,VERUSB17"},
                {"pvp", "[ + ] Criticals [Добавлены новые моды такие как \"GroundSpoof\",\"Visual\",\"Ncp\",\"Packet\",\"SPRx\"]"},
                {"visual", "[ ~ ] Полностью изменен ColorPicker"},
                {"visual", "[ ~ ] Пофикшены баги в MainMenu"},
                {"visual", "[ + ] Изменена CustomCamera теперь можно выбрать плавность и будет очень красиво двигатся камера"},
                {"visual", "[ + ] Добавлены новые настройки то есть кастомизация в MainMenu теперь можно выбрать Украшение - Тянку - Задний фон"},
                {"visual", "[ + ] ClickGuiSettings для UmbrellaGui можно полностью кастомизировать саму гуишку но пока не до идеала доделана функция"},
                {"visual", "[ + ] Новый вид ClickGui (Umbrella)"},
                {"visual", "[ + ] Новый вид ClickGui (Panel) (теперь без багов открывается панельная гуи)"},
                {"visual", "[ # ] Переработан полностью вид ClickGui (CsGo Gui) теперь в минималистике"},
                {"visual", "[ + ] Во все гуи мы добавили наши настройки для выбора айтемов и выбора тем так как в прошлой версии не было это-го во всех гуи и было только в ClickGui основной"},
                {"visual", "[ - ] Убрали анимацию с GameMenu"},
                {"visual", "[ + ] Вернули NearbyPlayers с новой кастомизацией"},
                {"visual", "[ + ] TypeSpeedGame (игра в тапалку то есть проверка вашего уровня печати)"},
                {"visual", "[ + ] Вернули ArrayList с новой кастомизацией"},
                {"visual", "[ ~ ] Изменен DamageIncidicatro aka HitMarker"},
                {"visual", "[ ~ ] Изменены JumpCircle"},
                {"visual", "[ ~ ] Изменены Trails"},
                {"visual", "[ ~ ] Изменены Particles"},
                {"visual", "[ + ] Добавили новую функцию ChatSetting можно настроить себе анимации чата и включение подсветки сообщение с ником и также цвет подсветки"},
                {"visual", "[ + ] TabSettings полная кастомизация таба но криво реализована но многое что и так хорошо"},
                {"visual", "[ ~ ] Кастомизация функции Notification выбор размера удаление разделителей и иконок"},
                {"visual", "[ + ] В весь HUD то есть InterFace было добавлена новая настройка Enable Outline она включает обводку элемента под цвет темы"},
                {"visual", "[ + ] WaterMark новые настройки \"GifLogo\",\"OnlyGif \",\"Enable Rect\""},
                {"visual", "[ + ] KeyBinds Новый настройки \"Show All Binds\",\"New Rect\",\"New Rect Gap\",\"Dynamic Rect Gap\",\"Show column rect\",\"Show bind rect\""},
                {"visual", "[ + ] Новые настройки Potions \"New Rect\",\"New Rect Gap\",\"Dynamic Rect Gap\",\"New Render\""},
                {"visual", "[ + ] Новые настройки NearbyPlayers \"ItemRender old-new-styled\",\"PlayerRender minimal-compact-FULL\",\"MultiColumn\",\"Max person for column\",\"Gap Column\",\"Show Healt\",\"Show Healt Bar\",\"Show Armor\",\"Show Tracked Items\""},
                {"visual", "[ ~ ] Полностью измени AltManager"},
                {"visual", "[ ~ ] Доработали MainMenu и доработали анимации"},
                {"visual", "[ ~ ] Новая страничка в прочтении Readme (4) Страница про AltManager"},
                {"visual", "[ ~ ] Readme & AccountManager & MainMenu добавили падающие снежинки"},
                {"visual", "[ + ] ChangeLogInfo (кнопка в mainmenu)"},
                {"visual", "[ # ] Esp полностью переработаны"},
                {"visual", "[ # ] Переработано полностью Kagune"},
                {"visual", "[ + ] CometParticles"},
                {"visual", "[ + ] ElytraPredictRender"}
        };

        for (String[] change : categoryChanges) {
            String category = change[0];
            String line = change[1];

            if (line.startsWith("[ + ]")) {
                addedCount++;
                entries.add(new ChangeEntry("+", category, line.substring(5).trim()));
            } else if (line.startsWith("[ ~ ]")) {
                fixedCount++;
                entries.add(new ChangeEntry("~", category, line.substring(5).trim()));
            } else if (line.startsWith("[ - ]")) {
                removedCount++;
                entries.add(new ChangeEntry("-", category, line.substring(5).trim()));
            } else if (line.startsWith("[ # ]")) {
                remakeCount++;
                entries.add(new ChangeEntry("#", category, line.substring(5).trim()));
            } else if (line.startsWith("[ * ]")) {
                entries.add(new ChangeEntry("*", category, line.substring(5).trim()));
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        this.openAnimation.run(1.0, 0.4, Easings.EXPO_OUT);
        this.closeAnimation.set(0.0);
        this.closing = false;
        this.scrollOffset = 0;
        this.targetScrollOffset = 0;
    }

    @Override
    public void onClose() {
        if (this.closing) return;
        this.closing = true;
        this.closeAnimation.run(1.0, 0.3, Easings.EXPO_IN);
        this.openAnimation.run(0.0, 0.3, Easings.EXPO_IN);
    }

    private void updateAnimations() {
        openAnimation.update();
        closeAnimation.update();
        scrollbarAnimation.update();
        highlightAnimation.update();

        for (Animation anim : buttonAnimations.values()) {
            anim.update();
        }

        if (!highlightedType.isEmpty() && System.currentTimeMillis() - highlightStartTime > 1500) {
            highlightAnimation.run(0.0, 0.8, Easings.EXPO_OUT);
            if (highlightAnimation.getValue() < 0.01) {
                highlightedType = "";
            }
        }

        float maxScroll = Math.max(0, totalContentHeight - 450);
        targetScrollOffset = Math.max(0, Math.min(maxScroll, targetScrollOffset));
        if (Math.abs(scrollOffset - targetScrollOffset) > 0.1f) {
            scrollOffset += (targetScrollOffset - scrollOffset) * 0.15f;
        } else {
            scrollOffset = targetScrollOffset;
        }
    }

    public void drawBackground(MatrixStack matrixStack) {
        Theme hud = Theme.getInstance();
        int width = mc.getMainWindow().getScaledWidth();
        int height = mc.getMainWindow().getScaledHeight();
        RectUtil.drawRect(matrixStack, 0, 0, width, height, ColorUtil.multDark(hud.clientColor(), 0.08f));
        drawImage(matrixStack, new Namespaced("texture/background2.png"), 0, 0, width, height, true, hud.clientColor());
    }

    public void drawImage(MatrixStack matrix, ResourceLocation location, float x, float y, float width, float height, boolean bloom, int color) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        mc.getTextureManager().bindTexture(location);
        RectUtil.drawRect(matrix, x, y, width, height, color, bloom, true);
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    private List<String> wrapText(String text, float maxWidth, float fontSize) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            float testWidth = Fonts.SF_BOLD.getWidth(testLine, fontSize);

            if (testWidth > maxWidth && currentLine.length() > 0) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (closing && closeAnimation.isFinished() && openAnimation.isFinished()) {
            mc.displayScreen(null);
            return;
        }

        RenderUtil.drawBackground(matrix);
        updateAnimations();

        Theme theme = Theme.getInstance();
        int clientColor = theme.clientColor();

        float s = getResolutionScale();
        float easedAnim = openAnimation.get() * (1.0f - closeAnimation.get());
        if (easedAnim <= 0.01f) return;

        float centerX = this.width / 2.0f;
        float centerY = this.height / 2.0f;

        matrix.push();
        matrix.translate(centerX, centerY, 0);
        matrix.scale(easedAnim, easedAnim, 1);
        matrix.translate(-centerX, -centerY, 0);

        String changelogText = "ChangeLog";
        float changelogFontSize = 14f * s;
        float changelogWidth = Fonts.SF_BOLD.getWidth(changelogText, changelogFontSize) + 20 * s;
        float changelogHeight = changelogFontSize + 12 * s;
        float changelogX = centerX - changelogWidth / 2;
        float changelogY = 10 * s;

        BlurRenderer2.registerRenderCall(() -> {
            int changelogBg = ColorUtil.multAlpha(ColorUtil.replAlpha(clientColor, 40), easedAnim);
            RenderUtil.Rounded.roundedRect(matrix, changelogX, changelogY, changelogWidth, changelogHeight, changelogBg, Round.of(6 * s));
        });
        BlurRenderer2.draw(10);

        int changelogBg = ColorUtil.multAlpha(ColorUtil.replAlpha(clientColor, 15), easedAnim);
        RenderUtil.Rounded.roundedRect(matrix, changelogX, changelogY, changelogWidth, changelogHeight, changelogBg, Round.of(6 * s));

        int changelogBorder = ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 18), easedAnim);
        RenderUtil.Rounded.roundedOutline(matrix, changelogX, changelogY, changelogWidth, changelogHeight, 0.945f * s, changelogBorder, Round.of(6 * s));

        int changelogTextColor = ColorUtil.multAlpha(ColorUtil.interpolateColor(clientColor, ColorUtil.WHITE, 0.3f), easedAnim);
        Fonts.SF_BOLD.drawCenter(matrix, changelogText, changelogX + changelogWidth / 2, changelogY + 5 * s, changelogTextColor, changelogFontSize);

        String[] buttonLabels = {"Added [+] " + addedCount, "Fixed [~] " + fixedCount, "Removed [-] " + removedCount, "Remake [#] " + remakeCount};
        String[] buttonTypes = {"+", "~", "-", "#"};
        int[] buttonColors = {ColorUtil.getColor(0, 255, 0), ColorUtil.getColor(255, 215, 0), ColorUtil.getColor(255, 0, 0), ColorUtil.getColor(138, 43, 226)};

        float totalButtonWidth = 0;
        float buttonFontSize = 9f * s;
        float buttonHeight = buttonFontSize + 6 * s;
        float buttonSpacing = 4 * s;
        float[] buttonWidths = new float[buttonLabels.length];

        for (int i = 0; i < buttonLabels.length; i++) {
            buttonWidths[i] = Fonts.SF_BOLD.getWidth(buttonLabels[i], buttonFontSize) + 10 * s;
            totalButtonWidth += buttonWidths[i];
            if (i < buttonLabels.length - 1) totalButtonWidth += buttonSpacing;
        }

        float titleWidth = totalButtonWidth + 30 * s;
        float titleHeight = buttonHeight + 20 * s;
        float titleX = centerX - titleWidth / 2;
        float titleY = changelogY + changelogHeight + 10 * s;

        BlurRenderer2.registerRenderCall(() -> {
            int titleBg = ColorUtil.multAlpha(ColorUtil.replAlpha(clientColor, 50), easedAnim);
            RenderUtil.Rounded.roundedRect(matrix, titleX, titleY, titleWidth, titleHeight, titleBg, Round.of(6 * s));
        });
        BlurRenderer2.draw(10);

        int titleBg = ColorUtil.multAlpha(ColorUtil.replAlpha(clientColor, 20), easedAnim);
        RenderUtil.Rounded.roundedRect(matrix, titleX, titleY, titleWidth, titleHeight, titleBg, Round.of(6 * s));

        int titleBorder = ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 18), easedAnim);
        RenderUtil.Rounded.roundedOutline(matrix, titleX, titleY, titleWidth, titleHeight, 0.945f * s, titleBorder, Round.of(6 * s));

        float buttonStartX = titleX + (titleWidth - totalButtonWidth) / 2;
        float buttonY = titleY + (titleHeight - buttonHeight) / 2;

        for (int i = 0; i < buttonLabels.length; i++) {
            boolean hovered = isHover(mouseX, mouseY, buttonStartX, buttonY, buttonWidths[i], buttonHeight);

            Animation buttonAnim = buttonAnimations.get(buttonTypes[i]);
            buttonAnim.run(hovered ? 1.0 : 0.0, 0.25, Easings.EXPO_OUT);

            int bgColor = ColorUtil.interpolate(
                    ColorUtil.multAlpha(ColorUtil.replAlpha(clientColor, 30), easedAnim * 0.3f),
                    ColorUtil.multAlpha(buttonColors[i], easedAnim * 0.3f),
                    buttonAnim.get()
            );

            RenderUtil.Rounded.roundedRect(matrix, buttonStartX, buttonY, buttonWidths[i], buttonHeight, bgColor, Round.of(3 * s));

            int textColor = ColorUtil.interpolate(
                    ColorUtil.multAlpha(ColorUtil.interpolateColor(clientColor, ColorUtil.WHITE, 0.5f), easedAnim),
                    ColorUtil.multAlpha(buttonColors[i], easedAnim),
                    buttonAnim.get()
            );

            Fonts.SF_BOLD.drawCenter(matrix, buttonLabels[i], buttonStartX + buttonWidths[i] / 2, buttonY + 3 * s, textColor, buttonFontSize);

            buttonStartX += buttonWidths[i] + buttonSpacing;
        }

        float mainWidth = 450 * s;
        float mainHeight = 500 * s;
        float mainX = centerX - mainWidth / 2;
        float mainY = titleY + titleHeight + 10 * s;

        BlurRenderer2.registerRenderCall(() -> {
            int mainBg = ColorUtil.multAlpha(ColorUtil.replAlpha(clientColor, 30), easedAnim);
            RenderUtil.Rounded.roundedRect(matrix, mainX, mainY, mainWidth, mainHeight, mainBg, Round.of(6 * s));
        });
        BlurRenderer2.draw(10);

        int mainBg = ColorUtil.multAlpha(ColorUtil.replAlpha(clientColor, 10), easedAnim);
        RenderUtil.Rounded.roundedRect(matrix, mainX, mainY, mainWidth, mainHeight, mainBg, Round.of(6 * s));

        int mainBorder = ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 18), easedAnim);
        RenderUtil.Rounded.roundedOutline(matrix, mainX, mainY, mainWidth, mainHeight, 0.945f * s, mainBorder, Round.of(6 * s));

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.makeScissorBox(mainX + 10 * s, mainY + 10 * s, mainX + mainWidth - 20 * s, mainY + mainHeight - 10 * s);

        float yOffset = mainY + 20 * s - scrollOffset;
        String lastCategory = "";
        totalContentHeight = 20 * s;

        for (ChangeEntry entry : entries) {
            if (!entry.category.equals(lastCategory)) {
                if (!lastCategory.isEmpty()) {
                    yOffset += 15 * s;
                    totalContentHeight += 15 * s;
                }

                String categoryName = entry.category.toUpperCase();
                float categoryFontSize = 11f * s;
                int categoryColor = ColorUtil.multAlpha(ColorUtil.interpolateColor(clientColor, ColorUtil.WHITE, 0.4f), easedAnim);

                Fonts.SF_BOLD.draw(matrix, categoryName, mainX + 20 * s, yOffset, categoryColor, categoryFontSize);
                yOffset += categoryFontSize + 8 * s;
                totalContentHeight += categoryFontSize + 8 * s;

                lastCategory = entry.category;
            }

            entry.yPosition = yOffset;

            boolean shouldHighlight = !highlightedType.isEmpty() && entry.type.equals(highlightedType);
            if (shouldHighlight) {
                float highlightAlpha = (float) highlightAnimation.getValue();
                RenderUtil.Rounded.roundedRect(matrix, mainX + 15 * s, yOffset - 2 * s, mainWidth - 30 * s, 18 * s, ColorUtil.multAlpha(clientColor, highlightAlpha * 0.15f * easedAnim), Round.of(4 * s));
            }

            String prefix = "";
            int prefixColor = -1;

            switch (entry.type) {
                case "+":
                    prefix = "[+]";
                    prefixColor = ColorUtil.getColor(0, 255, 0);
                    break;
                case "~":
                    prefix = "[~]";
                    prefixColor = ColorUtil.getColor(255, 215, 0);
                    break;
                case "-":
                    prefix = "[-]";
                    prefixColor = ColorUtil.getColor(255, 0, 0);
                    break;
                case "#":
                    prefix = "[#]";
                    prefixColor = ColorUtil.getColor(138, 43, 226);
                    break;
                case "*":
                    prefix = "[*]";
                    prefixColor = ColorUtil.interpolateColor(clientColor, ColorUtil.WHITE, 0.3f);
                    break;
            }

            float prefixX = mainX + 20 * s;
            Fonts.SF_BOLD.draw(matrix, prefix, prefixX, yOffset, ColorUtil.multAlpha(prefixColor, easedAnim), 10f * s);

            float textStartX = prefixX + 30 * s;
            float maxTextWidth = mainX + mainWidth - 30 * s - textStartX;
            int lightTextColor = ColorUtil.multAlpha(ColorUtil.interpolateColor(clientColor, ColorUtil.WHITE, 0.6f), easedAnim);

            List<String> wrappedLines = wrapText(entry.text, maxTextWidth, 9f * s);

            for (String line : wrappedLines) {
                Fonts.SF_BOLD.draw(matrix, line, textStartX, yOffset, lightTextColor, 9f * s);
                yOffset += 10 * s;
                totalContentHeight += 10 * s;
            }

            yOffset += 10 * s;
            totalContentHeight += 10 * s;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        float maxScroll = Math.max(0, totalContentHeight - (mainHeight - 30 * s));
        if (maxScroll > 0) {
            boolean scrollbarHovered = mouseX >= mainX + mainWidth - 6 * s && mouseX <= mainX + mainWidth - 2 * s;
            scrollbarAnimation.run(scrollbarHovered || draggingScroll ? 1.0 : 0.5, 0.25, Easings.EXPO_OUT);

            float scrollableHeight = mainHeight - 20 * s;
            float scrollBarHeight = Math.max(30 * s, (scrollableHeight / totalContentHeight) * scrollableHeight);
            float scrollBarY = mainY + 10 * s + (scrollOffset / maxScroll) * (scrollableHeight - scrollBarHeight);

            float alpha = scrollbarAnimation.get() * easedAnim * 0.5f;
            int scrollbarColor = ColorUtil.multAlpha(ColorUtil.interpolateColor(clientColor, ColorUtil.WHITE, 0.3f), alpha);

            RenderUtil.Rounded.roundedRect(matrix, mainX + mainWidth - 4 * s, scrollBarY, 2 * s, scrollBarHeight, scrollbarColor, Round.of(1 * s));
        }

        matrix.pop();
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float s = getResolutionScale();
        float centerX = this.width / 2.0f;
        float centerY = this.height / 2.0f;

        String changelogText = "ChangeLog";
        float changelogFontSize = 14f * s;
        float changelogWidth = Fonts.SF_BOLD.getWidth(changelogText, changelogFontSize) + 20 * s;
        float changelogHeight = changelogFontSize + 10 * s;
        float changelogY = 10 * s;

        String[] buttonLabels = {"Added [+] " + addedCount, "Fixed [~] " + fixedCount, "Removed [-] " + removedCount, "Remake [#] " + remakeCount};
        String[] buttonTypes = {"+", "~", "-", "#"};

        float totalButtonWidth = 0;
        float buttonFontSize = 9f * s;
        float buttonHeight = buttonFontSize + 6 * s;
        float buttonSpacing = 4 * s;
        float[] buttonWidths = new float[buttonLabels.length];

        for (int i = 0; i < buttonLabels.length; i++) {
            buttonWidths[i] = Fonts.SF_BOLD.getWidth(buttonLabels[i], buttonFontSize) + 10 * s;
            totalButtonWidth += buttonWidths[i];
            if (i < buttonLabels.length - 1) totalButtonWidth += buttonSpacing;
        }

        float titleWidth = totalButtonWidth + 30 * s;
        float titleHeight = buttonHeight + 20 * s;
        float titleX = centerX - titleWidth / 2;
        float titleY = changelogY + changelogHeight + 10 * s;

        float buttonStartX = titleX + (titleWidth - totalButtonWidth) / 2;
        float buttonY = titleY + (titleHeight - buttonHeight) / 2;

        for (int i = 0; i < buttonLabels.length; i++) {
            if (isHover(mouseX, mouseY, buttonStartX, buttonY, buttonWidths[i], buttonHeight)) {
                scrollToType(buttonTypes[i]);
                return true;
            }

            buttonStartX += buttonWidths[i] + buttonSpacing;
        }

        float mainWidth = 450 * s;
        float mainHeight = 500 * s;
        float mainX = centerX - mainWidth / 2;
        float mainY = titleY + titleHeight + 10 * s;

        float maxScroll = Math.max(0, totalContentHeight - (mainHeight - 30 * s));
        if (maxScroll > 0 && mouseX >= mainX + mainWidth - 6 * s && mouseX <= mainX + mainWidth - 2 * s && mouseY >= mainY + 10 * s && mouseY <= mainY + mainHeight - 10 * s) {
            draggingScroll = true;
            dragStartY = (float) mouseY;
            dragStartOffset = scrollOffset;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void scrollToType(String type) {
        highlightedType = type;
        highlightAnimation.run(1.0, 0.35, Easings.EXPO_OUT);
        highlightStartTime = System.currentTimeMillis();

        for (ChangeEntry entry : entries) {
            if (entry.type.equals(type)) {
                targetScrollOffset = entry.yPosition - 150;
                break;
            }
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingScroll = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingScroll) {
            float s = getResolutionScale();
            float mainHeight = 500 * s;
            float scrollableHeight = mainHeight - 20 * s;
            float dy = (float) mouseY - dragStartY;
            targetScrollOffset = dragStartOffset + (dy / scrollableHeight) * totalContentHeight;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        float s = getResolutionScale();
        targetScrollOffset -= delta * 20 * s;
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
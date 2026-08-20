package org.privates.client.screen.ai;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.impl.render.FriendAI;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.RenderUtil.Rounded;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FriendAIChatScreen extends Screen {
    private final Minecraft mc = Minecraft.getInstance();
    private final List<Message> messages = new ArrayList<>();
    private String input = "";
    private int cursorPos = 0;
    private final int scroll = 0;
    private boolean inputFocused = true;
    private Button sendButton;
    private Button copyButton;
    private long lastBlink = 0;
    private boolean showCursor = true;
    private boolean isRendering = false;
    private long animationStartTime = 0;
    private boolean isThinking = false;
    private float scrollAnimation = 0;
    private float targetScroll = 0;

    // Улучшенные цвета для темного дизайна с использованием правильного ColorUtil
    private static final int CHAT_WIDTH = 920;
    private static final int CHAT_HEIGHT = 700;
    private static final int MSG_PADDING = 22;
    private static final int MSG_AREA_MARGIN = 28;
    private static final int MSG_ROUND = 20;

    private static final int BG_COLOR = ColorUtil.getColor(32, 35, 42, 250);
    private static final int HEADER_COLOR = ColorUtil.getColor(255, 255, 255, 240);
    private static final int MSG_USER_BG = ColorUtil.getColor(88, 166, 255, 220);
    private static final int MSG_AI_BG = ColorUtil.getColor(48, 55, 68, 220);
    private static final int MSG_CODE_BG = ColorUtil.getColor(40, 47, 60, 240);
    private static final int MSG_USER_TEXT = ColorUtil.getColor(255, 255, 255, 255);
    private static final int MSG_AI_TEXT = ColorUtil.getColor(220, 225, 235, 255);
    private static final int MSG_CODE_TEXT = ColorUtil.getColor(150, 200, 120, 255);
    private static final int INPUT_BG = ColorUtil.getColor(48, 55, 68, 250);
    private static final int INPUT_OUTLINE = ColorUtil.getColor(88, 166, 255, 200);
    private static final int CHAT_AREA_BG = ColorUtil.getColor(40, 43, 50, 220);
    private static final int SCROLLBAR_BG = ColorUtil.getColor(55, 62, 75, 180);
    private static final int SCROLLBAR_THUMB = ColorUtil.getColor(88, 166, 255, 200);
    private static final int SHADOW_COLOR = ColorUtil.getColor(0, 0, 0, 100);

    // Паттерн для определения кода
    private static final Pattern CODE_PATTERN = Pattern.compile("```([\\s\\S]*?)```|`([^`]+)`");

    public FriendAIChatScreen() {
        super(new StringTextComponent("FriendAI Chat"));
    }

    @Override
    protected void init() {
        this.buttons.clear();
        this.children.clear();

        int w = CHAT_WIDTH, h = CHAT_HEIGHT;
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;

        sendButton = new Button(
                x + w - 130, y + h - 75, 110, 45,
                new StringTextComponent("Отправить"),
                btn -> sendMessage()
        );

        copyButton = new Button(
                x + w - 260, y + h - 75, 110, 45,
                new StringTextComponent("Копировать"),
                btn -> copyLastMessage()
        );

        this.addButton(sendButton);
        this.addButton(copyButton);
        inputFocused = true;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        if (isRendering) return;
        isRendering = true;
        try {
            renderContent(stack, mouseX, mouseY, partialTicks);
        } finally {
            isRendering = false;
        }
    }

    private void renderContent(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        int w = CHAT_WIDTH, h = CHAT_HEIGHT;
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;

        // Плавная анимация скролла с улучшенной интерполяцией
        if (Math.abs(scrollAnimation - targetScroll) > 1.0f) {
            scrollAnimation += (targetScroll - scrollAnimation) * 0.12f;
        } else {
            scrollAnimation = targetScroll;
        }

        // Основной контейнер с улучшенной тенью
        RenderUtil.Shadow.drawShadow(stack, x - 12, y - 12, w + 24, h + 24, 80, SHADOW_COLOR, Round.of(MSG_ROUND + 6));
        Rounded.roundedRect(stack, x, y, w, h, BG_COLOR, Round.of(MSG_ROUND));

        // Градиентный заголовок с улучшенными цветами
        int headerHeight = 75;
        int headerGradient1 = ColorUtil.getColor(88, 166, 255, 120);
        int headerGradient2 = ColorUtil.getColor(120, 200, 255, 80);
        Rounded.roundedRect(stack, x, y, w, headerHeight,
                ColorUtil.interpolate(headerGradient1, headerGradient2, 0.5f), Round.of(MSG_ROUND, MSG_ROUND, 0, 0));

        String title = "🤖 FriendAI Assistant";
        float titleWidth = Fonts.SF_MEDIUM.getWidth(title, 30);
        Fonts.SF_MEDIUM.draw(stack, title, x + (w - titleWidth) / 2f, y + 24, HEADER_COLOR, 30);

        // Область сообщений с улучшенным дизайном
        int msgAreaX = x + MSG_AREA_MARGIN;
        int msgAreaY = y + headerHeight + 18;
        int msgAreaW = w - MSG_AREA_MARGIN * 2;
        int msgAreaH = h - headerHeight - 140;

        // Фон области сообщений с градиентом
        Rounded.roundedRect(stack, msgAreaX, msgAreaY, msgAreaW, msgAreaH, CHAT_AREA_BG, Round.of(MSG_ROUND));

        // Анимированная обводка
        float time = System.currentTimeMillis() * 0.002f;
        int outlineAlpha = (int) (Math.sin(time) * 20 + 80);
        int animatedOutline = ColorUtil.getColor(88, 166, 255, outlineAlpha);
        Rounded.roundedOutline(stack, msgAreaX, msgAreaY, msgAreaW, msgAreaH, 2.5f, animatedOutline, Round.of(MSG_ROUND));

        // Рендер сообщений с улучшенным скроллингом
        scissorBox(msgAreaX, msgAreaY, msgAreaW, msgAreaH);

        int msgY = msgAreaY + MSG_PADDING - (int) scrollAnimation;

        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            msgY = renderMessage(stack, msg, msgAreaX, msgY, msgAreaW, msgAreaY, msgAreaH, i == messages.size() - 1);
        }

        endScissor();

        // Улучшенный скроллбар
        renderScrollbar(stack, msgAreaX, msgAreaY, msgAreaW, msgAreaH);

        // Улучшенное поле ввода
        renderInputField(stack, x, y, w, h);

        // Кнопки
        if (sendButton != null) sendButton.render(stack, mouseX, mouseY, partialTicks);
        if (copyButton != null) copyButton.render(stack, mouseX, mouseY, partialTicks);
    }

    private int renderMessage(MatrixStack stack, Message msg, int msgAreaX, int msgY, int msgAreaW, int msgAreaY, int msgAreaH, boolean isLast) {
        boolean isUser = msg.role.equals("user");
        boolean isThinking = msg.content.equals("Думаю...");

        int msgBG = isUser ? MSG_USER_BG : MSG_AI_BG;
        int msgText = isUser ? MSG_USER_TEXT : MSG_AI_TEXT;
        int msgMaxWidth = msgAreaW - 160;
        int msgX = isUser ? (msgAreaX + msgAreaW - msgMaxWidth - 30) : (msgAreaX + 30);

        // Разделение на обычный текст и код
        List<MessagePart> parts = parseMessageParts(msg.content);
        int totalHeight = calculateMessageHeight(parts, msgMaxWidth - MSG_PADDING * 2);

        if (msgY + totalHeight < msgAreaY - 50) {
            return msgY + totalHeight + 20;
        }
        if (msgY > msgAreaY + msgAreaH + 50) return msgY;

        // Анимация появления сообщения
        float alpha = 1.0f;
        if (isLast && System.currentTimeMillis() - animationStartTime < 300) {
            alpha = (System.currentTimeMillis() - animationStartTime) / 300f;
        }

        // Анимированный фон сообщения
        int animatedBG = ColorUtil.getColor(
                ColorUtil.red(msgBG),
                ColorUtil.green(msgBG),
                ColorUtil.blue(msgBG),
                (int) (ColorUtil.alpha(msgBG) * alpha)
        );

        Rounded.roundedRect(stack, msgX, msgY, msgMaxWidth, totalHeight, animatedBG, Round.of(15));

        // Тень для сообщения
        int shadowColor = ColorUtil.getColor(0, 0, 0, (int) (40 * alpha));
        RenderUtil.Shadow.drawShadow(stack, msgX - 2, msgY - 2, msgMaxWidth + 4, totalHeight + 4,
                20, shadowColor, Round.of(15));

        // Рендер содержимого
        int currentY = msgY + MSG_PADDING;

        if (isThinking) {
            currentY = renderThinkingAnimation(stack, msgX + MSG_PADDING, currentY, alpha);
        } else {
            for (MessagePart part : parts) {
                currentY = renderMessagePart(stack, part, msgX + MSG_PADDING, currentY,
                        msgMaxWidth - MSG_PADDING * 2, alpha);
            }
        }

        return msgY + totalHeight + 20;
    }

    private int renderThinkingAnimation(MatrixStack stack, int x, int y, float alpha) {
        long time = System.currentTimeMillis();

        // Анимированные точки
        String baseText = "Думаю";
        Fonts.SF_MEDIUM.draw(stack, baseText, x, y,
                ColorUtil.getColor(220, 225, 235, (int) (255 * alpha)), 19);

        int baseWidth = (int) Fonts.SF_MEDIUM.getWidth(baseText, 19);

        // Анимированные точки
        for (int i = 0; i < 3; i++) {
            float dotAlpha = (float) Math.sin((time + i * 200) * 0.01) * 0.5f + 0.5f;
            Fonts.SF_MEDIUM.draw(stack, ".", x + baseWidth + i * 8, y,
                    ColorUtil.getColor(220, 225, 235, (int) (255 * alpha * dotAlpha)), 19);
        }

        // Вращающийся индикатор
        float angle = (float) ((time * 0.01f) % (2 * Math.PI));
        int centerX = x + baseWidth + 40;
        int centerY = y + 10;

        stack.push();
        stack.translate(centerX, centerY, 0);
        stack.rotate(net.minecraft.util.math.vector.Vector3f.ZP.rotation(angle));

        // Рисуем простой спиннер
        for (int i = 0; i < 8; i++) {
            float lineAlpha = (float) (Math.sin(angle + i * Math.PI / 4) * 0.3f + 0.7f);
            int lineX = (int) (Math.cos(i * Math.PI / 4) * 8);
            int lineY = (int) (Math.sin(i * Math.PI / 4) * 8);

            int lineColor = ColorUtil.getColor(88, 166, 255, (int) (255 * alpha * lineAlpha));
            RenderUtil.drawRect(stack, lineX - 1, lineY - 1, 2, 2, lineColor, lineColor, lineColor, lineColor, false, true);
        }

        stack.pop();

        return y + 25;
    }

    private List<MessagePart> parseMessageParts(String content) {
        List<MessagePart> parts = new ArrayList<>();
        java.util.regex.Matcher matcher = CODE_PATTERN.matcher(content);

        int lastEnd = 0;
        while (matcher.find()) {
            // Добавляем текст до кода
            if (matcher.start() > lastEnd) {
                String textPart = content.substring(lastEnd, matcher.start());
                if (!textPart.trim().isEmpty()) {
                    parts.add(new MessagePart(textPart, false));
                }
            }

            // Добавляем код
            String codePart = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            parts.add(new MessagePart(codePart, true));
            lastEnd = matcher.end();
        }

        // Добавляем оставшийся текст
        if (lastEnd < content.length()) {
            String textPart = content.substring(lastEnd);
            if (!textPart.trim().isEmpty()) {
                parts.add(new MessagePart(textPart, false));
            }
        }

        // Если нет кода, добавляем весь текст как обычный
        if (parts.isEmpty()) {
            parts.add(new MessagePart(content, false));
        }

        return parts;
    }

    private int calculateMessageHeight(List<MessagePart> parts, int maxWidth) {
        int height = MSG_PADDING * 2;
        for (MessagePart part : parts) {
            List<String> lines = wrapText(part.content, maxWidth, 19);
            height += lines.size() * 22;
            if (part.isCode) {
                height += 20; // Дополнительное пространство для кода
            }
        }
        return height;
    }

    private int renderMessagePart(MatrixStack stack, MessagePart part, int x, int y, int maxWidth, float alpha) {
        List<String> lines = wrapText(part.content, maxWidth, 19);

        if (part.isCode) {
            // Фон для кода
            int codeHeight = lines.size() * 22 + 16;
            int codeBgColor = ColorUtil.getColor(
                    ColorUtil.red(MSG_CODE_BG),
                    ColorUtil.green(MSG_CODE_BG),
                    ColorUtil.blue(MSG_CODE_BG),
                    (int) (ColorUtil.alpha(MSG_CODE_BG) * alpha)
            );
            Rounded.roundedRect(stack, x - 8, y - 8, maxWidth + 16, codeHeight, codeBgColor, Round.of(8));

            // Рендер строк кода
            for (String line : lines) {
                int codeTextColor = ColorUtil.getColor(
                        ColorUtil.red(MSG_CODE_TEXT),
                        ColorUtil.green(MSG_CODE_TEXT),
                        ColorUtil.blue(MSG_CODE_TEXT),
                        (int) (255 * alpha)
                );
                Fonts.SF_MEDIUM.draw(stack, line, x, y, codeTextColor, 19);
                y += 22;
            }
            y += 8;
        } else {
            // Обычный текст
            for (String line : lines) {
                int textColor = ColorUtil.getColor(
                        ColorUtil.red(MSG_AI_TEXT),
                        ColorUtil.green(MSG_AI_TEXT),
                        ColorUtil.blue(MSG_AI_TEXT),
                        (int) (255 * alpha)
                );
                Fonts.SF_MEDIUM.draw(stack, line, x, y, textColor, 19);
                y += 22;
            }
        }

        return y;
    }

    private void renderScrollbar(MatrixStack stack, int msgAreaX, int msgAreaY, int msgAreaW, int msgAreaH) {
        int contentHeight = getMessagesTotalHeight(msgAreaW - 160);
        if (contentHeight > msgAreaH) {
            int scrollBarW = 8;
            int scrollBarH = Math.max(20, (int) ((float) msgAreaH * msgAreaH / contentHeight));
            int scrollBarX = msgAreaX + msgAreaW - scrollBarW - 8;

            // Правильно вычисляем позицию скроллбара
            int maxScroll = Math.max(1, contentHeight - msgAreaH + 50);
            float scrollPercent = Math.max(0, Math.min(1, scrollAnimation / maxScroll));
            int scrollBarY = msgAreaY + (int) (scrollPercent * (msgAreaH - scrollBarH));

            // Фон скроллбара
            Rounded.roundedRect(stack, scrollBarX, msgAreaY, scrollBarW, msgAreaH,
                    SCROLLBAR_BG, Round.of(4));

            // Ползунок скроллбара с анимацией
            Rounded.roundedRect(stack, scrollBarX, scrollBarY, scrollBarW, scrollBarH,
                    ColorUtil.interpolate(SCROLLBAR_THUMB, ColorUtil.getColor(120, 200, 255),
                            (float) Math.sin(System.currentTimeMillis() * 0.002) * 0.1f + 0.1f), Round.of(4));
        }
    }

    private void renderInputField(MatrixStack stack, int x, int y, int w, int h) {
        int inputX = x + MSG_AREA_MARGIN;
        int inputY = y + h - 110;
        int inputW = w - MSG_AREA_MARGIN * 2 - 280;
        int inputH = 55;

        // Фон поля ввода с градиентом
        Rounded.roundedRect(stack, inputX, inputY, inputW, inputH, INPUT_BG, Round.of(15));

        // Анимированная обводка при фокусе
        if (inputFocused) {
            float time = System.currentTimeMillis() * 0.003f;
            int glowAlpha = (int) (Math.sin(time) * 30 + 150);
            int focusColor = ColorUtil.getColor(88, 166, 255, glowAlpha);
            Rounded.roundedOutline(stack, inputX, inputY, inputW, inputH, 2f, focusColor, Round.of(15));
        }

        // Многострочный текст
        List<String> inputLines = wrapText(input, inputW - 20, 19);
        int maxLines = Math.min(inputLines.size(), 2); // Ограничиваем 2 строками

        int lineY = inputY + (inputH - maxLines * 22) / 2;
        for (int i = 0; i < maxLines; i++) {
            String line = inputLines.get(i);
            if (i == maxLines - 1 && inputLines.size() > maxLines) {
                line = line.substring(0, Math.min(line.length(), line.length() - 3)) + "...";
            }
            Fonts.SF_MEDIUM.draw(stack, line, inputX + 12, lineY,
                    ColorUtil.getColor(255, 255, 255, 230), 19);
            lineY += 22;
        }

        // Улучшенный курсор
        if (inputFocused && showCursor) {
            int curLine = Math.min(getCursorLine(input, inputW - 20, 19, cursorPos), maxLines - 1);
            String lineText = getLineText(input, inputW - 20, 19, cursorPos);
            int cx = inputX + 12 + (int) Fonts.SF_MEDIUM.getWidth(lineText, 19);
            int cy = inputY + (inputH - maxLines * 22) / 2 + 22 * curLine;

            // Анимированный курсор
            float cursorAlpha = (float) Math.sin(System.currentTimeMillis() * 0.01) * 0.3f + 0.7f;
            int cursorColor = ColorUtil.getColor(88, 166, 255, (int) (255 * cursorAlpha));
            RenderUtil.drawRect(stack, cx, cy, 2, 20, cursorColor, cursorColor, cursorColor, cursorColor, false, true);
        }

        // Плейсхолдер
        if (input.isEmpty()) {
            Fonts.SF_MEDIUM.draw(stack, "Введите сообщение...", inputX + 12, inputY + 18,
                    ColorUtil.getColor(150, 160, 180, 150), 19);
        }
    }

    private void copyLastMessage() {
        if (messages.isEmpty()) return;

        // Находим последнее сообщение от AI
        Message lastAiMessage = null;
        for (int i = messages.size() - 1; i >= 0; i--) {
            if (messages.get(i).role.equals("ai") && !messages.get(i).content.equals("Думаю...")) {
                lastAiMessage = messages.get(i);
                break;
            }
        }

        if (lastAiMessage != null) {
            copyToClipboard(lastAiMessage.content);
        }
    }

    private void copyToClipboard(String text) {
        try {
            StringSelection selection = new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getMessagesTotalHeight(int msgMaxWidth) {
        int total = 0;
        for (Message msg : messages) {
            List<MessagePart> parts = parseMessageParts(msg.content);
            total += calculateMessageHeight(parts, msgMaxWidth - MSG_PADDING * 2) + 20;
        }
        return total;
    }

    private void sendMessage() {
        if (input.trim().isEmpty()) return;

        messages.add(new Message("user", input.trim()));
        String userMsg = input.trim();
        input = "";
        cursorPos = 0;

        // Добавляем сообщение "Думаю..." и прокручиваем к нему
        messages.add(new Message("ai", "Думаю..."));
        isThinking = true;
        animationStartTime = System.currentTimeMillis();

        // Вычисляем правильную прокрутку к последнему сообщению
        int contentHeight = getMessagesTotalHeight(CHAT_WIDTH - MSG_AREA_MARGIN * 2 - 160);
        int msgAreaHeight = CHAT_HEIGHT - 75 - 140; // высота области сообщений

        if (contentHeight > msgAreaHeight) {
            targetScroll = contentHeight - msgAreaHeight + 50; // +50 для отступа снизу
        } else {
            targetScroll = 0;
        }

        new Thread(() -> {
            try {
                String aiReply = FriendAI.askGemini(userMsg);
                if (mc != null) {
                    mc.execute(() -> {
                        isThinking = false;
                        if (!messages.isEmpty() && messages.get(messages.size() - 1).content.equals("Думаю...")) {
                            messages.remove(messages.size() - 1);
                        }
                        messages.add(new Message("ai", aiReply));
                        animationStartTime = System.currentTimeMillis();

                        // Обновляем прокрутку для нового сообщения
                        int newContentHeight = getMessagesTotalHeight(CHAT_WIDTH - MSG_AREA_MARGIN * 2 - 160);
                        if (newContentHeight > msgAreaHeight) {
                            targetScroll = newContentHeight - msgAreaHeight + 50;
                        } else {
                            targetScroll = 0;
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (mc != null) {
                    mc.execute(() -> {
                        isThinking = false;
                        if (!messages.isEmpty() && messages.get(messages.size() - 1).content.equals("Думаю...")) {
                            messages.remove(messages.size() - 1);
                        }
                        messages.add(new Message("ai", "[Ошибка: " + e.getMessage() + "]"));
                        animationStartTime = System.currentTimeMillis();

                        // Обновляем прокрутку для сообщения об ошибке
                        int newContentHeight = getMessagesTotalHeight(CHAT_WIDTH - MSG_AREA_MARGIN * 2 - 160);
                        if (newContentHeight > msgAreaHeight) {
                            targetScroll = newContentHeight - msgAreaHeight + 50;
                        } else {
                            targetScroll = 0;
                        }
                    });
                }
            }
        }).start();
    }

    // Остальные методы остаются без изменений...
    private List<String> wrapText(String text, int maxWidth, int fontSize) {
        List<String> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        String[] words = text.split(" ");

        for (String word : words) {
            String test = sb.length() == 0 ? word : sb + " " + word;
            if (Fonts.SF_MEDIUM.getWidth(test, fontSize) > maxWidth) {
                if (sb.length() > 0) {
                    lines.add(sb.toString());
                    sb = new StringBuilder();
                }

                // Разбиваем длинные слова
                while (Fonts.SF_MEDIUM.getWidth(word, fontSize) > maxWidth) {
                    int cut = 1;
                    while (cut < word.length() && Fonts.SF_MEDIUM.getWidth(word.substring(0, cut), fontSize) <= maxWidth) {
                        cut++;
                    }
                    if (cut > 1) {
                        lines.add(word.substring(0, cut - 1));
                        word = word.substring(cut - 1);
                    } else {
                        break;
                    }
                }
                sb.append(word);
            } else {
                if (sb.length() > 0) sb.append(" ");
                sb.append(word);
            }
        }
        if (sb.length() > 0) lines.add(sb.toString());
        return lines;
    }

    private int getCursorLine(String text, int maxWidth, int fontSize, int cursorPos) {
        List<String> lines = wrapText(text, maxWidth, fontSize);
        int currentPos = 0;

        for (int i = 0; i < lines.size(); i++) {
            currentPos += lines.get(i).length();
            if (i < lines.size() - 1) currentPos++; // Пробел между словами
            if (currentPos >= cursorPos) {
                return i;
            }
        }
        return Math.max(0, lines.size() - 1);
    }

    private String getLineText(String text, int maxWidth, int fontSize, int cursorPos) {
        List<String> lines = wrapText(text, maxWidth, fontSize);
        int currentPos = 0;

        for (String line : lines) {
            if (currentPos + line.length() >= cursorPos) {
                return line.substring(0, Math.min(line.length(), cursorPos - currentPos));
            }
            currentPos += line.length() + 1; // +1 для пробела
        }
        return "";
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int w = CHAT_WIDTH, h = CHAT_HEIGHT;
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;
        int msgAreaY = y + 93;
        int msgAreaH = h - 233;

        if (mouseX >= x + MSG_AREA_MARGIN && mouseX <= x + w - MSG_AREA_MARGIN &&
                mouseY >= msgAreaY && mouseY <= msgAreaY + msgAreaH) {

            int contentHeight = getMessagesTotalHeight(w - MSG_AREA_MARGIN * 2 - 160);
            if (contentHeight > msgAreaH) {
                int maxScroll = Math.max(0, contentHeight - msgAreaH + 50);
                if (delta > 0) { // прокрутка вверх
                    targetScroll = Math.max(0, targetScroll - 45);
                } else { // прокрутка вниз
                    targetScroll = Math.min(maxScroll, targetScroll + 45);
                }
            } else {
                targetScroll = 0; // если контент помещается, сбрасываем скролл
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!inputFocused) return false;
        if (codePoint == 13 || codePoint == 10) return false;
        input = input.substring(0, cursorPos) + codePoint + input.substring(cursorPos);
        cursorPos++;
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!inputFocused) return super.keyPressed(keyCode, scanCode, modifiers);

        switch (keyCode) {
            case 257:
            case 335: // Enter
                sendMessage();
                return true;
            case 259: // Backspace
                if (cursorPos > 0) {
                    input = input.substring(0, cursorPos - 1) + input.substring(cursorPos);
                    cursorPos--;
                }
                return true;
            case 261: // Delete
                if (cursorPos < input.length()) {
                    input = input.substring(0, cursorPos) + input.substring(cursorPos + 1);
                }
                return true;
            case 262: // Right
                if (cursorPos < input.length()) cursorPos++;
                return true;
            case 263: // Left
                if (cursorPos > 0) cursorPos--;
                return true;
            case 268: // Home
                cursorPos = 0;
                return true;
            case 269: // End
                cursorPos = input.length();
                return true;
            case 67: // C
                if ((modifiers & 2) != 0) { // Ctrl
                    copyInput();
                return true;
                }
                break;
            case 86: // V
                if ((modifiers & 2) != 0) { // Ctrl
                    pasteInput();
                return true;
                }
                break;
            case 88: // X
                if ((modifiers & 2) != 0) { // Ctrl
                    cutInput();
                return true;
                }
                break;
            case 256: // ESC
                this.minecraft.displayScreen(null);
                return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int w = CHAT_WIDTH, h = CHAT_HEIGHT;
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;
        int inputX = x + MSG_AREA_MARGIN;
        int inputY = y + h - 110;
        int inputW = w - MSG_AREA_MARGIN * 2 - 280;
        int inputH = 55;

        if (mouseX >= inputX && mouseX <= inputX + inputW &&
                mouseY >= inputY && mouseY <= inputY + inputH) {
            inputFocused = true;
            cursorPos = input.length();
            return true;
        } else {
            inputFocused = false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void copyInput() {
        if (input.isEmpty()) return;
        copyToClipboard(input);
    }

    private void pasteInput() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            String data = (String) clipboard.getData(DataFlavor.stringFlavor);
            if (data != null && !data.isEmpty()) {
                data = data.replace("\n", " ").replace("\r", "");
                input = input.substring(0, cursorPos) + data + input.substring(cursorPos);
                cursorPos += data.length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cutInput() {
        if (input.isEmpty()) return;
        copyInput();
        input = "";
        cursorPos = 0;
    }

    @Override
    public void tick() {
        if (System.currentTimeMillis() - lastBlink > 500) {
            showCursor = !showCursor;
            lastBlink = System.currentTimeMillis();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void scissorBox(int x, int y, int width, int height) {
        try {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            double scale = mc.getMainWindow().getScaleFactor();
            GL11.glScissor((int) (x * scale),
                    (int) (mc.getMainWindow().getFramebufferHeight() - (y + height) * scale),
                    (int) (width * scale),
                    (int) (height * scale));
        } catch (Exception e) {
            // Fallback если GL недоступен
        }
    }

    private void endScissor() {
        try {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } catch (Exception e) {
            // Fallback если GL недоступен
        }
    }

    // Класс для частей сообщения (текст/код)
    private static class MessagePart {
        public final String content;
        public final boolean isCode;

        public MessagePart(String content, boolean isCode) {
            this.content = content;
            this.isCode = isCode;
        }
    }

    // Класс сообщения
    private static class Message {
        public final String role;
        public final String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
} 
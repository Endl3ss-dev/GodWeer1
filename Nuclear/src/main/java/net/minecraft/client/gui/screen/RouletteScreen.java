package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.awt.*;
import java.util.Random;

import nuclear.utils.IMinecraft;
import nuclear.utils.render.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import nuclear.utils.font.Fonts;
import org.joml.Vector4i;

import static nuclear.ui.clickgui.Panel.getColorByName;
import static nuclear.utils.IMinecraft.mc;
import static net.minecraft.util.math.MathHelper.lerp;

public class RouletteScreen extends Screen {
    private final Screen parent;
    private float slotPosition = 0f;
    private float slotStartPosition = 0f;
    private float slotTargetPosition = 0f;
    private boolean isSpinning = false;
    private int selectedPrize = -1;
    private long spinStartTime = 0;
    private static final long SPIN_DURATION = 3000;
    private static final int PRIZE_COUNT = 5;
    private static final float SLOT_WIDTH = 60f;
    private static final float SLOT_SPACING = -8f; // Расстояние между квадратиками // Уменьшено в 2 раза
    
    private static final String[] PRIZES = {
        "Ничего",
        "Ничего",
        "Ничего",
        "Ничего",
        "Ничего"
    };
    
    private static final int[] PRIZE_COLORS = {
        new Color(186, 186, 186, 122).getRGB(),
        new Color(186, 186, 186, 122).getRGB(),
        new Color(186, 186, 186, 122).getRGB(),
        new Color(186, 186, 186, 122).getRGB(),
        new Color(255, 100, 100).getRGB()
    };

    public RouletteScreen(Screen parent) {
        super(new TranslationTextComponent("roulette.title"));
        this.parent = parent;
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        
        int buttonWidth = (int) ((int) (353 / 2f));
        int buttonHeight = (int) ((int) (68 / 2f));
        int centerX = width / 2;
        int centerY = height / 2;
        
        // Вычисляем параметры слота для позиционирования кнопок
        float slotHeight = 60f;
        float prizeSpacing = SLOT_WIDTH + SLOT_SPACING;
        float visibleWidth = (SLOT_WIDTH * 7) + (SLOT_SPACING * 6);
        float slotX = centerX - visibleWidth / 2f;
        float slotY = centerY - 60f; // Сдвинуто на 25 пикселей вниз
        
        // Нижняя граница прямоугольника слота
        float rectBottom = slotY - 40 + slotHeight + 58;
        // Позиция кнопок под прямоугольником с отступом 5 пикселей
        int buttonY = (int) (rectBottom + 3);
        
        // Позиция кнопок: слева и справа от центра прямоугольника
        float rectLeft = slotX + 20;
        float rectCenter = rectLeft + (visibleWidth - 40) / 2f;
        
        // Кнопка "Крутить" слева
        int spinButtonWidth = buttonWidth - 50 - 14 - 24 + 11;
        int spinButtonX = (int) (rectCenter - spinButtonWidth - 1.5f); // Слева от центра с отступом
        
        // Кнопка "Назад" справа
        int backButtonX = (int) (rectCenter + 1.5f); // Справа от центра с отступом
        
        this.addButton(new Button(
            spinButtonX,
            buttonY,
            spinButtonWidth,
            buttonHeight - 4 - 10 - 2 + 2,
            new StringTextComponent("Крутить"),
            button -> {
                if (!isSpinning) {
                    startSpin();
                }
            },
            's'
        ));
        
        this.addButton(new Button(
            backButtonX,
            buttonY,
            spinButtonWidth,
            buttonHeight - 4 - 10 - 2 + 2,
            new StringTextComponent("Назад"),
            button -> {
                mc.displayGuiScreen(parent);
            },
            'b'
        ));
    }

    private void startSpin() {
        isSpinning = true;
        spinStartTime = System.currentTimeMillis();
        selectedPrize = -1;
        slotStartPosition = slotPosition;
        
        Random random = new Random();
        // Выбираем случайный приз
        int prizeIndex = random.nextInt(PRIZE_COUNT);
        
        // Вычисляем целевую позицию так, чтобы центр приза был точно в центре видимой области
        float prizeSpacing = SLOT_WIDTH + SLOT_SPACING; // Ширина приза + расстояние
        float extraSpins = 5 * PRIZE_COUNT * prizeSpacing; // Несколько полных прокруток
        
        // Нормализуем текущую позицию
        float totalCycleWidth = PRIZE_COUNT * prizeSpacing;
        float normalizedStart = slotStartPosition % totalCycleWidth;
        if (normalizedStart < 0) normalizedStart += totalCycleWidth;
        
        // Вычисляем позицию центра текущего приза в системе координат прокрутки
        int currentPrizeIndex = (int) (normalizedStart / prizeSpacing) % PRIZE_COUNT;
        float currentPrizeCenter = currentPrizeIndex * prizeSpacing + SLOT_WIDTH / 2f;
        
        // Вычисляем позицию центра целевого приза
        float targetPrizeCenter = prizeIndex * prizeSpacing + SLOT_WIDTH / 2f;
        
        // Вычисляем смещение для выравнивания по центру
        // Нужно чтобы центр целевого приза совпал с центром видимой области
        float offset = targetPrizeCenter - currentPrizeCenter;
        if (offset < 0) offset += totalCycleWidth;
        
        slotTargetPosition = slotStartPosition + extraSpins + offset;
        
        // Финальная корректировка для точного выравнивания по центру
        float finalNormalized = slotTargetPosition % totalCycleWidth;
        if (finalNormalized < 0) finalNormalized += totalCycleWidth;
        
        // Выравниваем так, чтобы остаток был SLOT_WIDTH/2 (центр приза в центре видимой области)
        float remainder = finalNormalized % prizeSpacing;
        float targetRemainder = SLOT_WIDTH / 2f;
        float adjustment = targetRemainder - remainder;
        slotTargetPosition += adjustment;
    }

    @Override
    public void tick() {
        if (isSpinning) {
            long elapsed = System.currentTimeMillis() - spinStartTime;
            float progress = Math.min(elapsed / (float) SPIN_DURATION, 1f);
            
            // Easing функция для плавного замедления
            float easedProgress = 1f - (float) Math.pow(1f - progress, 3);
            
            slotPosition = lerp(slotStartPosition, slotTargetPosition, easedProgress);
            
            // Нормализуем позицию для бесконечной прокрутки
            float prizeSpacing = SLOT_WIDTH + SLOT_SPACING;
            float totalCycleWidth = PRIZE_COUNT * prizeSpacing;
            while (slotPosition < 0) {
                slotPosition += totalCycleWidth;
            }
            while (slotPosition >= totalCycleWidth) {
                slotPosition -= totalCycleWidth;
            }
            
            if (progress >= 1f) {
                isSpinning = false;
                slotPosition = slotTargetPosition;
                
                // Нормализуем позицию
                float normalizedPos = slotPosition % totalCycleWidth;
                if (normalizedPos < 0) normalizedPos += totalCycleWidth;
                
                // Определяем индекс приза
                int prizeIndex = (int) (normalizedPos / prizeSpacing) % PRIZE_COUNT;
                selectedPrize = prizeIndex;
                
                // Применяем приз
                applyPrize(prizeIndex);
            }
        }
    }

    private void applyPrize(int prizeIndex) {
        // Логика применения приза
        String prizeName = PRIZES[prizeIndex];
        // TODO: Реализовать выдачу призов
        System.out.println("Вы выиграли: " + prizeName);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vec2i fixed = ScaleMath.getMouse((int) mouseX, (int) mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        mc.gameRenderer.setupOverlayRendering(2);
        int windowWidth = mc.getMainWindow().getScaledWidth();
        int windowHeight = mc.getMainWindow().getScaledHeight();
        int renderWidth = (int) (windowWidth * 1.05f);
        int renderHeight = (int) (windowHeight * 1.05f);

        float normMouseX = (mouseX / (float) windowWidth) * 2 - 1;
        float normMouseY = (mouseY / (float) windowHeight) * 2 - 1;

        float maxOffsetX = (renderWidth - windowWidth) / 2.0f;
        float maxOffsetY = (renderHeight - windowHeight) / 2.0f;

        float offsetX = MathHelper.clamp(normMouseX * 4, -maxOffsetX, maxOffsetX);
        float offsetY = MathHelper.clamp(normMouseY * 4, -maxOffsetY, maxOffsetY);
        RenderUtilka.Render2D.drawImage(new ResourceLocation("Nuclear/images/background.png"), offsetX - (renderWidth - windowWidth) / 2.0f, offsetY - (renderHeight - windowHeight) / 2.0f, renderWidth, renderHeight, -1);

        int centerX = width / 2;
        int centerY = height / 2;
        
        // Рисуем слоты
        renderSlots(matrixStack, centerX, centerY);
        
        // Рисуем текст с результатом
        int textColor = getColorByName("textColor");
        
        if (selectedPrize >= 0 && !isSpinning) {
            // Рисуем фон для текста результата
            String prizeText = "Вы выиграли: " + PRIZES[selectedPrize];

            Fonts.intl[16].drawCenteredString(matrixStack, prizeText, centerX, centerY - 70, textColor);
        } else if (!isSpinning) {
            Fonts.intl[14].drawCenteredString(matrixStack, "Нажмите 'Крутить' для запуска", centerX, centerY - 70, textColor);
        } else {
            Fonts.intl[14].drawCenteredString(matrixStack, "Крутится...", centerX, centerY - 70, textColor);
        }
        
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        mc.gameRenderer.setupOverlayRendering();
    }

    private void renderSlots(MatrixStack matrixStack, int centerX, int centerY) {
        int iconnoColor = getColorByName("iconnoColor");
        int iconColor = getColorByName("iconColor");
        float slotHeight = 60f;
        // Видимая ширина = 7 квадратиков + 6 промежутков по 3 пикселя
        float prizeSpacing = SLOT_WIDTH + SLOT_SPACING;
        float visibleWidth = (SLOT_WIDTH * 7) + (SLOT_SPACING * 6);
        float slotX = centerX - visibleWidth / 2f;
        float slotY = centerY - 55f; // Сдвинуто на 25 пикселей вниз
        
        GaussianBlur.startBlur();
        RenderUtilka.Render2D.drawRoundedRect(slotX + 40, slotY - 25, visibleWidth - 80, slotHeight + 38, 10f, ColorUtils.rgba(255, 255, 255, 5));
        GaussianBlur.endBlur(6, 3);

        RenderUtilka.Render2D.drawRoundedRect(slotX + 40, slotY - 25, visibleWidth - 80, slotHeight + 38, 10f, ColorUtils.setAlpha(iconnoColor, 45));
        RenderUtilka.Render2D.drawRoundOutline(slotX + 40, slotY - 25, visibleWidth - 80, slotHeight + 38, 10f, -0.9f, ColorUtils.rgba(25, 26, 33, 0), new Vector4i(
            ColorUtils.setAlpha(iconnoColor, 50),
            ColorUtils.setAlpha(iconnoColor, 50),
            ColorUtils.setAlpha(iconnoColor, 50),
            ColorUtils.setAlpha(iconnoColor, 50)
        ));

        SmartScissor.push();
        SmartScissor.setFromComponentCoordinates(
                slotX + 40, slotY, visibleWidth - 80, slotHeight);
        // Нормализуем позицию
        prizeSpacing = SLOT_WIDTH + SLOT_SPACING;
        float totalCycleWidth = PRIZE_COUNT * prizeSpacing;
        float currentPos = slotPosition;
        while (currentPos < 0) {
            currentPos += totalCycleWidth;
        }
        while (currentPos >= totalCycleWidth) {
            currentPos -= totalCycleWidth;
        }
        
        // Рисуем видимые призы (горизонтальная прокрутка слева направо) - видно 7 призов
        int visiblePrizes = 7;
        for (int i = -3; i < visiblePrizes + 1; i++) {
            // Вычисляем позицию приза с учетом расстояния между ними
            float prizeX = slotX - (currentPos % prizeSpacing) + (i * prizeSpacing);
            
            // Пропускаем призы, которые не видны
            if (prizeX + SLOT_WIDTH < slotX || prizeX > slotX + visibleWidth) {
                continue;
            }
            
            // Определяем индекс приза
            int prizeIndex = ((int) (currentPos / prizeSpacing) + i + PRIZE_COUNT * 10) % PRIZE_COUNT;
            
            // Вычисляем расстояние от центра для эффекта масштабирования
            float centerPrizeX = slotX + visibleWidth / 2f;
            float distanceFromCenter = Math.abs(prizeX + SLOT_WIDTH / 2f - centerPrizeX);
            float maxDistance = visibleWidth / 2f + SLOT_WIDTH;
            float scale = 1f - (distanceFromCenter / maxDistance) * 0.3f;
            scale = Math.max(0.7f, Math.min(1f, scale));
            
            float alphaMultiplier = 1f - (distanceFromCenter / maxDistance) * 0.5f;
            alphaMultiplier = Math.max(0.5f, Math.min(1f, alphaMultiplier));
            
            int prizeColor = PRIZE_COLORS[prizeIndex];
            GaussianBlur.startBlur();
            RenderUtilka.Render2D.drawRoundedRect(prizeX + 5, slotY + 5, SLOT_WIDTH - 10, slotHeight - 10, 6f, ColorUtils.setAlpha(prizeColor, (int)(100 * alphaMultiplier)));
            GaussianBlur.endBlur(6, 3);

            RenderUtilka.Render2D.drawRoundedRect(prizeX + 5, slotY + 5, SLOT_WIDTH - 10, slotHeight - 10, 6f, ColorUtils.setAlpha(prizeColor, (int)(120 * alphaMultiplier)));
            
            RenderUtilka.Render2D.drawRoundOutline(prizeX + 5, slotY + 5, SLOT_WIDTH - 10, slotHeight - 10, 6f, -0.9f, ColorUtils.rgba(25, 26, 33, 0), new Vector4i(
                ColorUtils.setAlpha(prizeColor, (int)(220 * alphaMultiplier)),
                ColorUtils.setAlpha(prizeColor, (int)(220 * alphaMultiplier)),
                ColorUtils.setAlpha(prizeColor, (int)(220 * alphaMultiplier)),
                ColorUtils.setAlpha(prizeColor, (int)(220 * alphaMultiplier))
            ));
            
            String prizeText = PRIZES[prizeIndex];
            int whiteColor = new Color(255, 255, 255).getRGB();
            
            if (Fonts.intl[12].getWidth(prizeText) > SLOT_WIDTH - 20) {
                prizeText = prizeText.substring(0, Math.min(10, prizeText.length())) + "...";
            }
            
            Fonts.intl[12].drawCenteredString(matrixStack, prizeText, prizeX + SLOT_WIDTH / 2, slotY + slotHeight / 2 - 1, ColorUtils.setAlpha(whiteColor, (int)(255 * alphaMultiplier)));
        }
        SmartScissor.unset();
        SmartScissor.pop();
        RenderUtilka.Render2D.drawRect(slotX + visibleWidth / 2f - 1, slotY, 2, slotHeight, ColorUtils.setAlpha(iconColor, 150));

    }

    public static class Button extends AbstractButton {

        public static final net.minecraft.client.gui.widget.button.Button.ITooltip field_238486_s_ = (button, matrixStack, mouseX, mouseY) ->
        {
        };
        protected final net.minecraft.client.gui.widget.button.Button.IPressable onPress;
        protected final net.minecraft.client.gui.widget.button.Button.ITooltip onTooltip;
        private char iconChar;

        public Button(int x, int y, int width, int height, ITextComponent title, net.minecraft.client.gui.widget.button.Button.IPressable pressedAction, char iconChar) {
            this(x, y, width, height, title, pressedAction, field_238486_s_);
            this.iconChar = iconChar;
        }

        public Button(int x, int y, int width, int height, ITextComponent title, net.minecraft.client.gui.widget.button.Button.IPressable pressedAction, net.minecraft.client.gui.widget.button.Button.ITooltip onTooltip) {
            super(x, y, width, height, title);
            this.onPress = pressedAction;
            this.onTooltip = onTooltip;
            this.iconChar = ' ';
        }


        float titleWidth = Fonts.intl[14].getWidth(this.getMessage().getString());

        public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            int textColor = getColorByName("textColor");
            int iconColor = getColorByName("iconColor");
            int iconnoColor = getColorByName("iconnoColor");

            GaussianBlur.startBlur();
            RenderUtilka.Render2D.drawRoundedRect(x, y, width, height, 4f, new Color(16, 16, 16, 25).getRGB());
            GaussianBlur.endBlur(6, 3);

            boolean isHovered = isMouseOverButton(mouseX, mouseY, (int) (x), y, width, height);

            if (isHovered) {
                if (this.iconChar == 'A') {
                    RenderUtilka.Render2D.drawRoundedRect(x, y, width, height, 4f, new Color(218, 56, 56, 15).getRGB());
                    RenderUtilka.Render2D.drawRoundOutline(x, y, width, height, 4f, -0.9f, ColorUtils.rgba(25, 26, 33, 0), new Vector4i(
                            new Color(218, 56, 56, 15).getRGB(),
                            new Color(218, 56, 56, 15).getRGB(),
                            new Color(218, 56, 56, 15).getRGB(),
                            new Color(218, 56, 56, 15).getRGB()
                    ));
                    Fonts.icon[14].drawCenteredString(matrixStack, String.valueOf(iconChar), this.x + this.width / 2 + 1f - titleWidth / 2, this.y + (this.height - 8) / 2 + 2 + 1.5f,
                            new Color(218, 56, 56, 164).getRGB()
                    );
                } else {
                    RenderUtilka.Render2D.drawRoundedRect(x, y, width, height, 4f, ColorUtils.setAlpha(iconnoColor, 15));
                    RenderUtilka.Render2D.drawRoundOutline(x, y, width, height, 4f, -0.9f, ColorUtils.rgba(25, 26, 33, 0), new Vector4i(ColorUtils.setAlpha(iconnoColor, 31),
                            ColorUtils.setAlpha(iconnoColor, 31),
                            ColorUtils.setAlpha(iconnoColor, 31),
                            ColorUtils.setAlpha(iconnoColor, 31)
                    ));
                    Fonts.intl[13].drawCenteredString(matrixStack, this.getMessage().getString(), this.x + this.width / 2 + 6, this.y + (this.height - 8) / 2 + 3f,
                            ColorUtils.setAlpha(textColor, 245)
                    );
                    Fonts.icon[14].drawCenteredString(matrixStack, String.valueOf(iconChar), this.x + this.width / 2 + 1f - titleWidth / 2, this.y + (this.height - 8) / 2 + 2 + 1.5f,
                            ColorUtils.setAlpha(iconColor, 184)
                    );
                }
            } else {
                RenderUtilka.Render2D.drawRoundedRect(x, y, width, height, 4f, ColorUtils.setAlpha(iconnoColor, 5));
                RenderUtilka.Render2D.drawRoundOutline(x, y, width, height, 4f, -0.9f, ColorUtils.rgba(25, 26, 33, 0), new Vector4i(ColorUtils.setAlpha(iconnoColor, 10),
                        ColorUtils.setAlpha(iconnoColor, 10),
                        ColorUtils.setAlpha(iconnoColor, 10),
                        ColorUtils.setAlpha(iconnoColor, 10)
                ));
                Fonts.intl[13].drawCenteredString(matrixStack, this.getMessage().getString(), this.x + this.width / 2 + 6, this.y + (this.height - 8) / 2 + 3f,
                        ColorUtils.setAlpha(textColor, 245)
                );
                Fonts.icon[14].drawCenteredString(matrixStack, String.valueOf(iconChar), this.x + this.width / 2 + 1f - titleWidth / 2, this.y + (this.height - 8) / 2 + 2 + 1.5f,
                        ColorUtils.setAlpha(iconColor, 184)
                );
            }
        }

        private boolean isMouseOverButton(int mouseX, int mouseY, int buttonX, int buttonY, int buttonWidth, int buttonHeight) {
            return mouseX >= buttonX && mouseY >= buttonY && mouseX < buttonX + buttonWidth && mouseY < buttonY + buttonHeight;
        }

        public void onPress() {
            this.onPress.onPress(this);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

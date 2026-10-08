package never.lose.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import never.lose.utils.render.Render;
import never.lose.screen.widget.AltManagerPanel;
import never.lose.screen.widget.CustomButton;
import never.lose.screen.widget.PlayerCard;
import never.lose.utils.color.ClientColors;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class TitleScreen extends Screen {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final Identifier BACKGROUND = Identifier.of("n2ver", "textures/mainmenu/background_1.png");

    private static final float TEXT_GAP = 8f;
    private static final float TEXT_ABOVE_BUTTONS = 14f;

    private static final Color WATERMARK_COLOR = new Color(160, 160, 160, 180);

    private static final float PANEL_W = 280f;
    private static final float PANEL_H = 300f;

    private final ArrayList<CustomButton> buttons = new ArrayList<>();

    private PlayerCard playerCard;
    private AltManagerPanel altPanel;

    private long lastSecond = -1L;
    private String timeCache = "";

    public TitleScreen() {
        super(Text.empty());
    }

    @Override
    protected void init() {
        super.init();

        int   baseY  = height / 2 - 14;
        float offX   = width < 642 ? width / 2f - 75f : width * 0.7f;
        float btnH   = 25f;
        float margin = btnH + 2f;

        buttons.clear();
        buttons.addAll(Arrays.asList(
                CustomButton.build(offX, baseY, 150, btnH,
                        "l", Text.translatable("menu.singleplayer").getString(),
                        Color.WHITE, Color.WHITE, CustomButton.ButtonType.MAIN,
                        () -> client.setScreen(new SelectWorldScreen(this))),

                CustomButton.build(offX, baseY + margin, 150, btnH,
                        "m", Text.translatable("menu.multiplayer").getString(),
                        Color.WHITE, Color.WHITE, CustomButton.ButtonType.MAIN,
                        () -> client.setScreen(new MultiplayerScreen(this))),

                CustomButton.build(offX, baseY + margin * 2, 150, btnH,
                        "o", Text.translatable("menu.options").getString(),
                        Color.WHITE, Color.WHITE, CustomButton.ButtonType.MAIN,
                        () -> client.setScreen(new OptionsScreen(this, client.options))),

                CustomButton.build(offX, baseY + margin * 3, 150, btnH,
                        "p", Text.translatable("menu.quit").getString(),
                        Color.WHITE, Color.WHITE, CustomButton.ButtonType.MAIN,
                        () -> client.close())
        ));

        playerCard = PlayerCard.build(6, 6, 124, 34,
                () -> {
                    if (altPanel != null) altPanel.toggle();
                });
        playerCard.updateInfo();

        altPanel = AltManagerPanel.build(
                (width - PANEL_W) / 2f,
                (height - PANEL_H) / 2f,
                PANEL_W, PANEL_H);
        altPanel.init(this);
        altPanel.setOnLogin(() -> playerCard.updateInfo());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(BACKGROUND, 0, 0, 0, 0, width, height, width, height);

        Color overlay = Render.injectAlpha(ClientColors.BACK_COLOR, 120);
        Render.round(0, 0, width, height, 0, 0, 0, 0, 0, overlay, overlay, overlay, overlay);

        float menuAlpha = 1f - altPanel.getAnim();

        if (menuAlpha > 0.01f) {
            renderMenu(mouseX, mouseY, menuAlpha);

            playerCard.setOpacity(menuAlpha);
            playerCard.render(mouseX, mouseY);
            playerCard.renderHead();

            Color watermark = Render.injectAlpha(WATERMARK_COLOR, WATERMARK_COLOR.getAlpha() * menuAlpha);
            String text = "NeverLose 1.21.1 v0.2";
            Render.textCentered(
                    text,
                    4f + Render.textWidth(text, "sfmedium", 8) / 2f,
                    height - 14f,
                    "sfmedium", 8,
                    watermark);
        }

        altPanel.render(mouseX, mouseY);
        altPanel.renderHeads();

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderMenu(int mouseX, int mouseY, float alpha) {
        float offX  = width < 642 ? width / 2f - 75f : width * 0.7f;
        float baseY = height / 2f - 14f;

        float welcomeY = baseY - TEXT_ABOVE_BUTTONS;
        float timeY    = welcomeY - TEXT_GAP;

        Color fore = Render.injectAlpha(ClientColors.FORE_COLOR, ClientColors.FORE_COLOR.getAlpha() * alpha);

        Render.textCentered(currentTime(), offX + 75f, timeY, "sfmedium", 9, fore);

        Render.textCentered(
                "WELCOME, " + playerName().toUpperCase(Locale.ROOT),
                offX + 75f, welcomeY,
                "sfbold", 12,
                fore);

        for (CustomButton button : buttons) {
            button.setOpacity(alpha);
            button.render(mouseX, mouseY);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    private String currentTime() {
        long second = System.currentTimeMillis() / 1000L;
        if (second != lastSecond) {
            lastSecond = second;
            timeCache = LocalTime.now().format(TIME_FORMAT);
        }
        return timeCache;
    }

    private String playerName() {
        if (client == null || client.getSession() == null) return "PLAYER";
        return client.getSession().getUsername();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (altPanel.getAnim() < 0.01f && !altPanel.isOpen()) {
            playerCard.click((int) mouseX, (int) mouseY, button);
            for (CustomButton b : buttons) {
                b.click((int) mouseX, (int) mouseY, button);
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        altPanel.click((int) mouseX, (int) mouseY, button);
        super.mouseClicked(mouseX, mouseY, button);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (altPanel.isOpen()) {
            altPanel.scroll((int) mouseX, (int) mouseY, (float) verticalAmount);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (altPanel.isOpen()) {
            altPanel.charTyped(chr, modifiers);
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (altPanel.isOpen()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                altPanel.toggle();
                return true;
            }
            altPanel.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
package org.privates.client.screen.mainmenu;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Namespaced;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.gif.GifRender;

import java.util.*;

public class InfoClientScreen extends Screen implements IMinecraft {
    private final Screen parent;
    private static final Map<String, GifRender> gifCache = new HashMap<>();
    private static boolean gifShouldAnimate = false;
    private final List<InfoItem> infoItems = Arrays.asList(
            new InfoItem("YouTube info", "youtube.gif",
                    "Чтобы попасть на YouTube тебе нужно иметь минимум 300+ просмотров за 7 дней на канале на последних 3-х видео.\n" +
                            "Твои видео должны быть ТОЛЬКО по тематике Minecraft cheats и сняты только на серверах:\n" +
                            "reallyworld, hollyworld, funtime, spookytime, bravohvh, metahvh, legendsgrief, wellmine."),
            new InfoItem("PrivateClient info", "privateclient.gif",
                    "Создатель: @fungetx\nНа данный момент один из лучших как по визуалу, так и по PvP-функционалу, а также часть PvE.\n" +
                            "Мы — первый красивый, удобный и межсерверный чит. Наш чит обходит сервера: reallyworld, funtime, hollyworld, spookytime.\n\n" +
                            "Изменить GUI (функция clickgui)\nbaritone #help\ncommand cheat .help"),
            new InfoItem("TikTok info", "tiktok.gif",
                    "Чтобы попасть на роль TikTok в проекте, твои просмотры должны быть от 3000+ просмотров.\n" +
                            "Твои видео должны быть ТОЛЬКО по тематике Minecraft cheats и сняты только на серверах:\n" +
                            "reallyworld, hollyworld, funtime, spookytime, bravohvh, metahvh, legendsgrief, wellmine."),
            new InfoItem("ChangeLog info", "changelog.gif",
                    "§a[ + ] §fAuraNew §7mode §dMix\n" +
                            "§c- holyworld §f- funtime §7- spookytime §b- neuro §7и ещё множество видов\n" +
                            "§a[ + ] §fTargetStrafe §7spookytime §c- holyworld\n" +
                            "§a[ + ] §f2 ClickGui §7FlatGui - clickgui\n" +
                            "§a[ + ] §fElytraTarget\n" +
                            "§a[ + ] §fCustomMainMenu\n" +
                            "§a[ + ] §eBest hud in industrie\n" +
                            "§a[ + ] §fHitBox §7mode §fFOV\n" +
                            "§a[ + ] §fMotionElytraFly\n" +
                            "§a[ + ] §cW§fater§cS§fpeed §7(§cF§fT§7)\n" +
                            "§a[ + ] §cW§fater§cS§fpider §7(§cF§fT§7)\n" +
                            "§a[ + ] §fElytraBoost §eRW\n"
            )
    );
    private int selectedInfo = -1;

    public InfoClientScreen(Screen parent) {
        super(StringTextComponent.EMPTY);
        this.parent = parent;
    }

    @Override
    protected void init() {
        gifShouldAnimate = true;
        selectedInfo = -1;
        // Пре-кэшируем все гифки из infoItems (один раз на весь клиент)
        for (InfoItem item : infoItems) {
            if (!gifCache.containsKey(item.gifName)) {
                try {
                    gifCache.put(item.gifName, new GifRender(new Namespaced("texture/" + item.gifName)));
                } catch (Exception e) {
                    e.printStackTrace();
                    gifCache.put(item.gifName, null);
                }
            }
        }
        // Общая инфо-гифка (кнопки выбора)
        if (!gifCache.containsKey("info.gif")) {
            try {
                gifCache.put("info.gif", new GifRender(new Namespaced("texture/info.gif")));
            } catch (Exception e) {
                e.printStackTrace();
                gifCache.put("info.gif", null);
            }
        }
    }

    @Override
    public void onClose() {
        gifShouldAnimate = false;
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrix);

        float gifWidth = width * 0.5f;
        float gifHeight = height * 0.7f;
        float gifX = 0;
        float gifY = (height - gifHeight) / 2f;

        if (selectedInfo == -1) {
            GifRender infoGif = gifCache.get("info.gif");
            if (infoGif != null) {
                infoGif.draw(matrix, gifX, gifY, gifWidth, gifHeight, 1f, 35, gifShouldAnimate);
            }

            float rightPanelX = width * 0.5f;
            float rightPanelY = gifY;
            float rightPanelW = width * 0.5f;
            float rightPanelH = gifHeight;

            int panelColor = ColorUtil.getColor(40, 40, 52, 200);
            RenderUtil.Rounded.smooth(matrix, rightPanelX + 25, rightPanelY + 25, rightPanelW - 50, rightPanelH - 50, panelColor, Round.of(21));
            Fonts.SERATI.draw(matrix, "Выберите раздел:", rightPanelX + 50, rightPanelY + 70, 0xFFFFFFFF, 22);

            float btnW = rightPanelW - 450;
            float btnH = 45f;
            float btnStartY = rightPanelY + 120;
            for (int i = 0; i < infoItems.size(); i++) {
                float btnY = btnStartY + i * (btnH + 20);
                boolean hovered = mouseX >= rightPanelX + 50 && mouseX <= rightPanelX + 50 + btnW &&
                        mouseY >= btnY && mouseY <= btnY + btnH;
                int bg = hovered
                        ? ColorUtil.getColor(65, 65, 90, 220)
                        : ColorUtil.getColor(52, 52, 62, 160);
                RenderUtil.Rounded.smooth(matrix, rightPanelX + 50, btnY, btnW, btnH, bg, Round.of(14));
                Fonts.SERATI.draw(matrix, infoItems.get(i).title, rightPanelX + 65, btnY + btnH / 2f - 8, 0xFFFFFFFF, 18);
            }
        } else {
            InfoItem item = infoItems.get(selectedInfo);
            GifRender thisGif = gifCache.get(item.gifName);
            if (thisGif != null) {
                thisGif.draw(matrix, gifX, gifY, gifWidth, gifHeight, 1f, 35, gifShouldAnimate);
            }

            float rightPanelX = width * 0.5f;
            float rightPanelY = gifY;
            float rightPanelW = width * 0.5f;
            float rightPanelH = gifHeight;

            int panelColor = ColorUtil.getColor(40, 40, 52, 210);
            RenderUtil.Rounded.smooth(matrix, rightPanelX + 25, rightPanelY + 25, rightPanelW - 50, rightPanelH - 50, panelColor, Round.of(21));
            Fonts.SERATI.draw(matrix, item.title, rightPanelX + 50, rightPanelY + 70, 0xFFFFFFFF, 22);

            drawMultiline(matrix, item.text, rightPanelX + 50, rightPanelY + 120, rightPanelW - 100, 0xFFCCCCCC, 16);

            float btnW = 120, btnH = 32;
            float btnX = rightPanelX + 50;
            float btnY = rightPanelY + rightPanelH - btnH - 40;
            boolean hovered = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
            int bg = hovered
                    ? ColorUtil.getColor(60, 60, 80, 210)
                    : ColorUtil.getColor(50, 50, 63, 150);
            RenderUtil.Rounded.smooth(matrix, btnX, btnY, btnW, btnH, bg, Round.of(9));
            Fonts.SERATI.drawCenter(matrix, "Назад", btnX + btnW / 2, btnY + btnH / 2 - 8, 0xFFFFFFFF, 15);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selectedInfo == -1) {
            float rightPanelX = width * 0.5f;
            float rightPanelY = (height - height * 0.7f) / 2f;
            float rightPanelW = width * 0.5f;
            float btnW = rightPanelW - 100;
            float btnH = 45f;
            float btnStartY = rightPanelY + 120;
            for (int i = 0; i < infoItems.size(); i++) {
                float btnY = btnStartY + i * (btnH + 20);
                if (mouseX >= rightPanelX + 50 && mouseX <= rightPanelX + 50 + btnW &&
                        mouseY >= btnY && mouseY <= btnY + btnH) {
                    selectedInfo = i;
                    return true;
                }
            }
        } else {
            float rightPanelX = width * 0.5f;
            float rightPanelY = (height - height * 0.7f) / 2f;
            float rightPanelH = height * 0.7f;
            float btnW = 120, btnH = 32;
            float btnX = rightPanelX + 50;
            float btnY = rightPanelY + rightPanelH - btnH - 40;
            if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                selectedInfo = -1;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    private void drawMultiline(MatrixStack matrix, String text, float x, float y, float maxWidth, int color, float size) {
        String[] lines = text.split("\n");
        float yOff = 0;
        for (String line : lines) {
            List<String> wrapped = wrapLine(line, maxWidth, size);
            for (String l : wrapped) {
                Fonts.SERATI.draw(matrix, l, x, y + yOff, color, size);
                yOff += size + 3;
            }
        }
    }

    private List<String> wrapLine(String text, float maxWidth, float fontSize) {
        List<String> result = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            float width = Fonts.SERATI.getWidth(testLine, fontSize);
            if (width > maxWidth && line.length() > 0) {
                result.add(line.toString());
                line = new StringBuilder(word);
            } else {
                if (line.length() > 0) line.append(" ");
                line.append(word);
            }
        }
        if (line.length() > 0) result.add(line.toString());
        return result;
    }

    private static class InfoItem {
        final String title;
        final String gifName;
        final String text;

        InfoItem(String title, String gifName, String text) {
            this.title = title;
            this.gifName = gifName;
            this.text = text;
        }
    }
}
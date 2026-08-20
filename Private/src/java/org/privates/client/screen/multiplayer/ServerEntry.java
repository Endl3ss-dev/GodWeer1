package org.privates.client.screen.multiplayer;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

public class ServerEntry implements IMinecraft {
    private final CustomMultiplayerScreen screen;
    private final ServerData serverData;
    private final int index;

    private float x, y, width, height;
    private float hoverAnim = 0f;
    private float selectedAnim = 0f;
    private float scaleAnim = 1f;
    private float pingAnim = 0f;
    private float iconLoadAnim = 0f;

    public long lastClickTime = 0;

    private boolean iconLoaded = false;

    public ServerEntry(CustomMultiplayerScreen screen, ServerData serverData, int index) {
        this.screen = screen;
        this.serverData = serverData;
        this.index = index;
    }

    public void tick() {
        if (serverData != null && !serverData.pinged) {
            try {
                screen.getServerPinger().ping(serverData, () -> {
                });
            } catch (Exception e) {
            }
        }
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void render(MatrixStack stack, float mouseX, float mouseY) {
        Theme theme = Theme.getInstance();

        boolean isSelected = screen.getSelectedEntry() == this;
        boolean isHovered = screen.getHoveredEntry() == this;

        hoverAnim = Mathf.fast(hoverAnim, isHovered ? 1f : 0f, 18);
        selectedAnim = Mathf.fast(selectedAnim, isSelected ? 1f : 0f, 16);
        scaleAnim = Mathf.fast(scaleAnim, isHovered ? 1.012f : 1f, 14);
        iconLoadAnim = Mathf.fast(iconLoadAnim, iconLoaded ? 1f : 0f, 10);

        float centerX = x + width / 2f;
        float centerY = y + height / 2f;

        stack.push();
        stack.translate(centerX, centerY, 0);
        stack.scale(scaleAnim, scaleAnim, 1);
        stack.translate(-centerX, -centerY, 0);

        int bgAlpha = (int) (35 + hoverAnim * 20 + selectedAnim * 25);
        int borderAlpha = (int) (55 + hoverAnim * 35 + selectedAnim * 45);

        int bgColor = ColorUtil.overCol(
                ColorUtil.getColor(22, 22, 22, bgAlpha),
                ColorUtil.multAlpha(theme.clientColor(), 0.04f),
                selectedAnim * 0.7f + hoverAnim * 0.3f
        );

        int borderColor = ColorUtil.overCol(
                ColorUtil.getColor(65, 65, 65, borderAlpha),
                ColorUtil.multAlpha(theme.clientColor(), 0.18f),
                selectedAnim * 0.8f + hoverAnim * 0.2f
        );

        RenderUtil.Rounded.roundedWithOutline(stack, x, y, width, height, 0.85f,
                bgColor, borderColor, Round.of(4.5f));

        if (selectedAnim > 0.01f) {
            float glowSize = 1.2f;
            RenderUtil.Shadow.drawShadow(stack, x - glowSize, y - glowSize,
                    width + glowSize * 2f, height + glowSize * 2f, 8,
                    ColorUtil.multAlpha(theme.clientColor(), selectedAnim * 0.3f));
        }

        renderServerIcon(stack);
        renderServerInfo(stack, theme);
        renderPingIndicator(stack, theme);
        renderPlayerCount(stack, theme);

        stack.pop();
    }

    private void renderServerIcon(MatrixStack stack) {
        float iconSize = 48f;
        float iconX = x + 8f;
        float iconY = y + height / 2f - iconSize / 2f;

        RenderUtil.Rounded.roundedRect(stack, iconX, iconY, iconSize, iconSize,
                ColorUtil.getColor(35, 35, 35), Round.of(4.5f));

        RenderUtil.Rounded.roundedOutline(stack, iconX, iconY, iconSize, iconSize, 0.75f,
                ColorUtil.getColor(70, 70, 70, 150), Round.of(4.5f));

        if (serverData.getBase64EncodedIconData() != null) {
            iconLoaded = true;

            stack.push();
            stack.translate(iconX + iconSize / 2f, iconY + iconSize / 2f, 0);
            stack.scale(iconLoadAnim, iconLoadAnim, 1);
            stack.translate(-(iconX + iconSize / 2f), -(iconY + iconSize / 2f), 0);

            RenderSystem.enableBlend();
            RenderSystem.color4f(1f, 1f, 1f, iconLoadAnim);
            // Здесь должен быть рендер иконки сервера
            // mc.getTextureManager().bindTexture(serverIconTexture);
            // AbstractGui.blit(stack, (int)iconX, (int)iconY, 0, 0, (int)iconSize, (int)iconSize, (int)iconSize, (int)iconSize);
            RenderSystem.disableBlend();

            stack.pop();
        } else {
            String firstLetter = serverData.serverName.isEmpty() ? "?" :
                    String.valueOf(serverData.serverName.charAt(0)).toUpperCase();

            int letterColor = ColorUtil.getColor(
                    (serverData.serverName.hashCode() & 0xFF),
                    ((serverData.serverName.hashCode() >> 8) & 0xFF),
                    ((serverData.serverName.hashCode() >> 16) & 0xFF)
            );

            Fonts.SF_BOLD.drawCenter(stack, firstLetter,
                    iconX + iconSize / 2f,
                    iconY + iconSize / 2f - Fonts.SF_BOLD.getHeight(firstLetter, 22f) / 2f,
                    letterColor, 22f);
        }
    }

    private void renderServerInfo(MatrixStack stack, Theme theme) {
        float textX = x + 68f;
        float maxTextWidth = width - 68f - 120f;

        float nameY = y + 12f;
        String serverName = serverData.serverName;

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(textX, nameY - 2f, maxTextWidth, 12f);

        int nameColor = ColorUtil.overCol(
                ColorUtil.getColor(210, 210, 210),
                theme.textColor(),
                selectedAnim * 0.5f + hoverAnim * 0.5f
        );

        Fonts.SF_BOLD.draw(stack, serverName, textX, nameY, nameColor, 9.5f);

        ScissorExp.unset();
        ScissorExp.pop();

        float motdY = nameY + 14f;
        String motd = getFormattedMOTD();

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(textX, motdY - 2f, maxTextWidth, 10f);

        Fonts.SF_REGULAR.draw(stack, motd, textX, motdY,
                ColorUtil.getColor(150, 150, 150), 7.8f);

        ScissorExp.unset();
        ScissorExp.pop();

        float ipY = motdY + 12f;
        String ip = serverData.serverIP;

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(textX, ipY - 2f, maxTextWidth, 10f);

        Fonts.SF_REGULAR.draw(stack, ip, textX, ipY,
                ColorUtil.getColor(120, 120, 120), 7f);

        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void renderPingIndicator(MatrixStack stack, Theme theme) {
        float pingX = x + width - 100f;
        float pingY = y + height / 2f;

        int pingMs = (int) serverData.pingToServer;

        if (pingMs < 0) {
            String statusText = getStatusText();
            int statusColor = getStatusColor();

            Fonts.SF_MEDIUM.draw(stack, statusText, pingX,
                    pingY - Fonts.SF_MEDIUM.getHeight(statusText, 7.5f) / 2f,
                    statusColor, 7.5f);
        } else {
            String pingText = pingMs + "ms";
            int pingColor = getPingColor(pingMs);

            float pingSize = 6f;
            float dotX = pingX - pingSize - 6f;
            float dotY = pingY - pingSize / 2f;

            pingAnim = Mathf.fast(pingAnim, 1f, 12);

            RenderUtil.Rounded.roundedRect(stack, dotX, dotY, pingSize, pingSize,
                    ColorUtil.multAlpha(pingColor, pingAnim), Round.of(pingSize / 2f));

            float pulseSize = pingSize + (float) Math.sin(System.currentTimeMillis() * 0.003) * 2f;
            RenderUtil.Rounded.roundedRect(stack,
                    dotX - (pulseSize - pingSize) / 2f,
                    dotY - (pulseSize - pingSize) / 2f,
                    pulseSize, pulseSize,
                    ColorUtil.multAlpha(pingColor, pingAnim * 0.2f), Round.of(pulseSize / 2f));

            Fonts.SF_MEDIUM.draw(stack, pingText, pingX,
                    pingY - Fonts.SF_MEDIUM.getHeight(pingText, 7.5f) / 2f,
                    ColorUtil.overCol(ColorUtil.getColor(140, 140, 140), pingColor, 0.6f), 7.5f);
        }
    }

    private void renderPlayerCount(MatrixStack stack, Theme theme) {
        float playerX = x + width - 16f;
        float playerY = y + height / 2f;

        String playerCount = getPlayerCountText();
        if (playerCount.isEmpty()) return;

        float playerWidth = Fonts.SF_REGULAR.getWidth(playerCount, 7.5f);
        playerX -= playerWidth;

        int playerColor = ColorUtil.getColor(140, 140, 140);

        Fonts.icons.draw(stack, "c", playerX - 12f,
                playerY - Fonts.icons.getHeight("c", 8f) / 2f,
                playerColor, 8f);

        Fonts.SF_REGULAR.draw(stack, playerCount, playerX,
                playerY - Fonts.SF_REGULAR.getHeight(playerCount, 7.5f) / 2f,
                playerColor, 7.5f);
    }

    private String getFormattedMOTD() {
        if (serverData.serverMOTD == null || serverData.serverMOTD == StringTextComponent.EMPTY) {
            return serverData.pinged ? "Нет описания" : "Подключение...";
        }

        String motd = serverData.serverMOTD.getString();
        if (motd.length() > 45) {
            motd = motd.substring(0, 42) + "...";
        }

        return motd;
    }

    private String getPlayerCountText() {
        if (serverData.populationInfo == null || serverData.populationInfo == StringTextComponent.EMPTY) {
            return "";
        }

        String population = serverData.populationInfo.getString();
        return population.isEmpty() ? "" : population;
    }

    private String getStatusText() {
        if (!serverData.pinged) {
            return "Проверка...";
        }

        if (serverData.pingToServer == -1L) {
            return "Нет связи";
        }

        if (serverData.pingToServer == -2L) {
            return "Подключение...";
        }

        return "Неизвестно";
    }

    private int getStatusColor() {
        if (!serverData.pinged) {
            return ColorUtil.getColor(150, 150, 150);
        }

        if (serverData.pingToServer == -1L) {
            return ColorUtil.getColor(255, 100, 100);
        }

        return ColorUtil.getColor(150, 150, 150);
    }

    private int getPingColor(int pingMs) {
        if (pingMs < 0) {
            return ColorUtil.getColor(100, 100, 100);
        } else if (pingMs < 75) {
            return ColorUtil.getColor(90, 255, 90);
        } else if (pingMs < 150) {
            return ColorUtil.getColor(150, 255, 90);
        } else if (pingMs < 250) {
            return ColorUtil.getColor(255, 220, 90);
        } else if (pingMs < 400) {
            return ColorUtil.getColor(255, 170, 90);
        } else {
            return ColorUtil.getColor(255, 90, 90);
        }
    }

    public ServerData getServerData() {
        return serverData;
    }

    public int getIndex() {
        return index;
    }
}
package org.privates.client.screen.multiplayer;

import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.ServerPinger;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer2;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.List;

public class CustomMultiplayerScreen extends Screen implements IMinecraft {
    private final Screen parentScreen;
    private final List<ServerEntry> serverEntries = new ArrayList<>();

    private float windowX, windowY, windowWidth, windowHeight;
    private float scrollOffset = 0f;
    private float scrollOffsetV = 0f;

    private ServerList serverList;
    private ServerPinger serverPinger;
    private ServerEntry selectedEntry;
    private ServerEntry hoveredEntry;

    private String searchQuery = "";
    private boolean searchActive = false;
    private float searchExpandAnim = 0f;

    private boolean deleteConfirmMode = false;

    public CustomMultiplayerScreen(Screen parentScreen) {
        super(new StringTextComponent("Multiplayer"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();

        this.windowWidth = Math.min(650f, width * 0.8f);
        this.windowHeight = Math.min(500f, height * 0.85f);
        this.windowX = width / 2f - windowWidth / 2f;
        this.windowY = height / 2f - windowHeight / 2f;

        this.serverList = new ServerList(minecraft);
        this.serverList.loadServerList();
        this.serverPinger = new ServerPinger();

        loadServerEntries();
    }

    private void loadServerEntries() {
        serverEntries.clear();
        for (int i = 0; i < serverList.countServers(); i++) {
            ServerData serverData = serverList.getServerData(i);
            ServerEntry entry = new ServerEntry(this, serverData, i);
            serverEntries.add(entry);
        }
        serverPinger.pingPendingNetworks();
    }

    @Override
    public void tick() {
        super.tick();
        serverPinger.pingPendingNetworks();

        for (ServerEntry entry : serverEntries) {
            entry.tick();
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        searchExpandAnim = Mathf.fast(searchExpandAnim, searchActive ? 1f : 0f, 14);

        Theme theme = Theme.getInstance();

        RenderUtil.drawBackground(stack);

        BlurRenderer2.registerRenderCall(() ->
                RenderUtil.Rounded.roundedRect(stack, windowX, windowY, windowWidth, windowHeight,
                        ColorUtil.getColor(0, 0, 0, 180), Round.of(6f))
        );
        BlurRenderer2.draw(20);

        RenderUtil.Rounded.roundedRect(stack, windowX, windowY, windowWidth, windowHeight,
                ColorUtil.getColor(0, 0, 0, 180), Round.of(6f));

        RenderUtil.Rounded.roundedOutline(stack, windowX - 0.75f, windowY - 0.75f,
                windowWidth + 1.5f, windowHeight + 1.5f, 0.75f,
                ColorUtil.getColor(50, 50, 50), Round.of(6f));

        renderHeader(stack, mouseX, mouseY, theme);
        renderSearchBar(stack, mouseX, mouseY, theme);
        renderServerList(stack, mouseX, mouseY, theme);
        renderBottomButtons(stack, mouseX, mouseY, theme);

        if (deleteConfirmMode) {
            renderDeleteConfirm(stack, mouseX, mouseY, theme);
        }

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void renderHeader(MatrixStack stack, float mouseX, float mouseY, Theme theme) {
        float headerHeight = 35f;

        String title = "Список серверов";
        float titleSize = 12f;
        Fonts.SF_BOLD.drawCenter(stack, title,
                windowX + windowWidth / 2f,
                windowY + headerHeight / 2f - Fonts.SF_BOLD.getHeight(title, titleSize) / 2f,
                theme.textColor(), titleSize);

        float closeSize = 20f;
        float closeX = windowX + windowWidth - closeSize - 8f;
        float closeY = windowY + 7.5f;
        boolean closeHovered = isHovered(mouseX, mouseY, closeX, closeY, closeSize, closeSize);

        RenderUtil.Rounded.roundedRect(stack, closeX, closeY, closeSize, closeSize,
                closeHovered ? ColorUtil.getColor(255, 70, 70) : ColorUtil.getColor(60, 60, 60),
                Round.of(4f));

        Fonts.icons.drawCenter(stack, "E", closeX + closeSize / 2f, closeY + closeSize / 2f - 1f,
                ColorUtil.getColor(255, 255, 255), 10f);
    }

    private void renderSearchBar(MatrixStack stack, float mouseX, float mouseY, Theme theme) {
        float searchY = windowY + 45f;
        float searchHeight = 24f;
        float searchWidth = windowWidth - 16f;
        float searchX = windowX + 8f;

        int bgAlpha = 50 + (searchActive ? 15 : 0);
        RenderUtil.Rounded.roundedWithOutline(stack, searchX, searchY, searchWidth, searchHeight, 0.75f,
                ColorUtil.getColor(30, 30, 30, bgAlpha),
                searchActive ? ColorUtil.multAlpha(theme.clientColor(), 0.3f) :
                        ColorUtil.getColor(80, 80, 80),
                Round.of(4f));

        String searchText = searchQuery.isEmpty() && !searchActive ? "Поиск серверов..." : searchQuery;
        Fonts.SF_MEDIUM.draw(stack, searchText, searchX + 8f,
                searchY + searchHeight / 2f - Fonts.SF_MEDIUM.getHeight(searchText, 8f) / 2f,
                searchQuery.isEmpty() && !searchActive ?
                        ColorUtil.getColor(150, 150, 150) : theme.textColor(), 8f);

        if (searchActive && System.currentTimeMillis() % 1000 > 500) {
            float cursorX = searchX + 8f + Fonts.SF_MEDIUM.getWidth(searchQuery, 8f);
            Fonts.SF_MEDIUM.draw(stack, "|", cursorX,
                    searchY + searchHeight / 2f - Fonts.SF_MEDIUM.getHeight("|", 8f) / 2f,
                    theme.clientColor(), 8f);
        }
    }

    private void renderServerList(MatrixStack stack, float mouseX, float mouseY, Theme theme) {
        float listX = windowX + 8f;
        float listY = windowY + 78f;
        float listWidth = windowWidth - 16f;
        float listHeight = windowHeight - 78f - 50f;

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(listX, listY, listWidth, listHeight);

        scrollOffsetV = Mathf.fast(scrollOffsetV, scrollOffset, 16);

        float currentY = listY + scrollOffsetV;
        float entryHeight = 60f;
        float entrySpacing = 6f;

        hoveredEntry = null;

        List<ServerEntry> filteredEntries = getFilteredEntries();

        if (filteredEntries.isEmpty()) {
            String emptyText = searchQuery.isEmpty() ? "Нет серверов" : "Серверы не найдены";
            Fonts.SF_MEDIUM.drawCenter(stack, emptyText,
                    listX + listWidth / 2f,
                    listY + listHeight / 2f - Fonts.SF_MEDIUM.getHeight(emptyText, 9f) / 2f,
                    ColorUtil.getColor(130, 130, 130), 9f);
        }

        for (ServerEntry entry : filteredEntries) {
            if (currentY + entryHeight < listY || currentY > listY + listHeight) {
                currentY += entryHeight + entrySpacing;
                continue;
            }

            entry.setX(listX);
            entry.setY(currentY);
            entry.setWidth(listWidth);
            entry.setHeight(entryHeight);

            boolean hovered = isHovered(mouseX, mouseY, listX, currentY, listWidth, entryHeight);
            if (hovered) hoveredEntry = entry;

            entry.render(stack, mouseX, mouseY);

            currentY += entryHeight + entrySpacing;
        }

        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void renderBottomButtons(MatrixStack stack, float mouseX, float mouseY, Theme theme) {
        float buttonY = windowY + windowHeight - 38f;
        float buttonHeight = 28f;
        float spacing = 6f;

        float[] buttonWidths = {90f, 100f, 80f, 80f, 80f, 80f};
        String[] buttonLabels = {"Подключиться", "Прямое", "Добавить", "Изменить", "Удалить", "Обновить"};

        float totalWidth = 0;
        for (float w : buttonWidths) totalWidth += w + spacing;
        totalWidth -= spacing;

        float startX = windowX + windowWidth / 2f - totalWidth / 2f;
        float currentX = startX;

        for (int i = 0; i < buttonLabels.length; i++) {
            boolean enabled = true;
            if (i == 0 || i == 3 || i == 4) {
                enabled = selectedEntry != null;
            }

            boolean hovered = enabled && isHovered(mouseX, mouseY, currentX, buttonY, buttonWidths[i], buttonHeight);

            int bgColor = enabled ?
                    (hovered ? ColorUtil.multBright(theme.clientColor(), 0.85f) : theme.clientColor()) :
                    ColorUtil.getColor(50, 50, 50);

            RenderUtil.Rounded.roundedRect(stack, currentX, buttonY, buttonWidths[i], buttonHeight,
                    bgColor, Round.of(4f));

            int textColor = enabled ? ColorUtil.getColor(255, 255, 255) : ColorUtil.getColor(120, 120, 120);

            Fonts.SF_SEMIBOLD.drawCenter(stack, buttonLabels[i],
                    currentX + buttonWidths[i] / 2f,
                    buttonY + buttonHeight / 2f - Fonts.SF_SEMIBOLD.getHeight(buttonLabels[i], 8f) / 2f,
                    textColor, 8f);

            currentX += buttonWidths[i] + spacing;
        }
    }

    private void renderDeleteConfirm(MatrixStack stack, float mouseX, float mouseY, Theme theme) {
        float panelWidth = 320f;
        float panelHeight = 140f;
        float panelX = width / 2f - panelWidth / 2f;
        float panelY = height / 2f - panelHeight / 2f;

        RenderUtil.Rounded.roundedRect(stack, 0, 0, width, height,
                ColorUtil.getColor(0, 0, 0, 100), Round.of(0));

        BlurRenderer2.registerRenderCall(() ->
                RenderUtil.Rounded.roundedRect(stack, panelX, panelY, panelWidth, panelHeight,
                        ColorUtil.getColor(0, 0, 0, 200), Round.of(5f))
        );
        BlurRenderer2.draw(15);

        RenderUtil.Rounded.roundedRect(stack, panelX, panelY, panelWidth, panelHeight,
                ColorUtil.getColor(20, 20, 20, 230), Round.of(5f));

        String title = "Удалить сервер?";
        Fonts.SF_BOLD.drawCenter(stack, title, panelX + panelWidth / 2f, panelY + 20f,
                ColorUtil.getColor(255, 70, 70), 11f);

        if (selectedEntry != null) {
            String serverName = selectedEntry.getServerData().serverName;
            Fonts.SF_MEDIUM.drawCenter(stack, serverName, panelX + panelWidth / 2f, panelY + 50f,
                    theme.textColor(), 9f);
        }

        float btnWidth = 100f;
        float btnHeight = 28f;
        float btnY = panelY + panelHeight - btnHeight - 15f;

        float deleteX = panelX + panelWidth / 2f - btnWidth - 5f;
        boolean deleteHovered = isHovered(mouseX, mouseY, deleteX, btnY, btnWidth, btnHeight);
        RenderUtil.Rounded.roundedRect(stack, deleteX, btnY, btnWidth, btnHeight,
                deleteHovered ? ColorUtil.getColor(220, 50, 50) : ColorUtil.getColor(180, 50, 50),
                Round.of(4f));
        Fonts.SF_SEMIBOLD.drawCenter(stack, "Удалить", deleteX + btnWidth / 2f,
                btnY + btnHeight / 2f - 3f, -1, 8f);

        float cancelX = panelX + panelWidth / 2f + 5f;
        boolean cancelHovered = isHovered(mouseX, mouseY, cancelX, btnY, btnWidth, btnHeight);
        RenderUtil.Rounded.roundedRect(stack, cancelX, btnY, btnWidth, btnHeight,
                cancelHovered ? ColorUtil.getColor(80, 80, 80) : ColorUtil.getColor(60, 60, 60),
                Round.of(4f));
        Fonts.SF_SEMIBOLD.drawCenter(stack, "Отмена", cancelX + btnWidth / 2f,
                btnY + btnHeight / 2f - 3f, -1, 8f);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (deleteConfirmMode) {
            return handleDeleteConfirmClick(mouseX, mouseY, button);
        }

        float closeSize = 20f;
        float closeX = windowX + windowWidth - closeSize - 8f;
        float closeY = windowY + 7.5f;
        if (isHovered(mouseX, mouseY, closeX, closeY, closeSize, closeSize)) {
            minecraft.displayScreen(parentScreen);
            return true;
        }

        float searchY = windowY + 45f;
        float searchHeight = 24f;
        float searchX = windowX + 8f;
        float searchWidth = windowWidth - 16f;
        if (isHovered(mouseX, mouseY, searchX, searchY, searchWidth, searchHeight)) {
            searchActive = true;
            return true;
        } else {
            searchActive = false;
        }

        if (hoveredEntry != null) {
            selectedEntry = hoveredEntry;
            if (button == 0 && System.currentTimeMillis() - hoveredEntry.lastClickTime < 500) {
                connectToSelected();
            }
            hoveredEntry.lastClickTime = System.currentTimeMillis();
            return true;
        }

        return handleBottomButtonsClick(mouseX, mouseY, button);
    }

    private boolean handleDeleteConfirmClick(double mouseX, double mouseY, int button) {
        if (button != 0) return false;

        float panelWidth = 320f;
        float panelHeight = 140f;
        float panelX = width / 2f - panelWidth / 2f;
        float panelY = height / 2f - panelHeight / 2f;

        float btnWidth = 100f;
        float btnHeight = 28f;
        float btnY = panelY + panelHeight - btnHeight - 15f;

        float deleteX = panelX + panelWidth / 2f - btnWidth - 5f;
        if (isHovered(mouseX, mouseY, deleteX, btnY, btnWidth, btnHeight)) {
            if (selectedEntry != null) {
                serverList.func_217506_a(selectedEntry.getServerData());
                serverList.saveServerList();
                loadServerEntries();
                selectedEntry = null;
            }
            deleteConfirmMode = false;
            return true;
        }

        float cancelX = panelX + panelWidth / 2f + 5f;
        if (isHovered(mouseX, mouseY, cancelX, btnY, btnWidth, btnHeight)) {
            deleteConfirmMode = false;
            return true;
        }

        return false;
    }

    private boolean handleBottomButtonsClick(double mouseX, double mouseY, int button) {
        if (button != 0) return false;

        float buttonY = windowY + windowHeight - 38f;
        float buttonHeight = 28f;
        float spacing = 6f;

        float[] buttonWidths = {90f, 100f, 80f, 80f, 80f, 80f};
        float totalWidth = 0;
        for (float w : buttonWidths) totalWidth += w + spacing;
        totalWidth -= spacing;

        float startX = windowX + windowWidth / 2f - totalWidth / 2f;
        float currentX = startX;

        // Подключиться
        if (isHovered(mouseX, mouseY, currentX, buttonY, buttonWidths[0], buttonHeight) && selectedEntry != null) {
            connectToSelected();
            return true;
        }
        currentX += buttonWidths[0] + spacing;

        // Прямое подключение
        if (isHovered(mouseX, mouseY, currentX, buttonY, buttonWidths[1], buttonHeight)) {
            openDirectConnect();
            return true;
        }
        currentX += buttonWidths[1] + spacing;

        // Добавить
        if (isHovered(mouseX, mouseY, currentX, buttonY, buttonWidths[2], buttonHeight)) {
            openAddServer();
            return true;
        }
        currentX += buttonWidths[2] + spacing;

        // Изменить
        if (isHovered(mouseX, mouseY, currentX, buttonY, buttonWidths[3], buttonHeight) && selectedEntry != null) {
            openEditServer();
            return true;
        }
        currentX += buttonWidths[3] + spacing;

        // Удалить
        if (isHovered(mouseX, mouseY, currentX, buttonY, buttonWidths[4], buttonHeight) && selectedEntry != null) {
            deleteConfirmMode = true;
            return true;
        }
        currentX += buttonWidths[4] + spacing;

        // Обновить
        if (isHovered(mouseX, mouseY, currentX, buttonY, buttonWidths[5], buttonHeight)) {
            refreshServerList();
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        float listX = windowX + 8f;
        float listY = windowY + 78f;
        float listWidth = windowWidth - 16f;
        float listHeight = windowHeight - 78f - 50f;

        if (isHovered(mouseX, mouseY, listX, listY, listWidth, listHeight)) {
            float entryHeight = 60f;
            float entrySpacing = 6f;
            float totalHeight = getFilteredEntries().size() * (entryHeight + entrySpacing);

            scrollOffset += (float) delta * 20f;
            float minScroll = Math.min(0, listHeight - totalHeight);
            scrollOffset = MathHelper.clamp(scrollOffset, minScroll, 0);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchActive) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                filterServers();
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                searchActive = false;
                searchQuery = "";
                filterServers();
                return true;
            }
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.displayScreen(parentScreen);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchActive) {
            searchQuery += codePoint;
            filterServers();
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    private void connectToSelected() {
        if (selectedEntry != null) {
            minecraft.displayScreen(new ConnectingScreen(this, minecraft, selectedEntry.getServerData()));
        }
    }

    private void openDirectConnect() {
        ServerData tempServer = new ServerData(I18n.format("selectServer.defaultName"), "", false);
        minecraft.displayScreen(new CustomServerListScreen(this, (result) -> {
            if (result) {
                minecraft.displayScreen(new ConnectingScreen(this, minecraft, tempServer));
            } else {
                minecraft.displayScreen(this);
            }
        }, tempServer));
    }

    private void openAddServer() {
        ServerData newServer = new ServerData(I18n.format("selectServer.defaultName"), "", false);
        minecraft.displayScreen(new CustomAddServerScreen(this, (result) -> {
            if (result) {
                serverList.addServerData(newServer);
                serverList.saveServerList();
                loadServerEntries();
            }
            minecraft.displayScreen(this);
        }, newServer));
    }

    private void openEditServer() {
        if (selectedEntry != null) {
            ServerData editServer = new ServerData(
                    selectedEntry.getServerData().serverName,
                    selectedEntry.getServerData().serverIP,
                    false
            );
            editServer.copyFrom(selectedEntry.getServerData());

            minecraft.displayScreen(new CustomAddServerScreen(this, (result) -> {
                if (result) {
                    selectedEntry.getServerData().serverName = editServer.serverName;
                    selectedEntry.getServerData().serverIP = editServer.serverIP;
                    selectedEntry.getServerData().copyFrom(editServer);
                    serverList.saveServerList();
                    loadServerEntries();
                }
                minecraft.displayScreen(this);
            }, editServer));
        }
    }

    private void refreshServerList() {
        loadServerEntries();
        scrollOffset = 0f;
    }

    private void filterServers() {
        scrollOffset = 0f;
    }

    private List<ServerEntry> getFilteredEntries() {
        if (searchQuery.isEmpty()) {
            return serverEntries;
        }

        List<ServerEntry> filtered = new ArrayList<>();
        String query = searchQuery.toLowerCase();

        for (ServerEntry entry : serverEntries) {
            ServerData data = entry.getServerData();
            if (data.serverName.toLowerCase().contains(query) ||
                    data.serverIP.toLowerCase().contains(query)) {
                filtered.add(entry);
            }
        }

        return filtered;
    }

    private boolean isHovered(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void removed() {
        if (serverPinger != null) {
            serverPinger.clearPendingNetworks();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // Getter методы для ServerEntry
    public ServerPinger getServerPinger() {
        return serverPinger;
    }

    public ServerEntry getSelectedEntry() {
        return selectedEntry;
    }

    public ServerEntry getHoveredEntry() {
        return hoveredEntry;
    }
}
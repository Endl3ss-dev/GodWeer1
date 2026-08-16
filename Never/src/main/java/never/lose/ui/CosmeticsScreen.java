package never.lose.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import never.lose.NeverLose;
import never.lose.cosmetics.CosmeticEntry;
import never.lose.cosmetics.CosmeticSection;
import never.lose.cosmetics.CosmeticsInstaller;
import never.lose.cosmetics.CosmeticsPreview;
import never.lose.cosmetics.CosmeticsStore;
import never.lose.cosmetics.FiguraBridge;
import never.lose.ui.component.SearchField;
import never.lose.utils.animation.Animation;
import never.lose.utils.animation.Easing;
import never.lose.utils.font.Fonts;
import never.lose.utils.input.CursorManager;
import never.lose.utils.render.Draw;
import never.lose.utils.render.Hover;
import never.lose.utils.render.Render;
import never.lose.utils.render.Scissor;
import never.lose.utils.render.providers.ColorProvider;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Галерея косметики Figura: список аватаров с диска, поиск, избранное,
 * 3D-предпросмотр без применения и применение одним кликом.
 *
 * Все списки переиспользуются между кадрами, отрисовываются только видимые ряды,
 * фильтр пересчитывается лишь при смене revision/секции/запроса.
 */
public class CosmeticsScreen extends Screen {

    private static final float PADDING = 12f;
    private static final float HEADER_H = 34f;
    private static final float TAB_H = 18f;
    private static final float SEARCH_H = 18f;
    private static final float CARD_H = 46f;
    private static final float CARD_GAP = 4f;
    private static final float ROW_PITCH = CARD_H + CARD_GAP;
    private static final int COLUMNS = 3;

    private static CosmeticsScreen instance;

    private final CosmeticsStore store = CosmeticsStore.get();
    private final CosmeticsPreview preview = new CosmeticsPreview();
    private final SearchField searchField = new SearchField("Поиск косметики...");

    private final List<CosmeticEntry> snapshot = new ArrayList<>();
    private final List<CosmeticEntry> visible = new ArrayList<>();

    private final Animation appearAnim = new Animation(Easing.QUINTIC_OUT, 320);

    private CosmeticSection section = CosmeticSection.ALL;
    private boolean favoritesOnly;

    private int appliedRevision = -1;
    private CosmeticSection appliedSection;
    private String appliedQuery = "\u0000";
    private boolean appliedFavoritesOnly;

    private float scroll;
    private float gridX;
    private float gridY;
    private float gridW;
    private float gridH;

    private float previewX;
    private float previewY;
    private float previewW;
    private float previewH;

    private boolean dragging;
    private double lastMouseX;
    private double lastMouseY;

    private String status = "";
    private long statusTime;

    private CosmeticsScreen() {
        super(Text.of("Endless Cosmetics"));
    }

    public static CosmeticsScreen get() {
        if (instance == null) {
            instance = new CosmeticsScreen();
        }

        return instance;
    }

    public void playOpenAnimation() {
        appearAnim.reset(0f);
        searchField.resetAppear();
        searchField.setFocused(false);
        store.requestScan(true);

        if (!CosmeticsInstaller.isFinished() && !CosmeticsInstaller.isRunning()
                && CosmeticsInstaller.hasAnySource()) {
            setStatus("Распаковка косметики...");
            CosmeticsInstaller.installAsync(() -> {
                store.requestScan(true);
                setStatus("Установлено файлов: " + CosmeticsInstaller.getInstalled());
            });
        }
    }

    private void setStatus(String message) {
        status = message;
        statusTime = System.currentTimeMillis();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        CursorManager.reset();
        CursorManager.resetIBeam();
        CursorManager.resetClick();

        store.requestScan(false);
        preview.update();
        rebuildIfNeeded();

        int screenW = NeverLose.mc.getWindow().getScaledWidth();
        int screenH = NeverLose.mc.getWindow().getScaledHeight();

        appearAnim.run(true);
        float appear = appearAnim.getValue();

        float width = Math.min(560f, screenW - 40f);
        float height = Math.min(320f, screenH - 40f);
        float x = (screenW - width) / 2f;
        float y = (screenH - height) / 2f + (1f - appear) * 12f;

        int alpha = (int) (255 * appear);

        Draw.round(0, 0, screenW, screenH, 0f, ColorProvider.rgba(0, 0, 0, (int) (120 * appear)));
        Draw.blur(x, y, width, height, 8f, ColorProvider.rgba(200, 200, 200, alpha), 16f);
        Draw.round(x - 1f, y - 1f, width + 2f, height + 2f, 8.5f, ColorProvider.rgba(48, 66, 122, (int) (140 * appear)));
        Draw.round(x, y, width, height, 8f, ColorProvider.setAlpha(ColorProvider.getColorClickGui(), (int) (235 * appear)));

        renderHeader(context, x, y, width, appear);

        float bodyY = y + HEADER_H;
        float rightW = 168f;
        float leftW = width - rightW - PADDING * 3f;

        renderTabs(context, x + PADDING, bodyY, leftW, mouseX, mouseY, appear);

        searchField.setBounds(x + PADDING, bodyY + TAB_H + 5f, leftW, SEARCH_H);
        searchField.render(context, mouseX, mouseY, delta);

        gridX = x + PADDING;
        gridY = bodyY + TAB_H + SEARCH_H + 12f;
        gridW = leftW;
        gridH = y + height - gridY - 26f;

        renderGrid(context, mouseX, mouseY);
        renderScrollbar();

        previewX = x + PADDING * 2f + leftW;
        previewY = bodyY;
        previewW = rightW;
        previewH = y + height - previewY - 26f;

        renderPreviewPanel(context, mouseX, mouseY);
        renderStatusBar(x, y + height);
    }

    private void renderHeader(DrawContext context, float x, float y, float width, float appear) {
        Draw.text(Fonts.MEDIUM, "Cosmetics", x + PADDING, y + 11f,
                ColorProvider.setAlpha(ColorProvider.getColorText(), (int) (255 * appear)), 10f);

        String subtitle = store.size() + " моделей";

        if (store.isScanning()) {
            subtitle = "сканирование...";
        }

        float titleWidth = Draw.width(Fonts.MEDIUM, "Cosmetics", 10f);
        Draw.text(Fonts.REGULAR, subtitle, x + PADDING + titleWidth + 6f, y + 12.5f,
                ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), (int) (255 * appear)), 7f);

        String hint = FiguraBridge.isModPresent() ? "Figura: OK" : "Figura не найдена";
        int hintColor = FiguraBridge.isModPresent()
                ? ColorProvider.setAlpha(ColorProvider.getColorClient(), (int) (220 * appear))
                : ColorProvider.rgba(220, 90, 90, (int) (220 * appear));
        float hintWidth = Draw.width(Fonts.REGULAR, hint, 7f);
        Draw.text(Fonts.REGULAR, hint, x + width - PADDING - hintWidth, y + 12.5f, hintColor, 7f);

        Draw.round(x + PADDING, y + HEADER_H - 6f, width - PADDING * 2f, 0.75f, 0.4f,
                ColorProvider.rgba(48, 66, 122, (int) (110 * appear)));
    }

    private void renderTabs(DrawContext context, float x, float y, float width, int mouseX, int mouseY, float appear) {
        CosmeticSection[] sections = CosmeticSection.values();
        float gap = 3f;
        float tabW = (width - 60f - gap * sections.length) / sections.length;

        for (int i = 0; i < sections.length; i++) {
            CosmeticSection value = sections[i];
            float tabX = x + i * (tabW + gap);
            boolean hovered = Hover.isHovered(mouseX, mouseY, tabX, y, tabW, TAB_H);
            boolean selected = value == section;

            if (hovered) {
                CursorManager.requestHand();
            }

            int background = selected
                    ? ColorProvider.setAlpha(ColorProvider.getColorClient(), (int) (200 * appear))
                    : ColorProvider.setAlpha(ColorProvider.getColorInactiveButton(), (int) ((hovered ? 210 : 150) * appear));

            Draw.round(tabX, y, tabW, TAB_H, 4f, background);

            String title = value.getTitle();
            float textWidth = Draw.width(Fonts.REGULAR, title, 7f);
            Draw.text(Fonts.REGULAR, title, tabX + (tabW - textWidth) / 2f, y + TAB_H / 2f - 3.4f,
                    ColorProvider.setAlpha(selected
                            ? ColorProvider.getColorText()
                            : ColorProvider.getColorInactiveText(), (int) (255 * appear)), 7f);
        }

        float favX = x + width - 56f;
        boolean favHovered = Hover.isHovered(mouseX, mouseY, favX, y, 56f, TAB_H);

        if (favHovered) {
            CursorManager.requestHand();
        }

        Draw.round(favX, y, 56f, TAB_H, 4f, favoritesOnly
                ? ColorProvider.setAlpha(ColorProvider.getColorClient(), (int) (200 * appear))
                : ColorProvider.setAlpha(ColorProvider.getColorInactiveButton(), (int) ((favHovered ? 210 : 150) * appear)));
        Draw.text(Fonts.REGULAR, "Избранное", favX + 5f, y + TAB_H / 2f - 3.4f,
                ColorProvider.setAlpha(favoritesOnly
                        ? ColorProvider.getColorText()
                        : ColorProvider.getColorInactiveText(), (int) (255 * appear)), 6.5f);
    }

    private void rebuildIfNeeded() {
        String query = searchField.text.trim().toLowerCase(Locale.ROOT);
        int revision = store.getRevision();

        if (revision == appliedRevision
                && section == appliedSection
                && query.equals(appliedQuery)
                && favoritesOnly == appliedFavoritesOnly) {
            return;
        }

        appliedRevision = revision;
        appliedSection = section;
        appliedQuery = query;
        appliedFavoritesOnly = favoritesOnly;

        store.copyEntries(snapshot);
        visible.clear();

        for (int i = 0; i < snapshot.size(); i++) {
            CosmeticEntry entry = snapshot.get(i);

            if (section != CosmeticSection.ALL && entry.getSection() != section) {
                continue;
            }

            if (favoritesOnly && !store.isFavorite(entry.getFolder())) {
                continue;
            }

            if (!entry.matches(query)) {
                continue;
            }

            visible.add(entry);
        }

        scroll = 0f;
    }

    private int rowCount() {
        return (visible.size() + COLUMNS - 1) / COLUMNS;
    }

    private float maxScroll() {
        float content = rowCount() * ROW_PITCH - CARD_GAP;
        return Math.max(0f, content - gridH);
    }

    private void renderGrid(DrawContext context, int mouseX, int mouseY) {
        Scissor.push();
        Scissor.setFromComponentCoordinates(gridX, gridY, gridW, gridH);

        if (visible.isEmpty()) {
            String message = store.size() == 0
                    ? "Нет установленных аватаров"
                    : "Ничего не найдено";
            float textWidth = Draw.width(Fonts.REGULAR, message, 8f);
            Draw.text(Fonts.REGULAR, message, gridX + (gridW - textWidth) / 2f, gridY + gridH / 2f - 4f,
                    ColorProvider.getColorInactiveText(), 8f);

            if (store.size() == 0) {
                String hint = "Положи папки в .minecraft/endless/cosmetics";
                float hintWidth = Draw.width(Fonts.REGULAR, hint, 6.5f);
                Draw.text(Fonts.REGULAR, hint, gridX + (gridW - hintWidth) / 2f, gridY + gridH / 2f + 7f,
                        ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), 150), 6.5f);
            }

            Scissor.unset();
            Scissor.pop();
            return;
        }

        scroll = MathHelper.clamp(scroll, 0f, maxScroll());

        float cardW = (gridW - CARD_GAP * (COLUMNS - 1)) / COLUMNS;
        int rows = rowCount();
        int firstRow = Math.max(0, (int) Math.floor(scroll / ROW_PITCH));
        int lastRow = Math.min(rows - 1, (int) Math.floor((scroll + gridH - 0.001f) / ROW_PITCH));

        for (int row = firstRow; row <= lastRow; row++) {
            float cardY = gridY + row * ROW_PITCH - scroll;

            for (int column = 0; column < COLUMNS; column++) {
                int index = row * COLUMNS + column;

                if (index >= visible.size()) {
                    break;
                }

                renderCard(context, visible.get(index),
                        gridX + column * (cardW + CARD_GAP), cardY, cardW, mouseX, mouseY);
            }
        }

        Scissor.unset();
        Scissor.pop();
    }

    private void renderCard(DrawContext context, CosmeticEntry entry,
                            float x, float y, float width, int mouseX, int mouseY) {
        boolean hovered = Hover.isHovered(mouseX, mouseY, x, y, width, CARD_H)
                && Hover.isHovered(mouseX, mouseY, gridX, gridY, gridW, gridH);
        boolean selected = entry.getFolder().equals(store.getSelectedFolder());
        boolean applied = entry.getFolder().equals(store.getAppliedFolder());

        if (hovered) {
            CursorManager.requestHand();
        }

        int outline = applied
                ? ColorProvider.setAlpha(ColorProvider.getColorClient(), 255)
                : selected
                ? ColorProvider.rgba(120, 150, 240, 210)
                : hovered
                ? ColorProvider.rgba(100, 130, 220, 150)
                : ColorProvider.rgba(48, 66, 122, 80);

        float outlineSize = applied ? 1.4f : 0.75f;

        Draw.round(x - outlineSize, y - outlineSize, width + outlineSize * 2f, CARD_H + outlineSize * 2f, 4.5f, outline);
        Draw.round(x, y, width, CARD_H, 4f, ColorProvider.setAlpha(
                hovered || selected ? ColorProvider.getColorButton() : ColorProvider.getColorInactiveButton(), 205));

        Identifier texture = store.getPreviewTexture(entry);
        float iconSize = 28f;
        float iconX = x + 5f;
        float iconY = y + (CARD_H - iconSize) / 2f;

        if (texture != null) {
            Color tint = new Color(255, 255, 255, 255);
            Render.texture(iconX, iconY, iconSize, iconSize, texture, 0f, 0f, 1f, 1f, 3f, tint, tint, tint, tint);
        } else {
            Draw.round(iconX, iconY, iconSize, iconSize, 3f, ColorProvider.rgba(30, 40, 70, 200));
            Draw.text(Fonts.MEDIUM, "?", iconX + iconSize / 2f - 2f, iconY + iconSize / 2f - 4f,
                    ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), 200), 8f);
        }

        float textX = iconX + iconSize + 6f;
        float textW = width - (textX - x) - 16f;

        Draw.text(Fonts.MEDIUM, entry.getName(), textX, y + 10f,
                ColorProvider.getColorText(), 7.5f, 0f, 0f, textW);
        Draw.text(Fonts.REGULAR, entry.getSection().getTitle(), textX, y + 21f,
                ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), 200), 6.5f, 0f, 0f, textW);

        int sizeColor = entry.isUploadable()
                ? ColorProvider.setAlpha(ColorProvider.getColorClient(), 220)
                : ColorProvider.rgba(220, 150, 80, 220);
        Draw.text(Fonts.REGULAR, entry.getSizeLabel(), textX, y + 31f, sizeColor, 6.5f);

        if (!entry.isUploadable()) {
            String local = "локально";
            float sizeWidth = Draw.width(Fonts.REGULAR, entry.getSizeLabel(), 6.5f);
            Draw.text(Fonts.REGULAR, local, textX + sizeWidth + 5f, y + 31f,
                    ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), 170), 6.5f);
        }

        boolean favorite = store.isFavorite(entry.getFolder());
        Draw.text(Fonts.ICONS, "j", x + width - 12f, y + 6f,
                favorite ? ColorProvider.getColorClient()
                        : ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), 120), 8f);
    }

    private void renderScrollbar() {
        float max = maxScroll();

        if (max <= 0f) {
            return;
        }

        float content = rowCount() * ROW_PITCH - CARD_GAP;
        float trackX = gridX + gridW + 2.5f;
        float thumbH = Math.max(16f, gridH * (gridH / content));
        float thumbY = gridY + (gridH - thumbH) * (scroll / max);

        Draw.round(trackX, gridY, 1.5f, gridH, 0.75f, ColorProvider.rgba(48, 66, 122, 90));
        Draw.round(trackX, thumbY, 1.5f, thumbH, 0.75f, ColorProvider.setAlpha(ColorProvider.getColorClient(), 210));
    }

    private void renderPreviewPanel(DrawContext context, int mouseX, int mouseY) {
        Draw.round(previewX - 0.75f, previewY - 0.75f, previewW + 1.5f, previewH + 1.5f, 5f,
                ColorProvider.rgba(48, 66, 122, 90));
        Draw.round(previewX, previewY, previewW, previewH, 4.5f,
                ColorProvider.rgba(8, 12, 24, 230));

        CosmeticEntry selected = store.findByFolder(store.getSelectedFolder());
        float modelH = previewH - 58f;

        if (selected == null) {
            String message = "Выбери модель";
            float textWidth = Draw.width(Fonts.REGULAR, message, 7.5f);
            Draw.text(Fonts.REGULAR, message, previewX + (previewW - textWidth) / 2f,
                    previewY + modelH / 2f - 4f, ColorProvider.getColorInactiveText(), 7.5f);
        } else {
            Scissor.push();
            Scissor.setFromComponentCoordinates(previewX + 1f, previewY + 1f, previewW - 2f, modelH - 2f);

            boolean rendered = preview.render(context, previewX, previewY, previewW, modelH);

            if (!rendered) {
                renderPreviewFallback(selected, modelH);
            }

            Scissor.unset();
            Scissor.pop();

            Draw.text(Fonts.MEDIUM, selected.getName(), previewX + 8f, previewY + modelH + 4f,
                    ColorProvider.getColorText(), 7.5f, 0f, 0f, previewW - 16f);
        }

        float buttonY = previewY + previewH - 34f;
        float buttonW = previewW - 16f;

        boolean applyHovered = Hover.isHovered(mouseX, mouseY, previewX + 8f, buttonY, buttonW, 15f);
        boolean removeHovered = Hover.isHovered(mouseX, mouseY, previewX + 8f, buttonY + 18f, buttonW, 13f);

        if (applyHovered || removeHovered) {
            CursorManager.requestHand();
        }

        boolean canApply = selected != null && FiguraBridge.isModPresent();

        Draw.round(previewX + 8f, buttonY, buttonW, 15f, 3.5f, canApply
                ? ColorProvider.setAlpha(ColorProvider.getColorClient(), applyHovered ? 255 : 210)
                : ColorProvider.rgba(40, 46, 70, 190));

        String applyLabel = canApply ? "Применить" : "Нужна Figura";
        float applyWidth = Draw.width(Fonts.MEDIUM, applyLabel, 7f);
        Draw.text(Fonts.MEDIUM, applyLabel, previewX + 8f + (buttonW - applyWidth) / 2f, buttonY + 4f,
                ColorProvider.getColorText(), 7f);

        Draw.round(previewX + 8f, buttonY + 18f, buttonW, 13f, 3.5f,
                ColorProvider.rgba(60, 26, 34, removeHovered ? 235 : 190));

        String removeLabel = "Снять аватар";
        float removeWidth = Draw.width(Fonts.REGULAR, removeLabel, 6.5f);
        Draw.text(Fonts.REGULAR, removeLabel, previewX + 8f + (buttonW - removeWidth) / 2f, buttonY + 21.5f,
                ColorProvider.setAlpha(ColorProvider.getColorText(), 220), 6.5f);
    }

    private void renderPreviewFallback(CosmeticEntry entry, float modelH) {
        Identifier texture = store.getPreviewTexture(entry);

        if (texture != null) {
            float size = Math.min(previewW - 24f, modelH - 24f);
            Color tint = new Color(255, 255, 255, 255);
            Render.texture(previewX + (previewW - size) / 2f, previewY + (modelH - size) / 2f,
                    size, size, texture, 0f, 0f, 1f, 1f, 4f, tint, tint, tint, tint);
        }

        String message = switch (preview.getState()) {
            case LOADING -> "Загрузка 3D...";
            case FAILED -> FiguraBridge.isModPresent() ? "3D недоступно" : "Figura не установлена";
            default -> "";
        };

        if (message.isEmpty()) {
            return;
        }

        float textWidth = Draw.width(Fonts.REGULAR, message, 6.5f);
        Draw.text(Fonts.REGULAR, message, previewX + (previewW - textWidth) / 2f, previewY + modelH - 12f,
                ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), 210), 6.5f);
    }

    private void renderStatusBar(float x, float bottom) {
        if (status.isEmpty() || System.currentTimeMillis() - statusTime > 6000L) {
            return;
        }

        Draw.text(Fonts.REGULAR, status, x + PADDING, bottom - 16f,
                ColorProvider.setAlpha(ColorProvider.getColorText(), 210), 6.5f);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        searchField.mouseClicked(mouseX, mouseY, button);

        if (handleTabsClick(mouseX, mouseY, button)) {
            return true;
        }

        if (handlePreviewClick(mouseX, mouseY, button)) {
            return true;
        }

        if (handleGridClick(mouseX, mouseY, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleTabsClick(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }

        int screenW = NeverLose.mc.getWindow().getScaledWidth();
        int screenH = NeverLose.mc.getWindow().getScaledHeight();
        float width = Math.min(560f, screenW - 40f);
        float height = Math.min(320f, screenH - 40f);
        float x = (screenW - width) / 2f;
        float y = (screenH - height) / 2f;
        float bodyY = y + HEADER_H;
        float rightW = 168f;
        float leftW = width - rightW - PADDING * 3f;

        CosmeticSection[] sections = CosmeticSection.values();
        float gap = 3f;
        float tabW = (leftW - 60f - gap * sections.length) / sections.length;

        for (int i = 0; i < sections.length; i++) {
            float tabX = x + PADDING + i * (tabW + gap);

            if (Hover.isHovered(mouseX, mouseY, tabX, bodyY, tabW, TAB_H)) {
                section = sections[i];
                return true;
            }
        }

        float favX = x + PADDING + leftW - 56f;

        if (Hover.isHovered(mouseX, mouseY, favX, bodyY, 56f, TAB_H)) {
            favoritesOnly = !favoritesOnly;
            return true;
        }

        return false;
    }

    private boolean handlePreviewClick(double mouseX, double mouseY, int button) {
        if (!Hover.isHovered(mouseX, mouseY, previewX, previewY, previewW, previewH)) {
            return false;
        }

        float buttonY = previewY + previewH - 34f;
        float buttonW = previewW - 16f;

        if (button == 0 && Hover.isHovered(mouseX, mouseY, previewX + 8f, buttonY, buttonW, 15f)) {
            applySelected();
            return true;
        }

        if (button == 0 && Hover.isHovered(mouseX, mouseY, previewX + 8f, buttonY + 18f, buttonW, 13f)) {
            if (FiguraBridge.clearLocalAvatar()) {
                store.setAppliedFolder("");
                setStatus("Аватар снят");
            } else {
                setStatus("Не удалось снять аватар");
            }

            return true;
        }

        if (button == 0) {
            dragging = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }

        if (button == 1) {
            preview.resetView();
            return true;
        }

        return false;
    }

    private boolean handleGridClick(double mouseX, double mouseY, int button) {
        if (!Hover.isHovered(mouseX, mouseY, gridX, gridY, gridW, gridH) || visible.isEmpty()) {
            return false;
        }

        float cardW = (gridW - CARD_GAP * (COLUMNS - 1)) / COLUMNS;
        int row = (int) Math.floor((mouseY - gridY + scroll) / ROW_PITCH);
        int column = (int) Math.floor((mouseX - gridX) / (cardW + CARD_GAP));

        if (row < 0 || column < 0 || column >= COLUMNS) {
            return false;
        }

        int index = row * COLUMNS + column;

        if (index >= visible.size()) {
            return false;
        }

        CosmeticEntry entry = visible.get(index);
        float cardX = gridX + column * (cardW + CARD_GAP);
        float cardY = gridY + row * ROW_PITCH - scroll;

        if (button == 0 && Hover.isHovered(mouseX, mouseY, cardX + cardW - 16f, cardY + 2f, 16f, 14f)) {
            store.toggleFavorite(entry.getFolder());
            return true;
        }

        if (button == 0) {
            store.setSelectedFolder(entry.getFolder());
            preview.request(entry);
            preview.resetView();
            return true;
        }

        if (button == 1) {
            store.setSelectedFolder(entry.getFolder());
            preview.request(entry);
            applySelected();
            return true;
        }

        return false;
    }

    private void applySelected() {
        CosmeticEntry entry = store.findByFolder(store.getSelectedFolder());

        if (entry == null) {
            setStatus("Модель не выбрана");
            return;
        }

        if (!FiguraBridge.isModPresent()) {
            setStatus("Установи мод Figura 0.1.5 для 1.21.1");
            return;
        }

        if (FiguraBridge.applyAvatar(entry.getPath())) {
            store.setAppliedFolder(entry.getFolder());
            setStatus(entry.isUploadable()
                    ? "Применено: " + entry.getName()
                    : "Применено локально (>100 KB): " + entry.getName());
        } else {
            setStatus("Ошибка Figura: " + FiguraBridge.getLastError());
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging && button == 0) {
            preview.rotate((float) (mouseX - lastMouseX) * 1.6f, (float) (mouseY - lastMouseY) * -1.1f);
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (Hover.isHovered(mouseX, mouseY, previewX, previewY, previewW, previewH)) {
            preview.zoom((float) verticalAmount * 0.12f);
            return true;
        }

        if (Hover.isHovered(mouseX, mouseY, gridX, gridY, gridW, gridH)) {
            scroll = MathHelper.clamp(scroll - (float) verticalAmount * 18f, 0f, maxScroll());
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_F5) {
            store.requestScan(true);
            setStatus("Обновление списка...");
            return true;
        }

        searchField.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        searchField.charTyped(chr, modifiers);
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void removed() {
        super.removed();
        preview.clear();
        dragging = false;
    }
}

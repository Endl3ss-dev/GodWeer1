package never.lose.screen.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import never.lose.alts.AltEntry;
import never.lose.alts.AltSession;
import never.lose.alts.AltStorage;
import never.lose.ui.object.RenderObject;
import never.lose.utils.render.Render;
import never.lose.managers.ThemeManager;
import never.lose.screen.settings.StyledTextField;
import never.lose.utils.color.ClientColors;
import never.lose.utils.math.MathUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class AltManagerPanel extends RenderObject {

    private static final float PAD = 12f;
    private static final float FIELD_H = 24f;
    private static final float ENTRY_H = 34f;
    private static final float SCROLL_STEP = 24f;
    private static final float HEAD_SIZE = 20f;
    private static final float HEAD_RADIUS = 6f;
    private static final String HINT = "LMB - Login  |  RMB - Delete  |  MMB - Favorite";

    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    private static final Color DATE_COLOR = new Color(160, 160, 160, 180);
    private static final Color HEAD_STROKE = new Color(255, 255, 255, 20);
    private static final Color ENTRY_HOVER_BG = new Color(255, 255, 255, 25);
    private static final Color FAVORITE_BG = new Color(255, 200, 60, 14);
    private static final Color FAVORITE_STROKE = new Color(255, 200, 60, 45);
    private static final Color FAVORITE_MARK = new Color(255, 200, 60, 190);

    private float anim = 0f;
    private boolean open = false;

    private final List<AltEntry> entries = new ArrayList<>();
    private final List<AltEntry> view = new ArrayList<>();
    private String lastSearchRaw = null;

    private float scroll = 0f;
    private float scrollTarget = 0f;
    private float screenH = 480f;
    private float renderY;

    private StyledTextField searchField;
    private StyledTextField nameField;

    private Runnable onLogin;

    public static AltManagerPanel build(float x, float y, float w, float h) {
        AltManagerPanel panel = new AltManagerPanel();
        panel.x = x;
        panel.y = y;
        panel.width = w;
        panel.height = h;
        panel.renderY = y;
        return panel;
    }

    public void setOnLogin(Runnable onLogin) {
        this.onLogin = onLogin;
    }

    public void init(Screen screen) {
        screenH = screen.height;

        float innerX = x + PAD;
        float innerW = width - PAD * 2f;

        searchField = StyledTextField.build(innerX, y + 24, innerW, FIELD_H,
                "s", "Поиск...", 64);
        searchField.setVisible(false);

        nameField = StyledTextField.build(innerX, y + 52, innerW, FIELD_H,
                "u", "Ник + Enter...", 16);
        nameField.setCharFilter(StyledTextField.USERNAME_FILTER);
        nameField.setOnSubmit(this::createFromField);
        nameField.setVisible(false);

        entries.addAll(AltStorage.load());
        sortEntries();
        ensureView();
    }

    public void toggle() {
        open = !open;
        if (!open) {
            searchField.setFocused(false);
            nameField.setFocused(false);
        }
    }

    public boolean isOpen() {
        return open;
    }

    public float getAnim() {
        return anim;
    }

    private void createFromField() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) return;

        AltEntry existing = findByName(name);
        if (existing == null) {
            existing = new AltEntry(name, System.currentTimeMillis());
            entries.add(existing);
            sortEntries();
            AltStorage.save(entries);
        }
        invalidateView();
        ensureView();
        ensureVisible(existing);
        nameField.setText("");
    }

    @Override
    public void render(int mouseX, int mouseY) {
        anim = MathUtil.fastAnim(anim, open ? 1f : 0f, 12f);
        if (anim < 0.01f && !open) return;

        renderY = y + slideOffset();

        scroll = MathUtil.fastAnim(scroll, scrollTarget, 16f);
        if (Math.abs(scroll - scrollTarget) < 0.3f) scroll = scrollTarget;

        searchField.y = renderY + 24f;
        nameField.y = renderY + 52f;

        ensureView();

        int strokeAlpha = clamp((int) (25 * anim));
        Color stroke = new Color(255, 255, 255, strokeAlpha);

        Render.outline(x, renderY, width, height, 8, 8, 8, 8, 0.5f, 0f,
                TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT,
                stroke, stroke, stroke, stroke);

        Color titleColor = Render.injectAlpha(ClientColors.FORE_COLOR, clamp((int) (255 * anim)));
        Render.textCentered("Alt Manager",
                x + width / 2f, renderY + 10f,
                "sfbold", 12, titleColor);

        boolean showFields = open && anim > 0.3f;
        searchField.setVisible(showFields);
        nameField.setVisible(showFields);

        if (showFields) {
            searchField.render(mouseX, mouseY);
            nameField.render(mouseX, mouseY);
        }

        if (anim > 0.5f) {
            renderEntries(mouseX, mouseY);

            Color hintColor = Render.injectAlpha(DATE_COLOR, clamp((int) (180 * anim)));
            Render.textCentered(HINT, x + width / 2f, renderY + height - 14f, "sfmedium", 7, hintColor);
        }
    }

    public void renderHeads() {
        if (anim < 0.5f) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        float areaH = listBottom() - listTop();
        int first = Math.max(0, (int) (scroll / ENTRY_H));
        int last = Math.min(view.size(), (int) ((scroll + areaH) / ENTRY_H) + 1);

        for (int i = first; i < last; i++) {
            float ey = listTop() + i * ENTRY_H - scroll;
            if (!entryFullyVisible(ey)) continue;

            AltEntry entry = view.get(i);
            float hx = x + PAD + 6f;
            float hy = ey + 7f;

            Identifier skinId = DefaultSkinHelper.getSkinTextures(entry.getUuid()).texture();
            AbstractTexture skin = mc.getTextureManager().getTexture(skinId);

            Render.texture(skin, hx, hy, HEAD_SIZE, HEAD_SIZE,
                    8f / 64f, 8f / 64f, 8f / 64f, 8f / 64f,
                    HEAD_RADIUS, HEAD_RADIUS, HEAD_RADIUS, HEAD_RADIUS,
                    Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE);
            Render.texture(skin, hx, hy, HEAD_SIZE, HEAD_SIZE,
                    40f / 64f, 8f / 64f, 8f / 64f, 8f / 64f,
                    HEAD_RADIUS, HEAD_RADIUS, HEAD_RADIUS, HEAD_RADIUS,
                    Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE);

            Render.outline(hx, hy, HEAD_SIZE, HEAD_SIZE,
                    HEAD_RADIUS, HEAD_RADIUS, HEAD_RADIUS, HEAD_RADIUS, 0.6f, 0f,
                    TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT,
                    HEAD_STROKE, HEAD_STROKE, HEAD_STROKE, HEAD_STROKE);
        }
    }

    private void renderEntries(int mouseX, int mouseY) {
        float entryX = x + PAD;
        float entryW = width - PAD * 2f;
        float areaH = listBottom() - listTop();

        int first = Math.max(0, (int) (scroll / ENTRY_H));
        int last = Math.min(view.size(), (int) ((scroll + areaH) / ENTRY_H) + 1);

        for (int i = first; i < last; i++) {
            float ey = listTop() + i * ENTRY_H - scroll;
            if (!entryFullyVisible(ey)) continue;

            AltEntry entry = view.get(i);

            boolean hovered = MathUtil.isInPosition(entryX, ey, entryW, ENTRY_H - 2, mouseX, mouseY);
            entry.hoverAnim = MathUtil.fastAnim(entry.hoverAnim, hovered ? 1f : 0f, 10f);

            Color entryBg = entry.isFavorite()
                    ? FAVORITE_BG
                    : ThemeManager.interpolateColor(TRANSPARENT, ENTRY_HOVER_BG, entry.hoverAnim);

            Render.round(entryX, ey, entryW, ENTRY_H - 2, 6, 6, 6, 6, 0,
                    entryBg, entryBg, entryBg, entryBg);

            if (entry.isFavorite()) {
                Render.outline(entryX, ey, entryW, ENTRY_H - 2, 6, 6, 6, 6, 0.6f, 0f,
                        TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT,
                        FAVORITE_STROKE, FAVORITE_STROKE, FAVORITE_STROKE, FAVORITE_STROKE);

                Render.textCentered("*", entryX + entryW - 10f, ey + 4f, "sfbold", 10, FAVORITE_MARK);
            }

            float textX = entryX + 34f;
            String name = entry.getName();
            float nameW = Render.textWidth(name, "sfmedium", 10);
            Render.textCentered(name, textX + nameW / 2f, ey + 5f, "sfmedium", 10, ClientColors.FORE_COLOR);

            String date = entry.getDate();
            float dateW = Render.textWidth(date, "sfmedium", 8);
            Render.textCentered(date, textX + dateW / 2f, ey + 18f, "sfmedium", 8, DATE_COLOR);
        }
    }

    @Override
    public boolean click(int mouseX, int mouseY, int button) {
        if (anim < 0.5f) return false;

        searchField.click(mouseX, mouseY, button);
        nameField.click(mouseX, mouseY, button);

        float entryX = x + PAD;
        float entryW = width - PAD * 2f;
        float areaH = listBottom() - listTop();

        if (!MathUtil.isInPosition(x, listTop(), width, areaH, mouseX, mouseY)) return false;

        int first = Math.max(0, (int) (scroll / ENTRY_H));
        int last = Math.min(view.size(), (int) ((scroll + areaH) / ENTRY_H) + 1);

        for (int i = first; i < last; i++) {
            float ey = listTop() + i * ENTRY_H - scroll;
            if (!entryFullyVisible(ey)) continue;
            if (!MathUtil.isInPosition(entryX, ey, entryW, ENTRY_H - 2, mouseX, mouseY)) continue;

            AltEntry entry = view.get(i);
            if (button == 0) {
                AltSession.login(entry);
                if (onLogin != null) onLogin.run();
            } else if (button == 1) {
                entries.remove(entry);
                invalidateView();
                ensureView();
                AltStorage.save(entries);
            } else if (button == 2) {
                entry.toggleFavorite();
                sortEntries();
                invalidateView();
                ensureView();
                ensureVisible(entry);
                AltStorage.save(entries);
            }
            break;
        }
        return false;
    }

    @Override
    public void charTyped(char ch, int keyCode) {
        searchField.charTyped(ch, keyCode);
        nameField.charTyped(ch, keyCode);
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        searchField.keyPressed(keyCode, scanCode, modifiers);
        nameField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void scroll(int mouseX, int mouseY, float scrollY) {
        if (!open || scrollY == 0f) return;
        if (!MathUtil.isInPosition(x, listTop(), width, listBottom() - listTop(), mouseX, mouseY)) return;
        scrollTarget = MathHelper.clamp(scrollTarget - scrollY * SCROLL_STEP, 0f, maxScroll());
    }

    private float slideOffset() {
        float inv = 1f - anim;
        if (open) return -(y + height + 12f) * inv;   // открытие — выезд сверху
        return (screenH - y + 12f) * inv;             // закрытие — уезд вниз
    }

    private float listTop() {
        return renderY + 80f;
    }

    private float listBottom() {
        return renderY + height - 24f;
    }

    private boolean entryFullyVisible(float ey) {
        // в шейдерном пайплайне нет scissor — частично видимые записи пропускаем,
        // иначе текст/голова вылезут за границы списка
        return ey >= listTop() - 0.01f && ey + ENTRY_H - 2f <= listBottom() + 0.01f;
    }

    private float maxScroll() {
        return Math.max(0f, view.size() * ENTRY_H - (listBottom() - listTop()));
    }

    private void clampScrollTarget() {
        scrollTarget = MathHelper.clamp(scrollTarget, 0f, maxScroll());
    }

    private void ensureVisible(AltEntry entry) {
        int index = view.indexOf(entry);
        if (index < 0) return;

        float areaH = listBottom() - listTop();
        float top = index * ENTRY_H;
        if (top < scrollTarget) {
            scrollTarget = top;
        } else if (top + ENTRY_H > scrollTarget + areaH) {
            scrollTarget = top + ENTRY_H - areaH;
        }
        clampScrollTarget();
    }

    private void sortEntries() {
        entries.sort(Comparator.comparing(AltEntry::isFavorite).reversed());
    }

    private AltEntry findByName(String name) {
        for (AltEntry entry : entries) {
            if (entry.getName().equalsIgnoreCase(name)) return entry;
        }
        return null;
    }

    private void invalidateView() {
        lastSearchRaw = null;
    }

    private void ensureView() {
        String raw = searchField.getText();
        if (lastSearchRaw != null && raw.equals(lastSearchRaw)) return;

        lastSearchRaw = raw;
        view.clear();

        String query = raw.trim().toLowerCase(Locale.ROOT);
        for (AltEntry entry : entries) {
            if (query.isEmpty() || entry.getName().toLowerCase(Locale.ROOT).contains(query)) {
                view.add(entry);
            }
        }
        clampScrollTarget();
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}
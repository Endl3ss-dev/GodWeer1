package net.skycore.client.gui.workspace;

import net.skycore.client.bot.BedWarsBot;
import net.skycore.client.bot.BotConfig;
import net.skycore.client.bot.BotAppearance;
import net.skycore.client.bot.BotScript;
import net.skycore.client.bot.BotStage;
import net.skycore.client.bot.BotStatus;
import net.skycore.client.gui.menu.MenuAnim;
import net.skycore.client.gui.menu.MenuDraw;
import net.skycore.client.gui.menu.MenuPrefs;
import net.skycore.client.gui.menu.MenuText.Weight;
import net.skycore.client.util.IconGlyphs;
import net.skycore.client.util.LegacyIconGlyphs;
import net.skycore.client.util.SelectAndSaveHelper;

/**
 * Вкладка «Бот» в меню Workspace — визуальная часть бота BedWars.
 *
 * <p>Это <b>только интерфейс</b>: ни подключения к серверу, ни пакетов, ни кликов по
 * игровым меню здесь нет. Панель показывает сценарий из {@link BotScript} и
 * {@link BotStage}, настройки из {@link BotConfig} и состояние заглушки
 * {@link BedWarsBot}, чтобы было видно, как вкладка будет выглядеть, когда шаги
 * сценария получат настоящую реализацию.
 *
 * <p>Компоновка повторяет остальной Workspace намеренно: те же две колонки
 * (широкая слева, узкая справа), та же карточная подложка {@link WorkspaceTheme},
 * те же разделители, тумблеры, чипы и скроллбары из {@link WorkspaceDraw}, та же
 * единица масштаба {@link WorkspaceLayout#s(float)}. Новая вкладка не должна
 * выглядеть вставленной сверху — она продолжает уже нарисованное меню.
 *
 * <p>Правила кадра SkyCore соблюдены (см. {@code docs/GUI-SELECTION.md}, раздел 3):
 * <ul>
 *   <li>анимации — целочисленные ключи {@link MenuAnim} в свободном диапазоне
 *       {@link #KEY_BASE}, не пересекающемся с карточками модулей (3000+) и строками
 *       настроек (9000+);</li>
 *   <li>хит-области — через {@link WorkspaceSettings.Sink}, типы {@code 130+}, вне
 *       диапазонов экрана (100…116), настроек (1…8) и пикера цвета (20…25);</li>
 *   <li>в отрисовке нет лямбд и явных {@code new}: номера ячеек, ники ботов,
 *       число ботов и строки журнала подготовлены заранее — в {@link BotScript},
 *       {@link BotConfig} и {@link BedWarsBot};</li>
 *   <li>строки вне видимой области не рисуются.</li>
 * </ul>
 */
final class WorkspaceBotPanel {

    // ---- типы хит-областей (вне диапазонов экрана, настроек и пикера)
    static final int HIT_START = 130;
    static final int HIT_STOP = 131;
    static final int HIT_STEP = 132;
    static final int HIT_CLEAR_LOG = 133;
    static final int HIT_STRING = 134;
    static final int HIT_SLIDER = 135;
    static final int HIT_VOTE = 136;
    static final int HIT_APPEARANCE = 137;
    static final int HIT_TEST_ERROR = 138;

    // ---- поля ввода
    static final int FIELD_PREFIX = 0;
    static final int FIELD_PASSWORD = 1;
    static final int FIELD_OWNER = 2;
    static final int FIELD_COUNT = 3;

    /** База ключей анимаций панели: свободно между карточками (3000+) и настройками (9000+). */
    private static final int KEY_BASE = 5000;

    // ---- геометрия в единицах меню (не в пикселях)
    private static final float PAD = 14.0F;
    private static final float HEAD = 38.0F;
    private static final float SECTION = 26.0F;
    private static final float SECTION_GAP = 16.0F;
    private static final float SERVER_H = 54.0F;
    private static final float STAGE_ROW = 32.0F;
    private static final float STRIP_H = 40.0F;
    private static final float CAPTION = 16.0F;
    private static final float VOTE_ROW = 40.0F;
    private static final float SWATCH = 26.0F;
    private static final float SWATCH_GAP = 6.0F;
    private static final float FIELD_ROW = 30.0F;
    private static final float SLIDER_ROW = 44.0F;
    private static final float BUTTON_ROW = 30.0F;
    private static final float BOT_ROW = 26.0F;
    private static final float LOG_ROW = 15.0F;

    /** Сколько строк журнала показывается в консоли. */
    private static final int LOG_LINES = 12;

    /** Оттенок «появится позже»: в палитре клиента такого токена нет. */
    private static final int LATER_COLOR = 0xFFE0A63C;

    /** Подписи слотов хотбара 1…9 — подготовлены один раз, не в кадре. */
    private static final String[] SLOT_TEXT = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9",
    };

    /** Номера этапов сценария — подготовлены один раз, не в кадре. */
    private static final String[] STAGE_TEXT = new String[BotStage.flow().length];

    static {
        for (int i = 0; i < STAGE_TEXT.length; i++) {
            STAGE_TEXT[i] = Integer.toString(i + 1);
        }
    }

    private float leftTotal;
    private float rightTotal;

    WorkspaceBotPanel() {
    }

    /** Полная высота содержимого левой колонки (для прокрутки). */
    float leftTotal() {
        return this.leftTotal;
    }

    /** Полная высота содержимой правой колонки (для прокрутки). */
    float rightTotal() {
        return this.rightTotal;
    }

    /**
     * Рисует обе колонки вкладки.
     *
     * @param live       {@code false}, когда меню закрывается: хит-области не регистрируются
     * @param editField  поле ввода, в котором сейчас стоит курсор (-1 — нет)
     * @param editBuffer текст этого поля
     */
    void render(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim, float mouseX, float mouseY,
                boolean live, int editField, String editBuffer, float leftScroll, float rightScroll,
                WorkspaceSettings.Sink sink) {
        this.leftTotal = leftHeight(l);
        this.rightTotal = rightHeight(l);
        drawLeft(l, t, anim, mouseX, mouseY, live, leftScroll, sink);
        drawRight(l, t, anim, mouseX, mouseY, live, editField, editBuffer, rightScroll, sink);
    }

    // ---- высоты (чистая математика, считается до отрисовки)

    private float leftHeight(WorkspaceLayout l) {
        int stages = BotStage.flow().length - 1;
        float units = PAD * 2.0F + 8.0F
                + SERVER_H + SECTION_GAP
                + SECTION + stages * STAGE_ROW + SECTION_GAP
                + SECTION + STRIP_H + 6.0F + CAPTION + SECTION_GAP
                + SECTION + STRIP_H + 6.0F + CAPTION + SECTION_GAP
                + SECTION + BotScript.VOTES.length * VOTE_ROW + SECTION_GAP
                + SECTION + 2.0F * (SWATCH + SWATCH_GAP) + CAPTION;
        return l.s(units);
    }

    private float rightHeight(WorkspaceLayout l) {
        int bots = BedWarsBot.INSTANCE.displayCount();
        float units = PAD * 2.0F + 8.0F
                + SECTION + FIELD_ROW + SLIDER_ROW + FIELD_ROW + FIELD_ROW + SECTION_GAP
                + SECTION + BUTTON_ROW * 2.0F + 8.0F + SECTION_GAP
                + SECTION + botCardsHeight() + SECTION_GAP
                + SECTION + (BedWarsBot.INSTANCE.partySize() + 3) * 22.0F + SECTION_GAP
                + SECTION + LOG_LINES * LOG_ROW;
        return l.s(units);
    }

    private float botCardsHeight() {
        float height = 0;
        for (int i = 0; i < BedWarsBot.INSTANCE.displayCount(); i++)
            height += BotAppearance.compact(i) ? 70.0F : 92.0F;
        return height;
    }

    // ---- левая колонка: сценарий

    private void drawLeft(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim, float mouseX, float mouseY,
                          boolean live, float scroll, WorkspaceSettings.Sink sink) {
        float x = l.listX;
        float y = l.listY;
        float w = l.listW;
        float h = l.listH;
        float radius = l.s(11.0F);
        float pad = l.s(PAD);

        WorkspaceDraw.pane(x, y, w, h, radius, t);

        float head = l.s(HEAD);
        MenuDraw.textMid(title(), x + pad, y, head, Weight.BOLD, l.s(12.5F), t.text);
        MenuDraw.textRight("DEMO", x + w - pad, y + l.s(14), Weight.SEMIBOLD, l.s(9), t.accent2);
        WorkspaceDraw.divider(l, x + pad, y + head - l.line(), w - pad * 2.0F, t);

        float viewTop = y + head;
        float viewBottom = y + h - l.s(6.0F);
        float viewHeight = Math.max(0.0F, viewBottom - viewTop);
        float contentX = x + pad;
        float contentWidth = w - pad * 2.0F;

        MenuDraw.scissor(x + l.s(1.0F), viewTop, w - l.s(2.0F), viewHeight);
        try {
            float cursor = viewTop + l.s(8.0F) - scroll;
            cursor = serverSection(l, t, anim, contentX, cursor, contentWidth);
            cursor = stageSection(l, t, anim, contentX, cursor, contentWidth, mouseY, viewTop, viewBottom);
            cursor = hotbarSection(l, t, anim, contentX, cursor, contentWidth, mouseX, mouseY,
                    BotScript.LOBBY_HOTBAR, lobbyTitle(), lobbyHint(), live, KEY_BASE + 40);
            cursor = hotbarSection(l, t, anim, contentX, cursor, contentWidth, mouseX, mouseY,
                    BotScript.GAME_HOTBAR, gameTitle(), gameHint(), live, KEY_BASE + 70);
            cursor = voteSection(l, t, anim, contentX, cursor, contentWidth, mouseX, mouseY,
                    viewTop, viewBottom, live, sink);
            swatchSection(l, t, anim, contentX, cursor, contentWidth, mouseX, mouseY,
                    viewTop, viewBottom);
        } finally {
            MenuDraw.scissorEnd();
        }

        WorkspaceDraw.scrollbar(l, t, x + w - l.s(6.0F), viewTop, viewHeight, this.leftTotal, scroll,
                mouseX, mouseY);
    }

    /** Плашка сервера: адрес зафиксирован задачей, поэтому показан заблокированным. */
    private float serverSection(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim,
                               float x, float y, float width) {
        float height = l.s(SERVER_H);
        MenuDraw.round(x, y, width, height, l.s(9.0F), t.inset);
        MenuDraw.outline(x, y, width, height, l.s(9.0F), t.border);

        float iconBox = l.s(28.0F);
        float iconY = y + (height - iconBox) * 0.5F;
        MenuDraw.round(x + l.s(8.0F), iconY, iconBox, iconBox, l.s(8.0F), MenuDraw.a(t.accent, t.light ? 22 : 34));
        MenuDraw.iconCs(IconGlyphs.CONFIGS, x + l.s(8.0F) + iconBox * 0.5F, iconY + iconBox * 0.5F,
                l.s(13.0F), t.light ? t.accent : t.accent2);

        float textX = x + l.s(8.0F) + iconBox + l.s(10.0F);
        MenuDraw.textMid(serverCaption(), textX, y + l.s(8.0F), l.s(16.0F), Weight.MEDIUM, l.s(9.0F), t.dim);
        MenuDraw.textMid(BotScript.SERVER, textX, y + l.s(24.0F), l.s(20.0F), Weight.SEMIBOLD, l.s(12.0F), t.text);

        return y + height + l.s(SECTION_GAP);
    }

    /** Степпер сценария: этапы в том порядке, в котором их проходит бот. */
    private float stageSection(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim,
                               float x, float y, float width, float mouseY,
                               float viewTop, float viewBottom) {
        y = sectionTitle(l, t, x, y, width, IconGlyphs.MISC, false, scenarioTitle(), scenarioHint());

        BedWarsBot bot = BedWarsBot.INSTANCE;
        boolean running = bot.isRunning();
        int current = bot.stageIndex();
        BotStage[] stages = BotStage.flow();
        float rowHeight = l.s(STAGE_ROW);
        float railX = x + l.s(11.0F);

        for (int i = 1; i < stages.length; i++) {
            float rowY = y + (i - 1) * rowHeight;
            if (rowY + rowHeight < viewTop - l.s(2.0F) || rowY > viewBottom + l.s(2.0F)) {
                continue;
            }
            BotStage stage = stages[i];
            boolean done = running && i < current;
            boolean active = running && i == current;

            // вертикальная рельса таймлайна — её видно сквозь строки, поэтому рисуем первой
            if (i + 1 < stages.length) {
                MenuDraw.rect(railX - l.s(0.5F), rowY + l.s(18.0F), Math.max(1.0F, l.s(1.0F)),
                        rowHeight - l.s(14.0F), done || active ? MenuDraw.a(t.accent, 120) : t.border);
            }

            int dotColor = done ? t.accent : active ? t.accent2 : t.track;
            if (active) {
                float pulse = anim.to(KEY_BASE + 400, 1.0F, 6.0F);
                MenuDraw.circle(railX, rowY + l.s(11.0F), l.s(6.0F) + l.s(1.5F) * pulse,
                        MenuDraw.a(t.accent, 60));
            }
            MenuDraw.circle(railX, rowY + l.s(11.0F), l.s(4.0F), dotColor);
            if (done) {
                MenuDraw.iconLetters(LegacyIconGlyphs.CHECK, railX, rowY + l.s(11.0F), l.s(6.0F), t.onAccent);
            }

            float textX = x + l.s(26.0F);
            int nameColor = done || active ? t.text : t.muted;
            MenuDraw.textMid(stage.label(), textX, rowY, l.s(16.0F), Weight.SEMIBOLD, l.s(10.5F), nameColor);
            MenuDraw.textMid(MenuDraw.ellipsize(stage.hint(), Weight.MEDIUM, l.s(8.5F),
                            Math.max(l.s(40.0F), width - l.s(120.0F))),
                    textX, rowY + l.s(14.0F), l.s(14.0F), Weight.MEDIUM, l.s(8.5F),
                    active ? MenuDraw.a(t.accent2, 220) : t.dim);

            String badge = STAGE_TEXT[i];
            float badgeFont = l.s(8.0F);
            float badgeWidth = Math.max(l.s(16.0F), MenuDraw.textWidth(badge, Weight.MEDIUM, badgeFont) + l.s(8.0F));
            float badgeHeight = l.s(13.0F);
            float badgeX = x + width - badgeWidth;
            float badgeY = rowY + l.s(3.0F);
            MenuDraw.round(badgeX, badgeY, badgeWidth, badgeHeight, badgeHeight * 0.5F,
                    active ? MenuDraw.a(t.accent, t.light ? 40 : 56) : MenuDraw.a(t.text, t.light ? 10 : 8));
            MenuDraw.textCenterMid(badge, badgeX + badgeWidth * 0.5F, badgeY, badgeHeight, Weight.MEDIUM,
                    badgeFont, active ? (t.light ? t.accent : t.accent2) : t.dim);
        }

        return y + (stages.length - 1) * rowHeight + l.s(SECTION_GAP);
    }

    /**
     * Полоса хотбара: ячейки как в игре, подпись наведенной — под полосой.
     *
     * <p>Состав и доступность ячеек взяты из {@link BotScript}: «Быстро играть»,
     * «Скрыть игроков» и «Назад в лобби» бот не трогает, «Статистика» и «Выбор лобби»
     * появятся позже, пустые ячейки показаны пустыми.
     */
    private float hotbarSection(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim,
                                float x, float y, float width, float mouseX, float mouseY,
                                BotScript.Slot[] slots, String title, String hint,
                                boolean live, int keyBase) {
        y = sectionTitle(l, t, x, y, width, IconGlyphs.AUTOBUY, false, title, hint);

        float gap = l.s(6.0F);
        float cell = Math.min(l.s(STRIP_H), (width - gap * (slots.length - 1)) / slots.length);
        float stripWidth = cell * slots.length + gap * (slots.length - 1);
        float startX = x + (width - stripWidth) * 0.5F;
        int hovered = -1;

        for (int i = 0; i < slots.length; i++) {
            float cellX = startX + i * (cell + gap);
            BotScript.Slot slot = slots[i];
            boolean blank = slot.blank();
            boolean over = live && mouseX >= cellX && mouseX <= cellX + cell
                    && mouseY >= y && mouseY <= y + cell;
            if (over) {
                hovered = i;
            }
            float hover = anim.to(keyBase + i, over ? 1.0F : 0.0F, 18.0F);
            boolean usable = slot.availability().usable();

            int background = blank
                    ? MenuDraw.a(t.text, t.light ? 6 : 5)
                    : MenuDraw.mix(t.inset, t.insetHover, hover);
            MenuDraw.round(cellX, y, cell, cell, l.s(7.0F), background);
            int borderColor = blank ? MenuDraw.a(t.text, t.light ? 14 : 10)
                    : usable ? MenuDraw.a(t.accent, (int) (90 + 90 * hover))
                    : MenuDraw.mix(t.border, MenuDraw.a(LATER_COLOR, 150), hover);
            MenuDraw.outline(cellX, y, cell, cell, l.s(7.0F), borderColor);

            if (!blank) {
                MenuDraw.textCenterMid(SLOT_TEXT[i], cellX + cell * 0.5F, y, cell * 0.62F, Weight.SEMIBOLD,
                        l.s(10.0F), usable ? t.text : t.muted);
                int dot = availabilityColor(t, slot.availability());
                MenuDraw.circle(cellX + cell * 0.5F, y + cell * 0.78F, l.s(2.0F), dot);
            }
        }

        float captionY = y + cell + l.s(6.0F);
        String caption;
        int captionColor;
        if (hovered >= 0 && !slots[hovered].blank()) {
            caption = slots[hovered].label() + " — " + slots[hovered].availability().label();
            captionColor = availabilityColor(t, slots[hovered].availability());
        } else if (hovered >= 0) {
            caption = emptyCaption();
            captionColor = t.dim;
        } else {
            caption = hint;
            captionColor = t.dim;
        }
        MenuDraw.textCenterMid(MenuDraw.ellipsize(caption, Weight.MEDIUM, l.s(9.0F), width),
                x + width * 0.5F, captionY, l.s(CAPTION), Weight.MEDIUM, l.s(9.0F), captionColor);

        return captionY + l.s(CAPTION) + l.s(SECTION_GAP);
    }

    /** Голосования: ячейка 12 или 14, у «позже» выбор недоступен. */
    private float voteSection(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim,
                              float x, float y, float width, float mouseX, float mouseY,
                              float viewTop, float viewBottom, boolean live,
                              WorkspaceSettings.Sink sink) {
        y = sectionTitle(l, t, x, y, width, IconGlyphs.STAR, false, voteTitle(), voteHint());

        float rowHeight = l.s(VOTE_ROW);
        float segmentWidth = l.s(38.0F);
        float segmentHeight = l.s(20.0F);
        float segmentGap = l.s(6.0F);

        for (int i = 0; i < BotScript.VOTES.length; i++) {
            float rowY = y + i * rowHeight;
            if (rowY + rowHeight < viewTop - l.s(2.0F) || rowY > viewBottom + l.s(2.0F)) {
                continue;
            }
            BotScript.Vote vote = BotScript.VOTES[i];
            boolean usable = vote.availability().usable();
            int selected = BotConfig.voteCell(i);

            boolean rowHover = live && mouseX >= x && mouseX <= x + width
                    && mouseY >= rowY && mouseY <= rowY + rowHeight;
            float hover = anim.to(KEY_BASE + 100 + i, rowHover && usable ? 1.0F : 0.0F, 18.0F);
            if (hover > 0.02F) {
                MenuDraw.round(x - l.s(6.0F), rowY, width + l.s(12.0F), rowHeight, l.s(7.0F),
                        MenuDraw.a(t.accent, (int) (18.0F * hover)));
            }

            MenuDraw.textMid(MenuDraw.ellipsize(vote.label(), Weight.MEDIUM, l.s(11.0F),
                            Math.max(l.s(40.0F), width - l.s(180.0F))),
                    x, rowY, l.s(20.0F), Weight.MEDIUM, l.s(11.0F), usable ? t.text : t.muted);

            String option = usable ? vote.optionLabel(selected) : vote.availability().label();
            MenuDraw.textMid(MenuDraw.ellipsize(option, Weight.MEDIUM, l.s(8.5F), l.s(150.0F)),
                    x, rowY + l.s(18.0F), l.s(14.0F), Weight.MEDIUM, l.s(8.5F),
                    usable ? MenuDraw.a(t.accent2, 220) : t.dim);

            float segmentY = rowY + (rowHeight - segmentHeight) * 0.5F;
            float secondX = x + width - segmentWidth;
            float firstX = secondX - segmentGap - segmentWidth;
            segment(l, t, anim, firstX, segmentY, segmentWidth, segmentHeight, BotScript.CELL_A_TEXT,
                    usable && selected == BotScript.VOTE_CELL_A, usable, live, i, 0,
                    mouseX, mouseY, sink, KEY_BASE + 140 + i * 2);
            segment(l, t, anim, secondX, segmentY, segmentWidth, segmentHeight, BotScript.CELL_B_TEXT,
                    usable && selected == BotScript.VOTE_CELL_B, usable, live, i, 1,
                    mouseX, mouseY, sink, KEY_BASE + 141 + i * 2);
        }

        return y + BotScript.VOTES.length * rowHeight + l.s(SECTION_GAP);
    }

    /** Образцы шерсти: так бот выведет цвета команд, когда прочитает меню выбора. */
    private void swatchSection(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim,
                               float x, float y, float width, float mouseX, float mouseY,
                               float viewTop, float viewBottom) {
        y = sectionTitle(l, t, x, y, width, IconGlyphs.COSMETICA, false, teamTitle(), teamHint());

        int[] colors = BotScript.WOOL_COLORS;
        int perRow = 8;
        float gap = l.s(SWATCH_GAP);
        float swatch = Math.min(l.s(SWATCH), (width - gap * 7) / 8);
        int hovered = -1;

        for (int i = 0; i < colors.length; i++) {
            int column = i % perRow;
            int row = i / perRow;
            float cellX = x + column * (swatch + gap);
            float cellY = y + row * (swatch + gap);
            if (cellY + swatch < viewTop - l.s(2.0F) || cellY > viewBottom + l.s(2.0F)) {
                continue;
            }
            boolean over = mouseX >= cellX && mouseX <= cellX + swatch
                    && mouseY >= cellY && mouseY <= cellY + swatch;
            if (over) {
                hovered = i;
            }
            float hover = anim.to(KEY_BASE + 200 + i, over ? 1.0F : 0.0F, 20.0F);
            MenuDraw.round(cellX, cellY, swatch, swatch, l.s(6.0F), colors[i]);
            MenuDraw.outline(cellX, cellY, swatch, swatch, l.s(6.0F),
                    MenuDraw.a(t.text, (int) (30 + 90 * hover)));
            if (hover > 0.02F) {
                MenuDraw.round(cellX - l.s(2.0F), cellY - l.s(2.0F), swatch + l.s(4.0F), swatch + l.s(4.0F),
                        l.s(8.0F), MenuDraw.a(t.accent, (int) (60 * hover)));
            }
        }

        float captionY = y + 2.0F * (swatch + gap) + l.s(2.0F);
        String caption = hovered >= 0 ? BotScript.woolName(hovered) : teamWaitCaption();
        MenuDraw.textCenterMid(MenuDraw.ellipsize(caption, Weight.MEDIUM, l.s(9.0F), width),
                x + width * 0.5F, captionY, l.s(CAPTION), Weight.MEDIUM, l.s(9.0F),
                hovered >= 0 ? t.text : t.dim);
    }

    // ---- правая колонка: управление

    private void drawRight(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim, float mouseX, float mouseY,
                           boolean live, int editField, String editBuffer, float scroll,
                           WorkspaceSettings.Sink sink) {
        float x = l.paneX;
        float y = l.paneY;
        float w = l.paneW;
        float h = l.paneH;
        float radius = l.s(11.0F);
        float pad = l.s(PAD);

        WorkspaceDraw.pane(x, y, w, h, radius, t);

        float head = l.s(HEAD);
        MenuDraw.textMid(controlTitle(), x + pad, y, head, Weight.BOLD, l.s(12.5F), t.text);
        WorkspaceDraw.divider(l, x + pad, y + head - l.line(), w - pad * 2.0F, t);

        float viewTop = y + head;
        float viewBottom = y + h - l.s(8.0F);
        float viewHeight = Math.max(0.0F, viewBottom - viewTop);
        float contentX = x + pad;
        float contentWidth = w - pad * 2.0F;

        MenuDraw.scissor(x + l.s(1.0F), viewTop, w - l.s(2.0F), viewHeight);
        try {
            float cursor = viewTop + l.s(8.0F) - scroll;
            cursor = fieldSection(l, t, anim, contentX, cursor, contentWidth, mouseX, mouseY,
                    viewTop, viewBottom, live && !BedWarsBot.INSTANCE.isRunning(), editField, editBuffer, sink);
            cursor = buttonSection(l, t, anim, contentX, cursor, contentWidth, mouseX, mouseY,
                    viewTop, viewBottom, live, sink);
            cursor = botSection(l, t, anim, contentX, cursor, contentWidth, mouseX, mouseY,
                    viewTop, viewBottom, live, sink);
            cursor = partySection(l, t, contentX, cursor, contentWidth);
            logSection(l, t, anim, contentX, cursor, contentWidth, mouseX, mouseY, viewTop, viewBottom);
        } finally {
            MenuDraw.scissorEnd();
        }

        WorkspaceDraw.scrollbar(l, t, x + w - l.s(6.0F), viewTop, viewHeight, this.rightTotal, scroll,
                mouseX, mouseY);
    }

    /** Поля ввода: префикс ника, число ботов, пароль регистрации, ник владельца пати. */
    private float fieldSection(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim,
                               float x, float y, float width, float mouseX, float mouseY,
                               float viewTop, float viewBottom, boolean live,
                               int editField, String editBuffer, WorkspaceSettings.Sink sink) {
        y = sectionTitle(l, t, x, y, width, IconGlyphs.BIND, false, connectTitle(), connectHint());

        y = stringField(l, t, anim, x, y, width, mouseX, mouseY, viewTop, viewBottom, live,
                prefixLabel(), BotConfig.nickPrefix, FIELD_PREFIX, editField, editBuffer, sink);
        y = countField(l, t, anim, x, y, width, mouseX, mouseY, viewTop, viewBottom, live, sink);
        y = stringField(l, t, anim, x, y, width, mouseX, mouseY, viewTop, viewBottom, live,
                passwordLabel(), BotConfig.password, FIELD_PASSWORD, editField, editBuffer, sink);
        y = stringField(l, t, anim, x, y, width, mouseX, mouseY, viewTop, viewBottom, live,
                ownerLabel(), BotConfig.ownerNick, FIELD_OWNER, editField, editBuffer, sink);

        return y + l.s(SECTION_GAP);
    }

    private float stringField(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim,
                              float x, float y, float width, float mouseX, float mouseY,
                              float viewTop, float viewBottom, boolean live,
                              String label, String value, int field,
                              int editField, String editBuffer, WorkspaceSettings.Sink sink) {
        float height = l.s(FIELD_ROW);
        if (y + height >= viewTop - l.s(2.0F) && y <= viewBottom + l.s(2.0F)) {
            boolean editing = editField == field;
            boolean hovered = live && mouseX >= x && mouseX <= x + width
                    && mouseY >= y && mouseY <= y + height;
            float hover = anim.to(KEY_BASE + 300 + field, hovered || editing ? 1.0F : 0.0F, 18.0F);

            float font = l.s(10.5F);
            MenuDraw.textMid(MenuDraw.ellipsize(label, Weight.MEDIUM, font, Math.max(l.s(40.0F),
                            width - l.s(150.0F))),
                    x, y, l.s(20.0F), Weight.MEDIUM, font, t.text);

            float boxWidth = Math.min(l.s(140.0F), Math.max(l.s(60.0F), width - l.s(110.0F)));
            float boxX = x + width - boxWidth;
            float boxHeight = l.s(20.0F);
            float boxY = y + (height - boxHeight) * 0.5F;
            MenuDraw.round(boxX, boxY, boxWidth, boxHeight, l.s(6.0F),
                    editing ? t.insetHover : MenuDraw.mix(t.inset, t.insetHover, hover * 0.6F));
            MenuDraw.outline(boxX, boxY, boxWidth, boxHeight, l.s(6.0F),
                    editing ? MenuDraw.a(t.accent, 170) : MenuDraw.mix(t.border, t.borderStrong, hover * 0.5F));

            String shown = editing && editBuffer != null ? editBuffer : value;
            MenuDraw.textMid(MenuDraw.ellipsize(shown.isEmpty() ? "—" : shown, Weight.MEDIUM, font,
                            boxWidth - l.s(12.0F)),
                    boxX + l.s(6.0F), boxY, boxHeight, Weight.MEDIUM, font,
                    shown.isEmpty() ? t.dim : t.text);
            if (editing) {
                float caretX = boxX + l.s(6.0F)
                        + Math.min(boxWidth - l.s(12.0F), MenuDraw.textWidth(shown, Weight.MEDIUM, font))
                        + l.s(1.0F);
                MenuDraw.rect(caretX, boxY + l.s(4.0F), Math.max(1.0F, l.s(1.0F)), boxHeight - l.s(8.0F),
                        t.accent);
            }
            if (live) {
                sink.hit(HIT_STRING, field, 0, boxX, boxY, boxWidth, boxHeight);
            }
        }
        return y + height;
    }

    /** Число ботов: ползунок от 1 до {@link BotScript#MAX_BOTS}. */
    private float countField(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim,
                             float x, float y, float width, float mouseX, float mouseY,
                             float viewTop, float viewBottom, boolean live,
                             WorkspaceSettings.Sink sink) {
        float height = l.s(SLIDER_ROW);
        if (y + height >= viewTop - l.s(2.0F) && y <= viewBottom + l.s(2.0F)) {
            float font = l.s(10.5F);
            MenuDraw.textMid(countLabel(), x, y, l.s(20.0F), Weight.MEDIUM, font, t.text);
            MenuDraw.textRight(BotConfig.botCountText(), x + width, y, Weight.SEMIBOLD, font, t.text);

            int count = BotConfig.nicknameCount();
            float ratio = (count - BotScript.MIN_BOTS)
                    / (float) Math.max(1, BotScript.MAX_BOTS - BotScript.MIN_BOTS);
            float trackHeight = Math.max(2.0F, l.s(3.0F));
            float trackY = y + l.s(30.0F) - trackHeight * 0.5F;
            MenuDraw.round(x, trackY, width, trackHeight, trackHeight * 0.5F, t.track);

            float animated = anim.to(KEY_BASE + 320, ratio, 26.0F);
            float filled = Math.max(trackHeight, width * animated);
            int fill = MenuDraw.mix(t.accent, t.accent2, animated);
            MenuDraw.round(x, trackY, filled, trackHeight, trackHeight * 0.5F, fill);

            float knobRadius = l.s(4.5F);
            float knobX = x + filled;
            MenuDraw.circle(knobX, trackY + trackHeight * 0.5F, knobRadius, t.knob);
            MenuDraw.ring(knobX, trackY + trackHeight * 0.5F, knobRadius + l.s(1.5F), l.s(1.5F),
                    MenuDraw.a(t.accent2, 170));

            String preview = BotConfig.nicknamePreview();
            MenuDraw.textMid(MenuDraw.ellipsize(preview, Weight.MEDIUM, l.s(8.5F), width),
                    x, y + l.s(34.0F), l.s(12.0F), Weight.MEDIUM, l.s(8.5F), t.dim);

            if (live) {
                sink.hit(HIT_SLIDER, FIELD_COUNT, 0, x, y + l.s(20.0F), width, l.s(22.0F));
            }
        }
        return y + height;
    }

    /** Кнопки управления заглушкой. */
    private float buttonSection(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim,
                                float x, float y, float width, float mouseX, float mouseY,
                                float viewTop, float viewBottom, boolean live,
                                WorkspaceSettings.Sink sink) {
        y = sectionTitle(l, t, x, y, width, LegacyIconGlyphs.COMMAND, true, actionTitle(), actionHint());

        float height = l.s(BUTTON_ROW);
        float gap = l.s(8.0F);
        float half = (width - gap) * 0.5F;
        boolean running = BedWarsBot.INSTANCE.isRunning();

        if (y + height >= viewTop - l.s(2.0F) && y <= viewBottom + l.s(2.0F)) {
            // «Запустить» доступна только на остановленном боте, «Остановить» — только на запущенном
            button(l, t, anim, x, y, half, height, startTitle(),
                    IconGlyphs.MOVEMENT, false, true, !running, live, HIT_START, KEY_BASE + 340,
                    mouseX, mouseY, sink);
            button(l, t, anim, x + half + gap, y, half, height, stopTitle(),
                    IconGlyphs.TRASH, false, false, running, live, HIT_STOP, KEY_BASE + 341,
                    mouseX, mouseY, sink);
        }

        float secondRow = y + height + gap;
        if (secondRow + height >= viewTop - l.s(2.0F) && secondRow <= viewBottom + l.s(2.0F)) {
            button(l, t, anim, x, secondRow, half, height, stepTitle(),
                    LegacyIconGlyphs.CHEVRON, true, false, true, live, HIT_STEP, KEY_BASE + 342,
                    mouseX, mouseY, sink);
            button(l, t, anim, x + half + gap, secondRow, half, height, clearTitle(),
                    LegacyIconGlyphs.TRASH, true, false, true, live, HIT_CLEAR_LOG, KEY_BASE + 343,
                    mouseX, mouseY, sink);
        }

        return secondRow + height + l.s(SECTION_GAP);
    }

    private void button(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim,
                        float x, float y, float width, float height, String label, String glyph,
                        boolean letters, boolean primary, boolean enabled, boolean live, int hitType, int key,
                        float mouseX, float mouseY, WorkspaceSettings.Sink sink) {
        boolean hovered = live && enabled && mouseX >= x && mouseX <= x + width
                && mouseY >= y && mouseY <= y + height;
        float hover = anim.to(key, hovered ? 1.0F : 0.0F, 18.0F);

        if (primary) {
            int alpha = enabled ? 255 : 110;
            if (MenuPrefs.gradient) {
                MenuDraw.roundGrad(x, y, width, height, l.s(8.0F),
                        MenuDraw.a(t.accent, alpha), MenuDraw.a(t.accent2, alpha));
            } else {
                MenuDraw.round(x, y, width, height, l.s(8.0F), MenuDraw.a(t.accent, alpha));
            }
            if (hover > 0.02F) {
                MenuDraw.glow(x, y, width, height, l.s(8.0F), MenuDraw.a(t.accent, (int) (70 * hover)));
            }
        } else {
            MenuDraw.round(x, y, width, height, l.s(8.0F),
                    enabled ? MenuDraw.mix(t.inset, t.insetHover, hover) : MenuDraw.a(t.text, t.light ? 6 : 5));
            MenuDraw.outline(x, y, width, height, l.s(8.0F),
                    enabled ? MenuDraw.mix(t.border, t.borderStrong, hover * 0.6F) : t.border);
        }

        int color = primary ? MenuDraw.a(t.onAccent, enabled ? 255 : 150)
                : enabled ? MenuDraw.mix(t.muted, t.text, hover) : t.dim;
        float font = l.s(10.0F);
        float iconSize = l.s(11.0F);
        float textWidth = MenuDraw.textWidth(label, Weight.SEMIBOLD, font);
        float total = iconSize + l.s(6.0F) + textWidth;
        float startX = x + (width - total) * 0.5F;
        if (letters) {
            MenuDraw.iconLetters(glyph, startX + iconSize * 0.5F, y + height * 0.5F, iconSize, color);
        } else {
            MenuDraw.iconCs(glyph, startX + iconSize * 0.5F, y + height * 0.5F, iconSize, color);
        }
        MenuDraw.textMid(label, startX + iconSize + l.s(6.0F), y, height, Weight.SEMIBOLD, font, color);

        if (live && enabled) {
            sink.hit(hitType, 0, 0, x, y, width, height);
        }
    }

    /** Список ботов с их статусами. */
    private float botSection(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim,
                             float x, float y, float width, float mouseX, float mouseY,
                             float viewTop, float viewBottom, boolean live, WorkspaceSettings.Sink sink) {
        y = sectionTitle(l, t, x, y, width, IconGlyphs.PLAYER, false, botListTitle(), "DEMO");
        BedWarsBot bot = BedWarsBot.INSTANCE;
        for (int i = 0; i < bot.displayCount(); i++) {
            float height = l.s(BotAppearance.compact(i) ? 62 : 84);
            if (y + height >= viewTop && y <= viewBottom) {
                boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                float hover = anim.to(KEY_BASE + 360 + i, hovered ? 1 : 0, 18);
                int accent = BotAppearance.color(i);
                MenuDraw.round(x, y, width, height, l.s(9), MenuDraw.mix(t.inset, t.insetHover, hover));
                MenuDraw.outline(x, y, width, height, l.s(9), MenuDraw.a(accent, 100));
                MenuDraw.round(x + l.s(8), y + l.s(9), l.s(27), l.s(27), l.s(8), MenuDraw.a(accent, 45));
                MenuDraw.iconCs(IconGlyphs.PLAYER, x + l.s(21.5F), y + l.s(22.5F), l.s(15), accent);
                MenuDraw.textMid(MenuDraw.ellipsize(bot.nickname(i), Weight.SEMIBOLD, l.s(11), width - l.s(54)),
                        x + l.s(43), y + l.s(8), l.s(15), Weight.SEMIBOLD, l.s(11), t.text);
                MenuDraw.textMid(bot.status(i).label(), x + l.s(43), y + l.s(23), l.s(14),
                        Weight.MEDIUM, l.s(9), bot.status(i).color());
                if (!BotAppearance.compact(i)) {
                    MenuDraw.textMid("mc.musteryworld.net · DEMO", x + l.s(10), y + l.s(38), l.s(15),
                            Weight.MEDIUM, l.s(8), t.dim);
                }
                float controlsY = y + height - l.s(23);
                float cell = (width - l.s(20)) / 4;
                cardControl(l, t, x + l.s(8), controlsY, cell, L("Цвет", "Color"), i, 0, live, sink);
                cardControl(l, t, x + l.s(8) + cell, controlsY, cell, L("Размер", "Size"), i, 1, live, sink);
                cardControl(l, t, x + l.s(8) + cell * 2, controlsY, cell,
                        BotAppearance.notifications(i) ? L("Увед. +", "Alerts on") : L("Увед. −", "Alerts off"), i, 2, live, sink);
                MenuDraw.textCenterMid(L("Тест !", "Test !"), x + l.s(8) + cell * 3.5F, controlsY, l.s(18),
                        Weight.MEDIUM, l.s(8), t.danger);
                if (live) sink.hit(HIT_TEST_ERROR, i, 0, x + l.s(8) + cell * 3, controlsY, cell, l.s(18));
            }
            y += height + l.s(8);
        }
        return y + l.s(SECTION_GAP);
    }

    private void cardControl(WorkspaceLayout l, WorkspaceTheme t, float x, float y, float w,
                             String title, int bot, int option, boolean live, WorkspaceSettings.Sink sink) {
        MenuDraw.round(x, y, w - l.s(3), l.s(18), l.s(5), t.inset);
        MenuDraw.textCenterMid(title, x + (w - l.s(3)) * 0.5F, y, l.s(18), Weight.MEDIUM, l.s(8), t.muted);
        if (live) sink.hit(HIT_APPEARANCE, bot, option, x, y, w - l.s(3), l.s(18));
    }

    private float partySection(WorkspaceLayout l, WorkspaceTheme t, float x, float y, float width) {
        y = sectionTitle(l, t, x, y, width, IconGlyphs.PLAYER, false, L("Пати", "Party"), "DEMO");
        BedWarsBot bot = BedWarsBot.INSTANCE;
        partyLine(l, t, x, y, width, L("Лидер", "Leader"), bot.partyLeader());
        y += l.s(22);
        partyLine(l, t, x, y, width, L("Приглашён", "Invited"), bot.invitedOwner());
        y += l.s(22);
        MenuDraw.textMid(L("Участники (демо)", "Members (demo)"), x, y, l.s(22), Weight.MEDIUM, l.s(9), t.dim);
        y += l.s(22);
        for (int i = 0; i < bot.partySize(); i++) {
            partyLine(l, t, x, y, width, i == 0 ? L("Лидер", "Leader") : L("Участник", "Member"), bot.partyMember(i));
            y += l.s(22);
        }
        return y + l.s(SECTION_GAP);
    }

    private void partyLine(WorkspaceLayout l, WorkspaceTheme t, float x, float y, float w, String label, String value) {
        MenuDraw.textMid(label, x, y, l.s(22), Weight.MEDIUM, l.s(9), t.dim);
        MenuDraw.textRight(MenuDraw.ellipsize(value, Weight.SEMIBOLD, l.s(9), w * 0.55F),
                x + w, y + l.s(6), Weight.SEMIBOLD, l.s(9), t.text);
    }

    /** Non-modal result banner. All successful operations here are explicitly demo operations. */
    void notification(WorkspaceLayout l, WorkspaceTheme t) {
        String message = BedWarsBot.INSTANCE.notice();
        if (message.isEmpty()) return;
        float w = l.contentW;
        float y = l.py + l.ph - l.s(48);
        int color = BedWarsBot.INSTANCE.noticeError() ? t.danger : t.accent2;
        MenuDraw.round(l.contentX, y, w, l.s(32), l.s(8), t.window);
        MenuDraw.outline(l.contentX, y, w, l.s(32), l.s(8), MenuDraw.a(color, 180));
        MenuDraw.textMid(MenuDraw.ellipsize(message, Weight.SEMIBOLD, l.s(10), w - l.s(24)),
                l.contentX + l.s(12), y, l.s(32), Weight.SEMIBOLD, l.s(10), color);
    }

    /** Консоль журнала: строки готовит {@link BedWarsBot}, панель их только показывает. */
    private void logSection(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim,
                            float x, float y, float width, float mouseX, float mouseY,
                            float viewTop, float viewBottom) {
        y = sectionTitle(l, t, x, y, width, LegacyIconGlyphs.TEXT, true, logTitle(), logHint());

        BedWarsBot bot = BedWarsBot.INSTANCE;
        int available = bot.logSize();
        int shown = Math.min(LOG_LINES, available);
        float rowHeight = l.s(LOG_ROW);

        MenuDraw.round(x, y, width, rowHeight * LOG_LINES + l.s(8.0F), l.s(8.0F),
                t.light ? MenuDraw.a(0x1B1530, 10) : MenuDraw.a(0x000000, 90));
        MenuDraw.outline(x, y, width, rowHeight * LOG_LINES + l.s(8.0F), l.s(8.0F), t.border);

        if (shown == 0) {
            MenuDraw.textCenterMid(logEmpty(), x + width * 0.5F, y, rowHeight * LOG_LINES + l.s(8.0F),
                    Weight.MEDIUM, l.s(9.0F), t.dim);
            return;
        }

        float font = l.s(8.5F);
        for (int i = 0; i < shown; i++) {
            float rowY = y + l.s(4.0F) + (shown - 1 - i) * rowHeight;
            if (rowY + rowHeight < viewTop - l.s(2.0F) || rowY > viewBottom + l.s(2.0F)) {
                continue;
            }
            String stamp = bot.logStamp(i);
            MenuDraw.textMid(stamp, x + l.s(6.0F), rowY, rowHeight, Weight.MEDIUM, font,
                    MenuDraw.a(t.dim, 170));
            float stampWidth = MenuDraw.textWidth(stamp, Weight.MEDIUM, font) + l.s(10.0F);
            int lineColor = MenuDraw.mix(t.dim, t.text, i == 0 ? 1.0F : 0.35F);
            MenuDraw.textMid(MenuDraw.ellipsize(bot.logLine(i), Weight.MEDIUM, font,
                            Math.max(l.s(30.0F), width - stampWidth - l.s(10.0F))),
                    x + l.s(6.0F) + stampWidth, rowY, rowHeight, Weight.MEDIUM, font, lineColor);
        }
    }

    // ---- общие детали

    /**
     * Заголовок раздела: иконка, название и подсказка справа.
     *
     * @param letters {@code true}, если глиф из «буквенного» набора
     *                ({@link LegacyIconGlyphs}) — такие рисуются через
     *                {@link MenuDraw#iconLetters}, а не {@link MenuDraw#iconCs};
     *                перепутанный набор дал бы вместо иконки другой символ
     */
    private float sectionTitle(WorkspaceLayout l, WorkspaceTheme t, float x, float y, float width,
                               String glyph, boolean letters, String title, String hint) {
        float height = l.s(SECTION);
        float iconSize = l.s(12.0F);
        float iconX = x + iconSize * 0.5F;
        float iconY = y + height * 0.5F;
        int iconColor = t.light ? t.accent : t.accent2;
        if (letters) {
            MenuDraw.iconLetters(glyph, iconX, iconY, iconSize, iconColor);
        } else {
            MenuDraw.iconCs(glyph, iconX, iconY, iconSize, iconColor);
        }
        MenuDraw.textMid(title, x + iconSize + l.s(8.0F), y, height, Weight.SEMIBOLD, l.s(10.5F), t.text);
        MenuDraw.textRight(MenuDraw.ellipsize(hint, Weight.MEDIUM, l.s(8.5F),
                        Math.max(l.s(30.0F), width - MenuDraw.textWidth(title, Weight.SEMIBOLD, l.s(10.5F))
                                - iconSize - l.s(20.0F))),
                x + width, y + (height - l.s(9.0F)) * 0.5F, Weight.MEDIUM, l.s(8.5F), t.dim);
        return y + height;
    }

    /** Индикатор состояния заглушки в шапке левой колонки. */
    private void statusPill(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim,
                            float right, float y, float height) {
        BedWarsBot bot = BedWarsBot.INSTANCE;
        String label = bot.isRunning() ? bot.stage().label() : stoppedLabel();
        int color = bot.isRunning() ? BotStatus.CONNECTING.color() : t.dim;
        float font = l.s(8.5F);
        float width = MenuDraw.textWidth(label, Weight.MEDIUM, font) + l.s(24.0F);
        float x = right - width;

        MenuDraw.round(x, y, width, height, height * 0.5F, MenuDraw.a(color, t.light ? 30 : 42));
        MenuDraw.outline(x, y, width, height, height * 0.5F, MenuDraw.a(color, 130));
        float pulse = bot.isRunning() ? anim.to(KEY_BASE + 420, 1.0F, 6.0F) : 0.0F;
        MenuDraw.circle(x + l.s(10.0F), y + height * 0.5F, l.s(2.5F) + l.s(1.0F) * pulse, color);
        MenuDraw.textMid(label, x + l.s(16.0F), y, height, Weight.MEDIUM, font, color);
    }

    /** Сегмент выбора ячейки голосования (12 или 14). */
    private void segment(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim,
                         float x, float y, float width, float height, String label,
                         boolean selected, boolean enabled, boolean live, int vote, int cell,
                         float mouseX, float mouseY, WorkspaceSettings.Sink sink, int key) {
        boolean hovered = live && enabled && mouseX >= x && mouseX <= x + width
                && mouseY >= y && mouseY <= y + height;
        float hover = anim.to(key, hovered ? 1.0F : 0.0F, 18.0F);
        int background = selected
                ? MenuDraw.a(t.accent, t.light ? 40 : 56)
                : enabled ? MenuDraw.mix(t.inset, t.insetHover, hover) : MenuDraw.a(t.text, t.light ? 6 : 5);
        MenuDraw.round(x, y, width, height, height * 0.5F, background);
        MenuDraw.outline(x, y, width, height, height * 0.5F,
                selected ? MenuDraw.a(t.accent, 160) : enabled ? t.border : MenuDraw.a(t.text, t.light ? 12 : 8));
        MenuDraw.textCenterMid(label, x + width * 0.5F, y, height, Weight.SEMIBOLD, l.s(9.5F),
                selected ? (t.light ? t.accent : t.accent2) : enabled ? t.muted : t.dim);
        if (live && enabled) {
            sink.hit(HIT_VOTE, vote, cell, x, y, width, height);
        }
    }

    /** Цвет бейджа доступности: сейчас — акцент, позже — янтарный, прочее — приглушённый. */
    private static int availabilityColor(WorkspaceTheme t, BotScript.Availability availability) {
        return switch (availability) {
            case NOW -> t.accent2;
            case LATER -> LATER_COLOR;
            case NEVER -> t.dim;
            case EMPTY -> MenuDraw.a(t.dim, 90);
        };
    }

    // ---- подписи (язык клиента)

    private static String title() {
        return L("Бот BedWars", "BedWars bot");
    }

    private static String subtitle() {
        return L("Сценарий и состояния — заглушка", "Scenario and states — stub");
    }

    private static String stoppedLabel() {
        return L("Остановлен", "Stopped");
    }

    private static String serverCaption() {
        return L("Сервер (зафиксирован)", "Server (fixed)");
    }

    private static String lockCaption() {
        return L("нельзя менять", "not editable");
    }

    private static String scenarioTitle() {
        return L("Сценарий", "Scenario");
    }

    private static String scenarioHint() {
        return L("порядок шагов бота", "bot step order");
    }

    private static String lobbyTitle() {
        return L("Хотбар лобби", "Lobby hotbar");
    }

    private static String lobbyHint() {
        return L("слот 3 — ПКМ по «Выбор категории»", "slot 3 — RMB on category select");
    }

    private static String gameTitle() {
        return L("Хотбар в игре", "In-game hotbar");
    }

    private static String gameHint() {
        return L("статистика и выбор лобби недоступны", "stats and lobby select unavailable");
    }

    private static String emptyCaption() {
        return L("Пусто", "Empty");
    }

    private static String voteTitle() {
        return L("Голосования", "Votes");
    }

    private static String voteHint() {
        return L("ячейка 12 или 14", "cell 12 or 14");
    }

    private static String teamTitle() {
        return L("Цвета команд", "Team colors");
    }

    private static String teamHint() {
        return L("шерсть в меню выбора команды", "wool in the team select menu");
    }

    private static String teamWaitCaption() {
        return L("Образцы шерсти · меню сервера не читалось", "Wool samples · server menu not read");
    }

    private static String controlTitle() {
        return L("Управление", "Control");
    }

    private static String connectTitle() {
        return L("Подключение", "Connection");
    }

    private static String connectHint() {
        return BedWarsBot.INSTANCE.isRunning() ? L("остановите демо для правки", "stop demo to edit") : L("ники и регистрация", "nicks and registration");
    }

    private static String prefixLabel() {
        return L("Префикс ника", "Nick prefix");
    }

    private static String countLabel() {
        return L("Количество ботов", "Bot count");
    }

    private static String passwordLabel() {
        return L("Пароль /reg", "Password /reg");
    }

    private static String ownerLabel() {
        return L("Пригласить в пати", "Invite to party");
    }

    private static String actionTitle() {
        return L("Действия", "Actions");
    }

    private static String actionHint() {
        return L("логика не реализована", "logic not implemented");
    }

    private static String startTitle() {
        return L("Запустить", "Start");
    }

    private static String stopTitle() {
        return L("Остановить", "Stop");
    }

    private static String stepTitle() {
        return L("Шаг сценария", "Scenario step");
    }

    private static String clearTitle() {
        return L("Очистить журнал", "Clear log");
    }

    private static String botListTitle() {
        return L("Боты", "Bots");
    }

    private static String botListHint() {
        return L("номер растёт с количеством", "number grows with count");
    }

    private static String logTitle() {
        return L("Журнал", "Log");
    }

    private static String logHint() {
        return L("строки пишет заглушка", "written by the stub");
    }

    private static String logEmpty() {
        return L("Журнал пуст", "Log is empty");
    }

    private static String L(String russian, String english) {
        return SelectAndSaveHelper.isRussian() ? russian : english;
    }

}

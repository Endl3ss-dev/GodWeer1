package org.privates.client.managers.module.impl.player;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.opengl.GL11;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer2;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.privates.client.api.interfaces.IMinecraft.mc;

public class ReadmeGui extends Screen {

    private static final List<String[]> PAGES_TEXT = Arrays.asList(
            new String[]{
                    "README",
                    "",
                    "ты попал в чит PRIVATE BETA я +- расскажу как все настроить",
                    "изначально гуи забинлджена правый шифт",
                    "дальше находим по поиску в низу гуи мы пишем туда HUD",
                    "открываем и настраиваем цвета нажимая на цвет это настройка также нажимаем на слайдер и настраиваем значение можем нажать на значение и написать сами с помощью клавиатуры",
                    "у вас изначально стоят нормальные настройки худ но вы можете включить BLUR и блюр для элементов рендера также выбрать моды ArmorHud, Keybinds, Potions",
                    "",
                    "расскажу за что отвечают элементы",
                    "= Potions =",
                    "* отрисовывает активные эффекты например вы выпили зелье силы 2 и будет показывать когда оно закончится и т.д",
                    "= KeyBinds =",
                    "* Активные включеные модули с помощью keybind",
                    "= Information =",
                    "* пишет ваши кординаты , скорость блоков в секунду, вам пинг (мс) мс это разрыв с сервером и вашей сессией - fps (кадры в секунду)",
                    "= NearblyPlayers =",
                    "* отрисовует ближайших игроков их хп и их последние предметы с броней и количеством",
                    "= KeyStrokes =",
                    "* отрисовывает WASD и когда вы нажимаете начинается прикольная анимация",
                    "= ArrayList =",
                    "* активные модули которые вы включили через UI или BIND",
                    "= Inventory =",
                    "* отрисовывает активные предметы которые находятся в вашем инвентаре",
                    "= WaterMark =",
                    "* водяной знак DLC чтобы при записи видео или попытке выдать наш проект за другой была watermark",
                    "= DisplayHelper =",
                    "* это помощник по игре то есть если у вас мало голода или здоровье или у вас почти сломалась элитра оно будет отрисовывать это",
                    "= BossBar =",
                    "* заменяет bossbar сервера на кастомный дизайн bossbar это линия например о кд на сервере то есть о режиме боя",
                    "= ScoreBoard =",
                    "* замена scoreboard сервера на кастомный scoreboard это обычно ваша информация на сервере",
                    "= Cooldowns =",
                    "* будет рендерить ваши задержки на предмета например на эндер жемчуг есть задержка между бросками и есть кастомные задежрки на серверах",
                    "= TargetHud  =",
                    "* показывает хп + броню таргета которые вы бьете",
                    "= MotionGraph =",
                    "* красивый интерфейс для понятие изменение вашей скорости (BPS)",
                    "",
                    "DragSetting",
                    "зайдите в чат наведитесь на элемент и начинайте его двигать мышкой и вы сможете передвинуть элемент в любую точку экрана"
            },
            new String[]{
                    "ClickGui",
                    "",
                    "У нас в проекте есть 3 вида CLICKGUI",
                    "чтобы отрыть clickgui нажмите правый шифт на клавиатуре он находится под под ENTER и выше CTRL (немецкая клавиатуре STRG)",
                    "",
                    "ДИЗАЙНЫ",
                    "DropDown - Cs Go - Flat Gui",
                    "",
                    "= DropDown =",
                    "* панели с настройкой под модулем или же в отдельном окне. Первая гуи которая у вас откроется как по мнение владельца самая лучшая. всего 5 панелей: COMBAT - MOVEMENT - PLAYER - RENDER - MISC",
                    "* COMBAT = это функционал пвп или же сражение",
                    "* MOVEMENT = в основном увеличение скоростей передвижение или функционал полета",
                    "* PLAYER = функционал связаный с игроком например отправление команд или выключение задержек",
                    "* RENDER = визуальная 2Д и 3Д часть улучшает внешний вид запушеного DLC",
                    "* MISC = это остальное по читу например PVE функционал или просто второстипенные функции которые не подходят под прошлые категории",
                    "= Cs-Go =",
                    "* разделение на колонке в колонке то есть слева у вас будут категории кликнув можно их изменить. Настройки пишутся сразу под модулем не нужно кликать ничего",
                    "= FlatGui =",
                    "* модули с категории в отдельной колонке а настройки открываются на 2-ю часть гуи",
                    "",
                    "НАСТРОЙКИ",
                    "= Слайдер =",
                    "* это настройка значение даю пример |1_______5_______10 вы можете передвинуть слайдер на свое значение или написать его сами кликнув на значение",
                    "= Переключатель =",
                    "* это включение выключение того или иного чекбокса",
                    "= Bind =",
                    "* это будет настройка клавиши на которую нужно кликнуть мышкой и нажать клавишу и при нажатии клавиши будет происходить действие которые вы забиндили",
                    "= Color =",
                    "* вы можете настроить цвет через color picker также можете настроить прозрачность нижней линией",
                    "= ItemSetting =",
                    "* (AutoSwap - MultiServerHelper) выберите нужные предмет между которыми будут происходить действие можете найти по пиоску",
                    "= ItemMultiSetting =",
                    "* (ContainerStealer) можете выбрать множество предметов для лута из сундуков нажимаете на предмет он у вас подсвечивается а потом отрисовуется предметы которые вы выбрали после нажмите кнопку сохранить и у вас будут использоваться только выбранные предметы (можете использовать поиск)",
                    "= StringSetting =",
                    "* настройки текста то есть вы можете написать свой текст (Пример Module HWSPAMMER)",
                    "= ModeSetting =",
                    "* выбор мода например есть моды (Пример MODULE CustomModel) <Демон>,<Кролик>,<Джефф>,<Амогус> мы выбираем 1 мод и он будет использвать",
                    "= MultiModeSetting =",
                    "* выбор нескольных модов (Пример MODULE ElytraTarget) Вы можете выбрать несколько модов например <AutoFireWork>.<Predict>,<AlwaysHit> можете выбрать сразу несколько и будут выполнятся сразу несколько действий"
            },
            new String[]{
                    "AuraNew",
                    "",
                    "Настройка AuraNew",
                    "",
                    "Ротации (Моды)",
                    "= Custom =",
                    "* Будет рассмотрен в отдельном абзаце ниже.",
                    "= Smooth =",
                    "* плавная ротация для обхода средних античитов, используя мультипоинт с простыми логиками.",
                    "= Snap =",
                    "* почти моментальная ротация без задержек для слабых античитов.",
                    "= SmotionSnap =",
                    "* более плавная ротация в отличии от Snap, но принцип работы тот же.",
                    "= HVH =",
                    "* моментальная ротация для использования на HVH серверах.",
                    "= Современная =",
                    "* ротация, которая обойдет новые сложные античиты, но имеет маленькую скорость.",
                    "= СпукиТайм =",
                    "* обходит сервер SpookyTime (ip: mc.spookytime.su).",
                    "= HW =",
                    "* обходит сервер Holyworld (ip: mc.holyworld.ru / .su / .fun).",
                    "= Фантайм =",
                    "* на данный момент может не работать из-за фиксов, но предназначена для FunTime (ip: funtime.su).",
                    "= SpookyFast =",
                    "* более быстрая ротация для сервера SpookyTime.",
                    "= Vulcan / Intave / Matrix =",
                    "* обходы соответствующих античитов разной степени сложности.",
                    "= LonyGrief + WellMine =",
                    "* обход античита Sloth и самих серверов.",
                    "= MineLegacy + LegendsGrief =",
                    "* обход слабых античитов.",
                    "= Test =",
                    "* тестовая киллаура для обхода античитов на основе нейросетей, пока не идеальна.",
                    "= RW =",
                    "* обходит сервер ReallyWorld (ip: mc.reallyworld.su).",
                    "= Повторяющая =",
                    "* в точности повторяет все скорости и движения камеры противника.",
                    "",
                    "Прочие настройки",
                    "= Коррекция =",
                    "* Свободная: дает вам свободное движение. Сфокусированная: фиксирует ваше движение.",
                    "= Таргет ЕСП =",
                    "* на момент написания (30/10/2025) стабильно работает только 'Круг', так как 'Призраки' не оптимизированы. В основе это рендер элементов на противнике.",
                    "= Сброс Спринта =",
                    "* различные легитные моды для обхода античитов и корректной работы системы критических ударов.",
                    "",
                    "Настройка Custom ротации",
                    "Это максимально сложная в настройке ротация, но вот основы:",
                    "* Скорости (Yaw / Pitch): ставьте примерно Yaw 90-120, Pitch 35-50.",
                    "* Плавность: от 0.35 до 0.50.",
                    "* Сглаживание: от 0.45 до 0.65.",
                    "* Мультипоинт: Точка 'Динамическая', Сила смещения 0.05-0.15, Скорость 0.20-0.30.",
                    "* Интерполяция: подбирайте индивидуально на тестовых серверах (например, pvp.mioclient.me).",
                    "* Гуманизация: используйте стандартные настройки.",
                    "* Джиттер: включаем все, ставим средние значения и выбираем мод 'Комбинированный' или 'Случайный'.",
                    "* МикроДвижение: 'Круговые' или 'Хаотичные', также средние значения.",
                    "* GCD: включаем.",
                    "* Тайминг ротации: под каждый сервер свой (SpookyTime - 'Постоянная', Holyworld - 'До и после', Funtime - 'Nexus').",
                    "* Нормализация углов, Плавный переход в 360: включаем."
            },
            new String[]{
                    "AltManager",
                    "",
                    "Управление аккаунтами",
                    "",
                    "Основные функции",
                    "= Добавление аккаунта =",
                    "* Введите имя в текстовое поле и нажмите кнопку 'Добавить' или Enter",
                    "* Используйте генератор случайных имен для быстрого создания",
                    "",
                    "= Удаление аккаунта =",
                    "* Нажмите красную кнопку 'X' на карточке аккаунта",
                    "* Аккаунт исчезнет с красивой анимацией взрыва",
                    "",
                    "= Избранное =",
                    "* Нажмите звездочку на карточке чтобы добавить в избранное",
                    "* Избранные аккаунты отображаются первыми в списке",
                    "",
                    "= Бан аккаунта =",
                    "* Нажмите средную кнопку мыши (колесико) на аккаунте",
                    "* Забаненные аккаунты помечаются красным значком",
                    "",
                    "= Поиск аккаунтов =",
                    "* Нажмите на лупу внизу экрана",
                    "* Введите имя для фильтрации списка",
                    "* Поиск работает в реальном времени",
                    "",
                    "Навигация",
                    "= Скролл =",
                    "* Используйте колесико мыши для прокрутки списка",
                    "* Поддерживается плавная прокрутка",
                    "",
                    "= Выбор аккаунта =",
                    "* Кликните на карточку аккаунта для входа",
                    "* Текущий аккаунт отображается вверху экрана",
                    "* Выбранный аккаунт подсвечивается рамкой",
                    "",
                    "Дополнительно",
                    "= Очистка списка =",
                    "* Кнопка 'X' удаляет все аккаунты сразу",
                    "* Будьте осторожны - это действие необратимо",
                    "",
                    "= Генератор имен =",
                    "* Создает случайные игровые никнеймы",
                    "* Поддержка различных стилей: gaming, celebrity, fictional",
                    "",
                    "= Справка =",
                    "* Кнопка книги открывает это руководство",
                    "* Используйте стрелки для переключения страниц"
            }
    );

    private enum LineStyle {
        TITLE, SUB_HEADER, BULLET_POINT, EMPHASIZED, NORMAL, EMPTY
    }

    private static class StyledLine {
        final String text;
        final LineStyle style;
        final float height;
        final float fontSize;

        StyledLine(String text, LineStyle style, float height, float fontSize) {
            this.text = text;
            this.style = style;
            this.height = height;
            this.fontSize = fontSize;
        }
    }

    private final List<StyledLine> wrappedLines = new ArrayList<>();
    private final Animation openAnimation = new Animation();
    private final Animation closeAnimation = new Animation();
    private final Animation scrollbarAnimation = new Animation();
    private final Animation pageSwitchAnimation = new Animation();
    private boolean closing = false;

    private int currentPage = 0;
    private int targetPage = 0;

    private int totalCharsToAnimate = 0;
    private int animatedChars = 0;
    private long lastCharTime = 0;
    private static final long TYPING_DELAY_MS = 5;

    private float scrollOffset = 0;
    private float targetScrollOffset = 0;
    private boolean draggingScroll = false;
    private float dragStartY = 0;
    private float dragStartOffset = 0;
    private float totalTextHeight = 0;

    public ReadmeGui() {
        super(new StringTextComponent("README GUI"));
    }

    @Override
    protected void init() {
        super.init();
        this.openAnimation.run(1.0, 0.4, Easings.EXPO_OUT);
        this.closeAnimation.set(0.0);
        this.pageSwitchAnimation.set(1.0);
        this.closing = false;
        this.scrollOffset = 0;
        this.targetScrollOffset = 0;

        switchPage(0, true);
    }

    @Override
    public void onClose() {
        if (this.closing) return;
        this.closing = true;
        this.closeAnimation.run(1.0, 0.3, Easings.EXPO_IN);
        this.openAnimation.run(0.0, 0.3, Easings.EXPO_IN);
    }

    private void switchPage(int newPage, boolean instant) {
        if (this.targetPage == newPage && !instant) return;
        this.targetPage = newPage;
        if (instant) {
            this.currentPage = newPage;
            this.pageSwitchAnimation.set(1.0);
            prepareWrappedLines();
        } else {
            this.pageSwitchAnimation.run(0.0, 0.2, Easings.CUBIC_IN);
        }
    }

    private List<String> wrapText(String text, float maxWidth, float fontSize) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) return lines;

        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (Fonts.SF_BOLD.getWidth(currentLine + " " + word, fontSize) < maxWidth) {
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    private void prepareWrappedLines() {
        wrappedLines.clear();
        totalTextHeight = 0;
        totalCharsToAnimate = 0;
        float textWidth = 600 - 50;

        String[] pageText = PAGES_TEXT.get(currentPage);

        for (String originalLine : pageText) {
            if (originalLine.isEmpty()) {
                wrappedLines.add(new StyledLine("", LineStyle.EMPTY, 8, 10f));
                totalTextHeight += 8;
                continue;
            }

            LineStyle style;
            float height;
            float fontSize;
            String textToWrap = originalLine;

            if (originalLine.equals("README") || originalLine.equals("ClickGui") || originalLine.equals("AuraNew") || originalLine.equals("AltManager") || originalLine.equals("НАСТРОЙКИ") || originalLine.equals("ДИЗАЙНЫ") || originalLine.equals("Прочие настройки") || originalLine.equals("Настройка Custom ротации") || originalLine.equals("Управление аккаунтами") || originalLine.equals("Основные функции") || originalLine.equals("Навигация") || originalLine.equals("Дополнительно")) {
                style = LineStyle.TITLE;
                height = 22;
                fontSize = 18f;
            } else if (originalLine.equals("DragSetting") || originalLine.equals("Ротации (Моды)")) {
                style = LineStyle.EMPHASIZED;
                height = 22;
                fontSize = 14f;
            } else if (originalLine.startsWith("= ")) {
                style = LineStyle.SUB_HEADER;
                height = 18;
                fontSize = 12f;
                textToWrap = originalLine.substring(2, originalLine.length() - 2);
            } else if (originalLine.startsWith("* ")) {
                style = LineStyle.BULLET_POINT;
                height = 14;
                fontSize = 10f;
                textToWrap = originalLine.substring(2);
            } else {
                style = LineStyle.NORMAL;
                height = 12;
                fontSize = 10f;
            }
            List<String> lines = wrapText(textToWrap, style == LineStyle.BULLET_POINT ? textWidth - 15 : textWidth, fontSize);
            for (String line : lines) {
                wrappedLines.add(new StyledLine(line, style, height, fontSize));
                totalTextHeight += height;
                totalCharsToAnimate += line.length();
            }
        }

        this.animatedChars = 0;
        this.lastCharTime = System.currentTimeMillis();
        this.scrollOffset = 0;
        this.targetScrollOffset = 0;
    }

    private void updateAnimations() {
        openAnimation.update();
        closeAnimation.update();
        scrollbarAnimation.update();
        pageSwitchAnimation.update();

        if (pageSwitchAnimation.isFinished() && currentPage != targetPage) {
            currentPage = targetPage;
            prepareWrappedLines();
            pageSwitchAnimation.run(1.0, 0.2, Easings.CUBIC_OUT);
        }

        long currentTime = System.currentTimeMillis();
        if (animatedChars < totalCharsToAnimate && currentTime - lastCharTime > TYPING_DELAY_MS) {
            animatedChars++;
            lastCharTime = currentTime;
        }

        float maxScroll = Math.max(0, totalTextHeight - 360);
        targetScrollOffset = Math.max(0, Math.min(maxScroll, targetScrollOffset));
        if (Math.abs(scrollOffset - targetScrollOffset) > 0.1f) {
            scrollOffset += (targetScrollOffset - scrollOffset) * 0.15f;
        } else {
            scrollOffset = targetScrollOffset;
        }
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

        float easedAnim = openAnimation.get() * (1.0f - closeAnimation.get());
        if (easedAnim <= 0.01f) return;

        float mainWidth = 600;
        float mainHeight = 400;

        float centerX = this.width / 2.0f;
        float centerY = this.height / 2.0f;

        float mainX = centerX - mainWidth / 2;
        float mainY = centerY - mainHeight / 2;

        matrix.push();
        matrix.translate(centerX, centerY, 0);
        matrix.scale(easedAnim, easedAnim, 1);
        matrix.translate(-centerX, -centerY, 0);

        float iconSize = 24f;
        float iconX = mainX + mainWidth / 2f - iconSize / 2f;
        float iconY = mainY - iconSize - 10f;
        Fonts.iconf.draw(matrix, "k", iconX - 15, iconY - 45, clientColor, iconSize + 15);
        BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(matrix, mainX, mainY, mainWidth, mainHeight, ColorUtil.replAlpha(-1, 150), Round.of(12)));
        BlurRenderer2.draw(6);

        float textAlpha = pageSwitchAnimation.get();
        matrix.push();
        matrix.translate(0, 20 * (1 - pageSwitchAnimation.get()), 0);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.makeScissorBox(mainX + 10, mainY + 10, mainX + mainWidth - 20, mainY + mainHeight - 10);

        float yOffset = mainY + 20 - scrollOffset;
        int charsDrawn = 0;
        boolean firstBulletOfGroup = true;

        for (int i = 0; i < wrappedLines.size(); i++) {
            if (charsDrawn >= animatedChars) break;

            StyledLine styledLine = wrappedLines.get(i);
            int charsInLine = styledLine.text.length();
            int charsToDraw = Math.min(charsInLine, animatedChars - charsDrawn);
            String textToRender = styledLine.text.substring(0, charsToDraw);

            float textX = mainX + 25;

            switch (styledLine.style) {
                case TITLE:
                    Fonts.SF_BOLD.draw(matrix, textToRender, mainX + mainWidth / 2f - Fonts.SF_BOLD.getWidth(styledLine.text, styledLine.fontSize) / 2f, yOffset, ColorUtil.multAlpha(clientColor, textAlpha), styledLine.fontSize);
                    break;
                case EMPHASIZED:
                    Fonts.SF_BOLD.draw(matrix, textToRender, textX, yOffset, ColorUtil.multAlpha(clientColor, textAlpha), styledLine.fontSize);
                    break;
                case SUB_HEADER:
                    Fonts.SF_BOLD.draw(matrix, textToRender, textX, yOffset, ColorUtil.multAlpha(ColorUtil.getColor(255, 215, 0), textAlpha), styledLine.fontSize);
                    break;
                case BULLET_POINT:
                    if (firstBulletOfGroup) {
                        Fonts.SF_BOLD.draw(matrix, "•", textX, yOffset + 2, ColorUtil.multAlpha(clientColor, textAlpha), 10f);
                    }
                    Fonts.SF_BOLD.draw(matrix, textToRender, textX + 15, yOffset, ColorUtil.multAlpha(ColorUtil.WHITE, textAlpha), styledLine.fontSize);
                    break;
                case NORMAL:
                default:
                    Fonts.SF_BOLD.draw(matrix, textToRender, textX, yOffset, ColorUtil.multAlpha(ColorUtil.WHITE, textAlpha), styledLine.fontSize);
                    break;
            }

            if (i + 1 < wrappedLines.size() && wrappedLines.get(i + 1).style == LineStyle.BULLET_POINT) {
                firstBulletOfGroup = (styledLine.style != LineStyle.BULLET_POINT);
            } else {
                firstBulletOfGroup = true;
            }


            yOffset += styledLine.height;
            charsDrawn += charsInLine;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        matrix.pop();

        float maxScroll = Math.max(0, totalTextHeight - (mainHeight - 40));
        if (maxScroll > 0) {
            boolean scrollbarHovered = mouseX >= mainX + mainWidth - 12 && mouseX <= mainX + mainWidth - 2;
            scrollbarAnimation.run(scrollbarHovered || draggingScroll ? 1.0 : 0.5, 0.2, Easings.EXPO_OUT);

            float scrollableHeight = mainHeight - 20;
            float scrollBarHeight = Math.max(30, (scrollableHeight / totalTextHeight) * scrollableHeight);
            float scrollBarY = mainY + 10 + (scrollOffset / maxScroll) * (scrollableHeight - scrollBarHeight);

            float alpha = scrollbarAnimation.get() * easedAnim;
            int scrollbarColor = ColorUtil.interpolate(ColorUtil.multAlpha(Color.GRAY.getRGB(), alpha), ColorUtil.multAlpha(clientColor, alpha), scrollbarAnimation.get());

            RenderUtil.Rounded.roundedRect(matrix, mainX + mainWidth - 12, mainY + 10, 8, scrollableHeight, ColorUtil.multAlpha(new Color(25, 25, 25).getRGB(), alpha * 0.7f), Round.of(4));
            RenderUtil.Rounded.roundedRect(matrix, mainX + mainWidth - 12, scrollBarY, 8, scrollBarHeight, scrollbarColor, Round.of(4));
        }

        float pageButtonY = mainY + mainHeight - 25;
        float pageButtonSize = 20;

        if (currentPage > 0) {
            float prevButtonX = mainX - 25;
            boolean prevHovered = mouseX >= prevButtonX && mouseX <= prevButtonX + pageButtonSize && mouseY >= pageButtonY && mouseY <= pageButtonY + pageButtonSize;
            int prevColor = prevHovered ? ColorUtil.interpolate(new Color(40, 40, 40).getRGB(), clientColor, 0.5f) : new Color(30, 30, 30).getRGB();
            RenderUtil.Rounded.roundedRect(matrix, prevButtonX, pageButtonY, pageButtonSize, pageButtonSize, prevColor, Round.of(5));
            Fonts.icon.draw(matrix, "J", prevButtonX + 6, pageButtonY + 5.5f, -1, 8f);
        }

        if (currentPage < PAGES_TEXT.size() - 1) {
            float nextButtonX = mainX + mainWidth + 25 - pageButtonSize;
            boolean nextHovered = mouseX >= nextButtonX && mouseX <= nextButtonX + pageButtonSize && mouseY >= pageButtonY && mouseY <= pageButtonY + pageButtonSize;
            int nextColor = nextHovered ? ColorUtil.interpolate(new Color(40, 40, 40).getRGB(), clientColor, 0.5f) : new Color(30, 30, 30).getRGB();
            RenderUtil.Rounded.roundedRect(matrix, nextButtonX, pageButtonY, pageButtonSize, pageButtonSize, nextColor, Round.of(5));
            Fonts.icon.draw(matrix, "J", nextButtonX + 6, pageButtonY + 5.5f, -1, 8f);
        }

        String pageText = (currentPage + 1) + " / " + PAGES_TEXT.size();
        Fonts.SF_BOLD.draw(matrix, pageText, mainX + mainWidth / 2f - Fonts.SF_BOLD.getWidth(pageText, 10f) / 2, pageButtonY + 30, -1, 10f);

        matrix.pop();
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float mainWidth = 600;
        float mainHeight = 400;
        float mainX = (this.width / 2.0f) - mainWidth / 2;
        float mainY = (this.height / 2.0f) - mainHeight / 2;

        float pageButtonY = mainY + mainHeight - 25;
        float pageButtonSize = 20;

        if (currentPage > 0) {
            float prevButtonX = mainX - 25;
            if (mouseX >= prevButtonX && mouseX <= prevButtonX + pageButtonSize && mouseY >= pageButtonY && mouseY <= pageButtonY + pageButtonSize) {
                switchPage(currentPage - 1, false);
                return true;
            }
        }
        if (currentPage < PAGES_TEXT.size() - 1) {
            float nextButtonX = mainX + mainWidth + 25 - pageButtonSize;
            if (mouseX >= nextButtonX && mouseX <= nextButtonX + pageButtonSize && mouseY >= pageButtonY && mouseY <= pageButtonY + pageButtonSize) {
                switchPage(currentPage + 1, false);
                return true;
            }
        }

        float maxScroll = Math.max(0, totalTextHeight - (mainHeight - 40));
        if (maxScroll > 0 && mouseX >= mainX + mainWidth - 12 && mouseX <= mainX + mainWidth - 2 && mouseY >= mainY + 10 && mouseY <= mainY + mainHeight - 10) {
            draggingScroll = true;
            dragStartY = (float) mouseY;
            dragStartOffset = scrollOffset;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingScroll = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingScroll) {
            float mainHeight = 400;
            float scrollableHeight = mainHeight - 20;
            float dy = (float) mouseY - dragStartY;
            targetScrollOffset = dragStartOffset + (dy / scrollableHeight) * totalTextHeight;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        targetScrollOffset -= delta * 20;
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

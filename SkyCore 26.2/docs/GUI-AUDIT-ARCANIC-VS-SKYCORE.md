# Аудит GUI: Arcanic Visuals ↔ SkyCore 26.2

> **Обновление 2026-11-12.** Документ описывает состояние **до** консолидации GUI.
> Сейчас в клиенте остался один интерфейс — `RenderGuiHelper` (стиль «Skycore»);
> меню Decide, Workspace, Swerve/`MenuScreen`, `ClickGuiScreen` и редактор HUD
> удалены. Настройки модулей больше не рисуются внутри карточек: они выезжают
> отдельной панелью справа по ПКМ на модуле (см. `renderSettingsPanel`).
> Пункты ниже про «два параллельных ClickGUI», `MenuScreen` и редактор HUD
> следует читать как исторические.

Дата: 2026-10-27. Задача: `Внимательно посмотри GUI клиента Arcanic и клиента SkyCore 26.2`.
Тип работы: **только чтение**. Код не изменялся, файлы других клиентов не тронуты.
Создан один файл документации — этот отчёт.

---

## 0. Резюме

1. В SkyCore 26.2 **два параллельных ClickGUI**: исходное меню на `RenderGuiHelper` (2839 строк, 8 разделов, включая конфиги и косметику) и портированное из Swerve/Arcanic меню `MenuScreen` (2785 строк, 5 категорий, 2 страницы). Переключение — `ClickGUIModule.menuStyle()` (`Swerve` / `Skycore`), см. `SkycoreClient.onInitializeClient()`.
2. Портированное меню **геометрически и поведенчески совпадает** с `ArcanicMenu`: панель 1010×640, `u = min(H/1080, W/1060) · scale · 0.86`, карточки 52 px в две колонки, попап 300 px, тот же док, те же пружины/тайм-твины, тот же набор палитр (у SkyCore расширен до 20).
3. **Главное расхождение — не стиль, а состав страниц.** У Arcanic 4 страницы меню + отдельные экраны (редактор HUD, аккаунты, «Social», диалоги, пикер цвета, загрузка, замена титульного экрана). У SkyCore в портированном меню — 2 страницы (Модули, Настройки), а всё остальное либо живёт в легаси-меню, либо отсутствует.
4. У SkyCore **нет редактора HUD**: перетаскивание работает только при открытом чате (`HudRenderer.render` → `ChatScreen` → `draggables.dragTo(...)`), элементов 10, и у них **нет собственных настроек** (только `InterfaceModule.elements` мультивыбор + `ClickGUIModule.style`).
5. У Arcanic каждый HUD-элемент — это `HudModule extends Module` со своими настройками (`scale`, `background`) и полноценный редактор: сетка, рамки, снап к центру/краям, колесо — размер, ПКМ — вкл/выкл, строка подсказок.
6. **Локализация:** легаси-меню и HUD SkyCore локализованы (`Translations` + `SelectAndSaveHelper`, `beta-language.json`), а портированное меню — нет: заголовки («Клавиша», «Ничего не найдено») по-русски, имена и описания модулей — сырые английские (`Attack Aura`, `Toggle ...`). Получается смешанный язык в одном окне.
7. **Пикер цвета** в SkyCore проще источника: SV-квадрат + hue + alpha + HEX. У Arcanic ещё пипетка по скриншоту (`Screenshot`), RGB-режим ввода, «цвет из темы», последние цвета.
8. **Производительность:** здесь SkyCore строго лучше. Порт переписан на int-ключи, массивы регионов, упакованные payload — ноль строк/лямбд/боксинга в кадре. В Arcanic в кадре живут `HashMap<String,…>` (`tweens`, `scroll`), `an("ch:" + m.id, …)`, `hover("sb:" + key)`, `hot = String`. Любой перенос из Arcanic обязан быть переписан по правилам SkyCore.
9. **Лицензия:** Arcanic — GPL-3.0 (шапки файлов). Порт в SkyCore уже оформлен с указанием происхождения в javadoc — эту практику надо сохранять для любых новых переносов.

---

## 1. Метод и границы

Прочитаны (полностью или в объёме, достаточном для выводов): все файлы `gui/`, `hud/`, `theme/`, `setting/`, `module/render/ClickGUIModule.java`, `module/render/InterfaceModule.java`, `SkycoreClient.java`, `util/RenderGuiHelper.java`, `util/MouseScrolledHelper.java`, `util/MenuBackgroundRenderState.java`, `mixin/TitleScreenReplaceTitleScreenMixin.java` в SkyCore; `gui/`, `hud/`, `render/`, `core/Theme.java`, `core/MenuPrefs.java`, `core/Module*.java`, `core/Setting.java`, `core/Lang.java`, `core/Category.java`, `media/Music.java`, `emote/*`, `cosmetic/*` в Arcanic.

Изменения в Arcanic (и любом другом клиенте) не вносились. Изменений кода в SkyCore 26.2 в этой задаче нет — только этот документ.

---

## 2. SkyCore 26.2 — карта интерфейса

### 2.1 Точка входа и переключатель меню

`net/skycore/client/SkycoreClient.java`:

```java
clickGUIModule.setOpenAction(() -> {
   if (clickGUIModule.menuStyle().is("Skycore")) {
      instance.setScreenAndShow(new RenderGuiHelper(MODULES, THEMES, config, clickGUIModule));
   } else {
      instance.setScreenAndShow(new MenuScreen(MODULES, config));
   }
});
```

`module/render/ClickGUIModule.java` (51 строка), бинд по умолчанию — `344` (Right Shift):

| Настройка | Тип | Значения / диапазон | По умолчанию |
|---|---|---|---|
| `Style` | Mode | `Transparent`, `Solid` | `Transparent` |
| `Menu style` | Mode | `Swerve`, `Skycore` | `Swerve` |
| `Render factor` | Number | 0.35 … 0.75, шаг 0.01 | 0.45 |

`style` влияет только на легаси-меню и `HudStyle.transparent()`; `renderFactor` — на масштаб легаси-меню и HUD (`HudRenderer.interfaceScale`).

### 2.2 Портированное меню `gui/menu/MenuScreen.java` (2785 строк)

- Страницы: `enum Page { MODULES, SETTINGS }`; вкладок категорий — 5 (`COMBAT`, `MOVEMENT`, `RENDER`, `PLAYER`, `MISC`).
- Док: 2 кнопки по 44 px, `«Модули»`, `«Настройки»`, иконки `IconGlyphs.MISC` и `LegacyIconGlyphs.FILTERS`, подписи появляются на ховере.
- Геометрия: панель `min(1010, W−80) × min(640, H−210)`, отступ `36`, шапка 40, `top/bottom` под линии; карточки — 2 колонки, высота 52, зазор 10; попап — ширина 300, заголовок 66, закреплённая строка бинда 38.
- Прокрутка: `scrolled(context, total, top, bottom)` + погашения сверху/снизу (`edges`) + кастомный скроллбар; контексты — категория, поиск (`SCROLL_SEARCH`), попап (`POPUP_SCROLL`), страница настроек (`SCROLL_PREFS`).
- Клики: не лямбды, а упакованные пары `action/payload` в массивах `regionX/Y/W/H/action/alt/payload`, лимит 512 регионов; hover — целочисленный `hot` (0 / 100 / 200 / 1000 / 2000 / 4000 / 6000 / 7000 / 8000 / 9000 / 9500 …).
- Анимации: `MenuAnim` — пружины (`to`) и тайм-твины (`tween`) в общем `float[]`, ключи-константы (`OPEN`, `SFOCUS`, `TAB_*`, `MODULE_BASE + mi*6`, `SETTING_BASE + …`, `PREFS_*`, `SCROLL_BASE + …`), `MAX_MODULES 128`, `MAX_SETTINGS 2048`.
- Фон: `MenuPrefs.blur` → `BeginFrameHelper.rect(...).blur(S(30), fade)`; иначе — плоская заливка с `MenuPrefs.dim`.
- Страница настроек: 4 группы (`Палитра`, `Звук`, `Вид`, `Поведение`) + 10 строк настроек + полоса палитр (`paletteStrip`), всего 20 палитр.
- Пикер цвета: SV-квадрат, hue-полоса, alpha-полоса, HEX-поле, кнопка закрытия; состояние — `pickerHue/Saturation/Value/Alpha/Hex`.
- Окно модуля: `ModulePopup.java` (232 строки) — заголовок и строка «Клавиша» закреплены, прокручиваются только настройки; окно встаёт сбоку панели, при нехватке места ложится поверх; тянет тонкую линию к карточке.
- Звук: `MenuSound` — 17 событий (`CLICK`, `HOVER`, `TICK`, `TYPE`, `POP`, `RESET`, `SELECT`, `CHARGE`, `DRAIN`, `GRAB`, `DROP`, `OPEN`, `CLOSE`, `TOGGLE_ON/OFF`, `PAGE`, `TAB`) через `skycore:sounds.json` и `SimpleSoundInstance`.
- Текст: `MenuText` → `InterBoldHelper.interMedium/interSemibold/interBold` (три веса), метрики берутся у того же шрифта.
- Палитры: `MenuPalette` (479 строк) — 20 палитр, плавный переход токенов (320/520 мс), `light()` = палитра «Светлая»; поле `fx` сохранено из источника, но **не используется** (анимированных фонов нет).

### 2.3 Исходное (легаси) меню `util/RenderGuiHelper.java` (2839 строк)

`enum View`: `COMBAT`, `MOVEMENT`, `RENDER`, `PLAYER`, `MISC`, **`AUTOBUY`**, **`CONFIGS`**, **`COSMETICS`**.
Что есть в легаси и чего нет в портированном:

- вертикальная навигация по категориям + горизонтальные табы (`renderVerticalTabGroup`, `renderTabGroup`), смена вида с анимацией (`viewTransition`);
- **профили конфигов**: карточки с автором, избранным, удалением с задержкой (`renderConfigCard`, `renderConfigs`, `CONFIG_CACHE_MS 750`), поиск по профилям;
- **косметика**: мечи и аксессуары, карточки с превью (`renderCosmetics`, `renderCosmeticCard`, `renderCosmeticThumb`, `CosmeticsRenderLayer`);
- **слоты цветов темы** (`renderThemeColorRow`, `ThemeSlotPickerTarget`) и свой пикер (`renderColorPicker`, `renderHueTrack`, `commitPickerHex`);
- строка поиска (`renderSearch`), кнопка языка (`LANGUAGE_ICON`, `renderLanguageButton`), строка ввода конфига (`renderConfigInput`);
- поэтапное появление модулей (`MODULE_STAGGER_MS 72`, лимит 7), «полёт» при открытии (`MODULE_FLY_MS 540`), `OPEN_MS 300` / `CLOSE_MS 240`.

### 2.4 Главное меню `util/MouseScrolledHelper.java` (1000 строк)

Заменяет ванильный `TitleScreen` через `mixin/TitleScreenReplaceTitleScreenMixin` (`init` → `setScreenAndShow(new MouseScrolledHelper())`, `cancel`).
Внутри: текстурный фон `skycore:textures/gui/main_menu_new.png`, анимированное приветствие (48 кадров, `TYPE_LINE_GAP_MS 280`, `GREETING_ERASE_MS 1650`), выбор языка (`renderLanguageSelection`, `renderLanguagePrompt`, флаги `english.png`/`russian.png`), альт-менеджер (`renderAltManager`, `openAltManager`, `commitNick`), ник, юридическая плашка, цитаты.

### 2.5 HUD

`hud/` — 13 файлов, 10 элементов:

`WatermarkHud` (241), `InformationHud`, `HotkeysHud` (294), `BindsHud` (480), `HotbarHud` (277), `PotionsHud` (398), `CooldownsHud` (465), `ArmorHudHud` (232), `TargetHudHud` (428), `NotificationsHud` (669).

- `HudRenderer` (311): регистрирует `Draggable` каждого элемента, `applySavedPositions()` (нормализованные координаты → пиксели, `AxisLock`), `updateMotion`, отрисовка по флагам `InterfaceModule.is*Enabled()`.
- `Draggable` (204) + `DraggableManager` (99): пружинная доводка с фиксированным шагом `0.008333334` с (120 Гц), ограничение по краям, `normalizedTargetX/Y` для сохранения.
- Перетаскивание **только при открытом чате**: `if (instance.gui.screen() instanceof ChatScreen) dragTo(...) else endDrag()`.
- `HudStyle` (51): прозрачность/блюр ставятся из `ClickGUIModule.style`, `blurRadius = 36 × value`, `border()` = 0.
- Собственных настроек у элементов нет: `grep "add(new"` по `hud/` не даёт ни одного совпадения; всё конфигурируется `InterfaceModule.elements` (мультивыбор из 10 имён) и стилем ClickGUI.

### 2.6 Данные, настройки, локализация

- `Setting<T>` (универсальный) + 8 наследников: `BooleanSetting`, `NumberSetting`, `ModeSetting`, `MultiSelectSetting`, `StringSetting`, `ColorSetting` (флаг `alpha`), `KeyBindSetting`, `ActionSetting`. У каждой настройки `name`, `description`, `isVisible()`/`visibleWhen()`. Дефолтные описания — английские строки (`"Toggle " + name + "."`).
- `MenuPrefs` — отдельный файл `skycore/clickgui-menu.json`: `sounds`, `volume`, `hoverSounds`, `sliderSounds`, `blur`, `dim`, `gradient`, `animations`, `animSpeed`, `scale`, `palette`; загрузка терпима к битому файлу.
- `ConfigManager` (`skycore/beta-local.json`) — модули, настройки, темы, позиции `Draggable`.
- Локализация: `config/Translations` + `util/SelectAndSaveHelper` (`skycore/beta-language.json`, карта RU-имён модулей/настроек/описаний, `isRussian()`), используется легаси-меню, `NotificationsHud`, `DescriptiveHeightHelper`. Портированное меню её **не использует**.

---

## 3. Arcanic — карта интерфейса

### 3.1 `gui/ArcanicMenu.java` (1576 строк)

- Страницы: `MODULES`, `WARDROBE`, `PRESETS`, `SETTINGS`. Док — 5 кнопок по 44 px: `Модули`, `Косметика`, `Редактор HUD`, `Пресеты`, `Язык` (`DOCK`, `dockClick`), у «Языка» вместо страницы открывается `PromptScreen.pick`.
- Вкладки категорий — 6: `COMBAT`, `PLAYERS`, `WORLD`, `EFFECTS`, `HANDS`, `HUD`; отдельная категория `COSMETICS` уведена в гардероб.
- Геометрия/анимации/регионы: те же формулы, что унаследовал SkyCore (панель 1010×640, `u · 0.86`, `ease`, `an`, `tween`, `scrolled`, `edges`, `scrollbar`, `card`, `keyChip`, `nameLit`, `toggle`, `valueBox`, `segmented`, `row`), но на `GuiGraphics` и в **реальных пикселях** (`Px`).
- Отличия в шапке: кнопки «Создать пресет» / «Вставить код», мастер-тумблер для косметики, «Сохранить»/«Импорт».
- Регионы — `record Region(..., Runnable click, Runnable alt, Drag drag, String id)` в `ArrayList`, пересоздаётся каждый кадр; hover — `String hot`.

### 3.2 Экраны

| Файл | Строк | Что делает |
|---|---|---|
| `gui/HudEditorScreen.java` | 147 | сетка 40 px, рамки, ПКМ — вкл/выкл, ЛКМ — тащить, колесо — `scale ±0.05`, подпись элемента, нижняя плашка подсказок, `Config.save()` на закрытии |
| `gui/AccountManagerScreen.java` | 714 | локальные профили «плитками» с головами (5×? по 9 на страницу), ввод ника, Microsoft-аккаунты, запрет смены в мире |
| `gui/SocialScreen.java` | 360 | карточка аккаунта над титульным экраном, `EditBox` для Premium-ключа, состояние/подписка |
| `gui/PromptScreen.java` | 451 | набор диалогов: `ask`, `field`, `fieldAndSwitch`, `pick` (список), `Kit` — общая вёрстка/логические координаты |
| `gui/ColourPicker.java` | 581 | пипетка по скриншоту, SV, hue, alpha, HEX/RGB-поле, последние цвета, «следует теме» |
| `gui/PrefsPage.java` | 148 | 4 группы настроек меню из `Modules.PREFS` |
| `gui/PresetsPage.java` | 358 | список пресетов, табы «все/локальные/по коду», кнопки load/save/share/delete, `CloudPresets.fetch(code)` |
| `gui/WardrobePage.java` | 339 | плащи (5 колонок), ножи (`MeleeCatalog`), головные уборы, рюкзаки, крылья, питомцы; превью через `TexWarm`/`MeleePreview` |
| `gui/ModulePopup.java` | 194 | окно настроек модуля (тот же приём «сбоку панели + линия-связка») |
| `gui/CalibScreen.java` | 50 | dev-экран калибровки размеров текста/иконок |

### 3.3 Front-end (то, чего у SkyCore нет как класса)

- `FrontEndMenu` (347) — перестройка ванильного титульного экрана: `SPECS` по ключам переводов (`menu.singleplayer`, `menu.multiplayer`, `menu.options`, `menu.quit` …), сортировка, свои иконки, добавленные пункты «Arcanic Social» и «Аккаунты», подсказка о входе.
- `FrontEndLoading` (191) — единая сцена загрузки ресурсов/мира с прогрессом, «уходом» сплэша (`LEAVE_MS 1500`, `LAND_MS 880`) и взаимодействием с островком.
- `FrontEndBackdrop` (145) / `MenuBackdrop` (153) — шейдерные фоны на своём pipeline (`core/front_end`, `core/menu_arcanic`), 4 режима (`Nebula`, `Silk`, `Contour`, `Ember`), палитра через `DynamicTexture`, плавный переход.
- `render/BrandLogo` (152), `render/Frost` (211, фрост-подложка), `render/SectionIcons` (154).

### 3.4 HUD

- `hud/HudModule` (154) — `extends Module`, поля `fx/fy` (доли экрана), `w/h`, настройки `scale` (0.5…2) и `bg` («Подложка»), `prepare()` считает `k = H/1080 × scale`, готовые `plate()`, `header()`, `chip()`, `badge()`.
- Элементы: `HudElements` (692) — `Performance`, `Coordinates`, `Potions`, `HotKeys`, `Cooldowns`, `Armor`, `Keystrokes`, `Clicks`; плюс `DynamicIsland` (762: время/ник/сервер/FPS/пинг, лицо игрока, раскрытие, плеер), `TargetHud` (441: стиль «Классический/Компактный», полоска, снаряжение, всплывающий урон), `Notify.Prefs` (362: всплывающие сообщения под прицелом — модули, предметы, эффекты), `Toasts` (140).
- `HudDrag` (116): порог 4 px на «клик vs перетаскивание», снап 8 px к центру и краям с направляющими, рамки при открытом чате.
- `HudRenderer` (94): `renderAll` + `menuOverlay` (островок и тосты поверх меню), защита от исключений в отдельном элементе.

### 3.5 Стек отрисовки

- `render/Px` (592): реальные пиксели экрана (`begin/end` через `pose().scale(...)`), атлас 1024² с 9-slice дисками/кольцами/тенями/градиентами (`atlas.json`), иконки из `icons.json` (глифовый шрифт), `MAX_R 36`, глобальный `fade`.
- `render/MsdfText` (201): MSDF-текст с настраиваемой толщиной (`Weight.MEDIUM/SEMIBOLD/BOLD`), кэш глифов.
- `render/Anim` (72): пружины по строковым ключам.
- `render/TexWarm` (99), `GrayTex` (196), `EmbeddedShaders`, `mixin/GuiGraphicsAccess`.
- Порядок слоёв — `g.nextStratum()`.

### 3.6 Тема, настройки, i18n

- `core/Theme` (358): 12 палитр, `fx` 1…6 у шести «анимированных» (Аврора/Матрица/Синтвейв/Призма/Вихрь/Неон), `light` = индекс 5, `style`, `menuScale`, `frontBackground`, плавный переход `FADE_MS 520`.
- `core/MenuPrefs` — это **модуль** с 16 настройками: `language`, `sounds`, `volume`, `hoverSounds`, `sliderSounds`, `blur`, `dim`, `spotlight`, `ripples`, `tips`, `notify`, `animations`, `animSpeed`, `scale`, `drag`, `remember` (+ `posX/posY`, `sync()`).
- `core/Setting` — 4 типа: `Bool`, `Num`, `Mode`, `Colour` (палитра пресетов + `PALETTE` из 12 цветов + «следует теме»), `showIf(...)`.
- `core/Lang` — `en.json` + `ru.json`, `Lang.tr(...)`, карта ванильных ключей (`VANILLA`), префиксы/суффиксы сообщений; локализовано всё меню, HUD, тосты.
- `core/UiSound` — звуки через `SimpleSoundInstance`; `media/Music` — трек/исполнитель/позиция для островка; `emote/EmoteWheel` — колесо эмоций.

---

## 4. Сравнительная матрица

| Подсистема | SkyCore 26.2 | Arcanic | Вывод |
|---|---|---|---|
| Меню: реализаций | 2 (легаси + порт) | 1 | у SkyCore уникальная возможность «двух стилей» |
| Страницы меню | 2 (Модули, Настройки) | 4 + «Язык» в доке | дефицит навигации |
| Вкладки категорий | 5 | 6 (+косметика отдельно) | паритет по смыслу |
| Док | 2 иконки | 5 иконок + подписи | дефицит |
| Карточки модулей | 2 колонки, 52 px, попап | 2–3 колонки, попап | паритет (у Arcanic 3 колонки при ширине ≥ 1100) |
| Окно настроек модуля | закреплённые шапка/бинд, скролл только настроек | скроллится всё окно | **SkyCore лучше** |
| Типы настроек | 8 (+description) | 4 (+showIf) | **SkyCore богаче** |
| Мультивыбор | `MultiSelectSetting` (чипы) | нет | **SkyCore лучше** |
| Пикер цвета | SV+hue+alpha+HEX | + пипетка, RGB, «из темы», последние | дефицит |
| Настройки меню | 10 строк, 4 группы, отдельный JSON | 16 настроек, модуль `PREFS` | дефицит по составу, **лучше по хранению** |
| Палитры | 20 | 12 | **SkyCore богаче** |
| Анимированные фоны палитр | поле `fx` не используется | 6 шейдерных режимов + 4 фона меню | дефицит |
| Размытие | `BeginFrameHelper.blur` | `Frost` + свои шейдеры | разный подход, оба рабочие |
| Шрифты/иконки | Inter (3 веса) + 2 иконочных шрифта (`IconGlyphs`, `LegacyIconGlyphs`) | MSDF + атлас/`icons.json` | SkyCore проще и без второго рендерера |
| Пространство координат | GUI-пиксели | реальные пиксели | SkyCore не требует собственного атласа |
| Слои | очередь `BeginFrameHelper` + `beginFrame/flush` | `nextStratum()` | эквивалент |
| HUD-элементов | 10 | 10 + островок/таргет/уведомления как модули с настройками | паритет по числу, дефицит по конфигурируемости |
| Настройки у HUD-элемента | нет | `scale`, `bg` + свои | дефицит |
| Редактор HUD | нет (только чат) | отдельный экран + сетка/снапы/колесо | **крупный дефицит** |
| Позиции HUD | нормализованные, пружина 120 Гц, `AxisLock` | доли экрана, снап, `drag/remember` | у каждого свои плюсы |
| Уведомления | `NotificationsHud` (тосты) | тосты + всплывающие под прицелом + островок с плеером | дефицит |
| Целевой HUD | `TargetHudHud` (428) | `TargetHud` (441, стили, всплывающий урон) | паритет |
| Конфиги/профили | легаси `CONFIGS` + `/cfg` | страница пресетов + коды + облако | дефицит в портированном меню |
| Косметика | легаси `COSMETICS` (мечи/аксессуары) | гардероб (плащи/ножи/шляпы/рюкзаки/крылья/питомцы, 3D-превью) | дефицит в портированном меню |
| Аккаунты | альт-менеджер в главном меню + `AccountManager`/`SkinService` | отдельный экран + Microsoft + Premium-ключ | паритет по функциям |
| Соц. функции | команды `friend`/`party`/`irc`, `IrcClient`, `PartyClient`; `gui/ClickGuiScreen` — заглушка с табами «Друзья/Party/Общий чат» | `SocialScreen` (карточка), `FrontEndMenu` → Social/Аккаунты | дефицит UI |
| Главное меню | `MouseScrolledHelper` (1000) на текстурах | `FrontEndMenu` + шейдерные фоны + загрузка | разный уровень, у Arcanic «дороже» |
| Экрана загрузки | нет | `FrontEndLoading` | дефицит |
| Локализация | легаси/HUD да, портированное меню — нет | всё, ru/en | **дефект порта** |
| Звук | 17 событий через звуковой движок | `UiSound` через звуковой движок | паритет, **SkyCore аккуратнее** (нет `Clip`-потоков) |
| Hot path кадра | int-ключи, массивы, ноль аллокаций | `String`-ключи, `HashMap`, конкатенация | **SkyCore строго лучше** |
| Сохранение настроек меню | отдельный `clickgui-menu.json` | внутри общего `Config` | **SkyCore лучше** |

---

## 5. Что в SkyCore уже сильнее и не должно пострадать при переносе

1. **Дисциплина кадра**: `MenuAnim` на индексах, `region*` массивы, `hot` как `int`, отсутствие `String`-конкатенаций, `HashMap` и боксинга в отрисовке. Любой код из Arcanic переносится только в такой форме.
2. **Отдельный файл настроек меню** (`MenuPrefs`, `skycore/clickgui-menu.json`) — не ломает формат пользовательских конфигов.
3. **Звук через `sounds.json`** вместо `javax.sound.sampled.Clip` в отдельном потоке: подчиняется ползунку Minecraft.
4. **Закреплённая шапка попапа** модуля (в источнике уезжала вместе со скроллом, а тумблер оставался точкой клика).
5. **8 типов настроек** (включая мультивыбор, ключ, строку, действие) и `description` у каждой.
6. **20 палитр** вместо 12.
7. **Два стиля меню** с переключателем — легаси содержит разделы, которых в порте нет (конфиги, косметика, автобай), и отбирать их у пользователя нельзя.
8. **Нормализованные позиции HUD** с пружинной доводкой на фиксированном шаге и `AxisLock` — перспективнее для редактора, чем доли экрана без инерции.

---

## 6. Дефицит: что стоит переносить из Arcanic и как адаптировать

Оценка: S — до дня, M — 1–3 дня, L — больше. Приоритет — по соотношению «польза / риск».

| # | Что | Приоритет | Объём | Как адаптировать под SkyCore 26.2 |
|---|---|---|---|---|
| 1 | Локализация портированного меню | P0 | S | Прогнать `module.getName()`, `getDescription()`, `setting.getName()`, `getDescription()` через существующий `SelectAndSaveHelper.text(...)`/`Translations`; хром-строки («Клавиша», «Ничего не найдено», «Настройки») вынести в `Translations`. Ничего нового не изобретать — слой уже есть и работает в легаси-меню и `NotificationsHud`. |
| 2 | Редактор HUD | P1 | M | Новый `Screen` (по образцу `HudEditorScreen`), рисующий через `HudRenderer` c флагом редактирования; вместо `HudDrag` — `Draggable`/`DraggableManager` (уже умеют нормализацию и края), сетка 40 px, снап к центру/краям, колесо → масштаб. Приём с `ChatScreen` оставить как быстрый путь. |
| 3 | Настройки у HUD-элементов | P1 | M | Элементы сейчас не `ClientModule`; минимальный путь — `InterfaceModule`-уровневые настройки (`scale`, `background`) с ключами по id элемента, без миграции конфига (`ConfigManager` читает неизвестные ключи терпимо — проверить). Иначе — новый тип `HudElement` с настройками, но это уже смена модели и формата конфига. |
| 4 | Пикер цвета: пипетка, RGB, «из темы», последние цвета | P1 | M | `Screen` уже получает `GuiGraphicsExtractor`; `Screenshot.takeScreenshot` и чтение пикселя — проверить по 26.2 (в Arcanic это 1.21.11). «Из темы» ложится на `MenuPalette.accent()`; последние цвета хранить в `MenuPrefs` (5 индексов), не в статике. |
| 5 | Диалоговый кит (`PromptScreen`) | P1 | M | Нужен для пресетов, переименования профиля, подтверждений удаления. Реализовать на `MenuDraw`/`BeginFrameHelper` + `MenuText`, `EditBox` — штатный; звуки — существующие `MenuSound.SELECT/CLICK`. |
| 6 | Страница пресетов в портированном меню | P2 | L | Данные уже есть: `ConfigManager` профили + `/cfg`. Коды-шеринга (`CloudPresets`) требуют сетевого API — у SkyCore есть `integration/RemoteApiConfig`, `IrcClient`, `PartyClient`; сначала локальные пресеты, потом коды. |
| 7 | Косметика в портированном меню | P2 | L | Данные и рендер уже есть (`CosmeticsRenderLayer`, легаси `renderCosmetics`/`renderCosmeticCard`). Перенос — это новая страница + сетка карточек, без новых источников данных. |
| 8 | Соц. экран (Friends/Party/IRC) | P2 | M | `gui/ClickGuiScreen` — заглушка, но `FriendCommand`, `PartyCommand`, `IrcCommand`, `IrcClient`, `PartyClient` живые. Экран можно собрать на существующих командах/клиентах. |
| 9 | Уведомления: всплывающие под прицелом | P2 | M | Сопоставить с `NotificationsHud` (669 строк) — там уже тосты; «Notify» из Arcanic добавляет предметы/эффекты. Часть логики (использование предметов, конец эффектов) в SkyCore отсутствует. |
| 10 | Островок (DynamicIsland) с плеером | P3 | L | Требует `media/Music`-аналога и интеграции с медиа-провайдером; в SkyCore нет даже заготовки. |
| 11 | Анимированные фоны меню/палитр (`fx`, `MenuBackdrop`) | P3 | L | В SkyCore уже есть свой pipeline `GuiPipelines.MENU_BACKGROUND` (`util/MenuBackgroundRenderState`) — порт шейдеров Arcanic не нужен, режимы можно собрать на нём. Риск по производительности и по совместимости палитр. |
| 12 | Главное меню: загрузочный экран, «дорогие» фоны | P3 | M | У SkyCore свой титульный экран с приветствием и альт-менеджером; выгода спорная, риск сломать привычный вид высокий. |
| 13 | 3 колонки карточек при широком экране | P3 | S | В Arcanic: `cols = cw0 >= S(1100) ? 3 : 2`. В SkyCore жёстко 2 (`rows()`), карточки считаются в `cardX/Y/H` на 128 слотов — расширение до 3 потребует правки буферов и раскладки. |

### Обязательные технические оговорки для любого переноса

- Экран в 26.2 рисует `extractRenderState(GuiGraphicsExtractor, ...)`, а не `render(GuiGraphics, ...)`; вся отрисовка — внутри `CurrentGuiGraphicsExtractorHelper.enter2D(...)` + `BeginFrameHelper.beginFrame()` / `flush()` / `exit2D()`.
- Никаких `String`-ключей анимаций: индексы в `MenuAnim`, payload — `int`. Списки — заранее выделенные массивы.
- Иконки — `IconGlyphs` / `LegacyIconGlyphs` (уже есть нужные: `MISC`, `CONFIGS`, `BIND`, `SEARCH`, `RENDER`, `COSMETICA`), а не `icons.json`/глиф-шрифт Arcanic.
- Текст — `MenuDraw`/`MenuText` (Inter), не `MsdfText`.
- Цвета — `MenuPalette` (`accent()`, `accent2()`, `text()`, `muted()`, `dim()`, `bg()`, `card()`, `control()`, `border()`), не `Theme`.
- Звук — только `MenuSound` (17 событий) и `skycore:sounds.json`.
- Сохранение — `MenuPrefs` (меню) / `ConfigManager` (модули и позиции), без статики, теряющейся при перезапуске.
- В шапке файла — указание происхождения, как в существующих портах: `Порт <Arcanic/Swerve>.ui.menu.X (GPL-3.0)`.

---

## 7. Риски и ограничения

1. **GPL-3.0**: Arcanic распространяется под GPL-3.0. Порт в SkyCore уже существует и подписан; любые новые переносы должны сохранять указание источника и лицензии.
2. **Версии API**: Arcanic — 1.21.11 (`GuiGraphics`, `screen.render`, `Px`), SkyCore — 26.2 (`GuiGraphicsExtractor`, `extractRenderState`, `RenderGuiState`, `setScreenAndShow`). Прямые копии не компилируются; нужен разбор каждого вызова.
3. **Производительность**: `HashMap`/строковые ключи/`Runnable`-регионы Arcanic в кадре недопустимы; при переносе — переписать на массивы.
4. **Формат конфигов**: добавление настроек HUD-элементов может потребовать новых ключей в `ConfigManager`; менять существующий формат «на живую» нельзя (риск потерять профили пользователей).
5. **Дублирование меню**: любые правки UI нужно делать в двух местах (легаси + порт) либо сознательно оставлять расхождение.
6. **Смешанный язык**: сейчас портированное меню показывает английские имена модулей с русским хромом — это выглядит как баг, а не как решение.

---

## 8. Проверки

- Сборка **не выполнялась**: в окружении нет JDK (`java -version` → `command not found`), нет `~/.gradle`, каталог `SkyCore 26.2/libs` отсутствует, а `gradle.properties` указывает `org.gradle.java.home=C:/Program Files/Eclipse Adoptium`. Даже при наличии JDK сборка упала бы на `implementation files('libs/jsvg-2.1.0.jar', …)`.
- Проверки ограничены статическим чтением кода и сверкой имён/сигнатур (grep/чтение файлов, подсчёт строк и сущностей). Ни один вывод о поведении не делался «по памяти» — все утверждения выше опираются на конкретные файлы и строки.
- Внешние источники: YouGame открыть напрямую из песочницы не удалось (SSL-обрыв на `yougame.biz`); поиск по YouGame выполнялся через поисковый инструмент. По теме нашлись страницы-теги и темы по SkyCore (`yougame.biz/tags/skycore/`, «Skycore assets», «Исходник SkyCore src Ready»), но **без разбора GUI** — только факт публикации jar/src. Гайды по ClickGUI на YouGame (например, «Делаем клиент с нуля 1.21.4 — GuiScreen aka ClickGui») описывают классические панели и к текущей архитектуре SkyCore неприменимы.
- Документация Fabric/Minecraft по 26.2 в этом окружении недоступна (внешний доступ ограничен доменами GitHub/npm/PyPI); API сверялось по фактическому коду клиента.

---

## 9. Источники

- Локально: `SkyCore 26.2/src/main/java/net/skycore/client/**`, `Arcanic/src/main/java/net/arcanic/visuals/**` (перечень файлов и строк — в разделах 2–3).
- YouGame (через поиск, содержимое тем не открывалось):
  - `https://yougame.biz/tags/skycore/` — темы «Skycore assets», «Исходник SkyCore src Ready»;
  - `https://yougame.biz/threads/352979/` — «Делаем клиент с нуля 1.21.4 — GuiScreen aka ClickGui»;
  - `https://yougame.biz/tags/click-gui/`, `https://yougame.biz/tags/clickgui-3/` — обзор визуальных ClickGUI.
- Официально недоступно из песочницы: fabricmc.net, docs.minecraft.net, yougame.biz (прямой доступ).

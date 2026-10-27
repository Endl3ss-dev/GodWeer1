# Новые и перенесённые модули

Дата: 2026-10-27. Задача: `KillSay`, перенос `DrinkUse` и `ItemSwap` из Swerve,
`SkinStealer`, настройки `FakePlayer`.

Все изменения — только в `SkyCore 26.2`. Swerve читался, но не менялся.

---

## 1. Что появилось

| Модуль | Категория | Что делает | Происхождение |
|---|---|---|---|
| `Kill Say` | Misc | пишет в чат заданный текст после убийства игрока | новое |
| `Drink Use` | Player | пьёт абсент / медовуху / грог с голов | порт `Swerve/modules/impl/player/DrinkUse.java` + `Swerve/utils/item/DrinkUtil.java` |
| `Item Swap` | Combat | свап предмета в левую руку по бинду, цикл или удержание | порт `Swerve/modules/impl/combat/ItemSwap.java` |
| `Skin Stealer` | Misc | качает скин по нику, подставляет локально, выдаёт ник в чат для `/skin` | новое (использует `SkinService`) |
| `Fake Player` | Combat | болванка с настройками тотема, щита и копирования инвентаря | новое (поверх `command/FakeplayerCommand`) |

Файлы:

```
src/main/java/net/skycore/client/module/misc/KillSayModule.java
src/main/java/net/skycore/client/module/misc/SkinStealerModule.java
src/main/java/net/skycore/client/module/player/DrinkUseModule.java
src/main/java/net/skycore/client/module/combat/ItemSwapModule.java
src/main/java/net/skycore/client/module/combat/FakePlayerModule.java
src/main/java/net/skycore/client/combat/FakePlayerSettings.java
src/main/java/net/skycore/client/util/DrinkItems.java
src/main/java/net/skycore/client/mixin/AbstractClientPlayerSkinOverrideMixin.java
```

Изменены: `module/ModuleManager.java` (регистрация), `account/SkinService.java`
(`skinIfReady`), `command/FakeplayerCommand.java` (публичный спавн/удаление, тотемы по
режимам, копирование инвентаря, щит), `src/main/resources/skycore.mixins.json`
(новый миксин).

## 2. KillSay

Настройка одна — строка `Сообщение`, как и просили. В тексте работают подстановки:

- `{name}` — ник убитого;
- `{me}` — свой ник.

Убийство определяется по серверному сообщению в чате (те же пакеты, что слушает
`AutoMessage`): клиент не получает события «сущность умерла», а сообщение
«Вы убили …» есть на серверах, где включены такие уведомления. Маркеры: `вы убили`,
`ты убил`, `вы одолели`, `you killed`, `you have killed`, `you slayed`.
Повтор того же ника в пределах 1.5 с игнорируется — сервер иногда дублирует строку.

Пустая строка = ничего не отправлять.

## 3. DrinkUse (порт)

Сохранено из источника: выбор напитка, режим «по бинду» / «по окончанию кд», действие
ПКМ/ЛКМ, бинд, работа в GUI, задержка старта, кулдаун, «пакетный» режим, рука,
уведомления.

Адаптация под 26.2:

- клик по слоту — `gameMode.handleContainerInput(…, ContainerInput.SWAP, …)`
  (в 26.2 `ClickType` заменён на `ContainerInput`, кнопка 40 — левая рука), как в
  `InventoryHelper.swapHand`;
- смена слота — `InventoryHelper.selectHotbarSlot` (в 26.2 нет поля
  `inventory.selected`, есть `getSelectedSlot()/setSelectedSlot()`);
- экран проверяется через `gui.screen()`;
- распознавание головы — свой `util/DrinkItems` вместо `DrinkUtil`: в 26.2 профиль
  читается через `ResolvableProfile.partialProfile()`, `GameProfile.id()` не `Optional`,
  а свойства — `PropertyMap`.

После использования предмет возвращается на место (в «пакетном» режиме — двумя
свапами подряд, в режиме «Рука» — через состояние `USE → RESTORE`).

## 4. ItemSwap (порт)

Сохранено: два предмета (Чарка / Тотем / Щит / Арбалет), режимы «Цикл» и «Удержание»,
биндинг, задержка, поиск по хотбару или всему инвентарю, «пропускать если в руке»,
«не свапать при использовании», уведомление.

Адаптация: слот ищется прямо в `containerMenu.slots` по `slot.index` (9…44), свап —
`ContainerInput.SWAP` с кнопкой 40, уведомление — в чат.

## 5. SkinStealer

Настройки: `Ник цели` (строка), `Применить скин`, `История ников`,
`Уведомление об успехе`, `Уведомление об ошибке`.

Порядок работы:

1. ник уходит в `SkinService.prefetch` — скачивание идёт в фоновом потоке
   (Mojang API + crafatar), в кадре никакой сети;
2. каждый тик модуль проверяет `SkinService.skinIfReady(nick)` (новый метод: старый
   `skin()` возвращал заглушку Стива и не позволял отличить «готово» от «качается»);
3. готовую текстуру модуль превращает в `PlayerSkin` так же, как
   `TextureHashHelper.skinForId` (`PlayerSkin.insecure(new ClientAsset.DownloadedTexture(...),
   null, null, PlayerModelType.WIDE)`);
4. при включённом «Применить скин» этот `PlayerSkin` отдаёт миксин
   `AbstractClientPlayerSkinOverrideMixin` — и только для локального игрока;
5. ник печатается в чат строкой `SkinStealer: /skin <ник>`, чтобы им можно было
   воспользоваться серверной командой. Это и есть основной результат модуля;
6. при включённой истории ник дописывается в `skycore/skin-history.txt`
   (последние 50, без дублей).

Модуль ничего не рисует на экране — только сообщения в чат, как и просили.

Ограничения, которые лучше знать заранее:

- нужен интернет; при таймауте 12 с модуль пишет «ник не найден» (если включено
  уведомление об ошибке);
- модель скина всегда широкая: Slim определяется по подписи профиля, а у скачанного
  PNG её нет;
- миксин подключён с `require = 0`: если в вашей сборке 26.2 `getSkin()` объявлен
  выше по иерархии, подмена просто не включится, но клиент не упадёт — ник в чате
  продолжит работать.

## 6. FakePlayer

Настройки, как в запросе:

| Настройка | Тип | Значение |
|---|---|---|
| Копировать инвентарь | bool | броня, левая и правая рука копируются у вас |
| Использовать тотем | bool | — |
| Режим тотема | режим | `Legit`, `Normal`, `Hard`, `Extra` |
| Кол-во тотемов | число | 0…300, шаг 1, по умолчанию 1 |
| Бесконечные тотемы | bool | счётчик не тратится |
| Использовать щит | bool | — |
| Режим щита | режим | `Legit`, `Normal`, `Hard`, `Extra` |

Что означают режимы (одинаково для тотема и щита — «чем выше, тем труднее убить»):

| Режим | Тотем тратится при здоровье | Щит поднимается |
|---|---|---|
| Legit | ≤ 3 hp | в 4 блоках и только после попадания |
| Normal | ≤ 6 hp | в 6 блоках |
| Hard | ≤ 10 hp | в 12 блоках |
| Extra | ≤ 16 hp | всегда |

Включение модуля создаёт болванку рядом с игроком, выключение — убирает её. Счётчик
тотемов живёт на самой болванке и обнуляется при её пересоздании; «Бесконечные
тотемы» его не уменьшают. Если тотемы выключены или закончились, болванка умирает.

Щит выдаётся в левую руку при спавне (вместо тотема), блокировка — обычное ванильное
использование левой руки, поэтому щит виден и работает как у игрока.

## 7. Проверки

| Проверка | Результат |
|---|---|
| Синтаксис (tree-sitter-java) всех новых и изменённых файлов | 0 ошибок |
| `skycore.mixins.json` разбирается как JSON, новый миксин в списке `client` | да |
| Каждый внешний метод, который вызывают новые модули, встречается в остальном коде SkyCore (`handleContainerInput`, `ContainerInput.SWAP`, `broadcastChanges`, `selectHotbarSlot`, `getItemBySlot`, `broadcastChanges`, `sendChat`, `swing`, `Items.*`, `PlayerSkin.insecure`, `ClientAsset.DownloadedTexture`, `PropertyMap.values`) | да |
| Уникальность имён модулей (регистрация падает на дубликатах) | конфликтов нет |
| Компиляция `./gradlew build` | **не выполнялась**: в среде нет JDK |
| Запуск в игре | не выполнялся |

Отдельно не проверено компилятором (используется в проекте впервые):
`LivingEntity.startUsingItem(InteractionHand)` и `stopUsingItem()` для щита болванки, а
также фактическое применение миксина подмены скина — метод `getSkin()` в 26.2.

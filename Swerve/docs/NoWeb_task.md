# ЗАДАЧА / ПРОМПТ: NoWeb для клиента Swerve (Minecraft 1.21.4, Fabric, Mojang mappings)

> Этот файл — самодостаточное ТЗ. Его можно целиком передать другому исполнителю (человеку или ИИ),
> у которого НЕТ доступа к репозиторию. Ответ исполнителя вставляется в репозиторий **без изменений**,
> поэтому формат ответа в разделе 9 обязателен к соблюдению побайтово.

---

## 1. Роль и окружение

Ты — Senior Java-разработчик клиентских модов Minecraft. Пишешь production-код, который компилируется
с первого раза и не правится руками после тебя.

| Параметр | Значение |
| --- | --- |
| Проект | Swerve (клиентский мод, `id = swerve`, точка входа `swerve.Swerve implements ClientModInitializer`) |
| Minecraft | **1.21.4** |
| Loader | Fabric, `fabric-loader 0.19.3`, `fabric-api 0.119.4+1.21.4`, loom `1.17-SNAPSHOT` |
| Mappings | **Mojang official mappings** (`loom.officialMojangMappings()`). **НЕ Yarn!** |
| Java | 21 (`options.release = 21`), кодировка UTF-8 |
| Mixin | sponge-mixin + **MixinExtras 0.5.4 доступен** (`com.llamalad7.mixinextras.*`) |
| Mod environment | `client` (все миксины регистрируются в списке `"client"` файла `swerve.mixins.json`) |

**Запрещено использовать Yarn-имена.** Ниже — таблица соответствия, которую легко перепутать:

| Yarn (НЕ ИСПОЛЬЗОВАТЬ) | Mojang (ИСПОЛЬЗОВАТЬ) |
| --- | --- |
| `net.minecraft.block.CobwebBlock` | `net.minecraft.world.level.block.WebBlock` |
| `onEntityCollision` | `entityInside` |
| `MinecraftClient` | `net.minecraft.client.Minecraft` |
| `ClientPlayerEntity` | `net.minecraft.client.player.LocalPlayer` |
| `World` | `net.minecraft.world.level.Level` |
| `BlockPos` (Yarn) | `net.minecraft.core.BlockPos` |
| `Box` | `net.minecraft.world.phys.AABB` |
| `Vec3d` | `net.minecraft.world.phys.Vec3` |
| `getVelocity()/setVelocity()` | `getDeltaMovement()/setDeltaMovement()` |
| `MathHelper` | `net.minecraft.util.Mth` |
| `Blocks.COBWEB` (Yarn `Block` тип) | `net.minecraft.world.level.block.Blocks.COBWEB` |
| `options.forwardKey/jumpKey/sneakKey` | `options.keyUp/keyDown/keyLeft/keyRight/keyJump/keyShift/keySprint` |
| `isPressed()` | `isDown()` |
| `player.getYaw()` | `player.getYRot()` |

---

## 2. Цель задачи

Сделать модуль **NoWeb** (категория `MOVEMENT`), при котором паутина **не чувствуется вообще**:
ни по горизонтали (X/Z), ни по вертикали (Y). Сохраняются обычные ускорение, инерция, гравитация
и прыжок. Поведение должно быть максимально «невидимым» для серверных проверок.

Требуется **два режима** (см. раздел 7), потому что «полный обход» и «минимальный риск флага» —
взаимоисключающие свойства (обоснование — раздел 6).

Референс для заимствования логики: `AlphaDLC/src/main/java/zov/alphadlc/module/list/movement/NoWeb.java`
(Yarn mappings) + `AlphaDLC/src/main/java/zov/alphadlc/mixin/CobWebMixin.java`. Исходник приведён в разделе 5
вместе со списком его дефектов. **Копировать вслепую нельзя** — нужна переработка под Swerve и под
физику 1.21.4, описанную в разделе 4.

---

## 3. Верифицированный API (проверен разбором class-файлов minecraft-merged 1.21.4)

Использовать ТОЛЬКО эти сигнатуры. Всё ниже реально существует в 1.21.4 (Mojang mappings):

```
net.minecraft.world.level.block.WebBlock extends Block
    protected void entityInside(BlockState, Level, BlockPos, Entity)

net.minecraft.world.entity.Entity (abstract)
    protected Vec3 stuckSpeedMultiplier            // поле, читается/пишется только внутри Entity
    public    void makeStuckInBlock(BlockState, Vec3)
    public    void resetFallDistance()
    public    float fallDistance                   // поле
    public    Vec3 getDeltaMovement()
    public    void setDeltaMovement(Vec3)
    public    void setDeltaMovement(double, double, double)
    public    void addDeltaMovement(Vec3)
    public    void move(MoverType, Vec3)
    public    boolean onGround()
    public    boolean isInWater()
    public    boolean isPassenger()
    public    Entity getVehicle()
    public    boolean isSprinting()
    public    boolean horizontalCollision          // поле
    public    boolean verticalCollision            // поле
    public    BlockPos blockPosition()
    public    final AABB getBoundingBox()
    public    void applyEffectsFromBlocks()
    public    void applyEffectsFromBlocks(Vec3, Vec3)

net.minecraft.world.entity.LivingEntity extends Entity
    public    boolean isFallFlying()
    public    boolean hasEffect(Holder<MobEffect>)
    public    void travel(Vec3)
    public    void aiStep()

net.minecraft.world.entity.player.Player extends LivingEntity
    public    Abilities getAbilities()
    public    void makeStuckInBlock(BlockState, Vec3)   // override

net.minecraft.world.entity.player.Abilities
    public    boolean flying                        // поле
    public    boolean mayfly                        // поле

net.minecraft.world.effect.MobEffects
    public static final Holder<MobEffect> WEAVING

net.minecraft.world.level.block.Blocks
    public static final Block COBWEB

net.minecraft.world.level.block.state.BlockState (через BlockBehaviour.BlockStateBase)
    public    boolean is(Block)

net.minecraft.world.level.Level
    public    boolean isClientSide                  // поле и метод isClientSide()
    public    BlockState getBlockState(BlockPos)

net.minecraft.world.phys.Vec3
    public static final Vec3 ZERO
    public    Vec3 multiply(Vec3)
    public    Vec3 multiply(double, double, double)
    public    double lengthSqr()
    public    double x, y, z                        // поля

net.minecraft.util.Mth
    public static float clamp(float, float, float)
    public static double clamp(double, double, double)
    public static int floor(double)

net.minecraft.client.Minecraft
    public static Minecraft getInstance()
    public    LocalPlayer player                    // поле
    public    ClientLevel level                     // поле
    public    Options options                       // поле

net.minecraft.client.Options
    public final KeyMapping keyUp, keyDown, keyLeft, keyRight, keyJump, keyShift, keySprint

net.minecraft.client.KeyMapping
    public    boolean isDown()

net.minecraft.client.player.ClientInput
    public    Input keyPresses                      // поле
    public    float leftImpulse                     // поле
    public    float forwardImpulse                  // поле
    public    boolean hasForwardImpulse()

net.minecraft.world.entity.player.Input (record)
    public    boolean forward(), backward(), left(), right(), jump(), shift(), sprint()

net.minecraft.client.player.LocalPlayer extends AbstractClientPlayer
    public final ClientPacketListener connection    // поле
```

**Swerve API (существует в репозитории, использовать как есть):**

```java
// swerve.modules.Module (abstract)
public Module(String name, String description, ModuleCategory category)
public Module(String name, ModuleCategory category)
public void onEnable()            // базовая реализация: this.enable = true  → override обязан вызвать super
public void onDisable()           // базовая реализация: this.enable = false → override обязан вызвать super
public void onTick()              // пустой
public void addSettings(Setting... settings)
public boolean isEnable()
public String getName()

// swerve.modules.ModuleCategory
COMBAT, MOVEMENT, RENDER, MISC, PLAYER, INTERFACE      // категории HUD НЕТ

// swerve.modules.api.setting.BooleanSetting
public BooleanSetting(String name, boolean state)
public boolean get() / isState(); public void setState(boolean); public BooleanSetting visible(Supplier<Boolean>)

// swerve.modules.api.setting.FloatSetting
public FloatSetting(String name, float value, float min, float max, float increment)
public float get(); public void setValue(float); public FloatSetting visible(Supplier<Boolean>)

// swerve.modules.api.setting.ModeSetting
public ModeSetting(String name, String current, String... modes)
public boolean is(String mode); public String getCurrent(); public void set(String); public List<String> getMods()
public ModeSetting visible(Supplier<Boolean>)

// swerve.managers.ModuleManager
public static <T extends Module> T get(Class<T> type)   // null, если модуль не зарегистрирован
public static Module getByName(String name)
public static void register(Module module)

// swerve.Swerve
public static final org.slf4j.Logger LOGGER             // Swerve.LOGGER.error(String, Throwable)
```

**Конвенция миксинов Swerve (обязательна):**

```java
package swerve.mixins.<подпакет>;

@Mixin(TargetClass.class)
public abstract class MixinTargetClass {

    @Unique
    private boolean swerve$someState;                 // все свои поля/методы — с префиксом swerve$

    @Inject(method = "targetMethod", at = @At("HEAD"))
    private void swerve$targetMethod(CallbackInfo ci) {   // имена обработчиков — с префиксом swerve$
        SomeModule module = ModuleManager.get(SomeModule.class);
        if (module == null || !module.isEnable()) return;
        try {
            module.someHook();
        } catch (Exception exception) {
            Swerve.LOGGER.error("Module 'SomeModule' failed during ...", exception);
            module.setEnabled(false);
        }
    }
}
```

Стиль кода Swerve: **4 пробела** в `.java`, **табы** в `.json`; `public final class X extends Module`;
имена настроек — **на русском**; короткие комментарии на русском, объясняющие «почему», а не «что».
Порядок импортов: `swerve.*` → `net.fabricmc.*` → `net.minecraft.*` → `org.spongepowered.*` → `java.*`
(группы разделяются пустой строкой; внутри файла встречается и алфавитный порядок `net.minecraft` первым —
допустим любой из двух вариантов, но единый внутри файла).

---

## 4. Физика паутины в 1.21.4 (получена разбором байткода, не по памяти)

### 4.1 `WebBlock.entityInside` — дословная логика (44 байта байткода)

```java
@Override
protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    Vec3 multiplier = new Vec3(0.25D, 0.05000000074505806D, 0.25D);
    if (entity instanceof LivingEntity living && living.hasEffect(MobEffects.WEAVING)) {
        multiplier = new Vec3(0.5D, 0.25D, 0.5D);
    }
    entity.makeStuckInBlock(state, multiplier);
}
```

Константы взяты из пула констант класса: `0.25`, `0.05000000074505806`, `0.5`. Условие —
`instanceof LivingEntity` + `LivingEntity.hasEffect(MobEffects.WEAVING)`.

### 4.2 `Entity.makeStuckInBlock` — дословная логика (10 байт)

```java
public void makeStuckInBlock(BlockState state, Vec3 movementMultiplier) {
    this.resetFallDistance();                       // ← ВАЖНО: сброс урона от падения
    this.stuckSpeedMultiplier = movementMultiplier; // ← множитель для СЛЕДУЮЩЕГО move()
}
```

### 4.3 `Player.makeStuckInBlock` — override (21 байт)

```java
@Override
public void makeStuckInBlock(BlockState state, Vec3 movementMultiplier) {
    if (!this.getAbilities().flying) {              // в байткоде: getfield abilities → getfield flying
        super.makeStuckInBlock(state, movementMultiplier);
    }
    this.tryResetCurrentImpulseContext();
}
```

Следствие: **в креатив-полёте паутина игрока не замедляет вообще** — `stuckSpeedMultiplier` не устанавливается.

### 4.4 `Entity.move(MoverType, Vec3)` — начало метода (байты 55…135)

```java
public void move(MoverType type, Vec3 movement) {
    if (this.noPhysics) { this.setPos(...); return; }
    if (type == MoverType.PISTON) movement = this.limitPistonMovement(movement);
    if (movement.equals(Vec3.ZERO)) return;                       // ← ранний выход
    ProfilerFiller profiler = Profiler.get();
    profiler.push("move");
    if (this.stuckSpeedMultiplier.lengthSqr() > 1.0E-7D) {        // ← потребление множителя
        movement = movement.multiply(this.stuckSpeedMultiplier);  //    (0.25, 0.05, 0.25)
        this.stuckSpeedMultiplier = Vec3.ZERO;                    //    сброс
        this.setDeltaMovement(Vec3.ZERO);                         // ← СКОРОСТЬ ОБНУЛЯЕТСЯ, а не умножается!
    }
    movement = this.maybeBackOffFromEdge(movement, type);
    Vec3 collided = this.collide(movement);
    ... // setPos, onGround, fallDamage, step sounds
}
```

**Ключевой факт:** множитель применяется к аргументу `movement` (позиционное перемещение за тик),
а `deltaMovement` при этом **обнуляется**. То есть в паутине у игрока каждый тик стирается инерция.

### 4.5 Порядок в тике (кто кого вызывает — проверено по ссылкам в байткоде)

```
LocalPlayer.tick()
 └─ LivingEntity.tick()
     ├─ Entity.baseTick()
     └─ LivingEntity.aiStep()
         ├─ ... travel(input)  →  Entity.move(MoverType.SELF, delta)
         │        └─ ЗДЕСЬ потребляется stuckSpeedMultiplier, установленный в ПРОШЛОМ тике
         └─ в конце aiStep(): Entity.applyEffectsFromBlocks(Vec3, Vec3)
              └─ Entity.checkInsideBlocks(List, Set)
                   └─ BlockState.entityInside(Level, BlockPos, Entity)
                        └─ WebBlock.entityInside(...)  →  Player.makeStuckInBlock(...)
                             └─ ЗДЕСЬ устанавливается stuckSpeedMultiplier для СЛЕДУЮЩЕГО тика
```

Проверено: `Entity.checkInsideBlocks` вызывается из `Entity.applyEffectsFromBlocks`,
а `applyEffectsFromBlocks` — из `LivingEntity.aiStep`. Поле `stuckSpeedMultiplier`
читается/пишется только в `Entity.<init>`, `Entity.move`, `Entity.makeStuckInBlock`.

### 4.6 Практические выводы (обязательно учесть в реализации)

1. Обработчик на `entityInside` выполняется **после** `travel()`/`move()` текущего тика.
   Всё, что мы там пишем в `deltaMovement`, будет использовано в `travel()` **следующего** тика,
   а затем умножено на множитель и обнулено в `move()`.
2. Поэтому «магические» константы AlphaDLC — это значения **ДО** умножения:
   `0.2 × 0.25 = 0.05` б/т по горизонтали, `1.219 × 0.05 ≈ 0.061` б/т вверх, `−3.5 × 0.05 = −0.175` б/т вниз.
3. Точная компенсация без подбора: чтобы фактическая (ощущаемая) скорость была `T`,
   задать надо `delta = T / multiplier` (по каждой оси отдельно, множитель брать из факта 4.1:
   `0.25/0.05` обычно, `0.5/0.25` при `MobEffects.WEAVING`).
4. Любая **отмена** `entityInside`/`makeStuckInBlock` отменяет и `resetFallDistance()` (факт 4.2) —
   игрок начнёт получать урон от падения после пролёта сквозь паутину. Это надо компенсировать вручную.
5. В креатив-полёте (факт 4.3) множитель не устанавливается → компенсация `T/multiplier` даст
   рывок. Такой случай обязан быть отфильтрован.

---

## 5. Референс: NoWeb из AlphaDLC и его дефекты

### 5.1 Исходник (Yarn mappings, только для чтения)

`AlphaDLC/src/main/java/zov/alphadlc/module/list/movement/NoWeb.java`:

```java
@ModuleInformation(moduleName = "No Web", moduleDesc = "Отменяет замедление в паутине", moduleCategory = ModuleCategory.MOVEMENT)
public class NoWeb extends Module {

    @Subscribe
    private void onCobweb(EventCobweb e) {
        Vec3d velocity = mc.player.getVelocity();

        float yaw = mc.player.getYaw();
        double forward = 0;
        double strafe = 0;

        if (mc.player.input.playerInput.forward()) forward += 1;
        if (mc.player.input.playerInput.backward()) forward -= 1;

        if (mc.player.input.playerInput.left()) strafe += 1;
        if (mc.player.input.playerInput.right()) strafe -= 1;

        if (!(forward == 0 && strafe == 0)) {

            if (forward != 0) {
                if (strafe > 0) yaw += (forward > 0 ? -45 : 45);
                else if (strafe < 0) yaw += (forward > 0 ? 45 : -45);

                strafe = 0;

                if (forward > 0) forward = 1;
                else forward = -1;
            }

            double movementYaw = Math.toDegrees(
                    Math.atan2(strafe, forward)
            ) + yaw;

            yaw = (float) ((movementYaw % 360 + 360) % 360);
        }

        float result = 0.2f;

        if ((yaw >= 313 && yaw <= 317) || (yaw >= 223 && yaw <= 227) || (yaw >= 133 && yaw <= 137) || (yaw >= 43 && yaw <= 47)) {
            result = 0.295f;
        } else if ((yaw >= 311 && yaw <= 319) || (yaw >= 221 && yaw <= 229) || (yaw >= 131 && yaw <= 139) || (yaw >= 41 && yaw <= 49)) {
            result = 0.292f;
        } else if ((yaw >= 310.8f && yaw <= 320.8f) || (yaw >= 220.8f && yaw <= 230.8f) || (yaw >= 130.8f && yaw <= 140.8f) || (yaw >= 38.7f && yaw <= 52.7f)) {
            result = 0.284f;
        } else if ((yaw >= 308.7f && yaw <= 322.7f) || (yaw >= 218.7f && yaw <= 232.7f) || (yaw >= 128.7f && yaw <= 142.7f) || (yaw >= 38.7f && yaw <= 52.7f)) {
            result = 0.275f;
        } else if ((yaw >= 306.5f && yaw <= 324.5f) || (yaw >= 216.5f && yaw <= 234.5f) || (yaw >= 126.5f && yaw <= 144.5f) || (yaw >= 36.5f && yaw <= 54.5f)) {
            result = 0.268f;
        } else if ((yaw >= 304f && yaw <= 327f) || (yaw >= 214f && yaw <= 237f) || (yaw >= 124f && yaw <= 147f) || (yaw >= 34f && yaw <= 57f)) {
            result = 0.255f;
        }

        if (mc.options.jumpKey.isPressed()) {
            mc.player.setVelocity(velocity.x, 1.219f, velocity.z);
        } else if (mc.options.sneakKey.isPressed()) {
            mc.player.setVelocity(velocity.x, mc.player.isGliding() ? -1.5f : -3.5f, velocity.z);
        } else {
            mc.player.setVelocity(velocity.x, 0, velocity.z);
        }

        MoveUtil.setMotion(result);
    }
}
```

`AlphaDLC/src/main/java/zov/alphadlc/mixin/CobWebMixin.java`:

```java
@Mixin(CobwebBlock.class)
public class CobWebMixin {

    @Inject(method = "onEntityCollision", at = @At("RETURN"))
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity != MinecraftClient.getInstance().player) return;
        var event = new EventCobweb();
        event.post();
    }
}
```

Семантика `MoveUtil.setMotion(double)` из AlphaDLC: задать горизонтальную составляющую `deltaMovement`
равной `velocity` в направлении ввода (по yaw), сохранив текущий Y.

### 5.2 Дефекты референса, которые ОБЯЗАТЕЛЬНО исправить

1. **Магическая таблица yaw-диапазонов** (0.2 / 0.295 / 0.292 / 0.284 / 0.275 / 0.268 / 0.255) — эмпирика
   вместо математики. Не работает при частичном импульсе (`forwardImpulse < 1`), при спринте, при движении
   «в пол-оборота» мышью между диапазонами, и даёт разную скорость на диагоналях. Заменить точным расчётом
   по факту 4.6.3 с нормализацией вектора ввода (диагональ не должна быть быстрее прямой).
2. **Вертикаль «в лоб»**: `setVelocity(x, 0, z)` без клавиш полностью гасит гравитацию и инерцию —
   игрок «висит» в паутине, ощущение ваты. `1.219` и `-3.5` — необъяснимые константы (см. 4.6.2).
3. **Креатив-полёт не отфильтрован**: событие постится на RETURN `onEntityCollision` независимо от того,
   применила ли ванила множитель (факт 4.3). При `abilities.flying` модуль пишет скорость, которую ванила
   не уменьшит → рывок/флаг.
4. **`MobEffects.WEAVING` не учтён**: множитель ванилы `(0.5, 0.25, 0.5)`, а компенсация рассчитана
   на `(0.25, 0.05, 0.25)` → ошибка в 2 раза по X и в 5 раз по Y.
5. **`resetFallDistance()` теряется** при любом варианте отмены коллизии (факт 4.2, 4.6.4) → урон от падения.
6. **Нет null-guard'ов**: `mc.player.getVelocity()` в обработчике без проверки `mc.player == null`
   → краш при выходе из мира / смене измерения / переподключении.
7. **Нет изоляции исключений**: падение внутри обработчика ломает тик игрока. В Swerve обязательно
   `try/catch` + `Swerve.LOGGER.error` + `setEnabled(false)` (см. конвенцию в разделе 3).
8. **`isGliding()` ветка** (`-1.5f`) недостижима по смыслу: при полёте на элитрах `travel` идёт через
   `travelFallFlying`, и запись Y туда не нужна. Поведение на элитрах должно быть явно определено (раздел 7.4).

---

## 6. Серверная сторона и античиты (проверено по байткоду 1.21.4, честно)

`ServerGamePacketListenerImpl.handleMovePlayer` (1359 байт) делает следующее:

- читает `ServerboundMovePlayerPacket.getX/getY/getZ/getYRot/getXRot/isOnGround/horizontalCollision`;
- при `d² > 100.0` (≈ 10 блоков/тик) и не-владельце одиночного мира → лог `"{} moved too quickly! {},{},{}"`
  и **телепорт назад** (`teleport(...)`), т.е. rubberband;
- **пересимулирует** движение серверным игроком: `ServerPlayer.move(MoverType.PLAYER, delta)` —
  значит серверный `Entity.move()` тоже потребляет свой `stuckSpeedMultiplier`, установленный серверной
  `WebBlock.entityInside`;
- сравнивает результат с позицией клиента, допуски `0.0625` (лог `"{} moved wrongly!"`) и `-0.03125` / `-0.5`;
  от лога освобождают `isChangingDimension()`, `isSleeping()`, `gameMode.isCreative()`, `GameType.SPECTATOR`;
- позиция клиента всё равно принимается через `ServerPlayer.absMoveTo(...)`;
- rubberband по коллизии: `ServerLevel.noCollision(player, bb)` + `isPlayerCollidingWithAnythingNew(...)`
  → `teleport(...)`;
- если клиент сообщил `onGround`, а серверный игрок нет → сервер сам вызывает `ServerPlayer.jumpFromGround()`.

**Честный вывод, который надо отразить в `docs/NoWeb.md`:**

- На ванильном сервере полный обход паутины даёт предупреждения `moved wrongly!` в консоли,
  но **не** rubberband (позиция принимается), пока движение не превышает 10 блоков/тик и не ведёт внутрь блока.
- Предиктивные античиты (Grim-класс) симулируют ту же физику с множителем `(0.25, 0.05, 0.25)`.
  Любой полный обход для них — рассинхрон предсказания → флаг. «Полный обход без единого флага
  на предиктивном античите» физически недостижим. Поэтому режим «Компенсация» должен позволять
  подобрать фактическую скорость под порог конкретного сервера (настройки в б/т, а не «магия»).
- Проверить поведение сторонних античит-плагинов в этой среде невозможно — это надо явно написать
  в разделе NOTES ответа, а не обещать «без флагов».

---

## 7. Требования к реализации

### 7.1 Файлы

| № | Действие | Путь |
| --- | --- | --- |
| 1 | `[+]` NEW | `Swerve/src/main/java/swerve/modules/impl/movement/NoWeb.java` |
| 2 | `[+]` NEW | `Swerve/src/main/java/swerve/mixins/world/MixinWebBlock.java` |
| 3 | `[+]` NEW | `Swerve/docs/NoWeb.md` |
| 4 | `[~]` EDIT | `Swerve/src/main/resources/swerve.mixins.json` |
| 5 | `[~]` EDIT | `Swerve/src/main/java/swerve/managers/ModuleManager.java` |

Новый пакет миксинов — `swerve.mixins.world` (в `swerve.mixins.json` уже есть `accessor`, `client`, `render`).
Миксин один: `MixinWebBlock` на `net.minecraft.world.level.block.WebBlock`.

### 7.2 Модуль

```java
public final class NoWeb extends Module
    super("NoWeb", "<описание на русском>", ModuleCategory.MOVEMENT);
```

- Имя модуля — `NoWeb` (в Swerve имена без пробелов: `Speed`, `KillAura`, `ElytraFlight`).
- Комментарий атрибуции над классом обязателен:
  `// Adapted from AlphaDLC/src/main/java/zov/alphadlc/module/list/movement/NoWeb.java`
- Настройки (имена — на русском), минимум:
  - `ModeSetting("Режим", "Обход", "Обход", "Компенсация")`;
  - для «Компенсации»: `FloatSetting("Скорость XZ", 0.05F, 0.0F, 0.30F, 0.005F)` — **фактическая**
    скорость после ванильного умножения, б/т; `FloatSetting("Скорость вверх Y", 0.06F, 0.0F, 0.20F, 0.005F)`;
    `FloatSetting("Скорость вниз Y", 0.175F, 0.0F, 0.40F, 0.005F)`;
    `BooleanSetting("Только с вводом", true)` — без нажатых клавиш движения не добавляем;
    все три слайдера — с `visible(() -> mode.is("Компенсация"))`;
  - `BooleanSetting("Гасить урон падения", true)` — вызывать `resetFallDistance()`, компенсируя факт 4.6.4;
  - `BooleanSetting("Не трогать в транспорте", true)`;
  - `BooleanSetting("Не трогать на элитрах", true)`.
- Модуль НЕ хранит состояние между мирами; всё, что накопилось, сбрасывается в `onDisable()`
  (с вызовом `super.onDisable()`).
- Логика должна жить в модуле, а не в миксине: миксин лишь проверяет условия и вызывает публичный
  метод модуля (например `onCobwebCollision(Entity entity, BlockState state)`), получая модуль через
  `ModuleManager.get(NoWeb.class)` и проверяя `isEnable()`.

### 7.3 Режим «Обход» (основной, «паутина не чувствуется»)

- Отмена `WebBlock.entityInside` на `@At("HEAD")`, `cancellable = true`, **только** если
  `entity == Minecraft.getInstance().player` и модуль включён, и режим = «Обход».
- Перед отменой — `entity.resetFallDistance()`, если настройка «Гасить урон падения» включена
  (иначе игрок получает урон от падения, факт 4.6.4).
- Результат: `stuckSpeedMultiplier` не устанавливается → `Entity.move()` не умножает движение и
  **не обнуляет** `deltaMovement` → полностью сохраняются инерция, гравитация, прыжок, спринт.
  Паутина перестаёт существовать физически, оставаясь видимой и ломаясь как обычно.
- Ничего не писать в `deltaMovement` в этом режиме.

### 7.4 Режим «Компенсация» (пониженный профиль)

- Ванила отрабатывает полностью (множитель устанавливается), затем на `@At("RETURN")`/`@At("TAIL")`
  того же `entityInside` модуль пересчитывает `deltaMovement` так, чтобы **фактическая** скорость
  следующего тика равнялась значениям настроек:
  - определить фактический множитель так же, как ванила (факт 4.1):
    `double mx = weaving ? 0.5D : 0.25D; double my = weaving ? 0.25D : 0.05D;`
    где `weaving = entity instanceof LivingEntity living && living.hasEffect(MobEffects.WEAVING)`;
  - `delta = target / multiplier` по каждой оси;
  - направление — по вектору ввода (`player.input.forwardImpulse/leftImpulse`, они `float` и могут быть
    частичными) или по `options.keyUp/keyDown/keyLeft/keyRight.isDown()`, **с нормализацией**:
    диагональ не должна превышать скорость прямой;
  - вертикаль: `keyJump.isDown()` → `+«Скорость вверх Y»`, иначе `keyShift.isDown()` → `−«Скорость вниз Y»`,
    иначе `0`;
  - записывать через `setDeltaMovement(double, double, double)` (без аллокации `Vec3`).
- Режим обязан ничего не делать при `player.getAbilities().flying` (факт 4.3) и при `isFallFlying()`.
- Ограничить итоговую скорость здравым пределом (не более ~10 блоков/тик, см. раздел 6: порог
  `moved too quickly`), чтобы не словить ванильный rubberband.

### 7.5 Общие условия (оба режима)

Не трогать движение, если выполняется любое:

- `Minecraft.getInstance().player == null` или `.level == null`;
- `entity` не является локальным игроком (сравнение по ссылке, `==`);
- `entity.isPassenger()` или `entity.getVehicle() != null` — при включённой настройке «Не трогать в транспорте»;
- `living.isFallFlying()` — при включённой настройке «Не трогать на элитрах»;
- модуль выключен.

### 7.6 Hot path

`entityInside` вызывается каждый тик, пока игрок в паутине:

- ноль `new` (кроме неизбежного в самом миксине `CallbackInfo`), ноль стримов, лямбд, итераторов, боксинга;
- не искать блоки через `BlockPos.betweenClosed` / `AABB` — мы уже внутри коллизии паутины;
- `ModuleManager.get(NoWeb.class)` — линейный поиск по ~20 модулям, это допустимо и соответствует
  существующим миксинам Swerve (кэшировать ссылку нельзя: `ModuleManager.init()` пересоздаёт список).

### 7.7 Взаимодействие с другими модулями Swerve (описать в `docs/NoWeb.md`)

- `Speed` (`modules/impl/movement/Speed.java`) пишет `deltaMovement` в `onTick()`, который вызывается из
  `ClientTickEvents.END_CLIENT_TICK` — то есть **после** тика игрока. В режиме «Компенсация» Speed
  перезатрёт нашу вертикаль/горизонталь; в режиме «Обход» конфликта нет (мы ничего не пишем).
  Ломать или отключать `Speed` запрещено — достаточно честно описать порядок в доке.
- `ElytraFlight` работает только при `isFallFlying()` → наши guard'ы не должны ему мешать.
- `KillAura`/`SexAura` управляют поворотами, не скоростью → пересечений нет.

---

## 8. Документация `Swerve/docs/NoWeb.md`

Стиль — как у существующего `Swerve/docs/ElytraFlight.md`. Обязательные разделы:

1. Заголовок и путь к основному файлу, категория, где зарегистрирован.
2. «Поведение» — по пунктам, что делает каждый режим, со значениями настроек по умолчанию.
3. «Физика 1.21.4» — кратко факты 4.1…4.6 (множитель, обнуление `deltaMovement` в `move()`,
   порядок `aiStep → move → applyEffectsFromBlocks → entityInside`, `resetFallDistance`, `WEAVING`,
   креатив-полёт).
4. «Адаптация из AlphaDLC» — таблица `AlphaDLC → Swerve` (Yarn-имена → Mojang-имена,
   `EventCobweb`/Guava EventBus → вызов из миксина, `MoveUtil.setMotion` → точный расчёт,
   магическая таблица yaw → нормализованный вектор ввода).
5. «Исправленные дефекты референса» — список из раздела 5.2 с указанием, как исправлено.
6. «Сервер и античиты» — честный вывод из раздела 6, включая недостижимость «полного обхода без флагов»
   на предиктивных античитах.
7. «Проверки» — что проверено (сигнатуры и точки внедрения по байткоду `minecraft-merged 1.21.4`),
   и что НЕ проверено (полная Gradle/Loom-сборка, запуск игры, поведение сторонних античит-плагинов).
8. «Перед выпуском» — чек-лист ручной проверки в игре: вход/выход из паутины на ходу, спринт, диагонали,
   прыжок в паутине, падение сквозь паутину (урон от падения), sneak в паутине, паутина в воде,
   креатив-полёт, элитры, транспорт, эффект `WEAVING`, смена измерения/перезаход, выключение модуля
   внутри паутины, совместная работа со `Speed`.

---

## 9. Формат ответа (обязателен, ответ вставляется в репозиторий без изменений)

Ответ состоит только из блоков ниже, без вступлений и рассуждений между ними.

```
=== FILE: Swerve/src/main/java/swerve/modules/impl/movement/NoWeb.java ===
<полное содержимое файла, от package до последнего }, без обрезок>

=== FILE: Swerve/src/main/java/swerve/mixins/world/MixinWebBlock.java ===
<полное содержимое файла>

=== FILE: Swerve/docs/NoWeb.md ===
<полное содержимое файла>

=== EDIT: Swerve/src/main/resources/swerve.mixins.json ===
--- FIND ---
		"render.MixinRenderTarget"
	],
--- REPLACE ---
		"render.MixinRenderTarget",
		"world.MixinWebBlock"
	],

=== EDIT: Swerve/src/main/java/swerve/managers/ModuleManager.java (1/2) ===
--- FIND ---
import swerve.modules.impl.movement.ElytraFlight;
--- REPLACE ---
import swerve.modules.impl.movement.ElytraFlight;
import swerve.modules.impl.movement.NoWeb;

=== EDIT: Swerve/src/main/java/swerve/managers/ModuleManager.java (2/2) ===
--- FIND ---
        register(new ElytraFlight());
--- REPLACE ---
        register(new ElytraFlight());
        register(new NoWeb());

=== NOTES ===
<риски, непокрытые случаи, всё, в чём нет уверенности>
```

Правила формата:

1. Новые файлы — **полным содержимым**, со всеми импортами. Никаких `// ...`, `/* остальной код */`,
   `TODO`, заглушек и псевдокода.
2. `FIND`-блоки двух правок приведены выше **побайтово** (в `swerve.mixins.json` отступы — ТАБЫ,
   в `ModuleManager.java` — 4 пробела). Их менять нельзя: они должны совпасть с текущим содержимым файлов.
3. Каждый блок `EDIT` — ровно одна замена в одном месте.
4. В `NOTES` обязательно указать: что не проверено, какие кейсы не покрыты, какие допущения сделаны.
   Если нужной сигнатуры нет в разделе 3 — **не выдумывать её**, а написать в `NOTES` «НЕ ПРОВЕРЕНО»
   и построить код только на проверенном API.

---

## 10. Чек-лист самопроверки перед сдачей (пройти целиком)

1. Все классы/методы/поля взяты из раздела 3; Yarn-имена не встречаются ни разу.
2. Точка внедрения — `entityInside` в `net.minecraft.world.level.block.WebBlock`, дескриптор
   `(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)V`,
   `protected`. Обработчик миксина — instance-метод с теми же параметрами + `CallbackInfo`.
3. «Обход»: `cancellable = true`, `ci.cancel()`, `resetFallDistance()` до отмены, ничего не пишется в delta.
4. «Компенсация»: множитель вычисляется по правилу ванилы (включая `WEAVING`), `delta = target / multiplier`,
   направление нормализовано, `setDeltaMovement(double, double, double)`, креатив-полёт и элитры отфильтрованы,
   итоговая скорость ограничена сверху.
5. Все guard'ы из раздела 7.5 на месте; `try/catch` + `Swerve.LOGGER.error` + `setEnabled(false)` в миксине.
6. Hot path: ноль аллокаций, стримов, лямбд, итераторов, боксинга.
7. Настройки — русские имена, `visible(...)` привязан к режиму, значения по умолчанию из раздела 7.2.
8. `ModuleManager` и `swerve.mixins.json` правятся ровно теми `FIND/REPLACE`, что заданы.
9. `docs/NoWeb.md` содержит все 8 разделов из пункта 8, включая честный вывод про античиты.
10. Код компилируется мысленно построчно: каждая скобка закрыта, каждый импорт использован,
    каждый использованный тип импортирован, `@Override` только там, где реально переопределение.

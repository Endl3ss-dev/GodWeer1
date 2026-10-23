package swerve.modules.impl.render;

import swerve.graffiti.GraffitiDesign;
import swerve.graffiti.GraffitiRenderer;
import swerve.graffiti.GraffitiSpot;
import swerve.graffiti.GraffitiStore;
import swerve.graffiti.GraffitiWorld;
import swerve.managers.ModuleManager;
import swerve.managers.ThemeManager;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BindSetting;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.utils.client.NotificationManager;
import swerve.utils.input.KeyBoardUtils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * [+] Graffiti — рисование граффити 1x1 на гранях блоков.
 *
 * <h2>Что делает</h2>
 * <ul>
 *   <li>по приседу + ПКМ (или по бинду) наносит выбранный рисунок на грань блока,
 *       в которую смотрит прицел;</li>
 *   <li>рисунки хранятся в {@code config/swerve/graffiti.json} и привязаны
 *       к миру и измерению — на другом сервере или в одиночке они не всплывают;</li>
 *   <li>повторное нанесение на ту же грань перерисовывает её (новый рисунок/поворот);</li>
 *   <li>блок сломали — рисунок снимается сам (см. {@link GraffitiStore#tick});</li>
 *   <li>биндинг «Стереть» убирает рисунок с грани под прицелом.</li>
 * </ul>
 *
 * <h2>Почему это только своя видимость</h2>
 * Граффити рисуются в клиенте и в сеть не уходят: сервер о них не знает, другие
 * игроки их не видят. Так же устроены Trails и ChinaHat — отправка требовала бы
 * своего канала и серверного мода, а в ванильном протоколе места для этого нет.
 * В обмен на это клиент не отправляет ни одного пакета и не зависит от прав
 * на сервере.
 *
 * <h2>Активация и отмена ванильного действия</h2>
 * ПКМ в ванилле — это «использовать предмет»: поставить блок, съесть еду, дёрнуть
 * рычаг. Рисование перехватывается в {@code Minecraft.startUseItem} (см.
 * {@code MixinMinecraft.swerve$graffitiUse}) и только когда рисунок действительно
 * нанесён: нажатие отменяется, и блок не ставится. Если грань не подходит
 * (далеко, закрыта, лимит), ПКМ проходит в ваниллу как обычно.
 *
 * <p>Нажатие считается «свежим» только в тике, где ПКМ была отпущена и нажата
 * снова: иначе удержание кнопки рисовало бы по грани каждый тик.
 */
public final class Graffiti extends Module {

    private static final String MODE_SNEAK = "Присед + ПКМ";
    private static final String MODE_USE = "ПКМ";
    private static final String MODE_BIND = "Бинд";

    /** Минимальный интервал между нанесениями, мс. */
    private static final long PLACE_COOLDOWN_MS = 180L;

    private static boolean eventsRegistered;

    private final ModeSetting designMode = new ModeSetting("Рисунок", GraffitiDesign.TAG.label(),
            designLabels());
    private final ModeSetting activation = new ModeSetting("Активация", MODE_SNEAK, MODE_SNEAK, MODE_USE, MODE_BIND);
    private final BindSetting drawBind = new BindSetting("Бинд рисования", -1)
            .visible(() -> activation.is(MODE_BIND));
    private final BindSetting eraseBind = new BindSetting("Бинд стирания", -1);
    private final FloatSetting spin = new FloatSetting("Поворот", 0.0F, 0.0F, 345.0F, 15.0F);
    private final FloatSetting distance = new FloatSetting("Дистанция", 4.5F, 1.0F, 8.0F, 0.5F);
    private final FloatSetting opacity = new FloatSetting("Прозрачность", 0.9F, 0.1F, 1.0F, 0.05F);
    private final BooleanSetting lighting = new BooleanSetting("Освещение", true);
    private final BooleanSetting highlight = new BooleanSetting("Подсветка грани", true);

    private boolean useWasDown;
    private boolean useFresh;
    private boolean drawBindWasDown;
    private long lastPlaceMs;

    public Graffiti() {
        super("Graffiti", "Граффити 1x1 на гранях блоков", ModuleCategory.RENDER);
        addSettings(designMode, activation, drawBind, eraseBind, spin, distance, opacity, lighting, highlight);
        registerEvents();
    }

    private static String[] designLabels() {
        GraffitiDesign[] designs = GraffitiDesign.values();
        String[] labels = new String[designs.length];
        for (int i = 0; i < designs.length; i++) {
            labels[i] = designs[i].label();
        }
        return labels;
    }

    private static void registerEvents() {
        if (eventsRegistered) {
            return;
        }
        eventsRegistered = true;

        // START_CLIENT_TICK идёт до обработки клавиш в тике, поэтому свежее нажатие ПКМ
        // видно в Minecraft.startUseItem того же тика (см. onVanillaUse).
        ClientTickEvents.START_CLIENT_TICK.register(Graffiti::startTick);

        // AFTER_TRANSLUCENT: после воды и стекла — рисунок не режет полупрозрачные блоки,
        // но и не спорит с ними за порядок отрисовки.
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            Graffiti module = ModuleManager.get(Graffiti.class);
            if (module != null && module.isEnable()) {
                module.onWorldRender(context);
            }
        });
    }

    private static void startTick(Minecraft minecraft) {
        Graffiti module = ModuleManager.get(Graffiti.class);
        if (module == null) {
            return;
        }
        module.refreshUseEdge(minecraft);
    }

    /**
     * Свежесть ПКМ считают два источника, и оба только поднимают флаг:
     *
     * <ul>
     *   <li>хук нажатия мыши ({@link #onMousePress}) — ловит быстрый клик,
     *       который успевает отпуститься между тиками;</li>
     *   <li>опрос в начале тика — страховка, если «использовать» переназначили
     *       на клавишу клавиатуры.</li>
     * </ul>
     *
     * <p>Флаг снимается только когда рисунок нанесён: сбрасывать его по отпусканию
     * нельзя — быстрый клик потерялся бы до того, как до него дойдёт `startUseItem`.
     */
    private void refreshUseEdge(Minecraft minecraft) {
        boolean down = minecraft.options.keyUse.isDown();
        if (minecraft.screen != null) {
            // Кнопка, зажатая при открытом меню, не должна считаться нажатием
            // в момент закрытия: иначе граффити ставится «на выходе» из GUI.
            useWasDown = down;
            useFresh = false;
            return;
        }
        if (down && !useWasDown) {
            useFresh = true;
        }
        useWasDown = down;
    }

    /**
     * Хук нажатия мыши (см. {@code MixinMouseHandler}).
     *
     * <p>Ваниль вызывает {@code startUseItem} по очереди нажатий, а не по
     * удержанию, поэтому одного нажатия — одного рисунка.
     */
    public static void onMousePress(int button, int action) {
        Graffiti module = ModuleManager.get(Graffiti.class);
        if (module != null) {
            module.noteMousePress(button, action);
        }
    }

    private void noteMousePress(int button, int action) {
        if (action != GLFW.GLFW_PRESS) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null) {
            return;
        }
        if (minecraft.options.keyUse.matchesMouse(button)) {
            useFresh = true;
        }
    }

    // ---- тик

    @Override
    public void onEnable() {
        super.onEnable();
        GraffitiWorld.refresh();
        GraffitiStore.load();
        useWasDown = false;
        useFresh = false;
        drawBindWasDown = false;
    }

    @Override
    public void onDisable() {
        // Выключение рисунки не стирает — они лежат в своём файле,
        // но несохранённую пачку правок нужно записать до выхода.
        GraffitiStore.save();
        super.onDisable();
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            useWasDown = false;
            useFresh = false;
            drawBindWasDown = false;
            return;
        }

        GraffitiWorld.refresh();

        if (minecraft.screen == null) {
            handleBindDraw(minecraft);
            handleErase(minecraft);
        }

        GraffitiStore.tick((pos, face) -> GraffitiStore.faceExists(minecraft.level, pos, face));
    }

    /**
     * ПКМ через {@code Minecraft.startUseItem}.
     *
     * @return {@code true}, если рисунок нанесён и ванильное действие нужно отменить
     */
    public boolean onVanillaUse() {
        if (!isEnable() || !useFresh) {
            return false;
        }
        if (!activation.is(MODE_USE) && !activation.is(MODE_SNEAK)) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return false;
        }
        if (activation.is(MODE_SNEAK) && !minecraft.player.isShiftKeyDown()) {
            return false;
        }

        if (paint(minecraft)) {
            useFresh = false;
            return true;
        }
        return false;
    }

    private void handleBindDraw(Minecraft minecraft) {
        if (!activation.is(MODE_BIND)) {
            drawBindWasDown = false;
            return;
        }

        int key = drawBind.getKey();
        boolean down = key != -1 && KeyBoardUtils.isBindHeld(key);
        boolean pressed = down && !drawBindWasDown;
        drawBindWasDown = down;
        if (pressed) {
            paint(minecraft);
        }
    }

    private void handleErase(Minecraft minecraft) {
        int key = eraseBind.getKey();
        if (key == -1 || !KeyBoardUtils.isBindHeld(key)) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastPlaceMs < PLACE_COOLDOWN_MS) {
            return;
        }

        Target target = target(minecraft);
        if (target == null) {
            return;
        }

        GraffitiSpot erased = GraffitiStore.remove(target.pos(), target.face());
        if (erased != null) {
            lastPlaceMs = now;
            NotificationManager.info("Graffiti", "стёрто: " + erased.design().label());
        }
    }

    /**
     * Наносит рисунок на грань под прицелом.
     *
     * @return {@code true}, если рисунок поставлен или перерисован
     */
    private boolean paint(Minecraft minecraft) {
        long now = System.currentTimeMillis();
        if (now - lastPlaceMs < PLACE_COOLDOWN_MS) {
            return false;
        }

        Target target = target(minecraft);
        if (target == null) {
            return false;
        }
        lastPlaceMs = now;

        GraffitiDesign design = GraffitiDesign.byLabel(designMode.getCurrent());
        if (design == null) {
            design = GraffitiDesign.TAG;
        }

        GraffitiSpot placed = GraffitiStore.place(target.pos(), target.face(), design, spin.get());
        if (placed == null) {
            NotificationManager.warning("Graffiti", "лимит " + GraffitiStore.MAX_PER_WORLD + " в мире");
            return false;
        }

        // Взмах рукой — единственный визуальный отклик: баллончика в руке нет,
        // а ПКМ отменён, поэтому без него нанесение выглядит «бесплатным».
        if (minecraft.player != null) {
            minecraft.player.swing(InteractionHand.MAIN_HAND);
        }
        return true;
    }

    /** Грань под прицелом, если на неё можно нанести рисунок. */
    private Target target(Minecraft minecraft) {
        Level level = minecraft.level;
        if (level == null || !(minecraft.hitResult instanceof BlockHitResult hit)) {
            return null;
        }
        if (hit.getType() != HitResult.Type.BLOCK || minecraft.player == null) {
            return null;
        }

        BlockPos pos = hit.getBlockPos();
        Direction face = hit.getDirection();

        // +1.0 — допуск на толщину блока: глаз и центр грани не совпадают.
        if (minecraft.player.getEyePosition().distanceTo(GraffitiRenderer.center(pos, face)) > distance.get() + 1.0D) {
            return null;
        }

        BlockState state = level.getBlockState(pos);
        if (state.isAir() || !GraffitiStore.faceExists(level, pos, face)) {
            return null;
        }
        return new Target(pos.immutable(), face);
    }

    // ---- рендер

    private void onWorldRender(WorldRenderContext context) {
        List<GraffitiSpot> spots = GraffitiStore.current();
        Minecraft minecraft = Minecraft.getInstance();

        if (!spots.isEmpty()) {
            GraffitiRenderer.render(context, spots, opacity.get(), lighting.get());
        }

        if (!highlight.get() || minecraft.screen != null) {
            return;
        }
        Target target = target(minecraft);
        if (target != null) {
            GraffitiRenderer.highlight(context, target.pos(), target.face(), ThemeManager.getColor());
        }
    }

    private record Target(BlockPos pos, Direction face) {
    }
}

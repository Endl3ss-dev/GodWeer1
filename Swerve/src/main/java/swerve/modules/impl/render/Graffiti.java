package swerve.modules.impl.render;

import com.mojang.blaze3d.vertex.PoseStack;

import swerve.graffiti.GraffitiDesign;
import swerve.graffiti.GraffitiGhost;
import swerve.graffiti.GraffitiQuad;
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
import swerve.ui.hud.HudManager;
import swerve.ui.newgui.GuiFonts;
import swerve.ui.newgui.GuiRender;
import swerve.utils.client.NotificationManager;
import swerve.utils.color.ColorUtils;
import swerve.utils.input.KeyBoardUtils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.lwjgl.glfw.GLFW;

/**
 * [+] Graffiti — одно граффити 1x1, которое игрок носит с собой и ставит на грани блоков.
 *
 * <h2>Модель поведения</h2>
 * <ul>
 *   <li><b>Призрак.</b> Пока прицел смотрит на подходящую грань, на ней полупрозрачным
 *       показан выбранный рисунок. Прицел ушёл на соседний блок — призрак плавно и
 *       быстро (≈120 мс) перелетает туда, а не прыгает (см. {@link GraffitiGhost}).</li>
 *   <li><b>Одна установка за {@code Кулдаун} секунд (по умолчанию 30).</b> Таймер
 *       виден на индикаторе под прицелом; пока он идёт, призрак тускнеет, а клик
 *       уходит в ваниллу (блок ставится, еда съедается как обычно).</li>
 *   <li><b>Одно граффити в мире.</b> Установка в новом месте снимает прежний рисунок:
 *       он исчезает там, где был, и появляется на новой грани с коротким проявлением.</li>
 *   <li><b>Биндинг «Стирание»</b> убирает рисунок из мира.</li>
 * </ul>
 *
 * <h2>Почему это только своя видимость</h2>
 * Рисунки живут в клиенте и в сеть не уходят: сервер о них не знает, другие игроки
 * их не видят. Так же устроены Trails и ChinaHat — отправка требовала бы своего
 * канала и серверного мода, а в ванильном протоколе места для этого нет. Взамен
 * клиент не отправляет ни одного пакета и не зависит от прав на сервере.
 *
 * <h2>Ввод</h2>
 * ПКМ в ванилле — «использовать предмет». Рисование перехватывается в
 * {@code Minecraft.startUseItem} (см. {@code MixinMinecraft.swerve$graffitiUse}) и
 * только когда рисунок действительно поставлен: нажатие отменяется, блок не ставится.
 * Нажатие считается «свежим» один раз на клик — удержание не ставит рисунок каждый тик.
 */
public final class Graffiti extends Module {

    private static final String MODE_SNEAK = "Присед + ПКМ";
    private static final String MODE_USE = "ПКМ";
    private static final String MODE_BIND = "Бинд";

    /** Прозрачность призрака, когда рисунок можно ставить. */
    private static final float GHOST_ALPHA = 0.42F;

    /** Сколько прозрачность призрака опускается на кулдауне. */
    private static final float GHOST_COOLDOWN_FACTOR = 0.55F;

    private static boolean eventsRegistered;

    private final ModeSetting designMode = new ModeSetting("Рисунок", GraffitiDesign.SKETCH.label(),
            designLabels());
    private final ModeSetting activation = new ModeSetting("Активация", MODE_SNEAK, MODE_SNEAK, MODE_USE, MODE_BIND);
    private final BindSetting drawBind = new BindSetting("Бинд рисования", -1)
            .visible(() -> activation.is(MODE_BIND));
    private final BindSetting eraseBind = new BindSetting("Бинд стирания", -1);
    private final FloatSetting spin = new FloatSetting("Поворот", 0.0F, 0.0F, 345.0F, 15.0F);
    private final FloatSetting distance = new FloatSetting("Дистанция", 4.5F, 1.0F, 8.0F, 0.5F);
    private final FloatSetting cooldown = new FloatSetting("Кулдаун", 30.0F, 5.0F, 120.0F, 5.0F);
    private final FloatSetting opacity = new FloatSetting("Прозрачность", 0.92F, 0.3F, 1.0F, 0.02F);
    private final BooleanSetting lighting = new BooleanSetting("Освещение", true);

    private final GraffitiGhost ghost = new GraffitiGhost();

    private BlockPos cachedPos;
    private Direction cachedFace;
    private float cachedSpin = Float.NaN;
    private GraffitiQuad cachedQuad;

    private boolean useWasDown;
    private boolean useFresh;
    private boolean drawBindWasDown;
    private long lastPlaceMs;
    private long lastEraseMs;

    public Graffiti() {
        super("Graffiti", "Граффити 1x1 на гранях блоков", ModuleCategory.RENDER);
        addSettings(designMode, activation, drawBind, eraseBind, spin, distance, cooldown, opacity, lighting);
        registerEvents();
        // Индикатор кулдауна под прицелом. Статическая ссылка: повторная инициализация
        // модулей не плодит дубликаты слоя (как у KillAura).
        HudManager.registerOverlay(Graffiti::renderHudOverlay);
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
        // Конец тика гасит флаг: нажатие без цели (обычный клик по блоку) не должно
        // «выстрелить» граффити позже, когда игрок просто присядет и нажмёт ПКМ.
        ClientTickEvents.END_CLIENT_TICK.register(Graffiti::endTick);

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

    private static void endTick(Minecraft minecraft) {
        Graffiti module = ModuleManager.get(Graffiti.class);
        if (module != null) {
            module.useFresh = false;
        }
    }

    /**
     * Свежесть ПКМ считают два источника, и оба только поднимают флаг:
     * хук нажатия мыши (быстрый клик успевает отпуститься между тиками) и опрос
     * в начале тика (страховка, если «использовать» переназначили на клавишу).
     * Гасит флаг конец тика ({@link #endTick}): клик без подходящей цели не должен
     * «выстрелить» позже, когда игрок просто присядет и нажмёт ПКМ. Сброс по
     * отпусканию кнопки не годится — быстрый клик отпускается раньше, чем
     * {@code startUseItem} до него доберётся.
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

    /** Хук нажатия мыши (см. {@code MixinMouseHandler}). */
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
        ghost.reset();
        useWasDown = false;
        useFresh = false;
        drawBindWasDown = false;
        // Кулдаун не переносится с прошлого включения: игрок только что включил модуль.
        lastPlaceMs = 0L;
    }

    @Override
    public void onDisable() {
        // Выключение рисунок не стирает — он лежит в своём файле,
        // но несохранённую правку нужно записать до выхода.
        GraffitiStore.save();
        ghost.reset();
        super.onDisable();
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            useWasDown = false;
            useFresh = false;
            drawBindWasDown = false;
            ghost.reset();
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
     * @return {@code true}, если рисунок поставлен и ванильное действие нужно отменить
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
        if (now - lastEraseMs < 300L) {
            return;
        }
        lastEraseMs = now;

        GraffitiSpot removed = GraffitiStore.remove();
        if (removed != null) {
            NotificationManager.info("Graffiti", "стёрто: " + removed.design().label());
        }
    }

    /**
     * Ставит рисунок на грань под прицелом, снимая прежний.
     *
     * @return {@code true}, если рисунок поставлен
     */
    private boolean paint(Minecraft minecraft) {
        long now = System.currentTimeMillis();
        long remaining = cooldownRemainingMs(now);
        if (remaining > 0L) {
            // Не молчим: клик ушёл в ваниллу, и без подсказки это выглядит как баг.
            // Текст без секунд — иначе каждое нажатие плодило бы новое уведомление.
            NotificationManager.warning("Graffiti", "рисунок ещё не готов");
            return false;
        }

        Target target = target(minecraft);
        if (target == null) {
            return false;
        }

        GraffitiSpot placed = GraffitiStore.place(target.pos(), target.face(), currentDesign(),
                placementSpin(minecraft, target.face()));
        if (placed == null) {
            return false;
        }

        lastPlaceMs = now;

        // Взмах рукой — единственный визуальный отклик: баллончика в руке нет,
        // а ПКМ отменён, поэтому без него установка выглядит «бесплатной».
        minecraft.player.swing(InteractionHand.MAIN_HAND);
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
        if (minecraft.player.getEyePosition().distanceTo(GraffitiQuad.centerOf(pos, face)) > distance.get() + 1.0D) {
            return null;
        }

        BlockState state = level.getBlockState(pos);
        if (state.isAir() || !GraffitiStore.faceExists(level, pos, face)) {
            return null;
        }
        return new Target(pos.immutable(), face);
    }

    /**
     * Итоговый поворот рисунка: для пола и потолка — «низом к игроку», как у ванильных
     * табличек, плюс пользовательский {@code Поворот} как смещение.
     *
     * <p>Угол берётся от {@code getDirection()} (целые 90°), а не от точного yaw:
     * иначе рисунок на полу крутился бы вслед за каждым движением мыши. Для боковых
     * граней верх картинки всегда вверх, поэтому там остаётся только настройка.
     */
    private float placementSpin(Minecraft minecraft, Direction face) {
        float base = spin.get();
        if (minecraft.player == null || !face.getAxis().isVertical()) {
            return GraffitiSpot.normalizeSpin(base);
        }

        // «Низ» картинки должен смотреть на игрока: это направление, противоположное взгляду.
        Direction towardPlayer = minecraft.player.getDirection().getOpposite();
        double angle = face == Direction.UP
                ? Math.atan2(towardPlayer.getStepX(), towardPlayer.getStepZ())
                : Math.atan2(towardPlayer.getStepX(), -towardPlayer.getStepZ());
        return GraffitiSpot.normalizeSpin(base + (float) Math.toDegrees(angle));
    }

    private GraffitiDesign currentDesign() {
        GraffitiDesign design = GraffitiDesign.byLabel(designMode.getCurrent());
        return design == null ? GraffitiDesign.SKETCH : design;
    }

    // ---- кулдаун

    private long cooldownMs() {
        return (long) (cooldown.get() * 1000.0F);
    }

    private long cooldownRemainingMs(long now) {
        if (lastPlaceMs == 0L) {
            return 0L;
        }
        long elapsed = now - lastPlaceMs;
        long total = cooldownMs();
        return elapsed >= total ? 0L : total - elapsed;
    }

    private static long seconds(long millis) {
        return Math.max(0L, (millis + 999L) / 1000L);
    }

    /**
     * Поза грани для призрака, собранная один раз на грань.
     *
     * <p>Рендер зовёт её каждый кадр, а {@link GraffitiQuad#of} строит десяток
     * векторов: пересобирать их без смены цели или поворота — мусор в куче на кадр.
     */
    private GraffitiQuad quadFor(Target target, float spinValue) {
        if (this.cachedQuad != null
                && target.face() == this.cachedFace
                && target.pos().equals(this.cachedPos)
                && spinValue == this.cachedSpin) {
            return this.cachedQuad;
        }

        this.cachedQuad = GraffitiQuad.of(target.pos(), target.face(), spinValue);
        this.cachedPos = target.pos();
        this.cachedFace = target.face();
        this.cachedSpin = spinValue;
        return this.cachedQuad;
    }

    // ---- рендер мира

    private void onWorldRender(WorldRenderContext context) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            ghost.reset();
            return;
        }

        long frameNanos = minecraft.getFrameTimeNs();

        // 1. Установленный рисунок (один на мир, только в своём измерении).
        GraffitiSpot placed = GraffitiStore.visible();
        if (placed != null) {
            GraffitiRenderer.renderPlaced(context, placed, opacity.get(), lighting.get());
        }

        // 2. Призрак на грани под прицелом.
        if (minecraft.screen != null || minecraft.options.hideGui || minecraft.player.isSpectator()) {
            ghost.hide(frameNanos);
            return;
        }

        Target target = target(minecraft);
        if (target == null) {
            ghost.hide(frameNanos);
            return;
        }

        ghost.follow(quadFor(target, placementSpin(minecraft, target.face())), frameNanos);
        if (!ghost.visible()) {
            return;
        }

        // Там, где рисунок уже стоит, призрак не нужен: два квада в одной плоскости
        // дали бы двойную яркость и «грязную» кромку.
        if (placed != null && ghost.target() != null
                && ghost.target().sameFace(placed.pos(), placed.face())) {
            return;
        }

        boolean ready = cooldownRemainingMs(System.currentTimeMillis()) == 0L;
        float alpha = GHOST_ALPHA * ghost.alpha() * (ready ? 1.0F : GHOST_COOLDOWN_FACTOR);
        GraffitiRenderer.renderGhost(context, currentDesign(), ghost.quad(), alpha, ready);
    }

    // ---- индикатор

    private static void renderHudOverlay(GuiGraphics context) {
        Graffiti module = ModuleManager.get(Graffiti.class);
        if (module != null) {
            module.renderIndicator(context);
        }
    }

    /**
     * Компактный индикатор под прицелом: что за рисунок в руках и сколько ждать.
     *
     * <p>Кулдаун 30 секунд без индикатора читается как «модуль сломался», поэтому
     * под прицелом всегда виден текущий рисунок, а во время ожидания — счётчик и
     * полоса прогресса.
     */
    private void renderIndicator(GuiGraphics context) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!isEnable() || minecraft.player == null || minecraft.level == null || minecraft.screen != null) {
            return;
        }

        GraffitiDesign design = currentDesign();
        long remaining = cooldownRemainingMs(System.currentTimeMillis());
        long total = cooldownMs();
        boolean ready = remaining == 0L;

        String label = design.label();
        String suffix = ready ? "готово" : seconds(remaining) + " с";
        float size = 7.0F;

        float labelWidth = GuiFonts.SF_MEDIUM.getWidth(label, size);
        float suffixWidth = GuiFonts.SF_MEDIUM.getWidth(suffix, size);
        float padding = 6.0F;
        float gap = 5.0F;
        float textWidth = labelWidth + gap + suffixWidth;
        float width = textWidth + padding * 2.0F;
        float height = 15.0F + (ready ? 0.0F : 2.0F);

        float centerX = context.guiWidth() * 0.5F;
        float centerY = context.guiHeight() * 0.5F;
        float x = centerX - width * 0.5F;
        float y = centerY + 20.0F;

        int accent = ThemeManager.getColor();
        int background = ColorUtils.rgba(12, 12, 14, 168);
        int textColor = ColorUtils.rgba(236, 236, 240, 235);

        PoseStack stack = context.pose();
        GuiRender.roundedRect(stack, x, y, width, height, 4.0F, background);
        GuiRender.roundedOutline(stack, x, y, width, height, 1.0F,
                ColorUtils.rgba(ColorUtils.red(accent), ColorUtils.green(accent), ColorUtils.blue(accent), 110), 4.0F);

        float textY = y + (height - GuiFonts.SF_MEDIUM.getHeight(label, size)) * 0.5F;
        GuiFonts.SF_MEDIUM.draw(stack, label, x + padding, textY, textColor, size);
        GuiFonts.SF_MEDIUM.draw(stack, suffix, x + padding + labelWidth + gap, textY,
                ready ? withAlpha(accent, 235) : ColorUtils.rgba(255, 196, 67, 235), size);

        if (!ready) {
            float progress = Mth.clamp(1.0F - remaining / (float) Math.max(1L, total), 0.0F, 1.0F);
            float barWidth = width - padding * 2.0F;
            float barY = y + height - 2.5F;
            GuiRender.roundedRect(stack, x + padding, barY, barWidth, 1.5F, 0.75F,
                    ColorUtils.rgba(255, 255, 255, 38));
            GuiRender.roundedRect(stack, x + padding, barY, Math.max(0.5F, barWidth * progress), 1.5F, 0.75F,
                    withAlpha(accent, 220));
        }
    }

    private static int withAlpha(int color, int alpha) {
        return ColorUtils.rgba(ColorUtils.red(color), ColorUtils.green(color), ColorUtils.blue(color), alpha);
    }

    private record Target(BlockPos pos, Direction face) {
    }
}

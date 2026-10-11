package swerve.modules.impl.hud;

import com.mojang.blaze3d.vertex.PoseStack;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import swerve.managers.ModuleManager;
import swerve.managers.ThemeManager;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.modules.api.setting.Setting;
import swerve.ui.hud.HudElement;
import swerve.ui.newgui.GuiFonts;
import swerve.ui.newgui.GuiKeys;
import swerve.ui.newgui.GuiRender;
import swerve.utils.color.ColorUtils;

import net.minecraft.client.Minecraft;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Список включённых модулей в стиле исходного клиента (Private, ArrayListRenderer):
// текст без плашки, градиент по индексу строки, построчный выезд при включении и уходе.
// Метрики строк и цвета пересобираются в onTick; в кадре — только отрисовка.
public final class ArrayListHud extends HudElement {

    private static final float TEXT_SIZE_DEFAULT = 8.0f;
    private static final float SPACING_DEFAULT = 11.0f;
    private static final float PADDING_DEFAULT = 3.0f;
    private static final float ANIM_SPEED = 14.0f;
    private static final int MAX_SUFFIX_PARTS = 2;
    private static final int GLOW_LAYERS = 5;
    private static final float GLOW_SPREAD = 0.5f;
    private static final float MIN_WIDTH = 16.0f;
    private static final String EMPTY = "";

    private static final int WHITE = ColorUtils.rgba(255, 255, 255, 255);
    private static final float[] HSB_SCRATCH = new float[3];

    private static final Comparator<Row> BY_WIDTH = (left, right) -> Float.compare(right.textWidth, left.textWidth);

    private final ModeSetting fontSize = new ModeSetting("Размер шрифта", "8", "6", "7", "8", "9", "10", "11", "12");
    private final ModeSetting spacing = new ModeSetting("Отступ строк", "11", "8", "9", "10", "11", "12", "13", "14", "15", "16");
    private final ModeSetting align = new ModeSetting("Выравнивание", "Авто", "Авто", "Право", "Лево");
    private final BooleanSetting gradient = new BooleanSetting("Градиент текста", true);
    private final BooleanSetting glow = new BooleanSetting("Свечение", true);
    private final FloatSetting glowRadius = new FloatSetting("Радиус свечения", 10.0f, 0.0f, 20.0f, 1.0f)
            .visible(glow::get);
    private final FloatSetting glowAlpha = new FloatSetting("Альфа свечения", 0.5f, 0.0f, 1.0f, 0.05f)
            .visible(glow::get);
    private final BooleanSetting outline = new BooleanSetting("Обводка", false);
    private final FloatSetting outlineWidth = new FloatSetting("Толщина обводки", 1.0f, 0.5f, 3.0f, 0.1f)
            .visible(outline::get);
    private final BooleanSetting showCombat = new BooleanSetting("Показ Combat", true);
    private final BooleanSetting showMovement = new BooleanSetting("Показ Movement", true);
    private final BooleanSetting showRender = new BooleanSetting("Показ Render", true);
    private final BooleanSetting showPlayer = new BooleanSetting("Показ Player", true);
    private final BooleanSetting showMisc = new BooleanSetting("Показ Misc", true);
    private final BooleanSetting suffixes = new BooleanSetting("Режимы настроек", false);
    private final ModeSetting bindMode = new ModeSetting("Бинды", "Скрыть", "Скрыть", "По CTRL", "Всегда");

    private final List<Row> rows = new ArrayList<>();
    private final List<Row> removal = new ArrayList<>();
    private final Object2ObjectOpenHashMap<Module, Row> rowByModule = new Object2ObjectOpenHashMap<>();
    private final StringBuilder scratch = new StringBuilder(48);

    private float measuredWidth = MIN_WIDTH;

    public ArrayListHud() {
        super("ArrayList", "Список включённых модулей. ALT + ЛКМ — перетащить", 0.995f, 0.02f);
        setAnchor(true, false);
        addSettings(fontSize, spacing, align, gradient, glow, glowRadius, glowAlpha, outline, outlineWidth,
                showCombat, showMovement, showRender, showPlayer, showMisc, suffixes, bindMode);
    }

    @Override
    public void onEnable() {
        rebuildRows();
        super.onEnable();
    }

    @Override
    public void onTick() {
        rebuildRows();
    }

    @Override
    protected void measure() {
        float rowSpacing = spacingValue();
        float height = 0.0f;

        for (int i = 0, size = this.rows.size(); i < size; i++) {
            height += rowSpacing * this.rows.get(i).anim;
        }

        setSize(this.measuredWidth, height);
    }

    @Override
    protected void renderContent(PoseStack stack, float alpha, float delta) {
        int count = this.rows.size();
        if (count == 0) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        boolean rightSide = switch (this.align.getCurrent()) {
            case "Право" -> true;
            case "Лево" -> false;
            default -> getRenderX() > minecraft.getWindow().getGuiScaledWidth() / 2.0f;
        };

        float textSize = textSizeValue();
        float rowSpacing = spacingValue();
        float padding = paddingValue();
        float y = 0.0f;

        for (int i = 0; i < count; i++) {
            Row row = this.rows.get(i);
            row.anim = approach(row.anim, row.target, delta, ANIM_SPEED);

            float anim = row.anim;
            float rowHeight = rowSpacing * anim;
            if (anim > 0.01f) {
                float slide = row.textWidth * (1.0f - anim);
                float textX = rightSide
                        ? this.measuredWidth - row.textWidth - padding + slide
                        : padding - slide;
                float textY = y + rowHeight * 0.5f - row.textHeight * 0.5f;
                float rowAlpha = alpha * anim;

                if (this.glow.get() && this.glowAlpha.get() > 0.0f) {
                    GuiRender.drawGlow(stack, textX - 1.0f, textY - 1.0f,
                            row.textWidth + 2.0f, row.textHeight + 2.0f, 3.0f,
                            this.glowRadius.get() * GLOW_SPREAD, GLOW_LAYERS,
                            ColorUtils.multAlpha(row.color, rowAlpha * this.glowAlpha.get()));
                }

                if (this.outline.get()) {
                    float width = this.outlineWidth.get();
                    float half = width * 0.5f;
                    GuiRender.roundedOutline(stack, textX - padding - half, textY - padding * 0.5f - half,
                            row.textWidth + padding * 2.0f + width, row.textHeight + padding + width,
                            width, ColorUtils.multAlpha(ColorUtils.interpolateColor(WHITE, row.color, 0.5f), rowAlpha), 3.0f);
                }

                GuiFonts.SF_MEDIUM.draw(stack, row.label, textX, textY,
                        ColorUtils.multAlpha(row.color, rowAlpha), textSize);
            }

            y += rowHeight;
        }
    }

    private void rebuildRows() {
        List<Module> modules = ModuleManager.getModules();
        for (int i = 0, size = modules.size(); i < size; i++) {
            Module module = modules.get(i);
            if (module.getCategory() == ModuleCategory.INTERFACE) {
                continue;
            }

            boolean visible = module.isEnable() && categoryVisible(module.getCategory());
            Row row = this.rowByModule.get(module);
            if (row == null) {
                if (!visible) {
                    continue;
                }
                row = new Row(module);
                this.rowByModule.put(module, row);
            }
            row.target = visible ? 1.0f : 0.0f;
        }

        this.removal.clear();
        this.rows.clear();
        for (Row row : this.rowByModule.values()) {
            if (row.target == 0.0f && row.anim <= 0.01f) {
                this.removal.add(row);
                continue;
            }
            this.rows.add(row);
        }
        for (int i = 0, size = this.removal.size(); i < size; i++) {
            this.rowByModule.remove(this.removal.get(i).module);
        }

        float textSize = textSizeValue();
        float padding = paddingValue();
        for (int i = 0, size = this.rows.size(); i < size; i++) {
            Row row = this.rows.get(i);
            updateLabel(row);
            row.textWidth = GuiFonts.SF_MEDIUM.getWidth(row.label, textSize);
            row.textHeight = GuiFonts.SF_MEDIUM.getHeight(row.label, textSize);
        }

        this.rows.sort(BY_WIDTH);

        int total = this.rows.size();
        int themeColor = ThemeManager.getColor();
        boolean useGradient = this.gradient.get();
        float maxWidth = MIN_WIDTH;

        for (int i = 0; i < total; i++) {
            Row row = this.rows.get(i);
            row.color = useGradient ? gradientColor(i, total, themeColor) : themeColor;
            if (row.textWidth > maxWidth) {
                maxWidth = row.textWidth;
            }
        }

        this.measuredWidth = maxWidth + padding * 2.0f;
    }

    // Градиент исходного клиента: сдвиг тона, рост насыщенности и падение яркости по мере списка.
    private static int gradientColor(int index, int total, int baseColor) {
        if (total <= 1) {
            return baseColor;
        }

        float progress = index / (float) (total - 1);
        float[] hsb = HSB_SCRATCH;
        Color.RGBtoHSB(ColorUtils.red(baseColor), ColorUtils.green(baseColor), ColorUtils.blue(baseColor), hsb);

        hsb[0] = (hsb[0] + progress * 0.15f) % 1.0f;
        hsb[1] = Math.min(1.0f, hsb[1] * (1.0f + progress * 0.2f));
        hsb[2] = Math.min(1.0f, hsb[2] * (1.0f - progress * 0.1f));

        return 0xFF000000 | (Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]) & 0x00FFFFFF);
    }

    private boolean categoryVisible(ModuleCategory category) {
        return switch (category) {
            case COMBAT -> this.showCombat.get();
            case MOVEMENT -> this.showMovement.get();
            case RENDER -> this.showRender.get();
            case PLAYER -> this.showPlayer.get();
            case MISC -> this.showMisc.get();
            default -> false;
        };
    }

    private void updateLabel(Row row) {
        this.scratch.setLength(0);
        this.scratch.append(row.module.getName());

        if (this.suffixes.get()) {
            List<Setting> settings = row.module.getSettings();
            int used = 0;
            for (int i = 0, size = settings.size(); i < size && used < MAX_SUFFIX_PARTS; i++) {
                Setting setting = settings.get(i);
                if (!(setting instanceof ModeSetting mode) || !setting.visible()) {
                    continue;
                }
                String current = mode.displayCurrent();
                if (current.isEmpty()) {
                    continue;
                }
                this.scratch.append(' ').append(current);
                used++;
            }
        }

        String bind = bindText(row.module);
        if (!bind.isEmpty()) {
            this.scratch.append(' ').append(bind);
        }

        row.label = this.scratch.toString();
    }

    private String bindText(Module module) {
        int key = module.getKey();
        if (key == -1) {
            return EMPTY;
        }

        boolean visible;
        switch (this.bindMode.getCurrent()) {
            case "Всегда" -> visible = true;
            case "По CTRL" -> visible = GuiKeys.isControlDown();
            default -> visible = false;
        }
        return visible ? "[" + GuiKeys.keyName(key) + "]" : EMPTY;
    }

    private float textSizeValue() {
        try {
            return Float.parseFloat(this.fontSize.getCurrent());
        } catch (NumberFormatException exception) {
            return TEXT_SIZE_DEFAULT;
        }
    }

    private float spacingValue() {
        try {
            return Float.parseFloat(this.spacing.getCurrent());
        } catch (NumberFormatException exception) {
            return SPACING_DEFAULT;
        }
    }

    private float paddingValue() {
        return PADDING_DEFAULT;
    }

    private static float approach(float current, float target, float delta, float speed) {
        return current + (target - current) * (1.0f - (float) Math.exp(-delta * speed));
    }

    private static final class Row {

        private final Module module;
        private String label = "";
        private float textWidth;
        private float textHeight;
        private float anim;
        private float target;
        private int color = WHITE;

        private Row(Module module) {
            this.module = module;
        }
    }
}

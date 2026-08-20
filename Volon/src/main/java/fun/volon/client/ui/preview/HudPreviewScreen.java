package fun.volon.client.ui.preview;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import fun.volon.api.QClient;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.storages.implement.DragStorage;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.api.utils.draggable.Draggable;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.impl.render.Interface;
import fun.volon.client.modules.impl.render.base.implement.Potions;

import java.util.ArrayList;
import java.util.List;

/**
 * Экран превью HUD: фон Minecraft + реальные виджеты (через синтетический EventRender)
 * + правая панель управления (видимость, модули, зелья, стиль, сброс позиций).
 *
 * Активируется при запуске с -Dvolon.hudPreview=true через ClientTickEvents в Volon.startClient().
 */
public class HudPreviewScreen extends Screen implements QClient {

    public static boolean opened = false;

    private static final Identifier BG = Identifier.of("volon", "textures/preview/background.png");

    private static final int PANEL_W = 220;
    private static final int PAD     = 8;

    private boolean Volon$leftPressed;
    private int panelScroll = 0;
    private final List<Rect> clickRegions = new ArrayList<>();
    private final List<PotionPreset> presets = List.of(
            new PotionPreset(StatusEffects.SPEED,           1, secs(60),     false),
            new PotionPreset(StatusEffects.STRENGTH,        1, secs(180),    false),
            new PotionPreset(StatusEffects.FIRE_RESISTANCE, 0, secs(420),    false),
            new PotionPreset(StatusEffects.REGENERATION,    1, secs(30),     false),
            new PotionPreset(StatusEffects.HASTE,           1, secs(120),    false),
            new PotionPreset(StatusEffects.JUMP_BOOST,      0, secs(180),    false),
            new PotionPreset(StatusEffects.NIGHT_VISION,    0, 0,            true),
            new PotionPreset(StatusEffects.SLOWNESS,        0, secs(45),     false)
    );

    public HudPreviewScreen() {
        super(Text.literal("HUD Preview"));
    }

    private static int secs(int s) { return s * 20; }

    private record PotionPreset(RegistryEntry<StatusEffect> entry, int amp, int durTicks, boolean infinite) {
        public String label() {
            String name = entry.value().getName().getString();
            String lvl  = amp == 0 ? "" : " " + roman(amp + 1);
            String dur  = infinite ? "inf" : durTicks / 20 / 60 + ":" + String.format("%02d", durTicks / 20 % 60);
            return name + lvl + "  " + dur;
        }
        private static String roman(int n) {
            return switch (n) {
                case 1 -> "I";
                case 2 -> "II";
                case 3 -> "III";
                case 4 -> "IV";
                case 5 -> "V";
                default -> String.valueOf(n);
            };
        }
    }

    private record Rect(int x, int y, int w, int h, Runnable onClick) {
        boolean contains(double mx, double my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }
    }

    @Override
    protected void init() {
        super.init();
        PreviewState.setActive(true);
        // Включаем Interface, чтобы Draggable принимал клики (mixin проверяет module.isEnable()).
        if (Interface.INSTANCE != null && !Interface.INSTANCE.isEnable()) {
            Interface.INSTANCE.setEnabled(true);
        }
    }

    @Override
    public void removed() {
        super.removed();
        for (Draggable d : DragStorage.draggables.values()) d.onRelease(0);
        PreviewState.setActive(false);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        PreviewState.tick();
    }

    // ── Render ───────────────────────────────────────────────────────────────

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack m = context.getMatrices();
        int sw = this.width;
        int sh = this.height;

        // Фон растягивается на всю область слева от панели.
        int viewW = sw - PANEL_W;
        RenderUtils.drawImage(m, BG, 0, 0, viewW, sh, 0xFFFFFFFF);

        // Лёгкое затемнение фона для контраста с HUD'ом.
        RenderUtils.drawRoundedRect(m, 0, 0, viewW, sh, 0, 0x40000000);

        // HUD: синтетический EventRender, вызываем Interface напрямую.
        EventRender.Default ev = new EventRender.Default(context, delta);
        if (Interface.INSTANCE != null) {
            try {
                Interface.INSTANCE.onEvent(ev);
            } catch (Throwable ignored) {
                // Не падаем превью — какой-то виджет может пожаловаться без player.
            }
        }

        // Обновление Draggable (драг мышкой). Mc уже инициализирован.
        boolean leftPressed = GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(),
                GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        if (Volon$leftPressed && !leftPressed) {
            for (Draggable d : DragStorage.draggables.values()) d.onRelease(0);
        }
        Volon$leftPressed = leftPressed;
        Window window = MinecraftClient.getInstance().getWindow();
        for (Draggable d : DragStorage.draggables.values()) {
            if (d.getModule().isEnable()) d.onDraw(mouseX, mouseY, window, m);
        }

        // Правая панель.
        clickRegions.clear();
        renderPanel(context, sw - PANEL_W, 0, PANEL_W, sh, mouseX, mouseY);
    }

    private void renderPanel(DrawContext ctx, int px, int py, int pw, int ph, int mx, int my) {
        // Подложка панели.
        ctx.fill(px, py, px + pw, py + ph, 0xE0101010);
        ctx.fill(px, py, px + 1, py + ph, 0xFF2A2A2A);

        int y = py + PAD - panelScroll;
        int leftX = px + PAD;
        int contentW = pw - PAD * 2;

        ctx.drawText(this.textRenderer, Text.literal("HUD PREVIEW").formatted(net.minecraft.util.Formatting.WHITE),
                leftX, y, 0xFFFFFFFF, false);
        y += 12;
        ctx.drawText(this.textRenderer,
                Text.literal("Перетаскивай виджеты мышкой").formatted(net.minecraft.util.Formatting.GRAY),
                leftX, y, 0xFFAAAAAA, false);
        y += 14;

        // ── Виджеты (видимость) ─────────────────────────────────────────────
        y = section(ctx, leftX, y, contentW, "Виджеты");
        if (Interface.INSTANCE != null) {
            for (String name : Interface.INSTANCE.getHudModuleNames()) {
                boolean on = Interface.INSTANCE.isHudModuleVisible(name);
                y = checkboxRow(ctx, leftX, y, contentW, name, on, mx, my, () ->
                        Interface.INSTANCE.setHudModuleVisible(name, !on));
            }
        }
        y += 6;

        // ── Модули (toggle) ────────────────────────────────────────────────
        y = section(ctx, leftX, y, contentW, "Модули (с биндом)");
        for (Module mod : ModuleClass.INSTANCE.getObject()) {
            if (mod.getKey() == -1 || mod.getKey() == 0) continue;
            boolean enabled = mod.isEnable();
            String label = mod.getDisplayName();
            y = checkboxRow(ctx, leftX, y, contentW, label, enabled, mx, my, () -> mod.setEnabled(!enabled));
        }
        y += 6;

        // ── Стиль Potions ──────────────────────────────────────────────────
        y = section(ctx, leftX, y, contentW, "Стиль Potions");
        Potions potions = findPotions();
        if (potions != null) {
            boolean compact = potions.isPotionStyleCompact();
            int btnW = (contentW - 4) / 2;
            y = buttonRow(ctx, leftX, y, btnW, "Обычный", !compact, mx, my,
                    () -> potions.setPotionStyleCompact(false));
            buttonRow(ctx, leftX + btnW + 4, y - 14, btnW, "Компакт", compact, mx, my,
                    () -> potions.setPotionStyleCompact(true));
        }
        y += 6;

        // ── Зелья: добавить ────────────────────────────────────────────────
        y = section(ctx, leftX, y, contentW, "Добавить зелье");
        for (PotionPreset preset : presets) {
            y = buttonRow(ctx, leftX, y, contentW, "+ " + preset.label(), false, mx, my,
                    () -> PreviewState.addEffect(preset.entry, preset.amp, preset.durTicks, preset.infinite));
        }
        y += 6;

        // ── Зелья: активные ────────────────────────────────────────────────
        y = section(ctx, leftX, y, contentW, "Активные эффекты");
        List<StatusEffectInstance> active = PreviewState.getEffects();
        if (active.isEmpty()) {
            ctx.drawText(this.textRenderer, Text.literal("(пусто)").formatted(net.minecraft.util.Formatting.DARK_GRAY),
                    leftX, y, 0xFF555555, false);
            y += 12;
        } else {
            for (StatusEffectInstance e : active) {
                String name = e.getEffectType().value().getName().getString();
                String lvl  = e.getAmplifier() == 0 ? "" : " " + romanShort(e.getAmplifier() + 1);
                String dur  = e.isInfinite() ? "inf" :
                        (e.getDuration() / 20 / 60) + ":" + String.format("%02d", e.getDuration() / 20 % 60);
                String label = "× " + name + lvl + "  " + dur;
                final RegistryEntry<StatusEffect> entry = e.getEffectType();
                y = buttonRow(ctx, leftX, y, contentW, label, false, mx, my,
                        () -> PreviewState.removeEffect(entry));
            }
        }
        y += 8;

        // ── Reset ───────────────────────────────────────────────────────────
        y = buttonRow(ctx, leftX, y, contentW, "Сбросить позиции", false, mx, my, this::resetPositions);
        y += 4;
        y = buttonRow(ctx, leftX, y, contentW, "Закрыть превью", false, mx, my, this::close);
    }

    private static String romanShort(int n) {
        return switch (n) { case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; case 4 -> "IV"; case 5 -> "V"; default -> String.valueOf(n); };
    }

    private int section(DrawContext ctx, int x, int y, int w, String title) {
        ctx.fill(x, y + 4, x + w, y + 5, 0xFF2A2A2A);
        ctx.drawText(this.textRenderer, Text.literal(title).formatted(net.minecraft.util.Formatting.GRAY),
                x, y - 2, 0xFFBFBFBF, false);
        return y + 12;
    }

    private int checkboxRow(DrawContext ctx, int x, int y, int w, String label, boolean checked,
                            int mx, int my, Runnable onClick) {
        int h = 12;
        boolean hover = mx >= x && mx < x + w && my >= y && my < y + h;
        if (hover) ctx.fill(x, y, x + w, y + h, 0x40FFFFFF);
        int boxColor = checked ? 0xFFAFB6FF : 0xFF555555;
        ctx.fill(x + 1, y + 2, x + 9, y + 10, 0xFF000000);
        ctx.fill(x + 2, y + 3, x + 8, y + 9, boxColor);
        if (checked) {
            ctx.fill(x + 3, y + 5, x + 7, y + 6, 0xFF000000);
            ctx.fill(x + 4, y + 6, x + 6, y + 7, 0xFF000000);
        }
        ctx.drawText(this.textRenderer, Text.literal(label), x + 13, y + 2, 0xFFE0E0E0, false);
        clickRegions.add(new Rect(x, y, w, h, onClick));
        return y + h + 1;
    }

    private int buttonRow(DrawContext ctx, int x, int y, int w, String label, boolean active,
                          int mx, int my, Runnable onClick) {
        int h = 12;
        boolean hover = mx >= x && mx < x + w && my >= y && my < y + h;
        int bg = active ? 0xFFAFB6FF : (hover ? 0xFF353535 : 0xFF1E1E1E);
        int fg = active ? 0xFF000000 : 0xFFE0E0E0;
        ctx.fill(x, y, x + w, y + h, bg);
        ctx.drawText(this.textRenderer, Text.literal(label), x + 4, y + 2, fg, false);
        clickRegions.add(new Rect(x, y, w, h, onClick));
        return y + h + 2;
    }

    private Potions findPotions() {
        if (Interface.INSTANCE == null) return null;
        var map = Interface.INSTANCE.getConfigurableHudElements();
        var p = map.get("potions");
        return p instanceof Potions ? (Potions) p : null;
    }

    private void resetPositions() {
        for (Draggable d : DragStorage.draggables.values()) {
            d.setX(d.initialXVal);
            d.setY(d.initialYVal);
        }
    }

    // ── Input ────────────────────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Сперва — клики по панели справа.
        if (mouseX >= this.width - PANEL_W) {
            if (button == 0) {
                for (Rect r : clickRegions) {
                    if (r.contains(mouseX, mouseY)) {
                        r.onClick.run();
                        return true;
                    }
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        // Иначе — пробрасываем в Draggable (драг виджетов мышкой).
        if (button == 0) {
            for (Draggable d : DragStorage.draggables.values()) {
                if (d.getModule().isEnable() && d.onClick(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Draggable d : DragStorage.draggables.values()) d.onRelease(button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX >= this.width - PANEL_W) {
            panelScroll = Math.max(0, panelScroll - (int) (verticalAmount * 14));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

package godweer.modules.impl.hud;

import com.mojang.blaze3d.vertex.PoseStack;

import godweer.managers.FriendManager;
import godweer.managers.ModuleManager;
import godweer.managers.ThemeManager;
import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.FloatSetting;
import godweer.ui.newgui.GuiFonts;
import godweer.ui.newgui.GuiRender;
import godweer.ui.newgui.Mathf;
import godweer.utils.color.ColorUtils;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.lwjgl.glfw.GLFW;

// TargetHud: компактная панель цели — голова (лицо из скина) и панель одного размера,
// градиентный фон под цвет темы. Панель перетаскивается ЛКМ; после наведения прицелом
// держится «Время исчезновения» секунд и плавно гаснет.
public final class TargetHud extends Module {

    private static boolean eventsRegistered;

    private final BooleanSetting showArmor = new BooleanSetting("Показывать броню", true);
    private final BooleanSetting showItems = new BooleanSetting("Показывать предметы", true);
    private final FloatSetting fadeTime = new FloatSetting("Время исчезновения", 3.0f, 1.0f, 10.0f, 0.5f);
    private final FloatSetting scale = new FloatSetting("Размер", 1.0f, 0.5f, 1.5f, 0.05f);

    private LivingEntity target;
    private long lastSeen;
    private float anim;
    private float displayHp;

    // Позиция панели и перетаскивание (в GUI-пикселях).
    private float posX = -1.0f;
    private float posY = -1.0f;
    private boolean dragging;
    private float grabX, grabY;

    public TargetHud() {
        super("TargetHud", "Панель с информацией о враге", ModuleCategory.INTERFACE);
        addSettings(showArmor, showItems, fadeTime, scale);
        registerEvents();
    }

    private static void registerEvents() {
        if (eventsRegistered) {
            return;
        }
        eventsRegistered = true;
        HudRenderCallback.EVENT.register((guiGraphics, deltaTracker) -> {
            TargetHud module = ModuleManager.get(TargetHud.class);
            if (module != null && module.isEnable()) {
                module.onHudRender(guiGraphics, guiGraphics.pose(),
                        deltaTracker.getGameTimeDeltaPartialTick(true));
            }
        });
    }

    @Override
    public void onDisable() {
        target = null;
        anim = 0.0f;
        displayHp = 0.0f;
        dragging = false;
        super.onDisable();
    }

    private void onHudRender(GuiGraphics context, PoseStack stack, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) {
            target = null;
            return;
        }

        // Цель: живое существо под прицелом (кроме себя и друзей).
        boolean hovered = false;
        Entity picked = mc.crosshairPickEntity;
        if (picked instanceof LivingEntity living && living != mc.getCameraEntity() && living.isAlive()) {
            if (!(living instanceof Player player && FriendManager.isFriend(player))) {
                target = living;
                lastSeen = System.currentTimeMillis();
                hovered = true;
            }
        }
        if (target != null && !target.isAlive()) {
            target = null;
        }

        boolean visible = target != null
                && (hovered || System.currentTimeMillis() - lastSeen <= fadeTime.get() * 1000.0f);
        anim = Mathf.fast(anim, visible ? 1.0f : 0.0f, 16.0f);
        if (anim < 0.02f || target == null) {
            return;
        }

        float gs = (float) mc.getWindow().getGuiScale();
        float s = gs * scale.get();

        // Геометрия: голова и панель одной высоты, всё компактно.
        float side = 38.0f * s;
        float gap = 4.0f * s;
        float panelWidth = 118.0f * s;
        float radius = 7.0f * s;
        float totalWidth = side + gap + panelWidth;

        if (posX < 0.0f || posY < 0.0f) {
            posX = 22.0f * gs;
            posY = mc.getWindow().getGuiScaledHeight() * 0.3f;
        }

        // Перетаскивание ЛКМ: опрос состояния мыши — HUD-колбэк не получает события ввода.
        double mouseX = mc.mouseHandler.xpos() / gs;
        double mouseY = mc.mouseHandler.ypos() / gs;
        boolean pressed = GLFW.glfwGetMouseButton(mc.getWindow().getWindow(),
                GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        if (pressed && !dragging && anim > 0.5f
                && mouseX >= posX && mouseX <= posX + totalWidth
                && mouseY >= posY && mouseY <= posY + side) {
            dragging = true;
            grabX = (float) mouseX - posX;
            grabY = (float) mouseY - posY;
        }
        if (!pressed) {
            dragging = false;
        }
        if (dragging) {
            posX = Mth.clamp((float) mouseX - grabX, 2.0f,
                    mc.getWindow().getGuiScaledWidth() - totalWidth - 2.0f);
            posY = Mth.clamp((float) mouseY - grabY, 2.0f,
                    mc.getWindow().getGuiScaledHeight() - side - 2.0f);
        }

        float x = posX;
        float y = posY;
        float bodyX = x + side + gap;

        // Фон — градиент, тонированный цветом темы (тёмный, панели не сливаются с миром).
        int theme = ThemeManager.getColor();
        int bgTop = ColorUtils.multAlpha(ColorUtils.multDark(theme, 0.30f), anim);
        int bgBottom = ColorUtils.multAlpha(ColorUtils.multDark(theme, 0.14f), anim);
        int track = ColorUtils.multAlpha(ColorUtils.multDark(theme, 0.08f), anim);
        int ink = ColorUtils.multAlpha(ColorUtils.getColor(255, 255, 255, 255), anim);
        int outline = ColorUtils.multAlpha(ColorUtils.getColor(255, 255, 255, 255), 0.20f * anim);

        // Голова: подложка + лицо игрока, обрезанное по радиусу шейдером.
        GuiRender.roundedWithOutline(stack, x, y, side, side, 1.5f * s, bgTop, outline, radius);
        if (target instanceof AbstractClientPlayer player) {
            PlayerSkin skin = player.getSkin();
            float inset = 1.5f * s;
            float u0 = 8.0f / 64.0f;
            float u1 = 16.0f / 64.0f;
            float vTop = 1.0f - 16.0f / 64.0f;
            float vBottom = 1.0f - 8.0f / 64.0f;
            GuiRender.roundedTexture(stack, skin.texture(),
                    x + inset, y + inset, side - inset * 2.0f, side - inset * 2.0f,
                    radius - inset, u0, vTop, u1, vBottom, ColorUtils.getColor(255, 255, 255, (int) (255.0f * anim)));
        } else {
            drawCross(stack, x, y, side, ink, 14.0f * s);
        }

        // Панель информации: тот же размер по высоте, градиент сверху вниз.
        GuiRender.drawGradientRound(stack, bodyX, y, panelWidth, side, radius, bgTop, bgBottom, bgBottom, bgTop);
        GuiRender.roundedOutline(stack, bodyX, y, panelWidth, side, 1.5f * s, outline, radius);

        float textX = bodyX + 8.0f * s;
        float labelSize = 6.5f * s;

        String nick = clip(target.getName().getString(),
                panelWidth - 14.0f * s - GuiFonts.SF_BOLD.getWidth("Nick: ", labelSize), labelSize);
        GuiFonts.SF_BOLD.draw(stack, "Nick: " + nick, textX, y + 6.5f * s, ink, labelSize);

        displayHp = Mathf.fast(displayHp, target.getHealth(), 12.0f);
        float maxHealth = Math.max(1.0f, target.getMaxHealth());
        GuiFonts.SF_BOLD.draw(stack, "HP: " + String.format(java.util.Locale.US, "%.1f", target.getHealth()),
                textX, y + 16.0f * s, ink, labelSize);

        // Полоска здоровья — без подчёркиваний под текстом.
        float barX = bodyX + 6.0f * s;
        float barY = y + side - 12.0f * s;
        float barWidth = panelWidth - 12.0f * s;
        float barHeight = 7.0f * s;
        GuiRender.roundedRect(stack, barX, barY, barWidth, barHeight, 3.0f * s, track);
        float progress = Mth.clamp(displayHp / maxHealth, 0.0f, 1.0f);
        float fillWidth = (barWidth - 2.0f * s) * progress;
        if (fillWidth > 0.5f) {
            GuiRender.roundedRect(stack, barX + s, barY + s, fillWidth, barHeight - 2.0f * s,
                    2.0f * s, ink);
        }

        // Слоты: броня слева, предметы справа.
        float rowY = y + side + gap;
        float box = 20.0f * s;
        float boxGap = 3.0f * s;
        float boxRadius = 5.0f * s;

        if (showArmor.get()) {
            EquipmentSlot[] slots = {
                    EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
            };
            float currentX = x;
            for (EquipmentSlot slot : slots) {
                drawSlot(context, stack, currentX, rowY, box, target.getItemBySlot(slot),
                        bgTop, outline, ink, boxRadius, s);
                currentX += box + boxGap;
            }
        }

        if (showItems.get()) {
            float mainHandX = bodyX + panelWidth - box;
            float offHandX = mainHandX - box - boxGap;
            drawSlot(context, stack, offHandX, rowY, box, target.getItemBySlot(EquipmentSlot.OFFHAND),
                    bgTop, outline, ink, boxRadius, s);
            drawSlot(context, stack, mainHandX, rowY, box, target.getItemBySlot(EquipmentSlot.MAINHAND),
                    bgTop, outline, ink, boxRadius, s);
        }
    }

    private void drawSlot(GuiGraphics context, PoseStack stack, float x, float y, float box,
                          ItemStack item, int bg, int outline, int ink, float radius, float s) {
        GuiRender.roundedWithOutline(stack, x, y, box, box, s, bg, outline, radius);
        if (!item.isEmpty()) {
            float itemSize = box - 5.0f * s;
            float itemScale = itemSize / 16.0f;
            stack.pushPose();
            stack.translate(x + (box - itemSize) / 2.0f, y + (box - itemSize) / 2.0f, 0.0f);
            stack.scale(itemScale, itemScale, 1.0f);
            context.renderItem(item, 0, 0);
            stack.popPose();
        } else {
            drawCross(stack, x, y, box, ColorUtils.multAlpha(ink, 0.55f), 9.0f * s);
        }
    }

    // Крестик-заглушка, как «X» в референсе.
    private void drawCross(PoseStack stack, float x, float y, float box, int ink, float markSize) {
        String mark = "X";
        float markWidth = GuiFonts.SF_BOLD.getWidth(mark, markSize);
        float markHeight = GuiFonts.SF_BOLD.getHeight(mark, markSize);
        GuiFonts.SF_BOLD.draw(stack, mark, x + (box - markWidth) / 2.0f, y + (box - markHeight) / 2.0f,
                ink, markSize);
    }

    private String clip(String value, float maxWidth, float size) {
        if (GuiFonts.SF_BOLD.getWidth(value, size) <= maxWidth) {
            return value;
        }
        while (value.length() > 1 && GuiFonts.SF_BOLD.getWidth(value + "...", size) > maxWidth) {
            value = value.substring(0, value.length() - 1);
        }
        return value + "...";
    }
}

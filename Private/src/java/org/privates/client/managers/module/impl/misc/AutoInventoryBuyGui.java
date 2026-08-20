package org.privates.client.managers.module.impl.misc;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer2;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.util.*;

import static org.privates.client.api.interfaces.IMinecraft.mc;

public class AutoInventoryBuyGui extends Screen {
    private static final float UI_SCALE = 1.0f / 1.65f;

    private final AutoInventoryBuy module;
    private final List<AutoInventoryBuy.Entry> filtered = new ArrayList<>();
    private final Map<String, Float> hoverAnim = new HashMap<>();
    private final Map<String, Float> enabledAnim = new HashMap<>();
    private final Map<String, Float> chipHoverAnim = new HashMap<>();

    private final Map<String, Float> marqueeOffset = new HashMap<>();
    private final Map<String, Boolean> marqueeForward = new HashMap<>();
    private final Map<String, Long> marqueePauseUntil = new HashMap<>();
    private final Map<String, Long> marqueeLastUpdate = new HashMap<>();

    private float x;
    private float y;
    private final float panelW = s(586f);
    private final float panelH = s(500f);

    private boolean drag;
    private float dragX;
    private float dragY;

    private float scroll;
    private float targetScroll;
    private float enabledScroll;
    private float targetEnabledScroll;
    private String search = "";
    private boolean searchActive;
    private AutoInventoryBuy.Entry selected;
    private AutoInventoryBuy.Entry editPrice;
    private AutoInventoryBuy.Entry editLimit;
    private float openAnim;

    public AutoInventoryBuyGui(AutoInventoryBuy module) {
        super(new StringTextComponent("AutoInventoryBuy"));
        this.module = module;
    }

    @Override
    protected void init() {
        x = mc.getMainWindow().getScaledWidth() / 2f - panelW / 2f;
        y = mc.getMainWindow().getScaledHeight() / 2f - panelH / 2f;
        openAnim = 0f;
        applyFilter();
        if (selected == null && !filtered.isEmpty()) {
            selected = filtered.get(0);
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        openAnim = Mathf.fast(openAnim, 1f, 14);
        scroll = Mathf.fast(scroll, targetScroll, 18);
        enabledScroll = Mathf.fast(enabledScroll, targetEnabledScroll, 18);

        float oldY = y;
        y += (1f - openAnim) * s(20f);

        drawMainPanels(stack);
        drawHeaders(stack);
        drawItemsList(stack, mouseX, mouseY);
        drawAddedItems(stack, mouseX, mouseY);
        drawBottomButtons(stack, mouseX, mouseY);

        if (drag) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }

        y = oldY;
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void drawMainPanels(MatrixStack stack) {
        int bg = ColorUtil.getColor(28, 28, 33);
        int stroke = ColorUtil.getColor(30, 30, 35);
        int panel = ColorUtil.getColor(30, 30, 35);
        int panelStroke = ColorUtil.getColor(33, 33, 38);

        BlurRenderer2.registerRenderCall(() -> {
            RenderUtil.Rounded.roundedRect(stack, x, y, panelW, panelH, ColorUtil.multAlpha(bg, .95f * openAnim), Round.of(s(8)));
        });
        BlurRenderer2.draw((int) s(7));

        RenderUtil.Rounded.roundedWithOutline(stack, x, y, panelW, panelH, 1, ColorUtil.multAlpha(bg, openAnim), ColorUtil.multAlpha(stroke, openAnim), Round.of(s(8)));

        RenderUtil.Rounded.roundedWithOutline(stack, x + s(4), y + s(4), panelW - s(8), s(48), 1, ColorUtil.getColor(33, 33, 38), ColorUtil.getColor(39, 39, 44), Round.of(s(8)));

        RenderUtil.Rounded.roundedWithOutline(stack, x + s(4), y + s(55), s(174), s(412), 1, panel, panelStroke, Round.of(s(10)));
        RenderUtil.Rounded.roundedWithOutline(stack, x + s(181), y + s(55), s(401), s(412), 1, panel, panelStroke, Round.of(s(10)));

        RenderUtil.Rounded.roundedWithOutline(stack, x + s(8), y + s(84), s(166), s(379), 1, ColorUtil.getColor(23, 23, 28), ColorUtil.getColor(39, 39, 44), Round.of(s(7)));
        RenderUtil.Rounded.roundedWithOutline(stack, x + s(185), y + s(84), s(393), s(379), 1, ColorUtil.getColor(23, 23, 28), ColorUtil.getColor(39, 39, 44), Round.of(s(8)));
    }

    private void drawHeaders(MatrixStack stack) {
        int active = ColorUtil.getColor(188, 177, 255);

        String title = "AutoInventoryBuy";
        float iconSize = s(12);
        float titleSize = s(10.2f);
        float titleWidth = Fonts.SF_BOLD.getWidth(title, titleSize);
        float totalWidth = iconSize + s(6) + titleWidth;
        float startX = x + panelW / 2f - totalWidth / 2f;
        Fonts.icon.draw(stack, "M", startX, y + s(16), active, iconSize);
        Fonts.SF_BOLD.draw(stack, title, startX + iconSize + s(6), y + s(18), ColorUtil.WHITE, titleSize);

        Fonts.SF_BOLD.draw(stack, "Available items", x + s(17), y + s(61), ColorUtil.getColor(229, 229, 229), s(8.8f));
        Fonts.SF_BOLD.draw(stack, "Added items", x + s(194), y + s(61), ColorUtil.getColor(229, 229, 229), s(8.8f));

        float sx = x + s(14);
        float sy = y + s(18);
        float sw = s(112);
        float sh = s(16);
        RenderUtil.Rounded.roundedWithOutline(stack, sx, sy, sw, sh, 1, ColorUtil.getColor(23, 23, 28), ColorUtil.getColor(39, 39, 44), Round.of(s(4)));
        String txt = search.isEmpty() && !searchActive ? "Search" : search;
        int color = search.isEmpty() && !searchActive ? ColorUtil.getColor(139, 139, 144) : ColorUtil.WHITE;
        Fonts.SF_BOLD.draw(stack, txt, sx + s(4), sy + s(3.4f), color, s(8.2f));
        if (searchActive && System.currentTimeMillis() % 800 < 400) {
            float cx = sx + s(4) + Fonts.SF_BOLD.getWidth(search, s(8.2f));
            Fonts.SF_BOLD.draw(stack, "|", cx, sy + s(3.4f), ColorUtil.getColor(188, 177, 255), s(8.2f));
        }
    }

    private void drawItemsList(MatrixStack stack, int mouseX, int mouseY) {
        float itemX = x + s(14);
        float itemY = y + s(90);
        float itemW = s(154);
        float itemH = s(27);

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x + s(8), y + s(84), s(166), s(379));
        for (int i = 0; i < filtered.size(); i++) {
            AutoInventoryBuy.Entry entry = filtered.get(i);
            float yy = itemY + i * s(32) - scroll;
            if (yy + itemH < y + s(84) || yy > y + s(463)) {
                continue;
            }

            boolean hover = Mathf.isHover(mouseX, mouseY, itemX, yy, itemW, itemH);
            boolean selectedState = selected == entry;
            hoverAnim.put(entry.id, Mathf.fast(hoverAnim.getOrDefault(entry.id, 0f), hover ? 1f : 0f, 16));
            float h = hoverAnim.get(entry.id);
            int bg = selectedState ? ColorUtil.getColor(33, 33, 38) : ColorUtil.getColor(30, 30, 35);
            bg = ColorUtil.overCol(bg, ColorUtil.getColor(39, 39, 44), h * .8f);
            int stroke = selectedState ? ColorUtil.getColor(39, 39, 44) : ColorUtil.getColor(33, 33, 38);
            RenderUtil.Rounded.roundedWithOutline(stack, itemX, yy, itemW, itemH, 1, bg, stroke, Round.of(s(5)));

            renderEntryIcon(entry, itemX + s(3), yy + s(0));
            drawScrollingText(stack, trim(entry.name, 64), itemX + s(30), yy + s(7.6f), itemW - s(34), ColorUtil.WHITE, s(9.3f), "list_" + entry.id);
        }
        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void drawAddedItems(MatrixStack stack, int mouseX, int mouseY) {
        float listX = x + s(188);
        float listY = y + s(90);
        float itemW = s(168);
        float itemH = s(27);
        float settingsX = x + s(362);

        List<AutoInventoryBuy.Entry> enabled = module.getEntries().stream().filter(e -> e.enabled).toList();
        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(listX, y + s(84), itemW, s(379));
        for (int i = 0; i < enabled.size(); i++) {
            AutoInventoryBuy.Entry entry = enabled.get(i);
            float yy = listY + i * s(31) - enabledScroll;

            enabledAnim.put(entry.id, Mathf.fast(enabledAnim.getOrDefault(entry.id, 0f), 1f, 16));
            int bg = selected == entry ? ColorUtil.getColor(42, 42, 48) : ColorUtil.getColor(33, 33, 38);
            RenderUtil.Rounded.roundedWithOutline(stack, listX, yy, itemW, itemH, 1, bg, ColorUtil.getColor(39, 39, 44), Round.of(s(5)));
            renderEntryIcon(entry, listX + s(3), yy + s(0));
            drawScrollingText(stack, trim(entry.name, 64), listX + s(30), yy + s(7.6f), itemW - s(50), ColorUtil.WHITE, s(9.3f), "added_" + entry.id);
            float rx = listX + itemW - s(17);
            float ry = yy + s(7);
            RenderUtil.Rounded.roundedRect(stack, rx, ry, s(11), s(11), ColorUtil.getColor(175, 52, 52, 200), Round.of(s(3)));
            Fonts.SF_BOLD.drawCenter(stack, "x", rx + s(5f), ry + s(0.0f), ColorUtil.WHITE, s(8.5f));
        }
        ScissorExp.unset();
        ScissorExp.pop();

        RenderUtil.Rounded.roundedWithOutline(stack, settingsX, y + s(90), s(212), s(373), 1, ColorUtil.getColor(33, 33, 38), ColorUtil.getColor(39, 39, 44), Round.of(s(6)));
        drawSettingsCard(stack, mouseX, mouseY, settingsX, y + s(90), s(212), s(373));
    }

    private void drawSettingsCard(MatrixStack stack, int mouseX, int mouseY, float sx, float sy, float sw, float sh) {
        if (selected == null || !selected.enabled) {
            Fonts.SF_BOLD.drawCenter(stack, "Select added item", sx + sw / 2f, sy + s(22), ColorUtil.getColor(160, 160, 170), s(8.8f));
            return;
        }

        Fonts.SF_BOLD.draw(stack, "Item settings", sx + s(10), sy + s(8), ColorUtil.WHITE, s(8.8f));
        drawScrollingText(stack, trim(selected.name, 48), sx + s(10), sy + s(21), sw - s(20), ColorUtil.getColor(196, 196, 210), s(8f), "settings_name");

        Fonts.SF_BOLD.draw(stack, "Limit", sx + s(10), sy + s(43), ColorUtil.WHITE, s(8.2f));
        Fonts.SF_BOLD.draw(stack, "Max unit price", sx + s(10), sy + s(71), ColorUtil.WHITE, s(8.2f));

        RenderUtil.Rounded.roundedWithOutline(stack, sx + sw - s(30), sy + s(38), s(20), s(20), 1, ColorUtil.getColor(23, 23, 28), ColorUtil.getColor(33, 33, 38), Round.of(s(4)));
        Fonts.SF_BOLD.drawCenter(stack, String.valueOf(selected.targetCount), sx + sw - s(20), sy + s(44), ColorUtil.WHITE, s(8.2f));
        RenderUtil.Rounded.roundedWithOutline(stack, sx + s(84), sy + s(66), sw - s(94), s(20), 1, ColorUtil.getColor(23, 23, 28), ColorUtil.getColor(33, 33, 38), Round.of(s(4)));
        Fonts.SF_BOLD.draw(stack, selected.maxUnitPrice <= 0 ? "auto" : String.valueOf(selected.maxUnitPrice), sx + s(88), sy + s(72), ColorUtil.WHITE, s(8.2f));

        Fonts.SF_BOLD.draw(stack, "Enchant levels", sx + s(10), sy + s(100), ColorUtil.WHITE, s(8.2f));
        List<EnumChip> chips = getEnumChips(selected.id);
        int cols = 1;
        float chipW = sw - s(20);
        float chipH = s(20);
        for (int i = 0; i < chips.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            float cx = sx + s(10) + col * (chipW + s(4));
            float cy = sy + s(115) + row * (chipH + s(4));
            EnumChip chip = chips.get(i);
            boolean active = chip.value == chip.current;
            String animKey = "chip_" + selected.id + "_" + chip.key + "_" + chip.value;
            boolean hover = Mathf.isHover(mouseX, mouseY, cx, cy, chipW, chipH);
            float hoverValue = Mathf.fast(chipHoverAnim.getOrDefault(animKey, 0f), hover ? 1f : 0f, 15);
            chipHoverAnim.put(animKey, hoverValue);

            int cBg = active ? ColorUtil.getColor(188, 177, 255, 40) : ColorUtil.getColor(39, 39, 44);
            cBg = ColorUtil.overCol(cBg, ColorUtil.getColor(52, 52, 60), hoverValue * 0.7f);
            int cStroke = active ? ColorUtil.getColor(188, 177, 255) : ColorUtil.getColor(50, 50, 55);
            int cText = active ? ColorUtil.WHITE : ColorUtil.getColor(139, 139, 144);
            RenderUtil.Rounded.roundedWithOutline(stack, cx, cy, chipW, chipH, 1, cBg, cStroke, Round.of(s(4)));
            drawScrollingText(stack, chip.text, cx + s(6), cy + s(6), chipW - s(12), cText, s(7.5f), "chip_txt_" + selected.id + "_" + i);
        }
    }

    private void drawBottomButtons(MatrixStack stack, int mouseX, int mouseY) {
        // Intentionally empty: adding/removing is done directly in item lists.
    }

    private void drawButton(MatrixStack stack, float bx, float by, float bw, float bh, String text, String icon, int mouseX, int mouseY) {
        boolean hover = Mathf.isHover(mouseX, mouseY, bx, by, bw, bh);
        int bg = hover ? ColorUtil.getColor(39, 39, 44) : ColorUtil.getColor(33, 33, 38);
        RenderUtil.Rounded.roundedWithOutline(stack, bx, by, bw, bh, 1, bg, ColorUtil.getColor(39, 39, 44), Round.of(s(5)));
        Fonts.icon.draw(stack, icon, bx + s(8), by + s(13), ColorUtil.getColor(188, 177, 255), s(7));
        Fonts.SF_BOLD.draw(stack, text, bx + s(16), by + s(10), ColorUtil.WHITE, s(6.5f));
    }

    private List<EnumChip> getEnumChips(String id) {
        List<EnumChip> chips = new ArrayList<>();
        if ("pickaxe_regular".equals(id)) {
            int cur = module.getEnchantSelection("pickaxe_efficiency", 5, 3, 10);
            for (int i = 3; i <= 10; i++) {
                chips.add(new EnumChip("Efficiency " + i, "pickaxe_efficiency", i, cur, 3, 10));
            }
        } else if ("sword_regular".equals(id)) {
            int sharpCur = module.getEnchantSelection("sword_sharpness", 5, 5, 7);
            for (int i = 5; i <= 7; i++) {
                chips.add(new EnumChip("Sharpness " + i, "sword_sharpness", i, sharpCur, 5, 7));
            }
            int fireCur = module.getEnchantSelection("sword_fire", 1, 1, 3);
            for (int i = 1; i <= 3; i++) {
                chips.add(new EnumChip("Fire Aspect " + i, "sword_fire", i, fireCur, 1, 3));
            }
        } else if ("helmet_regular".equals(id) || "chest_regular".equals(id) || "legs_regular".equals(id)) {
            int prot = module.getEnchantSelection("armor_protection", 4, 4, 5);
            int unb = module.getEnchantSelection("armor_unbreaking", 3, 2, 5);
            int mending = module.getEnchantSelection("armor_mending", 1, 0, 1);
            chips.add(new EnumChip("Protection IV", "armor_protection", 4, prot, 4, 5));
            chips.add(new EnumChip("Protection V", "armor_protection", 5, prot, 4, 5));
            chips.add(new EnumChip("Unbreaking II", "armor_unbreaking", 2, unb, 2, 5));
            chips.add(new EnumChip("Unbreaking III", "armor_unbreaking", 3, unb, 2, 5));
            chips.add(new EnumChip("Unbreaking IV", "armor_unbreaking", 4, unb, 2, 5));
            chips.add(new EnumChip("Unbreaking V", "armor_unbreaking", 5, unb, 2, 5));
            chips.add(new EnumChip("Mending Off", "armor_mending", 0, mending, 0, 1));
            chips.add(new EnumChip("Mending On", "armor_mending", 1, mending, 0, 1));
        } else if ("boots_regular".equals(id)) {
            int prot = module.getEnchantSelection("armor_protection", 4, 4, 5);
            int unb = module.getEnchantSelection("armor_unbreaking", 3, 2, 5);
            int mending = module.getEnchantSelection("armor_mending", 1, 0, 1);
            int depth = module.getEnchantSelection("boots_depth", 3, 0, 3);
            chips.add(new EnumChip("Protection IV", "armor_protection", 4, prot, 4, 5));
            chips.add(new EnumChip("Protection V", "armor_protection", 5, prot, 4, 5));
            chips.add(new EnumChip("Unbreaking II", "armor_unbreaking", 2, unb, 2, 5));
            chips.add(new EnumChip("Unbreaking III", "armor_unbreaking", 3, unb, 2, 5));
            chips.add(new EnumChip("Unbreaking IV", "armor_unbreaking", 4, unb, 2, 5));
            chips.add(new EnumChip("Unbreaking V", "armor_unbreaking", 5, unb, 2, 5));
            chips.add(new EnumChip("Mending Off", "armor_mending", 0, mending, 0, 1));
            chips.add(new EnumChip("Mending On", "armor_mending", 1, mending, 0, 1));
            chips.add(new EnumChip("Depth Off", "boots_depth", 0, depth, 0, 3));
            chips.add(new EnumChip("Depth I", "boots_depth", 1, depth, 0, 3));
            chips.add(new EnumChip("Depth II", "boots_depth", 2, depth, 0, 3));
            chips.add(new EnumChip("Depth III", "boots_depth", 3, depth, 0, 3));
        } else if ("bow".equals(id)) {
            int cur = module.getEnchantSelection("bow_power", 5, 1, 5);
            for (int i = 1; i <= 5; i++) {
                chips.add(new EnumChip("Power " + i, "bow_power", i, cur, 1, 5));
            }
        } else if ("crossbow".equals(id)) {
            int cur = module.getEnchantSelection("crossbow_quick", 3, 1, 5);
            for (int i = 1; i <= 5; i++) {
                chips.add(new EnumChip("Quick Charge " + i, "crossbow_quick", i, cur, 1, 5));
            }
        }
        return chips;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float searchX = x + s(14);
        if (button == 0 && Mathf.isHover(mouseX, mouseY, searchX, y + s(18), s(112), s(16))) {
            searchActive = true;
            editLimit = null;
            editPrice = null;
            return true;
        }
        searchActive = false;

        if (button == 0 && Mathf.isHover(mouseX, mouseY, x, y, panelW, s(54))) {
            drag = true;
            dragX = (float) (mouseX - x);
            dragY = (float) (mouseY - y);
            return true;
        }

        float itemX = x + s(14);
        float itemY = y + s(90);
        for (int i = 0; i < filtered.size(); i++) {
            float yy = itemY + i * s(32) - scroll;
            if (Mathf.isHover(mouseX, mouseY, itemX, yy, s(154), s(27))) {
                selected = filtered.get(i);
                selected.enabled = true;
                module.saveData();
                return true;
            }
        }

        List<AutoInventoryBuy.Entry> enabled = module.getEntries().stream().filter(e -> e.enabled).toList();
        float enabledX = x + s(188);
        float enabledY = y + s(90);
        float enabledW = s(168);
        for (int i = 0; i < enabled.size(); i++) {
            AutoInventoryBuy.Entry entry = enabled.get(i);
            float yy = enabledY + i * s(31) - enabledScroll;
            if (!Mathf.isHover(mouseX, mouseY, enabledX, yy, enabledW, s(27))) {
                continue;
            }
            float rx = enabledX + enabledW - s(17);
            float ry = yy + s(7);
            if (Mathf.isHover(mouseX, mouseY, rx, ry, s(11), s(11))) {
                entry.enabled = false;
                if (selected == entry) {
                    selected = null;
                    editLimit = null;
                    editPrice = null;
                }
            } else {
                selected = entry;
            }
            module.saveData();
            return true;
        }

        if (selected != null && selected.enabled && button == 0) {
            float sx = x + s(362);
            float sy = y + s(90);
            float sw = s(212);
            if (Mathf.isHover(mouseX, mouseY, sx + sw - s(30), sy + s(38), s(20), s(20))) {
                editLimit = selected;
                editPrice = null;
                return true;
            }
            if (Mathf.isHover(mouseX, mouseY, sx + s(84), sy + s(66), sw - s(94), s(20))) {
                editPrice = selected;
                editLimit = null;
                return true;
            }

            List<EnumChip> chips = getEnumChips(selected.id);
            int cols = 1;
            float chipW = sw - s(20);
            float chipH = s(20);
            for (int i = 0; i < chips.size(); i++) {
                int col = i % cols;
                int row = i / cols;
                float cx = sx + s(10) + col * (chipW + s(4));
                float cy = sy + s(115) + row * (chipH + s(4));
                if (Mathf.isHover(mouseX, mouseY, cx, cy, chipW, chipH)) {
                    EnumChip chip = chips.get(i);
                    module.setEnchantSelection(chip.key, chip.value, chip.min, chip.max);
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            drag = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        boolean overAvailable = Mathf.isHover(mouseX, mouseY, x + s(8), y + s(84), s(166), s(379));
        boolean overAdded = Mathf.isHover(mouseX, mouseY, x + s(188), y + s(84), s(168), s(379));

        if (overAdded) {
            targetEnabledScroll -= (float) (delta * s(24));
            float maxEnabled = Math.max(0, module.getEntries().stream().filter(e -> e.enabled).count() * s(31) - s(350));
            if (targetEnabledScroll < 0) {
                targetEnabledScroll = 0;
            }
            if (targetEnabledScroll > maxEnabled) {
                targetEnabledScroll = maxEnabled;
            }
            return true;
        }

        if (overAvailable) {
            targetScroll -= (float) (delta * s(24));
            float max = Math.max(0, filtered.size() * s(32) - s(360));
            if (targetScroll < 0) {
                targetScroll = 0;
            }
            if (targetScroll > max) {
                targetScroll = max;
            }
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchActive) {
            if (!Character.isISOControl(codePoint)) {
                search += codePoint;
                applyFilter();
            }
            return true;
        }
        if (editLimit != null && Character.isDigit(codePoint)) {
            String v = String.valueOf(editLimit.targetCount) + codePoint;
            if (v.length() <= 5) {
                editLimit.targetCount = Integer.parseInt(v);
                module.saveData();
            }
            return true;
        }
        if (editPrice != null && Character.isDigit(codePoint)) {
            String base = editPrice.maxUnitPrice <= 0 ? "" : String.valueOf(editPrice.maxUnitPrice);
            String v = base + codePoint;
            if (v.length() <= 8) {
                editPrice.maxUnitPrice = Integer.parseInt(v);
                module.saveData();
            }
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (searchActive && !search.isEmpty()) {
                search = search.substring(0, search.length() - 1);
                applyFilter();
                return true;
            }
            if (editLimit != null) {
                String v = String.valueOf(editLimit.targetCount);
                editLimit.targetCount = v.length() <= 1 ? 0 : Integer.parseInt(v.substring(0, v.length() - 1));
                module.saveData();
                return true;
            }
            if (editPrice != null) {
                String v = String.valueOf(editPrice.maxUnitPrice);
                editPrice.maxUnitPrice = v.length() <= 1 ? 0 : Integer.parseInt(v.substring(0, v.length() - 1));
                module.saveData();
                return true;
            }
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (searchActive || editLimit != null || editPrice != null) {
                searchActive = false;
                editLimit = null;
                editPrice = null;
                return true;
            }
            module.saveData();
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void applyFilter() {
        filtered.clear();
        String q = search.toLowerCase(Locale.ROOT);
        for (AutoInventoryBuy.Entry e : module.getEntries()) {
            if (q.isEmpty() || e.name.toLowerCase(Locale.ROOT).contains(q) || e.searchQuery.toLowerCase(Locale.ROOT).contains(q)) {
                filtered.add(e);
            }
        }
    }

    private String trim(String text, int max) {
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, Math.max(1, max - 3)) + "...";
    }

    private void renderEntryIcon(AutoInventoryBuy.Entry entry, float x, float y) {
        Item icon = resolveEntryIcon(entry);
        if (icon == null || icon == Items.AIR) {
            return;
        }
        RenderHelper.enableStandardItemLighting();
        mc.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(icon), (int) x, (int) y);
        RenderHelper.disableStandardItemLighting();
    }

    private Item resolveEntryIcon(AutoInventoryBuy.Entry entry) {
        if (entry.item != null && entry.item != Items.AIR) {
            return entry.item;
        }

        String name = entry.name == null ? "" : entry.name.toLowerCase(Locale.ROOT);
        if (name.contains("талисман") || name.contains("рўр°р»")) {
            return Items.TOTEM_OF_UNDYING;
        }
        if (name.contains("сфера") || name.contains("с„рµс")) {
            return Items.PLAYER_HEAD;
        }
        if (name.contains("зелье") || name.contains("р·рµр»")) {
            return Items.SPLASH_POTION;
        }
        return Items.AIR;
    }

    private float s(float value) {
        return value * UI_SCALE;
    }

    private void drawScrollingText(MatrixStack stack, String text, float x, float y, float availableWidth, int color, float size, String key) {
        float textWidth = Fonts.SF_BOLD.getWidth(text, size);
        float offset = marqueeOffset.getOrDefault(key, 0f);
        boolean forward = marqueeForward.getOrDefault(key, true);
        long pauseUntil = marqueePauseUntil.getOrDefault(key, 0L);
        long lastUpdate = marqueeLastUpdate.getOrDefault(key, 0L);

        if (textWidth > availableWidth) {
            long now = System.currentTimeMillis();
            if (now > pauseUntil && now - lastUpdate > 16L) {
                float speed = s(0.55f);
                float minX = -(textWidth - availableWidth);
                if (forward) {
                    offset -= speed;
                    if (offset <= minX) {
                        offset = minX;
                        forward = false;
                        pauseUntil = now + 900L;
                    }
                } else {
                    offset += speed;
                    if (offset >= 0f) {
                        offset = 0f;
                        forward = true;
                        pauseUntil = now + 900L;
                    }
                }
                lastUpdate = now;
            }
        } else {
            offset = 0f;
            forward = true;
        }

        marqueeOffset.put(key, offset);
        marqueeForward.put(key, forward);
        marqueePauseUntil.put(key, pauseUntil);
        marqueeLastUpdate.put(key, lastUpdate);

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, y - s(2f), availableWidth, size + s(4f));
        Fonts.SF_BOLD.draw(stack, text, x + offset, y, color, size);
        ScissorExp.unset();
        ScissorExp.pop();
    }

    private record EnumChip(String text, String key, int value, int current, int min, int max) {
    }
}

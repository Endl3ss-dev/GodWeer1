package tech.lightclient.ui.gui.elements;

import org.lwjgl.glfw.GLFW;
import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import tech.lightclient.autobuy.AutoBuyConfig;
import tech.lightclient.autobuy.ItemEntry;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.utils.render.CursorHelper;
import tech.lightclient.client.utils.render.animations.Easings;
import tech.lightclient.client.utils.render.color.basic.ColorUtils;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.ui.gui.ItemTextureCache;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * AutoBuy page for the compact ClickGUI. The automation itself remains owned by
 * {@link tech.lightclient.modules.impl.utils.AutoBuyFunction}; this page edits
 * the persisted item list and limits.
 */
public final class CsAutoBuyWidget {
    private static final float WIDTH = 720f;
    private static final float HEIGHT = 600f;
    private static final int COLUMNS = 2;
    private static final float PADDING = 18f;
    private static final float HEADER_HEIGHT = 108f;
    private static final float GAP = 14f;
    private static final float CARD_HEIGHT = 116f;
    private static final float INPUT_HEIGHT = 30f;

    private static final float CELL_GAP = 6f;
    private static final float RIGHT_RATIO = 0.4f;
    private static final float ITEM_GRID_ENTRANCE_DISTANCE = 324.576f;

    private final Map<ItemEntry, TextInputField> priceFields = new IdentityHashMap<>();
    private final Map<ItemEntry, ItemStack> itemStacks = new IdentityHashMap<>();
    private float scroll;
    private float targetScroll;
    private float listTop;
    private float listBottom;
    private float lastX;
    private float lastY;
    private int selectedIndex = 0;
    /** Entrance progress supplied by ClickGUI (0 = hidden, 1 = fully shown). */
    private float entranceProgress = 1f;
    // grid geometry (filled in draw, used by click/scroll)
    private float gridLeft, gridW, cellSize;
    private int gridCols = 1;
    // right-panel geometry
    private float rpInputX, rpInputY, rpInputW, rpInputH, rpToggleX, rpToggleY, rpToggleW, rpToggleH;

    public float getWidth() { return WIDTH; }
    public float getHeight() { return HEIGHT; }

    public void setEntranceProgress(float progress) {
        entranceProgress = clamp(progress, 0f, 1f);
    }

    public void draw(Renderer2D r, float x, float y, float alpha, int mouseX, int mouseY,
                     float screenW, float screenH) {
        lastX = x;
        lastY = y;
        float a = Math.max(0f, Math.min(1f, alpha / 255f));
        int bright = ColorUtils.rgba(228, 228, 234, (int) (255 * a));
        int dim = ColorUtils.rgba(161, 161, 170, (int) (255 * a));
        int faint = ColorUtils.rgba(113, 113, 122, (int) (255 * a));
        int accent = withAlpha(ClientColors.ICON.getRGB(), (int) (255 * a));

        List<ItemEntry> entries = AutoBuyConfig.cfg().entries;
        if (selectedIndex >= entries.size()) selectedIndex = entries.isEmpty() ? -1 : entries.size() - 1;

        // Strictly staged entrance: item cards, then detail panel, then title.
        // Ease each stage independently so every section has a slow start,
        // a fast middle and a soft, gradual finish instead of a linear snap.
        float itemProgress = Easings.EASE_IN_OUT_CUBIC.ease(
                clamp(entranceProgress / 0.42f, 0f, 1f));
        float panelProgress = Easings.EASE_IN_OUT_CUBIC.ease(
                clamp((entranceProgress - 0.42f) / 0.30f, 0f, 1f));
        float titleProgress = Easings.EASE_IN_OUT_CUBIC.ease(
                clamp((entranceProgress - 0.72f) / 0.28f, 0f, 1f));

        // The category title is deliberately the last element to enter.
        if (titleProgress > 0.001f) {
            r.pushAlpha(titleProgress);
            r.pushTranslation(0f, -24f * (1f - titleProgress));
            r.text(FontRegistry.DELTA_CLICKGUI_MEDIUM, x + PADDING, y + 30f, 20f, "AutoBuy", bright);
            r.popTransform();
            r.popAlpha();
        }

        // ---- layout split ----
        float rightW = WIDTH * RIGHT_RATIO;
        float rightX = x + WIDTH - rightW;
        gridLeft = x + PADDING;
        float gridRight = rightX - PADDING;
        gridW = Math.max(0f, gridRight - gridLeft);
        listTop = y + 46f;
        listBottom = y + HEIGHT - PADDING;
        float listHeight = listBottom - listTop;

        gridCols = Math.max(1, Math.round(gridW / 52f));
        cellSize = (gridW - CELL_GAP * (gridCols - 1)) / gridCols;
        float cellFull = cellSize + CELL_GAP;
        int rows = (entries.size() + gridCols - 1) / gridCols;
        float contentHeight = rows == 0 ? 0f : rows * cellFull - CELL_GAP;
        float maxScroll = Math.max(0f, contentHeight - listHeight);
        targetScroll = clamp(targetScroll, 0f, maxScroll);
        scroll += (targetScroll - scroll) * 0.18f;
        if (Math.abs(targetScroll - scroll) < 0.1f) scroll = targetScroll;

        // ---- left: item icon grid ----
        r.pushClipRect((int) gridLeft, (int) listTop, (int) Math.ceil(gridW), (int) Math.ceil(listHeight));
        r.pushTranslation(0f, -ITEM_GRID_ENTRANCE_DISTANCE * (1f - itemProgress));
        for (int i = 0; i < entries.size(); i++) {
            ItemEntry entry = entries.get(i);
            int col = i % gridCols;
            int row = i / gridCols;
            float cx = gridLeft + col * cellFull;
            float cy = listTop + row * cellFull - scroll;
            if (cy + cellSize < listTop || cy > listBottom) continue;
            boolean hovered = mouseX >= cx && mouseX <= cx + cellSize && mouseY >= cy && mouseY <= cy + cellSize;
            if (hovered) CursorHelper.setHand();
            boolean selected = i == selectedIndex;

            if (tech.lightclient.modules.impl.render.ClickGui.isSimpleBlurEnabled()) {
                r.prepareBlur(tech.lightclient.modules.impl.render.ClickGui.getSimpleBlurStrength());
                r.blur(cx, cy, cellSize, cellSize, 7f, a);
            }
            // enabled = accent-tinted glass; hover a touch brighter
            int base = entry.enabled ? withAlpha(ClientColors.ICON.getRGB(), (int) (46 * a))
                    : ColorUtils.rgba(255, 255, 255, (int) ((hovered ? 16 : 9) * a));
            r.rect(cx, cy, cellSize, cellSize, 7f, base);
            // accent outline for items included in the purchase
            if (entry.enabled) {
                r.rectOutline(cx, cy, cellSize, cellSize, 7f, withAlpha(ClientColors.ICON.getRGB(), (int) (200 * a)), 1f);
            }
            if (selected) {
                r.rect(cx, cy, cellSize, cellSize, 7f, withAlpha(ClientColors.ICON.getRGB(), (int) (30 * a)));
                r.rectOutline(cx, cy, cellSize, cellSize, 7f, withAlpha(ClientColors.ICON.getRGB(), (int) (245 * a)), 1.6f);
            }
            float ipad = cellSize * 0.2f;
            drawItemIcon(r, entry, cx + ipad, cy + ipad, cellSize - ipad * 2f, a);
        }
        r.popTransform();
        r.popClipRect();

        // ---- right: selected item detail ----
        float rpX = rightX;
        float rpY = listTop;                       // top aligned with the first item row
        float rpW = rightW - PADDING;
        float rpH = listBottom - listTop;
        // strong (blurred) background only over the main content block, clipped at its bottom
        float mainH = Math.min(rpH, 150f);
        r.pushClipRect((int) rpX, (int) rpY, (int) Math.ceil(rpW), (int) Math.ceil(mainH));
        r.pushAlpha(panelProgress);
        r.pushTranslation(0f, -24f * (1f - panelProgress));
        if (tech.lightclient.modules.impl.render.ClickGui.isSimpleBlurEnabled()) {
            r.prepareBlur(tech.lightclient.modules.impl.render.ClickGui.getSimpleBlurStrength());
            r.blur(rpX, rpY, rpW, mainH, 8f, a);
        }
        r.rect(rpX, rpY, rpW, mainH, 8f, ColorUtils.rgba(255, 255, 255, (int) (10 * a)));
        r.rectOutline(rpX, rpY, rpW, mainH, 8f, ColorUtils.rgba(255, 255, 255, (int) (24 * a)), 1f);

        if (selectedIndex < 0 || selectedIndex >= entries.size()) {
            r.text(FontRegistry.DELTA_CLICKGUI, rpX + 14f, rpY + mainH * 0.5f, 12f, "Выберите предмет", dim);
            rpInputW = 0f;
            rpToggleW = 0f;
            r.popTransform();
            r.popAlpha();
            r.popClipRect();
            return;
        }

        ItemEntry sel = entries.get(selectedIndex);
        float inX = rpX + 14f;
        // header: icon + name
        r.rect(inX, rpY + 12f, 34f, 34f, 7f, ColorUtils.rgba(8, 9, 12, (int) (150 * a)));
        drawItemIcon(r, sel, inX + 5f, rpY + 17f, 24f, a);
        r.text(FontRegistry.DELTA_CLICKGUI_MEDIUM, inX + 44f, rpY + 25f, 13f, trim(sel.label(), 18), bright);
        r.text(FontRegistry.DELTA_CLICKGUI, inX + 44f, rpY + 40f, 10f, trim(sel.itemId, 24), faint);

        // price
        float innerW = rpW - 28f;
        rpInputH = 24f;
        r.text(FontRegistry.DELTA_CLICKGUI, inX, rpY + 66f, 11f, "Макс. цена покупки", dim);
        rpInputX = inX;
        rpInputY = rpY + 73f;
        rpInputW = innerW;
        TextInputField field = priceField(sel);
        r.rect(rpInputX, rpInputY, rpInputW, rpInputH, 6f, ColorUtils.rgba(10, 11, 15, (int) (170 * a)));
        r.rectOutline(rpInputX, rpInputY, rpInputW, rpInputH, 6f,
                field.isFocused() ? accent : ColorUtils.rgba(255, 255, 255, (int) (26 * a)), 1f);
        field.render(r, rpInputX, rpInputY, rpInputW, rpInputH, a);

        // enable toggle switch
        r.text(FontRegistry.DELTA_CLICKGUI, inX, rpY + 118f, 11f, "Покупать этот предмет", dim);
        rpToggleW = 30f;
        rpToggleH = 16f;
        rpToggleX = rpX + rpW - 14f - rpToggleW;
        rpToggleY = rpY + 111f;
        int track = sel.enabled ? accent : ColorUtils.rgba(70, 72, 80, (int) (200 * a));
        r.rect(rpToggleX, rpToggleY, rpToggleW, rpToggleH, rpToggleH * 0.5f, track);
        float knob = rpToggleH - 4f;
        float kx = sel.enabled ? rpToggleX + rpToggleW - knob - 2f : rpToggleX + 2f;
        r.rect(kx, rpToggleY + 2f, knob, knob, knob * 0.5f, ColorUtils.rgba(245, 246, 250, (int) (255 * a)));
        r.popTransform();
        r.popAlpha();
        r.popClipRect();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, float x, float y,
                                float screenW, float screenH) {
        List<ItemEntry> entries = AutoBuyConfig.cfg().entries;
        // right panel: price input / enable toggle
        if (rpInputW > 0f && mouseX >= rpInputX && mouseX <= rpInputX + rpInputW
                && mouseY >= rpInputY && mouseY <= rpInputY + rpInputH
                && selectedIndex >= 0 && selectedIndex < entries.size()) {
            priceField(entries.get(selectedIndex)).mouseClicked(mouseX, mouseY, button, rpInputX, rpInputY, rpInputW, rpInputH);
            return true;
        }
        if (rpToggleW > 0f && button == GLFW.GLFW_MOUSE_BUTTON_LEFT
                && mouseX >= rpToggleX && mouseX <= rpToggleX + rpToggleW
                && mouseY >= rpToggleY && mouseY <= rpToggleY + rpToggleH
                && selectedIndex >= 0 && selectedIndex < entries.size()) {
            ItemEntry sel = entries.get(selectedIndex);
            sel.enabled = !sel.enabled;
            AutoBuyConfig.save();
            return true;
        }
        // left grid selection
        if (mouseY < listTop || mouseY > listBottom || mouseX < gridLeft || mouseX > gridLeft + gridW) return false;
        float cellFull = cellSize + CELL_GAP;
        int col = (int) ((mouseX - gridLeft) / cellFull);
        int row = (int) ((mouseY - listTop + scroll) / cellFull);
        if (col < 0 || col >= gridCols || row < 0) return true;
        int index = row * gridCols + col;
        if (index >= 0 && index < entries.size()) {
            selectedIndex = index;
        }
        return true;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount, float x, float y,
                                 float screenW, float screenH) {
        if (mouseX < gridLeft || mouseX > gridLeft + gridW || mouseY < listTop || mouseY > listBottom) return false;
        targetScroll -= (float) amount * 42f;
        return true;
    }

    public boolean keyPressed(int keyCode) {
        TextInputField field = TextInputField.focusedField;
        return field != null && field.keyPressed(keyCode, 0);
    }

    public boolean charTyped(char c) {
        TextInputField field = TextInputField.focusedField;
        return field != null && field.charTyped(c);
    }

    public void mouseReleased(int button) {
        if (TextInputField.focusedField != null) TextInputField.focusedField.mouseReleased(button);
    }

    public void tickDrag(double mouseX, double mouseY) {
        if (TextInputField.focusedField != null) TextInputField.focusedField.tickDrag(mouseX, mouseY);
    }

    private TextInputField priceField(ItemEntry entry) {
        TextInputField field = priceFields.get(entry);
        if (field == null) {
            TextInputField created = new TextInputField().setFont(FontRegistry.DELTA_CLICKGUI).setFontSize(13f)
                    .setPadding(9f, 0f).setMaxLength(12).setCharFilter(c -> Character.isDigit(c));
            ItemEntry target = entry;
            created.setText(String.valueOf(entry.maxUnitPrice));
            created.setOnChange(() -> savePrice(target, created.getText()));
            created.setOnSubmit(AutoBuyConfig::save);
            field = created;
            priceFields.put(entry, created);
        } else if (!field.isFocused() && !field.getText().equals(String.valueOf(entry.maxUnitPrice))) {
            field.setText(String.valueOf(entry.maxUnitPrice));
        }
        return field;
    }

    private void drawItemIcon(Renderer2D r, ItemEntry entry, float x, float y, float size, float alpha) {
        ItemStack stack = itemStack(entry);
        if (stack.isEmpty()) {
            r.text(FontRegistry.DELTA_CLICKGUI_MEDIUM, x + 11f, y + 23f, 16f, "?",
                    ColorUtils.rgba(190, 190, 198, (int) (220 * alpha)));
            return;
        }

        float[] transform = r.getTransformStack().current();
        float absX = x;
        float absY = y;
        float absSize = size;
        if (transform != null && transform.length >= 6) {
            float scaleX = (float) Math.sqrt(transform[0] * transform[0] + transform[3] * transform[3]);
            float scaleY = (float) Math.sqrt(transform[1] * transform[1] + transform[4] * transform[4]);
            absX = transform[0] * x + transform[1] * y + transform[2];
            absY = transform[3] * x + transform[4] * y + transform[5];
            absSize = size * Math.min(scaleX, scaleY);
        }
        ItemTextureCache cache = ItemTextureCache.get();
        cache.requestIcon(stack, absX, absY, absSize);
        cache.drawCaptured(r, absX, absY, absSize, x, y, size,
                ColorUtils.rgba(255, 255, 255, (int) (255 * alpha)));
    }

    private ItemStack itemStack(ItemEntry entry) {
        ItemStack cached = itemStacks.get(entry);
        if (cached != null) return cached;

        Item item = Items.BARRIER;
        try {
            if (entry.itemId != null) {
                Item resolved = Registries.ITEM.get(Identifier.of(entry.itemId));
                if (resolved != null && resolved != Items.AIR) item = resolved;
            }
        } catch (Throwable ignored) {
        }
        ItemStack stack = new ItemStack(item);
        String texture = headTexture(entry);
        if (texture != null && item == Items.PLAYER_HEAD) {
            PropertyMap properties = new PropertyMap(ImmutableMultimap.of(
                    "textures", new Property("textures", texture)));
            GameProfile profile = new GameProfile(
                    java.util.UUID.nameUUIDFromBytes(("autobuy:" + texture).getBytes(java.nio.charset.StandardCharsets.UTF_8)),
                    "AutoBuy",
                    properties);
            stack.set(DataComponentTypes.PROFILE, ProfileComponent.ofStatic(profile));
        }
        itemStacks.put(entry, stack);
        return stack;
    }

    private static String headTexture(ItemEntry entry) {
        StringBuilder value = new StringBuilder();
        if (entry.displayName != null) value.append(entry.displayName).append(' ');
        if (entry.searchQuery != null) value.append(entry.searchQuery).append(' ');
        if (entry.loreKeyword != null) value.append(entry.loreKeyword);
        String name = value.toString().toLowerCase(Locale.ROOT);
        if (name.contains("armortality") || name.contains("армортал")) {
            return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE2MmI5ZGU2YTI2Yjg2ODY5Y2EyMmVhNDBmMWJkZTgwYTA0MzBhNTQ1NDdiZWNjZThmZGE4NzA3Nzc3MjU4ZiJ9fX0=";
        }
        if (name.contains("immortality") || name.contains("иммортал")) {
            return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODNlZDRjZTIzOTMzZTY2ZTA0ZGYxNjA3MDY0NGY3NTk5ZWViNTUzMDdmN2VhZmU4ZDkyZjQwZmIzNTIwODYzYyJ9fX0=";
        }
        if (name.contains("cerber") || name.contains("цербер")) {
            return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA5NWE3ZmQ5MGRhYTFiYmU3MDY5MDg5NzQwZTA1ZDBiZmM2NjI5NmVlM2M0MGVlNzFhNGUwYTY2MTZiMmJiYyJ9fX0=";
        }
        return null;
    }

    private static void savePrice(ItemEntry entry, String text) {
        try {
            entry.maxUnitPrice = Math.max(0L, Long.parseLong(text));
            AutoBuyConfig.save();
        } catch (NumberFormatException ignored) {
        }
    }

    private static String trim(String text, int max) {
        if (text == null) return "";
        text = text.replace('\n', ' ').replace('\r', ' ');
        return text.length() <= max ? text : text.substring(0, Math.max(0, max - 3)) + "...";
    }

    private static int withAlpha(int color, int alpha) {
        return (Math.max(0, Math.min(255, alpha)) << 24) | (color & 0x00FFFFFF);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}

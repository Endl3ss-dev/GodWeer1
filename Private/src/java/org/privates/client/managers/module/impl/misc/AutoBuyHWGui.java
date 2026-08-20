package org.privates.client.managers.module.impl.misc;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.autobuy.AutoBuyItem;
import org.privates.client.utils.autobuy.AutoBuyManager;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.text.TextAlign;
import org.privates.client.utils.render.text.TextBox;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.privates.client.api.interfaces.IMinecraft.mc;

public class AutoBuyHWGui extends Screen {

    private final AutoBuyHW autoBuyModule;
    private List<AutoBuyItem> displayedItems;
    static final Map<AutoBuyItem, TextBox> priceFields = new LinkedHashMap<>();
    public static final Map<AutoBuyItem, Boolean> itemEnabled = new LinkedHashMap<>();

    private final Map<AutoBuyItem, Boolean> priceFieldFocused = new LinkedHashMap<>();
    private final Map<AutoBuyItem, Animation> priceFieldAnimations = new LinkedHashMap<>();
    private final Map<AutoBuyItem, Animation> itemHoverAnimations = new LinkedHashMap<>();
    private final Map<String, Animation> categoryHoverAnimations = new LinkedHashMap<>();
    private final Map<AutoBuyItem, Animation> itemBackgroundAnimations = new LinkedHashMap<>();

    private TextBox searchField;
    private String selectedCategory = "Все";
    private final Map<String, Item> categories = new LinkedHashMap<>();
    private boolean hwMode = false;

    private final Animation openAnimation = new Animation();
    private final Animation closeAnimation = new Animation();
    private boolean closing = false;
    private final String[] titleTexts = {"AutoBuyGui (HolyWorld)", "HolyWorld AutoBuy", "HW AutoBuy Pro", "HolyWorld BuyMaster", "AutoBuy HW Elite"};
    private String currentTitle = "";
    private int currentTitleIndex = 0;
    private int titleCharIndex = 0;
    private long lastTitleChangeTime = 0;
    private boolean deletingTitle = false;

    private final Map<AutoBuyItem, Animation> itemAnimations = new LinkedHashMap<>();
    private final Animation scrollbarAnimation = new Animation();
    private final Animation searchExpandAnimation = new Animation();
    private boolean searchExpanded = false;
    private final Animation smoothScrollAnimation = new Animation();
    private final Animation hwToggleAnimation = new Animation();

    private float scrollOffset = 0;
    private float targetScrollOffset = 0;
    private boolean draggingScroll = false;
    private float dragStartY = 0;
    private float dragStartOffset = 0;
    private static final int ITEM_HEIGHT = 50;
    private static final int VISIBLE_ITEMS_COUNT = 7;

    private float headerX;
    private float headerY;

    public AutoBuyHWGui() {
        super(new StringTextComponent("AutoBuy HW GUI"));
        this.autoBuyModule = AutoBuyHW.getInstance();
        setupCategories();
    }

    private void setupCategories() {
        categories.put("Все", Items.NETHER_STAR);
        categories.put("Оружие", Items.NETHERITE_SWORD);
        categories.put("Броня", Items.NETHERITE_CHESTPLATE);
        categories.put("Инструменты", Items.NETHERITE_PICKAXE);
        categories.put("Талисманы", Items.TOTEM_OF_UNDYING);
        categories.put("Сферы", Items.PLAYER_HEAD);
        categories.put("Ресурсы", Items.DIAMOND);
        categories.put("Зелья", Items.SPLASH_POTION);
        categories.put("Прочее", Items.TRIPWIRE_HOOK);

        for (String category : categories.keySet()) {
            Animation anim = new Animation();
            anim.set(0.0);
            categoryHoverAnimations.put(category, anim);
        }
    }

    @Override
    protected void init() {
        super.init();
        this.openAnimation.run(1.0, 0.4, Easings.EXPO_OUT);
        this.closeAnimation.set(0.0);
        this.closing = false;
        this.smoothScrollAnimation.set(0.0);
        this.hwToggleAnimation.set(hwMode ? 1.0 : 0.0);

        priceFieldFocused.clear();
        priceFieldAnimations.clear();
        itemAnimations.clear();
        itemHoverAnimations.clear();
        itemBackgroundAnimations.clear();
        this.children.clear();

        List<AutoBuyItem> allItems = new ArrayList<>();
        if (hwMode) {
            allItems.addAll(AutoBuyManager.getCustomAutoBuyItemList());
        } else {
            allItems.addAll(AutoBuyManager.getItems());
        }

        for (AutoBuyItem item : allItems) {
            if (!priceFields.containsKey(item)) {
                TextBox priceField = new TextBox(new org.joml.Vector2f(), Fonts.SF_REGULAR, 7, Theme.getInstance().textColor(), TextAlign.LEFT, String.valueOf(item.getPrice() > 0 ? item.getPrice() : ""), 0, false, false);
                priceFields.put(item, priceField);
            }
            if (!itemEnabled.containsKey(item)) {
                itemEnabled.put(item, true);
            }

            priceFieldFocused.put(item, false);

            Animation priceAnim = new Animation();
            priceAnim.set(0.0);
            priceFieldAnimations.put(item, priceAnim);

            Animation itemAnim = new Animation();
            itemAnim.set(0.0);
            itemAnimations.put(item, itemAnim);

            Animation hoverAnim = new Animation();
            hoverAnim.set(0.0);
            itemHoverAnimations.put(item, hoverAnim);

            Animation backgroundAnim = new Animation();
            backgroundAnim.set(0.0);
            itemBackgroundAnimations.put(item, backgroundAnim);
        }

        searchField = new TextBox(new org.joml.Vector2f(), Fonts.SF_REGULAR, 8, Theme.getInstance().textColor(), TextAlign.LEFT, "Поиск...", 0, false, false);
        searchExpandAnimation.set(0.0);
        scrollbarAnimation.set(0.0);

        filterItems();

        for (AutoBuyItem item : displayedItems) {
            Animation anim = itemAnimations.get(item);
            if (anim != null) {
                anim.run(1.0, 0.3 + Math.random() * 0.2, Easings.EXPO_OUT);
            }
        }

        currentTitle = titleTexts[0];
        currentTitleIndex = 0;
        titleCharIndex = 0;
        deletingTitle = false;
        lastTitleChangeTime = System.currentTimeMillis();
    }

    @Override
    public void onClose() {
        if (this.closing) return;
        this.closing = true;

        for (Map.Entry<AutoBuyItem, TextBox> entry : priceFields.entrySet()) {
            AutoBuyItem item = entry.getKey();
            TextBox priceField = entry.getValue();
            int price = 0;
            try {
                String text = priceField.getText();
                if (!text.isEmpty() && !text.equals("0")) {
                    price = Integer.parseInt(text.replaceAll("[^0-9]", ""));
                }
            } catch (NumberFormatException ignored) {
                price = 0;
            }
            item.setPrice(price);
        }

        AutoBuyManager.save();
        this.closeAnimation.run(1.0, 0.3, Easings.EXPO_IN);
        this.openAnimation.run(0.0, 0.3, Easings.EXPO_IN);
    }

    private void filterItems() {
        final String searchText = searchField.getText().toLowerCase();
        final String finalSearchText = searchText.equals("поиск...") ? "" : searchText;

        List<AutoBuyItem> allItems = new ArrayList<>();
        if (hwMode) {
            allItems.addAll(AutoBuyManager.getCustomAutoBuyItemList());
        } else {
            allItems.addAll(AutoBuyManager.getItems());
        }

        displayedItems = allItems.stream().filter(item -> {
            boolean categoryMatch = "Все".equals(selectedCategory) || getItemCategory(item).equals(selectedCategory);
            boolean searchMatch = finalSearchText.isEmpty() || item.getName().toLowerCase().contains(finalSearchText);
            return categoryMatch && searchMatch;
        }).collect(Collectors.toList());
    }

    private String getItemCategory(AutoBuyItem item) {
        String name = item.getName().toLowerCase();
        if (name.contains("меч") || name.contains("трезубец") || name.contains("арбалет")) return "Оружие";
        if (name.contains("шлем") || name.contains("нагрудник") || name.contains("поножи") || name.contains("ботинки") || (name.contains("элитры") && !item.getName().contains("Крушителя")))
            return "Броня";
        if (name.contains("кирка") || name.contains("молот") || name.contains("касание") || name.contains("удар"))
            return "Инструменты";
        if (name.contains("талисман")) return "Талисманы";
        if (name.contains("сфера")) return "Сферы";
        if (name.contains("зелье") || name.contains("кислота") || name.contains("вспышка") || name.contains("моча"))
            return "Зелья";
        if (item.getItem() == Items.DIAMOND || item.getItem() == Items.EMERALD || item.getItem() == Items.NETHERITE_INGOT || item.getItem() == Items.NETHERITE_SCRAP || item.getItem() == Items.GOLD_INGOT || item.getItem() == Items.GUNPOWDER || item.getItem() == Items.TNT)
            return "Ресурсы";
        return "Прочее";
    }

    private void updateAnimations() {
        openAnimation.update();
        closeAnimation.update();
        scrollbarAnimation.update();
        searchExpandAnimation.update();
        smoothScrollAnimation.update();
        hwToggleAnimation.update();

        float targetOffset = Math.max(0, Math.min(Math.max(0, displayedItems.size() * ITEM_HEIGHT - VISIBLE_ITEMS_COUNT * ITEM_HEIGHT + 25), targetScrollOffset));
        if (Math.abs(scrollOffset - targetOffset) > 0.1f) {
            scrollOffset += (targetOffset - scrollOffset) * 0.15f;
        } else {
            scrollOffset = targetOffset;
        }

        for (Animation anim : itemAnimations.values()) anim.update();
        for (Animation anim : priceFieldAnimations.values()) anim.update();
        for (Animation anim : itemHoverAnimations.values()) anim.update();
        for (Animation anim : categoryHoverAnimations.values()) anim.update();
        for (Animation anim : itemBackgroundAnimations.values()) anim.update();

        long currentTime = System.currentTimeMillis();
        long timeSinceLastChange = currentTime - lastTitleChangeTime;

        if (timeSinceLastChange < 4000) {
            long charInterval = 4000 / (currentTitle.length() * 2L + 20);
            if (!deletingTitle) {
                if (titleCharIndex < currentTitle.length()) {
                    if (timeSinceLastChange / charInterval > titleCharIndex) titleCharIndex++;
                } else {
                    if (timeSinceLastChange > 2000) deletingTitle = true;
                }
            } else {
                long deleteStart = timeSinceLastChange - 2000;
                if (titleCharIndex > 0) {
                    if (deleteStart / charInterval > (currentTitle.length() - titleCharIndex)) titleCharIndex--;
                }
            }
        } else if (timeSinceLastChange >= 5000) {
            currentTitleIndex = (currentTitleIndex + 1) % titleTexts.length;
            currentTitle = titleTexts[currentTitleIndex];
            deletingTitle = false;
            titleCharIndex = 0;
            lastTitleChangeTime = currentTime;
        }
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (closing && closeAnimation.isFinished() && openAnimation.isFinished()) {
            mc.displayScreen(null);
            return;
        }

        this.renderBackground(matrix);
        updateAnimations();

        Theme theme = Theme.getInstance();
        int clientColor = theme.clientColor();

        float easedAnim = openAnimation.get() * (1.0f - closeAnimation.get());
        if (easedAnim <= 0.01f) return;

        float mainWidth = 500;
        float mainHeight = (ITEM_HEIGHT * VISIBLE_ITEMS_COUNT) + 10;
        float headerHeight = 35;
        float categoryWidth = 30;
        float categoryHeight = categories.size() * 25 + 10;
        float totalHeight = headerHeight + 4 + mainHeight;

        float centerX = this.width / 2.0f;
        float centerY = this.height / 2.0f;

        float mainX = centerX - mainWidth / 2;
        float mainY = centerY - totalHeight / 2 + headerHeight + 4;
        headerX = mainX;
        headerY = mainY - headerHeight - 4;
        float categoryX = mainX - categoryWidth - 5;
        float categoryY = mainY + (mainHeight - categoryHeight) / 2;

        matrix.push();
        matrix.translate(centerX, centerY, 0);
        matrix.scale(easedAnim, easedAnim, 1);
        matrix.translate(-centerX, -centerY, 0);
        RenderSystem.color4f(1, 1, 1, easedAnim);

        RenderUtil.Shadow.drawShadow(matrix, headerX, headerY, mainWidth, headerHeight, 6, 0.7f, ColorUtil.getColor(0, 0, 0, 90), Round.of(8, 8, 0, 0));
        RenderUtil.Rounded.roundedRect(matrix, headerX, headerY, mainWidth, headerHeight, new Color(12, 12, 12, (int) (230 * easedAnim)).getRGB(), Round.of(8, 8, 0, 0));

        GlStateManager.pushMatrix();
        GlStateManager.translatef((headerX + 25), (headerY + headerHeight / 2), 1);
        GlStateManager.rotatef(180, 0, 0, 1);
        GlStateManager.translatef(-(headerX + 25), -(headerY + headerHeight / 2), 1);
        RenderUtil.Texture.roundedHead(mc.player.getLocationSkin(), headerX + 15, headerY + 7.5f, 20, 20, 8, easedAnim, 0);
        GlStateManager.popMatrix();

        String displayTitle = currentTitle.substring(0, titleCharIndex);
        if (!deletingTitle && titleCharIndex < currentTitle.length() && System.currentTimeMillis() / 500 % 2 == 0) {
            displayTitle += "_";
        }
        float titleWidth = Fonts.SF_MEDIUM.getWidth(displayTitle, 20f);
        RenderUtil.Shadow.drawShadow(matrix, headerX + (mainWidth - titleWidth) / 2 - 2, headerY + 6, titleWidth + 4, 24, 4, 0.6f, ColorUtil.getColor(0, 0, 0, 130), Round.of(3));
        Fonts.SF_MEDIUM.draw(matrix, displayTitle, headerX + (mainWidth - titleWidth) / 2, headerY + 8, clientColor, 20f);

        RenderUtil.Shadow.drawShadow(matrix, mainX, mainY, mainWidth, mainHeight, 6, 0.7f, ColorUtil.getColor(0, 0, 0, 90), Round.of(0, 0, 8, 8));
        RenderUtil.Rounded.roundedRect(matrix, mainX, mainY, mainWidth, mainHeight, new Color(12, 12, 12, (int) (230 * easedAnim)).getRGB(), Round.of(0, 0, 8, 8));

        RenderUtil.Shadow.drawShadow(matrix, categoryX, categoryY, categoryWidth, categoryHeight, 5, 0.6f, ColorUtil.getColor(0, 0, 0, 80), Round.of(8));
        RenderUtil.Rounded.roundedRect(matrix, categoryX, categoryY, categoryWidth, categoryHeight, new Color(12, 12, 12, (int) (230 * easedAnim)).getRGB(), Round.of(8));

        float currentCatY = categoryY + 5;
        for (Map.Entry<String, Item> entry : categories.entrySet()) {
            boolean isSelected = entry.getKey().equals(selectedCategory);
            boolean isHovered = mouseX >= categoryX && mouseX <= categoryX + categoryWidth && mouseY >= currentCatY && mouseY <= currentCatY + 20;

            Animation hoverAnim = categoryHoverAnimations.get(entry.getKey());
            hoverAnim.run(isHovered ? 1.0 : 0.0, 0.2, Easings.EXPO_OUT);

            int catColor = isSelected ? clientColor : ColorUtil.interpolate(ColorUtil.getColor(40, 40, 40), ColorUtil.getColor(60, 60, 60), hoverAnim.get());
            float iconSize = categoryWidth * 0.65f;
            float hoverScale = 1.0f + hoverAnim.get() * 0.15f;

            RenderUtil.Rounded.roundedRect(matrix, categoryX + (categoryWidth - iconSize) / 2, currentCatY, iconSize, iconSize, catColor, Round.of(5));

            matrix.push();
            matrix.translate(categoryX + categoryWidth / 2, currentCatY + iconSize / 2, 0);
            matrix.scale(hoverScale, hoverScale, 1);
            matrix.translate(-(categoryX + categoryWidth / 2), -(currentCatY + iconSize / 2), 0);
            drawItemIconWithShadow(new ItemStack(entry.getValue()), (int) (categoryX + categoryWidth / 2 - 8), (int) (currentCatY + iconSize / 2 - 8));
            matrix.pop();

            currentCatY += 25;
        }

        float hwButtonWidth = 80;
        float hwButtonHeight = 25;
        float hwButtonX = headerX + mainWidth - hwButtonWidth - 10;
        float hwButtonY = headerY + 5;
        boolean hwHovered = mouseX >= hwButtonX && mouseX <= hwButtonX + hwButtonWidth && mouseY >= hwButtonY && mouseY <= hwButtonY + hwButtonHeight;
        hwToggleAnimation.run(hwHovered ? 1.0 : 0.0, 0.2, Easings.EXPO_OUT);

        int hwColor = hwMode ? ColorUtil.getColor(60, 180, 60) : ColorUtil.getColor(180, 60, 60);
        int hwHoverColor = ColorUtil.interpolate(hwColor, ColorUtil.brighter(hwColor, 0.2f), hwToggleAnimation.get());
        RenderUtil.Shadow.drawShadow(matrix, hwButtonX, hwButtonY, hwButtonWidth, hwButtonHeight, 3, 0.4f, ColorUtil.getColor(0, 0, 0, 80), Round.of(5));
        RenderUtil.Rounded.roundedRect(matrix, hwButtonX, hwButtonY, hwButtonWidth, hwButtonHeight, ColorUtil.multAlpha(hwHoverColor, easedAnim), Round.of(5));
        String hwText = hwMode ? "HW Mode" : "Default";
        float hwTextWidth = Fonts.SF_MEDIUM.getWidth(hwText, 12f);
        Fonts.SF_MEDIUM.draw(matrix, hwText, hwButtonX + (hwButtonWidth - hwTextWidth) / 2, hwButtonY + 7, ColorUtil.multAlpha(Color.WHITE.getRGB(), easedAnim), 12f);

        float baseSearchWidth = 35;
        float expandedSearchWidth = 140;
        float currentSearchWidth = baseSearchWidth + (expandedSearchWidth - baseSearchWidth) * searchExpandAnimation.get();
        float searchBarHeight = 26;
        float searchBarX = centerX - currentSearchWidth / 2;
        float searchBarY = mainY + mainHeight + 8;

        RenderUtil.Shadow.drawShadow(matrix, searchBarX, searchBarY, currentSearchWidth, searchBarHeight, 4, 0.5f, ColorUtil.getColor(0, 0, 0, 70), Round.of(8));
        RenderUtil.Rounded.roundedRect(matrix, searchBarX, searchBarY, currentSearchWidth, searchBarHeight, new Color(20, 20, 20, (int) (230 * easedAnim)).getRGB(), Round.of(8));
        RenderUtil.Rounded.roundedRect(matrix, searchBarX + 1, searchBarY + 1, currentSearchWidth - 2, searchBarHeight - 2, new Color(30, 30, 30, (int) (190 * easedAnim)).getRGB(), Round.of(7));

        float iconX = searchBarX + (baseSearchWidth - 12.6f) / 2;
        if (searchExpandAnimation.get() > 0) iconX = searchBarX + 8;
        Fonts.icon.draw(matrix, "G", iconX + 0.5f, searchBarY + 7, ColorUtil.multAlpha(theme.iconColor(), easedAnim), 11f);

        if (searchExpandAnimation.get() > 0) {
            searchField.position.set(searchBarX + 28, searchBarY + 7);
            searchField.setWidth((int) currentSearchWidth - 32);
            searchField.setColor(ColorUtil.multAlpha(theme.textColor(), easedAnim));
            searchField.draw(matrix);
        } else {
            Fonts.SF_REGULAR.draw(matrix, "Поиск...", searchBarX + baseSearchWidth - 38, searchBarY + 22, ColorUtil.multAlpha(Color.GRAY.getRGB(), easedAnim * 0.7f), 11.5f);
        }

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.makeScissorBox(mainX, mainY, mainX + mainWidth, mainY + mainHeight);

        float currentItemY = mainY + 8 - scrollOffset;
        int itemIndex = 0;
        for (AutoBuyItem item : displayedItems) {
            if (currentItemY > mainY - ITEM_HEIGHT && currentItemY < mainY + mainHeight) {
                Animation itemAnim = itemAnimations.get(item);
                Animation hoverAnim = itemHoverAnimations.get(item);
                Animation backgroundAnim = itemBackgroundAnimations.get(item);
                float itemAlpha = itemAnim != null ? itemAnim.get() * easedAnim : easedAnim;

                if (itemAlpha > 0.01f) {
                    float itemCenterY = currentItemY + ITEM_HEIGHT / 2;
                    boolean isHovered = mouseX >= mainX + 10 && mouseX <= mainX + mainWidth - 10 && mouseY >= currentItemY && mouseY <= currentItemY + ITEM_HEIGHT;

                    if (hoverAnim != null) {
                        hoverAnim.run(isHovered ? 1.0 : 0.0, 0.2, Easings.EXPO_OUT);
                    }
                    if (backgroundAnim != null) {
                        backgroundAnim.run(isHovered ? 1.0 : 0.0, 0.15, Easings.EXPO_OUT);
                    }

                    float itemRectX = mainX + 8;
                    float itemRectY = currentItemY + 2;
                    float itemRectWidth = mainWidth - 16;
                    float itemRectHeight = ITEM_HEIGHT - 4;

                    int baseColor = new Color(18, 18, 18, (int) (150 * itemAlpha)).getRGB();
                    int hoveredColor = new Color(25, 25, 35, (int) (200 * itemAlpha)).getRGB();
                    int finalColor = backgroundAnim != null ?
                            ColorUtil.interpolate(baseColor, hoveredColor, backgroundAnim.get()) : baseColor;

                    RenderUtil.Shadow.drawShadow(matrix, itemRectX, itemRectY, itemRectWidth, itemRectHeight, 3, 0.3f, ColorUtil.getColor(0, 0, 0, 60), Round.of(6));
                    RenderUtil.Rounded.roundedRect(matrix, itemRectX, itemRectY, itemRectWidth, itemRectHeight, finalColor, Round.of(6));

                    float hoverScale = hoverAnim != null ? 1.0f + hoverAnim.get() * 0.4f : 1.0f;
                    matrix.push();
                    matrix.translate(mainX + 35, itemCenterY, 0);
                    matrix.scale(hoverScale, hoverScale, 1);
                    matrix.translate(-(mainX + 35), -itemCenterY, 0);
                    drawItemIconWithShadow(new ItemStack(item.getItem()), (int) mainX + 27, (int) (itemCenterY - 8));
                    matrix.pop();

                    boolean enabled = itemEnabled.getOrDefault(item, true);
                    float textAlpha = enabled ? itemAlpha : itemAlpha * 0.5f;
                    int textColor = enabled ? clientColor : Color.GRAY.getRGB();

                    float textY = itemCenterY - 8;
                    Fonts.SF_REGULAR.draw(matrix, item.getName(), mainX + 65, textY - 8, ColorUtil.multAlpha(textColor, textAlpha), 15f);
                    Fonts.SF_REGULAR.draw(matrix, "Цена:", mainX + 65, textY + 8, ColorUtil.multAlpha(Color.GRAY.getRGB(), textAlpha), 13f);

                    TextBox priceField = priceFields.get(item);
                    Animation priceAnim = priceFieldAnimations.get(item);
                    boolean focused = priceFieldFocused.get(item);

                    if (priceField != null && priceAnim != null) {
                        String priceText = priceField.getText();
                        if (priceText.isEmpty() && !focused) {
                            priceField.setText(String.valueOf(item.getPrice() > 0 ? item.getPrice() : ""));
                            priceText = priceField.getText();
                        }

                        if (!focused && priceAnim.get() < 0.1f) {
                            Fonts.SF_REGULAR.draw(matrix, priceText, mainX + 115, textY + 8.5f, ColorUtil.multAlpha(theme.textColor(), textAlpha), 13f);
                        } else {
                            float fieldAlpha = Math.max(0.4f, priceAnim.get());
                            float fieldWidth = 60 + 25 * priceAnim.get();
                            float fieldHeight = 18;
                            float fieldX = mainX + 110;
                            float fieldY = textY + 14;

                            RenderUtil.Shadow.drawShadow(matrix, fieldX - 1, fieldY - 1, fieldWidth + 2, fieldHeight + 2, 2, fieldAlpha * 0.5f, ColorUtil.multAlpha(clientColor, itemAlpha * 0.4f), Round.of(4));
                            RenderUtil.Rounded.roundedRect(matrix, fieldX, fieldY, fieldWidth, fieldHeight, ColorUtil.multAlpha(new Color(40, 40, 50, 220).getRGB(), itemAlpha * fieldAlpha), Round.of(4));

                            priceField.position.set(fieldX + 5, fieldY + 3);
                            priceField.setWidth((int) fieldWidth - 10);
                            priceField.setColor(ColorUtil.multAlpha(theme.textColor(), itemAlpha));
                            priceField.draw(matrix);
                        }
                    }

                    float buttonSize = 20;
                    float buttonY = itemCenterY - buttonSize / 2;
                    float resetButtonX = mainX + mainWidth - 90;
                    float toggleButtonX = mainX + mainWidth - 60;

                    boolean resetHovered = mouseX >= resetButtonX && mouseX <= resetButtonX + buttonSize && mouseY >= buttonY && mouseY <= buttonY + buttonSize;
                    boolean toggleHovered = mouseX >= toggleButtonX && mouseX <= toggleButtonX + buttonSize && mouseY >= buttonY && mouseY <= buttonY + buttonSize;

                    int resetColor = resetHovered ? ColorUtil.getColor(220, 60, 60) : ColorUtil.getColor(180, 40, 40);
                    RenderUtil.Shadow.drawShadow(matrix, resetButtonX, buttonY, buttonSize, buttonSize, 2, 0.3f, ColorUtil.getColor(0, 0, 0, 80), Round.of(5));
                    RenderUtil.Rounded.roundedRect(matrix, resetButtonX, buttonY, buttonSize, buttonSize, ColorUtil.multAlpha(resetColor, itemAlpha), Round.of(5));
                    Fonts.icon.draw(matrix, "T", resetButtonX + 3.3f, buttonY + 3.7F, ColorUtil.multAlpha(Color.WHITE.getRGB(), itemAlpha), 12f);

                    int toggleColor = enabled ?
                            (toggleHovered ? ColorUtil.getColor(60, 180, 60) : ColorUtil.getColor(40, 140, 40)) :
                            (toggleHovered ? ColorUtil.getColor(180, 180, 60) : ColorUtil.getColor(140, 140, 40));
                    RenderUtil.Shadow.drawShadow(matrix, toggleButtonX, buttonY, buttonSize, buttonSize, 2, 0.3f, ColorUtil.getColor(0, 0, 0, 80), Round.of(5));
                    RenderUtil.Rounded.roundedRect(matrix, toggleButtonX, buttonY, buttonSize, buttonSize, ColorUtil.multAlpha(toggleColor, itemAlpha), Round.of(5));
                    String toggleText = enabled ? "ON" : "OFF";
                    float toggleTextWidth = Fonts.SF_MEDIUM.getWidth(toggleText, 9f);
                    Fonts.SF_MEDIUM.draw(matrix, toggleText, toggleButtonX + (buttonSize - toggleTextWidth) / 2, buttonY + 6, ColorUtil.multAlpha(Color.WHITE.getRGB(), itemAlpha), 9f);

                    if (itemIndex < displayedItems.size() - 1) {
                        RenderUtil.Rounded.roundedRect(matrix, mainX + 15, currentItemY + ITEM_HEIGHT - 3, mainWidth - 30, 1, ColorUtil.multAlpha(ColorUtil.getColor(35, 35, 35), itemAlpha), Round.of(1));
                    }
                }
            }
            currentItemY += ITEM_HEIGHT;
            itemIndex++;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        int maxScroll = Math.max(0, displayedItems.size() * ITEM_HEIGHT - (int) mainHeight + 16);
        if (maxScroll > 0) {
            boolean scrollbarVisible = mouseX >= mainX + mainWidth - 18 && mouseX <= mainX + mainWidth && mouseY >= mainY && mouseY <= mainY + mainHeight;
            scrollbarAnimation.run(scrollbarVisible ? 1.0 : 0.4, 0.2, Easings.EXPO_OUT);

            float scrollBarHeight = Math.max(25, (mainHeight / (displayedItems.size() * ITEM_HEIGHT)) * mainHeight);
            float scrollBarY = mainY + (scrollOffset / (float) maxScroll) * (mainHeight - scrollBarHeight);
            float scrollbarAlpha = scrollbarAnimation.get() * easedAnim;

            int scrollbarColor = ColorUtil.interpolate(ColorUtil.multAlpha(ColorUtil.getColor(70, 70, 70), scrollbarAlpha), ColorUtil.multAlpha(clientColor, scrollbarAlpha), Math.min(1.0f, scrollbarAnimation.get() * 2.5f));

            RenderUtil.Rounded.roundedRect(matrix, mainX + mainWidth - 10, mainY, 6, mainHeight, ColorUtil.multAlpha(ColorUtil.getColor(25, 25, 25), scrollbarAlpha * 0.6f), Round.of(3));
            RenderUtil.Shadow.drawShadow(matrix, mainX + mainWidth - 10, scrollBarY, 6, scrollBarHeight, 2, 0.4f, ColorUtil.getColor(0, 0, 0, 100), Round.of(3));
            RenderUtil.Rounded.roundedRect(matrix, mainX + mainWidth - 10, scrollBarY, 6, scrollBarHeight, scrollbarColor, Round.of(3));
        }

        matrix.pop();
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    private void drawItemIconWithShadow(ItemStack stack, int x, int y) {
        RenderUtil.Shadow.drawShadow(new MatrixStack(), x - 2, y - 2, 20, 20, 3, 0.4f, ColorUtil.getColor(0, 0, 0, 120), Round.of(3));
        RenderSystem.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRenderer.renderItemOverlayIntoGUI(this.font, stack, x, y, null);
        RenderHelper.disableStandardItemLighting();
        RenderSystem.popMatrix();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float mainWidth = 500;
        float mainHeight = (ITEM_HEIGHT * VISIBLE_ITEMS_COUNT) + 10;
        float headerHeight = 35;
        float categoryWidth = 30;
        float categoryHeight = categories.size() * 25 + 10;
        float totalHeight = headerHeight + 4 + mainHeight;

        float centerX = this.width / 2.0f;
        float centerY = this.height / 2.0f;

        float mainX = centerX - mainWidth / 2;
        float mainY = centerY - totalHeight / 2 + headerHeight + 4;
        float categoryX = mainX - categoryWidth - 5;
        float categoryY = mainY + (mainHeight - categoryHeight) / 2;

        float hwButtonWidth = 80;
        float hwButtonHeight = 25;
        float hwButtonX = headerX + mainWidth - hwButtonWidth - 10;
        float hwButtonY = headerY + 5;
        if (mouseX >= hwButtonX && mouseX <= hwButtonX + hwButtonWidth && mouseY >= hwButtonY && mouseY <= hwButtonY + hwButtonHeight) {
            hwMode = !hwMode;
            hwToggleAnimation.set(hwMode ? 1.0 : 0.0);
            init();
            return true;
        }

        float currentCatY = categoryY + 5;
        for (Map.Entry<String, Item> entry : categories.entrySet()) {
            float iconSize = categoryWidth * 0.65f;
            if (mouseX >= categoryX + (categoryWidth - iconSize) / 2 && mouseX <= categoryX + (categoryWidth - iconSize) / 2 + iconSize && mouseY >= currentCatY && mouseY <= currentCatY + iconSize) {
                selectedCategory = entry.getKey();
                filterItems();
                targetScrollOffset = 0;
                for (AutoBuyItem itemData : displayedItems) {
                    Animation anim = itemAnimations.get(itemData);
                    if (anim != null) {
                        anim.set(0.0);
                        anim.run(1.0, 0.3 + Math.random() * 0.2, Easings.EXPO_OUT);
                    }
                }
                return true;
            }
            currentCatY += 25;
        }

        float baseSearchWidth = 35;
        float expandedSearchWidth = 140;
        float currentSearchWidth = baseSearchWidth + (expandedSearchWidth - baseSearchWidth) * searchExpandAnimation.get();
        float searchBarHeight = 26;
        float searchBarX = centerX - currentSearchWidth / 2;
        float searchBarY = mainY + mainHeight + 8;

        if (mouseX >= searchBarX && mouseX <= searchBarX + currentSearchWidth && mouseY >= searchBarY && mouseY <= searchBarY + searchBarHeight) {
            if (!searchExpanded) {
                searchExpanded = true;
                searchExpandAnimation.run(1.0, 0.3, Easings.EXPO_OUT);
                if (searchField.getText().equals("Поиск...")) searchField.setText("");
            }
            searchField.mouse(mouseX, mouseY, button);
            return true;
        } else if (searchExpanded && !searchField.isSelected()) {
            searchExpanded = false;
            searchExpandAnimation.run(0.0, 0.3, Easings.EXPO_IN);
            if (searchField.getText().isEmpty()) searchField.setText("Поиск...");
        }

        if (mouseX >= mainX && mouseX <= mainX + mainWidth && mouseY >= mainY && mouseY <= mainY + mainHeight) {
            for (Map.Entry<AutoBuyItem, Boolean> entry : priceFieldFocused.entrySet()) {
                entry.setValue(false);
                Animation anim = priceFieldAnimations.get(entry.getKey());
                if (anim != null) anim.run(0.0, 0.2, Easings.EXPO_OUT);
            }

            float currentItemY = mainY + 8 - scrollOffset;
            for (AutoBuyItem item : displayedItems) {
                if (currentItemY > mainY - ITEM_HEIGHT && currentItemY < mainY + mainHeight) {
                    float itemCenterY = currentItemY + ITEM_HEIGHT / 2;
                    float iconX = mainX + 27;
                    float iconY = itemCenterY - 8;

                    if (button == 1 && mouseX >= iconX && mouseX <= iconX + 16 && mouseY >= iconY && mouseY <= iconY + 16) {
                        this.onClose();
                        return true;
                    }

                    float textY = itemCenterY - 8;
                    float priceX = mainX + 110;
                    float priceY = textY + 14;

                    if (mouseX >= priceX && mouseX <= priceX + 85 && mouseY >= priceY && mouseY <= priceY + 18) {
                        priceFieldFocused.put(item, true);
                        Animation anim = priceFieldAnimations.get(item);
                        if (anim != null) anim.run(1.0, 0.2, Easings.EXPO_OUT);
                        TextBox priceField = priceFields.get(item);
                        if (priceField != null) priceField.mouse(mouseX, mouseY, button);
                        return true;
                    }

                    float buttonSize = 20;
                    float buttonY = itemCenterY - buttonSize / 2;
                    float resetButtonX = mainX + mainWidth - 90;
                    float toggleButtonX = mainX + mainWidth - 60;

                    if (mouseX >= resetButtonX && mouseX <= resetButtonX + buttonSize && mouseY >= buttonY && mouseY <= buttonY + buttonSize) {
                        TextBox priceField = priceFields.get(item);
                        if (priceField != null) {
                            priceField.setText("0");
                        }
                        return true;
                    }

                    if (mouseX >= toggleButtonX && mouseX <= toggleButtonX + buttonSize && mouseY >= buttonY && mouseY <= buttonY + buttonSize) {
                        boolean currentState = itemEnabled.getOrDefault(item, true);
                        itemEnabled.put(item, !currentState);
                        return true;
                    }
                }
                currentItemY += ITEM_HEIGHT;
            }

            int maxScroll = Math.max(0, displayedItems.size() * ITEM_HEIGHT - (int) mainHeight + 16);
            if (maxScroll > 0 && mouseX >= mainX + mainWidth - 10 && mouseX <= mainX + mainWidth - 4) {
                draggingScroll = true;
                dragStartY = (float) mouseY;
                dragStartOffset = targetScrollOffset;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingScroll = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingScroll) {
            float mainHeight = (ITEM_HEIGHT * VISIBLE_ITEMS_COUNT) + 10;
            int totalContentHeight = displayedItems.size() * ITEM_HEIGHT;
            float scrollableAreaHeight = mainHeight;
            float dy = (float) mouseY - dragStartY;
            targetScrollOffset = dragStartOffset + (dy / scrollableAreaHeight) * totalContentHeight;
            targetScrollOffset = Math.max(0, Math.min(Math.max(0, totalContentHeight - (int) mainHeight + 16), targetScrollOffset));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        float mainHeight = (ITEM_HEIGHT * VISIBLE_ITEMS_COUNT) + 10;
        int maxScroll = Math.max(0, displayedItems.size() * ITEM_HEIGHT - (int) mainHeight + 16);
        if (maxScroll > 0) {
            targetScrollOffset -= (float) (delta * ITEM_HEIGHT * 1.5);
            targetScrollOffset = Math.max(0, Math.min(maxScroll, targetScrollOffset));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchExpanded && searchField.isSelected()) {
            searchField.keyPressed(keyCode);
            if (keyCode == 257) {
                filterItems();
                targetScrollOffset = 0;
                for (AutoBuyItem itemData : displayedItems) {
                    Animation anim = itemAnimations.get(itemData);
                    if (anim != null) {
                        anim.set(0.0);
                        anim.run(1.0, 0.3 + Math.random() * 0.2, Easings.EXPO_OUT);
                    }
                }
            }
        }

        for (Map.Entry<AutoBuyItem, Boolean> entry : priceFieldFocused.entrySet()) {
            if (entry.getValue()) {
                TextBox priceField = priceFields.get(entry.getKey());
                if (priceField != null) priceField.keyPressed(keyCode);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchExpanded && searchField.isSelected()) {
            searchField.charTyped(codePoint);
        }

        for (Map.Entry<AutoBuyItem, Boolean> entry : priceFieldFocused.entrySet()) {
            if (entry.getValue()) {
                TextBox priceField = priceFields.get(entry.getKey());
                if (priceField != null && priceField.isSelected()) priceField.charTyped(codePoint);
            }
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
package org.privates.client.managers.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.utils.render.color.ColorUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BaseFinderPlusScreen extends Screen {
    private final BaseFinderPlus module;
    private final Minecraft mc = Minecraft.getInstance();
    private int scrollOffset = 0;
    private int filterScrollOffset = 0;
    private static final int VISIBLE_CARDS = 8;
    private static final int VISIBLE_FILTERS = 10;
    private boolean showFilters = false;

    private final List<FilterItem> availableFilters = new ArrayList<>();
    private final Set<String> selectedFilters = new HashSet<>();

    public BaseFinderPlusScreen(BaseFinderPlus module) {
        super(new StringTextComponent("BaseFinder+"));
        this.module = module;
        initializeFilters();
    }

    private void initializeFilters() {
        availableFilters.add(new FilterItem("Игроки", "player"));
        availableFilters.add(new FilterItem("Сундуки", "chest"));
        availableFilters.add(new FilterItem("Эндер-сундуки", "ender_chest"));
        availableFilters.add(new FilterItem("Бочки", "barrel"));
        availableFilters.add(new FilterItem("Шалкеры", "shulker_box"));
        availableFilters.add(new FilterItem("Печи", "furnace"));
        availableFilters.add(new FilterItem("Наковальни", "anvil"));
        availableFilters.add(new FilterItem("Рассадники", "spawner"));
        availableFilters.add(new FilterItem("Зельеварки", "brewing_stand"));
        availableFilters.add(new FilterItem("Раздатчики", "dispenser"));
        availableFilters.add(new FilterItem("Выбрасыватели", "dropper"));
        availableFilters.add(new FilterItem("Воронки", "hopper"));
        availableFilters.add(new FilterItem("Кровати", "bed"));
        availableFilters.add(new FilterItem("Рамки", "item_frame"));
        availableFilters.add(new FilterItem("Картины", "painting"));
        availableFilters.add(new FilterItem("Предметы", "item"));
        availableFilters.add(new FilterItem("Всплохи", "blaze"));
        availableFilters.add(new FilterItem("Слизни", "slime"));
        availableFilters.add(new FilterItem("Эндермены", "enderman"));
        availableFilters.add(new FilterItem("Криперы", "creeper"));
        availableFilters.add(new FilterItem("Скелеты", "skeleton"));
        availableFilters.add(new FilterItem("Зомби", "zombie"));
        availableFilters.add(new FilterItem("Пауки", "spider"));
        availableFilters.add(new FilterItem("Маяки", "beacon"));
        availableFilters.add(new FilterItem("Кондуиты", "conduit"));
    }

    @Override
    protected void init() {

    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);

        int guiWidth = 480;
        int guiHeight = 380;
        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;

        fill(matrix, x, y, x + guiWidth, y + guiHeight, ColorUtil.getColor(0, 0, 0, 180));

        String title = "BaseFinder+ — поиск объектов";
        int titleWidth = this.font.getStringWidth(title);
        this.font.drawString(matrix, title, this.width / 2 - titleWidth / 2, y + 16, ColorUtil.WHITE);

        int filterBtnX = x + guiWidth - 140;
        int filterBtnY = y + 10;
        int filterBtnW = 120;
        int filterBtnH = 20;

        boolean filterHover = isIn(mouseX, mouseY, filterBtnX, filterBtnY, filterBtnW, filterBtnH);
        fill(matrix, filterBtnX, filterBtnY, filterBtnX + filterBtnW, filterBtnY + filterBtnH,
                filterHover ? ColorUtil.getColor(60, 60, 60) : ColorUtil.getColor(40, 40, 40));

        String filterText = showFilters ? "Скрыть фильтры" : "Показать фильтры";
        int filterTextW = this.font.getStringWidth(filterText);
        this.font.drawString(matrix, filterText, filterBtnX + filterBtnW / 2 - filterTextW / 2,
                filterBtnY + 6, ColorUtil.WHITE);

        if (showFilters) {
            renderFilters(matrix, mouseX, mouseY, x, y + 40, guiWidth);
        } else {
            renderFoundObjects(matrix, mouseX, mouseY, x, y + 40, guiWidth);
        }

        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    private void renderFilters(MatrixStack matrix, int mouseX, int mouseY, int x, int y, int guiWidth) {
        int filterHeight = 30;
        int totalFilters = availableFilters.size();
        int maxScroll = Math.max(0, totalFilters - VISIBLE_FILTERS);
        if (filterScrollOffset > maxScroll) filterScrollOffset = maxScroll;

        int start = filterScrollOffset;
        int end = Math.min(totalFilters, start + VISIBLE_FILTERS);

        for (int i = start; i < end; i++) {
            int filterY = y + (i - start) * filterHeight;
            FilterItem filter = availableFilters.get(i);
            boolean selected = selectedFilters.contains(filter.id);
            boolean hover = isIn(mouseX, mouseY, x + 20, filterY, guiWidth - 40, filterHeight - 2);

            int bgColor = selected ? ColorUtil.getColor(60, 100, 60, 180) :
                    hover ? ColorUtil.getColor(50, 50, 50, 180) : ColorUtil.getColor(30, 30, 30, 150);

            fill(matrix, x + 20, filterY, x + guiWidth - 20, filterY + filterHeight - 2, bgColor);

            this.font.drawString(matrix, filter.name, x + 30, filterY + 8, ColorUtil.WHITE);

            String status = selected ? "Активен" : "Неактивен";
            this.font.drawString(matrix, status, x + guiWidth - 100, filterY + 8,
                    selected ? ColorUtil.getColor(120, 255, 120) : ColorUtil.getColor(160, 160, 160));
        }

        if (totalFilters > VISIBLE_FILTERS) {
            int barHeight = (int) ((float) VISIBLE_FILTERS / totalFilters * (filterHeight * VISIBLE_FILTERS));
            int barY = y + (int) ((float) filterScrollOffset / maxScroll * (filterHeight * VISIBLE_FILTERS - barHeight));
            int barX = x + guiWidth - 12;

            fill(matrix, barX, y, barX + 6, y + filterHeight * VISIBLE_FILTERS, ColorUtil.getColor(40, 40, 40));
            fill(matrix, barX, barY, barX + 6, barY + barHeight, ColorUtil.getColor(120, 120, 120));
        }
    }

    private void renderFoundObjects(MatrixStack matrix, int mouseX, int mouseY, int x, int y, int guiWidth) {
        int cardHeight = 35;
        List<BaseFinderPlus.FoundObject> filteredObjects = getFilteredObjects();
        int totalCards = filteredObjects.size();
        int maxScroll = Math.max(0, totalCards - VISIBLE_CARDS);
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;

        int start = scrollOffset;
        int end = Math.min(totalCards, start + VISIBLE_CARDS);

        for (int i = start; i < end; i++) {
            int cardY = y + (i - start) * cardHeight;
            BaseFinderPlus.FoundObject obj = filteredObjects.get(i);

            boolean hover = isIn(mouseX, mouseY, x + 20, cardY, guiWidth - 40, cardHeight - 2);
            int bgColor = hover ? ColorUtil.getColor(50, 50, 50, 200) : ColorUtil.getColor(30, 30, 30, 150);

            fill(matrix, x + 20, cardY, x + guiWidth - 20, cardY + cardHeight - 2, bgColor);

            this.font.drawString(matrix, obj.name(), x + 30, cardY + 6, ColorUtil.WHITE);

            String coords = String.format("X: %d  Y: %d  Z: %d", obj.pos().getX(), obj.pos().getY(), obj.pos().getZ());
            this.font.drawString(matrix, coords, x + 30, cardY + 18, ColorUtil.getColor(180, 180, 180));

            int btnY = cardY + 6;
            int btnW = 60;
            int btnH = 16;
            int btn1X = x + guiWidth - 140;
            int btn2X = x + guiWidth - 70;

            boolean copyHover = isIn(mouseX, mouseY, btn1X, btnY, btnW, btnH);
            fill(matrix, btn1X, btnY, btn1X + btnW, btnY + btnH,
                    copyHover ? ColorUtil.getColor(80, 80, 80) : ColorUtil.getColor(60, 60, 60));

            String copyText = "Копировать";
            int copyTextW = this.font.getStringWidth(copyText);
            this.font.drawString(matrix, copyText, btn1X + btnW / 2 - copyTextW / 2, btnY + 4, ColorUtil.WHITE);

            boolean gpsHover = isIn(mouseX, mouseY, btn2X, btnY, btnW, btnH);
            fill(matrix, btn2X, btnY, btn2X + btnW, btnY + btnH,
                    gpsHover ? ColorUtil.getColor(80, 80, 80) : ColorUtil.getColor(60, 60, 60));

            String gpsText = "GPS";
            int gpsTextW = this.font.getStringWidth(gpsText);
            this.font.drawString(matrix, gpsText, btn2X + btnW / 2 - gpsTextW / 2, btnY + 4, ColorUtil.WHITE);
        }

        if (totalCards > VISIBLE_CARDS) {
            int barHeight = (int) ((float) VISIBLE_CARDS / totalCards * (cardHeight * VISIBLE_CARDS));
            int barY = y + (int) ((float) scrollOffset / maxScroll * (cardHeight * VISIBLE_CARDS - barHeight));
            int barX = x + guiWidth - 12;

            fill(matrix, barX, y, barX + 6, y + cardHeight * VISIBLE_CARDS, ColorUtil.getColor(40, 40, 40));
            fill(matrix, barX, barY, barX + 6, barY + barHeight, ColorUtil.getColor(120, 120, 120));
        }

        String objectCount = String.format("Найдено объектов: %d", totalCards);
        this.font.drawString(matrix, objectCount, x + 20, y + cardHeight * VISIBLE_CARDS + 10, ColorUtil.WHITE);
    }

    private List<BaseFinderPlus.FoundObject> getFilteredObjects() {
        if (selectedFilters.isEmpty()) {
            return module.foundObjects;
        }

        List<BaseFinderPlus.FoundObject> filtered = new ArrayList<>();
        for (BaseFinderPlus.FoundObject obj : module.foundObjects) {
            if (matchesFilter(obj)) {
                filtered.add(obj);
            }
        }
        return filtered;
    }

    private boolean matchesFilter(BaseFinderPlus.FoundObject obj) {
        String name = obj.name().toLowerCase();
        for (String filterId : selectedFilters) {
            if (filterId.equals("player") && name.contains("игрок")) return true;
            if (filterId.equals("chest") && name.contains("сундук")) return true;
            if (filterId.equals("ender_chest") && name.contains("эндер")) return true;
            if (filterId.equals("barrel") && name.contains("бочка")) return true;
            if (filterId.equals("shulker_box") && name.contains("шалкер")) return true;
            if (filterId.equals("furnace") && name.contains("печ")) return true;
            if (filterId.equals("anvil") && name.contains("наковальн")) return true;
            if (filterId.equals("spawner") && name.contains("рассадник")) return true;
            if (filterId.equals("brewing_stand") && name.contains("зельевар")) return true;
            if (filterId.equals("dispenser") && name.contains("раздатчик")) return true;
            if (filterId.equals("dropper") && name.contains("выбрасыват")) return true;
            if (filterId.equals("hopper") && name.contains("воронк")) return true;
            if (filterId.equals("bed") && name.contains("кровать")) return true;
            if (filterId.equals("item_frame") && name.contains("рамк")) return true;
            if (filterId.equals("painting") && name.contains("картин")) return true;
            if (filterId.equals("item") && name.contains("предмет")) return true;
            if (filterId.equals("blaze") && name.contains("всплох")) return true;
            if (filterId.equals("slime") && name.contains("слиз")) return true;
            if (filterId.equals("enderman") && name.contains("эндермен")) return true;
            if (filterId.equals("creeper") && name.contains("крипер")) return true;
            if (filterId.equals("skeleton") && name.contains("скелет")) return true;
            if (filterId.equals("zombie") && name.contains("зомби")) return true;
            if (filterId.equals("spider") && name.contains("паук")) return true;
            if (filterId.equals("beacon") && name.contains("маяк")) return true;
            if (filterId.equals("conduit") && name.contains("кондуит")) return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int guiWidth = 480;
        int guiHeight = 380;
        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;

        int filterBtnX = x + guiWidth - 140;
        int filterBtnY = y + 10;
        int filterBtnW = 120;
        int filterBtnH = 20;

        if (isIn(mouseX, mouseY, filterBtnX, filterBtnY, filterBtnW, filterBtnH)) {
            showFilters = !showFilters;
            return true;
        }

        if (showFilters) {
            int filterHeight = 30;
            int start = filterScrollOffset;
            int end = Math.min(availableFilters.size(), start + VISIBLE_FILTERS);

            for (int i = start; i < end; i++) {
                int filterY = y + 40 + (i - start) * filterHeight;
                if (isIn(mouseX, mouseY, x + 20, filterY, guiWidth - 40, filterHeight - 2)) {
                    FilterItem filter = availableFilters.get(i);
                    if (selectedFilters.contains(filter.id)) {
                        selectedFilters.remove(filter.id);
                    } else {
                        selectedFilters.add(filter.id);
                    }
                    return true;
                }
            }
        } else {
            int cardHeight = 35;
            List<BaseFinderPlus.FoundObject> filteredObjects = getFilteredObjects();
            int start = scrollOffset;
            int end = Math.min(filteredObjects.size(), start + VISIBLE_CARDS);

            for (int i = start; i < end; i++) {
                int cardY = y + 40 + (i - start) * cardHeight;
                int btnY = cardY + 6;
                int btnW = 60;
                int btnH = 16;
                int btn1X = x + guiWidth - 140;
                int btn2X = x + guiWidth - 70;
                BaseFinderPlus.FoundObject obj = filteredObjects.get(i);

                if (isIn(mouseX, mouseY, btn1X, btnY, btnW, btnH)) {
                    module.copyCoords(obj.pos());
                    return true;
                }
                if (isIn(mouseX, mouseY, btn2X, btnY, btnW, btnH)) {
                    module.sendGPS(obj.pos());
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (showFilters) {
            int totalFilters = availableFilters.size();
            int maxScroll = Math.max(0, totalFilters - VISIBLE_FILTERS);
            filterScrollOffset -= (int) Math.signum(delta);
            if (filterScrollOffset < 0) filterScrollOffset = 0;
            if (filterScrollOffset > maxScroll) filterScrollOffset = maxScroll;
        } else {
            List<BaseFinderPlus.FoundObject> filteredObjects = getFilteredObjects();
            int totalCards = filteredObjects.size();
            int maxScroll = Math.max(0, totalCards - VISIBLE_CARDS);
            scrollOffset -= (int) Math.signum(delta);
            if (scrollOffset < 0) scrollOffset = 0;
            if (scrollOffset > maxScroll) scrollOffset = maxScroll;
        }
        return true;
    }

    private boolean isIn(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record FilterItem(String name, String id) {
    }
}
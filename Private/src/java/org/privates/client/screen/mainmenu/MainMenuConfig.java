package org.privates.client.screen.mainmenu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.privates.client.utils.render.color.ColorUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

public class MainMenuConfig {
    private static final String CONFIG_FILE = "mainmenu_config.json";
    private CustomizeData customizeData;

    public void load() {
        try {
            File configFile = new File(CONFIG_FILE);
            if (configFile.exists()) {
                Gson gson = new Gson();
                FileReader reader = new FileReader(configFile);
                customizeData = gson.fromJson(reader, CustomizeData.class);
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (customizeData == null) {
            createDefaultConfig(800, 600);
        }
    }

    public void save() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(CONFIG_FILE);
            gson.toJson(customizeData, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createDefaultConfig(float width, float height) {
        customizeData = new CustomizeData();

        customizeData.colors = new ColorSettings();
        customizeData.colors.background1 = ColorUtil.getColor(8, 12, 28);
        customizeData.colors.background2 = ColorUtil.getColor(12, 18, 38);
        customizeData.colors.background3 = ColorUtil.getColor(16, 24, 48);
        customizeData.colors.background4 = ColorUtil.getColor(18, 28, 52);
        customizeData.colors.menuBarBg = ColorUtil.getColor(20, 30, 55, 180);
        customizeData.colors.buttonBg = ColorUtil.getColor(30, 50, 90, 120);
        customizeData.colors.primaryText = ColorUtil.getColor(200, 220, 255);
        customizeData.colors.secondaryText = ColorUtil.getColor(180, 200, 240);
        customizeData.colors.accent = ColorUtil.getColor(100, 160, 240);

        customizeData.title = new TextElement("PrivateBeta 1.16.5", width / 2f, height / 2f - 110, 46, TextAlignment.CENTER, 0);
        customizeData.description = new TextElement("Получай доминацию и удовольствие играя каждую минуту в этот клиент с нами доминация и лучшая кастомизация как с визуальной части так и с пвп в нашем чите насчитывается более 160 полезных функций для улучшения вашей игры как визуально так и в других смыслах выбирай правильно и тщательно с PrivateBeta", width / 2f, height / 2f - 45, 12, TextAlignment.CENTER, 0);

        customizeData.menuBar = new MenuBarElement(width / 2f - 190, 24, 380, 36, 11, MenuAlignment.HORIZONTAL, 8);
        customizeData.infoButton = new ButtonElement("Информация", width / 2f - 70, height / 2f + 90, 140, 32, 12, TextAlignment.CENTER, 8);
        customizeData.customizeButton = new ButtonElement("Кастомизировать", width / 2f - 70, height / 2f + 130, 140, 32, 12, TextAlignment.CENTER, 8);

        customizeData.flagsContainer = new FlagsContainer(30, height - 100, 70, 42, true);

        customizeData.menuItems = Arrays.asList("Главная", "Одиночная", "Мульти", "Аккаунты", "Выход");
    }

    public void updateElementPositions(float width, float height) {
        if (customizeData.menuBar.x == 0) customizeData.menuBar.x = width / 2f - customizeData.menuBar.width / 2f;
        if (customizeData.title.x == 0) customizeData.title.x = width / 2f;
        if (customizeData.description.x == 0) customizeData.description.x = width / 2f;
        if (customizeData.infoButton.x == 0)
            customizeData.infoButton.x = width / 2f - customizeData.infoButton.width / 2f;
        if (customizeData.customizeButton.x == 0)
            customizeData.customizeButton.x = width / 2f - customizeData.customizeButton.width / 2f;
    }

    public void updateElementPosition(DraggableElement element, float newX, float newY) {
        float snapSize = 5f;
        newX = Math.round(newX / snapSize) * snapSize;
        newY = Math.round(newY / snapSize) * snapSize;

        switch (element.id) {
            case "menuBar":
                customizeData.menuBar.x = newX;
                customizeData.menuBar.y = newY;
                break;
            case "title":
                customizeData.title.x = newX + 150;
                customizeData.title.y = newY + 15;
                break;
            case "description":
                customizeData.description.x = newX + 200;
                customizeData.description.y = newY + 30;
                break;
            case "infoButton":
                customizeData.infoButton.x = newX;
                customizeData.infoButton.y = newY;
                break;
            case "customizeButton":
                customizeData.customizeButton.x = newX;
                customizeData.customizeButton.y = newY;
                break;
            case "flags":
                customizeData.flagsContainer.x = newX;
                customizeData.flagsContainer.y = newY;
                break;
        }
    }

    public CustomizeData getCustomizeData() {
        return customizeData;
    }

    @Data
    public static class CustomizeData {
        public TextElement title;
        public TextElement description;
        public MenuBarElement menuBar;
        public ButtonElement infoButton;
        public ButtonElement customizeButton;
        public ButtonElement readmeButton;

        public FlagsContainer flagsContainer;
        public ColorSettings colors;
        public List<String> menuItems;
    }

    @Data
    public static class TextElement {
        public String text;
        public float x, y;
        public float fontSize;
        public TextAlignment alignment;
        public float rotation;

        public TextElement(String text, float x, float y, float fontSize, TextAlignment alignment, float rotation) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.fontSize = fontSize;
            this.alignment = alignment;
            this.rotation = rotation;
        }
    }

    @Data
    public static class MenuBarElement {
        public float x, y, width, height;
        public float fontSize;
        public MenuAlignment alignment;
        public float cornerRadius;

        public MenuBarElement(float x, float y, float width, float height, float fontSize, MenuAlignment alignment, float cornerRadius) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.fontSize = fontSize;
            this.alignment = alignment;
            this.cornerRadius = cornerRadius;
        }
    }

    @Data
    public static class ButtonElement {
        public String text;
        public float x, y, width, height;
        public float fontSize;
        public TextAlignment textAlignment;
        public float cornerRadius;

        public ButtonElement(String text, float x, float y, float width, float height, float fontSize, TextAlignment textAlignment, float cornerRadius) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.fontSize = fontSize;
            this.textAlignment = textAlignment;
            this.cornerRadius = cornerRadius;
        }
    }

    @Data
    public static class FlagsContainer {
        public float x, y, width, height;
        public boolean enabled;

        public FlagsContainer(float x, float y, float width, float height, boolean enabled) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.enabled = enabled;
        }
    }

    @Data
    public static class ColorSettings {
        public int background1, background2, background3, background4;
        public int menuBarBg, buttonBg;
        public int primaryText, secondaryText;
        public int accent;
    }

    public enum TextAlignment {
        LEFT, CENTER, RIGHT, TOP_LEFT, TOP_CENTER, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
    }

    public enum MenuAlignment {
        HORIZONTAL, VERTICAL, GRID_2X3, GRID_3X2, CUSTOM
    }
}
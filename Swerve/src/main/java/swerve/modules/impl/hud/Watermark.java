package swerve.modules.impl.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import swerve.managers.ThemeManager;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.ui.hud.HudElement;
import swerve.ui.newgui.GuiFonts;
import swerve.ui.newgui.GuiRender;
import swerve.utils.color.ColorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

// Водяной знак в стиле исходного клиента (Private, SWatermark): сетка отдельных плиток,
// сверху «логотип, ник, сервер», снизу — метрики. У каждой плитки своя иконка из шрифтов
// icon/icon1, фон — акцент темы. Плюс физика драга из NewHudWatermarkRenderer:
// пружинный сдвиг, наклон по скорости и лёгкий рост при захвате.
public final class Watermark extends HudElement {

    private static final float TEXT_SIZE = 8.0f;
    private static final float TILE_HEIGHT = 18.0f;
    private static final float TILE_RADIUS = 4.0f;
    private static final float TILE_SPACING = 2.0f;
    private static final float TILE_PADDING_X = 6.0f;
    private static final float TILE_MIN_WIDTH = 26.0f;
    private static final float ICON_SIZE = 10.0f;
    private static final float ICON_GAP = 4.0f;

    private static final float TOP_BACKGROUND = 0.72f;
    private static final float BOTTOM_BACKGROUND = 0.42f;

    private static final float SPRING_LIMIT = 5.0f;
    private static final float ROTATION_LIMIT = 15.0f;
    private static final float DRAG_SCALE = 1.02f;

    private static final String BRAND = "Swerve";
    private static final String SINGLEPLAYER = "singleplayer";
    private static final String EMPTY = "";

    private static final int WHITE = ColorUtils.rgba(255, 255, 255, 255);
    private static final int ICON_COLOR = ColorUtils.rgba(255, 255, 255, 245);

    private final ModeSetting style = new ModeSetting("Стиль", "Плитки", "Плитки", "Минимал");
    private final BooleanSetting icons = new BooleanSetting("Иконки", true);
    private final BooleanSetting showLogo = new BooleanSetting("Показ логотипа", true);
    private final BooleanSetting showName = new BooleanSetting("Показ ника", true);
    private final BooleanSetting showServer = new BooleanSetting("Показ сервера", true);
    private final BooleanSetting showFps = new BooleanSetting("Показ FPS", true);
    private final BooleanSetting showPing = new BooleanSetting("Показ пинга", true);
    private final BooleanSetting showBps = new BooleanSetting("Показ BPS", true);
    private final BooleanSetting dragAnimation = new BooleanSetting("Анимация драга", true);

    private final Tile logo = new Tile("A", GuiFonts.ICON);
    private final Tile name = new Tile("n", GuiFonts.ICON1);
    private final Tile server = new Tile("e", GuiFonts.ICON1);
    private final Tile fps = new Tile("d", GuiFonts.ICON1);
    private final Tile ping = new Tile("c", GuiFonts.ICON1);
    private final Tile bps = new Tile("q", GuiFonts.ICON1);

    private final Tile[] topRow = {this.logo, this.name, this.server};
    private final Tile[] bottomRow = {this.fps, this.ping, this.bps};

    private float billboardWidth = TILE_MIN_WIDTH;

    private String nickname = "";
    private String serverIp = SINGLEPLAYER;

    private int lastFps = Integer.MIN_VALUE;
    private int lastPing = Integer.MIN_VALUE;
    private int lastBps = Integer.MIN_VALUE;
    private boolean lastIcons;

    private float lastX;
    private float lastY;
    private boolean physicsReady;
    private float velocityX;
    private float velocityY;
    private float rotation;
    private float springX;
    private float springY;
    private float dragScale = 1.0f;

    public Watermark() {
        super("Watermark", "Водяной знак клиента. ALT + ЛКМ — перетащить", 0.006f, 0.006f);
        setAnchor(false, false);
        addSettings(style, icons, showLogo, showName, showServer, showFps, showPing, showBps, dragAnimation);
    }

    @Override
    public void onEnable() {
        this.physicsReady = false;
        super.onEnable();
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        this.nickname = minecraft.player == null ? EMPTY : "@" + minecraft.player.getGameProfile().getName();

        ServerData data = minecraft.getCurrentServer();
        this.serverIp = data != null && data.ip != null && !data.ip.isEmpty() ? data.ip : SINGLEPLAYER;

        int fps = this.showFps.get() ? minecraft.getFps() : -1;
        int ping = this.showPing.get() ? readPing(minecraft) : -1;
        int bps = this.showBps.get() ? Math.round(speedBps(minecraft) * 10.0f) : -1;

        boolean metricsChanged = this.lastFps != fps || this.lastPing != ping || this.lastBps != bps;
        this.lastFps = fps;
        this.lastPing = ping;
        this.lastBps = bps;

        if (metricsChanged || this.lastIcons != this.icons.get()) {
            this.lastIcons = this.icons.get();
            setText(this.fps, fps < 0 ? EMPTY : fps + " FPS");
            setText(this.ping, ping < 0 ? EMPTY : ping + " MS");
            setText(this.bps, bps < 0 ? EMPTY : formatBps(bps));
        }

        setText(this.logo, this.showLogo.get() ? BRAND : EMPTY);
        setText(this.name, this.showName.get() ? this.nickname : EMPTY);
        setText(this.server, this.showServer.get() ? this.serverIp : EMPTY);

        this.billboardWidth = Math.max(TILE_MIN_WIDTH,
                Math.max(rowWidth(this.topRow), rowWidth(this.bottomRow)));
    }

    @Override
    protected void measure() {
        setSize(this.billboardWidth, TILE_HEIGHT * 2.0f + TILE_SPACING);
    }

    @Override
    protected void renderContent(PoseStack stack, float alpha, float delta) {
        if (!rowVisible(this.topRow) && !rowVisible(this.bottomRow)) {
            return;
        }

        if (this.dragAnimation.get()) {
            updateDragPhysics(getRenderX(), getRenderY(), delta);
            stack.pushPose();
            applyDragTransform(stack, this.billboardWidth);
        }

        boolean tiles = this.style.is("Плитки");

        drawRow(stack, this.topRow, 0.0f, alpha, tiles, TOP_BACKGROUND);
        drawRow(stack, this.bottomRow, TILE_HEIGHT + TILE_SPACING, alpha, tiles, BOTTOM_BACKGROUND);

        if (this.dragAnimation.get()) {
            stack.popPose();
        }
    }

    private void drawRow(PoseStack stack, Tile[] row, float y, float alpha,
                         boolean tiles, float backgroundAlpha) {
        int theme = ThemeManager.getColor();
        boolean withIcons = this.icons.get();
        float x = 0.0f;

        for (Tile tile : row) {
            if (!tile.active) {
                continue;
            }

            if (tiles) {
                GuiRender.roundedRect(stack, x, y, tile.width, TILE_HEIGHT, TILE_RADIUS,
                        ColorUtils.multAlpha(theme, backgroundAlpha * alpha));
            }

            float textY = y + (TILE_HEIGHT - GuiFonts.SF_MEDIUM.getHeight(tile.text, TEXT_SIZE)) * 0.5f;
            float textX;

            if (withIcons && tile.face != null) {
                float iconY = y + (TILE_HEIGHT - tile.face.getHeight(tile.icon, ICON_SIZE)) * 0.5f;
                tile.face.draw(stack, tile.icon, x + TILE_PADDING_X, iconY,
                        ColorUtils.multAlpha(ICON_COLOR, alpha), ICON_SIZE);
                textX = x + TILE_PADDING_X + ICON_SIZE + ICON_GAP;
            } else {
                float textWidth = GuiFonts.SF_MEDIUM.getWidth(tile.text, TEXT_SIZE);
                textX = x + (tile.width - textWidth) * 0.5f;
            }

            GuiFonts.SF_MEDIUM.draw(stack, tile.text, textX, textY,
                    ColorUtils.multAlpha(WHITE, alpha), TEXT_SIZE);

            x += tile.width + TILE_SPACING;
        }
    }

    private void setText(Tile tile, String text) {
        if (text.equals(tile.text)) {
            return;
        }
        tile.text = text;
        tile.active = !text.isEmpty();
        tile.width = tileWidth(tile);
    }

    private float tileWidth(Tile tile) {
        float textWidth = GuiFonts.SF_MEDIUM.getWidth(tile.text, TEXT_SIZE);
        if (this.icons.get() && tile.face != null) {
            return Math.max(TILE_MIN_WIDTH, TILE_PADDING_X * 2.0f + ICON_SIZE + ICON_GAP + textWidth);
        }
        return Math.max(TILE_MIN_WIDTH, TILE_PADDING_X * 2.0f + textWidth);
    }

    private float rowWidth(Tile[] row) {
        float width = 0.0f;
        int count = 0;

        for (Tile tile : row) {
            if (!tile.active) {
                continue;
            }
            width += tile.width;
            count++;
        }

        return count > 1 ? width + TILE_SPACING * (count - 1) : width;
    }

    private boolean rowVisible(Tile[] row) {
        for (Tile tile : row) {
            if (tile.active) {
                return true;
            }
        }
        return false;
    }

    // Пружина и наклон по скорости курсора — формулы исходного клиента,
    // переведённые с покадрового шага на дельту кадра.
    private void updateDragPhysics(float x, float y, float delta) {
        if (!this.physicsReady) {
            this.lastX = x;
            this.lastY = y;
            this.physicsReady = true;
            return;
        }

        boolean dragging = isDragging();
        float instantX = x - this.lastX;
        float instantY = y - this.lastY;
        float velocitySpeed = dragging ? 22.0f : 8.0f;

        this.velocityX = approach(this.velocityX, instantX, delta, velocitySpeed);
        this.velocityY = approach(this.velocityY, instantY, delta, velocitySpeed);

        float targetRotation = Mth.clamp(this.velocityX * 1.8f - this.velocityY * 0.3f,
                -ROTATION_LIMIT, ROTATION_LIMIT);
        this.rotation = approach(this.rotation, targetRotation, delta, dragging ? 16.0f : 6.0f);
        if (!dragging && Math.abs(this.rotation) < 0.05f) {
            this.rotation = 0.0f;
        }

        this.springX = Mth.clamp(approach(this.springX, -this.velocityX * 0.3f, delta, 12.0f),
                -SPRING_LIMIT, SPRING_LIMIT);
        this.springY = Mth.clamp(approach(this.springY, -this.velocityY * 0.2f, delta, 12.0f),
                -SPRING_LIMIT, SPRING_LIMIT);
        this.dragScale = approach(this.dragScale, dragging ? DRAG_SCALE : 1.0f, delta, 15.0f);

        this.lastX = x;
        this.lastY = y;
    }

    private void applyDragTransform(PoseStack stack, float width) {
        float height = TILE_HEIGHT * 2.0f + TILE_SPACING;
        float centerX = width * 0.5f;
        float centerY = height * 0.5f;

        stack.translate(centerX, centerY, 0.0f);
        stack.scale(this.dragScale, this.dragScale, 1.0f);
        stack.translate(-centerX, -centerY, 0.0f);
        stack.translate(this.springX, this.springY, 0.0f);

        if (Math.abs(this.rotation) > 0.01f) {
            float pivotY = height * 0.2f;
            stack.translate(centerX, pivotY, 0.0f);
            stack.mulPose(Axis.ZP.rotationDegrees(this.rotation));
            stack.translate(-centerX, -pivotY, 0.0f);
        }
    }

    private String formatBps(int bps) {
        return (bps / 10) + "." + (bps % 10) + " BPS";
    }

    private static int readPing(Minecraft minecraft) {
        if (minecraft.player == null || minecraft.getConnection() == null) {
            return -1;
        }
        PlayerInfo info = minecraft.getConnection().getPlayerInfo(minecraft.player.getUUID());
        return info == null ? -1 : info.getLatency();
    }

    private static float speedBps(Minecraft minecraft) {
        if (minecraft.player == null) {
            return 0.0f;
        }
        Vec3 movement = minecraft.player.getDeltaMovement();
        return (float) Math.sqrt(movement.x * movement.x + movement.z * movement.z) * 20.0f;
    }

    private static float approach(float current, float target, float delta, float speed) {
        return current + (target - current) * (1.0f - (float) Math.exp(-delta * speed));
    }

    private static final class Tile {

        private final String icon;
        private final GuiFonts.Face face;

        private String text = EMPTY;
        private boolean active;
        private float width = TILE_MIN_WIDTH;

        private Tile(String icon, GuiFonts.Face face) {
            this.icon = icon;
            this.face = face;
        }
    }
}

package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "CustomDebugOverlay", category = Category.RENDER, description = "Кастомный F3 оверлей")
public class CustomDebugOverlayGui extends Module {
    public static CustomDebugOverlayGui getInstance() {
        return Instance.get(CustomDebugOverlayGui.class);
    }

    final BooleanSetting showCoords = new BooleanSetting(this, "Координаты", true);
    final BooleanSetting showFPS = new BooleanSetting(this, "FPS", true);
    final BooleanSetting showBiome = new BooleanSetting(this, "Биом", true);
    final BooleanSetting showDirection = new BooleanSetting(this, "Направление", true);
    final BooleanSetting showLight = new BooleanSetting(this, "Освещение", true);
    final BooleanSetting showBlock = new BooleanSetting(this, "Блок под курсором", true);
    final BooleanSetting showMemory = new BooleanSetting(this, "Память", true);
    final BooleanSetting showPing = new BooleanSetting(this, "Пинг", false);
    final BooleanSetting blur = new BooleanSetting(this, "Размытие", true);
    final SliderSetting blurStrength = new SliderSetting(this, "Сила размытия", 10f, 5f, 30f, 1f)
            .setVisible(blur::getValue);
    final ModeSetting position = new ModeSetting(this, "Позиция", "Слева сверху", "Справа сверху", "Слева снизу", "Справа снизу");
    final SliderSetting scale = new SliderSetting(this, "Масштаб", 1.0f, 0.5f, 2.0f, 0.1f);
    final BooleanSetting compactMode = new BooleanSetting(this, "Компактный режим", false);

    @Setter
    boolean blurEnabled = true;
    @Setter
    float blurStrengthValue = 10f;

    private final Map<String, TextAnimationData> textAnimations = new HashMap<>();
    private final Animation panelAnimation = new Animation();
    private final Map<String, String> cachedValues = new HashMap<>();
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 100;

    private static class TextAnimationData {
        String currentText = "";
        String targetText = "";
        final Animation alphaAnimation = new Animation();
        float charProgress = 0f;
        long lastChangeTime = 0;

        void update() {
            alphaAnimation.update();
            if (!currentText.equals(targetText)) {
                long elapsed = System.currentTimeMillis() - lastChangeTime;
                if (elapsed > 50) {
                    charProgress += 0.1f;
                    if (charProgress >= 1.0f) {
                        currentText = targetText;
                        charProgress = 0f;
                    }
                }
            }
        }

        String getDisplayText() {
            if (currentText.equals(targetText)) {
                return currentText;
            }
            int targetLen = targetText.length();
            int displayLen = (int) (targetLen * Math.min(charProgress, 1.0f));
            return targetText.substring(0, Math.min(displayLen, targetLen));
        }

        void setTarget(String text) {
            if (!text.equals(targetText)) {
                targetText = text;
                lastChangeTime = System.currentTimeMillis();
                charProgress = 0f;
                alphaAnimation.run(1.0, 0.3, Easings.CUBIC_OUT);
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        panelAnimation.set(0);
        panelAnimation.run(1.0, 0.4, Easings.CUBIC_OUT);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        textAnimations.clear();
        cachedValues.clear();
    }

    @EventHandler
    public void onRender(Render2DEvent event) {
        if (mc.player == null || mc.world == null) return;

        panelAnimation.update();
        float animProgress = (float) panelAnimation.getValue();
        if (animProgress < 0.01f) return;

        updateCachedValues();

        MatrixStack matrix = event.getMatrix();
        float finalScale = scale.getValue();
        List<InfoLine> lines = buildInfoLines();

        if (lines.isEmpty()) return;

        float maxWidth = calculateMaxWidth(lines, finalScale);
        float totalHeight = calculateTotalHeight(lines, finalScale);
        float padding = compactMode.getValue() ? 6f * finalScale : 8f * finalScale;
        float lineSpacing = compactMode.getValue() ? 10f * finalScale : 12f * finalScale;

        float panelWidth = maxWidth + padding * 2;
        float panelHeight = totalHeight + padding * 2;

        float[] pos = calculatePosition(panelWidth, panelHeight);
        float x = pos[0];
        float y = pos[1];

        matrix.push();
        matrix.translate(x + panelWidth / 2f, y + panelHeight / 2f, 0);
        matrix.scale(animProgress, animProgress, 1f);
        matrix.translate(-(x + panelWidth / 2f), -(y + panelHeight / 2f), 0);

        renderPanel(matrix, x, y, panelWidth, panelHeight, finalScale);
        renderLines(matrix, lines, x + padding, y + padding, lineSpacing, finalScale, animProgress);

        matrix.pop();
    }

    private void updateCachedValues() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime < UPDATE_INTERVAL) return;
        lastUpdateTime = currentTime;

        if (mc.player != null) {
            BlockPos pos = mc.player.getPosition();
            updateValue("x", String.format("%.1f", mc.player.getPosX()));
            updateValue("y", String.format("%.1f", mc.player.getPosY()));
            updateValue("z", String.format("%.1f", mc.player.getPosZ()));
            updateValue("blockX", String.valueOf(pos.getX()));
            updateValue("blockY", String.valueOf(pos.getY()));
            updateValue("blockZ", String.valueOf(pos.getZ()));

            Direction facing = mc.player.getHorizontalFacing();
            updateValue("direction", getDirectionString(facing));
            updateValue("yaw", String.format("%.1f", MathHelper.wrapDegrees(mc.player.rotationYaw)));
            updateValue("pitch", String.format("%.1f", MathHelper.wrapDegrees(mc.player.rotationPitch)));

            if (mc.world.isBlockLoaded(pos)) {
                Biome biome = mc.world.getBiome(pos);
                String biomeName = mc.world.func_241828_r().getRegistry(Registry.BIOME_KEY)
                        .getKey(biome).getPath();
                updateValue("biome", biomeName.replace("_", " "));

                int skyLight = mc.world.getLightFor(LightType.SKY, pos);
                int blockLight = mc.world.getLightFor(LightType.BLOCK, pos);
                updateValue("light", skyLight + " sky, " + blockLight + " block");
            }
        }

        updateValue("fps", String.valueOf(Minecraft.getDebugFPS()));

        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = (totalMemory - freeMemory) / 1024 / 1024;
        long maxMemoryMB = maxMemory / 1024 / 1024;
        updateValue("memory", usedMemory + "MB / " + maxMemoryMB + "MB");

        if (mc.getCurrentServerData() != null) {
            updateValue("ping", mc.getCurrentServerData().pingToServer + "ms");
        }

        RayTraceResult rayTrace = mc.objectMouseOver;
        if (rayTrace != null && rayTrace.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockRayTrace = (BlockRayTraceResult) rayTrace;
            BlockPos blockPos = blockRayTrace.getPos();
            BlockState state = mc.world.getBlockState(blockPos);
            String blockName = Registry.BLOCK.getKey(state.getBlock()).getPath();
            updateValue("targetBlock", blockName.replace("_", " "));
            updateValue("targetPos", blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());

            FluidState fluidState = mc.world.getFluidState(blockPos);
            if (!fluidState.isEmpty()) {
                String fluidName = Registry.FLUID.getKey(fluidState.getFluid()).getPath();
                updateValue("targetFluid", fluidName.replace("_", " "));
            } else {
                updateValue("targetFluid", "");
            }
        } else {
            updateValue("targetBlock", "");
            updateValue("targetPos", "");
            updateValue("targetFluid", "");
        }
    }

    private void updateValue(String key, String value) {
        if (value == null) value = "";
        cachedValues.put(key, value);
        TextAnimationData data = textAnimations.computeIfAbsent(key, k -> new TextAnimationData());
        data.setTarget(value);
        data.update();
    }

    private List<InfoLine> buildInfoLines() {
        List<InfoLine> lines = new ArrayList<>();
        int themeColor = Theme.getInstance().clientColor();

        if (showFPS.getValue()) {
            lines.add(new InfoLine("FPS", getAnimatedValue("fps"), themeColor, true));
        }

        if (showCoords.getValue()) {
            lines.add(new InfoLine("XYZ",
                    getAnimatedValue("x") + " / " +
                            getAnimatedValue("y") + " / " +
                            getAnimatedValue("z"), -1, false));
            lines.add(new InfoLine("Block",
                    getAnimatedValue("blockX") + " " +
                            getAnimatedValue("blockY") + " " +
                            getAnimatedValue("blockZ"), ColorUtil.getColor(150), false));
        }

        if (showDirection.getValue()) {
            lines.add(new InfoLine("Facing",
                    getAnimatedValue("direction") + " (" +
                            getAnimatedValue("yaw") + " / " +
                            getAnimatedValue("pitch") + ")", -1, false));
        }

        if (showBiome.getValue()) {
            String biome = getAnimatedValue("biome");
            if (!biome.isEmpty()) {
                lines.add(new InfoLine("Biome", biome, themeColor, false));
            }
        }

        if (showLight.getValue()) {
            String light = getAnimatedValue("light");
            if (!light.isEmpty()) {
                lines.add(new InfoLine("Light", light, ColorUtil.getColor(200), false));
            }
        }

        if (showMemory.getValue()) {
            lines.add(new InfoLine("Memory", getAnimatedValue("memory"), ColorUtil.getColor(180), false));
        }

        if (showPing.getValue() && mc.getCurrentServerData() != null) {
            lines.add(new InfoLine("Ping", getAnimatedValue("ping"), themeColor, true));
        }

        if (showBlock.getValue()) {
            String targetBlock = getAnimatedValue("targetBlock");
            if (!targetBlock.isEmpty()) {
                lines.add(new InfoLine("", "", -1, false));
                lines.add(new InfoLine("Target Block", targetBlock, themeColor, true));
                lines.add(new InfoLine("Position", getAnimatedValue("targetPos"), ColorUtil.getColor(150), false));

                String targetFluid = getAnimatedValue("targetFluid");
                if (!targetFluid.isEmpty()) {
                    lines.add(new InfoLine("Fluid", targetFluid, ColorUtil.getColor(100, 150, 255), false));
                }
            }
        }

        return lines;
    }

    private String getAnimatedValue(String key) {
        TextAnimationData data = textAnimations.get(key);
        if (data == null) return cachedValues.getOrDefault(key, "");
        return data.getDisplayText();
    }

    private float calculateMaxWidth(List<InfoLine> lines, float scale) {
        float maxWidth = 0;
        for (InfoLine line : lines) {
            if (line.label.isEmpty() && line.value.isEmpty()) continue;
            float width = calculateLineWidth(line, scale);
            maxWidth = Math.max(maxWidth, width);
        }
        return maxWidth;
    }

    private float calculateLineWidth(InfoLine line, float scale) {
        if (line.label.isEmpty()) return Fonts.SF_BOLD.getWidth(line.value, 7f * scale);
        float labelWidth = Fonts.SF_BOLD.getWidth(line.label + ": ", 7f * scale);
        float valueWidth = Fonts.SF_MEDIUM.getWidth(line.value, 7f * scale);
        return labelWidth + valueWidth;
    }

    private float calculateTotalHeight(List<InfoLine> lines, float scale) {
        float lineSpacing = compactMode.getValue() ? 10f * scale : 12f * scale;
        return lines.size() * lineSpacing;
    }

    private float[] calculatePosition(float width, float height) {
        float screenWidth = mc.getMainWindow().getScaledWidth();
        float screenHeight = mc.getMainWindow().getScaledHeight();
        float margin = 8f * scale.getValue();

        return switch (position.getValue()) {
            case "Справа сверху" -> new float[]{screenWidth - width - margin, margin};
            case "Слева снизу" -> new float[]{margin, screenHeight - height - margin};
            case "Справа снизу" -> new float[]{screenWidth - width - margin, screenHeight - height - margin};
            default -> new float[]{margin, margin};
        };
    }

    private void renderPanel(MatrixStack matrix, float x, float y, float width, float height, float scale) {
        float radius = 6f * scale;

        if (blur.getValue()) {
            RenderUtil.CustomStyled.draw(matrix, x, y, width, height, Round.of(radius), true, blurStrength.getValue());
        }

        int bgColor = ColorUtil.getColor(0, 0, 0, 100);
        RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, bgColor, Round.of(radius));

        int themeColor = Theme.getInstance().clientColor();
        int borderColor = ColorUtil.setAlpha(themeColor, 60);
        RenderUtil.Rounded.roundedOutline(matrix, x, y, width, height, 1.5f * scale, borderColor, Round.of(radius));
    }

    private void renderLines(MatrixStack matrix, List<InfoLine> lines, float startX, float startY, float lineSpacing, float scale, float alpha) {
        float currentY = startY;

        for (InfoLine line : lines) {
            if (line.label.isEmpty() && line.value.isEmpty()) {
                currentY += lineSpacing * 0.5f;
                continue;
            }

            float currentX = startX;
            int finalAlpha = (int) (255 * alpha);

            if (!line.label.isEmpty()) {
                int labelColor = line.highlight ? line.color : ColorUtil.getColor(200);
                labelColor = ColorUtil.replAlpha(labelColor, finalAlpha);
                Fonts.SF_BOLD.draw(matrix, line.label + ": ", currentX, currentY, labelColor, 7f * scale);
                currentX += Fonts.SF_BOLD.getWidth(line.label + ": ", 7f * scale);
            }

            if (!line.value.isEmpty()) {
                int valueColor = line.highlight ? line.color : ColorUtil.getColor(255, 255, 255);
                valueColor = ColorUtil.replAlpha(valueColor, finalAlpha);
                Fonts.SF_MEDIUM.draw(matrix, line.value, currentX, currentY, valueColor, 7f * scale);
            }

            currentY += lineSpacing;
        }
    }

    private String getDirectionString(Direction dir) {
        return switch (dir) {
            case NORTH -> "North (-Z)";
            case SOUTH -> "South (+Z)";
            case WEST -> "West (-X)";
            case EAST -> "East (+X)";
            default -> "Unknown";
        };
    }

    private record InfoLine(String label, String value, int color, boolean highlight) {
    }
}
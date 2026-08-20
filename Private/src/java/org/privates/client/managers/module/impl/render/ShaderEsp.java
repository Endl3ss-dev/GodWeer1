package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.events.orbit.EventPriority;
import org.privates.client.managers.component.impl.target.TargetComponent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.Project;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.client.utils.render.framebuffer.CustomFramebuffer;
import org.privates.client.utils.render.shader.ShaderManager;
import org.privates.client.utils.render.shader.impl.entity.EntityShader;
import org.privates.client.utils.render.shader.impl.outline.EntityOutlineShader;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ShaderEsp", category = Category.RENDER, description = "Шейдерная красивая подсветка игроков")
public class ShaderEsp extends Module {
    public static ShaderEsp getInstance() {
        return Instance.get(ShaderEsp.class);
    }

    private final CustomFramebuffer buffer = new CustomFramebuffer(true);
    private final EntityShader bloom = new EntityShader();
    private final ModeSetting mode = new ModeSetting(this, "Режим", "Обводка", "Chams", "Обводка + Chams");
    private final BooleanSetting hitboxOnly = new BooleanSetting(this, "Только хитбокс", false);
    private final BooleanSetting filledHitbox = new BooleanSetting(this, "Заливка хитбокса", true).setVisible(hitboxOnly::getValue);
    private final SliderSetting hitboxFillAlpha = new SliderSetting(this, "Прозрачность заливки", 0.3f, 0.1f, 1.0f, 0.05f).setVisible(() -> hitboxOnly.getValue() && filledHitbox.getValue());
    private final ModeSetting chamsMode = new ModeSetting(this, "Chams Mode", "Glow", "Rainbow", "Astral", "Crystal", "Pulse", "Transparent Detail", "Ghost Glass").setVisible(() -> mode.is("Chams") || mode.is("Обводка + Chams"));
    private final SliderSetting iterations = new SliderSetting(this, "Сила", 3, 1, 5, 1);
    private final SliderSetting divider = new SliderSetting(this, "Сила размытия", 8, 1, 8, 0.1F);
    private final BooleanSetting layers = new BooleanSetting(this, "Слои", true).setVisible(() -> !hitboxOnly.getValue());
    private final ColorSetting friendColor = new ColorSetting(this, "Цвет друга", 0xFF00FF00);
    private final ColorSetting enemyColor = new ColorSetting(this, "Цвет врага", 0xFFFF0000);
    private final ColorSetting outlineColor = new ColorSetting(this, "Цвет обводки", 0xFFFFFFFF);
    private final SliderSetting outlineThickness = new SliderSetting(this, "Толщина обводки", 2.0f, 0.5f, 8.0f, 0.1f).setVisible(() -> mode.is("Обводка") || mode.is("Обводка + Chams"));
    private final SliderSetting alphaSetting = new SliderSetting(this, "Прозрачность", 1.0f, 0.1f, 1.0f, 0.01f);
    private final SliderSetting chamsIntensity = new SliderSetting(this, "Интенсивность Chams", 1.0f, 0.1f, 2.0f, 0.1f).setVisible(() -> mode.is("Chams") || mode.is("Обводка + Chams"));
    private final SliderSetting chamsSpeed = new SliderSetting(this, "Скорость анимации", 1.0f, 0.1f, 5.0f, 0.1f).setVisible(() -> mode.is("Chams") || mode.is("Обводка + Chams"));
    private final SliderSetting ghostLayers = new SliderSetting(this, "Ghost layers", 4.0f, 2.0f, 10.0f, 1.0f).setVisible(() -> (mode.is("Chams") || mode.is("Обводка + Chams")) && chamsMode.is("Ghost Glass"));
    private final SliderSetting ghostShift = new SliderSetting(this, "Ghost shift", 0.08f, 0.02f, 0.2f, 0.01f).setVisible(() -> (mode.is("Chams") || mode.is("Обводка + Chams")) && chamsMode.is("Ghost Glass"));
    private final SliderSetting ghostWidth = new SliderSetting(this, "Ghost width", 1.45f, 0.8f, 2.5f, 0.05f).setVisible(() -> (mode.is("Chams") || mode.is("Обводка + Chams")) && chamsMode.is("Ghost Glass"));
    private final SliderSetting ghostAlpha = new SliderSetting(this, "Ghost alpha", 120.0f, 20.0f, 220.0f, 5.0f).setVisible(() -> (mode.is("Chams") || mode.is("Обводка + Chams")) && chamsMode.is("Ghost Glass"));
    private final SliderSetting transparentOutlineWidth = new SliderSetting(this, "Ширина обводки (TD)", 1.5f, 0.5f, 4.0f, 0.5f).setVisible(() -> (mode.is("Chams") || mode.is("Обводка + Chams")) && chamsMode.is("Transparent Detail"));
    private final SliderSetting transparentFillAlpha = new SliderSetting(this, "Прозрачность заливки (TD)", 0.2f, 0.05f, 0.6f, 0.05f).setVisible(() -> (mode.is("Chams") || mode.is("Обводка + Chams")) && chamsMode.is("Transparent Detail"));
    private final ColorSetting transparentOutlineColorSetting = new ColorSetting(this, "Цвет обводки (TD)", 0xFFFFFFFF).setVisible(() -> (mode.is("Chams") || mode.is("Обводка + Chams")) && chamsMode.is("Transparent Detail"));
    private final ModeSetting styleSetting = new ModeSetting(this, "Стиль", "Минимализм", "Классика", "Soft");
    
    private float chamsTime = 0.0f;
    private final Tessellator tessellator = Tessellator.getInstance();
    private final BufferBuilder bufferBuilder = tessellator.getBuffer();

    private boolean isPlayerAimingDangerous(PlayerEntity local, PlayerEntity target) {
        ItemStack main = local.getHeldItem(Hand.MAIN_HAND);
        ItemStack off = local.getHeldItem(Hand.OFF_HAND);
        double reach = 64.0D;
        Vector3d eyePos = local.getEyePosition(1.0F);
        Vector3d look = local.getLook(1.0F);
        Vector3d end = eyePos.add(look.x * reach, look.y * reach, look.z * reach);
        AxisAlignedBB aabb = target.getBoundingBox().grow(0.5);
        boolean crosshairOnTarget = aabb.rayTrace(eyePos, end).isPresent();

        boolean dangerous = isDangerousItem(main) || isDangerousItem(off);
        return dangerous && crosshairOnTarget;
    }

    private boolean isDangerousItem(ItemStack stack) {
        return stack != null && !stack.isEmpty() && (
                stack.getItem() == Items.CROSSBOW ||
                        stack.getItem() == Items.BOW ||
                        stack.getItem() == Items.TRIDENT ||
                        stack.getItem() == Items.SNOWBALL
        );
    }

    public void patch(PlayerEntity entity, Runnable runnable) {
        Vector3d interpolated = entity.getPositionVec().subtract(entity.getPositionVec(mc.getRenderPartialTicks()));
        AxisAlignedBB aabb = entity.getBoundingBox().offset(interpolated.inverse().add(interpolated.scale(mc.getRenderPartialTicks())));
        Vector2f center = Project.project2D(aabb.getCenter());

        if (center.x == Float.MAX_VALUE && center.y == Float.MAX_VALUE) {
            return;
        }

        float minX = center.x, minY = center.y, maxX = center.x, maxY = center.y;

        for (Vector3d corner : aabb.getCorners()) {
            Vector2f vec = Project.project2D(corner);

            if (vec.x == Float.MAX_VALUE && vec.y == Float.MAX_VALUE) {
                continue;
            }

            minX = Math.min(minX, vec.x);
            minY = Math.min(minY, vec.y);
            maxX = Math.max(maxX, vec.x);
            maxY = Math.max(maxY, vec.y);
        }

        float posX = minX, posY = minY, width = maxX - minX, height = maxY - minY;

        float hurtPC = (float) Math.sin(entity.hurtTime * (18F * Math.PI / 180F));
        boolean isFriend = Excellent.inst().friendManager().isFriend(entity.getGameProfile().getName());

        // Цвет по умолчанию (дружеский или тема)
        int baseColor = isFriend ? ColorUtil.GREEN : Theme.getInstance().clientColor();

        // <<<< ВАЖНОЕ МЕСТО: выделить красным если наведен опасный предмет >>>>
        PlayerEntity local = mc.player;
        if (local != null && local != entity && isPlayerAimingDangerous(local, entity)) {
            baseColor = ColorUtil.RED;
        }

        int color = ColorUtil.overCol(baseColor, ColorUtil.RED, hurtPC);

        // Выбор шейдера в зависимости от режима Chams
        boolean useChams = mode.is("Chams") || mode.is("Обводка + Chams");
        
        if (useChams) {
            applyChamsShader(posX, posY, width, height, color, entity);
        } else {
            // Старый шейдер для обводки
            ShaderManager gradient = ShaderManager.entityChamsShader;
            gradient.load();
            gradient.setUniformi("tex", 0);
            gradient.setUniformf("location", posX, posY);
            gradient.setUniformf("rectSize", width, height);
            gradient.setUniformf("color", ColorUtil.getRGBAf(color));
        }

        runnable.run();

        if (useChams) {
            unloadChamsShader();
        } else {
            ShaderManager.entityChamsShader.unload();
        }
    }
    
    private void applyChamsShader(float posX, float posY, float width, float height, int color, PlayerEntity entity) {
        chamsTime += 0.016f * chamsSpeed.getValue(); // ~60 FPS
        
        float intensity = chamsIntensity.getValue();
        float alpha = alphaSetting.getValue();
        
        switch (chamsMode.getValue()) {
            case "Glow" -> {
                ShaderManager gradient = ShaderManager.entityChamsShader;
                gradient.load();
                gradient.setUniformi("tex", 0);
                gradient.setUniformf("location", posX, posY);
                gradient.setUniformf("rectSize", width, height);
                
                // Пульсирующее свечение
                float pulse = (float) (0.7f + 0.3f * Math.sin(chamsTime * 2.0f));
                int glowColor = ColorUtil.replAlpha(color, (int) (alpha * pulse * 255));
                gradient.setUniformf("color", ColorUtil.getRGBAf(glowColor));
                gradient.setUniformf("intensity", intensity);
            }
            
            case "Rainbow" -> {
                ShaderManager gradient = ShaderManager.entityChamsShader;
                gradient.load();
                gradient.setUniformi("tex", 0);
                gradient.setUniformf("location", posX, posY);
                gradient.setUniformf("rectSize", width, height);
                
                // Радужный эффект
                float hue = (chamsTime * 0.5f) % 1.0f;
                int rainbowColor = getColorFromHSB(hue, 0.8f, 1.0f);
                rainbowColor = ColorUtil.replAlpha(rainbowColor, (int) (alpha * 255));
                gradient.setUniformf("color", ColorUtil.getRGBAf(rainbowColor));
                gradient.setUniformf("time", chamsTime);
            }
            
            case "Astral" -> {
                ShaderManager gradient = ShaderManager.entityChamsShader;
                gradient.load();
                gradient.setUniformi("tex", 0);
                gradient.setUniformf("location", posX, posY);
                gradient.setUniformf("rectSize", width, height);
                
                // Астральный эффект с волнами
                float wave1 = (float) Math.sin(chamsTime * 1.5f) * 0.5f + 0.5f;
                float wave2 = (float) Math.cos(chamsTime * 2.0f) * 0.5f + 0.5f;
                int astralColor = ColorUtil.interpolateColor(color, ColorUtil.getColor(100, 150, 255), wave1);
                astralColor = ColorUtil.replAlpha(astralColor, (int) (alpha * 255 * (0.6f + wave2 * 0.4f)));
                gradient.setUniformf("color", ColorUtil.getRGBAf(astralColor));
                gradient.setUniformf("time", chamsTime);
                gradient.setUniformf("intensity", intensity);
            }
            
            case "Crystal" -> {
                ShaderManager gradient = ShaderManager.entityChamsShader;
                gradient.load();
                gradient.setUniformi("tex", 0);
                gradient.setUniformf("location", posX, posY);
                gradient.setUniformf("rectSize", width, height);
                
                // Кристальный эффект с переливами
                float shimmer = (float) (Math.sin(chamsTime * 3.0f) * 0.3f + 0.7f);
                int crystalColor = ColorUtil.interpolateColor(color, ColorUtil.getColor(200, 220, 255), 0.4f);
                crystalColor = ColorUtil.replAlpha(crystalColor, (int) (alpha * 255 * shimmer));
                gradient.setUniformf("color", ColorUtil.getRGBAf(crystalColor));
                gradient.setUniformf("time", chamsTime);
                gradient.setUniformf("intensity", intensity * 1.5f);
            }
            
            case "Pulse" -> {
                ShaderManager gradient = ShaderManager.entityChamsShader;
                gradient.load();
                gradient.setUniformi("tex", 0);
                gradient.setUniformf("location", posX, posY);
                gradient.setUniformf("rectSize", width, height);
                
                // Пульсирующий эффект
                float pulse = (float) Math.abs(Math.sin(chamsTime * 2.5f));
                int pulseColor = ColorUtil.replAlpha(color, (int) (alpha * 255 * (0.3f + pulse * 0.7f)));
                gradient.setUniformf("color", ColorUtil.getRGBAf(pulseColor));
                gradient.setUniformf("time", chamsTime);
                gradient.setUniformf("intensity", intensity * (0.5f + pulse * 1.5f));
            }
            
            case "Transparent Detail" -> {
                ShaderManager gradient = ShaderManager.entityTransparentOutlineShader;
                gradient.load();
                gradient.setUniformi("tex", 0);
                gradient.setUniformf("location", posX, posY);
                gradient.setUniformf("rectSize", width, height);
                
                // Прозрачный шейдер с детальной обводкой
                int fillColor = ColorUtil.replAlpha(color, (int) (transparentFillAlpha.getValue() * 255));
                gradient.setUniformf("color", ColorUtil.getRGBAf(fillColor));
                
                // Цвет обводки
                int outlineColorValue = transparentOutlineColorSetting.getValue();
                gradient.setUniformf("outlineColor", ColorUtil.getRGBAf(outlineColorValue));
                
                // Параметры обводки
                gradient.setUniformf("outlineWidth", transparentOutlineWidth.getValue());
                gradient.setUniformf("fillAlpha", transparentFillAlpha.getValue() * alpha);
                gradient.setUniformf("time", chamsTime);
            }
        }
    }
    
    private void unloadChamsShader() {
        ShaderManager.entityChamsShader.unload();
        ShaderManager.entityTransparentOutlineShader.unload();
    }
    
    private int getColorFromHSB(float hue, float saturation, float brightness) {
        return java.awt.Color.HSBtoRGB(hue, saturation, brightness);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEvent(Render3DPosedEvent event) {
        MatrixStack stack = event.getMatrix();
        buffer.setup();
        boolean useGhostGlass = !hitboxOnly.getValue()
                && (mode.is("Chams") || mode.is("РћР±РІРѕРґРєР° + Chams"))
                && chamsMode.is("Ghost Glass");
        useGhostGlass = useGhostGlass || (!hitboxOnly.getValue() && mode.getValue().contains("Chams") && chamsMode.is("Ghost Glass"));

        if (hitboxOnly.getValue()) {
            // Рендерим только хитбоксы с шейдерами
            renderHitboxes(event);
        } else if (useGhostGlass) {
            renderGhostGlassTargets(event);
        } else {
            // Стандартный рендеринг полной модели
            TargetComponent.getTargets(128, this::isValid, false)
                    .forEach(entity -> {
                        if (entity instanceof PlayerEntity player) {
                            patch(player, () -> {
                                EntityRendererManager rendererManager = mc.getRenderManager();
                                stack.push();
                                stack.translate(-rendererManager.renderPosX(), -rendererManager.renderPosY(), -rendererManager.renderPosZ());
                                RenderSystem.depthMask(true);
                                rendererManager.setRenderShadow(false);
                                rendererManager.setRenderName(false);
                                IRenderTypeBuffer.Impl irendertypebuffer$impl = mc.getRenderTypeBuffers().getBufferSource();
                                Vector3d pos = entity.getPositionVec(event.getPartialTicks());
                                EntityRenderer<?> renderer = rendererManager.getRenderer(entity);
                                boolean nameVisible = renderer.isRenderName();

                                if (nameVisible) renderer.setRenderName(false);
                                if (!layers.getValue()) renderer.setRenderLayers(false);
                                rendererManager.renderClearEntityStatic(entity, pos.getX(), pos.getY(), pos.getZ(), entity.rotationYaw, event.getPartialTicks(), stack, irendertypebuffer$impl, rendererManager.getPackedLight(entity, event.getPartialTicks()));
                                if (!layers.getValue()) renderer.setRenderLayers(true);
                                if (nameVisible) renderer.setRenderName(true);

                                irendertypebuffer$impl.finish();

                                rendererManager.setRenderName(true);
                                rendererManager.setRenderShadow(true);
                                RenderSystem.depthMask(false);
                                RenderSystem.enableDepthTest();
                                stack.pop();
                            });
                        }
                    });
        }

        buffer.stop();
    }
    
    private void renderGhostGlassTargets(Render3DPosedEvent event) {
        MatrixStack matrixStack = event.getMatrix();
        float partialTicks = event.getPartialTicks();
        Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        TargetComponent.getTargets(128, this::isValid, false).forEach(entity -> {
            if (!(entity instanceof PlayerEntity player)) return;

            float hurtPC = (float) Math.sin(player.hurtTime * (18F * Math.PI / 180F));
            boolean isFriend = Excellent.inst().friendManager().isFriend(player.getGameProfile().getName());
            int baseColor = isFriend ? ColorUtil.GREEN : Theme.getInstance().clientColor();

            PlayerEntity local = mc.player;
            if (local != null && local != player && isPlayerAimingDangerous(local, player)) {
                baseColor = ColorUtil.RED;
            }

            int color = ColorUtil.overCol(baseColor, ColorUtil.RED, hurtPC);
            Vector3d playerPos = RenderUtil3D.interpolate(player, partialTicks).subtract(cameraPos);

            matrixStack.push();
            matrixStack.translate(playerPos.x, playerPos.y, playerPos.z);
            drawGhostFigure(matrixStack, player, color, partialTicks);
            matrixStack.pop();
        });

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.defaultBlendFunc();
    }

    private void drawGhostFigure(MatrixStack matrixStack, PlayerEntity player, int color, float partialTicks) {
        float bodyYaw = MathHelper.interpolateAngle(partialTicks, player.prevRenderYawOffset, player.renderYawOffset);
        float halfWidth = (player.getWidth() * 0.5f) * ghostWidth.getValue();
        float height = player.getHeight();
        float thickness = 0.015f * ghostWidth.getValue();
        int layerCount = ghostLayers.getValue().intValue();
        float baseAlpha = ghostAlpha.getValue();
        float time = (System.currentTimeMillis() % 2600L) / 2600.0f;
        float bob = (float) Math.sin(time * Math.PI * 2.0) * 0.05f;

        matrixStack.push();
        matrixStack.rotate(Vector3f.YP.rotationDegrees(-bodyYaw));

        for (int i = 0; i < layerCount; i++) {
            float t = i / (float) Math.max(1, layerCount - 1);
            float layerShiftY = (t - 0.5f) * (ghostShift.getValue() * 2.0f);
            float floatY = (float) Math.sin((time * Math.PI * 2.0) + t * 1.7f) * 0.03f;
            float layerScale = 1.0f + t * 0.12f;

            int fillAlpha = Math.max(14, (int) (baseAlpha * (1.0f - t * 0.62f)));
            int lineAlpha = Math.min(255, fillAlpha + 55);

            matrixStack.push();
            matrixStack.translate(0.0, bob + layerShiftY + floatY, 0.0);

            drawGhostPlate(matrixStack,
                    -halfWidth * layerScale, 0.04f, -thickness,
                    halfWidth * layerScale, height - 0.04f, thickness,
                    color, fillAlpha, lineAlpha);

            matrixStack.push();
            matrixStack.rotate(Vector3f.YP.rotationDegrees(38.0f));
            drawGhostPlate(matrixStack,
                    -(halfWidth * 0.82f) * layerScale, 0.06f, -thickness,
                    (halfWidth * 0.82f) * layerScale, height - 0.06f, thickness,
                    color, Math.max(10, fillAlpha - 10), Math.max(20, lineAlpha - 15));
            matrixStack.pop();

            matrixStack.push();
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-38.0f));
            drawGhostPlate(matrixStack,
                    -(halfWidth * 0.82f) * layerScale, 0.06f, -thickness,
                    (halfWidth * 0.82f) * layerScale, height - 0.06f, thickness,
                    color, Math.max(10, fillAlpha - 10), Math.max(20, lineAlpha - 15));
            matrixStack.pop();

            matrixStack.pop();
        }

        matrixStack.pop();
    }

    private void drawGhostPlate(MatrixStack matrixStack,
                                float minX, float minY, float minZ,
                                float maxX, float maxY, float maxZ,
                                int color, int fillAlpha, int lineAlpha) {
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;

        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        RenderUtil3D.renderFilledBox(
                bufferBuilder, matrixStack,
                minX, minY, minZ,
                maxX, maxY, maxZ,
                red, green, blue, fillAlpha / 255.0f
        );
        tessellator.draw();

        RenderSystem.lineWidth(1.0f);
        bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        RenderUtil3D.renderWireframeBox(
                bufferBuilder, matrixStack,
                minX, minY, minZ,
                maxX, maxY, maxZ,
                220f / 255f, 240f / 255f, 1.0f, lineAlpha / 255.0f
        );
        tessellator.draw();
    }

    private void renderHitboxes(Render3DPosedEvent event) {
        MatrixStack matrixStack = event.getMatrix();
        float partialTicks = event.getPartialTicks();
        Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();

        setupHitboxRender();

        TargetComponent.getTargets(128, this::isValid, false).forEach(entity -> {
            if (entity instanceof PlayerEntity player) {
                float hurtPC = (float) Math.sin(player.hurtTime * (18F * Math.PI / 180F));
                boolean isFriend = Excellent.inst().friendManager().isFriend(player.getGameProfile().getName());
                int baseColor = isFriend ? ColorUtil.GREEN : Theme.getInstance().clientColor();
                
                PlayerEntity local = mc.player;
                if (local != null && local != player && isPlayerAimingDangerous(local, player)) {
                    baseColor = ColorUtil.RED;
                }
                
                int color = ColorUtil.overCol(baseColor, ColorUtil.RED, hurtPC);
                float red = ((color >> 16) & 0xFF) / 255.0F;
                float green = ((color >> 8) & 0xFF) / 255.0F;
                float blue = (color & 0xFF) / 255.0F;
                float alpha = alphaSetting.getValue();

                Vector3d playerPos = RenderUtil3D.interpolate(player, partialTicks).subtract(cameraPos);

                matrixStack.push();
                matrixStack.translate(playerPos.x, playerPos.y, playerPos.z);

                // Применяем шейдер перед рендерингом хитбокса
                patch(player, () -> {
                    drawHitbox(matrixStack, player, red, green, blue, alpha);
                });

                matrixStack.pop();
            }
        });

        releaseHitboxRender();
    }
    
    private void drawHitbox(MatrixStack matrixStack, PlayerEntity player, float r, float g, float b, float alpha) {
        float halfWidth = player.getWidth() / 2.0f;
        float height = player.getHeight();

        // Заливка хитбокса
        if (filledHitbox.getValue()) {
            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            RenderUtil3D.renderFilledBox(bufferBuilder, matrixStack, -halfWidth, 0, -halfWidth, halfWidth, height, halfWidth, r, g, b, hitboxFillAlpha.getValue() * alpha);
            tessellator.draw();
        }

        // Обводка хитбокса
        GL11.glLineWidth(outlineThickness.getValue());
        bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        RenderUtil3D.renderWireframeBox(bufferBuilder, matrixStack, -halfWidth, 0, -halfWidth, halfWidth, height, halfWidth, r, g, b, alpha);
        tessellator.draw();
    }
    
    private void setupHitboxRender() {
        RenderSystem.pushMatrix();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
    }
    
    private void releaseHitboxRender() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEvent(Render2DEvent event) {
        boolean showOutline = mode.is("Обводка") || mode.is("Обводка + Chams");
        boolean showChams = mode.is("Chams") || mode.is("Обводка + Chams");
        
        if (showOutline) {
            EntityOutlineShader.draw(1, buffer.framebufferTexture);
        }
        
        bloom.render(event.getMatrix(), buffer.framebufferTexture, iterations.getValue().intValue(), 1F, 4 + divider.getValue());
        
        if (showChams) {
            buffer.draw();
        }
        
        buffer.framebufferClear();
        mc.getFramebuffer().bindFramebuffer(true);
    }

    private boolean isValid(final Entity entity) {
        if (!entity.isAlive() || entity.isGlowing()) {
            return false;
        }
        if (mc.renderViewEntity != null && entity == mc.renderViewEntity && mc.gameSettings.getPointOfView().firstPerson()) {
            return false;
        }
        return isInView(entity) && entity instanceof PlayerEntity;
    }

    public boolean isInView(Entity entity) {
        if (mc.getRenderViewEntity() == null || mc.worldRenderer.getClippinghelper() == null) {
            return false;
        }
        return mc.worldRenderer.getClippinghelper().isBoundingBoxInFrustum(entity.getBoundingBox()) || entity.ignoreFrustumCheck;
    }
}

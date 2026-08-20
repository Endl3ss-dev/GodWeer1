package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.interfaces.IRender;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.Project;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;

import java.util.ArrayList;
import java.util.List;

import static org.privates.client.screen.hud.IRenderer.font;
import static org.privates.client.screen.hud.IRenderer.fontSize;

@Category2
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "PotionTrajectory", category = Category.RENDER, description = "Траектория полёта зелья")
public class PotionTrajectory extends Module implements IRender {

    public static PotionTrajectory getInstance() {
        return Instance.get(PotionTrajectory.class);
    }

    // Настройки модуля
    final BooleanSetting showPath = new BooleanSetting(this, "Показать траекторию", true);
    final BooleanSetting showRadius = new BooleanSetting(this, "Показать радиус", true);
    final BooleanSetting showTimer = new BooleanSetting(this, "Показать таймер", true);
    final BooleanSetting showEffectName = new BooleanSetting(this, "Показать название эффекта", true);
    final SliderSetting lineWidth = new SliderSetting(this, "Толщина линии", 2.0f, 0.5f, 5.0f, 0.1f);
    final ColorSetting trajectoryColor = new ColorSetting(this, "Цвет траектории");


    // Списки для хранения данных о зельях
    final List<ThrowablePoint> throwablePoints = new ArrayList<>();
    final List<PotionRadius> potionRadii = new ArrayList<>();

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (!showTimer.getValue() && !showEffectName.getValue()) return;

        MatrixStack matrix = event.getMatrix();

        // Отрисовка информации о точках приземления
        for (ThrowablePoint throwablePoint : throwablePoints) {
            Vector3d pos = throwablePoint.position;
            org.joml.Vector2f projection = Project.project2D(pos.x, pos.y - 0.3, pos.z);

            if (projection.x != Float.MAX_VALUE && projection.y != Float.MAX_VALUE) {
                int ticks = throwablePoint.ticks;
                boolean isMoving = ticks > 0;

                if (isMoving && showTimer.getValue()) {
                    double time = (double) (ticks * 50) / 1000.0;
                    String text = String.format("%.1f сек.", time);
                    float textWidth = font.getWidth(text, fontSize) + 16f;

                    float posX = projection.x - textWidth / 2.0f;
                    float posY = projection.y - 10f;

                    // Фон для текста
                    int bgColor = ColorUtil.getColor(0, 0, 0, 120);
                    RectUtil.drawRect(matrix, posX, posY, textWidth, 12f, bgColor);

                    // Иконка зелья
                    RenderUtil.bindTexture(throwablePoint.texture);
                    RenderUtil.drawImage(throwablePoint.texture, matrix, posX + 4, posY + 2, 8, 8, ColorUtil.WHITE);

                    // Текст таймера
                    font.draw(matrix, text, posX + 14, posY + 4, ColorUtil.WHITE, fontSize);
                }
            }
        }

        // Отрисовка информации о радиусе эффектов
        for (PotionRadius radius : potionRadii) {
            org.joml.Vector2f projection = Project.project2D(radius.position.x, radius.position.y, radius.position.z);

            if (projection.x != Float.MAX_VALUE && projection.y != Float.MAX_VALUE) {
                if (showEffectName.getValue()) {
                    String effectText = getEffectText(radius.potionType);
                    String radiusText = String.format("%.1f блоков", radius.radius);

                    float effectWidth = font.getWidth(effectText, fontSize);
                    float radiusWidth = font.getWidth(radiusText, fontSize - 1);

                    float posX = projection.x - Math.max(effectWidth, radiusWidth) / 2.0f;
                    float posY = projection.y - 30.0f;

                    // Название эффекта
                    font.draw(matrix, effectText, posX, posY, getTextColor(radius.potionType), fontSize);
                    // Радиус действия
                    font.draw(matrix, radiusText, posX, posY + 10.0f, ColorUtil.getColor(200, 200, 200), fontSize - 1);
                }
            }
        }
    }

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        MatrixStack matrix = event.getMatrix();

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrix.getLast().getMatrix());
        RenderSystem.translated(-mc.getRenderManager().renderPosX(), -mc.getRenderManager().renderPosY(), -mc.getRenderManager().renderPosZ());

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.lineWidth(lineWidth.getValue());
        RenderSystem.color4f(1f, 1f, 1f, 1f);

        // Очищаем списки
        throwablePoints.clear();
        potionRadii.clear();

        // Обрабатываем все зелья в мире
        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof PotionEntity potion && isExplosivePotion(potion)) {
                if (showPath.getValue()) {
                    renderTrajectoryLine(potion);
                }
                processPotion(potion);
            }
        }

        // Отрисовка радиусов эффектов
        if (showRadius.getValue() && !potionRadii.isEmpty()) {
            drawPotionRadii();
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.translated(mc.getRenderManager().renderPosX(), mc.getRenderManager().renderPosY(), mc.getRenderManager().renderPosZ());
        RenderSystem.popMatrix();
    }

    private void renderTrajectoryLine(PotionEntity potion) {
        Vector3d motion = potion.getMotion();
        Vector3d pos = potion.getPositionVec();

        BUFFER.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        int trajectoryColor = this.trajectoryColor.getValue();

        for (int i = 0; i < 200; i++) {
            Vector3d prevPos = pos;
            pos = pos.add(motion);
            motion = getNextMotion(potion, motion);

            // Проверяем столкновение с блоками
            RayTraceContext rayTraceContext = new RayTraceContext(
                    prevPos, pos,
                    RayTraceContext.BlockMode.COLLIDER,
                    RayTraceContext.FluidMode.NONE,
                    potion
            );

            BlockRayTraceResult blockHitResult = mc.world.rayTraceBlocks(rayTraceContext);
            boolean isLast = blockHitResult.getType() == RayTraceResult.Type.BLOCK;

            if (isLast) {
                pos = blockHitResult.getHitVec();
            }

            // Добавляем точки траектории
            BUFFER.pos(prevPos.x, prevPos.y, prevPos.z).color(trajectoryColor).endVertex();
            BUFFER.pos(pos.x, pos.y, pos.z).color(trajectoryColor).endVertex();

            // Проверяем условия остановки
            if (blockHitResult.getType() == RayTraceResult.Type.BLOCK || pos.y < -64.0) {
                break;
            }
        }

        TESSELLATOR.draw();
    }

    private void processPotion(PotionEntity potion) {
        Vector3d motion = potion.getMotion();
        Vector3d pos = potion.getPositionVec();
        int ticks = 0;
        String potionType = getPotionType(potion.getItem());

        // Текстура зелья
        ResourceLocation texture = new ResourceLocation("textures/item/splash_potion.png");

        for (int i = 0; i < 200; i++) {
            Vector3d prevPos = pos;
            pos = pos.add(motion);
            motion = getNextMotion(potion, motion);

            // Проверяем столкновение с блоками
            RayTraceContext rayTraceContext = new RayTraceContext(
                    prevPos, pos,
                    RayTraceContext.BlockMode.COLLIDER,
                    RayTraceContext.FluidMode.NONE,
                    potion
            );

            BlockRayTraceResult blockHitResult = mc.world.rayTraceBlocks(rayTraceContext);
            boolean isLast = blockHitResult.getType() == RayTraceResult.Type.BLOCK;

            if (isLast) {
                pos = blockHitResult.getHitVec();
                // Добавляем радиус эффекта в точке приземления
                potionRadii.add(new PotionRadius(pos, getRadiusForPotion(potionType), potionType));
            }

            // Проверяем условия остановки
            if (blockHitResult.getType() == RayTraceResult.Type.BLOCK || pos.y < -64.0) {
                if (motion.lengthSquared() >= 0.04) {
                    throwablePoints.add(new ThrowablePoint(pos, ticks, texture));
                }
                break;
            }

            ticks++;
        }
    }

    private void drawPotionRadii() {
        // Отрисовка заполненных кругов (фон радиуса)
        BUFFER.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);

        for (PotionRadius radius : potionRadii) {
            Vector3d pos = radius.position;
            float r = radius.radius;
            int color = getPotionColor(radius.potionType);

            // Центр круга
            BUFFER.pos(pos.x, pos.y, pos.z).color(ColorUtil.multAlpha(color, 0.3f)).endVertex();

            // Окружность
            for (int i = 0; i <= 360; i += 10) {
                double x = pos.x + Math.sin(Math.toRadians(i)) * r;
                double z = pos.z + Math.cos(Math.toRadians(i)) * r;
                BUFFER.pos(x, pos.y, z).color(ColorUtil.multAlpha(color, 0.1f)).endVertex();
            }
        }

        TESSELLATOR.draw();

        // Отрисовка контуров радиусов
        BUFFER.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);

        for (PotionRadius radius : potionRadii) {
            Vector3d pos = radius.position;
            float r = radius.radius;
            int color = getPotionColor(radius.potionType);

            for (int i = 0; i <= 360; i += 5) {
                double x = pos.x + Math.sin(Math.toRadians(i)) * r;
                double z = pos.z + Math.cos(Math.toRadians(i)) * r;
                BUFFER.pos(x, pos.y, z).color(color).endVertex();
            }
        }

        TESSELLATOR.draw();
    }

    private String getEffectText(String potionType) {
        return switch (potionType) {
            case "HARMING" -> "Урон";
            case "POISON" -> "Яд";
            case "SLOWNESS" -> "Замедление";
            case "WEAKNESS" -> "Слабость";
            case "INSTANT_HEALTH" -> "Лечение";
            case "REGENERATION" -> "Регенерация";
            case "FIRE_RESISTANCE" -> "Огнестойкость";
            case "WATER_BREATHING" -> "Подводное дыхание";
            case "INVISIBILITY" -> "Невидимость";
            case "NIGHT_VISION" -> "Ночное зрение";
            case "JUMP_BOOST" -> "Прыжок";
            case "SPEED" -> "Скорость";
            case "STRENGTH" -> "Сила";
            default -> "Неизвестный эффект";
        };
    }

    private int getTextColor(String potionType) {
        return switch (potionType) {
            case "HARMING" -> ColorUtil.getColor(255, 80, 80);
            case "POISON" -> ColorUtil.getColor(80, 255, 80);
            case "SLOWNESS" -> ColorUtil.getColor(80, 80, 255);
            case "WEAKNESS" -> ColorUtil.getColor(150, 150, 150);
            case "INSTANT_HEALTH" -> ColorUtil.getColor(0, 200, 0);
            case "REGENERATION" -> ColorUtil.getColor(255, 105, 180);
            case "FIRE_RESISTANCE" -> ColorUtil.getColor(255, 140, 0);
            case "WATER_BREATHING" -> ColorUtil.getColor(0, 150, 200);
            case "INVISIBILITY" -> ColorUtil.getColor(150, 150, 150);
            case "NIGHT_VISION" -> ColorUtil.getColor(100, 0, 200);
            case "JUMP_BOOST" -> ColorUtil.getColor(0, 255, 150);
            case "SPEED" -> ColorUtil.getColor(100, 255, 255);
            case "STRENGTH" -> ColorUtil.getColor(255, 140, 0);
            default -> ColorUtil.WHITE;
        };
    }

    private int getPotionColor(String potionType) {
        return switch (potionType) {
            case "HARMING" -> ColorUtil.getColor(255, 0, 0, 100);
            case "POISON" -> ColorUtil.getColor(0, 255, 0, 100);
            case "SLOWNESS" -> ColorUtil.getColor(0, 0, 255, 100);
            case "WEAKNESS" -> ColorUtil.getColor(128, 128, 128, 100);
            case "INSTANT_HEALTH" -> ColorUtil.getColor(0, 200, 0, 80);
            case "REGENERATION" -> ColorUtil.getColor(255, 105, 180, 80);
            case "FIRE_RESISTANCE" -> ColorUtil.getColor(255, 140, 0, 80);
            case "WATER_BREATHING" -> ColorUtil.getColor(0, 150, 200, 80);
            case "INVISIBILITY" -> ColorUtil.getColor(150, 150, 150, 80);
            case "NIGHT_VISION" -> ColorUtil.getColor(100, 0, 200, 80);
            case "JUMP_BOOST" -> ColorUtil.getColor(0, 255, 150, 80);
            case "SPEED" -> ColorUtil.getColor(100, 255, 255, 80);
            case "STRENGTH" -> ColorUtil.getColor(255, 140, 0, 80);
            default -> ColorUtil.getColor(255, 165, 0, 100);
        };
    }

    private String getPotionType(ItemStack potionStack) {
        List<EffectInstance> effects = PotionUtils.getEffectsFromStack(potionStack);
        if (effects.isEmpty()) {
            return "OTHER";
        }

        EffectInstance mainEffect = effects.get(0);
        ResourceLocation effectId = Registry.EFFECTS.getKey(mainEffect.getPotion());

        if (effectId == null) {
            return "OTHER";
        }

        return switch (effectId.getPath()) {
            case "instant_damage" -> "HARMING";
            case "poison" -> "POISON";
            case "slowness" -> "SLOWNESS";
            case "weakness" -> "WEAKNESS";
            case "instant_health" -> "INSTANT_HEALTH";
            case "regeneration" -> "REGENERATION";
            case "fire_resistance" -> "FIRE_RESISTANCE";
            case "water_breathing" -> "WATER_BREATHING";
            case "invisibility" -> "INVISIBILITY";
            case "night_vision" -> "NIGHT_VISION";
            case "jump_boost" -> "JUMP_BOOST";
            case "speed" -> "SPEED";
            case "strength" -> "STRENGTH";
            default -> "OTHER";
        };
    }

    private float getRadiusForPotion(String potionType) {
        return switch (potionType) {
            case "HARMING" -> 4.0f;
            case "POISON" -> 3.5f;
            case "SLOWNESS" -> 3.0f;
            case "WEAKNESS" -> 2.5f;
            case "INSTANT_HEALTH" -> 3.0f;
            case "REGENERATION" -> 3.0f;
            case "FIRE_RESISTANCE" -> 2.5f;
            case "WATER_BREATHING" -> 2.5f;
            case "INVISIBILITY" -> 2.0f;
            case "NIGHT_VISION" -> 2.0f;
            case "JUMP_BOOST" -> 2.5f;
            case "SPEED" -> 3.0f;
            case "STRENGTH" -> 3.0f;
            default -> 3.0f;
        };
    }

    private boolean isExplosivePotion(PotionEntity potion) {
        ItemStack stack = potion.getItem();
        return (stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION)
                && !PotionUtils.getEffectsFromStack(stack).isEmpty();
    }

    private Vector3d getNextMotion(Entity entity, Vector3d motion) {
        if (entity instanceof PotionEntity) {
            // Применяем сопротивление воды/воздуха
            if (entity.isInWater()) {
                motion = motion.scale(0.8);
            } else {
                motion = motion.scale(0.99);
            }

            // Применяем гравитацию
            if (!entity.hasNoGravity()) {
                motion = motion.add(0, -0.05, 0);
            }
        }
        return motion;
    }

    // Внутренние классы для хранения данных
    record ThrowablePoint(Vector3d position, int ticks, ResourceLocation texture) {
    }

    record PotionRadius(Vector3d position, float radius, String potionType) {
    }
}
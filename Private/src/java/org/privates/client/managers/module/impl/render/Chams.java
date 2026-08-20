package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector2f;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.events.orbit.EventPriority;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.Project;
import org.privates.client.utils.render.framebuffer.CustomFramebuffer;
import org.privates.client.utils.render.shader.ShaderManager;
import org.privates.client.utils.render.shader.impl.entity.EntityShader;
import org.privates.client.utils.render.shader.impl.outline.EntityOutlineShader;

import java.awt.Color;

@Category1
@Category2
@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Chams", category = Category.RENDER, description = "Обводка и заливка по модели")
public class Chams extends Module {
    public static Chams getInstance() {
        return Instance.get(Chams.class);
    }

    final MultiBooleanSetting rendering = new MultiBooleanSetting(this, "Отображать",
            new BooleanSetting("Игроков", true),
            new BooleanSetting("Монстров", false),
            new BooleanSetting("Друзей", true),
            new BooleanSetting("Животных", false),
            new BooleanSetting("Себя", false)
    );

    final ModeSetting mode = new ModeSetting(this, "Режим", "Оба", "Заливка", "Контур");
    final ModeSetting colorMode = new ModeSetting(this, "Цвет", "Клиентский", "Свой", "Радуга");

    final ColorSetting fillColor = new ColorSetting(this, "Цвет заливки", new Color(100, 180, 255, 80).getRGB())
            .setVisible(() -> (mode.is("Заливка") || mode.is("Оба")) && colorMode.is("Свой"));
    final ColorSetting outlineColor = new ColorSetting(this, "Цвет контура", new Color(150, 220, 255, 255).getRGB())
            .setVisible(() -> (mode.is("Контур") || mode.is("Оба")) && colorMode.is("Свой"));

    final SliderSetting lineWidth = new SliderSetting(this, "Толщина линий", 1.5F, 0.5F, 5.0F, 0.1F)
            .setVisible(() -> mode.is("Контур") || mode.is("Оба"));
    final BooleanSetting glow = new BooleanSetting(this, "Свечение", true)
            .setVisible(() -> mode.is("Контур") || mode.is("Оба"));
    final SliderSetting glowIntensity = new SliderSetting(this, "Сила свечения", 2.0F, 1.0F, 5.0F, 0.1F)
            .setVisible(() -> glow.getValue() && (mode.is("Контур") || mode.is("Оба")));
    final SliderSetting glowLayers = new SliderSetting(this, "Слои свечения", 3.0F, 1.0F, 6.0F, 1.0F)
            .setVisible(() -> glow.getValue() && (mode.is("Контур") || mode.is("Оба")));

    final BooleanSetting pulse = new BooleanSetting(this, "Пульсация", false);
    final SliderSetting pulseSpeed = new SliderSetting(this, "Скорость пульсации", 2.0F, 0.5F, 5.0F, 0.1F)
            .setVisible(pulse::getValue);

    final BooleanSetting damageEffect = new BooleanSetting(this, "Эффект при уроне", true);
    final ColorSetting damageColor = new ColorSetting(this, "Цвет урона", new Color(255, 50, 50, 255).getRGB())
            .setVisible(damageEffect::getValue);

    final BooleanSetting friendCustomColor = new BooleanSetting(this, "Свой цвет для друзей", false)
            .setVisible(() -> rendering.getValue("Друзей"));
    final ColorSetting friendFillColor = new ColorSetting(this, "Заливка друзей", new Color(85, 255, 85, 60).getRGB())
            .setVisible(() -> friendCustomColor.getValue() && (mode.is("Заливка") || mode.is("Оба")));
    final ColorSetting friendOutlineColor = new ColorSetting(this, "Контур друзей", new Color(100, 255, 100, 255).getRGB())
            .setVisible(() -> friendCustomColor.getValue() && (mode.is("Контур") || mode.is("Оба")));

    final CustomFramebuffer fillBuffer = new CustomFramebuffer(true);
    final CustomFramebuffer outlineBuffer = new CustomFramebuffer(true);
    final EntityShader bloom = new EntityShader();
    final long startTime = System.currentTimeMillis();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRender3D(Render3DPosedEvent event) {
        if (mc.world == null || mc.player == null) return;

        boolean showOutline = mode.is("РљРѕРЅС‚СѓСЂ") || mode.is("РћР±Р°");
        boolean showFill = mode.is("Р—Р°Р»РёРІРєР°") || mode.is("РћР±Р°");

        if (showFill) {
            fillBuffer.setup();
            for (Entity entity : mc.world.getAllEntities()) {
                if (!(entity instanceof LivingEntity living) || !affects(living)) continue;
                patchModel(living, resolveFillColor(living), () -> renderLivingModel(event, living));
            }
            fillBuffer.stop();
        }

        if (showOutline) {
            outlineBuffer.setup();
            for (Entity entity : mc.world.getAllEntities()) {
                if (!(entity instanceof LivingEntity living) || !affects(living)) continue;
                patchModel(living, resolveOutlineColor(living), () -> renderLivingModel(event, living));
            }
            outlineBuffer.stop();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRender2D(Render2DEvent event) {
        boolean showOutline = mode.is("Контур") || mode.is("Оба");
        boolean showFill = mode.is("Заливка") || mode.is("Оба");

        if (showOutline) {
            EntityOutlineShader.draw(Math.max(1, Math.round(lineWidth.getValue())), outlineBuffer.framebufferTexture);
            if (glow.getValue()) {
                bloom.render(event.getMatrix(), outlineBuffer.framebufferTexture,
                        glowLayers.getValue().intValue(),
                        1F,
                        4F + glowIntensity.getValue());
            }
        }

        if (showFill) {
            fillBuffer.draw();
        }

        fillBuffer.framebufferClear();
        outlineBuffer.framebufferClear();
        mc.getFramebuffer().bindFramebuffer(true);
    }

    private void renderLivingModel(Render3DPosedEvent event, LivingEntity living) {
        EntityRendererManager rendererManager = mc.getRenderManager();
        MatrixStack stack = event.getMatrix();
        stack.push();
        stack.translate(-rendererManager.renderPosX(), -rendererManager.renderPosY(), -rendererManager.renderPosZ());
        RenderSystem.depthMask(true);
        rendererManager.setRenderShadow(false);
        rendererManager.setRenderName(false);

        IRenderTypeBuffer.Impl source = mc.getRenderTypeBuffers().getBufferSource();
        Vector3d pos = living.getPositionVec(event.getPartialTicks());
        EntityRenderer<?> renderer = rendererManager.getRenderer(living);
        boolean nameVisible = renderer.isRenderName();

        if (nameVisible) renderer.setRenderName(false);
        rendererManager.renderClearEntityStatic(living, pos.getX(), pos.getY(), pos.getZ(), living.rotationYaw, event.getPartialTicks(), stack, source, rendererManager.getPackedLight(living, event.getPartialTicks()));
        if (nameVisible) renderer.setRenderName(true);

        source.finish();
        rendererManager.setRenderName(true);
        rendererManager.setRenderShadow(true);
        RenderSystem.depthMask(false);
        RenderSystem.enableDepthTest();
        stack.pop();
    }

    private void patchModel(LivingEntity entity, int color, Runnable runnable) {
        Vector3d interpolated = entity.getPositionVec().subtract(entity.getPositionVec(mc.getRenderPartialTicks()));
        AxisAlignedBB aabb = entity.getBoundingBox().offset(interpolated.inverse().add(interpolated.scale(mc.getRenderPartialTicks())));
        Vector2f center = Project.project2D(aabb.getCenter());
        if (center.x == Float.MAX_VALUE && center.y == Float.MAX_VALUE) return;

        float minX = center.x, minY = center.y, maxX = center.x, maxY = center.y;
        for (Vector3d corner : aabb.getCorners()) {
            Vector2f vec = Project.project2D(corner);
            if (vec.x == Float.MAX_VALUE && vec.y == Float.MAX_VALUE) continue;
            minX = Math.min(minX, vec.x);
            minY = Math.min(minY, vec.y);
            maxX = Math.max(maxX, vec.x);
            maxY = Math.max(maxY, vec.y);
        }

        ShaderManager shader = ShaderManager.entityChamsShader;
        shader.load();
        shader.setUniformi("tex", 0);
        shader.setUniformf("location", minX, minY);
        shader.setUniformf("rectSize", maxX - minX, maxY - minY);
        shader.setUniformf("color", ColorUtil.getRGBAf(color));

        runnable.run();
        shader.unload();
    }

    private boolean affects(LivingEntity entity) {
        if (entity == null || !entity.isAlive()) return false;
        if (entity == mc.player) {
            return rendering.getValue("Себя") && !mc.gameSettings.getPointOfView().firstPerson();
        }
        if (entity instanceof PlayerEntity) {
            boolean friend = Excellent.inst().friendManager().isFriend(entity.getName().getString());
            if (friend) return rendering.getValue("Друзей");
            return rendering.getValue("Игроков");
        }
        if (entity instanceof MobEntity) return rendering.getValue("Монстров");
        if (entity instanceof AnimalEntity) return rendering.getValue("Животных");
        return false;
    }

    private int resolveFillColor(LivingEntity entity) {
        int color = baseFillColor(entity);
        color = ColorUtil.multBright(color, 0.9F);
        color = ColorUtil.replAlpha(color, Math.max(ColorUtil.alpha(color), 89));
        return applyPulse(color);
    }

    private int resolveOutlineColor(LivingEntity entity) {
        return applyPulse(baseOutlineColor(entity));
    }

    private int baseFillColor(LivingEntity entity) {
        if (damageEffect.getValue() && entity.hurtTime > 0) {
            return ColorUtil.replAlpha(damageColor.getValue(), 110);
        }
        if (entity instanceof PlayerEntity && Excellent.inst().friendManager().isFriend(entity.getName().getString()) && friendCustomColor.getValue()) {
            return friendFillColor.getValue();
        }
        if (colorMode.is("Радуга")) {
            float hue = (System.currentTimeMillis() % 3000L) / 3000.0F;
            return ColorUtil.replAlpha(Color.HSBtoRGB(hue, 0.7F, 1.0F), 110);
        }
        if (colorMode.is("Свой")) return fillColor.getValue();
        return ColorUtil.replAlpha(Theme.getInstance().clientColor(), 110);
    }

    private int baseOutlineColor(LivingEntity entity) {
        if (damageEffect.getValue() && entity.hurtTime > 0) {
            return damageColor.getValue();
        }
        if (entity instanceof PlayerEntity && Excellent.inst().friendManager().isFriend(entity.getName().getString()) && friendCustomColor.getValue()) {
            return friendOutlineColor.getValue();
        }
        if (colorMode.is("Радуга")) {
            float hue = (System.currentTimeMillis() % 3000L) / 3000.0F;
            return Color.HSBtoRGB(hue, 0.8F, 1.0F) | 0xFF000000;
        }
        if (colorMode.is("Свой")) return outlineColor.getValue();
        return ColorUtil.replAlpha(Theme.getInstance().clientColor(), 255);
    }

    private int applyPulse(int color) {
        if (!pulse.getValue()) return color;
        float t = (System.currentTimeMillis() - startTime) / 1000.0F;
        float v = (float) ((Math.sin(t * pulseSpeed.getValue() * Math.PI) + 1.0D) * 0.5D);
        return ColorUtil.multAlpha(color, 0.3F + 0.7F * v);
    }
}

package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector2f;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.draw.Project;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Category1

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "FireWorkEsp", category = Category.RENDER, description = "Валлхак на феерверки то есть вы можете лучше увидеть феерверки с доп информацией")
public class FireWorkEsp extends Module {
    public static FireWorkEsp getInstance() {
        return Instance.get(FireWorkEsp.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Old", "New");
    private final SliderSetting displayTime = new SliderSetting(this, "Время отображения (сек)", 3.0f, 0.5f, 10.0f, 0.5f).setVisible(() -> mode.is("New"));

    private final CopyOnWriteArrayList<Firework> fireworks = new CopyOnWriteArrayList<>();
    private final Set<Integer> trackedFireworks = new HashSet<>();

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (mc.player == null || mc.world == null) return;

        MatrixStack matrix = event.getMatrix();

        if (mode.is("Old")) {
            for (Entity entity : mc.world.getAllEntities()) {
                if (entity instanceof FireworkRocketEntity firework) {
                    if (isPlayerFirework(firework)) continue;

                    Vector3d pos = firework.getPositionVec();
                    ItemStack item = firework.getItem();
                    int count = item.getCount();

                    renderFireworkOld(matrix, pos, item, count);
                }
            }
        } else if (mode.is("New")) {
            long currentTime = System.currentTimeMillis();
            long maxDisplayTime = (long) (displayTime.getValue() * 1000);

            for (Entity entity : mc.world.getAllEntities()) {
                if (entity instanceof FireworkRocketEntity firework) {
                    if (isPlayerFirework(firework)) continue;

                    int entityId = firework.getEntityId();
                    if (!trackedFireworks.contains(entityId)) {
                        fireworks.add(new Firework(firework.getPositionVec(), firework.getItem().copy(), entityId));
                        trackedFireworks.add(entityId);
                    }
                }
            }

            fireworks.removeIf(f -> {
                boolean shouldRemove = currentTime - f.spawnTime > maxDisplayTime;
                if (shouldRemove) {
                    trackedFireworks.remove(f.entityId);
                }
                return shouldRemove;
            });

            for (Firework firework : fireworks) {
                renderFireworkNew(matrix, firework.position, firework.itemStack, firework.itemStack.getCount());
            }
        }
    }

    private boolean isPlayerFirework(FireworkRocketEntity firework) {
        if (mc.player == null) return false;
        if (firework.boostedEntity == null) return false;
        return firework.boostedEntity.getEntityId() == mc.player.getEntityId();
    }

    private void renderFireworkOld(MatrixStack matrix, Vector3d position, ItemStack itemStack, int count) {
        Vector2f screen = Project.project2D(position);
        if (screen.x == Float.MAX_VALUE || screen.y == Float.MAX_VALUE) return;

        int x = (int) screen.x;
        int y = (int) screen.y;

        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        mc.getItemRenderer().renderItemAndEffectIntoGUI(itemStack, x - 8, y - 8);

        if (count > 1) {
            String countStr = String.valueOf(count);
            float scale = 0.7f;
            matrix.push();
            matrix.scale(scale, scale, scale);
            mc.fontRenderer.drawStringWithShadow(matrix, countStr, (x + 8) / scale, (y + 8) / scale, -1);
            matrix.pop();
        }

        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private void renderFireworkNew(MatrixStack matrix, Vector3d position, ItemStack itemStack, int count) {
        Vector2f screen = Project.project2D(position);
        if (screen.x == Float.MAX_VALUE || screen.y == Float.MAX_VALUE) return;

        int x = (int) screen.x;
        int y = (int) screen.y;

        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        mc.getItemRenderer().renderItemAndEffectIntoGUI(itemStack, x - 8, y - 8);

        if (count > 1) {
            String countStr = String.valueOf(count);
            float scale = 0.7f;
            matrix.push();
            matrix.scale(scale, scale, scale);
            mc.fontRenderer.drawStringWithShadow(matrix, countStr, (x + 8) / scale, (y + 8) / scale, -1);
            matrix.pop();
        }

        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        fireworks.clear();
        trackedFireworks.clear();
    }

    @Getter
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static class Firework {
        Vector3d position;
        ItemStack itemStack;
        int entityId;
        long spawnTime = System.currentTimeMillis();
    }
}